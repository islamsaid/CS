/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.db.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.delivery.models.ConcatSms;
import com.asset.contactstrategy.delivery.models.FullSms;
import com.asset.contactstrategy.delivery.models.JobModel;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.task.DeciderWorker;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author mohamed.halawa
 */
public class DeliveryDao {

    //Select that streams all subparition data joined with Concat
    public static void getMsgWithConcats(Connection conn, JobModel jobModel) throws Exception {
//        StringBuilder sqlQuery = new StringBuilder();

//        String query = "SELECT "
//                + DBStruct.VFE_CS_SMS_H.TABLE_NAME + ".ROWID sms_h_rowid, "
//                + DBStruct.VFE_CS_SMS_H.TABLE_NAME + ".*,"
//                //                + DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME + ".*"
//                + " concat." + DBStruct.VFE_CS_SMS_CONCAT_H.DELIVERY_DATE + " " + DBStruct.VFE_CS_SMS_CONCAT_H.DELIVERY_DATE + "_concat_h,"
//                + " concat." + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN + " " + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN + "_concat_h,"
//                + " concat." + DBStruct.VFE_CS_SMS_CONCAT_H.SENDING_DATE + " " + DBStruct.VFE_CS_SMS_CONCAT_H.SENDING_DATE + "_concat_h,"
//                + " concat." + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID + " " + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID + "_concat_h,"
//                + " concat." + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID + " " + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID + "_concat_h,"
//                + " concat." + DBStruct.VFE_CS_SMS_CONCAT_H.STATUS + " " + DBStruct.VFE_CS_SMS_CONCAT_H.STATUS + "_concat_h,"
//                + " concat." + DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE + " " + DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE + "_concat_h"
//                + " FROM " + DBStruct.VFE_CS_SMS_H.TABLE_NAME
//                + " RIGHT JOIN " + DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME + " concat"
//                + " ON " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + "." + DBStruct.VFE_CS_SMS_H.CS_MSG_ID
//                + " = concat." + DBStruct.VFE_CS_SMS_CONCAT_H.CS_MSG_ID
//                + " WHERE "
//                + " TRUNC (" + DBStruct.VFE_CS_SMS_H.TABLE_NAME + "." + DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE + ") = ?"
//                + " AND TRUNC (concat." + DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE + ") = ?"
//                + " AND " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + "." + DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X + " = ?"
//                + " AND concat." + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN_MOD_X + " = ?"
//                + " AND " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + "." + DBStruct.VFE_CS_SMS_H.STATUS + " IN (" + Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC
//                + ", " + Defines.VFE_CS_SMS_H_STATUS_LK.ENQUEUED + ")";
        String parallelHint;
        if (Defines.DELIVERY_AGGREGATION_PROPERTIES.PARALLEL_HINT_COUNT_VALUE <= 0) {
            parallelHint = "/*+Parallel*/";
        } else {
            parallelHint = "/*+Parallel(" + Defines.DELIVERY_AGGREGATION_PROPERTIES.PARALLEL_HINT_COUNT_VALUE + ")*/";
        }
        String query = "SELECT " //+ parallelHint parallel removed by Mohamed Hatem after reconsalidation via phone with mostafa khalil 13-oct-21 4:00 pm
                + " sms_h.ROWID sms_h_rowid, "
                + " sms_h.*,"
                + " concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.DELIVERY_DATE + " " + DBStruct.VFE_CS_SMS_CONCAT_H.DELIVERY_DATE + "_concat_h,"
                + " concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN + " " + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN + "_concat_h,"
                + " concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.SENDING_DATE + " " + DBStruct.VFE_CS_SMS_CONCAT_H.SENDING_DATE + "_concat_h,"
                + " concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID + " " + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID + "_concat_h,"
                + " concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID + " " + DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID + "_concat_h,"
                + " concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.STATUS + " " + DBStruct.VFE_CS_SMS_CONCAT_H.STATUS + "_concat_h,"
                + " concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE + " " + DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE + "_concat_h"
                + " FROM " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " sms_h,"
                + DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME + " concat_h"
                + " WHERE "
                + " sms_h." + DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE + " BETWEEN TO_DATE(?, 'DD-MON-YYYY') AND TO_DATE(?, 'DD-MON-YYYY')"
                + " AND concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE + " BETWEEN TO_DATE(?, 'DD-MON-YYYY') AND TO_DATE(?, 'DD-MON-YYYY')"
                + " AND sms_h." + DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X + " = ?"
                + " AND concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN_MOD_X + " = ?"
                + " AND sms_h." + DBStruct.VFE_CS_SMS_H.CS_MSG_ID + " = concat_h." + DBStruct.VFE_CS_SMS_CONCAT_H.CS_MSG_ID
                + " AND sms_h." + DBStruct.VFE_CS_SMS_H.STATUS + " IN (" + Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC
                + ", " + Defines.VFE_CS_SMS_H_STATUS_LK.ENQUEUED + ")";

        PreparedStatement stmt = null;
        ResultSet rs = null;
        HashMap<Long, FullSms> map = new HashMap<>();
        FullSms baseFullSms = new FullSms();
        FullSms fullSms;
        ConcatSms concatSms = new ConcatSms();
        ThreadPoolExecutor deciderPool = JobManager.getInstance().getDeciderPool();
        long time = System.currentTimeMillis();
        long count = 0;

        try {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Logging Query with parameters")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query)
                    .put(GeneralConstants.StructuredLogKeys.DAY, jobModel.getDate())
                    .put(GeneralConstants.StructuredLogKeys.TO, jobModel.getToDate())
                    .put(GeneralConstants.StructuredLogKeys.MSISDN_LAST_TWO_DIGITS, jobModel.getModX())
                    .build());

            int i = 1;
            stmt = conn.prepareStatement(query);
            stmt.setString(i++, jobModel.getDate());
            stmt.setString(i++, jobModel.getToDate());
            stmt.setString(i++, jobModel.getDate());
            stmt.setString(i++, jobModel.getToDate());
            stmt.setInt(i++, jobModel.getModX());
            stmt.setInt(i++, jobModel.getModX());

            rs = stmt.executeQuery();
            rs.setFetchSize(Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_FETCH_SIZE_VALUE);
            while (rs.next()) {
                long csMsgId = rs.getLong(DBStruct.VFE_CS_SMS_H.CS_MSG_ID);
                fullSms = map.get(csMsgId);
                if (fullSms == null) {
                    fullSms = (FullSms) baseFullSms.clone();
                    fullSms.setCS_MSG_ID(csMsgId);
                    fullSms.setRowId(rs.getString("sms_h_rowid"));
                    //concat_count, delivery_report, msisdn, smsc_concat_count, smsc_id, status, submission_Date, sending_date
                    fullSms.setCONCATENATION_COUNT(rs.getInt(DBStruct.VFE_CS_SMS_H.CONCATENATION_COUNT));
                    fullSms.setDeliveryReport(rs.getInt(DBStruct.VFE_CS_SMS_H.DELIVERY_REPORT));
                    fullSms.setMSISDN(rs.getString(DBStruct.VFE_CS_SMS_H.MSISDN));
                    fullSms.setSMSC_CONCATENATION_COUNT(rs.getInt(DBStruct.VFE_CS_SMS_H.SMSC_CONCATENATION_COUNT));
                    fullSms.setSMSC_ID(rs.getInt(DBStruct.VFE_CS_SMS_H.SMSC_ID));
                    fullSms.setSTATUS(rs.getInt(DBStruct.VFE_CS_SMS_H.STATUS));
                    fullSms.setSUBMISSION_DATE(rs.getTimestamp(DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE));
                    fullSms.setSENDING_DATE(rs.getTimestamp(DBStruct.VFE_CS_SMS_H.SENDING_DATE));
                    map.put(fullSms.getCS_MSG_ID(), fullSms);
                }
                concatSms = (ConcatSms) concatSms.clone();
                concatSms.setDeliveryDate(rs.getDate(DBStruct.VFE_CS_SMS_CONCAT_H.DELIVERY_DATE + "_concat_h"));
                concatSms.setMsgId(csMsgId);
                concatSms.setMsisdn(rs.getString(DBStruct.VFE_CS_SMS_CONCAT_H.MSISDN + "_concat_h"));
                concatSms.setSendingDate(rs.getDate(DBStruct.VFE_CS_SMS_CONCAT_H.SENDING_DATE + "_concat_h"));
                concatSms.setSmscId(rs.getInt(DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_ID + "_concat_h"));
                concatSms.setSmscMsgId(rs.getString(DBStruct.VFE_CS_SMS_CONCAT_H.SMSC_MSG_ID + "_concat_h"));
                concatSms.setStatus(rs.getInt(DBStruct.VFE_CS_SMS_CONCAT_H.STATUS + "_concat_h"));
                concatSms.setSubmissionDate(rs.getDate(DBStruct.VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE + "_concat_h"));
                fullSms.addToConcats(concatSms);
                if (fullSms.isSmsComplete()) {
                    map.remove(csMsgId);
                    //send to decider pool
                    deciderPool.execute(new DeciderWorker(fullSms));
                }
                count++;
            }
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Fetched Sms History Records")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time))
                    .put(GeneralConstants.StructuredLogKeys.DAY, jobModel.getDate())
                    .put(GeneralConstants.StructuredLogKeys.MSISDN_LAST_TWO_DIGITS, jobModel.getModX())
                    .put(GeneralConstants.StructuredLogKeys.COUNT, count)
                    .put(GeneralConstants.StructuredLogKeys.SIZE, map.size()).build());
        } catch (Throwable t) {
            CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error while fetching Sms History Records")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time))
                    .put(GeneralConstants.StructuredLogKeys.DAY, jobModel.getDate())
                    .put(GeneralConstants.StructuredLogKeys.MSISDN_LAST_TWO_DIGITS, jobModel.getModX())
                    .put(GeneralConstants.StructuredLogKeys.COUNT, count)
                    .put(GeneralConstants.StructuredLogKeys.SIZE, map.size()).build(), t);
            throw t;
        } finally {
            DataSourceManger.closeDBResources(rs, stmt);
        }

    }

    public static int[] updateToDelivered(Connection conn, ArrayList<FullSms> fullSmsBatch) throws Exception {
        //Update statement for Delivery Status
        String query = "Update " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " Set "
                + DBStruct.VFE_CS_SMS_H.DELIVERY_DATE + " = SYSTIMESTAMP,"
                + DBStruct.VFE_CS_SMS_H.STATUS + " = " + Defines.VFE_CS_SMS_H_STATUS_LK.DELIVERED + ","
                + DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT + " = ?,"
                + DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT + " = ?"
                + " Where ROWID = ?";

        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(query);
            for (FullSms fullSms : fullSmsBatch) {
                int i = 1;
                ps.setInt(i++, fullSms.getDelivered());
                ps.setInt(i++, fullSms.getAllStatusExceptDeliveredCount());
                ps.setString(i++, fullSms.getRowId());
                ps.addBatch();
            }
            return ps.executeBatch();
        } finally {
            DataSourceManger.closeDBResources(null, ps);
        }
    }

    public static int[] updateToPartialDelivery(Connection conn, ArrayList<FullSms> fullSmsBatch) throws Exception {
        //Update statement for Delivery Status
        String query = "Update " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " Set "
                + DBStruct.VFE_CS_SMS_H.DELIVERY_DATE + " = SYSTIMESTAMP,"
                + DBStruct.VFE_CS_SMS_H.STATUS + " = " + Defines.VFE_CS_SMS_H_STATUS_LK.PARTIAL_DELIVERY + ","
                + DBStruct.VFE_CS_SMS_H.DELIVERED_COUNT + " = ?,"
                + DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT + " = ?"
                + " Where ROWID = ?";

        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(query);
            for (FullSms fullSms : fullSmsBatch) {
                int i = 1;
                ps.setInt(i++, fullSms.getDelivered());
                ps.setInt(i++, fullSms.getAllStatusExceptDeliveredCount());
                ps.setString(i++, fullSms.getRowId());
                ps.addBatch();
            }
            return ps.executeBatch();
        } finally {
            DataSourceManger.closeDBResources(null, ps);
        }
    }

    public static int[] updateToNotDelivered(Connection conn, ArrayList<FullSms> fullSmsBatch) throws Exception {
        //Update statement for Delivery Status
        String query = "Update " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " Set "
                + DBStruct.VFE_CS_SMS_H.STATUS + " = " + Defines.VFE_CS_SMS_H_STATUS_LK.NOT_DELIVERED + ","
                + DBStruct.VFE_CS_SMS_H.OTHER_STATUS_COUNT + " = ?"
                + " Where ROWID = ?";

        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(query);
            for (FullSms fullSms : fullSmsBatch) {
                int i = 1;
                ps.setInt(i++, fullSms.getAllStatusExceptDeliveredCount());
                ps.setString(i++, fullSms.getRowId());
                ps.addBatch();
            }
            return ps.executeBatch();
        } finally {
            DataSourceManger.closeDBResources(null, ps);
        }
    }

    public static int[] updateToSentToSmsc(Connection conn, ArrayList<FullSms> fullSmsBatch) throws Exception {
        //Update statement for Delivery Status
        String query = "Update " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " Set "
                + DBStruct.VFE_CS_SMS_H.STATUS + " = " + Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC + ","
                + DBStruct.VFE_CS_SMS_H.SENDING_DATE + " = ?"
                + " Where ROWID = ?";

        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(query);
            for (FullSms fullSms : fullSmsBatch) {
                int i = 1;
                ps.setTimestamp(i++, fullSms.getSENDING_DATE());
                ps.setString(i++, fullSms.getRowId());
                ps.addBatch();
            }
            return ps.executeBatch();
        } finally {
            DataSourceManger.closeDBResources(null, ps);
        }
    }

    public static int updateToTimedOut(Connection conn, JobModel jobModel) throws Exception {
        //Update statement for Delivery Status
        String query = "Update " + DBStruct.VFE_CS_SMS_H.TABLE_NAME + " Set "
                + DBStruct.VFE_CS_SMS_H.STATUS + " = " + Defines.VFE_CS_SMS_H_STATUS_LK.TIMED_OUT
                + " Where TRUNC(" + DBStruct.VFE_CS_SMS_H.SUBMISSION_DATE + ") = ?"
                + " And " + DBStruct.VFE_CS_SMS_H.MSISDN_MOD_X + " = ?"
                + " And " + DBStruct.VFE_CS_SMS_H.STATUS + " In (" + Defines.VFE_CS_SMS_H_STATUS_LK.ENQUEUED
                + "," + Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC + ")";

        PreparedStatement ps = null;

        try {
            int i = 1;
            ps = conn.prepareStatement(query);
            ps.setString(i++, jobModel.getDate());
            ps.setInt(i++, jobModel.getModX());
            return ps.executeUpdate();
        } finally {
            DataSourceManger.closeDBResources(null, ps);
        }
    }

}
