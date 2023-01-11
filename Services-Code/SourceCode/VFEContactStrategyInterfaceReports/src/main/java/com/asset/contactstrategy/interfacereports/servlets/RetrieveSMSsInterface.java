/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfacereports.servlets;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfacereports.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import com.asset.contactstrategy.interfaces.models.RetrieveSMSsInputModel;
import com.asset.contactstrategy.interfaces.models.ServiceWhitelistModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author esmail.anbar
 */
public class RetrieveSMSsInterface extends HttpServlet {

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
        String xmlResponse = "";
        if (ConfigurationManager.INTERFACE_REPORTS_SHUTDOWN_FLAG) {
            CommonLogger.businessLogger.info("Shutdown Flag is True... Interfaces Are Shutting Down... Can't Process Requests");
            response.getWriter().write("Interfaces Are Shutting Down... Can't Process Requests");
            response.getWriter().close();
        } else {
//            CommonLogger.businessLogger.info("Starting Retrieve SMS Interface");
//            CommonLogger.businessLogger.info("Applicaiton Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet()
//                    + " | RetrieveSMSsInterface Accumlator Count: " + ConfigurationManager.retrieveSMSsInterfaceAccumulator.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting RetrieveSMSsInterface")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.retrieveSMSsInterfaceAccumulator.incrementAndGet()).build());
            long timeForRequestCompletion = System.currentTimeMillis();
            RetrieveSMSsInputModel inputModel = null;
            String transId = Utility.generateTransId("TRANS");
            String status = "";
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset=UTF-8");

            try {
                ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, transId);
                inputModel = getParameters(request);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, inputModel.getMsisdn());
                validateInputData(inputModel, request.getRemoteAddr());

                //Call Main Service and use the Dao & Service for SMSHistory Table
                MainService mainService = new MainService();

                xmlResponse = mainService.getMessagesForMSISDNwithDaySpan(inputModel, ConfigurationManager.statusHashMap);

//                if (xmlResponse.equals(""))
//                {
//                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE, 
//                            ErrorCodes.RETRIEVE_SMSs_INTERFACE.NO_RECORD_FOUND, "No Records found in the specified period for MSISDN=" 
//                            + inputModel.getMsisdn());
//                    CommonLogger.businessLogger.error(transId + " || " + ibe.getDetailedMessage());
//                    throw ibe;
//                }
//                //Presisting it as an HTML page for consistant display....
//                xmlResponse = "<!DOCTYPE html><html><head><title>Retrieve SMSs Interface</title></head><body>" 
//                        + xmlResponse + "</body></html>";
                status = "SUCCESS";
//                CommonLogger.businessLogger.info("RetrieveSMSsInterface Success Count: " + ConfigurationManager.retrieveSMSsInterfaceSuccessCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveSMSsInterface Success")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.retrieveSMSsInterfaceSuccessCount.incrementAndGet()).build());
            } catch (InterfacesBusinessException ibe) {
                xmlResponse = "<Response><ErrorCode>" + ibe.getErrorCode()
                        + "</ErrorCode><ErrorDescription>" + ibe.getMessage() + "</ErrorDescription></Response>";
                status = "FAILED";
//                response.getWriter().write("<!DOCTYPE html><html><head><title>Retrieve SMSs Interface</title></head><body>" 
//                        + "<Response><ErrorCode>" + ibe.getErrorCode() 
//                        + "</ErrorCode><ErrorDescription>" + ibe.getMessage() + "</ErrorDescription></Response>" + "</body></html>");
//                CommonLogger.businessLogger.info("RetrieveSMSsInterface Failed Count: " + ConfigurationManager.retrieveSMSsInterfaceFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveSMSsInterface Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.retrieveSMSsInterfaceFailedCount.incrementAndGet()).build());
            } catch (CommonException ce) {
                xmlResponse = "<Response><ErrorCode>-3000</ErrorCode><ErrorDescription>UNKNOWN_ERROR</ErrorDescription></Response>";

                status = "FAILED";
//                response.getWriter().write("<!DOCTYPE html><html><head><title>Retrieve SMSs Interface</title></head><body>" 
//                        + "<Response><ErrorCode>-3000</ErrorCode><ErrorDescription>UNKNOWN_ERROR</ErrorDescription></Response>" + "</body></html>");
//                CommonLogger.businessLogger.info("RetrieveSMSsInterface Failed Count: " + ConfigurationManager.retrieveSMSsInterfaceFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveSMSsInterface Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.retrieveSMSsInterfaceFailedCount.incrementAndGet()).build());
            } catch (Exception ex) {
                CommonLogger.businessLogger.error(ex.toString() + " || " + ex.getMessage());
                CommonLogger.errorLogger.error(ex.toString() + " || ", ex);

                xmlResponse = "<Response><ErrorCode>-3000</ErrorCode><ErrorDescription>UNKNOWN_ERROR</ErrorDescription></Response>";

//                response.getWriter().write("<!DOCTYPE html><html><head><title>Retrieve SMSs Interface</title></head><body>" 
//                        + "<Response><ErrorCode>-3000</ErrorCode><ErrorDescription>UNKNOWN_ERROR</ErrorDescription></Response>" + "</body></html>");
                status = "FAILED";
//                CommonLogger.businessLogger.info("RetrieveSMSsInterface Failed Count: " + ConfigurationManager.retrieveSMSsInterfaceFailedCount.incrementAndGet());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveSMSsInterface Failed")
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.retrieveSMSsInterfaceFailedCount.incrementAndGet()).build());
            } finally {
                timeForRequestCompletion = (System.currentTimeMillis() - timeForRequestCompletion);
                String decodedURL = "";
                if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
                    decodedURL = URLDecoder.decode(request.getRequestURL().toString() + request.getQueryString(), "UTF-8");
                } else {
                    decodedURL = URLDecoder.decode(request.getRequestURL().toString(), "UTF-8");
                }
                Utility.logInterfaceResult(transId, null, decodedURL, xmlResponse, status, timeForRequestCompletion, Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.NAME, ConfigurationManager.requestsToBeLogged);
//                logInterfaceResult(request, xmlResponse, status, timeForRequestCompletion, transId);
                response.getWriter().write(xmlResponse);
//                CommonLogger.businessLogger.info("Applicaiton Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
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

    private RetrieveSMSsInputModel getParameters(HttpServletRequest request) throws InterfacesBusinessException {
        RetrieveSMSsInputModel inputModel = new RetrieveSMSsInputModel();
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            String mSISDN = request.getParameterValues(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.INPUTS.MSISDN)[0];
            Date from = simpleDateFormatter.parse(request.getParameterValues(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.INPUTS.FROM)[0]);
            Date to = simpleDateFormatter.parse(request.getParameterValues(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.INPUTS.TO)[0]);
            String systemName = request.getParameterValues(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.INPUTS.SERVICE_NAME)[0];
            String systemPassword = request.getParameterValues(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.INPUTS.SERVICE_PASSWORD)[0];
            //System.out.println(simpleDateFormatter.format(new Date()));

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(to.getTime());
            c.add(Calendar.DATE, 1);

            to.setTime(c.getTimeInMillis());

            inputModel.setMsisdn(mSISDN);
            inputModel.setFrom(new Timestamp(from.getTime()));
            inputModel.setTo(new Timestamp(to.getTime()));
            inputModel.setSystemName(systemName);
            inputModel.setSystemPassword(systemPassword);

            if (request.getParameter(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.INPUTS.SMS_SCRIPT) != null) {
                inputModel.setSmsScript(request.getParameterValues(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.INPUTS.SMS_SCRIPT)[0]);
            }
        } catch (Exception e) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE, ErrorCodes.RETRIEVE_SMSs_INTERFACE.INVALID_INCOMPLETE_DATA, "Error Reading Inputs");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        CommonLogger.businessLogger.info(transId + " || " + "Interface Parameters :{destinationMSISDN: " + inputModel.getMsisdn()
//                + ", from:" + inputModel.getFrom() + ", to:" + inputModel.getTo() + ", SystemName:" + inputModel.getSystemName() + ", SMSscript:" + inputModel.getSmsScript() + "}");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetrieveSMSsInterface Parameters")
                .put(GeneralConstants.StructuredLogKeys.MSISDN, inputModel.getMsisdn())
                .put(GeneralConstants.StructuredLogKeys.FROM, inputModel.getFrom()).put(GeneralConstants.StructuredLogKeys.TO, inputModel.getTo())
                .put(GeneralConstants.StructuredLogKeys.SYSTEM_NAME, inputModel.getSystemName())
                .put(GeneralConstants.StructuredLogKeys.SMS_SCRIPT, inputModel.getSmsScript()).build());
        return inputModel;
    }

    private void validateInputData(RetrieveSMSsInputModel inputModel, String remoteIP) throws InterfacesBusinessException, CommonException {
        HashMap<String, ServicesModel> services = SystemLookups.SERVICES;
        ServicesModel service = services.get(inputModel.getSystemName());

        //Check system existance
        if (service == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                    ErrorCodes.RETRIEVE_SMSs_INTERFACE.SERVICE_NAME_NOT_DEFINED, "Service Not Found");
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Service...");

        // CR 1901 | eslam.ahmed
        // Check system password 
        if (inputModel.getSystemPassword() == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                    ErrorCodes.RETRIEVE_SMSs_INTERFACE.INVALID_INCOMPLETE_DATA, " Password is missing");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        if (SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY) == null
                || SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY).isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                    ErrorCodes.RETRIEVE_SMSs_INTERFACE.HASH_KEY_NOT_FOUND, " Hash Key Not Found");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        boolean validPassword = Utility.validateServicePassword(service, inputModel.getSystemPassword(), SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY));
        if (!validPassword) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                    ErrorCodes.RETRIEVE_SMSs_INTERFACE.INVALID_PASSWORD, " Invalid Password");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }

        //HAZEMTODO:Check destination MSISDN format
        CommonLogger.businessLogger.info("Validating Destination MSISIDN Format...");
        try {
            //CSPhase1.5 | Esmail.Anbar | Updating Code to use new Validation Function
            inputModel.setMsisdn(Utility.validateMSISDNFormatSMPP(inputModel.getMsisdn()));
        } catch (CommonException ce) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                    ErrorCodes.RETRIEVE_SMSs_INTERFACE.INVALID_MSISDN, "MSISDN doesnt match Regular Expression: " + inputModel.getMsisdn());
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        if (!inputModel.getMsisdn().matches(String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.MSISDN_VALIDATION_PATTERN))))
//        {
//            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE, 
//                    ErrorCodes.RETRIEVE_SMSs_INTERFACE.INVALID_MSISDN, "MSISDN doesnt match Regular Expression");
//            CommonLogger.businessLogger.error(transId + " || " + ibe.getDetailedMessage());
//            throw ibe;
//        }
        CommonLogger.businessLogger.info("Valid Destination MSISIDN Format...");

        //Check IP existance
        if (service.getHasWhitelist() == 1) {
            CommonLogger.businessLogger.info("Service Has Whitelist...");

            if (service.getWhiteListModel() == null) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                        ErrorCodes.RETRIEVE_SMSs_INTERFACE.INVALID_IP_ADDRESS, "ipAddress " + remoteIP + " Doesnt Exist in Whitelist");
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
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                        ErrorCodes.RETRIEVE_SMSs_INTERFACE.INVALID_IP_ADDRESS, "ipAddress " + remoteIP + " Doesnt Exist in Whitelist");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }
        } else {
            CommonLogger.businessLogger.info("Service Has No Whitelist...");
        }

        long from = inputModel.getFrom().getTime();
        long to = inputModel.getTo().getTime();
        long daysSpan = TimeUnit.DAYS.convert(to - from, TimeUnit.MILLISECONDS);
        if (daysSpan < 0 || daysSpan > Integer.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.INPUTS.DAYS_SPAN))) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                    ErrorCodes.RETRIEVE_SMSs_INTERFACE.INVALID_DAYS_SPAN, "Entered period doesnt match that in configuration ! DaysSpan=" + daysSpan);
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
    }

    private void logInterfaceResult(HttpServletRequest request, String output, String status, long timeInMs, String transId) {
        InterfacesLogModel logModel = new InterfacesLogModel();
        String decodedURL = "";
        try {
            decodedURL = URLDecoder.decode(request.getRequestURL().toString() + request.getQueryString(), "UTF-8");
            logModel.setInterfaceInputURL(decodedURL);
            logModel.setInterfaceName(Defines.INTERFACES.SEND_SMSs.NAME);
            logModel.setInterfaceOutput(output);
            logModel.setRequestStatus(status);
            logModel.setResponseTime(timeInMs);
            logModel.setTransId(transId);

//            CommonLogger.businessLogger.info(transId + " || " + "Logging Object: ");
//            CommonLogger.businessLogger.info(transId + " || " + "URL: " + decodedURL);
//            CommonLogger.businessLogger.info(transId + " || " + "InterfaceName: " + Defines.INTERFACES.SEND_SMSs.NAME);
//            CommonLogger.businessLogger.info(transId + " || " + "Output: " + output);
//            CommonLogger.businessLogger.info(transId + " || " + "Status: " + status);
//            CommonLogger.businessLogger.info(transId + " || " + "ResponseTime: " + String.valueOf(timeInMs));
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RetriveSMSInterface Logging Objects")
                    .put(GeneralConstants.StructuredLogKeys.URL, decodedURL)
                    .put(GeneralConstants.StructuredLogKeys.INTERFACE_NAME, Defines.INTERFACES.SEND_SMSs.NAME)
                    .put(GeneralConstants.StructuredLogKeys.OUTPUT, output)
                    .put(GeneralConstants.StructuredLogKeys.STATUS, status)
                    .put(GeneralConstants.StructuredLogKeys.RESPONSE_TIME, String.valueOf(timeInMs)).build());

            ConfigurationManager.requestsToBeLogged.put(logModel);
        } catch (InterruptedException ex) {
            CommonLogger.businessLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            CommonLogger.businessLogger.info(transId + " || " + "Error While Adding Interface Request to Logging Queue...");
        } catch (UnsupportedEncodingException ex) {
            CommonLogger.businessLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            CommonLogger.businessLogger.info(transId + " || " + "Error While Decoding Interface Request for Logging Queue...");
        } catch (Exception e) {
            CommonLogger.businessLogger.error(transId + " || " + e.getMessage());
            CommonLogger.errorLogger.error(transId + " || " + e.getMessage(), e);
        }
    }
}
