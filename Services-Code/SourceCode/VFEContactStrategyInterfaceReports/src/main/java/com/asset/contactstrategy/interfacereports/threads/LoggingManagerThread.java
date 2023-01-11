/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfacereports.threads;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.interfacereports.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author esmail.anbar
 */
public class LoggingManagerThread implements Runnable {

    ArrayList<InterfacesLogModel> logs;
    InterfacesLogModel model;
    static int MAX_LOGGING_DB_ARRAY_SIZE = Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE_VALUE;
    static int MAX_NUM_OF_RETRIES_LTHREAD = Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD_VALUE;
    int threadNo;
    static int pullTimeOut;

    public LoggingManagerThread(int threadNo) {
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("LoggingThread_" + threadNo);
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
        pullTimeOut = Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT_VALUE;
        MainService mainService = new MainService();
        while (!ConfigurationManager.SHUTDOWN_FLAG || !ConfigurationManager.requestsToBeLogged.isEmpty()) {
            try {
                for (int i = 0; i < MAX_LOGGING_DB_ARRAY_SIZE; i++) {
                    if (ConfigurationManager.requestsToBeLogged != null) {
                        if (logs == null || logs.isEmpty()) {
                            model = ConfigurationManager.requestsToBeLogged.poll(pullTimeOut, TimeUnit.MILLISECONDS);
                        } else {
                            model = ConfigurationManager.requestsToBeLogged.poll();
                        }
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
                            mainService.logAction(logs);
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
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended || Queue Size: " + ConfigurationManager.requestsToBeLogged.size() 
//                + " || Queue Remaining Capacity: " + ConfigurationManager.requestsToBeLogged.remainingCapacity() 
//                + " || PullTimeOut: " + pullTimeOut + " || MAX_NUM_OR_RETRIES_ATHREAD: " + MAX_NUM_OF_RETRIES_LTHREAD
//                + " || MAX_LOGGING_DB_ARRAY_SIZE: " + LoggingManagerThread.MAX_LOGGING_DB_ARRAY_SIZE);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + " has Ended")
                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.requestsToBeLogged.size())
                .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.requestsToBeLogged.remainingCapacity())
                .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, pullTimeOut)
                .put(GeneralConstants.StructuredLogKeys.THREAD_MAX_RETRIES, MAX_NUM_OF_RETRIES_LTHREAD)
                .put(GeneralConstants.StructuredLogKeys.MAX_LOGGING_DB_ARRAY_SIZE, LoggingManagerThread.MAX_LOGGING_DB_ARRAY_SIZE).build());
        //CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }

}
