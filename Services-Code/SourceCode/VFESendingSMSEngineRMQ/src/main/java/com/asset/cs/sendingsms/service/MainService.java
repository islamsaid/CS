/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.service;

import client.HashObject;
import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.sendingsms.controller.DataSourceManager;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.defines.ErrorCodes;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class MainService {

    final static String CLASS_NAME = "com.asset.cs.sendingsms.service.MainService";

    public ArrayList<HashObject> deqBatch(String appId, int batchSize, int waitTime, DataSourceManager dataSource, QueueModel appQueue, int threadNumber) throws CommonException {
        Connection conn = null;
        DeqBatchService deqBatchService = new DeqBatchService();
        ArrayList<HashObject> smsObjectsList = new ArrayList<HashObject>();
        try {
            conn = dataSource.getConnection();
            smsObjectsList = deqBatchService.deqBatch(conn, appId, batchSize, waitTime, appQueue, threadNumber);
            conn.commit();
        } catch (CommonException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in deqBatch rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in deqBatch rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in deqBatch rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in deqBatch rollback--->" + ex, ex);
            handleServiceException(ex, "deqBatch");
            throw ex;
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in deqBatch rollback--->" + ex);
                    CommonLogger.errorLogger.error("Exception in deqBatch rollback--->" + ex, ex);
                }
            }
            CommonLogger.businessLogger.error("Exception in deqBatch--->" + ex);
            CommonLogger.errorLogger.error("Exception in deqBatch--->" + ex, ex);
            handleServiceException(ex, "deqBatch");
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
        } finally {
            if (conn != null) {
                dataSource.closeConnection(conn);
            }
        }
        return smsObjectsList;
    }

    public boolean enqMsg(HashObject sms, DataSourceManager dataSource) throws CommonException {
        Connection con = null;
        EnqMsgService enqMsgService = new EnqMsgService();
        boolean ret = false;
        try {
            con = dataSource.getConnection();
            ret = enqMsgService.enqMsg(sms, con);
            con.commit();
        } catch (CommonException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in enqMsg rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in enqMsg rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in enqMsg rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in enqMsg rollback--->" + ex, ex);
            handleServiceException(ex, "enqMsg");
            throw ex;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in enqMsg rollback--->" + ex);
                    CommonLogger.errorLogger.error("Exception in enqMsg rollback--->" + ex, ex);
                }
            }
            CommonLogger.businessLogger.error("Exception in enqMsg rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in enqMsg rollback--->" + ex, ex);
            handleServiceException(ex, "enqMsg");
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
        return ret;
    }

    public void updateArchive(ArrayList<HashObject> messages) throws CommonException {
        Connection con = null;
        InsertArchiveService archiveService = new InsertArchiveService();
        try {
            con = DataSourceManger.getConnection();
            archiveService.updateArchive(messages, con);
            con.commit();
        } catch (CommonException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateArchive rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in updateArchive rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateArchive rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateArchive rollback--->" + ex, ex);
            handleServiceException(ex, "updateArchive");
            throw ex;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateArchive rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in updateArchive rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateArchive rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateArchive rollback--->" + ex, ex);
            handleServiceException(ex, "updateArchive");
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    public void insertArchiveConc(ArrayList<HashObject> messages) throws CommonException {
        Connection con = null;
        InsertArchiveConcService archiveConcService = new InsertArchiveConcService();
        try {
            con = DataSourceManger.getConnection();
            archiveConcService.insertArchiveConc(messages, con);
            con.commit();
        } catch (CommonException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in insertArchiveConc rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in insertArchiveConc rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in insertArchiveConc rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in insertArchiveConc rollback--->" + ex, ex);
            handleServiceException(ex, "insertArchiveConc");
            throw ex;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in insertArchiveConc rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in insertArchiveConc rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in insertArchiveConc rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in insertArchiveConc rollback--->" + ex, ex);
            handleServiceException(ex, "insertArchiveConc");
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    /**
     * update message status in sms_concat_h, returning true if number of rows
     * affected > 0
     *
     * @param message
     * @return (rows_affected > 0)
     * @throws CommonException
     *
     */
    public void updateStatusAndCounterCachedBatch(ArrayList<HashObject> messages) throws CommonException {
        Connection con = null;
        UpdateOnDeliveryService updateOnDeliveryService = new UpdateOnDeliveryService();
        try {
            con = DataSourceManger.getConnection();
            updateOnDeliveryService.updateStatusCachedBatch(messages, con);
//            updateOnDeliveryService.updateCounterCachedBatch(messages, con);
            con.commit();
        } catch (CommonException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex, ex);
            handleServiceException(ex, "updateStatusAndCounterBatch");
            throw ex;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex);
                    CommonLogger.errorLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex, ex);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex, ex);
            handleServiceException(ex, "updateStatusAndCounter");
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    public void updateStatusAndCounterBatch(ArrayList<HashObject> messages) throws CommonException {
        Connection con = null;
        UpdateOnDeliveryService updateOnDeliveryService = new UpdateOnDeliveryService();
        try {
            con = DataSourceManger.getConnection();
            updateOnDeliveryService.updateStatusBatch(messages, con);
//            updateOnDeliveryService.updateCounterBatch(messages, con);
            con.commit();
        } catch (CommonException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex, ex);
            handleServiceException(ex, "updateStatusAndCounterBatch");
            throw ex;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex);
                    CommonLogger.errorLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex, ex);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateStatusAndCounterBatch rollback--->" + ex, ex);
            handleServiceException(ex, "updateStatusAndCounter");
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    public boolean updateStatusAndCounter(HashObject message) throws CommonException {
        Connection con = null;
        UpdateOnDeliveryService updateOnDeliveryService = new UpdateOnDeliveryService();
        boolean ret = false;
        try {
            con = DataSourceManger.getConnection();
            updateOnDeliveryService.selectCsId(message, con);
//            CommonLogger.businessLogger.info("message id got for (" + message.getSMS().getSeqId() + ")");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message is Got")
                    .put(GeneralConstants.StructuredLogKeys.MSG_ID, message.getSMS().getSeqId()).build());
            if (message.getSMS().getSeqId().compareTo(BigDecimal.valueOf(Long.valueOf(Defines.NOT_FOUND_SEQ_ID))) == 1) {
                ret = updateOnDeliveryService.updateStatus(message, con);
                if (ret == true) {
                    if (updateOnDeliveryService.updateCoutner(message, con)) {
//                        CommonLogger.businessLogger.info("done updating counter in history for (" + message.getSMS().getSeqId() + ")");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Done Updating Counter in History")
                                .put(GeneralConstants.StructuredLogKeys.MSG_ID, message.getSMS().getSeqId()).build());
                    }
                }
            }
            con.commit();
        } catch (CommonException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateStatusAndCounter rollback--->" + ex1);
                    CommonLogger.errorLogger.error("Exception in updateStatusAndCounter rollback--->" + ex1, ex1);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateStatusAndCounter rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateStatusAndCounter rollback--->" + ex, ex);
            handleServiceException(ex, "updateStatusAndCounter");
            throw ex;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    CommonLogger.businessLogger.error("Exception in updateStatusAndCounter rollback--->" + ex);
                    CommonLogger.errorLogger.error("Exception in updateStatusAndCounter rollback--->" + ex, ex);
                }
            }
            CommonLogger.businessLogger.error("Exception in updateStatusAndCounter rollback--->" + ex);
            CommonLogger.errorLogger.error("Exception in updateStatusAndCounter rollback--->" + ex, ex);
            handleServiceException(ex, "updateStatusAndCounter");
            throw new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_MAIN_SERVICE, ex.getMessage());
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
        return ret;
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException commonException = null;
        // Handle CommonException 
        if (e instanceof CommonException) {
            commonException = (CommonException) e;
        } // Handle SQL Exception 
        else {
            commonException = new CommonException(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE, ErrorCodes.GENERAL_ERROR_IN_MAIN_SERVICE, e.getMessage());
        }
        MOMErrorsModel errorModel = new MOMErrorsModel();
        //errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setPreceivedSeverity(Integer.valueOf(Defines.databaseConfigurations.get(Defines.DATABASE_MOM_ERROR_SEVERITY)));
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE);
        errorModel.setErrorCode(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE + commonException.getErrorCode());
        errorModel.setModuleName("Sending SMS Engine");
        errorModel.setFunctionName(methodName);
        errorModel.setErrorMessage(commonException.getErrorMsg());
        Utility.sendMOMAlarem(errorModel);
    }

}
