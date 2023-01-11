/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.managers;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customermatching.constants.EngineDefines;
import java.sql.SQLException;

/**
 *
 * @author Zain Al-Abedin
 */
public class ShutdownManager {
    
    private static final String CLASS_NAME = "com.asset.cs.customermatching.managers.ShutdownManager";
    
    public static void shutDown() {
        String methodName = "shutDown";
        CommonLogger.businessLogger.info("starting ShutDown Groups Customers Matching Engine");
        try {
//            if (!ResourcesCachingManager.shutdownEngineFlag) {
//                MainService mainServ = new MainService();
//                SystemPropertiesModel model = new SystemPropertiesModel();
//                //SRC ID =Group ID
//                model.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE);
//                model.setItemKey(GeneralConstants.KEY_ENGINE_SHUTDOWN_FLAG);
//                model.setItemValue(GeneralConstants.SHUTDOWN_FLAG_TRUE);
//                mainServ.updateSystemProperty(model);
//            }
            CommonLogger.businessLogger.info("Queue Sizes");
            if (CustomerMatchingManager.adsMSISDNSQueue != null) {
//                CommonLogger.businessLogger.info("ads MSISDNS Queue Size=>[" + CustomerMatchingManager.adsMSISDNSQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.adsMSISDNSQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ads MSISDNs Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.adsMSISDNSQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.adsMSISDNSQueue.remainingCapacity()).build());
            }
            if (CustomerMatchingManager.adsfilesQueue != null) {
//                CommonLogger.businessLogger.info("ads Files Queue Size=>[" + CustomerMatchingManager.adsfilesQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.adsfilesQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ads Files Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.adsfilesQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.adsfilesQueue.remainingCapacity()).build());
            }
            
            if (CustomerMatchingManager.smsMSISDNSQueue != null) {
//                CommonLogger.businessLogger.info("sms MSISDNS Queue Size=>[" + CustomerMatchingManager.smsMSISDNSQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.smsMSISDNSQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS MSISDNs Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.smsMSISDNSQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.smsMSISDNSQueue.remainingCapacity()).build());
            }
            if (CustomerMatchingManager.smsfilesQueue != null) {
//                CommonLogger.businessLogger.info("sms Files Queue Size=>[" + CustomerMatchingManager.smsfilesQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.smsfilesQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Files Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.smsfilesQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.smsfilesQueue.remainingCapacity()).build());
            }
            
            if (CustomerMatchingManager.campMSISDNSQueue != null) {
//                CommonLogger.businessLogger.info("sms CAMPAIGN Queue Size=>[" + CustomerMatchingManager.campMSISDNSQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.campMSISDNSQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS CAMPAIGN Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.campMSISDNSQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.campMSISDNSQueue.remainingCapacity()).build());
            }
            if (CustomerMatchingManager.campfilesQueue != null) {
//                CommonLogger.businessLogger.info("sms Files Queue Size=>[" + CustomerMatchingManager.campfilesQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.campfilesQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Files Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.campfilesQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.campfilesQueue.remainingCapacity()).build());
            }
            
            CustomerMatchingManager.shutDownCustomerMatchingEngine();
            
            DataSourceManger.closeConnectionPool();

//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())));
            CommonLogger.businessLogger.info("End ShutDown Groups Customers Matching Engine");
            
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Failed to ShutDown Groups Customers Matching Engine " + ex, ex);
            CommonLogger.businessLogger.error("Failed to ShutDown Groups Customers Matching Engine-->" + ex);
            handleServiceException(ex, methodName);
        } catch (InterruptedException ex) {
            CommonLogger.errorLogger.error("Failed to ShutDown Groups Customers Matching Engine " + ex, ex);
            CommonLogger.businessLogger.error("Failed to ShutDown Groups Customers Matching Engine-->" + ex);
            //handleServiceException(ex, methodName);
        }
    }
    
    private static void handleServiceException(Exception e, String methodName) {
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
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        errorModel.setErrorMessage(campaignException.getMessage());
        Utility.sendMOMAlarem(errorModel);
        
    }
}
