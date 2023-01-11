/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.api;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.ServiceWhitelistModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author Esmail.Anbar
 */
@Path("/Consult")
public class Consult {

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getRequest(@Context HttpServletRequest request) {
        if (ConfigurationManager.INTERFACES_SHUTDOWN_FLAG) {
            CommonLogger.businessLogger.info("Shutdown Flag is True... Interfaces Are Shutting Down... Can't Process Requests");
            return "Interfaces Are Shutting Down... Can't Process Requests";
        } else {
            long timeForRequestCompletion = System.currentTimeMillis();
            CommonLogger.businessLogger.info("Starting Consult Interface");
//            CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet() + " | Consult Accumlator Count: " + ConfigurationManager.consultAccumulator.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Consult Interface")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.consultAccumulator.incrementAndGet()).build());
            String transId = Utility.generateTransId("TRANS");
//            String transId = transId;
//            long csMsgId = Utility.generateCsMsgId(transId, Defines.INTERFACES.CONSULT_INTERFACE.NAME);
            InputModel input = null;
            String status = Defines.INTERFACES.STATUS_FAILED;
            StringBuilder output = new StringBuilder();

            String resultIdentifier = Defines.INTERFACES.resultIdentifier_VALUE;
            String resultSeparator = Defines.INTERFACES.resultSeparator_VALUE;
            String ipAddress = null;
            try {
                ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
                input = getParameters(request);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, input.getDestinationMSISDN());
                input.setTransId(transId);
                // CR 1901 | eslam.ahmed
                input.setSystemPassword(request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.SERVICE_PASSWORD));
                MainService mainService = new MainService();
                ipAddress = request.getRemoteAddr();
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.FORWARDING_IPADDRESS, request.getHeader("X-Forwarded-For")).build());
                int todayColumn = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.TODAY_COLUMN_NUM));
                int lastColumn = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.LAST_COLUMN_NUM));
                String yesterdayPOSTFIX = String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.YESTERDAY_COLUMN_NAME));
                ServicesModel service = SystemLookups.SERVICES.get(input.getSystemName());;
                SMSGroupModel defaultGroup = new SMSGroupModel();
                defaultGroup.setGuardPeriod(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.GUARD_PERIOD)));
                defaultGroup.setMonthlyThreshold(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.MONTHLY_THRESHOLD)));
                defaultGroup.setWeeklyThreshold(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.WEEKLY_THRESHOLD)));
                defaultGroup.setDailyThreshold(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.DAILY_THRESHOLD)));
                defaultGroup.setGroupDescription(String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.GROUP_DESCRIPTION)));
                defaultGroup.setGroupId(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.GROUP_ID)));
                defaultGroup.setGroupName(String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.GROUP_NAME)));
                defaultGroup.setDonotContact(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.DO_NOT_CONTACT)));
                int runId = Integer.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY));

                service = validateParameters(input, ipAddress, service);
                boolean sendMsg = mainService.consultSms(input, ipAddress, service, defaultGroup, runId, transId, todayColumn, lastColumn, yesterdayPOSTFIX);
                if (!sendMsg && input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }

                output.append("SendMessage" + resultIdentifier).append(sendMsg).append(resultSeparator + Defines.INTERFACES.ERROR_CODE + resultIdentifier
                        + ErrorCodes.CONSULT_INTERFACE.SUCCESS + resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + Defines.INTERFACES.STATUS_SUCCESS);
                status = Defines.INTERFACES.STATUS_SUCCESS;
//                CommonLogger.businessLogger.info("Consult Success Count: " + ConfigurationManager.consultSuccessCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Consult Successful")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.consultSuccessCount.incrementAndGet()).build());
            } catch (InterfacesBusinessException ibe) {
                output.append("SendMessage" + resultIdentifier + "false").append(resultSeparator + Defines.INTERFACES.ERROR_CODE + resultIdentifier).append(ibe.getErrorCode()).append(resultSeparator
                        + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier).append(ibe.getMessage());
//                CommonLogger.businessLogger.info("Consult Failed Count: " + ConfigurationManager.consultFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Consult Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.consultFailedCount.incrementAndGet()).build());
                if (input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
            } catch (CommonException ce) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier + "-3000" + resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + "UNKNOWN_ERROR");

//                CommonLogger.businessLogger.info("Consult Failed Count: " + ConfigurationManager.consultFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Consult Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.consultFailedCount.incrementAndGet()).build());
                if (input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("Caught Exception---->processRequest Consult-->" + ex);
                CommonLogger.errorLogger.error("Caught Exception---->processRequest Consult-->" + ex, ex);
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier + "-3000" + resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier);

//                CommonLogger.businessLogger.info("Consult Failed Count: " + ConfigurationManager.consultFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Consult Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.consultFailedCount.incrementAndGet()).build());
                if (input.isUpdatedSystemQuota()) {
                    ConfigurationManager.smsToBeRollbacked.put(new SmsBusinessModel(null, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), null, input.isUpdatedSystemQuota(), false, false, -1, false, false, null, ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE));
                }
            } finally {
                timeForRequestCompletion = (System.currentTimeMillis() - timeForRequestCompletion);
                CommonLogger.businessLogger.info("Adding Interface Request to Logging Queue...");
                //output.append("<h2>timeInMilliSecs = ").append(timeForRequestCompletion).append(" msecs</h2>");
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
                Utility.logInterfaceResult(transId, null, decodedURL, output.toString(), status, timeForRequestCompletion, Defines.INTERFACES.CONSULT_INTERFACE.NAME, ConfigurationManager.requestsToBeLogged);
//                CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Requesting Consult Interface Ended")
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
                CommonLogger.businessLogger.info("Interface Request Ended...");
                ThreadContext.clearMap();
                return output.toString();
            }
        }
    }

    private InputModel getParameters(HttpServletRequest request) throws InterfacesBusinessException {
        String dest = request.getParameter(Defines.INTERFACES.CONSULT_INTERFACE.INPUTS.MSISDN);
        String sysName = request.getParameter(Defines.INTERFACES.CONSULT_INTERFACE.INPUTS.SERVICE_NAME);
        //Kashif condition fix 24/9/2016
        if ((dest == null) || (sysName == null)) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.CONSULT_INTERFACE.INPUT_IS_MISSING, "");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        CommonLogger.businessLogger.info(logPrefix + " || " + "Interface Parameters :{destinationMSISDN: "+dest+", systemName:"+sysName+"}");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Consult Interface Parameters")
                .put(GeneralConstants.StructuredLogKeys.DESTINATION_MSISDN, dest)
                .put(GeneralConstants.StructuredLogKeys.SYSTEM_NAME, sysName).build());

        return new InputModel(dest, sysName);
    }

    private ServicesModel validateParameters(InputModel input, String ipAddress, ServicesModel service) throws InterfacesBusinessException, CommonException, Exception {
        service = SystemLookups.SERVICES.get(input.getSystemName());
        //Check system existance
        if (service == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                    ErrorCodes.CONSULT_INTERFACE.SERVICE_NAME_NOT_DEFINED, "Service Not Found");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Service...");

        //Check IP existance
        if (service.getHasWhitelist() == 1) {
            CommonLogger.businessLogger.info("Service Has Whitelist...");

            if (service.getWhiteListModel() == null) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                        ErrorCodes.CONSULT_INTERFACE.IP_NOT_DEFINED, "ipAddress " + ipAddress + " Doesnt Exist in Whitelist");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }

            boolean ipExists = false;
            for (ServiceWhitelistModel whitelistIp : service.getWhiteListModel()) {
                if (whitelistIp.getIpAddress().equals(ipAddress)) {
                    CommonLogger.businessLogger.info("Valid IP Address...");
                    ipExists = true;
                    break;
                }
            }
            if (!ipExists) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                        ErrorCodes.CONSULT_INTERFACE.IP_NOT_DEFINED, "ipAddress " + ipAddress + " Doesnt Exist in Whitelist");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }
        } else {
            CommonLogger.businessLogger.info("Service Has No Whitelist...");
        }

        //CR 1901 | eslam.ahmed
        if (input.getSystemPassword() == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                    ErrorCodes.CONSULT_INTERFACE.INPUT_IS_MISSING, " Password is missing");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        if (SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY) == null
                || SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY).isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                    ErrorCodes.CONSULT_INTERFACE.HASH_KEY_NOT_FOUND, " Hash Key Not Found");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        boolean passwordValid = Utility.validateServicePassword(service, input.getSystemPassword(), SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY));
        if (!passwordValid) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                    ErrorCodes.CONSULT_INTERFACE.INVALID_PASSWORD, " Invalid Password");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }

        //ESMAIL.ANBAR FIX 28/9/2016
        try {
            //CSPhase1.5 | Esmail.Anbar | Updating Code to use new Validation Function
            input.setDestinationMSISDN(Utility.validateMSISDNFormatSMPP(input.getDestinationMSISDN()));
        } catch (CommonException ce) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                    ErrorCodes.CONSULT_INTERFACE.DESTINATION_MSISDN_NOT_VALID, "MSISDN doesnt match Regular Expression: " + input.getDestinationMSISDN());
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Destination MSISIDN Format...");

        //KASHIF FIX 22/9
//        MainService mainService = new MainService();
//        //Check Service Quota
//        long serviceDailyQoutaCounter = mainService.updateAndSelectServiceQouta(service.getServiceId(), service.getDailyQuota());
//        CommonLogger.businessLogger.info(logPrefix + " || csMsgId: " + input.getCsMsgId() + " | Service Qouta: " + service.getDailyQuota() + " | Service Daily Qouta Counter: " + serviceDailyQoutaCounter);
//        ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, input.getCsMsgId() + "");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Consult Service Quota")
                .put(GeneralConstants.StructuredLogKeys.SERVICE_QUOTA, service.getDailyQuota()).build());
        if (!MainService.updateServiceQuota(service.getServiceId(), 1, service.getDailyQuota(), DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.CONSULT_INTERFACE.SERVICE_QUOTA_VALIDATION_ERROR, "Daily Qouta For Service Has Expired... Service Name: " + service.getServiceName());
//            CommonLogger.businessLogger.info(logPrefix + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
//            ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, input.getCsMsgId() + "");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, ibe.getDetailedMessage()).build());

            throw ibe;
        }
//        CommonLogger.businessLogger.info(logPrefix + " || csMsgId: " + input.getCsMsgId() + " | Valid Service Qouta...");
//        ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, input.getCsMsgId() + "");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Valid Service Quota...").build());
        input.setUpdatedSystemQuota(true);
        return service;
    }
}
