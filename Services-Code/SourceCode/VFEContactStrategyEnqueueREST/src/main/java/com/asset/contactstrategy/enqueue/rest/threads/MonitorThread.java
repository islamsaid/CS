/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.enqueue.rest.threads;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.enqueue.rest.common.ConfigurationManager;
import static com.asset.contactstrategy.enqueue.rest.common.ConfigurationManager.enqueueSmsFailedCount;
import java.util.Map;
import static com.asset.contactstrategy.enqueue.rest.common.ConfigurationManager.senderQueuesDatabaseConnectionPool;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author kerollos.asaad
 */
public class MonitorThread implements Runnable {

    private int MONITOR_THREAD_SLEEP_TIME;

    @Override
    public void run() {
        //CommonLogger.businessLogger.info("******************Monitor Thread Started***************");
        Thread.currentThread().setName("Monitoring Thread");
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
//        StringBuilder monitorLog;
        while (!ConfigurationManager.MONITOR_THREAD_SHUTDOWN_FLAG) {
//            monitorLog = new StringBuilder();
            try {
//                monitorLog.append("***************************************Monitoring Started*********************************************");
//                monitorLog.append("\n ##### Heap utilization statistics [MB] #######")
//                          .append("\n [MonitoringThread] Used Memory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Free Memory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Total Memory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Max Memory:" + (Runtime.getRuntime().maxMemory() / (1024 * 1024)))
//                          .append("\n ###############################################");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Heap Utilization Statistics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());
                for (Map.Entry<String, QueueModel> queue : ConfigurationManager.senderQueueListServiceAndStatusApproved.entrySet()) {
                    try {
//                        monitorLog//.append("\n ###############################################")
//                                .append("\n #### Enqueue ").append(queue.getValue().getAppName()).append(" Data")
//                                //                                  .append("\n Queue Size: ").append(ConfigurationManager.smsToBeSent.get(queue.getKey()).size())
//                                //                                  .append(" || Queue Remaining Capacity: ").append(ConfigurationManager.smsToBeSent.get(queue.getKey()).remainingCapacity())
//                                //.append("\n ShutDownFlag Status: ").append(ConfigurationManager.shutdownFlagPool.get(queue.getKey()))
//                                //                                  .append("\n Active Threads Count: ").append(((ThreadPoolExecutor) ConfigurationManager.enQueueThreadPoolHashMap.get(queue.getKey())).getActiveCount())
//                                .append("\n DatabasePool Stats \n")
//                                //                                  .append(Utility.getC3P0ConnectionPoolStats(senderQueuesDatabaseConnectionPool.get(queue.getKey()).getC3p0ConnectionPool()));
//                                .append(Utility.getHikariConnectionPoolStats(senderQueuesDatabaseConnectionPool.get(queue.getKey()).getHikariDataSourse()));
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueue Data")
                                .put(GeneralConstants.StructuredLogKeys.APP_NAME, queue.getValue().getAppName())
                                .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, Utility.getHikariConnectionPoolStats(senderQueuesDatabaseConnectionPool.get(queue.getKey()).getHikariDataSourse())).build());
//                        monitorLog.append("\n ###############################################");
                    } catch (Exception e) {
                        CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error Reading Data")
                                .put(GeneralConstants.StructuredLogKeys.APP_NAME, queue.getValue().getAppName()).build());
//                        monitorLog.append("\n Error Reading Data " + e)
//                                .append("\n ###############################################");
                    }
                }

                try {
//                    monitorLog.append("\n Reloading Thread || Reloading Times: " + ReloadingThread.reloadedTimes + " | Thread Status: " + ConfigurationManager.reloadingThread.getState() + " | Sleep Time: " + ReloadingThread.sleepTime)
//                            .append("\n Logging Threads || Queue Size: " + ConfigurationManager.loggingQueue.size()
//                                    + " | Queue Remaining Capacity: " + ConfigurationManager.loggingQueue.remainingCapacity()
//                                    + " | Active Threads Count: " + ((ThreadPoolExecutor) ConfigurationManager.logThreadPool).getActiveCount()
//                                    + " | PullTimeOut: " + Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT_VALUE + " | MAX_NUM_OR_RETRIES_LTHREAD: " + Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD_VALUE
//                                    + " | MAX_LOGGING_DB_ARRAY_SIZE: " + Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE_VALUE)
//                            .append("\n Concurrent Requests: " + ConfigurationManager.concurrentRequests.get()
//                                    + " | Enqueue Sms Accumlator: " + ConfigurationManager.enqueueSmsAccumulator.get()
//                                    + " | Enqueue Sms Success Count: " + ConfigurationManager.enqueueSmsSuccessCount.get()
//                                    + " | Enqueue Sms Failed Count: " + ConfigurationManager.enqueueSmsFailedCount.get()
//                                    + " | Enqueue Birdge Sms Accumlator: " + ConfigurationManager.enqueueBridgeSmsAccumulator.get()
//                                    + " | Enqueue Birdge Sms Success Count: " + ConfigurationManager.enqueueBridgeSmsSuccessCount.get()
//                                    + " | Enqueue Birdge Sms Failed Count: " + ConfigurationManager.enqueueBridgeSmsFailedCount.get()
//                                    + " | Ready Accumlator: " + ConfigurationManager.readyCheckAccumulator.get()
//                                    + " | Ready Success Count: " + ConfigurationManager.readyCheckSuccessCount.get()
//                                    + " | Ready Failed Count: " + ConfigurationManager.readyCheckFailedCount.get()
//                                    + " | Check Interface Accumlator: " + ConfigurationManager.checkInterfaceAccumulator.get()
//                                    + " | Check Interface Success Count: " + ConfigurationManager.checkInterfaceSuccessCount.get()
//                                    + " | Check Interface Failed Count: " + ConfigurationManager.checkInterfaceFailedCount.get()
//                                    + " | Refresh Accumlator: " + ConfigurationManager.refreshAccumulator.get()
//                                    + " | Refresh Success Count: " + ConfigurationManager.refreshSuccessCount.get()
//                                    + " | Refresh Failed Count: " + ConfigurationManager.refreshFailedCount.get())
//                                        
//                    //                              .append("\n SMS Validation Threads || Queue Size: " + ConfigurationManager.smsToBeValidated.size() 
//                    //                                        + " | Queue Remaining Capacity: " + ConfigurationManager.smsToBeValidated.remainingCapacity() 
//                    //                                        + " | Active Threads Count: " + ((ThreadPoolExecutor)ConfigurationManager.smsValidationThreadPool).getActiveCount()
//                    //                                        + " | PullTimeOut: " + SmsValidationThread.pullTimeOut)
//
//                    //                              .append("\n SMS Rollback Threads || Queue Size: " + ConfigurationManager.smsToBeRollbacked.size() 
//                    //                                        + " | Queue Remaining Capacity: " + ConfigurationManager.smsToBeRollbacked.remainingCapacity() 
//                    //                                        + " | Active Threads Count: " + ((ThreadPoolExecutor)ConfigurationManager.rollBacksmsThreadPool).getActiveCount()
//                    //                                        + " | PullTimeOut: " + RollbackSMSThread.pullTimeOut)
//
//                    .
//                    append("\n Monitoring Thread || Thread Status: " + ConfigurationManager.monitoringThread.getState() + " | Sleep Time: " + MONITOR_THREAD_SLEEP_TIME)
//                            .append("\n ###############################################");
//                    
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Started")
                        .put(GeneralConstants.StructuredLogKeys.RELOADING_TIME, ReloadingThread.reloadedTimes)
                        .put(GeneralConstants.StructuredLogKeys.THREAD_STATUS, ConfigurationManager.reloadingThread.getState())
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, ReloadingThread.sleepTime)
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.loggingQueue.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.loggingQueue.remainingCapacity())
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) ConfigurationManager.logThreadPool).getActiveCount())
                        .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT_VALUE)
                        .put(GeneralConstants.StructuredLogKeys.THREAD_MAX_RETRIES, Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD)
                        .put(GeneralConstants.StructuredLogKeys.MAX_LOGGING_DB_ARRAY_SIZE, Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE_VALUE)
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for EnqueueSMS")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.enqueueSmsAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.enqueueSmsSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.enqueueSmsFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for EnqueueBridgeSMS")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.enqueueBridgeSmsAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.enqueueBridgeSmsSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.enqueueBridgeSmsFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for Ready Interface")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.readyCheckAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.readyCheckSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.readyCheckFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for Check Interface")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.checkInterfaceAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.checkInterfaceSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.checkInterfaceFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for Refresh Interface")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.refreshAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.refreshSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.refreshFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Thread Status")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_STATUS, ConfigurationManager.monitoringThread.getState())
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, MONITOR_THREAD_SLEEP_TIME).build());
                } catch (Exception e) {
                CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error reading data")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_STATUS, ConfigurationManager.monitoringThread.getState()).build());
                }

//                monitorLog.append("***************************************Monitoring Finished*********************************************");
//                CommonLogger.businessLogger.info(monitorLog.toString());
            } catch (Exception e) {
//                monitorLog.append("***************************************Monitoring Finished*********************************************");
//                CommonLogger.businessLogger.info(monitorLog.toString());
                CommonLogger.businessLogger.error("Monitor Thread Caught Exception---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught Exception---->" + e, e);
            }
            try {
                MONITOR_THREAD_SLEEP_TIME = Integer.valueOf(ConfigurationManager.SRC_ID_SYSTEM_PROPERIES.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.MONITOR_THREAD_SLEEP_TIME));
                Thread.sleep(MONITOR_THREAD_SLEEP_TIME);
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error("Monitor Thread Caught InterruptedException---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught InterruptedException---->" + e, e);
            }
        }
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }
}
