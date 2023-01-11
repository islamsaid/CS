package com.asset.contactstrategy.controllers;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.threads.SMSBulkExecutorThread;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hazem.fekry
 */
public class SMSBulkManager implements Runnable {

    public static ExecutorService smsBulkExecutor;
    public static ArrayList<SMSBulkExecutorThread> listOfThreads;
    private static boolean running = true;

    public static void init() {
        CommonLogger.businessLogger.debug("[SMSBulkManager] init() Invoked...");
        running = true;
        listOfThreads = new ArrayList<>();
        int poolSize = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_POOL_SIZE));
        int batchSize = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_MAX_BATCH_SIZE));
        int maxHTTPHitTimes = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_MAX_HTTP_HIT_TIMES));
        int connTimeOut = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_CONN_TIME_OUT));
        int readTimeOut = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_READ_TIME_OUT));
        String url = SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_SENDSMSBULKOFFLINE_URL);
//        int maxNumOfTrails = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_NUM_OF_TRAILS));

        smsBulkExecutor = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            SMSBulkExecutorThread thread = new SMSBulkExecutorThread(i, poolSize, batchSize, maxHTTPHitTimes, connTimeOut, readTimeOut, url);
//            thread.setName("Thread:"+i);
            listOfThreads.add(thread);
            smsBulkExecutor.execute(thread);
        }
        Thread t = new Thread(new SMSBulkManager());
        t.start();
        CommonLogger.businessLogger.debug("[SMSBulkManager] init() Ended...");
    }

    public static void refresh() {
        shutdown();
        init();

    }

    public void notifyAllThreads() {
        CommonLogger.businessLogger.debug("Notify all Threads");
        for (SMSBulkExecutorThread thread : listOfThreads) {
            synchronized (thread) {
                thread.notify();
                CommonLogger.businessLogger.debug("Thread ["+thread.getThreadNumber() +"] notified");
            }
        }
    }

    @Override
    public void run() {
        MainService mainService = new MainService();
        int threadSleepTime=20000;
        CommonLogger.businessLogger.debug("Records Checking Thread Started");
        Thread.currentThread().setName("SMSBulkManager Thread");
        while (running) {
            try {
                int bulkRecordsCount = mainService.getBulkRecordsCount();

                if (bulkRecordsCount>0) {
                    notifyAllThreads();
                }
                Thread.sleep(threadSleepTime);
            } catch (CommonException ex) {
                CommonLogger.businessLogger.debug("Exception---->  at [SMSBulkManager] "+ ex.getErrorMsg());
                CommonLogger.errorLogger.error("Exception---->  at [SMSBulkManager] ", ex);
                try {
                    Thread.sleep(threadSleepTime);
                } catch (InterruptedException ex1) {
                    CommonLogger.businessLogger.debug("Exception---->  at [SMSBulkManager] "+ ex1.getMessage());
                    CommonLogger.errorLogger.error("Exception---->  at [SMSBulkManager]", ex1);
                }
            } catch (InterruptedException ex) {
                try {
                    CommonLogger.businessLogger.debug("Exception---->  at [SMSBulkManager] " + ex.getMessage());
                    CommonLogger.errorLogger.error("Exception---->  at [SMSBulkManager]", ex);
                    Thread.sleep(threadSleepTime);
                } catch (InterruptedException ex1) {
                    CommonLogger.businessLogger.debug("Exception---->  at [SMSBulkManager] "+ ex1.getMessage());
                    CommonLogger.errorLogger.error("Exception---->  at [SMSBulkManager]", ex1);
                }
            }
        }
        CommonLogger.businessLogger.debug("Records Checking Thread Ended");
    }

    public static void shutdown() {
        if (smsBulkExecutor != null) {
            running = false;
            for (SMSBulkExecutorThread thread : listOfThreads) {
                thread.setRunning(false);
                synchronized (thread) {
                    thread.notify();
                }
            }
            smsBulkExecutor.shutdown();
            try {
                smsBulkExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                CommonLogger.businessLogger.info("[SMSBulkManager] shutdown");
            } catch (InterruptedException ex) {
                CommonLogger.businessLogger.debug("InterruptedException---->  at [SMSBulkManager] while trying to refresh:"+ ex.getMessage());
                CommonLogger.errorLogger.error("InterruptedException---->  at [SMSBulkManager] while trying to refresh", ex);
            } catch (Exception ex) {
                CommonLogger.businessLogger.debug("Exception---->  at [SMSBulkManager] while trying to refresh :"+ ex.getMessage());
                CommonLogger.errorLogger.error("Exception---->  at [SMSBulkManager] while trying to refresh", ex);
            }
        }
    }

}
