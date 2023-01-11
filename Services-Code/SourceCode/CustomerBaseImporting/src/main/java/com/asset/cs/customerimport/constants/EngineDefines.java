/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.constants;

/**
 *
 * @author Zain Al-Abedin
 */
public class EngineDefines {

    public static final String NO_OF_PREPARING_WORKER_THREADS = "NO_OF_PREPARING_WORKER_THREADS";
    public static final String NO_OF_WRITER_WORKERS_PER_THREAD = "NO_OF_WRITER_WORKERS_PER_THREAD";
    public static final String NO_OF_READER_WORKER_THREADS = "NO_OF_READER_WORKER_THREADS";

    public static final String NO_OF_SUB_PARTITIONS_PER_THREADPOOL = "NO_OF_SUB_PARTITIONS_PER_THREADPOOL";
    public static final String PREPARING_STATMENT_THREAD_SLEEP_TIME = "PREPARING_STATMENT_THREAD_SLEEP_TIME";
    public static final String READER_THREAD_SLEEP_TIME = "READER_THREAD_SLEEP_TIME";
    public static final String WRITER_THREAD_SLEEP_TIME = "WRITER_THREAD_SLEEP_TIME";
    public static final String FILES_WORKER_THREAD_SLEEP_TIME = "FILES_WORKER_THREAD_SLEEP_TIME";
    public static final String CONFUGURATION_THREAD_SLEEP_TIME = "CONFUGURATION_THREAD_SLEEP_TIME";
    public static final String CUSTOMER_BASE_MONITORING_THREAD_SLEEP_TIME = "CUSTOMER_BASE_MONITORING_THREAD_SLEEP_TIME";
    public static final String NO_NEW_FILES_SLEEP_TIME = "NO_NEW_FILES_SLEEP_TIME";
    public static final String WRITER_BATCH_MAX_SIZE = "WRITER_BATCH_MAX_SIZE";
    public static final String WRITER_BATCH_MIN_SIZE = "WRITER_BATCH_MIN_SIZE";
    public static final String READER_THRESHOLD_MAX_SIZE = "READER_DATA_QUEUE_MAX_SIZE";
    public static final String WRITER_DATA_QUEUE_THRESHOLD_MAX_SIZE = "WRITER_DATA_QUEUE_THRESHOLD_MAX_SIZE";
    public static final String KEY_ENGINE_SHUTDOWN_FLAG = "DW_EXTRACOR_ENGINE_SHUTDOWN_FLAG";
    public static final String KEY_DWH_ZIP_FOLDER_PATH = "DWH_ZIP_FOLDER_PATH";
    public static final String KEY_DWH_UNZIP_FOLDER_PATH = "DWH_UNZIP_FOLDER_PATH";
    public static final String KEY_DWH_ZIP_FILE_NAMES = "DWH_ZIPPED_FILE_NAMES";
    public static final String KEY_MONITOR_SLEEP_TIME = "MONITOR_SLEEP_TIME";
    public static final String KEY_JAVA_QUEUE_EXCEED_PERCENTAGE = "JAVA_QUEUE_EXCEED_PERCENTAGE";

    public static final String KEY_GET_CONNECTION_RETRY_NUM = "GET_CONNECTION_RETAY_NUM";
    public static final String KEY_INVALID_MSISDN_FORMATE_MOM_SEVERITY = "INVALID_MSISDN_FORMATE_MOM_SEVERITY";
    public static final String KEY_FAILED_EXTRACT_DWH_FILES_MOM_SEVERITY = "FAILED_EXTRACT_DWH_FILES_MOM_SEVERITY";
    public static final String KEY_MOVE_FILES_HISTORICAL_MOM_SEVERITY = "MOVE_FILES_HISTORICAL_MOM_SEVERITY";
     public static final String KEY_NO_FILES_RECEIVED_MOM_SEVERITY = "NO_FILES_RECEIVED_MOM_SEVERITY";

    // public static final String KEY_DWH_UNZIP_FILE_NAMES = "DWH_UNZIPPED_FILE_NAMES";
    public static final String KEY_RUNNING_DWH_PROFILE_PARTITION = "RUNNING_DWH_PROFILE_PARTITION";
    public static final String KEY_DWH_FAILURE_HISTORY_PATH = "DWH_FAILURE_HISTORY_PATH";
    public static final String KEY_DWH_SUCCESS_HISTORY_PATH = "DWH_SUCCESS_HISTORY_PATH";
    public static final String DW_FILE_SPLITTER = ",";
    public static final String DW_FILE_NAME_SPLITTER = ",";
    public static final String DW_ZIP_FILE_EXTENSION = "DW_ZIP_FILE_EXTENSION";
    public static final String DW_UNZIP_FILE_EXTENSION = "DW_UNZIP_FILE_EXTENSION";
    public static final String FILE_LAST_MODIFIED_TIME_MIN = "FILE_LAST_MODIFIED_TIME_MIN";
    public static final String FOLDER_EXPIRY_PERIOD_MIN = "FOLDER_EXPIRY_PERIOD_MIN";
    public static final String OPERATING_SYSTEM = "OPERATING_SYSTEM";
    public static final String DWH_HISTORICAL_FILE_NAME = "yyMMdd_HHmmss";
    public static final String MOVE_COMMAND_WINDOWS = "MOVE_COMMAND_WINDOWS";
    public static final String MOVE_COMMAND_UNIX = "MOVE_COMMAND_UNIX";
    public static final String UNIX = "unix";
    public static final String WINDOWS = "windows";
    public static final String DWH_LAST_UPLOADED_DATE = "DWH_LAST_UPLOADED_DATE";
    public static final String DWH_LAST_ERROR_DATE = "DWH_LAST_ERROR_DATE";
    public static final String FILE_RECEIVED_NUMBER_OF_HOURS = "FILE_RECEIVED_NUMBER_OF_HOURS";
    
    //Esmail.Anbar | Cloud CR
    public static final String SFTP_HOST = "SFTP_HOST";
    public static final String SFTP_PORT = "SFTP_PORT";
    public static final String SFTP_USER = "SFTP_USER";
    public static final String SFTP_PASS = "SFTP_PASS";
    public static final String SFTP_WORKING_DIR = "SFTP_WORKING_DIR";
    public static final String REMOTE_FILE_DIRECTORY = "REMOTE_FILE_DIRECTORY";
    public static final String TRANSFER_PROTOCOL = "TRANSFER_PROTOCOL";
}
