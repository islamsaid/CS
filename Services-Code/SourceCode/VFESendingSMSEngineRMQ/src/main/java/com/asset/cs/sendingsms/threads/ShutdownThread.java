/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.threads;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.sendingsms.defines.Defines;

/**
 *
 * @author kerollos.asaad
 */
public class ShutdownThread extends Thread {

    @Override
    public void run() {
//        CommonLogger.businessLogger.info("*****************SHUTDOWN THREAD STARTED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Shutdown Thread Started").build());

        MainService mainService = new MainService();
        while (!Defines.ENGINE_SHUTDOWN_FLAG.get()) {
            try {
                long sleepTime = Long.valueOf(Defines.databaseConfigurations.get(Defines.SENDING_SMS_SHUTDOWN_THREAD_SLEEP_TIME));
                String shutdownFlag = mainService.getInstanceShutdownFlag(Integer.valueOf(Defines.fileConfigurations.get(Defines.INSTACE_ID)));
                if (shutdownFlag.equalsIgnoreCase("true")) {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Shutdown Thread Setting engine shutdown flag to true").build());
                    Defines.ENGINE_SHUTDOWN_FLAG.set(true);
                    mainService.updateInstanceShutdownFlag(Integer.valueOf(Defines.fileConfigurations.get(Defines.INSTACE_ID)), "false");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Shutdown Thread Set engine flag to false").build());
                }
                Thread.sleep(sleepTime);
            } catch (CommonException e) {
                CommonLogger.businessLogger.error("Shutdown Thread Caugth CommonException--->" + e);
                CommonLogger.errorLogger.error("Shutdown Thread Caugth CommonException--->" + e, e);
            } catch (Exception e) {
                CommonLogger.businessLogger.error("Shutdown Thread Caugth Exception--->" + e);
                CommonLogger.errorLogger.error("Shutdown Thread Caugth Exception--->" + e, e);
            }
        }
//        CommonLogger.businessLogger.info("*****************SHUTDOWN THREAD FINISHED***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shutdown Thread Finished").build());

    }
}
