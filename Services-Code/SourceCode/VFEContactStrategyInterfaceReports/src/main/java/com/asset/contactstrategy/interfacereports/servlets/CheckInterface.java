/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfacereports.servlets;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfacereports.common.ConfigurationManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author esmail.anbar
 */
public class CheckInterface extends HttpServlet {

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
//        CommonLogger.businessLogger.info("Started CheckInterface Servlet");
//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet() 
//                + " | Check Interface Accumlator Count: " + ConfigurationManager.checkInterfaceAccumulator.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started CheckInterface Servlet")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.checkInterfaceAccumulator.incrementAndGet()).build());
        ArrayList<Boolean> flags = new ArrayList<>();
        flags.add(ConfigurationManager.INTERFACE_REPORTS_SHUTDOWN_FLAG);
        flags.add(ConfigurationManager.SHUTDOWN_FLAG);
        flags.add(ConfigurationManager.MONITOR_THREAD_SHUTDOWN_FLAG);

        ConcurrentHashMap<String, ExecutorService> executors = new ConcurrentHashMap<>();
        executors.put("logThreadPool", ConfigurationManager.logThreadPool);

        ArrayList<Integer> poolSizes = new ArrayList<>();
        poolSizes.add(ConfigurationManager.MAX_LOGGING_THREAD_POOL_SIZE);

        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<>();
        threads.put("monitoringThread", ConfigurationManager.monitoringThread);
        threads.put("reloadingThread", ConfigurationManager.reloadingThread);

        if (!Utility.checkInterface(flags, executors, poolSizes, threads, CommonLogger.businessLogger)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            CommonLogger.businessLogger.info("Check Interface Failed Count: " + ConfigurationManager.checkInterfaceFailedCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "CheckInterface Failed")
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.checkInterfaceFailedCount.incrementAndGet()).build());
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
//            CommonLogger.businessLogger.info("Check Interface Success Count: " + ConfigurationManager.checkInterfaceSuccessCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "CheckInterface Successful")
                    .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.checkInterfaceSuccessCount.incrementAndGet()).build());
        }
//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
//        CommonLogger.businessLogger.info("Ended CheckInterface Servlet");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "CheckInterface Ended")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
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
}
