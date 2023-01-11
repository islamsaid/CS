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
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;

/**
 *
 * @author mostafa.kashif
 */
public class ShutdownHookThread {

    public void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                CommonLogger.businessLogger.info("Starting Thread " + currentThread().getName());
                try {
                    CommonLogger.businessLogger.info("Shutdown Thread started");
                    //Raise flag to start closing producers and handlers threads
                    Defines.ENGINE_SHUTDOWN_FLAG.set(true);
                    Manager.shutdownEngine();
//                    CommonLogger.businessLogger.info("/////////////////////SENDING SMS ENGINE IS SHUTTING DOWN/////////////////////");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SENDING SMS ENGINE IS SHUTTING DOWN").build());
                } catch (Exception e) {
                    // CommonLogger.businessLogger.error("Shutdown FlagShutdown Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + e);
                    CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + e, e);
                } catch (Throwable e) {
                    // CommonLogger.businessLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e);
                    CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e, e);
                }
            }
        });

    }
}
