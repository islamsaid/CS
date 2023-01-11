/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.defines;

/**
 *
 * @author Mostafa Kashif
 */
public class EngineErrorCodes {

    public static final int LOADIND_PROPERTIES_FILE_FAILED = 1;
    public static final int ERROR_MESSAGES_PROPERTIES_FILE_FAILED = 2;
    public static final int ERROR_IN_DATABASE_POOL = 3;
    public static final int SQL_ERROR_IN_DAO = 4;
    public static final int GENERAL_ERROR_IN_DAO = 5;
    public static final int INVALID_USER = 6;
    public static final int INVALID_PASSWORD = 7;
    public static final int TEMPLATE_NOT_EXIST_IN_DATABASE = 8;
    public static final int ERROR_IN_MESSAGE_LINE = 9;
    public static final int MORE_THAN_FIVE_TEMPLATES_IN_LINE = 10;
    public static final int TEMPLATES_PARAMETERS_SIZE_MORE_THAN_MAX_SIZE = 11;
    public static final int LINE_EXCEED_MAX_CHARACTERS = 12;
    public static final int ERROR_ENQUEUIN_MESSAGE_IN_DB_QUEUE = 13;
    public static final int ERROR_GETTING_QUEUE_NAME_FROM_LINE = 14;
    public static final int GENERAL_ERROR = 15;
    public static final int ERROR_IN_TEMPLATE_PARAMETERS_REPLACEMENT = 16;
    public static final int ERROR_TEMPLATE_NOT_IN_DATABASE = 17;
    public static final int ERROR_PARSING_TEMPLATE_ID = 18;
    public static final int ERROR_MAX_NO_OF_WORKERS = 19;
    public static final int ERROR_IN_REPLACEMENT_KEY = 20;
    public static final int ERROR_IN_TEMPLATE_SCRIPT = 21;
    public static final int ERROR_NO_OF_WORKERS_EXCEED = 22;
    public static final int SQL_ERROR_IN_TEMPLATE_DAO = 23;
    public static final int GENERAL_ERROR_IN_TEMPLATE_DAO = 24;
    public static final int SQL_ERROR_IN_SMPP_DAO = 25;
    public static final int ERROR_VALIDATING_MSISDN = 26;
    public static final int ERROR_IN_SHUTDOWN_FLAG = 27;
    public static final int SQL_ERROR_IN_SMS_DAO = 28;
    public static final int GENERAL_ERROR_IN_SMS_DAO = 29;
    public static final int TEMPLATE_HAS_MORE_THAN_TEN_PARAMETERS = 30;
    public static final int UNKNOWN_ERROR = 31;
    public static final int GENERAL_SQL_ERROR = 32;
    public static final int FTP_CONNECTION_ERROR=33;
    public static final int PARSE_ERROR=34;
}
