/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMS;
import com.asset.contactstrategy.common.models.SMSBridgeDBObject;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author esmail.anbar
 */
public class DBQueueDAO {

    public void sendMessageBatch(Connection conn, String queueName, SMS[] objectsArray, StringBuilder csMsgIds) throws CommonException {
        long time = System.currentTimeMillis();
        CallableStatement callableStatement = null;
        Connection oracleConn = null;

        try {
//            ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, csMsgIds.toString());
//            CommonLogger.businessLogger.info("Attempting Insertion of CsMsgIds: " + csMsgIds.toString());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Attempting Insertion of sendMessageBatch")
                    .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, objectsArray.length).build());
            oracleConn = conn.unwrap(OracleConnection.class);
//          oracleConn.setAutoCommit(false);

            ArrayDescriptor arrDesc = ArrayDescriptor.createDescriptor("SMS_BATCH", oracleConn);
            ARRAY array = new ARRAY(arrDesc, oracleConn, objectsArray);

            callableStatement = oracleConn.prepareCall("CALL " + "ENQ_BATCH" + " (?,?)");
            callableStatement.setArray(1, array);
            callableStatement.setString(2, queueName);

            callableStatement.executeUpdate();

//            CommonLogger.businessLogger.info("Successfull Insertion of CsMsgIds: " + csMsgIds.toString() + "Time in MilliSeconds: " + (System.currentTimeMillis() - time));
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfull Insertion")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time)).build());
//          oracleConn.commit();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("Exception---->  for [sendMessageBatch] Failed To send message batch " + e);
            CommonLogger.errorLogger.error("Exception---->  for [sendMessageBatch] Failed To send message batch " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception---->  for [sendMessageBatch] Failed To send message batch " + e);
            CommonLogger.errorLogger.error("Exception---->  for [sendMessageBatch] Failed To send message batch " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
//                if (oracleConn != null)
//                    oracleConn.close();
                if (callableStatement != null) {
                    callableStatement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.businessLogger.error("Exception---->  for [sendMessageBatch] Failed To Close Connection " + ex);
                CommonLogger.errorLogger.error("Exception---->  for [sendMessageBatch] Failed To Close Connection " + ex, ex);
//                ThreadContext.clearMap();
            }
        }
    }

    public void enqueueBatch(Connection con, String queueName, ArrayList<SMSBridgeDBObject> sms) throws CommonException {
        long time = System.currentTimeMillis();
        CallableStatement callableStatement = null;
        Connection oracleConn = null;

        try {
            //CommonLogger.businessLogger.info("Starting Enqueue of SMS Bridge: " + sms.toString());
//            CommonLogger.businessLogger.info("Starting Enqueue of SMS Bridge: " + sms.toString());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Enqueue of SMS Bridge").build());
            CommonLogger.businessLogger.trace(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Exposing SMSs batch")
                    .put(GeneralConstants.StructuredLogKeys.SMS_INFO, sms).build());
            oracleConn = con.unwrap(OracleConnection.class);

            ArrayDescriptor arrDesc = ArrayDescriptor.createDescriptor("SMS_BATCH", oracleConn);
            ARRAY array = new ARRAY(arrDesc, oracleConn, sms.toArray());

            callableStatement = oracleConn.prepareCall("CALL " + Defines.SMS_BRIDGING_ENGINE.ENQ_BATCH_PROCEDURE + " (?,?)");
            callableStatement.setArray(1, array);
            callableStatement.setString(2, queueName);

            callableStatement.executeUpdate();

            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfull Insertion of CsMsgIds")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time)).build());
//            System.out.println("Successfull Insertion of CsMsgIds" + sms.toString() + "Time in MilliSeconds: " + (System.currentTimeMillis() - time));
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
