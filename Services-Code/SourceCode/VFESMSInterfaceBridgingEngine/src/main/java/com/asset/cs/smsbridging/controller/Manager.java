package com.asset.cs.smsbridging.controller;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.interfaces.threads.LivenessThread;
import com.asset.cs.smsbridging.models.HTTPResponse;
import com.asset.cs.smsbridging.models.QueueNeedsHolder;
import com.asset.cs.smsbridging.models.ServiceNeedsHolder;
import com.asset.cs.smsbridging.threads.ArchiverThread;
import com.asset.cs.smsbridging.threads.DequeuerThread;
import com.asset.cs.smsbridging.threads.EnqueuerThread;
import com.asset.cs.smsbridging.threads.HTTPSubmitterThread;
import com.asset.cs.smsbridging.threads.JSONConstructorThread;
import com.asset.cs.smsbridging.threads.MonitorThread;
import com.asset.cs.smsbridging.threads.ReloadThread;
import com.asset.cs.smsbridging.threads.ShutdownHookThread;
import com.asset.cs.smsbridging.threads.ShutdownThread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author aya.moawed 2595
 */
public class Manager {

    public static ReloadThread reloadThread = new ReloadThread();
    public static ShutdownThread shutdownThread;
    public static MonitorThread monitorThread;

    //Hash Map holding the curent queues of the instance along with their information and connections and shutdown flags
    public static HashMap<String, QueueNeedsHolder> workingAppQueues = new HashMap<>();
    //Hash Map holding the curent queues to be used with enqueuer threads
    // public static ConcurrentHashMap<String, QueueNeedsHolder> enqueuerAppQueues = new ConcurrentHashMap<>();
    //Hash Map holding services along with its info and the array blocking queues( holding the smss in a json structured form ) and the shutdown flag
    public static ConcurrentHashMap<String, ServiceNeedsHolder> workingServices = new ConcurrentHashMap<>();
    //Pool of json constructors
    public static ExecutorService jsonConstructorPool;
    //Queue holding JSON requests created by the JSON Constructor threads
    public static ArrayBlockingQueue<RequestPreparator> queueForJSONRequests;
    //Pool of http submitters
    public static ExecutorService httpSubmitterPool;
    //Queue holding HTTP responses created by the HTTP submitter threads
    public static ArrayBlockingQueue<HTTPResponse> queueForHTTPResult;
    //Queue holding SMSs will be enqueued again
    public static ConcurrentHashMap<String, ArrayBlockingQueue<ArrayList<SMSBridge>>> submitterEnqueuerQueueMap = new ConcurrentHashMap<String, ArrayBlockingQueue<ArrayList<SMSBridge>>>();
    //Pool of Archivers thread
    public static ExecutorService archiverPool;
    private static LivenessThread livenessThread = null;

    public static DataSourceManager queuDataSourceManager;

    public static void initializePropsLoggersAndDataSource() throws Exception {
        //Read properties files , initialize loggers , initialize datasource
        Initializer.readPropertiesFile(SMSBridgingDefines.propertiesFileConfigs);
        Initializer.initializeLoggers();
        Initializer.initializeDataSource();

    }

    public static void initialize() throws CommonException {
        try {
            CommonLogger.businessLogger.debug("Manager is starting to load system properties from database and get essential properties from property file");
            MainService mainService = new MainService();
            // LOAD CONFIGURATIONS
            SMSBridgingDefines.databaseConfigs = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE);
            checkDataBaseConfigsAvailability();
            CommonLogger.businessLogger.debug("Retrieved Database configuration for SRC ID [" + GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE + "] are " + SMSBridgingDefines.databaseConfigs);
            queuDataSourceManager = new DataSourceManager(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.QUEUES_DB_URL), SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.QUEUES_DB_USER), SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.QUEUES_DB_PASSWORD));
            int maxSizeForAppQueuesQueues = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_APP_QUEUE_QUEUE_SIZE_PROP));
            int maxSizeForServiceQueues = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_SERVICE_QUEUE_SIZE_PROP));
            int jsonConsructorPoolSize = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.JSON_CONSTRUCTOR_POOL_SIZE));
            int maxSizeForJSONQueue = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_JSON_CONSTRUCTOR_QUEUE_SIZE_PROP));
            int maxHTTPHitTimes = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_HTTP_HITS));
            int maxHTTPResultQueueSize = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_HTTP_RESULT_QUEUE_SIZE));
            int httpPoolSize = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.HTTP_POOL_SIZE));
            int archiverPoolSize = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.ARCHIVERS_POOL_SIZE));
            String queueNamesSplitter = SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.QUEUE_NAMES_SPLITTER);
            int dequeueWaitTime = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.DEQUEUE_WAIT_TIME));
            int dequeueBatchSize = Integer.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.DEQUEUE_BATCH_SIZE));
            CommonLogger.businessLogger.debug("Retrieved Properties File needed configuration for SRC ID [" + GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE + "] are {"
                    + SMSBridgingDefines.MAX_SERVICE_QUEUE_SIZE_PROP + "=[" + maxSizeForServiceQueues + "] , "
                    + SMSBridgingDefines.MAX_APP_QUEUE_QUEUE_SIZE_PROP + "=[" + maxSizeForAppQueuesQueues + "] , "
                    + SMSBridgingDefines.JSON_CONSTRUCTOR_POOL_SIZE + "=[" + jsonConsructorPoolSize + "] , "
                    + SMSBridgingDefines.MAX_JSON_CONSTRUCTOR_QUEUE_SIZE_PROP + "=[" + maxSizeForJSONQueue + "] , "
                    + SMSBridgingDefines.MAX_HTTP_HITS + "=[" + maxHTTPHitTimes + "] , "
                    + SMSBridgingDefines.ARCHIVERS_POOL_SIZE + "=[" + archiverPoolSize + "] , "
                    + SMSBridgingDefines.DEQUEUE_WAIT_TIME + "=[" + dequeueWaitTime + "] , "
                    + SMSBridgingDefines.QUEUE_NAMES_SPLITTER + "=[" + queueNamesSplitter + "]}");

            //LOAD APPLICATION QUEUES PART
            prepareArrayBlockingQueuesForApplicationQueues(maxSizeForAppQueuesQueues, queueNamesSplitter, dequeueWaitTime, dequeueBatchSize);
            //LOAD SERVICES PART
            prepareArrayBlockingQueuesForServices(maxSizeForServiceQueues);
            //LOAD JSON CONSTRUCTORS
            prepareJSONConstructors(jsonConsructorPoolSize, maxSizeForJSONQueue);
            //LOAD HTTP HITTERS
            prepareHTTPBulkSenders(httpPoolSize, maxHTTPHitTimes, maxHTTPResultQueueSize);
            //LOAD ARCHIVERS
            prepareArchivers(archiverPoolSize);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof CommonException) {
                throw new CommonException(((CommonException) ex).getErrorMsg(), ErrorCodes.INIT_ERROR);
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.INIT_ERROR);
            }
        }
    }

    public static void reload() throws CommonException {
        try {
            CommonLogger.businessLogger.info("Manager is starting to load system properties from database and get essential properties from property file");
            MainService mainService = new MainService();

            // LOAD CONFIGURATIONS
            SMSBridgingDefines.databaseConfigs = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE);
            checkDataBaseConfigsAvailability();
            CommonLogger.businessLogger.debug("Retrieved Database configuration for SRC ID [" + GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE + "] are " + SMSBridgingDefines.databaseConfigs);
            int maxSizeForServiceQueues = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_SERVICE_QUEUE_SIZE_PROP));
            int maxSizeForAppQueuesQueues = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_APP_QUEUE_QUEUE_SIZE_PROP));
            int jsonConsructorPoolSize = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.JSON_CONSTRUCTOR_POOL_SIZE));
            int dequeueWaitTime = Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.DEQUEUE_WAIT_TIME));
            String queueNamesSplitter = SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.QUEUE_NAMES_SPLITTER);
            int dequeueBatchSize = Integer.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.DEQUEUE_BATCH_SIZE));
            CommonLogger.businessLogger.debug("Retrieved Properties File configuration for SRC ID [" + GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE + "] are {"
                    + SMSBridgingDefines.MAX_SERVICE_QUEUE_SIZE_PROP + "=[" + maxSizeForServiceQueues + "] , "
                    + SMSBridgingDefines.MAX_APP_QUEUE_QUEUE_SIZE_PROP + "=[" + maxSizeForAppQueuesQueues + "] , "
                    + SMSBridgingDefines.JSON_CONSTRUCTOR_POOL_SIZE + "=[" + jsonConsructorPoolSize + "] , "
                    + SMSBridgingDefines.DEQUEUE_WAIT_TIME + "=[" + dequeueWaitTime + "] , "
                    + SMSBridgingDefines.DEQUEUE_BATCH_SIZE + "=[" + dequeueBatchSize + "] , "
                    + SMSBridgingDefines.QUEUE_NAMES_SPLITTER + "=[" + queueNamesSplitter + "]}");

            //LOAD APPLICATION QUEUES PART
            prepareArrayBlockingQueuesForApplicationQueues(maxSizeForAppQueuesQueues, queueNamesSplitter, dequeueWaitTime, dequeueBatchSize);
            //LOAD SERVICES PART
            prepareArrayBlockingQueuesForServices(maxSizeForServiceQueues);
        } catch (Exception ex) {
            if (ex instanceof CommonException) {
                throw new CommonException(((CommonException) ex).getErrorMsg(), ErrorCodes.INIT_ERROR);
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.INIT_ERROR);
            }
        }

    }

    public static void startReloadThread() {
        // reloadThread = new ReloadThread();
        reloadThread.setName("ReloadThread");
        reloadThread.start();
    }

    public static void startMonitorThread() {
        monitorThread = new MonitorThread();
        monitorThread.setName("MonitorThread");
        monitorThread.start();
    }

//    public static void startShutdownThread() {
//        shutdownThread = new ShutdownThread();
//        shutdownThread.setName("ShutdownThread");
//        shutdownThread.start();
//    }
    public static void shutdownEngine() throws CommonException {
        try {
            //workingAppQueues   workingServices  jsonConstructorPool  queueForJSONResult  httpSubmitterPool  queueForHTTPResult  archiverPool
            Iterator<QueueNeedsHolder> queuesIterator = workingAppQueues.values().iterator();
            while (queuesIterator.hasNext()) {
                QueueNeedsHolder holder = queuesIterator.next();
                holder.setShutDown(true);
//                synchronized (livenessThread.getExecutors()) {
//                    livenessThread.setExecutors(new ConcurrentHashMap <String,ExecutorService>());
//                    for (Map.Entry<String, QueueNeedsHolder> pair : Manager.workingAppQueues.entrySet()) {
//                        if (!pair.getValue().isShutDown()) {
//                            livenessThread.getExecutors().put(pair.getValue().getModel().getAppName(),pair.getValue().getDequeuerPool());
//                        }
//                    }
//                }
//                CommonLogger.businessLogger.info("Terminating Dequeuer Thread pool for Queue with APP_NAME=[" + holder.getModel().getAppName() + "]..");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Terminating Dequeuer Thread Pool for Queue")
                        .put(GeneralConstants.StructuredLogKeys.APP_NAME, holder.getModel().getAppName()).build());
                holder.getDequeuerPool().shutdown();
                holder.getDequeuerPool().awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
//                CommonLogger.businessLogger.info("Dequeuer Thread pool terminated for Queue with APP_NAME=[" + holder.getModel().getAppName() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Thread Pool Terminated for Queue")
                        .put(GeneralConstants.StructuredLogKeys.APP_NAME, holder.getModel().getAppName()).build());
            }

            CommonLogger.businessLogger.info("Removing Services with empty (java) array blocking queues .. ");
            Iterator<ServiceNeedsHolder> serviceIterator = workingServices.values().iterator();
            while (serviceIterator.hasNext()) {
                ServiceNeedsHolder holder = serviceIterator.next();
                if (holder.getQueueForDequeuerResult().isEmpty()) {
//                    CommonLogger.businessLogger.info("Sevice Name : " + holder.getModel().getServiceName() + "Queue Size: " + holder.getQueueForDequeuerResult().size() + " RemainingAvailableSizeForQueue: " + holder.getQueueForDequeuerResult().remainingCapacity());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Queue Stats")
                            .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, holder.getModel().getServiceName())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, holder.getQueueForDequeuerResult().size())
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, holder.getQueueForDequeuerResult().remainingCapacity()).build());
                    workingServices.remove(holder.getModel().getServiceName());
                } else {
                    holder.setShutDown(true);
                    while (!holder.getQueueForDequeuerResult().isEmpty()) {
                        Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SHUTDOWN_THREAD_SLEEP_TIME)));
                    }
                    workingServices.remove(holder.getModel().getServiceName());
                }
            }
            CommonLogger.businessLogger.info("Services were removed successfully");

            CommonLogger.businessLogger.info("Terminating JSON Constructor Thread Pool .. ");
            jsonConstructorPool.shutdown();
            jsonConstructorPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            CommonLogger.businessLogger.info("JSON Constructor Thread Pool Terminated");

            CommonLogger.businessLogger.info("Terminating HTTP Submitter Thread Pool");
            httpSubmitterPool.shutdown();
            httpSubmitterPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            CommonLogger.businessLogger.info("HTTP Submitter Thread Pool Terminated");

            Iterator<QueueNeedsHolder> queuesIterator2 = workingAppQueues.values().iterator();
            while (queuesIterator2.hasNext()) {
                QueueNeedsHolder holder = queuesIterator2.next();
                holder.setShutDown(true);
//                CommonLogger.businessLogger.info("Terminating Enqueuer Thread pool for Queue with APP_NAME=[" + holder.getModel().getAppName() + "]..");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Terminating Enqueuer Thread Pool for Queue")
                        .put(GeneralConstants.StructuredLogKeys.APP_NAME, holder.getModel().getAppName()).build());
                holder.getEnqueuerPool().shutdown();
                holder.getEnqueuerPool().awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
//                CommonLogger.businessLogger.info("Enqueuer Thread pool terminated for Queue with APP_NAME=[" + holder.getModel().getAppName() + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueuer Thread Pool Terminated for Queue")
                        .put(GeneralConstants.StructuredLogKeys.APP_NAME, holder.getModel().getAppName()).build());
            }

            CommonLogger.businessLogger.info("Terminating Archiver Thread Pool");
            archiverPool.shutdown();
            archiverPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            CommonLogger.businessLogger.info("Archiver Thread Pool Terminated");
            CommonLogger.businessLogger.info("Terminating Reload Thread");
            reloadThread.join();
            CommonLogger.businessLogger.info("Terminating Monitor Thread");
            monitorThread.join();
            CommonLogger.businessLogger.info("Terminating liveness Thread");
            livenessThread.join();

//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())).build());
//            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Dump Ended").build());
            //archiverPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            //CommonLogger.businessLogger.info("Archiver Thread Pool Terminated");
        } catch (InterruptedException e) {

            throw new CommonException(e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        }
    }

    //BEGINING OF HANDLING APP QUEUES
    public static void prepareArrayBlockingQueuesForApplicationQueues(int maxQueueABQSize, String queueNameSplitter, int dequeueWaitTime, int dequeueBatchSize) throws CommonException {
        CommonLogger.businessLogger.debug("Preparing Application Queues Information ..");
        MainService mainService = new MainService();
        // String queues = mainService.getSMSBridgingInstance(instanceID, DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.INSTANCE_APP_QUEUES).toUpperCase();
        //if (queues != null && !queues.trim().isEmpty()) {
        //  String[] queueNames = queues.split(queueNameSplitter);
        //GET MODELS FROM QUEUE NAME
        //ArrayList<QueueModel> queueModels = mainService.getApplicationQueuesBySetOfNames(Arrays.asList(queueNames), Defines.VFE_CS_QUEUES_TYPE_LK.PROCEDURE_QUEUES);

        HashMap<String, QueueModel> queuesMap = mainService.getApplicationQueuesServiceAndStatusApproved(Defines.VFE_CS_QUEUES_TYPE_LK.PROCEDURE_QUEUES);
        ArrayList<QueueModel> queueModels = new ArrayList<QueueModel>();
        Iterator it = queuesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            queueModels.add((QueueModel) pair.getValue());
        }
        if (queueModels != null && queueModels.size() > 0) {
            ArrayList<QueueModel> appQueuesCloned = (ArrayList<QueueModel>) queueModels.clone();
            for (QueueModel model : queueModels) {
                if (workingAppQueues.get(model.getAppName()) == null) {
                    addNewApplicationQueue(model, dequeueWaitTime, dequeueBatchSize);
                }
//                    else {
//                        updateExistingApplicationQueueModel(model,dequeueBatchSize);
//                    }
                appQueuesCloned.remove(model);
            }
            for (QueueModel model : appQueuesCloned) {
                shutdownExistingQueue(model);
            }
        } else {
            //CommonLogger.businessLogger.info("No procedure queues were found for the given queue names");
            CommonLogger.businessLogger.info("No procedure queues were found for the given queue names");
        }
//        } else {
//            //CommonLogger.businessLogger.info("No procedure queue names were found for instance [" + instanceID + "]");
//            CommonLogger.businessLogger.info("No procedure queue names were found for instance [" + instanceID + "]");
//        }
    }

    private static void addNewApplicationQueue(QueueModel model, int dequeueWaitTime, int dequeueBatchSize) throws CommonException {
//        CommonLogger.businessLogger.debug("Found new queue with APP_NAME:" + model.getAppName() + " Preparing connections and Starting creating dequeuer thread pool");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found new Queue. Preparin Connection and Starting Creating Dequeuer Thread Pool")
                .put(GeneralConstants.StructuredLogKeys.APP_NAME, model.getAppName()).build());
        model.setThreshold(dequeueBatchSize);
        QueueNeedsHolder queue = new QueueNeedsHolder();
        queue.setShutDown(false);
        queue.setModel(model);
        // queue.setConnection(new DataSourceManager(model.getDatabaseUrl(), model.getSchemaName(),model.getSchemaPassword()));
        workingAppQueues.put(queue.getModel().getAppName(), queue);
        submitterEnqueuerQueueMap.put(queue.getModel().getAppName(), new ArrayBlockingQueue<ArrayList<SMSBridge>>(Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_SUBMITTER_ENQUEUER_QUEUE_SIZE_PROEPRTY))));
        ExecutorService dequeuersPool = Executors.newFixedThreadPool(model.getDequeuePoolSize());
        for (int i = 0; i < model.getDequeuePoolSize(); i++) {
            DequeuerThread dequeuerThread = new DequeuerThread(queue, i + 1, dequeueWaitTime);
            dequeuersPool.submit(dequeuerThread);
        }
        queue.setDequeuerPool(dequeuersPool);
        ExecutorService enqueuersPool = Executors.newFixedThreadPool(1);
        for (int i = 0; i < model.getDequeuePoolSize(); i++) {
            EnqueuerThread enqueuerThread = new EnqueuerThread(i + 1, queue);
            enqueuersPool.submit(enqueuerThread);
        }
        queue.setEnqueuerPool(enqueuersPool);
        if (livenessThread != null) {
            synchronized (livenessThread.getExecutors()) {
                livenessThread.setExecutors(new ConcurrentHashMap<String, ExecutorService>());
                for (Map.Entry<String, QueueNeedsHolder> pair : Manager.workingAppQueues.entrySet()) {
                    if (!pair.getValue().isShutDown()) {
                        livenessThread.getExecutors().put(pair.getValue().getModel().getQueueName() + "_Dequeuer", pair.getValue().getDequeuerPool());
                        livenessThread.getExecutors().put(pair.getValue().getModel().getQueueName() + "_Enqueuer", pair.getValue().getEnqueuerPool());
                    }
                }
                livenessThread.getExecutors().put("jsonConstructorPool", jsonConstructorPool);
                livenessThread.getExecutors().put("httpSubmitterPool", httpSubmitterPool);
                livenessThread.getExecutors().put("archiverPool", archiverPool);
            }
        }
    }

//    private static void updateExistingApplicationQueueModel(QueueModel model, int dequeueBatchSize) {
//        CommonLogger.businessLogger.debug("Updating queue model for application queue with APP_NAME:" + model.getAppName());
//        model.setThreshold(dequeueBatchSize);
//        workingAppQueues.get(model.getAppName()).setModel(model);
//    }
    private static void shutdownExistingQueue(QueueModel model) {
//        CommonLogger.businessLogger.debug("Shutting down queue with APP_NAME:" + model.getAppName() + " .. queue will stop its dequeing.. queue will be removed when all its curent sms are handled");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "queue will stop its dequeing.. queue will be removed when all its curent sms are handled")
                .put(GeneralConstants.StructuredLogKeys.APP_NAME, model.getAppName()).build());
        workingAppQueues.get(model.getAppName()).setShutDown(true);
        synchronized (livenessThread.getExecutors()) {
            livenessThread.setExecutors(new ConcurrentHashMap<String, ExecutorService>());
            for (Map.Entry<String, QueueNeedsHolder> pair : Manager.workingAppQueues.entrySet()) {
                if (!pair.getValue().isShutDown()) {
                    livenessThread.getExecutors().put(pair.getValue().getModel().getQueueName() + "_Dequeuer", pair.getValue().getDequeuerPool());
                    livenessThread.getExecutors().put(pair.getValue().getModel().getQueueName() + "_Enqueuer", pair.getValue().getEnqueuerPool());
                }
            }
            livenessThread.getExecutors().put("jsonConstructorPool", jsonConstructorPool);
            livenessThread.getExecutors().put("httpSubmitterPool", httpSubmitterPool);
            livenessThread.getExecutors().put("archiverPool", archiverPool);
        }
    }

    public static void prepareArrayBlockingQueuesForServices(int maxServiceABQSize) throws CommonException {
        MainService mainService = new MainService();
        ArrayList<ServiceModel> serviceModels = mainService.getApprovedServices(false);
        ArrayList<ServiceModel> servicesCloned = (ArrayList<ServiceModel>) serviceModels.clone();
        for (ServiceModel model : serviceModels) {
            if (workingServices.get(model.getServiceName()) == null) {
                addNewService(model, maxServiceABQSize);
            }
            servicesCloned.remove(model);
        }
        for (ServiceModel model : servicesCloned) {
            shutdownExistingService(model);
        }
    }
    //END OF HANDLING APP QUEUES

    //BEGINING OF HANDLING SERVICES
    private static void addNewService(ServiceModel model, int maxServiceABQSize) {
//        CommonLogger.businessLogger.debug("Found new service with SERVICE_NAME:" + model.getServiceName());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found New Service")
                .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, model.getServiceName()).build());
        ServiceNeedsHolder service = new ServiceNeedsHolder();
        service.setModel(model);
        service.setQueueForDequeuerResult(new ArrayBlockingQueue(maxServiceABQSize));
        service.setShutDown(false);
        workingServices.put(service.getModel().getServiceName(), service);
    }

    private static void shutdownExistingService(ServiceModel model) {
//        CommonLogger.businessLogger.debug("Shutting down service with SERVICE_NAME:" + model.getServiceName());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shuttingdown Service")
                .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, model.getServiceName()).build());
        //if a service was no longer found and its queue was empty then it will be removed
        if (workingServices.containsKey(model.getServiceName())) {
            if (workingServices.get(model.getServiceName()).getQueueForDequeuerResult().isEmpty()) {
                workingServices.remove(model.getServiceName());
            } else {
                workingServices.get(model.getServiceName()).setShutDown(true);
            }
        }
    }
    //END OF HANDLING SERVICES

    //BEGINNING OF HANDLING JSON CONSTRUCTOR THREADS
    public static void prepareJSONConstructors(int jsonConsructorPoolSize, int jsonConsructorQueueSize) {
        CommonLogger.businessLogger.debug("Creating json constructors ..Creating json constructor queue and Starting creating json constructors threads");
        queueForJSONRequests = new ArrayBlockingQueue<>(jsonConsructorQueueSize);
        jsonConstructorPool = Executors.newFixedThreadPool(jsonConsructorPoolSize);
        for (int i = 0; i < jsonConsructorPoolSize; i++) {
            JSONConstructorThread jsonConstructorThread = new JSONConstructorThread(i + 1);
            jsonConstructorPool.submit(jsonConstructorThread);
        }
    }
    //END OF HANDLING JSON CONSTRUCTOR THREADS

    //BEGINNING OF HANDLING HTTP THREADS
    private static void prepareHTTPBulkSenders(int httpPoolSize, int maxHTTPHitTimes, int maxHTTPResultQueueSize) {
        CommonLogger.businessLogger.debug("Creating http senders .. Creating http result queue and Starting creating https threads");
        queueForHTTPResult = new ArrayBlockingQueue<>(maxHTTPResultQueueSize);
        httpSubmitterPool = Executors.newFixedThreadPool(httpPoolSize);
        for (int i = 0; i < httpPoolSize; i++) {
            HTTPSubmitterThread httpThread = new HTTPSubmitterThread(i + 1, maxHTTPHitTimes);
            httpSubmitterPool.submit(httpThread);
        }

    }
    //END OF HANDLING HTTP THREADS

    //BEGINNING OF HANDLING ARCHIVER THREADS
    private static void prepareArchivers(int maxPoolSize) {
        CommonLogger.businessLogger.debug("Creating archiver Threads .. Starting creating archiver threads pool");
        archiverPool = Executors.newFixedThreadPool(maxPoolSize);
        for (int i = 0; i < maxPoolSize; i++) {
            ArchiverThread thread = new ArchiverThread(i + 1);
            archiverPool.submit(thread);
        }

    }
    //END OF HANDLING ARCHIVER THREADS

    private static void checkDataBaseConfigsAvailability() throws CommonException {
        if (SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SHUTDOWN_THREAD_SLEEP_TIME) == null) {
            throw new CommonException("Error SMS_BRIDGE_SHUTDOWN_THREAD_SLEEP_TIME data base configuration was not found", ErrorCodes.INIT_ERROR);
        }

        if (SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_RELOAD_CONFIGURATION_TIME) == null) {
            throw new CommonException("Error SMS_BRIDGE_RELOAD_CONFIGURATION_TIME data base configuration was not found", ErrorCodes.INIT_ERROR);
        }

        if (SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_URL) == null) {
            throw new CommonException("Error SMS_BRIDGE_HTTP_URL data base configuration was not found", ErrorCodes.INIT_ERROR);
        }

        if (SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_CONN_TIME_OUT) == null) {
            throw new CommonException("Error SMS_BRIDGE_HTTP_CONN_TIME_OUT data base configuration was not found", ErrorCodes.INIT_ERROR);
        } else {
            try {
                if (Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_CONN_TIME_OUT)) == 0) {
                    throw new CommonException("Error HTTP_BATCH_SIZE data base configuration was not found", ErrorCodes.INIT_ERROR);
                }
            } catch (Exception e) {
                throw new CommonException("Error SMS_BRIDGE_HTTP_CONN_TIME_OUT data base configuration has an invalid value", ErrorCodes.INIT_ERROR);
            }
        }

        if (SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_READ_TIME_OUT) == null) {
            throw new CommonException("Error SMS_BRIDGE_HTTP_READ_TIME_OUT data base configuration was not found", ErrorCodes.INIT_ERROR);
        } else {
            try {
                if (Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_READ_TIME_OUT)) == 0) {
                    throw new CommonException("Error HTTP_BATCH_SIZE data base configuration was not found", ErrorCodes.INIT_ERROR);
                }
            } catch (Exception e) {
                throw new CommonException("Error SMS_BRIDGE_HTTP_READ_TIME_OUT data base configuration has an invalid value", ErrorCodes.INIT_ERROR);
            }
        }

        if (SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.DEQUEUE_BATCH_SIZE) == null) {
            throw new CommonException("Error DEQUEUE_BATCH_SIZE data base configuration was not found", ErrorCodes.INIT_ERROR);
        } else {
            try {
                if (Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.DEQUEUE_BATCH_SIZE)) == 0) {
                    throw new CommonException("Error HTTP_BATCH_SIZE data base configuration was not found", ErrorCodes.INIT_ERROR);
                }
            } catch (Exception e) {
                throw new CommonException("Error DEQUEUE_BATCH_SIZE data base configuration has an invalid value", ErrorCodes.INIT_ERROR);
            }
        }

        if (SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.HTTP_BATCH_SIZE) == null) {
            throw new CommonException("Error HTTP_BATCH_SIZE data base configuration was not found", ErrorCodes.INIT_ERROR);
        } else {
            try {
                if (Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.HTTP_BATCH_SIZE)) == 0) {
                    throw new CommonException("Error HTTP_BATCH_SIZE data base configuration has an invalid value", ErrorCodes.INIT_ERROR);
                }
            } catch (Exception e) {
                throw new CommonException("Error HTTP_BATCH_SIZE data base configuration has an invalid value", ErrorCodes.INIT_ERROR);
            }
        }
    }

    public static void startShutdownHookThread() {
        ShutdownHookThread shutdownHookThread = new ShutdownHookThread();
        shutdownHookThread.attachShutDownHook();
    }

    public static void initLivenessThread() {
        CommonLogger.businessLogger.info("Manager initLivenessThread() Invoked...");
        //   initializeSystemProperties();
        ConcurrentHashMap<String, ExecutorService> executorServices = new ConcurrentHashMap<String, ExecutorService>();
        for (Map.Entry<String, QueueNeedsHolder> pair : Manager.workingAppQueues.entrySet()) {
            if (!pair.getValue().isShutDown()) {
                //executorServices.put(pair.getValue().getModel().getAppName(),pair.getValue().getDequeuerPool());
                executorServices.put(pair.getValue().getModel().getQueueName() + "_Dequeuer", pair.getValue().getDequeuerPool());
                executorServices.put(pair.getValue().getModel().getQueueName() + "_Enqueuer", pair.getValue().getEnqueuerPool());
            }
        }
        executorServices.put("jsonConstructorPool", jsonConstructorPool);
        executorServices.put("httpSubmitterPool", httpSubmitterPool);
        executorServices.put("archiverPool", archiverPool);
        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<String, Thread>();
        threads.put("monitorThread", monitorThread);
        threads.put("reloadThread", reloadThread);
        livenessThread = new LivenessThread(SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG, null, executorServices, threads, CommonLogger.livenessLogger, CommonLogger.businessLogger, Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(Defines.LIVENESS_THREAD_SLEEP_TIME_PROPERTY_NAME)));
        livenessThread.start();
        CommonLogger.businessLogger.info("Manager initLivenessThread() Ended...");
    }
}
