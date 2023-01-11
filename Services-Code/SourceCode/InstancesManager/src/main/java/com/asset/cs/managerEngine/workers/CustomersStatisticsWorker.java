/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.workers;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.managerEngine.constants.EngineDefines;
import com.asset.cs.managerEngine.managers.Manager;
import com.asset.cs.managerEngine.managers.ResourcesCachingManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class CustomersStatisticsWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.managerEngine.workers.CustomersStatisticsWorker";

    private MainService mainServ = new MainService();
    private long sleepTime = 10000;
    private int currentDayColumn;
    private int lastWorkingDay = 0; // last successful working day 
    private boolean exceedsMidnight = false;

    public void run() {
//        CommonLogger.businessLogger.info("--------- Starting Customers Statistics Worker : " + this.getName() + " ----------");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting CustomersStatisticsWorker").build());
        String methodName = "run";
        intializeLastWorkingDay();
        while (!isWorkerShutDownFlag()) {
            try {
                sleepTillMidNight();
//                CommonLogger.businessLogger.info("=============================Before Mid Night by <2 Min will sleep for 20 sec=============================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Before Midnight by <2 Min will Sleep for 20 Sec").build());
                while ((!exceedsMidnight && !midNight()) && !isWorkerShutDownFlag()) {
                    this.sleep(sleepTime);
                }
//                CommonLogger.businessLogger.info("=============================It Exceeds MidNight Starting Handling Statistics=============================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "It Exceeds Midnight Starting Handling Statistics").build());
                handleStatistics();

            } catch (InterruptedException ex) {
                CommonLogger.errorLogger.error(" Interruption " + ex);
                CommonLogger.businessLogger.error("Fatal Error -->" + ex);
                if (!isWorkerShutDownFlag()) {
                    handleServiceException(ex, methodName);
                }
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", e);
                CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + e);
                handleServiceException(e, methodName);
            }
        }

    }

    public void intializeLastWorkingDay() {

        String methodName = "intializeLastWorkingDay";
        try {
            Timestamp current = mainServ.getCurrentDatabaseTime();
//            CommonLogger.businessLogger.info("Current Time From DB=[" + current + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "IntializeLastWorkingDay Current Time From DB")
                    .put(GeneralConstants.StructuredLogKeys.CURRENT_TIME, current).build());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            lastWorkingDay = calendar.get(Calendar.DAY_OF_MONTH);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
            intializeLastWorkingDay();
        }
    }

    public void handleStatistics() {
        String methodName = "handleStatistics";
        long startTime = System.currentTimeMillis();
        CommonLogger.businessLogger.info("Start Handling SMS Statistics");
        try {
            //Log DB Current Time--Start Time
            Timestamp current = mainServ.getCurrentDatabaseTime();
//            CommonLogger.businessLogger.info("Current Time From DB=[" + current + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "HandleStatistics Current Time From DB")
                    .put(GeneralConstants.StructuredLogKeys.CURRENT_TIME, current).build());
            //step 1 -->update Today Column and set Yesterday Counter
            while (!updateTodayColumnAndCounter()) {
                CommonLogger.businessLogger.info("Failed to Update Today Column and set Yesterday Counter ");
            }
//            //step 2 --> update yesterday Column to COUNTER
//            while (!updateYesterdayColumn(GeneralConstants.COUNTER_COLUMN_NAME)) {
//                CommonLogger.businessLogger.info("Failed to Update Yesterday Column to COUNTER ");
//            }
            //step 2 --> Reload Counters
            while (!reloadCounters()) {
                CommonLogger.businessLogger.info("Failed to Reload Counters After Updating Today Column ");
            }

            //step 3 copy yesterday counter to ysterday temp
            Manager.setStatisticsCatchingError(false);
//            CommonLogger.businessLogger.info("===========================================================================");
//            CommonLogger.businessLogger.info("================Start Update Workers For Job=[Copy Columns]======================");
//            CommonLogger.businessLogger.info("===========================================================================");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Update Workers for Job=[Copy Columns]").build());
            Manager.startUpdateWorkers(0);

            //step 3 Just in case if workers failed to Copy Columns by threads then try working on Full Table
            if (Manager.isStatisticsCatchingError()) {
//                CommonLogger.businessLogger.info("===========================================================================");
                CommonLogger.businessLogger.info("Failed to Copy Columns  By Threads");
                while (!mainServ.copyCounterToTempFullTable(DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME, Manager.getYesterdayColumn())) {
                    CommonLogger.businessLogger.info("Failed to Copy Columns Full Table");
                }
                while (!mainServ.copyCounterToTempFullTable(DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME, Manager.getYesterdayColumn())) {
                    CommonLogger.businessLogger.info("Failed to Copy Columns Full Table");
                }
//                CommonLogger.businessLogger.info("===========================================================================");
//                CommonLogger.businessLogger.info("=============================Successfully Copy Columns SMS & ADS Customers Statistics=============================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Copy Columns SMS & ADS Customers Statistics").build());
                Manager.setStatisticsCatchingError(false);
            } else {
//                CommonLogger.businessLogger.info("===========================================================================");
//                CommonLogger.businessLogger.info("=============================Successfully Copy Columns SMS & ADS Customers Statistics=============================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Copy Columns SMS & ADS Customers Statistics").build());
            }
//            CommonLogger.businessLogger.info("===========================================================================");

            //step 4 update yesterday Column to TEMP
            while (!updateYesterdayColumn(GeneralConstants.TEMP_COLUMN_NAME)) {
                CommonLogger.businessLogger.info("Failed to Update Yesterday Column to TEMP ");
            }
            //step 5 Reload Counters
            while (!reloadCounters()) {
                CommonLogger.businessLogger.info("Failed to Reload Counters After Copying Yesterday Data");
            }

            //step 6 Reset Yesterday Counter Column
            Manager.setStatisticsCatchingError(false);
//            CommonLogger.businessLogger.info("===========================================================================");
//            CommonLogger.businessLogger.info("================Start Update Workers For Job=[Reset Counter Column]======================");
//            CommonLogger.businessLogger.info("===========================================================================");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Update Workers for Job=[Reset Counter Column]").build());
            Manager.startUpdateWorkers(1);

            //step 6 Just in case if workers failed to Reset Counter Column by Threads then try on Full Table
            if (Manager.isStatisticsCatchingError()) {
//                CommonLogger.businessLogger.info("===========================================================================");
                CommonLogger.businessLogger.info("Failed to Reset Yesterday Counter Column By Threads");
                while (!mainServ.resetStatisticsCounterColumnTable(DBStruct.VFE_CS_SMS_CUST_STATISTICS.TABLE_NAME, Manager.getYesterdayColumn())) {
                    CommonLogger.businessLogger.info("Failed to Reset Yesterday Counter Column Full Table");
                }
                while (!mainServ.resetStatisticsCounterColumnTable(DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME, Manager.getYesterdayColumn())) {
                    CommonLogger.businessLogger.info("Failed to Reset Yesterday Counter Column Full Table");
                }
//                CommonLogger.businessLogger.info("===========================================================================");
//                CommonLogger.businessLogger.info("=============================Successfully Reset Yesterday Counter Column SMS & ADS Customers Statistics=============================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Reset Yesterday Counter Column SMS & ADS Customers Statistics").build());
                Manager.setStatisticsCatchingError(false);
            } else {
//                CommonLogger.businessLogger.info("===========================================================================");
//                CommonLogger.businessLogger.info("=============================Successfully Reset Yesterday Counter Column SMS & ADS Customers Statistics=============================");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Reset Yesterday Counter Column SMS & ADS Customers Statistics").build());
            }
//            CommonLogger.businessLogger.info("===========================================================================");
            //step 7 update statistics last updated date system property
            while (!updateStatisticsLastUpdateDate()) {
                CommonLogger.businessLogger.info("Failed to Update Statistics Last Update Date system Property ");
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            lastWorkingDay = calendar.get(Calendar.DAY_OF_MONTH);

//            CommonLogger.businessLogger.info("=============================Handled Statistics Successfully in [" + (System.currentTimeMillis() - startTime) + "]msec Last Successful Working Day=[" + lastWorkingDay + "]=============================");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Handled Statistics Successfully")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime))
                    .put(GeneralConstants.StructuredLogKeys.LAST_WORKING_DAY, lastWorkingDay).build());

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }
    }

    public boolean updateStatisticsLastUpdateDate() throws CommonException {
        String methodName = "updateStatisticsLastUpdateDate";
        boolean updateLastUpdateSystemProperty = false;
        try {
            SystemPropertiesModel lkSystemPropertiesModel = new SystemPropertiesModel();
            lkSystemPropertiesModel.setItemKey(EngineDefines.KEY_STATISTICS_LAST_UPDATE);
            lkSystemPropertiesModel.setItemValue("");
            lkSystemPropertiesModel.setGroupId(GeneralConstants.SRC_ID_MANAGER_ENGINE);
            mainServ.updateTimeSystemProperty(lkSystemPropertiesModel);
            CommonLogger.businessLogger.info("updateLastUpdateSystemProperty successfully...");
            updateLastUpdateSystemProperty = true;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }
        return updateLastUpdateSystemProperty;
    }

    public void sleepTillMidNight() throws InterruptedException {
        String methodName = "sleepTillMidNight";
        try {
            Timestamp current = mainServ.getCurrentDatabaseTime();
//            CommonLogger.businessLogger.info("Current Time From DB=[" + current + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SleepTillMidNight Current Time From DB")
                    .put(GeneralConstants.StructuredLogKeys.CURRENT_TIME, current).build());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (currentDay != lastWorkingDay) {
//                CommonLogger.businessLogger.info("Exceeds MidNight of Day=[" + currentDay + "] Last Successful Working Day=[" + lastWorkingDay + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Exceeds Midnight")
                        .put(GeneralConstants.StructuredLogKeys.DAY, currentDay)
                        .put(GeneralConstants.StructuredLogKeys.LAST_WORKING_DAY, lastWorkingDay).build());
                exceedsMidnight = true;
                return;
            } else {
                exceedsMidnight = false;
            }
            Timestamp midNight = mainServ.getMidNightDatabaseTime();
//            CommonLogger.businessLogger.info("Mid Night Time From DB=[" + midNight + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Midnight from DB")
                    .put(GeneralConstants.StructuredLogKeys.MIDNIGHT_TIME, midNight).build());
            long diff = midNight.getTime() - current.getTime();
//            CommonLogger.businessLogger.info("Sleep Interval Till MidNight=[" + diff + "]msec [" + TimeUnit.MILLISECONDS.toMinutes(diff) + "]Minutes");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sleep Interval Till Midnight")
                    .put(GeneralConstants.StructuredLogKeys.MIDNIGHT_TIME, diff)
                    .put(GeneralConstants.StructuredLogKeys.TIME_DIFF, TimeUnit.MILLISECONDS.toMinutes(diff)).build());
            this.sleep(diff);
            // this.sleep(100);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
            sleepTillMidNight();
        }
    }

    public boolean midNight() {
        String methodName = "midNight";
        boolean midnight = false;
        int hour;
        try {
            Timestamp current = mainServ.getCurrentDatabaseTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour == 0) {
                midnight = true;
            }
//            CommonLogger.businessLogger.info("Current Time=[" + current + "]MidNight=[" + midnight + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Current and midnight time")
                    .put(GeneralConstants.StructuredLogKeys.CURRENT_TIME, current)
                    .put(GeneralConstants.StructuredLogKeys.MIDNIGHT_TIME, midnight).build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }

        return midnight;
    }

    public boolean reloadCounters() {
        String methodName = "reloadCounters";
        boolean updateReloadCounters = false;
        try {
            mainServ.CountersReloader();
            CommonLogger.businessLogger.info("Finished Reload Counters");
            updateReloadCounters = true;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }

        return updateReloadCounters;
    }

    public boolean updateTodayColumnAndCounter() {
        String methodName = "updateTodayColumnAndCounter";
        boolean updateTodayColumnAndCounter = false;
        Connection connection = null;
        try {
            Manager.setYesterdayColumn(Integer.parseInt(mainServ.getSystemPropertyByKey(EngineDefines.KEY_TODAY_COLUMN_NUM, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE)));
//            CommonLogger.businessLogger.info("Get Current Today Column =[" + Manager.getYesterdayColumn() + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Get Current Today Column")
                    .put(GeneralConstants.StructuredLogKeys.TODAY_COLUMN, Manager.getYesterdayColumn()).build());
            if (Manager.getYesterdayColumn() >= 60) {
                currentDayColumn = 1;
            } else {
                currentDayColumn = Manager.getYesterdayColumn() + 1;
            }
            connection = DataSourceManger.getConnection();
            SystemPropertiesModel todayColumn = new SystemPropertiesModel();
            todayColumn.setGroupId(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE);
            todayColumn.setItemKey(EngineDefines.KEY_TODAY_COLUMN_NUM);
            todayColumn.setItemValue(String.valueOf(currentDayColumn));
            mainServ.updateSystemProperty(connection, todayColumn);
            SystemPropertiesModel yesterdayColumn = new SystemPropertiesModel();
            yesterdayColumn.setGroupId(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE);
            yesterdayColumn.setItemKey(EngineDefines.KEY_YESTERDAY_COLUMN_NAME);
            yesterdayColumn.setItemValue(GeneralConstants.COUNTER_COLUMN_NAME);
            mainServ.updateSystemProperty(connection, yesterdayColumn);
            connection.commit();
//            CommonLogger.businessLogger.info("Update Current Today Column =[" + currentDayColumn + "] YesterDay Column=[" + GeneralConstants.COUNTER_COLUMN_NAME + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Columns")
                    .put(GeneralConstants.StructuredLogKeys.YESTERDAY_COLUMN, GeneralConstants.COUNTER_COLUMN_NAME).build());
            updateTodayColumnAndCounter = true;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
            try {
                DataSourceManger.rollBack(connection);
            } catch (CommonException e) {
                CommonLogger.errorLogger.error("Failed to [Rollback] [" + methodName + "]" + e);
            }
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Failed to [Close Connection] [" + methodName + "]" + ex);
            }
        }

        return updateTodayColumnAndCounter;
    }

//    public boolean updateTodayColumn() {
//        String methodName = "updateTodayColumn";
//        boolean updateTodayColumn = false;
//        try {
//            Manager.setYesterdayColumn(Integer.parseInt(mainServ.getSystemPropertyByKey(EngineDefines.KEY_TODAY_COLUMN_NUM, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE)));
//            CommonLogger.businessLogger.info("Get Current Today Column =[" + Manager.getYesterdayColumn() + "]");
//            if (Manager.getYesterdayColumn() >= 60) {
//                currentDayColumn = 1;
//            } else {
//                currentDayColumn = Manager.getYesterdayColumn() + 1;
//            }
//            SystemPropertiesModel todayColumn = new SystemPropertiesModel();
//            todayColumn.setGroupId(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE);
//            todayColumn.setItemKey(EngineDefines.KEY_TODAY_COLUMN_NUM);
//            todayColumn.setItemValue(String.valueOf(currentDayColumn));
//            mainServ.updateSystemProperty(todayColumn);
//            CommonLogger.businessLogger.info("Update Current Today Column =[" + currentDayColumn + "]");
//            updateTodayColumn = true;
//        } catch (Exception ex) {
//            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
//            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
//            handleServiceException(ex, methodName);
//        }
//
//        return updateTodayColumn;
//    }
    public boolean updateYesterdayColumn(String yesterday) {
        String methodName = "updateYesterdayColumn";
        boolean updateYesterdayColumn = false;
        try {
            SystemPropertiesModel yesterdayColumn = new SystemPropertiesModel();
            yesterdayColumn.setGroupId(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE);
            yesterdayColumn.setItemKey(EngineDefines.KEY_YESTERDAY_COLUMN_NAME);
            yesterdayColumn.setItemValue(yesterday);
            mainServ.updateSystemProperty(yesterdayColumn);
//            CommonLogger.businessLogger.info("Update YesterDay Column=[" + yesterday + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Yesterday Column")
                    .put(GeneralConstants.StructuredLogKeys.YESTERDAY_COLUMN, yesterday).build());
            updateYesterdayColumn = true;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error  Method=[" + methodName + "]--->", ex);
            CommonLogger.businessLogger.error("Fatal Error Method=[" + methodName + "]--->" + ex);
            handleServiceException(ex, methodName);
        }
        return updateYesterdayColumn;
    }

    private void handleServiceException(Exception e, String methodName) {

        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof InterruptedException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
            campaignException = new CommonException("Interrupted exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        //  errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_MANAGER_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

}
