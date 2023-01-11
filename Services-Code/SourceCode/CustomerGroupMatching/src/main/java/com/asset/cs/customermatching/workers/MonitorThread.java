/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.workers;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customermatching.constants.EngineDefines;
import com.asset.cs.customermatching.managers.CustomerMatchingManager;
import com.asset.cs.customermatching.managers.ResourcesCachingManager;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author Mina
 */
public class MonitorThread extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customermatching.workers.MonitorThread";
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
                if (CustomerMatchingManager.adsMSISDNSQueue != null) {
//                    CommonLogger.businessLogger.info("ads MSISDNS Queue Size=>[" + CustomerMatchingManager.adsMSISDNSQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.adsMSISDNSQueue.remainingCapacity() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ads MSISDNs Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.adsMSISDNSQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.adsMSISDNSQueue.remainingCapacity()).build());
                }
                if (CustomerMatchingManager.adsfilesQueue != null) {
//                    CommonLogger.businessLogger.info("ads Files Queue Size=>[" + CustomerMatchingManager.adsfilesQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.adsfilesQueue.remainingCapacity() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ads Files Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.adsfilesQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.adsfilesQueue.remainingCapacity()).build());
                }

                if (CustomerMatchingManager.smsMSISDNSQueue != null) {
//                    CommonLogger.businessLogger.info("sms MSISDNS Queue Size=>[" + CustomerMatchingManager.smsMSISDNSQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.smsMSISDNSQueue.remainingCapacity() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS MSISDNs Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.smsMSISDNSQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.smsMSISDNSQueue.remainingCapacity()).build());
                }
                if (CustomerMatchingManager.smsfilesQueue != null) {
//                    CommonLogger.businessLogger.info("sms Files Queue Size=>[" + CustomerMatchingManager.smsfilesQueue.size() + "]  Remaining=>[" + CustomerMatchingManager.smsfilesQueue.remainingCapacity() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Files Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerMatchingManager.smsfilesQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerMatchingManager.smsfilesQueue.remainingCapacity()).build());
                }

//                CommonLogger.businessLogger.info("===============================Thread Pools======================================");
                if (CustomerMatchingManager.smsCriteriaWorkersPool != null) {
//                    CommonLogger.businessLogger.info("sms Criteria Workers Thread Pool  Size=>[" + ((ThreadPoolExecutor) CustomerMatchingManager.smsCriteriaWorkersPool).getPoolSize() + "] "
//                            + "  Active=>[" + ((ThreadPoolExecutor) CustomerMatchingManager.smsCriteriaWorkersPool).getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Criteria Workers Thread Pool Stats")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) CustomerMatchingManager.smsCriteriaWorkersPool).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) CustomerMatchingManager.smsCriteriaWorkersPool).getActiveCount()).build());
                }
                if (CustomerMatchingManager.smsUploadWorkersPool != null) {
//                    CommonLogger.businessLogger.info("sms Upload Workers Thread Pool  Size=>[" + ((ThreadPoolExecutor) CustomerMatchingManager.smsUploadWorkersPool).getPoolSize() + "] "
//                            + "  Active=>[" + ((ThreadPoolExecutor) CustomerMatchingManager.smsUploadWorkersPool).getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Upload Workers Thread Pool")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) CustomerMatchingManager.smsUploadWorkersPool).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) CustomerMatchingManager.smsUploadWorkersPool).getActiveCount()).build());
                }
                if (CustomerMatchingManager.adsCriteriaWorkersPool != null) {
//                    CommonLogger.businessLogger.info("ads Criteria Workers Thread Pool  Size=>[" + ((ThreadPoolExecutor) CustomerMatchingManager.adsCriteriaWorkersPool).getPoolSize() + "] "
//                            + "  Active=>[" + ((ThreadPoolExecutor) CustomerMatchingManager.adsCriteriaWorkersPool).getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ads Criteria Workers Thread Pool")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) CustomerMatchingManager.adsCriteriaWorkersPool).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) CustomerMatchingManager.adsCriteriaWorkersPool).getActiveCount()).build());
                }
                if (CustomerMatchingManager.adsUploadWorkersPool != null) {
//                    CommonLogger.businessLogger.info("ads Upload Workers Thread Pool  Size=>[" + ((ThreadPoolExecutor) CustomerMatchingManager.adsUploadWorkersPool).getPoolSize() + "] "
//                            + "  Active=>[" + ((ThreadPoolExecutor) CustomerMatchingManager.adsUploadWorkersPool).getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ads Upload Workers Thread Pool")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) CustomerMatchingManager.adsUploadWorkersPool).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) CustomerMatchingManager.adsUploadWorkersPool).getActiveCount()).build());
                }
//                CommonLogger.businessLogger.info("===============================DataBase Connections ======================================");
                if (DataSourceManger.getHikariDataSourse() != null) {
//                    CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(DataSourceManger.getC3p0ConnectionPool()));
                    CommonLogger.businessLogger.info(Utility.getHikariConnectionPoolStats(DataSourceManger.getHikariDataSourse()));
                } else {
                    CommonLogger.businessLogger.info(" C3P0 DataSource is null");
                }
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sleeping Time")
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
//                CommonLogger.businessLogger.info("####################Sleeping for " + sleepTime + "#########################");
            } catch (Throwable th) {
                CommonLogger.errorLogger.error("Fatal Exception => " + th, th);
                CommonLogger.businessLogger.error("Fatal Exception => " + th);
            } finally {
                try {
                    this.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    CommonLogger.errorLogger.error("Monitor Thread Failed to sleep" + ex, ex);
                    CommonLogger.businessLogger.error("Monitor Thread Failed to sleep-->" + ex);
                }
            }
        }
    }
}
