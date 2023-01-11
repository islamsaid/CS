/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.api;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.CustomersModel;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
import com.asset.contactstrategy.interfaces.service.MainService;
import com.google.gson.internal.LinkedTreeMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author esmail.anbar
 */
@Path("/SendSMSBulkOffline")
public class SendBulkSmsOffline {

    Date systemSubmissionDate;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String processRequest(@Context HttpServletRequest request) throws UnsupportedEncodingException {
        if (ConfigurationManager.INTERFACES_SHUTDOWN_FLAG) {
            CommonLogger.businessLogger.info("Shutdown Flag is True... Interfaces Are Shutting Down... Can't Process Requests");
            return "Interfaces Are Shutting Down... Can't Process Requests";
        } else {
            String transId = Utility.generateTransId("TRANS");
            String requestId = request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.REQUEST_ID);
            if (requestId != null && !requestId.isEmpty()) {
//                CommonLogger.businessLogger.info(transId + " || Request Id Recieved With Value: " + requestId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting SendSmsOffline Interface")
                        .put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId).build());
            }
//            CommonLogger.businessLogger.info(transId + " || " + "Starting Send Sms Bulk Offline Interaface"
//                    + " || " + "Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet()
//                    + " || " + "SendBulkSmsOffline Accumlator Count: " + ConfigurationManager.sendBulkSmsOfflineAccumulator.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Send SMS Bulk Offilne Interface")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.sendBulkSmsOfflineAccumulator.incrementAndGet()).build());
            long timeForRequestCompletion = System.currentTimeMillis();
            //BUGFIX: Adding Request Character Encoding for Arabic Handling
            request.setCharacterEncoding("UTF-8");
            String status = Defines.INTERFACES.STATUS_FAILED;
            String output;
            ServicesModel service;
            String ipAddress;
            String body = "";
            HashMap<String, Object> resultHashMap = new HashMap<>();

            try {
                ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
                //1. Generate JSON object from Request
                body = URLDecoder.decode(getJSONFromRequestBody(request), "UTF-8");
                long jsonParsingTime = System.currentTimeMillis();
                systemSubmissionDate = new Date(jsonParsingTime);
                HashMap<String, Object> jsonObject = Utility.gsonJSONStringToHashMapObject(body);
//                CommonLogger.businessLogger.info(transId + " || Parsed Request Body (JSON) to HashMap successfully in: " + (System.currentTimeMillis() - jsonParsingTime) + " msec for " + ((ArrayList) jsonObject.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs)).size() + " msgs");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Parsed Request Body (JSON) to HashMap successfully")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - jsonParsingTime))
                        .put(GeneralConstants.StructuredLogKeys.SIZE, ((ArrayList) jsonObject.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs)).size()).build());
                //2. Validate Service and ipaddress
                service = SystemLookups.SERVICES.get(String.valueOf(jsonObject.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SERVICE_NAME)));
                ipAddress = request.getRemoteAddr();
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.FORWARDING_IPADDRESS, request.getHeader("X-Forwarded-For")).build());

                // CR 1901 | eslam.ahmed
                String inputPassword = String.valueOf(jsonObject.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SERVICE_PASSWORD));

                validateService(service, ipAddress, inputPassword);

                //3. Run Input Validations for each SMS
                //& Insert SMSs into CS Validation QUEUE
                resultHashMap.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs, validateSMSsInput(transId, (ArrayList) jsonObject.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs), service, ipAddress));

                //5. Reply with all statuses for input validations
                resultHashMap.put(Defines.INTERFACES.ERROR_CODE, ErrorCodes.SEND_SMS_BULK_OFFLINE.SUCCESS);
                resultHashMap.put(Defines.INTERFACES.ERROR_DESCRIPTION, Defines.INTERFACES.STATUS_SUCCESS);

                status = Defines.INTERFACES.STATUS_SUCCESS;
//                CommonLogger.businessLogger.info(transId + " || " + "SendBulkSmsOffline Success Count: " + ConfigurationManager.sendBulkSmsOfflineSuccessCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendBulkSMSOffline Successful")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.sendBulkSmsOfflineSuccessCount.incrementAndGet()).build());
            } catch (InterfacesBusinessException ibe) {
                resultHashMap.put(Defines.INTERFACES.ERROR_CODE, ibe.getErrorCode());
                resultHashMap.put(Defines.INTERFACES.ERROR_DESCRIPTION, ibe.getMessage());
                resultHashMap.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs, new ArrayList<>());
//                CommonLogger.businessLogger.info(transId + " || " + "SendBulkSmsOffline Failed Count: " + ConfigurationManager.sendBulkSmsOfflineFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendBulkSmsOffline Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendBulkSmsOfflineFailedCount.incrementAndGet()).build());
            } catch (Exception e) {
                resultHashMap.put(Defines.INTERFACES.ERROR_CODE, "-3000");
                resultHashMap.put(Defines.INTERFACES.ERROR_DESCRIPTION, "UNKNOWN_ERROR");
                resultHashMap.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs, new ArrayList<>());
//                CommonLogger.businessLogger.info(transId + " || " + "SendBulkSmsOffline Failed Count: " + ConfigurationManager.sendBulkSmsOfflineFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendBulkSmsOffline Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.sendBulkSmsOfflineFailedCount.incrementAndGet()).build());
            } finally {
                long jsonParsingTime = System.currentTimeMillis();
                output = Utility.gsonObjectToJSONString(resultHashMap);
//                CommonLogger.businessLogger.info(transId + " || Parsed HashMap OutPut to String JSON successfully in: " + (System.currentTimeMillis() - jsonParsingTime) + " msec for " + ((ArrayList) resultHashMap.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs)).size() + " msgs");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Parsed HashMap Output to String JSON successfully")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - jsonParsingTime))
                        .put(GeneralConstants.StructuredLogKeys.SIZE, ((ArrayList) resultHashMap.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs)).size()).build());
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
                Utility.logInterfaceResult(transId, body, decodedURL, output, status, timeForRequestCompletion, Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.NAME, ConfigurationManager.requestsToBeLogged);
//                CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending Interface Request")
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
//                CommonLogger.businessLogger.info("Interface Request Ended...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Interface Request Ended...").build());
                ThreadContext.clearMap();
                return output.toString();
            }
        }
    }

    public String getJSONFromRequestBody(HttpServletRequest request) throws CommonException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                CommonLogger.businessLogger.info("Started Reading Request Body...");
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[8192];//8Kb
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                CommonLogger.businessLogger.error("Request body was found empty... InputStream returned null");
//                CommonLogger.errorLogger.error(transId + " || Request body was found empty... InputStream returned null " + Thread.currentThread().getStackTrace());
                throw new CommonException("Request body was found empty... InputStream returned null", "");
            }
            CommonLogger.businessLogger.info("Read Request Body [JSON]: " + stringBuilder.toString());
        } catch (IOException ex) {
            CommonLogger.businessLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            throw new CommonException(ex.getMessage(), "");
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            throw new CommonException(ex.getMessage(), "");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    CommonLogger.businessLogger.error(ex.getMessage());
                    CommonLogger.errorLogger.error(ex.getMessage(), ex);
                    throw new CommonException(ex.getMessage(), "");
                }
            }
        }

        return stringBuilder.toString();
    }

    public void validateService(ServicesModel service, String ipAddress, String inputPassword) throws InterfacesBusinessException, CommonException {
        CommonLogger.businessLogger.info("Starting Service Validation...");
        Utility.basicServiceValidation(service, ipAddress);
        // CR 1901 | eslam.ahmed
        if (inputPassword == null) {
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
        boolean passwordValid = Utility.validateServicePassword(service, inputPassword, SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY));
        if (!passwordValid) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.INVALID_PASSWORD, " Invalid Password");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
    }

    public ArrayList validateSMSsInput(String transId, ArrayList<LinkedTreeMap<String, LinkedTreeMap<String, String>>> messages, ServicesModel service, String ipAddress) throws InterfacesBusinessException, CommonException, InterruptedException {
        CommonLogger.businessLogger.info("Started SendSmsBulkOfflineInteraface.validateSMSsInput()...");
        InputModel inputModel = null;
        ArrayList<LinkedTreeMap<String, LinkedTreeMap<String, String>>> SMSsArray = new ArrayList<>();
        LinkedTreeMap<String, LinkedTreeMap<String, String>> SMSs;
        LinkedTreeMap<String, String> SMS;
        Long csMsgId = 0l;
        String originatorMSISDN;
        String destinationMSISDN;
        String messageText;
        String originatorTypeTemp;
        String messageTypeTemp;
        String lang;
        String doNotApplyTemp;
        String messagePriorityTemp;
        String optionalParam1;
        String optionalParam2;
        String optionalParam3;
        String optionalParam4;
        String optionalParam5;
        String templatesIds;
        String templatesParameters;
        String submissionDateString;
        int dayInSmsStats = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.TODAY_COLUMN_NUM));
        LinkedTreeMap<String, String> message;
        HashMap<String, CustomersModel> customersMap;
        ArrayList<String> MSISDNs = new ArrayList<>();
        ArrayList<InputModel> inputModelsList = new ArrayList<>();
        int inputModelsListIndex = 0;

        MainService mainService = new MainService();

        for (LinkedTreeMap<String, LinkedTreeMap<String, String>> entry : messages) {
            SMSs = new LinkedTreeMap<>();
            SMS = new LinkedTreeMap<>();
            message = entry.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMS);
            try {
                csMsgId = Utility.generateCsMsgId(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.NAME);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, csMsgId + "");
                originatorMSISDN = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_MSISDN);
                destinationMSISDN = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.DESTINATION_MSISDN);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, destinationMSISDN);
                messageText = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TEXT);
                originatorTypeTemp = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_TYPE);
                messageTypeTemp = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TYPE);
                lang = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.LANGUAGE);
                doNotApplyTemp = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.DO_NOT_APPLY);
                messagePriorityTemp = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_PRORITY);
                optionalParam1 = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_1);
                optionalParam2 = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_2);
                optionalParam3 = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_3);
                optionalParam4 = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_4);
                optionalParam5 = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_5);
                templatesIds = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_IDS);
                templatesParameters = message.get(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_PARAMETERS);
                submissionDateString = message.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SUBMISSION_DATE);

                inputModel = Utility.getInputModel(csMsgId, originatorMSISDN, destinationMSISDN, messageText,
                        originatorTypeTemp, messageTypeTemp, lang, service.getServiceName(), doNotApplyTemp,
                        messagePriorityTemp, optionalParam1, optionalParam2, optionalParam3, optionalParam4,
                        optionalParam5, templatesIds, templatesParameters, submissionDateString);

                inputModel.setDayInSmsStats(dayInSmsStats);
                inputModel.setIpaddress(ipAddress);
                inputModel.setTransId(transId);
                if (inputModel.getSubmissionDate() == null) {
                    inputModel.setSubmissionDate(systemSubmissionDate);
                }

                Utility.basicParametersValidation(service, inputModel, mainService);

                MSISDNs.add(inputModel.getDestinationMSISDN());

//                CommonLogger.businessLogger.info(transId + " || " + "csMsgId: " + csMsgId.toString() + " | Valid SMS... Adding SMS to Response HashMap And CS Validation Queue");
                SMS.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.CS_MSG_ID, csMsgId.toString());
                SMS.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.MESSAGE_STATUS, Defines.INTERFACES.STATUS_SUCCESS);

                inputModelsList.add(inputModel);
            } catch (InterfacesBusinessException ibe) {
                CommonLogger.businessLogger.info("Invalid SMS... Adding SMS to Response HashMap with Message Status: " + ibe.getDetailedMessage());
                SMS.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.CS_MSG_ID, csMsgId.toString());
                SMS.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.MESSAGE_STATUS, ibe.getDetailedMessage());
                inputModelsList.add(null);
                if (inputModel != null && inputModel.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(inputModel.getCsMsgId()), inputModel.getDestinationMSISDN(), inputModel.getSystemName(), null, inputModel.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
            } catch (Exception e) {
                CommonLogger.businessLogger.info("InValid SMS... Adding SMS to Response HashMap with Message Status: " + e.getMessage());
                SMS.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.CS_MSG_ID, csMsgId.toString());
                SMS.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.MESSAGE_STATUS, e.getMessage());
                inputModelsList.add(null);
                if (inputModel != null && inputModel.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(inputModel.getCsMsgId()), inputModel.getDestinationMSISDN(), inputModel.getSystemName(), null, inputModel.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
            } finally {
                SMSs.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMS, SMS);
                SMSsArray.add(SMSs);
                ThreadContext.remove(GeneralConstants.StructuredLogKeys.MSG_ID);
                ThreadContext.remove(GeneralConstants.StructuredLogKeys.MSISDN);
            }
        }
        CommonLogger.businessLogger.info("Finished Primary Validations");

        if (!MSISDNs.isEmpty()) {
            customersMap = mainService.getCustomersMap(MSISDNs);

            for (LinkedTreeMap<String, LinkedTreeMap<String, String>> entry : SMSsArray) {
                String csMsgIdStr = entry.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMS).get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.CS_MSG_ID);
                try {
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, csMsgIdStr);
                    if ((inputModel = inputModelsList.get(inputModelsListIndex)) != null) {
                        ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, inputModel.getDestinationMSISDN());
                        Utility.validateCustomerSendSMSBulk(inputModel, customersMap, mainService);

                        Utility.templateAndMessageValidation(inputModel, service);

                        CommonLogger.businessLogger.info("Valid SMS... Adding SMS to Response HashMap And CS Validation Queue");
                        ConfigurationManager.smsToBeValidated.put(inputModel);
                    } else {

                    }
                } catch (InterfacesBusinessException e) {
                    CommonLogger.businessLogger.info("Invalid SMS... Adding SMS to Response HashMap with Message Status: " + e.getDetailedMessage());
                    entry.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMS).put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.MESSAGE_STATUS, e.getDetailedMessage());
                    if (inputModel != null && inputModel.isUpdatedSystemQuota()) {
                        ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(inputModel.getCsMsgId()), inputModel.getDestinationMSISDN(), inputModel.getSystemName(), null, inputModel.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                    }
                } catch (CommonException e) {
                    CommonLogger.businessLogger.info("Invalid SMS... Adding SMS to Response HashMap with Message Status: " + e.getMessage());
                    entry.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMS).put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.MESSAGE_STATUS, e.getMessage());
                    if (inputModel != null && inputModel.isUpdatedSystemQuota()) {
                        ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(inputModel.getCsMsgId()), inputModel.getDestinationMSISDN(), inputModel.getSystemName(), null, inputModel.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                    }
                } catch (Exception e) {
                    CommonLogger.businessLogger.info("Invalid SMS... Adding SMS to Response HashMap with Message Status: " + e.getMessage());
                    entry.get(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMS).put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.OUTPUTS.MESSAGE_STATUS, e.getMessage());
                    if (inputModel != null && inputModel.isUpdatedSystemQuota()) {
                        ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(inputModel.getCsMsgId()), inputModel.getDestinationMSISDN(), inputModel.getSystemName(), null, inputModel.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                    }
                } finally {
                    ThreadContext.remove(GeneralConstants.StructuredLogKeys.MSG_ID);
                    ThreadContext.remove(GeneralConstants.StructuredLogKeys.MSISDN);
                }
                inputModelsListIndex++;
            }

            CommonLogger.businessLogger.info("Finished Secondary Validations");
        }

        CommonLogger.businessLogger.info("Ended SendSmsBulkOfflineInteraface.validateSMSsInput()...");
        return SMSsArray;
    }
}
