/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.CustomerStatisticsDAO;
import com.asset.contactstrategy.common.dao.CustomersMatchingDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import java.sql.Connection;

/**
 *
 * @author Administrator
 */
public class CustomerStatisticsService {

    public void resetStatisticsCounterColumn(Connection connection, String tableName, int columnId, int lastMSISDNTwoDigits) throws CommonException {
        CustomerStatisticsDAO dao = new CustomerStatisticsDAO();
        dao.resetStatisticsCounterColumn(connection, tableName, columnId, lastMSISDNTwoDigits);
    }

    public boolean resetStatisticsCounterColumnTable(Connection connection, String tableName, int columnId) throws CommonException {
        CustomerStatisticsDAO dao = new CustomerStatisticsDAO();
        return dao.resetStatisticsCounterColumnTable(connection, tableName, columnId);
    }

    public void copyCounterToTemp(Connection connection, String tableName, int columnId, int lastMSISDNTwoDigits) throws CommonException {
        CustomerStatisticsDAO dao = new CustomerStatisticsDAO();
        dao.copyCounterToTemp(connection, tableName, columnId, lastMSISDNTwoDigits);
    }

    public boolean copyCounterToTempFullTable(Connection connection, String tableName, int columnId) throws CommonException {
        CustomerStatisticsDAO dao = new CustomerStatisticsDAO();
        return dao.copyCounterToTempFullTable(connection, tableName, columnId);
    }

    
    
    
//    public void updateSMSCustomerStatistics(Connection connection, int columnId, int lastMSISDNTwoDigits) throws CommonException {
//        CustomerStatisticsDAO dao = new CustomerStatisticsDAO();
//        dao.updateSMSCustomerStatistics(connection, columnId, lastMSISDNTwoDigits);
//
//    }
//
//    public void updateADSCustomerStatistics(Connection connection, int columnId, int lastMSISDNTwoDigits) throws CommonException {
//        CustomerStatisticsDAO dao = new CustomerStatisticsDAO();
//        dao.updateADSCustomerStatistics(connection, columnId, lastMSISDNTwoDigits);
//
//    }
//
//    public boolean updateSMSStatisticsTable(Connection connection, int columnId) throws CommonException {
//        CustomerStatisticsDAO dao = new CustomerStatisticsDAO();
//        return dao.updateSMSStatisticsTable(connection, columnId);
//
//    }
//
//    public boolean updateADSStatisticsTable(Connection connection, int columnId) throws CommonException {
//        CustomerStatisticsDAO dao = new CustomerStatisticsDAO();
//        return dao.updateADSStatisticsTable(connection, columnId);
//    }

}
