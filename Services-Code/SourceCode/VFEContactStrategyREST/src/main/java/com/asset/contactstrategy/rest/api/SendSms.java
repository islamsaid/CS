package com.asset.contactstrategy.rest.api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModelREST;
import com.asset.contactstrategy.common.models.RESTResponseModel;
import com.asset.contactstrategy.common.models.SMSHistoryModel;
import com.asset.contactstrategy.common.models.TLVOptionalModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.service.MainService;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
import com.asset.contactstrategy.rest.client.Data;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author hazem.fekry
 */
@Path("/SendSMS")
public class SendSms {

    Date systemSubmissionDate;

    //added by John
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String processRequest(InputModel input, @Context HttpServletRequest request) {
        if (ConfigurationManager.INTERFACES_SHUTDOWN_FLAG) {
            CommonLogger.businessLogger.info("Shutdown Flag is True... Interfaces Are Shutting Down... Can't Process Requests");
            return "Interfaces Are Shutting Down... Can't Process Requests";

        } else {
            if (input == null) {
                CommonLogger.businessLogger.info("Input model is null");
                return "The request model is empty";
            }
            if (!input.getTlvs().isEmpty()) {
                for (TLVOptionalModel tlvModel : input.getTlvs()) {
                    if (tlvModel.getTag() == Data.OPT_PAR_SAR_MSG_REF_NUM
                            || tlvModel.getTag() == Data.OPT_PAR_SAR_SEG_SNUM
                            || tlvModel.getTag() == Data.OPT_PAR_SAR_TOT_SEG
                            || tlvModel.getTag() == Data.OPT_PAR_PAYLOAD_TYPE
                            || tlvModel.getTag() == Data.OPT_PAR_MSG_PAYLOAD) {
                        CommonLogger.businessLogger.info("Invalid TLV optional param in the request");
                        return "Invalid TLV optional param in the request";
                    }
                }
            }

            String transId = Utility.generateTransId("TRANS");
            String requestId = input.getRequestId();
//            CommonLogger.businessLogger.info(transId + " || " + "Starting SendSMS Interaface");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting SendSMS Interface").build());
//            CommonLogger.businessLogger.info(transId + " || " + "Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet()
//                    + " || " + "SendSms Accumlator Count: " + ConfigurationManager.sendSmsAccumulator.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSMS Interface Started")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.sendSmsAccumulator.incrementAndGet()).build());
            long timeForRequestCompletion = System.currentTimeMillis();
            systemSubmissionDate = new Date(timeForRequestCompletion);
            //BUGFIX: Adding Request Character Encoding for Arabic Handling
            long csMsgId;
            String status = Defines.INTERFACES.STATUS_FAILED;
            StringBuilder output = new StringBuilder();
//            InputModel input = null;
            ServicesModel service;
            String resultIdentifier = Defines.INTERFACES.resultIdentifier_VALUE;
            String resultSeparator = Defines.INTERFACES.resultSeparator_VALUE;
            String ipAddress = null;
            String inputString = "";

            try {
                ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, input.getDestinationMSISDN());
                if (requestId != null && !requestId.isEmpty()) {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting SendSmsOffline Interface")
                            .put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId).build());
//                    CommonLogger.businessLogger.info("Request Id Recieved With Value: " + requestId);
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId);
                }
                HashMap<String, ServicesModel> services = SystemLookups.SERVICES;
                int srcId = GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE;

                request.setCharacterEncoding("UTF-8");
                csMsgId = Utility.generateCsMsgId(Defines.INTERFACES.SEND_SMSs.NAME);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, csMsgId + "");

                byte messagePriority;
                if (input.getMessagePriorityText() == null || input.getMessagePriorityText().isEmpty()) {
                    messagePriority = 0;
                } else {
                    switch (input.getMessagePriorityText().toLowerCase()) {
                        case "high":
                            messagePriority = Defines.INTERFACES.MESSAGE_PRIORITY_HIGH;
                            break;
                        case "normal":
                            messagePriority = Defines.INTERFACES.MESSAGE_PRIORITY_NORMAL;
                            break;
                        default:
                            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                                    ErrorCodes.SEND_SMS.INVALID_MESSAGE_PRIORITY, "Entered MessagePriority: " + input.getMessagePriorityText());
//                            CommonLogger.businessLogger.info(transId + " || csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, ibe.getDetailedMessage()).build());
                            throw ibe;
                    }
                }
                input.setMessagePriority(messagePriority);
//                input = inputModel;
                input.setDayInSmsStats(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.TODAY_COLUMN_NUM)));
                service = services.get(input.getSystemName());
                ipAddress = input.getIpaddress();
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.FORWARDING_IPADDRESS, request.getHeader("X-Forwarded-For")).build());
                input.setIpaddress(ipAddress);
                input.setTransId(transId);
                input.setSubmissionDate(systemSubmissionDate);

                input.setCsMsgId(csMsgId);

//                inputString = Utility.gsonObjectToJSONString(input);
//                CommonLogger.businessLogger.info(transId + " || " + "Input model string: " + inputString);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Interface inputModel")
                        .put(GeneralConstants.StructuredLogKeys.INPUT_STRING, input).build());
                validateParameters(input, ipAddress, service);//First DB Connection

                MainService mainService = new MainService();
                if (mainService.sendSms(srcId, input, ipAddress, transId, ConfigurationManager.messagesToBeArchived) == 1) {
                    enqueueMessage(input, ipAddress, SystemLookups.SERVICES.get(input.getSystemName()), transId);
                } else {
                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                            ErrorCodes.SEND_SMS.CANNOT_SEND_DUE_TO_FAILED_CS_RULES, "mainService.sendSms() Returned false...");
//                    CommonLogger.businessLogger.info(transId + " || " + ibe.getDetailedMessage());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, ibe.getDetailedMessage()).build());
                    throw ibe;
                }

                status = Defines.INTERFACES.STATUS_SUCCESS;
                /* TODO output your page here. You may use following sample code. */
//                output
//                        //.append("<h1>Servlet Send SMS at ").append(request.getContextPath()).append("</h1>")
//                        .append("MessageId" + resultIdentifier).append(csMsgId)
//                        .append(resultSeparator + Defines.INTERFACES.ERROR_CODE + resultIdentifier + ErrorCodes.SEND_SMS.SUCCESS)
//                        .append(resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + Defines.INTERFACES.STATUS_SUCCESS);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Interface Successful")
                        .put(GeneralConstants.StructuredLogKeys.ERROR_CODE, ErrorCodes.SEND_SMS.SUCCESS).
                        put(GeneralConstants.StructuredLogKeys.ERROR_DESCRIPTION, Defines.INTERFACES.STATUS_SUCCESS).build());
                //.append("<h2>timeInMilliSecs = ").append(timeForRequestCompletion).append("</h2>");  
//                CommonLogger.businessLogger.info(transId + " || " + "SendSms Success Count: " + ConfigurationManager.sendSmsSuccessCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Successful")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.sendSmsSuccessCount.incrementAndGet()).build());
            } catch (InterfacesBusinessException ibe) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier).append(ibe.getErrorCode())
                        .append(resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier).append(ibe.getMessage());
                if (input != null && input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
//                CommonLogger.businessLogger.info(transId + " || " + "SendSms Failed Count: " + ConfigurationManager.sendSmsFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendSmsFailedCount.incrementAndGet()).build());
            } catch (CommonException ce) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier + "-3000" + resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + "UNKNOWN_ERROR");
//                CommonLogger.businessLogger.info(transId + " || " + ce.getErrorMsg());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, ce.getErrorMsg()).build());
                if (input != null && input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
//                CommonLogger.businessLogger.info(transId + " || " + "SendSms Failed Count: " + ConfigurationManager.sendSmsFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendSmsFailedCount.incrementAndGet()).build());
            } catch (Exception e) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier + "-3000" + resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + "UNKNOWN_ERROR");
                CommonLogger.businessLogger.error(transId + " || " + e.toString() + " || " + e.getMessage());
                CommonLogger.errorLogger.error(transId + " || " + e.toString() + " || " + e.getMessage(), e);
                if (input != null && input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
//                CommonLogger.businessLogger.info(transId + " || " + "SendSms Failed Count: " + ConfigurationManager.sendSmsFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendSmsFailedCount.incrementAndGet()).build());
            } finally {
//                CommonLogger.businessLogger.info(transId + " || " + "Adding Interface Request to Logging Queue...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Adding Interface Request to Logging Queue...").build());
                timeForRequestCompletion = (System.currentTimeMillis() - timeForRequestCompletion);
                String decodedURL = "";
                try {
                    if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
                        decodedURL = URLDecoder.decode(request.getRequestURL().toString() + request.getQueryString(), "UTF-8");
                    } else {
                        decodedURL = URLDecoder.decode(request.getRequestURL().toString(), "UTF-8");
                    }
                } catch (Exception e) {
                    decodedURL = request.getRequestURL().toString();
                }
                Utility.logInterfaceResult(transId, inputString, decodedURL, output.toString(), status, timeForRequestCompletion, Defines.INTERFACES.SEND_SMSs.NAME, ConfigurationManager.requestsToBeLogged);
//                CommonLogger.businessLogger.info(transId + " || " + "Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending Interface Request")
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
//                CommonLogger.businessLogger.info(transId + " || " + "Interface Request Ended...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Interface Request Ended...").build());
                ThreadContext.clearMap();
                return output.toString();
            }
        }
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String processRequest(@Context HttpServletRequest request
    ) {
        if (ConfigurationManager.INTERFACES_SHUTDOWN_FLAG) {
            CommonLogger.businessLogger.info("Shutdown Flag is True... Interfaces Are Shutting Down... Can't Process Requests");
            return "Interfaces Are Shutting Down... Can't Process Requests";
        } else {
            String transId = Utility.generateTransId("TRANS");
            String requestId = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.REQUEST_ID);
//            CommonLogger.businessLogger.info(transId + " || " + "Starting SendSMS Interaface");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting SendSms Interface").build());
//            CommonLogger.businessLogger.info(transId + " || " + "Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet()
//                    + " || " + "SendSms Accumlator Count: " + ConfigurationManager.sendSmsAccumulator.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Interface Started")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.sendSmsAccumulator.incrementAndGet()).build());
            long timeForRequestCompletion = System.currentTimeMillis();
            systemSubmissionDate = new Date(timeForRequestCompletion);
            //BUGFIX: Adding Request Character Encoding for Arabic Handling
            long csMsgId;
            String status = Defines.INTERFACES.STATUS_FAILED;
            StringBuilder output = new StringBuilder();
            InputModel input = null;
            ServicesModel service;
            String resultIdentifier = Defines.INTERFACES.resultIdentifier_VALUE;
            String resultSeparator = Defines.INTERFACES.resultSeparator_VALUE;
            String ipAddress = null;

            try {
                ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
                if (requestId != null && !requestId.isEmpty()) {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting SendSmsOffline Interface")
                            .put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId).build());
//                    CommonLogger.businessLogger.info("Request Id Recieved With Value: " + requestId);
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId);
                }
                HashMap<String, ServicesModel> services = SystemLookups.SERVICES;
                int srcId = GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE;

                request.setCharacterEncoding("UTF-8");
                csMsgId = Utility.generateCsMsgId(Defines.INTERFACES.SEND_SMSs.NAME);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, csMsgId + "");
                input = getParameters(request, transId, csMsgId);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, input.getDestinationMSISDN());
                input.setDayInSmsStats(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.TODAY_COLUMN_NUM)));
                service = services.get(input.getSystemName());
                ipAddress = request.getRemoteAddr();
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.FORWARDING_IPADDRESS, request.getHeader("X-Forwarded-For")).build());
                input.setIpaddress(ipAddress);
                
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.IP_ADDRRESS, ipAddress).build());
                
                input.setTransId(transId);
                input.setSubmissionDate(systemSubmissionDate);
                // CR 1901 | eslam.ahmed
                input.setSystemPassword(request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.SERVICE_PASSWORD));
                validateParameters(input, ipAddress, service);

                MainService mainService = new MainService();
                if (mainService.sendSms(srcId, input, ipAddress, transId, ConfigurationManager.messagesToBeArchived) == 1) {
                    enqueueMessage(input, ipAddress, SystemLookups.SERVICES.get(input.getSystemName()), transId);
                } else {
                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                            ErrorCodes.SEND_SMS.CANNOT_SEND_DUE_TO_FAILED_CS_RULES, "mainService.sendSms() Returned false...");
//                    CommonLogger.businessLogger.info(transId + " || " + ibe.getDetailedMessage());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, ibe.getDetailedMessage()).build());
                    throw ibe;
                }

                status = Defines.INTERFACES.STATUS_SUCCESS;
                /* TODO output your page here. You may use following sample code. */
                output
//                        //.append("<h1>Servlet Send SMS at ").append(request.getContextPath()).append("</h1>")
                        .append("MessageId" + resultIdentifier).append(csMsgId)
                        .append(resultSeparator + Defines.INTERFACES.ERROR_CODE + resultIdentifier + ErrorCodes.SEND_SMS.SUCCESS)
                        .append(resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + Defines.INTERFACES.STATUS_SUCCESS);
                //.append("<h2>timeInMilliSecs = ").append(timeForRequestCompletion).append("</h2>");  
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Interface Successful")
                        .put(GeneralConstants.StructuredLogKeys.ERROR_CODE, ErrorCodes.SEND_SMS.SUCCESS)
                        .put(GeneralConstants.StructuredLogKeys.ERROR_DESCRIPTION, Defines.INTERFACES.STATUS_SUCCESS).build());
//                CommonLogger.businessLogger.info(transId + " || " + "SendSms Success Count: " + ConfigurationManager.sendSmsSuccessCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Interface Successful")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.sendSmsSuccessCount.incrementAndGet()).build());
            } catch (InterfacesBusinessException ibe) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier).append(ibe.getErrorCode())
                        .append(resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier).append(ibe.getMessage());
                if (input != null && input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
//                CommonLogger.businessLogger.info(transId + " || " + "SendSms Failed Count: " + ConfigurationManager.sendSmsFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendSmsFailedCount.incrementAndGet()).build());
            } catch (CommonException ce) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier + "-3000" + resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + "UNKNOWN_ERROR");
//                CommonLogger.businessLogger.info(transId + " || " + ce.getErrorMsg());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, ce.getErrorMsg()).build());
                if (input != null && input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
//                CommonLogger.businessLogger.info(transId + " || " + "SendSms Failed Count: " + ConfigurationManager.sendSmsFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Interface Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendSmsFailedCount.incrementAndGet()).build());
            } catch (Exception e) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier + "-3000" + resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + "UNKNOWN_ERROR");
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
                if (input != null && input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
//                CommonLogger.businessLogger.info(transId + " || " + "SendSms Failed Count: " + ConfigurationManager.sendSmsFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendSms Interface Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendSmsFailedCount.incrementAndGet()).build());
            } finally {
//                CommonLogger.businessLogger.info(transId + " || " + "Adding Interface Request to Logging Queue...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Adding Interface Request to Logging Queue...").build());
                timeForRequestCompletion = (System.currentTimeMillis() - timeForRequestCompletion);
                String decodedURL = "";
                try {
                    if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
                        decodedURL = URLDecoder.decode(request.getRequestURL().toString() + request.getQueryString(), "UTF-8");
                    } else {
                        decodedURL = URLDecoder.decode(request.getRequestURL().toString(), "UTF-8");
                    }
                } catch (Exception e) {
                    decodedURL = request.getRequestURL().toString();
                }
                Utility.logInterfaceResult(transId, null, decodedURL, output.toString(), status, timeForRequestCompletion, Defines.INTERFACES.SEND_SMSs.NAME, ConfigurationManager.requestsToBeLogged);
//                CommonLogger.businessLogger.info(transId + " || " + "Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending Interface Request")
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
//                CommonLogger.businessLogger.info(transId + " || " + "Interface Request Ended...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Interface Request Ended...").build());
                ThreadContext.clearMap();
                return output.toString();
            }
        }
    }

    private InputModel getParameters(HttpServletRequest request, String transId, long csMsgId) throws InterfacesBusinessException {
//        CommonLogger.businessLogger.info(transId + " || " + "Started SendSmsInterface.getParameters()...");
//        CommonLogger.businessLogger.info(transId + " || " + "Fetching Servlet Parameters...");
//        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started SendSmsInterface.getParameters()...").build());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Fetching Servlet Parameters...").build());

        String originatorMSISDN = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_MSISDN);
        String destinationMSISDN = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.DESTINATION_MSISDN);
        String messageText = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TEXT);
        String originatorTypeTemp = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_TYPE);
        String messageTypeTemp = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TYPE);
        String lang = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.LANGUAGE);
        String systemName = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.SERVICE_NAME);
        String doNotApplyTemp = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.DO_NOT_APPLY);
        String messagePriorityTemp = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_PRORITY);
        String optionalParam1 = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_1);
        String optionalParam2 = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_2);
        String optionalParam3 = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_3);
        String optionalParam4 = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_4);
        String optionalParam5 = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_5);
        //CSPhase1.5 | Esmail.Anbar | Adding Template Update
        String templatesIds = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_IDS);
        String templatesParameters = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_PARAMETERS);
        String submissionDateString = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.SUBMISSION_DATE);

        return Utility.getInputModel(csMsgId, originatorMSISDN, destinationMSISDN, messageText, originatorTypeTemp,
                messageTypeTemp, lang, systemName, doNotApplyTemp, messagePriorityTemp, optionalParam1, optionalParam2,
                optionalParam3, optionalParam4, optionalParam5, templatesIds, templatesParameters, submissionDateString);
    }

    private void validateParameters(InputModel input, String ipAddress, ServicesModel service) throws InterfacesBusinessException, CommonException, Exception {
//        CommonLogger.businessLogger.info(transId + " || " + "Started SendSmsInterface.validateParameters()...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started SendSmsInterface.validateParameters()...").build());

        MainService mainService = new MainService();

        Utility.basicServiceValidation(service, ipAddress);

        // CR 1909 | eslam.ahmed
        if (input.getSystemPassword() == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.INPUT_IS_MISSING, " Password is missing");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        if (SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY) == null
                || SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY).isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.HASH_KEY_NOT_FOUND, " Hash Key Not Found");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        boolean passwordValid = Utility.validateServicePassword(service, input.getSystemPassword(), SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY));
        if (!passwordValid) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.INVALID_PASSWORD, " Invalid Password");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }

        Utility.basicParametersValidation(service, input, mainService);

        Utility.validateCustomerSendSMS(input, mainService);

        Utility.templateAndMessageValidation(input, service);

//        CommonLogger.businessLogger.info(transId + " || " + "csMsgId: " + input.getCsMsgId() + " | All Parameters Validated...");
//        CommonLogger.businessLogger.info(transId + " || " + "csMsgId: " + input.getCsMsgId() + " | Ended SendSmsInterface.validateParameters()...");
    }

    public void enqueueMessage(InputModel input, String ipAddress, ServicesModel service, String transId) throws CommonException {
//        CommonLogger.businessLogger.info(transId + " || " + "Started SendSmsInterface.enqueueMessage()...");
//        CommonLogger.businessLogger.info(transId + " || " + "Enqueueing Message in Java Queue...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started SendSmsInterface.enqueueMessage()...").build());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueueing Message in Java Queue...").build());
        //CommonLogger.businessLogger.info(transId + " || " + "Checking Enqueue Flag...");
        String enqueueFlag = Defines.INTERFACES.SINGLE_ENQUEUE_FLAG_VALUE;
//        CommonLogger.businessLogger.info(transId + " || " + "Enqueue Flag: " + enqueueFlag);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueueing Message")
                .put(GeneralConstants.StructuredLogKeys.ENQUEUE_FLAGE, enqueueFlag).build());
        if (enqueueFlag.equals("true")) {
//            CommonLogger.businessLogger.info(transId + " || " + "Single Enqueue Configured...");
//            CommonLogger.businessLogger.info(transId + " || " + "Enqueuing Message in Database...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Single Enqueue Configured...").build());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueing Message in Database...").build());
            sendAndArchiveSingleMessage(transId, input, service, ipAddress);
        } else {
//            CommonLogger.businessLogger.info(transId + " || " + "Message Batch Configured...");
//            CommonLogger.businessLogger.info(transId + " || " + "Enqueuing Message in Java Queue...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Batch Configured...").build());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueueing Message in Java Queue...").build());
            Utility.addMessageToQueues(transId, input, service, ipAddress, ConfigurationManager.smsToBeSent, ConfigurationManager.messagesToBeArchived);
        }
//        CommonLogger.businessLogger.info(transId + " || " + "Enqueued Message Successfully...");
//        CommonLogger.businessLogger.info(transId + " || " + "Ended SendSmsInterface.enqueueMessage()...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueued Message Successfully...").build());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended SendSmsInterface.enqueueMessage()...").build());
    }

    private void sendAndArchiveSingleMessage(String transId, InputModel input, ServicesModel service, String ipAddress) throws CommonException {
        MainService mainService = new MainService();
//        if (input.getSubmissionDate() == null)
//            input.setSubmissionDate(systemSubmissionDate);
        try {
            ArrayList<SMSHistoryModel> msgs = new ArrayList<>();
            msgs.add(new SMSHistoryModel(
                    input.getCsMsgId(),
                    input.getDestinationMSISDN(),
                    new Timestamp(input.getSubmissionDate().getTime()),
                    null,
                    input.getMessageText(),
                    Defines.VFE_CS_SMS_H_STATUS_LK.ENQUEUED,
                    null,
                    input.getConcatNumber(),
                    0,
                    0,
                    0,
                    0,
                    input.getMessageType(),
                    input.isDoNotApply() ? 1 : 0,
                    input.isViolateFlag() ? 1 : 0,
                    service.getSystemCategory(),
                    input.getMessagePriority(),
                    Integer.parseInt(String.valueOf(Long.parseLong(input.getDestinationMSISDN()) % Defines.MOD_X)),
                    service.getVersionId(),
                    input.getOriginatorMSISDN(),
                    input.getOptionalParam1(),
                    input.getOptionalParam2(),
                    input.getOptionalParam3(),
                    input.getOptionalParam4(),
                    input.getOptionalParam5(),
                    0,
                    service.getQueueModel().getAppName(),
                    service.getDeliveryReport()));

            mainService.archiveMessage(msgs);

            //CSPhase1.5 | Esmail.Anbar | Adding Expiration Duration Update
//            SMS smsModel = new SMS(
//                BigDecimal.valueOf(input.getCsMsgId()),
//                String.valueOf(service.getQueueModel().getAppName()),
//                input.getOriginatorMSISDN(),
//                input.getDestinationMSISDN(),
//                input.getMessageText(),
//                BigDecimal.valueOf(input.getMessageType()),
//                BigDecimal.valueOf(input.getOriginatorType()),
//                BigDecimal.valueOf(input.getLanguage()),
//                BigDecimal.ZERO,
//                BigDecimal.ZERO,
//                BigDecimal.valueOf(input.getConcatNumber()),
//                BigDecimal.ZERO,
//                ipAddress,
//                BigDecimal.valueOf(service.getDeliveryReport()),
//                input.getOptionalParam1(),
//                input.getOptionalParam2(),
//                input.getOptionalParam3(),
//                input.getOptionalParam4(),
//                input.getOptionalParam5(),
//                new Timestamp(input.getSubmissionDate().getTime()),
//                BigDecimal.valueOf(input.getExpirationHours()));
//            
//            mainService.sendSingleMessage(smsModel, service.getQueueModel().getAppName(), new StringBuilder(String.valueOf(input.getCsMsgId())));
            EnqueueModelREST smsModel = new EnqueueModelREST(
                    input.getCsMsgId(),
                    service.getQueueModel().getAppName(),
                    input.getOriginatorMSISDN(),
                    input.getDestinationMSISDN(),
                    input.getMessageText(),
                    input.getMessageType(),
                    input.getOriginatorType(),
                    input.getLanguage(),
                    0,
                    0,
                    input.getConcatNumber(),
                    0,
                    ipAddress,
                    service.getDeliveryReport(),
                    input.getOptionalParam1(),
                    input.getOptionalParam2(),
                    input.getOptionalParam3(),
                    input.getOptionalParam4(),
                    input.getOptionalParam5(),
                    new Timestamp(input.getSubmissionDate().getTime()),
                    input.getExpirationHours(),
                    input.getTlvs(),
                    input.getRequestId(),
                    input.getServiceType(),
                    input.getEsmClass(),
                    input.getProtocolId(),
                    input.getPriorityFlag(),
                    input.getScheduleDeliveryTime(),
                    input.getValidityPeriod(),
                    input.getSmDefaultMsgId());

            SmsBusinessModel smsBusinessModel = new SmsBusinessModel(smsModel, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), input.getExpirationDate(), input.isUpdatedSystemQuota(), input.isUpdatedSystemMonitor(), input.isUpdatedDoNotApply(), input.getDayInSmsStats(), true, input.isUpdatedCustomerStatistics(), input.getCampaignId(), ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE);
            ArrayList<EnqueueModelREST> enqueueModels = new ArrayList<>();
            enqueueModels.add(smsBusinessModel.getSmsModel());

            String enqueueModelsJSON = Utility.gsonObjectToJSONStringWithDateFormat(enqueueModels, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String url = ConfigurationManager.SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.ENQUEUE_SMS_REST_URL);
            String USER_AGENT = "VFEContactStrategyREST";
            String contentType = "application/json;charset=UTF-8";
            CommonLogger.businessLogger.info("Enqueue Models JSON: " + enqueueModelsJSON);
            String enqueueResponse = Utility.sendRestRequest(url, USER_AGENT, enqueueModelsJSON, contentType, "queueName=" + service.getQueueModel().getAppName() + "&csMsgIds=" + input.getCsMsgId(), "POST", ConfigurationManager.CONNECTION_TIMEOUT, ConfigurationManager.READ_TIMEOUT, CommonLogger.businessLogger);
            CommonLogger.businessLogger.info("Enqueue SMS REST Response: " + enqueueResponse);
            RESTResponseModel responseModel = Utility.gsonJSONStringToRESTResponseModel(enqueueResponse);
            if (responseModel.getCode().equalsIgnoreCase("0")) {
                throw new Exception(responseModel.getDescription());
            }
            //ConfigurationManager.messagesToBeArchived.put();
        } //        catch (SQLException e) 
        //        {
        //            CommonLogger.businessLogger.error(transId + " || " + e.getMessage());
        //            CommonLogger.errorLogger.error(transId + " || " + e.getMessage(), e);
        //            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        //        } 
        //        catch (InterruptedException e) 
        //        {
        //            CommonLogger.businessLogger.error(transId + " || " + e.getMessage());
        //            CommonLogger.errorLogger.error(transId + " || " + e.getMessage(), e);
        //            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        //        }
        catch (Exception e) {
            CommonLogger.businessLogger.error(transId + " || " + e.getMessage());
            CommonLogger.errorLogger.error(transId + " || " + e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
    }
}
