/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.task;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.delivery.db.facade.DeliveryFacade;
import com.asset.contactstrategy.delivery.models.JobModel;

/**
 *
 * @author esmail.anbar
 */
public class TimeoutWorker implements Runnable {

    private final JobModel jobModel;

    public TimeoutWorker(JobModel jobModel) {
        this.jobModel = jobModel;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        int count = Integer.MIN_VALUE;
        try {
            count = DeliveryFacade.updateToTimedOut(jobModel);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated Sms Batch to TimedOut")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time))
                    .put(GeneralConstants.StructuredLogKeys.DAY, jobModel.getDate())
                    .put(GeneralConstants.StructuredLogKeys.MSISDN_LAST_TWO_DIGITS, jobModel.getModX())
                    .put(GeneralConstants.StructuredLogKeys.COUNT, count).build());
        } catch (Exception e) {
            CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error while Updating Sms Batch to TimedOut")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time))
                    .put(GeneralConstants.StructuredLogKeys.DAY, jobModel.getDate())
                    .put(GeneralConstants.StructuredLogKeys.MSISDN_LAST_TWO_DIGITS, jobModel.getModX())
                    .put(GeneralConstants.StructuredLogKeys.COUNT, count).build(), e);
        }
    }

}
