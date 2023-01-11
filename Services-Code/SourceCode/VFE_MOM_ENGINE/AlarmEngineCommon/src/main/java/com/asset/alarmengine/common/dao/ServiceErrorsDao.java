/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.dao;

import com.asset.alarmengine.common.controller.DBpoolManager;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.alarmengine.common.models.HServiceErrorModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Mostafa Kashif
 */
public class ServiceErrorsDao {

    public static void insertHServiceError(Connection connection, HServiceErrorModel errorModel, String serviceErrorTblName, String idColumnName, String errorTypeColumnName, String errorSeverityColumnName,
            String errorParamsColumnName, String errorMsgColumnName, String insertionTimeColumnName, String idSeqName) throws EngineException {
        String methodName = "insertHServiceError";

        long time=System.currentTimeMillis();
        EngineLogger.debugLogger.info("Method has been started");

        PreparedStatement ps = null;

        try {
            StringBuilder sqlQuery = new StringBuilder("INSERT INTO " + serviceErrorTblName);
            sqlQuery.append("(" + idColumnName + ", " + errorTypeColumnName + ", " + errorSeverityColumnName + ", ");
            sqlQuery.append( errorParamsColumnName + ", ");
            sqlQuery.append(errorMsgColumnName + ", ");
            sqlQuery.append(insertionTimeColumnName + ")");
            sqlQuery.append(" VALUES (" + idSeqName + ".nextval,?,?,?,?,?)");

            ps = connection.prepareStatement(sqlQuery.toString());

            ps.setString(1, errorModel.getErrorType());
            ps.setInt(2, errorModel.getErrorSeverity());
            ps.setString(3, errorModel.getErrorParams());
            ps.setString(4, errorModel.getErrorMessage());
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));

            ps.executeUpdate();

        } catch (SQLException e) {
            EngineLogger.debugLogger.error("SQLException while insertHServiceError---->" + e);
            EngineLogger.errorLogger.error("SQLException while insertHServiceError---->" + e);

            throw new EngineException(e, EngineErrorCodes.SQL_ERROR_IN_DAO);
        } catch (Exception e) {
            EngineLogger.debugLogger.error("Exception while insertHServiceError---->" + e);
            EngineLogger.errorLogger.error("Exception while insertHServiceError---->" + e);

            throw new EngineException(e, EngineErrorCodes.GENERAL_ERROR_IN_DAO);
        } finally {
            DBpoolManager.close(ps);
            EngineLogger.debugLogger.debug("Function Executed in Time: " + (System.currentTimeMillis() -time ) + " msecs");

        }


    }

    // public static Vector<HServiceErrorModel> getServiceErrorsByDate(Connection connection, String date, String severitiesCommaSeparated, String dateFormat) throws EngineException {
//    public static Vector<HServiceErrorModel> getServiceErrorsByDate(Connection connection, String date, String severitiesCommaSeparated, String dateFormat, String serviceErrorTblName, String idColumnName, String errorTypeColumnName, String errorSeverityColumnName,
//            String msisdnColumnName, String errorParamsColumnName, String errorCodeColumnName, String errorMsgColumnName, String txidColumnName, String errorTimeColumnName, String insertionTimeColumnName, String idSeqName,
//            String lookupErrorTblName, String lookupErrorLabelColumnName, String lookupErrorIdColumnName) throws EngineException {
//
//        // Created by Mahmoud Abdou
//
//        long executionTime = System.currentTimeMillis();
//
//        String methodName = "getServiceErrorsByDate";
//
//        EngineLogger.debugLogger.info("Method started...");
//
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        Vector<HServiceErrorModel> modelList = new Vector<HServiceErrorModel>();
//
//        try {
//
//            StringBuilder sqlQuery = new StringBuilder("SELECT a." + idColumnName);
//            sqlQuery.append(" ,a." + errorCodeColumnName);
//            sqlQuery.append(" ,a." + insertionTimeColumnName);
//            sqlQuery.append(" ,a." + errorMsgColumnName);
//            sqlQuery.append(" ,a." + msisdnColumnName);
//            sqlQuery.append(" ,a." + errorParamsColumnName);
//            sqlQuery.append(" ,a." + errorSeverityColumnName);
//            sqlQuery.append(" ,a." + errorTimeColumnName);
//            sqlQuery.append(" ,a." + txidColumnName);
//            sqlQuery.append(" ,a." + errorTypeColumnName);
//            sqlQuery.append(" ," + lookupErrorTblName + "." + lookupErrorLabelColumnName);
//            sqlQuery.append(" FROM " + serviceErrorTblName + " a");
//            sqlQuery.append(" RIGHT OUTER JOIN " + lookupErrorTblName);
//            sqlQuery.append(" on a." + errorTypeColumnName).append("= " + lookupErrorTblName + "." + lookupErrorIdColumnName);
//            sqlQuery.append(" WHERE " + errorSeverityColumnName).append(" IN (").append(severitiesCommaSeparated.trim()).append(")");
//            sqlQuery.append(" AND TO_TIMESTAMP(a." + insertionTimeColumnName + ",'").append(dateFormat).append("')>");
//            sqlQuery.append(" TO_TIMESTAMP('").append(date.trim()).append("','").append(dateFormat).append("')");
//
//            EngineLogger.debugLogger.info(sqlQuery.toString());
//            ps = connection.prepareStatement(sqlQuery.toString());
//            rs = ps.executeQuery();
//            HServiceErrorModel hServiceErrorModel = new HServiceErrorModel();
//
//            while (rs.next()) {
//
//                hServiceErrorModel = new HServiceErrorModel();
//                hServiceErrorModel.setId(rs.getInt(idColumnName));
//                hServiceErrorModel.setErrorCode(rs.getString(errorCodeColumnName));
//                hServiceErrorModel.setInsertionTime(new Date(rs.getTimestamp(insertionTimeColumnName).getTime()));
//                hServiceErrorModel.setErrorMessage(rs.getString(errorMsgColumnName));
//                hServiceErrorModel.setMsisdn(rs.getString(msisdnColumnName));
//                hServiceErrorModel.setErrorParams(rs.getString(errorParamsColumnName));
//                hServiceErrorModel.setErrorSeverity(rs.getInt(errorSeverityColumnName));
//                hServiceErrorModel.setErrorTime(new Date(rs.getTimestamp(errorTimeColumnName).getTime()));
//                hServiceErrorModel.setErrorType(rs.getInt(errorTypeColumnName));
//                hServiceErrorModel.setErrorTypeString(rs.getString(lookupErrorLabelColumnName));
//
//                modelList.add(hServiceErrorModel);
//
//            }
//
//        } catch (SQLException e) {
//            EngineLogger.debugLogger.error("SQLException while insertHServiceError---->" + e);
//            EngineLogger.errorLogger.error("SQLException while insertHServiceError---->" + e);
//
//            throw new EngineException(e, EngineErrorCodes.SQL_ERROR_IN_DAO);
//        } catch (Exception e) {
//            EngineLogger.debugLogger.error("Exception while insertHServiceError---->" + e);
//            EngineLogger.errorLogger.error("Exception while insertHServiceError---->" + e);
//
//            throw new EngineException(e, EngineErrorCodes.GENERAL_ERROR_IN_DAO);
//        } finally {
//            DBpoolManager.close(rs);
//            DBpoolManager.close(ps);
//        }
//
//        executionTime = System.currentTimeMillis() - executionTime;
//        EngineLogger.debugLogger.info("Method ended in " + executionTime + " ms");
//        return modelList;
//    }
    
    
     public static Vector<HServiceErrorModel> getServiceErrorsByDate(Connection connection, String date, String severitiesCommaSeparated, String dateFormat, String serviceErrorTblName, String idColumnName, String errorTypeColumnName, String errorSeverityColumnName,
            String errorParamsColumnName, String errorMsgColumnName,String insertionTimeColumnName, String idSeqName,
            String lookupErrorTblName, String lookupErrorLabelColumnName, String lookupErrorIdColumnName) throws EngineException {

        // Created by Mahmoud Abdou

        long executionTime = System.currentTimeMillis();

        String methodName = "getServiceErrorsByDate";

        EngineLogger.debugLogger.info("Method started...");

        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<HServiceErrorModel> modelList = new Vector<HServiceErrorModel>();

        try {

            StringBuilder sqlQuery = new StringBuilder("SELECT a." ).append( idColumnName);
//            sqlQuery.append(" ,a." ).append( errorCodeColumnName);
            sqlQuery.append(" ,a." ).append( insertionTimeColumnName);
            sqlQuery.append(" ,a." ).append( errorMsgColumnName);
//            sqlQuery.append(" ,a." ).append( msisdnColumnName);
            sqlQuery.append(" ,a." ).append( errorParamsColumnName);
            sqlQuery.append(" ,a." ).append( errorSeverityColumnName);
//            sqlQuery.append(" ,a." ).append( errorTimeColumnName);
//            sqlQuery.append(" ,a." ).append( txidColumnName);
            sqlQuery.append(" ,a." ).append( errorTypeColumnName);
            sqlQuery.append(" ," ).append( lookupErrorTblName ).append( "." ).append( lookupErrorLabelColumnName);
            sqlQuery.append(" FROM " ).append( serviceErrorTblName ).append( " a");
            sqlQuery.append(" RIGHT OUTER JOIN " ).append( lookupErrorTblName);
            sqlQuery.append(" on a." ).append( errorTypeColumnName).append("= " ).append( lookupErrorTblName ).append( "." ).append( lookupErrorIdColumnName);
            sqlQuery.append(" WHERE " ).append( errorSeverityColumnName).append(" IN (").append(severitiesCommaSeparated.trim()).append(")");
            sqlQuery.append(" AND TO_TIMESTAMP(a." ).append( insertionTimeColumnName ).append( ",'").append(dateFormat).append("')>");
            sqlQuery.append(" TO_TIMESTAMP('").append(date.trim()).append("','").append(dateFormat).append("')");

            EngineLogger.debugLogger.info(sqlQuery.toString());
            ps = connection.prepareStatement(sqlQuery.toString());
            rs = ps.executeQuery();
            HServiceErrorModel hServiceErrorModel = new HServiceErrorModel();

            while (rs.next()) {

                hServiceErrorModel = new HServiceErrorModel();
                hServiceErrorModel. setId(rs.getInt(idColumnName));
//                hServiceErrorModel.setErrorCode(rs.getString(errorCodeColumnName));
                hServiceErrorModel.setInsertionTime(new Date(rs.getTimestamp(insertionTimeColumnName).getTime()));
                hServiceErrorModel.setErrorMessage(rs.getString(errorMsgColumnName));
                hServiceErrorModel.setMsisdn(null);
                hServiceErrorModel.setErrorParams(rs.getString(errorParamsColumnName));
                hServiceErrorModel.setErrorSeverity(rs.getInt(errorSeverityColumnName));
                hServiceErrorModel.setErrorTime(new Date(rs.getTimestamp(insertionTimeColumnName).getTime()));
                hServiceErrorModel.setErrorType(rs.getString(errorTypeColumnName));
                hServiceErrorModel.setErrorTypeString(rs.getString(lookupErrorLabelColumnName));

                modelList.add(hServiceErrorModel);
            }

        } catch (SQLException e) {
            EngineLogger.debugLogger.error("SQLException while insertHServiceError---->" + e);
            EngineLogger.errorLogger.error("SQLException while insertHServiceError---->" + e);

            throw new EngineException(e, EngineErrorCodes.SQL_ERROR_IN_DAO);
        } catch (Exception e) {
            EngineLogger.debugLogger.error("Exception while insertHServiceError---->" + e);
            EngineLogger.errorLogger.error("Exception while insertHServiceError---->" + e);

            throw new EngineException(e, EngineErrorCodes.GENERAL_ERROR_IN_DAO);
        } finally {
            DBpoolManager.close(rs);
            DBpoolManager.close(ps);
        }

        executionTime = System.currentTimeMillis() - executionTime;
        EngineLogger.debugLogger.info("Method ended in " + executionTime + " ms");
        return modelList;
    }
}
