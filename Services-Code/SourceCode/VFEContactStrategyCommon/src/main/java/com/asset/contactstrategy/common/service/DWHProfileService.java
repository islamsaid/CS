/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.DWHProfileDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.LineModel;
import com.asset.contactstrategy.interfaces.models.CustomersModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Zain Al-Abedin
 */
public class DWHProfileService {

    public void insertDWHProfileBatch(Connection connection, String SQLStatment, Vector<LineModel> dwListbatch) throws CommonException {
     //  CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "Starting insertDWHProfileBatch");
        try {
            DWHProfileDAO dao = new DWHProfileDAO();
            dao.insertDWHProfileBatch(connection, SQLStatment, dwListbatch);
       //    CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "End insertDWHProfileBatch");
        } catch (Exception e) {
            throw e;
        }
    }

    public void addDWHProfileNewPartation(Connection connection, int partitionId) throws CommonException {
       // CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "Starting addDWHProfileNewPartation");
        try {
            DWHProfileDAO dao = new DWHProfileDAO();
            dao.addDWHProfileNewPartation(connection, partitionId);
         //   CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "End addDWHProfileNewPartation");
        } catch (Exception e) {
            throw e;
        }
    }

    public void dropDWHProfilePartation(Connection connection, int partitionId) throws CommonException {
       //CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "Starting dropDWHProfilePartation");
        try {
            DWHProfileDAO dao = new DWHProfileDAO();
            dao.dropDWHProfilePartation(connection, partitionId);
        //  CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "End dropDWHProfilePartation");
        } catch (Exception e) {
            throw e;
        }
    }

    public CustomersModel getCustomer(Connection connection, String MSISDN) throws CommonException {
//        CommonLogger.debugLogger.info(DWHProfileService.class.getName() + " || " + "Starting dropDWHProfilePartation");
        try 
        {
            DWHProfileDAO dao = new DWHProfileDAO();
//            CustomerService customerService = new CustomerService();
//            CustomersAdsGroupsModel customersAdsGroups = customerService.getCustomerAdsGroup(connection, MSISDN);
//            CustomersSmsGroupsModel customersSmsGroups = customerService.getCustomerSmsGroup(connection, MSISDN);
            CustomersModel customer = dao.getCustomerWithGroups(connection, MSISDN);
//            if (customersAdsGroups!=null) {                
//                customer.setAdsGroupId(customersAdsGroups.getAdsGroupId());
//            }
//            if (customersSmsGroups!=null) {                                
//                customer.setSmsGroupId(customersSmsGroups.getGroupId());
//            }
//            CommonLogger.debugLogger.info(DWHProfileService.class.getName() + " || " + "End dropDWHProfilePartation");
            return customer;
        } 
        catch (Exception e) 
        {
            throw e;
        }
    }
    
    public boolean checkCustomerExist(Connection connection, String MSISDN) throws Exception {
        try {
            DWHProfileDAO dao = new DWHProfileDAO();
//           
            CustomersModel customerModel = dao.getCustomer(connection, MSISDN);
            if (customerModel == null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            //we need to add logs and error code
            throw e;
        }
    }
    
    public HashMap<String, CustomersModel> getCustomersMap (Connection conn, ArrayList<String> MSISDNs) throws CommonException
    {
        return new DWHProfileDAO().getCustomersMap(conn, MSISDNs);
    }
}
