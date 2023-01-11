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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Administrator
 */
public class CustomerStatisticsDAO {

    public void resetStatisticsCounterColumn(Connection connection, String tableName, int columnId, int lastMSISDNTwoDigits) throws CommonException {
        long startime = System.currentTimeMillis();
        PreparedStatement pstm = null;
        StringBuilder counter = new StringBuilder();
        counter.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.COUNTER_COLUMN_NAME);
        try {
            String query = " Update " + tableName + " SET "
                    + counter.toString() + " = 0  Where "
                    + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + " = ?";
            //TODO
            //if MSISDN_MOD_X Column Name Changed in VFE_CS_ADS_CUST_STATISTICS then edit code
            pstm = connection.prepareStatement(query);
            pstm.setInt(1, lastMSISDNTwoDigits);
            pstm.executeUpdate();
//            CommonLogger.businessLogger.debug("Reset Statistics Counter Column in [" + (System.currentTimeMillis() - startime) + "]mssec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reset Statistics Counter Column")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [resetStatisticsCounterColumn] Failed columnId=[" + columnId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [resetStatisticsCounterColumn] Failed columnId=[" + columnId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstm);
        }
    }

    public boolean resetStatisticsCounterColumnTable(Connection connection, String tableName, int columnId) throws CommonException {
        long startime = System.currentTimeMillis();
        boolean success = false;
        PreparedStatement pstm = null;
        StringBuilder counter = new StringBuilder();
        counter.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.COUNTER_COLUMN_NAME);
        try {
            String query = " Update " + tableName + " SET "
                    + counter.toString() + " = 0";
            pstm = connection.prepareStatement(query);
            pstm.executeUpdate();
//            CommonLogger.businessLogger.debug("Reset Statistics Counter Column entire Table in [" + (System.currentTimeMillis() - startime) + "]mssec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reset Statistics Counter Column Entire Table")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
            success = true;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [resetStatisticsCounterColumnTable] Failed columnId=[" + columnId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [resetStatisticsCounterColumnTable] Failed columnId=[" + columnId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstm);
        }
        return success;
    }

    public void copyCounterToTemp(Connection connection, String tableName, int columnId, int lastMSISDNTwoDigits) throws CommonException {
        long startime = System.currentTimeMillis();
        PreparedStatement pstm = null;
        StringBuilder temp = new StringBuilder();
        temp.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.TEMP_COLUMN_NAME);
        StringBuilder counter = new StringBuilder();
        counter.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.COUNTER_COLUMN_NAME);
        try {
            String query = " Update " + tableName + " SET "
                    + temp.toString() + " = " + counter.toString()
                    + " Where "
                    + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + " = ?";
            //TODO
            //if MSISDN_MOD_X Column Name Changed in VFE_CS_ADS_CUST_STATISTICS then edit code

            pstm = connection.prepareStatement(query);
            pstm.setInt(1, lastMSISDNTwoDigits);
            pstm.executeUpdate();
//            CommonLogger.businessLogger.debug("copy Counter To Temp in [" + (System.currentTimeMillis() - startime) + "]mssec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Copy Counter to Temp")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [copyCounterToTemp] Failed columnId=[" + columnId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [copyCounterToTemp] Failed columnId=[" + columnId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstm);
        }
    }

    public boolean copyCounterToTempFullTable(Connection connection, String tableName, int columnId) throws CommonException {
        long startime = System.currentTimeMillis();
        boolean success = false;
        PreparedStatement pstm = null;
        StringBuilder temp = new StringBuilder();
        temp.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.TEMP_COLUMN_NAME);
        StringBuilder counter = new StringBuilder();
        counter.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.COUNTER_COLUMN_NAME);
        try {
            String query = " Update " + tableName + " SET "
                    + temp.toString() + " = " + counter.toString();
            pstm = connection.prepareStatement(query);
            pstm.executeUpdate();
//            CommonLogger.businessLogger.debug("copy Counter To Temp Full Table in [" + (System.currentTimeMillis() - startime) + "]mssec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Copy Counter to Temp full Table")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
            success = true;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [copyCounterToTempFullTable] Failed columnId=[" + columnId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [copyCounterToTempFullTable] Failed columnId=[" + columnId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstm);
        }
        return success;
    }

//    public void updateSMSCustomerStatistics(Connection connection, int columnId, int lastMSISDNTwoDigits) throws CommonException {
//        long startime = System.currentTimeMillis();
//        CallableStatement callableStatement = null;
//        try {
//            String procedureName = DBStruct.PRO_UPDATE_SMS_CUSTOMER_STATISTICS;
//            // create Statement for querying database
//
//            callableStatement
//                    = connection.prepareCall("call "
//                            + procedureName + "(?,?)");
//            callableStatement.setInt(1, columnId);
//            callableStatement.setInt(2, lastMSISDNTwoDigits);
//            callableStatement.executeQuery();
//
//            CommonLogger.businessLogger.info("Finished updateSMSCustomerStatistics in [" + (System.currentTimeMillis() - startime) + "] msec");
//        } catch (SQLException e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateSMSCustomerStatistics] columnId=[" + columnId + "] LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "]" + e, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateSMSCustomerStatistics] columnId=[" + columnId + "] LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "]" + e, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            DataSourceManger.closeDBResources(null, null, callableStatement);
//        }
//    }
//
//    public void updateADSCustomerStatistics(Connection connection, int columnId, int lastMSISDNTwoDigits) throws CommonException {
//        long startime = System.currentTimeMillis();
//        CallableStatement callableStatement = null;
//        try {
//            String procedureName = DBStruct.PRO_UPDATE_ADS_CUSTOMER_STATISTICS;
//            // create Statement for querying database
//
//            callableStatement
//                    = connection.prepareCall("call "
//                            + procedureName + "(?,?)");
//            callableStatement.setInt(1, columnId);
//            callableStatement.setInt(2, lastMSISDNTwoDigits);
//            callableStatement.executeQuery();
//
//            CommonLogger.businessLogger.info("Finished updateADSCustomerStatistics in [" + (System.currentTimeMillis() - startime) + "] msec");
//        } catch (SQLException e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateADSCustomerStatistics] columnId=[" + columnId + "] LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "]" + e, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateADSCustomerStatistics] columnId=[" + columnId + "] LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "]" + e, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            DataSourceManger.closeDBResources(null, null, callableStatement);
//        }
//    }
//
//    public boolean updateSMSStatisticsTable(Connection connection, int columnId) throws CommonException {
//        boolean success = false;
//        long startime = System.currentTimeMillis();
//        PreparedStatement pstm = null;
//        StringBuilder temp = new StringBuilder();
//        temp.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.TEMP_COLUMN_NAME);
//        StringBuilder counter = new StringBuilder();
//        counter.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.COUNTER_COLUMN_NAME);
//        try {
//            String query = " Update " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME + " SET "
//                    + temp.toString() + " = " + counter.toString() + " , " + counter.toString() + " = 0";
//            pstm = connection.prepareStatement(query);
//            pstm.executeUpdate();
//            CommonLogger.businessLogger.debug("Update SMS Customers Statistics in [" + (System.currentTimeMillis() - startime) + "]mssec");
//            success = true;
//        } catch (SQLException e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateSMSStatisticsTable] Failed columnId=[" + columnId + "]" + e, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateSMSStatisticsTable] Failed columnId=[" + columnId + "]" + e, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            DataSourceManger.closeDBResources(null, pstm);
//        }
//        return success;
//    }
//
//    public boolean updateADSStatisticsTable(Connection connection, int columnId) throws CommonException {
//        boolean success = false;
//        long startime = System.currentTimeMillis();
//        PreparedStatement pstm = null;
//        StringBuilder temp = new StringBuilder();
//        temp.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.TEMP_COLUMN_NAME);
//        StringBuilder counter = new StringBuilder();
//        counter.append(GeneralConstants.DAY_COLUMN_NAME).append("_").append(columnId).append("_").append(GeneralConstants.COUNTER_COLUMN_NAME);
//        try {
//            String query = " Update " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME + " SET "
//                    + temp.toString() + " = " + counter.toString() + " , " + counter.toString() + " = 0";
//            pstm = connection.prepareStatement(query);
//            pstm.executeUpdate();
//            CommonLogger.businessLogger.debug("Update ADS Customers Statistics in [" + (System.currentTimeMillis() - startime) + "]mssec");
//            success = true;
//        } catch (SQLException e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateADSStatisticsTable] Failed columnId=[" + columnId + "]" + e, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateADSStatisticsTable] Failed columnId=[" + columnId + "]" + e, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            DataSourceManger.closeDBResources(null, pstm);
//        }
//        return success;
//    }
}
