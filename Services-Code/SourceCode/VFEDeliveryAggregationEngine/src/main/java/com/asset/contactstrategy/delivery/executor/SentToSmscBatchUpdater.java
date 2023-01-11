/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.executor;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.executor.RollingBatchExecutor;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.delivery.db.facade.DeliveryFacade;
import com.asset.contactstrategy.delivery.models.FullSms;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author esmail.anbar
 */
public class SentToSmscBatchUpdater extends RollingBatchExecutor<FullSms, FullSms> {

    public SentToSmscBatchUpdater(int i, RollingBatchExecutor.ConsumePolicy cp, ThreadPoolExecutor tpe) {
        super(i, cp, tpe);
    }

    @Override
    protected void consumeBatchList(ArrayList<FullSms> batchList) {
        long time = System.currentTimeMillis();
        String originalThreadName = Thread.currentThread().getName();;
        try {
            Thread.currentThread().setName(originalThreadName + "-" + SentToSmscBatchUpdater.class.getSimpleName());
            DeliveryFacade.updateToSentToSmsc(batchList);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated Sms Batch to Sent To Smsc")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time))
                    .put(GeneralConstants.StructuredLogKeys.COUNT, batchList.size()).build());
        } catch (Exception e) {
            CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error while Updating Sms Batch to Sent To Smsc")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time))
                    .put(GeneralConstants.StructuredLogKeys.COUNT, batchList.size()).build(), e);
        } finally {
            Thread.currentThread().setName(originalThreadName);
        }
    }

    @Override
    protected FullSms processBeforeAdding(FullSms payload) {
        payload.calculateSendingDate();
        return payload;
    }

    @Override
    protected void postShutdown() {
    }
}
