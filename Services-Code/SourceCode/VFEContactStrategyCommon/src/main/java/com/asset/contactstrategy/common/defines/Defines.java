/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.defines;

import com.asset.contactstrategy.common.models.LookupModel;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import org.apache.poi.hssf.util.HSSFColor;

/**
 *
 * @author mostafa.kashif
 */
public class Defines {

    public static final String WEBLOGIC_CONTEXT_FACTORY_PROPERTY_NAME = "WeblogicInitialContextFactory";
    public static final String WEBLOGIC_PROVIDER_URL_PROPERTY_NAME = "WeblogicProviderUrl";
    public static final String DATA_SOURCE_NAME_PROPERTY_NAME = "DataSourceJndiName";
    public static final String LOG4J_FILE_PATH_PROPERTY_NAME = "log4jpath";
    public static final String LOG4J_DEBUG_LOGGER_PROPERTY_NAME = "debugLoggerName";
    public static final String LOG4J_INFO_LOGGER_PROPERTY_NAME = "infoLoggerName";
    public static final String LOG4J_ERROR_LOGGER_PROPERTY_NAME = "errorLoggerName";
    public static final String LDAP_SERVER_URL_PROPERTY_NAME = "LDAP_SERVER_URL";
    public static final String LDAP_SERVER_DN_PROPERTY_NAME = "LDAP_SERVER_DN";
    public static final String LDAP_USERS_SB_PROPERTY_NAME = "LDAP_USERS_SB";
    public static final String LDAP_AUTHENTICATION_PROPERTY_NAME = "LDAP_AUTHENTICATION";

    public static final String DB_URL_PROPERTY_NAME = "DB_URL";
    public static final String DB_USERNAME_PROPERTY_NAME = "DB_USERNAME";
    public static final String DB_PASSWORD_PROPERTY_NAME = "DB_PASSWORD";
    public static final String DB_CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME = "DB_POOL_MAX_SIZE";
    public static final String DB_DRIVER_CLASS_PROPERTY_NAME = "DB_DRIVER_CLASS";

    public static final String KEY_DWH_PROFILE_STRINGS = "DWH_CUSTOMERS_COLUMNS_STRINGS";
    public static final String KEY_DWH_PROFILE_DATES = "DWH_CUSTOMERS_COLUMNS_DATES";
    public static final String KEY_DWH_PROFILE_NUMBERS = "DWH_CUSTOMERS_COLUMNS_NUMBERS";

    public static String JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY = "JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY";
    public static String SMSC_CONNECTION_MOM_ERROR_SEVERITY = "SMSC_CONNECTION_MOM_ERROR_SEVERITY";
    public static String DATABASE_MOM_ERROR_SEVERITY = "DATABASE_MOM_ERROR_SEVERITY";

    public static String WEBLOGIC_CONTEXT_FACTORY;
    public static String WEBLOGIC_PROVIDER_URL;
    public static String DATA_SOURCE_NAME;
    public static String LOG4J_FILE_PATH;
    // public static String LOG4J_DEBUG_LOGGER_NAME;
    // public static String LOG4J_INFO_LOGGER_NAME;
    // public static String LOG4J_ERROR_LOGGER_NAME;

    // public static String DB_URL;
    // public static String DB_USERNAME;
    // public static String DB_PASSWORD;
    // public static String DB_CONNECTION_POOL_MAX_SIZE;
    // public static String DB_DRIVER_CLASS;
    public static String DB_STATIC_DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

    public static int LANGUAGE_ENGLISH = 0;
    public static int LANGUAGE_ARABIC = 1;

    public static int USER_DELETED = 1;
    public static int USER_NOT_DELETED = 0;

    public static int CAMPAIGNS_CUSTOMERS_SUSPENDED = 1;
    public static int CAMPAIGNS_CUSTOMERS_NOT_SUSPENDED = 0;

    // LDAP configration
    public static HashMap<String, String> LDAPLOOKUP;
    // public static String LDAP_SERVER_URL;
    // public static String LDAP_SERVER_DN;
    // public static String LDAP_USERS_SB;
    // public static String LDAP_AUTHENTICATION;

    public static String DWH_PROFILE_STRINGS;
    public static String DWH_PROFILE_DATES;
    public static String DWH_PROFILE_NUMBERS;

    // Filters operator
    public static final int EQUAL_OPERATOR = 1;
    public static final int NOT_EQUAL_OPERATOR = 2;
    public static final int GREATER_THAN_OPERATOR = 3;
    public static final int LESS_THAN_OPERATOR = 4;
    public static final int GREATER_EQUAL_OPERATOR = 5;
    public static final int LESS_EQUAL_OPERATOR = 6;
    public static final int IN_OPERATOR = 7;
    public static final int BETWEEN_OPERATOR = 8;
    public static final String DATE_FORMAT = "yyyy MM dd";
    public static final String DATE_FORMAT_REVERSE = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "yyyy MM dd  hh:mi:ss am";
    public static final String NOT_BOUNDED_GIFT = "0";
    public static final String BOUNDED_GIFT = "1";
    public static final int ALL_GIFTS = 0;
    public static final int Default_GIFTS = 1;
    public static final int PENDING_STATUS = 1;
    public static final int INT_TRUE = 1;
    public static final int INT_FALSE = 0;
    public static final int NO_TRIGGER_ID = -1;
    public static final String LDAP_TRUE = "TRUE";
    public static final String LDAP_FALSE = "FALSE";
    public static final int TRIGGERS_INPUT_NO = 4;
    public static final String STRING_TRUE = "TRUE";
    public static final String STRING_FALSE = "FALSE";

    public static final String PROPERTIES_FILE_NAME = "VFE_Contact_Strategy";
    public static final String MESSAGES_FILE_NAME = "errormessages";

    // System Type validation flag
    // Reports
    public static final String COUNT = "count";
    public static final String SMSC_NAME = "";
    public static final String SERVICE_NAME = "SERVICE_NAME";
    public static final String SYSTEM_NAME = "";
    public static final String SYSTEM_PRIORITY = "SYSTEM_CATEGORY";
    public static final int HIGH = 1;
    public static final String HIGH_PRIORITY = "High";
    public static final String NORMAL_PRIORITY = "Normal";
    public static final int VIOLATED = 1;

    public static final String ENCRYPTION_KEY_PROPERTY = "encryptionKey";
    public static int runningProjectId;
    public static String ENCRYPTION_KEY;

    //CR 1909 | eslam.ahmed
    public static final String HASH_KEY_PROPERTY = "HASH_KEY";
    // 2595
    public static final String SMS_BULK_UPLOAD_BATCH_SIZE_PROPERTY = "SMSBulkUploadBatchSize";
    public static Long SMS_BULK_UPLOAD_BATCH_SIZE;
    public static final String SMS_BULK_MAX_BATCH_SIZE = "SMS_BULK_MAX_BATCH_SIZE";
    public static final String SMS_BULK_POOL_SIZE = "SMS_BULK_POOL_SIZE";
    public static final String SMS_BULK_MAX_HTTP_HIT_TIMES = "SMS_BULK_MAX_HTTP_HIT_TIMES";
    public static final String SMS_BULK_CONN_TIME_OUT = "SMS_BULK_CONN_TIME_OUT";
    public static final String SMS_BULK_READ_TIME_OUT = "SMS_BULK_READ_TIME_OUT";
    public static final String SMS_BULK_SENDSMSBULKOFFLINE_URL = "SMS_BULK_SENDSMSBULKOFFLINE_URL";
    public static final String SMS_BULK_NUM_OF_TRAILS = "SMS_BULK_NUM_OF_TRAILS";

    public static final String SEND_SMS_SINGLE_HTTP_URL = "SEND_SMS_SINGLE_HTTP_URL";

    public static class DELIVERY_AGGREGATION_PROPERTIES {

        public static final String LOADING_TIME_AFTER_SYSTEM_PROPERTIES = "LOADING_TIME_AFTER_SYSTEM_PROPERTIES";
        public static final String HOURS_BEFORE_TERMINATION_ON_SHTUDOWN = "HOURS_BEFORE_TERMINATION_ON_SHTUDOWN";
        public static final String SLEEP_TIME_PER_SYSTEMPROPERTIES_UPDATE = "SLEEP_TIME_PER_SYSTEMPROPERTIES_UPDATE";
        public static int SLEEP_TIME_PER_SYSTEMPROPERTIES_UPDATE_VALUE;
        public static final String SLEEP_TIME_AFTER_JOBS_UPDATE = "SLEEP_TIME_AFTER_JOBS_UPDATE";
        public static int SLEEP_TIME_AFTER_JOBS_UPDATE_VALUE;
        public static final String SHUTDOWN_FLAG = "SHUTDOWN_FLAG";
        public static final String MONITOR_THREAD_SLEEP_TIME = "MONITOR_THREAD_SLEEP_TIME";
        public static final String SLEEP_TIME_IF_NO_JOB_FOUND = "SLEEP_TIME_IF_NO_JOB_FOUND";
        public static int SLEEP_TIME_IF_NO_JOB_FOUND_VALUE;
        public static final String DAYS_BEFORE_SMS_TIMEOUT = "DAYS_BEFORE_SMS_TIMEOUT";
        public static final String MAX_TIMEOUT_DAYS = "MAX_TIMEOUT_DAYS";
        // public static final String JAVA_QUEUE_MAX_PERCENTAGE =
        // "JAVA_QUEUE_MAX_PERCENTAGE";
        public static final String TIMEOUT_POOL_SIZE = "TIMEOUT_POOL_SIZE";
        public static int TIMEOUT_POOL_SIZE_VALUE;
        public static final String TIMEOUT_POOL_QUEUE_SIZE = "TIMEOUT_POOL_QUEUE_SIZE";
        public static int TIMEOUT_POOL_QUEUE_SIZE_VALUE;
        public static final String SELECTOR_POOL_SIZE = "SELECTOR_POOL_SIZE";
        public static int SELECTOR_POOL_SIZE_VALUE;
        public static final String SELECTOR_POOL_QUEUE_SIZE = "SELECTOR_POOL_QUEUE_SIZE";
        public static int SELECTOR_POOL_QUEUE_SIZE_VALUE;
        public static final String SELECTOR_POOL_FETCH_SIZE = "SELECTOR_POOL_FETCH_SIZE";
        public static int SELECTOR_POOL_FETCH_SIZE_VALUE;
        public static final String DECIDER_POOL_SIZE = "DECIDER_POOL_SIZE";
        public static int DECIDER_POOL_SIZE_VALUE;
        public static final String DECIDER_POOL_QUEUE_SIZE = "DECIDER_POOL_QUEUE_SIZE";
        public static int DECIDER_POOL_QUEUE_SIZE_VALUE;
        public static final String UPDATER_POOL_SIZE = "UPDATER_POOL_SIZE";
        public static int UPDATER_POOL_SIZE_VALUE;
        public static final String UPDATER_POOL_QUEUE_SIZE = "UPDATER_POOL_QUEUE_SIZE";
        public static int UPDATER_POOL_QUEUE_SIZE_VALUE;
        public static final String UPDATER_POOL_BATCH_SIZE = "UPDATER_POOL_BATCH_SIZE";
        public static int UPDATER_POOL_BATCH_SIZE_VALUE;
        public static final String AUTOMATICALLY_DROP_CONCAT_PARTITIONS = "AUTOMATICALLY_DROP_CONCAT_PARTITIONS";
        public static boolean AUTOMATICALLY_DROP_CONCAT_PARTITIONS_VALUE;
        public static final String PARALLEL_HINT_COUNT = "PARALLEL_HINT_COUNT";
        public static int PARALLEL_HINT_COUNT_VALUE;
    }

    public static class SMSC_INTERFACE_PROPERTIES {

        public static final String SHUTDOWN_FLAG = "SHUTDOWN_FLAG";
        public static final String SYSTEM_TYPE_VALIDATION_FLAG = "SYSTEM_TYPE_VALIDATION_FLAG";

        public static String SYSTEM_TYPE_VALIDATION_FLAG_VALUE;

        public static final String AllOW_INSECURE_ACCESS_FLAG = "AllOW_INSECURE_ACCESS_FLAG";

        public static String AllOW_INSECURE_ACCESS_FLAG_VALUE;

        public static final String APPENDING_BIND_OPTIONAL_PARAM_FLAG = "APPENDING_BIND_OPTIONAL_PARAM_FLAG";

        public static String APPENDING_BIND_OPTIONAL_PARAM_FLAG_VALUE;

        public static final String SESSION_TIME_OUT = "SESSION_TIME_OUT";
        public static final String SMSC_INTERFACE_LIVENESS_THREAD_SLEEP_TIME = "SMSC_INTERFACE_LIVENESS_THREAD_SLEEP_TIME";
        public static final String SERVER_PORT = "SERVER_PORT";
        public static final String NUMBER_HANDLERS_PER_SESSION = "NUMBER_HANDLERS_PER_SESSION";
        public static final String REQUESTS_QUEUE_SESSION_PER_SESSION = "REQUESTS_QUEUE_SESSION_PER_SESSION";
        public static final String RESPONSES_QUEUE_SESSION_PER_SESSION = "RESPONSES_QUEUE_SESSION_PER_SESSION";

        public static final String SAFELY_UNBIND_TIMEOUT = "SAFELY_UNBIND_TIMEOUT";
        public static Integer SAFELY_UNBIND_TIMEOUT_VALUE = 1000; // default value

        public static Integer SERVER_PORT_VALUE;
        public static Integer NUMBER_HANDLERS_PER_SESSION_VALUE;
        public static Integer REQUESTS_QUEUE_SESSION_PER_SESSION_VALUE;
        public static Integer RESPONSES_QUEUE_SESSION_PER_SESSION_VALUE;

        public static final String ARCHIVER_QUEUE_SIZE = "ARCHIVER_QUEUE_SIZE";
        public static int ARCHIVER_QUEUE_SIZE_VALUE;
        public static final String LOG_BATCH_SIZE = "LOG_BATCH_SIZE";
        public static int LOG_BATCH_SIZE_VALUE;
        public static final String NO_OF_ARCHIVER_THREADS = "NO_OF_ARCHIVER_THREADS";
        public static int NO_OF_ARCHIVER_THREADS_VALUE;

        public static final String RELOADER_THREAD_SLEEP_TIME = "RELOADER_THREAD_SLEEP_TIME";
        public static final String SMSC_INTERFACE_MONITOR_THREAD_SLEEP_TIME = "SMSC_INTERFACE_MONITOR_THREAD_SLEEP_TIME";
        public static final String ARCHIVER_THREAD_SLEEP_TIME = "ARCHIVER_THREAD_SLEEP_TIME";
        public static final String CS_SMS_ARCHIVER_THREAD_SLEEP_TIME = "CS_SMS_ARCHIVER_THREAD_SLEEP_TIME";

        public static final String CS_SMS_ARCHIVER_QUEUE_SIZE = "CS_SMS_ARCHIVER_QUEUE_SIZE";
        public static int CS_SMS_ARCHIVER_QUEUE_SIZE_VALUE;
        public static final String CS_SMS_LOG_BATCH_SIZE = "CS_SMS_LOG_BATCH_SIZE";
        public static int CS_SMS_LOG_BATCH_SIZE_VALUE;
        public static final String CS_SMS_NO_OF_ARCHIVER_THREADS = "CS_SMS_NO_OF_ARCHIVER_THREADS";
        public static int CS_SMS_NO_OF_ARCHIVER_THREADS_VALUE;

        public static String SMSC_INTERFACE_CS_MAX_HTTP_HITS = "SMSC_INTERFACE_CS_MAX_HTTP_HITS";
        public static String SMSC_INTERFACE_CS_CONNECT_TIMEOUT = "SMSC_INTERFACE_CS_CONNECT_TIMEOUT";
        public static String SMSC_INTERFACE_CS_READ_TIMEOUT = "SMSC_INTERFACE_CS_READ_TIMEOUT";
        public static String SMSC_INTERFACE_WRITE_FUTURE_TIMEOUT = "SMSC_INTERFACE_WRITE_FUTURE_TIMEOUT";
        public static String SEND_SMS_SINGLE_HTTP_URL_VALUE;
        
        public static final String RABBITMQ_CONSUMERS_NUMBER = "RABBITMQ_CONSUMERS_NUMBER";
        public static int RABBITMQ_CONSUMERS_NUMBER_VALUE;
        public static final String DEQUEUED_DELIVER_QUEUE_SIZE = "DEQUEUED_DELIVER_QUEUE_SIZE";
        public static int DEQUEUED_DELIVER_QUEUE_SIZE_VALUE;
        public static String QUEUE_NAME_PREFIX = "QUEUE_NAME_PREFIX";
        public static String DELIVER_QUEUE_NAME_PREFIX = "DELIVER_QUEUE_NAME_PREFIX";
        public static final String WINDOW_SIZE_PER_SESSION = "WINDOW_SIZE_PER_SESSION";
        public static int WINDOW_SIZE_PER_SESSION_VALUE;
        public static final String MAX_SESSIONS = "MAX_SESSIONS";
        public static int MAX_SESSIONS_VALUE;

    }

    public static ResourceBundle messagesBundle;

    public static class DWHELEMENT_DISPLAY_TYPES {

        public static final int NUMERIC = 1;
        public static final int STRING = 2;
        public static final int DATE = 3;
        public static final int MULTI_SELECTION = 4;
    }

    public static class GROUP_TYPES {

        public static final LookupModel CRITERIA_BASED = new LookupModel(1, "Criteria Based");
        public static final LookupModel UPLOADED = new LookupModel(2, "Uploaded");
    }

    public static class DWH_ELEMENTS {

        public static final int CUSTOMER_TYPE = 5;
        public static final int GOVERNMENT = 8;
    }

    public static class DWHELEMENT_DATA_TYPES {

        public static final int NUMERIC = 1;
        public static final int STRING = 2;
        public static final int DATE = 3;
    }

    public static class LK_DISPLAY_TYPE {

        public static final int NUMERIC = 1;
        public static final int STRING = 2;
        public static final int DATE = 3;
        public static final int SMS_PARAM_MAPPING = 5;
        public static final int PROMO_GIFT_EQUATION = 6;
    }

    public static class LK_FILE_STATUS {

        public static final int CREATED = 1;
        public static final int UPDTAED = 2;
        public static final int PROCCESSING = 3;
        public static final int FINISHED = 4;
        public static final int DELETED = 5;
        public static final int FAILED = 6;

    }

    public static class LK_WL_FILE_TYPE {

        public static final int ADD = 1;
        public static final int REMOVE = 2;

    }

    public static class REST {

        public static class ENQUEUE_SMS {

            public static String NAME = "ENQUEUE_SMS";
        }

        public static class ENQUEUE_BRIDGE_SMS {

            public static String NAME = "ENQUEUE_BRIDGE_SMS";
        }
    }

    public static class INTERFACES {

        public static String INSTANCE_ID = "INSTANCE_ID";

        public static String STATUS_FAILED = "Failed";
        public static String STATUS_SUCCESS = "Success";

        public static String ENQUEUE_SMS_REST_URL = "ENQUEUE_SMS_REST_URL";

        public static String ERROR_CODE = "ErrorCode";
        public static String ERROR_DESCRIPTION = "ErrorDesc";

        public static int CAMPAIGN_STATUS_RESUMED = 1;
        public static int CAMPAIGN_STATUS_STOPPED = 2;
        public static int CAMPAIGN_STATUS_PAUSED = 3;

        // public static int SYSTEM_NOT_SUPPORT_ADS = 1;
        // public static int SYSTEM_SUPPORT_ADS = 2;
        public static int SYSTEM_TYPE_MONITOR = 1;
        public static int SYSTEM_TYPE_CONTROL = 2;

        public static int SYSTEM_CATEGORY_NORMAL = 1;
        public static int SYSTEM_CATEGORY_HIGH = 2;
        //
        // public static int DO_NOT_CONTACT_FLAG_TRUE = 1;
        //
        // public static int SYSTEM_ADS_CONSULT_COUNTER_NO = 1;
        // public static int SYSTEM_ADS_CONSULT_COUNTER_YES = 2;
        //
        // public static int GROUP_GUARD_PERIOD_NO = 1;
        // public static int GROUP_GUARD_PERIOD_YES = 2;

        public static int ORIGINATOR_TYPE_SHORT_CODE = 0;
        public static int ORIGINATOR_TYPE_INTERNATIONAL = 1;
        public static int ORIGINATOR_TYPE_ALPHANUMERIC = 2;

        public static byte MESSAGE_TYPE_TEXT_MESSAGE = 0;

        public static byte MESSAGE_PRIORITY_NORMAL = 1;
        public static byte MESSAGE_PRIORITY_HIGH = 2;

        // public static byte DO_NOT_APPLY_NO =1;
        // public static byte DO_NOT_APPLY_YES =2;
        //
        // public static byte Message_Violation_FLAG_NO =1;
        // public static byte Message_Violation_FLAG_YES =2;
        //
        // public static byte CANNOT_SEND_SMS=1;
        // public static byte CAN_SEND_SMS=2;
        public static String MSISDN_VALIDATION_PATTERN = "MSISDN_VALIDATION_PATTERN";
        public static String MSISDN_LENGTH = "MSISDN_LENGTH";
        public static String SINGLE_ENQUEUE_FLAG = "SINGLE_ENQUEUE_FLAG";
        public static String SINGLE_ENQUEUE_FLAG_VALUE;

        public static String GUARD_PERIOD = "GUARD_PERIOD";
        public static String MONTHLY_THRESHOLD = "MONTHLY_THRESHOLD";
        public static String WEEKLY_THRESHOLD = "WEEKLY_THRESHOLD";
        public static String DAILY_THRESHOLD = "DAILY_THRESHOLD";
        public static String GROUP_DESCRIPTION = "GROUP_DESCRIPTION";
        public static String GROUP_ID = "GROUP_ID";
        public static String GROUP_NAME = "GROUP_NAME";
        public static String DO_NOT_CONTACT = "DO_NOT_CONTACT";
        public static String UPDATE_SERVICE_QUOTA_FLAG = "UPDATE_SERVICE_QUOTA_FLAG";
        public static String UPDATE_SERVICE_QUOTA_VALUE;

        public static String AD_GROUP_DAILY_THRESHOLD = "AD_GROUP_DAILY_THRESHOLD";
        public static String AD_GROUP_WEEKLY_THRESHOLD = "AD_GROUP_WEEKLY_THRESHOLD";
        public static String AD_GROUP_MONTHLY_THRESHOLD = "AD_GROUP_MONTHLY_THRESHOLD";

        public static String MAX_ARCHIVING_QUEUE_SIZE = "MAX_ARCHIVING_QUEUE_SIZE";
        public static int MAX_ARCHIVING_QUEUE_SIZE_VALUE;
        public static String MAX_LOGGING_QUEUE_SIZE = "MAX_LOGGING_QUEUE_SIZE";
        public static int MAX_LOGGING_QUEUE_SIZE_VALUE;
        public static String MAX_SMS_SEND_QUEUE_SIZE = "MAX_SMS_SEND_QUEUE_SIZE";
        public static int MAX_SMS_SEND_QUEUE_SIZE_VALUE;
        public static String MAX_SMS_VALIDATION_QUEUE_SIZE = "MAX_SMS_VALIDATION_QUEUE_SIZE";
        public static int MAX_SMS_VALIDATION_QUEUE_SIZE_VALUE;
        public static String MAX_ROLLBACK_SMS_QUEUE_SIZE = "MAX_ROLLBACK_SMS_QUEUE_SIZE";
        public static int MAX_ROLLBACK_SMS_QUEUE_SIZE_VALUE;

        public static String MAX_QUEUE_THREAD_POOL_SIZE = "MAX_QUEUE_THREAD_POOL_SIZE";
        public static int MAX_QUEUE_THREAD_POOL_SIZE_VALUE;
        public static String MAX_LOGGING_THREAD_POOL_SIZE = "MAX_LOGGING_THREAD_POOL_SIZE";
        public static int MAX_LOGGING_THREAD_POOL_SIZE_VALUE;
        public static String MAX_ARCHIVING_THREAD_POOL_SIZE = "MAX_ARCHIVING_THREAD_POOL_SIZE";
        public static int MAX_ARCHIVING_THREAD_POOL_SIZE_VALUE;
        public static String MAX_SMS_VALIDATION_THREAD_POOL_SIZE = "MAX_SMS_VALIDATION_THREAD_POOL_SIZE";
        public static int MAX_SMS_VALIDATION_THREAD_POOL_SIZE_VALUE;
        public static String MAX_ROLLBACK_SMS_THREAD_POOL_SIZE = "MAX_ROLLBACK_SMS_THREAD_POOL_SIZE";
        public static int MAX_ROLLBACK_SMS_THREAD_POOL_SIZE_VALUE;

        public static String ENQUEUE_THREAD_PULL_TIMEOUT = "ENQUEUE_THREAD_PULL_TIMEOUT";
        public static int ENQUEUE_THREAD_PULL_TIMEOUT_VALUE;
        public static String LOGGING_THREAD_PULL_TIMEOUT = "LOGGING_THREAD_PULL_TIMEOUT";
        public static int LOGGING_THREAD_PULL_TIMEOUT_VALUE;
        public static String ARCHIVING_THREAD_PULL_TIMEOUT = "ARCHIVING_THREAD_PULL_TIMEOUT";
        public static int ARCHIVING_THREAD_PULL_TIMEOUT_VALUE;
        public static String VALIDATION_THREAD_PULL_TIMEOUT = "VALIDATION_THREAD_PULL_TIMEOUT";
        public static int VALIDATION_THREAD_PULL_TIMEOUT_VALUE;
        public static String ROLLBACK_THREAD_PULL_TIMEOUT = "ROLLBACK_THREAD_PULL_TIMEOUT";
        public static int ROLLBACK_THREAD_PULL_TIMEOUT_VALUE;

        public static String INTERFACES_MONITOR_THREAD_SLEEP_TIME = "INTERFACES_MONITOR_THREAD_SLEEP_TIME";
        public static String INTERFACE_REPORTS_MONITOR_THREAD_SLEEP_TIME = "INTERFACE_REPORTS_MONITOR_THREAD_SLEEP_TIME";
        public static String RELOAD_THREAD_SLEEP_TIME = "RELOAD_THREAD_SLEEP_TIME";
        public static int RELOAD_THREAD_SLEEP_TIME_VALUE;

        public static String MAX_ENQUEUE_DB_ARRAY_SIZE = "MAX_ENQUEUE_DB_ARRAY_SIZE";
        public static int MAX_ENQUEUE_DB_ARRAY_SIZE_VALUE;
        public static String MAX_LOGGING_DB_ARRAY_SIZE = "MAX_LOGGING_DB_ARRAY_SIZE";
        public static int MAX_LOGGING_DB_ARRAY_SIZE_VALUE;
        public static String MAX_ARCHIVING_DB_ARRAY_SIZE = "MAX_ARCHIVING_DB_ARRAY_SIZE";
        public static int MAX_ARCHIVING_DB_ARRAY_SIZE_VALUE;

        public static String MAX_NUM_OF_RETRIES_QTHREAD = "MAX_NUM_OF_RETRIES_QTHREAD";
        public static int MAX_NUM_OF_RETRIES_QTHREAD_VALUE;
        public static String MAX_NUM_OF_RETRIES_LTHREAD = "MAX_NUM_OF_RETRIES_LTHREAD";
        public static int MAX_NUM_OF_RETRIES_LTHREAD_VALUE;
        public static String MAX_NUM_OF_RETRIES_ATHREAD = "MAX_NUM_OF_RETRIES_ATHREAD";
        public static int MAX_NUM_OF_RETRIES_ATHREAD_VALUE;

        public static String TODAY_COLUMN_NUM = "TODAY_COLUMN_NUM";
        public static String LAST_COLUMN_NUM = "LAST_COLUMN_NUM";
        public static String YESTERDAY_COLUMN_NAME = "YESTERDAY_COLUMN_NAME";

        public static String CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT";
        public static String READ_TIMEOUT = "READ_TIMEOUT";
        public static String QUERY_TIMEOUT = "QUERY_TIMEOUT";
        public static int QUERY_TIMEOUT_VALUE;

        public static String MAX_CONCURRENT_REQUESTS = "MAX_CONCURRENT_REQUESTS";
        public static long MAX_CONCURRENT_REQUESTS_VALUE;

        public static String MAX_QUEUE_OCCUPANCY_RATE = "MAX_QUEUE_OCCUPANCY_RATE";
        public static float MAX_QUEUE_OCCUPANCY_RATE_VALUE;

        public static String MAX_SMS_VALIDATION_QUEUE_OCCUPANCY_RATE = "MAX_SMS_VALIDATION_QUEUE_OCCUPANCY_RATE";
        public static float MAX_SMS_VALIDATION_QUEUE_OCCUPANCY_RATE_VALUE;

        public static String resultSeparator = "resultSeparator";
        public static String resultSeparator_VALUE;
        public static String resultIdentifier = "resultIdentifier";
        public static String resultIdentifier_VALUE;

        public static String SUBMISSION_DATE_FORMAT = "dd-MM-yyyy hh:mm:ss a";

        public static String TEMPLATE_REPLACE_REGEX = ".*\\Q$\\E[0-9]+\\Q$\\E.*";

        public static int FULLY = 1;
        public static int DEDICATED = 2;

        public static class SEND_SMSs {

            public static String NAME = "SendSMSInterface";

            public static class INPUTS {

                public static String ORIGINATOR_MSISDN = "OriginatorMSISDN";
                public static String DESTINATION_MSISDN = "DestinationMSISDN";
                public static String MESSAGE_TEXT = "MessageText";
                public static String ORIGINATOR_TYPE = "OriginatorType";
                public static String MESSAGE_TYPE = "MessageType";
                public static String LANGUAGE = "Language";
                public static String SERVICE_NAME = "SystemName";
                public static String SERVICE_PASSWORD = "SystemPassword"; // CR1901 | eslam.ahmed 
                public static String DO_NOT_APPLY = "DoNotApply";
                public static String MESSAGE_PRORITY = "MessagePriority";
                public static String OPTIONAL_PARAM_1 = "OptionalParam1";
                public static String OPTIONAL_PARAM_2 = "OptionalParam2";
                public static String OPTIONAL_PARAM_3 = "OptionalParam3";
                public static String OPTIONAL_PARAM_4 = "OptionalParam4";
                public static String OPTIONAL_PARAM_5 = "OptionalParam5";
                // CSPhase1.5 | Esmail.Anbar | Adding Template Update
                public static String TEMPLATES_IDS = "TemplatesIds";
                public static String TEMPLATES_PARAMETERS = "TemplatesParameters";
                public static String SUBMISSION_DATE = "SubmissionDate";
                public static String REQUEST_ID = "requestId";
            }
        }

        public static class SEND_SMS_BULK_OFFLINE {

            public static String NAME = "SendSMSBulkOffilneInterface";

            public static class INPUTS {

                public static String SERVICE_NAME = "ServiceName";
                public static String SERVICE_PASSWORD = "ServicePassword"; // CR 1901 | eslam.ahmed
                public static String SMSs = "SMSs";
                public static String SMS = "SMS";
                public static String SUBMISSION_DATE = "SubmissionDate";
                public static String REQUEST_ID = "requestId";
            }

            public static class OUTPUTS {

                public static String CS_MSG_ID = "csMsgId";
                public static String MESSAGE_STATUS = "messageStatus";
            }

            // 2595
            public static class OUTPUTS_WRAPPER {

                public static String SMSs = "SMSs";
                public static String SMS = "SMS";
                public static String ERROR_CODE = "ErrorCode";
                public static String ERROR_DESCRIPTION = "ErrorDescription";
            }
        }

        public static class ADVERTISMENT_CONSULT {

            public static String NAME = "AdvertismentConsultInterface";

            public static class INPUTS {

                public static String DESTINATION_MSISDN = "DestinationMSISDN";
                public static String LANGUAGE = "Language";
                public static String SERVICE_NAME = "SystemName";
            }
        }

        public static class RETRIEVE_SMS_INTERFACE {

            public static String NAME = "RetrieveSmsInterface";
            public static String MAX_NO_OF_ROWS = "MAX_NO_OF_ROWS";

            public static class INPUTS {

                public static String MSISDN = "MSISDN";
                public static String FROM = "from";
                public static String TO = "to";
                public static String SMS_SCRIPT = "SMSScript";
                public static String SERVICE_NAME = "SystemName";
                public static String DAYS_SPAN = "DAYS_SPAN";
                public static String SUBMISSION_DATE = "SubmissionDate";// update2595
                public static String APP_NAME = "ApplicationName";
                public static String SERVICE_PASSWORD = "SystemPassword"; // CR 1901 | eslam.ahmed
            }
        }

        public static class RETRIEVE_MESSAGE_STATUS {

            public static String NAME = "RetrieveMessageStatus";

            public static class INPUTS {

                public static String MSISDN = "MSISDN";
                public static String MESSAGE_ID = "MessageID";
                public static String SERVICE_NAME = "SystemName";
                public static String SUBMISSION_DATE = "SubmissionDate";// update2595
                public static String APP_NAME = "ApplicationName";
                public static String SERVICE_PASSWORD = "SystemPassword"; // CR 1901 | eslam.ahmed

            }
        }

        public static class CONSULT_INTERFACE {

            public static String NAME = "ConsultInterface";

            public static class INPUTS {

                public static String MSISDN = "DestinationMSISDN";
                public static String SERVICE_NAME = "SystemName";

            }
        }
    }

    public static int MOD_X = 100;
    public static int RUN_ID_GROUP_ID = 1;
    public static String SYSTEM_PROPERTIES_RUN_ID_KEY = "ACTIVE_RUN_ID";
    public static String SYSTEM_PROPERTIES_MOD_X_KEY = "MOD_X";
    public static String UPLOAD_FILE_SIZE_LIMIT_KEY = "UPLOAD_FILE_SIZE_LIMIT";
    public static String LANGUAGE_COLUMN = "LANGUAGE_COLUMN";
    public static String ARABIC_LANG = "ARABIC_LANG";
    public static String ENGLISH_LANG = "ENGLISH_LANG";

    public static final String DWH_CUSTOMERS_COLUMNS_STRINGS_PROPERTY_NAME = "DWH_STRINGS_COLUMNS";
    public static final String DWH_CUSTOMERS_COLUMNS_DATES_PROPERTY_NAME = "DWH_DATES_COLUMNS";
    public static final String DWH_CUSTOMERS_COLUMNS_NUMBERS_PROPERTY_NAME = "DWH_NUMBERS_COLUMNS";

    public static int DWH_CUSTOMERS_COLUMNS_STRINGS = 60;
    public static int DWH_CUSTOMERS_COLUMNS_DATES = 60;
    public static int DWH_CUSTOMERS_COLUMNS_NUMBERS = 60;

    public static class VFE_CS_QUEUES_TYPE_LK {

        public static final int PROCEDURE_QUEUES = 2;
        public static final int SMS_SENDER_QUEUES = 1;
    }

    public static class VFE_CS_SMS_H_STATUS_LK {

        public static final int FAILED_CS_RULES = 1;
        public static final int SENT_TO_SMSC = 2;
        public static final int DELIVERED = 3;
        public static final int NOT_DELIVERED = 4;
        public static final int ENQUEUED = 5;
        public static final int TIMED_OUT = 6;
        public static final int EXPIRED = 7;
        public static final int FAILED_ENQUEUE = 8;
        public static final int PARTIAL_DELIVERY = 9;
    }

    public static class VFE_CS_SMSC_MESSAGE_STATUS_LK {

        public static final int ENROUTE = 1;
        public static final int DELIVERED = 2;
        public static final int EXPIRED = 3;
        public static final int DELETED = 4;
        public static final int UNDELIVERABLE = 5;
        public static final int ACCEPTED = 6;
        public static final int UNKNOWN = 7;
        public static final int REJECTED = 8;
    }

    public static class EXCEL_GENERATION {

        public static String ExcelContentType = "file/csv";
        public static String ExcelFileType = ".csv";
        public static Integer RowStart = 0;
        public static Integer ColumnStart = 0;
        public static String NULL_VAL = "null";
        public static String DEFAULT_FONT_NAME = "Arial";
        public static short BLACK_COLOR = HSSFColor.BLACK.index;
        public static short RED_COLOR = HSSFColor.RED.index;
    }

    public static class QUEUE_QUERY {

        public static HashMap<String, String> QUERIES;
        public static String TABLE_NAME = "$TABLE_NAME$";
        public static String OBJECT_NAME = "$OBJECT_NAME$";
        public static String TABLE_SPACE_NAME = "$TABLE_SPACE_NAME$";
        public static String QUEUE_NAME = "$QUEUE_NAME$";
        public static String QUEUE_TBL_QUERY = "query.table_queue";
        public static String QUEUE_CREATE_QUERY = "query.create_queue";
        public static String QUEUE_START_QUERY = "query.start_queue";
        public static String QUEUE_DELETE_QUERY = "query.delete_queue";
        public static String TABLE_NAME_POSTFIX = "_TBL";
        public static String QUEUE_NAME_POSTFIX = "_Q";
        public static String OBJECT_NAME_VALUE = "SMS";
        public static String OBJECT_NAME_SP_VALUE = "SMS_BRIDGE";

    }

    //// Adding A Synchronizable Object For Send SMS, Consult SMS & Adv Consult
    //// Interfaces
    public static final Object smsCustomerStatsSyncObject = new Object();
    public static final Object adsCustomerStatsSyncObject = new Object();

    public static final class SMPP_Defines {

        public static final String DATE_FORMAT_FROM_REQ = "yyyyMMdd";
        public static final String DATE_FORMAT_FOR_QUERY = "MM/dd/yyyy";
    }

    public static final class HTTP {

        public static final String POST = "POST";
    }

    public static class SMS_BRIDGING_ENGINE {

        public static String ENQ_BATCH_PROCEDURE = "ENQ_BATCH";
        public static String DEQ_BATCH_PROCEDURE = "DEQ_BATCH";
    }

    public static class VFE_CS_SMS_BULK_STATUS_LK {

        public static final int READY = 1;
        public static final int PROCESSING = 2;
        public static final int FINISHED = 3;
        public static final int FAILED = 4;

    }

    public static Pattern numaricPattern;

    // C3P0 Properties Names
    public static final String C3P0_ACQUIRE_INCREMENT_PROPERTY_NAME = "C3P0_ACQUIRE_INCREMENT";
    public static final String C3P0_ACQUIRE_RETRY_ATTEMPTS_PROPERTY_NAME = "C3P0_ACQUIRE_RETRY_ATTEMPTS";
    public static final String C3P0_ACQUIRE_RETRY_DELAY_PROPERTY_NAME = "C3P0_ACQUIRE_RETRY_DELAY";
    public static final String C3P0_AUTO_COMMIT_ON_CLOSE_PROPERTY_NAME = "C3P0_AUTO_COMMIT_ON_CLOSE";
    public static final String C3P0_AUTOMATIC_TEST_TABLE_PROPERTY_NAME = "C3P0_AUTOMATIC_TEST_TABLE";
    public static final String C3P0_BREAK_AFTER_ACQUIRE_FAILURE_PROPERTY_NAME = "C3P0_BREAK_AFTER_ACQUIRE_FAILURE";
    public static final String C3P0_CHECKOUT_TIMEOUT_PROPERTY_NAME = "C3P0_CHECKOUT_TIMEOUT";
    public static final String C3P0_CONNECTION_CUSTOMIZER_CLASS_NAME_PROPERTY_NAME = "C3P0_CONNECTION_CUSTOMIZER_CLASS_NAME";
    public static final String C3P0_CONNECTION_TESTERCLASS_NAME_PROPERTY_NAME = "C3P0_CONNECTION_TESTERCLASS_NAME";
    public static final String C3P0_CONTEXT_CLASS_LOADER_SOURCE_PROPERTY_NAME = "C3P0_CONTEXT_CLASS_LOADER_SOURCE";
    public static final String C3P0_DEBUG_UNRETURNED_CONNECTION_STACKTRACES_PROPERTY_NAME = "C3P0_DEBUG_UNRETURNED_CONNECTION_STACKTRACES";
    public static final String C3P0_DESCRIPTION_PROPERTY_NAME = "C3P0_DESCRIPTION";
    public static final String C3P0_DRIVER_CLASS_PROPERTY_NAME = "C3P0_DRIVER_CLASS";
    public static final String C3P0_FACTORY_CLASS_LOCATION_PROPERTY_NAME = "C3P0_FACTORY_CLASS_LOCATION";
    public static final String C3P0_FORCE_IGNORE_UNRESOLVED_TRANSACTIONS_PROPERTY_NAME = "C3P0_FORCE_IGNORE_UNRESOLVED_TRANSACTIONS";
    public static final String C3P0_FORCE_SYNCHRONOUS_CHECKINS_PROPERTY_NAME = "C3P0_FORCE_SYNCHRONOUS_CHECKINS";
    public static final String C3P0_FORCE_USENAME_DDRIVER_CLASS_PROPERTY_NAME = "C3P0_FORCE_USENAME_DDRIVER_CLASS";
    public static final String C3P0_IDLE_CONNECTION_TEST_PERIOD_PROPERTY_NAME = "C3P0_IDLE_CONNECTION_TEST_PERIOD";
    public static final String C3P0_INITIAL_POOL_SIZE_PROPERTY_NAME = "C3P0_INITIAL_POOL_SIZE";
    public static final String C3P0_JDBC_URL_PROPERTY_NAME = "C3P0_JDBC_URL";
    public static final String C3P0_MAX_ADMINISTRATIVE_TASK_TIME_PROPERTY_NAME = "C3P0_MAX_ADMINISTRATIVE_TASK_TIME";
    public static final String C3P0_MAX_CONNECTION_AGE_PROPERTY_NAME = "C3P0_MAX_CONNECTION_AGE";
    public static final String C3P0_MAX_IDLE_TIME_PROPERTY_NAME = "C3P0_MAX_IDLE_TIME";
    public static final String C3P0_MAX_IDLE_TIME_EXCESS_CONNECTIONS_PROPERTY_NAME = "C3P0_MAX_IDLE_TIME_EXCESS_CONNECTIONS";
    public static final String C3P0_MAX_POOL_SIZE_PROPERTY_NAME = "C3P0_MAX_POOL_SIZE";
    public static final String C3P0_MAX_STATEMENTS_PROPERTY_NAME = "C3P0_MAX_STATEMENTS";
    public static final String C3P0_MAX_STATEMENTS_PER_CONNECTION_PROPERTY_NAME = "C3P0_MAX_STATEMENTS_PER_CONNECTION";
    public static final String C3P0_MIN_POOL_SIZE_PROPERTY_NAME = "C3P0_MIN_POOL_SIZE";
    public static final String C3P0_OVERRIDE_DEFAULT_PASSWORD_PROPERTY_NAME = "C3P0_OVERRIDE_DEFAULT_PASSWORD";
    public static final String C3P0_OVERRIDE_DEFAULT_USER_PROPERTY_NAME = "C3P0_OVERRIDE_DEFAULT_USER";
    public static final String C3P0_PASSWORD_PROPERTY_NAME = "C3P0_PASSWORD";
    public static final String C3P0_PREFERRED_TEST_QUERY_PROPERTY_NAME = "C3P0_PREFERRED_TEST_QUERY";
    public static final String C3P0_PRIVILEGES_PAWNED_THREADS_PROPERTY_NAME = "C3P0_PRIVILEGES_PAWNED_THREADS";
    public static final String C3P0_PROPERTY_CYCLE_PROPERTY_NAME = "C3P0_PROPERTY_CYCLE";
    public static final String C3P0_STATEMENT_CACHE_NUM_DEFERRED_CLOSE_THREADS_PROPERTY_NAME = "C3P0_STATEMENT_CACHE_NUM_DEFERRED_CLOSE_THREADS";
    public static final String C3P0_TEST_CONNECTION_ON_CHECKIN_PROPERTY_NAME = "C3P0_TEST_CONNECTION_ON_CHECKIN";
    public static final String C3P0_TEST_CONNECTION_ON_CHECKOUT_PROPERTY_NAME = "C3P0_TEST_CONNECTION_ON_CHECKOUT";
    public static final String C3P0_UNRETURNED_CONNECTION_TIMEOUT_PROPERTY_NAME = "C3P0_UNRETURNED_CONNECTION_TIMEOUT";
    public static final String C3P0_USER_PROPERTY_NAME = "C3P0_USER";
    public static final String C3P0_USER_OVERRIDES_AS_STRING_PROPERTY_NAME = "C3P0_USER_OVERRIDES_AS_STRING";
    public static final String C3P0_USES_TRADITIONAL_REFLECTIVE_PROXIES_PROPERTY_NAME = "C3P0_USES_TRADITIONAL_REFLECTIVE_PROXIES";
    public static final String C3P0_LOGIN_TIMEOUT_PROPERTY_NAME = "C3P0_LOGIN_TIMEOUT";
    public static final String C3P0_CONNECTION_POOL_DATASOURCE_PROPERTY_NAME = "C3P0_CONNECTION_POOL_DATASOURCE";
    public static final String C3P0_DATASOURCE_NAME_PROPERTY_NAME = "C3P0_DATASOURCE_NAME";
    public static final String C3P0_EXTENSIONS_PROPERTY_NAME = "C3P0_EXTENSIONS";
    public static final String C3P0_IDENTITY_TOKEN_PROPERTY_NAME = "C3P0_IDENTITY_TOKEN";
    public static final String C3P0_NUM_HELPER_THREADS_PROPERTY_NAME = "C3P0_NUM_HELPER_THREADS";

    public static final String LIVENESS_THREAD_SLEEP_TIME_PROPERTY_NAME = "LIVENESS_THREAD_SLEEP_TIME";
    ////////////////////////////////////////////////
    public static Integer C3P0_ACQUIRE_INCREMENT;
    public static Integer C3P0_ACQUIRE_RETRY_ATTEMPTS;
    public static Integer C3P0_ACQUIRE_RETRY_DELAY;
    public static Boolean C3P0_AUTO_COMMIT_ON_CLOSE;
    public static String C3P0_AUTOMATIC_TEST_TABLE;
    public static Boolean C3P0_BREAK_AFTER_ACQUIRE_FAILURE;
    public static Integer C3P0_CHECKOUT_TIMEOUT;
    public static String C3P0_CONNECTION_CUSTOMIZER_CLASS_NAME;
    public static String C3P0_CONNECTION_TESTERCLASS_NAME;
    public static String C3P0_CONTEXT_CLASS_LOADER_SOURCE;
    public static String C3P0_DEBUG_UNRETURNED_CONNECTION_STACKTRACES;
    public static Boolean C3P0_DESCRIPTION;
    public static String C3P0_DRIVER_CLASS;
    public static String C3P0_FACTORY_CLASS_LOCATION;
    public static Boolean C3P0_FORCE_IGNORE_UNRESOLVED_TRANSACTIONS;
    public static Boolean C3P0_FORCE_SYNCHRONOUS_CHECKINS;
    public static String C3P0_FORCE_USENAME_DDRIVER_CLASS;
    public static Integer C3P0_IDLE_CONNECTION_TEST_PERIOD;
    public static Integer C3P0_INITIAL_POOL_SIZE;
    public static String C3P0_JDBC_URL;
    public static Integer C3P0_MAX_ADMINISTRATIVE_TASK_TIME;
    public static Integer C3P0_MAX_CONNECTION_AGE;
    public static Integer C3P0_MAX_IDLE_TIME;
    public static Integer C3P0_MAX_IDLE_TIME_EXCESS_CONNECTIONS;
    public static Integer C3P0_MAX_POOL_SIZE;
    public static Integer C3P0_MAX_STATEMENTS;
    public static Integer C3P0_MAX_STATEMENTS_PER_CONNECTION;
    public static Integer C3P0_MIN_POOL_SIZE;
    public static String C3P0_OVERRIDE_DEFAULT_PASSWORD;
    public static String C3P0_OVERRIDE_DEFAULT_USER;
    public static String C3P0_PASSWORD;
    public static String C3P0_PREFERRED_TEST_QUERY;
    public static Boolean C3P0_PRIVILEGES_PAWNED_THREADS;
    public static Integer C3P0_PROPERTY_CYCLE;
    public static Integer C3P0_STATEMENT_CACHE_NUM_DEFERRED_CLOSE_THREADS;
    public static Boolean C3P0_TEST_CONNECTION_ON_CHECKIN;
    public static Boolean C3P0_TEST_CONNECTION_ON_CHECKOUT;
    public static String C3P0_UNRETURNED_CONNECTION_TIMEOUT;
    public static String C3P0_USER;
    public static String C3P0_USER_OVERRIDES_AS_STRING;
    public static Boolean C3P0_USES_TRADITIONAL_REFLECTIVE_PROXIES;
    public static Integer C3P0_LOGIN_TIMEOUT;
    // public static final String C3P0_CONNECTION_POOL_DATASOURCE_PROPERTY_NAME =
    // "C3P0_CONNECTION_POOL_DATASOURCE";
    public static String C3P0_DATASOURCE_NAME;
    // public static final String C3P0_EXTENSIONS_PROPERTY_NAME = "C3P0_EXTENSIONS";
    public static String C3P0_IDENTITY_TOKEN;
    public static Integer C3P0_NUM_HELPER_THREADS;

    public static String QUEUE_C3P0_ACQUIRE_INCREMENT = "QUEUE_C3P0_ACQUIRE_INCREMENT";
    public static String QUEUE_C3P0_CHECKOUT_TIMEOUT = "QUEUE_C3P0_CHECKOUT_TIMEOUT";
    public static String QUEUE_C3P0_INITIAL_POOL_SIZE = "QUEUE_C3P0_INITIAL_POOL_SIZE";
    public static String QUEUE_C3P0_MAX_CONNECTION_AGE = "QUEUE_C3P0_MAX_CONNECTION_AGE";
    public static String QUEUE_C3P0_MAX_IDLE_TIME = "QUEUE_C3P0_MAX_IDLE_TIME";
    public static String QUEUE_C3P0_MAX_POOL_SIZE = "QUEUE_C3P0_MAX_POOL_SIZE";
    public static String QUEUE_C3P0_MAX_STATEMENTS = "QUEUE_C3P0_MAX_STATEMENTS";
    public static String ENQUEUE_C3P0_MIN_POOL_SIZE = "QUEUE_C3P0_MIN_POOL_SIZE";

    public static Integer QUEUE_C3P0_ACQUIRE_INCREMENT_VALUE;
    public static Integer QUEUE_C3P0_CHECKOUT_TIMEOUT_VALUE;
    public static Integer ENQUEUE_C3P0_INITIAL_POOL_SIZE_VALUE;
    public static Integer ENQUEUE_C3P0_MAX_CONNECTION_AGE_VALUE;
    public static Integer ENQUEUE_C3P0_MAX_IDLE_TIME_VALUE;
    public static Integer ENQUEUE_C3P0_MAX_POOL_SIZE_VALUE;
    public static Integer ENQUEUE_C3P0_MAX_STATEMENTS_VALUE;
    public static Integer ENQUEUE_C3P0_MIN_POOL_SIZE_VALUE;

    // messaging mode
    public static String MESSAGING_MODE;

    // messaging mode name
    public static final String MESSAGING_MODE_NAME = "messaging_mode";

    public static final String RABBITMQ = "rabbitmq";
    public static final String ORCLAQ = "orclaq";

    // rabbitmq properties
    public static String RABBITMQ_USERNAME;
    public static String RABBITMQ_PASSWORD;
    public static String RABBITMQ_VIRTUAL_HOST;
    public static String RABBITMQ_HOST;
    public static int RABBITMQ_PORT;
    public static boolean RABBITMQ_AUTO_RECOVERY;

    // rabbitmq properites names
    public static final String RABBITMQ_USERNAME_NAME = "rabbitmq_username";
    public static final String RABBITMQ_PASSWORD_NAME = "rabbitmq_password";
    public static final String RABBITMQ_VIRTUAL_HOST_NAME = "rabbitmq_virtual_host";
    public static final String RABBITMQ_HOST_NAME = "rabbitmq_host";
    public static final String RABBITMQ_PORT_NAME = "rabbitmq_port";
    public static final String RABBITMQ_AUTO_RECOVERY_NAME = "rabbitmq_auto_recovery";

    public static class DequeuerRestWebService {

        public static final String RELOAD_THREAD_SLEEP_TIME_DB_PROEPRTY_NAME = "RELOAD_THREAD_SLEEP_TIME";
        public static final String MONITOR_THREAD_SLEEP_TIME_DB_PROEPRTY_NAME = "MONITOR_THREAD_SLEEP_TIME";
        public static final String MAX_CONCURRENT_REQUESTS_DB_PROEPRTY_NAME = "MAX_CONCURRENT_REQUESTS";

    }

}
