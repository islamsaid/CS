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
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.GroupParentModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.models.UploadProcedureResult;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customermatching.constants.EngineDefines;
import com.asset.cs.customermatching.managers.CustomerMatchingManager;
import com.asset.cs.customermatching.managers.ResourcesCachingManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Zain Al-Abedin
 */
public class UploadedCreatorWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customermatching.workers.UploadedCreatorWorker";
    private int groupObjectType = 0;
    private int groupId = 0;
    private String filterQuery = "";
    private int runId = 0;
    private BlockingQueue<FileModel> filesQueue;
    private MainService mainServ = new MainService();

    private UploadProcedureResult procedureResult = new UploadProcedureResult();

    public UploadedCreatorWorker(int groupId, String filterQuery, BlockingQueue<FileModel> filesQueue, int type) {
        this.groupId = groupId;
        this.filterQuery = filterQuery;
        this.runId = ResourcesCachingManager.getImportRunId();
        this.groupObjectType = type;
        this.filesQueue = filesQueue;
    }

    public void run() {
        Thread.currentThread().setName(this.getName());
        String methodName = "run";
        CommonLogger.businessLogger.info("--------- Starting UploadedCreatorWorker : " + this.getName() + " ----------");
        String errorParams = "";
        try {
            String fixedErrorParams = "Group_ID:" + this.groupId + ", RunId:" + this.runId + ", Type:" + this.groupObjectType + ", FilterQuery:" + this.filterQuery;
            FileModel file = this.filesQueue.poll();

            while (file != null
                    && !CustomerMatchingManager.isCatchingError()
                    && !ResourcesCachingManager.shutdownEngineFlag.get()) {
                if (this.groupObjectType == GeneralConstants.CAMPAIGN_GROUP && CustomerMatchingManager.campaignTargetedCustomers.get() <= 0) {
                    break;
                }
                if (CustomerMatchingManager.checkFileStatus(file.getFileID(), this.groupObjectType)) {
//                    CommonLogger.businessLogger.info(this.getName() + " FileId=[" + file.getFileID() + "] Is Valid ");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File is Valid")
                            .put(GeneralConstants.StructuredLogKeys.FILE_ID, file.getFileID()).build());
                    // mainServ.updateGroupFileStatus(file.getFileID(), GeneralConstants.FILE_STATUS_PROCESSING_VALUE, this.groupObjectType);
                } else {
//                    CommonLogger.businessLogger.info(this.getName() + " FileId=[" + file.getFileID() + "] Is Processing By another Instance");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File is Processing by another Instance")
                            .put(GeneralConstants.StructuredLogKeys.FILE_ID, file.getFileID()).build());
                    file = this.filesQueue.poll();
                    continue;
                }

                errorParams = fixedErrorParams + ", File:" + file.getFileID();
//                CommonLogger.businessLogger.info("Processing File_ID=[" + file.getFileID() + "] File_Name=[" + file.getFileName() + "] by " + this.getName());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Processing File")
                        .put(GeneralConstants.StructuredLogKeys.FILE_ID, file.getFileID())
                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, file.getFileName()).build());
                long startTime = System.currentTimeMillis();

                procedureResult = mainServ.matchGroupsCustomersByUpload(this.groupId, this.runId, file.getFileID(), this.groupObjectType, ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_RETRIES_FOR_DATABASE_CONNECTION));

//                CommonLogger.businessLogger.info(this.getName() + " Finished Processing File_ID=[" + file.getFileID() + "] File_Name=[" + file.getFileName() + "] Inserted Records=["
//                        + procedureResult.getAffectedRows() + "]  Uploaded Records =[" + procedureResult.getMsisdnCount() + "] in [" + (System.currentTimeMillis() - startTime) + "] msec");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished File Processing")
                        .put(GeneralConstants.StructuredLogKeys.FILE_ID, file.getFileID())
                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, file.getFileName())
                        .put(GeneralConstants.StructuredLogKeys.INSERTED_RECORDS, procedureResult.getAffectedRows())
                        .put(GeneralConstants.StructuredLogKeys.UPLOADED_RECORDS, procedureResult.getMsisdnCount())
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

                file = this.filesQueue.poll();

                //file = CustomerMatchingManager.filesQueue.poll();
                //errorParams = fixedErrorParams + ", File:" + file.getFileID();
            }
            CommonLogger.businessLogger.info("--------- Shutdown UploadedCreatorWorker : " + this.getName() + " ----------");

        } catch (CommonException e) {
            CommonLogger.errorLogger.error("Fatal Error -->" + e, e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            CustomerMatchingManager.setCatchingError(true);
            handleServiceException(e, methodName, errorParams);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error -->" + e, e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            CustomerMatchingManager.setCatchingError(true);
        }

    }

    private void handleServiceException(Exception e, String methodName, String errorParams) {

        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof InterruptedException) {
            campaignException = new CommonException("Interrupted exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof SQLException) {
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        errorModel.setErrorMessage(campaignException.getErrorMsg());
        errorModel.setErrorParams(errorParams);
        Utility.sendMOMAlarem(errorModel);

    }

}
