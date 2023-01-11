package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.cs.smsbridging.models.HTTPMsgResult;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.common.models.SMSBridgeDBObject;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.smsbridging.models.HTTPResponse;
import com.asset.cs.smsbridging.services.SMSBridgingMainService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aya.moawed
 */
public class HTTPSubmitterThread extends Thread {

    private int threadNo;
    private int maxHTTPHitTimes;
    private String url;
    private String getRequestUrl;
    private int connTimeOut;
    private int readTimeOut;
    private RequestPreparator json;

    public HTTPSubmitterThread(int threadNo, int maxHTTPHitTimes) {
        this.threadNo = threadNo;
        this.maxHTTPHitTimes = maxHTTPHitTimes;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("httpsubmitter" + threadNo);

        String queueName = null;
        String test = "";
//        CommonLogger.businessLogger.debug("*****************HTTP SUBMITTER THREAD STARTED WITH NAME:" + Thread.currentThread().getName() + "***********************");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "HTTP Submitter Thread Started").build());
        while (!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.get() || !Manager.queueForJSONRequests.isEmpty() || !Manager.workingServices.isEmpty() || ((ThreadPoolExecutor) Manager.jsonConstructorPool).getActiveCount() > 0) {
            try {
                url = SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_URL);
                getRequestUrl = SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SEND_SMS_SINGLE_HTTP_URL);
                String connTimeOutStr = SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_CONN_TIME_OUT);
                String readTimeOutStr = SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_READ_TIME_OUT);
                if (url == null || url.trim().isEmpty()) {
                    throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.HTTP_SUBMITTER_STEP, "Invalid URL was provided");
                }
                try {
                    this.connTimeOut = Integer.parseInt(connTimeOutStr);
                    this.readTimeOut = Integer.parseInt(readTimeOutStr);
                } catch (Exception ex) {
                    throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.HTTP_SUBMITTER_STEP, "Invalid Number Format for connection time out [" + connTimeOutStr + "]" + "or read time out [" + readTimeOutStr + "]");
                }

                json = Manager.queueForJSONRequests.poll();
                if (json != null) {
                    queueName = json.getSms().get(0).getQueueName();
//                    CommonLogger.businessLogger.info("Handling Batch with Total SIZE =" + json.getSms().size() + " SMSs and SMSs information:" + Utility.convertSMSBridgeListToString(json.getSms()).toString());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Handling Batch")
                            .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, json.getSms().size())
                            .put(GeneralConstants.StructuredLogKeys.SMS_INFO, Utility.convertSMSBridgeListToString(json.getSms()).toString()).build());

                    /*
                     Takes JSON result that contains the String  produced in the JSON Constructor thread and connect to Send SMS Bulk Offline Interface
                     */
                    String response;
                    String responseType;
                    long beginTime = System.currentTimeMillis();
                    if (SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.CALL_SEND_SMS_INTERFACE_FLAG_PROPERTY).equalsIgnoreCase("true")) {
//                        CommonLogger.businessLogger.info("Connecting to Send  Sms  Interface with information ..URL [" + getRequestUrl + "] .. Connection TimeOut [" + Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SINGLE_SMS_CONNECT_TIMEOUT)) + "] .. Read TimeOut [" + Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SINGLE_SMS_READ_TIMEOUT)) + "] .. Maximum Hit Times If Failed [" + maxHTTPHitTimes + "] .. Request Information [" + json.getSimpleRequest() + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Connecting to Send SMS Interface with Information")
                                .put(GeneralConstants.StructuredLogKeys.URL, getRequestUrl)
                                .put(GeneralConstants.StructuredLogKeys.CONNECTION_TIMEOUT, Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SINGLE_SMS_CONNECT_TIMEOUT)))
                                .put(GeneralConstants.StructuredLogKeys.READ_TIMEOUT, Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SINGLE_SMS_READ_TIMEOUT)))
                                .put(GeneralConstants.StructuredLogKeys.MAX_HITS, maxHTTPHitTimes)
                                .put(GeneralConstants.StructuredLogKeys.REQUEST_INFO, json.getSimpleRequest()).build());
                        response = Utility.sendRestRequestWithRetries(Integer.valueOf(SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.MAX_HTTP_HITS)), getRequestUrl, null, null, "text/plain", json.getSimpleRequest() + "&requestId=" + Utility.generateTransId("BRDGHTPPSMS"), "GET", Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SINGLE_SMS_CONNECT_TIMEOUT)), Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_SINGLE_SMS_READ_TIMEOUT)), CommonLogger.businessLogger);
                        responseType = "SimpleResponse";
                    } else {
//                        CommonLogger.businessLogger.info("Connecting to Send Bulk Sms Offline Interface with information ..URL [" + url + "] .. Connection TimeOut [" + connTimeOut + "] .. Read TimeOut [" + readTimeOut + "] .. Maximum Hit Times If Failed [" + maxHTTPHitTimes + "] .. JSON Information [" + json.toString() + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Connecting to Send Bulk SMS Interface with Information")
                                .put(GeneralConstants.StructuredLogKeys.URL, url)
                                .put(GeneralConstants.StructuredLogKeys.CONNECTION_TIMEOUT, connTimeOut)
                                .put(GeneralConstants.StructuredLogKeys.READ_TIMEOUT, readTimeOut)
                                .put(GeneralConstants.StructuredLogKeys.MAX_HITS, maxHTTPHitTimes)
                                .put(GeneralConstants.StructuredLogKeys.REQUEST_INFO, json.toString()).build());
                        response = Utility.submitSendBulkSMSHTTP(json, maxHTTPHitTimes, connTimeOut, readTimeOut, url + "?requestId=" + Utility.generateTransId("BRDGHTPPSMS"));
                        responseType = "Json";
                    }

                    long endTime = System.currentTimeMillis();
//                    CommonLogger.businessLogger.debug("Response done in " + (endTime - beginTime) + " msec");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Response Done")
                            .put(GeneralConstants.StructuredLogKeys.RESPONSE_TIME, (endTime - beginTime))
                            .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, response).build());
//                    CommonLogger.businessLogger.debug("Response  string [" + response + "]");

                    setHTTPResult(response, queueName, json.getSms(), responseType);
                } else {

                    try {
//                        CommonLogger.businessLogger.info("HTTP submitter Thread  " + Thread.currentThread().getName() + " is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_SUBMITTER_THREAD_SLEEP_TIME) + " msecs ");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "HTTP Submitter Thread is going to sleep")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_SUBMITTER_THREAD_SLEEP_TIME)).build());
                        Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_HTTP_SUBMITTER_THREAD_SLEEP_TIME)));
                    } catch (InterruptedException ex) {
                        CommonLogger.businessLogger.error("HTTP submitter Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                        CommonLogger.errorLogger.error("HTTP submitter Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
                    }
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error("HTTP submitter Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("HTTP submitter Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
                if (ex.getJavaErrorMessage().contains("Read timed out") || ex.getErrorCode().equals(ErrorCodes.HTTP_TIMEOUT_CONNECTION_ERROR)) {
                    try {
                        // enqueueMsgs(json, queueName);
                        //Utility.sendRestRequest(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_SMS_REST_URL), "Mozilla/5.0", Utility.gsonObjectToJSONStringWithDateFormat(json.getSms(),"yyyy-MM-dd'T'HH:mm:ss.SSSZ"), "application/json;charset=UTF-8", "queueName="+queueName, "POST",Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_WEB_SERVICE_CONNECT_TIMEOUT)),Integer.parseInt(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ENQUEUE_WEB_SERVICE_READ_TIMEOUT)),CommonLogger.businessLogger);
                        CommonLogger.businessLogger.info("CommonException returned :" + ex.getJavaErrorMessage() + " so no retries will be handled and messages will  be updated in database as read timed out");
                        HTTPResponse response = new HTTPResponse();
                        response.setSmsMsgIds(new ArrayList<Long>());
                        response.setSms(new ArrayList<HTTPMsgResult>());
                        for (SMSBridge smsBridge : json.getSms()) {
                            response.getSmsMsgIds().add(smsBridge.getMsgId());
                            HTTPMsgResult sms = new HTTPMsgResult();
                            sms.setMsgID(null);
                            sms.setQueueName(queueName);
                            sms.setStatus("Read time out exception while calling interface");
                            sms.setMsisdnLastTwoDigits(Utility.getMsisdnModX(smsBridge.getDestinationMSISDN()));
                            sms.setSubmissionDate(smsBridge.getSubmissionDate());
                            response.getSms().add(sms);
                        }
                        Manager.queueForHTTPResult.put(response);
                    } catch (Exception ex1) {
                        CommonLogger.businessLogger.error("HTTP submitter Thread  " + Thread.currentThread().getName() + "  caught JsonProcessingException--->" + ex1);
                        CommonLogger.errorLogger.error("HTTP submitter Thread  " + Thread.currentThread().getName() + "  caught JsonProcessingException--->" + ex1, ex1);

                    }
                } else {
                    if (json != null && json.getSms() != null && json.getSms().size() > 0) {
                        CommonLogger.businessLogger.info("Starting enqueuing messages again");
                        Manager.submitterEnqueuerQueueMap.get(queueName).add(json.getSms());
                    }
                }
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("HTTP submitter Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("HTTP submitter Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
            }
        }
//        CommonLogger.businessLogger.info("*****************HTTP Submitter " + Thread.currentThread().getName() + "   THREAD FINISHED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "HTTP Submitter Thread Finished").build());
    }

    /*
     Takes HTTP response json string and converts it into HTTP Response Object
     */
    private void setHTTPResult(String httpResult, String queueName, ArrayList<SMSBridge> sentSmss, String responseType) throws InterruptedException {
        HTTPResponse response = new HTTPResponse();
        response.setSmsMsgIds(new ArrayList<Long>());
        for (SMSBridge smsBridge : sentSmss) {
            response.getSmsMsgIds().add(smsBridge.getMsgId());
        }
        try {
            if (responseType.equalsIgnoreCase("json")) {
                HashMap<String, Object> hashMapResponse = Utility.gsonJSONStringToHashMapObject(httpResult);
                Object errorCode = hashMapResponse.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS_WRAPPER.ERROR_CODE);
                Object errorDescription = hashMapResponse.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS_WRAPPER.ERROR_DESCRIPTION);
                Object sMSs = hashMapResponse.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS_WRAPPER.SMSs);
                response.setErrorCode(((errorCode != null) ? errorCode.toString() : null));
                response.setErrorDescription(((errorDescription != null) ? errorDescription.toString() : null));
                response.setSms(new ArrayList<HTTPMsgResult>());
                //Check that the retrieved number of smss = the number of smss sent
                if (sentSmss == null || sMSs == null
                        || !(sMSs instanceof ArrayList)
                        || (sMSs instanceof ArrayList && ((ArrayList) sMSs).size() != sentSmss.size())) {
                    throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.HTTP_SUBMITTER_STEP, "retrieved number of smss != the number of smss sent for smss with Ids = ");
                }
                ArrayList<LinkedTreeMap<String, LinkedTreeMap<String, String>>> sMSsResponse = (ArrayList<LinkedTreeMap<String, LinkedTreeMap<String, String>>>) sMSs;
                for (LinkedTreeMap<String, LinkedTreeMap<String, String>> sMSResponse : sMSsResponse) {
                    LinkedTreeMap<String, String> smsProps = sMSResponse.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS_WRAPPER.SMS);
                    HTTPMsgResult sms = new HTTPMsgResult();
                    sms.setQueueName(queueName);
                    sms.setMsgID(Long.parseLong(smsProps.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.CS_MSG_ID)));
                    sms.setStatus(smsProps.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.MESSAGE_STATUS));
                    response.getSms().add(sms);
                }

            } else {

                HTTPMsgResult sms = new HTTPMsgResult();
                String[] httpKeys = httpResult.split(",");
                for (int i = 0; i < httpKeys.length; i++) {
                    if (((httpKeys[i].split("="))[0]).trim().equalsIgnoreCase("MessageId")) {
                        sms.setMsgID(Long.parseLong((httpKeys[i].split("="))[1].trim()));
                    } else if (((httpKeys[i].split("="))[0]).trim().equalsIgnoreCase(Defines.INTERFACES.ERROR_CODE)) {
                        response.setErrorCode((httpKeys[i].split("="))[1].trim());

                    } else if (((httpKeys[i].split("="))[0]).trim().equalsIgnoreCase(Defines.INTERFACES.ERROR_DESCRIPTION)) {
                        response.setErrorDescription((httpKeys[i].split("="))[1].trim());
                        sms.setStatus((httpKeys[i].split("="))[1].trim());
                    }
                    sms.setMsisdnLastTwoDigits(Utility.getMsisdnModX(sentSmss.get(0).getDestinationMSISDN()));
                    sms.setSubmissionDate(sentSmss.get(0).getSubmissionDate());
                }
                ArrayList<HTTPMsgResult> httpMsgResultList = new ArrayList<HTTPMsgResult>();
                httpMsgResultList.add(sms);
                response.setSms(httpMsgResultList);
            }
            Manager.queueForHTTPResult.put(response);
        } catch (Exception ex) {
            CommonLogger.businessLogger.debug("Exception occured while parsing response JSON String to HTTP Reaponse Object");
            response.setSms(new ArrayList<HTTPMsgResult>());
            for (SMSBridge failedResult : json.getSms()) {
                HTTPMsgResult sms = new HTTPMsgResult();
                sms.setMsgID(null);
                sms.setQueueName(queueName);
                sms.setStatus("Exception in response parsing occured [" + ex.getMessage() + "]");
                sms.setMsisdnLastTwoDigits(Utility.getMsisdnModX(failedResult.getDestinationMSISDN()));
                sms.setSubmissionDate(failedResult.getSubmissionDate());
                response.getSms().add(sms);
            }
            Manager.queueForHTTPResult.put(response);
        }
    }

//    private void enqueueMsgs(RequestPreparator json, String queueName) throws CommonException {
//        SMSBridgingMainService service = new SMSBridgingMainService();
//        service.enqueueMsg(json.getSms(), queueName, Manager.workingAppQueues.get(json.getSms().get(0).getQueueName()).getConnection());
//    }
}
