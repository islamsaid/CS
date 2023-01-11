/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.smsbridging.defines;

import com.asset.contactstrategy.common.models.ServiceModel;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author aya.moawed
 */
public class SMSBridgingDefines {

    //properties file defines
    public static HashMap<String, String> propertiesFileConfigs = new HashMap<String, String>();
    public static String INSTACE_ID_PROP = "instanceID";
    public static String MAX_SERVICE_QUEUE_SIZE_PROP = "maxServicesQueueSize";
    public static String MAX_APP_QUEUE_QUEUE_SIZE_PROP = "maxAppQueueQueueSize";
    public static String DEQUEUE_WAIT_TIME = "dequeueWaitTime";
    public static String QUEUE_NAMES_SPLITTER = "queueNamesSplitter";
    public static String JSON_CONSTRUCTOR_POOL_SIZE = "JSONConstructorThreadPoolSize";
    public static String MAX_JSON_CONSTRUCTOR_QUEUE_SIZE_PROP = "maxJSONConstructorThreadQueueSize";
    public static String MAX_HTTP_HITS = "maxHTTPHitTimes";
    public static String HTTP_POOL_SIZE = "HTTPThreadPoolSize";
    public static String MAX_HTTP_RESULT_QUEUE_SIZE = "maxHTTPResultQueueSize";
    public static String ARCHIVERS_POOL_SIZE = "archiverThreadPoolSize";
    public static String MAX_SUBMITTER_ENQUEUER_QUEUE_SIZE_PROEPRTY="maxSubmitterEnqueuerQueueSize";
    public static String QUEUES_DB_USER = "QUEUES_DB_USER";
    public static String QUEUES_DB_PASSWORD = "QUEUES_DB_PASSWORD";
    public static String QUEUES_DB_URL = "QUEUES_DB_URL";
    public static String CALL_SEND_SMS_INTERFACE_FLAG_PROPERTY="CALL_SEND_SMS_INTERFACE_FLAG";
    public static String ARCHIVE_BATCH_SIZE_PROPERTY="ARCHIVE_BATCH_SIZE";
    //database defines
    public static HashMap<String, String> databaseConfigs = new HashMap<String, String>();
    public static AtomicBoolean ENGINE_SHUTDOWN_FLAG = new AtomicBoolean(false);
    public static final String SMS_BRIDGE_SHUTDOWN_THREAD_SLEEP_TIME = "SMS_BRIDGE_SHUTDOWN_THREAD_SLEEP_TIME";
    public static final String SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME = "SMS_BRIDGE_DEQUEUER_THREAD_SLEEP_TIME";
    public static final String SMS_BRIDGE_JSON_CONSTRUCTOR_THREAD_SLEEP_TIME = "SMS_BRIDGE_JSON_CONSTRUCTOR_THREAD_SLEEP_TIME";
    public static final String SMS_BRIDGE_ARCHIVER_THREAD_SLEEP_TIME = "SMS_BRIDGE_ARCHIVER_THREAD_SLEEP_TIME";
    public static final String SMS_BRIDGE_HTTP_SUBMITTER_THREAD_SLEEP_TIME = "SMS_BRIDGE_HTTP_SUBMITTER_THREAD_SLEEP_TIME";
    public static final String MONITOR_THREAD_SLEEP_TIME = "MONITOR_THREAD_SLEEP_TIME";
    public static final String SMS_BRIDGE_RELOAD_CONFIGURATION_TIME = "SMS_BRIDGE_RELOAD_CONFIGURATION_TIME";
    public static final String SMS_BRIDGE_HTTP_URL = "SMS_BRIDGE_HTTP_URL";
    public static final String SEND_SMS_SINGLE_HTTP_URL = "SEND_SMS_SINGLE_HTTP_URL";
    public static final String SMS_BRIDGE_HTTP_CONN_TIME_OUT = "SMS_BRIDGE_HTTP_CONN_TIME_OUT";
    public static final String SMS_BRIDGE_HTTP_READ_TIME_OUT = "SMS_BRIDGE_HTTP_READ_TIME_OUT";
    public static String DEQUEUE_BATCH_SIZE = "DEQUEUE_BATCH_SIZE";
    public static String HTTP_BATCH_SIZE = "HTTP_BATCH_SIZE";
    public static String DEQUEUE_SMS_REST_URL = "DEQUEUE_SMS_REST_URL";
    public static String DEQUEUE_WEB_SERVICE_READ_TIMEOUT = "DEQUEUE_READ_TIMEOUT";
    public static String DEQUEUE_WEB_SERVICE_CONNECT_TIMEOUT = "DEQUEUE_CONNECT_TIMEOUT";
    public static String ENQUEUE_WEB_SERVICE_READ_TIMEOUT = "ENQUEUE_READ_TIMEOUT";
    public static String ENQUEUE_WEB_SERVICE_CONNECT_TIMEOUT = "ENQUEUE_CONNECT_TIMEOUT";
    public static String SMS_BRIDGE_SINGLE_SMS_CONNECT_TIMEOUT = "SMS_BRIDGE_SINGLE_SMS_CONNECT_TIMEOUT";
    public static String SMS_BRIDGE_SINGLE_SMS_READ_TIMEOUT = "SMS_BRIDGE_SINGLE_SMS_READ_TIMEOUT";
    public static String ENQUEUE_SMS_REST_URL = "ENQUEUE_SMS_REST_URL";
    public static String ENQUEUER_THREAD_SLEEP_TIME = "ENQUEUER_THREAD_SLEEP_TIME";
    public static String ENQUEUE_BATCH_SIZE = "ENQUEUE_BATCH_SIZE";
    //general defines
    public static String C3P0_QUEUE_SETTING_NAME = "queuesettings";
    public static final int MOD_X = 100;

    public static class HTTP {

        public static String SERVICE_NAME = "ServiceName";
        public static String SMS_NAME = "SMSs";
    }
}
