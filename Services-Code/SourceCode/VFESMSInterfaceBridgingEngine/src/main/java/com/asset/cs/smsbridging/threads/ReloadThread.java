package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.smsbridging.controller.Controller;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;

/**
 *
 * @author aya.moawed 2595
 */
public class ReloadThread extends Thread {

    @Override
    public void run() {
//        CommonLogger.businessLogger.info("*****************RELOAD THREAD STARTED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reload Thread Started").build());
        // MainService mainService = new MainService();
        while (!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.get()) {
            try {
                // int counter = Integer.valueOf(mainService.getSMSBridgingInstance(Controller.instanceID, DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.RELOAD_COUNTER));
                //if (counter > 0) {
                Manager.reload();
                //  mainService.updateSMSBridgingInstance(Controller.instanceID, DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.RELOAD_COUNTER, "0");
                //}
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + ex);
                CommonLogger.errorLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + ex, ex);
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
            } finally {
                try {
//                    CommonLogger.businessLogger.info("Reload Thread " + Thread.currentThread().getName() + " is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_RELOAD_CONFIGURATION_TIME) + " msecs ");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reload Thread is goint to sleep")
                            .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_RELOAD_CONFIGURATION_TIME)).build());
                    Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_RELOAD_CONFIGURATION_TIME)));
                } catch (InterruptedException ex) {
                    CommonLogger.businessLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                    CommonLogger.errorLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
                }
            }
        }
//        CommonLogger.businessLogger.info("*****************RELOAD " + Thread.currentThread().getName() + "   THREAD FINISHED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reload Thread Finished").build());
    }
}
