/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.dao;

import client.HashObject;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.sendingsms.defines.ErrorCodes;
import com.asset.cs.sendingsms.util.Utility;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class UpdateOnDeliveryDAO {

    public static synchronized void updateCounterCachedBatch(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append("UPDATE ").append(DBStruct.VFE_CS_SMS_H.TABLE_NAME).append(" SET ")
                    .append(DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT).append("=").append(DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT).append("+?, ")
                    .append(DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT).append("=").append(DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT).append("+? ")
                    .append(" WHERE ").append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE).append(">(systimestamp-?) AND ")
                    .append(DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X).append("=? AND ").append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID).append("=?");
            pstat = conn.prepareStatement(query.toString());
            for (HashObject message : messages) {
                pstat.setInt(1, (message.getSMS().getSmsc_sms_status() == Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.DELIVERED) ? 1 : 0);
                pstat.setInt(2, (message.getSMS().getSmsc_sms_status() == Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.DELIVERED) ? 0 : 1);
                pstat.setInt(3, Integer.valueOf(com.asset.cs.sendingsms.defines.Defines.deliveryEngineConfigurations.get(com.asset.cs.sendingsms.defines.Defines.DAYS_BEFORE_SMS_TIMEOUT)) + 1);
                pstat.setInt(4, Utility.getMsisdnModX(message.getSMS().getDstMsisdn()));
                pstat.setLong(5, message.getSMS().getSeqId().longValue());
                pstat.addBatch();
            }
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateCounterBatch--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateCounterBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_UPDATE_COUNTER_BATCH_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateCounterBatch--->" + e);
            CommonLogger.errorLogger.error("Exception in updateCounterBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_UPDATE_COUNTER_BATCH_DAO, e.getMessage());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

    public static synchronized void updateCounterBatch(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE ").append(DBStruct.VFE_CS_SMS_H.TABLE_NAME).append(" SET ")
                    .append(DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT).append("=").append(DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT).append("+?, ")
                    .append(DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT).append("=").append(DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT).append("+? ")
                    .append(" WHERE ").append(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE).append(">(systimestamp-?) AND ")
                    .append(DBStruct.VFE_CS_SMS_H.CS_MSG_ID)
                    .append("=(SELECT ").append(DBStruct.VFE_CS_SMS_CONCAT_H.CS_MSG_ID).append(" FROM ").append(DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME)
                    .append(" WHERE ").append(DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE).append(">(systimestamp-?) AND ").append(DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID)
                    .append("=? AND (").append(DBStruct.VFE_CS_SMS_CONCAT_H.TRIMMED_SMSC_MSG_ID).append(" =? OR ").append(DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID).append(" =?))");
            pstat = conn.prepareStatement(query.toString());
            for (HashObject message : messages) {
                pstat.setInt(1, (message.getSMS().getSmsc_sms_status() == Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.DELIVERED) ? 1 : 0);
                pstat.setInt(2, (message.getSMS().getSmsc_sms_status() == Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.DELIVERED) ? 0 : 1);
                pstat.setInt(3, Integer.valueOf(com.asset.cs.sendingsms.defines.Defines.deliveryEngineConfigurations.get(com.asset.cs.sendingsms.defines.Defines.DAYS_BEFORE_SMS_TIMEOUT)) + 1);
                pstat.setInt(4, Integer.valueOf(com.asset.cs.sendingsms.defines.Defines.deliveryEngineConfigurations.get(com.asset.cs.sendingsms.defines.Defines.DAYS_BEFORE_SMS_TIMEOUT)) + 1);
                pstat.setInt(5, message.getSMS().getSmsc_id());
                //Changed by kashif
                pstat.setString(6, message.getSMS().getSmsc_msg_id());
                pstat.setString(7, message.getSMS().getSmsc_msg_id());
                pstat.addBatch();
            }
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateCounterBatch--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateCounterBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_UPDATE_COUNTER_BATCH_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateCounterBatch--->" + e);
            CommonLogger.errorLogger.error("Exception in updateCounterBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_UPDATE_COUNTER_BATCH_DAO, e.getMessage());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

    public static void updateStatusCachedBatch(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE ").append(DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME).append(" SET ")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.DELIVERY_DATE).append(" = systimestamp ,")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.STATUS).append("=? WHERE ")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE).append(">(systimestamp-?) AND ")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN_MOD_X).append("=? AND ")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID).append("=? AND ").append(DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID).append("=?");
            //pstat = conn.prepareStatement("SELECT " + DBStruct.VFE_CS_SMS_CONCAT_H.CS_MSG_ID + " FROM " + DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME + " FOR UPDATE");
            pstat = conn.prepareStatement(query.toString());
            for (HashObject message : messages) {
                pstat.setInt(1, message.getSMS().getSmsc_sms_status());
                pstat.setInt(2, Integer.valueOf(com.asset.cs.sendingsms.defines.Defines.deliveryEngineConfigurations.get(com.asset.cs.sendingsms.defines.Defines.DAYS_BEFORE_SMS_TIMEOUT)) + 1);
                pstat.setInt(3, Utility.getMsisdnModX(message.getSMS().getDstMsisdn()));
                pstat.setString(4, message.getSMS().getSmsc_msg_id());
                pstat.setInt(5, message.getSMS().getSmsc_id());
                pstat.addBatch();
            }
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateStatusBatch--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateStatusBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_UPDATE_STATUS_BATCH_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateStatusBatch--->" + e);
            CommonLogger.errorLogger.error("Exception in updateStatusBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_UPDATE_STATUS_BATCH_DAO, e.getMessage());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

    public static void updateStatusBatch(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        try {
            //pstat = conn.prepareStatement("SELECT " + DBStruct.VFE_CS_SMS_CONCAT_H.CS_MSG_ID + " FROM " + DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME + " FOR UPDATE");
            StringBuilder stmt = new StringBuilder();
            stmt.append("UPDATE ").append(DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME).append(" SET ")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.STATUS).append("=? , ")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.DELIVERY_DATE).append(" = systimestamp").append(" WHERE ")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE).append(">(systimestamp-?) AND ")
                    .append(DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID).append("=? AND (").append(DBStruct.VFE_CS_SMS_CONCAT_H.TRIMMED_SMSC_MSG_ID)
                    .append(" =? OR ").append(DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID).append(" =?)");
            pstat = conn.prepareStatement(stmt.toString());
            for (HashObject message : messages) {
                pstat.setInt(1, message.getSMS().getSmsc_sms_status());
                pstat.setInt(2, Integer.valueOf(com.asset.cs.sendingsms.defines.Defines.deliveryEngineConfigurations.get(com.asset.cs.sendingsms.defines.Defines.DAYS_BEFORE_SMS_TIMEOUT)) + 1);
                pstat.setInt(3, message.getSMS().getSmsc_id());
                //changed by kashif
                pstat.setString(4, message.getSMS().getSmsc_msg_id());
                pstat.setString(5, message.getSMS().getSmsc_msg_id());
                pstat.addBatch();
            }
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateStatusBatch--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateStatusBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_UPDATE_STATUS_BATCH_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateStatusBatch--->" + e);
            CommonLogger.errorLogger.error("Exception in updateStatusBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_UPDATE_STATUS_BATCH_DAO, e.getMessage());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

    /**
     * update message stats in sms_concat_h, returning true if number of rows
     * affected > 0
     *
     * @param message
     * @param conn
     * @return (rows_affected > 0)
     * @throws CommonException
     *
     */
    public static synchronized boolean updateStatus(HashObject message, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        boolean ret = false;
        try {
            pstat = conn.prepareStatement("UPDATE " + DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME + " SET "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.STATUS + "=? WHERE "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN_MOD_X + "=? AND "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID + "=? AND " + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID + "=?");
            pstat.setInt(1, message.getSMS().getSmsc_sms_status());
            pstat.setInt(2, Utility.getMsisdnModX(message.getSMS().getDstMsisdn()));
            pstat.setString(3, message.getSMS().getSmsc_msg_id());
            pstat.setInt(4, message.getSMS().getSmsc_id());
            ret = pstat.executeUpdate() > 0;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateStatus--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateStatus--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_UPDATE_STATUS_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateStatus--->" + e);
            CommonLogger.errorLogger.error("Exception in updateStatus--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_UPDATE_STATUS_DAO, e.getMessage());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
        return ret;
    }

    public static void selectCsId(HashObject message, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String compareExpression = "";
        String simulatorTesting = com.asset.cs.sendingsms.defines.Defines.fileConfigurations.get(com.asset.cs.sendingsms.defines.Defines.SMSC_SIMULATOR_FLAG_PROPERTY);

        if (message.isOptionalMsgId() || (simulatorTesting != null && !simulatorTesting.isEmpty() && simulatorTesting.equalsIgnoreCase("true"))) {
            compareExpression = "=";
        } else {
            compareExpression = "LIKE";
        }
        try {
            pstat = conn.prepareStatement("SELECT " + DBStruct.VFE_CS_SMS_CONCAT_H.CS_MSG_ID + ", " + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN
                    + " FROM " + DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME
                    + " WHERE " + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID + " " + compareExpression + " ? AND " + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID + "=?");
//            pstat.setString(1, "%" +message.getSMS().getSmsc_msg_id());
            if (message.isOptionalMsgId() || (simulatorTesting != null && !simulatorTesting.isEmpty() && simulatorTesting.equalsIgnoreCase("true"))) {
                pstat.setString(1, message.getSMS().getSmsc_msg_id());
            } else {
                pstat.setString(1, "%" + message.getSMS().getSmsc_msg_id());
            }
            pstat.setInt(2, message.getSMS().getSmsc_id());
            rs = pstat.executeQuery();
            while (rs.next()) {
                message.getSMS().setSeqId(BigDecimal.valueOf(rs.getLong(DBStruct.VFE_CS_SMS_CONCAT_H.CS_MSG_ID)));
                message.getSMS().setDstMsisdn(String.valueOf(rs.getString(DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN)));
                break;
            }
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateStatus--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateStatus--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_UPDATE_STATUS_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateStatus--->" + e);
            CommonLogger.errorLogger.error("Exception in updateStatus--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_UPDATE_STATUS_DAO, e.getMessage());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

    public static synchronized boolean updateCounter(HashObject message, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        try {
            pstat = conn.prepareStatement("UPDATE " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " SET "
                    + DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT + "=" + DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT + "+?, "
                    + DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT + "=" + DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT + "+? "
                    + " WHERE " + DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X + "=? AND " + DBStruct.VFE_CS_SMS_H.CS_MSG_ID + "=?");
            pstat.setInt(1, (message.getSMS().getSmsc_sms_status() == Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.DELIVERED) ? 1 : 0);
            pstat.setInt(2, (message.getSMS().getSmsc_sms_status() == Defines.VFE_CS_SMSC_MESSAGE_STATUS_LK.DELIVERED) ? 0 : 1);
            // pstat.setTimestamp(3, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())); // TODO
            pstat.setInt(3, Utility.getMsisdnModX(message.getSMS().getDstMsisdn()));
            pstat.setLong(4, message.getSMS().getSeqId().longValue());
            return pstat.executeUpdate() > 0;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateCounter--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateCounter--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_UPDATE_COUNTER_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateCounter--->" + e);
            CommonLogger.errorLogger.error("Exception in updateCounter--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_UPDATE_COUNTER_DAO, e.getMessage());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }
}
