/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.defines;

/**
 *
 * @author Yomna Naser
 */
public class ErrorCodes {

    public static final String UNKOWN_ERROR = "common-0001";
    public static final String DATASOURCE_NOT_SET = "common-0002";
    public static final String DATASOURCE_CLOSE = "common-0003";
    public static final String INIT_ERROR = "common-0004";
    
    //  public static final String UNZIPPPING_ERROR = "common-0006";
    public static final String INTERRUPTED_ERROR = "common-0007";
    public static final String NULL_POINTER_ERROR = "common-0008";

    public static final String ERROR_SQL = "sql-0071";

    public static String MATCHING_INSTANCE_FAILED = "809-100";
    public static String MATCHING_INSTANCE_DELAY = "809-200";
    public static String INSTANCES_MANAGER_DEAD = "809-300";
    public static String STATISTICS_MANAGER_DEAD = "809-400";

    public static String UNIQUE_PRIORITY = "uniquePriority.error";
    public static String DATABASE_ERROR = "web-100";
    public static int TABLESPACE_ERROR = 959;
    public static String TABLESPACE_NOT_EXIST = "web-959";
    public static int QUEUE_TABLE_ALREADY_EXISTS=24001;
    public static int INTEGRITY_ERROR = 2292;
    public static String INTEGRITY_CONSTRAINT_ERROR = "web-2292";
    public static String GET_CONNECTION_ERROR = "web-101";
    public static String CLOSE_CONNECTION_ERROR = "web-102";
    public static String COMMITTING_ERROR = "web-103";
    public static String ROLL_BACK_ERROR = "web-104";
    public static String GENERAL_ERROR = "web-105";

    public static String LOGIN_LDAB_EXCEPTION = "web-200";
    public static String LOGIN_LDAB_TIMEOUT = "web-201";
    public static String GETTING_USER_FROM_SESSION_ERROR = "web-202";
    public static String GETTING_USER = "web-203";
    public static String USER_ALREADY_EXIST="web-204"; // eslam.ahmed | 10-05-2020 | add user existance check 

    public static String UNIQUE_NAME = "uniqueName.error";

    public static String LOAD_DWH_ELEMENTS_DB_ERROR = "web-0002";
    public static String DELET_DWH_ELEMENT_DB_ERROR = "web-0003";
    public static String DWH_ELEMENT_LOV_IN_USE = "web-0004";
    public static String DWH_ELEMENT_IN_USE = "web-0006";

    public static String LOAD_LOOKUPS_DB_ERROR = "web-0005";

    public static String SERVICE_RETREIVE_DB_ERROR = "web-0007";

    
    
    public static final String READING_ENVIRONMENT_VARIABLES_ERROR = "-500";
    public static String HTTP_TIMEOUT_CONNECTION_ERROR = "-501";
    public static String HTTP_CONNECTION_ERROR = "-502";
    /**
     * ******************************Customer Base Importing
     * ********************************
     */
    public static final String UNZIPPPING_ERROR = "common-0006";
    public static final String SHUTDOWN_CUSTOMER_BASE_IMPORTING = "802-450";
    public static final String ERROR_IN_PREPARING_INSERT_STATMENT = "802-451";
    public static final String ERROR_IN_SPLITTING_DWH_LINE = "802-452";
    public static final String ERROR_IN_READING_DWH_FILE = "802-453";
    public static final String ERROR_IN_MOVING_DWH_FILE_TO_HISTORY = "802-454";
    public static final String JAVA_QUEUE_EXCEEDS_PERCENTAGE = "802-457";
    public static final String ERROR_IN_INSERTING_DWH_LINE = "802-458";
    public static final String ERROR_INVALID_MSISDN_FORMATE = "802-459";
    public static final String ERROR_NO_FILES_RECEIVED = "802-460";

    /**
     * * ******************************Group Matching
     */
    public static final String SHUTDOWN_CUSTOMER_SMS_MATCHIN = "804-450";
    public static final String SHUTDOWN_CUSTOMER_ADS_MATCHING = "805-450";
    public static final String SHUTDOWN_CUSTOMER_CAMPAIGN_MATCHING = "806-450";
     ////////////////////////////////////////////////////////

    public static class ADVERTISEMENT_CONSULT_INTERFACE {

        public static int SUCCESS = 0;
        public static int SERVICE_NAME_NOT_DEFINED = -1001;
        public static int IP_NOT_DEFINED = -1002;
        public static int LANGUAGE_NOT_DEFINED = -1003;
        public static int CUSTOMER_HAS_DO_NOT_CONTACT_FLAG = -1004;
        public static int DESTINATION_MSISDN_NOT_VALID = -1006;
        public static int SYSTEM_DOESNT_SUPPORT_ADS = -1008;
        public static int SYSTEM_DOESNT_EXIST_IN_ANY_CAMPAIGN = -1009;
        public static int CUSTOMER_DOESNT_EXIST_IN_ANY_CAMPAIGN = -1010;
        public static int INPUT_IS_MISSING = -1011;
        public static int CANNOT_SEND_DUE_TO_FAILED_CS_RULES = -1012;
        public static int MAX_COMMUNICATION_REACHED_FOR_ALL_CAMPAIGNS = -1013;

        public static int MONTHLY_ADs_THRESHOLD_REACHED_FOR_CUSTOMER = -1021;
        public static int DAILY_ADs_THRESHOLD_REACHED_FOR_CUSTOMER = -1023;
        public static int WEEKLY_ADs_THRESHOLD_REACHED_FOR_CUSTOMER = -1024;
        public static int INVALID_PASSWORD = -1041; // CR 1901 | eslam.ahmed
        public static int HASH_KEY_NOT_FOUND = -1042; // CR 1901 | eslam.ahmed
//        public static int CANNOT_SEND_DUE_GUARD_PERIOD = -1005;
//        public static int CANNOT_SEND_DUE_ADS_THRESHOLD = -1007;
    }

    public static class CONSULT_INTERFACE {

        public static int SUCCESS = 0;
        public static int INPUT_IS_MISSING = -1011;
        public static int SERVICE_NAME_NOT_DEFINED = -1001;
        public static int IP_NOT_DEFINED = -1002;
        public static int DESTINATION_MSISDN_NOT_VALID = -1006;
        public static int SERVICE_QUOTA_VALIDATION_ERROR = -1003;
        public static int CUSTOMER_HAS_DO_NOT_CONTACT_FLAG = -1004;
        public static int GROUP_HAS_DO_NOT_CONTACT_FLAG = -1008;

        public static int MONTHLY_THRESHOLD_REACHED_FOR_CUSTOMER = -1021;
        public static int CUSTOMER_IN_GUARD_PERIOD = -1022;
        public static int DAILY_THRESHOLD_REACHED_FOR_CUSTOMER = -1023;
        public static int WEEKLY_THRESHOLD_REACHED_FOR_CUSTOMER = -1024;
        public static int INVALID_PASSWORD = -1041; // CR 1901 | eslam.ahmed
        public static int HASH_KEY_NOT_FOUND = -1042; // CR 1901 | eslam.ahmed
//        public static int CANNOT_SEND_DUE_GUARD_PERIOD = -1005;
//        public static int CANNOT_SEND_DUE_SMS_THRESHOLD = -1007;
    }

    public static class SEND_SMS {

        public static int SUCCESS = 0;
        public static int CANNOT_SEND_DUE_TO_FAILED_CS_RULES = -1016;
        public static int INPUT_IS_MISSING = -1014;
        public static int SERVICE_NAME_NOT_DEFINED = -1001;
        public static int IP_NOT_DEFINED = -1002;
        public static int SERVICE_QUOTA_VALIDATION_ERROR = -1003;
        public static int MESSAGE_TEXT_NULL_OR_EMPTY = -1004;
        public static int ORIGINATOR_TYPE_NOT_DEFINED = -1005;
        public static int ORIGINATOR_MSISDN_NOT_VALID = -1006;
        public static int DESTINATION_MSISDN_NOT_VALID = -1013;
        public static int LANGUAGE_NOT_DEFINED = -1007;
        public static int MESSAGE_TYPE_NOT_DEFINED = -1008;
        public static int MAX_ALLOWED_SMSs_NOT_VALID = -1009;
        public static int CUSTOMER_HAS_DO_NOT_CONTACT_FLAG = -1010;
        public static int GROUP_HAS_DO_NOT_CONTACT_FLAG = -1012;
        public static int OPERATION_TIMED_OUT_USER_IS_LOCKED = -1020;

        public static int MONTHLY_THRESHOLD_REACHED_FOR_CUSTOMER = -1021;
        public static int CUSTOMER_IN_GUARD_PERIOD = -1022;
        public static int DAILY_THRESHOLD_REACHED_FOR_CUSTOMER = -1023;
        public static int WEEKLY_THRESHOLD_REACHED_FOR_CUSTOMER = -1024;

        public static int INVALID_DO_NOT_APPLY_FLAG = -1025;
        public static int INVALID_MESSAGE_PRIORITY = -1026;
        public static int TEMPLATE_NOT_FOUND = -1027;
        public static int INVALID_TEMPLATES_INPUT = -1028;
        public static int INVALID_ORIGINATOR_OR_ORIGINATOR_TYPE_FOR_DEDICATED_SERVICE = -1029;
        
        public static int ORIGINATOR_MSISDN_IS_MISSING = -1030;
        public static int DESTINATION_MSISDN_IS_MISSING = -1031;
        public static int MESSAGE_TEXT_AND_TEMPLATE_ARE_MISSING = -1032;
        public static int ORIGINATOR_TYPE_IS_MISSING = -1033;
        public static int MESSAGE_TYPE_IS_MISSING = -1034;
        public static int LANGUGAGE_IS_MISSING = -1035;
        public static int SYSTEM_NAME_IS_MISSING = -1036;
        public static int MESSAGE_EXPIRATION_DURATION_WAS_EXCEEDED = -1037;
        public static int TEMPLATE_PARAMETERS_ARE_INVALID = -1038;
        public static int INVALID_TEMPLATE_INPUT = -1039;
        public static int EMPTY_MESSAGE_TEXT = -1040;
        public static int INVALID_PASSWORD = -1041; // CR 1901 | eslam.ahmed
        public static int HASH_KEY_NOT_FOUND = -1042; // CR 1901 | eslam.ahmed
//        public static int CANNOT_SEND_DUE_GUARD_PERIOD = -1011;
//        public static int CANNOT_SEND_DUE_SMS_THRESHOLD = 1012;
//        public static int UNABLE_TO_RETRIEVE_ADS_SCRIPT = -1015;
//        public static int FAILED_TO_ADD_MESSAGE_TO_DATABASE_QUEUE = -1017;
//        public static int FAILED_TO_ADD_MESSAGE_TO_ARCHIVE_QUEUE = -1018;
//        public static int FAILED_TO_SAVE_INTERFACE_LOG_IN_DATABASE = -1019;
//        public static int MAX_COMMUNICATION_REACHED_FOR_ALL_CAMPAIGNS = -1021;
    }

    public static class SEND_SMS_BULK_OFFLINE {

        public static int SUCCESS = 0;
//        public static int CANNOT_SEND_DUE_TO_FAILED_CS_RULES = -1016;
//        public static int INPUT_IS_MISSING = -1014;
//        public static int SERVICE_NAME_NOT_DEFINED = -1001;
//        public static int IP_NOT_DEFINED = -1002;
//        public static int SERVICE_QUOTA_VALIDATION_ERROR = -1003;
//        public static int MESSAGE_TEXT_NULL_OR_EMPTY = -1004;
//        public static int ORIGINATOR_TYPE_NOT_DEFINED = -1005;
//        public static int ORIGINATOR_MSISDN_NOT_VALID = -1006;
//        public static int DESTINATION_MSISDN_NOT_VALID = -1013;
//        public static int LANGUAGE_NOT_DEFINED = -1007;
//        public static int MESSAGE_TYPE_NOT_DEFINED = -1008;
//        public static int MAX_ALLOWED_SMSs_NOT_VALID = -1009;
//        public static int CUSTOMER_HAS_DO_NOT_CONTACT_FLAG = -1010;
//        public static int GROUP_HAS_DO_NOT_CONTACT_FLAG = -1012;
//        public static int OPERATION_TIMED_OUT_USER_IS_LOCKED = -1020;
//
//        public static int MONTHLY_THRESHOLD_REACHED_FOR_CUSTOMER = -1021;
//        public static int CUSTOMER_IN_GUARD_PERIOD = -1022;
//        public static int DAILY_THRESHOLD_REACHED_FOR_CUSTOMER = -1023;
//        public static int WEEKLY_THRESHOLD_REACHED_FOR_CUSTOMER = -1024;
//
//        public static int INVALID_DO_NOT_APPLY_FLAG = -1025;
//        public static int INVALID_MESSAGE_PRIORITY = -1026;
//        public static int TEMPLATE_NOT_FOUND = -1027;
//        public static int INVALID_TEMPLATES_INPUT = -1028;
//        public static int INVALID_ORIGINATOR_OR_ORIGINATOR_TYPE_FOR_DEDICATED_SERVICE = -1029;
//
////        public static int CANNOT_SEND_DUE_GUARD_PERIOD = -1011;
////        public static int CANNOT_SEND_DUE_SMS_THRESHOLD = 1012;
////        public static int UNABLE_TO_RETRIEVE_ADS_SCRIPT = -1015;
////        public static int FAILED_TO_ADD_MESSAGE_TO_DATABASE_QUEUE = -1017;
////        public static int FAILED_TO_ADD_MESSAGE_TO_ARCHIVE_QUEUE = -1018;
////        public static int FAILED_TO_SAVE_INTERFACE_LOG_IN_DATABASE = -1019;
////        public static int MAX_COMMUNICATION_REACHED_FOR_ALL_CAMPAIGNS = -1021;
    }

    public static class RETRIEVE_MESSAGE_STATUS_INTERFACE {

        public static int SUCCESS = 0;
        public static int INVALID_INCOMPLETE_DATA = -3001;
        public static int INVALID_MSISDN = -3002;
        public static int INVALID_IP_ADDRESS = -3003;
        public static int NO_RECORD_FOUND = -3004;
//        public static int INVALID_DAYS_SPAN = -3005;
        public static int SERVICE_NAME_NOT_DEFINED = -3006;
        public static int INVALID_MSISDN_LENGTH = -3007;
        public static int INVALID_MSISDN_PATTERN = -3008;
        public static int MSISDN_CONFIGS_MISSING = -3009;
        public static int APP_QUEUE_NAME_NOT_DEFINED = -3010;
        public static int INVALID_PASSWORD = -3011; // CR 1901 | eslam.ahmed
        public static int HASH_KEY_NOT_FOUND = -3012; // CR 1901 | eslam.ahmed
    }

    public static class RETRIEVE_SMSs_INTERFACE {

        public static int SUCCESS = 0;
        public static int INVALID_INCOMPLETE_DATA = -3001;
        public static int INVALID_MSISDN = -3002;
        public static int INVALID_IP_ADDRESS = -3003;
//        public static int NO_RECORD_FOUND = -3004;
        public static int INVALID_DAYS_SPAN = -3005;
        public static int SERVICE_NAME_NOT_DEFINED = -3006;
        public static int INVALID_PASSWORD = -3011; // CR 1901 | eslam.ahmed
        public static int HASH_KEY_NOT_FOUND = -3012; // CR 1901 | eslam.ahmed
    }

    public static class SMS_BRIDGING_ENGINE {

        //general error codes
        public static int SQL_ERROR_IN_ENQ_SMS_BRDGING_BATCH_DAO = -1;
        public static int SQL_ERROR_IN_DEQ_SMS_BRDGING_BATCH_DAO = -2;
        public static int SHUTDOWN_FLAG_ERROR = -3;
        public static int GENERAL_ERROR_IN_MAIN_SERVICE = -4;
        public static int CONNECTION_CLOSE = -5;
        
        public static int DEQUEUER_STEP = -1001;
        public static int JSON_CONSTRUCTOR_STEP = -1002;
        public static int HTTP_SUBMITTER_STEP = -1003;
        public static int ARCHIVER_STEP = -1004;
        public static int RELOAD_STEP = -1005;
        public static int SHUTDOWN_STEP = -1006;
        public static int THREAD_EXCEPTION = -1007;
    }
    
    
      public static class DEQUEUER_REST_WEB_SERVICE {

        public static int SUCCESS = 0;
        public static int GENERAL_ERROR = -4000;
        public static int QUEUE_NOT_FOUND_ERROR = -1;
    }

    public static class RABBITMQ {

        public static int CONNECTION_ERROR = 0;
        public static int CHANNEL_ERROR = -5001;
        public static int QUEUE_ERROR = -5002;
        public static int ENQUEUE_ERROR = -5003;
        public static int DEQUEUE_ERROR = -5004;
    }

}
