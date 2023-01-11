/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.threads;

import client.HashObject;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.service.MainService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kerollos.asaad
 */
public class UpdateOnDeliveryThread extends Thread {

    private final ArrayBlockingQueue<HashObject> deliverResp_updateStatusCountH_Q;
    private final int threadNumber;
    private HashObject smsObject;
    private ArrayList<HashObject> messagesCachedBatch;
    private ArrayList<HashObject> messagesBatch;
    private int cachedCounter;
    private int counter;
    private final MainService mainService;
    private long batchSize;
    private String updatedCachedObjects;
    private String updatedObjects;

    public UpdateOnDeliveryThread(int threadNumber) {
        this.updatedCachedObjects = new String();
        this.updatedObjects = new String();
        this.threadNumber = threadNumber;
        this.deliverResp_updateStatusCountH_Q = Manager.deliverResp_updateStatusCountH_Q;
        this.cachedCounter = 0;
        this.counter = 0;
        this.messagesCachedBatch = new ArrayList<HashObject>();
        this.messagesBatch = new ArrayList<>();
        this.mainService = new MainService();
        this.batchSize = Long.valueOf(Defines.fileConfigurations.get(Defines.UPDATE_ON_DELIVERY_THREAD_BATCH_SIZE));
    }

    public void updateOnDeliveryCachedBatchProcess() throws CommonException {
        try {
            long beginTime = System.currentTimeMillis();
//        CommonLogger.businessLogger.info("update status and counter cached batch begin of (" + updatedCachedObjects + ")");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Status and Counter Cached Batch begin")
                    .put(GeneralConstants.StructuredLogKeys.BATCH_OBJECT, updatedCachedObjects).build());

            this.mainService.updateStatusAndCounterCachedBatch(this.messagesCachedBatch);
            long endTime = System.currentTimeMillis();
//        CommonLogger.businessLogger.info("update status and counter cached batch end in " + (endTime - beginTime) + " msec || size = " + this.messagesCachedBatch.size());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Status and Counter Cached Batch End")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (endTime - beginTime))
                    .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, this.messagesCachedBatch.size()).build());

//        this.messagesCachedBatch.clear();
//        this.messagesCachedBatch.trimToSize();
        } finally {
            this.messagesCachedBatch = new ArrayList<>();
            this.cachedCounter = 0;
            this.updatedCachedObjects = "";
        }
    }

    public void updateOnDeliveryBatchProcess() throws CommonException {
        long beginTime = System.currentTimeMillis();
//        CommonLogger.businessLogger.info("update status and counter batch begin of (" + updatedObjects + ")");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Status and Counter Cached Batch begin")
                .put(GeneralConstants.StructuredLogKeys.BATCH_OBJECT, updatedCachedObjects).build());
        this.mainService.updateStatusAndCounterBatch(this.messagesBatch);
        long endTime = System.currentTimeMillis();
//        CommonLogger.businessLogger.info("update status and counter batch end in " + (endTime - beginTime) + " msec || size = " + this.messagesBatch.size());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Status and Counter Cached Batch End")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (endTime - beginTime))
                .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, this.messagesCachedBatch.size()).build());
//        this.messagesBatch.clear();
//        this.messagesBatch.trimToSize();
        this.messagesBatch = new ArrayList<>();
        this.counter = 0;
        this.updatedObjects = "";
    }

    @Override
    public void run() {
        Thread.currentThread().setName("updateOnDelivery" + "_" + this.threadNumber);
//        CommonLogger.businessLogger.info("*****************UPDATE ON DELIVERY THREAD STARTED***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update on Delivery Thread Started").build());
        while (!Defines.SENDER_THREAD_SHUTDOWN_FLAG) {
            try {
                if (messagesCachedBatch == null || messagesCachedBatch.isEmpty()) {
                    smsObject = this.deliverResp_updateStatusCountH_Q.poll(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.UPDATE_ON_DELIVERY_POOLING_TIMEOUT)), TimeUnit.MILLISECONDS);
                } else {
                    smsObject = this.deliverResp_updateStatusCountH_Q.poll();
                }
                if (smsObject == null) {
                    if (messagesCachedBatch != null && !messagesCachedBatch.isEmpty()) {
                        updateOnDeliveryCachedBatchProcess();
                        CommonLogger.businessLogger.debug("A || UPDATE CASHED COUNTER AND STATUS DONE");
                    } else if (messagesBatch != null && !messagesBatch.isEmpty()) {
                        updateOnDeliveryBatchProcess();
                        CommonLogger.businessLogger.debug("A || UPDATE COUNTER AND STATUS DONE");
                    } else {
                        this.messagesCachedBatch = new ArrayList<HashObject>();
                        this.messagesBatch = new ArrayList<HashObject>();
                        this.cachedCounter = 0;
                        this.counter = 0;
                        long sleepTime = Long.valueOf(Defines.fileConfigurations.get(Defines.UPDATE_ON_DELIVERY_THREAD_SLEEP_TIME));
//                        CommonLogger.businessLogger.info("update counter and status messages Thread is going to sleep for " + sleepTime + " msecs as thread polled/archivedArray both found null.");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Counter and Status Messages Thread is going to sleep, as Thread polled/archivedArray both Found Null")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
                        Thread.sleep(sleepTime);
                    }
                } else {
                    CommonLogger.businessLogger.trace(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Preparing msg for delivery update")
                            .put(GeneralConstants.StructuredLogKeys.MSISDN, smsObject.getSMS().getDstMsisdn())
                            .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getMsgid())
                            .build());
                    if (smsObject.getSMS().getSeqId().compareTo(BigDecimal.valueOf(Long.valueOf(Defines.NOT_FOUND_SEQ_ID))) == 0) {
                        messagesBatch.add(smsObject);
                        this.updatedObjects += "(" + smsObject.getSMS().getSmsc_id() + ";" + smsObject.getSMS().getSmsc_msg_id() + ")";
//                        CommonLogger.businessLogger.info("Not In hashmap");
                        if (++counter % batchSize == 0) {
                            updateOnDeliveryBatchProcess();
                        }
                    } else {
                        messagesCachedBatch.add(smsObject);
                        this.updatedCachedObjects += "(" + smsObject.getSMS().getSeqId() + ")";
                        if (++cachedCounter % batchSize == 0) {
                            updateOnDeliveryCachedBatchProcess();
                        }
                    }
                }
            } catch (Exception ex) {
                // CommonLogger.businessLogger.error("UpdateOnDeliveryThread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("UpdateOnDeliveryThread Caugth Exception--->" + ex, ex);
            }
        }

        while (deliverResp_updateStatusCountH_Q.remainingCapacity() != Integer.valueOf(Defines.fileConfigurations.get(Defines.UPDATE_ON_DELIVERY_SMS_QUEUE_SIZE))) {
            try {
                smsObject = this.deliverResp_updateStatusCountH_Q.poll(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.UPDATE_ON_DELIVERY_POOLING_TIMEOUT)), TimeUnit.MILLISECONDS);
                if (smsObject == null) {
                    if (messagesCachedBatch != null && !messagesCachedBatch.isEmpty()) {
                        updateOnDeliveryCachedBatchProcess();
                        CommonLogger.businessLogger.debug("A || UPDATE CASHED COUNTER AND STATUS DONE");
                    } else if (messagesBatch != null && !messagesBatch.isEmpty()) {
                        updateOnDeliveryBatchProcess();
                        CommonLogger.businessLogger.debug("A || UPDATE COUNTER AND STATUS DONE");
                    } else {
                        this.messagesCachedBatch = new ArrayList<HashObject>();
                        this.messagesBatch = new ArrayList<HashObject>();
                        this.cachedCounter = 0;
                        this.counter = 0;
                        long sleepTime = Long.valueOf(Defines.fileConfigurations.get(Defines.UPDATE_ON_DELIVERY_THREAD_SLEEP_TIME));
//                        CommonLogger.businessLogger.info("update counter and status messages Thread is going to sleep for " + sleepTime + " msecs as thread polled/archivedArray both found null.");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Counter and Status Messages Thread is going to sleep, as Thread polled/archivedArray both Found Null")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
                        Thread.sleep(sleepTime);
                    }
                } else {
                    if (smsObject.getSMS().getSeqId().compareTo(BigDecimal.valueOf(Long.valueOf(Defines.NOT_FOUND_SEQ_ID))) == 0) {
                        messagesBatch.add(smsObject);
                        this.updatedObjects += "(" + smsObject.getSMS().getSmsc_id() + ";" + smsObject.getSMS().getSmsc_msg_id() + ")";
//                        CommonLogger.businessLogger.info("NOT in Hashmap");
                        if (++counter % batchSize == 0) {
                            updateOnDeliveryBatchProcess();
                        }
                    } else {
                        messagesCachedBatch.add(smsObject);
                        this.updatedCachedObjects += "(" + smsObject.getSMS().getSeqId() + ")";
                        if (++cachedCounter % batchSize == 0) {
                            updateOnDeliveryCachedBatchProcess();
                        }
                    }
                }
            } catch (Exception ex) {
                // CommonLogger.businessLogger.error("UpdateOnDeliveryThread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("UpdateOnDeliveryThread Caugth Exception--->" + ex, ex);
            }
        }
//        CommonLogger.businessLogger.info("*****************UPDATE ON DELIVERY THREAD FINISHED***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update On Delivery Thread Finished").build());
    }

}
