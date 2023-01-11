/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.CommonDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import java.sql.Connection;
import java.sql.Timestamp;

/**
 *
 * @author esmail.anbar
 */
public class CommonService {
    
    public Timestamp getCurrentDatabaseTime(Connection conn) throws CommonException
    {
        //CommonLogger.infoLogger.info("CommonService.getCurrentDatabaseTime() Invoked...");
        CommonDAO commonDAO = new CommonDAO();
        return commonDAO.getCurrentDatabaseTime(conn);
    }
    
     public Timestamp getMidNightDatabaseTime(Connection conn) throws CommonException
    {
       // CommonLogger.infoLogger.info("CommonService.getCurrentDatabaseTime() Invoked...");
        CommonDAO commonDAO = new CommonDAO();
        return commonDAO.getMidNightDatabaseTime(conn);
    }
    
}
