/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.controller;

import client.HashObject;
import client.Throttling;
import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.EngineManager;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines.VFE_CS_QUEUES_TYPE_LK;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.threads.LivenessThread;
import static com.asset.cs.sendingsms.controller.Controller.debugLogger;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.threads.ArchiveConcThread;
import com.asset.cs.sendingsms.threads.ArchiveMessageThread;
import com.asset.cs.sendingsms.threads.DequeuerThread;
import com.asset.cs.sendingsms.threads.MonitorThread;
import com.asset.cs.sendingsms.threads.ReloadThread;
import com.asset.cs.sendingsms.threads.SenderThread;
import com.asset.cs.sendingsms.threads.ShutdownHookThread;
import com.asset.cs.sendingsms.threads.ShutdownThread;
import com.asset.cs.sendingsms.threads.UpdateOnDeliveryThread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kerollos.asaad
 */
public class Manager extends EngineManager {

    public static HashMap<String, ExecutorService> dequeuersPoolsMap = new HashMap<String, ExecutorService>();
    public static HashMap<String, ExecutorService> sendersPoolsMap = new HashMap<String, ExecutorService>();
    public static ExecutorService archivePool = null;
    public static ExecutorService archiveConcPool = null;
    public static ExecutorService updateOnDeliveryPool = null;
    public static HashMap<String, ArrayBlockingQueue<HashObject>> deqeueurs_Senders_QMap = new HashMap<String, ArrayBlockingQueue<HashObject>>();
    public static HashMap<String, ArrayBlockingQueue<HashObject>> receiver_DeliveryResp_QMap = new HashMap<String, ArrayBlockingQueue<HashObject>>(); // ISSUE 25/10, put sms objects for each session on it's blocking queue.
    public static ArrayBlockingQueue<HashObject> receiver_ArchiveConcH_Q = new ArrayBlockingQueue<HashObject>(Integer.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_CONCAT_SMS_QUEUE_SIZE)));
    public static ArrayBlockingQueue<HashObject> senders_ArchiveH_Q = new ArrayBlockingQueue<HashObject>(Integer.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_SMS_QUEUE_SIZE)));
    public static ArrayBlockingQueue<HashObject> deliverResp_updateStatusCountH_Q = new ArrayBlockingQueue<HashObject>(Integer.valueOf(Defines.fileConfigurations.get(Defines.UPDATE_ON_DELIVERY_SMS_QUEUE_SIZE)));
    public static HashMap<String, Boolean> appsThreadsShutdownMap = new HashMap<String, Boolean>();
    public static HashMap<String, DataSourceManager> connectionPerQueue = new HashMap<String, DataSourceManager>();
    public static HashMap<String, Boolean> deploymentQueues = new HashMap<String, Boolean>();
    // public static Hashtable RcvDelHash = null; // needed to construct a new hashmap between receiver (when recieve deliver_sm) and deliver_sm_resp thread.
    public static HashMap<String, Hashtable> receiver_deleteHashTable_QMap = new HashMap<String, Hashtable>();
    /* ISSUE 04/11, Add recvdel hashtable for each application. */
    public static ReloadThread reloadThread = new ReloadThread();
    ;
    public static ShutdownThread shutdownThread;
    public static MonitorThread monitorThread; // to be determined later. // done
    private static LivenessThread livenessThread = null;
    ////////////////////////////////////////////

    public static void prepareSMPPApps() throws CommonException {
        CommonLogger.businessLogger.info("Engine Starting preparing threads and queues");
        if (!Defines.CLOUD_MODE) {
            debugLogger.info("Starting deployment queues for instance:" + Defines.INSTACE_ID);
            prepareDeploymentQueues();
        }
        QueueModel appQueue;
        Iterator smppAppsIterator = Defines.appQueueMap.entrySet().iterator();
        while (smppAppsIterator.hasNext()) {
            Map.Entry pair = (Map.Entry) smppAppsIterator.next();
            try {
                appQueue = (QueueModel) ((QueueModel) pair.getValue()).clone();
                if (((!Defines.CLOUD_MODE) && (!deploymentQueues.containsKey(appQueue.getAppName()))) || (appQueue.getQueueType() != VFE_CS_QUEUES_TYPE_LK.SMS_SENDER_QUEUES)) {
                    continue;
                }
                addAppToShutdownMap(appQueue);
                addAppToDeqeueursSendersSMSMap(appQueue);
                addAppToRecieverDeliveryRespMap(appQueue);
                addAppToRecvDelMap(appQueue);
                startDequeuersThreads(appQueue);
                startSendersThreads(appQueue);
            } catch (CloneNotSupportedException ex) {
                // CommonLogger.businessLogger.error("CloneNotSupportedException in prepareSMPPApps---->" + ex);
                CommonLogger.errorLogger.error("CloneNotSupportedException in prepareSMPPApps---->" + ex, ex);
            }
        }
        startArchiveMessagesThreads();
        startArchiveConcMessagesThreads();
        startUpdateOnDeliveryThreads();
        CommonLogger.businessLogger.info("Engine finished preparing threads and queues");
    }

    public static void prepareDeploymentQueues() {
        try {
            deploymentQueues.clear();
            String instanceKey = Defines.instanceConfigurations.get(DBStruct.VFE_CS_SENDING_SMS_INSTANCES.INSTANCE_APP_QUEUES);
            if (instanceKey == null || instanceKey.isEmpty()) {
                return;
            }
//            CommonLogger.businessLogger.info("Instance application queues: " + instanceKey);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Preparing Instance Application Queues")
                    .put(GeneralConstants.StructuredLogKeys.INSTANCE_ID, instanceKey).build());
            StringTokenizer queuesStrings = new StringTokenizer(instanceKey, ",");
            while (queuesStrings.hasMoreTokens()) {
                deploymentQueues.put(queuesStrings.nextToken(), Boolean.TRUE);
            }
        } catch (Exception ex) {
            // CommonLogger.businessLogger.error("Database configurations for queues per deployment format error prepareDeploymentQueues---->" + ex);
            CommonLogger.errorLogger.error("Database configurations for queues per deployment format error prepareDeploymentQueues---->" + ex, ex);
        }
    }

    private static void addAppToShutdownMap(QueueModel appProfile) {
//        CommonLogger.businessLogger.info("Adding app_name:" + appProfile.getAppName() + " to shutdown Map");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Adding application to shutdown")
                .put(GeneralConstants.StructuredLogKeys.APP_NAME, appProfile.getAppName()).build());
        appsThreadsShutdownMap.put(appProfile.getAppName(), false);
    }

    private static void addAppToDeqeueursSendersSMSMap(QueueModel appQueue) throws CommonException {
//        CommonLogger.businessLogger.info("Starting creating app sms queue for APP_ID:" + appQueue.getAppName());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Creating App SMS Queue")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, appQueue.getAppName()).build());
        connectionPerQueue.put(appQueue.getAppName(), new DataSourceManager(appQueue.getDatabaseUrl(), appQueue.getSchemaName(), appQueue.getSchemaPassword()));
        deqeueurs_Senders_QMap.put(appQueue.getAppName(), new ArrayBlockingQueue<HashObject>(Integer.valueOf(Defines.fileConfigurations.get(Defines.SENDER_SMS_QUEUE_SIZE))));
    }

    /* ISSUE 25/10, put sms objects for each session on it's blocking queue. */
    private static void addAppToRecieverDeliveryRespMap(QueueModel appQueue) {
//        CommonLogger.businessLogger.info("Starting creating delivery report response queue for APP_ID:" + appQueue.getAppName());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Crating Delivery Report Response Queue")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, appQueue.getAppName()).build());
        receiver_DeliveryResp_QMap.put(appQueue.getAppName(), new ArrayBlockingQueue<HashObject>(Integer.valueOf(Defines.fileConfigurations.get(Defines.DELIVERY_RESP_SMS_QUEUE_SIZE))));
    }

    /* ISSUE 04/11, Add recvdel hashtable for each application. */
    private static void addAppToRecvDelMap(QueueModel appQueue) {
//        CommonLogger.businessLogger.info("Starting creating recvDel hashtable  for APP_ID:" + appQueue.getAppName());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Crating recDel Hashtable")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, appQueue.getAppName()).build());
        // intialize hashtable
        receiver_deleteHashTable_QMap.put(appQueue.getAppName(), new Hashtable(Integer.valueOf(Defines.fileConfigurations.get(Defines.RecvDelHashWindowSize))));
    }

    private static void startDequeuersThreads(QueueModel app) {
//        CommonLogger.businessLogger.info("Starting creating dequeuer thread pool for APP_ID:" + app.getAppName());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Crating dequeuer thread pool")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, app.getAppName()).build());
        ExecutorService dequeuersPool = Executors.newFixedThreadPool(app.getDequeuePoolSize());
        for (int i = 0; i < app.getDequeuePoolSize(); i++) {
            DequeuerThread dequeuerThread = new DequeuerThread(app, i + 1);
            dequeuersPool.submit(dequeuerThread);
        }
        dequeuersPoolsMap.put(app.getAppName(), dequeuersPool);
    }

    private static void startSendersThreads(QueueModel app) {
//        CommonLogger.businessLogger.info("Starting creating sender thread pool for APP_ID:" + app.getAppName());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Crating Sender Thread Pool")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, app.getAppName()).build());
        ExecutorService sendersPool = Executors.newFixedThreadPool(app.getSenderPoolSize());
        for (int i = 0; i < app.getSenderPoolSize(); i++) {
            SenderThread senderThread = new SenderThread(app, i + 1);
            sendersPool.submit(senderThread);
        }
        sendersPoolsMap.put(app.getAppName(), sendersPool);
    }

    private static void startArchiveMessagesThreads() {
        int archiveMessagesPoolSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_POOL_SIZE));
        archivePool = Executors.newFixedThreadPool(archiveMessagesPoolSize);
        for (int i = 0; i < archiveMessagesPoolSize; i++) {
            ArchiveMessageThread archiveThread = new ArchiveMessageThread(i);
            archivePool.submit(archiveThread);
        }
    }

    private static void startArchiveConcMessagesThreads() {
        int archiveMessagesPoolSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_CONCAT_POOL_SIZE));
        archiveConcPool = Executors.newFixedThreadPool(archiveMessagesPoolSize);
        for (int i = 0; i < archiveMessagesPoolSize; i++) {
            ArchiveConcThread archiveConcThread = new ArchiveConcThread(i);
            archiveConcPool.submit(archiveConcThread);
        }
    }

    private static void startUpdateOnDeliveryThreads() {
        int archiveMessagesPoolSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.UPDATE_ON_DELIVERY_POOL_SIZE));
        updateOnDeliveryPool = Executors.newFixedThreadPool(archiveMessagesPoolSize);
        for (int i = 0; i < archiveMessagesPoolSize; i++) {
            UpdateOnDeliveryThread updateOnDeliveryThread = new UpdateOnDeliveryThread(i);
            updateOnDeliveryPool.submit(updateOnDeliveryThread);
        }
    }

    public static void startReloadThread() {
        //      reloadThread = new ReloadThread();
        reloadThread.setName("ReloadThread");
        reloadThread.start();
    }

//    public static void startShutdownThread() {
//        shutdownThread = new ShutdownThread();
//        shutdownThread.setName("ShutdownThread");
//        shutdownThread.start();
//    }
//    public static void startShutdownHookThread() {
//          ShutdownHookThread shutdownHookThread = new ShutdownHookThread();
//          shutdownHookThread.attachShutDownHook();
//    }
    public static void startMonitorThread() {
        monitorThread = new MonitorThread();
        monitorThread.setName("MonitorThread");
        monitorThread.start();
    }

    public static void shutdownEngine() {
        CommonLogger.businessLogger.info("Manager Starting Shutdown Engine");
        try {
            Iterator dequeuersMapiterator = dequeuersPoolsMap.entrySet().iterator();
            while (dequeuersMapiterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) dequeuersMapiterator.next();
                ExecutorService deqeueurPoolExec = (ExecutorService) mapEntry.getValue();
                deqeueurPoolExec.shutdown();
                deqeueurPoolExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            }
            Iterator queuesConnectionsIterator = connectionPerQueue.entrySet().iterator();
            while (queuesConnectionsIterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) queuesConnectionsIterator.next();
                DataSourceManager dataSource = (DataSourceManager) mapEntry.getValue();
                dataSource.closeConnectionPool();
            }
            Defines.DEQUEUER_THREAD_SHUTDOWN_FLAG = true;
            CommonLogger.businessLogger.info("Manager Shutdown all deqeueurs Thread pools");
            Iterator sendersMapiterator = sendersPoolsMap.entrySet().iterator();
            while (sendersMapiterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) sendersMapiterator.next();
                ExecutorService sendersPoolExec = (ExecutorService) mapEntry.getValue();
                sendersPoolExec.shutdown();
                sendersPoolExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            }
            CommonLogger.businessLogger.info("Manager Shutdown all senders Thread pools ");
            Defines.SENDER_THREAD_SHUTDOWN_FLAG = true;
            //
            //Defines.ARCHIVE_THREAD_SHUTDOWN_FLAG = true;
            ExecutorService archivePoolExec = (ExecutorService) archivePool;
            archivePoolExec.shutdown();
            archivePoolExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            //
            //Defines.UPDATE_ON_DELIVERY_THREAD_SHUTDOWN_FLAG = true;
            ExecutorService updateOnDeliveryPoolExec = (ExecutorService) updateOnDeliveryPool;
            updateOnDeliveryPoolExec.shutdown();
            updateOnDeliveryPoolExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            //
            //Defines.ARCHIVE_CONCAT_THREAD_SHUTDOWN_FLAG = true;
            ExecutorService archiveConcPoolExec = (ExecutorService) archiveConcPool;
            archiveConcPoolExec.shutdown();
            archiveConcPoolExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            //
            Defines.ARCHIVES_THREADS_SHUTDOWN_FLAG = true;
            Iterator smsQueueIterator = deqeueurs_Senders_QMap.entrySet().iterator();
            while (smsQueueIterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) smsQueueIterator.next();
//                CommonLogger.businessLogger.info("SMSQueue with size:" + ((ArrayBlockingQueue<HashObject>) mapEntry.getValue()).size() + " for APP_ID:" + (String) mapEntry.getKey());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSQueue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ((ArrayBlockingQueue<HashObject>) mapEntry.getValue()).size())
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, (String) mapEntry.getKey()).build());
            }
            /* ISSUE 25/10, put sms objects for each session on it's blocking queue. */
            Iterator smsDeliveryReportIterator = receiver_DeliveryResp_QMap.entrySet().iterator();
            /* ISSUE 25/10, put sms objects for each session on it's blocking queue. */
            while (smsDeliveryReportIterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) smsDeliveryReportIterator.next();
//                CommonLogger.businessLogger.info("DeliveryRespSize with size:" + ((ArrayBlockingQueue<HashObject>) mapEntry.getValue()).size() + " for APP_ID:" + (String) mapEntry.getKey());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DeliveryRespSize Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ((ArrayBlockingQueue<HashObject>) mapEntry.getValue()).size())
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, (String) mapEntry.getKey()).build());
            }

//            CommonLogger.businessLogger.info("ArchiveHSize: " + Manager.senders_ArchiveH_Q.size() + " ArchiveHRemaianingSize:" + Manager.senders_ArchiveH_Q.remainingCapacity());
//            CommonLogger.businessLogger.info("ArchiveConcHSize: " + Manager.receiver_ArchiveConcH_Q.size() + " ArchiveConcHRemaianingSize:" + Manager.receiver_ArchiveConcH_Q.remainingCapacity());
//            CommonLogger.businessLogger.info("StatusCountHSize: " + Manager.deliverResp_updateStatusCountH_Q.size() + " StatusCountHSizeRemaianingSize:" + Manager.deliverResp_updateStatusCountH_Q.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiveHSize Queue Stats")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.senders_ArchiveH_Q.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.senders_ArchiveH_Q.remainingCapacity()).build());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiveConcHSize Queue Stats")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.receiver_ArchiveConcH_Q.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.receiver_ArchiveConcH_Q.remainingCapacity()).build());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "StatusCountHSize Queue Stats")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.deliverResp_updateStatusCountH_Q.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.deliverResp_updateStatusCountH_Q.remainingCapacity()).build());
            //CommonLogger.businessLogger.info("DeliveryRespSize: " + Manager.receiver_DeliveryResp_QMap.size() + " DeliveryRespRemaianingSize:" + Manager.senders_ArchiveH_Q.remainingCapacity());
            /* ISSUE 25/10, put sms objects for each session on it's blocking queue. */
            // shutdownThread.join();
            reloadThread.join();
            monitorThread.join();
//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())).build());
//            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Dump Ended").build());

        } catch (InterruptedException e) {
            CommonLogger.businessLogger.error("Error while SHUTTINGDOWN  ENGINE--->" + e);
            CommonLogger.errorLogger.error("Error while SHUTTINGDOWN  ENGINE--->" + e, e);
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error while SHUTTINGDOWN  ENGINE--->" + e);
            CommonLogger.errorLogger.error("Error while SHUTTINGDOWN  ENGINE--->" + e, e);
        }
    }

    public static void addNewSMPPApp(QueueModel smppApp) throws CommonException {
//        CommonLogger.businessLogger.info("Engine Starting adding new APP with id:" + smppApp.getAppName());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Engine Starting Adding New App")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, smppApp.getAppName()).build());
        QueueModel tmpApp;
        try {
            tmpApp = (QueueModel) smppApp.clone();
            if (deploymentQueues.containsKey(tmpApp.getAppName())) {
                addAppToShutdownMap(tmpApp);
                addAppToDeqeueursSendersSMSMap(tmpApp);
                addAppToRecieverDeliveryRespMap(tmpApp);
                /* ISSUE 25/10, put sms objects for each session on it's blocking queue. */
                startDequeuersThreads(tmpApp);
                startSendersThreads(tmpApp);
                synchronized (livenessThread.getExecutors()) {
                    livenessThread.setExecutors(new ConcurrentHashMap<String, ExecutorService>());
                    Iterator sendersMapiterator = Manager.sendersPoolsMap.entrySet().iterator();
                    while (sendersMapiterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) sendersMapiterator.next();
                        ExecutorService sendersPoolExec = (ExecutorService) mapEntry.getValue();
                        livenessThread.getExecutors().put(((String) mapEntry.getKey()).concat("sender"), sendersPoolExec);
                    }
                    Iterator dequeuersMapiterator = Manager.dequeuersPoolsMap.entrySet().iterator();
                    while (dequeuersMapiterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) dequeuersMapiterator.next();
                        ExecutorService dequeuersPoolExec = (ExecutorService) mapEntry.getValue();
                        livenessThread.getExecutors().put(((String) mapEntry.getKey()).concat("dequeuer"), dequeuersPoolExec);
                    }
                    livenessThread.getExecutors().put("archiveConcPool", archiveConcPool);
                    livenessThread.getExecutors().put("archivePool", archivePool);
                    livenessThread.getExecutors().put("updateOnDeliveryPool", updateOnDeliveryPool);
                }
            }
        } catch (CloneNotSupportedException ex) {
            CommonLogger.businessLogger.error("CloneNotSupportedException in prepareSMPPApps---->" + ex);
            CommonLogger.errorLogger.error("CloneNotSupportedException in prepareSMPPApps---->" + ex, ex);
        }
//        CommonLogger.businessLogger.info("Engine finished adding new APP with id:" + smppApp.getAppName());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Engine Finished Adding New App")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, smppApp.getAppName()).build());
    }

    public static void shutdownApp(String appId) {
//        CommonLogger.businessLogger.info("Manager Starting Shutdown APP queues,threadpools for APP_id: " + appId);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Manager Starting Shutdown  App Queues, Threadpools")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, appId).build());
        try {

            ExecutorService deqeueurPoolExec = (ExecutorService) dequeuersPoolsMap.get(appId);
            deqeueurPoolExec.shutdown();
            deqeueurPoolExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            dequeuersPoolsMap.remove(appId);
//            CommonLogger.businessLogger.info("Manager Shutdown  deqeueurs Thread pool and removed it from dequeuersPoolsMap for APP_id: " + appId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Manager Shutdown Dequeuers Threadpool and Removed it from DequeuersPoolsMap")
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, appId).build());
            ExecutorService senderPoolExec = (ExecutorService) sendersPoolsMap.get(appId);
            senderPoolExec.shutdown();
            senderPoolExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            sendersPoolsMap.remove(appId);
            synchronized (livenessThread.getExecutors()) {
                livenessThread.setExecutors(new ConcurrentHashMap<String, ExecutorService>());
                Iterator sendersMapiterator = Manager.sendersPoolsMap.entrySet().iterator();
                while (sendersMapiterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) sendersMapiterator.next();
                    ExecutorService sendersPoolExec = (ExecutorService) mapEntry.getValue();
                    livenessThread.getExecutors().put(((String) mapEntry.getKey()).concat("sender"), sendersPoolExec);
                }
                Iterator dequeuersMapiterator = Manager.dequeuersPoolsMap.entrySet().iterator();
                while (dequeuersMapiterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) dequeuersMapiterator.next();
                    ExecutorService dequeuersPoolExec = (ExecutorService) mapEntry.getValue();
                    livenessThread.getExecutors().put(((String) mapEntry.getKey()).concat("dequeuer"), dequeuersPoolExec);
                }
                livenessThread.getExecutors().put("archiveConcPool", archiveConcPool);
                livenessThread.getExecutors().put("archivePool", archivePool);
                livenessThread.getExecutors().put("updateOnDeliveryPool", updateOnDeliveryPool);
            }
//            CommonLogger.businessLogger.info("Manager Shutdown  senders Thread pool and removed it from sendersPoolsMap for APP_id: " + appId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Manager Shutdown Senders Threadpool and Removed it from SenderPoolsMap")
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, appId).build());

//            CommonLogger.businessLogger.info("smsQueue will be removed from map with size:" + deqeueurs_Senders_QMap.get(appId).size() + "for APP_id:" + appId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Queue will be Removed from map")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, deqeueurs_Senders_QMap.get(appId).size())
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, appId).build());
            /* ISSUE 25/10, put sms objects for each session on it's blocking queue. */
//            CommonLogger.businessLogger.info("deliveryRespQueue will be removed from map with size:" + receiver_DeliveryResp_QMap.get(appId).size() + "for APP_id:" + appId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DeliveryRespQueue will be Removed from map")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, receiver_DeliveryResp_QMap.get(appId).size())
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, appId).build());
//            CommonLogger.businessLogger.info("ArchiveHSize: " + Manager.senders_ArchiveH_Q.size() + " ArchiveHRemaianingSize:" + Manager.senders_ArchiveH_Q.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiveHSize stats")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.senders_ArchiveH_Q.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.senders_ArchiveH_Q.remainingCapacity()).build());
//            CommonLogger.businessLogger.info("ArchiveConcHSize: " + Manager.receiver_ArchiveConcH_Q.size() + " ArchiveConcHRemaianingSize:" + Manager.receiver_ArchiveConcH_Q.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiveConcHSize stats")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.receiver_ArchiveConcH_Q.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.receiver_ArchiveConcH_Q.remainingCapacity()).build());
//            CommonLogger.businessLogger.info("StatusCountHSize: " + Manager.deliverResp_updateStatusCountH_Q.size() + " StatusCountHSizeRemaianingSize:" + Manager.deliverResp_updateStatusCountH_Q.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "StatusCountHSize stats")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.deliverResp_updateStatusCountH_Q.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, Manager.deliverResp_updateStatusCountH_Q.remainingCapacity()).build());
            //CommonLogger.businessLogger.info("DeliveryRespSize: " + Manager.receiver_DeliveryResp_Q.size() + " DeliveryRespRemaianingSize:" + Manager.senders_ArchiveH_Q.remainingCapacity());

            /* ISSUE 25/10, put sms objects for each session on it's blocking queue. */
            receiver_DeliveryResp_QMap.remove(appId);
            deqeueurs_Senders_QMap.remove(appId);
            appsThreadsShutdownMap.remove(appId);
            connectionPerQueue.get(appId).closeConnectionPool();
            connectionPerQueue.remove(appId);

//            CommonLogger.businessLogger.info("AppShutdownFlag will be removed from map for APP_id:" + appId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "AppShutdownFlag will be Removed from Map")
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, appId).build());
//            CommonLogger.businessLogger.info("Manager finished shutdown for APP_id:" + appId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Manager Finished Shutdown")
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, appId).build());
        } catch (InterruptedException e) {
            // CommonLogger.businessLogger.error("Error while SHUTTINGDOWN APP_id:" + appId + "--->" + e);
            CommonLogger.errorLogger.error("Error while SHUTTINGDOWN APP_id:" + appId + "--->" + e, e);
        } catch (Exception e) {
            // CommonLogger.businessLogger.error("Error while SHUTTINGDOWN APP_id:" + appId + "--->" + e);
            CommonLogger.errorLogger.error("Error while SHUTTINGDOWN APP_id:" + appId + "--->" + e, e);
        }
    }

    public static void initLivenessThread() {
        CommonLogger.businessLogger.info("JobManager.initLivenessThread() Invoked...");
        //   initializeSystemProperties();

        ConcurrentHashMap<String, ExecutorService> executorServices = new ConcurrentHashMap<String, ExecutorService>();
        Iterator sendersMapiterator = Manager.sendersPoolsMap.entrySet().iterator();
        while (sendersMapiterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) sendersMapiterator.next();
            ExecutorService sendersPoolExec = (ExecutorService) mapEntry.getValue();
            executorServices.put(((String) mapEntry.getKey()).concat("sender"), sendersPoolExec);
        }
        Iterator dequeuersMapiterator = Manager.dequeuersPoolsMap.entrySet().iterator();
        while (dequeuersMapiterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) dequeuersMapiterator.next();
            ExecutorService dequeuersPoolExec = (ExecutorService) mapEntry.getValue();
            executorServices.put(((String) mapEntry.getKey()).concat("dequeuer"), dequeuersPoolExec);
        }
        executorServices.put("archiveConcPool", archiveConcPool);
        executorServices.put("archivePool", archivePool);
        executorServices.put("updateOnDeliveryPool", updateOnDeliveryPool);
        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<String, Thread>();
        threads.put("monitorThread", monitorThread);
        threads.put("reloadThread", reloadThread);
        livenessThread = new LivenessThread(Defines.ENGINE_SHUTDOWN_FLAG, null, executorServices, threads, CommonLogger.livenessLogger, CommonLogger.businessLogger, Integer.parseInt(Defines.databaseConfigurations.get(com.asset.contactstrategy.common.defines.Defines.LIVENESS_THREAD_SLEEP_TIME_PROPERTY_NAME)));
        livenessThread.start();
        CommonLogger.businessLogger.info("JobManager.initLivenessThread() Ended...");
    }
    
    public static void loadSmscThrottlingValues() throws CommonException {
        MainService commonMainService = new MainService();
        ArrayList<SMSCModel> smscs = commonMainService.getSMSCs();
        HashMap<Long, Integer> smscIdAndNumberOfSenderThreadsMap = getTotalNumberOfSenderThreadsPerSmsc();
        for (SMSCModel smsc : smscs) {
            if (smscIdAndNumberOfSenderThreadsMap.containsKey(smsc.getVersionId())) {
                int actualSmscWindowSize = smsc.getWindowSize() / smscIdAndNumberOfSenderThreadsMap.get(smsc.getVersionId());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSC [" + smsc.getSMSCname() + "] | window size is " + actualSmscWindowSize
                        + " | throughput is " + smsc.getThroughput())                        
                        .put(GeneralConstants.StructuredLogKeys.SMSC_THROUGHPUT, smsc.getThroughput())
                        .build());
                Defines.smscThrottlingMap.put(smsc.getVersionId(), new Throttling(smsc.getThroughput(), actualSmscWindowSize, smsc.getVersionId()));
            }
        }

    }

    public static HashMap<Long, Integer> getTotalNumberOfSenderThreadsPerSmsc() {
        HashMap<Long, Integer> smscIdNumberOfSenderThreadsMap = new HashMap<>();
        for (Map.Entry pair : Defines.appQueueMap.entrySet()) {
            QueueModel queue = (QueueModel) pair.getValue();
            List<SMSCModel> smscModels = queue.getSmscModels();
            int senderPoolSize = queue.getSenderPoolSize();
            for (SMSCModel smsc : smscModels) {
                if (smscIdNumberOfSenderThreadsMap.containsKey(smsc.getVersionId())) {
                    Integer totalSenderThreads = smscIdNumberOfSenderThreadsMap.get(smsc.getVersionId()) + senderPoolSize;
                    smscIdNumberOfSenderThreadsMap.put(smsc.getVersionId(), totalSenderThreads);
                } else {
                    smscIdNumberOfSenderThreadsMap.put(smsc.getVersionId(), senderPoolSize);
                }

            }
        }
        return smscIdNumberOfSenderThreadsMap;
    } 

    @Override
    public void shutdown() {
        shutdownEngine();
    }

}
