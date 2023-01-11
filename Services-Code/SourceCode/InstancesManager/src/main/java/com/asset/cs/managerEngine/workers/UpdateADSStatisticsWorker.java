/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.workers;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.managerEngine.constants.EngineDefines;
import com.asset.cs.managerEngine.managers.Manager;
import com.asset.cs.managerEngine.managers.ResourcesCachingManager;
import java.sql.SQLException;

/**
 *
 * @author Administrator
 */
public class UpdateADSStatisticsWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.managerEngine.workers.UpdateADSStatisticsWorker";
    private MainService mainServ = new MainService();

    private int column;
    private int jobType;

    public UpdateADSStatisticsWorker(int column, int jobType) {
        this.column = column;
        this.jobType = jobType;
    }

    public void run() {
        Thread.currentThread().setName(this.getName());
        String methodName = "run";
//        CommonLogger.businessLogger.info("--------- Starting ADS UPDATE WORKER : " + this.getName() + " ----------");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting ADS update Worker").build());
        String errorParams = "";
        try {
            String fixedErrorParams = "Update Column:[" + column + "]";
            Integer lastMSISDNTwoDigits = Manager.adsMSISDNSQueue.poll();
            errorParams = fixedErrorParams + ", Last MSISDN TWO Digits:" + lastMSISDNTwoDigits;
            while (lastMSISDNTwoDigits != null
                    && !Manager.isStatisticsCatchingError()
                    && !ResourcesCachingManager.shutdownEngineFlag.get()) {
//                CommonLogger.businessLogger.info("Processing Last MSISDN TWO Digits=[" + lastMSISDNTwoDigits + "] by " + this.getName());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Processing Last MSISDN Two Digits")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN_LAST_TWO_DIGITS, lastMSISDNTwoDigits).build());
                long startTime = System.currentTimeMillis();
                if (jobType == EngineDefines.JOB_COPY_COLUMNS) {
                    mainServ.copyCounterToTemp(DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME, column, lastMSISDNTwoDigits);
                } else {
                    mainServ.resetStatisticsCounterColumn(DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME, column, lastMSISDNTwoDigits);
                }
//                CommonLogger.businessLogger.info(this.getName() + " Finished ADS Processing Last MSISDN TWO Digits=[" + lastMSISDNTwoDigits + "]  in [" + (System.currentTimeMillis() - startTime) + "] msec");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished Ads Processing Last MSISDN Two Digits")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN_LAST_TWO_DIGITS, lastMSISDNTwoDigits)
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
                lastMSISDNTwoDigits = Manager.adsMSISDNSQueue.poll();
                errorParams = fixedErrorParams + ", Last MSISDN TWO Digits:" + lastMSISDNTwoDigits;
            }
//            CommonLogger.businessLogger.info("--------- Shutdown ADS UPDATE WORKER : " + this.getName() + " ----------");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shutdown ADS Update Worker").build());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error -->", e);
            CommonLogger.businessLogger.error("Fatal Error -->" + e);
            Manager.setStatisticsCatchingError(true);
            handleServiceException(e, methodName, errorParams);
        }
    }

    private void handleServiceException(Exception e, String methodName, String errorParams) {

        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        //   errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_MANAGER_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        errorModel.setErrorParams(errorParams);
        Utility.sendMOMAlarem(errorModel);

    }

}
