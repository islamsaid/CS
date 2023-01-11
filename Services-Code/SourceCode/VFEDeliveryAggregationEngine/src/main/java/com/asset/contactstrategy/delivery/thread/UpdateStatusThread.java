/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.thread;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.models.JobModel;
import com.asset.contactstrategy.delivery.services.DeliveryAggregationServices;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author esmail.anbar
 */
/*public class UpdateStatusThread implements Runnable {

    private final int threadID;

    public UpdateStatusThread(int threadID) {
        this.threadID = threadID;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("UpdateStatusThread_" + threadID);
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");

        DeliveryAggregationServices deliveryAggregationServices = new DeliveryAggregationServices();
        int sleepTime = Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_IF_NO_JOB_FOUND_VALUE;
        JobModel job = null;

       while (!JobManager.ENGINE_SHUTDOWN_FLAG.get()
                || !JobManager.getInstance().getJobQueue().isEmpty()) {
            try {
                job = JobManager.getInstance().getJobQueue().poll(sleepTime, TimeUnit.MILLISECONDS);

                if (job != null) {
//                    CommonLogger.businessLogger.info("updateDeliveryStatus Initiated with daysBeforeTimeOut: " + job.getDaysBeforeTimeOut());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateDeliveryStatus Initiated with daysBeforeTimeOut")
                            .put(GeneralConstants.StructuredLogKeys.DAYS_BEFORE_TIMEOUT, job.getDaysBeforeTimeOut()).build());
                    deliveryAggregationServices.updateDeliveryStatus(job.getModX(), job.getDate());
//                    CommonLogger.businessLogger.info("updateOtherStatus Initiated with daysBeforeTimeOut: " + job.getDaysBeforeTimeOut());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateOtherStatus Initiated with daysBeforeTimeOut")
                            .put(GeneralConstants.StructuredLogKeys.DAYS_BEFORE_TIMEOUT, job.getDaysBeforeTimeOut()).build());
                    deliveryAggregationServices.updateOtherStatus(job.getModX(), job.getDate());
                }
            } catch (CommonException e) {
                CommonLogger.businessLogger.error("Error While Processing Job: "
                        + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e + " || " + e.getErrorMsg());
                CommonLogger.errorLogger.error("Error While Processing Job: "
                        + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e + " || " + e.getErrorMsg(), e);
                try {
                    JobManager.getInstance().getJobQueue().put(job);
                } catch (Exception e1) {
                    CommonLogger.businessLogger.error("Error While ReAdding Job in Queue Job: "
                            + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e1 + " || " + e1.getMessage());
                    CommonLogger.errorLogger.error("Error While ReAdding Job in Queue Job: "
                            + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e1 + " || " + e1.getMessage(), e1);
                }
            } catch (Exception e) {
                CommonLogger.businessLogger.error("Error While Processing Job: "
                        + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e + " || " + e.getMessage());
                CommonLogger.errorLogger.error("Error While Processing Job: "
                        + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e + " || " + e.getMessage(), e);
                try {
                    JobManager.getInstance().getJobQueue().put(job);
                } catch (Exception e1) {
                    CommonLogger.businessLogger.error("Error While ReAdding Job in Queue Job: "
                            + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e1 + " || " + e1.getMessage());
                    CommonLogger.errorLogger.error("Error While ReAdding Job in Queue Job: "
                            + ((job != null) ? job.getDate() : "FaultyJob") + " || " + e1 + " || " + e1.getMessage(), e1);
                }
            }
        }
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }

}*/
