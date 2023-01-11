/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cc.alarm.services;

import com.asset.alarmengine.common.controller.DBpoolManager;
import com.asset.alarmengine.common.defines.EngineDBStructs.SYSTEM_PROPERTIES;
import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.alarmengine.common.service.MainService;
import com.asset.cc.alarm.constants.Defines;
import com.asset.cc.alarm.managers.ShutdownManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mahmoud.abdou
 */
public class ConsumerWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cc.alarm.services.ConsumerWorker";
    private HashMap<String, String> dbProperties;
    private Connection connection = null;
    private boolean result;
    private static int dbAlarmSeq;

    @Override
    public void run() {
        String methodName = "run";
        EngineLogger.debugLogger.info("--------- Starting WorkerThread : " + this.getName() + " ----------");
        try {

            long sleepTime = Long.valueOf(ShutdownManager.getPropertiesMap().get(EngineDefines.WORKER_THREAD_SLEEP_TIME).toString()).longValue();
       //     long connectionSleepTime = Long.valueOf(ShutdownManager.getPropertiesMap().get(EngineDefines.CONNECTION_RETRY_SLEEP_TIME).toString()).longValue();

            BusinessHandler handler = null;

        //    connection = CCConnectionManager.getConnection();
            //kashif changes 
//            DBpoolManager dbManager = DBpoolManager.getInstance();
//            connection = dbManager.getConnection();
            //kashif changes
       //     LkSystemPropertiesService lkSystemPropertiesService = new LkSystemPropertiesService();
            MainService mainService =new MainService();
            
            //dbProperties = ConfigurationDAO.loadEngineConfig(connection);
            dbProperties=mainService.loadAllProperties(SYSTEM_PROPERTIES.SYSTEM_PROPERTIES_TBL,SYSTEM_PROPERTIES.CONSTANT_VALUE,SYSTEM_PROPERTIES.CONSTANT_KEY,SYSTEM_PROPERTIES.GROUP_ID_COLUMN,SYSTEM_PROPERTIES.GROUP_ID);
            handler = new BusinessHandler(connection, dbProperties);

            while (!isWorkerShutDownFlag()) {
                try {

                    
             //       String lastAlarm = lkSystemPropertiesService.getSystemPropertyByKey(connection, Defines.KEY_LAST_ALARM);
                    //KASHIF CHANGES
                    String lastAlarm = mainService.getSystemProperty(SYSTEM_PROPERTIES.SYSTEM_PROPERTIES_TBL,SYSTEM_PROPERTIES.CONSTANT_VALUE,SYSTEM_PROPERTIES.CONSTANT_KEY, EngineDefines.KEY_LAST_ALARM_CONSTANT_KEY);
                    //
                    
                    if (lastAlarm != null && !lastAlarm.trim().isEmpty()) {
               //         dbProperties.put(Defines.KEY_LAST_ALARM, lastAlarm);
                        //kashif changes
                        dbProperties.put(String.valueOf(EngineDefines.KEY_LAST_ALARM_CONSTANT_KEY), lastAlarm);
                        result = handler.WriteAlarmFiles();
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");
                        lastAlarm = sdf.format(new Date(System.currentTimeMillis()));
                    //    dbProperties.put(Defines.KEY_LAST_ALARM, lastAlarm);
                         //kashif changes
                        dbProperties.put(String.valueOf(EngineDefines.KEY_LAST_ALARM_CONSTANT_KEY), lastAlarm);
                       
                        handler.setDbProperties(dbProperties);
                        result = handler.WriteAlarmFiles();
                    }
                    if (result) {
                         EngineLogger.debugLogger.info("---------- Updating last alarm date-------------");
//                        LkSystemPropertiesModel model = new LkSystemPropertiesModel();
//                        model.setItemKey(Defines.KEY_LAST_ALARM);
//                        model.setItemKey(Defines.KEY_LAST_ALARM);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");
                      //  model.setItemValue(sdf.format(new Date(System.currentTimeMillis())));
                  //      lkSystemPropertiesService.updateLkSystemProperty(connection, model);
                        //kashif changes
                        mainService.updateSystemProperty(SYSTEM_PROPERTIES.SYSTEM_PROPERTIES_TBL, SYSTEM_PROPERTIES.CONSTANT_VALUE, SYSTEM_PROPERTIES.CONSTANT_KEY, sdf.format(new Date(System.currentTimeMillis())),  EngineDefines.KEY_LAST_ALARM_CONSTANT_KEY);
                         EngineLogger.debugLogger.info("---------- Updated last alarm date-------------");
                    }
                     EngineLogger.debugLogger.info("---------- Engine sleeping for " + getTimeInMinutes(sleepTime) + " minutes-------------");
                    this.sleep(sleepTime);

                } catch (EngineException ex) {
//                    if (ex.getErrorCode().equalsIgnoreCase(ErrorCodes.ERROR_SQL) && !databaseConnectionAlive(connection)) {
//                         EngineLogger.debugLogger.info("-------Database connection is closed--------------");
//                        HServiceErrorModel model = new HServiceErrorModel();
//                        model.setErrorCode(ErrorCodes.CLOSE_SQL_CONNECTION);
//                        model.setErrorMessage("Database connection closed");
//                        model.setErrorSeverity(GeneralConstants.ERROR_SEVERITY_CRITICAL);
//                        model.setErrorTypeString("ERROR_ALARM_ENGINE");
//                        handler.writeRecordToFile(model, "ALARM_DB_CONNECTION_" + getNextDbAlarmSeqVal());
//                        this.sleep(connectionSleepTime);
//                        CCConnectionManager.initialize(ShutdownManager.getPropertiesMap());
//                        connection = CCConnectionManager.getConnection();
//                    } else {
//                        handleServiceException(ex, methodName, CLASS_NAME);
//                    }
                    //kashif changes
                    handleServiceException(ex, methodName, CLASS_NAME);
                }
            }

             EngineLogger.debugLogger.info("--------- Shutting down WorkerThread : " + this.getName() + " ----------");

        } catch (Exception e) {
            handleServiceException(e, methodName, CLASS_NAME);
        }
    }

    public boolean databaseConnectionAlive(Connection conn) {

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

    public double getTimeInMinutes(long timeInMilliseconds) {
        double minutes = timeInMilliseconds / 1000; // Time in seconds
        minutes = minutes / 60; // Time in minutes;
        return minutes;
    }

    public int getNextDbAlarmSeqVal() {
        dbAlarmSeq++;
        return dbAlarmSeq;
    }
}
