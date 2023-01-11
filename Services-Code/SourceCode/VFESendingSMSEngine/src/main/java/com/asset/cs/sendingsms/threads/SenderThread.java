/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.threads;

import client.Data;
import client.HashObject;
import client.SMPP;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.service.MainService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author kerollos.asaad
 */
public class SenderThread extends Thread {

    private ArrayBlockingQueue<HashObject> deqeuer_sender_Q;
    private HashObject smsObject;
    private SMPP smppSession;
    private QueueModel appQueue;
    private int threadNumber;

    public SenderThread(QueueModel appQueue, int threadNumber) {
        this.deqeuer_sender_Q = Manager.deqeueurs_Senders_QMap.get(appQueue.getAppName());
        this.appQueue = appQueue;
        this.smppSession = new SMPP(this.appQueue, threadNumber);
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("sender_" + this.appQueue.getAppName() + "_" + this.threadNumber);
//        CommonLogger.businessLogger.info("*****************SENDER THREAD STARTED FOR APP_ID:" + this.appQueue.getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sender Thread Started")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, this.appQueue.getAppName()).build());
        int msgCounter = 0;
        int nullMsgCounter = 0;
        long beginTime = System.currentTimeMillis();
        smppSession.ConnectToFTD();
        MainService mainService = new MainService();
        while (!Defines.DEQUEUER_THREAD_SHUTDOWN_FLAG && !Manager.appsThreadsShutdownMap.get(this.appQueue.getAppName())) {
            try {
                smsObject = deqeuer_sender_Q.poll(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.SENDER_THREAD_POLLING_TIMEOUT)), TimeUnit.MILLISECONDS);
                if (smsObject != null) {
                    CommonLogger.businessLogger.debug(smsObject.toString());
                    if (smsObject.getSMS().getSubmissionDate() != null && smsObject.getSMS().getExpirationHours() != null && smsObject.getSMS().getExpirationHours().intValue() > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(smsObject.getSMS().getSubmissionDate().getTime());
                        calendar.add(Calendar.HOUR, smsObject.getSMS().getExpirationHours().intValue());
                        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
//                            CommonLogger.businessLogger.info("S || " + this.appQueue.getAppName() + " || QUEUE_MSG_ID || " + smsObject.getMsgid() + " || BATCH_ID|| " + smsObject.getBatchId() + "Message expired and will not be sent");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Expired and Will not Be Sent")
                                    .put(GeneralConstants.StructuredLogKeys.APP_ID, this.appQueue.getAppName())
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, smsObject.getSMS().getDstMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getMsgid())
                                    .put(GeneralConstants.StructuredLogKeys.BATCH_ID, smsObject.getBatchId()).build());
                            smsObject.setMessageStatus(com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMS_H_STATUS_LK.EXPIRED);
                            Manager.senders_ArchiveH_Q.put(smsObject);
                            continue;
                        }
                    }
                    msgCounter++;
                    if (smppSession.getConnection() != null && smppSession.getConnectionStatus() == false) {
                        smppSession.ConnectToFTD(); // reconnect
                    }

                    if (smppSession.getConnection() != null && smppSession.getConnectionStatus() == true) {
                        CommonLogger.businessLogger.debug("Calling Submit_SM");
                        int submitReturnValue = 0;
                        try {
                            ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, smsObject.getSMS().getDstMsisdn());
                            ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getMsgid());
                            submitReturnValue = smppSession.Submit_SM(Data.GSM_TON_INTERNATIONAL, Data.GSM_NPI_ISDN, smsObject);
                            CommonLogger.businessLogger.info("S || " + this.appQueue.getAppName() + "  || submit_sm || " + submitReturnValue);
//                            CommonLogger.businessLogger.debug("S || " + this.appQueue.getAppName() + "  || submit_sm || " + submitReturnValue);
                        } catch (Exception ex) {//added in case connection on port is true but username and password are wrong
                            // CommonLogger.businessLogger.error("SenderThread Caugth Exception in Submit--->" + ex);
                            CommonLogger.errorLogger.error("SenderThread Caugth Exception in Submit--->" + ex, ex);
                        } finally {
                            ThreadContext.remove(GeneralConstants.StructuredLogKeys.MSISDN);
                            ThreadContext.remove(GeneralConstants.StructuredLogKeys.MSG_ID);
                        }
                        if (submitReturnValue == 1) { // message written successfully on smsc socket, should be enqueued to update delivery counter.
                            if (smsObject.getSMS().getNbtrials().compareTo(BigDecimal.ZERO) == 0) {
                                smsObject.setMessageStatus(com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC);
                                Manager.senders_ArchiveH_Q.put(smsObject);
                            }
                        } else if (submitReturnValue != 1) {
                            CommonLogger.businessLogger.debug("Submit_SM finished");
                            CommonLogger.businessLogger.debug("ENQ MSG");
                            CommonLogger.businessLogger.info("Enqueuing message again for resending submitReturnValue != 1");
                            mainService.enqMsg(smsObject, Manager.connectionPerQueue.get(appQueue.getAppName()));
                            CommonLogger.businessLogger.debug("MSG ENQD");
                        }
                    } else//enq message again in case not connected to SMSC
                    {
                        CommonLogger.businessLogger.info("Enqueuing message again in database queue for resending not connected to smsc");
                        mainService.enqMsg(smsObject, Manager.connectionPerQueue.get(appQueue.getAppName()));
                    }
                } else {//sms in queue is null
                    nullMsgCounter++;
                    if (nullMsgCounter == this.appQueue.getThreshold()) {
//                        CommonLogger.businessLogger.info("Sender Thread is going to sleep for " + Long.valueOf((String) Defines.fileConfigurations.get(Defines.SENDER_THREAD_SLEEPING_TIME)) + " msecs as thread polled null sms with same threshold size:" + this.appQueue.getThreshold());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread is goinng to sleep, as thread polled null sms with same Threshold")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, Long.valueOf((String) Defines.fileConfigurations.get(Defines.SENDER_THREAD_SLEEPING_TIME)))
                                .put(GeneralConstants.StructuredLogKeys.THRESHOLD_SIZE, this.appQueue.getThreshold()).build());
                        Thread.sleep(Long.valueOf((String) Defines.fileConfigurations.get(Defines.SENDER_THREAD_SLEEPING_TIME)));
                        nullMsgCounter = 0;
                    }
                }
                if (msgCounter == this.appQueue.getThreshold()) {
                    long diffTime = 1000 - (System.currentTimeMillis() - beginTime);
                    long waitTime;
                    if (diffTime < 0) {
                        waitTime = 0;
                    } else {
                        waitTime = diffTime;
                    }
//                    CommonLogger.businessLogger.info("diffTime || " + diffTime + " || waitTime ||" + waitTime);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Time Stats")
                            .put(GeneralConstants.StructuredLogKeys.DIFF_TIME, diffTime)
                            .put(GeneralConstants.StructuredLogKeys.WAIT_TIME, waitTime).build());
                    if (this.smppSession.getSendRcvHash() != null) {
//                        CommonLogger.businessLogger.info("SLEEP || " + waitTime + " || SendRcvHash size ||" + this.smppSession.getSendRcvHash().size());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread is Sleeping")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, waitTime)
                                .put(GeneralConstants.StructuredLogKeys.SIZE, this.smppSession.getSendRcvHash().size()).build());
                    }
                    Thread.sleep(waitTime);
                    CommonLogger.businessLogger.info("AWAKE");
                    msgCounter = 0;
                    beginTime = System.currentTimeMillis();
                }
            } catch (CommonException ex) {
                // CommonLogger.businessLogger.error("SenderThread Caugth SMPPSenderException--->" + ex);
                CommonLogger.errorLogger.error("SenderThread Caugth SMPPSenderException--->" + ex, ex);
            } catch (InterruptedException ex) {
                // CommonLogger.businessLogger.error("SenderThread Caugth InterruptedException--->" + ex);
                CommonLogger.errorLogger.error("SenderThread Caugth InterruptedException--->" + ex, ex);
            } catch (Exception ex) {
                // CommonLogger.businessLogger.error("SenderThread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("SenderThread Caugth Exception--->" + ex, ex);
            }
        }

        while (deqeuer_sender_Q.remainingCapacity() != Integer.valueOf(Defines.fileConfigurations.get(Defines.SENDER_SMS_QUEUE_SIZE))) {
            try {
                smsObject = deqeuer_sender_Q.poll(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.SENDER_THREAD_POLLING_TIMEOUT)), TimeUnit.MILLISECONDS);
                //  msgCounter++;
                if (smsObject != null) {
                    if (smsObject.getSMS().getSubmissionDate() != null && smsObject.getSMS().getExpirationHours().intValue() > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(smsObject.getSMS().getSubmissionDate().getTime());
                        calendar.add(Calendar.HOUR, smsObject.getSMS().getExpirationHours().intValue());
                        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                            smsObject.setMessageStatus(com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMS_H_STATUS_LK.EXPIRED);
                            Manager.senders_ArchiveH_Q.put(smsObject);
                            continue;
                        }
                    }
                    msgCounter++;
                    if (smppSession.getConnection() != null && smppSession.getConnectionStatus() == false) {
                        smppSession.ConnectToFTD(); // reconnect
                    }
                    if (smppSession.getConnection() != null && smppSession.getConnectionStatus() == true) {
                        CommonLogger.businessLogger.debug("Calling Submit_SM");
                        int submitReturnValue = 0;
                        try {
                            submitReturnValue = smppSession.Submit_SM(Data.GSM_TON_INTERNATIONAL, Data.GSM_NPI_ISDN, smsObject);
                            CommonLogger.businessLogger.info("S || " + this.appQueue.getAppName() + "  || submit_sm || " + submitReturnValue);
                            CommonLogger.businessLogger.debug("S || " + this.appQueue.getAppName() + "  || submit_sm || " + submitReturnValue);
                        } catch (Exception ex) {//added in case connection on port is true but username and password are wrong
                            // CommonLogger.businessLogger.error("SenderThread Caugth Exception in Submit--->" + ex);
                            CommonLogger.errorLogger.error("SenderThread Caugth Exception in Submit--->" + ex, ex);
                        }
                        //    if (smppSession.Submit_SM(Data.GSM_TON_INTERNATIONAL, Data.GSM_NPI_ISDN, smsObject) != 1) {
                        if (submitReturnValue == 1) { // message written successfully on smsc socket, should be enqueued to update delivery counter.
                            if (smsObject.getSMS().getNbtrials().compareTo(BigDecimal.ZERO) == 0) {
                                smsObject.setMessageStatus(com.asset.contactstrategy.common.defines.Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC);
                                Manager.senders_ArchiveH_Q.put(smsObject);
                            }
                        } else if (submitReturnValue != 1) {
                            CommonLogger.businessLogger.debug("Submit_SM finished");
                            CommonLogger.businessLogger.debug("ENQ MSG");
                            CommonLogger.businessLogger.info("Enqueuing message again for resending submitReturnValue != 1");
                            mainService.enqMsg(smsObject, Manager.connectionPerQueue.get(appQueue.getAppName()));
                            CommonLogger.businessLogger.debug("MSG ENQD");
                        }
                    } else//enq message again in case not connected to SMSC
                    {
                        CommonLogger.businessLogger.info("Enqueuing message again for resending not connected to smsc");
                        mainService.enqMsg(smsObject, Manager.connectionPerQueue.get(appQueue.getAppName()));
                    }
                } else {//sms in queue is null
                    nullMsgCounter++;
                    if (nullMsgCounter == this.appQueue.getThreshold()) {
//                        CommonLogger.businessLogger.info("Sender Thread is going to sleep for " + Defines.SENDER_THREAD_SLEEPING_TIME + " msecs as thread polled null sms with same threshold size:" + this.appQueue.getThreshold());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread is going to sleep, as thread polled null sms with same threshold")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, Defines.SENDER_THREAD_SLEEPING_TIME)
                                .put(GeneralConstants.StructuredLogKeys.THRESHOLD_SIZE, this.appQueue.getThreshold()).build());
                        Thread.sleep(Long.valueOf((String) Defines.fileConfigurations.get(Defines.SENDER_THREAD_SLEEPING_TIME)));
                        nullMsgCounter = 0;
                    }
                }
                if (msgCounter == this.appQueue.getThreshold()) {
                    long diffTime = 1000 - (System.currentTimeMillis() - beginTime);
                    long waitTime;
                    if (diffTime < 0) {
                        waitTime = 0;
                    } else {
                        waitTime = diffTime;
                    }
//                    CommonLogger.businessLogger.info("diffTime || " + diffTime + " || waitTime ||" + waitTime);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Time Stats")
                            .put(GeneralConstants.StructuredLogKeys.DIFF_TIME, diffTime)
                            .put(GeneralConstants.StructuredLogKeys.WAIT_TIME, waitTime).build());

                    if (this.smppSession.getHashtable() != null) {
//                        CommonLogger.businessLogger.info("SLEEP || " + waitTime + " || SendRcvHash size ||" + this.smppSession.getHashtable().size());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread is Sleeping")
                                .put(GeneralConstants.StructuredLogKeys.WAIT_TIME, waitTime)
                                .put(GeneralConstants.StructuredLogKeys.SIZE, this.smppSession.getHashtable().size()).build());
                    }
                    Thread.sleep(waitTime);
                    CommonLogger.businessLogger.info("AWAKE");
                    msgCounter = 0;
                    beginTime = System.currentTimeMillis();
                }
            } catch (CommonException ex) {
                // CommonLogger.businessLogger.error("SenderThread Caugth SMPPSenderException--->" + ex);
                CommonLogger.errorLogger.error("SenderThread Caugth SMPPSenderException--->" + ex, ex);
            } catch (InterruptedException ex) {
                // CommonLogger.businessLogger.error("SenderThread Caugth InterruptedException--->" + ex);
                CommonLogger.errorLogger.error("SenderThread Caugth InterruptedException--->" + ex, ex);
            } catch (Exception ex) {
                // CommonLogger.businessLogger.error("SenderThread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("SenderThread Caugth Exception--->" + ex, ex);
            }
        }
        if (smppSession != null) {
            smppSession.Close();
        }
//        CommonLogger.businessLogger.info("*****************SNEDER THREAD FINISHED FOR APP_ID:" + this.appQueue.getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sender Thread Finished")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, this.appQueue.getAppName()).build());
    }
}
