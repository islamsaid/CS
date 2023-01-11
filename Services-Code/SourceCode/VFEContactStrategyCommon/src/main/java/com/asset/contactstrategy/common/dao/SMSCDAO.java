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
import com.asset.contactstrategy.common.models.SMSCModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author rania.magdy
 */
public class SMSCDAO {

    public ArrayList<SMSCModel> getSMSCs(Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [getSMSCs] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCs Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;

        try {
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.SMSC.TABLE_NAME);
            sql.append(" order by ").append(DBStruct.SMSC.SMSC_ID);
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCs Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCs Executed")
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
                smscModel.setDescription(resultSet.getString(DBStruct.SMSC.DESCRIPTION));
                smscModel.setWindowSize(resultSet.getInt(DBStruct.SMSC.WINDOW_SIZE));
                smscModel.setThroughput(resultSet.getInt(DBStruct.SMSC.THROUGHPUT));
                smscModels.add(smscModel);
            }
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "SMSC List Size :" + smscModels.size());
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [getSMSCs] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCs Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, smscModels.size()).build());

            return smscModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [getSMSCs]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [getSMSCs]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [getSMSCs]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void editSMSC(Connection connection, SMSCModel sMSCModel) throws CommonException {

//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[editSMSC] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editSMSC Started").build());
        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        long startTime = System.currentTimeMillis();

        long id;
        try {
            id = CommonDAO.getNextId(connection,
                    DBStruct.SMSC.SMSC_ID_SEQUENCE);
            long groupID = sMSCModel.getSMSCid();
            sqlQuery = new StringBuilder();

            sqlQuery.append("INSERT INTO ");
            sqlQuery.append(DBStruct.SMSC.TABLE_NAME);
            sqlQuery.append(" ( ");
            sqlQuery.append(DBStruct.SMSC.VERSION_ID).append(", ");
            sqlQuery.append(DBStruct.SMSC.SMSC_ID)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.IP).append(", ");
            sqlQuery.append(DBStruct.SMSC.PASSWORD)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.PORT)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.SMSC_NAME)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.STATUS).append(
                    ", ");
            sqlQuery.append(DBStruct.SMSC.SYSTEM_TYPE)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.USERNAME)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.DESCRIPTION)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.WINDOW_SIZE)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.THROUGHPUT);
            sqlQuery.append(" ) ");
            sqlQuery.append(" VALUES ");
            sqlQuery.append(" (?,?,?,?,?,?,?,?,?,?,?,?) ");
            statement = connection.prepareStatement(sqlQuery.toString());
            statement.setLong(1, id);
            statement.setLong(2, groupID);
            statement.setString(3, sMSCModel.getIp());
            statement.setString(4, sMSCModel.getPassword());
            statement.setInt(5, sMSCModel.getPort());
            statement.setString(6, sMSCModel.getSMSCname());
            statement.setInt(7, GeneralConstants.STATUS_PENDING_VALUE);
            statement.setString(8, sMSCModel.getSystemType());
            statement.setString(9, sMSCModel.getUsername());
            statement.setString(10, sMSCModel.getDescription());
            statement.setInt(11, sMSCModel.getWindowSize());
            statement.setInt(12, sMSCModel.getThroughput());
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sqlQuery.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editSMSC Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery.toString()).build());

            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Updating Date time :" + (System.currentTimeMillis() - startTime));
//
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[editSMSC] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editSMSC Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [editSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [editSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [editSMSC]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void editEditedSMSC(Connection connection, SMSCModel sMSCModel) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[editEditedSMSC] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedSMSC Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("update ").append(DBStruct.SMSC.TABLE_NAME)
                    .append(" set ").append(DBStruct.SMSC.IP).append(" = '")
                    .append(sMSCModel.getIp()).append("' , ")
                    .append(DBStruct.SMSC.PASSWORD).append(" = '")
                    .append(sMSCModel.getPassword()).append("' , ")
                    .append(DBStruct.SMSC.PORT).append(" = ")
                    .append(sMSCModel.getPort()).append(" , ")
                    .append(DBStruct.SMSC.SYSTEM_TYPE).append(" = '")
                    .append(sMSCModel.getSystemType()).append("' , ")
                    .append(DBStruct.SMSC.USERNAME).append(" = '")
                    .append(sMSCModel.getUsername()).append("' , ")
                    .append(DBStruct.SMSC.STATUS).append(" = ")
                    .append(sMSCModel.getStatus()).append(" , ")
                    .append(DBStruct.SMSC.DESCRIPTION).append(" = '")
                    .append(sMSCModel.getDescription()).append("' , ")
                    .append(DBStruct.SMSC.WINDOW_SIZE).append(" = ")
                    .append(sMSCModel.getWindowSize()).append(" , ")
                    .append(DBStruct.SMSC.THROUGHPUT).append(" = ")
                    .append(sMSCModel.getThroughput())
                    .append(" where ")
                    .append(DBStruct.SMSC.VERSION_ID).append(" = ")
                    .append(sMSCModel.getVersionId());

//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedSMSC Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [editEditedSMSC] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedSMSC Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [editEditedSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [editEditedSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [editEditedSMSC]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public SMSCModel createSMSC(Connection connection, SMSCModel sMSCModel) throws CommonException {

//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[createSMSC] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createSMSC Started").build());
        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        long startTime = System.currentTimeMillis();

        long id;
        try {
            id = CommonDAO.getNextId(connection,
                    DBStruct.SMSC.ID_SEQUENCE);
            long groupID = CommonDAO.getNextId(connection,
                    DBStruct.SMSC.SMSC_ID_SEQUENCE);
            sMSCModel.setVersionId(id);
            sMSCModel.setSMSCid(groupID);
            sqlQuery = new StringBuilder();

            sqlQuery.append("INSERT INTO ");
            sqlQuery.append(DBStruct.SMSC.TABLE_NAME);
            sqlQuery.append(" ( ");
            sqlQuery.append(DBStruct.SMSC.VERSION_ID).append(", ");
            sqlQuery.append(DBStruct.SMSC.SMSC_ID)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.IP).append(", ");
            sqlQuery.append(DBStruct.SMSC.PASSWORD)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.PORT)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.SMSC_NAME)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.STATUS).append(
                    ", ");
            sqlQuery.append(DBStruct.SMSC.SYSTEM_TYPE)
                    .append(", ");
            sqlQuery.append(DBStruct.SMSC.USERNAME).append(", ");
            sqlQuery.append(DBStruct.SMSC.CREATOR).append(", ");
            sqlQuery.append(DBStruct.SMSC.DESCRIPTION).append(", ");
            sqlQuery.append(DBStruct.SMSC.WINDOW_SIZE).append(", ");
            sqlQuery.append(DBStruct.SMSC.THROUGHPUT);
            sqlQuery.append(" ) ");
            sqlQuery.append(" VALUES ");
            sqlQuery.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            statement = connection.prepareStatement(sqlQuery.toString());
            statement.setLong(1, id);
            statement.setLong(2, groupID);
            statement.setString(3, sMSCModel.getIp());
            statement.setString(4, sMSCModel.getPassword());
            statement.setInt(5, sMSCModel.getPort());
            statement.setString(6, sMSCModel.getSMSCname());
            statement.setInt(7, sMSCModel.getStatus());
            statement.setString(8, sMSCModel.getSystemType());
            statement.setString(9, sMSCModel.getUsername());
            statement.setLong(10, sMSCModel.getCreator());
            statement.setString(11, sMSCModel.getDescription());
            statement.setInt(12, sMSCModel.getWindowSize());
            statement.setInt(13, sMSCModel.getThroughput());
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sqlQuery.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createSMSC Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery.toString()).build());

            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Updating Date time :" + (System.currentTimeMillis() - startTime));
//
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [createSMSC] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createSMSC Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
            return sMSCModel;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [createSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [createSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [createSMSC]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public SMSCModel retrieveConnectedSMSC(Connection connection, SMSCModel sMSCModel) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[retrieveConnectedSMSC] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedSMSC Started").build());
        // boolean hasChild = false;
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        SMSCModel returnedModel = null;
        try {

            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.SMSC.TABLE_NAME)
                    .append(" where ").append(DBStruct.SMSC.SMSC_ID)
                    .append(" = ").append(sMSCModel.getSMSCid()).append(" and ")
                    .append(DBStruct.SMSC.VERSION_ID).append(" <> ")
                    .append(sMSCModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedSMSC Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                returnedModel = new SMSCModel();
                returnedModel.setVersionId(resultSet.getInt(DBStruct.SMSC.VERSION_ID));
                returnedModel.setSMSCid(resultSet.getInt(DBStruct.SMSC.SMSC_ID));
                returnedModel.setSMSCname(resultSet.getString(DBStruct.SMSC.SMSC_NAME));
                returnedModel.setIp(resultSet.getString(DBStruct.SMSC.IP));
                returnedModel.setPort(resultSet.getInt(DBStruct.SMSC.PORT));
                returnedModel.setSystemType(resultSet.getString(DBStruct.SMSC.SYSTEM_TYPE));
                returnedModel.setUsername(resultSet.getString(DBStruct.SMSC.USERNAME));
                returnedModel.setPassword(resultSet.getString(DBStruct.SMSC.PASSWORD));
                returnedModel.setCreator(resultSet.getInt(DBStruct.SMSC.CREATOR));
                returnedModel.setStatus(resultSet.getInt(DBStruct.SMSC.STATUS));
                returnedModel.setDescription(resultSet.getString(DBStruct.SMSC.DESCRIPTION));
                returnedModel.setWindowSize(resultSet.getInt(DBStruct.SMSC.WINDOW_SIZE));
                returnedModel.setThroughput(resultSet.getInt(DBStruct.SMSC.THROUGHPUT));

            }
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [retrieveConnectedSMSC] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedSMSC Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + " ---->  for [retrieveConnectedSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + " ---->  for [retrieveConnectedSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [retrieveConnectedSMSC]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
        return returnedModel;
    }

    public void deleteSMSC(Connection connection, SMSCModel sMSCModel) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[deleteSMSC] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSC Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("delete  from ").append(DBStruct.SMSC.TABLE_NAME)
                    .append(" where ")
                    .append(DBStruct.SMSC.VERSION_ID).append(" = ")
                    .append(sMSCModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSC Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [deleteSMSC] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSC Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [deleteSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [deleteSMSC]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [deleteSMSC]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    // added by KerollosAsaad
    public void deleteSMSCsQueue(Connection connection, SMSCModel sMSCModel) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[deleteSMSCsQueue] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSCsQueue Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {
            sql = new StringBuilder();
            sql.append("delete from ").append(DBStruct.APP_QUEUES_SMSC.TABLE_NAME)
                    .append(" where ")
                    .append(DBStruct.APP_QUEUES_SMSC.SMSC_ID).append(" = ").append(sMSCModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSCsQueue Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [deleteSMSCsQueue] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSCsQueue Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [deleteSMSCsQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [deleteSMSCsQueue]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [deleteSMSCsQueue]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void ChangeSMSCStatusToDelete(Connection connection, SMSCModel sMSCModel) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[ChangeSMSCStatusToDelete] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeSMSCStatusToDelete Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("update ").append(DBStruct.SMSC.TABLE_NAME)
                    .append(" set ").append(DBStruct.SMSC.STATUS).append(" = ")
                    .append(GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE)
                    .append(" where ")
                    .append(DBStruct.SMSC.VERSION_ID).append(" = ")
                    .append(sMSCModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeSMSCStatusToDelete Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [ChangeSMSCStatusToDelete] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeSMSCStatusToDelete Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [ChangeSMSCStatusToDelete]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [ChangeSMSCStatusToDelete]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [ChangeSMSCStatusToDelete]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public ArrayList<SMSCModel> getApprovedSMSCs(Connection connection) throws
            CommonException {

//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || "
//                + "Starting [getApprovedSMSCs]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedSMSCs Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;

        try {

            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.SMSC.TABLE_NAME)
                    .append(" where ").append(DBStruct.SMSC.STATUS)
                    .append(" = ")
                    .append(GeneralConstants.STATUS_APPROVED_VALUE);
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();

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
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "End [getApprovedSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedSMSCs Ended").build());

            return smscModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [getApprovedSMSCs]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [getApprovedSMSCs]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [getApprovedSMSCs]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public SMSCModel getSMSCByName(Connection connection, String name) throws
            CommonException {
//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [getSMSCByName] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCByName Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        SMSCModel smscModel = null;
        try {
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.SMSC.TABLE_NAME);
            sql.append(" where ").append(DBStruct.SMSC.SMSC_NAME).append(" = '").append(name).append("'");
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCByName Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCByName Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            if (resultSet.next()) {
                smscModel = new SMSCModel();
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
                smscModel.setDescription(resultSet.getString(DBStruct.SMSC.DESCRIPTION));
                smscModel.setWindowSize(resultSet.getInt(DBStruct.SMSC.WINDOW_SIZE));
                smscModel.setThroughput(resultSet.getInt(DBStruct.SMSC.THROUGHPUT));

            }
            //CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "SMSC List Size :" + smscModels.size());
//            CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + " [getSMSCByName] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCByName Ended").build());

            return smscModel;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [getSMSCByName]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [getSMSCByName]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [getSMSCByName]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }
}
