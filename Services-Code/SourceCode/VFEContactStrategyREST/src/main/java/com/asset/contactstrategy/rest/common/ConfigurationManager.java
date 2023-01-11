/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.common;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.InstanceVariables;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMSHistoryModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
//import com.asset.contactstrategy.rest.test.Test;
import com.asset.contactstrategy.rest.threads.ArchiveManagerThread;
import com.asset.contactstrategy.rest.threads.LoggingManagerThread;
import com.asset.contactstrategy.rest.threads.EnQueueManagerThread;
import com.asset.contactstrategy.rest.threads.MonitorThread;
import com.asset.contactstrategy.rest.threads.ReloadingThread;
import com.asset.contactstrategy.rest.threads.RollbackSMSThread;
import com.asset.contactstrategy.interfaces.threads.ServiceQuotaUpdaterThread;
import com.asset.contactstrategy.rest.threads.SmsValidationThread;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author hazem.fekry
 */
public class ConfigurationManager implements ServletContextListener {

    public static boolean INTERFACES_SHUTDOWN_FLAG = false;
    public static boolean SHUTDOWN_FLAG = false;
    public static boolean MONITOR_THREAD_SHUTDOWN_FLAG = false;
//    public static int INSTANCE_ID;
    public static int CONNECTION_TIMEOUT;
    public static int READ_TIMEOUT;
    public static String encryptionKey;
    public static HashMap<String, String> SRC_ID_SYSTEM_PROPERIES;

    public static ArrayBlockingQueue<InterfacesLogModel> requestsToBeLogged;
    public static ExecutorService logThreadPool;
    public static int MAX_LOGGING_THREAD_POOL_SIZE;

    public static ArrayBlockingQueue<SMSHistoryModel> messagesToBeArchived;
    public static ExecutorService archiveThreadPool;
    public static int MAX_ARCHIVING_THREAD_POOL_SIZE;

    public static ArrayBlockingQueue<InputModel> smsToBeValidated;
    public static ExecutorService smsValidationThreadPool;
    public static int MAX_SMS_VALIDATION_THREAD_POOL_SIZE;

    public static ArrayBlockingQueue<SmsBusinessModel> smsToBeRollbacked;
    public static ExecutorService rollBacksmsThreadPool;
    public static int MAX_ROLLBACK_SMS_THREAD_POOL_SIZE;

    public static HashMap<Long, ArrayBlockingQueue<SmsBusinessModel>> smsToBeSent;
    public static HashMap<Long, ExecutorService> enQueueThreadPoolHashMap;
    public static HashMap<Long, Boolean> shutdownFlagPool;
    //public static HashMap<Long, DataSourceManager> databaseConnectionPool;
    //public static ExecutorService queueThreadPool;
    private static int MAX_SMS_SEND_QUEUE_SIZE;
    public static int MAX_QUEUE_THREAD_POOL_SIZE;

    public static AtomicLong concurrentRequests;

    public static AtomicLong advertismentConsultAccumulator;
    public static AtomicLong advertismentConsultSuccessCount;
    public static AtomicLong advertismentConsultFailedCount;

    public static AtomicLong consultAccumulator;
    public static AtomicLong consultSuccessCount;
    public static AtomicLong consultFailedCount;

    public static AtomicLong sendBulkSmsOfflineAccumulator;
    public static AtomicLong sendBulkSmsOfflineSuccessCount;
    public static AtomicLong sendBulkSmsOfflineFailedCount;

    public static AtomicLong sendSmsAccumulator;
    public static AtomicLong sendSmsSuccessCount;
    public static AtomicLong sendSmsFailedCount;

    public static AtomicLong checkInterfaceAccumulator;
    public static AtomicLong checkInterfaceSuccessCount;
    public static AtomicLong checkInterfaceFailedCount;

    public static AtomicLong readyCheckAccumulator;
    public static AtomicLong readyCheckSuccessCount;
    public static AtomicLong readyCheckFailedCount;

    public static AtomicLong refreshAccumulator;
    public static AtomicLong refreshSuccessCount;
    public static AtomicLong refreshFailedCount;

    public static Thread reloadingThread;

    public static Thread monitoringThread;
    
    
    public static ScheduledExecutorService serviceQuotaUpdaterExecutor;

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        //Empty because we use init() as our initialization function..
        //init() is called from ApplicationConfig.java to be able to 
        //throw exception on failure from the function context and stop 
        //the deployment
    }

    public static void init() throws CommonException {
        System.out.println("Configuration Manager Initialized");

        try {
            Defines.runningProjectId = GeneralConstants.SRC_ID_WEBSERIVCE_REST;
            initializeAll();
//            Test.prepareInputJsonModel();
            Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME_VALUE = Integer.parseInt(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME));
            CONNECTION_TIMEOUT = Integer.parseInt(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.CONNECTION_TIMEOUT));
            READ_TIMEOUT = Integer.parseInt(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.READ_TIMEOUT));
            //Initializing All Queues
            messagesToBeArchived = new ArrayBlockingQueue<>(Defines.INTERFACES.MAX_ARCHIVING_QUEUE_SIZE_VALUE);
            requestsToBeLogged = new ArrayBlockingQueue<>(Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE_VALUE);
            smsToBeValidated = new ArrayBlockingQueue<>(Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_SIZE_VALUE);
            smsToBeRollbacked = new ArrayBlockingQueue<>(Defines.INTERFACES.MAX_ROLLBACK_SMS_QUEUE_SIZE_VALUE);

            concurrentRequests = new AtomicLong(0);

            advertismentConsultAccumulator = new AtomicLong(0);
            advertismentConsultSuccessCount = new AtomicLong(0);
            advertismentConsultFailedCount = new AtomicLong(0);

            consultAccumulator = new AtomicLong(0);
            consultSuccessCount = new AtomicLong(0);
            consultFailedCount = new AtomicLong(0);

            sendBulkSmsOfflineAccumulator = new AtomicLong(0);
            sendBulkSmsOfflineSuccessCount = new AtomicLong(0);
            sendBulkSmsOfflineFailedCount = new AtomicLong(0);

            sendSmsAccumulator = new AtomicLong(0);
            sendSmsSuccessCount = new AtomicLong(0);
            sendSmsFailedCount = new AtomicLong(0);

            checkInterfaceAccumulator = new AtomicLong(0);
            checkInterfaceSuccessCount = new AtomicLong(0);
            checkInterfaceFailedCount = new AtomicLong(0);

            readyCheckAccumulator = new AtomicLong(0);
            readyCheckSuccessCount = new AtomicLong(0);
            readyCheckFailedCount = new AtomicLong(0);

            refreshAccumulator = new AtomicLong(0);
            refreshSuccessCount = new AtomicLong(0);
            refreshFailedCount = new AtomicLong(0);

//            INSTANCE_ID = Integer.parseInt(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.INSTANCE_ID));
            encryptionKey = Defines.ENCRYPTION_KEY;
            MONITOR_THREAD_SHUTDOWN_FLAG = false;
            INTERFACES_SHUTDOWN_FLAG = false;
            SHUTDOWN_FLAG = false;

            Defines.INTERFACES.MAX_CONCURRENT_REQUESTS_VALUE = Long.parseLong(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_CONCURRENT_REQUESTS));
            Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE = Float.parseFloat(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE));
            Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_OCCUPANCY_RATE_VALUE = Float.parseFloat(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_OCCUPANCY_RATE));

            //Initializing SMS Queues
            smsToBeSent = new HashMap<>();
            enQueueThreadPoolHashMap = new HashMap<>();
            shutdownFlagPool = new HashMap<>();
            //databaseConnectionPool = new HashMap<>();
            MAX_SMS_SEND_QUEUE_SIZE = Defines.INTERFACES.MAX_SMS_SEND_QUEUE_SIZE_VALUE;
            MAX_QUEUE_THREAD_POOL_SIZE = Defines.INTERFACES.MAX_QUEUE_THREAD_POOL_SIZE_VALUE;
//            CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//            CommonLogger.businessLogger.info("//Enqueue Queues/////////////////////////////////////////////////////////////////////");
            for (Map.Entry<Long, QueueModel> queue : SystemLookups.QUEUE_LIST.entrySet()) {
//                CommonLogger.businessLogger.info("//QueueName: " + queue.getValue().getAppName() + " Queue Id: " + queue.getKey());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueue Queue Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queue.getValue().getAppName())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queue.getKey())
                        .put("Max " + GeneralConstants.StructuredLogKeys.QUEUE_SIZE, MAX_SMS_SEND_QUEUE_SIZE).build());
                smsToBeSent.put(queue.getKey(), new ArrayBlockingQueue<SmsBusinessModel>(MAX_SMS_SEND_QUEUE_SIZE));
//                CommonLogger.businessLogger.info("//Java Queue MaxSize: " + MAX_SMS_SEND_QUEUE_SIZE);
//                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ConfigurationManager Queue Stats")
//                        .put("Max " + GeneralConstants.StructuredLogKeys.QUEUE_SIZE, MAX_SMS_SEND_QUEUE_SIZE).build());

                shutdownFlagPool.put(queue.getKey(), Boolean.FALSE);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shutdown Flag Status")
                        .put(GeneralConstants.StructuredLogKeys.SHUTDOWN_FLAG, Boolean.FALSE).build());
//                CommonLogger.businessLogger.info("Shutdown Flag: " + Boolean.FALSE);

//                databaseConnectionPool.put(queue.getKey(), new DataSourceManager(queue.getValue().getDatabaseUrl(), 
//                        queue.getValue().getSchemaName(), Utility.decrypt(queue.getValue().getSchemaPassword(), encryptionKey)));
//                CommonLogger.businessLogger.info("//Database Connection Pool: ");
//                CommonLogger.businessLogger.info("//    URL: " + queue.getValue().getDatabaseUrl());
//                CommonLogger.businessLogger.info("//    UserName: " + queue.getValue().getSchemaName());
//                CommonLogger.businessLogger.info("//    Password: " + queue.getValue().getSchemaPassword());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Queue Database Connection Pool")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queue.getValue().getAppName())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queue.getKey())
                        .put(GeneralConstants.StructuredLogKeys.URL, queue.getValue().getDatabaseUrl())
                        .put(GeneralConstants.StructuredLogKeys.USER_NAME, queue.getValue().getSchemaName())
                        .put(GeneralConstants.StructuredLogKeys.PASSWORD, queue.getValue().getSchemaPassword()).build());
                enQueueThreadPoolHashMap.put(queue.getKey(), Executors.newFixedThreadPool(MAX_QUEUE_THREAD_POOL_SIZE));
//                CommonLogger.businessLogger.info("//Thread Pool Max Size: " + MAX_QUEUE_THREAD_POOL_SIZE);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Queue Enqueue Threads Pool Stats")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_MAX_SIZE, MAX_QUEUE_THREAD_POOL_SIZE));
                for (int i = 0; i < MAX_QUEUE_THREAD_POOL_SIZE; i++) {
                    enQueueThreadPoolHashMap.get(queue.getKey()).execute(new EnQueueManagerThread(queue.getKey(), String.valueOf(queue.getValue().getAppName()), i));
                }
//                CommonLogger.businessLogger.info("//Threads Initialized: " + MAX_QUEUE_THREAD_POOL_SIZE);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Queue Enqueue Threads Pool Stats")
                        .put(GeneralConstants.StructuredLogKeys.THREADS_INITIALIZED, MAX_QUEUE_THREAD_POOL_SIZE).build());
//                CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
            }

            MAX_LOGGING_THREAD_POOL_SIZE = Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE_VALUE;
            logThreadPool = Executors.newFixedThreadPool(MAX_LOGGING_THREAD_POOL_SIZE);
            for (int i = 0; i < MAX_LOGGING_THREAD_POOL_SIZE; i++) {
                logThreadPool.execute(new LoggingManagerThread(i));
            }
//            CommonLogger.businessLogger.info("Logging Thread Pool MaxSize: " + MAX_LOGGING_THREAD_POOL_SIZE);
//            CommonLogger.businessLogger.info("Logging Threads Initialized: " + MAX_LOGGING_THREAD_POOL_SIZE);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Logging Thread Pool Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_MAX_SIZE, MAX_LOGGING_THREAD_POOL_SIZE)
                    .put(GeneralConstants.StructuredLogKeys.THREADS_INITIALIZED, MAX_LOGGING_THREAD_POOL_SIZE).build());

            MAX_ARCHIVING_THREAD_POOL_SIZE = Defines.INTERFACES.MAX_ARCHIVING_THREAD_POOL_SIZE_VALUE;
            archiveThreadPool = Executors.newFixedThreadPool(MAX_ARCHIVING_THREAD_POOL_SIZE);
            for (int i = 0; i < MAX_ARCHIVING_THREAD_POOL_SIZE; i++) {
                archiveThreadPool.execute(new ArchiveManagerThread(i));
            }
//            CommonLogger.businessLogger.info("Archiving Thread Pool MaxSize: " + MAX_ARCHIVING_THREAD_POOL_SIZE);
//            CommonLogger.businessLogger.info("Archiving Threads Initialized: " + MAX_ARCHIVING_THREAD_POOL_SIZE);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archiving Thread Pool Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_MAX_SIZE, MAX_LOGGING_THREAD_POOL_SIZE)
                    .put(GeneralConstants.StructuredLogKeys.THREADS_INITIALIZED, MAX_LOGGING_THREAD_POOL_SIZE).build());

            MAX_SMS_VALIDATION_THREAD_POOL_SIZE = Defines.INTERFACES.MAX_SMS_VALIDATION_THREAD_POOL_SIZE_VALUE;
            smsValidationThreadPool = Executors.newFixedThreadPool(MAX_SMS_VALIDATION_THREAD_POOL_SIZE);
            for (int i = 0; i < MAX_SMS_VALIDATION_THREAD_POOL_SIZE; i++) {
                smsValidationThreadPool.execute(new SmsValidationThread(i));
            }
//            CommonLogger.businessLogger.info("SMS Validation Thread Pool MaxSize: " + MAX_SMS_VALIDATION_THREAD_POOL_SIZE);
//            CommonLogger.businessLogger.info("SMS Validation Threads Initialized: " + MAX_SMS_VALIDATION_THREAD_POOL_SIZE);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Validation Thread Pool Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_MAX_SIZE, MAX_LOGGING_THREAD_POOL_SIZE)
                    .put(GeneralConstants.StructuredLogKeys.THREADS_INITIALIZED, MAX_LOGGING_THREAD_POOL_SIZE).build());

            MAX_ROLLBACK_SMS_THREAD_POOL_SIZE = Defines.INTERFACES.MAX_ROLLBACK_SMS_THREAD_POOL_SIZE_VALUE;
            rollBacksmsThreadPool = Executors.newFixedThreadPool(MAX_ROLLBACK_SMS_THREAD_POOL_SIZE);
            for (int i = 0; i < MAX_ROLLBACK_SMS_THREAD_POOL_SIZE; i++) {
                rollBacksmsThreadPool.execute(new RollbackSMSThread(i));
            }
//            CommonLogger.businessLogger.info("Rollback SMS Thread Pool MaxSize: " + MAX_ROLLBACK_SMS_THREAD_POOL_SIZE);
//            CommonLogger.businessLogger.info("Rollback SMS Threads Initialized: " + MAX_ROLLBACK_SMS_THREAD_POOL_SIZE);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Rollback SMS Thread Pool Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_MAX_SIZE, MAX_LOGGING_THREAD_POOL_SIZE)
                    .put(GeneralConstants.StructuredLogKeys.THREADS_INITIALIZED, MAX_LOGGING_THREAD_POOL_SIZE).build());

            reloadingThread = new Thread(new ReloadingThread());
            reloadingThread.start();
            CommonLogger.businessLogger.info("Reloading Thread Initialized");

            monitoringThread = new Thread(new MonitorThread());
            monitoringThread.start();
            CommonLogger.businessLogger.info("Monitoring Thread Initialized");
            
            serviceQuotaUpdaterExecutor = Executors.newSingleThreadScheduledExecutor();
            InstanceVariables.serviceQuotaUpdaterThread = new ServiceQuotaUpdaterThread();
            serviceQuotaUpdaterExecutor.scheduleAtFixedRate(InstanceVariables.serviceQuotaUpdaterThread, 0, 5, TimeUnit.SECONDS);
            CommonLogger.businessLogger.info("Service Quota Updater Thread Initialized");
            
        } catch (CommonException e) {
            CommonLogger.businessLogger.info("Error While Initializing");
            CommonLogger.businessLogger.error(e.getErrorMsg());
            CommonLogger.errorLogger.error(e.getErrorMsg(), e);
            throw e;
        } catch (Exception e) {
            CommonLogger.businessLogger.info("Error While Initializing");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        }

    }

    private static void initializeAll() throws Exception {
        Initializer.readPropertiesFile();
        Initializer.initializeLoggers();
        Initializer.initializeDataSource();
        Initializer.loadInterfacesLookups();
        Initializer.loadInterfacesData();
        Initializer.loadSystemProperties();
        MainService mainService = new MainService();
        SRC_ID_SYSTEM_PROPERIES = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_WEBSERIVCE_REST);
        //Initializer.loadAppsQueues();
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        INTERFACES_SHUTDOWN_FLAG = true;
        CommonLogger.businessLogger.info("INTERFACES_SHUTDOWN_FLAG was Rasied...");

        try {
            for (Map.Entry<Long, ExecutorService> queueThreadPool : enQueueThreadPoolHashMap.entrySet()) {
                shutdownFlagPool.put(queueThreadPool.getKey(), Boolean.TRUE);
                queueThreadPool.getValue().shutdown();
//                CommonLogger.businessLogger.info("Shutdoown Flag was rasied and Shutdown signal was sent for Enqueuer Thread: " + SystemLookups.QUEUE_LIST.get(queueThreadPool.getKey()).getAppName());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shutdoown Flag was rasied and Shutdown signal was sent for Enqueuer Thread")
                        .put("Enqueuer " + GeneralConstants.StructuredLogKeys.THREAD_NAME, SystemLookups.QUEUE_LIST.get(queueThreadPool.getKey()).getAppName()).build());
            }

            Iterator enQueueThreadPool = enQueueThreadPoolHashMap.keySet().iterator();
            Long queueId;
            while (enQueueThreadPool.hasNext()) {
                queueId = Long.valueOf(String.valueOf(enQueueThreadPool.next()));
//                CommonLogger.businessLogger.info("Awaiting Termination for Enqueue Thread: " + queueId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Awaiting Termination for Enqueue Thread")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queueId).build());
                enQueueThreadPoolHashMap.get(queueId).awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
                enQueueThreadPoolHashMap.remove(queueId);
//                CommonLogger.businessLogger.info("Removed Enqueue ThreadPool for Queue " + queueId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removed Enqueue ThreadPool for Queue")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queueId).build());
                shutdownFlagPool.remove(queueId);
//                CommonLogger.businessLogger.info("Removed Enqueue Flag for Queue " + queueId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removed Enqueue Flag for Queue")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queueId).build());
                smsToBeSent.remove(queueId);
//                CommonLogger.businessLogger.info("Removed Enqueue Queue for Queue " + queueId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removed Enqueue Queue for Queue")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queueId).build());
//                CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(databaseConnectionPool.get(queueId).getC3p0ConnectionPool()));
//                databaseConnectionPool.get(queueId).closeConnectionPool();
//                databaseConnectionPool.remove(queueId);
//                CommonLogger.businessLogger.info("Removed Enqueue DatabasePool for Queue " + queueId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removed Enqueue DatabasePool for Queue")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queueId).build());
                enQueueThreadPool = enQueueThreadPoolHashMap.keySet().iterator();
            }

            SHUTDOWN_FLAG = true;

            smsValidationThreadPool.shutdown();
            rollBacksmsThreadPool.shutdown();
            logThreadPool.shutdown();
            archiveThreadPool.shutdown();
            serviceQuotaUpdaterExecutor.shutdown();
            
            smsValidationThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            CommonLogger.businessLogger.info("SMS Validation Thread Pool Closed Successfully");
            rollBacksmsThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            CommonLogger.businessLogger.info("Rollback SMS Thread Pool Closed Successfully");
            logThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            CommonLogger.businessLogger.info("Logging Thread Pool Closed Successfully");
            archiveThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            CommonLogger.businessLogger.info("Archiving Thread Pool Closed Successfully");
            reloadingThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info("Reloading Thread Closed Successfully");
            serviceQuotaUpdaterExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            CommonLogger.businessLogger.info("ServiceQuotaUpdater Thread Pool Closed Successfully");

            MONITOR_THREAD_SHUTDOWN_FLAG = true;
            monitoringThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info("Monitoring Thread Closed Successfully");

            DataSourceManger.closeConnectionPool();
            CommonLogger.businessLogger.info("DataSourceManager Closed Successfully");

//            CommonLogger.businessLogger.info("Request Logging Queue Details ||  Size: " + requestsToBeLogged.size()
//                    + " | Remaining Capacity: " + requestsToBeLogged.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Request Logging Queue Details")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, requestsToBeLogged.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, requestsToBeLogged.remainingCapacity()).build());

//            CommonLogger.businessLogger.info("Messages Archiving Queue Details ||  Size: " + messagesToBeArchived.size()
//                    + " | Remaining Capacity: " + messagesToBeArchived.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Messages Archiving Queue Details")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, messagesToBeArchived.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, messagesToBeArchived.remainingCapacity()).build());

//            CommonLogger.businessLogger.info("SMS Validation Queue Details ||  Size: " + smsToBeValidated.size()
//                    + " | Remaining Capacity: " + smsToBeValidated.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Validation Queue Details")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, smsToBeValidated.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, smsToBeValidated.remainingCapacity()).build());

//            CommonLogger.businessLogger.info("SMS Rollback Queue Details ||  Size: " + smsToBeRollbacked.size()
//                    + " | Remaining Capacity: " + smsToBeRollbacked.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Rollback Queue Details")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, smsToBeRollbacked.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, smsToBeRollbacked.remainingCapacity()).build());

            for (Map.Entry<Long, ArrayBlockingQueue<SmsBusinessModel>> entry : smsToBeSent.entrySet()) {
//                CommonLogger.businessLogger.info("SMS Sending Queue Details || queueId: " + entry.getKey() + " |  Size: " + entry.getValue().size()
//                        + " | Remaining Capacity: " + entry.getValue().remainingCapacity());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Sending Queue Details")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, entry.getKey())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, entry.getValue().size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, entry.getValue().remainingCapacity()).build());
            }
            
//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())));
            CommonLogger.businessLogger.info("Interfaces UnDeployed Successfully");
        } catch (InterruptedException e) {
            CommonLogger.businessLogger.info("Error While Shutting Down Threads");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        } catch (Exception e) {
            CommonLogger.businessLogger.info("Error While Shutting Down Threads");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }

    public static void reloadData() throws CommonException {
        try {
//            CommonLogger.businessLogger.info("Started Reloading Data..");
//            CommonLogger.businessLogger.info("Current Data...");
//            CommonLogger.businessLogger.info("Current Services Count: " + SystemLookups.SERVICES.size());
//            CommonLogger.businessLogger.info("Current Queues Count: " + SystemLookups.QUEUE_LIST.size());
//            CommonLogger.businessLogger.info("Current AdsGroups Count: " + SystemLookups.ADS_GROUPS.size());
//            CommonLogger.businessLogger.info("Current SmsGroups Count: " + SystemLookups.SMS_GROUPS.size());
//            CommonLogger.businessLogger.info("Current SystemProperties Count: " + SystemLookups.SYSTEM_PROPERTIES.size());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started Reloading Data")
                    .put(GeneralConstants.StructuredLogKeys.SERVICE_COUNT, SystemLookups.SERVICES.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, SystemLookups.QUEUE_LIST.size())
                    .put("Ads " + GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, SystemLookups.ADS_GROUPS.size())
                    .put("SMS " + GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, SystemLookups.SMS_GROUPS.size())
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_COUNT, SystemLookups.SYSTEM_PROPERTIES.size()).build());

            Initializer.loadInterfacesData();
            Initializer.loadSystemProperties();
            Initializer.loadInterfacesLookups();
            MainService mainService = new MainService();
            SRC_ID_SYSTEM_PROPERIES = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_WEBSERIVCE_REST);
            CONNECTION_TIMEOUT = Integer.parseInt(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.CONNECTION_TIMEOUT));
            READ_TIMEOUT = Integer.parseInt(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.READ_TIMEOUT));
            Defines.INTERFACES.MAX_CONCURRENT_REQUESTS_VALUE = Long.parseLong(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_CONCURRENT_REQUESTS));
            Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE = Float.parseFloat(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE));
            Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_OCCUPANCY_RATE_VALUE = Float.parseFloat(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_OCCUPANCY_RATE));
            //Initializer.loadAppsQueues();

            for (Map.Entry<Long, QueueModel> queue : SystemLookups.QUEUE_LIST.entrySet()) {
                if (!enQueueThreadPoolHashMap.containsKey(queue.getKey())) {
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Adding New Enqueue Queue//////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//QueueName: " + queue.getValue().getAppName() + " Queue Id: " + queue.getKey());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Adding new Enqueue Queue")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queue.getValue().getAppName())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queue.getKey()).build());

                    smsToBeSent.put(queue.getKey(), new ArrayBlockingQueue<SmsBusinessModel>(MAX_SMS_SEND_QUEUE_SIZE));
//                    CommonLogger.businessLogger.info("//Java Queue MaxSize: " + MAX_SMS_SEND_QUEUE_SIZE);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Queue Stats")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, MAX_SMS_SEND_QUEUE_SIZE).build());

                    shutdownFlagPool.put(queue.getKey(), Boolean.FALSE);
//                    CommonLogger.businessLogger.info("//Shutdown Flag: " + Boolean.FALSE);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shutdown Flag stats")
                            .put(GeneralConstants.StructuredLogKeys.SHUTDOWN_FLAG, Boolean.FALSE).build());

//                    databaseConnectionPool.put(queue.getKey(), new DataSourceManager(queue.getValue().getDatabaseUrl(), 
//                            queue.getValue().getSchemaName(), Utility.decrypt(queue.getValue().getSchemaPassword(), encryptionKey)));
//                    CommonLogger.businessLogger.info("//Database Connection Pool: ");
//                    CommonLogger.businessLogger.info("//    URL: " + queue.getValue().getDatabaseUrl());
//                    CommonLogger.businessLogger.info("//    UserName: " + queue.getValue().getSchemaName());
//                    CommonLogger.businessLogger.info("//    Password: " + queue.getValue().getSchemaPassword());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Connection Pool Stats")
                            .put(GeneralConstants.StructuredLogKeys.URL, queue.getValue().getDatabaseUrl())
                            .put(GeneralConstants.StructuredLogKeys.USER_NAME, queue.getValue().getSchemaName())
                            .put(GeneralConstants.StructuredLogKeys.PASSWORD, queue.getValue().getSchemaPassword()).build());

                    enQueueThreadPoolHashMap.put(queue.getKey(), Executors.newFixedThreadPool(MAX_QUEUE_THREAD_POOL_SIZE));
//                    CommonLogger.businessLogger.info("//Thread Pool Max Size: " + MAX_QUEUE_THREAD_POOL_SIZE);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Pool Stats")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_MAX_SIZE, MAX_QUEUE_THREAD_POOL_SIZE).build());

                    for (int i = 0; i < MAX_QUEUE_THREAD_POOL_SIZE; i++) {
                        enQueueThreadPoolHashMap.get(queue.getKey()).execute(new EnQueueManagerThread(queue.getKey(), String.valueOf(queue.getValue().getAppName()), i));
                    }
//                    CommonLogger.businessLogger.info("//Threads Initialized: " + MAX_QUEUE_THREAD_POOL_SIZE);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Initialization Stats")
                            .put(GeneralConstants.StructuredLogKeys.THREADS_INITIALIZED, MAX_QUEUE_THREAD_POOL_SIZE).build());
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
                }
            }

            Iterator enQueueThreadPool = enQueueThreadPoolHashMap.keySet().iterator();
            Long queueId;
            while (enQueueThreadPool.hasNext()) {
                queueId = Long.valueOf(String.valueOf(enQueueThreadPool.next()));
                if (!SystemLookups.QUEUE_LIST.containsKey(queueId)) {
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Removing Enqueue Queue/////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Queue Id: " + queueId);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removing Enqueue Queue")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queueId).build());

                    shutdownFlagPool.put(queueId, Boolean.TRUE);
                    enQueueThreadPoolHashMap.get(queueId).shutdown();
                    enQueueThreadPoolHashMap.get(queueId).awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);

                    smsToBeSent.remove(queueId);
                    CommonLogger.businessLogger.info("Java Queue Closed And Removed");

//                    CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(databaseConnectionPool.get(queueId).getC3p0ConnectionPool()));
//                    databaseConnectionPool.get(queueId).closeConnectionPool();
//                    databaseConnectionPool.remove(queueId);
                    CommonLogger.businessLogger.info("DatabasePool Closed And Removed");

                    enQueueThreadPoolHashMap.remove(queueId);
                    CommonLogger.businessLogger.info("Thread Pool Closed And Removed");

                    shutdownFlagPool.remove(queueId);
                    CommonLogger.businessLogger.info("ShutdownFlag Closed And Removed");
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
                    enQueueThreadPool = enQueueThreadPoolHashMap.keySet().iterator();
                }
            }

//            CommonLogger.businessLogger.info("Updated Data...");
//            CommonLogger.businessLogger.info("Current Services Count: " + SystemLookups.SERVICES.size());
//            CommonLogger.businessLogger.info("Current Queues Count: " + SystemLookups.QUEUE_LIST.size());
//            CommonLogger.businessLogger.info("Current AdsGroups Count: " + SystemLookups.ADS_GROUPS.size());
//            CommonLogger.businessLogger.info("Current SmsGroups Count: " + SystemLookups.SMS_GROUPS.size());
//            CommonLogger.businessLogger.info("Current SystemProperties Count: " + SystemLookups.SYSTEM_PROPERTIES.size());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Current Data Updated")
                    .put(GeneralConstants.StructuredLogKeys.SERVICE_COUNT, SystemLookups.SERVICES.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, SystemLookups.QUEUE_LIST.size())
                    .put("Ads " + GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, SystemLookups.ADS_GROUPS.size())
                    .put("SMS " + GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, SystemLookups.SMS_GROUPS.size())
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_COUNT, SystemLookups.SYSTEM_PROPERTIES.size()).build());
            for (Map.Entry<String, ServicesModel> service : SystemLookups.SERVICES.entrySet()) {
//                CommonLogger.businessLogger.info(service.getKey() + " Service Info || AdsConsultCounter:" + service.getValue().getAdsConsultCounter()
//                        + " | AllowedSms:" + service.getValue().getAllowedSms() + " | AppId:" + service.getValue().getAppId() + " | CampaignCount:" + ((service.getValue().getCampiagnModel() != null) ? service.getValue().getCampiagnModel().size() : "null")
//                        + " | ConsultCounter:" + service.getValue().getConsultCounter() + " | Creator:" + service.getValue().getCreator() + " | DailyQouta:" + service.getValue().getDailyQuota()
//                        + " | DeliveryReport:" + service.getValue().getDeliveryReport() + " | HasWhiteList:" + service.getValue().getHasWhitelist() + " | Id:" + service.getValue().getVersionId()
//                        + " | InterfaceType:" + service.getValue().getInterfaceType() + " | QueueName:" + ((service.getValue().getQueueModel() != null) ? service.getValue().getQueueModel().getQueueName() : "null") + " | ServiceId:" + service.getValue().getServiceId()
//                        + " | Status:" + service.getValue().getStatus() + " | SupportAds:" + service.getValue().getSupportAds() + " | SystemCategory:" + service.getValue().getSystemCategory()
//                        + " | SystemType:" + service.getValue().getSystemType() + " | WhiteListSize:" + ((service.getValue().getWhiteListModel() != null) ? service.getValue().getWhiteListModel().size() : "null"));
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Status")
                        .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, service.getKey())
                        .put(GeneralConstants.StructuredLogKeys.ADS_CONSULT_COUNTER, service.getValue().getAdsConsultCounter())
                        .put(GeneralConstants.StructuredLogKeys.ALLOWED_SMS, service.getValue().getAllowedSms())
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, service.getValue().getAppId())
                        .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_COUNT, ((service.getValue().getCampiagnModel() != null) ? service.getValue().getCampiagnModel().size() : "null"))
                        .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, service.getValue().getConsultCounter())
                        .put(GeneralConstants.StructuredLogKeys.CREATOR, service.getValue().getCreator())
                        .put(GeneralConstants.StructuredLogKeys.DAILY_QUOTA, service.getValue().getDailyQuota())
                        .put(GeneralConstants.StructuredLogKeys.DELIVERY_REPORT, service.getValue().getDeliveryReport())
                        .put(GeneralConstants.StructuredLogKeys.HASH_WHITE_LIST, service.getValue().getHasWhitelist())
                        .put(GeneralConstants.StructuredLogKeys.SERVIC_ID, service.getValue().getVersionId())
                        .put(GeneralConstants.StructuredLogKeys.INTERFACE_TYPE, service.getValue().getInterfaceType())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, ((service.getValue().getQueueModel() != null) ? service.getValue().getQueueModel().getQueueName() : "null"))
                        .put(GeneralConstants.StructuredLogKeys.STATUS, service.getValue().getStatus())
                        .put(GeneralConstants.StructuredLogKeys.SUPPORT_ADS, service.getValue().getSupportAds())
                        .put(GeneralConstants.StructuredLogKeys.SYSTEM_CATEGORY, service.getValue().getSystemCategory())
                        .put(GeneralConstants.StructuredLogKeys.SYSTEM_TYPE, service.getValue().getSystemType())
                        .put(GeneralConstants.StructuredLogKeys.WHITE_LIST_SIZE, ((service.getValue().getWhiteListModel() != null) ? service.getValue().getWhiteListModel().size() : "null")).build());
            }
        } catch (CommonException ce) {
            CommonLogger.businessLogger.info("Error While Refreshing Data");
            CommonLogger.businessLogger.error(ce.getErrorMsg());
            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
            throw ce;
        } catch (InterruptedException e) {
            CommonLogger.businessLogger.info("Error While Refreshing Data");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), 0);
        } catch (Exception e) {
            CommonLogger.businessLogger.info("Error While Refreshing Data");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), 0);
        }
    }
}
