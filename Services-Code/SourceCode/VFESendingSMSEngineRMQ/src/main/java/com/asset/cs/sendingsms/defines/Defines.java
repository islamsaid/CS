/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.defines;

import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.rabbitmq.client.Channel;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author kerollos.asaad
 */
public class Defines {

    // system properties file keys.
    public static final String MESSAGES_FILE_NAME = "messages";
    public static String DATABASE_CREDENTIALS = "Connection";
    public static String SendRecvHashWindowSize = "SendRecvHashWindowSize";
    public static String RecvDelHashWindowSize = "RecvDelHashWindowSize";

    public static String KEEP_ALIVE = "KeepAlive";
    public static String GARBAGE_COLLECTIOR_TIME = "garbageCollectorTime";
    public static final String SENDER_SMS_QUEUE_SIZE = "SENDER_SMS_QUEUE_SIZE";
    public static final String ARCHIVE_SMS_QUEUE_SIZE = "ARCHIVE_SMS_QUEUE_SIZE";
    public static final String ARCHIVE_CONCAT_SMS_QUEUE_SIZE = "ARCHIVE_CONCAT_SMS_QUEUE_SIZE";
    public static final String UPDATE_ON_DELIVERY_SMS_QUEUE_SIZE = "UPDATE_ON_DELIVERY_SMS_QUEUE_SIZE";
    public static final String DELIVERY_RESP_SMS_QUEUE_SIZE = "DELIVERY_RESP_SMS_QUEUE_SIZE";
    public static final String ARCHIVE_POOL_SIZE = "ARCHIVE_POOL_SIZE";
    public static final String ARCHIVE_THREAD_SLEEP_TIME = "ARCHIVE_THREAD_SLEEP_TIME";
    public static final String ARCHIVE_THREAD_BATCH_SIZE = "ARCHIVE_THREAD_BATCH_SIZE";
    public static final String ARCHIVE_POOLING_TIMEOUT = "ARCHIVE_POOLING_TIMEOUT";
    public static final String ARCHIVE_CONCAT_POOL_SIZE = "ARCHIVE_CONCAT_POOL_SIZE";
    public static final String ARCHIVE_CONCAT_THREAD_SLEEP_TIME = "ARCHIVE_CONCAT_THREAD_SLEEP_TIME";
    public static final String ARCHIVE_CONCAT_THREAD_BATCH_SIZE = "ARCHIVE_CONCAT_THREAD_BATCH_SIZE";
    public static final String ARCHIVE_CONCAT_POOLING_TIMEOUT = "ARCHIVE_CONCAT_POOLING_TIMEOUT";
    public static final String UPDATE_ON_DELIVERY_POOL_SIZE = "UPDATE_ON_DELIVERY_POOL_SIZE";
    public static final String UPDATE_ON_DELIVERY_THREAD_SLEEP_TIME = "UPDATE_ON_DELIVERY_THREAD_SLEEP_TIME";
    public static final String UPDATE_ON_DELIVERY_THREAD_BATCH_SIZE = "UPDATE_ON_DELIVERY_THREAD_BATCH_SIZE";
    public static final String UPDATE_ON_DELIVERY_POOLING_TIMEOUT = "UPDATE_ON_DELIVERY_POOLING_TIMEOUT";
    public static final String MONITOR_THREAD_SLEEP_TIME = "MONITOR_THREAD_SLEEP_TIME";
    public static final String SMSC_NOT_CONNECTED_MOM_ALARM = "SMSC_NOT_CONNECTED_MOM_ALARM";
    public static final String DAYS_BEFORE_SMS_TIMEOUT = "DAYS_BEFORE_SMS_TIMEOUT";    
    public static final String RECIEVED_SMS_QUEUE_SIZE = "RECIEVED_SMS_QUEUE_SIZE";
    public static final String RECEIVED_SMS_LOG_QUEUE_SIZE = "RECEIVED_SMS_LOG_QUEUE_SIZE";
    public static final String RECEIVED_SMS_LOG_THRED_POOL_SIZE = "RECEIVED_SMS_LOG_THRED_POOL_SIZE";
    public static final String ENQUEUER_MAX_BATCH_SIZE = "ENQUEUER_MAX_BATCH_SIZE";
    public static final String ENQUEUER_THREAD_POOL_SIZE = "ENQUEUER_THREAD_POOL_SIZE";
    public static final String RECEIVED_SMS_LOG_SLEEP_TIME = "RECEIVED_SMS_LOG_SLEEP_TIME";
    public static final String ENQUEUER_SLEEP_TIME = "ENQUEUER_SLEEP_TIME";
    //DATABASE ENCODING
    public static String DATABASE_VERSION = "Version";
    //Thrads Configuration
    public static String DEQUEUER_THREAD_SLEEPING_TIME = "dequeuerthreadsleepingtime";
    public static String SENDER_THREAD_POLLING_TIMEOUT = "senderthreadpollingtimeout";
    public static String SENDER_THREAD_SLEEPING_TIME = "senderthreadsleepingtime";
    public static String DELIVERRESP_THREAD_POLLING_TIMEOUT = "deliverrespthreadpollingtimeout";
    public static String DELIVERRESP_THREAD_SLEEPING_TIMEOUT = "deliverrespthreadsleepingtimeout";
    public static String INSTACE_ID = "instanceID";
    // end properties file keys.

    public static String C3P0_QUEUE_SETTING_NAME = "queuesettings";
    public static HashMap<String, QueueModel> appQueueMap = new HashMap<String, QueueModel>();
    public static HashMap<Long, List<Channel>> queueChannelMap = new HashMap<>();
    public static HashMap<Long, com.rabbitmq.client.Connection> queueConnectionMap = new HashMap<>();
    //Engine Shutdown Flags
    public static AtomicBoolean ENGINE_SHUTDOWN_FLAG = new AtomicBoolean(false);
    public static boolean DEQUEUER_THREAD_SHUTDOWN_FLAG = false;
    public static boolean SENDER_THREAD_SHUTDOWN_FLAG = false;
    public static boolean DATASM_RECEIVER_SHUTDOWN_FLAG = false;
    //public static boolean ARCHIVE_THREAD_SHUTDOWN_FLAG = false;
    //public static boolean UPDATE_ON_DELIVERY_THREAD_SHUTDOWN_FLAG = false;
    //public static boolean ARCHIVE_CONCAT_THREAD_SHUTDOWN_FLAG = false;
    public static boolean ARCHIVES_THREADS_SHUTDOWN_FLAG = false;

    //Database system properties settings keys.
    public static final String SENDING_SMS_SHUTDOWN_FLAG = "SENDING_SMS_SHUTDOWN_FLAG";
    public static final String SENDING_SMS_SHUTDOWN_THREAD_SLEEP_TIME = "SENDING_SMS_SHUTDOWN_THREAD_SLEEP_TIME";
    public static final String SENDING_SMS_RELOAD_CONFIGURATION_TIME = "SENDING_SMS_RELOAD_CONFIGURATION_TIME";
    public static final String SENDING_SMS_TIMEOUT = "SENDING_SMS_TIMEOUT";
    public static final String DELIVER_SMS_TIMEOUT = "DELIVER_SMS_TIMEOUT";
    public static final String HEXADECIMAL_CONVERTER = "HEXADECIMAL_CONVERTER";
    public static final String INSTANCE_ID_ = "INSTANCE_ID_";
    public static final String NB_TRIALS = "NB_TRIALS";
    public static final String JAVA_QUEUES_FULL_PERCENTAGE = "JAVA_QUEUES_FULL_PERCENTAGE";

    public static final String JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY = "JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY";
    public static final String SMSC_CONNECTION_MOM_ERROR_SEVERITY = "SMSC_CONNECTION_MOM_ERROR_SEVERITY";
    public static final String DATABASE_MOM_ERROR_SEVERITY = "DATABASE_MOM_ERROR_SEVERITY";

    //DATABASE ENCODING 
    public static final String DATABASE_WESTERN = "WESTERN";
    public static final String DATABASE_ARABIC = "ARABIC";
    //public static final String MOD_X = "MOD_X";
    public static final int MOD_X = 100;
    public static final String DELIVRD_STATUS_STRING = "DELIVRD";
    public static final String EXPIRED_STATUS_STRING = "EXPIRED";
    public static final String DELETED_STATUS_STRING = "DELETED";
    public static final String UNDELIV_STATUS_STRING = "UNDELIV";
    public static final String ACCEPTD_STATUS_STRING = "ACCEPTD";
    public static final String UNKNOWN_STATUS_STRING = "UNKNOWN";
    public static final String REJECTD_STATUS_STRING = "REJECTD";

    public static final String ID_STRING = "id";
    public static final String SUB_STRING = "sub";
    public static final String STAT_STRING = "stat";
    public static final String SUBMIT_DATE_STRING = "submit date:";
    public static final String DONE_DATE_STRING = "done date:";
    public static final String ERR_STRING = "err";
    public static final String NOT_FOUND_SEQ_ID = "-1";
    //public static final double FULL_PERCENTAGE = 75;
    // Database configuration hashmap.
    public static HashMap<String, String> databaseConfigurations = new HashMap<String, String>();
    public static HashMap<String, String> instanceConfigurations = new HashMap<String, String>();
    public static HashMap<String, String> deliveryEngineConfigurations = new HashMap<String, String>();
    // File configuration hashmap.
    public static HashMap<String, String> fileConfigurations = new HashMap<String, String>();
    public static HashMap<String, Integer> messageStatus = new HashMap<String, Integer>();
    public static ResourceBundle errorMessagesBundle;

    //Flag to define if engine working on SMSC simulator
    public static String SMSC_SIMULATOR_FLAG_PROPERTY = "SMSC_SIMULATOR_RUNNING";

    public static boolean CLOUD_MODE = false;
    public static boolean RELOAD_SMSCS_FLAG = false;
    
    public static final String ENQUEUER_SMS_QUEUE_SIZE = "ENQUEUER_SMS_QUEUE_SIZE";    
    public static String DELIVER_QUEUE_NAME_PREFIX = "DELIVER_QUEUE_NAME_PREFIX";
}
