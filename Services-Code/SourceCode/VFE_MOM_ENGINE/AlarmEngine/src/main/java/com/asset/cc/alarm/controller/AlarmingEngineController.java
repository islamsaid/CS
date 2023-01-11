/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cc.alarm.controller;

import com.asset.alarmengine.common.controller.DBpoolManager;
import com.asset.alarmengine.common.defines.EngineDBStructs;
import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.alarmengine.common.models.HServiceErrorModel;
import com.asset.alarmengine.common.service.MainService;
import com.asset.cc.alarm.constants.Defines;
import com.asset.cc.alarm.managers.ConsumerManager;
import com.asset.cc.alarm.managers.ShutdownManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mahmoud.abdou
 */
public class AlarmingEngineController {

    private static final String CLASS_NAME = "com.asset.cc.alarm.controller.AlarmEngineController";
    private static Connection conn;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String methodName = "main";

        System.out.println("----------- Starting Alarming Engine -------------");

        // LkSystemPropertiesService service = new LkSystemPropertiesService();

        try {
            //     LogUtil.initialize();
            // Initialize Startup Manager
            ShutdownManager.initialize();

            // conn = CCConnectionManager.getConnection();
            //kashif changes
            DBpoolManager dbManager = DBpoolManager.getInstance();
            conn = dbManager.getConnection();
        
            //kashif changes
            boolean load = true;
            String engineShutdownFlag;
            long sleepTime = Long.valueOf(ShutdownManager.getPropertiesMap().get(EngineDefines.CONTROLLER_THREAD_SLEEP_TIME).toString()).longValue();
       //     long connectionSleepTime = Long.valueOf(ShutdownManager.getPropertiesMap().get(EngineDefines.CONNECTION_RETRY_SLEEP_TIME).toString()).longValue();
            while (true) {
                try {

                    //  engineShutdownFlag = service.getSystemPropertyByKey(conn, Defines.KEY_ENGINE_SHUTDOWN_FLAG);
                    //kashif changes
                    MainService mainService = new MainService();
                    engineShutdownFlag = mainService.getSystemProperty(EngineDBStructs.SYSTEM_PROPERTIES.SYSTEM_PROPERTIES_TBL, EngineDBStructs.SYSTEM_PROPERTIES.CONSTANT_VALUE, EngineDBStructs.SYSTEM_PROPERTIES.CONSTANT_KEY, EngineDefines.ENGINE_SHUTDOWN_FLAG_CONSTANT_KEY);
                    //kashif changes
                    //Esmail.Anbar || Fixing Shutdown Loop Issue | 3/10/2017
                    if (engineShutdownFlag == null || engineShutdownFlag.isEmpty() || 
                        (!engineShutdownFlag.equalsIgnoreCase(EngineDefines.SHUTDOWN_FLAG_FALSE) && !engineShutdownFlag.equalsIgnoreCase(EngineDefines.SHUTDOWN_FLAG_TRUE)))
                    {
                        EngineLogger.debugLogger.error("Invalid 'ALARM_ENGINE_SHUTDOWN_FLAG' from database... value returned was '" + engineShutdownFlag + "'");
                        EngineLogger.errorLogger.error("Invalid 'ALARM_ENGINE_SHUTDOWN_FLAG' from database... value returned was '" + engineShutdownFlag + "'");
                        EngineLogger.debugLogger.info("Shutting Down Alarm Engine...");
                        break;
                    }
                    //    if (engineShutdownFlag.equalsIgnoreCase(GeneralConstants.SHUTDOWN_FLAG_FALSE) && load == true) {
                    if (engineShutdownFlag.equalsIgnoreCase(EngineDefines.SHUTDOWN_FLAG_FALSE) && load == true) {
                        ConsumerManager.startConsumerManager();
                        load = false;
                        //       } else if (engineShutdownFlag.equalsIgnoreCase(GeneralConstants.SHUTDOWN_FLAG_TRUE)) {
                    } else if (engineShutdownFlag.equalsIgnoreCase(EngineDefines.SHUTDOWN_FLAG_TRUE)) {
                        ConsumerManager.shutdownConsumerManager();
                        break;
                    }
                    Thread.currentThread().sleep(sleepTime);

                } catch (EngineException ex) {
//                    if (ex.getErrorCode().equalsIgnoreCase(ErrorCodes.ERROR_SQL) && !databaseConnectionAlive(conn)) {
//                        Thread.currentThread().sleep(connectionSleepTime);
//                        CCConnectionManager.initialize(ShutdownManager.getPropertiesMap());
//                        conn = CCConnectionManager.getConnection();
//                    } else {
//                        handleServiceException(ex, methodName);
//                    }
                    //kashif changes
                    handleServiceException(ex, methodName);
                }
            }
        }// catch (ContextualCampaignException ex) {
        catch (EngineException ex) {
            handleServiceException(ex, methodName);
        } catch (InterruptedException ex) {
            handleServiceException(ex, methodName);
        } catch (Exception ex) {
            handleServiceException(ex, methodName);
        }
    }

    public static boolean databaseConnectionAlive(Connection conn) {

        PreparedStatement pstmt = null;
        try {
            String query = "SELECT 1 FROM DUAL";
            pstmt = conn.prepareStatement(query);
            pstmt.executeQuery();
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }

    private static void handleServiceException(Exception e, String methodName) {
        try {
            EngineException engineException = null;
            // Handle ContextualCampaignException 
            if (e instanceof EngineException) {
                engineException = (EngineException) e;
            } // Handle SQL Exception 
            else if (e instanceof SQLException) {
                engineException = new EngineException(e, EngineErrorCodes.SQL_ERROR_IN_DAO, "Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName);
            } // Handle other exception types
            else {
                engineException = new EngineException(e, EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
            }

            HServiceErrorModel errorModel = new HServiceErrorModel();
            //   errorModel.setErrorType(GeneralConstants.ERROR_TYPE_ALARM_ENGINE);
            // errorModel.setErrorSeverity(GeneralConstants.ERROR_SEVERITY_CRITICAL);
            //kashif changes
            errorModel.setErrorType(EngineDefines.ERROR_TYPE_ALARM_ENGINE);
            errorModel.setErrorSeverity(EngineDefines.ERROR_SEVERITY_CRITICAL);
            errorModel.setErrorTime(new Date());
            /*Add Service Error To DB*/
            //    HServiceErrorService errorService = new HServiceErrorService();
            MainService mainService = new MainService();
            mainService.handleServiceError(errorModel, engineException, EngineDBStructs.SERVICE_ERRORS.SERVICE_ERRORS_TBL, EngineDBStructs.SERVICE_ERRORS.ID, EngineDBStructs.SERVICE_ERRORS.ERROR_TYPE, EngineDBStructs.SERVICE_ERRORS.ERROR_SEVERITY,  EngineDBStructs.SERVICE_ERRORS.ERROR_PARAMS, EngineDBStructs.SERVICE_ERRORS.ERROR_MESSAGE, EngineDBStructs.SERVICE_ERRORS.INSERTION_TIME, EngineDBStructs.SERVICE_ERRORS.SERVICE_ERRORS_SEQ);
            // errorService.handleServiceError(errorModel, campaignException);
        } catch (EngineException ex) {
               EngineLogger.debugLogger.error("Exception in handleServiceException--->"+e);
            EngineLogger.errorLogger.error("Exception in handleServiceException--->"+e,e);
        }

    }
}
