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
import com.asset.cs.sendingsms.defines.ErrorCodes;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;

/**
 *
 * @author kerollos.asaad
 */
public class EnqMsgDAO {

    public static boolean enqMsg(HashObject SMSObject, Connection con) throws CommonException {
        OracleCallableStatement ocs = null;
        boolean ret = false;
        try {
            CommonLogger.businessLogger.debug("ThreadName || " + Thread.currentThread().getName());
//            CommonLogger.businessLogger.debug("Params || AppID || " + SMSObject.getSMS().getAppId());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "App Params")
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, SMSObject.getSMS().getAppId()).build());
            Connection oracleConnection = con.unwrap(OracleConnection.class);
            ocs = (OracleCallableStatement) oracleConnection.prepareCall("{call ENQ_SMS(?,?,?)}");
            ocs.setObject(1, SMSObject.getSMS());
            ocs.setBigDecimal(2, new BigDecimal(SMSObject.getPriority()));
            //SMSObject.getSMS().setAppId("SMS_Q1");
            ocs.setString(3, SMSObject.getSMS().getAppId());

            CommonLogger.businessLogger.debug("Calling Enq prc");
            ocs.execute();
            CommonLogger.businessLogger.debug("Enq prc finished");
            ret = true;// operation done successfully
        } // end of try
        catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException || " + e);
            CommonLogger.errorLogger.error("SQLException || " + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.SQL_ERROR_IN_ENQ_SMS_DAO, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception || " + e);
            CommonLogger.errorLogger.error("Exception || " + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_ENQ_SMS_DAO, e.getMessage());
        } finally {
            try {
                if (ocs != null) {
                    ocs.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error("SQLException || " + e);
                CommonLogger.errorLogger.error("SQLException || " + e, e);
            }
        }
        return ret;
    }// end function
}
