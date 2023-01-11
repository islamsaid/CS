/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.controller;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.AppQueueModel;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.defines.ErrorCodes;
//import com.asset.cs.sendingsms.test.Test;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author kerollos.asaad
 */
public class Controller {

    private static final String CLASS_NAME = "com.asset.cs.sendingsms.controller.Controller";
    //////////////////////////////////////////
    private boolean exitFlag = false;
    private boolean alive = true;
    public ArrayList<AppQueueModel> serviceProfileArray = null;
    public static Logger debugLogger = null;
    public static Logger infoLogger = null;
    public static Logger errorLogger = null;

    public static void main(String[] args) {
        String methodName = "main";
//        System.out.println("-----------STARTING SENDING SMS ENGINE-------------");
        try {
            com.asset.contactstrategy.common.defines.Defines.runningProjectId = GeneralConstants.SRC_ID_SENDING_SMS_ENGINE;
            Initializer.readPropertiesFile(Defines.fileConfigurations);
            Initializer.initializeLoggers();
            Initializer.initializeDataSource();
            initializeLogger();
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Sending SMS Engine").build());
            SystemLookups.VFE_PREFIX = Utility.loadLookupsMap(DBStruct.VFE_TRIM_SMSC_MSG_ID_PREFIX.TABLE_NAME, DBStruct.VFE_TRIM_SMSC_MSG_ID_PREFIX.VFE_TRIM_SMSC_MSG_ID_PREFIX_ID, DBStruct.VFE_TRIM_SMSC_MSG_ID_PREFIX.VFE_TRIM_SMSC_MSG_ID_PREFIX_PREFIX);
            MainService mainService = new MainService();
            if (Defines.fileConfigurations.get(Defines.INSTACE_ID) == null || Defines.fileConfigurations.get(Defines.INSTACE_ID).isEmpty()) {
                Defines.CLOUD_MODE = true;
//                debugLogger.info("Enigne working in cloud mode");
                debugLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Engine Working in Cloud Mode").build());
            }
            if (!Defines.CLOUD_MODE) {
                if (mainService.getInstanceShutdownFlag(Integer.valueOf(Defines.fileConfigurations.get(Defines.INSTACE_ID))).equalsIgnoreCase("false")) {
                    startEngine(mainService);
                } else {
                    throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SHUTDOWN_FLAG_ERROR, null);
                }
            } else {
                startEngine(mainService);
            }
        } catch (Exception ex) {
            //handleServiceException(ex, methodName);
            CommonLogger.businessLogger.error("Controller Caugth Exception--->" + ex);
            CommonLogger.errorLogger.error("Controller Caugth Exception--->" + ex, ex);
        }
    }

    public static void initializeLogger() {
        String methodName = "initializeLogger";
        try {
            infoLogger = CommonLogger.businessLogger;
            debugLogger = CommonLogger.businessLogger;
            errorLogger = CommonLogger.errorLogger;
//            debugLogger.info("Loggers initialized successfully...");
            debugLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Logger Initialize Successfullys").build());
        } catch (Exception ex) {
            //handleServiceException(ex, methodName);
            CommonLogger.businessLogger.error("Controller Caugth Exception--->" + ex);
            CommonLogger.errorLogger.error("Controller Caugth Exception--->" + ex, ex);
        }
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException commonException = null;
        // Handle CommonException 
        if (e instanceof CommonException) {
            commonException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            commonException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), com.asset.contactstrategy.common.defines.ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            commonException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), com.asset.contactstrategy.common.defines.ErrorCodes.UNKOWN_ERROR);
        }
        MOMErrorsModel errorModel = new MOMErrorsModel();
        //errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setPreceivedSeverity(Integer.valueOf(Defines.databaseConfigurations.get(Defines.DATABASE_MOM_ERROR_SEVERITY)));
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE);
        errorModel.setErrorCode(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE + commonException.getErrorCode());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);
    }

    public void WaitNotify() {
        synchronized (this) {
            while (alive == true) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    debugLogger.error("InterruptedException || " + e.getMessage());
                    errorLogger.error("InterruptedException || " + e.getMessage());
                }
            }
            this.notifyAll();
        }
    }

    ////////////////////////////
    /// Setters and getters ////
    ///////////////////////////
    public boolean getExitFlag() {
        return this.exitFlag;
    }

    public void setExitFlag(boolean ExitFlag) {
        this.exitFlag = ExitFlag;
    }

    public boolean getAlive() {
        return alive;
    }

    public void setAlive(boolean Alive) {
        this.alive = Alive;
    }

    private static void startEngine(MainService mainService) {

        try {
//            Test.prepareInputJsonModel();
//            CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//            CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//            CommonLogger.businessLogger.info("/////////////////////SENDING SMS ENGINE IS ON/////////////////////");
//            CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//            CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sending SMS Engine is On").build());

            Defines.databaseConfigurations = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE);
            Defines.deliveryEngineConfigurations = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_DELIVERY_AGGREGATION);
            if (!Defines.CLOUD_MODE) {
//                debugLogger.info("Starting getting instance configuratio for instance:" + Defines.INSTACE_ID);
                debugLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Getting Instance Configuration")
                        .put(GeneralConstants.StructuredLogKeys.INSTANCE_ID, Defines.INSTACE_ID).build());
                Defines.instanceConfigurations = mainService.getInstanceProperties(Integer.valueOf(Defines.fileConfigurations.get(Defines.INSTACE_ID)));
            }
            Defines.appQueueMap = mainService.getHashedApplicationQueues();
            Defines.messageStatus.put(Defines.DELIVRD_STATUS_STRING, com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.DELIVERED);
            Defines.messageStatus.put(Defines.EXPIRED_STATUS_STRING, com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.EXPIRED);
            Defines.messageStatus.put(Defines.DELETED_STATUS_STRING, com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.DELETED);
            Defines.messageStatus.put(Defines.UNDELIV_STATUS_STRING, com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.UNDELIVERABLE);
            Defines.messageStatus.put(Defines.ACCEPTD_STATUS_STRING, com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.ACCEPTED);
            Defines.messageStatus.put(Defines.UNKNOWN_STATUS_STRING, com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.UNKNOWN);
            Defines.messageStatus.put(Defines.REJECTD_STATUS_STRING, com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.REJECTED);
            // need to connect to each queue database. TODO. SEVERE. DONE.
            Manager.prepareSMPPApps();
            //Manager.startEnqueuerThreads();
            Manager.startReceivedSMSLogThreads();
            Manager.startMonitorThread();
            Manager.initLivenessThread();

            Manager.startReloadThread();

            //  if(!Defines.CLOUD_MODE){
            //          businessLogger.info("Starting getting shut down thread for instance:"+Defines.INSTACE_ID);
            //        Manager.startShutdownThread();
            //     }
            //   else 
            //    {
//            debugLogger.info("Starting shutdownhook");
            debugLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Shutdownhook").build());
            // Manager.startShutdownHookThread();
            Initializer.attachShutDownHook(debugLogger, Defines.ENGINE_SHUTDOWN_FLAG, new Manager(), "SendingEngine");
            //   }

            while (!Defines.ENGINE_SHUTDOWN_FLAG.get()) {
                Thread.sleep(10000);
            }
            //Manager.shutdownEngine();
//            CommonLogger.businessLogger.info("/////////////////////SENDING SMS ENGINE IS SHUTTING DOWN/////////////////////");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sending SMS Engine is Shutting Down").build());

        } catch (Exception ex) {
            //handleServiceException(ex, methodName);
            CommonLogger.businessLogger.error("Controller Caugth Exception--->" + ex);
            CommonLogger.errorLogger.error("Controller Caugth Exception--->" + ex, ex);
        }
    }
}
