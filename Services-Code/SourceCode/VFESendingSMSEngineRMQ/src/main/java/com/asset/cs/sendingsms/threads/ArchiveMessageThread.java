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
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerollos.asaad
 */
public class ArchiveMessageThread extends Thread {

    private final ArrayBlockingQueue<HashObject> senders_ArchiveH_Q;
    private final int threadNumber;
    private HashObject smsObject;
    private ArrayList<HashObject> messagesBatch;
    private int counter;
    private final MainService mainService;
    private String updatedObjects;

    public ArchiveMessageThread(int threadNumber) {
        this.updatedObjects = new String();
        this.threadNumber = threadNumber;
        this.senders_ArchiveH_Q = Manager.senders_ArchiveH_Q;
        this.counter = 0;
        this.messagesBatch = new ArrayList<HashObject>();
        this.mainService = new MainService();
    }

    public void updateArchiveBatchProcess() throws CommonException {
        try {
            long beginTime = System.currentTimeMillis();
//        CommonLogger.businessLogger.info("update archive message batch begin of (" + this.updatedObjects + ")");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Archive Message Batch Begin")
                    .put(GeneralConstants.StructuredLogKeys.BATCH_OBJECT, this.updatedObjects).build());
            this.mainService.updateArchive(this.messagesBatch);
            long endTime = System.currentTimeMillis();
//        CommonLogger.businessLogger.info("update archive message batch in " + (endTime - beginTime) + " msec || size = " + this.messagesBatch.size());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Archive Messages Batch End")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (endTime - beginTime))
                    .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, this.messagesBatch.size()).build());
//        this.messagesBatch.clear();
//        this.messagesBatch.trimToSize();
        } finally {
            this.messagesBatch = new ArrayList<>();//Esmail.Anbar | Updating list to use a new reference for faster memory adaptation 
            this.counter = 0;
            this.updatedObjects = "";
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("archiveMessage_" + "_" + this.threadNumber);
//        CommonLogger.businessLogger.info("*****************ARCHIVE MESSAGE THREAD STARTED***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive Message Thread Started").build());
        while (!Defines.SENDER_THREAD_SHUTDOWN_FLAG) {
            try {
                if (messagesBatch == null || messagesBatch.isEmpty()) {
                    smsObject = this.senders_ArchiveH_Q.poll(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.ARCHIVE_POOLING_TIMEOUT)), TimeUnit.MILLISECONDS);
                } else {
                    smsObject = this.senders_ArchiveH_Q.poll();
                }
                if (smsObject == null) {
                    if (messagesBatch != null && !messagesBatch.isEmpty()) {
                        updateArchiveBatchProcess();
                        CommonLogger.businessLogger.debug("A || INSERT ARCHIVE DONE");
                    } else {
                        this.messagesBatch = new ArrayList<HashObject>();
                        this.counter = 0;
                        long sleepTime = Long.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_THREAD_SLEEP_TIME));
//                        CommonLogger.businessLogger.info("archive messages Thread is going to sleep for " + sleepTime + " msecs as thread polled/archivedArray both found null.");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive Messages Thread is going to sleep, as thread polled/archivedArray both found null")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
                        Thread.sleep(sleepTime);
                    }
                } else {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Preparing msg for msg archiving")
                            .put(GeneralConstants.StructuredLogKeys.BATCH_ID, smsObject.getBatchId())
                            .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getMsgid())
                            .put(GeneralConstants.StructuredLogKeys.MSISDN, smsObject.getSMS().getDstMsisdn())
                            .build());
                    messagesBatch.add(smsObject);
                    this.updatedObjects += "(" + smsObject.getSMS().getSeqId() + ")";
                    long batchSize = Long.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_THREAD_BATCH_SIZE));
                    if (++counter % batchSize == 0) {
                        updateArchiveBatchProcess();
                    }
                }
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("ArchiveMessageThread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("ArchiveMessageThread Caugth Exception--->" + ex, ex);
            }
        }

        while (senders_ArchiveH_Q.remainingCapacity() != Integer.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_SMS_QUEUE_SIZE))) {
            try {
                smsObject = this.senders_ArchiveH_Q.poll(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.ARCHIVE_POOLING_TIMEOUT)), TimeUnit.MILLISECONDS);
                if (smsObject == null) {
                    if (messagesBatch != null && !messagesBatch.isEmpty()) {
                        updateArchiveBatchProcess();
                        CommonLogger.businessLogger.debug("A || INSERT ARCHIVE DONE");
                    } else {
                        this.messagesBatch = new ArrayList<HashObject>();
                        this.counter = 0;
                        long sleepTime = Long.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_THREAD_SLEEP_TIME));
//                        CommonLogger.businessLogger.info("archive messages Thread is going to sleep for " + sleepTime + " msecs as thread polled/archivedArray both found null.");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive Messages Thread is going to sleep, as thread polled/archivedArray both found null")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
                        Thread.sleep(sleepTime);
                    }
                } else {
                    messagesBatch.add(smsObject);
                    this.updatedObjects += "(" + smsObject.getSMS().getSeqId() + ")";
                    long batchSize = Long.valueOf(Defines.fileConfigurations.get(Defines.ARCHIVE_THREAD_BATCH_SIZE));
                    if (++counter % batchSize == 0) {
                        updateArchiveBatchProcess();
                    }
                }
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("ArchiveMessageThread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("ArchiveMessageThread Caugth Exception--->" + ex, ex);
            }
        }
//        CommonLogger.businessLogger.info("*****************ARCHIVE MESSAGE THREAD FINISHED***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive Message Thread Finished").build());
    }

}
