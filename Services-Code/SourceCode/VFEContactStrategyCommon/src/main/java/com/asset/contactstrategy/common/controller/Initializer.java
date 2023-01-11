/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.controller;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.SmsModel;
import com.asset.contactstrategy.interfaces.threads.EngineShutdownThread;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

/**
 *
 * @author mostafa.kashif
 */
public class Initializer {

    public static ResourceBundle propertiesFileBundle = null;
    public static String errorFunctionName = "";
    public static ArrayBlockingQueue<SmsModel> smsQueue;

    /*public static void readPropertiesFile(HashMap propertiesMap) throws Exception {
        try {
            System.out.println("Starting reading messages properties");
            Defines.messagesBundle = ResourceBundle.getBundle(Defines.MESSAGES_FILE_NAME);
        } catch (Exception ex) {
            System.out.println("Exception in errormessagebundle---->" + ex);
            System.err.println("Exception in errormessagebundle---->" + ex);
            errorFunctionName = "readPropertiesFile";
            throw new Exception(ex);
        }

        try {
            System.out.println("Starting reading  jsftemplate properties");
            ResourceBundle resourceBundle = ResourceBundle.getBundle(Defines.PROPERTIES_FILE_NAME);

            Enumeration<String> keys = resourceBundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = resourceBundle.getString(key).trim();
                propertiesMap.put(key, value);
            }

            //Defines.LOG4J_DEBUG_LOGGER_NAME = resourceBundle.getString(Defines.LOG4J_DEBUG_LOGGER_PROPERTY_NAME);
            //Defines.LOG4J_INFO_LOGGER_NAME = resourceBundle.getString(Defines.LOG4J_INFO_LOGGER_PROPERTY_NAME);
            //Defines.LOG4J_ERROR_LOGGER_NAME = resourceBundle.getString(Defines.LOG4J_ERROR_LOGGER_PROPERTY_NAME);
            //Log4j configuration file
            Defines.LOG4J_FILE_PATH = resourceBundle.getString(Defines.LOG4J_FILE_PATH_PROPERTY_NAME);
            //weblogic prperties
            Defines.WEBLOGIC_CONTEXT_FACTORY = resourceBundle.getString(Defines.WEBLOGIC_CONTEXT_FACTORY_PROPERTY_NAME);
            Defines.WEBLOGIC_PROVIDER_URL = resourceBundle.getString(Defines.WEBLOGIC_PROVIDER_URL_PROPERTY_NAME);
            Defines.DATA_SOURCE_NAME = resourceBundle.getString(Defines.DATA_SOURCE_NAME_PROPERTY_NAME);

            //c3p0 connection pool properities
            if (resourceBundle.containsKey(Defines.DB_DRIVER_CLASS_PROPERTY_NAME)) {
                Defines.DB_DRIVER_CLASS = resourceBundle.getString(Defines.DB_DRIVER_CLASS_PROPERTY_NAME);
                Defines.DB_URL = resourceBundle.getString(Defines.DB_URL_PROPERTY_NAME);
                Defines.DB_USERNAME = resourceBundle.getString(Defines.DB_USERNAME_PROPERTY_NAME);
                Defines.DB_PASSWORD = resourceBundle.getString(Defines.DB_PASSWORD_PROPERTY_NAME);
                //Defines.DB_CONNECTION_POOL_MAX_SIZE = resourceBundle.getString(Defines.DB_CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME);
            }
            
            try {
                Defines.ENCRYPTION_KEY = resourceBundle.getString(Defines.ENCRYPTION_KEY_PROPERTY);
              } catch (Exception e) {
                if (Defines.runningProjectId == GeneralConstants.SRC_ID_SENDING_SMS_ENGINE|| Defines.runningProjectId == GeneralConstants.SRC_ID_WEB_INTERFACE||Defines.runningProjectId == GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE) {
                    throw e;
                }
            }
            
            try {
                 Defines.SMS_BULK_UPLOAD_BATCH_SIZE = Long.parseLong(resourceBundle.getString(Defines.SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY));
            } catch (Exception e) {
                if (Defines.runningProjectId == GeneralConstants.SRC_ID_WEB_INTERFACE) {
                    throw e;
                }
            }
            
          

        } catch (Exception e) {
            System.out.println("Exception in reading properties file---->" + e);
            System.err.println("Exception in reading properties file---->" + e);
            errorFunctionName = "readPropertiesFile";
            throw e;
        }
    }*/
    public static void readPropertiesFile(HashMap propertiesMap) throws Exception {

        try {
            readPropertiesFile();
            try {
                convertResourceBundleToMap(propertiesMap, ResourceBundle.getBundle(Defines.PROPERTIES_FILE_NAME));
            } catch (Exception e) {
                convertEnvVarToMap(propertiesMap);
            }

        } catch (Exception e) {
            System.out.println("Exception in reading properties file---->" + e);
            System.err.println("Exception in reading properties file---->" + e);
            errorFunctionName = "readPropertiesFile";
            throw e;
        }
    }

    /*
    public static void readPropertiesFile() throws Exception {
        try {
            System.out.println("Starting reading messages properties");
            Defines.messagesBundle = ResourceBundle.getBundle(Defines.MESSAGES_FILE_NAME);
        } catch (Exception ex) {
            System.out.println("Exception in errormessagebundle---->" + ex);
            System.err.println("Exception in errormessagebundle---->" + ex);
            errorFunctionName = "readPropertiesFile";
            throw new Exception(ex);
        }
        try {
            System.out.println("Starting reading  jsftemplate properties");
            propertiesFileBundle = ResourceBundle.getBundle(Defines.PROPERTIES_FILE_NAME);
            //Loggers names
            Defines.LOG4J_DEBUG_LOGGER_NAME = propertiesFileBundle.getString(Defines.LOG4J_DEBUG_LOGGER_PROPERTY_NAME);
            Defines.LOG4J_INFO_LOGGER_NAME = propertiesFileBundle.getString(Defines.LOG4J_INFO_LOGGER_PROPERTY_NAME);
            Defines.LOG4J_ERROR_LOGGER_NAME = propertiesFileBundle.getString(Defines.LOG4J_ERROR_LOGGER_PROPERTY_NAME);
            //Log4j configuration file
            Defines.LOG4J_FILE_PATH = propertiesFileBundle.getString(Defines.LOG4J_FILE_PATH_PROPERTY_NAME);
            //weblogic prperties
            Defines.WEBLOGIC_CONTEXT_FACTORY = propertiesFileBundle.getString(Defines.WEBLOGIC_CONTEXT_FACTORY_PROPERTY_NAME);
            Defines.WEBLOGIC_PROVIDER_URL = propertiesFileBundle.getString(Defines.WEBLOGIC_PROVIDER_URL_PROPERTY_NAME);
            Defines.DATA_SOURCE_NAME = propertiesFileBundle.getString(Defines.DATA_SOURCE_NAME_PROPERTY_NAME);

            //c3p0 connection pool properities
            if (propertiesFileBundle.containsKey(Defines.DB_DRIVER_CLASS_PROPERTY_NAME)) {
                Defines.DB_DRIVER_CLASS = propertiesFileBundle.getString(Defines.DB_DRIVER_CLASS_PROPERTY_NAME);
                Defines.DB_URL = propertiesFileBundle.getString(Defines.DB_URL_PROPERTY_NAME);
                Defines.DB_USERNAME = propertiesFileBundle.getString(Defines.DB_USERNAME_PROPERTY_NAME);
                Defines.DB_PASSWORD = propertiesFileBundle.getString(Defines.DB_PASSWORD_PROPERTY_NAME);
                //Defines.DB_CONNECTION_POOL_MAX_SIZE = propertiesFileBundle.getString(Defines.DB_CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME);
            }
            
            try
            {
                //Update queue Query HashMap
                Defines.QUEUE_QUERY.QUERIES =  new HashMap<>();
                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY, propertiesFileBundle.getString(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY));
                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY, propertiesFileBundle.getString(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY));
                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_START_QUERY, propertiesFileBundle.getString(Defines.QUEUE_QUERY.QUEUE_START_QUERY));
                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY, propertiesFileBundle.getString(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY));
            }
            catch (Exception e) 
            {
                System.out.println("Exception in reading properties file---->" + e);
                System.err.println("Exception in reading properties file---->" + e);
                errorFunctionName = "readPropertiesFile";
            }
            
            try{
                Defines.DWH_CUSTOMERS_COLUMNS_STRINGS=Integer.parseInt(propertiesFileBundle.getString(Defines.DWH_CUSTOMERS_COLUMNS_STRINGS_PROPERTY_NAME));
                Defines.DWH_CUSTOMERS_COLUMNS_DATES=Integer.parseInt(propertiesFileBundle.getString(Defines.DWH_CUSTOMERS_COLUMNS_DATES_PROPERTY_NAME));
                Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS=Integer.parseInt(propertiesFileBundle.getString(Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS_PROPERTY_NAME));
            }catch (Exception e) 
            {
                System.out.println("Exception in reading properties file---->" + e);
                System.err.println("Exception in reading properties file---->" + e);
                errorFunctionName = "readPropertiesFile";
            }
            try {
                Defines.ENCRYPTION_KEY = propertiesFileBundle.getString(Defines.ENCRYPTION_KEY_PROPERTY);
                Defines.SMS_BULK_UPLOAD_BATCH_SIZE = Long.parseLong(propertiesFileBundle.getString(Defines.SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY));
            } catch (Exception e) {
                if (Defines.runningProjectId == GeneralConstants.SRC_ID_SENDING_SMS_ENGINE
                        || Defines.runningProjectId == GeneralConstants.SRC_ID_WEB_INTERFACE) {
                    throw e;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in reading properties file---->" + e);
            System.err.println("Exception in reading properties file---->" + e);
            errorFunctionName = "readPropertiesFile";
            throw e;
        }
    }*/
    //Reading Propertis File Environment Variables ---CR Cloud--
    public static void readPropertiesFile() throws Exception {
        try {
            System.out.println("Starting reading messages properties");
            Defines.messagesBundle = ResourceBundle.getBundle(Defines.MESSAGES_FILE_NAME);
        } catch (Exception ex) {
            System.out.println("Exception in errormessagebundle---->" + ex);
            System.err.println("Exception in errormessagebundle---->" + ex);
            errorFunctionName = "readPropertiesFile";
            throw new Exception(ex);
        }
        try {
            System.out.println("Starting reading environment variables");
            HashMap<String, String> propertiesHashMap = new HashMap<String, String>();
            try {
                convertResourceBundleToMap(propertiesHashMap, ResourceBundle.getBundle(Defines.PROPERTIES_FILE_NAME));
            } catch (Exception e) {
                convertEnvVarToMap(propertiesHashMap);
            }

            Defines.LOG4J_FILE_PATH = checkForNull(propertiesHashMap.get(Defines.LOG4J_FILE_PATH_PROPERTY_NAME), Defines.LOG4J_FILE_PATH_PROPERTY_NAME);
            
            //c3p0 connection pool properities
            if (propertiesHashMap.get(Defines.C3P0_DRIVER_CLASS_PROPERTY_NAME) != null) {
                System.out.println("Found C3P0_DRIVER_CLASS with value: " + propertiesHashMap.get(Defines.C3P0_DRIVER_CLASS_PROPERTY_NAME));
                System.out.println("Running on C3P0");
                readC3P0Configurations(propertiesHashMap);
            } else {
                System.out.println("Empty C3P0_DRIVER_CLASS");
                System.out.println("Running on DataSource");
                //weblogic prperties
                Defines.WEBLOGIC_CONTEXT_FACTORY = checkForNull(propertiesHashMap.get(Defines.WEBLOGIC_CONTEXT_FACTORY_PROPERTY_NAME), Defines.WEBLOGIC_CONTEXT_FACTORY_PROPERTY_NAME);
                Defines.WEBLOGIC_PROVIDER_URL = checkForNull(propertiesHashMap.get(Defines.WEBLOGIC_PROVIDER_URL_PROPERTY_NAME), Defines.WEBLOGIC_PROVIDER_URL_PROPERTY_NAME);
                Defines.DATA_SOURCE_NAME = checkForNull(propertiesHashMap.get(Defines.DATA_SOURCE_NAME_PROPERTY_NAME), Defines.DATA_SOURCE_NAME_PROPERTY_NAME);

            }

            if (Defines.runningProjectId == GeneralConstants.SRC_ID_DELIVERY_AGGREGATION) {
                Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_PER_SYSTEMPROPERTIES_UPDATE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_PER_SYSTEMPROPERTIES_UPDATE), Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_PER_SYSTEMPROPERTIES_UPDATE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_AFTER_JOBS_UPDATE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_AFTER_JOBS_UPDATE), Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_AFTER_JOBS_UPDATE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_IF_NO_JOB_FOUND_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_IF_NO_JOB_FOUND), Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_IF_NO_JOB_FOUND));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_QUEUE_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_QUEUE_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_FETCH_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_FETCH_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.SELECTOR_POOL_FETCH_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_QUEUE_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.DECIDER_POOL_QUEUE_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_QUEUE_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_QUEUE_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_BATCH_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_BATCH_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.UPDATER_POOL_BATCH_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.AUTOMATICALLY_DROP_CONCAT_PARTITIONS_VALUE = Boolean.parseBoolean(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.AUTOMATICALLY_DROP_CONCAT_PARTITIONS), Defines.DELIVERY_AGGREGATION_PROPERTIES.AUTOMATICALLY_DROP_CONCAT_PARTITIONS));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_QUEUE_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_QUEUE_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_SIZE), Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_SIZE));
                Defines.DELIVERY_AGGREGATION_PROPERTIES.PARALLEL_HINT_COUNT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.PARALLEL_HINT_COUNT), Defines.DELIVERY_AGGREGATION_PROPERTIES.PARALLEL_HINT_COUNT));
            }

            if (Defines.runningProjectId == GeneralConstants.SRC_ID_SMSC_INTERFACE) {
                Defines.SMSC_INTERFACE_PROPERTIES.SYSTEM_TYPE_VALIDATION_FLAG_VALUE = checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.SYSTEM_TYPE_VALIDATION_FLAG), Defines.SMSC_INTERFACE_PROPERTIES.SYSTEM_TYPE_VALIDATION_FLAG);
                Defines.SMSC_INTERFACE_PROPERTIES.AllOW_INSECURE_ACCESS_FLAG_VALUE = checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.AllOW_INSECURE_ACCESS_FLAG), Defines.SMSC_INTERFACE_PROPERTIES.AllOW_INSECURE_ACCESS_FLAG);
                Defines.SMSC_INTERFACE_PROPERTIES.APPENDING_BIND_OPTIONAL_PARAM_FLAG_VALUE = checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.APPENDING_BIND_OPTIONAL_PARAM_FLAG), Defines.SMSC_INTERFACE_PROPERTIES.APPENDING_BIND_OPTIONAL_PARAM_FLAG);
                Defines.SMSC_INTERFACE_PROPERTIES.SERVER_PORT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.SERVER_PORT), Defines.SMSC_INTERFACE_PROPERTIES.SERVER_PORT));
                Defines.SMSC_INTERFACE_PROPERTIES.NUMBER_HANDLERS_PER_SESSION_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.NUMBER_HANDLERS_PER_SESSION), Defines.SMSC_INTERFACE_PROPERTIES.NUMBER_HANDLERS_PER_SESSION));
                Defines.SMSC_INTERFACE_PROPERTIES.SAFELY_UNBIND_TIMEOUT_VALUE
                        = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.SAFELY_UNBIND_TIMEOUT), Defines.SMSC_INTERFACE_PROPERTIES.SAFELY_UNBIND_TIMEOUT));
                Defines.SMSC_INTERFACE_PROPERTIES.REQUESTS_QUEUE_SESSION_PER_SESSION_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.REQUESTS_QUEUE_SESSION_PER_SESSION), Defines.SMSC_INTERFACE_PROPERTIES.REQUESTS_QUEUE_SESSION_PER_SESSION));
                Defines.SMSC_INTERFACE_PROPERTIES.RESPONSES_QUEUE_SESSION_PER_SESSION_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.RESPONSES_QUEUE_SESSION_PER_SESSION), Defines.SMSC_INTERFACE_PROPERTIES.RESPONSES_QUEUE_SESSION_PER_SESSION));

                Defines.SMSC_INTERFACE_PROPERTIES.ARCHIVER_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.ARCHIVER_QUEUE_SIZE), Defines.SMSC_INTERFACE_PROPERTIES.ARCHIVER_QUEUE_SIZE));
                Defines.SMSC_INTERFACE_PROPERTIES.LOG_BATCH_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.LOG_BATCH_SIZE), Defines.SMSC_INTERFACE_PROPERTIES.LOG_BATCH_SIZE));
                Defines.SMSC_INTERFACE_PROPERTIES.NO_OF_ARCHIVER_THREADS_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.NO_OF_ARCHIVER_THREADS), Defines.SMSC_INTERFACE_PROPERTIES.NO_OF_ARCHIVER_THREADS));

                Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_ARCHIVER_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_ARCHIVER_QUEUE_SIZE), Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_ARCHIVER_QUEUE_SIZE));
                Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_LOG_BATCH_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_LOG_BATCH_SIZE), Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_LOG_BATCH_SIZE));
                Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_NO_OF_ARCHIVER_THREADS_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_NO_OF_ARCHIVER_THREADS), Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_NO_OF_ARCHIVER_THREADS));
                Defines.SMSC_INTERFACE_PROPERTIES.WINDOW_SIZE_PER_SESSION_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.WINDOW_SIZE_PER_SESSION), Defines.SMSC_INTERFACE_PROPERTIES.WINDOW_SIZE_PER_SESSION));
                Defines.SMSC_INTERFACE_PROPERTIES.MAX_SESSIONS_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.MAX_SESSIONS), Defines.SMSC_INTERFACE_PROPERTIES.MAX_SESSIONS));
                Defines.SMSC_INTERFACE_PROPERTIES.RABBITMQ_CONSUMERS_NUMBER_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.RABBITMQ_CONSUMERS_NUMBER), Defines.SMSC_INTERFACE_PROPERTIES.RABBITMQ_CONSUMERS_NUMBER));
                Defines.SMSC_INTERFACE_PROPERTIES.DEQUEUED_DELIVER_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.SMSC_INTERFACE_PROPERTIES.DEQUEUED_DELIVER_QUEUE_SIZE), Defines.SMSC_INTERFACE_PROPERTIES.DEQUEUED_DELIVER_QUEUE_SIZE));
            }

            if (Defines.runningProjectId == GeneralConstants.SRC_ID_WEB_INTERFACE) {
                //Update queue Query HashMap
                //Esmail.Anbar | 13/7/2017 | Adding Query Creation Sqls to Database
//                Defines.QUEUE_QUERY.QUERIES = new HashMap<>();
//                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY, checkForNull(propertiesHashMap.get(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY), Defines.QUEUE_QUERY.QUEUE_TBL_QUERY));
//                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY, checkForNull(propertiesHashMap.get(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY), Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY));
//                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_START_QUERY, checkForNull(propertiesHashMap.get(Defines.QUEUE_QUERY.QUEUE_START_QUERY), Defines.QUEUE_QUERY.QUEUE_START_QUERY));
//                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY, checkForNull(propertiesHashMap.get(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY), Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY));
                Defines.DWH_CUSTOMERS_COLUMNS_STRINGS = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DWH_CUSTOMERS_COLUMNS_STRINGS_PROPERTY_NAME), Defines.DWH_CUSTOMERS_COLUMNS_STRINGS_PROPERTY_NAME));
                Defines.DWH_CUSTOMERS_COLUMNS_DATES = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DWH_CUSTOMERS_COLUMNS_DATES_PROPERTY_NAME), Defines.DWH_CUSTOMERS_COLUMNS_DATES_PROPERTY_NAME));
                Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS_PROPERTY_NAME), Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS_PROPERTY_NAME));

                Defines.LDAPLOOKUP = new HashMap<>();
                Defines.LDAPLOOKUP.put(Defines.LDAP_SERVER_DN_PROPERTY_NAME, checkForNull(propertiesHashMap.get(Defines.LDAP_SERVER_DN_PROPERTY_NAME), Defines.LDAP_SERVER_DN_PROPERTY_NAME));
                Defines.LDAPLOOKUP.put(Defines.LDAP_SERVER_URL_PROPERTY_NAME, checkForNull(propertiesHashMap.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME), Defines.LDAP_SERVER_URL_PROPERTY_NAME));
                Defines.LDAPLOOKUP.put(Defines.LDAP_USERS_SB_PROPERTY_NAME, checkForNull(propertiesHashMap.get(Defines.LDAP_USERS_SB_PROPERTY_NAME), Defines.LDAP_USERS_SB_PROPERTY_NAME));
                Defines.LDAPLOOKUP.put(Defines.LDAP_AUTHENTICATION_PROPERTY_NAME, checkForNull(propertiesHashMap.get(Defines.LDAP_AUTHENTICATION_PROPERTY_NAME), Defines.LDAP_AUTHENTICATION_PROPERTY_NAME));
//            }
//            if ( Defines.runningProjectId == GeneralConstants.SRC_ID_WEB_INTERFACE) {
                Defines.ENCRYPTION_KEY = checkForNull(propertiesHashMap.get(Defines.ENCRYPTION_KEY_PROPERTY), Defines.ENCRYPTION_KEY_PROPERTY);
                Defines.SMS_BULK_UPLOAD_BATCH_SIZE = Long.parseLong(checkForNull(propertiesHashMap.get(Defines.SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY), Defines.SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY));
            }

            if (Defines.runningProjectId == GeneralConstants.SRC_ID_DEQUEUER_WEB_SERVICE || Defines.runningProjectId == GeneralConstants.SRC_ID_SENDING_SMS_ENGINE) {
                Defines.ENCRYPTION_KEY = checkForNull(propertiesHashMap.get(Defines.ENCRYPTION_KEY_PROPERTY), Defines.ENCRYPTION_KEY_PROPERTY);
            }

            if (Defines.runningProjectId == GeneralConstants.SRC_ID_INTEGERATION_INTERFACES || Defines.runningProjectId == GeneralConstants.SRC_ID_WEBSERIVCE_REST) {
                Defines.ENCRYPTION_KEY = checkForNull(propertiesHashMap.get(Defines.ENCRYPTION_KEY_PROPERTY), Defines.ENCRYPTION_KEY_PROPERTY);
                Defines.INTERFACES.QUERY_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.QUERY_TIMEOUT), Defines.INTERFACES.QUERY_TIMEOUT));
                Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME), Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME));

                Defines.INTERFACES.resultIdentifier_VALUE = checkForNull(propertiesHashMap.get(Defines.INTERFACES.resultIdentifier), Defines.INTERFACES.resultIdentifier);
                Defines.INTERFACES.resultSeparator_VALUE = checkForNull(propertiesHashMap.get(Defines.INTERFACES.resultSeparator), Defines.INTERFACES.resultSeparator);

                Defines.INTERFACES.MAX_SMS_SEND_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_SMS_SEND_QUEUE_SIZE), Defines.INTERFACES.MAX_SMS_SEND_QUEUE_SIZE));
                Defines.INTERFACES.MAX_QUEUE_THREAD_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_QUEUE_THREAD_POOL_SIZE), Defines.INTERFACES.MAX_QUEUE_THREAD_POOL_SIZE));
                Defines.INTERFACES.MAX_ENQUEUE_DB_ARRAY_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_ENQUEUE_DB_ARRAY_SIZE), Defines.INTERFACES.MAX_ENQUEUE_DB_ARRAY_SIZE));
                Defines.INTERFACES.MAX_NUM_OF_RETRIES_QTHREAD_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_NUM_OF_RETRIES_QTHREAD), Defines.INTERFACES.MAX_NUM_OF_RETRIES_QTHREAD));
                Defines.INTERFACES.ENQUEUE_THREAD_PULL_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.ENQUEUE_THREAD_PULL_TIMEOUT), Defines.INTERFACES.ENQUEUE_THREAD_PULL_TIMEOUT));

                Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE), Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE));
                Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE), Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE));
                Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE), Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE));
                Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD), Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD));
                Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT), Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT));

                Defines.INTERFACES.MAX_ARCHIVING_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_ARCHIVING_QUEUE_SIZE), Defines.INTERFACES.MAX_ARCHIVING_QUEUE_SIZE));
                Defines.INTERFACES.MAX_ARCHIVING_THREAD_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_ARCHIVING_THREAD_POOL_SIZE), Defines.INTERFACES.MAX_ARCHIVING_THREAD_POOL_SIZE));
                Defines.INTERFACES.MAX_ARCHIVING_DB_ARRAY_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_ARCHIVING_DB_ARRAY_SIZE), Defines.INTERFACES.MAX_ARCHIVING_DB_ARRAY_SIZE));
                Defines.INTERFACES.MAX_NUM_OF_RETRIES_ATHREAD_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_NUM_OF_RETRIES_ATHREAD), Defines.INTERFACES.MAX_NUM_OF_RETRIES_ATHREAD));
                Defines.INTERFACES.ARCHIVING_THREAD_PULL_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.ARCHIVING_THREAD_PULL_TIMEOUT), Defines.INTERFACES.ARCHIVING_THREAD_PULL_TIMEOUT));

                Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_SIZE), Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_SIZE));
                Defines.INTERFACES.MAX_SMS_VALIDATION_THREAD_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_SMS_VALIDATION_THREAD_POOL_SIZE), Defines.INTERFACES.MAX_SMS_VALIDATION_THREAD_POOL_SIZE));
                Defines.INTERFACES.VALIDATION_THREAD_PULL_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.VALIDATION_THREAD_PULL_TIMEOUT), Defines.INTERFACES.VALIDATION_THREAD_PULL_TIMEOUT));

                Defines.INTERFACES.MAX_ROLLBACK_SMS_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_ROLLBACK_SMS_QUEUE_SIZE), Defines.INTERFACES.MAX_ROLLBACK_SMS_QUEUE_SIZE));
                Defines.INTERFACES.MAX_ROLLBACK_SMS_THREAD_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_ROLLBACK_SMS_THREAD_POOL_SIZE), Defines.INTERFACES.MAX_ROLLBACK_SMS_THREAD_POOL_SIZE));
                Defines.INTERFACES.ROLLBACK_THREAD_PULL_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.ROLLBACK_THREAD_PULL_TIMEOUT), Defines.INTERFACES.ROLLBACK_THREAD_PULL_TIMEOUT));

                Defines.INTERFACES.SINGLE_ENQUEUE_FLAG_VALUE = checkForNull(propertiesHashMap.get(Defines.INTERFACES.SINGLE_ENQUEUE_FLAG), Defines.INTERFACES.SINGLE_ENQUEUE_FLAG);
            }
            if (Defines.runningProjectId == GeneralConstants.SRC_ID_INTERFACE_REPORTS) {
                Defines.INTERFACES.resultIdentifier_VALUE = checkForNull(propertiesHashMap.get(Defines.INTERFACES.resultIdentifier), Defines.INTERFACES.resultIdentifier);
                Defines.INTERFACES.resultSeparator_VALUE = checkForNull(propertiesHashMap.get(Defines.INTERFACES.resultSeparator), Defines.INTERFACES.resultSeparator);
                Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME), Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME));

                Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE), Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE));
                Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE), Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE));
                Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE), Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE));
                Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD), Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD));
                Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT), Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT));
            }
            if (Defines.runningProjectId == GeneralConstants.SRC_ID_ENQUEUE_SMS_REST) {
                Defines.ENCRYPTION_KEY = checkForNull(propertiesHashMap.get(Defines.ENCRYPTION_KEY_PROPERTY), Defines.ENCRYPTION_KEY_PROPERTY);
//                Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME), Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME));
                Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE), Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE));
                Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE), Defines.INTERFACES.MAX_LOGGING_DB_ARRAY_SIZE));
                Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE), Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE));
                Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD), Defines.INTERFACES.MAX_NUM_OF_RETRIES_LTHREAD));
                Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT), Defines.INTERFACES.LOGGING_THREAD_PULL_TIMEOUT));
            }
            if (Defines.runningProjectId == GeneralConstants.SRC_ID_ENQUEUE_SMS_REST || Defines.runningProjectId == GeneralConstants.SRC_ID_DEQUEUER_WEB_SERVICE || Defines.runningProjectId == GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE || Defines.runningProjectId == GeneralConstants.SRC_ID_SENDING_SMS_ENGINE) {
                Defines.QUEUE_C3P0_ACQUIRE_INCREMENT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.QUEUE_C3P0_ACQUIRE_INCREMENT), Defines.QUEUE_C3P0_ACQUIRE_INCREMENT));
                Defines.QUEUE_C3P0_CHECKOUT_TIMEOUT_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.QUEUE_C3P0_CHECKOUT_TIMEOUT), Defines.QUEUE_C3P0_CHECKOUT_TIMEOUT));
                Defines.ENQUEUE_C3P0_INITIAL_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.QUEUE_C3P0_INITIAL_POOL_SIZE), Defines.QUEUE_C3P0_INITIAL_POOL_SIZE));
                Defines.ENQUEUE_C3P0_MAX_CONNECTION_AGE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.QUEUE_C3P0_MAX_CONNECTION_AGE), Defines.QUEUE_C3P0_MAX_CONNECTION_AGE));
                Defines.ENQUEUE_C3P0_MAX_IDLE_TIME_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.QUEUE_C3P0_MAX_IDLE_TIME), Defines.QUEUE_C3P0_MAX_IDLE_TIME));
                Defines.ENQUEUE_C3P0_MAX_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.QUEUE_C3P0_MAX_POOL_SIZE), Defines.QUEUE_C3P0_MAX_POOL_SIZE));
                Defines.ENQUEUE_C3P0_MAX_STATEMENTS_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.QUEUE_C3P0_MAX_STATEMENTS), Defines.QUEUE_C3P0_MAX_STATEMENTS));
                Defines.ENQUEUE_C3P0_MIN_POOL_SIZE_VALUE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.ENQUEUE_C3P0_MIN_POOL_SIZE), Defines.ENQUEUE_C3P0_MIN_POOL_SIZE));
            }

            if (Defines.runningProjectId == GeneralConstants.SRC_ID_SMSC_INTERFACE 
                    || Defines.runningProjectId == GeneralConstants.SRC_ID_SENDING_SMS_ENGINE
                    || Defines.runningProjectId == GeneralConstants.SRC_ID_ENQUEUE_SMS_REST
                    || Defines.runningProjectId == GeneralConstants.SRC_ID_WEB_INTERFACE
                    ) {
                // messaging mode
                Defines.MESSAGING_MODE = checkForNull(propertiesHashMap.get(Defines.MESSAGING_MODE_NAME), Defines.MESSAGING_MODE_NAME);
                // rabbitmq properties
                Defines.RABBITMQ_USERNAME = checkForNull(propertiesHashMap.get(Defines.RABBITMQ_USERNAME_NAME), Defines.RABBITMQ_USERNAME_NAME);
                Defines.RABBITMQ_PASSWORD = checkForNull(propertiesHashMap.get(Defines.RABBITMQ_PASSWORD_NAME), Defines.RABBITMQ_PASSWORD_NAME);
                Defines.RABBITMQ_VIRTUAL_HOST = checkForNull(propertiesHashMap.get(Defines.RABBITMQ_VIRTUAL_HOST_NAME), Defines.RABBITMQ_VIRTUAL_HOST_NAME);
                Defines.RABBITMQ_HOST = checkForNull(propertiesHashMap.get(Defines.RABBITMQ_HOST_NAME), Defines.RABBITMQ_HOST_NAME);
                Defines.RABBITMQ_PORT = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.RABBITMQ_PORT_NAME), Defines.RABBITMQ_PORT_NAME));
            }

            if (Defines.runningProjectId == GeneralConstants.SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE) {
                Defines.ENCRYPTION_KEY = checkForNull(propertiesHashMap.get(Defines.ENCRYPTION_KEY_PROPERTY), Defines.ENCRYPTION_KEY_PROPERTY);
            }
        } catch (Exception e) {
            System.out.println("Exception in reading properties file---->" + e);
            System.err.println("Exception in reading properties file---->" + e);
            errorFunctionName = "readPropertiesFile";
            throw e;
        }
    }

    public static void initializeDataSource() throws Exception {
        try {
            DataSourceManger.init();
        } catch (Exception ex) {
            System.out.println("Exception in initializeDataSource---->" + ex);
            System.err.println("Exception in initializeDataSource---->" + ex);
            errorFunctionName = "initializeDataSource";
            throw ex;
        }
    }

//     public static void initializeConnectionPool() throws Exception {
//        try {
//            DBConnPoolManager.initialize();
//        } catch (Exception ex) {
//            System.out.println("Exception in initializeConnectionPool---->" + ex);
//            System.err.println("Exception in initializeConnectionPool---->" + ex);
//            throw ex;
//        }
//    }
    public static void initializeLoggers() throws Exception {
        try {
//            Properties logProperties = new Properties();
//            logProperties.load(new FileInputStream(Defines.LOG4J_FILE_PATH));
//            PropertyConfigurator.configure(logProperties);
            ConfigurationSource source = new ConfigurationSource(new FileInputStream(Defines.LOG4J_FILE_PATH));
            Configurator.initialize(null, source);
            CommonLogger.initLoggers();
            CommonLogger.businessLogger.info("Loggers Initialized successfully...");
//            CommonLogger.businessLogger.debug("Loggers Initialized successfully...");
//            CommonLogger.businessLogger.info(StructuredLogFactory.put("First", "true").put("Second", "false").build());
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException in initializeLoggers---->" + ex);
            System.err.println("FileNotFoundException in initializeLoggers---->" + ex);
            errorFunctionName = "initializeLoggers";
            throw ex;
        } catch (IOException ex) {
            System.out.println("IOException in initializeLoggers---->" + ex);
            System.err.println("IOException in initializeLoggers---->" + ex);
            errorFunctionName = "initializeLoggers";
            throw ex;
        } catch (Exception ex) {
            System.out.println("Exception in initializeLoggers---->" + ex);
            System.err.println("Exception in initializeLoggers---->" + ex);
            errorFunctionName = "initializeLoggers";
            throw ex;
        }
    }

    public static void initializeLookups() throws Exception {
        try {
            System.out.println("Start reading  lookups properties");
            //call added properties 
//            loadLDAPProperites();
            SystemLookups.ORIGINATORS_LIST = new com.asset.contactstrategy.interfaces.service.MainService().getOriginatorsList();
            //  loadDWHProperites();
            loadInterfacesLookups();
        } catch (Exception e) {
            System.out.println("Exception in initializeLookups---->" + e);
            System.err.println("Exception in initializeLookups---->" + e);
            errorFunctionName = "initializeLookups";
            throw e;
        }
    }

//    public static void loadLDAPProperites() throws Exception {
//        try {
//            System.out.println("Start reading LDAB properties");
//            propertiesFileBundle = ResourceBundle.getBundle(Defines.PROPERTIES_FILE_NAME);
//            Defines.LDAPLOOKUP = new HashMap<>();
//            Defines.LDAPLOOKUP.put(Defines.LDAP_SERVER_DN_PROPERTY_NAME, propertiesFileBundle.getString(Defines.LDAP_SERVER_DN_PROPERTY_NAME));
//            Defines.LDAPLOOKUP.put(Defines.LDAP_SERVER_URL_PROPERTY_NAME, propertiesFileBundle.getString(Defines.LDAP_SERVER_URL_PROPERTY_NAME));
//            Defines.LDAPLOOKUP.put(Defines.LDAP_USERS_SB_PROPERTY_NAME, propertiesFileBundle.getString(Defines.LDAP_USERS_SB_PROPERTY_NAME));
//            Defines.LDAPLOOKUP.put(Defines.LDAP_AUTHENTICATION_PROPERTY_NAME, propertiesFileBundle.getString(Defines.LDAP_AUTHENTICATION_PROPERTY_NAME));
//
//        } catch (Exception e) {
//            System.out.println("Exception in reading LDAP properites---->" + e);
//            System.err.println("Exception in reading LDAP properites---->" + e);
//            throw e;
//        }
//    }
//    public static void loadDWHProperites() throws Exception {
//        try {
//            System.out.println("Start reading DWH properties");
//            int noOfDates;
//            int noOfStrings;
//            int noOfNumbers;
//            int defaultNoOfColumns = 60;
//            String DATE_PREFIX = "D";
//            String STRING_PREFIX = "S";
//            String NUMBER_PREFIX = "N";
//
//            propertiesFileBundle = ResourceBundle.getBundle(Defines.PROPERTIES_FILE_NAME);
//
////            Defines.DWH_PROFILE_DATES = propertiesFileBundle.getString(Defines.KEY_DWH_PROFILE_DATES);
////            Defines.DWH_PROFILE_NUMBERS = propertiesFileBundle.getString(Defines.KEY_DWH_PROFILE_NUMBERS);
////            Defines.DWH_PROFILE_STRINGS = propertiesFileBundle.getString(Defines.KEY_DWH_PROFILE_STRINGS);
////
////            noOfDates = (String) Defines.DWH_PROFILE_DATES == null ? defaultNoOfColumns : Integer.parseInt((String) Defines.DWH_PROFILE_DATES);
////            noOfStrings = (String) Defines.DWH_PROFILE_NUMBERS == null ? defaultNoOfColumns : Integer.parseInt((String) Defines.DWH_PROFILE_NUMBERS);
////            noOfNumbers = (String) Defines.DWH_PROFILE_STRINGS == null ? defaultNoOfColumns : Integer.parseInt((String) Defines.DWH_PROFILE_STRINGS);
////
////            DBStruct.DWH_CUSTOMERS.DATES = loadFieldNames(DATE_PREFIX, noOfDates);
////            DBStruct.DWH_CUSTOMERS.NUMBERS = loadFieldNames(NUMBER_PREFIX, noOfNumbers);
////            DBStruct.DWH_CUSTOMERS.NUMBERS.add(DBStruct.DWH_CUSTOMERS.RATE_PLAN);
////            DBStruct.DWH_CUSTOMERS.NUMBERS.add(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE);
////            DBStruct.DWH_CUSTOMERS.NUMBERS.add(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID);
////            DBStruct.DWH_CUSTOMERS.STRINGS = loadFieldNames(STRING_PREFIX, noOfStrings);
////            DBStruct.DWH_CUSTOMERS.STRINGS.add(DBStruct.DWH_CUSTOMERS.MSISDN);
////            DBStruct.DWH_CUSTOMERS.STRINGS.add(DBStruct.DWH_CUSTOMERS.SERVICE_CLASS);
//
//        } catch (Exception e) {
//            System.out.println("Exception in reading DWH properites---->" + e);
//            System.err.println("Exception in reading DWH properites---->" + e);
//            throw e;
//        }
//    }
//    public static void loadAppsQueues() throws CommonException {
//        MainService mainService = new MainService();
//        SystemLookups.QUEUE_LIST =  mainService.getHashedApplicationQueues();
//    }
    public static void initializeRabbitmq() throws IOException, TimeoutException {
        RabbitmqUtil.init();
    }

    public static void loadAppsQueues() throws CommonException {
        MainService mainService = new MainService();
        SystemLookups.QUEUE_LIST_BY_NAME = mainService.getHashedApplicationQueues();
    }

//    public static void loadInterfaceDataForSMPP()throws CommonException{
//        loadAppsQueues();
//    }
    public static void loadInterfacesData() throws CommonException {
        com.asset.contactstrategy.interfaces.service.MainService mainService = new com.asset.contactstrategy.interfaces.service.MainService();
        SystemLookups.SERVICES = mainService.getAllServices();
        SystemLookups.SMS_GROUPS = mainService.getSmsGroupsList();
        SystemLookups.ADS_GROUPS = mainService.getAdsGroupsList();
        SystemLookups.ORIGINATORS_LIST = mainService.getOriginatorsList();
        //CSPhase1.5 | Esmail.Anbar | Adding Template Update
        SystemLookups.TEMPLATES = mainService.getAllTemplates();
    }

    public static void loadSystemProperties() throws CommonException {
        MainService mainService = new MainService();
        SystemLookups.SYSTEM_PROPERTIES = mainService.getAllSystemProperties();
    }

    private static ArrayList<String> loadFieldNames(String prefix, int numberOfFields) {
        ArrayList<String> colNames = new ArrayList<String>();
        for (int i = 1; i < numberOfFields; i++) {
            String columnName = prefix + i;
            colNames.add(columnName);
        }

        return colNames;
    }

    public static void loadInterfacesLookups() throws CommonException {
        SystemLookups.SMS_H_STATUS = Utility.loadLookupsMap(DBStruct.VFE_CS_SMS_H_STATUS_LK.TABLE_NAME, DBStruct.VFE_CS_SMS_H_STATUS_LK.SMS_H_STATUS_ID, DBStruct.VFE_CS_SMS_H_STATUS_LK.SMS_H_STATUS_NAME);
        SystemLookups.ORIGINATOR_VALUES_LK = Utility.loadLookupsMap(DBStruct.VFE_CS_ORIGINATOR_VALUES_LK.TABLE_NAME, DBStruct.VFE_CS_ORIGINATOR_VALUES_LK.ORIGINATOR_VALUE_ID, DBStruct.VFE_CS_ORIGINATOR_VALUES_LK.ORIGINATOR_VALUE_NAME);
    }

    public static ArrayBlockingQueue getSmsQueue() throws Exception {
        if (smsQueue == null) {
            int queueCapacity = 0;
            if (SystemLookups.SYSTEM_PROPERTIES != null) {
                try {
                    queueCapacity = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get("BLOCKING_QUEUE_CAPACITY"));
                } catch (NumberFormatException e) {
                    throw new Exception("Exception in errormessagebundle-->getSmsQueue" + e.getMessage());
                }
            }
            smsQueue = new ArrayBlockingQueue(queueCapacity, true);//HAZEMTODO: Check queue size
        }
        return smsQueue;
    }

    public static String checkForNull(String propertyValue, String propertyName) throws CommonException {
        if (propertyValue == null) {
            System.out.println("Exception in reading property---->" + propertyName);
            System.err.println("Exception in reading property---->" + propertyName);
            errorFunctionName = "readProperties";
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.READING_ENVIRONMENT_VARIABLES_ERROR, null);
        } else {
            return propertyValue;
        }
    }

    private static void readC3P0Configurations(HashMap<String, String> propertiesHashMap) throws Exception {

        try {
            System.out.println("Starting reading C3P0 Configurations");
            Defines.C3P0_INITIAL_POOL_SIZE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_INITIAL_POOL_SIZE_PROPERTY_NAME), Defines.C3P0_INITIAL_POOL_SIZE_PROPERTY_NAME));
            Defines.C3P0_ACQUIRE_INCREMENT = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_ACQUIRE_INCREMENT_PROPERTY_NAME), Defines.C3P0_ACQUIRE_INCREMENT_PROPERTY_NAME));
            Defines.C3P0_MAX_POOL_SIZE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_MAX_POOL_SIZE_PROPERTY_NAME), Defines.C3P0_MAX_POOL_SIZE_PROPERTY_NAME));
            Defines.C3P0_MIN_POOL_SIZE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_MIN_POOL_SIZE_PROPERTY_NAME), Defines.C3P0_MIN_POOL_SIZE_PROPERTY_NAME));
            Defines.C3P0_MAX_STATEMENTS = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_MAX_STATEMENTS_PROPERTY_NAME), Defines.C3P0_MAX_STATEMENTS_PROPERTY_NAME));
            Defines.C3P0_MAX_IDLE_TIME = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_MAX_IDLE_TIME_PROPERTY_NAME), Defines.C3P0_MAX_IDLE_TIME_PROPERTY_NAME));
            Defines.C3P0_MAX_CONNECTION_AGE = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_MAX_CONNECTION_AGE_PROPERTY_NAME), Defines.C3P0_MAX_CONNECTION_AGE_PROPERTY_NAME));
            Defines.C3P0_CHECKOUT_TIMEOUT = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME), Defines.C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME));
            Defines.C3P0_CHECKOUT_TIMEOUT = Integer.parseInt(checkForNull(propertiesHashMap.get(Defines.C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME), Defines.C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME));
            Defines.C3P0_UNRETURNED_CONNECTION_TIMEOUT = checkForNull(propertiesHashMap.get(Defines.C3P0_UNRETURNED_CONNECTION_TIMEOUT_PROPERTY_NAME), Defines.C3P0_UNRETURNED_CONNECTION_TIMEOUT_PROPERTY_NAME);
            Defines.C3P0_JDBC_URL = checkForNull(propertiesHashMap.get(Defines.C3P0_JDBC_URL_PROPERTY_NAME), Defines.C3P0_JDBC_URL_PROPERTY_NAME);
            Defines.C3P0_USER = checkForNull(propertiesHashMap.get(Defines.C3P0_USER_PROPERTY_NAME), Defines.C3P0_USER_PROPERTY_NAME);
            Defines.C3P0_PASSWORD = checkForNull(propertiesHashMap.get(Defines.C3P0_PASSWORD_PROPERTY_NAME), Defines.C3P0_PASSWORD_PROPERTY_NAME);
            Defines.C3P0_DRIVER_CLASS = checkForNull(propertiesHashMap.get(Defines.C3P0_DRIVER_CLASS_PROPERTY_NAME), Defines.C3P0_DRIVER_CLASS_PROPERTY_NAME);
        } catch (Exception e) {
            System.out.println("Exception in reading C3P0 Configuration---->" + e);
            System.err.println("Exception in reading C3P0 Configuration---->" + e);
            errorFunctionName = "readPropertiesFile";
            throw e;
        }
    }

    private static Long convertResourceBundleToMapPrintTime;

    private static HashMap<String, String> convertResourceBundleToMap(HashMap propertiesMap, ResourceBundle resource) {
        //   HashMap<String, String> map = new HashMap<>();
        boolean printLogs = false;
        if (convertResourceBundleToMapPrintTime == null
                || (System.currentTimeMillis() - convertResourceBundleToMapPrintTime) > 3600000) {//prints every hour
            printLogs = true;
        }
        //Esmail.Anbar | 13/7/2017 | Adding Properties Logging
        if (printLogs) {
            System.out.println("======================================== Started Reading Properties from ResourceBundle");
        }
        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            propertiesMap.put(key, resource.getString(key));
            if (printLogs) {
                System.out.println("Key: " + key + " | Value: " + resource.getString(key));
            }
        }
        if (printLogs) {
            System.out.println("======================================== Ended Reading Properties from ResourceBundle");
        }
        return propertiesMap;
    }

    private static Long convertEnvVarToMapPrintTime;

    private static HashMap<String, String> convertEnvVarToMap(HashMap propertiesMap) {
        //  HashMap<String, String> map = new HashMap<>();
        boolean printLogs = false;
        if (convertEnvVarToMapPrintTime == null
                || (System.currentTimeMillis() - convertEnvVarToMapPrintTime) > 3600000) {//prints every hour
            printLogs = true;
        }
        //Esmail.Anbar | 13/7/2017 | Adding Properties Logging
        if (printLogs) {
            System.out.println("======================================== Started Reading Properties from EnvironmentVariables");
        }
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            propertiesMap.put(entry.getKey(), entry.getValue());
            if (printLogs) {
                System.out.println("Key: " + entry.getKey() + " | Value: " + entry.getValue());
            }
        }
        if (printLogs) {
            System.out.println("============= Adding Lower Case from EnvironmentVariables");
        }
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            propertiesMap.put(entry.getKey().toLowerCase(), entry.getValue());
            if (printLogs) {
                System.out.println("Key: " + entry.getKey() + " | Value: " + entry.getValue());
            }
        }
        if (printLogs) {
            System.out.println("======================================== Ended Reading Properties from EnvironmentVariables");
        }
        return propertiesMap;
    }

    public static void readPropertiesFileTest(boolean C3P0ConnectionFlag) throws Exception {
        try {
            System.out.println("Starting reading messages properties");
            Defines.messagesBundle = ResourceBundle.getBundle(Defines.MESSAGES_FILE_NAME);
        } catch (Exception ex) {
            System.out.println("Exception in errormessagebundle---->" + ex);
            System.err.println("Exception in errormessagebundle---->" + ex);
            errorFunctionName = "readPropertiesFile";
            throw new Exception(ex);
        }
        try {
            System.out.println("Starting reading environment variables");
            Defines.LOG4J_FILE_PATH = checkForNull(System.getenv(Defines.LOG4J_FILE_PATH_PROPERTY_NAME), Defines.LOG4J_FILE_PATH_PROPERTY_NAME);

            //c3p0 connection pool properities
            if (C3P0ConnectionFlag) {
                readC3P0Configurations();
            } else {
                //weblogic prperties
                Defines.WEBLOGIC_CONTEXT_FACTORY = checkForNull(System.getenv(Defines.WEBLOGIC_CONTEXT_FACTORY_PROPERTY_NAME), Defines.WEBLOGIC_CONTEXT_FACTORY_PROPERTY_NAME);
                Defines.WEBLOGIC_PROVIDER_URL = checkForNull(System.getenv(Defines.WEBLOGIC_PROVIDER_URL_PROPERTY_NAME), Defines.WEBLOGIC_PROVIDER_URL_PROPERTY_NAME);
                Defines.DATA_SOURCE_NAME = checkForNull(System.getenv(Defines.DATA_SOURCE_NAME_PROPERTY_NAME), Defines.DATA_SOURCE_NAME_PROPERTY_NAME);

            }

            if (Defines.runningProjectId == GeneralConstants.SRC_ID_WEB_INTERFACE) {
                //Update queue Query HashMap
//                Defines.QUEUE_QUERY.QUERIES = new HashMap<>();
//                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY, checkForNull(System.getenv(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY), Defines.QUEUE_QUERY.QUEUE_TBL_QUERY));
//                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY, checkForNull(System.getenv(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY), Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY));
//                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_START_QUERY, checkForNull(System.getenv(Defines.QUEUE_QUERY.QUEUE_START_QUERY), Defines.QUEUE_QUERY.QUEUE_START_QUERY));
//                Defines.QUEUE_QUERY.QUERIES.put(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY, checkForNull(System.getenv(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY), Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY));
                Defines.DWH_CUSTOMERS_COLUMNS_STRINGS = Integer.parseInt(checkForNull(System.getenv(Defines.DWH_CUSTOMERS_COLUMNS_STRINGS_PROPERTY_NAME), Defines.DWH_CUSTOMERS_COLUMNS_STRINGS_PROPERTY_NAME));
                Defines.DWH_CUSTOMERS_COLUMNS_DATES = Integer.parseInt(checkForNull(System.getenv(Defines.DWH_CUSTOMERS_COLUMNS_DATES_PROPERTY_NAME), Defines.DWH_CUSTOMERS_COLUMNS_DATES_PROPERTY_NAME));
                Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS = Integer.parseInt(checkForNull(System.getenv(Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS_PROPERTY_NAME), Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS_PROPERTY_NAME));
            }
            if (Defines.runningProjectId == GeneralConstants.SRC_ID_SENDING_SMS_ENGINE || Defines.runningProjectId == GeneralConstants.SRC_ID_WEB_INTERFACE) {
                Defines.ENCRYPTION_KEY = checkForNull(System.getenv(Defines.ENCRYPTION_KEY_PROPERTY), Defines.ENCRYPTION_KEY_PROPERTY);
                Defines.SMS_BULK_UPLOAD_BATCH_SIZE = Long.parseLong(checkForNull(System.getenv(Defines.SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY), Defines.SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY));
            }
            if (Defines.runningProjectId == GeneralConstants.SRC_ID_DEQUEUER_WEB_SERVICE) {
                Defines.ENCRYPTION_KEY = checkForNull(System.getenv(Defines.ENCRYPTION_KEY_PROPERTY), Defines.ENCRYPTION_KEY_PROPERTY);
                /// Defines.SMS_BULK_UPLOAD_BATCH_SIZE = Long.parseLong(checkForNull(System.getenv(Defines.SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY), Defines.SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY));
            }
        } catch (Exception e) {
            System.out.println("Exception in reading properties file---->" + e);
            System.err.println("Exception in reading properties file---->" + e);
            errorFunctionName = "readPropertiesFile";
            throw e;
        }
    }

    private static void readC3P0Configurations() throws Exception {

        try {
            System.out.println("Starting reading C3P0 Configurations");
            Defines.C3P0_INITIAL_POOL_SIZE = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_INITIAL_POOL_SIZE_PROPERTY_NAME), Defines.C3P0_INITIAL_POOL_SIZE_PROPERTY_NAME));
            Defines.C3P0_ACQUIRE_INCREMENT = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_ACQUIRE_INCREMENT_PROPERTY_NAME), Defines.C3P0_ACQUIRE_INCREMENT_PROPERTY_NAME));
            Defines.C3P0_MAX_POOL_SIZE = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_MAX_POOL_SIZE_PROPERTY_NAME), Defines.C3P0_MAX_POOL_SIZE_PROPERTY_NAME));
            Defines.C3P0_MIN_POOL_SIZE = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_MIN_POOL_SIZE_PROPERTY_NAME), Defines.C3P0_MIN_POOL_SIZE_PROPERTY_NAME));
            Defines.C3P0_MAX_STATEMENTS = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_MAX_STATEMENTS_PROPERTY_NAME), Defines.C3P0_MAX_STATEMENTS_PROPERTY_NAME));
            Defines.C3P0_MAX_IDLE_TIME = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_MAX_IDLE_TIME_PROPERTY_NAME), Defines.C3P0_MAX_IDLE_TIME_PROPERTY_NAME));
            Defines.C3P0_MAX_CONNECTION_AGE = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_MAX_CONNECTION_AGE_PROPERTY_NAME), Defines.C3P0_MAX_CONNECTION_AGE_PROPERTY_NAME));
            Defines.C3P0_CHECKOUT_TIMEOUT = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME), Defines.C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME));
            Defines.C3P0_CHECKOUT_TIMEOUT = Integer.parseInt(checkForNull(System.getenv(Defines.C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME), Defines.C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME));
            Defines.C3P0_UNRETURNED_CONNECTION_TIMEOUT = checkForNull(System.getenv(Defines.C3P0_UNRETURNED_CONNECTION_TIMEOUT_PROPERTY_NAME), Defines.C3P0_UNRETURNED_CONNECTION_TIMEOUT_PROPERTY_NAME);
            Defines.C3P0_DRIVER_CLASS = checkForNull(System.getenv(Defines.C3P0_DRIVER_CLASS_PROPERTY_NAME), Defines.C3P0_DRIVER_CLASS_PROPERTY_NAME);
            Defines.C3P0_USER = checkForNull(System.getenv(Defines.C3P0_USER_PROPERTY_NAME), Defines.C3P0_USER_PROPERTY_NAME);
            Defines.C3P0_PASSWORD = checkForNull(System.getenv(Defines.C3P0_PASSWORD_PROPERTY_NAME), Defines.C3P0_PASSWORD_PROPERTY_NAME);
            Defines.C3P0_JDBC_URL = checkForNull(System.getenv(Defines.C3P0_JDBC_URL_PROPERTY_NAME), Defines.C3P0_JDBC_URL_PROPERTY_NAME);
        } catch (Exception e) {
            System.out.println("Exception in reading C3P0 Configuration---->" + e);
            System.err.println("Exception in reading C3P0 Configuration---->" + e);
            errorFunctionName = "readPropertiesFile";
            throw e;
        }
    }

    public static void attachShutDownHook(Logger commonLogger, AtomicBoolean engineShutdownFlag, EngineManager manager, String engineName) {
        Runtime.getRuntime().addShutdownHook(new EngineShutdownThread(commonLogger, engineShutdownFlag, manager, engineName));

    }
}
