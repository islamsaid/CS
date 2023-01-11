/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.interfaces.models.CustomerConfigAndGrpModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mostafa.kashif
 */
public class SMSCustomerStatsticsDao {

    //Adding Source Id for Usage with Both Consult & Send SMS Interfaces
    public static boolean updateCustomerSMSStatistics(Connection con, ServicesModel service,
            CustomerConfigAndGrpModel customerModel, String monthlyString, String todayColumn,
            int start, int end, int dailyThreshold, int weeklyThreshold, int monthlyThreshold,
            int guardPeriod, String logPrefix, int srcId, boolean hasMsgPriority, int msgPriority, long csMsgId) throws CommonException, InterfacesBusinessException {
//        CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | Starting updateCustomerSMSStatistics dao");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerSMSStatistics Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean sendMsg = false;
        try {
            String sql = "Select " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + "," + monthlyString + "," + todayColumn + " todayColumn,(SELECT SYSDATE FROM DUAL) SQLSYSDATE," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_START_DATE + "," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_END_DATE + " FROM " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + "=? AND " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + "=? FOR UPDATE";
            String sqlUpdateCounters = "Update " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME + " set " + todayColumn + "=" + todayColumn + "+1  WHERE " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + "=? AND " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + "=?";
            String sqlUpdateCountersAndGuardPeriod = "Update " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME + " set " + todayColumn + "=" + todayColumn + "+1," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_START_DATE + "= SYSDATE," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_END_DATE + "=SYSDATE+" + guardPeriod + "  WHERE " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + "=? AND " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + "=?";
            String sqlInsertCounters = "Insert into " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME + "(" + DBStruct.VFE_CS_SMS_CUST_STATISTICS.ID + "," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + "," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + "," + todayColumn + ") VALUES (VFE_CS_SMS_CUST_STAT_SEQ.nextval,?,?,?)";
            String sqlInsertCountersAndGuard = "Insert into " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME + "(" + DBStruct.VFE_CS_SMS_CUST_STATISTICS.ID + "," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + "," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + "," + todayColumn + "," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_START_DATE + "," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_END_DATE + ") VALUES (" + DBStruct.VFE_CS_SMS_CUST_STATISTICS.VFE_CS_SMS_CUST_STATISTICS_SEQ + ".nextval,?,?,?,SYSDATE,SYSDATE+" + guardPeriod + ")";
            String sqlUpdateGuardPeriod = "Update " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME + " set " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_START_DATE + "= SYSDATE," + DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_END_DATE + "=SYSDATE+" + guardPeriod + "  WHERE " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + "=? AND " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + "=?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, customerModel.getMsisdn());
            stmt.setInt(2, customerModel.getLastTwoDigits());
            stmt.setQueryTimeout(Defines.INTERFACES.QUERY_TIMEOUT_VALUE);
//            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | Sql: " + sql + " inputs 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerSMSStatistics Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql)
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
            rs = stmt.executeQuery();
            int j = 1;
            int Sum = 0;
            int weeklySum = 0;
            int monthlySum = 0;
            boolean customerExists = false;
            //CommonLogger.businessLogger.info(logPrefix + "Started Calculation Loop");
            while (rs.next()) {
                customerExists = true;
                for (int i = start;; i--) {
                    j++;
                    Sum = Sum + rs.getInt(j);
                    if (j == 8) {
                        weeklySum = Sum;
                        // break;
                    }
                    if (j == 31) {
                        monthlySum = Sum;
                        break;
                    }
                }
                int todayCounter = rs.getInt("TODAYCOLUMN");
                Timestamp sqlDate = rs.getTimestamp("SQLSYSDATE");
                Timestamp guardStart = rs.getTimestamp(DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_START_DATE);
                Timestamp guardEnd = rs.getTimestamp(DBStruct.VFE_CS_SMS_CUST_STATISTICS.GUARD_END_DATE);
                boolean customerInGurad = false;
                if (guardStart != null && guardEnd != null) {
                    customerInGurad = (sqlDate.compareTo(guardStart) > 0) && (sqlDate.compareTo(guardEnd) < 0);
                }
//                CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | MSISDN:" + customerModel.getMsisdn() + ",todayCounter:" + todayCounter + ",weeklySum:" + weeklySum + ",monthlySum:" + monthlySum + ","
//                        + "SQLDate:" + sqlDate.toString() + ",guardStart:" + guardStart + ",guardEnd:" + guardEnd + ",customerInGurad:" + customerInGurad);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer's Info")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                        .put(GeneralConstants.StructuredLogKeys.TODAY_COUNTER, todayColumn)
                        .put(GeneralConstants.StructuredLogKeys.WEEKLY_SUM, weeklySum)
                        .put(GeneralConstants.StructuredLogKeys.MONTHLY_SUM, monthlySum)
                        .put(GeneralConstants.StructuredLogKeys.QUERY_DATE, sqlDate.toString())
                        .put(GeneralConstants.StructuredLogKeys.GUARD_START, guardStart)
                        .put(GeneralConstants.StructuredLogKeys.GUARD_END, guardEnd)
                        .put(GeneralConstants.StructuredLogKeys.CUSTOMER_IN_GUARD, customerInGurad).build());
                //if (service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) {

                if ((srcId == GeneralConstants.SRC_ID_CONSULT_INTERFACE && service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH)
                        || (srcId == GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE && ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH)
                        || (!hasMsgPriority && service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH)))) {
                    if (srcId == GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE || service.getConsultCounter() == Defines.INT_TRUE) {
                        if (monthlyThreshold <= monthlySum) {
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + " and monthlyThreshold >= monthlySum message can't be sent");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, monthlyThreshold >= monthlySum message can't be sent")
                                    .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());

                            InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.MONTHLY_THRESHOLD_REACHED_FOR_CUSTOMER, "");
                            CommonLogger.businessLogger.info(logPrefix + ibe.getDetailedMessage());
                            throw ibe;

//                            return sendMsg = false;
                        } else if (todayCounter >= dailyThreshold) {
                            customerModel.setViolationFlag(true);
                            stmt = con.prepareStatement(sqlUpdateCountersAndGuardPeriod);
                            stmt.setString(1, customerModel.getMsisdn());
                            stmt.setInt(2, customerModel.getLastTwoDigits());
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | SQL:" + sqlUpdateCountersAndGuardPeriod + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerSMSStatistics Query")
                                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlUpdateCountersAndGuardPeriod)
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
                            if (stmt.executeUpdate() > 0) {
//                                CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and todayCounter >= dailyThreshold message can be sent and updating customer today counter and shifting customer guard period by " + guardPeriod + " days");
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, todayCounter >= dailyThreshold message can be sent and updating customer today counter and shifting customer guard period")
                                        .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                        .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                        .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false"))
                                        .put(GeneralConstants.StructuredLogKeys.GUARD_PERIOD, guardPeriod).build());
                                sendMsg = true;
                                return sendMsg;
                            }
                        } else {

                            stmt = con.prepareStatement(sqlUpdateCounters);
                            stmt.setString(1, customerModel.getMsisdn());
                            stmt.setInt(2, customerModel.getLastTwoDigits());
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | SQL:" + sqlUpdateCounters + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerSMSStatistics Query")
                                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlUpdateCountersAndGuardPeriod)
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
                            if (stmt.executeUpdate() > 0) {
//                                CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ", consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and todayCounter < dailyThreshold message can be sent and updating customer today counter ");
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, and todayCounter < dailyThreshold message can be sent and updating customer today counter")
                                        .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                        .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                        .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());
                                sendMsg = true;
                                return sendMsg;
                            }
                        }

                    } else if (customerInGurad) {
                        stmt = con.prepareStatement(sqlUpdateGuardPeriod);
                        stmt.setString(1, customerModel.getMsisdn());
                        stmt.setInt(2, customerModel.getLastTwoDigits());
//                        CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | SQL:" + sqlUpdateCountersAndGuardPeriod + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerSMSStatistics Query")
                                .put(GeneralConstants.StructuredLogKeys.QUERY, sqlUpdateCountersAndGuardPeriod)
                                .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
                        if (stmt.executeUpdate() > 0) {
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + ", message can be sent and shifting customer guard period by " + guardPeriod + " days");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, message can be sent and shifting customer guard period")
                                    .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false"))
                                    .put(GeneralConstants.StructuredLogKeys.GUARD_PERIOD, guardPeriod).build());
                            sendMsg = true;
                            return sendMsg;
                        }
                    } else {
//                        CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + ", message can be sent  and customer not in guard period ");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, message can be sent  and customer not in guard period")
                                .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());
                        sendMsg = true;
                        return sendMsg;

                    }
                } else if ((srcId == GeneralConstants.SRC_ID_CONSULT_INTERFACE && service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_NORMAL)
                        || (srcId == GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE && ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_NORMAL)
                        || (!hasMsgPriority && service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_NORMAL)))) {
                    if (customerInGurad) {
//                        CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",customer in guard period  message can't be sent");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, customer in guard period  message can't be sent")
                                .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());

                        InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.CUSTOMER_IN_GUARD_PERIOD, "");
                        CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
                        throw ibe;

//                        sendMsg = false;
                    } else if (srcId == GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE || service.getConsultCounter() == Defines.INT_TRUE) {
                        if (todayCounter >= dailyThreshold) {
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and todayCounter >= dailyThreshold message can't be sent ");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, and todayCounter >= dailyThreshold message can't be sent")
                                    .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());

                            InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.DAILY_THRESHOLD_REACHED_FOR_CUSTOMER, "");
                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
                            throw ibe;

//                            sendMsg = false;
                        } else if (weeklySum >= weeklyThreshold) {
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and weeklySum >= weeklyThreshold message can't be sent ");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, and weeklySum >= weeklyThreshold message can't be sent")
                                    .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());

                            InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.WEEKLY_THRESHOLD_REACHED_FOR_CUSTOMER, "");
                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
                            throw ibe;

//                                sendMsg = false;
                        } else if (monthlySum >= monthlyThreshold) {
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and monthlySum >= monthlyThreshold message can't be sent ");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, and monthlySum >= monthlyThreshold message can't be sent ")
                                    .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                    .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());

                            InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.MONTHLY_THRESHOLD_REACHED_FOR_CUSTOMER, "");
                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
                            throw ibe;

//                                    sendMsg = false;
                        } else {
                            if (todayCounter + 1 == dailyThreshold) {
                                //update counters and guard period
                                stmt = con.prepareStatement(sqlUpdateCountersAndGuardPeriod);
                                stmt.setString(1, customerModel.getMsisdn());
                                stmt.setInt(2, customerModel.getLastTwoDigits());
//                                CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | SQL:" + sqlUpdateCountersAndGuardPeriod + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCountersAndGuardPeriod Query")
                                        .put(GeneralConstants.StructuredLogKeys.QUERY, sqlUpdateCountersAndGuardPeriod)
                                        .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                        .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
                                if (stmt.executeUpdate() > 0) {
//                                    CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and todayCounter + 1 == dailyThreshold message cant be sent,customer will be in guard period for" + guardPeriod + " days");
                                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, and todayCounter + 1 == dailyThreshold message cant be sent,customer will be in guard period ")
                                            .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                            .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                            .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false"))
                                            .put(GeneralConstants.StructuredLogKeys.GUARD_PERIOD, guardPeriod).build());

                                    sendMsg = true;
                                    return sendMsg;
                                }
                            }
                            //update counters 
                            // message send true
                            stmt = con.prepareStatement(sqlUpdateCounters);
                            stmt.setString(1, customerModel.getMsisdn());
                            stmt.setInt(2, customerModel.getLastTwoDigits());
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | SQL:" + sqlUpdateCounters + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateConter Query")
                                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlUpdateCountersAndGuardPeriod)
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
                            if (stmt.executeUpdate() > 0) {
//                                CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and todayCounter < dailyThreshold message cant be sent");
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, and todayCounter < dailyThreshold message cant be sent")
                                        .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                        .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                        .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());

                                sendMsg = true;
                                return sendMsg;
                            }
                        }
                    } else {
//                        CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and customer not in guard period message can be sent");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Stats, and customer not in guard period message can be sent")
                                .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal"))
                                .put(GeneralConstants.StructuredLogKeys.PRIORITY, ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal"))
                                .put(GeneralConstants.StructuredLogKeys.CONSULT_COUNTER, ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false")).build());
                        sendMsg = true;
                        return sendMsg;
                    }
                }
            }
            if (!customerExists) {
//                synchronized(Defines.smsCustomerStatsSyncObject)
//                {
                //  stmt.clearParameters(); // eslam.ahmed | 5-5-2020
                if (rs != null) { // eslam.ahmed | 5-5-2020
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        CommonLogger.businessLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->Closing Resultset-->" + ex);
                        CommonLogger.errorLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->Closing Resultset-->" + ex, ex);
                    }
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        CommonLogger.businessLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->Closing Statement-->" + ex);
                        CommonLogger.errorLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->Closing Statement-->" + ex, ex);
                    }
                }

                stmt = con.prepareStatement("Select Count(" + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + ") FROM " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN + "=? AND " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X + "=?");
                stmt.setString(1, customerModel.getMsisdn());
                stmt.setInt(2, customerModel.getLastTwoDigits());
                rs = stmt.executeQuery();

                while (rs.next()) {
                    //System.err.println(" " + rs.getInt(1));
                    if (rs.getInt(1) > 0) {
                        return updateCustomerSMSStatistics(con, service, customerModel, monthlyString, todayColumn,
                                start, end, dailyThreshold, weeklyThreshold, monthlyThreshold, guardPeriod, logPrefix,
                                srcId, hasMsgPriority, msgPriority, csMsgId);
                    } else {
                        try {
                            insertCustomerSMSStatsRecord(logPrefix, sqlInsertCounters, customerModel, csMsgId);
                        } catch (SQLException e) {
                            if (!e.getMessage().toLowerCase().contains("unique constraint")) {
                                CommonLogger.businessLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->updateCustomerSMSStatistics Dao-->" + e);
                                CommonLogger.errorLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->updateCustomerSMSStatistics Dao-->" + e, e);
                                throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
                            }
//                            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | Customer Already Exists... Retrying Update... " + e);
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Already Exists, Retrying Update").build(), e);
                        }
                        return updateCustomerSMSStatistics(con, service, customerModel, monthlyString, todayColumn,
                                start, end, dailyThreshold, weeklyThreshold, monthlyThreshold, guardPeriod, logPrefix,
                                srcId, hasMsgPriority, msgPriority, csMsgId);
                    }
                }

//                    CommonLogger.businessLogger.info(logPrefix + "MSISDN: " + customerModel.getMsisdn() + " doesn't exist in " + DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME);
//
//                    if ((srcId == GeneralConstants.SRC_ID_CONSULT_INTERFACE && service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH)
//                            || (srcId == GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE && ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH)
//                            || (!hasMsgPriority && service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH)))) {
//                        if (srcId == GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE || service.getConsultCounter() == Defines.INT_TRUE) {
//                            if (monthlyThreshold == 0) {
//                                CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + " and monthlyThreshold ==0 message can't be sent");
//
//                                InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.MONTHLY_THRESHOLD_REACHED_FOR_CUSTOMER, "");
//                                CommonLogger.businessLogger.info(logPrefix + ibe.getDetailedMessage());
//                                throw ibe;
//
//    //                            return sendMsg = false;
//                            } else if (dailyThreshold == 0) {
//                                customerModel.setViolationFlag(true);
//                                stmt = con.prepareStatement(sqlInsertCountersAndGuard);
//                                stmt.setString(1, customerModel.getMsisdn());
//                                stmt.setInt(2, customerModel.getLastTwoDigits());
//                                stmt.setInt(3, 1);
//                                CommonLogger.businessLogger.info(logPrefix + "SQL:" + sqlInsertCountersAndGuard + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
//                                if (stmt.executeUpdate() > 0) {
//                                    CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and dailyThreshold == 0 message can be sent and adding customer today counter and shifting customer guard period by " + guardPeriod + " days");
//                                    sendMsg = true;
//                                    return sendMsg;
//                                }
//                            } else {
//                                stmt = con.prepareStatement(sqlInsertCounters);
//                                stmt.setString(1, customerModel.getMsisdn());
//                                stmt.setInt(2, customerModel.getLastTwoDigits());
//                                stmt.setInt(3, 1);
//                                CommonLogger.businessLogger.info(logPrefix + "SQL:" + sqlInsertCounters + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
//                                if (stmt.executeUpdate() > 0) {
//                                    CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and dailyThreshold != 0 message can be sent ");
//                                    sendMsg = true;
//                                    return sendMsg;
//                                }
//                            }
//                        } else {
//                            CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + ", message can be sent");
//                            sendMsg = true;
//                            return sendMsg;
//                        }
//                    } else if ((srcId == GeneralConstants.SRC_ID_CONSULT_INTERFACE && service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_NORMAL)
//                            || (srcId == GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE && ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_NORMAL)
//                            || (!hasMsgPriority && service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_NORMAL)))) {
//                        if (srcId == GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE || service.getConsultCounter() == Defines.INT_TRUE) {
//                            if (dailyThreshold == 0) {
//                                CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and dailyThreshold==0 message can't be sent ");
//
//                                InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.DAILY_THRESHOLD_REACHED_FOR_CUSTOMER, "");
//                                CommonLogger.businessLogger.info(logPrefix + ibe.getDetailedMessage());
//                                throw ibe;
//
//    //                            sendMsg = false;
//                            } else if (weeklyThreshold == 0) {
//                                CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and weeklyThreshold==0 message can't be sent ");
//
//                                InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.WEEKLY_THRESHOLD_REACHED_FOR_CUSTOMER, "");
//                                CommonLogger.businessLogger.info(logPrefix + ibe.getDetailedMessage());
//                                throw ibe;
//
//    //                                sendMsg = false;
//                            } else if (monthlyThreshold == 0) {
//                                CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and monthlyThreshold == 0 message can't be sent ");
//
//                                InterfacesBusinessException ibe = new InterfacesBusinessException(srcId, ErrorCodes.SEND_SMS.MONTHLY_THRESHOLD_REACHED_FOR_CUSTOMER, "");
//                                CommonLogger.businessLogger.info(logPrefix + ibe.getDetailedMessage());
//                                throw ibe;
//
//    //                                    sendMsg = false;
//                            } else if (1 == dailyThreshold) {
//                                //update counters and guard period
//                                stmt = con.prepareStatement(sqlInsertCountersAndGuard);
//                                stmt.setString(1, customerModel.getMsisdn());
//                                stmt.setInt(2, customerModel.getLastTwoDigits());
//                                stmt.setInt(3, 1);
//                                CommonLogger.businessLogger.info(logPrefix + "SQL:" + sqlInsertCountersAndGuard + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
//                                if (stmt.executeUpdate() > 0) {
//                                    CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and todayCounter + 1 == dailyThreshold message can be sent,customer will be in guard period for" + guardPeriod + " days");
//                                    sendMsg = true;
//                                    return sendMsg;
//                                }
//                            } else {
//                                stmt = con.prepareStatement(sqlInsertCounters);
//                                stmt.setString(1, customerModel.getMsisdn());
//                                stmt.setInt(2, customerModel.getLastTwoDigits());
//                                stmt.setInt(3, 1);
//                                CommonLogger.businessLogger.info(logPrefix + "SQL:" + sqlInsertCounters + ",INPUTS : 1:" + customerModel.getMsisdn() + ",2:" + customerModel.getLastTwoDigits());
//                                if (stmt.executeUpdate() > 0) {
//                                    CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "   message cant be sent");
//
//                                    sendMsg = true;
//                                    return sendMsg;
//                                }
//                            }
//                        } else {
//                            CommonLogger.businessLogger.info(logPrefix + "service is " + ((service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) ? "high" : "normal") + ",MessagePriority is " + ((hasMsgPriority && msgPriority == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) ? "high" : "normal") + ",consult counter check " + ((service.getConsultCounter() == Defines.INT_TRUE) ? "true" : "false") + "  and customer not in guard period message can be sent");
//                            sendMsg = true;
//                            return sendMsg;
//                        }
//                    }
//                }
            }
            return sendMsg;
        } catch (InterfacesBusinessException ibe) {
            throw ibe;
        } catch (SQLTimeoutException e) {
            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | Query Timed Out... User Is Locked For Another Action || " + e.getMessage());
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, ErrorCodes.SEND_SMS.OPERATION_TIMED_OUT_USER_IS_LOCKED, e.getMessage());
            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
            throw ibe;
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->updateCustomerSMSStatistics Dao-->" + ex);
            CommonLogger.errorLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->updateCustomerSMSStatistics Dao-->" + ex, ex);
            //We need to change error code
            throw new CommonException(ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught Exception---->updateCustomerSMSStatistics Dao-->" + ex);
            CommonLogger.errorLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught Exception---->updateCustomerSMSStatistics Dao-->" + ex, ex);
            //We need to change error code
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->Closing Resultset-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->Closing Resultset-->" + ex, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "csMsgId: " + csMsgId + " | Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
//            CommonLogger.businessLogger.info(logPrefix + "csMsgId: " + csMsgId + " | updateCustomerSMSStatistics dao ended in " + (System.currentTimeMillis() - startTime) + " msecs");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerSMSStatistics DAO Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        }
    }

    private static void insertCustomerSMSStatsRecord(String logPrefix, String sqlInsertCounters, CustomerConfigAndGrpModel customerModel, long csMsgId) throws SQLException, CommonException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DataSourceManger.getConnection();
            stmt = conn.prepareStatement(sqlInsertCounters);
            stmt.setString(1, customerModel.getMsisdn());
            stmt.setInt(2, customerModel.getLastTwoDigits());
            stmt.setInt(3, 0);

            stmt.executeUpdate();
            conn.commit();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + " csMsgId: " + csMsgId + " | Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + " csMsgId: " + csMsgId + " | Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + " csMsgId: " + csMsgId + " | Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + " csMsgId: " + csMsgId + " | Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerSMSStats(Connection conn, String logPrefix, String msisdn, int day) throws SQLException, CommonException {
        PreparedStatement stmt = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("Update ")
                    .append(DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME);

            if (Integer.valueOf(SystemLookups.SYSTEM_PROPERTIES.get("TODAY_COLUMN_NUM")) > day) {
                sql.append(" Set DAY_" + day + "_" + SystemLookups.SYSTEM_PROPERTIES.get("YESTERDAY_COLUMN_NAME") + " = DAY_" + day + "_" + SystemLookups.SYSTEM_PROPERTIES.get("YESTERDAY_COLUMN_NAME") + " - 1 WHERE ");
            } else {
                sql.append(" Set DAY_" + day + "_COUNTER = DAY_" + day + "_COUNTER - 1 WHERE ");
            }

            sql.append(DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN)
                    .append(" = ? And ")
                    .append(DBStruct.VFE_CS_SMS_CUST_STATISTICS.MSISDN_MOD_X)
                    .append(" = ?");

            stmt = conn.prepareStatement(sql.toString());
//            stmt.setInt(1, day);
//            stmt.setInt(2, day);
            stmt.setString(1, msisdn);
            stmt.setInt(2, Integer.valueOf(msisdn.substring(msisdn.length() - 2)));

            stmt.executeUpdate();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + e);
            CommonLogger.errorLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + e, e);
            throw new CommonException(e.getMessage(), "");
        } catch (Exception e) {
            CommonLogger.businessLogger.error(logPrefix + "Caught Exception---->Closing Statement-->" + e);
            CommonLogger.errorLogger.error(logPrefix + "Caught Exception---->Closing Statement-->" + e, e);
            throw new CommonException(e.getMessage(), "");
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
        }
    }
}
