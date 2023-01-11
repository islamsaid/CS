/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSCInterfaceClientModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author mohamed.osman
 */
public class ClientDAO {

    public HashMap<String, SMSCInterfaceClientModel> retrieveClients(Connection connection) throws CommonException {
        CommonLogger.businessLogger.debug("[retrievingClients] started...");
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql;
        ResultSet resultSet = null;
        SMSCInterfaceClientModel clientModel;
        HashMap<String, SMSCInterfaceClientModel> clientsMap = null;
        try {
            clientsMap = new HashMap<>();
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.VFE_CS_SMSC_INT_CLIENT.TABLE_NAME);
//            CommonLogger.businessLogger.debug(" [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Selection SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                clientModel = new SMSCInterfaceClientModel();
                clientModel.setId(resultSet.getInt(DBStruct.VFE_CS_SMSC_INT_CLIENT.ID));
                clientModel.setPassword(resultSet.getString(DBStruct.VFE_CS_SMSC_INT_CLIENT.PASSWORD));
                clientModel.setSystemId(resultSet.getString(DBStruct.VFE_CS_SMSC_INT_CLIENT.SYSTEM_ID));
                clientModel.setSystemName(resultSet.getString(DBStruct.VFE_CS_SMSC_INT_CLIENT.SYSTEM_NAME));
                clientModel.setSystemType(resultSet.getString(DBStruct.VFE_CS_SMSC_INT_CLIENT.SYSTEM_TYPE));
                clientModel.setCsCalledThreads(resultSet.getInt(DBStruct.VFE_CS_SMSC_INT_CLIENT.CS_CALLED_THREADS));
                clientModel.setCsSubmitSmsQueueSize(resultSet.getInt(DBStruct.VFE_CS_SMSC_INT_CLIENT.CS_SUBMIT_SMS_QUEUE_SIZE));
                clientsMap.put(clientModel.getSystemId(), clientModel);

            }
//            CommonLogger.businessLogger.debug("Retriving Data time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Retrieve  Data Time")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis()-startTime)).build());
            CommonLogger.businessLogger.debug(" [retrieveClients] ended...");
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for [retrieveClients]" + e);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveClients]" + e, e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveClients]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveClients]" + ex);

            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveClients]" + ex);

            }
        }
        return clientsMap;
    }

}
