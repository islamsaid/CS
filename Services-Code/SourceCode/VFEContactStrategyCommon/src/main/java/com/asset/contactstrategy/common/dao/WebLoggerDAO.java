/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.WebLogModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Zain Al-Abedin
 */
public class WebLoggerDAO {
    
    public void insertLog(Connection connection,WebLogModel logModel){
//        CommonLogger.businessLogger.debug(WebLoggerDAO.class.getName() + " || " + "Starting [insertLog]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertLog Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        try {
            
        long id = CommonDAO.getNextId(connection, DBStruct.WEB_LOGGING.WEB_LOGGING_SEQ);
            logModel.setID(id);
            sql = new StringBuilder();
            sql.append("INSERT INTO ").append(DBStruct.WEB_LOGGING.TBL_NAME).append(" (")
                    .append(DBStruct.WEB_LOGGING.ID).append(",")
                    .append(DBStruct.WEB_LOGGING.ENTRY_DATE).append(",")
                    .append(DBStruct.WEB_LOGGING.OPERATION_NAME).append(",")
                    .append(DBStruct.WEB_LOGGING.PAGE_NAME).append(",")
                    .append(DBStruct.WEB_LOGGING.STRING_AFTER).append(",")
                    .append(DBStruct.WEB_LOGGING.STRING_BEFORE).append(",")
                    .append(DBStruct.WEB_LOGGING.USER_NAME).append(")")
                    .append(" VALUES (?,?,?,?,?,?,?)");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, logModel.getID());
            Timestamp timestamp = new Timestamp(new Date().getTime());
            statement.setTimestamp(2, timestamp);
            statement.setString(3, logModel.getOperationName());
            statement.setString(4, logModel.getPageName());
            statement.setString(5, logModel.getStringAfter());
            statement.setString(6, logModel.getStringBefore());
            statement.setString(7, logModel.getUserName());
            
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [insertLog]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertLog Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [insertLog] Failed to insert web log => " , e);
            
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [insertLog] Failed Failed to insert web log => " , e);
            
        } finally {
            DataSourceManger.closeDBResources(null, statement);
        }
            
    }
    
}
