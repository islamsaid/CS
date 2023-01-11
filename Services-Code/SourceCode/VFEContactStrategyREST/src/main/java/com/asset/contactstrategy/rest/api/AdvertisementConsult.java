package com.asset.contactstrategy.rest.api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.interfaces.service.MainService;
import com.asset.contactstrategy.interfaces.models.ServiceWhitelistModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import java.net.URLDecoder;
import java.util.HashMap;
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
@Path("/AdsConsult")
public class AdvertisementConsult {

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getRequest(@Context HttpServletRequest request) {
        if (ConfigurationManager.INTERFACES_SHUTDOWN_FLAG) {
            CommonLogger.businessLogger.info("Shutdown Flag is True... Interfaces Are Shutting Down... Can't Process Requests");
            return "Interfaces Are Shutting Down... Can't Process Requests";
        } else {
            long timeForRequestCompletion = System.currentTimeMillis();
            CommonLogger.businessLogger.info("Starting Advertisment Consult Interface");
//             CommonLogger.businessLogger.info("Applicaiton Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet()
//                    + " | AdvertisementConsult Accumlator Count: " + ConfigurationManager.advertismentConsultAccumulator.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Advertisment Consult Interface Started")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.advertismentConsultAccumulator.incrementAndGet()).build());
            StringBuilder output = new StringBuilder();
            ServicesModel service;
            InputModel input = null;
            String status = Defines.INTERFACES.STATUS_FAILED;
            String transId = Utility.generateTransId("TRANS");
            String resultIdentifier = Defines.INTERFACES.resultIdentifier_VALUE;
            String resultSeparator = Defines.INTERFACES.resultSeparator_VALUE;

            try {
                ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
                HashMap<String, ServicesModel> services = SystemLookups.SERVICES;
                int srcId = GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE;

                input = getParameters(request);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, input.getDestinationMSISDN());
                // CR 1901 | eslam.ahmed
                input.setSystemPassword(request.getParameter(Defines.INTERFACES.SEND_SMSs.INPUTS.SERVICE_PASSWORD));
                service = services.get(input.getSystemName());
                String ipAddress = request.getRemoteAddr();
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.FORWARDING_IPADDRESS, request.getHeader("X-Forwarded-For")).build());
                MainService mainService = new MainService();
                validateParameters(input, ipAddress, service);
                String script = mainService.consultAds(srcId, input, service, ipAddress, transId);

                output.append("AdvertisementScript" + resultIdentifier)
                        .append(script).append(resultSeparator + Defines.INTERFACES.ERROR_CODE + resultIdentifier + ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.SUCCESS + resultSeparator
                        + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + Defines.INTERFACES.STATUS_SUCCESS);
                status = Defines.INTERFACES.STATUS_SUCCESS;
//                CommonLogger.businessLogger.info("AdvertisementConsult Success Count: " + ConfigurationManager.advertismentConsultSuccessCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Advertisment Consult Successful")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.advertismentConsultSuccessCount.incrementAndGet()).build());
            } catch (InterfacesBusinessException ibe) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier).append(ibe.getErrorCode()).append(resultSeparator
                        + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier).append(ibe.getMessage());
//                CommonLogger.businessLogger.info("AdvertisementConsult Failed Count: " + ConfigurationManager.advertismentConsultFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Advertisement Consult Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.advertismentConsultFailedCount.incrementAndGet()).build());
            } catch (CommonException ce) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier + "-3000").append(resultSeparator
                        + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + "UNKOWN_ERROR");
//                CommonLogger.businessLogger.info("AdvertisementConsult Failed Count: " + ConfigurationManager.advertismentConsultFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Advertisement Consult Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.advertismentConsultFailedCount.incrementAndGet()).build());
            } catch (Exception e) {
                output.append(Defines.INTERFACES.ERROR_CODE + resultIdentifier + "-3000" + resultSeparator + Defines.INTERFACES.ERROR_DESCRIPTION + resultIdentifier + "UNKOWN_ERROR");
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
//                CommonLogger.businessLogger.info("AdvertisementConsult Failed Count: " + ConfigurationManager.advertismentConsultFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Advertisement Consult Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.advertismentConsultFailedCount.incrementAndGet()).build());
            } finally {
                timeForRequestCompletion = (System.currentTimeMillis() - timeForRequestCompletion);
                CommonLogger.businessLogger.info("Adding Interface Request to Logging Queue...");
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
                Utility.logInterfaceResult(transId, null, decodedURL, output.toString(), status, timeForRequestCompletion, Defines.INTERFACES.ADVERTISMENT_CONSULT.NAME, ConfigurationManager.requestsToBeLogged);
//                CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending Interface Request")
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
                CommonLogger.businessLogger.info("Interface Request Ended...");
                ThreadContext.clearMap();
                return output.toString();
            }
        }
    }

    private InputModel getParameters(HttpServletRequest request) throws InterfacesBusinessException {
        String dest = request.getParameter(Defines.INTERFACES.ADVERTISMENT_CONSULT.INPUTS.DESTINATION_MSISDN);
        String sysName = request.getParameter(Defines.INTERFACES.ADVERTISMENT_CONSULT.INPUTS.SERVICE_NAME);
        String tempLang = request.getParameter(Defines.INTERFACES.ADVERTISMENT_CONSULT.INPUTS.LANGUAGE);
        byte lang;

        //BUGFIX Esmail.Anbar 29/09/2016
        if (dest == null// || dest.isEmpty()
                || sysName == null// || sysName.isEmpty()
                || tempLang == null)// || tempLang.isEmpty()) 
        {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE, ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.INPUT_IS_MISSING, "Error Reading Inputs");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }

        try {
            lang = Byte.valueOf(tempLang);
        } catch (NumberFormatException e) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.LANGUAGE_NOT_DEFINED, e.getMessage());
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        CommonLogger.businessLogger.info(transId + " || " + "Interface Parameters :{destinationMSISDN: " + dest + ", language:" + lang + ", systemName:" + sysName + "}");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Advertisement Interface Parameters")
                .put(GeneralConstants.StructuredLogKeys.MSISDN, dest)
                .put(GeneralConstants.StructuredLogKeys.LANGUAGE, lang)
                .put(GeneralConstants.StructuredLogKeys.SYSTEM_NAME, sysName).build());
        return new InputModel(dest, sysName, lang);
    }

    private void validateParameters(InputModel input, String ipAddress, ServicesModel service) throws InterfacesBusinessException, CommonException {
        //Check system existance
        CommonLogger.businessLogger.info("Validating Service Existance...");
        if (service == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.SERVICE_NAME_NOT_DEFINED, "Service Not Found");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Service...");

        //Check IP existance
        CommonLogger.businessLogger.info("Validating Service Whitelist...");
        if (service.getHasWhitelist() == 1) {
            CommonLogger.businessLogger.info("Service Has Whitelist...");

            if (service.getWhiteListModel() == null) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                        ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.IP_NOT_DEFINED, "ipAddress " + ipAddress + " Doesnt Exist in Whitelist");
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
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                        ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.IP_NOT_DEFINED, "ipAddress " + ipAddress + " Doesnt Exist in Whitelist");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }
        } else {
            CommonLogger.businessLogger.info("Service Has No Whitelist...");
        }

        //CR 1901 | eslam.ahmed
        if (input.getSystemPassword() == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.INPUT_IS_MISSING, " Password is missing");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        if (SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY) == null
                || SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY).isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.HASH_KEY_NOT_FOUND, " Hash Key Not Found");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        boolean validPassword = Utility.validateServicePassword(service, input.getSystemPassword(), SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY));
        if (!validPassword) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.INVALID_PASSWORD, " Invalid Password");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        //HAZEMTODO:Check destination MSISDN format
        CommonLogger.businessLogger.info("Validating Destination MSISIDN Format...");
        try {
            //CSPhase1.5 | Esmail.Anbar | Updating Code to use new Validation Function
            input.setDestinationMSISDN(Utility.validateMSISDNFormatSMPP(input.getDestinationMSISDN()));
        } catch (CommonException ce) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.DESTINATION_MSISDN_NOT_VALID, "MSISDN Doesnt Match Regular Expression");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Destination MSISIDN Format...");

        //Validate Language
        CommonLogger.businessLogger.info("Validating Language...");
        if (input.getLanguage() != Defines.LANGUAGE_ENGLISH && input.getLanguage() != Defines.LANGUAGE_ARABIC) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.LANGUAGE_NOT_DEFINED, "");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Language...");
    }
}
