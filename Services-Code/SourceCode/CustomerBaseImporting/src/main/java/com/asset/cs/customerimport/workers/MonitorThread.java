/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.workers;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.managers.CustomerImportingManager;
import com.asset.cs.customerimport.managers.ResourcesCachingManager;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author Mina
 */
public class MonitorThread extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.workers.MonitorThread";
    private long sleepTime = 10000;
    private int javaQueueExceedPerc = 75;
    private double queuePercentage = 0;

    public MonitorThread() {

    }

    public void run() {
        CommonLogger.businessLogger.info("--------- Starting System Monitor : " + this.getName() + " ----------");
        while (!this.isWorkerShutDownFlag()) {
            try {
                try {
                    sleepTime = Long.parseLong(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_MONITOR_SLEEP_TIME));
                    javaQueueExceedPerc = Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_JAVA_QUEUE_EXCEED_PERCENTAGE));
                } catch (Exception e) {
                    sleepTime = 10000;
                    javaQueueExceedPerc = 75;
                    CommonLogger.errorLogger.error("Colud not load System Properties", e);
                    CommonLogger.businessLogger.error("Colud not load System Properties" + e);
                }
//                CommonLogger.businessLogger.info("##### Heap utilization statistics [MB] #####");
//                CommonLogger.businessLogger.info("Used Memory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
//                CommonLogger.businessLogger.info("Free Memory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)));
//                CommonLogger.businessLogger.info("Total Memory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
//                CommonLogger.businessLogger.info("Max Memory:" + (Runtime.getRuntime().maxMemory() / (1024 * 1024)));
//                CommonLogger.businessLogger.info("===============================Que Sizes======================================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Heap utilization statistics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory()) / (1024 * 102))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory()) / (1024 * 1024))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory()) / (1024 * 1024)).build());
                if (CustomerImportingManager.readersDataQueue != null) {
//                    CommonLogger.businessLogger.info("Readers Data Queue Size=>[" + CustomerImportingManager.readersDataQueue.size() + "]  Remaining=>[" + CustomerImportingManager.readersDataQueue.remainingCapacity() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reader data queue stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, CustomerImportingManager.readersDataQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, CustomerImportingManager.readersDataQueue.remainingCapacity()).build());
                    queuePercentage = (CustomerImportingManager.readersDataQueue.size() / CustomerImportingManager.readerQueueSize) * 100;
                    if (queuePercentage >= javaQueueExceedPerc) {
                        CommonLogger.errorLogger.error("Readers Java Queue Exceeds Configuration Percentage, Send MOM");
                        // CommonLogger.businessLogger.error("Readers Java Queue Exceeds Configuration Percentage, Send MOM");
                        sendMom(ErrorCodes.JAVA_QUEUE_EXCEEDS_PERCENTAGE, "Readers Java Queue Exceeds Configuration Percentage", "Queue Size=[" + CustomerImportingManager.readersDataQueue.size() + "] Capacity=[" + CustomerImportingManager.readerQueueSize + "]");
                    }
                }
                Iterator workersQueuesIterator = CustomerImportingManager.writersDataQueuesMap.entrySet().iterator();
                while (workersQueuesIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) workersQueuesIterator.next();
                    Integer queueId = (Integer) mapEntry.getKey();
                    BlockingQueue queue = (BlockingQueue) mapEntry.getValue();
//                    CommonLogger.businessLogger.info("Worker Data Queue [" + (queueId + 1) + "] Size=> [" + queue.size() + "]  Remaining=>" + queue.remainingCapacity() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Worker Data Queue stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, (queueId + 1))
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, queue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, queue.remainingCapacity()).build());
                    queuePercentage = (queue.size() / CustomerImportingManager.writerQueueSize) * 100;
                    if (queuePercentage >= javaQueueExceedPerc) {
                        CommonLogger.errorLogger.error("Writer Java QueueID=[" + (queueId + 1) + "]  Exceeds Configuration Percentage, Send MOM");
                        sendMom(ErrorCodes.JAVA_QUEUE_EXCEEDS_PERCENTAGE, "Writer Java Queue Exceeds Configuration Percentage", "Queue Id=[" + (queueId + 1) + "] Size=[" + queue.size() + "] Capacity=[" + CustomerImportingManager.writerQueueSize + "]");
                    }
                }
//                CommonLogger.businessLogger.info("===============================Thread Pools======================================");
                if (CustomerImportingManager.readerWorkersPool != null) {
//                    CommonLogger.businessLogger.info("Readers Thread Pool  Size=>[" + ((ThreadPoolExecutor) CustomerImportingManager.readerWorkersPool).getPoolSize() + "] "
//                            + "  Active=>[" + ((ThreadPoolExecutor) CustomerImportingManager.readerWorkersPool).getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Readers Thread Pool")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) CustomerImportingManager.readerWorkersPool).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) CustomerImportingManager.readerWorkersPool).getActiveCount()).build());
                }

                if (CustomerImportingManager.preparingWorkersPool != null) {
//                    CommonLogger.businessLogger.info("Preparing Thread Pool  Size=>[" + ((ThreadPoolExecutor) CustomerImportingManager.preparingWorkersPool).getPoolSize() + "] "
//                            + "  Active=>[" + ((ThreadPoolExecutor) CustomerImportingManager.preparingWorkersPool).getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Preparing Thread Pool")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) CustomerImportingManager.readerWorkersPool).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) CustomerImportingManager.readerWorkersPool).getActiveCount()).build());
                }
                Iterator workersPoolsIterator = CustomerImportingManager.writersWorkersMap.entrySet().iterator();
                while (workersPoolsIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) workersPoolsIterator.next();
                    Integer poolId = (Integer) mapEntry.getKey();
                    ThreadPoolExecutor pool = (ThreadPoolExecutor) mapEntry.getValue();
//                    CommonLogger.businessLogger.info("Writer Thread Pool [" + (poolId + 1) + "] Size=>[" + pool.getPoolSize() + "] "
//                            + "  Active=>[" + pool.getActiveCount() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Writer Thread Pool Stats")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_ID, (poolId + 1))
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, pool.getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, pool.getActiveCount()).build());
                }

                CommonLogger.businessLogger.info("===============================DataBase Connections ======================================");
                if (DataSourceManger.getHikariDataSourse() != null) {
//                    CommonLogger.businessLogger.info(Utility.getHikariConnectionPoolStats(DataSourceManger.getHikariDataSourse()));
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Connection Stats")
                            .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, Utility.getHikariConnectionPoolStats(DataSourceManger.getHikariDataSourse())).build());
                } else {
                    CommonLogger.businessLogger.info(" HikariCP DataSource is null");
                }

//                CommonLogger.businessLogger.info("####################Sleeping for " + sleepTime + "#########################");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread Sleeping time")
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
            } catch (Throwable th) {
                CommonLogger.errorLogger.error("Fatal Exception => ", th);
                CommonLogger.businessLogger.error("Fatal Error-->" + th);
            } finally {
                try {
                    this.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    CommonLogger.errorLogger.error("Monitor Thread Failed to sleep", ex);
                    CommonLogger.businessLogger.error("Monitor Thread Failed to sleep" + ex);
                }
            }
        }
        CommonLogger.businessLogger.info("--------- Shutdown System Monitor : " + this.getName() + " ----------");
    }

    private void sendMom(String errorCode, String errorMsg, String errorParam) {
        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY)));
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        errorModel.setErrorCode(errorCode);
        errorModel.setErrorMessage(errorMsg);
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setErrorParams(errorParam);
        Utility.sendMOMAlarem(errorModel);
    }
}
