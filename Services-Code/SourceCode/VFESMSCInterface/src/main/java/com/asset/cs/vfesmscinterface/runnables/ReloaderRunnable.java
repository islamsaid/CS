/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.runnables;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import java.time.LocalDateTime;

/**
 *
 * @author Mohamed
 */
public class ReloaderRunnable implements Runnable {

    private static MainService mainService = new MainService();

    public ReloaderRunnable() {
        CommonLogger.businessLogger.debug("creating ReloaderThread");
    }

    @Override
    public void run() {
        try {
            CommonLogger.businessLogger.debug("Reloader thread started successfully");
            Thread.currentThread().setName("ReloaderThread");

            long startTime = System.currentTimeMillis();
            while (!Manager.isShutdown.get()) {

                long sleepTime = Long.valueOf(
                        Manager.systemProperities.get(Defines.SMSC_INTERFACE_PROPERTIES.RELOADER_THREAD_SLEEP_TIME));
                Thread.sleep(sleepTime);

                Manager.systemProperities = mainService.getSystemPropertiesByGroupID(Defines.runningProjectId);

//				CommonLogger.businessLogger.debug("System Properties reloaded at " + LocalDateTime.now());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "System Properties Reloaded")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

                Manager.services = mainService.getServices();
//                CommonLogger.businessLogger.info("Services reloaded at " + LocalDateTime.now());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Services Reloaded")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            }

//            CommonLogger.businessLogger.debug("Reloader job time :" + (System.currentTimeMillis() - startTime) + " msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reloader Job Timing")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
            CommonLogger.businessLogger.debug("Reloader thread  closed successfully");
        } catch (InterruptedException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        } catch (CommonException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }

}
