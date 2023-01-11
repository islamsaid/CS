/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.task;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.models.ConcatSms;
import com.asset.contactstrategy.delivery.models.FullSms;
import java.util.ArrayList;

/**
 *
 * @author mohamed.halawa
 */
public class DeciderWorker implements Runnable {

    private final FullSms fullSms;

    public DeciderWorker(FullSms fullSms) {
        this.fullSms = fullSms;
    }

    @Override
    public void run() {
        int status = Integer.MIN_VALUE;
        try {
            //To decide the status of SMSs
            //If all concats are delivered then delivered
            //If any of the concats are delivered then partial delivery or uncertain delivery
            //If none is delivered then not delivered
            if (!fullSms.isHasAnyStatus()) {
                if (fullSms.getSTATUS() == Defines.VFE_CS_SMS_H_STATUS_LK.ENQUEUED) {
                    status = Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC;
                    JobManager.sentToSmscBatchUpdater.execute(fullSms);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message status decided")
                            .put(GeneralConstants.StructuredLogKeys.MSISDN, fullSms.getMSISDN())
                            .put(GeneralConstants.StructuredLogKeys.STATUS, status).build());
                } else {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message status dismissed... No status was found for any concat")
                            .put(GeneralConstants.StructuredLogKeys.MSISDN, fullSms.getMSISDN()).build());
                }
            } else {
                ArrayList<ConcatSms> concats = fullSms.getConcats();
                if (concats.size() == fullSms.getDelivered()) {
                    //enqueue for delivery update
                    status = Defines.VFE_CS_SMS_H_STATUS_LK.DELIVERED;
                    JobManager.deliveryBatchUpdater.execute(fullSms);
                } else if (fullSms.getDelivered() != 0 && concats.size() > fullSms.getDelivered()) {
                    //enqueue for partial/uncertain delivery
                    status = Defines.VFE_CS_SMS_H_STATUS_LK.PARTIAL_DELIVERY;
                    JobManager.partialDeliveryBatchUpdater.execute(fullSms);
                } else if (fullSms.getDelivered() == 0) {
                    //enqueue for not delivered
                    status = Defines.VFE_CS_SMS_H_STATUS_LK.NOT_DELIVERED;
                    JobManager.notDeliveredBatchUpdater.execute(fullSms);
                }
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message status decided")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN, fullSms.getMSISDN())
                        .put(GeneralConstants.StructuredLogKeys.STATUS, status).build());
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error while deciding message status")
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, fullSms.getMSISDN())
                    .put(GeneralConstants.StructuredLogKeys.STATUS, status).build(), e);
        }
    }

}
