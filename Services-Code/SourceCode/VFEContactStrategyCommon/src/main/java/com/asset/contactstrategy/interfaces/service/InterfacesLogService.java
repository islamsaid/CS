/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.service;

import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.interfaces.dao.InterfacesLogDAO;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author esmail.anbar
 */
public class InterfacesLogService {
    
    public void logAction(Connection conn, ArrayList<InterfacesLogModel> logs) throws CommonException
    {
        //CommonLogger.infoLogger.info("InterfaceLogService.logAction was Invoked");
        
        InterfacesLogDAO interfacesLogDAO = new InterfacesLogDAO();
        interfacesLogDAO.logAction(conn, logs);
        
        //CommonLogger.infoLogger.info("InterfaceLogService.logAction has Ended");
    }
    
}
