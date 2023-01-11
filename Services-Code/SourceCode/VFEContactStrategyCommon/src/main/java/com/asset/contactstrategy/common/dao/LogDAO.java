/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CsSmscInterfaceHistoryModel;
import com.asset.contactstrategy.common.models.LogModel;

/**
 *
 * @author mohamed.osman
 */
public class LogDAO {

    public void insertLog(Connection connection, List<LogModel> logModels) throws CommonException {

        CommonLogger.businessLogger.debug("[insertLog] started...");
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql;
        try {
            sql = new StringBuilder();
            sql.append(" Insert into ").append(DBStruct.VFE_CS_SMSC_INT_LOG.TABLE_NAME).append(" ( ")
                    .append(DBStruct.VFE_CS_SMSC_INT_LOG.ID).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_LOG.SESSION_ID).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_LOG.COMMAND_NAME).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_LOG.COMMAND_TYPE).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_LOG.SEQUENCE_NUMBER).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_LOG.PDU).append(" , ").append(DBStruct.VFE_CS_SMSC_INT_LOG.PARSE)
                    .append(" , ").append(DBStruct.VFE_CS_SMSC_INT_LOG.COMMAND_STATUS).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_LOG.REQUEST_ERROR).append(" ) ")
                    .append(" Values (?,?,?,?,?,?,?,?,?)");

//			CommonLogger.businessLogger.debug(" [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertingLog Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            for (LogModel logModel : logModels) {
                logModel.setId(CommonDAO.getNextId(connection, DBStruct.VFE_CS_SMSC_INT_LOG.LOG_SEQ));

                statement.setInt(1, logModel.getId());
                statement.setString(2, logModel.getSessionId());
                statement.setString(3, logModel.getCommandName());
                statement.setString(4, logModel.getCommandType());
                statement.setInt(5, logModel.getSequenceNumber());
                statement.setString(6, logModel.getPdu());
                statement.setString(7, logModel.getParse());
                statement.setString(8, logModel.getCommandStatus());
                statement.setString(9, logModel.getRequestError());
                statement.addBatch();
            }

            statement.executeBatch();
//            CommonLogger.businessLogger.debug("Logging Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(" [insertLog] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insetLog Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for [insertLog]" + e);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertLog]" + e, e);
            throw new CommonException(e.getMessage(), e.getErrorCode());

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertLog]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertLog]" + ex);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertLog]" + ex);
            }
        }
    }

    public void insertCsSmscInterfaceHistoryModel(Connection connection, List<CsSmscInterfaceHistoryModel> logModels)
            throws CommonException {

        CommonLogger.businessLogger.debug("[insertCsSmscInterfaceHistoryModel] started...");
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql;
        try {
            sql = new StringBuilder();
            sql.append(" Insert into ").append(DBStruct.VFE_CS_SMSC_INT_H.TABLE_NAME).append(" ( ")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.ID).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.REQUEST_DATE).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.SESSION_ID).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.REQUEST).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.RESPONSE).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.EXECUTION_TIME).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.REQUEST_ERROR).append(" , ")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.SMSC_MESSAGE_ID).append(" ) ").append(" Values (")
                    .append(DBStruct.VFE_CS_SMSC_INT_H.LOG_SEQ + ".NEXTVAL,").append("SYSTIMESTAMP,?,?,?,?,?,?)");

//            CommonLogger.businessLogger.debug(" [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertCsSmscInterfaceHistoryModel Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());

            for (CsSmscInterfaceHistoryModel csSmscInterfaceHistoryModel : logModels) {
                statement.setString(1, csSmscInterfaceHistoryModel.getSessionId());
                statement.setString(2, csSmscInterfaceHistoryModel.getRequest());
                statement.setString(3, csSmscInterfaceHistoryModel.getResponse());
                statement.setInt(4, csSmscInterfaceHistoryModel.getExecutionTime());
                statement.setString(5, csSmscInterfaceHistoryModel.getRequestError());
                statement.setString(6, csSmscInterfaceHistoryModel.getSmscMessageId());
                statement.addBatch();
            }

            statement.executeBatch();
//            CommonLogger.businessLogger.debug("Logging Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(" [insertCsSmscInterfaceHistoryModel] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertCsSmscInterfaceHistoryModel Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for [insertCsSmscInterfaceHistoryModel]" + e);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertCsSmscInterfaceHistoryModel]" + e, e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertCsSmscInterfaceHistoryModel]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertCsSmscInterfaceHistoryModel]" + ex);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertCsSmscInterfaceHistoryModel]" + ex);
            }
        }
    }

}
