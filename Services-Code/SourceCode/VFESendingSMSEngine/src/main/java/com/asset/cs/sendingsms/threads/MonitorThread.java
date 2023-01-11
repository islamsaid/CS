/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.threads;

import client.HashObject;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.sendingsms.controller.Manager;
import static com.asset.cs.sendingsms.controller.Manager.receiver_DeliveryResp_QMap;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.defines.ErrorCodes;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author kerollos.asaad
 */
public class MonitorThread extends Thread {

    @Override
    public void run() {
//        CommonLogger.businessLogger.info("******************Monitor Thread Started***************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitor Thread Started").build());
        while (!Defines.DEQUEUER_THREAD_SHUTDOWN_FLAG || !Defines.SENDER_THREAD_SHUTDOWN_FLAG || !Defines.ARCHIVES_THREADS_SHUTDOWN_FLAG) {
            try {
                int queueSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.SENDER_SMS_QUEUE_SIZE));
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
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Heap Utilization Statistics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());
                Iterator dequeuersMapiterator = Manager.dequeuersPoolsMap.entrySet().iterator();
                while (dequeuersMapiterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) dequeuersMapiterator.next();
                    ExecutorService dequeuersPoolExec = (ExecutorService) mapEntry.getValue();
//                    CommonLogger.businessLogger.info("DequeuerPoolSize: " + ((ThreadPoolExecutor) dequeuersPoolExec).getPoolSize() + " DequeuerPoolActiveThreadsSize:" + ((ThreadPoolExecutor) dequeuersPoolExec).getActiveCount() + " for app_id:" + mapEntry.getKey());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dqueuer Pool Stats")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) dequeuersPoolExec).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) dequeuersPoolExec).getActiveCount())
                            .put(GeneralConstants.StructuredLogKeys.APP_ID, mapEntry.getKey()).build());
//                    CommonLogger.businessLogger.info("C3P0 configuration: \n");
//                    CommonLogger.businessLogger.info(Utility.getHikariConnectionPoolStats(Manager.connectionPerQueue.get((String) mapEntry.getKey()).getHikariDataSourse()));
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Datasource stats")
                            .put(GeneralConstants.StructuredLogKeys.APP_ID, mapEntry.getKey())
                            .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, Utility.getHikariConnectionPoolStats(Manager.connectionPerQueue.get((String) mapEntry.getKey()).getHikariDataSourse())).build());
                }

                Iterator sendersMapiterator = Manager.sendersPoolsMap.entrySet().iterator();
                while (sendersMapiterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) sendersMapiterator.next();
                    ExecutorService senderPoolExec = (ExecutorService) mapEntry.getValue();
//                    CommonLogger.businessLogger.info("SenderPoolSize: " + ((ThreadPoolExecutor) senderPoolExec).getPoolSize() + " SenderPoolActiveThreadsSize:" + ((ThreadPoolExecutor) senderPoolExec).getActiveCount() + " for app_id:" + mapEntry.getKey());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sender Pool Stats")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) senderPoolExec).getPoolSize())
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) senderPoolExec).getActiveCount())
                            .put(GeneralConstants.StructuredLogKeys.APP_ID, mapEntry.getKey()).build());
                }

                Iterator smsDeliveryReportIterator = receiver_DeliveryResp_QMap.entrySet().iterator();
                /* ISSUE 25/10, put sms objects for each session on it's blocking queue. */

                while (smsDeliveryReportIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) smsDeliveryReportIterator.next();
                    ArrayBlockingQueue<HashObject> tmpQueue = (ArrayBlockingQueue<HashObject>) mapEntry.getValue();
                    double fullPercentage = (tmpQueue.size() / queueSize) * 100;
                    double violationPercentage = Double.valueOf(Defines.databaseConfigurations.get(Defines.JAVA_QUEUES_FULL_PERCENTAGE));
                    if (fullPercentage >= violationPercentage) {
                        MOMErrorsModel errorModel = new MOMErrorsModel();
                        //errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
                        errorModel.setPreceivedSeverity(Integer.valueOf(Defines.databaseConfigurations.get(Defines.JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY)));
                        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE);
                        errorModel.setErrorCode(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE + String.valueOf(ErrorCodes.MONITOR_THREAD_JAVA_QUEUE_SIZE_LIMIT));
                        errorModel.setModuleName("Sending SMS Engine");
                        errorModel.setFunctionName("MonitorThread - receiver_DeliveryResp_QMap - " + String.valueOf(mapEntry.getKey()));
                        Utility.sendMOMAlarem(errorModel);
                    }
//                    CommonLogger.businessLogger.info("DeliveryRespSize with size:" + tmpQueue.size() + " DeliveryRespRemaianingSize:" + tmpQueue.remainingCapacity() + " for APP_ID:" + (String) mapEntry.getKey());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Delivery Queue Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, tmpQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, tmpQueue.remainingCapacity())
                            .put(GeneralConstants.StructuredLogKeys.APP_ID, mapEntry.getKey()).build());
                }

                Iterator smsQueueMapiterator = Manager.deqeueurs_Senders_QMap.entrySet().iterator();
                while (smsQueueMapiterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) smsQueueMapiterator.next();
                    ArrayBlockingQueue<HashObject> parserSenderQueue = (ArrayBlockingQueue<HashObject>) mapEntry.getValue();
                    double fullPercentage = (parserSenderQueue.size() / queueSize) * 100;
                    double violationPercentage = Double.valueOf(Defines.databaseConfigurations.get(Defines.JAVA_QUEUES_FULL_PERCENTAGE));
//                    if (fullPercentage >= violationPercentage) {
//                        MOMErrorsModel errorModel = new MOMErrorsModel();
//                        //errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
//                        errorModel.setPreceivedSeverity(Integer.valueOf(Defines.databaseConfigurations.get(Defines.JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY)));
//                        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE);
//                        errorModel.setErrorCode(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE + String.valueOf(ErrorCodes.MONITOR_THREAD_JAVA_QUEUE_SIZE_LIMIT));
//                        errorModel.setModuleName("Sending SMS Engine");
//                        errorModel.setFunctionName("MonitorThread - deqeueurs_Senders_QMap - " + String.valueOf(mapEntry.getKey()));
//                        Utility.sendMOMAlarem(errorModel);
//                    }
//                    CommonLogger.businessLogger.info("SMSQueueSize: " + parserSenderQueue.size() + " SMSQueueRemaianingSize:" + parserSenderQueue.remainingCapacity() + " for app_id:" + mapEntry.getKey());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Queue Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, parserSenderQueue.size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, parserSenderQueue.remainingCapacity())
                            .put(GeneralConstants.StructuredLogKeys.APP_ID, mapEntry.getKey()).build());
                }
//                CommonLogger.businessLogger.info("ArchiveHSize: " + Manager.senders_ArchiveH_Q.size() + " ArchiveHRemaianingSize:" + Manager.senders_ArchiveH_Q.remainingCapacity());
//                CommonLogger.businessLogger.info("ArchiveHPoolSize: " + ((ThreadPoolExecutor) Manager.archivePool).getPoolSize() + " ArchiveHPoolActiveThreadsSize:" + ((ThreadPoolExecutor) Manager.archivePool).getActiveCount());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiveH Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.senders_ArchiveH_Q.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.senders_ArchiveH_Q.remainingCapacity()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiveH Pool Stats")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) Manager.archivePool).getPoolSize())
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) Manager.archivePool).getActiveCount()).build());

//                CommonLogger.businessLogger.info("ArchiveConcHSize: " + Manager.receiver_ArchiveConcH_Q.size() + " ArchiveConcHRemaianingSize:" + Manager.receiver_ArchiveConcH_Q.remainingCapacity());
//                CommonLogger.businessLogger.info("ArchiveConcHPoolSize: " + ((ThreadPoolExecutor) Manager.archiveConcPool).getPoolSize() + " ArchiveConcHPoolActiveThreadsSize:" + ((ThreadPoolExecutor) Manager.archiveConcPool).getActiveCount());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiveConcH Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.receiver_ArchiveConcH_Q.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.receiver_ArchiveConcH_Q.remainingCapacity()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiveConcH Pool Stats")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) Manager.archiveConcPool).getPoolSize())
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) Manager.archiveConcPool).getActiveCount()).build());

//                CommonLogger.businessLogger.info("StatusCountHSize: " + Manager.deliverResp_updateStatusCountH_Q.size() + " StatusCountHSizeRemaianingSize:" + Manager.deliverResp_updateStatusCountH_Q.remainingCapacity());
//                CommonLogger.businessLogger.info("StatusCountHPoolSize: " + ((ThreadPoolExecutor) Manager.updateOnDeliveryPool).getPoolSize() + " StatusCountHPoolActiveThreadsSize:" + ((ThreadPoolExecutor) Manager.updateOnDeliveryPool).getActiveCount());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "StatusCountH Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.deliverResp_updateStatusCountH_Q.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.deliverResp_updateStatusCountH_Q.remainingCapacity()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "StatusCountH Pool Stats")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) Manager.updateOnDeliveryPool).getPoolSize())
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) Manager.updateOnDeliveryPool).getActiveCount()).build());

                //CommonLogger.businessLogger.info("DeliveryRespSize: " + Manager.receiver_DeliveryResp_Q.size() + " DeliveryRespRemaianingSize:" + Manager.senders_ArchiveH_Q.remainingCapacity());
//                CommonLogger.businessLogger.info("Database Configuration: " + printMapContents(Defines.databaseConfigurations));
//                CommonLogger.businessLogger.info("File Configuration: " + printMapContents(Defines.fileConfigurations));
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Configuration Info")
                        .put(GeneralConstants.StructuredLogKeys.DATABASE_CONFIGURATION, printMapContents(Defines.databaseConfigurations)).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File Configuration Info")
                        .put(GeneralConstants.StructuredLogKeys.FILE_CONFIGURATION, printMapContents(Defines.fileConfigurations)).build());

//                CommonLogger.businessLogger.info("***************************************ENGINE STATUS*************************************************");
//                CommonLogger.businessLogger.info("*****************************************************************************************************");
//                CommonLogger.businessLogger.info("*****************************************************************************************************");
            } catch (Exception e) {
                // CommonLogger.businessLogger.error("Monitor Thread Caught Exception---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught Exception---->" + e, e);
            }
            try {
                long sleepTime = Long.valueOf(Defines.databaseConfigurations.get(Defines.MONITOR_THREAD_SLEEP_TIME));
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // CommonLogger.businessLogger.error("Monitor Thread Caught InterruptedException---->" + e);
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
