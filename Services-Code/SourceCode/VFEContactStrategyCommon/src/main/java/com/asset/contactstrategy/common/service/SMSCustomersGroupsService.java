/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.SMSCustomersGroupsDAO;
import com.asset.contactstrategy.interfaces.models.CustomerConfigAndGrpModel;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author mostafa.kashif
 */
public class SMSCustomersGroupsService {
    
    public  CustomerConfigAndGrpModel getCustomerSpecialConfigAndGrp(Connection con, CustomerConfigAndGrpModel customerModel, int runId,String logPrefix) throws SQLException, Exception {
       return SMSCustomersGroupsDAO.getCustomerSpecialConfigAndGrp(con, customerModel, runId,logPrefix);
    }

     public  CustomerConfigAndGrpModel getCustomerGrp(Connection con, CustomerConfigAndGrpModel customerModel, int runId,String logPrefix) throws SQLException, Exception {
       return SMSCustomersGroupsDAO.getCustomerGrp(con, customerModel, runId,logPrefix);
    }
}
