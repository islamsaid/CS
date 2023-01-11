/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfacereports.servlets;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.interfaces.models.RetrieveMessageInputModel;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfacereports.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import com.asset.contactstrategy.interfaces.models.ServiceWhitelistModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author esmail.anbar
 */
public class RetrieveMessageStatus extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (ConfigurationManager.INTERFACE_REPORTS_SHUTDOWN_FLAG) {
            CommonLogger.businessLogger.info("Shutdown Flag is True... Interfaces Are Shutting Down... Can't Process Requests");
            response.getWriter().write("Interfaces Are Shutting Down... Can't Process Requests");
        } else {
//            CommonLogger.businessLogger.info("Starting Retrieve Message Status Interface");
//            CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet() 
//                    + " | RetrieveMessageStatus Accumlator Count: " + ConfigurationManager.retrieveMessageStatusAccumulator.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting RetrieveMessageStatus")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.retrieveMessageStatusAccumulator.incrementAndGet()).build());
            long timeForRequestCompletion = System.currentTimeMillis();
            String transId = Utility.generateTransId("TRANS");
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            RetrieveMessageInputModel inputModel = null;
            String htmlResponse = "";
            String status = "";
            String resultIdentifier = Defines.INTERFACES.resultIdentifier_VALUE;
            String resultSeparator = Defines.INTERFACES.resultSeparator_VALUE;

            try {
                ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
                inputModel = getParameters(request, transId);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, inputModel.getMsisdn());
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, inputModel.getMessageID() + "");
                validateInputData(inputModel, request.getRemoteAddr());
                MainService mainService = new MainService();
                String messageStatus = mainService.getMessageStatus(inputModel.getMessageID(), inputModel.getMsisdn(), ConfigurationManager.statusHashMap);
                if (messageStatus == null || messageStatus.equals("")) {
                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                            ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.NO_RECORD_FOUND, "");
                    CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                    throw ibe;
                }

                //Check result and return suitable response
                htmlResponse = "Message_Status" + resultIdentifier + messageStatus + resultSeparator + "Error_Code "
                        + resultIdentifier + "0" + resultSeparator + "Error_Description" + resultIdentifier + "SUCCESS";
                out.println(htmlResponse);

                status = "SUCCESS";

//                CommonLogger.businessLogger.info("RetrieveMessageStatus Success Count: " + ConfigurationManager.retrieveMessageStatusSuccessCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveMessageStatus Successful")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.retrieveMessageStatusSuccessCount.incrementAndGet()).build());
            } catch (InterfacesBusinessException ibe) {
                out.flush();
                htmlResponse = "Error_Code" + resultIdentifier + ibe.getErrorCode() + resultSeparator
                        + "Error_Description" + resultIdentifier + ibe.getMessage();
                out.println(htmlResponse);
                status = "FAILED";
//                CommonLogger.businessLogger.info("RetrieveMessageStatus Failed Count: " + ConfigurationManager.retrieveMessageStatusFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveMessageStatus Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.retrieveMessageStatusFailedCount.incrementAndGet()).build());
            } catch (CommonException ce) {
                out.flush();
                htmlResponse = "Error_Code" + resultIdentifier + "-3000" + resultSeparator + "Error_Description"
                        + resultIdentifier + "UNKNOWN_ERROR";
                out.println(htmlResponse);
                status = "FAILED";
//                CommonLogger.businessLogger.info("RetrieveMessageStatus Failed Count: " + ConfigurationManager.retrieveMessageStatusFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveMessageStatus Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.retrieveMessageStatusFailedCount.incrementAndGet()).build());
            } catch (Exception ex) {
                CommonLogger.businessLogger.error(ex.toString() + " || " + ex.getMessage());
                CommonLogger.errorLogger.error(ex.toString() + " || ", ex);
                out.flush();
                htmlResponse = "Error_Code" + resultIdentifier + "-3000" + resultSeparator + "Error_Description" + resultIdentifier + "UNKNOWN_ERROR";
                out.println(htmlResponse);
                status = "FAILED";
//                CommonLogger.businessLogger.info("RetrieveMessageStatus Failed Count: " + ConfigurationManager.retrieveMessageStatusFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveMessageStatus Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.retrieveMessageStatusFailedCount.incrementAndGet()).build());
            } finally {
                out.close();
                timeForRequestCompletion = (System.currentTimeMillis() - timeForRequestCompletion);
                String decodedURL = "";
                if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
                    decodedURL = URLDecoder.decode(request.getRequestURL().toString() + request.getQueryString(), "UTF-8");
                } else {
                    decodedURL = URLDecoder.decode(request.getRequestURL().toString(), "UTF-8");
                }
                Utility.logInterfaceResult(transId, null, decodedURL, htmlResponse, status, timeForRequestCompletion, Defines.INTERFACES.RETRIEVE_MESSAGE_STATUS.NAME, ConfigurationManager.requestsToBeLogged);
//                logInterfaceResult(request, htmlResponse, status, timeForRequestCompletion, transId);
//                CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
//                CommonLogger.businessLogger.info(transId + " || " + "Interface Request Ended...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Interface Request Ended")
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
                ThreadContext.clearMap();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private RetrieveMessageInputModel getParameters(HttpServletRequest request, String transId) throws InterfacesBusinessException {
        RetrieveMessageInputModel inputModel = new RetrieveMessageInputModel();

        try {
            String mSISDN = request.getParameterValues(Defines.INTERFACES.RETRIEVE_MESSAGE_STATUS.INPUTS.MSISDN)[0];
            long messageID = Long.parseLong(request.getParameterValues(Defines.INTERFACES.RETRIEVE_MESSAGE_STATUS.INPUTS.MESSAGE_ID)[0]);
            String systemName = request.getParameterValues(Defines.INTERFACES.RETRIEVE_MESSAGE_STATUS.INPUTS.SERVICE_NAME)[0];
            String systePassword = request.getParameterValues(Defines.INTERFACES.RETRIEVE_MESSAGE_STATUS.INPUTS.SERVICE_PASSWORD)[0]; // CR 1901 | eslam.ahmed

            inputModel.setMsisdn(mSISDN);
            inputModel.setMessageID(messageID);
            inputModel.setSystemName(systemName);
            inputModel.setSystemPassword(systePassword);// CR 1901 | eslam.ahmed
        } catch (Exception e) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                    ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_INCOMPLETE_DATA, "");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        CommonLogger.businessLogger.info(transId + " || " + "Interface Parameters :{destinationMSISDN: " + inputModel.getMsisdn()
//                + ", messageId:" + inputModel.getMessageID() + ", systemName:" + inputModel.getSystemName() + "}");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveMessageStatus Interface Parameters")
                .put(GeneralConstants.StructuredLogKeys.MSISDN, inputModel.getMsisdn())
                .put(GeneralConstants.StructuredLogKeys.MSG_ID, inputModel.getMessageID())
                .put(GeneralConstants.StructuredLogKeys.SYSTEM_NAME, inputModel.getSystemName()).build());
        return inputModel;
    }

    private void validateInputData(RetrieveMessageInputModel inputModel, String remoteIP) throws InterfacesBusinessException, CommonException {
        HashMap<String, ServicesModel> services = SystemLookups.SERVICES;
        ServicesModel service = services.get(inputModel.getSystemName());

        //Check system existance
        if (service == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                    ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.SERVICE_NAME_NOT_DEFINED, "Service Not Found");
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Service...");

        // CR 1901 | eslam.ahmed
        // Check system password 
        if (inputModel.getSystemPassword() == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                    ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_INCOMPLETE_DATA, " Password is missing");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        if (SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY) == null
                || SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY).isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                    ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.HASH_KEY_NOT_FOUND, " Hash Key Not Found");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        boolean validPassword = Utility.validateServicePassword(service, inputModel.getSystemPassword(), SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY));
        if (!validPassword) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                    ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_PASSWORD, " Invalid Password");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        //HAZEMTODO:Check destination MSISDN format
        CommonLogger.businessLogger.info("Validating Destination MSISIDN Format...");
        try {
            //CSPhase1.5 | Esmail.Anbar | Updating Code to use new Validation Function
            inputModel.setMsisdn(Utility.validateMSISDNFormatSMPP(inputModel.getMsisdn()));
        } catch (CommonException ce) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                    ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_MSISDN, "MSISDN doesnt match Regular Expression: " + inputModel.getMsisdn());
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        if (!inputModel.getMsisdn().matches(String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.MSISDN_VALIDATION_PATTERN))))
//        {
//            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE, 
//                    ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_MSISDN, "MSISDN not doesnt match Regular Expression");
//            CommonLogger.businessLogger.error(transId + " || " + ibe.getDetailedMessage());
//            throw ibe;
//        }
        CommonLogger.businessLogger.info("Valid Destination MSISIDN Format...");

        //Check IP existance
        if (service.getHasWhitelist() == 1) {
            CommonLogger.businessLogger.info("Service Has Whitelist...");

            if (service.getWhiteListModel() == null) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                        ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_IP_ADDRESS, "ipAddress " + remoteIP + " Doesnt Exist in Whitelist");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }

            boolean ipExists = false;
            for (ServiceWhitelistModel whitelistIp : service.getWhiteListModel()) {
                if (whitelistIp.getIpAddress().equals(remoteIP)) {
                    CommonLogger.businessLogger.info("Valid IP Address...");
                    ipExists = true;
                    break;
                }
            }
            if (!ipExists) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                        ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_IP_ADDRESS, "ipAddress " + remoteIP + " Doesnt Exist in Whitelist");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }
        } else {
            CommonLogger.businessLogger.info("Service Has No Whitelist...");
        }
    }
}
