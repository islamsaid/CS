/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.defines;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 *
 * @author Mostafa Kashif
 */
public class EngineDefines {

    public static final String No_OF_WORKER_THREADS = "No_OF_WORKER_THREADS";
    public static final String WORKER_THREAD_SLEEP_TIME = "WORKER_THREAD_SLEEP_TIME";
    public static final String CONNECTION_RETRY_SLEEP_TIME = "CONNECTION_RETRY_SLEEP_TIME";
    public static final String CONTROLLER_THREAD_SLEEP_TIME = "CONTROLLER_THREAD_SLEEP_TIME";
    public static final String DB_DRIVERCLASS = "DB_DRIVERCLASS";
    public static final String DB_URL = "DB_URL";
    public static final String DB_USERNAME = "DB_USERNAME";
    public static final String DB_PASSWORD = "DB_PASSWORD";
    public static final String ACQUIRE_INCREMENT = "ACQUIRE_INCREMENT";
    public static final String INITIAL_POOL_SIZE = "INITIAL_POOL_SIZE";
    public static final String MAX_POOL_SIZE = "MAX_POOL_SIZE";
    public static final String MIN_POOL_SIZE = "MIN_POOL_SIZE";
    public static final String MAX_STATMENTS = "MAX_STATMENTS";
    public static final String MAX_IDLE_TIME = "MAX_IDLE_TIME";
    public static final String KEY_ENGINE_SHUTDOWN_FLAG = "ALARM_ENGINE_SHUTDOWN_FLAG";
    public static final String KEY_LAST_ALARM = "LAST_ALARM";
    public static final String CHECKOUT_TIMEOUT = "CHECKOUT_TIMEOUT";
    public static final String ALARM_ENGINE_TIMESTAMP_FORMAT = "ALARM_ENGINE_TIMESTAMP_FORMAT";
    public static final String FILE_NODE = "NODE";
    public static final String FILE_LOCATION_OF_NODE = "LOCATION_OF_NODE";
    public static final String FILE_OTHERS = "OTHERS";
    public static final String FILE_ERROR_MESSAGE_MAX_CHARS = "ERROR_MESSAGE_MAX_CHARS";
    public static final String SEVERITIES_COMMA_SEPARATED = "SEVERITIES_COMMA_SEPARATED";
    public static final String FTP_SERVER_IP = "FTP_SERVER_IP";
    public static final String FTP_SERVER_PORT = "FTP_SERVER_PORT";
    public static final String FTP_SERVER_USER = "FTP_SERVER_USER";
    public static final String FTP_SERVER_PASSWORD = "FTP_SERVER_PASSWORD";
    public static final String FTP_SERVER_REMOTE_PATH = "FTP_SERVER_REMOTE_PATH";
    public static final String ERROR_MESSAGES_FILE_NAME = "messages";
    public static final String ENGINE_PROPERTIES_FILE_NAME = "AlarmEngineConfig";
    public static ResourceBundle errorMessagesBundle;
    public static HashMap propertiesMap = new HashMap();
    //Esmail.Anbar || Fixing System Property Reading "ITEM_KEY" instead of "ITEM_ID" | 3/10/2017
    public static final String ENGINE_SHUTDOWN_FLAG_CONSTANT_KEY = "ALARM_ENGINE_SHUTDOWN_FLAG";
    public static final String KEY_LAST_ALARM_CONSTANT_KEY = "ALARM_ENGINE_LAST_RUNTIME";
    public static final String ALARM_ENGINE_TIMESTAMP_FORMAT_CONSTANT_KEY = "ALARM_ENGINE_TIMESTAMP_FORMAT ";
    public static final String SEVERITIES_COMMA_SEPARATED_CONSTANT_KEY = "SEVERITIES_COMMA_SEPARATED";
    public static final String FTP_SERVER_IP_CONSTANT_KEY = "FTP_SERVER_IP";
    public static final String FTP_SERVER_PORT_CONSTANT_KEY = "FTP_SERVER_PORT";
    public static final String FTP_SERVER_USER_CONSTANT_KEY = "FTP_SERVER_USER";
    public static final String FTP_SERVER_PASSWORD_CONSTANT_KEY = "FTP_SERVER_PASSWORD";
    public static final String FTP_SERVER_REMOTE_PATH_CONSTANT_KEY = "FTP_SERVER_REMOTE_PATH";
    
    public static final String SHUTDOWN_FLAG_TRUE = "TRUE";
    public static final String SHUTDOWN_FLAG_FALSE = "FALSE";
    public static final String LOG4J_PATH = "log4j_path";
    /////////////////////// ERRORS_SEVERITY ///////////////////
    public static final int ERROR_SEVERITY_LOW = 1;
    public static final int ERROR_SEVERITY_MEDIUM = 2;
    public static final int ERROR_SEVERITY_HIGH = 3;
    public static final int ERROR_SEVERITY_CRITICAL = 4;
    public static final int ERROR_SEVERITY_SHOW_STOPPER = 5;
    public static final String ERROR_TYPE_ALARM_ENGINE = "813-1";
}
