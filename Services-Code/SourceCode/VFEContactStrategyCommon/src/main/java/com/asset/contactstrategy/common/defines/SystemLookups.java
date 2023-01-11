/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.defines;

import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.ReportsViewModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.interfaces.models.AdsGroupModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.models.TemplateModel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Zain Al-Abedin
 */
public class SystemLookups {

    public static ArrayList<LookupModel> interfaceTypeList = new ArrayList<LookupModel>();
    public static ArrayList<LookupModel> serviceCategoryList = new ArrayList<LookupModel>();
    public static ArrayList<LookupModel> serviceTypeList = new ArrayList<LookupModel>();
    public static ArrayList<LookupModel> services = new ArrayList<LookupModel>();
    public static ArrayList<LookupModel>  messageStatus = new ArrayList<LookupModel>();
    public static ArrayList<LookupModel>  SMSCNames = new ArrayList<LookupModel>();
    public static HashMap<Integer, LookupModel> DWH_DATA_TYPES = new HashMap<Integer, LookupModel>();
    public static HashMap<Integer, LookupModel> DWH_DISPLAY_TYPES = new HashMap<Integer, LookupModel>();
    public static HashMap<String, ServicesModel> SERVICES;
    public static HashMap<Integer, SMSGroupModel> SMS_GROUPS;
    public static HashMap<Integer, AdsGroupModel> ADS_GROUPS;
    public static HashMap<Integer, LookupModel> OPERATORS = new HashMap<Integer, LookupModel>();
    public static HashMap<Integer, LookupModel> FILE_STATUS = new HashMap<Integer, LookupModel>();
    public static HashMap<Integer, LookupModel> GOVERNMENTS = new HashMap<Integer, LookupModel>();
    public static HashMap<Integer, LookupModel> CUSTOMER_TYPES = new HashMap<Integer, LookupModel>();
    public static HashMap<Integer, LookupModel> GROUP_TYPES = new HashMap<Integer, LookupModel>();
    public static ArrayList<LookupModel> OPERATION_STATUS = new ArrayList<LookupModel>();
    public static HashMap<String, String> SYSTEM_PROPERTIES;
    public static HashMap<Integer, LookupModel> SMS_H_STATUS;
    public static HashMap<Integer, LookupModel> VFE_PREFIX;
    public static HashMap<Integer, LookupModel> USERS_TYPE;
    public static ArrayList<ReportsViewModel> REPORTS_LIST;
    public static HashMap<Integer, OriginatorTypeModel> ORIGINATORS_LIST;
    public static HashMap<Long, QueueModel> QUEUE_LIST;
    public static HashMap<Integer,LookupModel> QUEUE_TYPE_LK;
    public static HashMap<Integer, LookupModel> ORIGINATOR_VALUES_LK;
    public static ArrayList<OriginatorTypeModel> ORIGINATOR_TYPE_LK;
    public static ArrayList<LookupModel> SERVICE_PRIVILEGES_LK;
    public static HashMap<String, QueueModel> QUEUE_LIST_BY_NAME;
    //CSPhase1.5 | Esmail.Anbar | Adding Template Update
    public static HashMap<Integer, TemplateModel> TEMPLATES;
}
