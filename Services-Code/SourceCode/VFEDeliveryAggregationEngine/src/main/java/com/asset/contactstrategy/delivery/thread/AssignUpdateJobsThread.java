/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.thread;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.services.DeliveryAggregationServices;

/**
 *
 * @author esmail.anbar
 */
public class AssignUpdateJobsThread implements Runnable {

//    String threadName;
//    
//    public AssignUpdateJobsThread(String threadName)
//    {
//        this.threadName = threadName;
//    }
    @Override
    public void run() {
        Thread.currentThread().setName("AssignUpdateJobsThread");
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");

//        DeliveryAggregationServices deliveryAggregationServices = new DeliveryAggregationServices();
        int sleepTime = Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_AFTER_JOBS_UPDATE_VALUE;

        while (!JobManager.ENGINE_SHUTDOWN_FLAG.get()) {
            try {
                DeliveryAggregationServices.assignUpdateJobs();
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Hase Ended");
    }

}
