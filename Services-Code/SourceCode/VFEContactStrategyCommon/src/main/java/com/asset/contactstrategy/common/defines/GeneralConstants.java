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
public class GeneralConstants {

    public static int STATUS_APPROVED_VALUE = 1;
    public static int STATUS_PENDING_VALUE = 2;
    public static int STATUS_PENDING_FOR_DELETION_VALUE = 3;

    public static int FILE_STATUS_CREATED_VALUE = 1;
    public static int FILE_STATUS_PROCESSING_VALUE = 3;
    public static int FILE_STATUS_FINISHED_VALUE = 4;

    public static String GENERAL_ERROR = "general.error";
    public static String ENABLE = "false";
    public static String DISABLE = "true";
    public static String DATABASE_ERROR = "web-100";
    public static String STATUS_APPROVED = "Approved";
    public static String STATUS_PENDING = "Pending";
    public static String STATUS_PENDING_FOR_DELETION = "Pending for Deletion";

    public static int TRUE = 1;
    public static int FALSE = 0;

    public static String DATE_PREFIX = "D";
    public static String STRING_PREFIX = "S";
    public static String NUMBER_PREFIX = "N";

    public static final int USER_TYPE_ADMINISTRATOR_VALUE = 3;
    public static final int USER_TYPE_BUSINESS_VALUE = 2;
    public static final int USER_TYPE_OPERATIONAL_VALUE = 1;

    public static final String USER_TYPE_ADMINISTRATOR = "Admin";
    public static final String USER_TYPE_BUSINESS = "Business";
    public static final String USER_TYPE_OPERATIONAL = "Operational";

    public static final int GROUP_CUSTOMER_ELEMENT_FILTER = 1;
    public static final int GROUP_CUSTOMER_FILE_FILTER = 2;

    /////////////////////Engines Codes/////////////////////////////////////////
    public static final int SRC_ID_GLOABL_SETTINGS = 1;
    public static final int SRC_ID_WEB_INTERFACE = 800;
    public static final int SRC_ID_DELIVERY_AGGREGATION = 801;
    public static final int SRC_ID_CUSTOMER_IMPORT_ENGINE = 802;
    public static final int SRC_ID_CUSTOMER_SMS_GROUPS_MATCHING_ENGINE = 804;
    public static final int SRC_ID_CUSTOMER_ADS_GROUPS_MATCHING_ENGINE = 805;
    public static final int SRC_ID_CUSTOMER_CAMPAIGNS_MATCHING_ENGINE = 806;
    public static final int SRC_ID_SENDING_SMS_ENGINE = 808;
    public static final int SRC_ID_MANAGER_ENGINE = 809;
    public static final int SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE = 814;
    public static final int SRC_ID_SMS_INTERFACE_BRIDGING_ENGINE = 816;
    public static final int SRC_ID_SMSC_INTERFACE = 823;
    /////////////////////Interface Codes/////////////////////////////////////////
    public static final int SRC_ID_SEND_SMSs_INTERFACE = 810;
    public static final int SRC_ID_ADVERTISMENT_CONSULT_INTERFACE = 811;
    public static final int SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE = 803;
    public static final int SRC_ID_RETRIEVE_SMSs_INTERFACE = 807;
    public static final int SRC_ID_CONSULT_INTERFACE = 812;
    public static final int SRC_ID_SEND_SMS_BULK_OFFLINE_INTERFACE = 817;
    public static final int SRC_ID_ENQUEUE_SMS_REST = 818;
    public static final int SRC_ID_WEBSERIVCE_REST = 819;
    public static final int SRC_ID_INTEGERATION_INTERFACES = 820;
    public static final int SRC_ID_INTERFACE_REPORTS = 821;

    public static final int SRC_ID_DEQUEUER_WEB_SERVICE = 822;
    ////////////////////////////////////////////////////////////////////////////
    ///////////////// Engines Flags ////////////////////////////////
    public static final String SHUTDOWN_FLAG_TRUE = "T";
    public static final String SHUTDOWN_FLAG_FALSE = "F";

    /////////////////////////////MOM ERRORS SEVERITIES///////////////////////////////
    public static final int MOM_ERROR_MESSAGE_SEVERITY_LOW = 1;
    public static final int MOM_ERROR_MESSAGE_SEVERITY_MEDIUM = 2;
    public static final int MOM_ERROR_MESSAGE_SEVERITY_HIGH = 3;
    public static final int MOM_ERROR_MESSAGE_SEVERITY_CRITICAL = 4;
    public static final int MOM_ERROR_MESSAGE_SEVERITY_SHOW_STOPPER = 5;

    public static final String JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY = "JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY";
    public static final String DATABASE_MOM_ERROR_SEVERITY = "DATABASE_MOM_ERROR_SEVERITY";
    public static final String SHUTDOWN_ENGINE_MOM_ERROR_SEVERITY = "SHUTDOWN_ENGINE_MOM_ERROR_SEVERITY";
    ////////////////////////////////////////////////////////////////////////////

    public static final int GOVERNMENT_ELEMENT_ID = 8;
    public static final int CUSTOMER_TYPE_ELEMENT_ID = 5;

    //Old Regex
    //  public static final String MSISDN_VALIDATE_PATTERN = "^((201)[0-2]([0-9]{8}))$";
    public static final String MSISDN_VALIDATE_PATTERN = "(^(\\+201)||^(00201)||^(201)||^(01)||^(1))\\d{9}";
    public static final String DATE_FORMAT = "DD MM YYYY";
    public static final String DWH_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String DWH_DATE_FORMAT_WITH_OUT_TIME = "yyyy-MM-dd";
    public static final int DWH_ELEMENT_NUMERIC = 1;
    public static final int DWH_ELEMENT_STRING = 2;
    public static final int DWH_ELEMENT_DATE = 3;

    //CAMPAIGN STATUS
    public static int CAMPAIGN_STATUS_RESUMED_VALUE = 1;
    public static int CAMPAIGN_STATUS_STOPPED_VALUE = 2;
    public static int CAMPAIGN_STATUS_PAUSED_VALUE = 3;

    public static final String SYSTEM_PROPERTY_DATE_FORMAT = "YYYY-MM-DD HH24:MI:SS";
    public static final String SYSTEM_PROPERTY_JAVA_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    //Groups Type
    public static final int GROUP_TYPE_CRITERIA_BASED = 1;
    public static final int GROUP_TYPE_UPLOADED_BASED = 2;

    //System Properties Keys
    public static final String KEY_ENGINE_SHUTDOWN_FLAG = "ENGINE_SHUTDOWN_FLAG";
    public static final String KEY_ACTIVE_DWH_PROFILE_PARTITION = "ACTIVE_DWH_PROFILE_PARTITION";

    //Statistics Tables
    public static final String TEMP_COLUMN_NAME = "TEMP";
    public static final String COUNTER_COLUMN_NAME = "COUNTER";
    public static final String DAY_COLUMN_NAME = "DAY";

    public static final String APPROVAL_STATUS_NAME_CREATION = "Created";
    public static final String APPROVAL_STATUS_NAME_EDIT = "Edited";
    public static final String APPROVAL_STATUS_NAME_DELETION = "Deleted";
    public static final String RABBITMQ_EXCHANGE = "CS_Default_Exchange";

    //Queue Queries
    public static class QUEUE_QUERY {

        public static final String QUEUE_CONNECTION_EMPTY = "error.queue_connection";
        public static final String QUEUE_VARIABLE_EMPTY = "error.queue_variable";
    }

    // Group Types SMS, ADS, CAMPAIGN.
    public static final int SMS_GROUP = 1;
    public static final int ADS_GROUP = 2;
    public static final int CAMPAIGN_GROUP = 3;

    //General SQL Errors
    public static final int UNIQUENESS_VIOLATION = 1;

    public static class StructuredLogKeys {

//        public static final String STACKTRACE_ELEMENT = "accumulatedCount";
//        public static final String THREAD_ID = "accumulatedCount";
//        public static final String BLOCKED_TIME = "accumulatedCount";
//        public static final String BLOCKED_COUNT = "accumulatedCount";
//        public static final String WAITED_TIME = "accumulatedCount";
//        public static final String WAITED_COUNT = "accumulatedCount";
//        public static final String LOCK_NAME = "accumulatedCount";
//        public static final String LOCK_OWNER_ID = "accumulatedCount";
//        public static final String LOCK_OWNER_NAME = "accumulatedCount";
//        public static final String IN_NATIVE = "accumulatedCount";
//        public static final String SUSPENDED = "accumulatedCount";
        public static final String ACCUMULATED_COUNT = "accumulatedCount";
        public static final String ACTIVE_THREAD_COUNT = "activeThreadCount";
        public static final String ADS_CONSULT_COUNTER = "adsConsultCount";
        public static final String ADS_GROUPS_COUNT = "adsGroupCounter";
        public static final String ALERT_MSG_DELIVERY = "alertMsgOnDelivery";
        public static final String ALLOWED_COLUMN_NAME = "allowedColumnName";
        public static final String ALLOWED_SMS = "allowedSMS";
        public static final String APP_ID = "appId";
        public static final String APP_NAME = "appName";
        public static final String ARCHIVER_TASK = "archiverTask";
        public static final String ARCHIVING_THREAD_POOL_MAX_SIZE = "archivingThreadPoolMaxSize";
        public static final String ARCHIVING_THREADS_INITIALIZED = "archivingThreadsInitialized";

        public static final String BATCH_ID = "batchId";
        public static final String BATCH_OBJECT = "batchObject";
        public static final String BATCH_SIZE = "batchSize";
        public static final String BEAN_NAME = "beanName";
        public static final String BYTE = "byte";

        public static final String CALLBACK_NUM = "callbackNum";
        public static final String CALLBACK_NUM_A_TAG = "callbackNumATag";
        public static final String CALLBACK_NUM_PRES_IND = "callBackNumPresInd";
        public static final String CAMPAIGN_COUNT = "campaignCount";
        public static final String CAMPAIGN_ID = "campaignId";
        public static final String CAMPAIGN_NAME = "campaignName";
        public static final String CAMPAIGN_TYPE = "campaignType";
        public static final String CHAR = "char";
        public static final String CLASS_NAME = "className";
        public static final String TODAY_COLUMN = "todayColumn";
        public static final String DATA_CODING = "dataCoding";
        public static final String COLUMNS = "column";
        public static final String COMMAND = "command";
        public static final String CONCURRENT_COUNT = "concurrentCount";
        public static final String COMMAND_ID = "commandID";
        public static final String COMMAND_LENGTH = "commandLength";
        public static final String CONVERSION_REQUIRED = "conversionRequired";
        public static final String COMMAND_STATUS = "comandStatus";
        public static final String CONNECTION_TIMEOUT = "connectionTimeout";
        public static final String CONNECTION_POOL_STATS = "ConnectionPoolStats";
        public static final String CONSULT_COUNTER = "consultCounter";
        public static final String CONTROL_PERCENTAGE = "controlPercentage";
        public static final String COUNT = "count";
        public static final String CREATOR = "creator";
        public static final String CURRENT_TIME = "currentTime";
        public static final String CUSTOMER_IN_GUARD = "CustomerInGuard";
        public static final String CUSTOMER_SUSPENDED = "customerSuspended";
        public static final String CUSTOMER_TYPE = "customerType";

        public static final String DAILY_THRESHOLD_SMS = "dailyThresholdSms";
        public static final String DAILY_QUOTA = "dailyQuota";
        public static final String DATA = "data";
        public static final String DATABASE_CONFIGURATION = "databaseConfiguration";
        public static final String DATE = "date";
        public static final String DAY = "day";
        public static final String DAY_IN_SMS_STATS = "dayInSmsStats";
        public static final String DECIMAL_MSG_ID = "deximalMsgId";
        public static final String DECIMAL_TAG = "decimalTag";
        public static final String DST_TON = "dstTon";
        public static final String DST_NPI = "dstNPI";
        public static final String DEQ_TIME = "deqTime";
        public static final String DELETE_COUNT = "deleteCount";
        public static final String DAYS_BEFORE_TIMEOUT = "daysBeforeTimeout";
        public static final String DELETED_RECORDS = "deletedRecords";
        public static final String DELIVERY_REPORT = "deliveryReport";
        public static final String DEST_NUMBER = "destNumber";
        public static final String DEST_ADDR_SUBMIT = "destAddrSubmit";
        public static final String DEST_SUB_ADDRESS = "DestSubAddress";
        public static final String DESTINATION_ADDRESS_TON = "destinationAddressTon";
        public static final String DESTINATION_ADDRESS_NPI = "destinationAddressNpi";
        public static final String DESTINATION_FILE = "destinationFile";
        public static final String DESTINATION_MSISDN = "destinationMsisdn";
        public static final String DISPLAY_TIME = "displayTime";
        public static final String DESTINATION_ADDRESS = "destinationAddress";
        public static final String DIFF_TIME = "diffTime";
        public static final String DONOT_CONTACT = "donotContact";
        public static final String DO_NOT_APPLY = "doNotApply";
        public static final String DWH_ELEMENT = "DWHElement";

        public static final String ELAPSED_TIME = "elapsedTime";
        public static final String ELEMENT_NAME = "elementName";
        public static final String ENCODING = "encoding";
        public static final String ENQ_TIME = "dbEnqTime";
        public static final String EXPECTED_STATE = "expectedState";

//        public static final String ELEMENT_LIST = "elementList";
        public static final String ENGINE = "engine";
        public static final String ENGINE_SOURCE_ID = "engineSourceId";
        public static final String ENQUEUE_MODEL = "enqueueModel";
        public static final String ENQUEUE_FLAGE = "enqueueFlage";
        public static final String ERROR_CODE = "errorCode";
        public static final String ERROR_DESCRIPTION = "errorDescription";
        public static final String ERROR_MESSAGE = "errorMessage";
        public static final String ERROR_PARAMS = "errorParams";
        public static final String ESM_CLASS = "esmClass";
        //public static final String EXECUTION_TIME = "executionTime";

        public static final String FAILURE_COUNT = "failureCount";
        public static final String FILE_CONFIGURATION = "fileConfigurations";
        public static final String FILE_ID = "fileId";
        public static final String FILE_NAME = "fileName";
        public static final String REPLACE_IF_PRESENT_FLAG = "replaceIfPresent";
        public static final String FOLDER = "folder";
        public static final String FORWARDING_IPADDRESS = "forwardingIpAddress";
        public static final String FREE_MEMORY = "freeMemory";
        public static final String FROM = "from";
        public static final String FUNCTION_NAME = "functionName";
        public static final String FUTURE_TIME = "futureTime";

        public static final String GET_DEST_PORT = "getDestPort";
        public static final String GROUP_DESCRIPTION = "groupDescription";
        public static final String GROUP_EXIST_FLAG = "existInGroupFlag";
        public static final String GROUP_ID = "groupID";
        public static final String GROUPS_LIST_SIZE = "groupsListSize";
        public static final String GROUP_NAME = "groupName";
        public static final String GUARD_START = "guardStart";
        public static final String GUARD_PERIOD = "guardPeriod";
        public static final String GROUP_PRIORITY = "groupPriority";
        public static final String GROUP_OBJECT_TYPE = "groupObjectType";
        public static final String GROUP_TYPE = "groupType";
        public static final String GOVERNMENT = "government";
        public static final String GOVERNMENT_ID = "governmentId";
        public static final String GUARD_END = "guardEnd";

        public static final String HASH_WHITE_LIST = "hashWhiteList";
        public static final String HEADER_RET_VAL = "headerRetVal";
        public static final String HEX_DEC_MSG_ID = "hexDecMsgId";
        public static final String HEX_MSG_ID = "hexMsgId";
        public static final String HOST = "host";

        public static final String ID = "ID";
        public static final String INPUT_STRING = "inputString";
        public static final String INSERTED_RECORDS = "insertedRecords";
        public static final String INSTANCE_ID = "instanceID";
        public static final String INSTANCE_QUEUES = "instanceQueues";
        public static final String INTERFACE_NAME = "interfaceName";
        public static final String INTERFACE_TYPE = "interfaceType";
        public static final String IP_ADDRRESS = "ipAddress";
        public static final String ITS_REPLAY_TYPE = "itsReplayType";
        public static final String ITS_SESSION_INFO = "itsSessionInfo";

        public static final String JOB_DATE = "jobDate";
        public static final String JOB_MOD_X = "jobModX";
        public static final String JSON_STRING = "jsonString";

        public static final String KEEP_ALIVE = "keepAliver";
        public static final String KEY = "key";

        public static final String LANGUAGE_INDICATOR = "languageIndicator";
        public static final String LAST_APPEARANCE = "lastAppearance";
        public static final String LAST_ERROR_HOURS = "lastErrorHours";
        public static final String LAST_UPDATE = "lastUpdate";
        public static final String LAST_UPLOAD_HOURS = "lastUploadHours";
        public static final String LAST_WORKING_DAY = "lastWorkingDay";
        public static final String LANGUAGE = "language";
        public static final String LAST_MSISDN_TWO_DIGITS = "lastMSISDNTwoDigits";
        public static final String LATEST_MSG = "latestMsg";
        public static final String LENGTH = "length";
        public static final String LINE_NUMBER = "lineNumber";
        public static final String LIST_SIZE = "listSize";
        public static final String LOG_PREFIX = "logPrefix";
        public static final String LOGGING_THREAD_POOL_MAX_SIZE = "loggingThreadMaxPoolSize";
        public static final String LOGGING_THREADS_INITIALIZED = "loggingThreadInitialized";

        public static final String MAX_HITS = "maxHits";
        public static final String MATCHED_RECORDS = "matchedRecords";
        public static final String MAX_ALLOWED = "maxAllowed";
        public static final String MAX_ARCHIVING_DB_ARRAY_SIZE = "maxArchivingDBArraySize";
        public static final String MAX_CONNECTIONS = "maxConnections";
        public static final String MAX_ENQUEUE_DB_ARRAY_SIZE = "maxEnqueueDBArraySize";
        public static final String MAX_LOGGING_DB_ARRAY_SIZE = "maxLoggingDBArraySize";
        public static final String MAX_MEMORY = "maxMemeory";
        public static final String MAX_TARGETED_CUSTOMERS = "maxTargetedCustomers";
        public static final String MAX_CONCURRENT_COUNT = "maxConcurrentCount";
        public static final String MESSAGE = "message";
        public static final String MESSAGE_PRIORITY = "messagePriority";
        public static final String METHOD_NAME = "methodName";
        public static final String MIDNIGHT_TIME = "midnightTime";
        public static final String MODULE_NAME = "moduleName";
        public static final String MONTHLY_STATEMENT = "monthlyStatement";
        public static final String MONTHLY_SUM = "monthlySum";
        public static final String MONTHLY_THRESHOLD_SMS = "monthlyThrsholdSms";
        public static final String MONTHLY_QUOTA = "monthlyQuota";
        public static final String MORE_MESSAGE_TO_SEND = "moreMessageToSend";
        public static final String MS_MSG_WAIT_FACILITY = "msMsgWaitFacility";
        public static final String MS_VALIDITY = "msValidity";
        public static final String MSG_COUNT = "msgCount";
        public static final String MSG_ID = "msgId";
        public static final String MSG_IDS = "msgIds";
        public static final String MSG_PAYLOAD = "messagePayload";
        public static final String MSG_SEQUENCE = "msgSequence";
        public static final String MSG_TYPE = "msgType";
        public static final String MSISDN = "msisdn";
        public static final String MSISDN_COUNT = "msisdnCount";
        public static final String MSISDN_LAST_TWO_DIGITS = "msisdnLastTwoDigits";

        public static final String NBE_TRIALS = "nbeTrials";
        public static final String NEW_FILE_NAME = "newFileName";
        public static final String NUM_TRIALS = "numberOfTrials";
        public static final String NUMBER_OF_MESSAGES = "numberOfMessages";
//        public static final String NO_OF_PREPARING_STATEMENTS_THREADS = "noOfPreparingStatementThreads";
//        public static final String NO_OF_WRITING_THREAD_POOLS = "noOfWiritingThreadPools";
//        public static final String NO_OF_WRITING_WORKERS_PER_THREAD_POOL = "noOfWritingWorkersPerThreadPool";

        public static final String OPTIONAL_PARAMETER_1 = "optionalParameter1";
        public static final String OPTIONAL_PARAMETER_2 = "optionalParameter2";
        public static final String OPTIONAL_PARAMETER_3 = "optionalParameter3";
        public static final String OPTIONAL_PARAMETER_4 = "optionalParameter4";
        public static final String OPTIONAL_PARAMETER_5 = "optionalParameter5";
        public static final String OPTIONAL_ID = "optionalID";
        public static final String ORIG_TYPE = "origType";
        public static final String OUTPUT = "output";

//        public static final String PARSED_TIME = "parsedTime";
        public static final String PARTITION = "partition";
        public static final String PARTITION_ID = "partitionId";
        public static final String PASSWORD = "Passowrd";
        public static final String PATH = "path";
        public static final String PAYLOAD_TYPE = "payloadType";
        public static final String PDU_HEADER = "pduHeader";
        public static final String PRIVACY_INDICATOR = "privacyIndicator";
        public static final String PDU = "PDU";
        public static final String PORT = "port";
        public static final String PRECEIVED_SEVERITY = "preceivedSeverity";
        public static final String PRIORITY = "Priority";
        public static final String PRIORITY_FLAG = "priorityFlag";
        public static final String PROCEDURE = "procedure";
        public static final String PROCEDURE_QUEUELIST_COUNT = "procedureQueueListCount";
        public static final String PROCESS_ID = "processId";
        public static final String PROTOCOL_ID = "protocolId";
        public static final String PULL_TIME_OUT = "PullTimeOut";

        public static final String QOUTA = "qouta";
        public static final String QUEUE_ID = "queueId";
        public static final String QUEUE_MSG_ID = "queueMsgId";
        public static final String QUEUE_NAME = "queueName";
        public static final String QUEUE_OCCUPANCY = "queueOccupancy";
        public static final String QUEUE_REMAINING_CAPACITY = "queueRemainingCapacity";
        public static final String QUEUE_SIZE = "queueSize";
        public static final String QUEUE_VERSION = "queueVersion";
        public static final String QUERY = "query";
        public static final String QUERY_DATE = "queryDate";

        public static final String RATE_PLAN = "ratePlan";
        public static final String READ_TIMEOUT = "readTimeout";
        public static final String RECORD_COUNT = "recordCount";
        public static final String REGISTERED_DELIVERY = "registeredDelivery";
        public static final String RELOADING_TIME = "reloadingTimes";
        public static final String RETRIES = "retries";
        public static final String REQUEST_BODY = "requestBody";
        public static final String REQUEST_ID = "requestId";
        public static final String REQUEST_INFO = "requestInfo";
        public static final String REQUEST_PDU = "requestPDU";
        public static final String REQUEST_TYPE = "requestType";
        public static final String RESPONSE_CODE = "responseCode";
        public static final String RESPONSE_DESCRIPTION = "responseDescription";
        public static final String RESPONSE_PDU = "responsePDU";
        public static final String RESPONSE_TIME = "responseTime";
        public static final String RESULT_ID = "resultID";
        public static final String ROLLBACK_STATUS = "rollBackStatus";
        public static final String RUN_ID = "runID";

        public static final String SAR_MSG_REF_NUM = "sarMsgRefNum";
        public static final String SAR_SEGMENT_REF_NUM = "sarSegmentRefNum";
        public static final String SAR_TOTAL_SEGMENTS = "sarTotalSegments";
        public static final String SCHEDULE_DELIVERY_TIME = "scheduleDeliveryTime";
        public static final String SEND_SMS_ARCHIVER_TASK = "sendSMSArchiverTask";
        public static final String SENDER_IP = "senderIP";
        public static final String SENDER_QUEUELIST_COUNT = "senderQueueListCount";
        public static final String SERVER_NAME = "serverName";
        public static final String SERVICE_COUNT = "serviceCount";
        public static final String SERVIC_ID = "serviceId";
        public static final String SERVICE_NAME = "serviceName";
        public static final String SERVICE_QUOTA = "serviceQuota";
        public static final String SERVICE_QUOTA_COUNTER = "serviceQuotaCounter";
        public static final String SESSION = "session";
        public static final String SESSION_COUNT = "sessionCount";
        public static final String SESSION_ID = "sessionId";
        public static final String SEQ_ID = "seqId";
        public static final String SEQUENCE_NUMBER = "sequenceNumber";
        public static final String SHUTDOWN_FLAG = "shutdownFlag";
        public static final String SIZE = "size";
        public static final String SLEEP_TIME = "sleepTime";
        public static final String SM_DEFAULT_MSG_ID = "smDefaultMsgId";
        public static final String SMS_GROUPS_COUNT = "smsGroupCount";
        public static final String SMS_INFO = "smsInfo";
        public static final String SMS_MSG_ID = "smsMsgId";
        public static final String SMS_SCRIPT = "smsScript";
        public static final String SMS_SIGNAL = "smsSignal";
        public static final String SMSC_ID = "smscId";
        public static final String SMSC_IP = "smscIP";
        public static final String SOCKET_IP = "socketIP";
        public static final String SOURCE_ADDRESS = "sourceAddress";
        public static final String SOURCE_ADDRESS_NPI = "sourceAddressNpi";
        public static final String SOURCE_ADDR_SUBMIT = "sourceAddrSybmit";
        public static final String SOURCE_ADDRESS_TON = "sourceAddressTon";
        public static final String SOURCE_FILE = "sourceFile";
        public static final String SOURCE_PORT = "sourcePort";
        public static final String SOURCE_SUB_ADDRESS = "sourceSubAddress";
        public static final String SRC_NPI = "srcNpi";
        public static final String SRC_NUMBER = "srcNumber";
        public static final String SRC_TON = "srcTon";
        public static final String STACK_TRACE = "stackTrace";
        public static final String STATUS = "status";
        public static final String STATUS_COUNT = "statusCount";
        public static final String STATE = "state";
        public static final String STRING = "string";
        public static final String SUCCESS_COUNT = "successCount";
        public static final String SUPPORT_ADS = "supportADS";
        public static final String SUBMISSION_DATE = "submissionDate";
        public static final String SYSTEM_NAME = "systemName";
        public static final String SYSTEM_CATEGORY = "systemCategory";
        public static final String SYSTEM_COUNT = "systemPropertiesCount";
        public static final String SYSTEM_ID = "systemId";
        public static final String SYSTEM_TYPE = "systemType";
        public static final String SYSTEM_PROPERTIES = "systemProperties";

        public static final String TABLE_NAME = "tableName";
        public static final String TAG = "tag";
        public static final String TEMPLATE_IDs = "templateIds";
        public static final String TEMPLATE_PARAMETERS = "templateParameters";
        public static final String THREAD_DUMP = "threadDump";
        public static final String THREADS_INITIALIZED = "threadsInitialized";
        public static final String THREAD_MAX_RETRIES = "ThreadMaxRetries";
        public static final String THREAD_NAME = "threadName";
        public static final String THREAD_NUMBER = "threadNumber";
        public static final String THREAD_POOL_ID = "threadPoolId";
        public static final String THREAD_POOL_MAX_SIZE = "threadPoolMaxSize";
        public static final String THREAD_POOL_SIZE = "threadPoolSize";
        public static final String THREAD_POOLS_COUNT = "threadPoolsCount";
        public static final String THREAD_STATE = "accumulatedCount";
        public static final String THREAD_STATUS = "threadStatus";
        public static final String THRESHOLD_SIZE = "thresholdSize";
        public static final String TIME_DIFF = "timeDifference";
        public static final String TRANS_ID = "transId";
        public static final String TRANS_IDs = "transIds";
        public static final String TO = "to";
        public static final String TO_DAY = "to_day";
        public static final String TODAY_COUNTER = "todayCouner";
        public static final String TODAY_QUOTA = "todayQuota";
        public static final String TOTAL_COUNT = "totalCount";
        public static final String TOTAL_MEMORY = "totalMemeory";
        public static final String TRACKING_ID = "trackingId";
        public static final String TYPE = "type";

        public static final String URL = "URL";
        public static final String UPDATE_COUNT = "updateCount";
        public static final String UPDATED_QUEUE_SIZE = "updatedQueueSize";
        public static final String UPLOADED_RECORDS = "uploadedRecords";
        public static final String USED_CONNECTIONS = "usedConnections";
        public static final String USED_MEMORY = "usedMemory";
        public static final String USER = "user";
        public static final String USER_MESSAGE_REFERENCE = "userMessageReference";
        public static final String USER_NAME = "username";
        public static final String USSD_SERVICE_OP = "ussdServiceOP";

        public static final String VALIDITY_PERIOD = "validityPeriod";
        public static final String VALUE = "value";
        public static final String VERSION_ID = "versionId";

        public static final String WAIT_TIME = "waitTime";
        public static final String WEEKLY_STATEMENT = "weeklyStatement";
        public static final String WEEKLY_SUM = "weeklySum";
        public static final String WEEKLY_THRESHOLD_SMS = "weeklyThrsholdSms";
        public static final String WEEKLY_QUOTA = "weeklyQuota";
        public static final String WHITE_LIST_SIZE = "whiteListSized";

        public static final String YESTERDAY_COLUMN = "yesterdayColumn";
        
        public static final String SMSC_VERSION_ID = "smscVersionId";
        public static final String CURRENT_THROUGHPUT_COUNT = "currentThroughputCount";
        public static final String SMSC_THROUGHPUT = "smscThroughput";
        public static final String CURRENT_WINDOW_SIZE_COUNT = "currentWindowSizeCount";
        public static final String SMSC_WINDOW_SIZE = "smscWindowSize";
        
    }
}
