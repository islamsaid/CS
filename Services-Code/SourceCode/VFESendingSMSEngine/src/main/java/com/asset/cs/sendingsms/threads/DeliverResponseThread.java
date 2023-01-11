/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.threads;

import client.HashObject;
import client.SMPP;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.service.MainService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kerollos.asaad
 */
public class DeliverResponseThread extends Thread {

    private ArrayBlockingQueue<HashObject> sender_deliverResp_Q;
    private boolean killThread = false;
    private SMPP smppSession = null;
    private HashObject smsObject;

    public DeliverResponseThread(SMPP smppSession) {
        this.sender_deliverResp_Q = Manager.receiver_DeliveryResp_QMap.get(smppSession.getSmppApp().getAppName()); // ISSUE 25/10, put sms objects for each session on it's blocking queue.
        this.smppSession = smppSession;
        CommonLogger.businessLogger.debug("DeliverResponse Thread started");
    }

    public void stopThread() {
        this.killThread = true;
        CommonLogger.businessLogger.debug("DeliverResponse Thread called to be killed");
    }

    @Override
    public void run() {
//        CommonLogger.businessLogger.info("*****************DELIVER RESPONSE THREAD STARTED FOR APP_ID:" + smppSession.getSmppApp().getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Deliver Response Thread Started")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, smppSession.getSmppApp().getAppName()).build());
        int nullMsgCounter = 0;
        while (!killThread && !Manager.appsThreadsShutdownMap.get(this.smppSession.getSmppApp().getAppName())) {
            try {
                smsObject = sender_deliverResp_Q.poll(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.DELIVERRESP_THREAD_POLLING_TIMEOUT)), TimeUnit.MILLISECONDS);
                if (smsObject != null) {
                    /*if (smppSession.getConnection() != null && smppSession.getConnectionStatus() == false) {
                        smppSession.ConnectToFTD(); // reconnect
                    }*/
                    if (smppSession.getConnection() != null && smppSession.getConnectionStatus() == true) {
                        CommonLogger.businessLogger.debug("Calling deliver_sm_resp");
                        boolean submitReturnValue = false;
                        try {
                            submitReturnValue = smppSession.deliver_sm_resp(smsObject.getSMS().getSmsc_seq_num(), smsObject.getSMS().getSmsc_msg_id(), smsObject.getSMS().getSeqId().longValue());
                            //CommonLogger.businessLogger.debug("S || " + smsObject.getSMS().getAppId() + " deliver_sm_resp || " + submitReturnValue);
                        } catch (Exception ex) {//added in case connection on port is true but username and password are wrong
                            // CommonLogger.businessLogger.error("DeliverResponseThread Caugth Exception in deliver_sm_resp--->" + ex);
                            CommonLogger.errorLogger.error("DeliverResponseThread Caugth Exception in deliver_sm_resp--->" + ex, ex);
                        }
                        if (submitReturnValue) { // message written successfully on smsc socket.
//                            CommonLogger.businessLogger.info("Successed in submit return value on smsc, message written successfully on smsc socket.");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successed in submit return value on smsc, message written successfully on smsc socket")
                                    .put(GeneralConstants.StructuredLogKeys.BATCH_ID, smsObject.getBatchId())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getMsgid())
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, smsObject.getSMS().getDstMsisdn())
                                    .build());
                        } else if (!submitReturnValue) {
//                            CommonLogger.businessLogger.info("Enqueuing message again, fails in submit return value on smsc.");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueuing message again, fails in submit return value on smsc")
                                    .put(GeneralConstants.StructuredLogKeys.BATCH_ID, smsObject.getBatchId())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getMsgid())
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, smsObject.getSMS().getDstMsisdn())
                                    .build());
                            sender_deliverResp_Q.put(smsObject);
                        }
                    } else//enq message again in case not connected to SMSC
                    {
                        CommonLogger.businessLogger.info("Enqueuing message again in java queue for resending not connected to smsc");
                        sender_deliverResp_Q.put(smsObject);
                    }
                } else {//sms in queue is null
                    nullMsgCounter++;
                    if (nullMsgCounter == this.smppSession.getSmppApp().getThreshold()) {
                        long sleepTime = Long.valueOf((String) Defines.fileConfigurations.get(Defines.DELIVERRESP_THREAD_SLEEPING_TIMEOUT));
//                        CommonLogger.businessLogger.info("DeliverResponseThread is going to sleep for " + sleepTime + " msecs as thread polled null sms with same threshold size:" + this.smppSession.getSmppApp().getThreshold());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DeliverResponseThread is going to sleep, as thread polled null with same threshold")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime)
                                .put(GeneralConstants.StructuredLogKeys.THRESHOLD_SIZE, this.smppSession.getSmppApp().getThreshold()).build());
                        Thread.sleep(sleepTime);
                        nullMsgCounter = 0;
                    }
                }
            } catch (InterruptedException ex) {
                // CommonLogger.businessLogger.error("DeliverResponse Thread Caugth InterruptedException--->" + ex);
                CommonLogger.errorLogger.error("DeliverResponse Thread Caugth InterruptedException--->" + ex, ex);
            } catch (Exception ex) {
                // CommonLogger.businessLogger.error("DeliverResponse Thread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("DeliverResponse Thread Caugth Exception--->" + ex, ex);
            }
        }
//        CommonLogger.businessLogger.info("*****************DELIVER RESPONSE THREAD FINISHED FOR APP_ID:" + smppSession.getSmppApp().getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Deliver Response Thread Finished")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, smppSession.getSmppApp().getAppName()).build());
    }

}
