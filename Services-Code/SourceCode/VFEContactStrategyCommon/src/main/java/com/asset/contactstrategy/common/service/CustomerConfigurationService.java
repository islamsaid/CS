/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.CustomerConfigurationDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CustomerConfigurationModel;
import java.sql.Connection;

/**
 *
 * @author Ahmed Hisham
 */
public class CustomerConfigurationService {

    public CustomerConfigurationModel checkMSISDNAvailabilityService(String msisdn, Connection connection) throws CommonException {

        CustomerConfigurationModel configurationModel = null;
//        CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [checkMSISDNAvailabilityService]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkMSISDNAvailabilityService Started").build());
        CustomerConfigurationDAO configurationDAO = new CustomerConfigurationDAO();
        configurationModel = configurationDAO.checkMSISDN(msisdn, connection);
//        CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [checkMSISDNAvailabilityService]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkMSISDNAvailabilityService Ended").build());
        return configurationModel;

    }

    public void InsertCustomerData(String msisdn, CustomerConfigurationModel configurationModel, Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [InsertCustomerData]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "InsertCustomerData Started").build());
        CustomerConfigurationDAO configurationDAO = new CustomerConfigurationDAO();
        configurationDAO.insertCustomerConfig(msisdn, configurationModel, connection);
//        CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [InsertCustomerData]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "InsertCustomerData Ended").build());

    }

    public void updateCustomerData(String msisdn, CustomerConfigurationModel configurationModel, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [updateCustomerData]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerData Started").build());
        CustomerConfigurationDAO configurationDAO = new CustomerConfigurationDAO();
        configurationDAO.updateCustomerConfig(msisdn, configurationModel, connection);
//        CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [updateCustomerData]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerData Ended").build());
    }

    public void deleteCustomerData(String msisdn, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [deleteCustomerData]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCustomerData Started").build());
        CustomerConfigurationDAO configurationDAO = new CustomerConfigurationDAO();
        configurationDAO.deleteCustomerConfig(msisdn, connection);
//        CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [deleteCustomerData]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCustomerData Ended").build());

    }
}
