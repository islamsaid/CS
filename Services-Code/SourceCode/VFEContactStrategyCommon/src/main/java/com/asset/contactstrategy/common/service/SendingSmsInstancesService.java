/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.SendingSmsInstancesDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * @author kerollos.asaad
 */
public class SendingSmsInstancesService {

    public String getInstanceReloadCounter(Connection conn, int instanceID) throws CommonException {
        SendingSmsInstancesDAO sendingSmsInstancesDAO = new SendingSmsInstancesDAO();
        return sendingSmsInstancesDAO.getInstaceReloadCounter(conn, instanceID);
    }

    public HashMap<String, String> getInstanceProperties(Connection conn, int instanceID) throws CommonException {
        SendingSmsInstancesDAO sendingSmsInstancesDAO = new SendingSmsInstancesDAO();
        return sendingSmsInstancesDAO.getInstancProperties(conn, instanceID);
    }

    public void updateInstanceShutdownFlag(Connection conn, int instanceID, String flag) throws CommonException {
        SendingSmsInstancesDAO sendingSmsInstancesDAO = new SendingSmsInstancesDAO();
        sendingSmsInstancesDAO.updateInstanceShutdownFlag(conn, instanceID, flag);
    }
    
    public void updateInstanceReloadCounter(Connection conn, int instanceID, String flag) throws CommonException {
        SendingSmsInstancesDAO sendingSmsInstancesDAO = new SendingSmsInstancesDAO();
        sendingSmsInstancesDAO.updateInstanceReloadCounter(conn, instanceID, flag);
    }
    
    public String getInstanceShutdownFlag(Connection conn, int instanceID) throws CommonException {
        SendingSmsInstancesDAO sendingSmsInstancesDAO = new SendingSmsInstancesDAO();
        return sendingSmsInstancesDAO.getInstanceShutdownFlag(conn, instanceID);
    }

}
