package com.asset.cs.sendingsms.service;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.sendingsms.dao.ReceivedSMSLogDAO;
import com.asset.cs.sendingsms.models.ReceivedSMSLogModel;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author islam.said
 */
public class ReceivedSMSLogService {
    
     public static void insertReceivedSMS(ArrayList<ReceivedSMSLogModel> receivedSMSLogs) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ReceivedSMSLogDAO.insertReceivedSMS(connection, receivedSMSLogs);
            DataSourceManger.commitConnection(connection);
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(ce.getMessage());
            throw ce;

        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);

        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }
}
