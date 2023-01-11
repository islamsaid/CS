package com.asset.cs.sendingsms.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.sendingsms.models.ReceivedSMSLogModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author islam.said
 */
public class ReceivedSMSLogDAO {

    public static void insertReceivedSMS(Connection conn, ArrayList<ReceivedSMSLogModel> receivedSMSLogs) throws CommonException {
        CommonLogger.businessLogger.debug("SMSHistoryDao.archiveMsg() Invoked...");
        StringBuilder query = new StringBuilder();

        query.append("Insert Into ")
                .append(DBStruct.VFE_CS_RECEIVED_SMS_H.TABLE_NAME)
                .append(" (")
                .append(DBStruct.VFE_CS_RECEIVED_SMS_H.REQUEST)
                .append(", ")
                .append(DBStruct.VFE_CS_RECEIVED_SMS_H.RESPONSE)
                .append(", ")
                .append(DBStruct.VFE_CS_RECEIVED_SMS_H.REQUEST_DATE)
                .append(", ")
                .append(DBStruct.VFE_CS_RECEIVED_SMS_H.TX_ID)
                .append(") VALUES (?, ?, ?, ?) ");

       CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,"Attempting Query: " + query).build());

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ReceivedSMSLogModel receivedSMSLogModel = null;

        try {
            preparedStatement = conn.prepareStatement(query.toString());

            for (int i = 0; i < receivedSMSLogs.size(); i++) {
                receivedSMSLogModel = receivedSMSLogs.get(i);

                preparedStatement.setString(1, receivedSMSLogModel.getDataSMModel().toString());
                if (receivedSMSLogModel.getDataSMResponseModel() != null) {
                    preparedStatement.setString(2, receivedSMSLogModel.getDataSMResponseModel().toString());
                } else {
                    preparedStatement.setString(2, "");
                }
                preparedStatement.setTimestamp(3, receivedSMSLogModel.getRequestDate());
                preparedStatement.setString(4, receivedSMSLogModel.getTransId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
        }
    }
}
