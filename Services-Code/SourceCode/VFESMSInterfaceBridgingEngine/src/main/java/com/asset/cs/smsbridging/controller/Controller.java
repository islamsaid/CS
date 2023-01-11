package com.asset.cs.smsbridging.controller;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author aya.moawed 2595
 */
public class Controller {

    //public static int instanceID;
    public static void main(String[] args) {
//        System.out.println("-----------STARTING SMS INTERFACE BRIDGING ENGINE-------------");
        try {
            com.asset.contactstrategy.common.defines.Defines.runningProjectId = GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE;
            Manager.initializePropsLoggersAndDataSource();
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting SMS Interface Bridging Engine").build());
            //	Check Shutdown flag in database
            //instanceID = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.INSTACE_ID_PROP));
            MainService mainService = new MainService();
            //   if (mainService.getSMSBridgingInstance(instanceID, DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.SMS_BRIDGING_SHUTDOWN_FLAG).equalsIgnoreCase("false")) {
//                CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                CommonLogger.businessLogger.info("/////////////////////SMS INTERFACE BRIDGING ENGINE IS ON/////////////////////");
//                CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Interface Bridging Engine is ON").build());

            Manager.initialize();

            // Manager.startShutdownThread();
            Manager.startShutdownHookThread();
            Manager.startMonitorThread();
            Manager.initLivenessThread();
            Manager.startReloadThread();
            while (!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.get()) {
                Thread.sleep(10000);
            }
            //Manager.shutdownEngine();//to do
            //  CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
            // CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//            CommonLogger.businessLogger.info("/////////////////////SMS INTERFACE BRIDGING ENGINE IS SHUTTING DOWN/////////////////////");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Interface Bridging is Shutting Dwon").build());

            //   CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
            //   CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//            } else {
//                throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.SHUTDOWN_FLAG_ERROR, "ENGINE IS ALREADY IN SHUTDOWN MODE ");
//            }
        } catch (Exception ex) {
            CommonLogger.businessLogger.error("Controller Caugth Exception--->" + ex);
            CommonLogger.errorLogger.error("Controller Caugth Exception--->" + ex, ex);
        }
    }
}
