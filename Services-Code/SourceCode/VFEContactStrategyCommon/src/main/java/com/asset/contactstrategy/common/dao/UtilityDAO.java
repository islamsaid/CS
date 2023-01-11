/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MenuModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Esmail.Anbar
 */
public class UtilityDAO {

    public boolean checkDatasource(Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(UtilityDAO.class.getName() + " || " + "Starting [checkDatasource]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkDatasource Started").build());
//        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
//            conn = DataSourceManger.getConnection();
            CommonLogger.businessLogger.info("Got connnection successfully");
            stmt = connection.createStatement();
            CommonLogger.businessLogger.info("Attempting Query: Select SYSDATE from Dual");
            rs = stmt.executeQuery("Select SYSDATE from Dual");

            while (rs.next()) {
                CommonLogger.businessLogger.info("'Select SYSDATE from Dual' returned: " + rs.getString("SYSDATE"));
            }
        } //        catch (CommonException ex) 
        //        {
        //            CommonLogger.businessLogger.error("Exception Occured: " + ex.getErrorMsg());
        //            CommonLogger.errorLogger.error("Exception Occured: " + ex.getErrorMsg(), ex);
        //            return false;
        //        } 
        catch (SQLException ex) {
            CommonLogger.businessLogger.error("Exception Occured: " + ex.getMessage());
            CommonLogger.errorLogger.error("Exception Occured: " + ex.getMessage(), ex);
            throw new CommonException("Exception Occured: " + ex.getMessage(), 0);
//            return false;
        } catch (Exception ex) {
            CommonLogger.businessLogger.error("Exception Occured: " + ex.getMessage());
            CommonLogger.errorLogger.error("Exception Occured: " + ex.getMessage(), ex);
            throw new CommonException("Exception Occured: " + ex.getMessage(), 0);
//            return false;
        } finally {
            DataSourceManger.closeDBResources(rs, stmt);
//            CommonLogger.businessLogger.debug(UtilityDAO.class.getName() + " || " + "Ending [checkDatasource]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkDatasource Ended").build());
        }
        return true;
    }

}
