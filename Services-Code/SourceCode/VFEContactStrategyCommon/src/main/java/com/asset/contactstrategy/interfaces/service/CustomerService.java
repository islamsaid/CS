/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.service;

import com.asset.contactstrategy.common.dao.SMSCustomerStatsticsDao;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.interfaces.dao.CustomersDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.interfaces.models.AdsCountersModel;
import com.asset.contactstrategy.interfaces.models.AdsGroupModel;
import com.asset.contactstrategy.interfaces.models.CampaignModel;
import com.asset.contactstrategy.interfaces.models.CustomerCampaignsModel;
import com.asset.contactstrategy.interfaces.models.CustomerConfigAndGrpModel;
import com.asset.contactstrategy.interfaces.models.CustomerConfigurationsModel;
import com.asset.contactstrategy.interfaces.models.CustomersAdsGroupsModel;
import com.asset.contactstrategy.interfaces.models.CustomersModel;
import com.asset.contactstrategy.interfaces.models.CustomersSmsGroupsModel;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.models.SmsCountersModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author hazem.fekry
 */
public class CustomerService {

    public CustomerConfigurationsModel getCustomerConfigurations(Connection connection, String MSISDN) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [getCustomer]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCustomer Started").build());
        CustomersDAO customersDAO = new CustomersDAO();
        CustomerConfigurationsModel customerConfigurations = customersDAO.getCustomerConfigurations(connection, MSISDN);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [getCustomer]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCustomer Ended").build());

        return customerConfigurations;
    }

    public List<CustomerCampaignsModel> getCustomerCampaignsStatsList(Connection connection, String MSISDN, ArrayList<CampaignModel> campignsMap) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [isCustomerInCampaign]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "isCustomerInCampaign Started").build());

        CustomersDAO customersDAO = new CustomersDAO();
        List<CustomerCampaignsModel> customerCampaignsList = customersDAO.getCustomerCampaignsStatsList(connection, MSISDN, campignsMap);//, getCampaignIds(campignsMap));
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [isCustomerInCampaign]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "isCustomerInCampaign Ended").build());

        return customerCampaignsList;
    }

    private String getCampaignIds(ArrayList<CampaignModel> campignsList) {
        StringBuilder campaignNames = new StringBuilder("");
        for (int i = 0; i < campignsList.size(); i++) {
            campaignNames.append(campignsList.get(i).getCampaignId()).append(",");
        }
        return campaignNames.deleteCharAt(campaignNames.length() - 1).toString();
    }

//    public void updateAdsCounters(Connection connection, String MSISDN) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [updateAdsCounters]...");
//        CustomersDAO customersDAO = new CustomersDAO();
//        customersDAO.updateAdsCounters(connection, MSISDN);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [updateAdsCounters]...");
//    }
//
//    public void updateCustomerSmsQuotaCounter(Connection connection, String MSISDN) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [updateCustomerSmsQuotaCounter]...");
//        CustomersDAO customersDAO = new CustomersDAO();
//        customersDAO.updateAdsCounters(connection, MSISDN);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [updateCustomerSmsQuotaCounter]...");
//    }
//
//    public void updateCustomerAdsQuotaCounter(Connection connection, String MSISDN) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [updateCustomerAdsQuotaCounter]...");
//        CustomersDAO customersDAO = new CustomersDAO();
//        customersDAO.updateAdsCounters(connection, MSISDN);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [updateCustomerAdsQuotaCounter]...");
//    }
//    public SmsCountersModel handleCustomerSmsCounters(Connection connection, SmsCountersModel smsCountersModel, int guardPeriod, String transId) throws CommonException, InterfacesBusinessException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [handleCustomerSmsCounters]...");
//        CustomersDAO customersDAO = new CustomersDAO();
//        smsCountersModel = customersDAO.handleCustomerSmsCounters(connection, smsCountersModel, guardPeriod, transId);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [handleCustomerSmsCounters]...");
//        return smsCountersModel;
//    }
//    public AdsCountersModel handleCustomerAdsCounters(Connection connection, AdsCountersModel adsCountersModel, String transId) throws CommonException, InterfacesBusinessException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [handleCustomerAdsCounters]...");
//        CustomersDAO customersDAO = new CustomersDAO();
//        adsCountersModel= customersDAO.handleCustomerAdsCounters(connection, adsCountersModel, transId);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [handleCustomerAdsCounters]...");
//        return adsCountersModel;
//    }
    public boolean handleCustomerAdsCounters(Connection con, AdsCountersModel adsCountersModel, String yesterdayColumn, int today, int totalDays, String transId, InputModel input) throws CommonException, InterfacesBusinessException {
//        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Starting handleCustomerAdsCounters Service");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters Started").build());

        try {
            CustomersDAO customersDAO = new CustomersDAO();
            String todayColumnName = "DAY_" + today + "_COUNTER";
            String yesterdayColumnName = null;
            if (today - 1 <= 0) {
                yesterdayColumnName = "DAY_" + (totalDays) + "_" + yesterdayColumn;
            } else {
                yesterdayColumnName = "DAY_" + (today - 1) + "_" + yesterdayColumn;
            }
            String columnName = "DAY_$_TEMP";
            int j = 0;
            String weeklyStatement = todayColumnName + "+" + yesterdayColumnName;
            String monthlyStatement = todayColumnName + "+" + yesterdayColumnName;
            String calculationStmt = todayColumnName + "+" + yesterdayColumnName;
            int end = 0;
            for (int i = today - 2;; i--) {
                j++;
                if (i <= 0) {
                    i = totalDays;
                }
                calculationStmt = calculationStmt + "+" + columnName.replace("$", String.valueOf(i));
                if (j == 5) {
                    weeklyStatement = calculationStmt;
                    // break;
                }
                if (j == 28) {
                    monthlyStatement = calculationStmt;
                    end = i;
                    break;
                }
                //   weeklyStatement=weeklyStatement+columnName.replace("$", String.valueOf(i));
            }
            weeklyStatement = weeklyStatement.replace("+", ",");
            monthlyStatement = monthlyStatement.replace("+", ",");
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | TodayColumn: " + todayColumnName + ",WeeklyStatement:" + weeklyStatement + " ,monthlyStatement:" + monthlyStatement);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters Stats")
                    .put(GeneralConstants.StructuredLogKeys.TODAY_COLUMN, todayColumnName)
                    .put(GeneralConstants.StructuredLogKeys.WEEKLY_STATEMENT, weeklyStatement)
                    .put(GeneralConstants.StructuredLogKeys.MONTHLY_STATEMENT, monthlyStatement).build());

            return customersDAO.handleCustomerAdsCounters(con, adsCountersModel, monthlyStatement, todayColumnName, today, end, transId, input);
        } catch (InterfacesBusinessException ibe) {
            throw ibe;
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(transId + " || " + input.getCsMsgId() + " | Getting Caught Exception---->updateCustomerSMSStatistics Service-->" + ex);
            CommonLogger.errorLogger.error(transId + " || " + input.getCsMsgId() + " | Getting Caught Exception---->updateCustomerSMSStatistics Service-->" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
    }

//    public CustomersSmsGroupsModel getCustomerSmsGroup(Connection connection, String MSISDN) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [getCustomerSmsGroup]...");
//        CustomersDAO customersDAO = new CustomersDAO();
//        CustomersSmsGroupsModel customerSmsGroup = customersDAO.getCustomerSmsGroup(connection, MSISDN);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [getCustomerSmsGroup]...");
//        return customerSmsGroup;
//    }
//    public CustomersAdsGroupsModel getCustomerAdsGroup(Connection connection, String MSISDN) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [getCustomerAdsGroup]...");
//        CustomersDAO customersDAO = new CustomersDAO();
//        CustomersAdsGroupsModel customerAdsGroup = customersDAO.getCustomerAdsGroup(connection, MSISDN);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [getCustomerAdsGroup]...");
//        return customerAdsGroup;
//    }
    public void updateCustomerCampStats(Connection conn, CustomerCampaignsModel custCampModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CustomerService.class.getName() + " || " + "Starting Getting [updateCustomerCampStats]...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerCampStats Started").build());
        CustomersDAO customersDAO = new CustomersDAO();
        customersDAO.updateCustomerCampStats(conn, custCampModel);
//        CommonLogger.businessLogger.debug(CustomerService.class.getName() + " || " + "End Getting [updateCustomerCampStats]...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateCustomerCampStats Ended").build());

    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerAdsStats(Connection conn, String logPrefix, String msisdn, int day) throws CommonException {
//        CommonLogger.businessLogger.info(logPrefix + "Starting decrementCustomerAdsStats Service");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "decrementCustomerAdsStats Service Started").build());

        try {
            new CustomersDAO().decrementCustomerAdsStats(conn, logPrefix, msisdn, day);
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught Exception---->decrementCustomerAdsStats Service-->" + ex);

            CommonLogger.errorLogger.error(logPrefix + "Getting Caught Exception---->decrementCustomerAdsStats Service-->" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);

        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerCampaignStats(Connection conn, String logPrefix, String msisdn, int campaignId) throws CommonException {
//        CommonLogger.businessLogger.info(logPrefix + "Starting decrementCustomerCampaignStats Service");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "decrementCustomerCampaignStats Service Started").build());

        try {
            new CustomersDAO().decrementCustomerCampaignStats(conn, logPrefix, msisdn, campaignId);
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught Exception---->decrementCustomerCampaignStats Service-->" + ex);

            CommonLogger.errorLogger.error(logPrefix + "Getting Caught Exception---->decrementCustomerCampaignStats Service-->" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);

        }
    }
}
