/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.threads;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author kerollos.asaad
 */
public class MonitorThread implements Runnable {

    private int INTERFACES_MONITOR_THREAD_SLEEP_TIME;

    @Override
    public void run() {
        //CommonLogger.businessLogger.info("******************Monitor Thread Started***************");
        Thread.currentThread().setName("Monitoring Thread");
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
//        StringBuilder monitorLog = new StringBuilder();
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

                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + "Heap Utilization Statistics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());

                for (Map.Entry<Long, QueueModel> queue : SystemLookups.QUEUE_LIST.entrySet()) {
                    try {
//                        monitorLog//.append("\n ###############################################")
//                                .append("\n #### Enqueue ").append(queue.getValue().getAppName()).append(" Data")
//                                .append("\n Queue Size: ").append(ConfigurationManager.smsToBeSent.get(queue.getKey()).size())
//                                .append(" || Queue Remaining Capacity: ").append(ConfigurationManager.smsToBeSent.get(queue.getKey()).remainingCapacity())
//                                //.append("\n ShutDownFlag Status: ").append(ConfigurationManager.shutdownFlagPool.get(queue.getKey()))
//                                .append("\n Active Threads Count: ").append(((ThreadPoolExecutor) ConfigurationManager.enQueueThreadPoolHashMap.get(queue.getKey())).getActiveCount());
////                                  .append("\n DatabasePool Stats \n")
////                                  .append(Utility.getHikariConnectionPoolStats(databaseConnectionPool.get(queue.getKey())));
//                        monitorLog.append("\n ###############################################");

                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueue " + queue.getValue().getAppName() + " Data")
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.smsToBeSent.get(queue.getKey()).size())
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.smsToBeSent.get(queue.getKey()).remainingCapacity())
                                .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) ConfigurationManager.enQueueThreadPoolHashMap.get(queue.getKey())).getActiveCount()).build());

                    } catch (Exception e) {
//                        monitorLog.append("\n Error Reading Data " + e)
//                                .append("\n ###############################################");
                        CommonLogger.businessLogger.error(e);
                        CommonLogger.errorLogger.error(e, e);
                    }
                }

                try {

//                    monitorLog.append("\n Reloading Thread || Reloading Times: " + ReloadingThread.reloadedTimes + " | Thread Status: " + ConfigurationManager.reloadingThread.getState() + " | Sleep Time: " + ReloadingThread.sleepTime)
//                            .append("\n Logging Threads || Queue Size: " + ConfigurationManager.requestsToBeLogged.size()
//                                    + " | Queue Remaining Capacity: " + ConfigurationManager.requestsToBeLogged.remainingCapacity()
//                                    + " | Active Threads Count: " + ((ThreadPoolExecutor) ConfigurationManager.logThreadPool).getActiveCount()
//                                    + " | PullTimeOut: " + LoggingManagerThread.pullTimeOut + " | MAX_NUM_OR_RETRIES_LTHREAD: " + LoggingManagerThread.MAX_NUM_OF_RETRIES_LTHREAD
//                                    + " | MAX_LOGGING_DB_ARRAY_SIZE: " + LoggingManagerThread.MAX_LOGGING_DB_ARRAY_SIZE)
//                    .append("\n Archiving Threads || Queue Size: " + ConfigurationManager.messagesToBeArchived.size()
//                            + " | Queue Remaining Capacity: " + ConfigurationManager.messagesToBeArchived.remainingCapacity()
//                            + " | Active Threads Count: " + ((ThreadPoolExecutor) ConfigurationManager.archiveThreadPool).getActiveCount()
//                            + " | PullTimeOut: " + ArchiveManagerThread.pullTimeOut + " | MAX_NUM_OR_RETRIES_ATHREAD: " + ArchiveManagerThread.MAX_NUM_OF_RETRIES_ATHREAD
//                            + " | MAX_LOGGING_DB_ARRAY_SIZE: " + ArchiveManagerThread.MAX_ARCHIVING_DB_ARRAY_SIZE)
//                            .append("\n SMS Validation Threads || Queue Size: " + ConfigurationManager.smsToBeValidated.size()
//                                    + " | Queue Remaining Capacity: " + ConfigurationManager.smsToBeValidated.remainingCapacity()
//                                    + " | Active Threads Count: " + ((ThreadPoolExecutor) ConfigurationManager.smsValidationThreadPool).getActiveCount()
//                                    + " | PullTimeOut: " + SmsValidationThread.pullTimeOut)
//                            .append("\n SMS Rollback Threads || Queue Size: " + ConfigurationManager.smsToBeRollbacked.size()
//                                    + " | Queue Remaining Capacity: " + ConfigurationManager.smsToBeRollbacked.remainingCapacity()
//                                    + " | Active Threads Count: " + ((ThreadPoolExecutor) ConfigurationManager.rollBacksmsThreadPool).getActiveCount()
//                                    + " | PullTimeOut: " + RollbackSMSThread.pullTimeOut)
//                            .append("\n Concurrent Requests: " + ConfigurationManager.concurrentRequests.get()
//                                    + " | Advertisment Consult Accumlator: " + ConfigurationManager.advertismentConsultAccumulator.get()
//                                    + " | Advertisment Consult Success Count: " + ConfigurationManager.advertismentConsultSuccessCount.get()
//                                    + " | Advertisment Consult Failed Count: " + ConfigurationManager.advertismentConsultFailedCount.get()
//                                    + " | Consult Accumlator: " + ConfigurationManager.consultAccumulator.get()
//                                    + " | Consult Success Count: " + ConfigurationManager.consultSuccessCount.get()
//                                    + " | Consult Failed Count: " + ConfigurationManager.consultFailedCount.get()
//                                    + " | Send Bulk Sms Offline Accumlator: " + ConfigurationManager.sendBulkSmsOfflineAccumulator.get()
//                                    + " | Send Bulk Sms Offline Success Count: " + ConfigurationManager.sendBulkSmsOfflineSuccessCount.get()
//                                    + " | Send Bulk Sms Offline Failed Count: " + ConfigurationManager.sendBulkSmsOfflineFailedCount.get()
//                                    + " | Send Sms Accumlator: " + ConfigurationManager.sendSmsAccumulator.get()
//                                    + " | Send Sms Success Count: " + ConfigurationManager.sendSmsSuccessCount.get()
//                                    + " | Send Sms Failed Count: " + ConfigurationManager.sendSmsFailedCount.get()
//                                    + " | Ready Accumlator: " + ConfigurationManager.readyCheckAccumulator.get()
//                                    + " | Ready Success Count: " + ConfigurationManager.readyCheckSuccessCount.get()
//                                    + " | Ready Failed Count: " + ConfigurationManager.readyCheckFailedCount.get()
//                                    + " | Check Interface Accumlator: " + ConfigurationManager.checkInterfaceAccumulator.get()
//                                    + " | Check Interface Success Count: " + ConfigurationManager.checkInterfaceSuccessCount.get()
//                                    + " | Check Interface Failed Count: " + ConfigurationManager.checkInterfaceFailedCount.get()
//                                    + " | Refresh Accumlator: " + ConfigurationManager.refreshAccumulator.get()
//                                    + " | Refresh Success Count: " + ConfigurationManager.refreshSuccessCount.get()
//                                    + " | Refresh Failed Count: " + ConfigurationManager.refreshFailedCount.get())
//                            .append("\n Monitoring Thread || Thread Status: " + ConfigurationManager.monitoringThread.getState() + " | Sleep Time: " + INTERFACES_MONITOR_THREAD_SLEEP_TIME)
//                            .append("\n ###############################################");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reloading Thread Stats")
                            .put(GeneralConstants.StructuredLogKeys.RELOADING_TIME, ReloadingThread.reloadedTimes)
                            .put(GeneralConstants.StructuredLogKeys.THREAD_STATUS, ConfigurationManager.reloadingThread.getState() + "")
                            .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, ReloadingThread.sleepTime)
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.requestsToBeLogged.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.requestsToBeLogged.remainingCapacity())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) ConfigurationManager.logThreadPool).getActiveCount())
                            .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, LoggingManagerThread.pullTimeOut)
                            .put(GeneralConstants.StructuredLogKeys.THREAD_MAX_RETRIES, LoggingManagerThread.MAX_NUM_OF_RETRIES_LTHREAD).
                            put(GeneralConstants.StructuredLogKeys.MAX_LOGGING_DB_ARRAY_SIZE, LoggingManagerThread.MAX_LOGGING_DB_ARRAY_SIZE).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archived Thread Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.messagesToBeArchived.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.messagesToBeArchived.remainingCapacity())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) ConfigurationManager.archiveThreadPool).getActiveCount())
                            .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, ArchiveManagerThread.pullTimeOut)
                            .put(GeneralConstants.StructuredLogKeys.THREAD_MAX_RETRIES, ArchiveManagerThread.MAX_NUM_OF_RETRIES_ATHREAD).
                            put(GeneralConstants.StructuredLogKeys.MAX_ARCHIVING_DB_ARRAY_SIZE, ArchiveManagerThread.MAX_ARCHIVING_DB_ARRAY_SIZE).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Validation Thread Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.smsToBeValidated.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.smsToBeValidated.remainingCapacity())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) ConfigurationManager.smsValidationThreadPool).getActiveCount())
                            .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, SmsValidationThread.pullTimeOut).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Rollback SMS Thread Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.smsToBeRollbacked.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.smsToBeRollbacked.remainingCapacity())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) ConfigurationManager.rollBacksmsThreadPool).getActiveCount())
                            .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, RollbackSMSThread.pullTimeOut).build());

                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Application Stats")
                            .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.get()).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Advertisment Consult Stats")
                            .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.advertismentConsultAccumulator.get())
                            .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.advertismentConsultSuccessCount.get())
                            .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.advertismentConsultFailedCount.get()).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Consult Stats")
                            .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.consultAccumulator.get())
                            .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.consultSuccessCount.get())
                            .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.consultFailedCount.get()).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Send Bulk Sms Offline Stats")
                            .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.sendBulkSmsOfflineAccumulator.get())
                            .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.sendBulkSmsOfflineSuccessCount.get())
                            .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendBulkSmsOfflineFailedCount.get()).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Send Sms Stats")
                            .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.sendSmsAccumulator.get())
                            .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.sendSmsSuccessCount.get())
                            .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendSmsFailedCount.get()).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ready Stats")
                            .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.readyCheckAccumulator.get())
                            .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.readyCheckSuccessCount.get())
                            .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.readyCheckFailedCount.get()).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Check Interface Stats")
                            .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.checkInterfaceAccumulator.get())
                            .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.checkInterfaceSuccessCount.get())
                            .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.checkInterfaceFailedCount.get()).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Refresh Stats")
                            .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.refreshAccumulator.get())
                            .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.refreshSuccessCount.get())
                            .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.refreshFailedCount.get()).build());

                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Thread Status")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_STATUS, ConfigurationManager.monitoringThread.getState() + "")
                            .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, INTERFACES_MONITOR_THREAD_SLEEP_TIME).build());
                } catch (Exception e) {
//                    monitorLog.append("\n Error Reading Data " + e)
//                            .append("\n ###############################################");
                    CommonLogger.businessLogger.error(e);
                    CommonLogger.errorLogger.error(e, e);
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
                INTERFACES_MONITOR_THREAD_SLEEP_TIME = Integer.valueOf(ConfigurationManager.SRC_ID_SYSTEM_PROPERIES.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.MONITOR_THREAD_SLEEP_TIME));
                Thread.sleep(INTERFACES_MONITOR_THREAD_SLEEP_TIME);
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error("Monitor Thread Caught InterruptedException---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught InterruptedException---->" + e, e);
            }
        }
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }
}
