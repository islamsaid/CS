/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.controllers;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.threads.SMSBulkExecutorThread;
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
            throws ServletException, IOException 
    {
        CommonLogger.businessLogger.info("Started CheckInterface Servlet");
        ArrayList<Boolean> flags = new ArrayList<>();
        
        ConcurrentHashMap<String, ExecutorService> executors = new ConcurrentHashMap<>();
        executors.put("smsBulkExecutor", SMSBulkManager.smsBulkExecutor);
        
        ArrayList<Integer> poolSizes = new ArrayList<>();
        poolSizes.add(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_POOL_SIZE)));
        
        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<>();
        
        if (!Utility.checkInterface(flags, executors, poolSizes, threads, CommonLogger.businessLogger))
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        else
            response.setStatus(HttpServletResponse.SC_OK);
        CommonLogger.businessLogger.info("Ended CheckInterface Servlet");
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
