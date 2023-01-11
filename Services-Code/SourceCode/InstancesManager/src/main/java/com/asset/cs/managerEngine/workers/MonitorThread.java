/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.workers;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.managerEngine.constants.EngineDefines;
import com.asset.cs.managerEngine.managers.Manager;
import com.asset.cs.managerEngine.managers.ResourcesCachingManager;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author Mina
 */
public class MonitorThread extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.managerEngine.workers.MonitorThread";
    private long sleepTime = 10000;

    public MonitorThread() {

    }

    public void run() {
        while (!this.isWorkerShutDownFlag()) {
            try {
                try {
                    sleepTime = Long.parseLong(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_MONITOR_SLEEP_TIME));
                } catch (Exception e) {
                    sleepTime = 10000;
                    CommonLogger.errorLogger.error("Colud not load System Properties", e);
                }
//                CommonLogger.businessLogger.info("##### Heap utilization statistics [MB] #####");
//                CommonLogger.businessLogger.info("Used Memory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
//                CommonLogger.businessLogger.info("Free Memory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)));
//                CommonLogger.businessLogger.info("Total Memory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
//                CommonLogger.businessLogger.info("Max Memory:" + (Runtime.getRuntime().maxMemory() / (1024 * 1024)));
//                CommonLogger.businessLogger.info("===============================Que Sizes======================================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Heap Utilization Statistics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());
                if (Manager.smsMSISDNSQueue != null) {
//                    CommonLogger.businessLogger.info("SMS MSISDNS Queue Size=>[" + Manager.smsMSISDNSQueue.size() + "]  Remaining=>[" + Manager.smsMSISDNSQueue.remainingCapacity() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS MSISDNS Queue Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.smsMSISDNSQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.smsMSISDNSQueue.remainingCapacity()).build());
                }
                if (Manager.adsMSISDNSQueue != null) {
//                    CommonLogger.businessLogger.info("ADS MSISDNS Queue Size=>[" + Manager.adsMSISDNSQueue.size() + "]  Remaining=>[" + Manager.adsMSISDNSQueue.remainingCapacity() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ADS MSISDNS Queue Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.adsMSISDNSQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.adsMSISDNSQueue.remainingCapacity()).build());
                }
//                CommonLogger.businessLogger.info("===============================Thread Pools======================================");
                if (Manager.smsUpdateWorkersPool != null) {
//                    CommonLogger.businessLogger.info("SMS Update Workers Thread Pool  Size=>[" + ((ThreadPoolExecutor) Manager.smsUpdateWorkersPool).getPoolSize() + "] "
//                            + "  Active=>[" + ((ThreadPoolExecutor) Manager.smsUpdateWorkersPool).getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Update Workers Thread Pool")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) Manager.smsUpdateWorkersPool).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) Manager.smsUpdateWorkersPool).getActiveCount()).build());
                }
                if (Manager.adsUpdateWorkersPool != null) {
//                    CommonLogger.businessLogger.info("ADS Update Workers Thread Pool  Size=>[" + ((ThreadPoolExecutor) Manager.adsUpdateWorkersPool).getPoolSize() + "] "
//                            + "  Active=>[" + ((ThreadPoolExecutor) Manager.adsUpdateWorkersPool).getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ADS Update Workers Thread Pool")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) Manager.adsUpdateWorkersPool).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) Manager.adsUpdateWorkersPool).getActiveCount()).build());
                }
//                CommonLogger.businessLogger.info("===============================DataBase Connections ======================================");
                if (DataSourceManger.getHikariDataSourse() != null) {
//                    CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(DataSourceManger.getC3p0ConnectionPool()));
//                    CommonLogger.businessLogger.info(Utility.getHikariConnectionPoolStats(DataSourceManger.getHikariDataSourse()));
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Connections")
                            .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, Utility.getHikariConnectionPoolStats(DataSourceManger.getHikariDataSourse())).build());
                } else {
                    CommonLogger.businessLogger.info("HikariCP DataSource is null");
                }
//                CommonLogger.businessLogger.info("####################Sleeping for " + sleepTime + "#########################");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Connection is Sleeping")
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
            } catch (Throwable th) {
                CommonLogger.errorLogger.error("Fatal Exception => ", th);
            } finally {
                try {
                    this.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    CommonLogger.errorLogger.error("Monitor Thread Failed to sleep", ex);
                    CommonLogger.businessLogger.error("Monitor Thread Failed to sleep-->" + ex);
                }
            }
        }
    }
}
