/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.managers;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.GroupsParentModel;
import com.asset.contactstrategy.common.models.MatchingInstanceModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.interfaces.threads.LivenessThread;
import com.asset.cs.customermatching.constants.EngineDefines;
import com.asset.cs.customermatching.models.StatusModel;
import com.asset.cs.customermatching.workers.CriteriaCreatorWorker;
import com.asset.cs.customermatching.workers.GroupsWorker;
import com.asset.cs.customermatching.workers.ManagerWorker;
import com.asset.cs.customermatching.workers.MonitorThread;
import com.asset.cs.customermatching.workers.ShutdownHookThread;
import com.asset.cs.customermatching.workers.UploadedCreatorWorker;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zain Al-Abedin
 */
public class CustomerMatchingManager {

    private static final String CLASS_NAME = "com.asset.cs.customermatching.managers.CustomerMatchingManager";

    private static boolean catchingError;

    public static GroupsWorker smsGroupsWorker = null;
    public static BlockingQueue<Integer> smsMSISDNSQueue;
    public static BlockingQueue<FileModel> smsfilesQueue;
    public static ExecutorService smsCriteriaWorkersPool;
    public static ExecutorService smsUploadWorkersPool;
    public static StatusModel smsStatusModel;

    public static GroupsWorker adsGroupsWorker = null;
    public static BlockingQueue<Integer> adsMSISDNSQueue;
    public static BlockingQueue<FileModel> adsfilesQueue;
    public static ExecutorService adsCriteriaWorkersPool;
    public static ExecutorService adsUploadWorkersPool;
    public static StatusModel adsStatusModel;

    public static GroupsWorker campGroupsWorker = null;
    public static BlockingQueue<Integer> campMSISDNSQueue;
    public static BlockingQueue<FileModel> campfilesQueue;
    public static ExecutorService campCriteriaWorkersPool;
    public static ExecutorService campUploadWorkersPool;
    public static StatusModel campStatusModel;
    public static AtomicLong campaignTargetedCustomers = new AtomicLong();
    //public static Hashtable<StatusModel, Integer> workersCatchingError = new Hashtable<StatusModel, Integer>();

    public static MatchingInstanceModel instance = null;
    public static MonitorThread systemMonitor;
    public static MainService mainServ = new MainService();
    private final static ManagerWorker manager = new ManagerWorker();
    private static LivenessThread livenessThread = null;

    public static void startCustomerMatchingManager() {
        String methodName = "startCustomerMatchingManager";
        manager.setName("ManagerWorkerThread");
        manager.start();
        CustomerMatchingManager.systemMonitor = new MonitorThread();
        CustomerMatchingManager.systemMonitor.setName("SystemMonitorThread");
        CustomerMatchingManager.systemMonitor.start();
        startShutdownHookThread();
        initLivenessThread();

    }

    public static void startCriteriaWorkers(int groupId, String filterQuery, BlockingQueue<Integer> MSISDNSQueue, ExecutorService criteriaWorkersPool, int type, long maxTargetedCustomers) throws CommonException, InterruptedException {
        long startTime = System.currentTimeMillis();

        fillMSISDNSQueue(MSISDNSQueue);

        int no_of_criteria_threads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_CRITERIA_CREATOR_THREADS);
//        CommonLogger.businessLogger.info("No Of Criteria Threads is: " + no_of_criteria_threads);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No Of Criteria Threads")
                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, no_of_criteria_threads).build());

        //criteriaWorkersPool = Executors.newFixedThreadPool(no_of_criteria_threads);
        if (type == GeneralConstants.CAMPAIGN_GROUP) {
            campaignTargetedCustomers.set(maxTargetedCustomers);
        }

        int criteria_thread_no;
        for (int i = 0; i < no_of_criteria_threads; i++) {
            criteria_thread_no = i + 1;
            CriteriaCreatorWorker worker = new CriteriaCreatorWorker(groupId, filterQuery, MSISDNSQueue, type);
            worker.setName("CriteriaWorker_" + criteria_thread_no + "_" + no_of_criteria_threads + "_" + type + "_" + groupId);
            switch (type) {
                case GeneralConstants.SMS_GROUP:
                    worker.setName("smsCriteriaWorker_" + groupId + "_" + criteria_thread_no + "_" + no_of_criteria_threads);
                    break;
                case GeneralConstants.ADS_GROUP:
                    worker.setName("adsCriteriaWorker_" + groupId + "_" + criteria_thread_no + "_" + no_of_criteria_threads);
                    break;
                case GeneralConstants.CAMPAIGN_GROUP:
                    worker.setName("campCriteriaWorker_" + groupId + "_" + criteria_thread_no + "_" + no_of_criteria_threads);
                    break;
            }
            criteriaWorkersPool.execute(worker);
        }

        criteriaWorkersPool.shutdown();
        CommonLogger.businessLogger.info("Waiting While Criteria Workers Finish");
        while (!criteriaWorkersPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
        }
//        CommonLogger.businessLogger.info("All Criteria Workers Finished Working on Group_ID=[" + groupId + "], type=[" + type + "] in [" + (System.currentTimeMillis() - startTime) + "] msec");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "All Criteria Workers Finished Working")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupId)
                .put(GeneralConstants.StructuredLogKeys.TYPE, type)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
    }

    public static void fillMSISDNSQueue(BlockingQueue<Integer> MSISDNSQueue) {
        //MSISDNSQueue = new ArrayBlockingQueue<Integer>(Math.abs(instance.getSubPartitionEnd() - instance.getSubPartitionStart()) + 1);
        for (int i = instance.getSubPartitionStart(); i <= instance.getSubPartitionEnd(); i++) {
            MSISDNSQueue.add(i);
        }
        if (MSISDNSQueue.isEmpty()) {
            CommonLogger.businessLogger.debug("MSISDN Queue Is Empty");
        }
    }

    public static void startUpladWorkers(int groupId, String filterQuery, BlockingQueue<FileModel> filesQueue, ExecutorService uploadWorkersPool, int type, long maxTargetedCustomers) throws CommonException, InterruptedException {
        long startTime = System.currentTimeMillis();

        fillFilesQueue(groupId, filesQueue, type);

        int no_of_upload_threads = ResourcesCachingManager.getIntValue(EngineDefines.NO_OF_UPLOADED_CREATOR_THREADS);
//        CommonLogger.businessLogger.info("No Of Upload Threads is: " + no_of_upload_threads);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No Of Upload Threads")
                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, no_of_upload_threads).build());

        uploadWorkersPool = Executors.newFixedThreadPool(no_of_upload_threads);
        if (type == GeneralConstants.CAMPAIGN_GROUP) {
            campaignTargetedCustomers.set(maxTargetedCustomers);
        }
        int upload_thread_no;
        for (int i = 0; i < no_of_upload_threads; i++) {
            upload_thread_no = i + 1;
            UploadedCreatorWorker worker = new UploadedCreatorWorker(groupId, filterQuery, filesQueue, type);
            switch (type) {
                case GeneralConstants.SMS_GROUP:
                    worker.setName("smsUploadWorker_" + groupId + "_" + upload_thread_no + "_" + no_of_upload_threads);
                    break;
                case GeneralConstants.ADS_GROUP:
                    worker.setName("adsUploadWorker_" + groupId + "_" + upload_thread_no + "_" + no_of_upload_threads);
                    break;
                case GeneralConstants.CAMPAIGN_GROUP:
                    worker.setName("campUploadWorker_" + groupId + "_" + upload_thread_no + "_" + no_of_upload_threads);
                    break;

            }

            uploadWorkersPool.execute(worker);
        }

        uploadWorkersPool.shutdown();
//        CommonLogger.businessLogger.info("Waiting While Upload Workers Finish for Group_ID=[" + groupId + "], type=[" + type + "]");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Waiting While Upload Workers Finish")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupId)
                .put(GeneralConstants.StructuredLogKeys.TYPE, type).build());
        while (!uploadWorkersPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
        }

//        CommonLogger.businessLogger.info("All Upload Workers Finished Working on Group_ID=[" + groupId + "], type=[" + type + "] in [" + (System.currentTimeMillis() - startTime) + "] msec");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "All Upload Workers Finished Working")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupId)
                .put(GeneralConstants.StructuredLogKeys.TYPE, type)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
    }

    public static void fillFilesQueue(int groupId, BlockingQueue<FileModel> filesQueue, int type) throws CommonException {
        ArrayList<FileModel> groupFiles = new ArrayList<>();
        try {
            groupFiles = mainServ.retrieveGroupFiles(type, groupId);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Failed To get Group Files group_ID=[" + groupId + "], type=[" + type + "]" + ex, ex);
            CommonLogger.businessLogger.error("Failed To get Group Files group_ID=[" + groupId + "], type=[" + type + "]" + ex);
            throw ex;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Failed To get Group Files group_ID=[" + groupId + "], type=[" + type + "]" + ex, ex);
            CommonLogger.businessLogger.error("Failed To get Group Files group_ID=[" + groupId + "], type=[" + type + "]" + ex);
            throw processException(ex, "fillAdsFilesQueue");
        }
        CommonLogger.businessLogger.info("Retrieve [" + groupFiles.size() + "] Files For Group_ID=[" + groupId + "], type=[" + type + "]");
        if (groupFiles.size() == 0) {
            return;
        }
        //filesQueue = new ArrayBlockingQueue<>(groupFiles.size());
        for (FileModel file : groupFiles) {
            filesQueue.add(file);
        }
    }

    public static boolean checkFileStatus(int fileId, int type) throws CommonException {
        boolean valid = false;
        if (type == GeneralConstants.SMS_GROUP) {
            valid = mainServ.checkSMSGroupFileStatus(fileId);
        } else if (type == GeneralConstants.CAMPAIGN_GROUP) {
            valid = mainServ.checkCampaignFileStatus(fileId);
        } else {
            valid = mainServ.checkADSGroupFileStatus(fileId);
        }
        CommonLogger.businessLogger.info("Check FileId=[" + fileId + "] Status Valid=[" + valid + "], type=[" + type + "]");
        return valid;
    }

    public static void shutDownCustomerMatchingEngine() throws InterruptedException, CommonException {

        if (adsGroupsWorker != null) {
            adsGroupsWorker.setWorkerShutDownFlag(true);
            while (adsGroupsWorker.isAlive()) {
                Thread.sleep(5000);
            }
        }
        if (smsGroupsWorker != null) {
            smsGroupsWorker.setWorkerShutDownFlag(true);
            while (smsGroupsWorker.isAlive()) {
                Thread.sleep(5000);
            }
        }
        if (campGroupsWorker != null) {
            campGroupsWorker.setWorkerShutDownFlag(true);
            while (campGroupsWorker.isAlive()) {
                Thread.sleep(5000);
            }
        }

        if (adsCriteriaWorkersPool != null) {
            // adsCriteriaWorkersPool.shutdownNow();
            adsCriteriaWorkersPool.shutdown();
            adsCriteriaWorkersPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        if (smsCriteriaWorkersPool != null) {
            //smsCriteriaWorkersPool.shutdownNow();
            smsCriteriaWorkersPool.shutdown();
            smsCriteriaWorkersPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        if (campCriteriaWorkersPool != null) {
            // campCriteriaWorkersPool.shutdownNow();
            campCriteriaWorkersPool.shutdown();
            campCriteriaWorkersPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }

        if (adsUploadWorkersPool != null) {
            //adsUploadWorkersPool.shutdownNow();
            adsUploadWorkersPool.shutdown();
            adsUploadWorkersPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        if (smsUploadWorkersPool != null) {
            // smsUploadWorkersPool.shutdownNow();
            smsUploadWorkersPool.shutdown();
            smsUploadWorkersPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }

        if (campUploadWorkersPool != null) {
            campUploadWorkersPool.shutdown();
            campUploadWorkersPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }

        if (systemMonitor != null) {
            systemMonitor.setWorkerShutDownFlag(true);
            while (systemMonitor.isAlive()) {
                Thread.sleep(5000);
            }
        }
        if (livenessThread != null) {
            livenessThread.join();
        }

        CommonLogger.businessLogger.info(" ShutDown Groups Customers Matching Engine");
    }

    public static boolean isCatchingError() {
        return catchingError;
    }

    public static void setCatchingError(boolean aCatchingError) {
        catchingError = aCatchingError;
    }

    private static CommonException processException(Exception e, String methodName) {
        if (e instanceof CommonException) {
            return (CommonException) e;
        } // Handle SQL exception
        else if (e instanceof SQLException) {
            return new CommonException("SQL error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } else {
            return new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
    }

    public static MatchingInstanceModel getInstance() {
        return instance;
    }

    public static void setInstance(MatchingInstanceModel aInstance) {
        instance = aInstance;
    }

    public static void initLivenessThread() {
        CommonLogger.businessLogger.info("Manager initLivenessThread() Invoked...");
        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<String, Thread>();

        threads.put("systemMonitor", systemMonitor);
        threads.put("manager", manager);
        livenessThread = new LivenessThread(ResourcesCachingManager.shutdownEngineFlag, null, null, threads, CommonLogger.livenessLogger, CommonLogger.businessLogger, Integer.parseInt(ResourcesCachingManager.getConfigurationValue(Defines.LIVENESS_THREAD_SLEEP_TIME_PROPERTY_NAME)));
        livenessThread.start();
        CommonLogger.businessLogger.info("Manager initLivenessThread() Ended...");
    }

    public static void startShutdownHookThread() {
        ShutdownHookThread shutdownHookThread = new ShutdownHookThread();
        shutdownHookThread.attachShutDownHook();
    }

}
