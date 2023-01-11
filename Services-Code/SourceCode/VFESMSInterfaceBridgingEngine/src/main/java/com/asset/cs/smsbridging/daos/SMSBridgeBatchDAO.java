package com.asset.cs.smsbridging.daos;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.SMSBridgeDBObject;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

/**
 *
 * @author aya.moawed 2595
 */
public class SMSBridgeBatchDAO {

    public ArrayList<SMSBridgeDBObject> dequeueBatch(Connection connection, String queueName, int batchSize, int waitTime, int threadNumber) throws CommonException {
        long t = System.currentTimeMillis();
        StringBuilder batchId = new StringBuilder(queueName);
        ArrayList<SMSBridgeDBObject> smsObjectList = null;
        CallableStatement callableStatement = null;
        CommonLogger.businessLogger.info("Start calling " + Defines.SMS_BRIDGING_ENGINE.DEQ_BATCH_PROCEDURE + " stored procedure....");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Calling Stored Procedure")
                .put(GeneralConstants.StructuredLogKeys.PROCEDURE, Defines.SMS_BRIDGING_ENGINE.DEQ_BATCH_PROCEDURE).build());
        try {
            Connection oracleConnection = connection.unwrap(OracleConnection.class);
            callableStatement = oracleConnection.prepareCall("CALL " + Defines.SMS_BRIDGING_ENGINE.DEQ_BATCH_PROCEDURE + "(?,?,?,?,?,?)");
            callableStatement.setString(1, queueName);
            callableStatement.setInt(2, waitTime);
            callableStatement.setInt(3, batchSize);
            callableStatement.registerOutParameter(4, OracleTypes.ARRAY, "SMS_BATCH");
            callableStatement.registerOutParameter(5, OracleTypes.ARRAY, "PRIORITY_ARRAY");
            callableStatement.registerOutParameter(6, OracleTypes.ARRAY, "ENQTIME_ARRAY");
            callableStatement.executeUpdate();
            smsObjectList = new ArrayList<SMSBridgeDBObject>();
            ArrayDescriptor outSMSArrDesc = ArrayDescriptor.createDescriptor("SMS_BATCH", oracleConnection);
            ARRAY outSMSArray = new ARRAY(outSMSArrDesc, oracleConnection, null);
            outSMSArray = (ARRAY) callableStatement.getArray(4);
            ArrayDescriptor outArrPriorityDesc = ArrayDescriptor.createDescriptor("PRIORITY_ARRAY", oracleConnection);
            ARRAY outArrPriority = new ARRAY(outArrPriorityDesc, oracleConnection, null);
            outArrPriority = (ARRAY) callableStatement.getArray(5);
            ArrayDescriptor outArrEnqTimeDesc = ArrayDescriptor.createDescriptor("ENQTIME_ARRAY", oracleConnection);
            ARRAY outArrEnqTime = new ARRAY(outArrEnqTimeDesc, oracleConnection, null);
            outArrEnqTime = (ARRAY) callableStatement.getArray(6);

            if (outSMSArray != null && outSMSArray.getArray() != null) {
                batchId.append("_" + threadNumber);
                batchId.append("_" + String.valueOf(System.currentTimeMillis()));

                Object[] objs_SMS = (Object[]) outSMSArray.getArray();
                Object[] objs_EnqTime = (Object[]) outArrEnqTime.getArray();
                Object[] objs_Priority = (Object[]) outArrPriority.getArray();

                for (int i = 0; i < objs_SMS.length; i++) {
                    if (objs_SMS[i] != null) {
                        Struct struct = (Struct) objs_SMS[i];
                        Object[] attrs = struct.getAttributes();
                        SMSBridgeDBObject smsObj = new SMSBridgeDBObject(
                                ((BigDecimal) attrs[0]),
                                ((attrs.length >= 2 && attrs[1] != null) ? attrs[1].toString() : null),
                                ((attrs.length >= 3 && attrs[2] != null) ? attrs[2].toString() : null),
                                ((attrs.length >= 4 && attrs[3] != null) ? attrs[3].toString() : null),
                                ((attrs.length >= 5 && attrs[4] != null) ? attrs[4].toString() : null),
                                ((attrs.length >= 6 && attrs[5] != null) ? attrs[5].toString() : null),
                                ((BigDecimal) attrs[6]), ((BigDecimal) attrs[7]), ((BigDecimal) attrs[8]),
                                ((attrs.length >= 10 && attrs[9] != null) ? attrs[9].toString() : null),
                                ((attrs.length >= 11 && attrs[10] != null) ? attrs[10].toString() : null),
                                ((attrs.length >= 12 && attrs[11] != null) ? attrs[11].toString() : null),
                                ((attrs.length >= 13 && attrs[12] != null) ? attrs[12].toString() : null),
                                ((attrs.length >= 14 && attrs[13] != null) ? attrs[13].toString() : null),
                                ((attrs.length >= 15 && attrs[14] != null) ? attrs[14].toString() : null),
                                ((attrs.length >= 16 && attrs[15] != null) ? ((String) attrs[15]) : null),
                                ((attrs.length >= 17 && attrs[16] != null) ? ((String) attrs[16]) : null),
                                ((attrs.length >= 18 && attrs[17] != null) ? ((String) attrs[17]) : null),
                                ((attrs.length == 19 && attrs[18] != null) ? ((String) attrs[18]) : null),
                                ((attrs.length == 20 && attrs[19] != null) ? ((Timestamp) attrs[19]) : null));
                        smsObj.setMsgId(((attrs.length >= 1 && attrs[0] != null) ? Long.parseLong(attrs[0].toString()) : null));
                        smsObj.setQueueName(((attrs.length >= 2 && attrs[1] != null) ? attrs[1].toString() : null));
                        smsObj.setServiceName(((attrs.length >= 3 && attrs[2] != null) ? attrs[2].toString() : null));
                        smsObj.setOriginatorMSISDN(((attrs.length >= 4 && attrs[3] != null) ? attrs[3].toString() : null));
                        smsObj.setDestinationMSISDN(((attrs.length >= 5 && attrs[4] != null) ? attrs[4].toString() : null));
                        smsObj.setMsgText(((attrs.length >= 6 && attrs[5] != null) ? attrs[5].toString() : null));
                        smsObj.setMsgType(((attrs.length >= 7 && attrs[6] != null) ? Integer.parseInt(attrs[6].toString()) : null));
                        smsObj.setOriginatorType(((attrs.length >= 8 && attrs[7] != null) ? Integer.parseInt(attrs[7].toString()) : null));
                        smsObj.setLanguageId(((attrs.length >= 9 && attrs[8] != null) ? Integer.parseInt(attrs[8].toString()) : null));
                        smsObj.setIpAddress(((attrs.length >= 10 && attrs[9] != null) ? attrs[9].toString() : null));
                        smsObj.setDoNotApply(((attrs.length >= 11 && attrs[10] != null) ? attrs[10].toString() : null));
                        smsObj.setMessagePriority(((attrs.length >= 12 && attrs[11] != null) ? attrs[11].toString() : null));
                        smsObj.setTemplateId(((attrs.length >= 13 && attrs[12] != null) ? attrs[12].toString() : null));
                        smsObj.setTemplateParameters(((attrs.length >= 14 && attrs[13] != null) ? attrs[13].toString() : null));
                        smsObj.setOptionalParam1(((attrs.length >= 15 && attrs[14] != null) ? attrs[14].toString() : null));
                        smsObj.setOptionalParam2(((attrs.length >= 16 && attrs[15] != null) ? ((String) attrs[15]) : null));
                        smsObj.setOptionalParam3(((attrs.length >= 17 && attrs[16] != null) ? ((String) attrs[16]) : null));
                        smsObj.setOptionalParam4(((attrs.length >= 18 && attrs[17] != null) ? ((String) attrs[17]) : null));
                        smsObj.setOptionalParam5(((attrs.length >= 19 && attrs[18] != null) ? ((String) attrs[18]) : null));
                        smsObj.setSubmissionDate(((attrs.length == 20 && attrs[19] != null) ? new Date(((Timestamp) attrs[19]).getTime()) : null));
                        smsObjectList.add(smsObj);
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 25228) {
//                CommonLogger.businessLogger.info("No SMS found for queue with APP_NAME = " + queueName);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No SMS Found for Queue")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());
                //CommonLogger.businessLogger.info("No SMS found for queue with APP_NAME = " + queueName);
            } else {
                CommonLogger.businessLogger.error("SQLException in dequeueBatch--->" + e);
                CommonLogger.errorLogger.error("SQLException in dequeueBatch--->" + e, e);
                throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.SQL_ERROR_IN_DEQ_SMS_BRDGING_BATCH_DAO, e.getMessage());
            }
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in dequeueBatch--->" + e);
            CommonLogger.errorLogger.error("Exception in dequeueBatch--->" + e, e);
            throw new CommonException(e.getMessage(), com.asset.contactstrategy.common.defines.ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (callableStatement != null) {
                try {
                    callableStatement.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing callableStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing callableStatement--->" + e, e);
                }
            }
            if (smsObjectList != null) {
//                CommonLogger.businessLogger.info("DeqBatch Function executed in " + (System.currentTimeMillis() - t) + " msecs for batch size:" + smsObjectList.size() + " and batch id:" + batchId);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DeqBatch Function Execution")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - t))
                        .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, smsObjectList.size())
                        .put(GeneralConstants.StructuredLogKeys.BATCH_ID, batchId).build());
            } else {
//                CommonLogger.businessLogger.info("DeqBatch Function executed in " + (System.currentTimeMillis() - t) + " msecs");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DeqBatch Function Execution")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - t)).build());
            }
        }
        return smsObjectList;

    }

    public void enqueueBatch(Connection con, String queueName, ArrayList<SMSBridgeDBObject> sms) throws CommonException {
        long time = System.currentTimeMillis();
        CallableStatement callableStatement = null;
        Connection oracleConn = null;

        try {
            //CommonLogger.businessLogger.info("Starting Enqueue of SMS Bridge: " + sms.toString());
//            CommonLogger.businessLogger.info("Starting Enqueue of SMS Bridge: " + sms.toString());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting  Enqueue for SMS Bridge")
                    .put(GeneralConstants.StructuredLogKeys.SMS_SCRIPT, sms.toString()).build());
            oracleConn = con.unwrap(OracleConnection.class);

            ArrayDescriptor arrDesc = ArrayDescriptor.createDescriptor("SMS_BATCH", oracleConn);
            ARRAY array = new ARRAY(arrDesc, oracleConn, sms.toArray());

            callableStatement = oracleConn.prepareCall("CALL " + Defines.SMS_BRIDGING_ENGINE.ENQ_BATCH_PROCEDURE + " (?,?)");
            callableStatement.setArray(1, array);
            callableStatement.setString(2, queueName);

            callableStatement.executeUpdate();

            System.out.println("Successfull Insertion of CsMsgIds: " + sms.toString() + "Time in MilliSeconds: " + (System.currentTimeMillis() - time));
        } catch (SQLException e) {
            // CommonLogger.businessLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + e);
            CommonLogger.businessLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + e);
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.SQL_ERROR_IN_ENQ_SMS_BRDGING_BATCH_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + e);
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + e, e);
            throw new CommonException(e.getMessage(), com.asset.contactstrategy.common.defines.ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (callableStatement != null) {
                    callableStatement.close();
                }
            } catch (SQLException e) {
                //CommonLogger.businessLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + e);
                CommonLogger.businessLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + e);
                CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + e, e);
                throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.CONNECTION_CLOSE, e.getMessage());
            }
        }
    }
}
