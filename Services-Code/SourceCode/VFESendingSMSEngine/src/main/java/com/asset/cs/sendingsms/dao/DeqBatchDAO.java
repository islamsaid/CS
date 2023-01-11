/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.dao;

import client.HashObject;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMS;
import com.asset.cs.sendingsms.defines.ErrorCodes;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.ArrayList;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

/**
 *
 * @author kerollos.asaad
 */
public class DeqBatchDAO {

    public static ArrayList<HashObject> deqBatch(Connection con, String appId, int batchSize, int waitTime, QueueModel appQueue, int threadNumber) throws CommonException {
        long t = System.currentTimeMillis();
        StringBuilder batchId = new StringBuilder(appId);
        ArrayList<HashObject> smsObjectList = null;
        CallableStatement callableStatement = null;
        CommonLogger.businessLogger.info("Start calling DEQ_BATCH stored procedure....");
        try {
            Connection oracleConnection = con.unwrap(OracleConnection.class);
            callableStatement = oracleConnection.prepareCall("CALL " + "DEQ_BATCH" + "(?,?,?,?,?,?,?,?,?,?,?,?)");
            callableStatement.setString(1, appId);
            callableStatement.setInt(2, waitTime);
            callableStatement.setInt(3, batchSize);
            ///////
            if (appQueue.getTimeWindowFromHour() == null) {
                callableStatement.setInt(4, 0);
            } else {
                callableStatement.setInt(4, appQueue.getTimeWindowFromHour().intValue());
            }
            if (appQueue.getTimeWindowFromMin() == null) {
                callableStatement.setInt(5, 0);
            } else {
                callableStatement.setInt(5, appQueue.getTimeWindowFromMin().intValue());
            }
            if (appQueue.getTimeWindowToHour() == null) {
                callableStatement.setInt(6, 0);
            } else {
                callableStatement.setInt(6, appQueue.getTimeWindowToHour().intValue());
            }
            if (appQueue.getTimeWindowToMin() == null) {
                callableStatement.setInt(7, 0);
            } else {
                callableStatement.setInt(7, appQueue.getTimeWindowToMin().intValue());
            }
            if (appQueue.isTimeWindowFlag()) {
                callableStatement.setInt(8, 1);
            } else {
                callableStatement.setInt(8, 0);
            }
            ///
            callableStatement.registerOutParameter(9, OracleTypes.ARRAY, "SMS_BATCH");
            callableStatement.registerOutParameter(10, OracleTypes.ARRAY, "PRIORITY_ARRAY");
            callableStatement.registerOutParameter(11, OracleTypes.ARRAY, "ENQTIME_ARRAY");
            callableStatement.registerOutParameter(12, OracleTypes.ARRAY, "ENQTIME_ARRAY");
            callableStatement.executeUpdate();
            smsObjectList = new ArrayList<HashObject>();
            ArrayDescriptor outSMSArrDesc = ArrayDescriptor.createDescriptor("SMS_BATCH", oracleConnection);
            ARRAY outSMSArray = new ARRAY(outSMSArrDesc, oracleConnection, null);
            outSMSArray = (ARRAY) callableStatement.getArray(9);
            ArrayDescriptor outArrPriorityDesc = ArrayDescriptor.createDescriptor("PRIORITY_ARRAY", oracleConnection);
            ARRAY outArrPriority = new ARRAY(outArrPriorityDesc, oracleConnection, null);
            outArrPriority = (ARRAY) callableStatement.getArray(10);
            ArrayDescriptor outArrEnqTimeDesc = ArrayDescriptor.createDescriptor("ENQTIME_ARRAY", oracleConnection);
            ARRAY outArrEnqTime = new ARRAY(outArrEnqTimeDesc, oracleConnection, null);
            outArrEnqTime = (ARRAY) callableStatement.getArray(11);
            ArrayDescriptor outArrMsgsIdsDesc = ArrayDescriptor.createDescriptor("ENQTIME_ARRAY", oracleConnection);
            ARRAY outArrMsgsIds = new ARRAY(outArrMsgsIdsDesc, oracleConnection, null);
            outArrMsgsIds = (ARRAY) callableStatement.getArray(12);

            if (outSMSArray != null && outSMSArray.getArray() != null) {
                batchId.append("_" + threadNumber);
                batchId.append("_" + String.valueOf(System.currentTimeMillis()));

                Object[] objs_SMS = (Object[]) outSMSArray.getArray();
                Object[] objs_EnqTime = (Object[]) outArrEnqTime.getArray();
                Object[] objs_Priority = (Object[]) outArrPriority.getArray();
                Object[] objs_MsgsIds = (Object[]) outArrMsgsIds.getArray();

                HashObject smsObject;
                for (int i = 0; i < objs_SMS.length; i++) {
                    smsObject = new HashObject();
                    if (objs_SMS[i] != null) {
                        Struct struct = (Struct) objs_SMS[i];
                        Object[] attrs = struct.getAttributes();
                        SMS smsObj = new SMS(((BigDecimal) attrs[0]), attrs[1].toString(), attrs[2].toString(),
                                attrs[3].toString(), attrs[4].toString(), ((BigDecimal) attrs[5]),
                                ((BigDecimal) attrs[6]), ((BigDecimal) attrs[7]), ((BigDecimal) attrs[8]),
                                ((BigDecimal) attrs[9]), ((BigDecimal) attrs[10]), ((BigDecimal) attrs[11]),
                                (String) attrs[12], ((BigDecimal) attrs[13]), ((String) attrs[14]),
                                ((String) attrs[15]), ((String) attrs[16]), ((String) attrs[17]),
                                ((String) attrs[18]), (Timestamp) attrs[19], ((BigDecimal) attrs[20]),
                                (String) attrs[21], (String) attrs[22], (String) attrs[23], ((BigDecimal) attrs[24]),
                                ((BigDecimal) attrs[25]), ((BigDecimal) attrs[26]), (String) attrs[27], (String) attrs[28],
                                ((BigDecimal) attrs[29]));
                        smsObject.setSMS(smsObj);
                    }
                    if (objs_EnqTime[i] != null) {
                        smsObject.setEnqueueTime((String) objs_EnqTime[i]);
                    }
                    if (objs_Priority[i] != null) {
                        smsObject.setPriority(((BigDecimal) objs_Priority[i]).intValue());
                    }

                    if (objs_MsgsIds[i] != null) {
                        smsObject.setMsgid((String) objs_MsgsIds[i]);
                    }
                    smsObject.setBatchId(batchId.toString());
                    smsObjectList.add(smsObject);
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 25228) {
//                CommonLogger.businessLogger.info("No SMS in Q for " + appId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No SMS in Queue")
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, appId).build());
            } else {
                // CommonLogger.businessLogger.error("SQLException in deqBatch--->" + e);
                CommonLogger.errorLogger.error("SQLException in deqBatch--->" + e, e);
                throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_DEQ_BATCH_DAO, e.getMessage());
            }
        } catch (Exception e) {
            // CommonLogger.businessLogger.error("Exception in deqBatch--->" + e);
            CommonLogger.errorLogger.error("Exception in deqBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_DEQ_BATCH_DAO, e.getMessage());
        } finally {
            // 20 0 10 0
            // 18 
            if (callableStatement != null) {
                try {
                    callableStatement.close();
                } catch (SQLException e) {
                    // CommonLogger.businessLogger.error("SQLException while closing callableStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing callableStatement--->" + e, e);
                }
            }
            if (smsObjectList != null) {
//                CommonLogger.businessLogger.info("DeqBatch Function executed in " + (System.currentTimeMillis() - t) + " msecs for batch size:" + smsObjectList.size() + " and batch id:" + batchId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DeqBatch Function Executed")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - t))
                        .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, smsObjectList.size())
                        .put(GeneralConstants.StructuredLogKeys.BATCH_ID, batchId).build());
            } else {
//                CommonLogger.businessLogger.info("DeqBatch Function executed in " + (System.currentTimeMillis() - t) + " msecs");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DeqBatch Function Executed")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - t)).build());
            }
        }
        return smsObjectList;

    }
}
