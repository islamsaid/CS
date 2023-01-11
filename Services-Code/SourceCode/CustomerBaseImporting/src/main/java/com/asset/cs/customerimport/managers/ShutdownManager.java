/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.managers;

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
import com.asset.cs.customerimport.constants.EngineDefines;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zain Al-Abedin
 */
public class ShutdownManager {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.managers.ShutdownManager";

    public static void shutDown() {
        String methodName = "shutDown";
        try {
            CommonLogger.businessLogger.info("starting ShutDown  Customer Base Importing Engine");
//            if (!ResourcesCachingManager.shutdownEngineFlag) {
//                MainService mainServ = new MainService();
//                SystemPropertiesModel model = new SystemPropertiesModel();
//                //SRC ID =Group ID
//                model.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
//                model.setItemKey(EngineDefines.KEY_ENGINE_SHUTDOWN_FLAG);
//                model.setItemValue(GeneralConstants.SHUTDOWN_FLAG_TRUE);
//            //    mainServ.updateSystemProperty(model);
//            }
//            CommonLogger.businessLogger.info("===============================Que Sizes======================================");
            if (CustomerImportingManager.readersDataQueue != null) {
//                CommonLogger.businessLogger.info("Readers Data Queue Size=>[" + CustomerImportingManager.readersDataQueue.size() + "]  Remaining=>[" + CustomerImportingManager.readersDataQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Readers Data Queue Size")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerImportingManager.readersDataQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerImportingManager.readersDataQueue.remainingCapacity()).build());
            }
            Iterator workersQueuesIterator = CustomerImportingManager.writersDataQueuesMap.entrySet().iterator();
            while (workersQueuesIterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) workersQueuesIterator.next();
                Integer queueId = (Integer) mapEntry.getKey();
                BlockingQueue queue = (BlockingQueue) mapEntry.getValue();
//                CommonLogger.businessLogger.info("Worker Data Queue [" + (queueId + 1) + "] Size=> [" + queue.size() + "]  Remaining=>" + queue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Worker Data Queue")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, (queueId + 1))
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, queue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, queue.remainingCapacity()).build());
            }

            CustomerImportingManager.shutdownCustomerImportingManager();
            DataSourceManger.closeConnectionPool();
//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())).build());
//            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump End").build());
            
            CommonLogger.businessLogger.info("End ShutDown  Customer Base Importing Engine");
        } catch (InterruptedException ex) {
            CommonLogger.errorLogger.error("Failed to ShutDown Customer Base Importing Engine ", ex);
            CommonLogger.businessLogger.info("Failed to ShutDown Customer Base Importing Engine", ex.getMessage());
            handleServiceException(ex, methodName);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Failed to ShutDown Customer Base Importing Engine ", ex);
            CommonLogger.businessLogger.info("Failed to ShutDown Customer Base Importing Engine", ex.getMessage());
            handleServiceException(ex, methodName);
        }
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_MEDIUM);
            campaignException = (CommonException) e;
            // Handle SQL Exception 
        } else if (e instanceof InterruptedException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_MEDIUM);
            campaignException = new CommonException("Interrupted exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_MEDIUM);
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        errorModel.setErrorMessage(campaignException.getMessage());
        Utility.sendMOMAlarem(errorModel);

    }
}
