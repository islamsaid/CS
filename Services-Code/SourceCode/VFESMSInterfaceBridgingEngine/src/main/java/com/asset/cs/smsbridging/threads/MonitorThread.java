package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.cs.smsbridging.models.QueueNeedsHolder;
import com.asset.cs.smsbridging.models.ServiceNeedsHolder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author aya.moawed 2595
 */
public class MonitorThread extends Thread {

    @Override
    public void run() {
//        CommonLogger.businessLogger.info("******************Monitor Thread Started***************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitor Thread Started").build());
        while (!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.get() || !Manager.workingServices.isEmpty() || !Manager.queueForJSONRequests.isEmpty() || !Manager.queueForHTTPResult.isEmpty()) {
            try {
//                CommonLogger.businessLogger.info("*****************************************************************************************************");
//                CommonLogger.businessLogger.info("*****************************************************************************************************");
//                CommonLogger.businessLogger.info("*****************************************ENGINE STATUS****************************************");

//                String monitorLog = "\n ##### Heap utilization statistics [MB] #####";
//                monitorLog += "\n [MonitoringThread] Used Memory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));
//                monitorLog += "\n [MonitoringThread] Free Memory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024));
//                monitorLog += "\n [MonitoringThread] Total Memory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024));
//                monitorLog += "\n [MonitoringThread] Max Memory:" + (Runtime.getRuntime().maxMemory() / (1024 * 1024));
//                monitorLog += "\n #############################################";
//                CommonLogger.businessLogger.info(monitorLog);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Started. Heap Utilization Statistics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());
//                CommonLogger.businessLogger.info("Database Configuration: " + printMapContents(SMSBridgingDefines.databaseConfigs));
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Configuration info")
                        .put(GeneralConstants.StructuredLogKeys.DATABASE_CONFIGURATION, printMapContents(SMSBridgingDefines.databaseConfigs)).build());
//                CommonLogger.businessLogger.info("File Configuration: " + printMapContents(SMSBridgingDefines.propertiesFileConfigs));
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File Configuration info")
                        .put(GeneralConstants.StructuredLogKeys.FILE_CONFIGURATION, printMapContents(SMSBridgingDefines.propertiesFileConfigs)).build());
                //step 1
                for (Map.Entry<String, QueueNeedsHolder> pair : Manager.workingAppQueues.entrySet()) {
//                    CommonLogger.businessLogger.info("Dequeuer Pool : StatusCount" + pair.getKey() + "PoolSize: " + ((ThreadPoolExecutor) pair.getValue().getDequeuerPool()).getPoolSize() + " StatusCount" + pair.getKey() + "PoolActiveThreadsSize:" + ((ThreadPoolExecutor) pair.getValue().getDequeuerPool()).getActiveCount());
//                    CommonLogger.businessLogger.info("Enqueuer Pool : StatusCount" + pair.getKey() + "PoolSize: " + ((ThreadPoolExecutor) pair.getValue().getEnqueuerPool()).getPoolSize() + " StatusCount" + pair.getKey() + "PoolActiveThreadsSize:" + ((ThreadPoolExecutor) pair.getValue().getEnqueuerPool()).getActiveCount());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Pool Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, pair.getKey())
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) pair.getValue().getDequeuerPool()).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) pair.getValue().getDequeuerPool()).getActiveCount()).build());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueuer Pool Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, pair.getKey())
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) pair.getValue().getEnqueuerPool()).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) pair.getValue().getEnqueuerPool()).getActiveCount()).build());
                }
                for (Map.Entry<String, ServiceNeedsHolder> pair : Manager.workingServices.entrySet()) {
//                    CommonLogger.businessLogger.info("TotalSizeOf" + pair.getKey() + "Queue: " + pair.getValue().getQueueForDequeuerResult().size() + " RemainingAvailableSizeFor" + pair.getKey() + "Queue: " + pair.getValue().getQueueForDequeuerResult().remainingCapacity());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Queue Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, pair.getKey())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, pair.getValue().getQueueForDequeuerResult().size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, pair.getValue().getQueueForDequeuerResult().remainingCapacity()).build());
                }

                for (Map.Entry<String, ArrayBlockingQueue<ArrayList<SMSBridge>>> pair : Manager.submitterEnqueuerQueueMap.entrySet()) {
//                    CommonLogger.businessLogger.info("TotalSizeOfsubmitterEnqueuerQueue name: " + pair.getKey() + "total size: " + pair.getValue().size() + " RemainingAvailableSizeForsubmitterEnqueuerQueue: " + pair.getValue().remainingCapacity());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "TotalSizeOfSubmittedEnqueuerQueue stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, pair.getKey())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, pair.getValue().size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, pair.getValue().remainingCapacity()).build());
                }

                //step 2
//                CommonLogger.businessLogger.info("StatusCountJSONConstructorPoolSize: " + ((ThreadPoolExecutor) Manager.jsonConstructorPool).getPoolSize() + " StatusCountJSONConstructorPoolActiveThreadsSize:" + ((ThreadPoolExecutor) Manager.jsonConstructorPool).getActiveCount());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "StatusCountJSONConstructorPool Stats")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) Manager.jsonConstructorPool).getPoolSize())
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) Manager.jsonConstructorPool).getActiveCount()).build());
//                CommonLogger.businessLogger.info("TotalSizeOfJSONRequestQueue: " + Manager.queueForJSONRequests.size() + " RemainingAvailableSizeForJSONRequestQueue: " + Manager.queueForJSONRequests.remainingCapacity());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "JSONRequestQueue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.queueForJSONRequests.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.queueForJSONRequests.remainingCapacity()).build());
                //step 3
//                CommonLogger.businessLogger.info("StatusCountHTTPSubmitterPoolSize: " + ((ThreadPoolExecutor) Manager.httpSubmitterPool).getPoolSize() + " StatusCountHTTPSubmitterPoolActiveThreadsSize:" + ((ThreadPoolExecutor) Manager.httpSubmitterPool).getActiveCount());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "StatusCountHTTPSubmitterPool Stats")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) Manager.httpSubmitterPool).getPoolSize())
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) Manager.httpSubmitterPool).getActiveCount()).build());
//                CommonLogger.businessLogger.info("TotalSizeOfHTTPResponseQueue: " + Manager.queueForHTTPResult.size() + " RemainingAvailableSizeForHTTPResponseQueue: " + Manager.queueForHTTPResult.remainingCapacity());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "HTTPResponseQueue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.queueForHTTPResult.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.queueForHTTPResult.remainingCapacity()).build());
                //step 4
//                CommonLogger.businessLogger.info("StatusCountArchiverPoolSize: " + ((ThreadPoolExecutor) Manager.archiverPool).getPoolSize() + " StatusCountArchiverPoolActiveThreadsSize:" + ((ThreadPoolExecutor) Manager.archiverPool).getActiveCount());
//                CommonLogger.businessLogger.info("***************************************ENGINE STATUS*************************************************");
//                CommonLogger.businessLogger.info("*****************************************************************************************************");
//                CommonLogger.businessLogger.info("*****************************************************************************************************");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "StatusCountArchiverPool Stats")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) Manager.archiverPool).getPoolSize())
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) Manager.archiverPool).getActiveCount()).build());
            } catch (Exception e) {
                CommonLogger.businessLogger.error("Monitor Thread Caught Exception---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught Exception---->" + e, e);
            }
            try {
                long sleepTime = Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.MONITOR_THREAD_SLEEP_TIME));
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error("Monitor Thread Caught InterruptedException---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught InterruptedException---->" + e, e);
            }
        }
//        CommonLogger.businessLogger.info("******************Monitor Thread Finished***************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitor Thread Finished").build());
    }

    // <String, String>
    public static String printMapContents(Map<String, String> mp) {
        String ret = "";
        for (Map.Entry<String, String> pair : mp.entrySet()) {
            ret += "(" + pair.getKey() + "=>" + pair.getValue() + ")";
        }
        return ret;
    }
}
