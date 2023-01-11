/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.SMSCustomerStatsticsDao;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.interfaces.models.CustomerConfigAndGrpModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import java.sql.Connection;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author mostafa.kashif
 */
public class SMSCustomerStatsticsService {

    //Adding Source Id for using with Both Consult & Send SMS Interfaces
    public boolean updateCustomerSMSStatistics(Connection con, ServicesModel service, CustomerConfigAndGrpModel customerModel,
            String yesterdayColumn, int today, int totalDays, int dailyThreshold, int weeklyThreshold,
            int monthlyThreshold, String logPrefix, int srcId, boolean hasMsgPriority, int msgPrioriy, int guardPeriod, long csMsgId) throws CommonException, InterfacesBusinessException {
//        CommonLogger.businessLogger.info(logPrefix + " csMsgId: " + csMsgId + " | Starting updateCustomerSMSStatistics Service");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting updateCustomerSMSStatistics Service").build());
        try {
          
            String todayColumnName = "DAY_" + today + "_COUNTER";
            String yesterdayColumnName = null;
            if (today - 1 <= 0) {
                yesterdayColumnName = "DAY_" + (totalDays) + "_" + yesterdayColumn;
            } else {
                yesterdayColumnName = "DAY_" + (today - 1) + "_" + yesterdayColumn;
            }

            String columnName = "DAY_$_TEMP";
            int j = 0;
            String weeklyStatement = todayColumnName + "+" + yesterdayColumnName;
            String monthlyStatement = todayColumnName + "+" + yesterdayColumnName;
            String calculationStmt = todayColumnName + "+" + yesterdayColumnName;
            int end = 0;
            for (int i = today - 2;; i--) {
                j++;
                if (i <= 0) {
                    i = totalDays;
                }
                calculationStmt = calculationStmt + "+" + columnName.replace("$", String.valueOf(i));
                if (j == 5) {
                    weeklyStatement = calculationStmt;
                    // break;
                }
                if (j == 28) {
                    monthlyStatement = calculationStmt;
                    end = i;
                    break;
                }
                //   weeklyStatement=weeklyStatement+columnName.replace("$", String.valueOf(i));
            }
            weeklyStatement = weeklyStatement.replace("+", ",");
            monthlyStatement = monthlyStatement.replace("+", ",");
//            CommonLogger.businessLogger.info(logPrefix + " csMsgId: " + csMsgId + " | TodayColumn: " + todayColumnName + ",WeeklyStatement:" + weeklyStatement + " ,monthlyStatement:" + monthlyStatement);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting updateCustomerSMSStatistics Service")
                    .put(GeneralConstants.StructuredLogKeys.TODAY_COLUMN, todayColumnName)
                    .put(GeneralConstants.StructuredLogKeys.WEEKLY_STATEMENT, weeklyStatement)
                    .put(GeneralConstants.StructuredLogKeys.MONTHLY_STATEMENT, monthlyStatement).build());
            return SMSCustomerStatsticsDao.updateCustomerSMSStatistics(con, service, customerModel, monthlyStatement, todayColumnName, today, end, dailyThreshold, weeklyThreshold, monthlyThreshold, guardPeriod, logPrefix, srcId, hasMsgPriority, msgPrioriy, csMsgId);
        } catch (InterfacesBusinessException ibe) {
            throw ibe;
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught Exception---->updateCustomerSMSStatistics Service-->" + ex);

            CommonLogger.errorLogger.error(logPrefix + "Getting Caught Exception---->updateCustomerSMSStatistics Service-->" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);

        } 
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerSMSStats(Connection conn, String logPrefix, String msisdn, int day) throws CommonException {
//        CommonLogger.businessLogger.info(logPrefix + "Starting decrementCustomerSMSStats Service");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting decrementCustomerSMSStats Service").build());

        try {
            new SMSCustomerStatsticsDao().decrementCustomerSMSStats(conn, logPrefix, msisdn, day);
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught Exception---->decrementCustomerSMSStats Service-->" + ex);

            CommonLogger.errorLogger.error(logPrefix + "Getting Caught Exception---->decrementCustomerSMSStats Service-->" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);

        }
    }
}
