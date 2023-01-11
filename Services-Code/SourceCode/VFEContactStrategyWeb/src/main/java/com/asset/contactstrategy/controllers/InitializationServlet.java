/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.controllers;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.DWHElementService;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.facade.LookupFacade;
import com.asset.contactstrategy.utils.Constants;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mostafa.kashif
 */
public class InitializationServlet extends HttpServlet {

    private static final String CLASS_NAME = "com.asset.contactstrategy.controllers.InitializationServlet";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config); //To change body of generated methods, choose Tools | Templates.
        String methodName = "init";
        try {
            System.out.println("Start init(ServletConfig config)");
            Defines.runningProjectId = GeneralConstants.SRC_ID_WEB_INTERFACE;
            Initializer.readPropertiesFile();
            Initializer.initializeLoggers();
            
             if(Defines.MESSAGING_MODE.equals(Defines.RABBITMQ)){
                Initializer.initializeRabbitmq();
            }
            
            
            Initializer.initializeDataSource();
            Initializer.initializeLookups();
            Initializer.loadSystemProperties();
            loadlookups();
            SMSBulkManager.refresh();
            System.out.println("End init(ServletConfig config)");
//            System.out.println("**************************************************************");
//            System.out.println("**************************************************************");
//            System.out.println("********************Contact Strategy**************************");
//            System.out.println("****************initialized successfully**********************");
//            System.out.println("**************************************************************");
//            System.out.println("**************************************************************");
            System.out.println("Initialization For Contact Strategy Web Successfull");
        } catch (Exception ex) {
            handleServiceException(ex, methodName);
            System.out.println("Exception in init(ServletConfig config)" + ex);
            System.err.println("Exception in init(ServletConfig config)" + ex);
//            System.err.println("**************************************************************");
//            System.err.println("**************************************************************");
            System.err.println("Initialization For Contact Strategy Web Failed");
//            System.err.println("Initialization Failed");
            System.err.println("Can't[" + Initializer.errorFunctionName + "]");
//            System.err.println("**************************************************************");
            throw new ServletException("Exception in init(ServletConfig config)" + ex);
        }
    }

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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String resp = "";
        try {
            System.out.println("Start processRequest InitializationServlet");
            SMSBulkManager.refresh();
            Initializer.loadSystemProperties();
            System.out.println("End processRequest InitializationServlet");
            resp = "Configuration reloaded successfully";
        } catch (Exception ex) {
            System.out.println("Exception in init(ServletConfig config)" + ex);
            resp = "ERROR DESCRIPTION : Error in reloading " + ex;
        } finally {
            out.write(resp);
            out.close();
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

    private void loadlookups() throws CommonException {
        LookupFacade lookupFacade = new LookupFacade();
        MainService mainService = new MainService();
        try {

            SystemLookups.DWH_DATA_TYPES = lookupFacade.loadLookupsMap(DBStruct.LK_DATA_TYPE.TBL_NAME, DBStruct.LK_DATA_TYPE.DATA_TYPE_ID, DBStruct.LK_DATA_TYPE.DATA_TYPE_LABEL);
            SystemLookups.DWH_DISPLAY_TYPES = lookupFacade.loadLookupsMap(DBStruct.LK_DISPLAY_TYPE.TBL_NAME, DBStruct.LK_DISPLAY_TYPE.DISPLAY_TYPE_ID, DBStruct.LK_DISPLAY_TYPE.DISPLAY_TYPE_LABEL);
            SystemLookups.services = lookupFacade.loadIdLableLookups(DBStruct.VFE_CS_SERVICES.TABLE_NAME, DBStruct.VFE_CS_SERVICES.VERSION_ID, DBStruct.VFE_CS_SERVICES.SERVICE_NAME);
            SystemLookups.SMSCNames = lookupFacade.loadIdLableLookups(DBStruct.SMSC.TABLE_NAME, DBStruct.SMSC.VERSION_ID, DBStruct.SMSC.SMSC_NAME);
            // SystemLookups.messageStatus = lookupFacade.loadIdLableLookups(DBStruct.VFE_CS_MESSAGE_STATUS.TABLE_NAME, DBStruct.VFE_CS_MESSAGE_STATUS.MESSAGE_STATUS_ID, DBStruct.VFE_CS_MESSAGE_STATUS.MESSAGE_STATUS);
            SystemLookups.interfaceTypeList = lookupFacade.loadIdLableLookups(DBStruct.VFE_CS_INTERFACE_TYPE.TABLE_NAME, DBStruct.VFE_CS_INTERFACE_TYPE.INTERFACE_ID, DBStruct.VFE_CS_INTERFACE_TYPE.INTERFACE_NAME);
            //SystemLookups.serviceCategoryList = lookupFacade.loadIdLableLookups(DBStruct.VFE_CS_SERVICE_CATEGORY.TABLE_NAME, DBStruct.VFE_CS_SERVICE_CATEGORY.CATEGORY_ID, DBStruct.VFE_CS_SERVICE_CATEGORY.CATEGORY_NAME);
            //SystemLookups.serviceTypeList = lookupFacade.loadIdLableLookups(DBStruct.VFE_CS_SERVICE_TYPE.TABLE_NAME, DBStruct.VFE_CS_SERVICE_TYPE.TYPE_ID, DBStruct.VFE_CS_SERVICE_TYPE.TYPE_NAME);
            SystemLookups.GOVERNMENTS = lookupFacade.loadLookupsMap(DBStruct.LK_GOVERNMENT.TBL_NAME, DBStruct.LK_GOVERNMENT.GOVERNMENT_ID, DBStruct.LK_GOVERNMENT.GOVERNMENT_NAME);
            SystemLookups.CUSTOMER_TYPES = lookupFacade.loadLookupsMap(DBStruct.LK_CUSTOMER_TYPE.TBL_NAME, DBStruct.LK_CUSTOMER_TYPE.CUSTOMER_TYPE_ID, DBStruct.LK_CUSTOMER_TYPE.CUSTOMER_TYPE_LABEL);
            SystemLookups.GROUP_TYPES = lookupFacade.loadLookupsMap(DBStruct.VFE_CS_FILTER_TYPE_LK.TABLE_NAME, DBStruct.VFE_CS_FILTER_TYPE_LK.GROUP_TYPE_ID, DBStruct.VFE_CS_FILTER_TYPE_LK.GROUP_TYPE_LABEL);
            SystemLookups.OPERATORS = lookupFacade.loadLookupsMap(DBStruct.LK_OPERATOR.TBL_NAME, DBStruct.LK_OPERATOR.OPERATOR_ID, DBStruct.LK_OPERATOR.OPERATOR_VALUE);
            SystemLookups.FILE_STATUS = lookupFacade.loadLookupsMap(DBStruct.VFE_CS_FILE_STATUS_LK.TABLE_NAME, DBStruct.VFE_CS_FILE_STATUS_LK.FILE_STATUS_ID, DBStruct.VFE_CS_FILE_STATUS_LK.STATUS_NAME);
            SystemLookups.USERS_TYPE = lookupFacade.loadLookupsMap(DBStruct.VFE_CS_USERS_TYPES_LK.TABLE_NAME, DBStruct.VFE_CS_USERS_TYPES_LK.ID, DBStruct.VFE_CS_USERS_TYPES_LK.TYPE_NAME);

            SystemLookups.QUEUE_TYPE_LK = lookupFacade.loadLookupsMap(DBStruct.LK_QUEUES_TYPE.TBL_NAME, DBStruct.LK_QUEUES_TYPE.QUEUE_TYPE_ID, DBStruct.LK_QUEUES_TYPE.QUEUE_TYPE_LABLE);
            SystemLookups.ORIGINATOR_TYPE_LK = lookupFacade.loadOriginatorLookup(DBStruct.VFE_CS_ORIGINATORS_LK.TABLE_NAME, DBStruct.VFE_CS_ORIGINATORS_LK.ORGINATOR_TYPE, DBStruct.VFE_CS_ORIGINATORS_LK.ORGINATOR_NAME, DBStruct.VFE_CS_ORIGINATORS_LK.ALLOWED_LENGTH);
            SystemLookups.ORIGINATOR_VALUES_LK = lookupFacade.loadLookupsMap(DBStruct.VFE_CS_ORIGINATOR_VALUES_LK.TABLE_NAME, DBStruct.VFE_CS_ORIGINATOR_VALUES_LK.ORIGINATOR_VALUE_ID, DBStruct.VFE_CS_ORIGINATOR_VALUES_LK.ORIGINATOR_VALUE_NAME);
            SystemLookups.SERVICE_PRIVILEGES_LK = lookupFacade.loadIdLableLookups(DBStruct.VFE_CS_SERVICE_PRIVILEGES_LK.TABLE_NAME, DBStruct.VFE_CS_SERVICE_PRIVILEGES_LK.PRIVILEGES_ID, DBStruct.VFE_CS_SERVICE_PRIVILEGES_LK.PRIVILEGES_NAME);
            //status lookups
            LookupModel lookupModel = new LookupModel(GeneralConstants.STATUS_APPROVED_VALUE, GeneralConstants.STATUS_APPROVED);
            SystemLookups.OPERATION_STATUS.add(lookupModel);
            lookupModel = new LookupModel(GeneralConstants.STATUS_PENDING_VALUE, GeneralConstants.STATUS_PENDING);
            SystemLookups.OPERATION_STATUS.add(lookupModel);
            lookupModel = new LookupModel(GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE, GeneralConstants.STATUS_PENDING_FOR_DELETION);
            SystemLookups.OPERATION_STATUS.add(lookupModel);
            SystemLookups.REPORTS_LIST = mainService.getReportsList();

            //Esmail.Anbar | 13/7/2017 | Adding Query Creation Sqls to Database
            Defines.QUEUE_QUERY.QUERIES = new HashMap<>();
            Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY, Initializer.checkForNull(SystemLookups.SYSTEM_PROPERTIES.get(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY), Defines.QUEUE_QUERY.QUEUE_TBL_QUERY));
            Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY, Initializer.checkForNull(SystemLookups.SYSTEM_PROPERTIES.get(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY), Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY));
            Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_START_QUERY, Initializer.checkForNull(SystemLookups.SYSTEM_PROPERTIES.get(Defines.QUEUE_QUERY.QUEUE_START_QUERY), Defines.QUEUE_QUERY.QUEUE_START_QUERY));
            Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY, Initializer.checkForNull(SystemLookups.SYSTEM_PROPERTIES.get(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY), Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY));
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for loadlookups()" + ex);
            throw ex;
        }

    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_WEB_INTERFACE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

    @Override
    public void destroy() {
        SMSBulkManager.shutdown();

        try {
            DataSourceManger.closeConnectionPool();
            CommonLogger.businessLogger.info("DataSourceManager Closed Successfully");
        } catch (CommonException ex) {
//            CommonLogger.businessLogger.info("Exception Occured When Trying To Close DataSource... " + ex);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Exception Occured When Trying to Close DataSource...").build(), ex);
            CommonLogger.errorLogger.error("Exception Occured When Trying To Close DataSource... ", ex);
        }

//        CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//        CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//        CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())).build());
//        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Ended").build());
    }

}
