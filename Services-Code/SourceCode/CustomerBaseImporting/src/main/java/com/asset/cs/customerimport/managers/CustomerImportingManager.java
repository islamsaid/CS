/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.managers;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.LineModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.interfaces.threads.LivenessThread;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.workers.CustomerBaseMontiringWorker;
import com.asset.cs.customerimport.workers.FilesWorker;
import com.asset.cs.customerimport.workers.MonitorThread;
import com.asset.cs.customerimport.workers.PreparingStatmentWorker;
import com.asset.cs.customerimport.workers.ReaderWorker;
import com.asset.cs.customerimport.workers.ShutdownHookThread;
import com.asset.cs.customerimport.workers.WriterWorker;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Zain Al-Abedin
 */
public class CustomerImportingManager {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.managers.CustomerImportingManager";

    public static String SQLStatment = "";
    public static ExecutorService preparingWorkersPool;
    public static ExecutorService readerWorkersPool;
    public static HashMap<Integer, ExecutorService> writersWorkersMap = new HashMap<Integer, ExecutorService>();

    public static BlockingQueue<LineModel> readersDataQueue;
    public static HashMap<Integer, BlockingQueue> writersDataQueuesMap = new HashMap<Integer, BlockingQueue>();

    private static boolean catchingError;
    private static boolean preparingWorkersShutdownFlag;
    private static boolean writerWorkersShutdownFlag;

    private static MonitorThread systemMonitor;
    private static FilesWorker filesWorker = null;
    private static CustomerBaseMontiringWorker customerBaseMontiringWorker = null;

    public static int no_Writer_ThreadPools;
    private static int noReaderThreads;
    public static int no_of_subPartitions_per_threadPool;

    public static int readerQueueSize;
    public static int writerQueueSize;

    private static MainService mainServ = new MainService();
    private static LivenessThread livenessThread = null;

    public static void startExtractorManager() throws CommonException {
        String methodName = "startExtractorManager";
        try {
            // Log
            CommonLogger.businessLogger.info("Initializing Extractor Manager");

            // Reader thread
            filesWorker = new FilesWorker();
            filesWorker.setName("FilesWorker");
            filesWorker.setWorkerShutDownFlag(false);
            filesWorker.start();

            systemMonitor = new MonitorThread();
            getSystemMonitor().setName("System Monitor Thread");
            getSystemMonitor().start();

            customerBaseMontiringWorker = new CustomerBaseMontiringWorker();
            customerBaseMontiringWorker.setName("customerBaseMontiringWorker");
            customerBaseMontiringWorker.start();
            initLivenessThread();
            startShutdownHookThread();
            // Log
            CommonLogger.businessLogger.info("Extractor Manager Initialized Successfully");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error-->", e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            throw processException(e, methodName);
        }
    }

    private static CommonException processException(Exception e, String methodName) {
        if (e instanceof CommonException) {
            return (CommonException) e;
        } // Handle SQL exception
        else if (e instanceof SQLException) {
            return new CommonException("SQL error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } else {
            return new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
    }

    public static void startReaderWorker(int processID, File dwFile) throws CommonException {
        String methodName = "startReaderWorker";
        try {
            ReaderWorker readerWorker = new ReaderWorker(dwFile);
            readerWorker.setName("ReaderWorker_" + processID + "_" + noReaderThreads);
            readerWorkersPool.execute(readerWorker);
            // readerWorker.start();
            CommonLogger.businessLogger.info("Reader Worker started Successfully");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error in startReaderWorker -->", e);
            CommonLogger.businessLogger.error("Fatal Error in startReaderWorker -->" + e);
            throw processException(e, methodName);
        }
    }

    public static void startPreparingWritingWorkers() throws CommonException {
        CommonLogger.businessLogger.info("StartPreparingWritingWorkers  Started");
        String insertStatment = "INSERT /*+APPEND */ INTO " + DBStruct.DWH_CUSTOMERS.TBL_NAME + " ( ";
        String valueStatment = "VALUES( ";
        for (DWHElementModel dWHElementsModel : ResourcesCachingManager.getDwhElementsList()) {
            insertStatment += dWHElementsModel.getDwhName() + " , ";
            valueStatment += "? , ";
//            CommonLogger.businessLogger.info("DWH Element[" + dWHElementsModel.toString() + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Preparing Writing Workers")
                    .put(GeneralConstants.StructuredLogKeys.DWH_ELEMENT, dWHElementsModel).build());
        }
        insertStatment += DBStruct.DWH_CUSTOMERS.LAST_MSISDN_TWO_DIGITS + " , " + DBStruct.DWH_CUSTOMERS.RUN_ID + ")";
        valueStatment += "? , ? )";
        SQLStatment = insertStatment + valueStatment;

        noReaderThreads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_READER_WORKER_THREADS);
        readerWorkersPool = Executors.newFixedThreadPool(noReaderThreads);
//        CommonLogger.businessLogger.info("No Of Reader  Threads are: " + noReaderThreads);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No Of Reader  Threads")
                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, noReaderThreads).build());
        readerQueueSize = ResourcesCachingManager.getIntValue(EngineDefines.READER_THRESHOLD_MAX_SIZE);
        readersDataQueue = new ArrayBlockingQueue<LineModel>(readerQueueSize);

        int no_of_preparing_threads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_PREPARING_WORKER_THREADS);
//        CommonLogger.businessLogger.info("No Of Preparing Statments Threads are: " + no_of_preparing_threads);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Preparing Statments Threads Stats")
                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, no_of_preparing_threads).build());

        setPreparingWorkersShutdownFlag(false);
        preparingWorkersPool = Executors.newFixedThreadPool(no_of_preparing_threads);
        int prepare_thread_no;
        for (int i = 0; i < no_of_preparing_threads; i++) {
            prepare_thread_no = i + 1;
            PreparingStatmentWorker worker = new PreparingStatmentWorker();
            worker.setName("PreparingStatmentWorker_" + prepare_thread_no + "_" + no_of_preparing_threads);
            preparingWorkersPool.execute(worker);
            //  worker.start();
        }

        setWriterWorkersShutdownFlag(false);
        no_of_subPartitions_per_threadPool = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_SUB_PARTITIONS_PER_THREADPOOL);
        no_Writer_ThreadPools = 100 / no_of_subPartitions_per_threadPool;
        int no_of_writer_workers_per_threadpool = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_WRITER_WORKERS_PER_THREAD);
//        CommonLogger.businessLogger.info("No Of Writing ThreadPools is: " + no_Writer_ThreadPools);
//        CommonLogger.businessLogger.info("No Of Writing Workers Per ThreadPool is: " + no_of_writer_workers_per_threadpool);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Writing Thread Pools and Workers Stats")
                .put(GeneralConstants.StructuredLogKeys.THREAD_POOLS_COUNT, no_Writer_ThreadPools)
                .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, no_of_writer_workers_per_threadpool).build());

        int runId = Integer.valueOf(mainServ.getSystemPropertyByKey(EngineDefines.KEY_RUNNING_DWH_PROFILE_PARTITION, GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE));
        writerQueueSize = ResourcesCachingManager.getIntValue(EngineDefines.WRITER_DATA_QUEUE_THRESHOLD_MAX_SIZE);

        int writer_thread_no;
        int writer_threadPool_no;
        for (int j = 0; j < no_Writer_ThreadPools; j++) {
            writer_threadPool_no = j + 1;
            BlockingQueue<LineModel> queue = new ArrayBlockingQueue<LineModel>(writerQueueSize);
            writersDataQueuesMap.put(j, queue);

            ExecutorService writersExecutor = Executors.newFixedThreadPool(no_of_writer_workers_per_threadpool);
            writer_thread_no = 0;
            for (int i = 0; i < no_of_writer_workers_per_threadpool; i++) {
                writer_thread_no = i + 1;
                WriterWorker writerWorker = new WriterWorker(queue, runId);
                writerWorker.setName("WritersPool_" + writer_threadPool_no + "_Thread_" + writer_thread_no);
                writersExecutor.execute(writerWorker);
                // writerWorker.start();
            }
            writersWorkersMap.put(j, writersExecutor);
        }

        CommonLogger.businessLogger.info("Finished Preparing Writing Workers");

    }

    public static void shutdownReaderWorkers() throws InterruptedException, CommonException {
        CommonLogger.businessLogger.info("Starting ShutDown Reader Workers ThreadPool ");
        readerWorkersPool.shutdown();
        long executionTime = System.currentTimeMillis();
        CommonLogger.businessLogger.info("Sleep while waiting Reader Workers to Finish ");
        while (!readerWorkersPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
        }

//        CommonLogger.businessLogger.info("Successfully ShutDown Reader Workers ThreadPool in  [" + (System.currentTimeMillis() - executionTime) + "] msec");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Shutdown Reader Workers ThreadPool")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - executionTime)).build());
    }

    public static void shutdownWriterWorkers() throws InterruptedException, CommonException {
        CommonLogger.businessLogger.info("Starting ShutDown Writer Workers ThreadPools ");
        setWriterWorkersShutdownFlag(true);

        for (ExecutorService ececutor : writersWorkersMap.values()) {
            ececutor.shutdown();
        }
        long executionTime = System.currentTimeMillis();
        CommonLogger.businessLogger.info("Sleep  while waiting Writer Workers to Finish ");
        for (ExecutorService executor : writersWorkersMap.values()) {
            while (!executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
            }
        }
//        CommonLogger.businessLogger.info("Successfully ShutDown Writer Workers ThreadPools in  [" + (System.currentTimeMillis() - executionTime) + "] msec");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Shutdown Writer Workers ThreadPools")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - executionTime)).build());
    }

    public static void shutdownPreparingWorkers() throws InterruptedException, CommonException {
        CommonLogger.businessLogger.info("Starting ShutDown Preparing Workers ThreadPool ");
        setPreparingWorkersShutdownFlag(true);
        preparingWorkersPool.shutdown();
        long executionTime = System.currentTimeMillis();
        CommonLogger.businessLogger.info("Sleep while waiting Preparing Workers to Finish ");
        while (!preparingWorkersPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
        }
//        CommonLogger.businessLogger.info("Successfully ShutDown Preparing Workers ThreadPool [" + (System.currentTimeMillis() - executionTime) + "] msec");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Shutdown Preparing Workers ThreadPool")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - executionTime)).build());
    }

    public static void shutdownCustomerImportingManager() throws InterruptedException, CommonException {
        CommonLogger.businessLogger.info("-------------- Shutdown Extractor Manager ---------------");
        if (filesWorker != null) {
            filesWorker.setWorkerShutDownFlag(true);
        }

        if (ResourcesCachingManager.configurationWorker != null) {
            ResourcesCachingManager.configurationWorker.join();
        }
        if (customerBaseMontiringWorker != null) {
            customerBaseMontiringWorker.setWorkerShutDownFlag(true);
            customerBaseMontiringWorker.join();
        }
        if (getSystemMonitor() != null) {
            getSystemMonitor().setWorkerShutDownFlag(true);
            getSystemMonitor().join();
        }
        if (filesWorker != null) {
            while (filesWorker.isAlive()) {
                Thread.sleep(5000);
            }
        }
        if (readerWorkersPool != null) {
            //   readerWorkersPool.shutdownNow();
            readerWorkersPool.shutdown();
            readerWorkersPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        if (preparingWorkersPool != null) {
            //   preparingWorkersPool.shutdownNow();
            preparingWorkersPool.shutdown();
            preparingWorkersPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        if (writersWorkersMap != null) {
            for (ExecutorService ececutor : writersWorkersMap.values()) {
                //    ececutor.shutdownNow();
                ececutor.shutdown();
                ececutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            }
        }
        if (livenessThread != null) {
            livenessThread.join();
        }

    }

    public static boolean isAnyWorkerAlive() {

        if (readerWorkersPool != null) {
            if (!readerWorkersPool.isTerminated()) {
                return true;
            }
        }

        if (preparingWorkersPool != null) {
            if (!preparingWorkersPool.isTerminated()) {
                return true;
            }
        }

        for (ExecutorService ececutor : writersWorkersMap.values()) {
            if (ececutor != null) {
                if (!ececutor.isTerminated()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void updateRunningAndActiveParitions() throws CommonException {

        CommonLogger.businessLogger.info("Start updating last uploaded date...");

        SystemPropertiesModel activeParitionModel = new SystemPropertiesModel();
        activeParitionModel.setItemKey(GeneralConstants.KEY_ACTIVE_DWH_PROFILE_PARTITION);
        activeParitionModel.setItemValue(String.valueOf(ResourcesCachingManager.runId));
        activeParitionModel.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        mainServ.updateSystemProperty(activeParitionModel);
        SystemPropertiesModel runningParitionModel = new SystemPropertiesModel();
        runningParitionModel.setItemKey(EngineDefines.KEY_RUNNING_DWH_PROFILE_PARTITION);
        runningParitionModel.setItemValue("");
        runningParitionModel.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        mainServ.updateSystemProperty(runningParitionModel);

        SystemPropertiesModel lkSystemPropertiesModel = new SystemPropertiesModel();
        lkSystemPropertiesModel.setItemKey(EngineDefines.DWH_LAST_UPLOADED_DATE);
        lkSystemPropertiesModel.setItemValue("");
        lkSystemPropertiesModel.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        mainServ.updateTimeSystemProperty(lkSystemPropertiesModel);
        CommonLogger.businessLogger.info("Last uploaded date updated successfully...");

    }

    public static boolean isCatchingError() {
        return catchingError;
    }

    public static void setCatchingError(boolean aCatchingError) {
        catchingError = aCatchingError;
    }

    public static boolean isPreparingWorkersShutdownFlag() {
        return preparingWorkersShutdownFlag;
    }

    public static void setPreparingWorkersShutdownFlag(boolean aPreparingWorkersShutdownFlag) {
        preparingWorkersShutdownFlag = aPreparingWorkersShutdownFlag;
    }

    public static boolean isWriterWorkersShutdownFlag() {
        return writerWorkersShutdownFlag;
    }

    public static void setWriterWorkersShutdownFlag(boolean aWriterWorkersShutdownFlag) {
        writerWorkersShutdownFlag = aWriterWorkersShutdownFlag;
    }

    public static MonitorThread getSystemMonitor() {
        return systemMonitor;
    }

    public static void initLivenessThread() {
        CommonLogger.businessLogger.info("Manager initLivenessThread() Invoked...");
        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<String, Thread>();

        threads.put("systemMonitor", systemMonitor);
        threads.put("ResourcesCachingManager.configurationWorker", ResourcesCachingManager.configurationWorker);
        threads.put("customerBaseMontiringWorker", customerBaseMontiringWorker);
        threads.put("filesWorker", filesWorker);
        livenessThread = new LivenessThread(ResourcesCachingManager.shutdownEngineFlag, null, null, threads, CommonLogger.livenessLogger, CommonLogger.businessLogger, Integer.parseInt(ResourcesCachingManager.getConfigurationValue(Defines.LIVENESS_THREAD_SLEEP_TIME_PROPERTY_NAME)));
        livenessThread.start();
        CommonLogger.businessLogger.info("Manager initLivenessThread() Ended...");
    }

    public static void startShutdownHookThread() {
        ShutdownHookThread shutdownHookThread = new ShutdownHookThread();
        shutdownHookThread.attachShutDownHook();
    }
}
