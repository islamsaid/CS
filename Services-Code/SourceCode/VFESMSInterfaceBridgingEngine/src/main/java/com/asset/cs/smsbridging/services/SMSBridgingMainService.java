package com.asset.cs.smsbridging.services;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.cs.smsbridging.models.HTTPMsgResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author aya.moawed 2595
 */
public class SMSBridgingMainService {

//    public ArrayList<SMSBridgeDBObject> dequeueBatch(DataSourceManager dataSource, String appQueueName, int batchSize, int waitTime, int threadNumber) throws CommonException {
//        Connection conn = null;
//        SMSBridgeBatchService deqBatchService = new SMSBridgeBatchService();
//        ArrayList<SMSBridgeDBObject> smsObjectsList = new ArrayList<>();
//        try {
//            conn = dataSource.getConnection();
//            smsObjectsList = deqBatchService.dequeueBatch(conn, appQueueName, batchSize, waitTime, threadNumber);
//            conn.commit();
//        } catch (CommonException ex) {
//            if (conn != null) {
//                try {
//                    conn.rollback();
//                } catch (SQLException ex1) {
//                    CommonLogger.businessLogger.error("Exception in dequeueBatch rollback--->" + ex1);
//                    CommonLogger.errorLogger.error("Exception in dequeueBatch rollback--->" + ex1, ex1);
//                }
//            }
//            CommonLogger.businessLogger.error("Exception in dequeueBatch rollback--->" + ex);
//            CommonLogger.errorLogger.error("Exception in dequeueBatch rollback--->" + ex, ex);
//            handleServiceException(ex, "dequeueBatch");
//            throw ex;
//        } catch (Exception ex) {
//            if (conn != null) {
//                try {
//                    conn.rollback();
//                } catch (SQLException ex1) {
//                    CommonLogger.businessLogger.error("Exception in dequeueBatch rollback--->" + ex);
//                    CommonLogger.errorLogger.error("Exception in dequeueBatch rollback--->" + ex, ex);
//                }
//            }
//            CommonLogger.businessLogger.error("Exception in dequeueBatch--->" + ex);
//            CommonLogger.errorLogger.error("Exception in dequeueBatch--->" + ex, ex);
//            handleServiceException(ex, "dequeueBatch");
//            throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
//        } finally {
//            if (conn != null) {
//                dataSource.closeConnection(conn);
//            }
//        }
//        return smsObjectsList;
//    }

//    public void enqueueMsg(ArrayList<SMSBridgeDBObject> sms, String queueName, DataSourceManager dataSource) throws CommonException {
//        Connection con = null;
//        SMSBridgeBatchService enqMsgService = new SMSBridgeBatchService();
//        try {
//            con = dataSource.getConnection();
//            enqMsgService.enqueueBatch(sms, queueName, con);
//            con.commit();
//        } catch (CommonException ex) {
//            if (con != null) {
//                try {
//                    con.rollback();
//                } catch (SQLException ex1) {
//                    CommonLogger.businessLogger.error("Exception in enqueueMsg rollback--->" + ex1);
//                    CommonLogger.errorLogger.error("Exception in enqueueMsg rollback--->" + ex1, ex1);
//                }
//            }
//            CommonLogger.businessLogger.error("Exception in enqueueMsg rollback--->" + ex);
//            CommonLogger.errorLogger.error("Exception in enqueueMsg rollback--->" + ex, ex);
//            handleServiceException(ex, "enqueueMsg");
//            throw ex;
//        } catch (Exception ex) {
//            if (con != null) {
//                try {
//                    con.rollback();
//                } catch (SQLException ex1) {
//                    CommonLogger.businessLogger.error("Exception in enqueueMsg rollback--->" + ex);
//                    CommonLogger.errorLogger.error("Exception in enqueueMsg rollback--->" + ex, ex);
//                }
//            }
//            CommonLogger.businessLogger.error("Exception in enqueueMsg rollback--->" + ex);
//            CommonLogger.errorLogger.error("Exception in enqueueMsg rollback--->" + ex, ex);
//            handleServiceException(ex, "enqueueMsg");
//            throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
//        } finally {
//            if (con != null) {
//                DataSourceManger.closeConnection(con);
//            }
//        }
//    }

    public void updateStatusForSMSBatch(ArrayList<HTTPMsgResult> msgsStatus,ArrayList<Long> smsMsgIds) throws CommonException {
        ProcedureHistoryService service = new ProcedureHistoryService();
        Connection con = null;
        try {
            con = Manager.queuDataSourceManager.getConnection();
            service.updateStatusForSMSBatch(con, msgsStatus,smsMsgIds);
            con.commit();
        } catch (CommonException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateStatusForSMSBatch rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in updateStatusForSMSBatch rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateStatusForSMSBatch rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateStatusForSMSBatch rollback--->" + ex, ex);
            handleServiceException(ex, "updateStatusForSMSBatch");
            throw ex;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateStatusForSMSBatch rollback--->" + ex);
                    CommonLogger.errorLogger.error("Exception in updateStatusForSMSBatch rollback--->" + ex, ex);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateStatusForSMSBatch rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateStatusForSMSBatch rollback--->" + ex, ex);
            handleServiceException(ex, "updateStatusForSMSBatch");
            throw new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException commonException = null;
        // Handle CommonException 
        if (e instanceof CommonException) {
            commonException = (CommonException) e;
        } // Handle SQL Exception 
        else {
            commonException = new CommonException(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE, ErrorCodes.SMS_BRIDGING_ENGINE.GENERAL_ERROR_IN_MAIN_SERVICE, e.getMessage());
        }
        MOMErrorsModel errorModel = new MOMErrorsModel();
        //errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setPreceivedSeverity(Integer.valueOf(SMSBridgingDefines.databaseConfigs.get(Defines.DATABASE_MOM_ERROR_SEVERITY)));
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE);
        errorModel.setErrorCode(commonException.getErrorCode());
        errorModel.setModuleName("SMS Interface Bridging Engine");
        errorModel.setFunctionName(methodName);
        errorModel.setErrorMessage(commonException.getErrorMsg());
        Utility.sendMOMAlarem(errorModel);
    }

}
