/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.workers;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.GroupParentModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customermatching.constants.EngineDefines;
import com.asset.cs.customermatching.managers.CustomerMatchingManager;
import com.asset.cs.customermatching.managers.ResourcesCachingManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author Zain Al-Abedin
 */
public class CriteriaCreatorWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customermatching.workers.CriteriaCreatorWorker";
    private int groupObjectType = 0;
    private long groupId = 0;
    private String filterQuery = "";
    private int runId = 0;
    private BlockingQueue<Integer> MSISDNSQueue;
    private MainService mainServ = new MainService();

    public CriteriaCreatorWorker(int groupId, String filterQuery, BlockingQueue<Integer> MSISDNSQueue, int type) {
        this.groupId = groupId;
        this.filterQuery = filterQuery;
        this.runId = ResourcesCachingManager.getImportRunId();
        this.groupObjectType = type;
        this.MSISDNSQueue = MSISDNSQueue;
    }

    public void run() {
        Thread.currentThread().setName(this.getName());
        String methodName = "run";
        CommonLogger.businessLogger.info("--------- Starting CriteriaCreatorWorker : " + this.getName() + " ----------");
        String errorParams = "";
        try {
            ThreadContext.put(GeneralConstants.StructuredLogKeys.GROUP_ID, this.groupId + "");
            ThreadContext.put(GeneralConstants.StructuredLogKeys.RUN_ID, this.runId + "");
            ThreadContext.put(GeneralConstants.StructuredLogKeys.GROUP_OBJECT_TYPE, this.groupObjectType + "");
            ThreadContext.put(GeneralConstants.StructuredLogKeys.QUERY, this.filterQuery);
            String fixedErrorParams = "Group_ID:" + this.groupId + ", RunId:" + this.runId + ", Type:" + this.groupObjectType + ", FilterQuery:" + this.filterQuery;

            Integer lastMSISDNTwoDigits = this.MSISDNSQueue.poll();
            errorParams = fixedErrorParams + ", Last MSISDN TWO Digits:" + lastMSISDNTwoDigits;

            while (lastMSISDNTwoDigits != null && !CustomerMatchingManager.isCatchingError() && !ResourcesCachingManager.shutdownEngineFlag.get()) {
                try {
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, lastMSISDNTwoDigits + "");
                    if (this.groupObjectType == GeneralConstants.CAMPAIGN_GROUP && CustomerMatchingManager.campaignTargetedCustomers.get() <= 0) {
                        break;
                    }
//                CommonLogger.businessLogger.info("Processing Last MSISDN TWO Digits=[" + lastMSISDNTwoDigits + "] by " + this.getName());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "CriteriaCreatorWorker Processing with Thread Name " + this.getName()).build());
                    long startTime = System.currentTimeMillis();
                    long affectedRows = mainServ.matchGroupsCustomersByCriteria(this.groupObjectType, (int) this.groupId, this.runId, lastMSISDNTwoDigits, this.filterQuery,
                            (this.groupObjectType == GeneralConstants.CAMPAIGN_GROUP) ? CustomerMatchingManager.campaignTargetedCustomers.get() : -1,
                            ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_RETRIES_FOR_DATABASE_CONNECTION));

//                CommonLogger.businessLogger.info(this.getName() + " Finished Processing Last MSISDN TWO Digits=[" + lastMSISDNTwoDigits + "] Matched Records=["
//                        + affectedRows + "]  in [" + (System.currentTimeMillis() - startTime) + "] msec");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished Processing")
                            .put(GeneralConstants.StructuredLogKeys.MATCHED_RECORDS, affectedRows)
                            .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
                    lastMSISDNTwoDigits = this.MSISDNSQueue.poll();

                    //lastMSISDNTwoDigits = CustomerMatchingManager.MSISDNSQueue.poll();
                    errorParams = fixedErrorParams + ", Last MSISDN TWO Digits:" + lastMSISDNTwoDigits;
                } finally {
                    ThreadContext.remove(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS);
                }
            }
        } catch (CommonException e) {
            CommonLogger.errorLogger.error("Fatal Error -->" + e, e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            CustomerMatchingManager.setCatchingError(true);
            handleServiceException(e, methodName, errorParams);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error -->" + e, e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            CustomerMatchingManager.setCatchingError(true);
        } finally {
            ThreadContext.clearMap();
        }
        CommonLogger.businessLogger.info("--------- Shutdown CriteriaCreatorWorker : " + this.getName() + " ----------");
    }

    private void handleServiceException(Exception e, String methodName, String errorParams) {

        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(Integer.valueOf(ResourcesCachingManager.getConfigurationValue(GeneralConstants.SHUTDOWN_ENGINE_MOM_ERROR_SEVERITY)));
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        errorModel.setErrorParams(errorParams);
        Utility.sendMOMAlarem(errorModel);

    }
}
