/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.ReportsModel;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ReportsDAO {

    public String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        return dateFormat.format(date);

    }

    public int getNumberOfRecievedSMSPerCustomer(ReportsModel reportsModel, Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting getNumberOfRecievedSMS...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getNumberOfRecievedSMS Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        int count = 0;

        try {

            sql = new StringBuilder();
            sql.append("select ");
            sql.append(" count(").append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID).append(") ");
            sql.append(" as ");
            sql.append(Defines.COUNT);
            sql.append(" from ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(" where ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" >= TO_DATE(?,'dd/mm/yyyy hh12:mi:ss am') ");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" <= TO_DATE(?,'dd/mm/yyyy  hh12:mi:ss am') ");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.MSISDN);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.STATUS);
            sql.append(" IN(?,?)");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, getDate(reportsModel.getFromDate()));
            statement.setString(2, getDate(reportsModel.getToDate()));
            statement.setString(3, reportsModel.getMsisdn());
            statement.setInt(4, Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC);
            statement.setInt(5, Defines.VFE_CS_SMS_H_STATUS_LK.DELIVERED);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                count = resultSet.getInt(Defines.COUNT);
            }

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "getNumberOfRecievedSMS...");
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Query : " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getNumberOfRecievedSMS Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());

            return count;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNumberOfRecievedSMS" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNumberOfRecievedSMS" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);

        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNumberOfRecievedSMS" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }

    }

    public int getNumberOfViolationSMSPerCustomer(ReportsModel reportsModel, Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting getNumberOfViolationSMS...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getNumberOfViolationSMS Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        int count = 0;

        try {

            sql = new StringBuilder();
            sql.append("select ");
            sql.append("count(").append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID).append(") ");
            sql.append(" as ");
            sql.append(Defines.COUNT);
            sql.append(" from ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(" where ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" >= TO_DATE(?,'dd/mm/yyyy hh12:mi:ss am')");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" <= TO_DATE(?,'dd/mm/yyyy  hh12:mi:ss am') ");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.MSISDN);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.MESSAGE_VIOLATION_FLAG);
            sql.append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, getDate(reportsModel.getFromDate()));
            statement.setString(2, getDate(reportsModel.getToDate()));
            statement.setString(3, reportsModel.getMsisdn());
            statement.setInt(4, Defines.VIOLATED);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                count = resultSet.getInt(Defines.COUNT);
            }

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "getNumberOfViolationSMS...");
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Query : " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getNumberOfViolationSMS Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());

            return count;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNumberOfViolationSMS" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNumberOfViolationSMS" + e);
            throw new CommonException(e.getMessage(), 123);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNumberOfViolationSMS" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }

    }

    public int recievedSMSByCSPCount(ReportsModel reportsModel, Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting recievedSMSByCSPCount...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "recievedSMSByCSPCount Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        int count = 0;

        try {

            sql = new StringBuilder();
            sql.append("select ");
            sql.append("count(").append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID).append(") ");
            sql.append("as ");
            sql.append(Defines.COUNT);
            sql.append(" from ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(" where ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" >= TO_DATE (?,'dd/mm/yyyy hh12:mi:ss am')");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" <= TO_DATE (?,'dd/mm/yyyy  hh12:mi:ss am') ");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, getDate(reportsModel.getFromDate()));
            statement.setString(2, getDate(reportsModel.getToDate()));
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                count = resultSet.getInt(Defines.COUNT);
            }

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "recievedSMSByCSPCount...");
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Query : " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "recievedSMSByCSPCount Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());

            return count;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for recievedSMSByCSPCount" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for recievedSMSByCSPCount" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);

        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for recievedSMSByCSPCount" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }

    }

    public ReportsModel getNoOfSMSByPlatform(ReportsModel reportsModel, Connection connection) throws CommonException {

        ReportsModel model = null;
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting getNoOfSMSByPlatform...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getNoOfSMSByPlatform Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;

        try {

            sql = new StringBuilder();
            sql.append("select DISTINCT ");
            sql.append(" (SELECT count(").append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID).append(") ");
            sql.append(" from ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(" where ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" >= TO_DATE(?,'dd/mm/yyyy hh12:mi:ss am')");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" <= TO_DATE(?,'dd/mm/yyyy  hh12:mi:ss am') ");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.MSISDN);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SERVICE_ID);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.STATUS);
            sql.append(" = ? )");
            sql.append(" as ");
            sql.append(Defines.COUNT);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SMS_H.SYSTEM_CATEGORY);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_SERVICES.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SERVICES.SERVICE_NAME);
            sql.append(" from ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_SERVICES.TABLE_NAME);
            sql.append(" where ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SMS_H.SERVICE_ID);
            sql.append(" = ");
            sql.append(DBStruct.VFE_CS_SERVICES.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SERVICES.VERSION_ID);
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" >= TO_DATE(?,'dd/mm/yyyy hh12:mi:ss am')");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" <= TO_DATE(?,'dd/mm/yyyy  hh12:mi:ss am') ");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.MSISDN);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SMS_H.SERVICE_ID);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SMS_H.STATUS);
            sql.append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, getDate(reportsModel.getFromDate()));
            statement.setString(2, getDate(reportsModel.getToDate()));
            statement.setString(3, reportsModel.getMsisdn());
            statement.setInt(4, reportsModel.getServiceId());
            statement.setInt(5, reportsModel.getMessageStatus());
            statement.setString(6, getDate(reportsModel.getFromDate()));
            statement.setString(7, getDate(reportsModel.getToDate()));
            statement.setString(8, reportsModel.getMsisdn());
            statement.setInt(9, reportsModel.getServiceId());
            statement.setInt(10, reportsModel.getMessageStatus());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                model = new ReportsModel();
                model.setCount(resultSet.getInt(Defines.COUNT));
                model.setServiceName(resultSet.getString(Defines.SERVICE_NAME));
                if ((resultSet.getInt(Defines.SYSTEM_PRIORITY)) == Defines.HIGH) {
                    model.setSystemPriority(Defines.HIGH_PRIORITY);
                } else {
                    model.setSystemPriority(Defines.NORMAL_PRIORITY);
                }

            }

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "getNoOfSMSByPlatform...");
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Query : " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getNoOfSMSByPlatform Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());

            return model;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNoOfSMSByPlatform" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNoOfSMSByPlatform" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);

        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNoOfSMSByPlatform" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }

    }

    public ReportsModel getNoOfSMSToSMSC(ReportsModel reportsModel, Connection connection) throws CommonException {

        ReportsModel model = null;
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting getNoOfSMSToSMSC...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getNoOfSMSToSMSC Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;

        try {

            sql = new StringBuilder();
            sql.append("select DISTINCT ");
            sql.append(" (SELECT count(").append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID).append(") ");
            sql.append(" from ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(" where ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" >= TO_DATE(?,'dd/mm/yyyy hh12:mi:ss am')");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" <= TO_DATE(?,'dd/mm/yyyy  hh12:mi:ss am') ");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.MSISDN);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SERVICE_ID);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SMSC_ID);
            sql.append(" = ? )");
            sql.append(" as ");
            sql.append(Defines.COUNT);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SMS_H.SYSTEM_CATEGORY);
            sql.append(" , ");
            sql.append(DBStruct.SERVICE.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.SERVICE.SERVICE_NAME);
            sql.append(" , ");
            sql.append(DBStruct.SMSC.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.SMSC.SMSC_NAME);
            sql.append(" from ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(" , ");
            sql.append(DBStruct.SMSC.TABLE_NAME);
            sql.append(" , ");
            sql.append(DBStruct.SERVICE.TABLE_NAME);
            sql.append(" where ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.SERVICE.SERVICE_ID);
            sql.append(" = ");
            sql.append(DBStruct.SERVICE.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.SERVICE.VERSION_ID);
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.SMSC.SMSC_ID);
            sql.append(" = ");
            sql.append(DBStruct.SMSC.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.SMSC.VERSION_ID);
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" >= TO_DATE(?,'dd/mm/yyyy hh12:mi:ss am')");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE);
            sql.append(" <= TO_DATE(?,'dd/mm/yyyy  hh12:mi:ss am') ");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.MSISDN);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SMS_H.SERVICE_ID);
            sql.append(" = ?");
            sql.append(" and ");
            sql.append(DBStruct.VFE_CS_SMS_H.TABLE_NAME);
            sql.append(".");
            sql.append(DBStruct.VFE_CS_SMS_H.SMSC_ID);
            sql.append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, getDate(reportsModel.getFromDate()));
            statement.setString(2, getDate(reportsModel.getToDate()));
            statement.setString(3, reportsModel.getMsisdn());
            statement.setInt(4, reportsModel.getServiceId());
            statement.setInt(5, reportsModel.getSmscId());
            statement.setString(6, getDate(reportsModel.getFromDate()));
            statement.setString(7, getDate(reportsModel.getToDate()));
            statement.setString(8, reportsModel.getMsisdn());
            statement.setInt(9, reportsModel.getServiceId());
            statement.setInt(10, reportsModel.getSmscId());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {

                model = new ReportsModel();
                model.setCount(resultSet.getInt(Defines.COUNT));
                model.setSmscName(resultSet.getString(DBStruct.SMSC.SMSC_NAME));
                model.setServiceName(resultSet.getString(DBStruct.SERVICE.SERVICE_NAME));
                if ((resultSet.getInt(DBStruct.VFE_CS_SMS_H.SYSTEM_CATEGORY)) == Defines.HIGH) {
                    model.setSystemPriority(Defines.HIGH_PRIORITY);
                } else {
                    model.setSystemPriority(Defines.NORMAL_PRIORITY);
                }

            }

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "getNoOfSMSToSMSC...");
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Query : " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getNoOfSMSToSMSC Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());

            return model;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNoOfSMSToSMSC" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNoOfSMSToSMSC" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);

        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNoOfSMSToSMSC" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }

    }
}
