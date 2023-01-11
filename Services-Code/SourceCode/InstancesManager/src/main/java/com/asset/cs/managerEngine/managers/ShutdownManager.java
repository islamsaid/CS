/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.managers;

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
import com.asset.cs.managerEngine.constants.EngineDefines;
import java.sql.SQLException;

/**
 *
 * @author Zain Al-Abedin
 */
public class ShutdownManager {

    private static final String CLASS_NAME = "com.asset.cs.managerEngine.managers.ShutdownManager";

    public static void shutDown() {
        String methodName = "shutDown";
        CommonLogger.businessLogger.info("starting ShutDown Instances Manager Engine");
        try {
//            if (!ResourcesCachingManager.shutdownEngineFlag) {
//                MainService mainServ = new MainService();
//                SystemPropertiesModel model = new SystemPropertiesModel();
//                model.setGroupId(GeneralConstants.SRC_ID_MANAGER_ENGINE);
//                model.setItemKey(GeneralConstants.KEY_ENGINE_SHUTDOWN_FLAG);
//                model.setItemValue(GeneralConstants.SHUTDOWN_FLAG_TRUE);
//                mainServ.updateSystemProperty(model);
//            }
//            CommonLogger.businessLogger.info("===============================Que Sizes======================================");
            if (Manager.smsMSISDNSQueue != null) {
//                CommonLogger.businessLogger.info("SMS MSISDNS Queue Size=>[" + Manager.smsMSISDNSQueue.size() + "]  Remaining=>[" + Manager.smsMSISDNSQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS MSISDNS Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.smsMSISDNSQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.smsMSISDNSQueue.remainingCapacity()).build());
            }
            if (Manager.adsMSISDNSQueue != null) {
//                CommonLogger.businessLogger.info("ADS MSISDNS Queue Size=>[" + Manager.adsMSISDNSQueue.size() + "]  Remaining=>[" + Manager.adsMSISDNSQueue.remainingCapacity() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ADS MSISDNS Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.adsMSISDNSQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.adsMSISDNSQueue.remainingCapacity()).build());
            }
            Manager.shutDownInstancesEngine();

            DataSourceManger.closeConnectionPool();
//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())).build());
//            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Ended").build());
            CommonLogger.businessLogger.info("End ShutDown Instances Manager Engine");

        } catch (InterruptedException ex) {
            CommonLogger.errorLogger.error("Failed to ShutDown Instances Manager Engine ", ex);
            CommonLogger.businessLogger.error("Failed to ShutDown Instances Manager Engine -->" + ex);
            handleServiceException(ex, methodName);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Failed to ShutDown Instances Manager Engine ", ex);
            CommonLogger.businessLogger.error("Failed to ShutDown Instances Manager Engine -->" + ex);
            handleServiceException(ex, methodName);
        }
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof InterruptedException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
            campaignException = new CommonException("Interrupted exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        // errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_MANAGER_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        errorModel.setErrorMessage(campaignException.getMessage());
        Utility.sendMOMAlarem(errorModel);

    }
}
