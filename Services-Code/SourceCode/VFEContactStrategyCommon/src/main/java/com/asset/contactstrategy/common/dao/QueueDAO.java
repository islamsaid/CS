/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.InputModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yomna Naser
 */
public class QueueDAO {

    public ArrayList<QueueModel> getApplicationQueues(Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getApplicationQueues]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueues Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();

        try {

            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.APPQUEUE.TABLE_NAME)
                    .append(" inner join ").append(DBStruct.VFE_CS_USERS.TABLE_NAME)
                    .append(" on ").append(DBStruct.APPQUEUE.CREATOR).append(" = ")
                    .append(DBStruct.VFE_CS_USERS.USER_ID)
                    .append(" Order by ").append(DBStruct.APPQUEUE.APP_ID)
                    .append(" , ").append(DBStruct.APPQUEUE.VERSION_ID);
            statement = connection.prepareStatement(sql.toString());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueues Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueues Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            ArrayList<QueueModel> queueModels = new ArrayList<>();
            while (resultSet.next()) {
                QueueModel queueModel = new QueueModel();
                queueModel.setVersionId(resultSet.getInt(DBStruct.APPQUEUE.VERSION_ID));
                queueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
                queueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                queueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
                queueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
                queueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
                queueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
                queueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
                queueModel.setDatabaseUrl(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
                queueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
                queueModel.setCreatorName(resultSet.getString(DBStruct.VFE_CS_USERS.USER_NAME));
                queueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));
                queueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
                    queueModel.setTimeWindowFlag(true);
                } else {
                    queueModel.setTimeWindowFlag(false);
                }
                queueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
                queueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
                queueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
                queueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));
                queueModel.setQueueType(resultSet.getInt(DBStruct.APPQUEUE.QUEUE_TYPE));
                queueModel.setEditedVersionDescription(resultSet.getString(DBStruct.APPQUEUE.EDITED_VERSION_DESCRIPTION));
                queueModel.setSmscModels(this.getApplicationQueuesSMSCs(queueModel.getVersionId(), connection));
                queueModels.add(queueModel);
            }
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Application Queues List Size :" + queueModels.size());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueues Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, queueModels.size()).build());

            return queueModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueues]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueues]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueues]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    /*   public ArrayList<QueueModel> getApplicationQueues(Connection connection, int queueType) throws CommonException {
        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getApplicationQueues(queueType)]...");
        ArrayList<QueueModel> queueModels;

        queueModels = getApplicationQueues(connection);
        if (queueType == 1) {
            for (QueueModel queueModel : queueModels) {
                if (queueModel.getQueueType() == 2) {
                    queueModels.remove(queueModel);
                }
            }

        } else if (queueType == 2) {
            for (QueueModel queueModel : queueModels) {
                if (queueModel.getQueueType() == 1) {
                    queueModels.remove(queueModel);
                }
            }
        }
        
        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Application Queues List Size :" + queueModels.size());
        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getApplicationQueues(queuetype)]...");
        
        return queueModels;
    }*/
    public ArrayList<SMSCModel> getApplicationQueuesSMSCs(long queueID, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getApplicationQueuesSMSCs]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesSMSCs Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();

        try {

            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.SMSC.TABLE_NAME).append(" inner join ")
                    .append(DBStruct.APP_QUEUES_SMSC.TABLE_NAME).append(" on ")
                    .append(DBStruct.SMSC.TABLE_NAME).append(".")
                    .append(DBStruct.SMSC.VERSION_ID).append(" = ")
                    .append(DBStruct.APP_QUEUES_SMSC.TABLE_NAME).append(".")
                    .append(DBStruct.APP_QUEUES_SMSC.SMSC_ID)
                    .append(" where ").append(DBStruct.APP_QUEUES_SMSC.APP_QUEUE_ID).append(" = ? and ")
                    .append(DBStruct.SMSC.STATUS).append(" = ? ORDER BY ")
                    .append(DBStruct.APP_QUEUES_SMSC.TABLE_NAME).append(".").append(DBStruct.APP_QUEUES_SMSC.CONNECTIVITY_ORDER).append(" asc");
            //System.out.println(sql.toString());
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueID);
            statement.setInt(2, GeneralConstants.STATUS_APPROVED_VALUE);
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesSMSCs query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesSMSCs Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            ArrayList<SMSCModel> smscModels = new ArrayList<>();
            while (resultSet.next()) {
                SMSCModel smscModel = new SMSCModel();
                smscModel.setVersionId(resultSet.getInt(DBStruct.SMSC.VERSION_ID));
                smscModel.setSMSCid(resultSet.getInt(DBStruct.SMSC.SMSC_ID));
                smscModel.setSMSCname(resultSet.getString(DBStruct.SMSC.SMSC_NAME));
                smscModel.setIp(resultSet.getString(DBStruct.SMSC.IP));
                smscModel.setPort(resultSet.getInt(DBStruct.SMSC.PORT));
                smscModel.setSystemType(resultSet.getString(DBStruct.SMSC.SYSTEM_TYPE));
                smscModel.setUsername(resultSet.getString(DBStruct.SMSC.USERNAME));
                smscModel.setPassword(resultSet.getString(DBStruct.SMSC.PASSWORD));
                smscModel.setCreator(resultSet.getInt(DBStruct.SMSC.CREATOR));
                smscModel.setStatus(resultSet.getInt(DBStruct.SMSC.STATUS));
                smscModel.setWindowSize(resultSet.getInt(DBStruct.SMSC.WINDOW_SIZE));
                smscModel.setThroughput(resultSet.getInt(DBStruct.SMSC.THROUGHPUT));                
                smscModels.add(smscModel);
            }
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Application Queue SMSCs List Size :" + smscModels.size());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getApplicationQueuesSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesSMSCs Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, smscModels.size()).build());
            return smscModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesSMSCs]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesSMSCs]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesSMSCs]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void updateApplicationQueue(Connection connection, QueueModel queueModel) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [updateApplicationQueue]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateApplicationQueue Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("Update ").append(DBStruct.APPQUEUE.TABLE_NAME).append(" set ")
                    .append(DBStruct.APPQUEUE.APP_NAME).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.CREATOR).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.DATABASE_URL).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.SCHEMA_NAME).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.SCHEMA_PASSWORD).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.SENDER_POOL_SIZE).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.STATUS).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.THRESHOLD).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FLAG).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.LAST_MODIFIED_BY).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.EDITED_VERSION_DESCRIPTION).append(" = ? ")
                    .append(" where ").append(DBStruct.APPQUEUE.VERSION_ID).append(" = ?")
                    .append(" and ").append(DBStruct.APPQUEUE.APP_ID).append(" = ?");

            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, queueModel.getAppName());
            statement.setInt(2, queueModel.getCreator());
            statement.setString(3, queueModel.getDatabaseUrl());
            statement.setInt(4, queueModel.getDequeuePoolSize());
            statement.setString(5, queueModel.getSchemaName());
            statement.setString(6, queueModel.getSchemaPassword());
            statement.setInt(7, queueModel.getSenderPoolSize());
            statement.setInt(8, queueModel.getStatus());
            statement.setInt(9, queueModel.getThreshold());
            statement.setInt(10, queueModel.isTimeWindowFlag() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(11, queueModel.getTimeWindowFromHour());
            statement.setInt(12, queueModel.getTimeWindowFromMin());
            statement.setInt(13, queueModel.getTimeWindowToHour());
            statement.setInt(14, queueModel.getTimeWindowToMin());
            statement.setInt(15, queueModel.getLastModifiedBy());
            statement.setString(16, queueModel.getEditedVersionDescription());
            statement.setLong(17, queueModel.getVersionId());
            statement.setLong(18, queueModel.getAppId());

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateApplicationQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Updationg Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [updateApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateApplicationQueue Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [updateApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [updateApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [updateApplicationQueue]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    //insert edited application queue
    public void insertApplicationQueue(Connection connection, QueueModel queueModel) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [insertApplicationQueue]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueue Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("INSERT INTO ").append(DBStruct.APPQUEUE.TABLE_NAME).append(" (")
                    .append(DBStruct.APPQUEUE.VERSION_ID).append(",")
                    .append(DBStruct.APPQUEUE.APP_ID).append(",")
                    .append(DBStruct.APPQUEUE.APP_NAME).append(",")
                    .append(DBStruct.APPQUEUE.CREATOR).append(",")
                    .append(DBStruct.APPQUEUE.DATABASE_URL).append(",")
                    .append(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE).append(",")
                    .append(DBStruct.APPQUEUE.SCHEMA_NAME).append(",")
                    .append(DBStruct.APPQUEUE.SCHEMA_PASSWORD).append(",")
                    .append(DBStruct.APPQUEUE.SENDER_POOL_SIZE).append(",")
                    .append(DBStruct.APPQUEUE.STATUS).append(",")
                    .append(DBStruct.APPQUEUE.THRESHOLD).append(",")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FLAG).append(",")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR).append(",")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN).append(",")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR).append(",")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN).append(",")
                    .append(DBStruct.APPQUEUE.QUEUE_NAME).append(",")
                    .append(DBStruct.APPQUEUE.LAST_MODIFIED_BY).append(",")
                    .append(DBStruct.APPQUEUE.QUEUE_TYPE).append(",")
                    .append(DBStruct.APPQUEUE.EDITED_VERSION_DESCRIPTION).append(")")
                    .append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueModel.getVersionId());
            statement.setLong(2, queueModel.getAppId());
            statement.setString(3, queueModel.getAppName());
            statement.setInt(4, queueModel.getCreator());
            statement.setString(5, queueModel.getDatabaseUrl());
            statement.setInt(6, queueModel.getDequeuePoolSize());
            statement.setString(7, queueModel.getSchemaName());
            statement.setString(8, queueModel.getSchemaPassword());
            statement.setInt(10, queueModel.getStatus());
            if (queueModel.getQueueType() == 1) {
                statement.setInt(9, queueModel.getSenderPoolSize());
                statement.setInt(11, queueModel.getThreshold());
            } else {
                statement.setNull(9, java.sql.Types.INTEGER);
                statement.setNull(11, java.sql.Types.INTEGER);
            }
            statement.setInt(12, queueModel.isTimeWindowFlag() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            if (queueModel.getTimeWindowFromHour() != null) {
                statement.setInt(13, queueModel.getTimeWindowFromHour());
            } else {
                statement.setNull(13, java.sql.Types.INTEGER);
            }
            if (queueModel.getTimeWindowFromMin() != null) {
                statement.setInt(14, queueModel.getTimeWindowFromMin());
            } else {
                statement.setNull(14, java.sql.Types.INTEGER);
            }
            if (queueModel.getTimeWindowToHour() != null) {
                statement.setInt(15, queueModel.getTimeWindowToHour());
            } else {
                statement.setNull(15, java.sql.Types.INTEGER);
            }
            if (queueModel.getTimeWindowToMin() != null) {
                statement.setInt(16, queueModel.getTimeWindowToMin());
            } else {
                statement.setNull(16, java.sql.Types.INTEGER);
            }
            statement.setString(17, queueModel.getQueueName());
            if (queueModel.getLastModifiedBy() != null) {
                statement.setInt(18, queueModel.getLastModifiedBy());
            } else {
                statement.setNull(18, java.sql.Types.INTEGER);
            }
            statement.setInt(19, queueModel.getQueueType());
            statement.setString(20, queueModel.getEditedVersionDescription());

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Creation Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [insertApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueue Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [insertApplicationQueue]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public Long updateParentApplicationQueue(Connection connection, QueueModel queueModel) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [updateParentApplicationQueue]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateParentApplicationQueue Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet rs = null;
        long startTime = System.currentTimeMillis();

        try {
            //update application queue with latest changes and approve
            sql = new StringBuilder();
            sql.append("Update ").append(DBStruct.APPQUEUE.TABLE_NAME).append(" set ")
                    .append(DBStruct.APPQUEUE.APP_NAME).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.CREATOR).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.DATABASE_URL).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.SCHEMA_NAME).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.SCHEMA_PASSWORD).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.SENDER_POOL_SIZE).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.THRESHOLD).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FLAG).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.QUEUE_NAME).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.LAST_MODIFIED_BY).append(" = ? ,")
                    .append(DBStruct.APPQUEUE.EDITED_VERSION_DESCRIPTION).append(" = ?")
                    .append(" where ").append(DBStruct.APPQUEUE.STATUS).append(" = ?")
                    .append(" and ").append(DBStruct.APPQUEUE.APP_ID).append(" = ?");

            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, queueModel.getAppName());
            statement.setInt(2, queueModel.getCreator());
            statement.setString(3, queueModel.getDatabaseUrl());
            statement.setString(4, queueModel.getSchemaName());
            statement.setString(5, queueModel.getSchemaPassword());
            statement.setInt(6, queueModel.getSenderPoolSize());
            statement.setInt(7, queueModel.getThreshold());
            statement.setInt(8, queueModel.isTimeWindowFlag() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(9, queueModel.getTimeWindowFromHour());
            statement.setInt(10, queueModel.getTimeWindowFromMin());
            statement.setInt(11, queueModel.getTimeWindowToHour());
            statement.setInt(12, queueModel.getTimeWindowToMin());
            statement.setInt(13, queueModel.getDequeuePoolSize());
            statement.setString(14, queueModel.getQueueName());
            statement.setInt(15, queueModel.getLastModifiedBy());
            statement.setString(16, queueModel.getEditedVersionDescription());
            statement.setInt(17, GeneralConstants.STATUS_APPROVED_VALUE);
            statement.setLong(18, queueModel.getAppId());

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateParentApplicationQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Updationg Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateParentApplicationQueue Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
            //return parent app queue id
            sql = new StringBuilder();
            sql.append("Select ").append(DBStruct.APPQUEUE.VERSION_ID).append(" From ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME).append(" Where ")
                    .append(DBStruct.APPQUEUE.APP_ID).append(" = ? ")
                    .append("and ").append(DBStruct.APPQUEUE.STATUS).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueModel.getAppId());
            statement.setInt(2, GeneralConstants.STATUS_APPROVED_VALUE);

            //System.out.println(sql.toString());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateParentApplicationQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            rs = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Updationg Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateParentApplicationQueue Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            if (rs.next()) {
                Long id = rs.getLong(DBStruct.APPQUEUE.VERSION_ID);
                return id;
            }

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [updateParentApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateParentApplicationQueue Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [updateParentApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [updateParentApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                rs.close(); // eslam.ahmed | 5-5-2020
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [updateParentApplicationQueue]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
        return null;
    }

    public void deleteChildApplicationQueue(Connection connection, QueueModel queueModel) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [deleteChildApplicationQueue]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteChildApplicationQueue Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.APPQUEUE.TABLE_NAME).append(" Where ")
                    .append(DBStruct.APPQUEUE.APP_ID).append(" = ?").append(" and ")
                    .append(DBStruct.APPQUEUE.STATUS).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueModel.getAppId());
            statement.setInt(2, GeneralConstants.STATUS_PENDING_VALUE);

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteChildApplicationQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Deletion Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [deleteChildApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteChildApplicationQueue Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteChildApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteChildApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [deleteChildApplicationQueue]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void deleteApplicationQueueSMSCs(Connection connection, QueueModel queueModel) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [updateApplicationQueueSMSC]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteApplicationQueueSMSCs Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.APP_QUEUES_SMSC.TABLE_NAME).append(" where ")
                    .append(DBStruct.APP_QUEUES_SMSC.APP_QUEUE_ID).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueModel.getVersionId());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteApplicationQueueSMSCs Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Deletion Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [deleteApplicationQueueSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteApplicationQueueSMSCs Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteApplicationQueueSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteApplicationQueueSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [deleteApplicationQueueSMSC]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void insertApplicationQueueSMSC(Connection connection, QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [insertApplicationQueueSMSC]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueueSMSC Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.APP_QUEUES_SMSC.TABLE_NAME).append(" where ")
                    .append(DBStruct.APP_QUEUES_SMSC.APP_QUEUE_ID).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueModel.getVersionId());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueueSMSC Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Deletion Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueueSMSC Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            startTime = System.currentTimeMillis();
            sql = new StringBuilder();
            sql.append("Insert into ").append(DBStruct.APP_QUEUES_SMSC.TABLE_NAME).append(" ( ")
                    .append(DBStruct.APP_QUEUES_SMSC.ID).append(",")
                    .append(DBStruct.APP_QUEUES_SMSC.APP_QUEUE_ID).append(",")
                    .append(DBStruct.APP_QUEUES_SMSC.SMSC_ID).append(",")
                    .append(DBStruct.APP_QUEUES_SMSC.CONNECTIVITY_ORDER)
                    //CSPhase1.5 | Esmail.Anbar | Adding Connectivity Order Update
                    .append(" ) VALUES (?,?,?,?)");
            statement = connection.prepareStatement(sql.toString());

            for (int i = 0; i < smscModels.size(); i++) {
                int id = CommonDAO.getNextId(connection, DBStruct.APP_QUEUES_SMSC.SEQUENCE);
                statement.setInt(1, id);
                statement.setLong(2, queueModel.getVersionId());
                statement.setLong(3, smscModels.get(i).getVersionId());
                //CSPhase1.5 | Esmail.Anbar | Adding Connectivity Order Update
                statement.setLong(4, i + 1);
                statement.addBatch();
            }
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueueSMSC Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeBatch();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Insertion Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [insertApplicationQueueSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueueSMSC Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertApplicationQueueSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertApplicationQueueSMSC]", ex);
            throw ex;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertApplicationQueueSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [insertApplicationQueueSMSC]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void deleteApplicationQueue(Connection connection, QueueModel queueModel) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [deleteApplicationQueue]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteApplicationQueue Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.APPQUEUE.TABLE_NAME).append(" Where ")
                    .append(DBStruct.APPQUEUE.APP_ID).append(" = ?").append(" and ")
                    .append(DBStruct.APPQUEUE.VERSION_ID).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueModel.getAppId());
            statement.setLong(2, queueModel.getVersionId());

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteApplicationQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Deletion Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [deleteApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteApplicationQueue Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [deleteApplicationQueue]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void deleteParentAndChildApplicationQueue(Connection connection, QueueModel queueModel) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [deleteParentAndChildApplicationQueue]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteParentAndChildApplicationQueue Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.APPQUEUE.TABLE_NAME).append(" Where ")
                    .append(DBStruct.APPQUEUE.APP_ID).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueModel.getAppId());

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteParentAndChildApplicationQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Deletion Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [deleteParentAndChildApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteParentAndChildApplicationQueue Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            e.getErrorCode();
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteParentAndChildApplicationQueue]", e);
            if (e.getErrorCode() == ErrorCodes.INTEGRITY_ERROR) {
                throw new CommonException("A reference to the queue was found.", ErrorCodes.INTEGRITY_CONSTRAINT_ERROR);
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteParentAndChildApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [deleteParentAndChildApplicationQueue]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public boolean getApplicationQueueByName(Connection connection, String name) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getApplicationQueueByName]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueueByName Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("Select * From ").append(DBStruct.APPQUEUE.TABLE_NAME).append(" Where ")
                    .append(DBStruct.APPQUEUE.APP_NAME).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, name);

//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueueByName Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Retrieving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getApplicationQueueByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueueByName Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueueByName]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueueByName]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueueByName]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public QueueModel getChildApplicationQueue(Connection connection, QueueModel parentQueue) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getChildApplicationQueue]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getChildApplicationQueue Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();

        try {

            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.APPQUEUE.TABLE_NAME)
                    .append(" where ").append(DBStruct.APPQUEUE.APP_ID).append(" = ?")
                    .append(" and ").append(DBStruct.APPQUEUE.STATUS).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, parentQueue.getAppId());
            statement.setInt(2, GeneralConstants.STATUS_PENDING_VALUE);
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getChildApplicationQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getChildApplicationQueue Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            QueueModel queueModel = new QueueModel();
            if (resultSet.next()) {
                queueModel.setVersionId(resultSet.getInt(DBStruct.APPQUEUE.VERSION_ID));
                queueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
                queueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                queueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
                queueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
                queueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
                queueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
                queueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
                queueModel.setDatabaseUrl(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
                queueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
                queueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));
                queueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
                queueModel.setQueueType(resultSet.getInt(DBStruct.APPQUEUE.QUEUE_TYPE));
                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
                    queueModel.setTimeWindowFlag(true);
                } else {
                    queueModel.setTimeWindowFlag(false);
                }
                queueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
                queueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
                queueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
                queueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));
            }
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getChildApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getChildApplicationQueue Ended").build());

            return queueModel;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getChildApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getChildApplicationQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getChildApplicationQueue]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public HashMap<String, QueueModel> getHashedApplicationQueues(Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getHashedApplicationQueues]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();
        try {
            sql = new StringBuilder();
//            sql.append("Select * from ").append(DBStruct.APPQUEUE.TABLE_NAME)
//                    .append(" inner join ").append(DBStruct.VFE_CS_USERS.TABLE_NAME)
//                    .append(" on ").append(DBStruct.APPQUEUE.CREATOR).append(" = ")
//                    .append(DBStruct.VFE_CS_USERS.USER_ID)
//                    .append(" Order by ").append(DBStruct.APPQUEUE.APP_ID)
//                    .append(" , ").append(DBStruct.APPQUEUE.VERSION_ID);
            ///////////////////////////
            sql.append("Select ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME)
                    .append(".* from ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME)
                    .append(" inner join ")
                    .append(DBStruct.VFE_CS_SERVICES.TABLE_NAME)
                    .append(" on ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME).append(".").append(DBStruct.APPQUEUE.VERSION_ID)
                    .append(" = ");
            sql.append(DBStruct.VFE_CS_SERVICES.TABLE_NAME).append(".").append(DBStruct.SERVICE.APP_ID);
            sql.append(" WHERE ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME).append(".").append(DBStruct.APPQUEUE.STATUS)
                    .append("=")
                    .append(GeneralConstants.STATUS_APPROVED_VALUE)
                    .append(" and ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME).append(".").append(DBStruct.APPQUEUE.QUEUE_TYPE)
                    .append("=")
                    .append(Defines.VFE_CS_QUEUES_TYPE_LK.SMS_SENDER_QUEUES);
            ///////////////
            statement = connection.prepareStatement(sql.toString());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            HashMap<String, QueueModel> queueModels = new HashMap<String, QueueModel>();
            while (resultSet.next()) {
                QueueModel queueModel = new QueueModel();
                queueModel.setVersionId(resultSet.getInt(DBStruct.APPQUEUE.VERSION_ID));
                queueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
                queueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                queueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
                queueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
                queueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
                queueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
                queueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
                queueModel.setDatabaseUrl(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
                //   queueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
                //  queueModel.setCreatorName(resultSet.getString(DBStruct.VFE_CS_USERS.USER_NAME));
                queueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));
                queueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
                queueModel.setQueueType(resultSet.getInt(DBStruct.APPQUEUE.QUEUE_TYPE));
                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
                    queueModel.setTimeWindowFlag(true);
                } else {
                    queueModel.setTimeWindowFlag(false);
                }
                queueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
                queueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
                queueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
                queueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));
                queueModel.setEditedVersionDescription(resultSet.getString(DBStruct.APPQUEUE.EDITED_VERSION_DESCRIPTION));
                queueModel.setSmscModels(this.getApplicationQueuesSMSCs(queueModel.getVersionId(), connection));
                queueModel.setQueueType(resultSet.getInt(DBStruct.APPQUEUE.QUEUE_TYPE));
                queueModels.put(queueModel.getAppName(), queueModel);
            }
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Application Queues List Size :" + queueModels.size());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getHashedApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, queueModels.size()).build());

            return queueModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getHashedApplicationQueues]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getHashedApplicationQueues]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getHashedApplicationQueues]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    //Esmail.Anbar | Cloud CR
    public HashMap<String, QueueModel> getApplicationQueuesServiceAndStatusApproved(Connection connection, int queueType) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getApplicationQueuesServiceAndStatusApproved]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesServiceAndStatusApproved Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();
        try {
            sql = new StringBuilder();
            sql.append("Select ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME)
                    .append(".* from ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME)
                    .append(" inner join ")
                    .append(DBStruct.VFE_CS_SERVICES.TABLE_NAME)
                    .append(" on ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME).append(".").append(DBStruct.APPQUEUE.VERSION_ID)
                    .append(" = ");
            if (queueType == Defines.VFE_CS_QUEUES_TYPE_LK.SMS_SENDER_QUEUES) {
                sql.append(DBStruct.VFE_CS_SERVICES.TABLE_NAME).append(".").append(DBStruct.SERVICE.APP_ID);
            } else {
                sql.append(DBStruct.VFE_CS_SERVICES.TABLE_NAME).append(".").append(DBStruct.SERVICE.SMS_PROCEDURE_QUEUE_ID);
            }
            sql.append(" WHERE ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME).append(".").append(DBStruct.APPQUEUE.STATUS)
                    .append("=")
                    .append(GeneralConstants.STATUS_APPROVED_VALUE)
                    .append(" and ")
                    .append(DBStruct.APPQUEUE.TABLE_NAME).append(".").append(DBStruct.APPQUEUE.QUEUE_TYPE)
                    .append("=")
                    .append(queueType);
//                    .append(" Order by ").append(DBStruct.APPQUEUE.APP_ID)
//                    .append(" , ").append(DBStruct.APPQUEUE.VERSION_ID);
            statement = connection.prepareStatement(sql.toString());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesServiceAndStatusApproved Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesServiceAndStatusApproved Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            HashMap<String, QueueModel> queueModels = new HashMap<>();
            while (resultSet.next()) {
                QueueModel queueModel = new QueueModel();
                queueModel.setVersionId(resultSet.getInt(DBStruct.APPQUEUE.VERSION_ID));
                queueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
                queueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                queueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
                queueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
                queueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
                queueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
                queueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
                queueModel.setDatabaseUrl(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
                queueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
//                queueModel.setCreatorName(resultSet.getString(DBStruct.VFE_CS_USERS.USER_NAME));
                queueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));
                queueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
                queueModel.setQueueType(resultSet.getInt(DBStruct.APPQUEUE.QUEUE_TYPE));
                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
                    queueModel.setTimeWindowFlag(true);
                } else {
                    queueModel.setTimeWindowFlag(false);
                }
                queueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
                queueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
                queueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
                queueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));
                queueModel.setEditedVersionDescription(resultSet.getString(DBStruct.APPQUEUE.EDITED_VERSION_DESCRIPTION));
                queueModel.setSmscModels(this.getApplicationQueuesSMSCs(queueModel.getVersionId(), connection));
                queueModel.setQueueType(resultSet.getInt(DBStruct.APPQUEUE.QUEUE_TYPE));
                queueModels.put(queueModel.getAppName(), queueModel);
            }
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Application Queues List Size :" + queueModels.size());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getApplicationQueuesServiceAndStatusApproved]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesServiceAndStatusApproved Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, queueModels.size()).build());

            return queueModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesServiceAndStatusApproved]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesServiceAndStatusApproved]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesServiceAndStatusApproved]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public HashMap<Long, QueueModel> getHashedApplicationById(Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getHashedApplicationQueues]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();
        try {
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.APPQUEUE.TABLE_NAME)
                    .append(" inner join ").append(DBStruct.VFE_CS_USERS.TABLE_NAME)
                    .append(" on ").append(DBStruct.APPQUEUE.CREATOR).append(" = ")
                    .append(DBStruct.VFE_CS_USERS.USER_ID)
                    .append(" Order by ").append(DBStruct.APPQUEUE.APP_ID)
                    .append(" , ").append(DBStruct.APPQUEUE.VERSION_ID);
            statement = connection.prepareStatement(sql.toString());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            HashMap<Long, QueueModel> queueModels = new HashMap<>();
            while (resultSet.next()) {
                QueueModel queueModel = new QueueModel();
                queueModel.setVersionId(resultSet.getInt(DBStruct.APPQUEUE.VERSION_ID));
                queueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
                queueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                queueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
                queueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
                queueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
                queueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
                queueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
                queueModel.setDatabaseUrl(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
                queueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
                queueModel.setCreatorName(resultSet.getString(DBStruct.VFE_CS_USERS.USER_NAME));
                queueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));
                queueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
                queueModel.setQueueType(resultSet.getInt(DBStruct.APPQUEUE.QUEUE_TYPE));
                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
                    queueModel.setTimeWindowFlag(true);
                } else {
                    queueModel.setTimeWindowFlag(false);
                }
                queueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
                queueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
                queueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
                queueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));
                queueModel.setEditedVersionDescription(resultSet.getString(DBStruct.APPQUEUE.EDITED_VERSION_DESCRIPTION));
                queueModel.setSmscModels(this.getApplicationQueuesSMSCs(queueModel.getVersionId(), connection));
                //Changed queueModels.put(queueModel.getAppId(), queueModel);
                queueModels.put(queueModel.getVersionId(), queueModel);
            }
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Application Queues List Size :" + queueModels.size());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getHashedApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, queueModels.size()).build());

            return queueModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getHashedApplicationQueues]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getHashedApplicationQueues]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getHashedApplicationQueues]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void insertAtQueue(Connection connection) {
//        Class.forName("oracle.AQ.AQOracleDriver");
//        AQSession aq_sess = null;
//        aq_sess = AQDriverManager.createAQSession(connection);
//        AQQueueTable q_table;
//        AQQueue queue;
//
//        /* Get a handle to queue table - aq_table1 in aqjava schema: */
//        q_table = aq_sess.getQueueTable("aqjava", "aq_table1");
//        System.out.println("Successful getQueueTable");
//
//        /* Get a handle to a queue - aq_queue1 in aqjava schema: */
//        queue = aq_sess.getQueue("aqjava", "aq_queue1");
//        System.out.println("Successful getQueue");

    }

    public int enqueeMsg(Connection connection, InputModel input, String ipaddr, QueueModel applicationQueue) throws CommonException {
        CallableStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;
        int csMsgId;
        int conc_msg_count = Utility.calcConcMsgCount(input.getMessageText().length(), input.getLanguage());
        try {
            sql.append("{CALL ").append(DBStruct.ENQ_SMS.PROCDURE_NAME).append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            statement = connection.prepareCall(sql.toString());
            statement.setString(1, applicationQueue.getAppName());
            statement.setString(2, input.getOriginatorMSISDN());
            statement.setString(3, input.getDestinationMSISDN());
            statement.setString(4, input.getMessageText());
            statement.setInt(5, input.getMessageType());
            statement.setInt(6, input.getOriginatorType());
            statement.setInt(7, input.getLanguage());
            statement.setInt(8, 0);
            statement.setInt(9, 0);//Unkown
            statement.setInt(10, conc_msg_count);
            statement.setInt(11, 0);//Unkown
            statement.setString(12, ipaddr);
            statement.setString(13, "");//Unkown
            statement.setInt(14, 0);//Unkown
            statement.setString(15, "");//Unkown           
            statement.registerOutParameter(16, Types.NUMERIC);

            statement.executeUpdate();
            csMsgId = statement.getInt(16); //from https://docs.oracle.com/javase/tutorial/jdbc/basics/storedprocedures.html search for ( String supplierName = cs.getString(2); )

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for updateAdsCounters()" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for updateAdsCounters()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for updateAdsCounters()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }
        return csMsgId;
    }

    public void executeQueueQuery(Connection connection, StringBuilder query) throws CommonException {
        PreparedStatement statement = null; // eslam.ahmed | 5-5-2020
        try {
            statement = connection.prepareStatement(query.toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for executeQueueQuery()" + ex);
            if (ex.getErrorCode() == ErrorCodes.TABLESPACE_ERROR) {
                throw new CommonException(ex.getMessage(), ErrorCodes.TABLESPACE_NOT_EXIST);
            } else if (ex.getErrorCode() == ErrorCodes.QUEUE_TABLE_ALREADY_EXISTS) {
                throw new CommonException(ex.getMessage(), ErrorCodes.QUEUE_TABLE_ALREADY_EXISTS);
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for executeQueueQuery()" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, statement);
        }
    }

    //2595 SMS Bridging interface
    public ArrayList<QueueModel> getApplicationQueuesBySetOfNames(Connection connection, List<String> queueNames, int queueType) throws CommonException {

//        CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Starting [getApplicationQueuesBySetOfNames]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesBySetOfNames Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();

        try {

            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.APPQUEUE.TABLE_NAME)
                    .append(" WHERE UPPER(").append(DBStruct.APPQUEUE.APP_NAME).append(") IN (");
            for (String name : queueNames) {
                sql.append("UPPER(?),");
            }
            sql.setCharAt(sql.lastIndexOf(","), ' ');
            sql.append(") AND ").append(DBStruct.APPQUEUE.QUEUE_TYPE).append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            int i;
            for (i = 0; i < queueNames.size(); i++) {
                statement.setString(i + 1, queueNames.get(i));
            }
            statement.setInt(i + 1, queueType);
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesBySetOfNames Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesBySetOfNames Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            ArrayList<QueueModel> queueModels = new ArrayList<>();
            while (resultSet.next()) {
                QueueModel queueModel = new QueueModel();
                queueModel.setVersionId(resultSet.getInt(DBStruct.APPQUEUE.VERSION_ID));
                queueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
                queueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                queueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
                queueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
                queueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
                queueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
                queueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
                queueModel.setDatabaseUrl(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
                queueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
                queueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));
                queueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
                queueModel.setQueueType(resultSet.getInt(DBStruct.APPQUEUE.QUEUE_TYPE));
                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
                    queueModel.setTimeWindowFlag(true);
                } else {
                    queueModel.setTimeWindowFlag(false);
                }
                queueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
                queueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
                queueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
                queueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));
                queueModel.setEditedVersionDescription(resultSet.getString(DBStruct.APPQUEUE.EDITED_VERSION_DESCRIPTION));
                queueModels.add(queueModel);
            }
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "Application Queues List Size :" + queueModels.size());
//            CommonLogger.businessLogger.debug(QueueDAO.class.getName() + " || " + "End [getApplicationQueuesBySetOfNames]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesBySetOfNames Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, queueModels.size()).build());

            return queueModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesBySetOfNames]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesBySetOfNames]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getApplicationQueuesBySetOfNames]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

}
