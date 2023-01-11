package com.asset.cs.smsbridging.daos;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.smsbridging.models.HTTPMsgResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author aya.moawed
 */
public class ProcedureHistoryDAO {

    public void updateStatusForSMSBatch(Connection connection, ArrayList<HTTPMsgResult> msgsStatus, ArrayList<Long> smsMsgIds) throws CommonException {
        long t = System.currentTimeMillis();
        CommonLogger.businessLogger.debug("Start updateStatusForSMSBatch ....");
        PreparedStatement preparedStatement = null;
        String sql = null;
        int[] result;
        try {
            sql = "UPDATE " + DBStruct.VFE_CS_PROCEDURES_H.TABLE_NAME + " SET " + DBStruct.VFE_CS_PROCEDURES_H.CS_MSG_ID + "=? , " + DBStruct.VFE_CS_PROCEDURES_H.STATUS + "=? where " + DBStruct.VFE_CS_PROCEDURES_H.MSG_ID + "=?";
//            CommonLogger.businessLogger.info("SQL ["+sql+"]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            preparedStatement = connection.prepareStatement(sql);

            for (int index = 0; index < msgsStatus.size(); index++) {
//                CommonLogger.businessLogger.info("Updating Msg ID = " + smsMsgIds.get(index) + " with status =" + msgsStatus.get(index).getStatus() + " and CsMsgId = " + msgsStatus.get(index).getMsgID());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update SQL Query Info")
                        .put(GeneralConstants.StructuredLogKeys.SMS_MSG_ID, smsMsgIds.get(index))
                        .put(GeneralConstants.StructuredLogKeys.STATUS, msgsStatus.get(index))
                        .put(GeneralConstants.StructuredLogKeys.MSG_ID, msgsStatus.get(index).getMsgID()).build());
                if (msgsStatus.get(index).getMsgID() != null) {
                    preparedStatement.setLong(1, msgsStatus.get(index).getMsgID());
                } else {
                    preparedStatement.setLong(1, -1);
                }
                preparedStatement.setString(2, msgsStatus.get(index).getStatus());
                preparedStatement.setLong(3, smsMsgIds.get(index));
                preparedStatement.addBatch();
            }

            result = preparedStatement.executeBatch();

        } catch (SQLException e) {
            CommonLogger.businessLogger.error("SQLException in updateStatusForSMSBatch--->" + e);
            CommonLogger.errorLogger.error("SQLException in updateStatusForSMSBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.DATABASE_ERROR, e.getMessage());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Exception in updateStatusForSMSBatch--->" + e);
            CommonLogger.errorLogger.error("Exception in updateStatusForSMSBatch--->" + e, e);
            throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.GENERAL_ERROR, e.getMessage());
        } finally {
//            CommonLogger.businessLogger.info("End updateStatusForSMSBatch .... in " + (System.currentTimeMillis() - t) + " msecs");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End updateStatusForSMSBatch")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - t)).build());
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.DATASOURCE_CLOSE, ex.getMessage());
                }
            }
        }
    }

}
