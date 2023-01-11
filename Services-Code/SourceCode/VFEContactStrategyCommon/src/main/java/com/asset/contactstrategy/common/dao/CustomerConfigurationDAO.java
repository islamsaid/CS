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
import com.asset.contactstrategy.common.models.CustomerConfigurationModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Ahmed Hisham
 */
public class CustomerConfigurationDAO {

    public CustomerConfigurationModel checkMSISDN(String msisdn, Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkMSISDN...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting checkMSISDN").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        CustomerConfigurationModel configurationModel = null;

        try {

            sql = new StringBuilder();
            sql.append("SELECT ");
            sql.append("*");
            sql.append(" FROM ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.TABLE_NAME);
            sql.append(" WHERE ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MSISDN);
            sql.append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, msisdn);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                configurationModel = new CustomerConfigurationModel();
                configurationModel.setDailyThreshold(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_SMS));
                configurationModel.setWeeklyThreshold(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_SMS));
                configurationModel.setMounthlyThreshold(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_SMS));
                configurationModel.setDailyCampain(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_ADS));
                configurationModel.setWeeklyCampain(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_ADS));
                configurationModel.setMounthlyCampain(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_ADS));
                if (resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DO_NOT_CONTACT) == GeneralConstants.TRUE) {
                    configurationModel.setDoNotContact(true);
                } else {
                    configurationModel.setDoNotContact(false);
                }

            }

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "checkMSISDN...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkMSISDN ").build());

            return configurationModel;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkMSISDN" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkMSISDN" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);

        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkMSISDN" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void insertCustomerConfig(String msisdn, CustomerConfigurationModel configurationModel, Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting insertCustomerConfig...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting insertCustomConfig ").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("INSERT INTO ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.TABLE_NAME);
            sql.append(" ( ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MSISDN);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DO_NOT_CONTACT);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_SMS);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_SMS);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_SMS);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_ADS);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_ADS);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_ADS);
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.LAST_TWO_DIGITS);
            sql.append(" ) ");
            sql.append(" VALUES ");
            sql.append(" ( ? , ? , ? , ? , ? , ? , ? , ? , ? ) ");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, msisdn);
            if (configurationModel.isDoNotContact()) {
                statement.setInt(2, GeneralConstants.TRUE);
            } else {
                statement.setInt(2, GeneralConstants.FALSE);
            }

            statement.setInt(3, configurationModel.getDailyThreshold());
            statement.setInt(4, configurationModel.getWeeklyThreshold());
            statement.setInt(5, configurationModel.getMounthlyThreshold());
            statement.setInt(6, configurationModel.getDailyCampain());
            statement.setInt(7, configurationModel.getWeeklyCampain());
            statement.setInt(8, configurationModel.getMounthlyCampain());
            statement.setInt(9, Integer.parseInt(msisdn.substring(msisdn.length() - 2)));
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "insertCustomerConfig...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertCustomerConfig ").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for insertCustomerConfig" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for insertCustomerConfig" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);

        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for insertCustomerConfig" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void updateCustomerConfig(String msisdn, CustomerConfigurationModel configurationModel, Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting updateCustomerConfig...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting updateCustomerConfig").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append(" UPDATE ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.TABLE_NAME);
            sql.append(" SET ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DO_NOT_CONTACT);
            sql.append(" = ? ");
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_SMS);
            sql.append(" = ? ");
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_SMS);
            sql.append(" = ? ");
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_SMS);
            sql.append(" = ? ");
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_ADS);
            sql.append(" = ? ");
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_ADS);
            sql.append(" = ? ");
            sql.append(" , ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_ADS);
            sql.append(" = ? ");
            sql.append(" WHERE ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MSISDN);
            sql.append(" = ? ");

            statement = connection.prepareStatement(sql.toString());
            if (configurationModel.isDoNotContact()) {
                statement.setInt(1, GeneralConstants.TRUE);
            } else {
                statement.setInt(1, GeneralConstants.FALSE);
            }
            statement.setInt(2, configurationModel.getDailyThreshold());
            statement.setInt(3, configurationModel.getWeeklyThreshold());
            statement.setInt(4, configurationModel.getMounthlyThreshold());
            statement.setInt(5, configurationModel.getDailyCampain());
            statement.setInt(6, configurationModel.getWeeklyCampain());
            statement.setInt(7, configurationModel.getMounthlyCampain());
            statement.setString(8, msisdn);
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "updateCustomerConfig...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerConfig ").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for updateCustomerConfig" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for updateCustomerConfig" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);

        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for updateCustomerConfig" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void deleteCustomerConfig(String msisdn, Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting deleteCustomerConfig...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting deleteCustomerConfig").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append(" DELETE ");
            sql.append(" FROM ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.TABLE_NAME);
            sql.append(" WHERE ");
            sql.append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MSISDN);
            sql.append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, msisdn);
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "deleteCustomerConfig...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCustomConfig").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for deleteCustomerConfig" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for deleteCustomerConfig" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);

        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for deleteCustomerConfig" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }
}
