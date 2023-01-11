/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.threads;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModelREST;
import com.asset.contactstrategy.common.models.RESTResponseModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
import static com.asset.contactstrategy.rest.threads.ArchiveManagerThread.MAX_ARCHIVING_DB_ARRAY_SIZE;
import static com.asset.contactstrategy.rest.threads.ArchiveManagerThread.MAX_NUM_OF_RETRIES_ATHREAD;
import static com.asset.contactstrategy.rest.threads.ArchiveManagerThread.pullTimeOut;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author esmail.anbar
 */
public class EnQueueManagerThread implements Runnable {

    StringBuilder csMsgIds;
    private long queueId;
    private String queueName;
    private ArrayList<SmsBusinessModel> objectsArray;
    private int MAX_ENQUEUE_DB_ARRAY_SIZE = Defines.INTERFACES.MAX_ENQUEUE_DB_ARRAY_SIZE_VALUE;
    private int MAX_NUM_OF_RETRIES_QTHREAD = Defines.INTERFACES.MAX_NUM_OF_RETRIES_QTHREAD_VALUE;
    int pullTimeOut;
    int threadId;

    public EnQueueManagerThread(long queueId, String queueName, int threadId) {
        this.queueId = queueId;
        this.queueName = queueName;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("EnqueueThread_" + queueName + "_" + threadId);
        pullTimeOut = Defines.INTERFACES.ENQUEUE_THREAD_PULL_TIMEOUT_VALUE;
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + " has Started").build());

        while (!ConfigurationManager.shutdownFlagPool.get(queueId) || !ConfigurationManager.smsToBeSent.get(queueId).isEmpty()
                || ConfigurationManager.concurrentRequests.get() > 0 || !ConfigurationManager.smsToBeValidated.isEmpty()) {
            try {
                objectsArray = null;
                csMsgIds = new StringBuilder();

                createBatch();
                if (objectsArray != null && !objectsArray.isEmpty()) {
                    sendMessageBatch();
                }
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
                CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
            }
        }
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended || Queue Size: " + ConfigurationManager.smsToBeSent.get(queueId).size()
//                + " || Queue Remaining Capacity: " + ConfigurationManager.smsToBeSent.get(queueId).remainingCapacity()
//                + " || PullTimeOut: " + pullTimeOut + " || MAX_NUM_OF_RETRIES_QTHREAD: " + MAX_NUM_OF_RETRIES_QTHREAD
//                + " || MAX_ENQUEUE_DB_ARRAY_SIZE: " + MAX_ENQUEUE_DB_ARRAY_SIZE);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + " has Ended")
                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.smsToBeSent.get(queueId).size())
                .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.smsToBeSent.get(queueId).remainingCapacity())
                .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, pullTimeOut)
                .put(GeneralConstants.StructuredLogKeys.THREAD_MAX_RETRIES, MAX_NUM_OF_RETRIES_QTHREAD)
                .put(GeneralConstants.StructuredLogKeys.MAX_ENQUEUE_DB_ARRAY_SIZE, MAX_ENQUEUE_DB_ARRAY_SIZE).build());

        //CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }

    public void createBatch() throws InterruptedException {
        boolean firstTime = true;
        SmsBusinessModel smsObject;
        for (int i = 0; i < MAX_ENQUEUE_DB_ARRAY_SIZE; i++) {
            if (ConfigurationManager.smsToBeSent.get(queueId) == null) {
                break;
            } else {
                if (objectsArray == null || objectsArray.isEmpty()) {
                    smsObject = ConfigurationManager.smsToBeSent.get(queueId).poll(pullTimeOut, TimeUnit.MILLISECONDS);
                } else {
                    smsObject = ConfigurationManager.smsToBeSent.get(queueId).poll();
                }
            }
            //CSPhase1.5 | Esmail.Anbar | Adding Expiration Duration Update
            if (smsObject != null && (smsObject.getExpirationDate() == null || smsObject.getExpirationDate().getTime() > System.currentTimeMillis())) {
                if (firstTime) {
                    objectsArray = new ArrayList<>(MAX_ENQUEUE_DB_ARRAY_SIZE);
                    firstTime = false;
                }
                try {
                    if (i != 0) {
                        csMsgIds.append(",");
                    }
                    csMsgIds.append(smsObject.getSmsModel().getCsMsgId());
                } //                catch (SQLException ex) 
                //                {
                //                    CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Failed to get Object CsMsgId");
                //                    CommonLogger.businessLogger.error(ex.getMessage());
                //                    CommonLogger.errorLogger.error(ex.getMessage(), ex);
                //                }
                catch (Exception e) {
                    CommonLogger.businessLogger.error(e.getMessage());
                    CommonLogger.errorLogger.error(e.getMessage(), e);
                }
                objectsArray.add(smsObject);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Added msg to Enqueue Batch")
                        .put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsObject.getTransId())
                        .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getCsMsgId()).build());
            } else if (smsObject != null && smsObject.getExpirationDate() != null && smsObject.getExpirationDate().getTime() <= System.currentTimeMillis()) {
//                CommonLogger.businessLogger.info(Thread.currentThread().getName() + " || " + smsObject.getTransId() + " | csMsgId: " + smsObject.getCsMsgId() + " | Message Expired and adding it to RollBack Queue");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Expired and adding it to RollBack Queue")
                        .put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsObject.getTransId())
                        .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsObject.getCsMsgId()).build());
                smsObject.setRollbackStatus(Defines.VFE_CS_SMS_H_STATUS_LK.EXPIRED);
                ConfigurationManager.smsToBeRollbacked.put(smsObject);
            } else {
                break;
            }
        }
    }

    public void sendMessageBatch() {
//        Connection conn = null;
        boolean rollBack = false;
        try {
            for (int i = 0; i < MAX_NUM_OF_RETRIES_QTHREAD; i++) {
                try {
                    ArrayList<EnqueueModelREST> enqueueModels = new ArrayList<>();

                    for (SmsBusinessModel smsModel : objectsArray) {
                        enqueueModels.add(smsModel.getSmsModel());
                    }

                    String enqueueModelsJSON = Utility.gsonObjectToJSONStringWithDateFormat(enqueueModels, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    String url = ConfigurationManager.SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.ENQUEUE_SMS_REST_URL);
                    String USER_AGENT = "VFEContactStrategyREST";
                    String contentType = "application/json;charset=UTF-8";
//                    CommonLogger.businessLogger.info("Enqueue Models JSON: " + enqueueModelsJSON);
//                    String enqueueResponse = Utility.sendRestRequest(url, USER_AGENT, enqueueModelsJSON, contentType, "queueName=" + queueName + "&csMsgIds=" + csMsgIds + "&requestId=" + Utility.generateTransId("SENDSMSENQ"), "POST", ConfigurationManager.CONNECTION_TIMEOUT, ConfigurationManager.READ_TIMEOUT, CommonLogger.businessLogger);
                    String requestId = Utility.generateTransId("SENDSMSENQ");
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, requestId);
//                    CommonLogger.businessLogger.trace("Enqueue Models JSON " + enqueueModelsJSON);
                    CommonLogger.businessLogger.trace(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueue Models JSON")
                            .put(GeneralConstants.StructuredLogKeys.ENQUEUE_MODEL, enqueueModels).build());
//                    CommonLogger.businessLogger.info("RequestId: " + requestId + " | MsgIds: " + csMsgIds);
                    CommonLogger.businessLogger.trace(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Listing Msg Ids to be enqueued")
                            .putCSV(GeneralConstants.StructuredLogKeys.MSG_IDS, csMsgIds.toString()).build());
                    String enqueueResponse = Utility.sendRestRequest(url, USER_AGENT, enqueueModelsJSON, contentType, "queueName=" + queueName + "&csMsgIds=" + csMsgIds + "&requestId=" + requestId, "POST", ConfigurationManager.CONNECTION_TIMEOUT, ConfigurationManager.READ_TIMEOUT, CommonLogger.businessLogger);

                    RESTResponseModel responseModel = Utility.gsonJSONStringToRESTResponseModel(enqueueResponse);
//                    CommonLogger.businessLogger.info("Enqueue SMS REST Response: " + enqueueResponse);
                    CommonLogger.businessLogger.trace(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueue SMS REST Response")
                            .put(GeneralConstants.StructuredLogKeys.RESPONSE_DESCRIPTION, responseModel).build());
                    if (!responseModel.getCode().equalsIgnoreCase("0")) {
                        throw new Exception(responseModel.getDescription());
                    }
                    rollBack = false;
                    break;
                } catch (CommonException ce) {
                    CommonLogger.businessLogger.error(ce.getErrorMsg());
                    CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
                    if (i == MAX_NUM_OF_RETRIES_QTHREAD - 1) {
                        rollBack = true;
                    }
                    if (ce.getErrorMsg().contains("Read timed out")) {
                        rollBack = false;
                        break;
                    }
                } catch (Exception e) {
                    CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
                    CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
                    if (i == MAX_NUM_OF_RETRIES_QTHREAD - 1) {
                        rollBack = true;
                    }
                } finally {
                    ThreadContext.remove(GeneralConstants.StructuredLogKeys.TRANS_ID);
                }
            }

            if (rollBack) {
                for (SmsBusinessModel sms : objectsArray) {
                    sms.setRollbackStatus(Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE);
                    ConfigurationManager.smsToBeRollbacked.put(sms);
                }
            }
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
            CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
        }
    }
}
