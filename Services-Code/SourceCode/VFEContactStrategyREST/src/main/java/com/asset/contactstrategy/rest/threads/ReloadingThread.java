/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.threads;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.rest.common.ConfigurationManager;

/**
 *
 * @author esmail.anbar
 */
public class ReloadingThread implements Runnable {

    int reloadCounter;
    static int sleepTime = Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME_VALUE;
    boolean freshUpdate = true;
    static int reloadedTimes = 0;

    @Override
    public void run() {
        Thread.currentThread().setName("Reloading Thread");
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
        MainService mainService = new MainService();
        while (!ConfigurationManager.SHUTDOWN_FLAG) {
            try {
//                reloadCounter = mainService.getInterfaceInstaceReloadCounter(ConfigurationManager.INSTANCE_ID);
//                if (reloadCounter > 0)
//                {
//                    CommonLogger.businessLogger.error(Thread.currentThread().getName() + " Update Found in " + DBStruct.VFE_CS_INTERFACES_INSTANCES.TABLE_NAME 
//                            + " for instanceId " + ConfigurationManager.INSTANCE_ID);
                ConfigurationManager.reloadData();
//                    mainService.updateInterfaceInstanceReloadCounter(ConfigurationManager.INSTANCE_ID, 0);
                reloadedTimes++;
//                    freshUpdate = true;
//                }

                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            } //            catch (CommonException ce)
            //            {
            //                CommonLogger.businessLogger.error(ce.getErrorMsg());
            //                CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
            //            }
            catch (Exception e) {
                CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
                CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
            }
        }
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended || ReloadedTimes: " + reloadedTimes + " || sleepTime: " + sleepTime);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + " has Ended")
                .put(GeneralConstants.StructuredLogKeys.RELOADING_TIME, reloadedTimes)
                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
    }

}
