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
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.cs.sendingsms.controller.DataSourceManager;
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.service.MainService;
import com.asset.cs.sendingsms.util.Utility;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerollos.asaad
 */
public class DequeuerThread extends Thread {

    private ArrayList<HashObject> smsObjectsList;
    private ArrayBlockingQueue<HashObject> smsQueue;
    private DataSourceManager dataSource;
    private int deq_waitTime;
    private QueueModel appQueue;
    private int threadNumber;

    public DequeuerThread(QueueModel appQueue, int threadNumber) {
        this.smsQueue = Manager.deqeueurs_Senders_QMap.get(appQueue.getAppName());
        this.dataSource = Manager.connectionPerQueue.get(appQueue.getAppName());
        this.deq_waitTime = 0;
        this.appQueue = appQueue;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("dequeuer_" + this.appQueue.getAppName() + "_" + this.threadNumber);
//        CommonLogger.businessLogger.info("*****************DEQEUEUR THREAD STARTED FOR APP_ID:" + this.appQueue.getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Thread Started")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, this.appQueue.getAppName()).build());
        MainService mainService = new MainService();
        while (!Defines.ENGINE_SHUTDOWN_FLAG.get() && !Manager.appsThreadsShutdownMap.get(this.appQueue.getAppName())) {
            try {
                this.appQueue.setThreshold(Defines.appQueueMap.get(this.appQueue.getAppName()).getThreshold());
                ///Added by kashif to reload time window and flag////
                this.appQueue.setTimeWindowFlag(Defines.appQueueMap.get(this.appQueue.getAppName()).isTimeWindowFlag());
                this.appQueue.setTimeWindowFromHour(Defines.appQueueMap.get(this.appQueue.getAppName()).getTimeWindowFromHour());
                this.appQueue.setTimeWindowFromMin(Defines.appQueueMap.get(this.appQueue.getAppName()).getTimeWindowFromMin());
                this.appQueue.setTimeWindowToHour(Defines.appQueueMap.get(this.appQueue.getAppName()).getTimeWindowToHour());
                this.appQueue.setTimeWindowToMin(Defines.appQueueMap.get(this.appQueue.getAppName()).getTimeWindowToMin());
                ///Added by kashif to reload time window and flag////
                smsObjectsList = null;
                long beginTime = System.currentTimeMillis();
                smsObjectsList = mainService.deqBatch(this.appQueue.getAppName(), this.appQueue.getThreshold(), deq_waitTime, dataSource, this.appQueue, this.threadNumber);
                long endTime = System.currentTimeMillis();
//                CommonLogger.businessLogger.info("Dequeuing in " + (endTime - beginTime) + " msec");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Started")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (endTime - beginTime)).build());
                if (smsObjectsList != null && smsObjectsList.size() > 0) {
                    for (HashObject smsObject : smsObjectsList) {
                        // logger.debug("D || " + this.smppApp.appID + " || " + (smppSession.getSequenceNumber() + 1) + " || I || " + smsBatchObject.getEnqueueTime());
                        // opsLogger.info("D || " + this.smppApp.appID + " || " + (smppSession.getSequenceNumber() + 1) + " || I || " + smsBatchObject.getEnqueueTime());
                        // smsObject.setType(this.appQueue.get); // queue doesn't have type from web.
//                        CommonLogger.businessLogger.info("D || " + smsObject.getSMS().getSeqId() + " || " + this.appQueue.getAppName() + "  || I || " + smsObject.getEnqueueTime() + " || QUEUE_MSG_ID || " + smsObject.getMsgid() + " || BATCH_ID|| " + smsObject.getBatchId());
//                        CommonLogger.businessLogger.info("D || " + smsObject.getSMS().getSeqId() + " || " + this.appQueue.getAppName() + "  || I || " + smsObject.getEnqueueTime() + " || QUEUE_MSG_ID || " + smsObject.getMsgid() + " || BATCH_ID|| " + smsObject.getBatchId() + "|| Request: {" + smsObject.getSMS().toString() + "}");

                        if (CommonLogger.businessLogger.isTraceEnabled()) {
                            CommonLogger.businessLogger.trace(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "D")
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, smsObject.getSMS().getDstMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.SEQ_ID, smsObject.getSMS().getSeqId())
                                    .put(GeneralConstants.StructuredLogKeys.APP_NAME, this.appQueue.getAppName())
                                    .put(GeneralConstants.StructuredLogKeys.ENQ_TIME, smsObject.getEnqueueTime())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getSMS().getSeqId())
                                    .put(GeneralConstants.StructuredLogKeys.BATCH_ID, this.appQueue.getAppName())
                                    .put(GeneralConstants.StructuredLogKeys.REQUEST_INFO, smsObject.getSMS())
                                    .build());
                        } else {
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "D")
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, smsObject.getSMS().getDstMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.SEQ_ID, smsObject.getSMS().getSeqId())
                                    .put(GeneralConstants.StructuredLogKeys.APP_NAME, this.appQueue.getAppName())
                                    .put(GeneralConstants.StructuredLogKeys.ENQ_TIME, smsObject.getEnqueueTime())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getSMS().getSeqId())
                                    .put(GeneralConstants.StructuredLogKeys.BATCH_ID, this.appQueue.getAppName())
                                    .build());
                        }
                        smsObject.setDequeueTime(Utility.getDateTime());
                        smsObject.setInstanceId(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.INSTACE_ID)));
                        smsQueue.put(smsObject);
                    }
                } else {
//                    CommonLogger.businessLogger.info("DequeuerThread is going to sleep for " + Defines.fileConfigurations.get(Defines.DEQUEUER_THREAD_SLEEPING_TIME) + " msecs as thread dequeued batch with size:0");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DequeuerThread is going to Sleep, as Thread Dequeued Batch Size=0")
                            .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, Defines.fileConfigurations.get(Defines.DEQUEUER_THREAD_SLEEPING_TIME)).build());
                    Thread.sleep(Long.valueOf((String) Defines.fileConfigurations.get(Defines.DEQUEUER_THREAD_SLEEPING_TIME)));
                    deq_waitTime = 5;
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error("DequeuerThread Caugth CommonException--->" + ex);
                CommonLogger.errorLogger.error("DequeuerThread Caugth CommonException--->" + ex, ex);
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("DequeuerThread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("DequeuerThread Caugth Exception--->" + ex, ex);
            }
        }

//        try {
//            this.dataSource.closeConnectionPool();
//        } catch (CommonException ex) {
//            CommonLogger.businessLogger.error("DequeuerThread Caugth CommonException in Closing Connection Pool--->" + ex);
//            CommonLogger.errorLogger.error("DequeuerThread Caugth CommonException in Closing Connection Pool--->" + ex, ex);
//        } catch (Exception ex) {
//            CommonLogger.businessLogger.error("DequeuerThread Caugth Exception in Closing Connection Pool--->" + ex);
//            CommonLogger.errorLogger.error("DequeuerThread Caugth Exception in Closing Connection Pool--->" + ex, ex);
//        }
//        CommonLogger.businessLogger.info("*****************DEQEUEUR THREAD FINISHED FOR APP_ID:" + this.appQueue.getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Thread Finished")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, this.appQueue.getAppName()).build());
    }
}
