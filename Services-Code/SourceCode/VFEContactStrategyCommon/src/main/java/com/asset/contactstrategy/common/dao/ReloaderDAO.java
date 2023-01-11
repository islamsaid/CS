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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Mostafa Fouda
 */
public class ReloaderDAO {

    public void changeReloadCounterSendingSMS(Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ReloaderDAO.class.getName() + " || " + "[changeReloadCounterSendingSMS] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendingSMS Started").build());
        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        long startTime = System.currentTimeMillis();

        try {
            sqlQuery = new StringBuilder();

            sqlQuery.append("UPDATE " + DBStruct.VFE_CS_SENDING_SMS_INSTANCES.TABLE_NAME + " SET " + DBStruct.VFE_CS_SENDING_SMS_INSTANCES.RELOAD_COUNTER);
            sqlQuery.append(" =? ");
//            CommonLogger.businessLogger.info("updateInstanceShutdownFlag || attempting Query: " + sqlQuery.toString());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendingSMS Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery.toString()).build());
            statement = connection.prepareStatement(sqlQuery.toString());
            statement.setInt(1, Defines.INT_TRUE);
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(ReloaderDAO.class.getName() + " || " + "Update Data time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendingSMS Excecuted")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

//            CommonLogger.businessLogger.debug(ReloaderDAO.class.getName() + " || " + " [changeReloadCounterSendingSMS] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendingSMS Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [changeReloadCounterSendingSMS]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [changeReloadCounterSendingSMS]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void changeReloadCounterSendinginterfaces(Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ReloaderDAO.class.getName() + " || " + "[changeReloadCounterSendinginterfaces] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendinginterfaces Started").build());
        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        long startTime = System.currentTimeMillis();

        try {
            sqlQuery = new StringBuilder();

            sqlQuery.append("UPDATE " + DBStruct.VFE_CS_INTERFACES_INSTANCES.TABLE_NAME + " SET " + DBStruct.VFE_CS_INTERFACES_INSTANCES.RELOAD_COUNTER);
            sqlQuery.append(" =? ");
//            CommonLogger.businessLogger.info("updateInstanceShutdownFlag || attempting Query: " + sqlQuery.toString());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendinginterfaces Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery.toString()).build());
            statement = connection.prepareStatement(sqlQuery.toString());
            statement.setInt(1, Defines.INT_TRUE);
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(ReloaderDAO.class.getName() + " || " + "Update Data time :" + (System.currentTimeMillis() - startTime));
//
//            CommonLogger.businessLogger.debug(ReloaderDAO.class.getName() + " || " + " [changeReloadCounterSendinginterfaces] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendinginterfaces Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [changeReloadCounterSendinginterfaces]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [changeReloadCounterSendinginterfaces]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }
}
