package com.asset.contactstrategy.enqueue.rest.api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModelREST;
import com.asset.contactstrategy.common.models.RESTResponseModel;
import com.asset.contactstrategy.common.models.SMS;
import com.asset.contactstrategy.common.service.DBQueueService;
import com.asset.contactstrategy.common.service.RabbitmqQueueService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.enqueue.rest.common.ConfigurationManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author Esmail.Anbar
 */
@Path("/EnqueueSMS")
public class EnqueueSMS {

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON)
    public RESTResponseModel processRequest(ArrayList<EnqueueModelREST> enqueueModels,
            @QueryParam("queueName") String queueName, @QueryParam("csMsgIds") StringBuilder csMsgIds,
            @QueryParam("requestId") String requestId, @Context HttpServletRequest httpRequest) {
        Timestamp requestTime = new Timestamp(System.currentTimeMillis());
        String transId = Utility.generateTransId("ENQSMS");
//        CommonLogger.businessLogger.info(transId + " | Started Enqueue SMS Request");
//        CommonLogger.businessLogger.info(transId + " | Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet()
//                + " | Enqueue SMS Accumlator Count: " + ConfigurationManager.enqueueSmsAccumulator.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started EnqueueSMS Request")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.enqueueSmsAccumulator.incrementAndGet()).build());
        RESTResponseModel response = new RESTResponseModel();
        Connection conn = null;
        SMS[] dbArray;
        SMS smsModel;
        try {
            ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
            if (requestId != null && !requestId.isEmpty()) {
//                CommonLogger.businessLogger.info("Request Id Recieved With Value: " + requestId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Request Id Recieved")
                        .put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId).build());
                ThreadContext.put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId);
            }
            if (enqueueModels == null || enqueueModels.isEmpty()) {
                throw new CommonException("Enqueue Models ArrayList is Null or Empty", 0);
            } else if (queueName == null || queueName.isEmpty()) {
                throw new CommonException("Queue Name is Null or Empty", 0);
            } else if (!ConfigurationManager.senderQueuesDatabaseConnectionPool.containsKey(queueName)) {
                throw new CommonException("Queue Name is Not an Existing Queue '" + queueName + "'", 0);
            }

//            CommonLogger.businessLogger.info(transId + " | All inputs are valid");
//
//            CommonLogger.businessLogger.info(transId + " | Getting connection for SmsQueue: " + queueName);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "All inputs are valiid. Getting Connection for SMSQueue")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
            conn = ConfigurationManager.senderQueuesDatabaseConnectionPool.get(queueName).getConnection();

            dbArray = new SMS[enqueueModels.size()];
            int i = 0;
            for (EnqueueModelREST enqueueModel : enqueueModels) {
//                CommonLogger.businessLogger.info("Request body: {" + enqueueModel.toString() + "}");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "EnqueueSMS Request Body")
                        .put(GeneralConstants.StructuredLogKeys.ENQUEUE_MODEL, enqueueModel.toString()).build());

                BigDecimal seq_id = BigDecimal.valueOf(enqueueModel.getCsMsgId());
                String appName = String.valueOf(enqueueModel.getQueueAppName());
                String origMsisdn = enqueueModel.getOriginatorMSISDN();
                String dstMsisdn = enqueueModel.getDestinationMSISDN();
                String msgTxt = enqueueModel.getMessageText();
                BigDecimal msgType = BigDecimal.valueOf(enqueueModel.getMessageType());
                BigDecimal origType = BigDecimal.valueOf(enqueueModel.getOriginatorType());
                BigDecimal langId = BigDecimal.valueOf(enqueueModel.getLanguage());
                BigDecimal nbtrials = BigDecimal.ZERO;
                BigDecimal concMsgSequeunce = BigDecimal.ZERO;
                BigDecimal concMsgCount = BigDecimal.valueOf(enqueueModel.getConcMsgCount());
                BigDecimal concSarRefNum = BigDecimal.ZERO;
                String ipAddress = enqueueModel.getIpAddress();
                BigDecimal deliveryRequest = BigDecimal.valueOf(enqueueModel.getDeliveryReport());
                String optionalParameter1 = enqueueModel.getOptionalParam1();
                String optionalParameter2 = enqueueModel.getOptionalParam2();
                String optionalParameter3 = enqueueModel.getOptionalParam3();
                String optionalParameter4 = enqueueModel.getOptionalParam4();
                String optionalParameter5 = enqueueModel.getOptionalParam5();
                Timestamp submissionDate = enqueueModel.getSubmissionDate() == null ? requestTime : new Timestamp(enqueueModel.getSubmissionDate().getTime());
                BigDecimal expirationHours = BigDecimal.valueOf(enqueueModel.getExpirationHours());
                String tlvOptionalParams = Utility.gsonObjectToJSONString(enqueueModel.getTlvs());
                String inRequestId = enqueueModel.getRequestId();
                String serviceType = enqueueModel.getServiceType();
                BigDecimal esmClass = enqueueModel.getEsmClass() == null ? null : BigDecimal.valueOf(enqueueModel.getEsmClass());
                BigDecimal protocolId = enqueueModel.getProtocolId() == null ? null : BigDecimal.valueOf(enqueueModel.getProtocolId());
                BigDecimal priorityFlag = enqueueModel.getPriorityFlag() == null ? null : BigDecimal.valueOf(enqueueModel.getPriorityFlag());
                String scheduleDeliveryTime = enqueueModel.getScheduleDeliveryTime();
                String validityPeriod = enqueueModel.getValidityPeriod();
                BigDecimal smDefaultMsgId = enqueueModel.getSmDefaultMsgId() == null ? null : BigDecimal.valueOf(enqueueModel.getSmDefaultMsgId());

                smsModel = new SMS(seq_id,
                        appName,
                        origMsisdn,
                        dstMsisdn,
                        msgTxt,
                        msgType,
                        origType,
                        langId,
                        nbtrials,
                        concMsgSequeunce,
                        concMsgCount,
                        concSarRefNum,
                        ipAddress,
                        deliveryRequest,
                        optionalParameter1,
                        optionalParameter2,
                        optionalParameter3,
                        optionalParameter4,
                        optionalParameter5,
                        submissionDate,
                        expirationHours,
                        tlvOptionalParams,
                        inRequestId,
                        serviceType,
                        esmClass,
                        protocolId,
                        priorityFlag,
                        scheduleDeliveryTime,
                        validityPeriod,
                        smDefaultMsgId);

                dbArray[i] = smsModel;
                i++;
            }
//            CommonLogger.businessLogger.info(transId + " | Batch Ready for Enqueue in SmsQueue: " + queueName);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Batch Ready for Enqueue in SMSQueue")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());

            Timestamp databaseProcessTime = new Timestamp(System.currentTimeMillis());

            if (Defines.MESSAGING_MODE.equals(Defines.RABBITMQ)) {
                new RabbitmqQueueService().enqueueBatch(enqueueModels, queueName);
            } else if (Defines.MESSAGING_MODE.equals(Defines.ORCLAQ)) {
                new DBQueueService().sendMessageBatch(conn, dbArray, queueName, csMsgIds);
            }

//            CommonLogger.businessLogger.info(transId + " | Enqueued Batch of: " + dbArray.length + " in SmsQueue: " + queueName + " | Time: " + (System.currentTimeMillis() - databaseProcessTime.getTime()) + " msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSQueue Stats")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, dbArray.length)
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - databaseProcessTime.getTime()))
                    .putCSV(GeneralConstants.StructuredLogKeys.MSG_IDS, csMsgIds.toString()).build());
            ConfigurationManager.senderQueuesDatabaseConnectionPool.get(queueName).commitConnection(conn);
//            CommonLogger.businessLogger.info(transId + " | Commited Connection for SmsQueue: " + queueName);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Commited Connection for SMSQueue")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
            response.setCode("0");
            response.setDescription("Enqueued: " + csMsgIds);
            response.setTransId(transId);
//            CommonLogger.businessLogger.info(transId + " | Enqueue SMS Success Count: " + ConfigurationManager.enqueueSmsSuccessCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "EnqueueSMS Successful")
                    .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.enqueueSmsSuccessCount.incrementAndGet()).build());
        } catch (CommonException ce) {
            CommonLogger.businessLogger.error(ce.getErrorMsg());
            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
            try {
                if (ConfigurationManager.senderQueuesDatabaseConnectionPool.containsKey(queueName)) {
                    ConfigurationManager.senderQueuesDatabaseConnectionPool.get(queueName).rollBack(conn);
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error(ex.getErrorMsg());
                CommonLogger.errorLogger.error(ex.getErrorMsg(), ex);
            }
            response.setCode("" + ce.getErrorId());
            response.setDescription("Exception Occured: " + ce.getErrorMsg());
            response.setTransId(transId);
//            CommonLogger.businessLogger.info(transId + " | Enqueue SMS Failed Count: " + ConfigurationManager.enqueueSmsFailedCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "EnqueueSMS Failed")
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.enqueueSmsFailedCount.incrementAndGet()).build());
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            try {
                if (ConfigurationManager.senderQueuesDatabaseConnectionPool.containsKey(queueName)) {
                    ConfigurationManager.senderQueuesDatabaseConnectionPool.get(queueName).rollBack(conn);
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error(ex.getErrorMsg());
                CommonLogger.errorLogger.error(ex.getErrorMsg(), ex);
            }
            response.setCode("-3000");
            response.setDescription("UNKOWN_ERROR");
            response.setTransId(transId);
//            CommonLogger.businessLogger.info(transId + " | Enqueue SMS Failed Count: " + ConfigurationManager.enqueueSmsFailedCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "EnqueueSMS Failed")
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.enqueueSmsFailedCount.incrementAndGet()).build());
        } finally {
            try {
                if (ConfigurationManager.senderQueuesDatabaseConnectionPool.containsKey(queueName)) {
                    ConfigurationManager.senderQueuesDatabaseConnectionPool.get(queueName).closeConnection(conn);
                }
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }

            String decodedURL = "";
            try {
                if (httpRequest.getQueryString() != null && !httpRequest.getQueryString().isEmpty()) {
                    decodedURL = URLDecoder.decode(httpRequest.getRequestURL().toString() + httpRequest.getQueryString(), "UTF-8");
                } else {
                    decodedURL = URLDecoder.decode(httpRequest.getRequestURL().toString(), "UTF-8");
                }
            } catch (Exception e) {
                decodedURL = httpRequest.getRequestURL().toString();
            }
            long requestTimeLong = (System.currentTimeMillis() - requestTime.getTime());
            String enqueueModelsString;
            if (enqueueModels != null) {
                enqueueModelsString = enqueueModels.toString();
            } else {
                enqueueModelsString = null;
            }
            String body = "enqueueModels: " + enqueueModelsString + " | QueueName: " + queueName + " | csMsgIds: " + csMsgIds;
            Utility.logRESTResult(transId, body, decodedURL, response.toString(), response.getDescription(), requestTimeLong, Defines.REST.ENQUEUE_SMS.NAME, ConfigurationManager.loggingQueue);

//            CommonLogger.businessLogger.info(transId + " | Enqueue SMS Accumlator: " + ConfigurationManager.enqueueSmsAccumulator.incrementAndGet());
//            CommonLogger.businessLogger.info(transId + " | Applicaiton Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
//            CommonLogger.businessLogger.info(transId + " | Ended Enqueue SMS Request in: " + requestTimeLong + " msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended EnqueueSMS Request")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, requestTimeLong).build());
            ThreadContext.clearMap();
        }
        return response;
    }

}
