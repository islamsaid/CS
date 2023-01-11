/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.defines;

import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class DBStruct {

    public static String USER_TABLE = "USERS";
    public static String USER_ID = "USER_ID";
    public static String USER_NAME = "USERNAME";
    public static String USER_PASSWORD = "USER_PASSWORD";
    public static String DISPLAY_NAME = "DISPLAY_NAME";
    public static String EMAIL = "EMAIL";
    public static String MOBILE = "MOBILE";
    public static String BIRTHDATE = "BIRTHDATE";
    public static String COUNTRY = "COUNTRY";
    public static String CITY = "CITY";
    public static String ADDRESS = "ADDRESS";
    public static String IS_DELETED = "IS_DELETED";
    public static String TESTING_SEQ = "TESTING_SEQ";
    public static String VFE_CS_SMS_GROUPS_SEQ;

    public static class STORED_PROCEDURES {

        public static class UPDATE_DELIVERY_STATUS {

            public static String UPDATE_DELIVERY_STATUS = "UPDATE_DELIVERY_STATUS";
            public static String UPDATED_RECORDS = "o_UPDATED_RECORDS";
            public static String DELETED_RECORDS = "o_DELETED_RECORDS";
        }

        public static class UPDATE_OTHER_STATUS {

            public static String UPDATE_OTHER_STATUS = "UPDATE_OTHER_STATUS";
            public static String UPDATED_RECORDS = "o_UPDATED_RECORDS";
            public static String DELETED_RECORDS = "o_DELETED_RECORDS";
        }

        public static class UPDATE_TIMEOUT_STATUS {

            public static String UPDATE_TIMEOUT_STATUS = "UPDATE_TIMEOUT_STATUS";
            public static String UPDATED_RECORDS = "o_UPDATED_RECORDS";
            public static String DELETED_RECORDS = "o_DELETED_RECORDS";
        }

        public static class UPD_AND_SEL_SERVICE_QOUTA {

            public static String UPD_AND_SEL_SERVICE_QOUTA = "UPD_AND_SEL_SERVICE_QOUTA";
        }
    }

    public static class APPQUEUE {

        public static String TABLE_NAME = "VFE_CS_APP_QUEUES";
        public static String VERSION_ID = "VERSION_ID";
        public static String APP_ID = "APP_ID";
        public static String APP_NAME = "APP_NAME";
        public static String QUEUE_NAME = "QUEUE_NAME";
        public static String DEQUEUER_POOL_SIZE = "DEQUEUER_POOL_SIZE";
        public static String SENDER_POOL_SIZE = "SENDER_POOL_SIZE";
        public static String SCHEMA_NAME = "SCHEMA_NAME";
        public static String SCHEMA_PASSWORD = "SCHEMA_PASSWORD";
        public static String DATABASE_URL = "DATABASE_URL";
        public static String STATUS = "STATUS";
        public static String CREATOR = "CREATOR";
        public static String LAST_MODIFIED_BY = "LAST_MODIFIED_BY";
        public static String THRESHOLD = "THRESHOLD";
        public static String TIME_WINDOW_FLAG = "TIME_WINDOW_FLAG";
        public static String TIME_WINDOW_FROM_HOUR = "TIME_WINDOW_FROM_HOUR";
        public static String TIME_WINDOW_FROM_MIN = "TIME_WINDOW_FROM_MIN";
        public static String TIME_WINDOW_TO_HOUR = "TIME_WINDOW_TO_HOUR";
        public static String TIME_WINDOW_TO_MIN = "TIME_WINDOW_TO_MIN";
        public static String SEQUENCE = "VFE_CS_APP_QUEUES_SEQ";
        public static String SEQUENCE_SEC = "VFE_CS_APP_QUEUES_SEC_SEQ";
        public static String EDITED_VERSION_DESCRIPTION = "EDITED_VERSION_DESCRIPTION";
        public static String QUEUE_TYPE = "QUEUE_TYPE";

    }

    public static class INSTANCES {

        public static String TABLE_NAME = "VFE_CS_MATCHING_INSTANCES";
        public static String ENGINE_SRC_ID = "ENGINE_SRC_ID";
        public static String INSTANCE_ID = "INSTANCE_ID";
        public static String RUN_ID = "RUN_ID";
        public static String STATUS = "STATUS";
        public static String ACTIVE = "ACTIVE";
        public static String LAST_UPDATE_DATE = "LAST_UPDATE_DATE";
        public static String SUB_PARTITION_START = "SUB_PARTITION_START";
        public static String SUB_PARTITION_END = "SUB_PARTITION_END";
    }

    public static class ENQ_SMS {

        public static String PROCDURE_NAME = "ENQ_SMS";
        public static String ID = "ID";
        public static String APP_NAME = "APP_NAME";
        public static String ORIG_MSISDN = "ORIG_MSISDN";
        public static String DST_MSISDN = "DST_MSISDN";
        public static String MSG_TXT = "MSG_TXT";
        public static String MSG_TYPE = "MSG_TYPE";
        public static String ORIG_TYPE = "ORIG_TYPE";
        public static String LANG_ID = "LANG_ID";
        public static String NBTRIALS = "NBTRIALS";
        public static String CONC_MSG_SEQUEUNCE = "CONC_MSG_SEQUEUNCE";
        public static String CONC_MSG_COUNT = "CONC_MSG_COUNT";
        public static String CONC_SAR_REF_NUM = "CONC_SAR_REF_NUM";
        public static String IP_ADDRESS = "IP_ADDRESS";
        public static String TRACKING_ID = "TRACKING_ID";
        public static String PRIORITY = "PRIORITY";
        public static String CORR = "CORR";
    }

    public static class DWH_ELEMENTS {

        public static final String TBL_NAME = "VFE_CS_DWH_ELEMENTS";
        public static final String SEQ_NAME = "DWHELEMENT_SEQ";
        public static final String ELEMENT_ID = "ELEMENT_ID";
        public static final String NAME = "NAME";
        public static final String DESCRIPTION = "DESCRIPTION";
        public static final String COLUMN_NAME = "COLUMN_NAME";
        public static final String FILE_INDEX = "FILE_INDEX";
        public static final String DATA_TYPE = "DATA_TYPE";
        public static final String DISPLAY_COMM = "DISPLAY_COMM";
        public static final String DISPLAY_NAME = "DISPLAY_NAME";
        public static final String DISPLAY_TYPE = "DISPLAY_TYPE";
        public static final String IS_READ_ONLY = "IS_READ_ONLY";
        public static final String MANDATORY = "MANDATORY";
    }

    public static class DWH_ELEMENT_LOV {

        public static final String TBL_NAME = "VFE_CS_DWH_ELEMENT_LOV";
        public static final String SEQ_NAME = "ELEMENT_LOV_SEQ";
        public static final String VALUE_ID = "VALUE_ID";
        public static final String ELEMENT_ID = "ELEMENT_ID";
        public static final String VALUE_LABEL = "VALUE_LABEL";
    }

    public static class DWH_CUSTOMERS {

        public static String TBL_NAME = "VFE_CS_DWH_CUSTOMERS";
        public static ArrayList<String> DATES;
        public static ArrayList<String> NUMBERS;
        public static ArrayList<String> STRINGS;

        public static final String MSISDN = "MSISDN";
        public static final String SERVICE_CLASS = "SERVICE_CLASS";
        public static final String GOVERNMENT_ID = "GOVERNMENT_ID";
        public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
        public static final String RATE_PLAN = "RATE_PLAN";

//        public static final String C_DWH_PROFILE_DAILY_CAMPAIGN_SMS = "DAILY_CAMPAIGN_SMS";
        public static final String RUN_ID = "RUN_ID";
        public static final String LAST_MSISDN_TWO_DIGITS = "LAST_MSISDN_TWO_DIGITS";
    }

    public static class SMSC {

        public static String TABLE_NAME = "VFE_CS_SMSCS";
        public static String VERSION_ID = "VERSION_ID";
        public static String SMSC_ID = "SMSC_ID";
        public static String SMSC_NAME = "SMSC_NAME";
        public static String IP = "IP";
        public static String PORT = "PORT";
        public static String SYSTEM_TYPE = "SYSTEM_TYPE";
        public static String USERNAME = "USERNAME";
        public static String PASSWORD = "PASSWORD";
        public static String STATUS = "STATUS";
        public static String CREATOR = "CREATOR";
        public static String ID_SEQUENCE = "VFE_CS_SMSC_ID_SEQ";
        public static String SMSC_ID_SEQUENCE = "VFE_CS_SMSC_GROUP_ID_SEQ";
        public static String DESCRIPTION = "DESCRIPTION";
        public static String WINDOW_SIZE = "WINDOW_SIZE";
        public static String THROUGHPUT = "THROUGHPUT";

    }

    public static class SERVICE {

        public static String TABLE_NAME = "VFE_CS_SERVICES";
        public static String SERVICE_ID = "SERVICE_ID";
        public static String SERVICE_NAME = "SERVICE_NAME";
        public static String DAILY_QUOTA = "DAILY_QUOTA";
        public static String INTERFACE_TYPE = "INTERFACE_TYPE";
        public static String SYSTEM_TYPE = "SYSTEM_TYPE";
        public static String DELIVERY_REPORT = "DELIVERY_REPORT";
        public static String SYSTEM_CATEGORY = "SYSTEM_CATEGORY";
        public static String CONSULT_COUNTER = "CONSULT_COUNTER";
        public static String ADS_CONSULT_COUNTER = "ADS_CONSULT_COUNTER";
        public static String SUPPORT_ADS = "SUPPORT_ADS";
        public static String APP_ID = "APP_ID";
        public static String ALLOWED_SMS = "ALLOWED_SMS";
        public static String HAS_WHITELIST = "HAS_WHITELIST";
        public static String STATUS = "STATUS";
        public static String CREATOR = "CREATOR";
        public static String VERSION_ID = "VERSION_ID";
        public static String SEQUENCE = "VFE_CS_SERVICES_SEQ";
        public static String SEQUENCE_SEC = "VFE_CS_SERVICES_SEC_SEQ";
        public static String AUTO_CREATE_FLAG = "AUTO_CREATE_FLAG";
        public static String USE_SYSTEM_LANGUAGE = "USE_SYSTEM_LANGUAGE";
        public static String SMS_PROCEDURE_QUEUE_ID = "SMS_PROCEDURE_QUEUE_ID";
        public static String ORIGINATOR = "ORIGINATOR";
        public static String ORIGINATOR_TYPE = "ORIGINATOR_TYPE";
        public static String PRIVILEGE_LEVEL = "PRIVILEGE_LEVEL";
        public static String MAX_BATCH_SIZE  = "MAX_BATCH_SIZE";
        public static String PASSWORD = "PASSWORD"; // CR 1901 | eslam.ahmed
    }

    public static class SERVICE_WHITELIST {

        public static String TABLE_NAME = "VFE_CS_SERVICE_WHITELIST";
        public static String SERVICE_ID = "SERVICE_ID";
        public static String IP_ADDRESS = "IP_ADDR";
        public static final String ID = "ID";

    }

    public static class APP_QUEUES_SMSC {

        public static final String TABLE_NAME = "VFE_CS_APP_QUEUES_SMSC";
        public static final String ID = "ID";
        public static final String APP_QUEUE_ID = "APP_QUEUE_ID";
        public static final String SMSC_ID = "SMSC_ID";
        public static final String SEQUENCE = "VFE_CS_APP_QUEUES_SMSC_SEQ";
        public static final String CONNECTIVITY_ORDER = "CONNECTIVITY_ORDER";
    }

    public static class VFE_CS_INTERFACE_TYPE {

        public static final String TABLE_NAME = "VFE_CS_INTERFACE_TYPE";
        public static final String INTERFACE_ID = "INTERFACE_ID";
        public static final String INTERFACE_NAME = "INTERFACE_NAME";
    }

    public static class VFE_CS_SERVICE_PRIVILEGES_LK {

        public static final String TABLE_NAME = "VFE_CS_SERVICE_PRIVILEGES_LK";
        public static final String PRIVILEGES_ID = "PRIVILEGE_ID";
        public static final String PRIVILEGES_NAME = "PRIVILEGE_NAME";
    }

    public static class VFE_CS_ORIGINATOR_VALUES_LK {

        public static final String TABLE_NAME = "VFE_CS_ORIGINATOR_VALUES_LK";
        public static final String ORIGINATOR_VALUE_ID = "ORIGINATOR_VALUE_ID";
        public static final String ORIGINATOR_VALUE_NAME = "ORIGINATOR_VALUE_NAME";
    }

    public static class VFE_CS_SERVICE_CATEGORY {

        public static final String TABLE_NAME = "VFE_CS_SERVICE_CATEGORY";
        public static final String CATEGORY_ID = "CATEGORY_ID";
        public static final String CATEGORY_NAME = "CATEGORY_NAME";
    }

    public static class VFE_CS_SERVICE_TYPE {

        public static final String TABLE_NAME = "VFE_CS_SERVICE_TYPE";
        public static final String TYPE_ID = "TYPE_ID";
        public static final String TYPE_NAME = "TYPE_NAME";
    }

    public static class CAMPAIGNS_SERVICES {

        public static final String TABLE_NAME = "VFE_CS_CAMPAIGNS_SERVICES";
        public static final String CAMPAIGN_ID = "CAMPAIGN_ID";
        public static final String SERVICE_ID = "SERVICE_ID";

    }

    public static class VFE_CS_CUSTOMERS_CAMPAIGNS {

        public static final String TABLE_NAME = "VFE_CS_CUSTOMERS_CAMPAIGNS";
        public static final String CAMPAIGN_ID = "CAMPAIGN_ID";
        public static final String MSISDN = "MSISDN";
        public static final String MSISDN_LAST_TWO_DIGITS = "LAST_MSISDN_TWO_DIGITS";
        public static final String RUN_ID = "RUN_ID";
        public static final String SUSPENDED = "SUSPENDED";

    }

    public static class VFE_CS_CAMPAIGNS {

        public static final String TABLE_NAME = "VFE_CS_CAMPAIGNS";
        public static final String VERSION_ID = "VERSION_ID";
        public static final String CAMPAIGN_ID = "CAMPAIGN_ID";
        public static final String CAMPAIGN_NAME = "CAMPAIGN_NAME";
        public static final String CAMPAIGN_DESCRIPTION = "CAMPAIGN_DESCRIPTION";
        public static final String CAMPAIGN_START = "CAMPAIGN_START";
        public static final String CAMPAIGN_END = "CAMPAIGN_END";
        public static final String MAX_COMMUNICATIONS = "MAX_COMMUNICATIONS";
        public static final String FILTER_QUERY = "FILTER_QUERY";
        public static final String MAX_TARGETED = "MAX_TARGETED";
        public static final String CAMPAIGN_PRIORITY = "CAMPAIGN_PRIORITY";
        public static final String ARABIC_SCRIPT = "ARABIC_SCRIPT";
        public static final String ENGLISH_SCRIPT = "ENGLISH_SCRIPT";
        public static final String CAMPAIGN_STATUS = "CAMPAIGN_STATUS";
        public static final String CONTROL_PERCENTAGE = "CONTROL_PERCENTAGE";
        public static String STATUS = "STATUS";
        public static String EDITED_VERSION_DESCRIPTION = "EDITED_VERSION_DESCRIPTION";
        public static String LAST_MODIFIED_BY = "LAST_MODIFIED_BY";
        public static String FILTER_TYPE = "FILTER_TYPE";
        public static String ID_SEQUENCE = "VFE_CS_CAMPAIGN_ID_SEQ";
        public static String CAMPAIGN_ID_SEQUENCE = "VFE_CS_CAMPAIGN_GROUP_ID_SEQ";
    }

    public static class VFE_CS_CAMP_CUST_STATS {

        public static final String TABLE_NAME = "VFE_CS_CAMP_CUST_STATS";
        public static final String ID = "ID";
        public static final String MSISDN = "MSISDN";
        public static final String CAMP_ID = "CAMP_ID";
        public static final String CAMP_COUNT = "CAMP_COUNT";
        public static final String MSISDN_MOD_X = "MSISDN_MOD_X";
        public static final String VFE_CS_CAMP_CUST_STATS_SEQ = "VFE_CS_CAMP_CUST_STATS_SEQ";
    }

    public static class LK_DATA_TYPE {

        public static String TBL_NAME = "VFE_CS_DATA_TYPE_LK";
        public static String DATA_TYPE_ID = "DATA_TYPE_ID";
        public static String DATA_TYPE_LABEL = "DATA_TYPE_LABEL";
    }

    public static class LK_DISPLAY_TYPE {

        public static String TBL_NAME = "VFE_CS_DISPLAY_TYPE_LK";
        public static String DISPLAY_TYPE_ID = "DISPLAY_TYPE_ID";
        public static String DISPLAY_TYPE_LABEL = "DISPLAY_TYPE_LABEL";
    }

    public static class LK_OPERATOR {

        public static final String TBL_NAME = "VFE_CS_OPERATOR_LK";
        public static String OPERATOR_ID = "OPERATOR_ID";
        public static String OPERATOR_VALUE = "OPERATOR_VALUE";
        public static String OPERATOR_EXP_VALUE = "OPERATOR_EXP_VALUE";
    }

    public static class LK_QUEUES_TYPE {

        public static final String TBL_NAME = "VFE_CS_QUEUES_TYPE_LK";
        public static String QUEUE_TYPE_ID = "QUEUE_TYPE_ID";
        public static String QUEUE_TYPE_LABLE = "QUEUE_TYPE";
        public static final int PROCEDURE_QUEUES = 2;
        public static final int SMS_SENDER_QUEUES = 1;

    }

    public static class LK_EQUATION_OPERATOR {

        public static final String TBL_NAME = "VFE_CS_EQUATION_OPERATOR_LK";
        public static String OPERATOR_ID = "OPERATOR_ID";
        public static String OPERATOR_VALUE = "OPERATOR_VALUE";
        public static String OPERATOR_EXP_VALUE = "OPERATOR_EXP_VALUE";
    }

    public static class LK_DISPLAY_TYPE_OPERATORS {

        public static String TBL_NAME = "VFE_CS_DISPLAY_TYPE_OPERATO_LK";
        public static String OPERATOR_ID = "OPERATOR_ID";
        public static String DISPLAY_TYPE_ID = "DISPLAY_TYPE_ID";
    }

    public static class WEB_LOGGING {

        public static String TBL_NAME = "VFE_CS_WEB_LOGGING";
        public static String WEB_LOGGING_SEQ = "VFE_CS_WEB_LOGGING_SEQ";
        public static String ID = "ID";
        public static String PAGE_NAME = "PAGE_NAME";
        public static String OPERATION_NAME = "OPERATION_NAME";
        public static String USER_NAME = "USER_NAME";
        public static String ENTRY_DATE = "ENTRY_DATE";
        public static String STRING_BEFORE = "STRING_BEFORE";
        public static String STRING_AFTER = "STRING_AFTER";

    }

    public static class VFE_CS_FILTER_TYPE_LK {

        public static String TABLE_NAME = "VFE_CS_FILTER_TYPE_LK";
        public static String GROUP_TYPE_ID = "GROUP_TYPE_ID";
        public static String GROUP_TYPE_LABEL = "GROUP_TYPE_LABEL";
    }

    public static class VFE_CS_SERVICES {

        public static String TABLE_NAME = "VFE_CS_SERVICES";
        public static String SERVICE_ID = "SERVICE_ID";
        public static String SERVICE_NAME = "SERVICE_NAME";
        public static String VERSION_ID = "VERSION_ID";
    }

    public static class VFE_CS_MESSAGE_STATUS {

        public static String TABLE_NAME = "VFE_CS_MESSAGE_STATUS_LK";
        public static String MESSAGE_STATUS_ID = "ID";
        public static String MESSAGE_STATUS = "STATUS";
    }

    public static class VFE_CS_SMSC_MESSAGE_STATUS_LK {

        public static String TABLE_NAME = "VFE_CS_SMSC_MESSAGE_STATUS_LK";
        public static String MESSAGE_STATUS_ID = "ID";
        public static String MESSAGE_STATUS = "STATUS";
    }

    public static class VFE_CS_INTERFACE_LOGGING {

        public static final String TABLE_NAME = "VFE_CS_INTERFACE_LOGGING";
        public static final String ID = "ID";
        public static final String INTERFACE_NAME = "INTERFACE_NAME";
        public static final String INTERFACE_INPUT_URL = "INTERFACE_INPUT_URL";
        public static final String INTERFACE_OUTPUT = "INTERFACE_OUTPUT";
        public static final String ENTRY_DATE = "ENTRY_DATE";
        public static final String RESPONSE_TIME = "RESPONSE_TIME";
        public static final String REQUEST_STATUS = "REQUEST_STATUS";
        public static final String TRANS_ID = "TRANS_ID";
        public static final String ID_SEQUENCE = "VFE_CS_INTERFACE_LOGGING_SEQ";
        public static final String REQUEST_BODY = "REQUEST_BODY";
    }
    
    public static class VFE_CS_REST_LOGGING {

        public static final String TABLE_NAME = "VFE_CS_REST_LOGGING";
        public static final String ID = "ID";
        public static final String REST_NAME = "REST_NAME";
        public static final String INPUT_URL = "INPUT_URL";
        public static final String REST_OUTPUT = "REST_OUTPUT";
        public static final String ENTRY_DATE = "ENTRY_DATE";
        public static final String RESPONSE_TIME = "RESPONSE_TIME";
        public static final String REQUEST_STATUS = "REQUEST_STATUS";
        public static final String TRANS_ID = "TRANS_ID";
        public static final String ID_SEQUENCE = "VFE_CS_REST_LOGGING_SEQ";
        public static final String REQUEST_BODY = "REQUEST_BODY";
    }

    public static class VFE_CS_ORIGINATORS_LK {

        public static final String TABLE_NAME = "VFE_CS_ORIGINATORS_LK";
        public static final String ORGINATOR_TYPE = "ORGINATOR_TYPE";
        public static final String ORGINATOR_NAME = "ORGINATOR_NAME";
        public static final String ALLOWED_LENGTH = "ALLOWED_LENGTH";
    }

    public static class VFE_CS_REPORTS_LIST_LK {

        public static final String TABLE_NAME = "VFE_CS_REPORTS_LIST_LK";
        public static final String REPORT_NAME = "REPORT_NAME";
        public static final String REPORT_LINK = "REPORT_LINK";
    }

    public static class VFE_CS_SMS_H_STATUS_LK {

        public static String TABLE_NAME = "VFE_CS_SMS_H_STATUS_LK";
        public static String SMS_H_STATUS_ID = "SMS_H_STATUS_ID";
        public static String SMS_H_STATUS_NAME = "SMS_H_STATUS_NAME";
    }

    public static class VFE_TRIM_SMSC_MSG_ID_PREFIX {

        public static String TABLE_NAME = "VFE_TRIM_SMSC_MSG_ID_PREFIX";
        public static String VFE_TRIM_SMSC_MSG_ID_PREFIX_ID = "ID";
        public static String VFE_TRIM_SMSC_MSG_ID_PREFIX_PREFIX = "PREFIX";
    }

    public static class VFE_CS_SMS_H {

        public static String TABLE_NAME = "VFE_CS_SMS_H";
        public static String ID = "ID";
        public static String VFE_CS_SMS_H_SEQ = "VFE_CS_SMS_H_SEQ";
        public static String MSISDN = "MSISDN";
        public static String CS_MSG_ID = "CS_MSG_ID";
        public static String SENDING_DATE = "SENDING_DATE";
        public static String SUBMISSION_DATE = "SUBMISSION_DATE";
        public static String MESSAGE_TEXT = "MESSAGE_TEXT";
        public static String STATUS = "STATUS";
        public static String DELIVERY_DATE = "DELIVERY_DATE";
        public static String CONCATENATION_COUNT = "CONCATENATION_COUNT";
        public static String DELIVERED_COUNT = "DELIVERED_COUNT";
        public static String OTHER_STATUS_COUNT = "OTHER_STATUS_COUNT";
        public static String SMSC_ID = "SMSC_ID";
        public static String SENDER_ENGINE_ID = "SENDER_ENGINE_ID";
        public static String MESSAGE_TYPE = "MESSAGE_TYPE";
        public static String DO_NOT_APPLY = "DO_NOT_APPLY";
        public static String MESSAGE_VIOLATION_FLAG = "MESSAGE_VIOLATION_FLAG";
        public static String SYSTEM_CATEGORY = "SYSTEM_CATEGORY";
        public static String MESSAGE_CATEGORY = "MESSAGE_CATEGORY";
        public static String MSISDN_MOD_X = "MSISDN_MOD_X";
        public static String SERVICE_ID = "SERVICE_ID";
        public static String ORIGINATOR = "ORIGINATOR";
        public static String OPTIONAL_PARAM_1 = "OPTIONAL_PARAM_1";
        public static String OPTIONAL_PARAM_2 = "OPTIONAL_PARAM_2";
        public static String OPTIONAL_PARAM_3 = "OPTIONAL_PARAM_3";
        public static String OPTIONAL_PARAM_4 = "OPTIONAL_PARAM_4";
        public static String OPTIONAL_PARAM_5 = "OPTIONAL_PARAM_5";
        public static String SMSC_CONCATENATION_COUNT = "SMSC_CONCATENATION_COUNT";
        public static String APP_QUEUE_NAME = "APP_QUEUE_NAME";
        public static String DELIVERY_REPORT = "DELIVERY_REPORT";
    }

    public static class VFE_CS_SMS_CONCAT_H {

        public static String TABLE_NAME = "VFE_CS_SMS_CONCAT_H";
        public static String ID = "ID";
        public static String MSISDN = "MSISDN";
        public static String CS_MSG_ID = "CS_MSG_ID";
        public static String SMSC_MSG_ID = "SMSC_MSG_ID";
        public static String SUBMISSION_DATE = "SUBMISSION_DATE";
        public static String SENDING_DATE = "SENDING_DATE";
        public static String MESSAGE_TEXT = "MESSAGE_TEXT";
        public static String MSISDN_MOD_X = "MSISDN_MOD_X";
        public static String STATUS = "STATUS";
        public static String DELIVERY_DATE = "DELIVERY_DATE";
        public static String SMSC_ID = "SMSC_ID";
        public static String TRIMMED_SMSC_MSG_ID = "TRIMMED_SMSC_MSG_ID";
        public static String VFE_CS_SMS_CONCAT_H_SEQ = "VFE_CS_SMS_CONCAT_H_SEQ";
    }

    public static class VFE_CS_SMS_GROUPS {

        public static String TABLE_NAME = "VFE_CS_SMS_GROUPS";
        public static String VERSION_ID = "VERSION_ID";
        public static String GROUP_ID = "GROUP_ID";
        public static String GROUP_NAME = "GROUP_NAME";
        public static String GROUP_DESCRIPTION = "GROUP_DESCRIPTION";
        public static String GROUP_PRIORITY = "GROUP_PRIORITY";
        public static String DO_NOT_CONTACT = "DO_NOT_CONTACT";
        public static String DAILY_THRESHOLD = "DAILY_THRESHOLD";
        public static String WEEKLY_THRESHOLD = "WEEKLY_THRESHOLD";
        public static String MONTHLY_THRESHOLD = "MONTHLY_THRESHOLD";
        public static String GUARD_PERIOD = "GUARD_PERIOD";
        public static String FILTER_QUERY = "FILTER_QUERY";
        public static String STATUS = "STATUS";
        public static String CREATED_BY = "CREATED_BY";
        public static String GROUP_TYPE_ID = "GROUP_TYPE_ID";
        public static String VFE_CS_SMS_GROUPS_SEQ = "VFE_CS_SMS_GROUPS_SEQ";
        public static String VFE_CS_SMS_GROUPS_SEC_SEQ = "VFE_CS_SMS_GROUPS_SEC_SEQ";
    }

    public static class VFE_CS_ADS_GROUPS {

        public static String TABLE_NAME = "VFE_CS_ADS_GROUPS";
        public static String VERSION_ID = "VERSION_ID";
        public static String GROUP_NAME = "GROUP_NAME";
        public static String GROUP_DESCRIPTION = "GROUP_DESCRIPTION";
        public static String GROUP_PRIORITY = "GROUP_PRIORITY";
        public static String DO_NOT_CONTACT = "DO_NOT_CONTACT";
        public static String DAILY_THRESHOLD = "DAILY_THRESHOLD";
        public static String WEEKLY_THRESHOLD = "WEEKLY_THRESHOLD";
        public static String MONTHLY_THRESHOLD = "MONTHLY_THRESHOLD";
        public static String GUARD_PERIOD = "GUARD_PERIOD";
        public static String FILTER_QUERY = "FILTER_QUERY";
        public static String GROUP_ID = "GROUP_ID";
        public static String STATUS = "STATUS";
        public static String CREATED_BY = "CREATED_BY";
        public static String GROUP_TYPE_ID = "GROUP_TYPE_ID";
        public static String VFE_CS_ADS_GROUPS_SEQ = "ADS_GROUPS_SEQ";
        public static String VFE_CS_ADS_GROUPS_SEC_SEQ = "ADS_GROUPS_SEC_SEQ";
    }

    public static class VFE_CS_SMS_GROUP_FILES {

        public static final String TABLE_NAME = "VFE_CS_SMS_GROUPS_FILES";
        public static final String GROUP_FILE_ID = "GROUP_FILE_ID";
        public static final String FILE_BYTES = "FILE_BYTES";
        public static final String GROUP_ID = "GROUP_ID";
        public static final String GROUP_FILE_NAME = "GROUP_FILE_NAME";
        public static final String FILE_STATUS_ID = "FILE_STATUS_ID";
        public static final String LAST_STATUS_MODIEFIED = "LAST_STATUS_MODIEFIED";
        public static final String GROUP_FILE_TYPE_ID = "GROUP_FILE_TYPE_ID";
        public static final String VFE_CS_SMS_GROUP_FILE_ID_SEQ = "SMS_GROUP_FILE_ID_SEQ";
    }

    public static class VFE_CS_ADS_GROUP_FILES {

        public static final String TABLE_NAME = "VFE_CS_ADS_GROUPS_FILES";
        public static final String GROUP_FILE_ID = "GROUP_FILE_ID";
        public static final String FILE_BYTES = "FILE_BYTES";
        public static final String GROUP_ID = "GROUP_ID";
        public static final String GROUP_FILE_NAME = "GROUP_FILE_NAME";
        public static final String FILE_STATUS_ID = "FILE_STATUS_ID";
        public static final String LAST_STATUS_MODIEFIED = "LAST_STATUS_MODIEFIED";
        public static final String GROUP_FILE_TYPE_ID = "GROUP_FILE_TYPE_ID";
        public static final String VFE_CS_ADS_GROUP_FILE_ID_SEQ = "ADS_GROUP_FILE_ID_SEQ";
    }

    public static class VFE_CS_CUSTOMERS_CONFIGURATIONS {

        public static final String TABLE_NAME = "VFE_CS_CUSTOMER_CONFIGURATIONS";
        public static final String MSISDN = "MSISDN";
        public static final String LAST_TWO_DIGITS = "LAST_TWO_DIGITS";
        public static final String DO_NOT_CONTACT = "DO_NOT_CONTACT";
        public static final String DAILY_THRESHOLD_SMS = "DAILY_THRESHOLD_SMS";
        public static final String WEEKLY_THRESHOLD_SMS = "WEEKLY_THRESHOLD_SMS";
        public static final String MONTHLY_THRESHOLD_SMS = "MONTHLY_THRESHOLD_SMS";
        public static final String DAILY_THRESHOLD_ADS = "DAILY_THRESHOLD_ADS";
        public static final String WEEKLY_THRESHOLD_ADS = "WEEKLY_THRESHOLD_ADS";
        public static final String MONTHLY_THRESHOLD_ADS = "MONTHLY_THRESHOLD_ADS";
    }

    public static class VFE_CS_SMS_CUST_STATISTICS {

        public static final String TABLE_NAME = "VFE_CS_SMS_CUST_STATISTICS";
        public static final String ID = "ID";
        public static final String MSISDN = "MSISDN";
        public static final String MSISDN_MOD_X = "MSISDN_MOD_X";
        public static final String GUARD_START_DATE = "GUARD_START_DATE";
        public static final String GUARD_END_DATE = "GUARD_END_DATE";
        public static final String VFE_CS_SMS_CUST_STATISTICS_SEQ = "VFE_CS_SMS_CUST_STAT_SEQ";
    }

    public static class VFE_CS_ADS_CUST_STATISTICS {

        public static final String TABLE_NAME = "VFE_CS_ADS_CUST_STATISTICS";
        public static final String ID = "ID";
        public static final String MSISDN = "MSISDN";
        public static final String MSISDN_MOD_X = "MSISDN_MOD_X";
        public static final String VFE_CS_ADS_CUST_STAT_SEQ = "VFE_CS_ADS_CUST_STAT_SEQ";
    }

    public static class VFE_CS_CUSTOMERS_STATISTICS {

        public static final String TABLE_NAME = "VFE_CS_CUSTOMERS_STATISTICS";
        public static final String SEQ = "VFE_CS_CUSTOMERS_STAT_SEQ";
        public static final String ID = "ID";
        public static final String MSGS_DATE = "MSGS_DATE";
        public static final String MSG_COUNT = "MSG_COUNT";
        public static final String ADS_COUNT = "ADS_COUNT";
        public static final String MSISDN = "MSISDN";
        public static final String MSISDN_MOD_X = "MSISDN_MOD_X";

    }

    public static class VFE_CS_CUSTOMERS_GROUPS {

        public static final String TABLE_NAME = "VFE_CS_CUSTOMERS_GROUPS";
        public static final String RUN_ID = "RUN_ID";
        public static final String MSISDN = "MSISDN";
        public static final String LAST_TWO_DIGITS = "LAST_MSISDN_TWO_DIGITS";
        public static final String ADS_GROUP_ID = "GROUP_ID";
    }

    public static class VFE_CS_FILE_STATUS_LK {

        public static final String TABLE_NAME = "VFE_CS_FILE_STATUS_LK";
        public static final String FILE_STATUS_ID = "FILE_STATUS_ID";
        public static final String STATUS_NAME = "STATUS_NAME";
    }

    public static class VFE_CS_CUSTOMERS_ADS_GROUPS {

        public static final String TABLE_NAME = "VFE_CS_CUSTOMERS_ADS_GROUPS";
        public static final String RUN_ID = "RUN_ID";
        public static final String MSISDN = "MSISDN";
        public static final String LAST_TWO_DIGITS = "LAST_MSISDN_TWO_DIGITS";
        public static final String ADS_GROUP_ID = "ADS_GROUP_ID";
    }

    public static class LK_GOVERNMENT {

        public static String TBL_NAME = "VFE_CS_GOVERNMENT_LK";
        public static String GOVERNMENT_ID = "GOVERNMENT_ID";
        public static String GOVERNMENT_NAME = "GOVERNMENT_NAME";
    }

    public static class LK_CUSTOMER_TYPE {

        public static String TBL_NAME = "VFE_CS_CUSTOMER_TYPE_LK";
        public static String CUSTOMER_TYPE_ID = "CUSTOMER_TYPE_ID";
        public static String CUSTOMER_TYPE_LABEL = "CUSTOMER_TYPE_LABEL";
    }

    public static class VFE_CS_USERS {

        public static final String TABLE_NAME = "VFE_CS_USERS";
        public static final String USER_ID = "USER_ID";
        public static final String USER_NAME = "USER_NAME";
        public static final String USER_TYPE = "USER_TYPE";
        public static final String USERS_SEQ = "VFE_CS_USERS_SEQ";
        public static final String USER_DELETED = "DELETED";
    }

    public static class VFE_CS_SMS_ELEMENT_FILTERS {

        public static final String TABLE_NAME = "VFE_CS_SMS_ELEMENT_FILTERS";
        public static final String FILTER_ID = "FILTER_ID";
        public static final String ELEMENT_ID = "ELEMENT_ID";
        public static final String OPERATOR_ID = "OPERATOR_ID";
        public static final String FIRST_OPERAND = "FIRST_OPERAND";
        public static final String SECOND_OPERAND = "SECOND_OPERAND";
        public static final String GROUP_ID = "GROUP_ID";
        public static final String ELEMENT_FILTER_SEQ = "SMS_ELEMENT_FILTERS_SEQ";
    }

    public static class VFE_CS_SMS_ELEMENT_FILTER_LOV {

        public static final String TABLE_NAME = "VFE_CS_SMS_ELEMENT_FILTER_LOV";
        public static final String FILTER_VALUE_ID = "FILTER_VALUE_ID";
        public static final String VALUE_ID = "VALUE_ID";
        public static final String FILTER_ID = "FILTER_ID";
        public static final String ELEMENT_FILTER_LOV_SEQ = "SMS_ELEMENT_FILTER_LOV_SEQ";
    }

    public static class VFE_CS_ADS_ELEMENT_FILTERS {

        public static final String TABLE_NAME = "VFE_CS_ADS_ELEMENT_FILTERS";
        public static final String FILTER_ID = "FILTER_ID";
        public static final String ELEMENT_ID = "ELEMENT_ID";
        public static final String OPERATOR_ID = "OPERATOR_ID";
        public static final String FIRST_OPERAND = "FIRST_OPERAND";
        public static final String SECOND_OPERAND = "SECOND_OPERAND";
        public static final String GROUP_ID = "GROUP_ID";
        public static final String ELEMENT_FILTER_SEQ = "ADS_ELEMENT_FILTERS_SEQ";
    }

    public static class VFE_CS_ADS_ELEMENT_FILTER_LOV {

        public static final String TABLE_NAME = "VFE_CS_ADS_ELEMENT_FILTER_LOV";
        public static final String FILTER_VALUE_ID = "FILTER_VALUE_ID";
        public static final String VALUE_ID = "VALUE_ID";
        public static final String FILTER_ID = "FILTER_ID";
        public static final String ELEMENT_FILTER_LOV_SEQ = "ADS_ELEMENT_FILTER_LOV_SEQ";
    }

    public static class VFE_CS_CAM_ELEMENT_FILTERS {

        public static final String TABLE_NAME = "VFE_CS_CAM_ELEMENT_FILTERS";
        public static final String FILTER_ID = "FILTER_ID";
        public static final String ELEMENT_ID = "ELEMENT_ID";
        public static final String OPERATOR_ID = "OPERATOR_ID";
        public static final String FIRST_OPERAND = "FIRST_OPERAND";
        public static final String SECOND_OPERAND = "SECOND_OPERAND";
        public static final String CAMPAIGN_ID = "CAMPAIGN_ID";
        public static final String ELEMENT_FILTER_SEQ = "CAM_ELEMENT_FILTERS_SEQ";
    }

    public static class VFE_CS_CAM_ELEMENT_FILTER_LOV {

        public static final String TABLE_NAME = "VFE_CS_CAM_ELEMENT_FILTER_LOV";
        public static final String FILTER_VALUE_ID = "FILTER_VALUE_ID";
        public static final String VALUE_ID = "VALUE_ID";
        public static final String FILTER_ID = "FILTER_ID";
        public static final String ELEMENT_FILTER_LOV_SEQ = "CAM_ELEMENT_FILTER_LOV_SEQ";
    }

    public static class VFE_CS_CAMPAIGNS_FILES {

        public static final String TABLE_NAME = "VFE_CS_CAMPAIGNS_FILES";
        public static final String CAMPAIGN_FILE_ID = "CAMPAIGN_FILE_ID";
        public static final String FILE_BYTES = "FILE_BYTES";
        public static final String CAMPAIGN_ID = "CAMPAIGN_ID";
        public static final String CAMPAIGN_FILE_NAME = "CAMPAIGN_FILE_NAME";
        public static final String FILE_STATUS_ID = "FILE_STATUS_ID";
        public static final String LAST_STATUS_MODIEFIED = "LAST_STATUS_MODIEFIED";
        // public static final String GROUP_FILE_TYPE_ID = "GROUP_FILE_TYPE_ID";
        public static final String CAMPAIGN_FILE_ID_SEQ = "CAMPAIGN_FILE_ID_SEQ";
    }

    public static class SYSTEM_PROPERITES {

        public static String TABLE_NAME = "VFE_CS_SYSTEM_PROPERTIES";
        public static String ITEM_ID = "ITEM_ID";
        public static String GROUP_ID = "GROUP_ID";
        public static String ITEM_VALUE = "ITEM_VALUE";
        public static String DESCRIPTION = "DESCRIPTION";
        public static String GROUP_NAME = "GROUP_NAME";
        public static String ITEM_KEY = "ITEM_KEY";

    }

    public static class VFE_CS_MOM_ERRORS {

        public static String TABLE_NAME = "VFE_CS_MOM_ERRORS";
        public static String ERROR_ID = "ERROR_ID";
        public static String ENTRY_DATE = "ENTRY_DATE";
        public static String SRC_ID = "SRC_ID";
        public static String PERCEIVED_SEVERITY = "PERCEIVED_SEVERITY";
        public static String MODULE_NAME = "MODULE_NAME";
        public static String FUNCTION_NAME = "FUNCTION_NAME";
        public static String ENGINE_SRC_ID = "ENGINE_SRC_ID";
        public static String ERROR_MESSAGE = "ERROR_MESSAGE";
        public static String ERROR_PARAMS = "ERROR_PARAMS";
        public static String VFE_CS_MOM_ERRORS_SEQ = "VFE_CS_MOM_ERRORS_SEQ";
    }

    public static class VFE_CS_SERVICE_HISTORY {

        public static final String TABLE_NAME = "VFE_CS_SERVICE_HISTORY";
        public static final String SERVICE_ID = "SERVICE_ID";
        public static final String SENDING_DATE = "SENDING_DATE";
        public static final String MONITOR_COUNTER = "MONITOR_COUNTER";
        public static final String DO_NOT_APPLY_COUNTER = "DO_NOT_APPLY_COUNTER";
        public static final String DAILY_QUOTA_COUNTER = "DAILY_QUOTA_COUNTER";
        public static final String SERVICE_HISTORY_ID = "SERVICE_HISTORY_ID";
        public static final String VFE_CS_SERVICE_HISTORY_SEQ = "VFE_CS_SERVICE_HISTORY_SEQ";
    }

    public static class VFE_CS_MENU {

        public static String TABLE_NAME = "VFE_CS_MENU";
        public static String ID = "ID";
        public static String PAGE_NAME = "PAGE_NAME";
        public static String PAGE_URL = "PAGE_URL";
        public static String USER_TYPE = "USER_TYPE";
        public static String ICO_STYLE_CLASS = "ICO_STYLE_CLASS";

    }

    public static class HANDLE_CUST_SMS_COUNTERS_PROC {

        public static String PROCDURE_NAME = "GET_CUSTOMER_SMS_COUNTER";
        public static String MSISDN = "MSISDN";
        public static String DAILY_QUOTA = "DAILY_QUOTA";
        public static String WEEKLY_QUOTA = "WEEKLY_QUOTA";
        public static String MONTHLY_QUOTA = "MONTHLY_QUOTA";
        public static String MESSAGE_CATEGORY = "MESSAGE_CATEGORY";
        public static String GUARD_PERIOD = "GUARD_PERIOD";
        public static String CAN_SEND_SMS = "CAN_SEND_SMS";
        public static String MSG_VIOLAT_FLAG = "MSG_VIOLAT_FLAG";
        public static String v_LOG = "v_LOG";
    }

    public static class VFE_CS_SENDING_SMS_INSTANCES {

        public static String TABLE_NAME = "VFE_CS_SENDING_SMS_INSTANCES";
        public static String INSTANCE_ID = "INSTANCE_ID";
        public static String SENDING_SMS_SHUTDOWN_FLAG = "SENDING_SMS_SHUTDOWN_FLAG";
        public static String INSTANCE_APP_QUEUES = "INSTANCE_APP_QUEUES";
        public static String RELOAD_COUNTER = "RELOAD_COUNTER";
    }

    public static class VFE_CS_INTERFACES_INSTANCES {

        public static String TABLE_NAME = "VFE_CS_INTERFACES_INSTANCES";
        public static String INSTANCE_ID = "INSTANCE_ID";
        public static String RELOAD_COUNTER = "RELOAD_COUNTER";
    }

    public static class HANDLE_CUST_ADS_COUNTERS_PROC {

        public static String PROCDURE_NAME = "GET_CUSTOMER_ADS_COUNTER";
        public static String MSISDN = "MSISDN";
        public static String DAILY_QUOTA = "DAILY_QUOTA";
        public static String WEEKLY_QUOTA = "WEEKLY_QUOTA";
        public static String MONTHLY_QUOTA = "MONTHLY_QUOTA";
        public static String MESSAGE_CATEGORY = "MESSAGE_CATEGORY";
        public static String CAN_SEND_SMS = "CAN_SEND_SMS";
    }

    public static class VFE_CS_USERS_TYPES_LK {

        public static String TABLE_NAME = "VFE_CS_USERS_TYPES_LK";
        public static String ID = "ID";
        public static String TYPE_NAME = "TYPE_NAME";
    }

    public static class VFE_CS_TEMPLATES_LK {

        public static String TABLE_NAME = "VFE_CS_TEMPLATES_LK";
        public static String TEMPLATE_ID = "TEMPLATE_ID";
        public static String TEMPLATE_DECRIPTION = "TEMPLATE_DECRIPTION";
        public static String ARABIC_SCRIPT = "ARABIC_SCRIPT";
        public static String ENGLISH_SCRIPT = "ENGLISH_SCRIPT";
        public static String EXPIRATION_DURATION = "EXPIRATION_DURATION";
    }

    public static class VFE_CS_SMS_BULK_FILES_H {

        public static String TABLE_NAME = "VFE_CS_SMS_BULK_FILES_H";
        public static String ID = "ID";
        public static String FILE_NAME = "FILE_NAME";
        public static String TOTAL_RECORDS = "TOTAL_RECORDS";
        public static String SUCCESS_RECORDS = "SUCCESS_RECORDS";
        public static String FAILED_RECORDS = "FAILED_RECORDS";
        public static String USER_ID = "USER_ID";
        public static String ENTRY_DATE = "ENTRY_DATE";
        public static String RECORDS_INSERTION_STATUS = "RECORDS_INSERTION_STATUS";
        public static String ID_SEQ = "VFE_CS_SMS_BULK_FILES_SEQ";
    }

    public static class VFE_CS_SMS_BRIDGING_INSTANCES {//2595 sms bridging engine

        public static String TABLE_NAME = "VFE_CS_SMS_BRIDGING_INSTANCES";
        public static String INSTANCE_ID = "INSTANCE_ID";
        public static String SMS_BRIDGING_SHUTDOWN_FLAG = "SMS_BRIDGING_SHUTDOWN_FLAG";
        public static String INSTANCE_APP_QUEUES = "INSTANCE_APP_QUEUES";
        public static String RELOAD_COUNTER = "RELOAD_COUNTER";
    }

    public static class VFE_CS_PROCEDURES_H {//2595 sms bridging engine

        public static String TABLE_NAME = "VFE_CS_PROCEDURES_H";
        public static String CS_MSG_ID = "CS_MSG_ID";
        public static String MSISDN = "MSISDN";
        public static String SUBMISSION_DATE = "SUBMISSION_DATE";
        public static String MESSAGE_TEXT = "MESSAGE_TEXT";
        public static String MSISDN_MOD_X = "MSISDN_MOD_X";
        public static String STATUS = "STATUS";
        public static String MESSAGE_TYPE = "MESSAGE_TYPE";
        public static String DO_NOT_APPLY = "DO_NOT_APPLY";
        public static String MESSAGE_CATEGORY = "MESSAGE_CATEGORY";
        public static String SERVICE_ID = "SERVICE_ID";
        public static String ORIGINATOR = "ORIGINATOR";
        public static String OPTIONAL_PARAM_1 = "OPTIONAL_PARAM_1";
        public static String OPTIONAL_PARAM_2 = "OPTIONAL_PARAM_2";
        public static String OPTIONAL_PARAM_3 = "OPTIONAL_PARAM_3";
        public static String OPTIONAL_PARAM_4 = "OPTIONAL_PARAM_4";
        public static String OPTIONAL_PARAM_5 = "OPTIONAL_PARAM_5";
        public static String APP_QUEUE_NAME = "APP_QUEUE_NAME";
        public static String ID = "ID";
        public static String MSG_ID = "MSG_ID";
        public static String SYSTEMNAME = "SYSTEMNAME";
        public static String ORIGTYPE = "ORIGTYPE";
        public static String LANGID = "LANGID";
        public static String MESSAGE_PRIORITY = "MESSAGE_PRIORITY";
        public static String TEMPLATE_IDS = "TEMPLATE_IDS";
        public static String TEMPLATE_PARAMETERS = "TEMPLATE_PARAMETERS";

    }
    
    
     public static class VFE_CS_SMSC_INT_CLIENT {

        public static final String TABLE_NAME = "VFE_CS_SMSC_INT_CLIENT";
        public static final String ID = "ID";
        public static final String PASSWORD = "PASSWORD";
        public static final String SYSTEM_ID = "SYSTEM_ID";
        public static final String SYSTEM_NAME = "SYSTEM_NAME";
        public static final String SYSTEM_TYPE = "SYSTEM_TYPE";
        public static final String CS_CALLED_THREADS = "CS_CALLED_THREADS";
        public static final String CS_SUBMIT_SMS_QUEUE_SIZE = "CS_SUBMIT_SMS_QUEUE_SIZE";

    }
     
      public static class VFE_CS_SMSC_INT_LOG {

        public static final String TABLE_NAME = "VFE_CS_SMSC_INT_LOG";
        public static final String ID = "ID";
        public static final String SESSION_ID = "SESSION_ID";
        public static final String COMMAND_NAME = "COMMAND_NAME";
        public static final String COMMAND_TYPE = "COMMAND_TYPE";
        public static final String SEQUENCE_NUMBER = "SEQUENCE_NUMBER";
        public static final String PDU = "PDU";
        public static final String PARSE = "PARSE";
        public static final String COMMAND_STATUS = "COMMAND_STATUS";
        public static final String REQUEST_ERROR = "REQUEST_ERROR";
        public static final String LOG_SEQ = "VFE_CS_SMSC_INT_LOG_SEQ";


    }
      

	public static class VFE_CS_SMSC_INT_H {
		public static final String TABLE_NAME = "VFE_CS_SMSC_INT_H";
		public static final String ID = "ID";
		public static final String SESSION_ID = "SESSION_ID";
		public static final String SMSC_MESSAGE_ID = "SMSC_MESSAGE_ID";
		public static final String REQUEST = "REQUEST";
		public static final String RESPONSE = "RESPONSE";
		public static final String EXECUTION_TIME = "EXECUTION_TIME";
		public static final String REQUEST_DATE = "REQUEST_DATE";
		public static final String REQUEST_ERROR = "REQUEST_ERROR";
		public static final String LOG_SEQ = "VFE_CS_SMSC_INT_H_SEQ";

	}

    ////////////////////////// DWH Profile Procedures ///////////////////////////////
    public static final String PRD_ADD_PROFILE_PARTITION = "ADD_PROFILE_PARTITION";
    public static final String PRD_DROP_PROFILE_PARTITION = "DROP_PROFILE_PARTITION";
    ////////////////////////// Groups Matching Procedures ///////////////////////////////
    public static final String PRD_ADD_SMS_GROUPS_CUSTOMERS_PARTITION = "ADD_GROUPS_CUST_PARTITION";
    public static final String PRD_DROP_SMS_GROUPS_CUSTOMERS_PARTITION = "DROP_GROUPS_CUST_PARTITION";
    public static final String PRD_MATCH_SMS_GROUPS_CUSTOMERS_CRITERIA = "MATCH_CUSTOMER_SMS_CRITERIA";
    public static final String PRD_MATCH_SMS_GROUPS_CUSTOMERS_UPLOAD = "MATCH_CUSTOMER_SMS_UPLOAD";
    public static final String PRD_MATCH_ADS_GROUPS_CUSTOMERS_CRITERIA = "MATCH_CUSTOMER_ADS_CRITERIA";
    public static final String PRD_MATCH_ADS_GROUPS_CUSTOMERS_UPLOAD = "MATCH_CUSTOMER_ADS_UPLOAD";
    public static final String PRD_MATCH_CAMPAIGN_GROUPS_CUSTOMERS_CRITERIA = "MATCH_CUSTOMER_CAMP_CRITERIA";
    public static final String PRD_MATCH_CAMPAIGN_GROUPS_CUSTOMERS_UPLOAD = "MATCH_CUSTOMER_CAMPAIGN_UPLOAD";
    public static final String PRD_DELETE_EXTRA_CAMPAIGN_CUSTOMERS = "DELETE_EXTRA_CAMP_CUSTM";
    public static final String PRD_MATCH_GROUPS_CUSTOMERS_CRITERIA = "MATCH_CUSTOMER_CRITERIA";
    public static final String PRD_MATCH_GROUPS_CUSTOMERS_UPLOAD = "MATCH_CUSTOMER_UPLOAD";



    public static class VFE_CS_SMS_BULK_RECORDS {
        public static final String TABLE_NAME = "VFE_CS_SMS_BULK_RECORDS";
        public static final String ID = "ID";
        public static final String MSG_PARAMETERS = "MSG_PARAMETERS";
        public static final String JSON_OBJECT = "JSON_OBJECT";
        public static final String STATUS = "STATUS";
        public static final String HTTP_INTERFACE_STATUS = "HTTP_INTERFACE_STATUS";
        public static final String SERVICE_NAME = "SERVICE_NAME";
        public static final String SUBMISSION_DATE = "SUBMISSION_DATE";
        public static final String FILE_NAME = "FILE_NAME";
        public static final String SERVICE_PASSWORD = "SERVICE_PASSWORD"; // CR 1901 | eslam.ahmed
        public static final String SEQ_NAME = "VFE_CS_SMS_BULK_RECORDS_SEQ";
    }
    public static class VFE_CS_SMS_BULK_RECORDS_ARCHIVE {
        public static final String TABLE_NAME = "VFE_CS_SMS_BULK_RECORDS_ARCH";
        public static final String ID = "ID";
        public static final String MSG_PARAMETERS = "MSG_PARAMETERS";
        public static final String JSON_OBJECT = "JSON_OBJECT";
        public static final String STATUS = "STATUS";
        public static final String HTTP_INTERFACE_STATUS = "HTTP_INTERFACE_STATUS";
        public static final String SERVICE_NAME = "SERVICE_NAME";
        public static final String FILE_NAME = "FILE_NAME";
        public static final String SUBMISSION_DATE = "SUBMISSION_DATE";
        public static final String CS_MSG_ID = "CS_MSG_ID";
    }
    
    public static class VFE_CS_SMS_BULK_STATUS_LK {
        public static final String TABLE_NAME = "VFE_CS_SMS_BULK_STATUS_LK";
        public static final String ID = "ID";
        public static final String STATUS ="STATUS";
    }
    
     public static class VFE_CS_RECEIVED_SMS_H {

        public static String TABLE_NAME = "VFE_CS_RECEIVED_SMS_H",
                REQUEST = "REQUEST",
                RESPONSE = "RESPONSE",
                REQUEST_DATE = "REQUEST_DATE",
                TX_ID = "TX_ID";
    }
}