/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.managers;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.threads.LivenessThread;
import com.asset.cs.managerEngine.constants.EngineDefines;
import com.asset.cs.managerEngine.workers.CustomersStatisticsWorker;
import com.asset.cs.managerEngine.workers.InstancesWorker;
import com.asset.cs.managerEngine.workers.KeepAliveWorker;
import com.asset.cs.managerEngine.workers.MonitorThread;
import com.asset.cs.managerEngine.workers.ShutdownHookThread;
import com.asset.cs.managerEngine.workers.UpdateADSStatisticsWorker;
import com.asset.cs.managerEngine.workers.UpdateSMSStatisticsWorker;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Manager {

    private static final String CLASS_NAME = "com.asset.cs.managerEngine.managers.Manager";
    private static boolean statisticsCatchingError;

    private static MainService mainServ = new MainService();

    private static int dwhLatestRunID;
    private static int activeRunId;

    private static InstancesWorker instancesWorker;
    private static CustomersStatisticsWorker statisticsWorker;
    private static KeepAliveWorker keepAliveWorker;

    public static ExecutorService smsUpdateWorkersPool;
    public static BlockingQueue<Integer> smsMSISDNSQueue;

    public static ExecutorService adsUpdateWorkersPool;
    public static BlockingQueue<Integer> adsMSISDNSQueue;

    private static MonitorThread systemMonitor;

    private static int yesterdayColumn;
    private static LivenessThread livenessThread = null;

    public static void startInstancesManager() throws CommonException {
        String methodName = "startInstancesManager";
        try {
            CommonLogger.businessLogger.info("Initializing Instances Manager ");

            instancesWorker = new InstancesWorker();
            instancesWorker.setName("InstancesWorker");
            instancesWorker.start();

            statisticsWorker = new CustomersStatisticsWorker();
            statisticsWorker.setName("CustomerStatisticsWorker");
            statisticsWorker.start();

            Thread.sleep(5000);

            keepAliveWorker = new KeepAliveWorker();
            keepAliveWorker.setName("KeepAliveWorker");
            keepAliveWorker.start();

            systemMonitor = new MonitorThread();
            systemMonitor.setName("System Monitor Thread");
            systemMonitor.start();
            startShutdownHookThread();
            initLivenessThread();

            CommonLogger.businessLogger.info("Instances Manager Initialized Successfully");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error --->", e);
            CommonLogger.businessLogger.error("Fatal Error --->" + e);
            throw processException(e, methodName);
        }

    }

    public static void shutDownInstancesEngine() throws InterruptedException, CommonException {

        if (keepAliveWorker != null) {
            keepAliveWorker.setWorkerShutDownFlag(true);
            while (keepAliveWorker.isAlive()) {
                Thread.sleep(5000);
            }
        }

        if (instancesWorker != null) {
            instancesWorker.setWorkerShutDownFlag(true);
            while (instancesWorker.isAlive()) {
                Thread.sleep(5000);
            }
        }
        if (statisticsWorker != null) {
            statisticsWorker.setWorkerShutDownFlag(true);
            statisticsWorker.interrupt();
            while (statisticsWorker.isAlive()) {
                Thread.sleep(5000);
            }
        }

        if (systemMonitor != null) {
            systemMonitor.setWorkerShutDownFlag(true);
            while (systemMonitor.isAlive()) {
                Thread.sleep(5000);
            }
        }

        CommonLogger.businessLogger.info(" ShutDown SMS Groups Customers Matching Engine");
    }

    public static void startShutdownHookThread() {
        ShutdownHookThread shutdownHookThread = new ShutdownHookThread();
        shutdownHookThread.attachShutDownHook();
    }

    public static void startUpdateWorkers(int jobType) {
        String methodName = "startUpdateWorkers";
        try {

            long startTime = System.currentTimeMillis();
            fillMSISDNSQueue();

            int no_of_sms_threads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_SMS_UPDATE_THREADS);
//            CommonLogger.businessLogger.info("No Of SMS Update Threads is: " + no_of_sms_threads);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Update Threads")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, no_of_sms_threads).build());
            smsUpdateWorkersPool = Executors.newFixedThreadPool(no_of_sms_threads);
            int sms_thread_no;
            for (int i = 0; i < no_of_sms_threads; i++) {
                sms_thread_no = i + 1;
                UpdateSMSStatisticsWorker worker = new UpdateSMSStatisticsWorker(getYesterdayColumn(), jobType);
                if (jobType == EngineDefines.JOB_COPY_COLUMNS) {
                    worker.setName("SMSUpdateWorker_" + sms_thread_no + "_" + no_of_sms_threads + "_COPY");
                } else {
                    worker.setName("SMSUpdateWorker_" + sms_thread_no + "_" + no_of_sms_threads + "_Reset");
                }
                smsUpdateWorkersPool.execute(worker);
            }
            smsUpdateWorkersPool.shutdown();

            int no_of_ads_threads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_ADS_UPDATE_THREADS);
//            CommonLogger.businessLogger.info("No Of ADS Update Threads is: " + no_of_ads_threads);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ADS Update Threads")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, no_of_ads_threads).build());
            adsUpdateWorkersPool = Executors.newFixedThreadPool(no_of_ads_threads);
            int ads_thread_no;
            for (int i = 0; i < no_of_sms_threads; i++) {
                ads_thread_no = i + 1;
                UpdateADSStatisticsWorker worker = new UpdateADSStatisticsWorker(getYesterdayColumn(), jobType);
                if (jobType == EngineDefines.JOB_COPY_COLUMNS) {
                    worker.setName("ADSUpdateWorker_" + ads_thread_no + "_" + no_of_ads_threads + "_COPY");
                } else {
                    worker.setName("ADSUpdateWorker_" + ads_thread_no + "_" + no_of_ads_threads + "_Reset");
                }
                adsUpdateWorkersPool.execute(worker);
            }
            adsUpdateWorkersPool.shutdown();

            CommonLogger.businessLogger.info("Waiting While SMS Update Workers Finish");
            while (!smsUpdateWorkersPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
            }
            CommonLogger.businessLogger.info("Waiting While ADS Update Workers Finish");
            while (!adsUpdateWorkersPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
            }
//            CommonLogger.businessLogger.info("All Update Workers Finished Working on  in [" + (System.currentTimeMillis() - startTime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "All Update Workers Finished Working on")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error --->", ex);
            CommonLogger.businessLogger.error("Fatal Error --->" + ex);
            handleServiceException(ex, methodName);
            if (smsUpdateWorkersPool != null) {
                smsUpdateWorkersPool.shutdownNow();
            }
            if (adsUpdateWorkersPool != null) {
                adsUpdateWorkersPool.shutdownNow();
            }
            startUpdateWorkers(jobType);
        }
    }

    public static void fillMSISDNSQueue() {
        smsMSISDNSQueue = new ArrayBlockingQueue<Integer>(100);
        adsMSISDNSQueue = new ArrayBlockingQueue<Integer>(100);
        for (int i = 0; i <= 99; i++) {
            smsMSISDNSQueue.add(i);
            adsMSISDNSQueue.add(i);
        }
        if (smsMSISDNSQueue.isEmpty() || adsMSISDNSQueue.isEmpty()) {
            CommonLogger.businessLogger.debug("MSISDN Queue Is Empty");
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

    /**
     * @return the catchingError
     */
    public static boolean isStatisticsCatchingError() {
        return statisticsCatchingError;
    }

    /**
     * @param aCatchingError the catchingError to set
     */
    public static void setStatisticsCatchingError(boolean aCatchingError) {
        statisticsCatchingError = aCatchingError;
    }

    /**
     * @return the dwhLatestRunID
     */
    public static int getDwhLatestRunID() {
        return dwhLatestRunID;
    }

    /**
     * @param aDwhLatestRunID the dwhLatestRunID to set
     */
    public static void setDwhLatestRunID(int aDwhLatestRunID) {
        dwhLatestRunID = aDwhLatestRunID;
    }

    /**
     * @return the activeRunId
     */
    public static int getActiveRunId() {
        return activeRunId;
    }

    /**
     * @param aActiveRunId the activeRunId to set
     */
    public static void setActiveRunId(int aActiveRunId) {
        activeRunId = aActiveRunId;
    }

    public static InstancesWorker getInstancesWorker() {
        return instancesWorker;
    }

    public static void setInstancesWorker(InstancesWorker aInstancesWorker) {
        instancesWorker = aInstancesWorker;
    }

    public static CustomersStatisticsWorker getStatisticsWorker() {
        return statisticsWorker;
    }

    public static void setStatisticsWorker(CustomersStatisticsWorker aStatisticsWorker) {
        statisticsWorker = aStatisticsWorker;
    }

    public static int getYesterdayColumn() {
        return yesterdayColumn;
    }

    public static void setYesterdayColumn(int aYesterdayColumn) {
        yesterdayColumn = aYesterdayColumn;
    }

    private static void handleServiceException(Exception e, String methodName) {

        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
        } // Handle SQL Exception 
        else if (e instanceof InterruptedException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
            campaignException = new CommonException("Interrupted exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        //  errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_MANAGER_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

    public static void initLivenessThread() {
        CommonLogger.businessLogger.info("Manager initLivenessThread() Invoked...");
        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<String, Thread>();

        threads.put("systemMonitor", systemMonitor);
        threads.put("ResourcesCachingManager.configurationWorker", ResourcesCachingManager.configurationWorker);
        threads.put("statisticsWorker", statisticsWorker);
        threads.put("instancesWorker", instancesWorker);
        threads.put("keepAliveWorker", keepAliveWorker);
        livenessThread = new LivenessThread(ResourcesCachingManager.shutdownEngineFlag, null, null, threads, CommonLogger.livenessLogger, CommonLogger.businessLogger, Integer.parseInt(ResourcesCachingManager.getConfigurationValue(Defines.LIVENESS_THREAD_SLEEP_TIME_PROPERTY_NAME)));
        livenessThread.start();
        CommonLogger.businessLogger.info("Manager initLivenessThread() Ended...");
    }

}
