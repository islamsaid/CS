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
import java.util.ArrayList;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.sendingsms.defines.ErrorCodes;
import com.asset.cs.sendingsms.util.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.asset.contactstrategy.common.models.SMS;
import java.util.Calendar;

/**
 *
 * @author kerollos.asaad
 */
public class InsertArchiveDAO {

    /* public static void insertArchive(ArrayList<HashObject> messages, Connection conn) throws CommonException {
     PreparedStatement pstat = null;
     try {
     pstat = conn.prepareStatement("INSERT INTO " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " ("
     + DBStruct.VFE_CS_SMS_H.CS_MSG_ID + ", "
     + DBStruct.VFE_CS_SMS_H.MSISDN + ", "
     + DBStruct.VFE_CS_SMS_H.SENDING_DATE + ", "
     + DBStruct.VFE_CS_SMS_H.MESSAGE_TEXT + ", "
     + DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X + ", "
     + DBStruct.VFE_CS_SMS_H.STATUS + ", "
     + DBStruct.VFE_CS_SMS_H.CONCATENATION_COUNT + ", "
     + DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT + ", "
     + DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT + ", "
     + DBStruct.VFE_CS_SMS_H.SMSC_ID + ", "
     + DBStruct.VFE_CS_SMS_H.SENDER_ENGINE_ID + ", "
     + DBStruct.VFE_CS_SMS_H.MESSAGE_TYPE + ", "
     + DBStruct.VFE_CS_SMS_H.DO_NOT_APPLY + ", "
     + DBStruct.VFE_CS_SMS_H.MESSAGE_VIOLATION_FLAG + ", "
     + DBStruct.VFE_CS_SMS_H.SYSTEM_CATEGORY + ", "
     + DBStruct.VFE_CS_SMS_H.MESSAGE_CATEGORY + ", "
     + DBStruct.VFE_CS_SMS_H.SERVICE_ID
     + ") VALUES (?,?,SYSTIMESTAMP,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
     for (HashObject message : messages) {
     pstat.setInt(1, Integer.valueOf(message.getSMS().getSeqId()));
     pstat.setString(2, message.getSMS().getDstMsisdn());
     //pstat.setTimestamp(3, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
     pstat.setString(3, message.getSMS().getMsgTxt());
     pstat.setInt(4, Utility.getMsisdnModX(message.getSMS().getDstMsisdn()));
     pstat.setInt(5, Defines.VFE_CS_SMS_H_STATUS_LK.SENT);
     pstat.setInt(6, message.getSMS().getConcMsgCount().intValue());
     pstat.setInt(7, 0);
     pstat.setInt(8, 0);
     pstat.setInt(9, message.getSMS().getSmsc_id()); // smsc id
     pstat.setInt(10, message.getInstanceId()); // sender engine id
     pstat.setInt(11, message.getSMS().getMsgType().intValue());
     pstat.setInt(12, message.getSMS().getDonotApply().intValue());
     pstat.setInt(13, message.getSMS().getViolation().intValue());
     pstat.setInt(14, message.getSMS().getSystemCategory().intValue());
     pstat.setInt(15, message.getSMS().getMessageCategory().intValue());
     pstat.setInt(16, message.getSMS().getServiceId().intValue());
     pstat.addBatch();
     }
     pstat.executeBatch();
     } catch (SQLException e) {
     CommonLogger.businessLogger.error("SQLException in insertArchive--->" + e);
     CommonLogger.errorLogger.error("SQLException in insertArchive--->" + e, e);
     throw new CommonException(e.getMessage(), ErrorCodes.SQL_ERROR_IN_ARCHIVE_BATCH_DAO);
     } catch (Exception e) {
     CommonLogger.businessLogger.error("Exception in insertArchive--->" + e);
     CommonLogger.errorLogger.error("Exception in insertArchive--->" + e, e);
     throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR_IN_ARCHIVE_BATCH_DAO);
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
     }*/
    public static void updateArchive(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        try {
            int pstatX = 0;
            pstat = conn.prepareStatement("UPDATE " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " SET "
                    + DBStruct.VFE_CS_SMS_H.SENDING_DATE + "=SYSTIMESTAMP, "
                    + DBStruct.VFE_CS_SMS_H.STATUS + "=?, "
                    + DBStruct.VFE_CS_SMS_H.SMSC_ID + "=?, "
                    + DBStruct.VFE_CS_SMS_H.SENDER_ENGINE_ID + "=?, "
                    + DBStruct.VFE_CS_SMS_H.SMSC_CONCATENATION_COUNT + "=? "
                    + " WHERE " + DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X + "=? AND " + DBStruct.VFE_CS_SMS_H.CS_MSG_ID + "=?"
            );
            for (HashObject message : messages) {
              //  pstat.setInt(++pstatX, Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC);
                pstat.setInt(++pstatX,message.getMessageStatus());
                pstat.setInt(++pstatX, message.getSMS().getSmsc_id()); // smsc id
                pstat.setInt(++pstatX, message.getInstanceId()); // sender engine id
                pstat.setInt(++pstatX, message.getSMS().getSmsc_msg_count());
                pstat.setInt(++pstatX, Utility.getMsisdnModX(message.getSMS().getDstMsisdn()));
                pstat.setLong(++pstatX, message.getSMS().getSeqId().longValue());
                pstatX = 0;
                pstat.addBatch();
            }
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateArchive--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateArchive--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_ARCHIVE_BATCH_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateArchive--->" + e);
            CommonLogger.errorLogger.error("Exception in updateArchive--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_ARCHIVE_BATCH_DAO, e.getMessage());
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
