/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.service;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.InstanceVariables;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.models.SMS;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.models.SMSHistoryModel;
import com.asset.contactstrategy.common.models.ServiceQuotaCounter;
import com.asset.contactstrategy.common.service.DBQueueService;
import com.asset.contactstrategy.common.service.DWHProfileService;
import com.asset.contactstrategy.common.service.LookupService;
import com.asset.contactstrategy.common.service.SMSCustomerStatsticsService;
import com.asset.contactstrategy.common.service.SMSHistoryService;
import com.asset.contactstrategy.common.service.SystemPropertiesService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.AdsCountersModel;
import com.asset.contactstrategy.interfaces.models.AdsGroupModel;
import com.asset.contactstrategy.interfaces.models.CustomerCampaignsModel;
import com.asset.contactstrategy.interfaces.models.CustomerConfigAndGrpModel;
import com.asset.contactstrategy.interfaces.models.CustomerConfigurationsModel;
import com.asset.contactstrategy.interfaces.models.CustomersModel;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import com.asset.contactstrategy.interfaces.models.RESTLogModel;
import com.asset.contactstrategy.interfaces.models.RetrieveMessageInputModel;
import com.asset.contactstrategy.interfaces.models.RetrieveSMSsInputModel;
import com.asset.contactstrategy.interfaces.models.RetrieveSMSsOutputModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.models.SmsCountersModel;
import com.asset.contactstrategy.interfaces.models.TemplateModel;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

/**
 *
 * @author hazem.fekry
 */
public class MainService {

    public HashMap<String, ServicesModel> getAllServices() throws CommonException {

        Connection connection = null;
        HashMap<String, ServicesModel> allServices = null;
        try {
            connection = DataSourceManger.getConnection();
            ServicesManagmentService servicesManagmentService = new ServicesManagmentService();
            allServices = servicesManagmentService.getServices(connection);

        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "getAllServices()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
        return allServices;
    }

    public HashMap<Integer, SMSGroupModel> getSmsGroupsList() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            GroupsService groupsService = new GroupsService();
            HashMap<Integer, SMSGroupModel> allGroups = groupsService.getGroupsList(connection);
            return allGroups;
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "getSmsGroupsList()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    public HashMap<Integer, AdsGroupModel> getAdsGroupsList() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            GroupsService groupsService = new GroupsService();
            HashMap<Integer, AdsGroupModel> allAdsGroups = groupsService.getAdsGroupsList(connection);
            return allAdsGroups;
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "getAdsGroupsList()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

//    public void updateAdsCounters(String MSISDN) throws CommonException 
//    {
//        Connection connection = null;
//        try 
//        {
//            connection = DataSourceManger.getConnection();
//            CustomerService customerService = new CustomerService();
//            customerService.updateAdsCounters(connection, MSISDN);
//            DataSourceManger.commitConnection(connection);
//        }
//        catch (CommonException ce) 
//        {
//            DataSourceManger.rollBack(connection);
//            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS, 
//                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "MSISDN:" + MSISDN);
//            throw ce;
//        }
//        catch (Exception e) 
//        {
//            DataSourceManger.rollBack(connection);
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        }
//        finally
//        {
//            try 
//            {
//                DataSourceManger.closeConnection(connection);
//            } 
//            catch (CommonException ce) 
//            {
//                CommonLogger.businessLogger.error(ce.getMessage());
//                CommonLogger.errorLogger.error(ce.getMessage(), ce);
//            }
//        }
//    }
//    public void updateCustomerSmsQuotaCounter(String MSISDN) throws CommonException 
//    {
//        Connection connection = null;
//        try 
//        {
//            connection = DataSourceManger.getConnection();
//            CustomerService customerService = new CustomerService();
//            customerService.updateCustomerSmsQuotaCounter(connection, MSISDN);
//            DataSourceManger.commitConnection(connection);
//        }
//        catch (CommonException ce) 
//        {
//            DataSourceManger.rollBack(connection);
//            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS, 
//                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "MSISDN:" + MSISDN);
//            throw ce;
//        }
//        catch (Exception e) 
//        {
//            DataSourceManger.rollBack(connection);
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        }
//        finally
//        {
//            try 
//            {
//                DataSourceManger.closeConnection(connection);
//            } 
//            catch (CommonException ce) 
//            {
//                CommonLogger.businessLogger.error(ce.getMessage());
//                CommonLogger.errorLogger.error(ce.getMessage(), ce);
//            }
//        }
//    }
    /**
     *
     * @param input
     * @param service
     * @param ipAddress
     * @param transId
     * @return
     * @throws CommonException
     */
    public String consultAds(int srcId, InputModel input, ServicesModel service, String ipAddress, String transId) throws CommonException, InterfacesBusinessException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();

            //Check destination MSISDN existance
            DWHProfileService dWHProfileService = new DWHProfileService();
            CustomersModel customer = dWHProfileService.getCustomer(connection, input.getDestinationMSISDN());
//            if (customer == null || customer.getMsisdn() == null || customer.getMsisdn().equals("")) 
//            {
//                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE, 
//                        ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.DESTINATION_MSISDN_NOT_VALID, "MSISDN not in DWH Table");
//                CommonLogger.businessLogger.error(transId + " || " + ibe.getDetailedMessage());
//                throw ibe;
            //Esmail.Anbar | 22/1/2018 | Updating to complete execution when customer is not in DWH Table
//                customer = new CustomersModel();
            customer.setMsisdn(input.getDestinationMSISDN());
            customer.setLastTwoDigits(Integer.valueOf(input.getDestinationMSISDN().substring(input.getDestinationMSISDN().length() - 2)));
            if (input.getLanguage() != null) {
                customer.setLanguage(input.getLanguage().toString());
            } else {
                customer.setLanguage(SystemLookups.SYSTEM_PROPERTIES.get(Defines.ARABIC_LANG));
            }
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Customer not found in DWH Table... using default settings");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer not found in DWH Table... using default settings").build());
//            }

            //Get customer configurations
            CustomerService customerService = new CustomerService();
            CustomerConfigurationsModel customerConfigurations = customerService.getCustomerConfigurations(connection, input.getDestinationMSISDN());
            return adsValidations(srcId, transId, connection, input, service, customerService, customer, customerConfigurations);
        } catch (InterfacesBusinessException ibe) {
            throw ibe;
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            sendMOM(ce, GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "DestMSISDN:" + input.getDestinationMSISDN() + ", serviceName:" + service.getServiceName() + ", ipAddress:" + ipAddress + ", transId:" + transId);
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.commitConnection(connection);
            } catch (CommonException ce) {
//                CommonLogger.businessLogger.info(transId + " || " + "Error while commiting Connection");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error while commiting Connection").build());

                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    /**
     * Get advertisement script with highest priority
     *
     * @param language
     * @param customerCampaignsList
     * @return Advertisement Script
     * @throws CommonException
     */
    private String getAdsScript(int srcId, String transId, byte language, List<CustomerCampaignsModel> customerCampaignsList, Connection conn,
            CustomerService customerService, AdsCountersModel adsCountersModel, String yesterdayColumn, int todayColumn, int totalyDays, InputModel input) throws CommonException, InterfacesBusinessException {
        MainService mainService = new MainService();
        boolean sendCheck = false;
        for (CustomerCampaignsModel customerCampaign : customerCampaignsList) {
            if (customerCampaign.getCampCount() < customerCampaign.getCustomerCampaign().getMaxcommunications()) {
                sendCheck = customerService.handleCustomerAdsCounters(conn, adsCountersModel, yesterdayColumn, todayColumn, totalyDays, transId, input);
                if (sendCheck) {
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Campaign " + customerCampaign.getCustomerCampaign().getCampaignName() + " Selected...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Campaign Selected")
                            .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_NAME, customerCampaign.getCustomerCampaign().getCampaignName()).build());

                    if (language == Defines.LANGUAGE_ENGLISH) {
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | English Langugage Selected...");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "English Language Selected").build());
                        mainService.updateCustomerCampStats(srcId, conn, customerCampaign);
                        input.setCampaignId(customerCampaign.getCustomerCampaign().getCampaignId());
                        return customerCampaign.getCustomerCampaign().getEnglishScript();
                    } else if (language == Defines.LANGUAGE_ARABIC) {
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Arabic Langugage Selected...");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Arabic Languuage Selected").build());
                        mainService.updateCustomerCampStats(srcId, conn, customerCampaign);
                        input.setCampaignId(customerCampaign.getCustomerCampaign().getCampaignId());
                        return customerCampaign.getCustomerCampaign().getArabicScript();
                    }
                }
            }
        }

        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.MAX_COMMUNICATION_REACHED_FOR_ALL_CAMPAIGNS, "Campaigns Count: " + customerCampaignsList.size());
        CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
        throw ibe;
    }

    private String getAdsScriptWithoutCustAdsCheck(int srcId, String transId, byte language, List<CustomerCampaignsModel> customerCampaignsList, Connection conn, InputModel input) throws CommonException, InterfacesBusinessException {
        MainService mainService = new MainService();
        for (CustomerCampaignsModel customerCampaign : customerCampaignsList) {
            if (customerCampaign.getCampCount() < customerCampaign.getCustomerCampaign().getMaxcommunications()) {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Campaign " + customerCampaign.getCustomerCampaign().getCampaignName() + " Selected...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Campaign Selected")
                        .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_NAME, customerCampaign.getCustomerCampaign().getCampaignName()).build());
                if (language == Defines.LANGUAGE_ENGLISH) {
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | English Langugage Selected...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "English Language Selected").build());
                    mainService.updateCustomerCampStats(srcId, conn, customerCampaign);
                    input.setCampaignId(customerCampaign.getCustomerCampaign().getCampaignId());
                    return customerCampaign.getCustomerCampaign().getEnglishScript();
                } else if (language == Defines.LANGUAGE_ARABIC) {
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Arabic Langugage Selected...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Arabic Language Selected").build());
                    mainService.updateCustomerCampStats(srcId, conn, customerCampaign);
                    input.setCampaignId(customerCampaign.getCustomerCampaign().getCampaignId());
                    return customerCampaign.getCustomerCampaign().getArabicScript();
                }
            }
        }

        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.MAX_COMMUNICATION_REACHED_FOR_ALL_CAMPAIGNS, "Campaigns Count: " + customerCampaignsList.size());
        CommonLogger.businessLogger.error(ibe.getDetailedMessage());
        throw ibe;
    }

    public static Map<String, String> getSystemPropertiesByGroupID(int groupID) throws CommonException {
        Connection connection = null;
        Map<String, String> systemProperties = null;
        try {
            connection = DataSourceManger.getConnection();
            SystemPropertiesService systemPropertiesService = new SystemPropertiesService();
            systemProperties = systemPropertiesService.getSystemPropertiesByGroupID(connection, groupID);

        } catch (CommonException ce) {
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "groupID:" + groupID);
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
        return systemProperties;
    }

    public boolean consultSms(InputModel inputs, String ipAddress, ServicesModel service,
            SMSGroupModel defaultGroup, int runId, String logPrefix, int today, int totalyDays, String yesterdayColumn) throws CommonException, InterfacesBusinessException {
//        CommonLogger.businessLogger.info(logPrefix + "Starting consult request");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Consult Request").build());
        Connection connection = null;
        SMSGroupModel group = null;
        Boolean customerExist = false;
        boolean sendMSg = false;

        try {

            connection = DataSourceManger.getConnection();
            //Check destination MSISDN existance
            com.asset.contactstrategy.common.service.MainService mainService = new com.asset.contactstrategy.common.service.MainService();
            customerExist = mainService.checkCustomerExist(inputs.getDestinationMSISDN(), inputs.getTransId(), inputs.getCsMsgId());
            if (!customerExist) {
//                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE, 
//                        ErrorCodes.CONSULT_INTERFACE.DESTINATION_MSISDN_NOT_VALID, "MSISDN not exist in contact strategy");
//                CommonLogger.businessLogger.error(logPrefix + " || " + ibe.getDetailedMessage());
//                throw ibe;
                //Esmail.Anbar | 22/1/2018 | Updating to complete execution when customer is not in DWH Table
//                CommonLogger.businessLogger.info(logPrefix + " | csMsgId= " + inputs.getCsMsgId() + " | Customer not found in DWH Table... using default settings");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer not found in DWH Table... using default settings").build());
            }

            // If System type is "Control"
            if (service.getSystemType() == Defines.INTERFACES.SYSTEM_TYPE_CONTROL) {
                //Get customer configurations
//                CommonLogger.businessLogger.info(logPrefix + "Service:" + service.getServiceName() + " is control service");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service is control service")
                        .put(GeneralConstants.StructuredLogKeys.SERVER_NAME, service.getServiceName()).build());
                CustomerConfigAndGrpModel customerConfigAndGrpModel = new CustomerConfigAndGrpModel();
                customerConfigAndGrpModel.setMsisdn(inputs.getDestinationMSISDN());
                //Kashif fix 22/9 
                customerConfigAndGrpModel.setLastTwoDigits(Integer.parseInt(inputs.getDestinationMSISDN().substring(inputs.getDestinationMSISDN().length() - 2, inputs.getDestinationMSISDN().length())));
                customerConfigAndGrpModel = mainService.getCustomerSpecialConfigAndGrp(customerConfigAndGrpModel, runId, logPrefix);
                //Check Check "do not contact" flag for customer
                if (customerConfigAndGrpModel.isHasCustomConfig() && customerConfigAndGrpModel.getDoNotContact() == Defines.INT_TRUE) {
                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                            ErrorCodes.CONSULT_INTERFACE.CUSTOMER_HAS_DO_NOT_CONTACT_FLAG, "Do Not Contact Flag Exists for Customer");
                    CommonLogger.businessLogger.error(logPrefix + " || " + ibe.getDetailedMessage());
                    throw ibe;
                }
                //Check "do not contact" flag for group
                if (customerConfigAndGrpModel.isHasCustomConfig()) {
                    //kashif fix
                    customerConfigAndGrpModel.setGuardPeriod(defaultGroup.getGuardPeriod());
                } //Kashif fix
                // else if (customerConfigAndGrpModel.isExitsInGroup()) 
                else {
                    group = SystemLookups.SMS_GROUPS.get(customerConfigAndGrpModel.getGroupId());
                    if (group == null) {
                        // handle as default group
//                        CommonLogger.businessLogger.info(logPrefix + "Group_id: " + customerConfigAndGrpModel.getGroupId() + " not exist in COntact Strategy,MSISDN will be handled as default group");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Group not exist in COntact Strategy,MSISDN will be handled as default group")
                                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, customerConfigAndGrpModel.getGroupId()).build());
                        group = defaultGroup;
//                        CommonLogger.businessLogger.info(logPrefix + "default group configuration: dailythreshold: " + group.getDailyThreshold() + " ,weeklyThreshold: " + group.getWeeklyThreshold()
//                                + " ,monthlyThreshold: " + group.getMonthlyThreshold() + " do not contact:" + group.getDonotContact());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "default group configuration")
                                .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, group.getDailyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, group.getWeeklyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, group.getMonthlyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.DONOT_CONTACT, group.getDonotContact()).build());
                    }
//                    CommonLogger.businessLogger.info(logPrefix + " group configuration: dailythreshold: " + group.getDailyThreshold() + " ,weeklyThreshold: " + group.getWeeklyThreshold()
//                            + " ,monthlyThreshold: " + group.getMonthlyThreshold() + " do not contact:" + group.getDonotContact());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "default group configuration")
                            .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, group.getDailyThreshold())
                            .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, group.getWeeklyThreshold())
                            .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, group.getMonthlyThreshold())
                            .put(GeneralConstants.StructuredLogKeys.DONOT_CONTACT, group.getDonotContact()).build());

                    customerConfigAndGrpModel.setDailyThresholdSms(group.getDailyThreshold());
                    customerConfigAndGrpModel.setWeeklyThresholdSms(group.getWeeklyThreshold());
                    customerConfigAndGrpModel.setMonthlyThresholdSms(group.getMonthlyThreshold());
                    //kashif fix
                    customerConfigAndGrpModel.setGuardPeriod(group.getGuardPeriod());
                    if (group.getDonotContact() == Defines.INT_TRUE) {
                        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                                ErrorCodes.CONSULT_INTERFACE.GROUP_HAS_DO_NOT_CONTACT_FLAG, "Do Not Contact Flag Exists for Group");
                        CommonLogger.businessLogger.error(logPrefix + " || " + ibe.getDetailedMessage());
                        throw ibe;
                    }

                }

                SMSCustomerStatsticsService smsCustomerStatsticsService = new SMSCustomerStatsticsService();
                sendMSg = smsCustomerStatsticsService.updateCustomerSMSStatistics(connection, service, customerConfigAndGrpModel,
                        yesterdayColumn, today, totalyDays, customerConfigAndGrpModel.getDailyThresholdSms(),
                        customerConfigAndGrpModel.getWeeklyThresholdSms(), customerConfigAndGrpModel.getMonthlyThresholdSms(),
                        logPrefix, GeneralConstants.SRC_ID_CONSULT_INTERFACE, false, 0, customerConfigAndGrpModel.getGuardPeriod(), inputs.getCsMsgId());
//                if (sendMSg)
//                {
//                    updateSystemQuota(service, connection);
//                }
                DataSourceManger.commitConnection(connection);
            } else if (service.getSystemType() == Defines.INTERFACES.SYSTEM_TYPE_MONITOR) {
                //Update counters
//                CommonLogger.businessLogger.info(logPrefix + "Service:" + service.getServiceName() + " is monitor service");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service is Monitor Service")
                        .put(GeneralConstants.StructuredLogKeys.SERVER_NAME, service.getServiceName()).build());
//                updateSystemMonitorCounter(service, connection);
                updateServiceQuota(service.getServiceId(), 1, null, DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER);
                inputs.setUpdatedSystemMonitor(true);
//                updateSystemQuota(service, connection);

                DataSourceManger.commitConnection(connection);
                sendMSg = true;
            }
            return sendMSg;
        } catch (InterfacesBusinessException ibe) {
            throw ibe;
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            sendMOM(ce, GeneralConstants.SRC_ID_CONSULT_INTERFACE,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "DestMSISDN:" + inputs.getDestinationMSISDN() + ", ipAddress:" + ipAddress + ", serviceName:" + service.getServiceName()
                    + ", GroupName:" + defaultGroup.getGroupName() + ", runID:" + runId + ", logPrefix:" + logPrefix
                    + ", today:" + today + ", totalyDays:" + totalyDays + ", yesterdayColumn:" + yesterdayColumn);
            CommonLogger.businessLogger.error(logPrefix + " || " + ce.getErrorMsg());
            CommonLogger.errorLogger.error(logPrefix + " || " + ce.getErrorMsg(), ce);
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(logPrefix + " || " + e.getMessage());
            CommonLogger.errorLogger.error(logPrefix + " || " + e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNIQUE_NAME);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [closeConnection]" + ex);
                // throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR); // TODO
            }
        }
    }

    public HashMap<Integer, OriginatorTypeModel> getOriginatorsList() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            LookupService lookUpService = new LookupService();
            return lookUpService.getOriginatorsList(connection);
        } catch (CommonException ce) {
            CommonLogger.businessLogger.error(ce.getErrorMsg());
            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)), "getOriginatorsList()");
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

//    public int getServiceDailyQoutaCounter(ServicesModel service) throws CommonException {
//        Connection conn = null;
//        try {
//            conn = DataSourceManger.getConnection();
//            return getServiceDailyQoutaCounter(service, conn);
//        } catch (CommonException ce) {
//            sendMOM(ce, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
//                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "serviceName:" + service.getServiceName());
//            throw ce;
//        } catch (Exception e) {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            try {
//                DataSourceManger.closeConnection(conn);
//            } catch (CommonException ce) {
//                CommonLogger.businessLogger.error(ce.getMessage());
//                CommonLogger.errorLogger.error(ce.getMessage(), ce);
//            }
//        }
//    }
//    public long updateAndSelectServiceQouta(int serviceID, long serviceQouta) throws CommonException {
//        Connection conn = null;
//        try {
//            conn = DataSourceManger.getConnection();
//            serviceQouta = new ServicesManagmentService().updateAndSelectServiceQouta(conn, serviceID, serviceQouta);
//            conn.commit();
//            return serviceQouta;
//        } catch (CommonException ce) {
//            sendMOM(ce, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
//                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "ServiceId:" + serviceID);
//            throw ce;
//        } catch (Exception e) {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            try {
//                DataSourceManger.closeConnection(conn);
//            } catch (CommonException ce) {
//                CommonLogger.businessLogger.error(ce.getMessage());
//                CommonLogger.errorLogger.error(ce.getMessage(), ce);
//            }
//        }
//    }
//
//    private int getServiceDailyQoutaCounter(ServicesModel service, Connection connection) throws CommonException {
//        ServicesManagmentService servicesManagmentService = new ServicesManagmentService();
//        return servicesManagmentService.getDailyQoutaCounter(connection, service.getServiceId());
////        DataSourceManger.commitConnection(connection);
//    }
//
//    private void updateSystemQuota(ServicesModel service, Connection connection) throws CommonException {
//        ServicesManagmentService servicesManagmentService = new ServicesManagmentService();
//        servicesManagmentService.updateSystemQuota(connection, service.getServiceId());
////        DataSourceManger.commitConnection(connection);
//    }
//
//    private void updateSystemMonitorCounter(ServicesModel service, Connection connection) throws CommonException {
//        ServicesManagmentService servicesManagmentService = new ServicesManagmentService();
//        servicesManagmentService.updateSystemMonitorCounter(connection, service.getServiceId());
//        DataSourceManger.commitConnection(connection); // eslam.ahmed | 11-05-2020 | uncomment  
//    }
//
//    private void updateDoNotApplyCounter(ServicesModel service, Connection connection) throws CommonException {
//        ServicesManagmentService servicesManagmentService = new ServicesManagmentService();
//        servicesManagmentService.updateDoNotApplyCounter(connection, service.getServiceId());
//        DataSourceManger.commitConnection(connection); // eslam.ahmed | 11-05-2020 | uncomment 
//    }
    public String getMessageStatus(long MessageID, String MSISDN, HashMap<Integer, String> statusHashMap) throws CommonException {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSHistoryService sMSHistoryService = new SMSHistoryService();
            return sMSHistoryService.getMessageStatus(conn, MessageID, MSISDN, statusHashMap);
        } catch (CommonException ce) {
            sendMOM(ce, GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "MessageId:" + MessageID + ", MSISDN:" + MSISDN);
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    public String getMessagesForMSISDNwithDaySpan(RetrieveSMSsInputModel inputModel, HashMap<Integer, String> statusHashMap) throws CommonException {
        Connection conn = null;
        StringWriter response = new StringWriter();
        try {
            conn = DataSourceManger.getConnection();
            SMSHistoryService sMSHistoryService = new SMSHistoryService();

            JAXBContext jc = JAXBContext.newInstance(RetrieveSMSsOutputModel.class);

            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBElement<RetrieveSMSsOutputModel> jaxbElement = new JAXBElement<>(new QName("Response"), RetrieveSMSsOutputModel.class, sMSHistoryService.getMessagesForMSISDNwithDaySpan(conn, inputModel, statusHashMap));

            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            marshaller.marshal(jaxbElement, response);

            return response.toString();
        } catch (JAXBException e) {
            CommonException ce = wrapCommonException(e);
            sendMOM(ce, GeneralConstants.SRC_ID_RETRIEVE_SMSs_INTERFACE,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "MSISDN:" + inputModel.getMsisdn() + ", from:" + inputModel.getFrom() + ", to:" + inputModel.getTo());
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
        //return "";
    }

    public long sendSms(int srcId, InputModel input, String ipAddress, String transId, ArrayBlockingQueue archivingQueue) throws CommonException, InterruptedException, InterfacesBusinessException, Exception {

        boolean isMonitorCounterUpdated = false; // eslam.ahmed | 12-05-2020 | rollback service counters
        boolean isDoNotApplyCounterUpdated = false; // eslam.ahmed | 12-05-2020 | rollback service counters

//        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Started MainService.sendSMS()...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started MainService,sendSMS()").build());
        Connection connection = null;
        Connection adsConnection = null;
        ServicesModel service = SystemLookups.SERVICES.get(input.getSystemName());
        boolean archiveFlag = true;
//        String adsScript;

        try {
            connection = DataSourceManger.getConnection();

            //Check destination MSISDN existance
            CustomersModel customer = input.getCustomer();
//            CommonLogger.businessLogger.info(transId + " || " + "Validating Destination MSISDN Existance...");
//            if (customer == null)
//            {
//                customer = getCustomer(transId, input);
//                DWHProfileService dWHProfileService = new DWHProfileService();
//                CustomersModel customer = dWHProfileService.getCustomer(connection, input.getDestinationMSISDN());
            if (customer == null || customer.getMsisdn() == null || customer.getMsisdn().equals("")) {
//                    archiveFlag = false;
//                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, 
//                        ErrorCodes.SEND_SMS.DESTINATION_MSISDN_NOT_VALID, "Destination MSISDN doesnt Exist in DWH Table");
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
//                    throw ibe;
                //Esmail.Anbar | 22/1/2018 | Updating to complete execution when customer is not in DWH Table
                customer = new CustomersModel();
                customer.setMsisdn(input.getDestinationMSISDN());
                customer.setLastTwoDigits(Integer.valueOf(input.getDestinationMSISDN().substring(input.getDestinationMSISDN().length() - 2)));
                if (input.getLanguage() != null) {
                    customer.setLanguage(input.getLanguage().toString());
                } else {
                    customer.setLanguage(SystemLookups.SYSTEM_PROPERTIES.get(Defines.ARABIC_LANG));
                }
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Customer not found in DWH Table... using default settings");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer not Found in DWH Table, using default settings").build());
            } else {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Destination MSISDN Exists...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Destination MSISDN Exists").build());
            }
//            }

//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Fetchin Customer Configuration...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Fetching Customer configuration").build());
            CustomerService customerService = new CustomerService();
            CustomerConfigurationsModel customerConfigurations = customerService.getCustomerConfigurations(connection, input.getDestinationMSISDN());

            if (customerConfigurations == null) {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | No Customer Configuration for: " + input.getDestinationMSISDN());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No Customer Configuration")
                        .put(GeneralConstants.StructuredLogKeys.DESTINATION_MSISDN, input.getDestinationMSISDN()).build());
            } else {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Found Customer Configuration for: " + input.getDestinationMSISDN());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "default group configuration")
                        .put(GeneralConstants.StructuredLogKeys.DESTINATION_MSISDN, input.getDestinationMSISDN()).build());
            }

            if (input.isDoNotApply()) {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Do Not Apply Flag Exists...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Do Not Apply flag Exists").build());
//                CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Updating Do Not Apply Counter...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updating do not apply counter").build());

//                updateDoNotApplyCounter(service, connection);
                updateServiceQuota(service.getServiceId(), 1, null, DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER);
                isDoNotApplyCounterUpdated = true;// eslam.ahmed | 12-05-2020

                input.setUpdatedDoNotApply(true);
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Updated Do Not Apply Counter...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updated do not Apply Counter").build());
//                CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Updating Service Qouta...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updating service qouta").build());
//                updateSystemQuota(service, connection);
//                input.setUpdatedSystemQuota(true);
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Updated Service Qouta...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updated service qouta").build());

                adsConnection = DataSourceManger.getConnection();
                processAds(transId, adsConnection, input, service, srcId, customerService, customer, customerConfigurations);
//                adsConnection.close();

//                try
//                {
//                    CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Getting Ads Script...");
//                    adsScript = adsValidations(srcId, transId, connection, input, service, customerService, customer, customerConfigurations);
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Got Ads Script...");
//                    CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Adding Add to Message...");
//                    input.setMessageText(Utility.addAdsScriptToMsgText(input.getMessageText(), adsScript));
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Added Add to Message...");
//                }
//                catch (InterfacesBusinessException ibe)
//                {
//                    CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Buisness Error While Getting Ads Script");
//                }
//                catch (CommonException ce)
//                {
//                    CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Error While Getting Ads Script");
//                    CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ce.getErrorMsg());
//                }
//                catch (Exception e)
//                {
//                    CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Error While Getting Ads Script");
//                    CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + e.getMessage());
//                }
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Message Ready To Be Sent...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Ready to be Sent").build());
                return 1;
            } else {
                // If System type is "Control"
                if (service.getSystemType() == Defines.INTERFACES.SYSTEM_TYPE_CONTROL) {
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Service Type is Control...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service type is control").build());

//                    CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Getting User Group...");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getting user group").build());

                    SMSGroupModel group = SystemLookups.SMS_GROUPS.get(customer.getSmsGroupId());

                    if (group == null) {
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | No User Group... Getting Default Group");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No User Group... Getting Default Group").build());

                        group = new SMSGroupModel();
                        group.setGuardPeriod(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.GUARD_PERIOD)));
                        group.setMonthlyThreshold(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.MONTHLY_THRESHOLD)));
                        group.setWeeklyThreshold(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.WEEKLY_THRESHOLD)));
                        group.setDailyThreshold(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.DAILY_THRESHOLD)));
                        group.setGroupDescription(String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.GROUP_DESCRIPTION)));
                        group.setGroupId(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.GROUP_ID)));
                        group.setGroupName(String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.GROUP_NAME)));
                        group.setDonotContact(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.DO_NOT_CONTACT)));
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Default Group || GuardPeriod:" + group.getGuardPeriod() + " | MonthlyThreshold:" + group.getMonthlyThreshold()
//                                + " | WeeklyThreshold:" + group.getWeeklyThreshold() + " | DailyThreshold:" + group.getDailyThreshold()
//                                + " | GroupDescription:" + group.getGroupDescription() + " | DonotContact:" + group.getDonotContact());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Getting Default Group Stats")
                                .put(GeneralConstants.StructuredLogKeys.GUARD_PERIOD, group.getGuardPeriod())
                                .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, group.getMonthlyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, group.getWeeklyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, group.getDailyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.GROUP_DESCRIPTION, group.getGroupDescription())
                                .put(GeneralConstants.StructuredLogKeys.DONOT_CONTACT, group.getDonotContact()).build());

                    } else {
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | GroupName: " + group.getGroupName() + " || GuardPeriod:" + group.getGuardPeriod() + " | MonthlyThreshold:" + group.getMonthlyThreshold()
//                                + " | WeeklyThreshold:" + group.getWeeklyThreshold() + " | DailyThreshold:" + group.getDailyThreshold()
//                                + " | GroupDescription:" + group.getGroupDescription() + " | DonotContact:" + group.getDonotContact() + " | CreatedBy:" + group.getCreatedBy()
//                                + " | GroupId:" + group.getGroupId() + " | GroupPriority:" + group.getGroupPriority() + " | Status:" + group.getStatus());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found user group with following details...")
                                .put(GeneralConstants.StructuredLogKeys.GROUP_NAME, group.getGroupName())
                                .put(GeneralConstants.StructuredLogKeys.GUARD_PERIOD, group.getGuardPeriod())
                                .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, group.getMonthlyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, group.getWeeklyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, group.getDailyThreshold())
                                .put(GeneralConstants.StructuredLogKeys.GROUP_DESCRIPTION, group.getGroupDescription())
                                .put(GeneralConstants.StructuredLogKeys.DONOT_CONTACT, group.getDonotContact())
                                .put(GeneralConstants.StructuredLogKeys.CREATOR, group.getCreatedBy())
                                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, group.getGroupId())
                                .put(GeneralConstants.StructuredLogKeys.GROUP_PRIORITY, group.getGroupPriority())
                                .put(GeneralConstants.StructuredLogKeys.STATUS, group.getStatus()).build());
                    }
                    //CommonLogger.businessLogger.info(transId + " || " + "Got User Group...");

                    //Check "do not contact" flag for customer
//                    CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Checking Do Not Contact Flag For Customer & Group...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Checking Do Not Contact Flag For Customer & Group...").build());
                    if (customerConfigurations != null && customerConfigurations.getDoNotContact() == Defines.INT_TRUE) {
                        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                                ErrorCodes.SEND_SMS.CUSTOMER_HAS_DO_NOT_CONTACT_FLAG, "Customer Has Do Not Contact Flag...");
                        CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                        throw ibe;
                    } //Check "do not contact" flag for group
                    else if (group.getDonotContact() == Defines.INT_TRUE) {
                        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                                ErrorCodes.SEND_SMS.GROUP_HAS_DO_NOT_CONTACT_FLAG, "Group Has Do Not Contact Flag... ");
                        CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                        throw ibe;
                    }
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | No Do Not Contact Flag...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No Do Not Contact Flag..").build());

                    SmsCountersModel smsCountersModel = getCustomerSmsQuota(group, customerConfigurations);
                    smsCountersModel.setMsisdn(input.getDestinationMSISDN());
                    //BUGFIX "Sunday 25/9/2016" Esmail.Anbar
                    boolean hasMsgPriority = false;
                    if (input.getMessagePriority() != 0) {
                        hasMsgPriority = true;
                        smsCountersModel.setMessageCategory((byte) input.getMessagePriority());
                        input.setMessagePriority((byte) input.getMessagePriority());
                        if (input.getMessagePriority() == Defines.INTERFACES.MESSAGE_PRIORITY_HIGH) {
//                            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Message Category is Selected with value: High");
                            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Category is Selected with value: High").build());

                        } else {
//                            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Message Category is Selected with value: Normal");
                            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Category is Selected with value: Normal").build());

                        }
                    } else {
                        smsCountersModel.setMessageCategory((byte) service.getSystemCategory());
                        if (service.getSystemCategory() == Defines.INTERFACES.SYSTEM_CATEGORY_HIGH) {
//                            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | System Category is Selected with value: High");
                            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "System Categoty is Selected with value: High").build());

                        } else {
//                            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | System Category is Selected with value: Normal");
                            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "System Category is Selected with value: Normal").build());

                        }
                    }
                    //smsCountersModel.setMessageCategory((byte) (input.getMessagePriority() != 0 ? input.getMessagePriority() : service.getSystemCategory()));

//                    CommonLogger.businessLogger.debug(transId + " || " + "Checking Can Send SMS from Database Procedure ...");
//                    smsCountersModel = customerService.handleCustomerSmsCounters(connection, smsCountersModel, group.getGuardPeriod(), transId);
                    //Preparing Data for Dao
                    String logPrefix = transId + " || ";
                    CustomerConfigAndGrpModel customerConfigAndGrpModel = new CustomerConfigAndGrpModel();
                    customerConfigAndGrpModel.setMsisdn(customer.getMsisdn());
                    customerConfigAndGrpModel.setLastTwoDigits(customer.getLastTwoDigits());
                    int todayColumn = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.TODAY_COLUMN_NUM));
                    int totalyDays = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.LAST_COLUMN_NUM));
                    String yesterdayColumn = String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.YESTERDAY_COLUMN_NAME));

                    SMSCustomerStatsticsService smsCustomerStatsticsService = new SMSCustomerStatsticsService();
                    smsCountersModel.setCanSendSms(smsCustomerStatsticsService.updateCustomerSMSStatistics(connection, service, customerConfigAndGrpModel,
                            yesterdayColumn, todayColumn, totalyDays, smsCountersModel.getDailyQuota(), smsCountersModel.getWeeklyQuota(),
                            smsCountersModel.getMonthlyQuota(), logPrefix, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, hasMsgPriority,
                            smsCountersModel.getMessageCategory(), group.getGuardPeriod(), input.getCsMsgId()));
                    input.setUpdatedCustomerStatistics(true);

                    if (smsCountersModel.getCanSendSms()) {
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Can Send SMS to Customer ...");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Can Send SMS to Customer").build());

                        adsConnection = DataSourceManger.getConnection();
                        processAds(transId, adsConnection, input, service, srcId, customerService, customer, customerConfigurations);
//                        adsConnection.close();
//                        try 
//                        {
//                            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Getting Ads Script...");
//                            adsScript = adsValidations(srcId, transId, connection, input, service, customerService, customer, customerConfigurations);
//                            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Got Ads Script...");
//                            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Adding Add to Message...");
//                            input.setMessageText(Utility.addAdsScriptToMsgText(input.getMessageText(), adsScript));
//                            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Added Add to Message...");
//                        }
//                        catch (InterfacesBusinessException ibe)
//                        {
//                            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Buisness Error While Getting Ads Script");
//                        }
//                        catch (CommonException ce)
//                        {
//                            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Error While Getting Ads Script");
//                            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ce.getErrorMsg());
//                        }
//                        catch (Exception e)
//                        {
//                            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Error While Getting Ads Script");
//                            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + e.getMessage());
//                        }

//                        CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Updating Service Qouta...");
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updating service qouta").build());

//                        updateSystemQuota(service, connection);
//                        input.setUpdatedSystemQuota(true);
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Updated Service Qouta...");
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Message Ready To Be Sent...");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated Service Qouta, Message Ready to be Sent").build());

                        input.setViolateFlag(customerConfigAndGrpModel.isViolationFlag());
                        return 1;
                    } else {
                        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                                ErrorCodes.SEND_SMS.CANNOT_SEND_DUE_TO_FAILED_CS_RULES, "Cannt Send SMS to Customer Due to Failed CS Rules From Database Procedure Result");
                        CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                        throw ibe;
                    }

                } else if (service.getSystemType() == Defines.INTERFACES.SYSTEM_TYPE_MONITOR) {
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Service Type is Monitor...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Type is Monitor").build());

//                    CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Updating Service Monitor Counter...");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updating Service Monitor Counter").build());

//                    updateSystemMonitorCounter(service, connection);
                    updateServiceQuota(service.getServiceId(), 1, null, DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER);
                    isMonitorCounterUpdated = true; // eslam.ahmed | 12-05-2020

                    input.setUpdatedSystemMonitor(true);
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Updated Service Monitor Counter...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated Service Monitor Counter").build());

//                    CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Updating Service Qouta...");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updating Service Qouta").build());

//                    updateSystemQuota(service, connection);
//                    input.setUpdatedSystemQuota(true);
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Updated Service Qouta...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated Service Qouta").build());

                    adsConnection = DataSourceManger.getConnection();
                    processAds(transId, adsConnection, input, service, srcId, customerService, customer, customerConfigurations);
//                    adsConnection.close();
//                    try 
//                    {
//                        CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Getting Ads Script...");
//                        adsScript = adsValidations(srcId, transId, connection, input, service, customerService, customer, customerConfigurations);
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Got Ads Script... ");
//                        CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Adding Add to Message...");
//                        input.setMessageText(Utility.addAdsScriptToMsgText(input.getMessageText(), adsScript));
//                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Added Add to Message...");
//                    } 
//                    catch (InterfacesBusinessException ibe)
//                    {
//                        CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Buisness Error While Getting Ads Script");
//                    }
//                    catch (CommonException ce)
//                    {
//                        CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Error While Getting Ads Script");
//                        CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ce.getErrorMsg());
//                    }
//                    catch (Exception e)
//                    {
//                        CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Error While Getting Ads Script");
//                        CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + e.getMessage());
//                    }

//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Message Ready To Be Sent...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Ready To Be Sent").build());

                    return 1;
                }
            }

            return 0;
        } catch (InterfacesBusinessException ibe) {
            if (archiveFlag) {
                if (input.getSubmissionDate() == null) {
                    input.setSubmissionDate(new Date(System.currentTimeMillis()));
                }

//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Archiving Message");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archiving Message").build());

                archivingQueue.put(new SMSHistoryModel(
                        input.getCsMsgId(),
                        input.getDestinationMSISDN(),
                        new Timestamp(input.getSubmissionDate().getTime()),
                        null,
                        input.getMessageText(),
                        Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_CS_RULES,
                        null,
                        input.getConcatNumber(),
                        0,
                        0,
                        0,
                        0,
                        input.getMessageType(),
                        input.isDoNotApply() ? 1 : 0,
                        input.isViolateFlag() ? 1 : 0,
                        service.getSystemCategory(),
                        input.getMessagePriority(),
                        Integer.parseInt(String.valueOf(Long.parseLong(input.getDestinationMSISDN()) % 100)),
                        service.getVersionId(),
                        input.getOriginatorMSISDN(),
                        input.getOptionalParam1(),
                        input.getOptionalParam2(),
                        input.getOptionalParam3(),
                        input.getOptionalParam4(),
                        input.getOptionalParam5(),
                        0,
                        service.getQueueModel().getAppName(),
                        service.getDeliveryReport()));
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Archiving Successfull...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archiving Successful").build());

            }
            //CommonLogger.businessLogger.info(transId + " || " + "Attempting Rollback");
            DataSourceManger.rollBack(connection);
            DataSourceManger.rollBack(adsConnection);
//            if (isDoNotApplyCounterUpdated) { // eslam.ahmed | 12-05-2020 | rollback monitor and donot apply counters
//                decrementServiceCounters(service.getServiceId(), true, false, true);
//            } else if (isMonitorCounterUpdated) {
//                decrementServiceCounters(service.getServiceId(), false, true, true);
//            }
            String quotaColumn = null;
            if (isDoNotApplyCounterUpdated) {
                quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER;
            } else if (isMonitorCounterUpdated) {
                quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER;
            }
            if (quotaColumn != null && !quotaColumn.isEmpty()) {
                MainService.updateServiceQuota(
                        SystemLookups.SERVICES.get(input.getSystemName()).getServiceId(),
                        -1,
                        null,
                        quotaColumn);
            }
//            decrementServiceCounters(service.getServiceId(), false, false, true);
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Rollback Successfull...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Rollback Successful").build());

            throw ibe;
        } catch (CommonException ce) {
            sendMOM(ce, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "MSISDN:" + input.getDestinationMSISDN() + ", ipAddress:" + ipAddress + ", transId:" + transId);
            CommonLogger.businessLogger.error(ce.getErrorMsg());
            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
            //CommonLogger.businessLogger.info(transId + " || " + "Attempting Rollback");
            DataSourceManger.rollBack(connection);
            DataSourceManger.rollBack(adsConnection);
//            if (isDoNotApplyCounterUpdated) { // eslam.ahmed | 12-05-2020 | rollback monitor and donot apply counters
//                decrementServiceCounters(service.getServiceId(), true, false, true);
//            } else if (isMonitorCounterUpdated) {
//                decrementServiceCounters(service.getServiceId(), false, true, true);
//            }
            String quotaColumn = null;
            if (isDoNotApplyCounterUpdated) {
                quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER;
            } else if (isMonitorCounterUpdated) {
                quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER;
            }
            if (quotaColumn != null && !quotaColumn.isEmpty()) {
                MainService.updateServiceQuota(
                        SystemLookups.SERVICES.get(input.getSystemName()).getServiceId(),
                        -1,
                        null,
                        quotaColumn);
            }
//            decrementServiceCounters(service.getServiceId(), false, false, true);
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Rollback Successfull...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Rollback Successful").build());

            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            //CommonLogger.businessLogger.info(transId + " || " + "Attempting Rollback");
            DataSourceManger.rollBack(connection);
            DataSourceManger.rollBack(adsConnection);
//            if (isDoNotApplyCounterUpdated) { // eslam.ahmed | 12-05-2020 | rollback monitor and donot apply counters
//                decrementServiceCounters(service.getServiceId(), true, false, true);
//            } else if (isMonitorCounterUpdated) {
//                decrementServiceCounters(service.getServiceId(), false, true, true);
//            } else {
//                decrementServiceCounters(service.getServiceId(), false, false, true);
//            }
            String quotaColumn = null;
            if (isDoNotApplyCounterUpdated) {
                quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER;
            } else if (isMonitorCounterUpdated) {
                quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER;
            }
            if (quotaColumn != null && !quotaColumn.isEmpty()) {
                MainService.updateServiceQuota(
                        SystemLookups.SERVICES.get(input.getSystemName()).getServiceId(),
                        -1,
                        null,
                        quotaColumn);
            }
            //decrementServiceCounters(service.getServiceId(), false, false, true);
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Rollback Successfull...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Rollback Successful").build());

            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.commitConnection(connection);
                DataSourceManger.commitConnection(adsConnection);
            } catch (CommonException ce) {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Error while commiting Connection");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error while commiting Connection").build());

                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
            try {
                DataSourceManger.closeConnection(connection);
                DataSourceManger.closeConnection(adsConnection);
            } catch (CommonException ce) {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Error while closing Connection");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Error while closing Connection").build());

                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Ended MainService.sendSMS()...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended MainService.sendSMS()").build());

        }
    }

    private void processAds(String transId, Connection connection, InputModel input, ServicesModel service, int srcId,
            CustomerService customerService, CustomersModel customer, CustomerConfigurationsModel customerConfigurations) {
        try {
//            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Getting Ads Script...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Getting Ads Script").build());

            String adsScript = adsValidations(srcId, transId, connection, input, service, customerService, customer, customerConfigurations);
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Got Ads Script... ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Got Ads Script").build());

//            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Adding Add to Message...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Adding Add to Message").build());

            input.setMessageText(Utility.addAdsScriptToMsgText(input.getMessageText(), adsScript));
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Added Add to Message...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Added Add to Message").build());

        } catch (InterfacesBusinessException ibe) {
            CommonLogger.businessLogger.error("Buisness Error While Getting Ads Script ... " + ibe.getDetailedMessage());
        } catch (CommonException ce) {
            CommonLogger.businessLogger.error("Error While Getting Ads Script ... " + ce.getErrorMsg());
//            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ce.getErrorMsg());
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error While Getting Ads Script ... " + e.getMessage());
//            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + e.getMessage());
        }
    }

    private String adsValidations(int srcId, String transId, Connection connection, InputModel input, ServicesModel service,
            CustomerService customerService, CustomersModel customer, CustomerConfigurationsModel customerConfigurations) throws InterfacesBusinessException, CommonException {
        /**
         * @Fixing_AdsUpdateCounter_With_No_Campaign
         * @Esmail.Anbar Date: 10/11/2016
         */
        String adsScript = "";
        // Check system is supporting ads or not
//        CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Checking if Service Supports Ads");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Checking if Service Supports Ads").build());

        if (service.getSupportAds() == Defines.INT_FALSE) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.SYSTEM_DOESNT_SUPPORT_ADS, null);
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Service Supports Ads");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Supports Ads").build());

        //Check if service exist in any campaign
//        CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Checking if Service Has Any Campaigns");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Checking if Service has any Campaign").build());

        if (service.getCampiagnModel() == null || service.getCampiagnModel().isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.SYSTEM_DOESNT_EXIST_IN_ANY_CAMPAIGN, null);
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Service Has Campaigns");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service has Campaign").build());

        //Check if customer exist in any campaign
//        CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Checking if Customer Exists in Service Campaigns");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Checking if Customer Exists in Service Campaigns").build());

        List<CustomerCampaignsModel> customerCampaignsStatsList = customerService.getCustomerCampaignsStatsList(connection, input.getDestinationMSISDN(), service.getCampiagnModel());
        if (customerCampaignsStatsList == null || customerCampaignsStatsList.isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.CUSTOMER_DOESNT_EXIST_IN_ANY_CAMPAIGN, null);
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Customer Exists in Service Campaigns");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Exists in Service Campaigns").build());

        //Check Check "do not contact" flag for customer
//        CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Checking if Customer Has Do Not Contact Flag");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Checking if Customer has Donot Contact Flag").build());

        if (customerConfigurations != null && customerConfigurations.getDoNotContact() == Defines.INT_TRUE) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                    ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.CUSTOMER_HAS_DO_NOT_CONTACT_FLAG, "Customer Has Do Not Contact Flag");
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
//        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Customer Has no Do Not Contact Flag");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Has no Do Not Contact Flag").build());

        //Check "ADS CONSULT COUNTER" flag for group (Update counters or not)
//        CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Check \"ADS CONSULT COUNTER\" flag for Service");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Check ADS CONSULT COUNTER flag for Service").build());

        if (srcId == GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE && service.getAdsConsultCounter() == Defines.INT_FALSE) {
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | No Ads Consult Counter for Service");
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Getting Ad Script...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No Ads Consult Counter for Service, Getting Ad Script ").build());

            /**
             * @Fixing_AdsUpdateCounter_With_No_Campaign
             * @Esmail.Anbar Date: 10/11/2016
             */
            return getAdsScriptWithoutCustAdsCheck(srcId, transId, input.getLanguage(), customerCampaignsStatsList, connection, input);
        } else {
            // Check daily ,weekly and monthly ads counters
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Found Ads Consult Counter for Service");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found Ads Consult Counter for Service").build());

//            CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Checking Can Send ADs from Database Procedure ...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Checking Can Send ADs from Database Procedure").build());

            int todayColumn = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.TODAY_COLUMN_NUM));
            int totalyDays = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.LAST_COLUMN_NUM));
            String yesterdayColumn = String.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.YESTERDAY_COLUMN_NAME));

            /**
             * @Fixing_AdsUpdateCounter_With_No_Campaign
             * @Esmail.Anbar Date: 10/11/2016
             */
            AdsCountersModel adsCountersModel = getCustomerAdsQuota(transId, customer, customerConfigurations);
            adsScript = getAdsScript(srcId, transId, input.getLanguage(), customerCampaignsStatsList, connection, customerService, adsCountersModel, yesterdayColumn, todayColumn, totalyDays, input);
            //adsCountersModel.setCanSendSms(customerService.handleCustomerAdsCounters(connection, adsCountersModel, yesterdayColumn, todayColumn, totalyDays, transId));

            if (!adsScript.equals("") && !adsScript.isEmpty()) {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Can Send Ads ...");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Can Send Ads").build());

//                CommonLogger.businessLogger.debug(transId + " || csMsgId: " + input.getCsMsgId() + " | Getting Ad Script...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Getting Ad Script").build());

                return adsScript;
//                return getAdsScript(srcId, transId, input.getLanguage(), customerCampaignsStatsList, connection);
            } else {
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Cannot SEND Ads ... Faild CS Rules");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Cannot SEND Ads ... Faild CS Rules").build());

                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE,
                        ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.CANNOT_SEND_DUE_TO_FAILED_CS_RULES, "Procedure returned cannot Send Ad");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }
        }
    }

    private AdsCountersModel getCustomerAdsQuota(String transId, CustomersModel customer, CustomerConfigurationsModel customerConfigurations) {
//        CommonLogger.businessLogger.debug(transId + " || " + "Getting Customer Qouta ...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Getting Customer Qouta").build());

        AdsCountersModel adsCounters = new AdsCountersModel();
        if (customerConfigurations != null) {
            adsCounters.setDailyQuota(customerConfigurations.getDailyThresholdAds());
            adsCounters.setWeeklyQuota(customerConfigurations.getWeeklyThresholdAds());
            adsCounters.setMonthlyQuota(customerConfigurations.getMonthlyThresholdAds());
            adsCounters.setMsisdn(customer.getMsisdn());
//            CommonLogger.businessLogger.debug(transId + " || " + "Customer Configuration Selected for Ads Qouta... DailyThreshold: " + adsCounters.getDailyQuota()
//                    + " | WeeklyThreshold: " + adsCounters.getWeeklyQuota() + " | MonthlyThreshold: " + adsCounters.getMonthlyQuota());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Configuration Selected for Ads Qouta")
                    .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, adsCounters.getDailyQuota())
                    .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, adsCounters.getWeeklyQuota())
                    .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, adsCounters.getMonthlyQuota()).build());

        } else {
            AdsGroupModel adsGroup = SystemLookups.ADS_GROUPS.get(customer.getAdsGroupId());
            if (adsGroup != null) {
//                CommonLogger.businessLogger.debug(transId + " || " + "Ads Group Selected... Name: " + adsGroup.getGroupName()
//                        + " | DailyThreshold: " + adsGroup.getDailyThreshold() + " | WeeklyThreshold: " + adsGroup.getWeeklyThreshold()
//                        + " | MonthlyThreshold: " + adsGroup.getMonthlyThreshold());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ads Group Selected")
                        .put(GeneralConstants.StructuredLogKeys.GROUP_NAME, adsGroup.getGroupName())
                        .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, adsCounters.getDailyQuota())
                        .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, adsCounters.getWeeklyQuota())
                        .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, adsCounters.getMonthlyQuota()).build());
                adsCounters.setDailyQuota(adsGroup.getDailyThreshold());
                adsCounters.setWeeklyQuota(adsGroup.getWeeklyThreshold());
                adsCounters.setMonthlyQuota(adsGroup.getMonthlyThreshold());
                adsCounters.setMsisdn(customer.getMsisdn());
            } else {
                adsCounters.setDailyQuota(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.AD_GROUP_DAILY_THRESHOLD)));
                adsCounters.setWeeklyQuota(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.AD_GROUP_WEEKLY_THRESHOLD)));
                adsCounters.setMonthlyQuota(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.AD_GROUP_MONTHLY_THRESHOLD)));
                adsCounters.setMsisdn(customer.getMsisdn());
//                CommonLogger.businessLogger.debug(transId + " || " + "Default Ads Group Selected... DailyThreshold: " + adsCounters.getDailyQuota()
//                        + " | WeeklyThreshold: " + adsCounters.getWeeklyQuota() + " | MonthlyThreshold: " + adsCounters.getMonthlyQuota());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Default Ads Group Selected")
                        .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, adsCounters.getDailyQuota())
                        .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, adsCounters.getWeeklyQuota())
                        .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, adsCounters.getMonthlyQuota()).build());
            }
        }
        return adsCounters;
    }

    private SmsCountersModel getCustomerSmsQuota(SMSGroupModel smsGroup, CustomerConfigurationsModel customerConfigurations) throws CommonException {
        SmsCountersModel smsCounters = new SmsCountersModel();
        if (customerConfigurations != null) {
            smsCounters.setDailyQuota(customerConfigurations.getDailyThresholdSms());
            smsCounters.setWeeklyQuota(customerConfigurations.getWeeklyThresholdSms());
            smsCounters.setMonthlyQuota(customerConfigurations.getMonthlyThresholdSms());
        } else {
            if (smsGroup != null) {
                smsCounters.setDailyQuota(smsGroup.getDailyThreshold());
                smsCounters.setWeeklyQuota(smsGroup.getWeeklyThreshold());
                smsCounters.setMonthlyQuota(smsGroup.getMonthlyThreshold());
            } else {
                smsCounters.setDailyQuota(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.DAILY_THRESHOLD)));
                smsCounters.setWeeklyQuota(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.WEEKLY_THRESHOLD)));
                smsCounters.setMonthlyQuota(Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.MONTHLY_THRESHOLD)));
            }
        }
        return smsCounters;
    }

    //Old Method not used anymore ... Esmail.Anbar
//    public void sendMessageBatch(ArrayList<SmsBusinessModel> objectsArray, String queueName, StringBuilder csMsgIds) throws CommonException
//    {
//        Connection conn = null;
//        try
//        {
//            conn = DataSourceManger.getConnection();
//            DBQueueService queueService = new DBQueueService();
//            queueService.sendMessageBatch(conn, objectsArray, queueName,  csMsgIds);
//        }
//        catch (CommonException ce) 
//        {
//            sendMOM(ce, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, 
//                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "queueName:" + queueName + ", csMsgIds:" + csMsgIds);
//            throw ce;
//        }
//        catch (Exception e) 
//        {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        }
//        finally
//        {
//            try 
//            {
//                DataSourceManger.closeConnection(conn);
//            } 
//            catch (CommonException ce) 
//            {
//                CommonLogger.businessLogger.error(ce.getMessage());
//                CommonLogger.errorLogger.error(ce.getMessage(), ce);
//            }
//        }
//        
//    }
    public void sendSingleMessage(SMS smsObject, String queueName, StringBuilder csMsgIds) throws CommonException {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            DBQueueService queueService = new DBQueueService();
            queueService.sendSingleMessage(conn, smsObject, queueName, csMsgIds);
            DataSourceManger.commitConnection(conn);
        } catch (CommonException ce) {
            DataSourceManger.rollBack(conn);
            sendMOM(ce, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "queueName:" + queueName + ", csMsgIds:" + csMsgIds);
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(conn);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }

    }

    public void archiveMessage(ArrayList<SMSHistoryModel> msgs) throws CommonException {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSHistoryService sMSHistoryService = new SMSHistoryService();
            sMSHistoryService.archiveMsg(conn, msgs);
            DataSourceManger.commitConnection(conn);
        } catch (CommonException ce) {
            DataSourceManger.rollBack(conn);
            sendMOM(ce, GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(conn);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    public void logAction(ArrayList<InterfacesLogModel> logs) throws CommonException {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            InterfacesLogService interfacesLogService = new InterfacesLogService();
            interfacesLogService.logAction(conn, logs);
            DataSourceManger.commitConnection(conn);
        } catch (CommonException ce) {
            sendMOM(ce, 0,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "");
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    public void logRESTAction(ArrayList<RESTLogModel> logs) throws CommonException {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            RESTLogService restLogService = new RESTLogService();
            restLogService.logRESTAction(conn, logs);
            DataSourceManger.commitConnection(conn);
        } catch (CommonException ce) {
            sendMOM(ce, 0,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "");
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    public void updateCustomerCampStats(int srcId, Connection conn, CustomerCampaignsModel custCampModel) throws CommonException {
//        CommonLogger.businessLogger.debug(MainService.class.getName() + " || " + "Starting Getting [updateCustomerCampStats]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerCampStats Started").build());
//        Connection conn = null;
        try {
//            conn = DataSourceManger.getConnection();
            CustomerService customService = new CustomerService();
            customService.updateCustomerCampStats(conn, custCampModel);
//            DataSourceManger.commitConnection(conn);
        } catch (CommonException ce) {
            sendMOM(ce, srcId,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "CampaignName:" + custCampModel.getCustomerCampaign().getCampaignName());
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
//        finally
//        {
//            try 
//            {
//                DataSourceManger.closeConnection(conn);
//            } 
//            catch (CommonException ce) 
//            {
//                CommonLogger.businessLogger.error(ce.getMessage());
//                CommonLogger.errorLogger.error(ce.getMessage(), ce);
//            }
//        }
//        CommonLogger.businessLogger.debug(MainService.class.getName() + " || " + "End Getting [updateCustomerCampStats]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerCampStats Ended").build());

    }

    private static CommonException wrapCommonException(Exception e) {
        CommonLogger.businessLogger.info("CommonException.handleException() Invoked...");

        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        CommonException DeliveryAggregationException;

        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            DeliveryAggregationException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            DeliveryAggregationException = new CommonException("Sql exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.ERROR_SQL);
        } else if (e instanceof InterruptedException) {
            DeliveryAggregationException = new CommonException("Interrupted Exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof NullPointerException) {
            DeliveryAggregationException = new CommonException("Null Pointer Exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.NULL_POINTER_ERROR);
        }// Handle other exception types
        else {
            DeliveryAggregationException = new CommonException("Unknown error in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        CommonLogger.businessLogger.info("CommonException.handleException() Ended...");
        return DeliveryAggregationException;
    }

    private static void sendMOM(CommonException ce, int engSrcId, int preceivedSeverity, String errorParams) {
        CommonLogger.businessLogger.info("CommonException.handleException() Invoked...");

        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();

        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(preceivedSeverity);
        errorModel.setEngineSrcID(engSrcId);
        errorModel.setErrorCode(ce.getErrorCode());
        errorModel.setModuleName(stackTraceElement[2].getClassName());
        errorModel.setFunctionName(stackTraceElement[2].getMethodName());
        errorModel.setErrorMessage(ce.getErrorMsg());
        errorModel.setErrorParams(errorParams);

        Utility.sendMOMAlarem(errorModel);

        CommonLogger.businessLogger.info("CommonException.handleException() Ended...");
    }

    //2595 smpp
    public String getMessageStatus(RetrieveMessageInputModel input, HashMap<Integer, String> statusHashMap) throws CommonException {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSHistoryService sMSHistoryService = new SMSHistoryService();
            return sMSHistoryService.getMessageStatus(conn, input, statusHashMap);
        } catch (CommonException ce) {
            sendMOM(ce, GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "MessageId:" + input.getMessageID() + ", MSISDN:" + input.getMsisdn());
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    //CSPhase1.5 | Esmail.Anbar | Adding Template Update
    public HashMap<Integer, TemplateModel> getAllTemplates() throws CommonException {

        Connection connection = null;
        HashMap<Integer, TemplateModel> allTemplates = null;
        try {
            connection = DataSourceManger.getConnection();
            TemplatesService templatesService = new TemplatesService();
            allTemplates = templatesService.getTemplates(connection);

        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "getAllTemplates()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
        return allTemplates;
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerSMSStats(String logPrefix, String msisdn, int day) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            new SMSCustomerStatsticsService().decrementCustomerSMSStats(connection, logPrefix, msisdn, day);
            connection.commit();
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(ce.getMessage());
            CommonLogger.errorLogger.error(ce.getMessage(), ce);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "decrementCustomerStats()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerAdsStats(String logPrefix, String msisdn, int day) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            new CustomerService().decrementCustomerAdsStats(connection, logPrefix, msisdn, day);
            connection.commit();
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(ce.getMessage());
            CommonLogger.errorLogger.error(ce.getMessage(), ce);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "decrementCustomerStats()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerCampaignStats(String logPrefix, String msisdn, int campaignId) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            new CustomerService().decrementCustomerCampaignStats(connection, logPrefix, msisdn, campaignId);
            connection.commit();
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(ce.getMessage());
            CommonLogger.errorLogger.error(ce.getMessage(), ce);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "decrementCustomerStats()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

//    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
//    public void decrementServiceCounters(int systemID, boolean decrementDoNotApply,
//            boolean decrementMonitorCounter, boolean decrementDailyQouta) throws CommonException {
//
//        Connection connection = null;
//        try {
//            connection = DataSourceManger.getConnection();
//            new ServicesManagmentService().decrementServiceCounters(connection, systemID, decrementDoNotApply, decrementMonitorCounter, decrementDailyQouta);
//            connection.commit();
//        } catch (CommonException ce) {
//            DataSourceManger.rollBack(connection);
//            CommonLogger.businessLogger.error(ce.getMessage());
//            CommonLogger.errorLogger.error(ce.getMessage(), ce);
//            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
//                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "decrementServiceCounters()");
//            throw ce;
//        } catch (Exception e) {
//            DataSourceManger.rollBack(connection);
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            try {
//                DataSourceManger.closeConnection(connection);
//            } catch (CommonException ce) {
//                CommonLogger.businessLogger.error(ce.getMessage());
//                CommonLogger.errorLogger.error(ce.getMessage(), ce);
//            }
//        }
//    }
    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void updateArchivedMessageStatus(long csMsgId, int status, int msisdnModX) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            new SMSHistoryService().updateArchivedMessageStatus(connection, csMsgId, status, msisdnModX);
            connection.commit();
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(ce.getMessage());
            CommonLogger.errorLogger.error(ce.getMessage(), ce);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "updateArchivedMessageStatus()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding Language Update for Templates
    public CustomersModel getCustomer(InputModel input) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            DWHProfileService dWHProfileService = new DWHProfileService();
            CustomersModel customer = dWHProfileService.getCustomer(connection, input.getDestinationMSISDN());
            return customer;
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(ce.getMessage());
            CommonLogger.errorLogger.error(ce.getMessage(), ce);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "getCustomer()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding Language Update for Templates
    public HashMap<String, CustomersModel> getCustomersMap(ArrayList<String> MSISDNs) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            HashMap<String, CustomersModel> customersMap = new DWHProfileService().getCustomersMap(connection, MSISDNs);
            return customersMap;
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(ce.getMessage());
            CommonLogger.errorLogger.error(ce.getMessage(), ce);
            sendMOM(ce, GeneralConstants.SRC_ID_GLOABL_SETTINGS,
                    Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "getCustomersLanguageHashMap()");
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }

    public static boolean updateServiceQuota(
            Integer serviceId,
            Integer value,
            Long maxValue,
            String counterColumn) throws Exception {

        
        if (SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.UPDATE_SERVICE_QUOTA_FLAG).equalsIgnoreCase(Defines.STRING_FALSE)) {
            return true;
        }

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            boolean result = updateServiceQuotaInternal(connection, serviceId, value, maxValue, counterColumn);
            DataSourceManger.commitConnection(connection);
            return result;
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
        
    }
    
    private static boolean updateServiceQuotaInternal(
            Connection connection,
            Integer serviceId,
            Integer value,
            Long maxValue,
            String counterColumn) throws Exception {
        if (value == null) {
            throw new Exception("Invalid value input for 'updateServiceQuota' | value: " + value);
        }
//        else if (value > 0) {
//            if (maxValue == null || maxValue < 0) {
//                throw new Exception("Invalid maxValue input for 'updateServiceQuota' | maxValue: " + maxValue);
//            }
//        }
//        int updateResult = ServicesManagmentService.updateServiceQuota(connection, serviceId, value, maxValue, counterColumn);
        int updateResult = InstanceVariables.serviceQuotaUpdaterThread.updateServiceQuota(serviceId, value, maxValue, counterColumn);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated quota resutlt")
                .put(GeneralConstants.StructuredLogKeys.VALUE, updateResult).build());
        switch (updateResult) {
            case Statement.SUCCESS_NO_INFO://success but cant know what happened
            case 1://success we tamam
                return true;
            case 0://failed because no record for today or quota exceeded
//                int recordCount = ServicesManagmentService.getServiceQuota(connection, serviceId, counterColumn);
//                if (recordCount <= 0) {
                    //no records found
                    //insert a new record then issue the same update
                    try {
                        ServicesManagmentService.insertServiceQuota(connection, serviceId, value, counterColumn);
                        DataSourceManger.commitConnection(connection);
                        InstanceVariables.serviceQuotaUpdaterThread.run();
                    } catch (Exception e) {
                        CommonLogger.businessLogger.error(e.getMessage());
                        CommonLogger.errorLogger.error(e.getMessage(), e);
                        if (!(e.getMessage() != null && e.getMessage().contains("ORA-00001: unique constraint"))) {
                            throw e;
                        }
                    }
                    
                    return updateServiceQuotaInternal(connection, serviceId, value, maxValue, counterColumn);
//                    return true;
//                } else if (recordCount > 1) {
//                    //more than one record for same service and same day
//                    ServicesManagmentService.adjustDuplicatedServiceQuota(connection, serviceId);
//                    return false;
//                }
//                return false;
            case Statement.EXECUTE_FAILED://failed from database but no exception happened
            default://result < -3 or result > 2... 
                if (updateResult > 1) {
                    //more than one record for same service and same day
                    ServicesManagmentService.adjustDuplicatedServiceQuota(connection, serviceId);
                    return true;
                } else {
                    return false;
                }
        }
    }
    
    public static void updateServiceQuota(HashMap<Integer, ServiceQuotaCounter> serviceQuota) throws Exception {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServicesManagmentService.updateServiceQuota(connection,serviceQuota);
            DataSourceManger.commitConnection(connection);
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }
    
    public static HashMap<Integer, ArrayList<ServiceQuotaCounter>> getServiceQuota() throws Exception {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            return ServicesManagmentService.getServiceQuota(connection);
        } catch (CommonException ce) {
            DataSourceManger.rollBack(connection);
            throw ce;
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                DataSourceManger.closeConnection(connection);
            } catch (CommonException ce) {
                CommonLogger.businessLogger.error(ce.getMessage());
                CommonLogger.errorLogger.error(ce.getMessage(), ce);
            }
        }
    }
}

//    private String getAdsScript(InputModel inputModel, ServicesModel service) throws CommonException {
//        //Validate Language
//        if (inputModel.getLanguage() != Defines.LANGUAGE_ENGLISH && inputModel.getLanguage() != Defines.LANGUAGE_ARABIC) {
//            throw new CommonException(ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.LANGUAGE_NOT_DEFINED);
//        }
//        // Check system is supporting ads or not
//        if (service.getSupportAds() == Defines.SYSTEM_NOT_SUPPORT_ADS) {
//            throw new CommonException(ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.SYSTEM_DIDNT_SUPPORT_ADS);
//        }
//        return "";
//    }
// If System type is "Control"
//            if (service.getSystemType() == Defines.SYSTEM_TYPE_CONTROL) {
//                // If System category is "Normal"
//                if (service.getSystemCategory() == Defines.SYSTEM_CATEGORY_NORMAL) {
//
//                } else if (service.getSystemCategory() == Defines.SYSTEM_CATEGORY_HIGH) {
//                    if (service.getConsultCounter() == Defines.SYSTEM_ADS_CONSULT_COUNTER_NO) {
//                        return getAdsScript(language, customerCampaignsList);
//                    } else if (service.getConsultCounter() == Defines.SYSTEM_ADS_CONSULT_COUNTER_YES) {
//                        // Update counters
//                        customerService.updateAdsCounters(connection, customer.getMsisdn());
//                        DataSourceManger.commitConnection(connection);
//                        return getAdsScript(language, customerCampaignsList);
//                    }
//                }
//            } else if (service.getSystemType() == Defines.SYSTEM_TYPE_MONITOR) {
//                // Update counters
//                customerService.updateAdsCounters(connection, customer.getMsisdn());
//                DataSourceManger.commitConnection(connection);
//                return getAdsScript(language, customerCampaignsList);
//            }
//            return "";
//    public long enqueeMsg(InputModel input, String ipaddr,ServicesModel service,byte messageViolationFlag) throws CommonException {
//        Connection connection = null;
//        try {
//            connection = DataSourceManger.getConnection();
//            return enqueeMsg(connection, input, ipaddr, service, messageViolationFlag);
//        } catch (CommonException ex) {
//            throw ex;
//        } catch (Exception ex) {
//            DataSourceManger.rollBack(connection);
//            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [consultAds]" + ex);
//            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            if (connection != null) {
//                try {
//                    DataSourceManger.closeConnection(connection);
//                } catch (Exception ex) {
//                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [consultAds]" + ex);
//                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR); // TODO
//                }
//            }
//        }
//    }
//    public long enqueeMsg(Connection connection, InputModel input, String ipAddress,ServicesModel service,byte messageViolationFlag) throws CommonException {
//        try {
//            long csMsgId =  System.currentTimeMillis();
//            int concMsgCount=Utility.calcConcMsgCount(input.getMessageText().length(), input.getLanguage());
//            SmsModel smsModel = new SmsModel(csMsgId, 
//                    service.getQueueModel().getAppName(), 
//                    input.getOriginatorMSISDN(), 
//                    input.getDestinationMSISDN(), 
//                    input.getMessageText(), 
//                    input.getMessageType(), 
//                    input.getOriginatorType(), 
//                    input.getLanguage(), 
//                    0, 
//                    0,
//                    concMsgCount, 
//                    0, 
//                    ipAddress, 
//                    input.getTrackingId(), 
//                    input.isDoNotApply()?Defines.DO_NOT_APPLY_YES:Defines.DO_NOT_APPLY_NO, 
//                    messageViolationFlag,
//                    (byte) service.getSystemCategory(), 
//                    input.getMessagePriority(),
//                    (byte)  service.getServiceId(),
//                    (byte)  service.getDeliveryReport(), 
//                    input.getOptionalParam1(), 
//                    input.getOptionalParam2(), 
//                    input.getOptionalParam3(), 
//                    input.getOptionalParam4(), 
//                    input.getOptionalParam5());
//            
//            Initializer.getSmsQueue().add(smsModel);
//            return csMsgId;
//        } catch (Exception ex) {
//            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [consultAds]" + ex);
//            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        }
//    }
