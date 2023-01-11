/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.interfaces.models.RESTLogModel;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author esmail.anbar
 */
public class RESTLogDAO {
    
    public void logRESTAction(Connection conn, ArrayList<RESTLogModel> logs) throws CommonException
    {
        //CommonLogger.businessLogger.info("InterfaceLogDAO.logAction was Invoked");
        
        StringBuilder sqlQuery = new StringBuilder();
        PreparedStatement preStmt = null;
        int paramIndex;
        RESTLogModel logModel;
        StringBuilder logString = new StringBuilder("Attempting Insertion of Logging Requests for TransIds: ");
        
        sqlQuery.append("Insert Into ")
                .append(DBStruct.VFE_CS_REST_LOGGING.TABLE_NAME)
                .append(" (")
                .append(DBStruct.VFE_CS_REST_LOGGING.ID)
                .append(",")
                .append(DBStruct.VFE_CS_REST_LOGGING.INPUT_URL)
                .append(",")
                .append(DBStruct.VFE_CS_REST_LOGGING.REST_NAME)
                .append(",")
                .append(DBStruct.VFE_CS_REST_LOGGING.REST_OUTPUT)
                .append(",")
                .append(DBStruct.VFE_CS_REST_LOGGING.REQUEST_STATUS)
                .append(",")
                .append(DBStruct.VFE_CS_REST_LOGGING.RESPONSE_TIME)
                .append(",")
                .append(DBStruct.VFE_CS_REST_LOGGING.TRANS_ID)
                .append(",")
                .append(DBStruct.VFE_CS_REST_LOGGING.REQUEST_BODY)
                .append(") ")
                .append(" Values (").append(DBStruct.VFE_CS_REST_LOGGING.ID_SEQUENCE).append(".NEXTVAL").append(", ?, ?, ?, ?, ?, ?, ?)");
        
        try 
        {
            preStmt = conn.prepareStatement(sqlQuery.toString());
            
            for (int i = 0; i < logs.size(); i++)
            {
                logModel = logs.get(i);
                if (i != 0)
                    logString.append(", ");
                logString.append(logModel.getTransId());
                paramIndex = 1;
                preStmt.setString(paramIndex++, logModel.getInputURL());
                preStmt.setString(paramIndex++, logModel.getRestName());
                
//                Clob output = conn.createClob();
//                output.setString(1, (String) logModel.getRestOutput());
//                
//                preStmt.setClob(paramIndex++, output);
                String output = logModel.getRestOutput().length() > 4000
                        ? logModel.getRestOutput().substring(0, 3980) + "...Line is trimmed" : logModel.getRestOutput();
                preStmt.setString(paramIndex++, output);
                preStmt.setString(paramIndex++, logModel.getRequestStatus());
                preStmt.setLong(paramIndex++, logModel.getResponseTime());
                preStmt.setString(paramIndex++, logModel.getTransId());
                
                Clob requestBody = conn.createClob();
                requestBody.setString(1, (String) logModel.getRequestBody());
                
                preStmt.setClob(paramIndex++, requestBody);
                
                preStmt.addBatch();
            }
            
            CommonLogger.businessLogger.info(logString);
            preStmt.executeBatch();
        }
        catch (SQLException e) 
        {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        }
        catch (Exception e) 
        {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        finally 
        {
            try 
            {
                if (preStmt != null) 
                {
                    preStmt.close();
                }
            } 
            catch (SQLException e) 
            {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
        
        CommonLogger.businessLogger.info("InterfaceLogDAO.logAction has Ended");
    }
    
}
