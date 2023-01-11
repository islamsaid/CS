/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.workers;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.models.UploadProcedureResult;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.managerEngine.constants.EngineDefines;
import com.asset.cs.managerEngine.managers.Manager;
import com.asset.cs.managerEngine.managers.ResourcesCachingManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zain.Hamed
 */
public class InstancesWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.managerEngine.workers.InstancesWorker";
    private MainService mainServ = new MainService();
    private Vector<CampaignModel> groupsList = null;
    private UploadProcedureResult procedureResult = new UploadProcedureResult();
    private boolean instanceFailed = false;

    public void run() {
//        CommonLogger.businessLogger.info("--------- Starting GroupsWorker : " + this.getName() + " ----------");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting GroupsWorker").build());
        String methodName = "run";

        try {
            // System.out.println(Integer.parseInt("XYZ"));
            long dwhImportSleepTimeInMinutes = ResourcesCachingManager.getLongValue(EngineDefines.DWH_IMPORT_SLEEP_TIME);
            long dwhImportSleepTimeInMs = dwhImportSleepTimeInMinutes * 60 /*(sec)*/ * 1000 /*(ms)*/;

            long waitingInstancesSleepTime = ResourcesCachingManager.getLongValue(EngineDefines.WAITING_INSTANCES_SLEEP_TIME);
            waitingInstancesSleepTime = waitingInstancesSleepTime * 60 * 1000;

            long instanceFailSleepTime = ResourcesCachingManager.getLongValue(EngineDefines.INSTANCE_FAIL_SLEEP_TIME);
            instanceFailSleepTime = instanceFailSleepTime * 60 * 1000;

            int waitingInstancesTrials = ResourcesCachingManager.getIntValue(EngineDefines.WAITING_INSTANCES_TRIALS);

            while (!isWorkerShutDownFlag()) {
                ResourcesCachingManager.loadResourcesAndConfigurations();
                if (checkNewDWHImport()) {
                    instanceFailed = false;
//                    CommonLogger.businessLogger.info("==============================================================================================");
//                    CommonLogger.businessLogger.info("Find New DWH Import Worker will work on RUN_ID=[" + Manager.getDwhLatestRunID() + "]");
//                    CommonLogger.businessLogger.info("==============================================================================================");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Find new DWH import worker will work")
                            .put(GeneralConstants.StructuredLogKeys.RUN_ID, Manager.getDwhLatestRunID()).build());
                    int instancesWaitingNumTrials = 0;

                    while (!allInstancesFinished()) {
                        instancesWaitingNumTrials++;
//                        CommonLogger.businessLogger.info("Waiting While Instances Finish Working on Run_Id=[" + Manager.getDwhLatestRunID() + "]"
//                                + " Worker will Sleep For=[" + waitingInstancesSleepTime + "] msec Num_Trials=[" + instancesWaitingNumTrials + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Waiting While Instances Finish Working")
                                .put(GeneralConstants.StructuredLogKeys.RUN_ID, Manager.getDwhLatestRunID())
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, waitingInstancesSleepTime)
                                .put(GeneralConstants.StructuredLogKeys.NUM_TRIALS, instancesWaitingNumTrials).build());

                        if (instancesWaitingNumTrials == waitingInstancesTrials) {
                            sendMom(ErrorCodes.MATCHING_INSTANCE_DELAY, "Some Instances Delay", "RUN_ID=[" + Manager.getDwhLatestRunID() + "]");
                            CommonLogger.businessLogger.info("Execute Failure Action because not All Instances Finish,MOM Sent");
                            failureAction();
                            updateFilesStatus();
                            instancesWaitingNumTrials = 0;
                        }
                        this.sleep(waitingInstancesSleepTime);
                    }

//                    CommonLogger.businessLogger.info("====================================All Instances Finished Working On RUN_ID=[" + Manager.getDwhLatestRunID() + "]====================================");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "All Instances Finished Working")
                            .put(GeneralConstants.StructuredLogKeys.RUN_ID, Manager.getDwhLatestRunID()).build());

                    updateFilesStatus();
                    if (allInstancesSuccessed()) {
//                        CommonLogger.businessLogger.info("All Instances Finished Successfully Working On RUN_ID=[" + Manager.getDwhLatestRunID() + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "All Instances Finished Successfullu working")
                                .put(GeneralConstants.StructuredLogKeys.RUN_ID, Manager.getDwhLatestRunID()).build());
                        successAction();
//                        CommonLogger.businessLogger.info("====================================Success Action Executed Successfully for RUN_ID=[" + Manager.getDwhLatestRunID() + "]====================================");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Success Action Executed Successfully")
                                .put(GeneralConstants.StructuredLogKeys.RUN_ID, Manager.getDwhLatestRunID()).build());
                    } else {
                        sendMom(ErrorCodes.MATCHING_INSTANCE_FAILED, "Some Instances Failed", "RUN_ID=[" + Manager.getDwhLatestRunID() + "]");
//                        CommonLogger.businessLogger.info("Some Instances Failed Working On RUN_ID=[" + Manager.getDwhLatestRunID() + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Some Instances Failed Working")
                                .put(GeneralConstants.StructuredLogKeys.RUN_ID, Manager.getDwhLatestRunID()).build());
                        failureAction();
//                        CommonLogger.businessLogger.info("====================================Failure Action Executed Successfully for RUN_ID=[" + Manager.getDwhLatestRunID() + "]====================================");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Failure Action Executed Successfully")
                                .put(GeneralConstants.StructuredLogKeys.RUN_ID, Manager.getDwhLatestRunID()).build());
                        instanceFailed = true;
                    }

                }
                //  CommonLogger.businessLogger.info("NO NEW DWH Import Worker Will Sleep For=[" + dwhImportSleepTimeInMs + "] msec");
                if (instanceFailed) {
                    this.sleep(instanceFailSleepTime);
                } else {
                    this.sleep(dwhImportSleepTimeInMs);
                }

            }

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", e);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + e);
            handleServiceException(e, methodName);

        }
    }

    public void updateFilesStatus() {
        String methodName = "updateFilesStatus";
        try {
            mainServ.updateSMSGroupFilesStatus();
            mainServ.updateADSGroupFilesStatus();
            mainServ.updateCampaignFilesStatus();
            CommonLogger.businessLogger.info("All Files Status Updated Successfully");
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }
    }

    public boolean allInstancesSuccessed() {
        String methodName = "allInstancesSuccessed";
        boolean allSuccessed = false;
        try {
            allSuccessed = mainServ.allInstancesSuccessed(Manager.getDwhLatestRunID());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }
        return allSuccessed;
    }

    public void failureAction() {
        String methodName = "failureAction";
        try {
            mainServ.dropGroupsCustomersPartation(Manager.getDwhLatestRunID(), DBStruct.VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME);
            mainServ.dropGroupsCustomersPartation(Manager.getDwhLatestRunID(), DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.TABLE_NAME);
            mainServ.dropGroupsCustomersPartation(Manager.getDwhLatestRunID(), DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.TABLE_NAME);
            mainServ.updateALLMatchingInstancesRunId((Manager.getDwhLatestRunID() - 1));
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
            failureAction();    //if failed call recursive 
        }

    }

    public void successAction() {
        String methodName = "successAction";
        boolean failed = false;
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            processCampaigns(connection);
            updateActiveRunId(connection);

            connection.commit();
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", e);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + e);
            handleServiceException(e, methodName);
            failed = true;
            try {
                DataSourceManger.rollBack(connection);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Failed to [Rollback] [" + methodName + "]" + ex);
            }
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Failed to [Close Connection] [" + methodName + "]" + ex);
            }

            if (failed) {
//                CommonLogger.businessLogger.info("Call [" + methodName + "] Recursive ");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Calling Recursive Method")
                        .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
                successAction();    //if failed call recursive 
            }
        }
    }

    public void processCampaigns(Connection connection) throws CommonException {
        String methodName = "processCampaigns";
        try {
            groupsList = mainServ.getApprovedActiveCampaignsList();
//            CommonLogger.businessLogger.info("Engine Will Work on [" + groupsList.size() + "] Campaigns");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enging will work")
                    .put(GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, groupsList.size()).build());
            for (CampaignModel group : groupsList) {
//                CommonLogger.businessLogger.info("=====================================Campaign ID[" + group.getVersionId() + "] Name=[" + group.getCampaignName() + "] Priority=["
//                        + group.getPriority() + "] Max_Targeted_Customers=[" + group.getMaxTargetedCustomers() + "] Control Percentage=[" + group.getControlPercentage() + "]=====================================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Group Stats")
                        .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_ID, group.getVersionId())
                        .put(GeneralConstants.StructuredLogKeys.GROUP_NAME, group.getCampaignName())
                        .put(GeneralConstants.StructuredLogKeys.PRIORITY, group.getPriority())
                        .put(GeneralConstants.StructuredLogKeys.MAX_TARGETED_CUSTOMERS, group.getMaxTargetedCustomers())
                        .put(GeneralConstants.StructuredLogKeys.CONTROL_PERCENTAGE, group.getControlPercentage()).build());
            }

            for (CampaignModel group : groupsList) {
//                CommonLogger.businessLogger.info("======================Delete AND Suspend CAMPAIGN_ID=[" + group.getVersionId() + "]======================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Delete and Suspend Campaign")
                        .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_ID, group.getVersionId()).build());
                long deleteStartTime = System.currentTimeMillis();
                procedureResult = mainServ.deleteSuspendCampaign(connection, group.getVersionId(), Manager.getDwhLatestRunID(), group.getMaxTargetedCustomers(), group.getControlPercentage());
//                CommonLogger.businessLogger.info("Finished Delete AND Suspend CAMPAIGN_ID=[" + group.getVersionId() + "]  Deleted Records=["
//                        + procedureResult.getAffectedRows() + "] Customers Suspended =[" + procedureResult.getMsisdnCount() + "]  in [" + (System.currentTimeMillis() - deleteStartTime) + "] msec");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished Delete and Suspend Campaign")
                        .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_ID, group.getVersionId())
                        .put(GeneralConstants.StructuredLogKeys.DELETED_RECORDS, procedureResult.getAffectedRows())
                        .put(GeneralConstants.StructuredLogKeys.CUSTOMER_SUSPENDED, procedureResult.getMsisdnCount())
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - deleteStartTime)).build());
            }
            CommonLogger.businessLogger.info("Finished Processing Campaigns");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error [processCampaigns] --->", e);
            CommonLogger.businessLogger.error("Fatal Error [processCampaigns] --->" + e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        }

    }

    public void updateActiveRunId(Connection connection) throws CommonException {
        String methodName = "updateActiveRunId";
        try {
            SystemPropertiesModel activeParitionModel = new SystemPropertiesModel();
            activeParitionModel.setItemKey(EngineDefines.KEY_ACTIVE_RUN_ID);
            activeParitionModel.setItemValue(String.valueOf(Manager.getDwhLatestRunID()));
            activeParitionModel.setGroupId(GeneralConstants.SRC_ID_MANAGER_ENGINE);
            mainServ.updateSystemProperty(connection, activeParitionModel);
            CommonLogger.businessLogger.info("Finished Update Active RUN_ID");
            SystemPropertiesModel latestMatchingDate = new SystemPropertiesModel();
            latestMatchingDate.setItemKey(EngineDefines.KEY_MANAGER_LAST_UPDATE);
            latestMatchingDate.setGroupId(GeneralConstants.SRC_ID_MANAGER_ENGINE);
            latestMatchingDate.setItemValue("");
            mainServ.updateTimeSystemProperty(connection, latestMatchingDate);
            CommonLogger.businessLogger.info("Finished Update Last Update System Property");
            mainServ.CountersReloader(connection);
            CommonLogger.businessLogger.info("Finished Update Reload Counters");

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
            //handleServiceException(ex, methodName);
            //TODO recursive
            // updateActiveRunId();
        }

    }

    public boolean allInstancesFinished() {
        String methodName = "allInstancesFinished";
        boolean allFinished = false;
        try {
            allFinished = mainServ.allInstancesFinished(Manager.getDwhLatestRunID());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }
        return allFinished;
    }

    public boolean checkNewDWHImport() {
        String methodName = "checkNewDWHImport";
        boolean newImport = false;
        try {
            if (Manager.getActiveRunId() < Manager.getDwhLatestRunID()) {
                newImport = true;
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }
        return newImport;
    }

    private void sendMom(String errorCode, String errorMsg, String errorParam) {
        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_MATCHING_INSTANCES_FAILURE_MOM_SEVERITY)));
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_MANAGER_ENGINE);
        errorModel.setErrorCode(errorCode);
        errorModel.setErrorMessage(errorMsg);
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setErrorParams(errorParam);
        Utility.sendMOMAlarem(errorModel);
    }

    private void handleServiceException(Exception e, String methodName) {

        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof InterruptedException) {
            campaignException = new CommonException("Interrupted exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof SQLException) {
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_MANAGER_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

}
