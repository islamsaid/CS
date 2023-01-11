///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.asset.contactstrategy.delivery.thread;
//
//import com.asset.contactstrategy.common.controller.Initializer;
//import com.asset.contactstrategy.common.defines.Defines;
//import com.asset.contactstrategy.common.defines.GeneralConstants;
//import com.asset.contactstrategy.common.exception.CommonException;
//import com.asset.contactstrategy.common.logger.CommonLogger;
//import com.asset.contactstrategy.common.logger.StructuredLogFactory;
//import com.asset.contactstrategy.common.service.MainService;
//import com.asset.contactstrategy.delivery.manager.JobManager;
//import com.asset.contactstrategy.delivery.models.JobModel;
//import com.asset.contactstrategy.delivery.services.DeliveryAggregationServices;
//import java.sql.Timestamp;
//import java.util.Calendar;
//import java.util.concurrent.TimeUnit;
//
///**
// *
// * @author esmail.anbar
// */
//public class TimeOutStatusThread implements Runnable {
//
//    private final int threadID;
//
//    public TimeOutStatusThread(int threadID) {
//        this.threadID = threadID;
//    }
//
//    @Override
//    public void run() {
//        Thread.currentThread().setName("TimeOutStatusThread_" + threadID);
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
//        DeliveryAggregationServices deliveryAggregationServices = new DeliveryAggregationServices();
//        int sleepTime = Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_IF_NO_JOB_FOUND_VALUE;
//        JobModel job = null;
//        MainService mainService = new MainService();
//        long timeToSleep;
//
//        while (!JobManager.ENGINE_SHUTDOWN_FLAG.get()) {
//            while (!JobManager.getInstance().getJobTimeOutQueue().isEmpty()) {
//                try {
//                    job = JobManager.getInstance().getJobTimeOutQueue().poll(sleepTime, TimeUnit.MILLISECONDS);
//
//                    if (job != null) {
////                        CommonLogger.businessLogger.info("updateTimeOutStatus Initiated with daysBeforeTimeOut: " + job.getDaysBeforeTimeOut());
//                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateTimeOutStatus Initiated with daysBeforTimeOut")
//                                .put(GeneralConstants.StructuredLogKeys.DAYS_BEFORE_TIMEOUT, job.getDaysBeforeTimeOut()).build());
//                        deliveryAggregationServices.updateTimeOutStatus(job.getModX(), job.getDate());
//                    }
//                } catch (CommonException e) {
//                    CommonLogger.businessLogger.error("Error While Processing Job: "
//                            + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e + " || " + e.getErrorMsg());
//                    CommonLogger.errorLogger.error("Error While Processing Job: "
//                            + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e + " || " + e.getErrorMsg(), e);
//                    try {
//                        JobManager.getInstance().getJobQueue().put(job);
//                    } catch (Exception e1) {
//                        CommonLogger.businessLogger.error("Error While ReAdding Job in Queue Job: "
//                                + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e1 + " || " + e1.getMessage());
//                        CommonLogger.errorLogger.error("Error While ReAdding Job in Queue Job: "
//                                + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e1 + " || " + e1.getMessage(), e1);
//                    }
//                } catch (Exception e) {
//                    CommonLogger.businessLogger.error("Error While Processing Job: "
//                            + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e + " || " + e.getMessage());
//                    CommonLogger.errorLogger.error("Error While Processing Job: "
//                            + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e + " || " + e.getMessage(), e);
//                    try {
//                        JobManager.getInstance().getJobQueue().put(job);
//                    } catch (Exception e1) {
//                        CommonLogger.businessLogger.error("Error While ReAdding Job in Queue Job: "
//                                + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e1 + " || " + e1.getMessage());
//                        CommonLogger.errorLogger.error("Error While ReAdding Job in Queue Job: "
//                                + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e1 + " || " + e1.getMessage(), e1);
//                    }
//                }
//            }
//
//            try {
//                Calendar c = Calendar.getInstance();
//                c.setTimeInMillis(mainService.getCurrentDatabaseTime().getTime());
//
//                Timestamp currentTimeStamp = new Timestamp(c.getTimeInMillis());
//
//                c.add(Calendar.DATE, 1);
//                c.set(Calendar.HOUR_OF_DAY, 0);
//                c.set(Calendar.MINUTE, 15);
//                c.set(Calendar.SECOND, 0);
//                c.set(Calendar.MILLISECOND, 0);
//
//                Timestamp futureTimeStamp = new Timestamp(c.getTimeInMillis());
//
//                timeToSleep = futureTimeStamp.getTime() - currentTimeStamp.getTime();
////                CommonLogger.businessLogger.info("Sleeping for " + timeToSleep + " milliseconds till 12:15am Database Time next day || "
////                        + currentTimeStamp + " | " + futureTimeStamp);
//                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "TimeOutStatusThread is sleeping till 12:15am Database Time next day")
//                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, timeToSleep)
//                        .put(GeneralConstants.StructuredLogKeys.CURRENT_TIME, currentTimeStamp)
//                        .put(GeneralConstants.StructuredLogKeys.FUTURE_TIME, futureTimeStamp).build());
//
//                while (timeToSleep > 0) {
//                    //CommonLogger.businessLogger.info("Time to Sleep: " + timeToSleep);
//                    Thread.sleep(120000);
//                    if (Defines.STRING_TRUE.equalsIgnoreCase(JobManager.ENGINE_SHUTDOWN_FLAG.toString())) {
//                        break;
//                    }
//                    timeToSleep -= 120000;
//                }
//            } catch (Exception e) {
//                CommonLogger.businessLogger.error(e + " || " + e.getMessage());
//                CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
//            }
//        }
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
//    }
//
//}
