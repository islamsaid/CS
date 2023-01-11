/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;

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
                    SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.set(true);
                    Manager.shutdownEngine();
//                       CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                CommonLogger.businessLogger.info("/////////////////////SMS INTERFACE BRIDGING ENGINE IS OFF/////////////////////");
//                CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS INTERFACE BRIDGING ENGINE IS OFF").build());
                } catch (Exception e) {
                    CommonLogger.businessLogger.error("Shutdown FlagShutdown Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + e);
                    CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught CommonException--->" + e, e);
                } catch (Throwable e) {
                    CommonLogger.businessLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e);
                    CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e, e);
                }
            }
        });

    }
}
