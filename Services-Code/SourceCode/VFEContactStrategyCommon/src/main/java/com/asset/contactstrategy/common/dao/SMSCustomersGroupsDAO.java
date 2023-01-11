/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.defines.DBStruct.*;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.interfaces.models.CustomerConfigAndGrpModel;
import com.asset.contactstrategy.interfaces.models.CustomerConfigurationsModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mostafa.kashif
 */
public class SMSCustomersGroupsDAO {

    public static CustomerConfigAndGrpModel getCustomerSpecialConfigAndGrp(Connection con, CustomerConfigAndGrpModel customerModel, int runId, String logPrefix) throws CommonException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long startTime = System.currentTimeMillis();
        try {
            CommonLogger.businessLogger.info(logPrefix + "Starting getCustomerSpecialConfigAndGrp");

            String sql = "select  CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.MSISDN + " custmsisdn,CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_ADS + " dailyADS,CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_SMS + " dailySMS,CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.DO_NOT_CONTACT + " donotcontact,CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_ADS + " weeklyads,CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_SMS + " weeklysms,\n"
                    + "CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_ADS + " monthlyads,CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_SMS + " monthlySMS,GRP." + VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID + " groupId from " + VFE_CS_CUSTOMERS_CONFIGURATIONS.TABLE_NAME + " cust \n"
                    + "full outer join " + VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME + " grp\n"
                    + "on GRP." + VFE_CS_CUSTOMERS_GROUPS.MSISDN + "=CUST." + VFE_CS_CUSTOMERS_CONFIGURATIONS.MSISDN + "\n"
                    + "where (GRP." + VFE_CS_CUSTOMERS_GROUPS.MSISDN + "=? and GRP." + VFE_CS_CUSTOMERS_GROUPS.LAST_TWO_DIGITS + "=? and GRP." + VFE_CS_CUSTOMERS_GROUPS.RUN_ID + "=?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, customerModel.getMsisdn());
            stmt.setInt(2, customerModel.getLastTwoDigits());
            stmt.setInt(3, runId);
//            CommonLogger.businessLogger.info(logPrefix + "Sql:" + sql + ",Input 1,4:" + customerModel.getMsisdn() + " 2,5:" + customerModel.getLastTwoDigits() + " 3: " + runId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerSMSStatistics Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql)
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits())
                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, runId).build());
            rs = stmt.executeQuery();
            while (rs.next()) {
                String custMSISDN = rs.getString("CUSTMSISDN");
                if (custMSISDN == null || custMSISDN.trim().isEmpty()) {
//                    CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + " doesn't have specific configurations");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN doesnot have specific configurations")
                            .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                            .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
                    customerModel.setHasCustomConfig(false);
                    Object groupId = rs.getInt("GROUPID");
                    if (rs.wasNull()) {
//                        CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + " doesn't exist in group");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN Doesnot Exist in Group")
                                .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
                        customerModel.setExitsInGroup(false);
                    } else {
                        if (Integer.parseInt(groupId.toString()) != 0) {
//                            CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + "  exist in group:" + groupId.toString());
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN Doesnot Exist in Group")
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits())
                                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupId.toString()).build());
                            customerModel.setGroupId((int) groupId);
                            customerModel.setExitsInGroup(true);
                        }
                    }

                } else {
                    Object groupId = rs.getInt("GROUPID");
                    if (rs.wasNull()) {
//                        CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + " doesn't exist in group");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN Doesnot Exist in Group")
                                .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());

                        customerModel.setExitsInGroup(false);
                    } else {
                        if (Integer.parseInt(groupId.toString()) != 0) {
//                            CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + "  exist in group:" + groupId.toString());
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN Exists in Group")
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits())
                                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupId.toString()).build());
                            customerModel.setGroupId((int) groupId);
                            customerModel.setExitsInGroup(true);
                        }
                    }
                    customerModel.setHasCustomConfig(true);
                    customerModel.setDailyThresholdAds(rs.getInt("DAILYADS"));
                    customerModel.setDailyThresholdSms(rs.getInt("DAILYADS"));
                    customerModel.setDoNotContact(rs.getInt("DONOTCONTACT"));
                    customerModel.setWeeklyThresholdAds(rs.getInt("WEEKLYADS"));
                    customerModel.setWeeklyThresholdSms(rs.getInt("WEEKLYSMS"));
                    customerModel.setMonthlyThresholdAds(rs.getInt("MONTHLYADS"));
                    customerModel.setMonthlyThresholdSms(rs.getInt("MONTHLYSMS"));
//                    CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + "  specific configuration"
//                            + " dailyThresholdSMS:" + customerModel.getDailyThresholdSms() + " weeklyThresholdSMS:" + customerModel.getWeeklyThresholdSms() + " monthlyThresholdSMS:" + customerModel.getMonthlyThresholdSms()
//                            + " doNotContact: " + customerModel.getDoNotContact());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN Doesnot Exist in Group")
                            .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                            .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits())
                            .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, customerModel.getDailyThresholdSms())
                            .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, customerModel.getWeeklyThresholdSms())
                            .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, customerModel.getMonthlyThresholdSms()).build());

                }
            }

            return customerModel;
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught SQLException---->getCustomerSpecialConfigAndGrp-->" + ex);
            CommonLogger.errorLogger.error(logPrefix + "Getting Caught SQLException---->getCustomerSpecialConfigAndGrp-->" + ex, ex);
            // Error code
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught Exception---->getCustomerSpecialConfigAndGrp-->" + ex);
            CommonLogger.errorLogger.error(logPrefix + "Getting Caught Exception---->getCustomerSpecialConfigAndGrp-->" + ex, ex);
            // Error code
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "Getting Caught SQLException---->closing resultset-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "Getting Caught SQLException---->closing resultset-->" + ex, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "Getting Caught SQLException---->closing statement-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "Getting Caught SQLException---->closing statement-->" + ex, ex);
                }
            }
            CommonLogger.businessLogger.info(logPrefix + "Ending getCustomerSpecialConfigAndGrp in " + (System.currentTimeMillis() - startTime) + " msecs");
        }
    }

    public static CustomerConfigAndGrpModel getCustomerGrp(Connection con, CustomerConfigAndGrpModel customerModel, int runId, String logPrefix) throws CommonException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long startTime = System.currentTimeMillis();
        try {
            CommonLogger.businessLogger.info(logPrefix + "Starting getCustomerGrp");

            String sql = "select " + VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID + " from " + VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME + " \n"
                    + "where (" + VFE_CS_CUSTOMERS_GROUPS.MSISDN + "=? and " + VFE_CS_CUSTOMERS_GROUPS.LAST_TWO_DIGITS + "=? and " + VFE_CS_CUSTOMERS_GROUPS.RUN_ID + "=?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, customerModel.getMsisdn());
            stmt.setInt(2, customerModel.getLastTwoDigits());
            stmt.setInt(3, runId);
//            CommonLogger.businessLogger.info(logPrefix + "Sql:" + sql + ",Input 1:" + customerModel.getMsisdn() + " 2:" + customerModel.getLastTwoDigits() + " 3: " + runId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerSMSStatistics Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql)
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits())
                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, runId).build());
            rs = stmt.executeQuery();
            customerModel.setExitsInGroup(false);
            while (rs.next()) {

                Object groupId = rs.getInt(VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID);
                if (rs.wasNull()) {
//                    CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + " doesn't exist in group");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN Doesnot Exists in Group")
                            .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                            .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
                    customerModel.setExitsInGroup(false);
                } else {
                    if (Integer.parseInt(groupId.toString()) != 0) {
//                        CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + "  exist in group:" + groupId.toString());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN Exists in Group")
                                .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits())
                                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupId.toString()).build());
                        customerModel.setGroupId((int) groupId);
                        customerModel.setExitsInGroup(true);
                    }
                }

            }
//            CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + "  exist in group flag:" + customerModel.isExitsInGroup());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer MSISDN Exists in Group")
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits())
                    .put(GeneralConstants.StructuredLogKeys.GROUP_EXIST_FLAG, customerModel.isExitsInGroup()).build());
            return customerModel;
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught SQLException---->getCustomerGrp-->" + ex);
            CommonLogger.errorLogger.error(logPrefix + "Getting Caught SQLException---->getCustomerGrp-->" + ex, ex);
            // Error code
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught Exception---->getCustomerGrp-->" + ex);
            CommonLogger.errorLogger.error(logPrefix + "Getting Caught Exception---->getCustomerGrp-->" + ex, ex);
            // Error code
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "Getting Caught SQLException---->closing resultset-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "Getting Caught SQLException---->closing resultset-->" + ex, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "Getting Caught SQLException---->closing statement-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "Getting Caught SQLException---->closing statement-->" + ex, ex);
                }
            }
//            CommonLogger.businessLogger.info(logPrefix + "Ending getCustomerGrp in " + (System.currentTimeMillis() - startTime) + " msecs");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getCustomerGrp")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        }
    }
}
