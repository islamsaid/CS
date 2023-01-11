/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.InterfacesInstancesDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import java.sql.Connection;

/**
 *
 * @author esmail.anbar
 */
public class InterfacesInstancesService {
    
    public int getInstaceReloadCounter(Connection conn, int instanceID) throws CommonException 
    {
        InterfacesInstancesDAO dao = new InterfacesInstancesDAO();
        return dao.getInstaceReloadCounter(conn, instanceID);
    }
    
    public void updateInstanceReloadCounter(Connection conn, int instanceID, int reloadCounter) throws CommonException 
    {
        InterfacesInstancesDAO dao = new InterfacesInstancesDAO();
        dao.updateInstanceReloadCounter(conn, instanceID, reloadCounter);
    }
}
