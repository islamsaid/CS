/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.Threads;

import com.asset.CS.BridgingDequeuerService.beans.ManagerBean;
import com.asset.CS.BridgingDequeuerService.services.MainService;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mostafa.kashif
 */
public class ReloadingThread extends Thread {

    @Autowired
    private ManagerBean managerBean;

    @Autowired
    private MainService mainService;

    @Override
    public void run() {
        CommonLogger.businessLogger.info("*****************RELOAD THREAD STARTED ***********************");

        while (!ManagerBean.DEQUEUER_SERVICE_SHUTFOWN_FLAG.get()) {
            try {
                ManagerBean.systemPropertiesMap = mainService.getPropertiesPerInterface(GeneralConstants.SRC_ID_DEQUEUER_WEB_SERVICE);
                managerBean.prepareArrayBlockingQueuesForApplicationQueues();
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + ex);
                CommonLogger.errorLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + ex, ex);
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
            } finally {
                try {
//                    CommonLogger.businessLogger.info("Reload Thread " + Thread.currentThread().getName() + " is going to sleep for " + ManagerBean.systemPropertiesMap.get(Defines.DequeuerRestWebService.RELOAD_THREAD_SLEEP_TIME_DB_PROEPRTY_NAME).getItemValue() + " msecs ");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reload Thread")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_NAME, Thread.currentThread().getName())
                            .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, ManagerBean.systemPropertiesMap.get(Defines.DequeuerRestWebService.RELOAD_THREAD_SLEEP_TIME_DB_PROEPRTY_NAME).getItemValue()).build());
                    Thread.sleep(Long.valueOf(ManagerBean.systemPropertiesMap.get(Defines.DequeuerRestWebService.RELOAD_THREAD_SLEEP_TIME_DB_PROEPRTY_NAME).getItemValue()));
                } catch (InterruptedException ex) {
                    CommonLogger.businessLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                    CommonLogger.errorLogger.error("Reload Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
                }
            }
        }
        CommonLogger.businessLogger.info("*****************RELOAD " + Thread.currentThread().getName() + "   THREAD FINISHED ***********************");
    }
}
