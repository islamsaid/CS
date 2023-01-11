/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.utils;

/**
 *
 * @author rania.magdy
 */
public class Constants {

    public static final String MANAGEMENT_MODE = "management_mode";
    public static final Integer CREATION_MODE = 0;
    public static final Integer EDIT_MODE = 1;
    public static final Integer VIEW_MODE = 2;
    public static final Integer APPROVAL_MODE = 3;
    public static final Integer MAX_NO_OF_RECORD_IN_FILE= 50000;

    public static final String EDIT_MODEL_KEY = "web_model_key";
//Campaigns
    public static final String INVALID_DATE = "error.invalid_date";
    public static final String INVALID_TIME = "error.invalid_time";
    public static final String SELECT_SERVICES = "error.select_services";
    public static final String INVALID_SUSPEND_NUMBER = "error.invalid_suspend_number";
    public static final String DOWNLOAD_FAILURE = "error.download_failure";
    public static final String CUSTOMER_CAMPAIGN_IS_EMPTY = "info.customercampaign_empty";
    public static final String CUSTOMER_CAMPAIGN_IS_NOT_EMPTY = "info.customercampaign_not_empty";
    public static final String PAUSED = "info.pause";
    public static final String RESUMED = "info.resume";
    public static final String STOPPED = "info.stop";
    public static final String PAUSED_FAIL = "error.pause";
    public static final String RESUMED_FAIL = "error.resume";
    public static final String STOPPED_FAIL = "error.stop";
    

    public static final String MISSING_DATA = "error.missing_data";
    public static final String SMSC_ADDED = "info.smsc_added";
    public static final String SMSC_EDITED = "info.smsc_edited";
    public static final String SMSC_DELETED = "info.smsc_deleted";
    public static final String INVALID_IP = "error.invalid_ip";
    public static final String CONFIRM_PASSWORD = "error.confirm_password";
    public static final String INVALID_OLD_PASSWORD = "error.invalid_oldPassword";
    public static final String MISSING_OLD_PASSWORD = "error.missing_oldPassword";
    public static final String ITEM_ADDED = "addItem.success";
    public static final String ITEM_DELETED = "deleteItem.success";
    public static final String ITEM_EDITED = "editItem.success";
    public static final String ITEM_APPROVAL = "approveItem.success";
    public static final String ITEM_REJECTION = "rejectItem.success";
    public static final String INVALID_DAILY_QUOTA = "error.invalid_dailyQuota";
    public static final String INVALID_ALLOWED_SMS = "error.invalid_allowedSms";
    //queue errors
    public static final String UNIQUE_NAME_ERROR = "uniqueName.error";
    
    public static final String VALIDATE_ORIGINATOR_LENGTH = "validate.originator_length";

    //MODULE NAMES
    public static final String SMSC_MODULE = "SMSC";
    public static final String CAMPAIGN_MODULE = "Campaign";
    public static final String QUEUE_MODULE = "Queue";
    public static final String SERVICE_MODULE = "Service";
    public static final String USER_MODULE = "User";
    public static final String MENU_MODULE = "Menu";

    //FUNCTION NAMES
    public static final String GET_SMSC = "getSMSCs";
    public static final String EDIT_SMSC = "editSMSC";
    public static final String CREATE_SMSC = "createSMSC";
    public static final String DELETE_SMSC = "deleteSMSC";
    public static final String APPROVE_SMSC_OPERATION = "approveSMSCOperation";
    public static final String GET_APPROVED_SMSC = "getApprovedSMSCs";

    public static final String GET_CAMPAIGN = "getCampaigns";
    public static final String EDIT_CAMPAIGN = "editCampaign";
    public static final String CREATE_CAMPAIGN = "createCampaign";
    public static final String DELETE_CAMPAIGN = "deleteCampaign";
    public static final String CHANGE_CAMPAIGN_STATUS = "changeCampaignStatus";
    public static final String APPROVE_CAMPAIGN_OPERATION = "approveCampaignOperation";
    public static final String REJECT_CAMPAIGN_OPERATION = "rejectCampaignOperation";

    public static final String GET_QUEUES = "getApplicationQueues";
    public static final String GET_QUEUE_SMSC = "getApplicationQueuesSMSCs";
    public static final String CREATE_QUEUE = "createApplicationQueue";
    public static final String EDIT_QUEUE = "editApplicationQueue";
    public static final String DELETE_QUEUE = "deleteApplicationQueue";
    public static final String APPROVE_QUEUE = "approveApplicationQueue";
    public static final String REJECT_QUEUE = "rejectApplicationQueue";
    public static final String INVALED_URL = "error.invalidurl";

    public static final String GET_MENU = "getMenuList";

    public static final String HIGH_SEVERITY = "High";
    public static final String MEDIUM_SEVERITY = "Medium";
    public static final String LOW_SEVERITY = "Low";

    public static final String ACTIVE_MENU = "activeMenu";
    
    ///////Reports////
    public static final String INVALID_MSISDN = "error.invalid_msisdn";
    public static final String RETRIEVE_REPORT = "error.retrieveReport";
    
    public static final String ADD_ITEM_FAILURE = "addItem.failure";
    public static final String EDIT_ITME_FAILURE = "editItem.failure";
    public static final String DELETE_ITEM_FAILURE = "deleteItem.failure";
    public static final String PAGE_INIT_FAILURE = "pageInit.error";
    
    public static final String FILTER_CRITEREA_EMPTY = "empty.filter_criterea";
    public static final String FILTER_FILES_EMPTY = "empty.filter_files";
    
    public static final String EMPTY = "empty";
    public static final String INVALID_CONTENT = "error.invalid_content";
    public static final String MAXIMUM_REACHED = "error.maximum_reached";

    public static final String NO_FILES = "error.no_files";
    public static final String NO_SUCCESSFUL_RECORDS = "error.no_successful_records";
    
    // CR 1901 | eslam.ahmed
    public static final String INVALID_SERVICE_PASSWORD = "error.invalid_service_password";
    
    public static final String ERROR_DAILY_THRESHOLD="error.daily_threshold";
    public static final String ERROR_MONTHLY_THRESHOLD="error.monthly_threshold";
    
    public static final String ERROR_DAILY_CAMPAIGN = "error.daily_camp_threshold";
    public static final String ERROR_MONTHLY_CAMPAIGN ="error.monthly_camp_threshold";
    
    public static final String EMPTY_WHITE_LIST = "error.empty_white_list"; 
}
