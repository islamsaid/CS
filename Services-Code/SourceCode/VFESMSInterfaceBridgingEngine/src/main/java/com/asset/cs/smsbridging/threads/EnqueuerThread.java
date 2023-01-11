/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.cs.smsbridging.models.HTTPMsgResult;
import com.asset.cs.smsbridging.models.HTTPResponse;
import com.asset.cs.smsbridging.models.QueueNeedsHolder;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author mostafa.kashif
 */
public class EnqueuerThread extends Thread {

    private int threadNo;
    private QueueNeedsHolder queueHolder;

    public EnqueuerThread(int threadNo, QueueNeedsHolder queueHolder) {
        this.threadNo = threadNo;
        this.queueHolder = queueHolder;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Enqueuer_" + threadNo + "_" + queueHolder.getModel().getAppName());
//        CommonLogger.businessLogger.debug("*****************ENQUEUER THREAD STARTED WITH NAME:" + Thread.currentThread().getName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueuer Thread Started").build());

        ArrayList<SMSBridge> enqueueMsgList = new ArrayList<>();
        while ((!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.get() || !Manager.submitterEnqueuerQueueMap.get(queueHolder.getModel().getAppName()).isEmpty() || !Manager.queueForJSONRequests.isEmpty() || ((ThreadPoolExecutor) Manager.httpSubmitterPool).getActiveCount() > 0) && (!queueHolder.isShutDown() || !Manager.submitterEnqueuerQueueMap.get(queueHolder.getModel().getAppName()).isEmpty() || !Manager.queueForJSONRequests.isEmpty() || ((ThreadPoolExecutor) Manager.httpSubmitterPool).getActiveCount() > 0)) {
            try {
                ArrayList<SMSBridge> smsBridgeList = Manager.submitterEnqueuerQueueMap.get(queueHolder.getModel().getAppName()).poll();
                if (smsBridgeList != null && smsBridgeList.size() > 0) {

                    if (SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.CALL_SEND_SMS_INTERFACE_FLAG_PROPERTY).equalsIgnoreCase("true")) {
                        enqueueMsgList.add(smsBridgeList.get(0));

                        if (enqueueMsgList.size() == Integer.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_BATCH_SIZE))) {
//                            CommonLogger.businessLogger.info("Starting Enqueuing messages list size: " + enqueueMsgList.size() + "with information:"+Utility.convertSMSBridgeListToString(enqueueMsgList).toString());
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Enqueuing Messages")
                                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, enqueueMsgList.size())
                                    .put(GeneralConstants.StructuredLogKeys.SMS_INFO, Utility.convertSMSBridgeListToString(enqueueMsgList).toString()).build());
                            Utility.sendRestRequestWithRetries(Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_HTTP_HITS)), SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_SMS_REST_URL), "Mozilla/5.0", Utility.gsonObjectToJSONStringWithDateFormat(enqueueMsgList, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"), "application/json;charset=UTF-8", "queueName=" + queueHolder.getModel().getAppName() + "&requestId=" + Utility.generateTransId("BRDGENQSMS"), "POST", Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_WEB_SERVICE_CONNECT_TIMEOUT)), Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_WEB_SERVICE_READ_TIMEOUT)), CommonLogger.businessLogger);
                            enqueueMsgList.clear();
                            // CommonLogger.businessLogger.info("Successfully Enqueued messages list size: " + smsBridgeList.size());
                        }
                    } else {
//                        CommonLogger.businessLogger.info("Starting Enqueuing messages list size: " + smsBridgeList.size() + "with information:" + Utility.convertSMSBridgeListToString(smsBridgeList).toString());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Enqueuing Messages")
                                .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, enqueueMsgList.size())
                                .put(GeneralConstants.StructuredLogKeys.SMS_INFO, Utility.convertSMSBridgeListToString(enqueueMsgList).toString()).build());
                        Utility.sendRestRequestWithRetries(Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_HTTP_HITS)), SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_SMS_REST_URL), "Mozilla/5.0", Utility.gsonObjectToJSONStringWithDateFormat(smsBridgeList, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"), "application/json;charset=UTF-8", "queueName=" + queueHolder.getModel().getAppName() + "&requestId=" + Utility.generateTransId("BRDGENQSMS"), "POST", Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_WEB_SERVICE_CONNECT_TIMEOUT)), Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_WEB_SERVICE_READ_TIMEOUT)), CommonLogger.businessLogger);
                    }
//                    CommonLogger.businessLogger.info("Successfully Enqueued messages list size: " + smsBridgeList.size());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Enqueued Messages")
                            .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, enqueueMsgList.size()).build());
                } else {

                    try {
                        if (SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.CALL_SEND_SMS_INTERFACE_FLAG_PROPERTY).equalsIgnoreCase("true")) {
                            if (enqueueMsgList.size() > 0) {
//                                CommonLogger.businessLogger.info("Starting Enqueuing messages list size: " + enqueueMsgList.size() + "with information:" + Utility.convertSMSBridgeListToString(enqueueMsgList).toString());
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Enqueuing Messages")
                                        .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, enqueueMsgList.size())
                                        .put(GeneralConstants.StructuredLogKeys.SMS_INFO, Utility.convertSMSBridgeListToString(enqueueMsgList).toString()).build());
                                Utility.sendRestRequestWithRetries(Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_HTTP_HITS)), SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_SMS_REST_URL), "Mozilla/5.0", Utility.gsonObjectToJSONStringWithDateFormat(enqueueMsgList, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"), "application/json;charset=UTF-8", "queueName=" + queueHolder.getModel().getAppName() + "&requestId=" + Utility.generateTransId("BRDGENQSMS"), "POST", Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_WEB_SERVICE_CONNECT_TIMEOUT)), Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_WEB_SERVICE_READ_TIMEOUT)), CommonLogger.businessLogger);
                                enqueueMsgList.clear();
//                                CommonLogger.businessLogger.info("Successfully Enqueued messages list size: " + enqueueMsgList.size());
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Enqueued Messages")
                                        .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, enqueueMsgList.size()).build());
                            }
                        }
//                        CommonLogger.businessLogger.info("ENQUEUER Thread " + Thread.currentThread().getName() + " is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUER_THREAD_SLEEP_TIME) + " msecs ");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueuer Thread is sleeping")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUER_THREAD_SLEEP_TIME)).build());
                        Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUER_THREAD_SLEEP_TIME)));
                    } catch (InterruptedException ex) {
                        CommonLogger.businessLogger.error("ENQUEUER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                        CommonLogger.errorLogger.error("ENQUEUER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
                    }
                }
            } catch (CommonException ex) {
                if (ex.getJavaErrorMessage().contains("Read timed out") || ex.getErrorCode().equals(ErrorCodes.HTTP_TIMEOUT_CONNECTION_ERROR)) {
                    CommonLogger.businessLogger.info("CommonException returned :" + ex.getJavaErrorMessage() + " so no retries will be handled and messages will  be discarded");
                    enqueueMsgList.clear();
                } else {
                    CommonLogger.businessLogger.info("CommonException returned :" + ex.getErrorMsg() + " so no retries will be handled and messages will  be discarded");
                    enqueueMsgList.clear();
                }
                CommonLogger.businessLogger.error("ENQUEUER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("ENQUEUER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("ENQUEUER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("ENQUEUER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
            }
        }
//        CommonLogger.businessLogger.info("*****************ENQUEUER " + Thread.currentThread().getName() + "   THREAD FINISHED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueuer Thread Finished").build());
    }

}
