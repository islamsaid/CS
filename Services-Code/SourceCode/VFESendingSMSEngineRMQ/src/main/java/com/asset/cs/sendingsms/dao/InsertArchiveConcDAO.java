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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

/**
 *
 * @author kerollos.asaad
 */
public class InsertArchiveConcDAO {

    public static void insertArchiveConc(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        PreparedStatement pstat = null;
        try {
            pstat = conn.prepareStatement("INSERT INTO " + DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME + " ("
                    + DBStruct.VFE_CS_SMS_CONCAT_H.ID + ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN + ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.CS_MSG_ID + ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID + ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.SENDING_DATE + ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.MESSAGE_TEXT + ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN_MOD_X + ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.STATUS + ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID+ ", "
                    + DBStruct.VFE_CS_SMS_CONCAT_H.TRIMMED_SMSC_MSG_ID
                    + ") VALUES (?,?,?,?,SYSTIMESTAMP,?,?,?,?,?)");
            for (HashObject message : messages) {
                pstat.setInt(1, com.asset.contactstrategy.common.utils.Utility.getNextId(DBStruct.VFE_CS_SMS_CONCAT_H.VFE_CS_SMS_CONCAT_H_SEQ, conn));
                pstat.setString(2, message.getSMS().getDstMsisdn());
                pstat.setLong(3, message.getSMS().getSeqId().longValue());
                pstat.setString(4, message.getSMS().getSmsc_msg_id());
                //pstat.setTimestamp(5, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                pstat.setString(5, message.getSMS().getMsgTxt());
                pstat.setInt(6, com.asset.cs.sendingsms.util.Utility.getMsisdnModX(message.getSMS().getDstMsisdn()));
                if (message.getSMS().getSmsc_sms_status() != -1) {
                    pstat.setInt(7, message.getSMS().getSmsc_sms_status());
                } else {
                    //pstat.setObject(7, null);
                    pstat.setNull(7, java.sql.Types.INTEGER);
                }
                pstat.setInt(8, message.getSMS().getSmsc_id()); // smsc id
                pstat.setString(9, message.getSMS().getSmsc_msg_id_trimmed()); // trrimed msg id
                pstat.addBatch();
            }
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in insertArchiveConc--->" + e);
            CommonLogger.errorLogger.error("SQLException in insertArchiveConc--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_ARCHIVE_CONC_BATCH_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in insertArchiveConc--->" + e);
            CommonLogger.errorLogger.error("Exception in insertArchiveConc--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_ARCHIVE_CONC_BATCH_DAO, e.getMessage());
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
