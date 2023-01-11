/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.service;

import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.interfaces.dao.RESTLogDAO;
import com.asset.contactstrategy.interfaces.models.RESTLogModel;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author esmail.anbar
 */
public class RESTLogService {
    
    public void logRESTAction(Connection conn, ArrayList<RESTLogModel> logs) throws CommonException
    {
        //CommonLogger.infoLogger.info("InterfaceLogService.logAction was Invoked");
        
        RESTLogDAO restLogDAO = new RESTLogDAO();
        restLogDAO.logRESTAction(conn, logs);
        
        //CommonLogger.infoLogger.info("InterfaceLogService.logAction has Ended");
    }
    
}
