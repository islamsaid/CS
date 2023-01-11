/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.enqueue.rest.common;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.enqueue.rest.threads.MonitorThread;
import com.asset.contactstrategy.enqueue.rest.threads.ReloadingThread;
import com.asset.contactstrategy.interfaces.models.RESTLogModel;
import com.asset.contactstrategy.interfaces.threads.LoggingManagerThread;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Esmail.Anbar
 */
public class ConfigurationManager implements ServletContextListener {

    public static boolean SHUTDOWN_FLAG = false;
    public static boolean MONITOR_THREAD_SHUTDOWN_FLAG = false;
    public static String encryptionKey;
    public static HashMap<String, DataSourceManager> senderQueuesDatabaseConnectionPool;
    public static HashMap<String, DataSourceManager> procedureQueuesDatabaseConnectionPool;
    public static Thread reloadingThread;
    public static Thread monitoringThread;
    public static HashMap<String, String> SRC_ID_SYSTEM_PROPERIES;
    public static HashMap<String, QueueModel> senderQueueListServiceAndStatusApproved;
    public static HashMap<String, QueueModel> procedureQueueListServiceAndStatusApproved;

    public static ExecutorService logThreadPool;
    public static int MAX_LOGGING_THREAD_POOL_SIZE;

    public static AtomicLong concurrentRequests;

    public static AtomicLong enqueueSmsAccumulator;
    public static AtomicLong enqueueSmsSuccessCount;
    public static AtomicLong enqueueSmsFailedCount;

    public static AtomicLong enqueueBridgeSmsAccumulator;
    public static AtomicLong enqueueBridgeSmsSuccessCount;
    public static AtomicLong enqueueBridgeSmsFailedCount;

    public static AtomicLong checkInterfaceAccumulator;
    public static AtomicLong checkInterfaceSuccessCount;
    public static AtomicLong checkInterfaceFailedCount;

    public static AtomicLong readyCheckAccumulator;
    public static AtomicLong readyCheckSuccessCount;
    public static AtomicLong readyCheckFailedCount;

    public static AtomicLong refreshAccumulator;
    public static AtomicLong refreshSuccessCount;
    public static AtomicLong refreshFailedCount;

    public static ArrayBlockingQueue<RESTLogModel> loggingQueue;
    public static AtomicBoolean SHUTDOWN_LOGGING_THREADS = new AtomicBoolean(false);

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
            Defines.runningProjectId = GeneralConstants.SRC_ID_ENQUEUE_SMS_REST;
            initializeAll();
            concurrentRequests = new AtomicLong(0);

            enqueueSmsAccumulator = new AtomicLong(0);
            enqueueSmsSuccessCount = new AtomicLong(0);
            enqueueSmsFailedCount = new AtomicLong(0);

            enqueueBridgeSmsAccumulator = new AtomicLong(0);
            enqueueBridgeSmsSuccessCount = new AtomicLong(0);
            enqueueBridgeSmsFailedCount = new AtomicLong(0);

            checkInterfaceAccumulator = new AtomicLong(0);
            checkInterfaceSuccessCount = new AtomicLong(0);
            checkInterfaceFailedCount = new AtomicLong(0);

            readyCheckAccumulator = new AtomicLong(0);
            readyCheckSuccessCount = new AtomicLong(0);
            readyCheckFailedCount = new AtomicLong(0);

            refreshAccumulator = new AtomicLong(0);
            refreshSuccessCount = new AtomicLong(0);
            refreshFailedCount = new AtomicLong(0);

            Defines.INTERFACES.MAX_CONCURRENT_REQUESTS_VALUE = Long.parseLong(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_CONCURRENT_REQUESTS));
            Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE = Float.parseFloat(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE));

            Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME_VALUE = Integer.parseInt(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME));

            loggingQueue = new ArrayBlockingQueue<>(Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE_VALUE);

            encryptionKey = Defines.ENCRYPTION_KEY;
            MONITOR_THREAD_SHUTDOWN_FLAG = false;
            SHUTDOWN_FLAG = false;
            senderQueuesDatabaseConnectionPool = new HashMap<>();
//            CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//            CommonLogger.businessLogger.info("//Sender Queues/////////////////////////////////////////////////////////////////////");
            for (Map.Entry<String, QueueModel> queue : senderQueueListServiceAndStatusApproved.entrySet()) {
//                CommonLogger.businessLogger.info("//QueueName: " + queue.getKey() + " Queue Id: " + queue.getValue().getAppId() + " QueueVersion: " + queue.getValue().getAppName());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sender Queues Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queue.getKey())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queue.getValue().getAppId())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_VERSION, queue.getValue().getAppName())
                        .put(GeneralConstants.StructuredLogKeys.URL, queue.getValue().getDatabaseUrl())
                        .put(GeneralConstants.StructuredLogKeys.USER_NAME, queue.getValue().getSchemaName())
                        .put(GeneralConstants.StructuredLogKeys.PASSWORD, queue.getValue().getSchemaPassword()).build());
                //No need to decrypte the schema password because its already decrypted
                senderQueuesDatabaseConnectionPool.put(queue.getKey(), new DataSourceManager(queue.getValue().getDatabaseUrl(),
                        queue.getValue().getSchemaName(), queue.getValue().getSchemaPassword()));
//                CommonLogger.businessLogger.info("//Database Connection Pool: ");
//                CommonLogger.businessLogger.info("//    URL: " + queue.getValue().getDatabaseUrl());
//                CommonLogger.businessLogger.info("//    UserName: " + queue.getValue().getSchemaName());
//                CommonLogger.businessLogger.info("//    Password: " + queue.getValue().getSchemaPassword());
//                CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Connection Pool").build());
            }

            procedureQueuesDatabaseConnectionPool = new HashMap<>();
//            CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//            CommonLogger.businessLogger.info("//Procedure Queues/////////////////////////////////////////////////////////////////////");
            for (Map.Entry<String, QueueModel> queue : procedureQueueListServiceAndStatusApproved.entrySet()) {
//                CommonLogger.businessLogger.info("//QueueName: " + queue.getKey() + " Queue Id: " + queue.getValue().getAppId() + " QueueVersion: " + queue.getValue().getAppName());
                //No need to decrypte the schema password because its already decrypted
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Procedure Queues Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queue.getKey())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queue.getValue().getAppId())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_VERSION, queue.getValue().getAppName())
                        .put(GeneralConstants.StructuredLogKeys.URL, queue.getValue().getDatabaseUrl())
                        .put(GeneralConstants.StructuredLogKeys.USER_NAME, queue.getValue().getSchemaName())
                        .put(GeneralConstants.StructuredLogKeys.PASSWORD, queue.getValue().getSchemaPassword()).build());
                procedureQueuesDatabaseConnectionPool.put(queue.getKey(), new DataSourceManager(queue.getValue().getDatabaseUrl(),
                        queue.getValue().getSchemaName(), queue.getValue().getSchemaPassword()));
//                CommonLogger.businessLogger.info("//Database Connection Pool: ");
//                CommonLogger.businessLogger.info("//    URL: " + queue.getValue().getDatabaseUrl());
//                CommonLogger.businessLogger.info("//    UserName: " + queue.getValue().getSchemaName());
//                CommonLogger.businessLogger.info("//    Password: " + queue.getValue().getSchemaPassword());
//                CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Connection Pool").build());
            }

            MAX_LOGGING_THREAD_POOL_SIZE = Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE_VALUE;
            logThreadPool = Executors.newFixedThreadPool(MAX_LOGGING_THREAD_POOL_SIZE);
            for (int i = 0; i < MAX_LOGGING_THREAD_POOL_SIZE; i++) {
                logThreadPool.execute(new LoggingManagerThread(Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE_VALUE,
                        Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD_VALUE, loggingQueue,
                        Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT_VALUE, SHUTDOWN_LOGGING_THREADS, i));
            }
//            CommonLogger.businessLogger.info("Logging Thread Pool MaxSize: " + MAX_LOGGING_THREAD_POOL_SIZE);
//            CommonLogger.businessLogger.info("Logging Threads Initialized: " + MAX_LOGGING_THREAD_POOL_SIZE);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Logging Thread Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_MAX_SIZE, MAX_LOGGING_THREAD_POOL_SIZE)
                    .put(GeneralConstants.StructuredLogKeys.THREADS_INITIALIZED, MAX_LOGGING_THREAD_POOL_SIZE).build());

            reloadingThread = new Thread(new ReloadingThread());
            reloadingThread.start();
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Reloading Thread Initialized").build());

            monitoringThread = new Thread(new MonitorThread());
            monitoringThread.start();
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Monitoring Thread Initialized").build());
        } catch (CommonException e) {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Error While Initializing").build());
            CommonLogger.businessLogger.error(e.getErrorMsg());
            CommonLogger.errorLogger.error(e.getErrorMsg(), e);
            throw e;
        } catch (Exception e) {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Error While Initializing").build());
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        }

    }

    private static void initializeAll() throws Exception {
        Initializer.readPropertiesFile();
        Initializer.initializeLoggers();
        Initializer.initializeDataSource();
        Initializer.loadSystemProperties();
        
         if(Defines.MESSAGING_MODE.equals(Defines.RABBITMQ)){
            Initializer.initializeRabbitmq();
        }
         
        MainService mainService = new MainService();
        SRC_ID_SYSTEM_PROPERIES = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST);
        senderQueueListServiceAndStatusApproved = mainService.getApplicationQueuesServiceAndStatusApproved(Defines.VFE_CS_QUEUES_TYPE_LK.SMS_SENDER_QUEUES);
        procedureQueueListServiceAndStatusApproved = mainService.getApplicationQueuesServiceAndStatusApproved(Defines.VFE_CS_QUEUES_TYPE_LK.PROCEDURE_QUEUES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            Iterator databasePool = senderQueuesDatabaseConnectionPool.keySet().iterator();
            String queueName;
            while (databasePool.hasNext()) {
                queueName = String.valueOf(databasePool.next());
//                CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(senderQueuesDatabaseConnectionPool.get(queueName).getC3p0ConnectionPool()));
                CommonLogger.businessLogger.info(Utility.getHikariConnectionPoolStats(senderQueuesDatabaseConnectionPool.get(queueName).getHikariDataSourse()));
                senderQueuesDatabaseConnectionPool.get(queueName).closeConnectionPool();
                senderQueuesDatabaseConnectionPool.remove(queueName);
//                CommonLogger.businessLogger.info("Removed Sender DatabasePool for Queue " + queueName);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removed Sender DatabasePool for Queue")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
                databasePool = senderQueuesDatabaseConnectionPool.keySet().iterator();
            }

            databasePool = procedureQueuesDatabaseConnectionPool.keySet().iterator();
            queueName = "";
            while (databasePool.hasNext()) {
                queueName = String.valueOf(databasePool.next());
//                CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(procedureQueuesDatabaseConnectionPool.get(queueName).getC3p0ConnectionPool()));
//                CommonLogger.businessLogger.info(Utility.getHikariConnectionPoolStats(procedureQueuesDatabaseConnectionPool.get(queueName).getHikariDataSourse()));
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ConnectionPool stats")
                        .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, Utility.getHikariConnectionPoolStats(procedureQueuesDatabaseConnectionPool.get(queueName).getHikariDataSourse())).build());
                procedureQueuesDatabaseConnectionPool.get(queueName).closeConnectionPool();
                procedureQueuesDatabaseConnectionPool.remove(queueName);
//                CommonLogger.businessLogger.info("Removed Procedure DatabasePool for Queue " + queueName);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removed Procedure DatabasePool for Queue")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
                databasePool = procedureQueuesDatabaseConnectionPool.keySet().iterator();
            }

            SHUTDOWN_FLAG = true;
            SHUTDOWN_LOGGING_THREADS.set(true);

            logThreadPool.shutdown();
            logThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Logging Thread Pool Closed Successfully").build());

            reloadingThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Reloading Thread Closed Successfully").build());

            MONITOR_THREAD_SHUTDOWN_FLAG = true;
            monitoringThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Monitoring Thread Closed Successfully").build());

            DataSourceManger.closeConnectionPool();
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "DataSourceManager Closed Successfully").build());

//            CommonLogger.businessLogger.info("Request Logging Queue Details ||  Size: " + loggingQueue.size()
//                    + " | Remaining Capacity: " + loggingQueue.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Rquest Logging Queue Details")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, loggingQueue.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, loggingQueue.remainingCapacity()).build());

//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())).build());
//            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Dump Ended").build());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Enqueue REST UnDeployed Successfully").build());
        } catch (InterruptedException e) {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Error While Shutting Down Threads").build());
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        } catch (Exception e) {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Error While Shutting Down Threads").build());
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }

    public static void reloadData() throws CommonException {
        try {
//            CommonLogger.businessLogger.info("Started Reloading Data..");
//            CommonLogger.businessLogger.info("Current Data...");
//            CommonLogger.businessLogger.info("Current SystemProperties Count: " + SystemLookups.SYSTEM_PROPERTIES.size());
//            CommonLogger.businessLogger.info("Current Sender QueueList Count: " + senderQueueListServiceAndStatusApproved.size());
//            CommonLogger.businessLogger.info("Current Procedure QueueList Count: " + procedureQueueListServiceAndStatusApproved.size());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started Reloading Data. Current Data")
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_COUNT, SystemLookups.SYSTEM_PROPERTIES.size())
                    .put(GeneralConstants.StructuredLogKeys.SENDER_QUEUELIST_COUNT, senderQueueListServiceAndStatusApproved.size())
                    .put(GeneralConstants.StructuredLogKeys.PROCEDURE_QUEUELIST_COUNT, procedureQueueListServiceAndStatusApproved.size()).build());

            Initializer.loadSystemProperties();
            MainService mainService = new MainService();
            SRC_ID_SYSTEM_PROPERIES = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST);
            senderQueueListServiceAndStatusApproved = mainService.getApplicationQueuesServiceAndStatusApproved(Defines.VFE_CS_QUEUES_TYPE_LK.SMS_SENDER_QUEUES);
            procedureQueueListServiceAndStatusApproved = mainService.getApplicationQueuesServiceAndStatusApproved(Defines.VFE_CS_QUEUES_TYPE_LK.PROCEDURE_QUEUES);

            Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME_VALUE = Integer.parseInt(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME));
            Defines.INTERFACES.MAX_CONCURRENT_REQUESTS_VALUE = Long.parseLong(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_CONCURRENT_REQUESTS));
            Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE = Float.parseFloat(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE));

            for (Map.Entry<String, QueueModel> queue : senderQueueListServiceAndStatusApproved.entrySet()) {
                if (!senderQueuesDatabaseConnectionPool.containsKey(queue.getKey())) {
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Adding New Enqueue Queue//////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//QueueName: " + queue.getKey() + " Queue Id: " + queue.getValue().getAppId() + " QueueVersion: " + queue.getValue().getAppName());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Adding New Enqueue Queue")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queue.getKey())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queue.getValue().getAppId())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_VERSION, queue.getValue().getAppName())
                            .put(GeneralConstants.StructuredLogKeys.URL, queue.getValue().getDatabaseUrl())
                            .put(GeneralConstants.StructuredLogKeys.USER_NAME, queue.getValue().getSchemaName())
                            .put(GeneralConstants.StructuredLogKeys.PASSWORD, queue.getValue().getSchemaPassword()).build());
                    senderQueuesDatabaseConnectionPool.put(queue.getKey(), new DataSourceManager(queue.getValue().getDatabaseUrl(),
                            queue.getValue().getSchemaName(), queue.getValue().getSchemaPassword()));
//                    CommonLogger.businessLogger.info("//Database Connection Pool: ");
//                    CommonLogger.businessLogger.info("//    URL: " + queue.getValue().getDatabaseUrl());
//                    CommonLogger.businessLogger.info("//    UserName: " + queue.getValue().getSchemaName());
//                    CommonLogger.businessLogger.info("//    Password: " + queue.getValue().getSchemaPassword());
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Connection Pool")
//                            .put(GeneralConstants.StructuredLogKeys.URL, queue.getValue().getDatabaseUrl())
//                            .put(GeneralConstants.StructuredLogKeys.USER_NAME, queue.getValue().getSchemaName())
//                            .put(GeneralConstants.StructuredLogKeys.PASSWORD, queue.getValue().getSchemaPassword()).build());
                }
            }

            Iterator databasePool = senderQueuesDatabaseConnectionPool.keySet().iterator();
            String queueName;
            while (databasePool.hasNext()) {
                queueName = String.valueOf(databasePool.next());
                if (!senderQueueListServiceAndStatusApproved.containsKey(queueName)) {
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Removing Enqueue Queue/////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Queue Name: " + queueName);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removing Enqueue Queue")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
//                    CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(senderQueuesDatabaseConnectionPool.get(queueName).getC3p0ConnectionPool()));
//                    CommonLogger.businessLogger.info(Utility.getHikariConnectionPoolStats(senderQueuesDatabaseConnectionPool.get(queueName).getHikariDataSourse()));
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ConnectionPool Stats")
                            .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, Utility.getHikariConnectionPoolStats(senderQueuesDatabaseConnectionPool.get(queueName).getHikariDataSourse())).build());
                    senderQueuesDatabaseConnectionPool.get(queueName).closeConnectionPool();
                    senderQueuesDatabaseConnectionPool.remove(queueName);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "//DatabasePool Closed And Removed").build());
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
                    databasePool = senderQueuesDatabaseConnectionPool.keySet().iterator();
                }
            }

            for (Map.Entry<String, QueueModel> queue : procedureQueueListServiceAndStatusApproved.entrySet()) {
                if (!procedureQueuesDatabaseConnectionPool.containsKey(queue.getKey())) {
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Adding New Enqueue Queue//////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//QueueName: " + queue.getKey() + " Queue Id: " + queue.getValue().getAppId() + " QueueVersion: " + queue.getValue().getAppName());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Adding New Enqueue Queue")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queue.getKey())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, queue.getValue().getAppId())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_VERSION, queue.getValue().getAppName())
                            .put(GeneralConstants.StructuredLogKeys.URL, queue.getValue().getDatabaseUrl())
                            .put(GeneralConstants.StructuredLogKeys.USER_NAME, queue.getValue().getSchemaName())
                            .put(GeneralConstants.StructuredLogKeys.PASSWORD, queue.getValue().getSchemaPassword()).build());
                    procedureQueuesDatabaseConnectionPool.put(queue.getKey(), new DataSourceManager(queue.getValue().getDatabaseUrl(),
                            queue.getValue().getSchemaName(), queue.getValue().getSchemaPassword()));
//                    CommonLogger.businessLogger.info("//Database Connection Pool: ");
//                    CommonLogger.businessLogger.info("//    URL: " + queue.getValue().getDatabaseUrl());
//                    CommonLogger.businessLogger.info("//    UserName: " + queue.getValue().getSchemaName());
//                    CommonLogger.businessLogger.info("//    Password: " + queue.getValue().getSchemaPassword());
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Connection Pool").build());
                }
            }

            databasePool = procedureQueuesDatabaseConnectionPool.keySet().iterator();
            queueName = "";
            while (databasePool.hasNext()) {
                queueName = String.valueOf(databasePool.next());
                if (!procedureQueueListServiceAndStatusApproved.containsKey(queueName)) {
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Removing Enqueue Queue/////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Queue Name: " + queueName);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removing Enqueue Queue")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());

//                    CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(procedureQueuesDatabaseConnectionPool.get(queueName).getC3p0ConnectionPool()));
//                    CommonLogger.businessLogger.info(Utility.getHikariConnectionPoolStats(procedureQueuesDatabaseConnectionPool.get(queueName).getHikariDataSourse()));
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ConnectionPool Stats")
                            .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, Utility.getHikariConnectionPoolStats(procedureQueuesDatabaseConnectionPool.get(queueName).getHikariDataSourse())).build());
                    procedureQueuesDatabaseConnectionPool.get(queueName).closeConnectionPool();
                    procedureQueuesDatabaseConnectionPool.remove(queueName);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "//DatabasePool Closed And Removed").build());
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
                    databasePool = procedureQueuesDatabaseConnectionPool.keySet().iterator();
                }
            }

//            CommonLogger.businessLogger.info("Updated Data...");
//            CommonLogger.businessLogger.info("Current SystemProperties Count: " + SystemLookups.SYSTEM_PROPERTIES.size());
//            CommonLogger.businessLogger.info("Current Sender QueueList Count: " + senderQueueListServiceAndStatusApproved.size());
//            CommonLogger.businessLogger.info("Current Procedure QueueList Count: " + procedureQueueListServiceAndStatusApproved.size());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated Data")
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_COUNT, SystemLookups.SYSTEM_PROPERTIES.size())
                    .put(GeneralConstants.StructuredLogKeys.SENDER_QUEUELIST_COUNT, senderQueueListServiceAndStatusApproved.size())
                    .put(GeneralConstants.StructuredLogKeys.PROCEDURE_QUEUELIST_COUNT, procedureQueueListServiceAndStatusApproved.size()).build());
        } catch (CommonException ce) {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Error While Refreshing Data").build());
            CommonLogger.businessLogger.error(ce.getErrorMsg());
            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Error While Refreshing Data").build());
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), 0);
        }
    }
}
