package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DequeuerResponseModel;
import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.common.models.SMSBridgeDBObject;
import com.asset.contactstrategy.common.models.SMSBridgeJSONStructure;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.cs.smsbridging.models.HTTPMsgResult;
import com.asset.cs.smsbridging.models.HTTPResponse;
import com.asset.cs.smsbridging.models.QueueNeedsHolder;
import com.asset.cs.smsbridging.models.ServiceNeedsHolder;
import com.asset.cs.smsbridging.services.SMSBridgingMainService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author aya.moawed 2595
 *
 * Dequeuer thread is initialized per stored procedure queue .. it dequeues a
 * batch of smss from this queue .. for each sms an object will be prepared for
 * json converting then added to the java queue of the service of this sms
 */
public class DequeuerThread extends Thread {

    private ArrayList<SMSBridge> smsObjectsList;
    private HashMap<String, ArrayList<SMSBridge>> smsListPerService;
    private final QueueNeedsHolder queueHolder;
    private int dequeueWaitTime;
    private final int threadNumber;

    public DequeuerThread(QueueNeedsHolder queue, int threadNumber, int dequeueWaitTime) {
        this.queueHolder = queue;
        this.dequeueWaitTime = dequeueWaitTime;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("dequeuer_" + queueHolder.getModel().getAppName() + "_" + this.threadNumber);
//        CommonLogger.businessLogger.info("*****************DEQEUEUR THREAD STARTED WITH NAME " + Thread.currentThread().getName() + " FOR APP_QUEUE_NAME:" + queueHolder.getModel().getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Thread Started")
                .put(GeneralConstants.StructuredLogKeys.APP_NAME, queueHolder.getModel().getAppName()).build());
        SMSBridgingMainService mainService = new SMSBridgingMainService();
        while (!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.get() && !queueHolder.isShutDown()) {

            try {
                smsObjectsList = null;//contains dequeued smss
                smsListPerService = new HashMap<>();//dequeued smss grouped by service
                //Update Information in queue needs holder
                QueueNeedsHolder temp = Manager.workingAppQueues.get(this.queueHolder.getModel().getAppName());
                if (temp == null) {
                    throw new CommonException("THE QEQUEUER THREAD " + Thread.currentThread().getName() + " IS HOLDING A NON EXISTING QUEUE ..", ErrorCodes.GENERAL_ERROR);
                }
                queueHolder.setModel(temp.getModel());
                queueHolder.setShutDown(temp.isShutDown());
                long beginTime = System.currentTimeMillis();
                //Dequeue Batch 
//                CommonLogger.businessLogger.info("Dequeuing batch with size " + queueHolder.getModel().getThreshold() + " while waiting for " + dequeueWaitTime + " msec for queue with APP_NAME = " + queueHolder.getModel().getAppName());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeueing Batch")
                        .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, queueHolder.getModel().getThreshold())
                        .put(GeneralConstants.StructuredLogKeys.WAIT_TIME, dequeueWaitTime)
                        .put(GeneralConstants.StructuredLogKeys.APP_NAME, queueHolder.getModel().getAppName()).build());
                //smsObjectsList = mainService.dequeueBatch(queueHolder.getConnection(), queueHolder.getModel().getAppName(), queueHolder.getModel().getThreshold(), dequeueWaitTime, threadNumber);
                //////////////////////////                
                String response = Utility.sendRestRequestWithRetries(Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_HTTP_HITS)), SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.DEQUEUE_SMS_REST_URL), "Mozilla/5.0", null, "application/json;charset=UTF-8", "queueName=" + queueHolder.getModel().getAppName() + "&waitingTime=" + dequeueWaitTime + "&batchSize=" + queueHolder.getModel().getThreshold() + "&requestId=" + Utility.generateTransId("BRDGDEQSMS"), "GET", Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.DEQUEUE_WEB_SERVICE_CONNECT_TIMEOUT)), Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.DEQUEUE_WEB_SERVICE_READ_TIMEOUT)), CommonLogger.businessLogger);
                DequeuerResponseModel responseModel = Utility.gsonJSONStringToDequeuerResponseModel(response);
                if (!responseModel.getCode().equals(String.valueOf(ErrorCodes.DEQUEUER_REST_WEB_SERVICE.SUCCESS))) {
//                    CommonLogger.businessLogger.info("Response returned from dequeeur web service code:" + responseModel.getCode() + " and description:" + responseModel.getDescription() + ",transId:" + responseModel.getTransId());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Response returned from Dequeuer Web Service")
                            .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, responseModel.getCode())
                            .put(GeneralConstants.StructuredLogKeys.RESPONSE_DESCRIPTION, responseModel.getDescription())
                            .put(GeneralConstants.StructuredLogKeys.TRANS_ID, responseModel.getTransId()).build());
                    if (responseModel.getDescription() != null) {
                        if (!responseModel.getDescription().contains("timeout or end-of-fetch during message dequeue")) {
                            throw new CommonException(responseModel.getCode(), responseModel.getDescription());
                        }
                    } else {
                        throw new CommonException(responseModel.getCode(), responseModel.getDescription());
                    }
                }
                smsObjectsList = responseModel.getSms();
//  if(outputMap.get)
                /////////////////////////////
                long endTime = System.currentTimeMillis();
//                CommonLogger.businessLogger.info("Dequeuing done in " + (endTime - beginTime) + " msec");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuing is done")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (endTime - beginTime)).build());
                //Put batch to queue 
                if (smsObjectsList != null && smsObjectsList.size() > 0) {
                    int httpBatchSize = Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.HTTP_BATCH_SIZE));
                    if (SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.CALL_SEND_SMS_INTERFACE_FLAG_PROPERTY).equalsIgnoreCase("true")) {
                        httpBatchSize = 1;
                    }
//                    CommonLogger.businessLogger.info("HTTP batch size is " + httpBatchSize);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "HTTP rquest stats")
                            .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, httpBatchSize).build());
//                    CommonLogger.businessLogger.info("Dequeued Batch with Total SIZE =" + smsObjectsList.size() + " SMSs and SMSs information:" + Utility.convertSMSBridgeListToString(smsObjectsList).toString());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeued Batch")
                            .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, smsObjectsList.size())
                            .put(GeneralConstants.StructuredLogKeys.SMS_INFO, Utility.convertSMSBridgeListToString(smsObjectsList).toString()).build());
                    for (SMSBridge smsObject : smsObjectsList) {
//                        CommonLogger.businessLogger.info("Dequeued SMS Bridge Object with information [" + smsObject.toString() + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeued SMS Bridge Object")
                                .put(GeneralConstants.StructuredLogKeys.SMS_INFO, smsObject.toString()).build());
                        if (smsListPerService.containsKey(smsObject.getServiceName())) {
                            if (smsListPerService.get(smsObject.getServiceName()).size() < httpBatchSize) {
                                smsListPerService.get(smsObject.getServiceName()).add(smsObject);
                            } else {
                                //service list is full add .. to queue and create new list
                                addToServicesQueue(smsObject.getServiceName());
                                smsListPerService.remove(smsObject.getServiceName());
                                smsListPerService.put(smsObject.getServiceName(), new ArrayList<SMSBridge>());
                                smsListPerService.get(smsObject.getServiceName()).add(smsObject);
                            }
                        } else {
                            smsListPerService.put(smsObject.getServiceName(), new ArrayList<SMSBridge>());
                            smsListPerService.get(smsObject.getServiceName()).add(smsObject);
                        }

                    }
                    //put sms in java queue of service if it exists
                    Iterator<String> i = smsListPerService.keySet().iterator();
                    while (i.hasNext()) {
                        String serviceName = i.next();
                        addToServicesQueue(serviceName);
                    }
                } else {
                    //CommonLogger.businessLogger.info("Dequeuer " + Thread.currentThread().getName() + " dequeued batch with size:0");
                    try {
//                        CommonLogger.businessLogger.info("Dequeuer Thread " + Thread.currentThread().getName() + " is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME) + " msecs ");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Thread is Going to Sleep")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME)).build());
                        Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME)));
                    } catch (InterruptedException ex) {
                        CommonLogger.businessLogger.error("DequeuerThread Thread Caught Exception--->" + ex);
                        CommonLogger.errorLogger.error("DequeuerThread Thread Caught Exception--->" + ex, ex);
                    }
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error("DequeuerThread  " + Thread.currentThread().getName() + "  caught CommonException--->" + ex + " and message: " + ex.getJavaErrorMessage());
                CommonLogger.errorLogger.error("DequeuerThread  " + Thread.currentThread().getName() + "  caught CommonException--->" + ex, ex);
                try {
//                        CommonLogger.businessLogger.info("Dequeuer Thread " + Thread.currentThread().getName() + " is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME) + " msecs ");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Thread is Going to Sleep")
                            .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME)).build());
                    Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME)));
                } catch (InterruptedException ex1) {
                    CommonLogger.businessLogger.error("DequeuerThread Thread Caught Exception--->" + ex1);
                    CommonLogger.errorLogger.error("DequeuerThread Thread Caught Exception--->" + ex1, ex1);
                }
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("DequeuerThread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("DequeuerThread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
                try {
//                        CommonLogger.businessLogger.info("Dequeuer Thread " + Thread.currentThread().getName() + " is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME) + " msecs ");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Thread is Going to Sleep")
                            .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME)).build());
                    Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME)));
                } catch (InterruptedException ex1) {
                    CommonLogger.businessLogger.error("DequeuerThread Thread Caught Exception--->" + ex1);
                    CommonLogger.errorLogger.error("DequeuerThread Thread Caught Exception--->" + ex1, ex1);
                }
            } finally {

            }
        }
//        CommonLogger.businessLogger.info("*****************DEQEUEUR THREAD " + Thread.currentThread().getName() + " FINISHED FOR APP_QUEUE_NAME:" + queueHolder.getModel().getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Dequeuer Thread Finished")
                .put(GeneralConstants.StructuredLogKeys.APP_NAME, queueHolder.getModel().getAppName()).build());
    }

    private void addToServicesQueue(String serviceName) throws Exception {
        SMSBridgeJSONStructure smsToJSONWrapper = new SMSBridgeJSONStructure();
        smsToJSONWrapper.setServiceName(serviceName);
        smsToJSONWrapper.setSms(smsListPerService.get(serviceName));
        ServiceNeedsHolder holder = Manager.workingServices.get(serviceName);
        if (holder != null && !holder.isShutDown()) {
            Manager.workingServices.get(serviceName).getQueueForDequeuerResult().put(smsToJSONWrapper);
        } else {
            CommonLogger.businessLogger.info("Service with Name [" + serviceName + "] was removed or does not exist.. List of SMSs of size " + smsToJSONWrapper.getSms().size() + " will be ignored");
            // mainService.enqueueMsg(smsListPerService.get(serviceName), queueHolder.getModel().getAppName(), queueHolder.getConnection());

            ArrayList<HTTPMsgResult> httpMsgResults = new ArrayList<HTTPMsgResult>();
            ArrayList<Long> smsMsgIds = new ArrayList<Long>();
            for (int i = 0; i < smsListPerService.get(serviceName).size(); i++) {

                HTTPMsgResult httpMsgResult = new HTTPMsgResult();
                httpMsgResult.setMsgID(Long.valueOf(-1));
                httpMsgResult.setQueueName(smsListPerService.get(serviceName).get(i).getQueueName());
                httpMsgResult.setMsisdnLastTwoDigits(Utility.getMsisdnModX(smsListPerService.get(serviceName).get(i).getDestinationMSISDN()));
                httpMsgResult.setSubmissionDate(smsListPerService.get(serviceName).get(i).getSubmissionDate());
                httpMsgResult.setStatus("Service with Name [" + serviceName + "] was removed or does not exist");
                smsMsgIds.add(smsListPerService.get(serviceName).get(i).getMsgId());
                httpMsgResults.add(httpMsgResult);
            }
            HTTPResponse httpResponse = new HTTPResponse();
            httpResponse.setSmsMsgIds(smsMsgIds);
            httpResponse.setSms(httpMsgResults);
            Manager.queueForHTTPResult.put(httpResponse);
        }
    }
}
