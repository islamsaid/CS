/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author esmail.anbar
 */
public class InterfacesLogDAO {

    public void logAction(Connection conn, ArrayList<InterfacesLogModel> logs) throws CommonException {
        //CommonLogger.businessLogger.info("InterfaceLogDAO.logAction was Invoked");

        StringBuilder sqlQuery = new StringBuilder();
        PreparedStatement preStmt = null;
        int paramIndex;
        InterfacesLogModel logModel;
        StringBuilder logString = new StringBuilder();

        sqlQuery.append("Insert Into ")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.TABLE_NAME)
                .append(" (")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.ID)
                .append(",")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.INTERFACE_INPUT_URL)
                .append(",")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.INTERFACE_NAME)
                .append(",")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.INTERFACE_OUTPUT)
                .append(",")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.REQUEST_STATUS)
                .append(",")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.RESPONSE_TIME)
                .append(",")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.TRANS_ID)
                .append(",")
                .append(DBStruct.VFE_CS_INTERFACE_LOGGING.REQUEST_BODY)
                .append(") ")
                .append(" Values (").append(DBStruct.VFE_CS_INTERFACE_LOGGING.ID_SEQUENCE).append(".NEXTVAL").append(", ?, ?, ?, ?, ?, ?, ?)");

        try {
            preStmt = conn.prepareStatement(sqlQuery.toString());

            for (int i = 0; i < logs.size(); i++) {
                logModel = logs.get(i);
                if (i != 0) {
                    logString.append(",");
                }
                logString.append(logModel.getTransId());
                paramIndex = 1;
                preStmt.setString(paramIndex++, logModel.getInterfaceInputURL());
                preStmt.setString(paramIndex++, logModel.getInterfaceName());

                Clob output = conn.createClob();
                output.setString(1, (String) logModel.getInterfaceOutput());

                preStmt.setClob(paramIndex++, output);
                preStmt.setString(paramIndex++, logModel.getRequestStatus());
                preStmt.setLong(paramIndex++, logModel.getResponseTime());
                preStmt.setString(paramIndex++, logModel.getTransId());

                Clob requestBody = conn.createClob();
                requestBody.setString(1, (String) logModel.getRequestBody());

                preStmt.setClob(paramIndex++, requestBody);

                preStmt.addBatch();
            }

            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Attempting Insertion of Logging Requests for TransIds")
                    .putCSV(GeneralConstants.StructuredLogKeys.TRANS_IDs, logString.toString()).build());
//            CommonLogger.businessLogger.info(logString);
            preStmt.executeBatch();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (preStmt != null) {
                    preStmt.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }

        CommonLogger.businessLogger.info("InterfaceLogDAO.logAction has Ended");
    }

}
