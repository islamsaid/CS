package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.smsbridging.controller.Controller;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;

/**
 *
 * @author aya.moawed 2595
 */
public class ShutdownThread extends Thread {

    @Override
    public void run() {
//        CommonLogger.businessLogger.debug("*****************SHUTDOWN THREAD STARTED ***********************");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shutdown Thread Started").build());
//        MainService mainService = new MainService();
//        while (!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG) {
//            try {String shutdownFlag = mainService.getSMSBridgingInstance(Controller.instanceID, DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.SMS_BRIDGING_SHUTDOWN_FLAG);
//                if (shutdownFlag.equalsIgnoreCase("true")) {
//                    CommonLogger.businessLogger.info("Shutdown Thread Setting engine shutdown flag to true");
//                    SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG = true;
//                    mainService.updateSMSBridgingInstance(Controller.instanceID, DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.SMS_BRIDGING_SHUTDOWN_FLAG, "false");
//                    CommonLogger.businessLogger.info("Shutdown Thread Set engine flag to false");
//                    //break;
//                }
//            } catch (CommonException e) {
//                CommonLogger.businessLogger.error("Shutdown FlagShutdown Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + e);
//                CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + e, e);
//            } catch (Exception e) {
//                CommonLogger.businessLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e);
//                CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e, e);
//            }finally {
//                try {
//                    CommonLogger.businessLogger.info("Shutdown Thread " + Thread.currentThread().getName() + "  is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SHUTDOWN_THREAD_SLEEP_TIME) + " msecs ");
//                    Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SHUTDOWN_THREAD_SLEEP_TIME)));
//                } catch (InterruptedException ex) {
//                    CommonLogger.businessLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
//                    CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
//                }
//            }
//        }
//        CommonLogger.businessLogger.debug("*****************SHUTDOWN THREAD FINISHED ***********************");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shutdown Thread Finished").build());

    }

}
