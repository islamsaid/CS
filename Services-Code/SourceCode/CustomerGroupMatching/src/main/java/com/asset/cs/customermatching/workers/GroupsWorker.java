/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.workers;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.GroupParentModel;
import com.asset.contactstrategy.common.models.GroupsParentModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customermatching.constants.EngineDefines;
import com.asset.cs.customermatching.managers.CustomerMatchingManager;
import com.asset.cs.customermatching.managers.ResourcesCachingManager;
import com.asset.cs.customermatching.managers.ShutdownManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.poi.util.CommonsLogger;

/**
 *
 * @author Zain Al-Abedin
 */
public class GroupsWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customermatching.workers.GroupsWorker";
    private int groupObjectType = 0;
    private Vector<GroupsParentModel> groupsList = null;
    private MainService mainServ = new MainService();

    public GroupsWorker(int type) {
        this.groupObjectType = type;
    }

    public void run() {
        CommonLogger.businessLogger.info("--------- Starting GroupsWorker : " + this.getName() + " ----------");
        String methodName = "run";
        try {
            CustomerMatchingManager.setCatchingError(false);
            //ResourcesCachingManager.loadResourcesAndConfigurations();

//            CommonLogger.businessLogger.info("==============================================================================================");
//            CommonLogger.businessLogger.info("================================== Start to work on Customers Base Partition: " + (ResourcesCachingManager.getImportRunId()) + "==================================");
//            CommonLogger.businessLogger.info("==============================================================================================");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start to work on Customers Base Partition")
                    .put(GeneralConstants.StructuredLogKeys.PARTITION, (ResourcesCachingManager.getImportRunId())).build());
            long startTime = System.currentTimeMillis();

            addNewPartation(this.groupObjectType);
            groupsList = new Vector<GroupsParentModel>();
            groupsList = mainServ.getApprovedGroupsList(this.groupObjectType);
//            CommonLogger.businessLogger.info("Engine Will Work on [" + groupsList.size() + "] Group of type " + this.groupObjectType);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Engine Working Stats")
                    .put(GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, groupsList.size())
                    .put(GeneralConstants.StructuredLogKeys.GROUP_OBJECT_TYPE, this.groupObjectType).build());
            for (GroupsParentModel castedGroup : groupsList) {
                try {
                    if (this.groupObjectType == GeneralConstants.CAMPAIGN_GROUP) {

                        CampaignModel group = (CampaignModel) castedGroup;
                        ThreadContext.put(GeneralConstants.StructuredLogKeys.CAMPAIGN_ID, group.getVersionId() + "");
//                    CommonLogger.businessLogger.info("Engine Will Work on [" + groupsList.size() + "] Campaigns");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Engine Campaign Working Stats")
                                .put(GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, groupsList.size()).build());

                        long groupStartTime = System.currentTimeMillis();
                        if (ResourcesCachingManager.shutdownEngineFlag.get() || CustomerMatchingManager.isCatchingError()) {
//                        CommonLogger.businessLogger.info("Failed To Finish Processing Customers Base Partition: " + (ResourcesCachingManager.getImportRunId()));
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Failed to Finish Processing Customers Base Partition")
                                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, (ResourcesCachingManager.getImportRunId())).build());
                            CustomerMatchingManager.getInstance().setStatus(GeneralConstants.FALSE);//represents Failure
                            break;
                        }
//                    CommonLogger.businessLogger.info("==================================Handling CAMPAIGN_ID=[" + group.getVersionId() + "] CAMPAIGN_Version_ID=["
//                            + group.getCampaignId() + "] CAMPAIGN_MAX_TARGETED_CUSTOMERS =[" + group.getMaxTargetedCustomers() + "] CAMPAIGN_Type=[" + group.getFilterType().getLable() + "]==================================");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Handling Engine's Campaign")
                                .put(GeneralConstants.StructuredLogKeys.VERSION_ID, group.getCampaignId())
                                .put(GeneralConstants.StructuredLogKeys.MAX_TARGETED_CUSTOMERS, group.getMaxTargetedCustomers())
                                .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_TYPE, group.getFilterType().getLable()).build());
                        int no_of_criteria_threads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_CRITERIA_CREATOR_THREADS);
                        CustomerMatchingManager.campCriteriaWorkersPool = Executors.newFixedThreadPool(no_of_criteria_threads);

                        if (group.getFilterType().getId() == GeneralConstants.GROUP_TYPE_CRITERIA_BASED) {
                            CustomerMatchingManager.campMSISDNSQueue = new ArrayBlockingQueue<Integer>(Math.abs(CustomerMatchingManager.instance.getSubPartitionEnd() - CustomerMatchingManager.instance.getSubPartitionStart()) + 1);
                            CustomerMatchingManager.startCriteriaWorkers((int) group.getVersionId(), group.getFilterQuery(), CustomerMatchingManager.campMSISDNSQueue, CustomerMatchingManager.campCriteriaWorkersPool, this.groupObjectType, group.getMaxTargetedCustomers());
                        } else {
                            CustomerMatchingManager.campfilesQueue = new ArrayBlockingQueue<FileModel>(1000);
                            CustomerMatchingManager.startUpladWorkers((int) group.getVersionId(), group.getFilterQuery(), CustomerMatchingManager.campfilesQueue, CustomerMatchingManager.campCriteriaWorkersPool, this.groupObjectType, group.getMaxTargetedCustomers());
                        }
//                    CommonLogger.businessLogger.info("==================================Finished Processing CampaignId=[" + group.getVersionId() + "] in ["
//                            + (System.currentTimeMillis() - groupStartTime) + "] msec==================================");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished Engine's Campaign Processing")
                                .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_ID, group.getVersionId())
                                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - groupStartTime)).build());
                        /*    CustomerMatchingManager.campStatusModel.setFinished(true);
                     CustomerMatchingManager.campStatusModel.setStatus(
                     (!ResourcesCachingManager.shutdownEngineFlag && !CustomerMatchingManager.isCatchingError() && CustomerMatchingManager.campStatusModel.getFinished())
                     ? GeneralConstants.TRUE : GeneralConstants.FALSE);*/
                    } else {
                        // ADS or SMS

                        GroupParentModel group = (GroupParentModel) castedGroup;
                        String type = (this.groupObjectType == GeneralConstants.ADS_GROUP) ? "ADS" : "SMS";
//                    CommonLogger.businessLogger.info("===================================== " + type + " Group ID[" + group.getVersionId() + "] Name=[" + group.getGroupName() + "] Priority=["
//                            + group.getGroupPriority() + "] Group_Type=[ " + group.getGroupType().getLable() + "]=====================================");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Handling Engine's Group ")
                                .put(GeneralConstants.StructuredLogKeys.TYPE, type)
                                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, group.getVersionId())
                                .put(GeneralConstants.StructuredLogKeys.GROUP_NAME, group.getGroupName())
                                .put(GeneralConstants.StructuredLogKeys.PRIORITY, group.getGroupPriority())
                                .put(GeneralConstants.StructuredLogKeys.GROUP_TYPE, group.getGroupType().getLable()).build());
                        long groupStartTime = System.currentTimeMillis();
                        if (ResourcesCachingManager.shutdownEngineFlag.get() || CustomerMatchingManager.isCatchingError()) {
//                        CommonLogger.businessLogger.info("Failed To Finish Processing Customers Base Partition: " + (ResourcesCachingManager.getImportRunId()));
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Failed to Finsih Processing Customers Base Partition")
                                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, (ResourcesCachingManager.getImportRunId())).build());
                            CustomerMatchingManager.getInstance().setStatus(GeneralConstants.FALSE);//represents Failure
                            break;
                        }
//                    CommonLogger.businessLogger.info("==================================Handling " + type + " Group_ID=[" + group.getVersionId() + "] Group_Version_ID=["
//                            + group.getGroupId() + "] Group_Type=[" + group.getGroupType().getLable() + "]==================================");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Handling Engine's Group")
                                .put(GeneralConstants.StructuredLogKeys.TYPE, type)
                                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, group.getVersionId())
                                .put(GeneralConstants.StructuredLogKeys.VERSION_ID, group.getGroupId())
                                .put(GeneralConstants.StructuredLogKeys.PRIORITY, group.getGroupPriority())
                                .put(GeneralConstants.StructuredLogKeys.GROUP_TYPE, group.getGroupType().getLable()).build());
                        if (this.groupObjectType == GeneralConstants.SMS_GROUP) {
                            int no_of_criteria_threads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_CRITERIA_CREATOR_THREADS);
                            CustomerMatchingManager.smsCriteriaWorkersPool = Executors.newFixedThreadPool(no_of_criteria_threads);
                        } else {
                            int no_of_criteria_threads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_CRITERIA_CREATOR_THREADS);
                            CustomerMatchingManager.adsCriteriaWorkersPool = Executors.newFixedThreadPool(no_of_criteria_threads);
                        }

                        // 
                        if (group.getGroupType().getId() == GeneralConstants.GROUP_TYPE_CRITERIA_BASED) {
                            if (this.groupObjectType == GeneralConstants.SMS_GROUP) {
                                CustomerMatchingManager.smsMSISDNSQueue = new ArrayBlockingQueue<Integer>(Math.abs(CustomerMatchingManager.instance.getSubPartitionEnd() - CustomerMatchingManager.instance.getSubPartitionStart()) + 1);
                            } else {
                                CustomerMatchingManager.adsMSISDNSQueue = new ArrayBlockingQueue<Integer>(Math.abs(CustomerMatchingManager.instance.getSubPartitionEnd() - CustomerMatchingManager.instance.getSubPartitionStart()) + 1);
                            }

                            CustomerMatchingManager.startCriteriaWorkers(group.getVersionId(), group.getFilterQuery(),
                                    (this.groupObjectType == GeneralConstants.SMS_GROUP) ? CustomerMatchingManager.smsMSISDNSQueue : CustomerMatchingManager.adsMSISDNSQueue,
                                    (this.groupObjectType == GeneralConstants.SMS_GROUP) ? CustomerMatchingManager.smsCriteriaWorkersPool : CustomerMatchingManager.adsCriteriaWorkersPool,
                                    this.groupObjectType, -1);
                        } else {
                            if (this.groupObjectType == GeneralConstants.SMS_GROUP) {
                                CustomerMatchingManager.smsfilesQueue = new ArrayBlockingQueue<FileModel>(1000);
                            } else {
                                CustomerMatchingManager.adsfilesQueue = new ArrayBlockingQueue<FileModel>(1000);
                            }

                            CustomerMatchingManager.startUpladWorkers(group.getVersionId(), group.getFilterQuery(),
                                    (this.groupObjectType == GeneralConstants.SMS_GROUP) ? CustomerMatchingManager.smsfilesQueue : CustomerMatchingManager.adsfilesQueue,
                                    (this.groupObjectType == GeneralConstants.SMS_GROUP) ? CustomerMatchingManager.smsCriteriaWorkersPool : CustomerMatchingManager.adsCriteriaWorkersPool,
                                    this.groupObjectType, -1);
                        }
//                    CommonLogger.businessLogger.info("==================================Finished Processing " + type + " GroupId=[" + group.getVersionId() + "] in [" + (System.currentTimeMillis() - groupStartTime) + "] msec==================================");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished Engine's Grotp Processing")
                                .put(GeneralConstants.StructuredLogKeys.TYPE, type)
                                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, group.getVersionId())
                                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - groupStartTime)).build());
                    }
                } finally {
                    ThreadContext.remove(GeneralConstants.StructuredLogKeys.CAMPAIGN_ID);
                }
            }
            if (this.groupObjectType == GeneralConstants.CAMPAIGN_GROUP) {
                CustomerMatchingManager.campStatusModel.setFinished(true);
                CustomerMatchingManager.campStatusModel.setStatus(
                        (!ResourcesCachingManager.shutdownEngineFlag.get() && !CustomerMatchingManager.isCatchingError() && CustomerMatchingManager.campStatusModel.getFinished())
                        ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            } else if (this.groupObjectType == GeneralConstants.SMS_GROUP) {
                CustomerMatchingManager.smsStatusModel.setFinished(true);
                CustomerMatchingManager.smsStatusModel.setStatus(
                        (!ResourcesCachingManager.shutdownEngineFlag.get() && !CustomerMatchingManager.isCatchingError() && CustomerMatchingManager.smsStatusModel.getFinished())
                        ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            } else {
                CustomerMatchingManager.adsStatusModel.setFinished(true);
                CustomerMatchingManager.adsStatusModel.setStatus(
                        (!ResourcesCachingManager.shutdownEngineFlag.get() && !CustomerMatchingManager.isCatchingError() && CustomerMatchingManager.adsStatusModel.getFinished())
                        ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            }
            /*if (!ResourcesCachingManager.shutdownEngineFlag && !CustomerMatchingManager.isCatchingError()) {
             CustomerMatchingManager.getInstance().setStatus(GeneralConstants.TRUE);//represents Success
             } else {//to handle last group error
             CustomerMatchingManager.getInstance().setStatus(GeneralConstants.FALSE);//represents Failure
             }
             mainServ.updateMatchingInstanceModel(CustomerMatchingManager.getInstance());*/

//            CommonLogger.businessLogger.info("==============================================================================================");
//            CommonLogger.businessLogger.info("Finished Matching Groups Customers Status=[" + CustomerMatchingManager.getInstance().getStatus() + "] "
//                    + " in [" + (System.currentTimeMillis() - startTime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished Matching Groups")
                    .put(GeneralConstants.StructuredLogKeys.STATUS, CustomerMatchingManager.getInstance().getStatus())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
//            CommonLogger.businessLogger.info("==============================================================================================");

        } catch (CommonException e) {
            CommonLogger.errorLogger.error("Fatal Error -->" + e, e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            CustomerMatchingManager.setCatchingError(true);
            handleServiceException(e, methodName);
//            handleServiceException(new CommonException("ShutDown Groups Matching Engine", ErrorCodes.SHUTDOWN_CUSTOMER_SMS_MATCHIN), methodName);
//            ShutdownManager.shutDown();
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error -->" + e, e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            CustomerMatchingManager.setCatchingError(true);
            handleServiceException(e, methodName);
//            handleServiceException(new CommonException("ShutDown Groups Matching Engine", ErrorCodes.SHUTDOWN_CUSTOMER_SMS_MATCHIN), methodName);
//            ShutdownManager.shutDown();
        }
    }

    public boolean checkNewDWHImport() {
        if (CustomerMatchingManager.getInstance().getRunId() < ResourcesCachingManager.getImportRunId()) {
            CustomerMatchingManager.getInstance().setRunId(ResourcesCachingManager.getImportRunId());
            return true;
        } else {
            return false;
        }
    }

    private void addNewPartation(int type) throws CommonException {
        if (type == GeneralConstants.ADS_GROUP) {
            mainServ.addGroupsCustomersNewPartation(ResourcesCachingManager.getImportRunId(), DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.TABLE_NAME);
        } else if (type == GeneralConstants.SMS_GROUP) {
            mainServ.addGroupsCustomersNewPartation(ResourcesCachingManager.getImportRunId(), DBStruct.VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME);
        } else {
            mainServ.addGroupsCustomersNewPartation(ResourcesCachingManager.getImportRunId(), DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.TABLE_NAME);
        }
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
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        MOMErrorsModel errorModel = new MOMErrorsModel();
        //errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        if (campaignException.getErrorCode().equals(ErrorCodes.SHUTDOWN_CUSTOMER_SMS_MATCHIN)) {
            errorModel.setPreceivedSeverity(Integer.valueOf(ResourcesCachingManager.getConfigurationValue(GeneralConstants.SHUTDOWN_ENGINE_MOM_ERROR_SEVERITY)));
        } else {
            errorModel.setPreceivedSeverity(Integer.valueOf(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
        }
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);
    }

}
