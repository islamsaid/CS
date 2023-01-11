package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.SMSBridgingInstancesDAO;
import com.asset.contactstrategy.common.dao.SMSBridgingInstancesDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import java.sql.Connection;

/**
 *
 * @author aya.moawed 2595
 */
public class SMSBridgingInstancesService {

    public String getInstance(Connection conn, int instanceID, String key) throws CommonException {
        SMSBridgingInstancesDAO smsBridgingInstancesDAO = new SMSBridgingInstancesDAO();
        return smsBridgingInstancesDAO.getInstance(conn, instanceID, key);
    }

    void updateInstance(Connection conn, int instanceID, String key, String value) throws CommonException {
        SMSBridgingInstancesDAO smsBridgingInstancesDAO = new SMSBridgingInstancesDAO();
        smsBridgingInstancesDAO.updateInstance(conn, instanceID, key, value);
    }

}
