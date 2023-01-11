/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfacereports.threads;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.interfacereports.common.ConfigurationManager;
import java.util.Map;

/**
 *
 * @author kerollos.asaad
 */
public class MonitorThread implements Runnable {

    private int INTERFACE_REPORTS_MONITOR_THREAD_SLEEP_TIME;

    @Override
    public void run() {
        Thread.currentThread().setName("Monitoring Thread");
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
//        StringBuilder monitorLog = new StringBuilder();
        while (!ConfigurationManager.MONITOR_THREAD_SHUTDOWN_FLAG) {
//            monitorLog = new StringBuilder();
//            monitorLog.append("***************************************Monitoring Started*********************************************");
            try {
//                monitorLog.append("\n ##### Heap utilization statistics [MB] #######")
//                          .append("\n [MonitoringThread] Used Memory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Free Memory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Total Memory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Max Memory:" + (Runtime.getRuntime().maxMemory() / (1024 * 1024)))
//                          .append("\n ###############################################");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Heap Utilization Statistic [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());
//                for (Map.Entry<Long, QueueModel> queue : SystemLookups.QUEUE_LIST.entrySet())
//                {
//                try {
//                    monitorLog.append("\n Reloading Thread || Reloading Times: " + ReloadingThread.reloadedTimes + " | Sleep Time: " + ReloadingThread.sleepTime)
//                            .append("\n Logging Threads || Queue Size: " + ConfigurationManager.requestsToBeLogged.size()
//                                    + " | Queue Remaining Capacity: " + ConfigurationManager.requestsToBeLogged.remainingCapacity()
//                                    + " | PullTimeOut: " + LoggingManagerThread.pullTimeOut + " | MAX_NUM_OR_RETRIES_ATHREAD: " + LoggingManagerThread.MAX_NUM_OF_RETRIES_LTHREAD
//                                    + " | MAX_LOGGING_DB_ARRAY_SIZE: " + LoggingManagerThread.MAX_LOGGING_DB_ARRAY_SIZE)
//                            .append("\n Concurrent Requests: " + ConfigurationManager.concurrentRequests.get()
//                                    + " | RetrieveMessageStatus Accumlator: " + ConfigurationManager.retrieveMessageStatusAccumulator.get()
//                                    + " | RetrieveMessageStatus Success Count: " + ConfigurationManager.retrieveMessageStatusSuccessCount.get()
//                                    + " | RetrieveMessageStatus Failed Count: " + ConfigurationManager.retrieveMessageStatusFailedCount.get()
//                                    + " | RetrieveSMSsInterface Accumlator: " + ConfigurationManager.retrieveSMSsInterfaceAccumulator.get()
//                                    + " | RetrieveSMSsInterface Success Count: " + ConfigurationManager.retrieveSMSsInterfaceSuccessCount.get()
//                                    + " | RetrieveSMSsInterface Failed Count: " + ConfigurationManager.retrieveSMSsInterfaceFailedCount.get()
//                                    + " | Ready Accumlator: " + ConfigurationManager.readyCheckAccumulator.get()
//                                    + " | Ready Success Count: " + ConfigurationManager.readyCheckSuccessCount.get()
//                                    + " | Ready Failed Count: " + ConfigurationManager.readyCheckFailedCount.get()
//                                    + " | Check Interface Accumlator: " + ConfigurationManager.checkInterfaceAccumulator.get()
//                                    + " | Check Interface Success Count: " + ConfigurationManager.checkInterfaceSuccessCount.get()
//                                    + " | Check Interface Failed Count: " + ConfigurationManager.checkInterfaceFailedCount.get())
//                            .append("\n Monitoring Thread || Sleep Time: " + INTERFACE_REPORTS_MONITOR_THREAD_SLEEP_TIME)
//                            .append("\n ###############################################");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread Stats")
                        .put(GeneralConstants.StructuredLogKeys.RELOADING_TIME, ReloadingThread.reloadedTimes)
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, ReloadingThread.sleepTime)
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.requestsToBeLogged.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.requestsToBeLogged.remainingCapacity())
                        .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, LoggingManagerThread.pullTimeOut)
                        .put(GeneralConstants.StructuredLogKeys.THREAD_MAX_RETRIES, LoggingManagerThread.MAX_NUM_OF_RETRIES_LTHREAD)
                        .put(GeneralConstants.StructuredLogKeys.MAX_LOGGING_DB_ARRAY_SIZE, LoggingManagerThread.MAX_LOGGING_DB_ARRAY_SIZE)
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for RetrieveMessageStatus")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.retrieveMessageStatusAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.retrieveMessageStatusSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.retrieveMessageStatusFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for RetrieveSMSsInterface")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.retrieveSMSsInterfaceAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.retrieveSMSsInterfaceSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.retrieveSMSsInterfaceFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for ReadyInterface")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.readyCheckAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.readyCheckSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.readyCheckFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MonitorThread for Check InterFace")
                        .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.checkInterfaceAccumulator.get())
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.checkInterfaceSuccessCount.get())
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.checkInterfaceFailedCount.get()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Thread Status")
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, INTERFACE_REPORTS_MONITOR_THREAD_SLEEP_TIME).build());

//                } catch (Exception e) {
//                    CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage()).build());
//                    CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage()).build(), e);
////                    monitorLog.append("\n Error Reading Data " + e)
////                            .append("\n ###############################################");
//                }
//                }
//                monitorLog.append("***************************************Monitoring Finished*********************************************");
//                CommonLogger.businessLogger.info(monitorLog.toString());
            } catch (Exception e) {
//                monitorLog.append("***************************************Monitoring Finished*********************************************");
//                CommonLogger.businessLogger.info(monitorLog.toString());
                CommonLogger.businessLogger.error("Monitor Thread Caught Exception---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught Exception---->" + e, e);
            }

            try {
                INTERFACE_REPORTS_MONITOR_THREAD_SLEEP_TIME = Integer.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.INTERFACE_REPORTS_MONITOR_THREAD_SLEEP_TIME));
                Thread.sleep(INTERFACE_REPORTS_MONITOR_THREAD_SLEEP_TIME);
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error("Monitor Thread Caught InterruptedException---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught InterruptedException---->" + e, e);
            }
        }
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }
}
