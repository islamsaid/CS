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
import com.asset.contactstrategy.common.models.RESTResponseModel;
import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.common.models.SMSBridgeDBObject;
import com.asset.contactstrategy.common.service.DBQueueService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.enqueue.rest.common.ConfigurationManager;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
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
@Path("/EnqueueBridgeSMS")
public class EnqueueBridgeSMS {

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON)
    public RESTResponseModel processRequest(ArrayList<SMSBridge> enqueueModels,
            @QueryParam("queueName") String queueName, @QueryParam("requestId") String requestId,
            @Context HttpServletRequest httpRequest) {
        Timestamp requestTime = new Timestamp(System.currentTimeMillis());
        String transId = Utility.generateTransId("ENQBRDGSMS");
//        CommonLogger.businessLogger.info(transId + " | Started Enqueue Bridge SMS Request");
//        CommonLogger.businessLogger.info(transId + " | Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet()
//                + " | Enqueue Bridge SMS Accumlator Count: " + ConfigurationManager.enqueueBridgeSmsAccumulator.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started EnqueueBridgeSMS Request")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.enqueueBridgeSmsAccumulator.incrementAndGet()).build());
        RESTResponseModel response = new RESTResponseModel();
        Connection conn = null;
        ArrayList<SMSBridgeDBObject> dbArray;
        SMSBridgeDBObject smsModel;
        String csMsgIds = "";
        try {
            ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
            if (requestId != null && !requestId.isEmpty()) {
//                CommonLogger.businessLogger.info(transId + " | Request Id Recieved With Value: " + requestId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Request Id Recieved")
                        .put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId).build());
                ThreadContext.put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId);
            }
            if (enqueueModels == null || enqueueModels.isEmpty()) {
                throw new Exception("Enqueue Models ArrayList is Null or Empty");
            } else if (queueName == null || queueName.isEmpty()) {
                throw new Exception("Queue Name is Null or Empty");
            } else if (!ConfigurationManager.procedureQueuesDatabaseConnectionPool.containsKey(queueName)) {
                throw new Exception("Queue Name is Not an Existing Queue");
            }

//            CommonLogger.businessLogger.info(transId + " | All inputs are valid");
//            CommonLogger.businessLogger.info(transId + " | Getting connection for BridgeSmsQueue: " + queueName);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "All inpuuts are valid. Getting Connection for BridgeSMS Queue")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
            conn = ConfigurationManager.procedureQueuesDatabaseConnectionPool.get(queueName).getConnection();

            dbArray = new ArrayList<>();
            int i = 0;
            for (SMSBridge enqueueModel : enqueueModels) {
                smsModel = new SMSBridgeDBObject(
                        BigDecimal.valueOf(enqueueModel.getMsgId()),
                        enqueueModel.getQueueName(),
                        enqueueModel.getServiceName(),
                        enqueueModel.getOriginatorMSISDN(),
                        enqueueModel.getDestinationMSISDN(),
                        enqueueModel.getMsgText(),
                        BigDecimal.valueOf(enqueueModel.getMsgType()),
                        BigDecimal.valueOf(enqueueModel.getOriginatorType()),
                        BigDecimal.valueOf(Long.parseLong(enqueueModel.getLanguageId())),
                        enqueueModel.getIpAddress(),
                        enqueueModel.getDoNotApply(),
                        enqueueModel.getMessagePriority(),
                        enqueueModel.getTemplateId(),
                        enqueueModel.getTemplateParameters(),
                        enqueueModel.getOptionalParam1(),
                        enqueueModel.getOptionalParam2(),
                        enqueueModel.getOptionalParam3(),
                        enqueueModel.getOptionalParam4(),
                        enqueueModel.getOptionalParam5(),
                        new Timestamp(enqueueModel.getSubmissionDate().getTime()));
                dbArray.add(smsModel);

                if (i != 0) {
                    csMsgIds += ",";
                }
                csMsgIds += "" + enqueueModel.getMsgId();
                i++;
            }

//            CommonLogger.businessLogger.info(transId + " | Batch Ready for Enqueue in BridgeSmsQueue: " + queueName);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Batch Ready for Enqueue in BridgeSMSQueue")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
            Timestamp databaseProcessTime = new Timestamp(System.currentTimeMillis());
            new DBQueueService().enqueueBatch(dbArray, queueName, conn);
//            CommonLogger.businessLogger.info(transId + " | Enqueued Batch of: " + dbArray.size() + " in BridgeSmsQueue: " + queueName + " | Time: " + (System.currentTimeMillis() - databaseProcessTime.getTime()) + " msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "BridgeSMSQueue stats")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, dbArray.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - databaseProcessTime.getTime()))
                    .putCSV(GeneralConstants.StructuredLogKeys.MSG_IDS, csMsgIds).build());
            ConfigurationManager.procedureQueuesDatabaseConnectionPool.get(queueName).commitConnection(conn);
//            CommonLogger.businessLogger.info(transId + " | Commited Connection for BridgeSmsQueue: " + queueName);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Commited Connection for BridgeSMSQueue")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
            response.setCode("0");
            response.setDescription("Enqueued: " + csMsgIds);
            response.setTransId(transId);
//            CommonLogger.businessLogger.info(transId + " | Enqueue Bridge SMS Success Count: " + ConfigurationManager.enqueueBridgeSmsSuccessCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "EnqueueBridgeSMS Successful")
                    .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.enqueueBridgeSmsSuccessCount.incrementAndGet()).build());
        } catch (CommonException ce) {
            CommonLogger.businessLogger.error(ce.getErrorMsg());
            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
            try {
                if (ConfigurationManager.procedureQueuesDatabaseConnectionPool.containsKey(queueName)) {
                    ConfigurationManager.procedureQueuesDatabaseConnectionPool.get(queueName).rollBack(conn);
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error(ex.getErrorMsg());
                CommonLogger.errorLogger.error(ex.getErrorMsg(), ex);
            }
            response.setCode("" + ce.getErrorId());
            response.setDescription("Exception Occured: " + ce.getErrorMsg());
            response.setTransId(transId);
//            CommonLogger.businessLogger.info(transId + " | Enqueue Bridge SMS Failed Count: " + ConfigurationManager.enqueueBridgeSmsFailedCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "EnqueueBridgeSMS Failed")
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.enqueueBridgeSmsFailedCount.incrementAndGet()).build());
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
            CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
            try {
                if (ConfigurationManager.procedureQueuesDatabaseConnectionPool.containsKey(queueName)) {
                    ConfigurationManager.procedureQueuesDatabaseConnectionPool.get(queueName).rollBack(conn);
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error(ex.getErrorMsg());
                CommonLogger.errorLogger.error(ex.getErrorMsg(), ex);
            }
            response.setCode("-3000");
            response.setDescription("UNKOWN_ERROR");
            response.setTransId(transId);
//            CommonLogger.businessLogger.info(transId + " | Enqueue Bridge SMS Failed Count: " + ConfigurationManager.enqueueBridgeSmsFailedCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "EnqueueBridgeSMS Failed")
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.enqueueBridgeSmsFailedCount.incrementAndGet()).build());
        } finally {
            try {
                if (ConfigurationManager.procedureQueuesDatabaseConnectionPool.containsKey(queueName)) {
                    ConfigurationManager.procedureQueuesDatabaseConnectionPool.get(queueName).closeConnection(conn);
                }
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
                CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
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
            Utility.logRESTResult(transId, body, decodedURL, response.toString(), response.getDescription(), requestTimeLong, Defines.REST.ENQUEUE_BRIDGE_SMS.NAME, ConfigurationManager.loggingQueue);
//            CommonLogger.businessLogger.info(transId + " | Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
//            CommonLogger.businessLogger.info(transId + " | Ended Enqueue Bridge SMS Request in: " + (System.currentTimeMillis() - requestTime.getTime()) + " msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended EnqueueBridgeSMS Request")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - requestTime.getTime())).build());
            ThreadContext.clearMap();
        }
        return response;
    }
}
