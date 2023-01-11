/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.threads;

import com.asset.contactstrategy.common.controller.EngineManager;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import static java.lang.Thread.currentThread;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mostafa.kashif
 */
public class EngineShutdownThread extends Thread {

    private Logger commonLogger;
    private AtomicBoolean engineShutdownFlag;
    private EngineManager manager;
    private String engineName;

    public EngineShutdownThread(Logger commonLogger, AtomicBoolean engineShutdownFlag, EngineManager manager, String engineName) {
        this.commonLogger = commonLogger;
        this.engineShutdownFlag = engineShutdownFlag;
        this.manager = manager;
        this.engineName = engineName;
    }

    @Override
    public void run() {
        commonLogger.info("Starting Thread " + currentThread().getName());
        try {
            commonLogger.info("Shutdown Thread started");
            engineShutdownFlag.set(true);
            manager.shutdown();
//                    commonLogger.info("/////////////////////"+engineName+"  IS SHUTTING DOWN/////////////////////");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, engineName + "is Shutting Down").build());
        } catch (Exception e) {
            commonLogger.error("Shutdown FlagShutdown Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + e);
            CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + e, e);
        } catch (Throwable e) {
            commonLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e);
            CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e, e);
        }
    }

}
