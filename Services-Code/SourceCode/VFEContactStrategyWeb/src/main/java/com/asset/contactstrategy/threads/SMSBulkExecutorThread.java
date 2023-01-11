/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.threads;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author aya.moawed
 */
public class SMSBulkExecutorThread extends Thread {

    private final MainService mainService;
    private int threadNumber;
    private int numberOfThreads;
    private int batchSize;
    private boolean running;
    private String url;
    private int readTimeOut;
    private int connTimeOut;
    private int maxHTTPHitTimes;
    private int sleepTime = 10000;
    private int maxNumOfTrails;
//    private int trailsCounter;

    public SMSBulkExecutorThread(int threadNumber, int numberOfThreads, int batchSize, int maxHTTPHitTimes, int connTimeOut, int readTimeOut, String url) {
        this.threadNumber = threadNumber;
        this.numberOfThreads = numberOfThreads;
        this.batchSize = batchSize;
        this.maxHTTPHitTimes = maxHTTPHitTimes;
        this.connTimeOut = connTimeOut;
        this.readTimeOut = readTimeOut;
        this.url = url;
        this.maxNumOfTrails = maxNumOfTrails;
//        this.trailsCounter = 0;
        mainService = new MainService();
        running = true;
    }

    public SMSBulkExecutorThread() {
        mainService = new MainService();
    }

    @Override
    public void run() {
//        CommonLogger.businessLogger.info("Thread [" + threadNumber + "] Started");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Started")
                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
        Thread.currentThread().setName("ExecutorThread:" + threadNumber);
        ArrayList<Long> resultIds = null;
        while (running) {
            RequestPreparator jsonStringContainer;
            try {
                resultIds = new ArrayList<>();
                jsonStringContainer = mainService.getBatchRecords(threadNumber, numberOfThreads, batchSize, resultIds);
                if (!resultIds.isEmpty()) {
//                    CommonLogger.businessLogger.debug("Request at thread[" + threadNumber + "] : " + jsonStringContainer.getJsonString());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Requesting Thread")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
                    String submitSendBulkSMSHTTP = Utility.submitSendBulkSMSHTTP(jsonStringContainer, maxHTTPHitTimes, connTimeOut, readTimeOut, url);
                    HashMap<String, ArrayList> interfaceResult = new HashMap(Utility.gsonJSONStringToHashMapObject(submitSendBulkSMSHTTP));
                    mainService.archiveSMSBulkRecords(interfaceResult, resultIds);
                } else {
//                    Thread.sleep(sleepTime);
                    synchronized (this) {
//                        CommonLogger.businessLogger.debug("Thread [" + threadNumber + "] will wait");
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread will wait")
                                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
                        wait();
                    }
//                    CommonLogger.businessLogger.debug("Thread [" + threadNumber + "] awaked");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread was Awaked")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + ex.getMessage());
                CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", ex);
//                if (trailsCounter >= maxNumOfTrails) {
//                CommonLogger.businessLogger.debug("Number of Trials (" + maxNumOfTrails + ") exceeded batch ... Archive failed batch Started at Thread [" + threadNumber + "]");
                if (resultIds != null) {
                    try {
//                        CommonLogger.businessLogger.debug("Archive failed batch Started at Thread [" + threadNumber + "]");
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive Failed Batch Started")
                                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
                        mainService.archiveFailedSMSBulkRecords(resultIds);
//                        CommonLogger.businessLogger.debug("Archive failed batch Ended at Thread [" + threadNumber + "]");
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive Failed Batch Ended")
                                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
//                        trailsCounter = 0;
                    } catch (CommonException e) {
                        CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + e.getMessage());
                        CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", e);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException ex1) {
                            CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + ex1.getMessage());
                        }
                    }
                }
//                }else{
//                    trailsCounter++;
//                    CommonLogger.businessLogger.debug("Trial number "+trailsCounter+" of "+maxNumOfTrails+" trails Failed at Thread [" + threadNumber + "]");
//                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex1) {
                    CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + ex1.getMessage());
                    CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", ex1);
                }
            } //            catch (IOException ex) {
            //                CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + ex.getMessage());
            //                CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", ex);
            ////                if (trailsCounter >= maxNumOfTrails) {
            //                if (resultIds != null) {
            //                    try {
            //                        CommonLogger.businessLogger.debug("Archive failed batch Started at Thread [" + threadNumber + "]");
            //                        mainService.archiveFailedSMSBulkRecords(resultIds);
            //                        CommonLogger.businessLogger.debug("Archive failed batch Ended at Thread [" + threadNumber + "]");
            ////                            trailsCounter = 0;
            //
            //                    } catch (CommonException e) {
            //                        CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + e.getMessage());
            //                        CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", e);
            //                        try {
            //                            Thread.sleep(sleepTime);
            //                        } catch (InterruptedException ex1) {
            //                            CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", ex1);
            //                        }
            //                    }
            //                }
            ////                }else{
            ////                    trailsCounter++;
            ////                    CommonLogger.businessLogger.debug("Trial number "+trailsCounter+" of "+maxNumOfTrails+" trails Failed at Thread [" + threadNumber + "]");
            ////                }
            //                try {
            //                    Thread.sleep(sleepTime);
            //                } catch (InterruptedException e) {
            //                    CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + e.getMessage());
            //                    CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", e);
            //                }
            //            } 
            catch (InterruptedException ex) {
                CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + ex.getMessage());
                CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", ex);

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + e.getMessage());
                    CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", e);
                }
            } catch (Exception ex) {
                CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + ex.getMessage());
                CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", ex);
//                if (trailsCounter >= maxNumOfTrails) {
//                CommonLogger.businessLogger.debug("Number of Trials (" + maxNumOfTrails + ") exceeded batch ... Archive failed batch Started at Thread [" + threadNumber + "]");
                if (resultIds != null) {
                    try {
//                        CommonLogger.businessLogger.debug("Archive failed batch Started at Thread [" + threadNumber + "]");
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive Failed Batch Started")
                                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
                        mainService.archiveFailedSMSBulkRecords(resultIds);
//                        CommonLogger.businessLogger.debug("Archive failed batch Ended at Thread [" + threadNumber + "]");
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive Failed Batch Endeds")
                                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
//                        trailsCounter = 0;
                    } catch (CommonException e) {
                        CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + e.getMessage());
                        CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", e);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException ex1) {
                            CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + ex1.getMessage());
                            CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", ex1);
                        }
                    }
                }
//                }else{
//                    trailsCounter++;
//                    CommonLogger.businessLogger.debug("Trial number "+trailsCounter+" of "+maxNumOfTrails+" trails Failed at Thread [" + threadNumber + "]");
//                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    CommonLogger.businessLogger.debug("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> " + e.getMessage());
                    CommonLogger.errorLogger.error("Exception at " + SMSBulkExecutorThread.class.getName() + " ----> ", e);
                }
            }
        }
//        CommonLogger.businessLogger.info("Thread [" + threadNumber + "] Ended");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Ended")
                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

}
