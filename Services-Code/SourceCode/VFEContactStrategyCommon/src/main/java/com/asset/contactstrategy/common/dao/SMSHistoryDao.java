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
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSHistoryModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.RetrieveMessageInputModel;
import com.asset.contactstrategy.interfaces.models.RetrieveSMSsInputModel;
import com.asset.contactstrategy.interfaces.models.RetrieveSMSsMessageModel;
import com.asset.contactstrategy.interfaces.models.RetrieveSMSsOutputModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author esmail.anbar
 */
public class SMSHistoryDao {

//    public void updateDeliveryStatus(Connection conn, int modX, String date) throws CommonException {
//
//        CommonLogger.businessLogger.debug("SMSHistoryDao.updateDeliveryStatus() Invoked...");
//        StringBuilder SQLquery = new StringBuilder("{call ");
//
//        SQLquery.append(DBStruct.STORED_PROCEDURES.UPDATE_DELIVERY_STATUS.UPDATE_DELIVERY_STATUS)
//                .append("(?, ?, ?, ?)}");
//        int updatedRowCount = 0;
//        int deletedRowCount = 0;
//        //CommonLogger.businessLogger.info("Attempting Query: " + SQLquery + " ... Parameters: " + modX + ":" + date);
//
//        CallableStatement cStmt = null;
//        long elapsedTime = 0;
//        try {
//            cStmt = conn.prepareCall(SQLquery.toString());
//
//            cStmt.setInt(1, modX);
//            cStmt.setString(2, date);
//            cStmt.registerOutParameter(3, Types.NUMERIC);
//            cStmt.registerOutParameter(4, Types.NUMERIC);
//
//            long startTime = System.currentTimeMillis();
//
//            cStmt.executeUpdate();
//
//            updatedRowCount = cStmt.getInt(3);
//            deletedRowCount = cStmt.getInt(4);
//
//            elapsedTime = System.currentTimeMillis() - startTime;
//
////            CommonLogger.businessLogger.info("SMSHistoryDao.updateDeliveryStatus() Ended || JobDate: " + date 
////                    + " || JobModX: " + modX + " || UpdatedCount: " + updatedRowCount 
////                    + " || DeletedCount: " + deletedRowCount + " || Elapsed Time: " + elapsedTime/(1000));
//            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSHistoryDao.updateDeliveryStatus() Ended")
//                    .put(GeneralConstants.StructuredLogKeys.JOB_DATE, date)
//                    .put(GeneralConstants.StructuredLogKeys.JOB_MOD_X, modX)
//                    .put(GeneralConstants.StructuredLogKeys.UPDATE_COUNT, updatedRowCount)
//                    .put(GeneralConstants.StructuredLogKeys.DELETE_COUNT, deletedRowCount)
//                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, elapsedTime / (100)).build());
//        } catch (SQLException ex) {
//            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
//            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
//            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
//        } catch (Exception ex) {
//            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
//            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
//            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            try {
//                if (cStmt != null) {
//                    cStmt.close();
//                }
//            } catch (SQLException ex) {
//                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
//            }
//        }
//    }

    /*    public void updateOtherStatus(Connection conn, int modX, String date) throws CommonException {
        CommonLogger.businessLogger.debug("SMSHistoryDao.updateOtherStatus() Invoked...");
        StringBuilder SQLquery = new StringBuilder();

        SQLquery.append("{call ")
                .append(DBStruct.STORED_PROCEDURES.UPDATE_OTHER_STATUS.UPDATE_OTHER_STATUS)
                .append("(?, ?, ?, ?)}");
        int updatedRowCount = 0;
        int deletedRowCount = 0;

        //CommonLogger.businessLogger.info("Attempting Query: " + SQLquery + " ... Parameters: " + modX + ":" + date);
        CallableStatement cStmt = null;
        long elapsedTime = 0;
        try {
            cStmt = conn.prepareCall(SQLquery.toString());

            cStmt.setInt(1, modX);
            cStmt.setString(2, date);
            cStmt.registerOutParameter(3, Types.NUMERIC);
            cStmt.registerOutParameter(4, Types.NUMERIC);

            long startTime = System.currentTimeMillis();

            cStmt.executeUpdate();

            updatedRowCount = cStmt.getInt(3);
            deletedRowCount = cStmt.getInt(4);

            elapsedTime = System.currentTimeMillis() - startTime;
//            CommonLogger.businessLogger.info("SMSHistoryDao.updateOtherStatus() Ended || JobDate: " + date
//                    + " || JobModX: " + modX + " || UpdatedCount: " + updatedRowCount
//                    + " || DeletedCount: " + deletedRowCount + " || Elapsed Time - " + elapsedTime / (1000));
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSHistoryDao.updateOtherStatus() Ended")
                    .put(GeneralConstants.StructuredLogKeys.JOB_DATE, date)
                    .put(GeneralConstants.StructuredLogKeys.JOB_MOD_X, modX)
                    .put(GeneralConstants.StructuredLogKeys.UPDATE_COUNT, updatedRowCount)
                    .put(GeneralConstants.StructuredLogKeys.DELETE_COUNT, deletedRowCount)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, elapsedTime / (100)).build());
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (cStmt != null) {
                    cStmt.close();
                }
            } catch (SQLException ex) {
                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
        }

        //CommonLogger.businessLogger.info("SMSHistoryDao.updateOtherStatus() Ended: Elapsed Time - " + elapsedTime/(1000));
    }
     */
//    public static void updateTimeOutStatus(Connection conn, int modX, String date) throws CommonException {
//        CommonLogger.businessLogger.debug("SMSHistoryDao.updateTimeOutStatus() Invoked...");
//        StringBuilder SQLquery = new StringBuilder();
//
//        SQLquery.append("{call ")
//                .append(DBStruct.STORED_PROCEDURES.UPDATE_TIMEOUT_STATUS.UPDATE_TIMEOUT_STATUS)
//                .append("(?, ?, ?, ?)}");
//        int updatedRowCount = 0;
//        int deletedRowCount = 0;
//
//        //CommonLogger.businessLogger.info("Attempting Query: " + SQLquery + " ... Parameters: " + modX + ":" + date);
//        CallableStatement cStmt = null;
//        long elapsedTime = 0;
//        try {
//            cStmt = conn.prepareCall(SQLquery.toString());
//
//            cStmt.setInt(1, modX);
//            cStmt.setString(2, date);
//            cStmt.registerOutParameter(3, Types.NUMERIC);
//            cStmt.registerOutParameter(4, Types.NUMERIC);
//
//            long startTime = System.currentTimeMillis();
//
//            cStmt.executeUpdate();
//
//            updatedRowCount = cStmt.getInt(3);
//            deletedRowCount = cStmt.getInt(4);
//
//            elapsedTime = System.currentTimeMillis() - startTime;
////            CommonLogger.businessLogger.info("SMSHistoryDao.updateTimeOutStatus() Ended || JobDate: " + date
////                    + " || JobModX: " + modX + " || UpdatedCount: " + updatedRowCount
////                    + " || DeletedCount: " + deletedRowCount + " || Elapsed Time - " + elapsedTime / (1000));
//            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSHistoryDao.updateOtherStatus() Ended")
//                    .put(GeneralConstants.StructuredLogKeys.JOB_DATE, date)
//                    .put(GeneralConstants.StructuredLogKeys.JOB_MOD_X, modX)
//                    .put(GeneralConstants.StructuredLogKeys.UPDATE_COUNT, updatedRowCount)
//                    .put(GeneralConstants.StructuredLogKeys.DELETE_COUNT, deletedRowCount)
//                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, elapsedTime / (100)).build());
//        } catch (SQLException ex) {
//            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
//            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
//            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
//        } catch (Exception ex) {
//            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
//            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
//            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            try {
//                if (cStmt != null) {
//                    cStmt.close();
//                }
//            } catch (SQLException ex) {
//                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
//            }
//        }
//
//        //CommonLogger.businessLogger.info("SMSHistoryDao.updateTimeOutStatus() Ended: Elapsed Time - " + elapsedTime/(1000));
//    }

    public String getMessageStatus(Connection conn, long MessageID, String MSISDN, HashMap<Integer, String> statusHashMap) throws CommonException {
        CommonLogger.businessLogger.debug("SMSHistoryDao.getMessageStatus() Invoked...");
        StringBuilder SQLquery = new StringBuilder();
        int modX = Utility.getMsisdnLastTwoDigits(MSISDN);

        SQLquery
                //                .append("Select ")
                //                .append(DBStruct.VFE_CS_SMS_H_STATUS_LK.SMS_H_STATUS_NAME)
                //                .append(" From ")
                //                .append(DBStruct.VFE_CS_SMS_H_STATUS_LK.TABLE_NAME)
                //                .append(" Where ")
                //                .append(DBStruct.VFE_CS_SMS_H_STATUS_LK.SMS_H_STATUS_ID)
                //                .append(" In (")
                .append("SELECT ")
                .append(DBStruct.VFE_CS_SMS_H.STATUS)
                .append(" FROM ")
                .append(DBStruct.VFE_CS_SMS_H.TABLE_NAME)
                .append(" WHERE ")
                .append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID)
                .append("=? AND ")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN)
                .append("=? AND ")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X)
                .append("=?");
//                .append("=?)");

//        CommonLogger.businessLogger.info("Attempting Query: " + SQLquery + " ... Parameters: " + MSISDN + ":" + MessageID);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSHistoryDao.updateOtherStatus() Query")
                .put(GeneralConstants.StructuredLogKeys.QUERY, SQLquery)
                .put(GeneralConstants.StructuredLogKeys.MSISDN, MSISDN)
                .put(GeneralConstants.StructuredLogKeys.MSG_ID, MessageID).build());

        PreparedStatement preStmt = null;
        ResultSet resultSet = null;

        try {
            preStmt = conn.prepareStatement(SQLquery.toString());

            preStmt.setLong(1, MessageID);
            preStmt.setString(2, MSISDN);
            preStmt.setInt(3, modX);

            resultSet = preStmt.executeQuery();

            while (resultSet.next()) {
                return statusHashMap.get(resultSet.getInt(DBStruct.VFE_CS_SMS_H.STATUS));
            }

            return "";
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preStmt != null) {
                    preStmt.close();
                }
            } catch (SQLException ex) {
                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
            //CommonLogger.businessLogger.info("SMSHistoryDao.getMessageStatus() Ended");
        }

    }

    public RetrieveSMSsOutputModel getMessagesForMSISDNwithDaySpan(Connection conn, RetrieveSMSsInputModel inputModel, HashMap<Integer, String> statusHashMap) throws CommonException {
        CommonLogger.businessLogger.debug("SMSHistoryDao.getMessagesForMSISDNwithDaySpan() Invoked...");

        StringBuilder SQLquery = new StringBuilder();
        boolean hasSystemName = false, hasSMSScript = false;
        RetrieveSMSsOutputModel outputModel = null;
        RetrieveSMSsMessageModel messageModel = null;
        ArrayList<RetrieveSMSsMessageModel> messages = new ArrayList<>();
        int modX = Utility.getMsisdnLastTwoDigits(inputModel.getMsisdn());

        SQLquery.append("Select * from (")
                .append("SELECT ")
                .append(DBStruct.VFE_CS_SMS_H.MESSAGE_TEXT)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.TABLE_NAME)
                .append(".")
                .append(DBStruct.VFE_CS_SMS_H.STATUS)
                .append(",")
                .append(DBStruct.VFE_CS_SERVICES.SERVICE_NAME)
                .append(" FROM ")
                .append(DBStruct.VFE_CS_SMS_H.TABLE_NAME)
                .append(" LEFT JOIN ")
                .append(DBStruct.VFE_CS_SERVICES.TABLE_NAME)
                .append(" ON ")
                .append(DBStruct.VFE_CS_SMS_H.TABLE_NAME)
                .append(".")
                .append(DBStruct.VFE_CS_SMS_H.SERVICE_ID)
                .append(" = ")
                .append(DBStruct.VFE_CS_SERVICES.TABLE_NAME)
                .append(".")
                .append(DBStruct.VFE_CS_SERVICES.VERSION_ID)
                .append(" WHERE ")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN)
                .append("=? AND ")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X)
                .append("=? AND ")
                .append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE)
                .append(">= ? AND ")
                .append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE)
                .append("< ? AND ")
                .append(DBStruct.VFE_CS_SERVICES.SERVICE_NAME)
                .append("=?");

//        if (inputModel.getSystemName() != null)
//        {
//            hasSystemName = true;
//            SQLquery.append(" AND ")
//            .append(DBStruct.VFE_CS_SERVICES.SERVICE_NAME)
//            .append("=? ");
//        }
        if (inputModel.getSmsScript() != null) {
            hasSMSScript = true;
            SQLquery.append(" AND Lower(")
                    .append(DBStruct.VFE_CS_SMS_H.MESSAGE_TEXT)
                    .append(") Like Lower(?) ");
        }

        SQLquery.append(" ORDER BY  ")
                .append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE)
                .append(" DESC ")
                .append(") Where ROWNUM <= ?");

//        CommonLogger.businessLogger.info("Attempting Query: " + SQLquery + " ... Parameters: " + inputModel.getMsisdn() + " | " + inputModel.getSmsScript() + " | " + inputModel.getFrom() + " | " + inputModel.getTo());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSHistoryDao.RetrieveSMSsOutputModel() Query")
                .put(GeneralConstants.StructuredLogKeys.QUERY, SQLquery)
                .put(GeneralConstants.StructuredLogKeys.MSISDN, inputModel.getMsisdn())
                .put(GeneralConstants.StructuredLogKeys.SMS_SCRIPT, inputModel.getSmsScript())
                .put(GeneralConstants.StructuredLogKeys.FROM, inputModel.getFrom())
                .put(GeneralConstants.StructuredLogKeys.TO, inputModel.getTo()).build());

        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        int paramIndex = 1;

        try {
            preStmt = conn.prepareStatement(SQLquery.toString());

            preStmt.setString(paramIndex++, inputModel.getMsisdn());
            preStmt.setInt(paramIndex++, modX);
            preStmt.setTimestamp(paramIndex++, inputModel.getFrom());
            preStmt.setTimestamp(paramIndex++, inputModel.getTo());
            preStmt.setString(paramIndex++, inputModel.getSystemName());
//            if (hasSystemName)
//            {
//                preStmt.setString(paramIndex++, inputModel.getSystemName());
//            }
            if (hasSMSScript) {
                preStmt.setString(paramIndex++, "%" + inputModel.getSmsScript() + "%");
            }

            preStmt.setInt(paramIndex++, Integer.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.RETRIEVE_SMS_INTERFACE.MAX_NO_OF_ROWS)));

            resultSet = preStmt.executeQuery();

            outputModel = new RetrieveSMSsOutputModel();
            outputModel.setErrorCode("0");
            outputModel.setErrorDescription("SUCCESS");
            while (resultSet.next()) {
                messageModel = new RetrieveSMSsMessageModel();
                messageModel.setMessageText(resultSet.getString(DBStruct.VFE_CS_SMS_H.MESSAGE_TEXT));
                messageModel.setStatus(statusHashMap.get(Integer.parseInt(resultSet.getString(DBStruct.VFE_CS_SMS_H.STATUS))));
                messageModel.setSubmissionDate(resultSet.getTimestamp(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE));
                messageModel.setSystemName(resultSet.getString(DBStruct.VFE_CS_SERVICES.SERVICE_NAME));
                messages.add(messageModel);
            }
            outputModel.setMessages(messages);

            return outputModel;
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preStmt != null) {
                    preStmt.close();
                }
            } catch (SQLException ex) {
                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
        }
    }

    public void archiveMsg(Connection conn, ArrayList<SMSHistoryModel> msgs) throws CommonException {
        CommonLogger.businessLogger.debug("SMSHistoryDao.archiveMsg() Invoked...");
        StringBuilder SQLquery = new StringBuilder();
        StringBuilder log = new StringBuilder();
        long time = System.currentTimeMillis();

        SQLquery.append("INSERT INTO ")
                .append(DBStruct.VFE_CS_SMS_H.TABLE_NAME)
                .append(" (")
                .append(DBStruct.VFE_CS_SMS_H.ID)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.SENDING_DATE)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.MESSAGE_TEXT)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.STATUS)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.DELIVERY_DATE)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.CONCATENATION_COUNT)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.SMSC_ID)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.SENDER_ENGINE_ID)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.MESSAGE_TYPE)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.DO_NOT_APPLY)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.MESSAGE_VIOLATION_FLAG)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.SYSTEM_CATEGORY)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.MESSAGE_CATEGORY)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.SERVICE_ID)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.ORIGINATOR)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.OPTIONAL_PARAM_1)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.OPTIONAL_PARAM_2)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.OPTIONAL_PARAM_3)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.OPTIONAL_PARAM_4)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.OPTIONAL_PARAM_5)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.SMSC_CONCATENATION_COUNT)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.APP_QUEUE_NAME)
                .append(",")
                .append(DBStruct.VFE_CS_SMS_H.DELIVERY_REPORT)
                .append(") VALUES (").append(DBStruct.VFE_CS_SMS_H.VFE_CS_SMS_H_SEQ).append(".nextVal,?,?,SYSTIMESTAMP,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

//        CommonLogger.businessLogger.info("Attempting Query: " + SQLquery);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSHistoryDao.ArchiveMsg() Query")
                .put(GeneralConstants.StructuredLogKeys.QUERY, SQLquery).build());

        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        int paramIndex = 1;
        SMSHistoryModel smsModel = null;

        try {
            preStmt = conn.prepareStatement(SQLquery.toString());

            for (int i = 0; i < msgs.size(); i++) {
                smsModel = msgs.get(i);
                if (i != 0) {
                    log.append(",");
                }
                log.append(smsModel.getCS_MSG_ID());
                paramIndex = 1;
                preStmt.setLong(paramIndex++, smsModel.getCS_MSG_ID());
                preStmt.setString(paramIndex++, smsModel.getMSISDN());
                //Using SYSTIMESTAMP instead of machine time to localize on database
//                preStmt.setTimestamp(paramIndex++, smsModel.getSUBMISSION_DATE());
                preStmt.setTimestamp(paramIndex++, smsModel.getSENDING_DATE());
                preStmt.setString(paramIndex++, smsModel.getMESSAGE_TEXT());
                preStmt.setInt(paramIndex++, smsModel.getMSISDN_MOD_X());
                preStmt.setInt(paramIndex++, smsModel.getSTATUS());
                preStmt.setTimestamp(paramIndex++, smsModel.getDELIVERY_DATE());
                preStmt.setInt(paramIndex++, smsModel.getCONCATENATION_COUNT());
                preStmt.setInt(paramIndex++, smsModel.getDELIVERED_COUNT());
                preStmt.setInt(paramIndex++, smsModel.getOTHER_STATUS_COUNT());
                preStmt.setInt(paramIndex++, smsModel.getSMSC_ID());
                preStmt.setInt(paramIndex++, smsModel.getSENDER_ENGINE_ID());
                preStmt.setInt(paramIndex++, smsModel.getMESSAGE_TYPE());
                preStmt.setInt(paramIndex++, smsModel.getDO_NOT_APPLY());
                preStmt.setInt(paramIndex++, smsModel.getMESSAGE_VIOLATION_FLAG());
                preStmt.setInt(paramIndex++, smsModel.getSYSTEM_CATEGORY());
                preStmt.setInt(paramIndex++, smsModel.getMESSAGE_CATEGORY());
                preStmt.setInt(paramIndex++, smsModel.getSERVICE_ID());
                preStmt.setString(paramIndex++, smsModel.getORIGINATOR());
                preStmt.setString(paramIndex++, smsModel.getOPTIONAL_PARAM_1());
                preStmt.setString(paramIndex++, smsModel.getOPTIONAL_PARAM_2());
                preStmt.setString(paramIndex++, smsModel.getOPTIONAL_PARAM_3());
                preStmt.setString(paramIndex++, smsModel.getOPTIONAL_PARAM_4());
                preStmt.setString(paramIndex++, smsModel.getOPTIONAL_PARAM_5());
                preStmt.setInt(paramIndex++, smsModel.getSMSC_CONCATENATION_COUNT());
                preStmt.setString(paramIndex++, smsModel.getAPP_QUEUE_NAME());
                preStmt.setInt(paramIndex++, smsModel.getDeliveryReport());
                preStmt.addBatch();
            }

//            CommonLogger.businessLogger.info(log);
            preStmt.executeBatch();
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archived csMsgId")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time))
                    .putCSV(GeneralConstants.StructuredLogKeys.MSG_IDS, log.toString())
                    .build());
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preStmt != null) {
                    preStmt.close();
                }
            } catch (SQLException ex) {
                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
        }
    }

    //2595 smpp update
    public String getMessageStatus(Connection conn, RetrieveMessageInputModel input, HashMap<Integer, String> statusHashMap) throws CommonException {
        CommonLogger.businessLogger.debug("SMSHistoryDao.getMessageStatus() Invoked...");
        StringBuilder SQLquery = new StringBuilder();
        int modX = Utility.getMsisdnLastTwoDigits(input.getMsisdn());

        SQLquery.append("SELECT ").append(DBStruct.VFE_CS_SMS_H.STATUS).append(" FROM ").append(DBStruct.VFE_CS_SMS_H.TABLE_NAME)
                .append(" WHERE ").append(DBStruct.VFE_CS_SMS_H.APP_QUEUE_NAME).append("=? AND ").append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID).append("=? AND ")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN).append("=? AND ")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X).append("=? AND ")
                .append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE).append(" BETWEEN ").append(" TO_TIMESTAMP(?,'fmMMfm/fmDDfm/YYYY fmHH12fm:MI:SS.FF AM') AND TO_TIMESTAMP(?,'fmMMfm/fmDDfm/YYYY fmHH12fm:MI:SS.FF AM') ");

//        CommonLogger.businessLogger.info("Attempting Query: " + SQLquery + " ... Parameters: " + input.getMessageID() + ":" + input.getMsisdn() + ":" + modX);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSHistoryDao.getMessageStatus() Query")
                .put(GeneralConstants.StructuredLogKeys.QUERY, SQLquery)
                .put(GeneralConstants.StructuredLogKeys.MSG_ID, input.getMessageID())
                .put(GeneralConstants.StructuredLogKeys.MSISDN, input.getMsisdn())
                .put(GeneralConstants.StructuredLogKeys.JOB_MOD_X, modX).build());

        PreparedStatement preStmt = null;
        ResultSet resultSet = null;

        try {
            preStmt = conn.prepareStatement(SQLquery.toString());
            preStmt.setString(1, input.getSystemName());
            preStmt.setLong(2, input.getMessageID());
            preStmt.setString(3, input.getMsisdn());
            preStmt.setInt(4, modX);
            //'fmMMfm/fmDDfm/YYYY fmHH12fm:MI:SS.FF AM'         '3/08/2017 1:16:49.846786 PM'

            SimpleDateFormat formatter = new SimpleDateFormat(Defines.SMPP_Defines.DATE_FORMAT_FROM_REQ);
            Date date = formatter.parse(input.getSubmissionDate());
            SimpleDateFormat sdfr = new SimpleDateFormat(Defines.SMPP_Defines.DATE_FORMAT_FOR_QUERY);
            String dateString = sdfr.format(date);
            preStmt.setString(5, dateString + " 12:00:00.000000 AM");
            preStmt.setString(6, dateString + " 11:59:59.999999 PM");

            resultSet = preStmt.executeQuery();

            while (resultSet.next()) {
                return statusHashMap.get(resultSet.getInt(DBStruct.VFE_CS_SMS_H.STATUS));
            }

            return "";
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preStmt != null) {
                    preStmt.close();
                }
            } catch (SQLException ex) {
                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void updateArchivedMessageStatus(Connection conn, long csMsgId, int status, int msisdnModX) throws CommonException {
        StringBuilder SQLquery = new StringBuilder();

        SQLquery.append("Update ")
                .append(DBStruct.VFE_CS_SMS_H.TABLE_NAME)
                .append(" Set ")
                .append(DBStruct.VFE_CS_SMS_H.STATUS)
                .append(" = ? WHERE ")
                .append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID)
                .append(" = ? And ")
                .append(DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X)
                .append(" = ?");

//        CommonLogger.businessLogger.info("Attempting Query: " + SQLquery);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSHistoryDao.updateArchivedMessageStatus() Query")
                .put(GeneralConstants.StructuredLogKeys.QUERY, SQLquery).build());

        PreparedStatement preStmt = null;

        try {
            preStmt = conn.prepareStatement(SQLquery.toString());
            preStmt.setInt(1, status);
            preStmt.setLong(2, csMsgId);
            preStmt.setInt(3, msisdnModX);

            preStmt.executeUpdate();
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(ex + " || " + ex.getMessage());
            CommonLogger.errorLogger.error(ex + " || " + ex.getMessage(), ex);
            throw new CommonException("Exception : " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (preStmt != null) {
                    preStmt.close();
                }
            } catch (SQLException ex) {
                throw new CommonException("SQL Exception error: " + ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
        }
    }
}
//      Timestamp timeStamp = new Timestamp(1467615600000L);
//        
//      SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yy");
//	String dateInString = "04-07-16 09:00:00";
//	java.util.Date date = null;
//        try {
//            date = sdf.parse(dateInString);
//        } catch (ParseException ex) {
//            Logger.getLogger(VFECStrategySMSHistoryDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//		
//	System.out.println(dateInString);
//	System.out.println("Date - Time in milliseconds : " + date.toString());
//      System.out.println("Finished Callable Statment with TimeStamp= " + timeStamp.toString());
