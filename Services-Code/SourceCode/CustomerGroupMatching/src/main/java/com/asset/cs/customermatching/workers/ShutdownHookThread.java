/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.workers;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.customermatching.managers.CustomerMatchingManager;
import com.asset.cs.customermatching.managers.ResourcesCachingManager;
import com.asset.cs.customermatching.managers.ShutdownManager;
import static java.lang.Thread.currentThread;

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
                    ResourcesCachingManager.shutdownEngineFlag.set(true);
                    ShutdownManager.shutDown();
//                    CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
                    CommonLogger.businessLogger.info("/////////////////////Customer Group Matching Engine IS OFF/////////////////////");
//                    CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
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
