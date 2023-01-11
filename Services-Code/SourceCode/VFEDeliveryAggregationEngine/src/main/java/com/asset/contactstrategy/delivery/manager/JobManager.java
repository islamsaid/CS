/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.manager;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.executor.RollingBatchExecutor;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.BackPressurePolicy;
import com.asset.contactstrategy.common.utils.CustomThreadFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.delivery.executor.DeliveryBatchUpdater;
import com.asset.contactstrategy.delivery.executor.NotDeliveredBatchUpdater;
import com.asset.contactstrategy.delivery.executor.PartialDeliveryBatchUpdater;
import com.asset.contactstrategy.delivery.executor.SentToSmscBatchUpdater;
import com.asset.contactstrategy.delivery.thread.AssignUpdateJobsThread;
import com.asset.contactstrategy.delivery.services.DeliveryAggregationServices;
import com.asset.contactstrategy.delivery.thread.AssignSystemPropertiesThread;
import com.asset.contactstrategy.delivery.thread.AssignTimeOutJobsThread;
import com.asset.contactstrategy.delivery.thread.MonitorThread;
import com.asset.contactstrategy.interfaces.threads.LivenessThread;
import static java.lang.Thread.currentThread;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author esmail.anbar
 */
public class JobManager {

    private static JobManager instance = null;
    private static Thread assignUpdateJobsThread = null;
    private static Thread assignTimeOutJobsThread = null;
    private static Thread assignSystemPropertiesThread = null;
    private static Thread monitoringThread = null;
//    private ArrayBlockingQueue<Runnable> jobQueue = null;
//    private ArrayBlockingQueue<JobModel> jobTimeOutQueue = null;
//    public static ExecutorService updateThreadsPool;
    public static ThreadPoolExecutor timeOutPool;
    private Map<String, String> systemProperties = null;
    public static boolean updateJobsInitialized = false;
    public static boolean timeOutJobsInitialized = false;
    public static AtomicBoolean ENGINE_SHUTDOWN_FLAG = new AtomicBoolean(false);
    private static Thread livenessThread = null;
    public static ThreadPoolExecutor selectorPool;
    public static ThreadPoolExecutor deciderPool;
    public static ThreadPoolExecutor updaterPool;
    public static DeliveryBatchUpdater deliveryBatchUpdater;
    public static PartialDeliveryBatchUpdater partialDeliveryBatchUpdater;
    public static NotDeliveredBatchUpdater notDeliveredBatchUpdater;
    public static SentToSmscBatchUpdater sentToSmscBatchUpdater;

    public static JobManager getInstance() {
        //CommonLogger.businessLogger.info("Geting JobManager Instance...");
        if (instance == null) {
            instance = new JobManager();
        }

        return (instance);
    }

    public void init() throws CommonException {
        //CommonLogger.businessLogger.info("Initializing");
        try {

//            int queueSize = Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_QUEUE_SIZE_VALUE;
//            this.setSelectorPoolQueue(new ArrayBlockingQueue<Runnable>(queueSize));
//            this.setJobTimeOutQueue(new ArrayBlockingQueue<JobModel>(queueSize));
            updaterPool = new ThreadPoolExecutor(
                    Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_SIZE_VALUE, Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_SIZE_VALUE,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_QUEUE_SIZE_VALUE),
                    new CustomThreadFactory("updater-pool"), new BackPressurePolicy());

            sentToSmscBatchUpdater = new SentToSmscBatchUpdater(Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_BATCH_SIZE_VALUE,
                    RollingBatchExecutor.ConsumePolicy.Eager, updaterPool);
            notDeliveredBatchUpdater = new NotDeliveredBatchUpdater(Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_BATCH_SIZE_VALUE,
                    RollingBatchExecutor.ConsumePolicy.Eager, updaterPool);
            partialDeliveryBatchUpdater = new PartialDeliveryBatchUpdater(Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_BATCH_SIZE_VALUE,
                    RollingBatchExecutor.ConsumePolicy.Eager, updaterPool);
            deliveryBatchUpdater = new DeliveryBatchUpdater(Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_BATCH_SIZE_VALUE,
                    RollingBatchExecutor.ConsumePolicy.Eager, updaterPool);

            deciderPool = new ThreadPoolExecutor(
                    Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_SIZE_VALUE, Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_SIZE_VALUE,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_QUEUE_SIZE_VALUE),
                    new CustomThreadFactory("decieder-pool"), new BackPressurePolicy());

            selectorPool = new ThreadPoolExecutor(
                    Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_SIZE_VALUE, Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_SIZE_VALUE,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_QUEUE_SIZE_VALUE),
                    new CustomThreadFactory("selector-pool"), new BackPressurePolicy());

            timeOutPool = new ThreadPoolExecutor(
                    Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_SIZE_VALUE, Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_SIZE_VALUE,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_QUEUE_SIZE_VALUE),
                    new CustomThreadFactory("timeout-pool"), new BackPressurePolicy());

            initAssignSystemPropertiesThread();

            initAssignUpdateJobsThread();

            initAssignTimeOutJobsThread();

//            while (true) {
////                if (!selectorPoolQueue.isEmpty() && !jobTimeOutQueue.isEmpty()) {
//                if (!jobTimeOutQueue.isEmpty()) {
////                    CommonLogger.businessLogger.info("UpdateJobQueue Size: " + jobQueue.size());
////                    CommonLogger.businessLogger.info("TimeOutQueue Size: " + jobTimeOutQueue.size());
////                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Job Queue stats")
////                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, selectorPoolQueue.size()).build());
//                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Time Out Queue stats")
//                            .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, jobTimeOutQueue.size()).build());
//                    break;
//                }
//            }
//
//            CommonLogger.businessLogger.info("Jobs Ready for Working Threads...");
//            initTimeOutStatusThread();
//            initUpdateStatusThreadBatch();
            initMonitoringThread();
            initLivenessThread();
            startShutdownHookThread();
        } catch (CommonException ce) {
            String businessMessage = "Error While Initializing Delivery Aggregation Engine";
            CommonLogger.businessLogger.error(businessMessage + " Reason: " + ce.getErrorMsg());
            CommonLogger.errorLogger.error(businessMessage + " Reason: " + ce.getErrorMsg(), ce);
            throw ce;
        } catch (Exception e) {
            String businessMessage = "Error While Initializing Delivery Aggregation Engine";
            CommonLogger.businessLogger.error(businessMessage + " Reason: " + e + " || " + e.getMessage());
            CommonLogger.errorLogger.error(businessMessage + " Reason: " + e + " || " + e.getMessage(), e);
            throw new CommonException(businessMessage + " Reason: " + e + " || " + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        //CommonLogger.businessLogger.info("JobManager.init() Ended...");
    }

    /*    public void initUpdateStatusThreadBatch() {
        CommonLogger.businessLogger.debug("JobManager.initUpdateStatusThreadBatch() Invoked...");
        int poolSize;

        poolSize = Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_SIZE_VALUE;

        updateThreadsPool = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Runnable process = new UpdateStatusThread(i);
            updateThreadsPool.execute(process);
        }
        CommonLogger.businessLogger.debug("JobManager.initUpdateStatusThreadBatch() Ended...");
    }
     */
    public void initAssignUpdateJobsThread() {
        CommonLogger.businessLogger.debug("JobManager.initAssignUpdateJobsThread() Invoked...");
        assignUpdateJobsThread = new Thread(new AssignUpdateJobsThread());
        assignUpdateJobsThread.start();
        CommonLogger.businessLogger.debug("JobManager.initAssignUpdateJobsThread() Ended...");
    }

    public void initAssignTimeOutJobsThread() {
        CommonLogger.businessLogger.debug("JobManager.initAssignTimeOutJobsThread() Invoked...");
        assignTimeOutJobsThread = new Thread(new AssignTimeOutJobsThread());
        assignTimeOutJobsThread.start();
        CommonLogger.businessLogger.debug("JobManager.initAssignTimeOutJobsThread() Ended...");
    }

    public void initAssignSystemPropertiesThread() throws CommonException {
        CommonLogger.businessLogger.debug("JobManager.initAssignSystemPropertiesThread() Invoked...");
        assignSystemPropertiesThread = new Thread(new AssignSystemPropertiesThread());
        assignSystemPropertiesThread.start();
        CommonLogger.businessLogger.debug("JobManager.initAssignSystemPropertiesThread() Ended...");
    }

    public void initMonitoringThread() {
        CommonLogger.businessLogger.debug("JobManager.initMonitoringThread() Invoked...");
        //   initializeSystemProperties();
        monitoringThread = new Thread(new MonitorThread());
        monitoringThread.start();
        CommonLogger.businessLogger.debug("JobManager.initMonitoringThread() Ended...");
    }

    public void initLivenessThread() {
        CommonLogger.businessLogger.debug("JobManager.initLivenessThread() Invoked...");
        //   initializeSystemProperties();
        ConcurrentHashMap<String, ExecutorService> executorServices = new ConcurrentHashMap<String, ExecutorService>();
//        executorServices.put("updateThreadsPool", this.updateThreadsPool);
        executorServices.put("selectorPool", this.selectorPool);
        executorServices.put("deciderPool", this.deciderPool);
        executorServices.put("updaterPool", this.updaterPool);
        executorServices.put("timeOutThreadsPool", this.timeOutPool);

        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<String, Thread>();
        threads.put("monitoringThread", this.monitoringThread);
        threads.put("assignSystemPropertiesThread", this.assignSystemPropertiesThread);
        threads.put("assignTimeOutJobsThread", this.assignTimeOutJobsThread);
        threads.put("assignUpdateJobsThread", this.assignUpdateJobsThread);

        livenessThread = new Thread(new LivenessThread(JobManager.ENGINE_SHUTDOWN_FLAG, null, executorServices, threads, CommonLogger.livenessLogger, CommonLogger.businessLogger, Integer.parseInt(JobManager.getInstance().getSystemProperties().get(Defines.LIVENESS_THREAD_SLEEP_TIME_PROPERTY_NAME))));
        livenessThread.start();
        CommonLogger.businessLogger.debug("JobManager.initLivenessThread() Ended...");
    }

    public static void initializeSystemProperties() throws CommonException {
        CommonLogger.businessLogger.debug("JobManager.initializeSystemProperties() Invoked...");
        DeliveryAggregationServices deliveryAggregationServices = new DeliveryAggregationServices();
        try {
            deliveryAggregationServices.assignSystemProperties();
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception e) {
//            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
//            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
            throw new CommonException(e + " || " + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        CommonLogger.businessLogger.debug("JobManager.initializeSystemProperties() Ended...");
    }

//    public void initTimeOutStatusThread() {
//        CommonLogger.businessLogger.debug("JobManager.initTimeOutStatusThread() Invoked...");
//        int poolSize;
//
//        poolSize = Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_SIZE_VALUE;
//
//        timeOutPool = Executors.newFixedThreadPool(poolSize);
//        for (int i = 0; i < poolSize; i++) {
//            Runnable process = new TimeOutStatusThread(i);
//            timeOutPool.execute(process);
//        }
//        CommonLogger.businessLogger.debug("JobManager.initTimeOutStatusThread() Ended...");
//    }
    public void shutdown() throws CommonException {
        CommonLogger.businessLogger.info("JobManager.shutdown() Invoked...");

        try {
            assignSystemPropertiesThread.interrupt();
            assignSystemPropertiesThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info("assignSystemPropertiesThread Terminated");
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error While Attempting to Shutdown AssignSystemProperties Thread || " + e + " || " + e.getMessage());
        }

        try {
            assignTimeOutJobsThread.interrupt();
            assignTimeOutJobsThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info("assignTimeOutJobsThread Terminated");
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error While Attempting to Shutdown AssignTimeoutJobs Thread || " + e + " || " + e.getMessage());
        }

        try {
            assignUpdateJobsThread.interrupt();
            assignUpdateJobsThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info("assignUpdateJobsThread Terminated");
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error While Attempting to Shutdown AssigUpdateJobs Thread || " + e + " || " + e.getMessage());
        }

        selectorPool.shutdown();
        try {
            selectorPool.awaitTermination((Long.MAX_VALUE), TimeUnit.MILLISECONDS);
            CommonLogger.businessLogger.info("selectorPool Terminated");
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error While Attempting to Shutdown selector Pool || " + e + " || " + e.getMessage());
        }

        deciderPool.shutdown();
        try {
            deciderPool.awaitTermination((Long.MAX_VALUE), TimeUnit.MILLISECONDS);
            CommonLogger.businessLogger.info("deciderPool Terminated");
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error While Attempting to Shutdown decider Pool || " + e + " || " + e.getMessage());
        }

        updaterPool.shutdown();
        try {
            updaterPool.awaitTermination((Long.MAX_VALUE), TimeUnit.MILLISECONDS);
            CommonLogger.businessLogger.info("updaterPool Terminated");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error While Attempting to Shutdown Updaters Pool", e);
        }

        timeOutPool.shutdown();
        try {
            timeOutPool.awaitTermination((Long.MAX_VALUE), TimeUnit.MILLISECONDS);
            CommonLogger.businessLogger.info("timeOutThreadsPool Terminated");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error While Attempting to Shutdown Timeout Threads Pool", e);
        }

//        CommonLogger.businessLogger.info("Update Status Java Queue Size: " + JobManager.getInstance().getJobQueue().size() + " || Queue Remaining Capacity: " + JobManager.getInstance().getJobQueue().remainingCapacity());
//        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updataed Java Queue Status")
//                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, JobManager.getInstance().getSelectorPoolQueue().size())
//                .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, JobManager.getInstance().getSelectorPoolQueue().remainingCapacity()).build());
//        CommonLogger.businessLogger.info("Timeout Status Java Queue Size: " + JobManager.getInstance().getJobTimeOutQueue().size() + " || Queue Remaining Capacity: " + JobManager.getInstance().getJobTimeOutQueue().remainingCapacity());
//        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Timeout Java Queue Status")
//                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, JobManager.getInstance().getJobTimeOutQueue().size())
//                .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, JobManager.getInstance().getJobTimeOutQueue().remainingCapacity()).build());
        try {
            monitoringThread.interrupt();
            monitoringThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info("monitoringThread Terminated");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error While Attempting to Shutdown Monitor Thread", e);
        }

        try {
            livenessThread.interrupt();
            livenessThread.join();
            CommonLogger.businessLogger.info("livenessThread Terminated");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error While Attempting to Shutdown Liveness Thread", e);
        }

        DataSourceManger.closeConnectionPool();
        CommonLogger.businessLogger.info("DataSourceManager Closed Successfully");

//        CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//        CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//        CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())).build());
//        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Ended").build());
//        try
//        {
//            shutdownUpdateThreadsPool();
//            shutdowntimeOutThreadsPool();
//        }
//        catch (Exception e) 
//        {
//            HandleExceptionService handleExceptionService = new HandleExceptionService(e);
//            throw handleExceptionService.handleException();
//        } 
        CommonLogger.businessLogger.info("JobManager.shutdown() Ended...");
    }

//    public void shutdownUpdateThreadsPool() throws CommonException
//    {
//        CommonLogger.businessLogger.info("JobManager.shutdownUpdateThreadsPool() Invoked...");
//        try
//        {
//            
//            updateThreadsPool.shutdown();
//            updateThreadsPool.awaitTermination(Integer.valueOf(Initializer.propertiesFileBundle.getString
//               (Defines.DELIVERY_AGGREGATION_PROPERTIES.HOURS_BEFORE_TERMINATION_ON_SHTUDOWN)), TimeUnit.HOURS);
//        }
//        catch (Exception e) 
//        {
//            HandleExceptionService handleExceptionService = new HandleExceptionService(e);
//            throw handleExceptionService.handleException();
//        } 
//        CommonLogger.businessLogger.info("JobManager.shutdownUpdateThreadsPool() Ended...");
//    }
//    
//    public void shutdowntimeOutThreadsPool() throws CommonException
//    {
//        CommonLogger.businessLogger.info("JobManager.shutdowntimeOutThreadsPool() Invoked...");
//        try
//        {
//            timeOutPool.shutdown();
//            timeOutPool.awaitTermination(Integer.valueOf(Initializer.propertiesFileBundle.getString
//                (Defines.DELIVERY_AGGREGATION_PROPERTIES.HOURS_BEFORE_TERMINATION_ON_SHTUDOWN)), TimeUnit.HOURS);
//        }
//        catch (Exception e) 
//        {
//            HandleExceptionService handleExceptionService = new HandleExceptionService(e);
//            throw handleExceptionService.handleException();
//        } 
//        CommonLogger.businessLogger.info("JobManager.shutdowntimeOutThreadsPool() Ended...");
//    }
//    public ArrayBlockingQueue<JobModel> getJobTimeOutQueue() {
//        return jobTimeOutQueue;
//    }
//
//    public void setJobTimeOutQueue(ArrayBlockingQueue<JobModel> jobTimeOutQueue) {
//        this.jobTimeOutQueue = jobTimeOutQueue;
//    }
    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public void setSystemProperties(Map<String, String> systemProperties) {
        this.systemProperties = systemProperties;
    }

    public ThreadPoolExecutor getSelectorPool() {
        return selectorPool;
    }

    public ThreadPoolExecutor getDeciderPool() {
        return deciderPool;
    }

    public ThreadPoolExecutor getUpdaterPool() {
        return updaterPool;
    }

    public static void main(String[] args) {
        try {
            Defines.runningProjectId = GeneralConstants.SRC_ID_DELIVERY_AGGREGATION;
            Initializer.readPropertiesFile();
            Initializer.initializeLoggers();
            Initializer.initializeDataSource();

            initializeSystemProperties();

            if (Defines.STRING_TRUE.equalsIgnoreCase(JobManager.getInstance().getSystemProperties()
                    .get(Defines.DELIVERY_AGGREGATION_PROPERTIES.SHUTDOWN_FLAG))) {
                CommonLogger.businessLogger.info("Delivery Aggregation Engine Has Shutdown Flag On...");
                CommonLogger.businessLogger.info("Closing Engine...");
                System.exit(0);
            }

            CommonLogger.businessLogger.info("Delivery Aggregation Engine Initializing...");

            CommonLogger.livenessLogger.info("Engine is ready");

            JobManager.getInstance().init();

            CommonLogger.businessLogger.info("Delivery Aggregation Engine Initialized Successfully...");
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error While Starting Delivery Aggregation Engine || " + e.getMessage());
            CommonLogger.errorLogger.error("Error While Starting Delivery Aggregation Engine || " + e.getMessage(), e);
            System.exit(0);
        }

        try {
            while (true) {
                if (JobManager.ENGINE_SHUTDOWN_FLAG.get()) {
                    break;
                } else {
                    Thread.sleep(10000);
                }
            }

            // JobManager.getInstance().shutdown();
            CommonLogger.businessLogger.info("Delivery Aggregation Engine is shutting down");
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error While Shutting Down Delivery Aggregation Engine || " + e + " || " + e.getMessage());
        }
    }

    public void startShutdownHookThread() {
//        ShutdownHookThread shutdownHookThread = new ShutdownHookThread();
//        shutdownHookThread.attachShutDownHook();

        //Register Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                CommonLogger.businessLogger.info("Starting Thread " + currentThread().getName());
                try {
                    CommonLogger.businessLogger.info("Shutdown Thread started");
                    ENGINE_SHUTDOWN_FLAG.set(true);
                    shutdown();
//                    CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("/////////////////////DELIVERY ENGINE IS OFF/////////////////////");
                    CommonLogger.businessLogger.info("DELIVERY ENGINE IS OFF");
//                    CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//////////////////////////////////////////////////////////////////");
                } catch (Exception e) {
                    CommonLogger.businessLogger.error("Shutdown  Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e);
                    CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + e, e);
                } catch (Throwable e) {
                    CommonLogger.businessLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Throwable--->" + e);
                    CommonLogger.errorLogger.error("Shutdown Thread  " + Thread.currentThread().getName() + "  caught Throwable--->" + e, e);
                }
            }
        });
    }
}
