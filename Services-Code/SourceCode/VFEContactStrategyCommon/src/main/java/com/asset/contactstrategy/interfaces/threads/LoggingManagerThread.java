/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.threads;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.interfaces.models.RESTLogModel;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author esmail.anbar
 */
public class LoggingManagerThread implements Runnable {

    private ArrayList<RESTLogModel> logs;
    private RESTLogModel model;
    private final int MAX_LOGGING_DB_ARRAY_SIZE;
    private final int MAX_NUM_OF_RETRIES_LTHREAD;
    private final int threadNo;
    private int pullTimeOut;
    private ArrayBlockingQueue<RESTLogModel> loggingQueue;
    private AtomicBoolean SHUTDOWN_LOGGING_THREADS;

    public LoggingManagerThread(int MAX_LOGGING_DB_ARRAY_SIZE, int MAX_NUM_OF_RETRIES_LTHREAD, ArrayBlockingQueue<RESTLogModel> loggingQueue,
            int pullTimeOut, AtomicBoolean SHUTDOWN_LOGGING_THREADS, int threadNo) {
        this.MAX_LOGGING_DB_ARRAY_SIZE = MAX_LOGGING_DB_ARRAY_SIZE;
        this.MAX_NUM_OF_RETRIES_LTHREAD = MAX_NUM_OF_RETRIES_LTHREAD;
        this.loggingQueue = loggingQueue;
        this.pullTimeOut = pullTimeOut;
        this.SHUTDOWN_LOGGING_THREADS = SHUTDOWN_LOGGING_THREADS;
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("LoggingThread_" + threadNo);
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
        MainService mainService = new MainService();
        while (!SHUTDOWN_LOGGING_THREADS.get() || !loggingQueue.isEmpty()) {
            try {
                for (int i = 0; i < MAX_LOGGING_DB_ARRAY_SIZE; i++) {
                    if (loggingQueue != null) {
                        model = loggingQueue.poll(pullTimeOut, TimeUnit.MILLISECONDS);
                        if (model == null) {
                            break;
                        } else {
                            if (logs == null) {
                                logs = new ArrayList<>();
                            }
                            logs.add(model);
                            continue;
                        }
                    }
                }
                if (logs == null || logs.isEmpty()) {
                    continue;
                } else {
                    for (int i = 0; i < MAX_NUM_OF_RETRIES_LTHREAD; i++) {
                        try {
                            CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Try no: " + i);
                            mainService.logRESTAction(logs);
                            break;
                        } catch (CommonException ce) {
                            CommonLogger.businessLogger.error(ce.getErrorMsg());
                            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
                        } catch (Exception e) {
                            CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
                            CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
                        }
                    }
                    logs.clear();
                }
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
                CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
            }
        }
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended || Queue Size: " + loggingQueue.size() 
//                + " || Queue Remaining Capacity: " + loggingQueue.remainingCapacity() 
//                + " || PullTimeOut: " + pullTimeOut + " || MAX_NUM_OR_RETRIES_ATHREAD: " + MAX_NUM_OF_RETRIES_LTHREAD
//                + " || MAX_LOGGING_DB_ARRAY_SIZE: " + MAX_LOGGING_DB_ARRAY_SIZE);
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread has Ended")
                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, loggingQueue.size())
                .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, loggingQueue.remainingCapacity())
                .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, pullTimeOut)
                .put(GeneralConstants.StructuredLogKeys.THREAD_MAX_RETRIES, MAX_NUM_OF_RETRIES_LTHREAD)
                .put(GeneralConstants.StructuredLogKeys.MAX_LOGGING_DB_ARRAY_SIZE, MAX_LOGGING_DB_ARRAY_SIZE).build());
        //CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }

}
