/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cc.alarm.managers;

import com.asset.alarmengine.common.controller.Initializer;
import com.asset.alarmengine.common.defines.EngineDBStructs;
import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.service.MainService;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author mahmoud.abdou
 */
public class ShutdownManager {

    private static final String CLASS_NAME = "com.asset.cc.alarm.managers.ShutdownManager";
    private static HashMap propertiesMap = new HashMap();
    private static HashMap dataSourceMap = new HashMap();
    public static String dbUrl;
    public static String username;
    public static String password;

    public static void initialize() {
        String methodName = "initialize";
        try {

          //  ConfigUtil.loadPropertiesFile("AlarmEngineConfig", propertiesMap);
            Initializer.initializePropertiesFiles();
            Initializer.initializeLoggers();
            propertiesMap= EngineDefines.propertiesMap;
           // loadDataSourceMap();

            // Initialize Connection Manager no need for it as we will use c3p0
            //CCConnectionManager.initialize(dataSourceMap);
        } catch (Exception ex) {
            processException(ex, methodName);
        }
    }

    private static void loadDataSourceMap() {
//        dataSourceMap.put(GeneralConstants.DATASOURCE_DRIVER_CLASS, getStringValue(Defines.DB_DRIVERCLASS));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_URL, getStringValue(Defines.DB_URL));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_USERNAME, getStringValue(Defines.DB_USERNAME));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_PASSWORD, getStringValue(Defines.DB_PASSWORD));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_ACQUIRE_INCREMENT, getStringValue(Defines.ACQUIRE_INCREMENT));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_MAX_STATMENTS, getStringValue(Defines.MAX_STATMENTS));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_INITIAL_POOL_SIZE, getStringValue(Defines.INITIAL_POOL_SIZE));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_MIN_POOL_SIZE, getStringValue(Defines.MIN_POOL_SIZE));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_MAX_POOL_SIZE, getStringValue(Defines.MAX_POOL_SIZE));
//        dataSourceMap.put(GeneralConstants.DATASOURCE_MAX_IDLE_TIME, getStringValue(Defines.MAX_IDLE_TIME));
    }

    public static void setEngineShutDownFlag() throws EngineException {
        String methodName = "setEngineShutDownFlag";
        Connection connection = null;
        try {
//            connection = CCConnectionManager.getConnection();
//            LkSystemPropertiesModel model = new LkSystemPropertiesModel();
//            model.setItemKey(Defines.KEY_ENGINE_SHUTDOWN_FLAG);
//            model.setItemValue(GeneralConstants.SHUTDOWN_FLAG_TRUE);
//            LkSystemPropertiesService service = new LkSystemPropertiesService();
//            service.updateLkSystemProperty(connection, model);
             MainService mainService=new MainService();
            mainService.updateSystemProperty(EngineDBStructs.SYSTEM_PROPERTIES.SYSTEM_PROPERTIES_TBL, EngineDBStructs.SYSTEM_PROPERTIES.CONSTANT_VALUE, EngineDBStructs.SYSTEM_PROPERTIES.CONSTANT_KEY, EngineDefines.SHUTDOWN_FLAG_TRUE,EngineDefines.ENGINE_SHUTDOWN_FLAG_CONSTANT_KEY);
  

        } catch (EngineException ex) {
            throw new EngineException(ex,EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                throw new EngineException(ex,EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
            }
        }
    }

    public static void setEngineStartUpFlag() throws EngineException {
        String methodName = "setEngineStartUpFlag";
        Connection connection = null;
        try {
//            connection = CCConnectionManager.getConnection();
//            LkSystemPropertiesModel model = new LkSystemPropertiesModel();
//            model.setItemKey(Defines.KEY_ENGINE_SHUTDOWN_FLAG);
//            model.setItemValue(GeneralConstants.SHUTDOWN_FLAG_FALSE);
//            LkSystemPropertiesService service = new LkSystemPropertiesService();
//            service.updateLkSystemProperty(connection, model);
              //kashif changes
            MainService mainService=new MainService();
            mainService.updateSystemProperty(EngineDBStructs.SYSTEM_PROPERTIES.SYSTEM_PROPERTIES_TBL, EngineDBStructs.SYSTEM_PROPERTIES.CONSTANT_VALUE, EngineDBStructs.SYSTEM_PROPERTIES.CONSTANT_KEY, EngineDefines.SHUTDOWN_FLAG_FALSE,EngineDefines.ENGINE_SHUTDOWN_FLAG_CONSTANT_KEY);
  
            ///////
        } catch (EngineException ex) {
            throw new EngineException(ex, EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                throw new EngineException(ex, EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
            }
        }
    }

    public static String getStringValue(String key) {
        if (propertiesMap != null) {
            return propertiesMap.get(key).toString();
        } else {
            return null;
        }
    }

    public static HashMap getPropertiesMap() {
        return propertiesMap;
    }

    public static HashMap getDataSourceMap() {
        return dataSourceMap;
    }

    public static void setDataSourceMap(HashMap dataSourceMap) {
        ShutdownManager.dataSourceMap = dataSourceMap;
    }
    
    
    
    private static EngineException processException(Exception e, String methodName) {
        if (e instanceof EngineException) {
            return (EngineException) e;
        } // Handle SQL exception
        else if (e instanceof SQLException) {
            return new EngineException(e, EngineErrorCodes.UNKNOWN_ERROR, "SQL error in CLASS:" + CLASS_NAME + " method: " + methodName);
        } else {
            return new EngineException(e,EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
        }
    }
}
