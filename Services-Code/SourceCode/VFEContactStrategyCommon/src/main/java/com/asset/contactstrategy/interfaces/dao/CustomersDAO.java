/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.Initializer;
import static com.asset.contactstrategy.common.dao.SMSCustomerStatsticsDao.updateCustomerSMSStatistics;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.AdsCountersModel;
import com.asset.contactstrategy.interfaces.models.CampaignModel;
import com.asset.contactstrategy.interfaces.models.CustomerCampaignsModel;
import com.asset.contactstrategy.interfaces.models.CustomerConfigAndGrpModel;
import com.asset.contactstrategy.interfaces.models.CustomerConfigurationsModel;
import com.asset.contactstrategy.interfaces.models.CustomersAdsGroupsModel;
import com.asset.contactstrategy.interfaces.models.CustomersSmsGroupsModel;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.SmsCountersModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author hazem.fekry
 */
public class CustomersDAO {

    public List<CustomerCampaignsModel> getCustomerCampaignsStatsList(Connection connection, String MSISDN, ArrayList<CampaignModel> serviceCampignsList) throws CommonException {
        //CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkService...");
        PreparedStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;
        HashMap<Integer, CampaignModel> commonCustomerServiceCampaignHashMap = getCommonCustomerServiceCampaignHashMap(connection, MSISDN, serviceCampignsList);
        List<CustomerCampaignsModel> customerCampaignsStatsList = new ArrayList<>();
        CustomerCampaignsModel customerCampaignsModel;
        boolean hasRecords = false;
        int paramIndex = 1;
//        int runId=Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY));
        try {
            sql.append("SELECT * FROM ").append(DBStruct.VFE_CS_CAMP_CUST_STATS.TABLE_NAME)
                    .append(" WHERE ").append(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN).append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN_MOD_X).append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_ID).append(" IN (");
            for (int i = 0; i < commonCustomerServiceCampaignHashMap.size(); i++) {
                if (i != 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(")");

            if (commonCustomerServiceCampaignHashMap.isEmpty()) {
                return customerCampaignsStatsList;
            }

            statement = connection.prepareStatement(sql.toString());

            statement.setString(paramIndex++, MSISDN);
            statement.setInt(paramIndex++, Utility.getMsisdnLastTwoDigits(MSISDN));
            Iterator iterator = commonCustomerServiceCampaignHashMap.keySet().iterator();
            while (iterator.hasNext()) {
                statement.setInt(paramIndex++, commonCustomerServiceCampaignHashMap.get(iterator.next()).getCampaignId());
            }

            for (CampaignModel serviceCampaign : serviceCampignsList) {
                if (commonCustomerServiceCampaignHashMap.containsKey(serviceCampaign.getCampaignId())) {
                    hasRecords = true;
                    customerCampaignsModel = new CustomerCampaignsModel();
                    customerCampaignsModel.setId(0);
                    customerCampaignsModel.setMsisdn(MSISDN);
                    customerCampaignsModel.setCampId(commonCustomerServiceCampaignHashMap.get(serviceCampaign.getCampaignId()).getCampaignId());
                    customerCampaignsModel.setCampCount(0);
                    customerCampaignsModel.setCustomerCampaign(commonCustomerServiceCampaignHashMap.get(serviceCampaign.getCampaignId()));

                    customerCampaignsStatsList.add(customerCampaignsModel);
                }
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                hasRecords = true;
                for (int i = 0; i < customerCampaignsStatsList.size(); i++) {
                    if (customerCampaignsStatsList.get(i).getCampId() == resultSet.getInt(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_ID)) {
                        customerCampaignsStatsList.get(i).setId(resultSet.getInt(DBStruct.VFE_CS_CAMP_CUST_STATS.ID));
                        //customerCampaignsStatsList.get(i).setMsisdn(resultSet.getString(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN));
                        //customerCampaignsStatsList.get(i).setCampId(resultSet.getInt(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_ID));
                        customerCampaignsStatsList.get(i).setCampCount(resultSet.getInt(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_COUNT));
                        //customerCampaignsStatsList.get(i).setCustomerCampaign(commonCustomerServiceCampaignHashMap.get(resultSet.getInt(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_ID)));
                    }

                }
            }

//            Collections.sort(customerCampaignsStatsList, new Comparator<CustomerCampaignsModel>()
//            {
//                @Override
//                public int compare(CustomerCampaignsModel o1, CustomerCampaignsModel o2) 
//                { 
//                    //My implementation
//                    if (o1.getCustomerCampaign().getCampaignPriority() < o2.getCustomerCampaign().getCampaignPriority())
//                        return -1;
//                    else if (o1.getCustomerCampaign().getCampaignPriority() > o2.getCustomerCampaign().getCampaignPriority())
//                        return 1;
//                    else
//                        return 0;
//                }
//             });
            if (!hasRecords) {
                return new ArrayList<>();
            }

            return customerCampaignsStatsList;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
    }

    public HashMap<Integer, CampaignModel> getCommonCustomerServiceCampaignHashMap(Connection connection, String MSISDN, ArrayList<CampaignModel> serviceCampignsList) throws CommonException {
        //CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkService...");
        PreparedStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;
        HashMap<Integer, CampaignModel> commonCustomerServiceCampaignList = new HashMap<>();
        int runId = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY));
        //Changed by kashif
        int modX = Utility.getMsisdnLastTwoDigits(MSISDN);
        int paramIndex = 1;

        try {
            sql
                    .append("Select DISTINCT ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.CAMPAIGN_ID)
                    .append(" FROM ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.TABLE_NAME)
                    .append(" WHERE ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.MSISDN)
                    .append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.RUN_ID)
                    .append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.MSISDN_LAST_TWO_DIGITS)
                    .append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.SUSPENDED)
                    .append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.CAMPAIGN_ID)
                    .append(" IN (");

            for (int i = 0; i < serviceCampignsList.size(); i++) {
                if (i != 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(")");

            statement = connection.prepareStatement(sql.toString());

            statement.setString(paramIndex++, MSISDN);
            statement.setInt(paramIndex++, runId);
            statement.setInt(paramIndex++, modX);
            statement.setInt(paramIndex++, Defines.CAMPAIGNS_CUSTOMERS_NOT_SUSPENDED);
            for (int i = 0; i < serviceCampignsList.size(); i++) {
                statement.setInt(paramIndex++, serviceCampignsList.get(i).getCampaignId());
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                for (int i = 0; i < serviceCampignsList.size(); i++) {
                    if (serviceCampignsList.get(i).getCampaignId() == resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.CAMPAIGN_ID)) {
                        commonCustomerServiceCampaignList.put(serviceCampignsList.get(i).getCampaignId(), serviceCampignsList.get(i));
                    }
                }
            }
            return commonCustomerServiceCampaignList;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
    }

    public CustomerConfigurationsModel getCustomerConfigurations(Connection connection, String MSISDN) throws CommonException {
        PreparedStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;
        //Changed by kashif
        int modX = Utility.getMsisdnLastTwoDigits(MSISDN);
        try {
            sql.append("SELECT * FROM ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.TABLE_NAME)
                    .append(" WHERE ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MSISDN).append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.LAST_TWO_DIGITS).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, MSISDN);
            statement.setInt(2, modX);
            resultSet = statement.executeQuery();
            CustomerConfigurationsModel customerConfigurations = null;
            if (resultSet.next()) {
                customerConfigurations = new CustomerConfigurationsModel();
                customerConfigurations.setMsisdn(resultSet.getString("MSISDN"));
                customerConfigurations.setLastTwoDigits(resultSet.getInt("LAST_TWO_DIGITS"));
                customerConfigurations.setDoNotContact(resultSet.getInt("DO_NOT_CONTACT"));
                customerConfigurations.setDailyThresholdSms(resultSet.getInt("DAILY_THRESHOLD_SMS"));
                customerConfigurations.setWeeklyThresholdSms(resultSet.getInt("WEEKLY_THRESHOLD_SMS"));
                customerConfigurations.setMonthlyThresholdSms(resultSet.getInt("MONTHLY_THRESHOLD_SMS"));
                customerConfigurations.setDailyThresholdAds(resultSet.getInt("DAILY_THRESHOLD_ADS"));
                customerConfigurations.setWeeklyThresholdAds(resultSet.getInt("WEEKLY_THRESHOLD_ADS"));
                customerConfigurations.setMonthlyThresholdAds(resultSet.getInt("MONTHLY_THRESHOLD_ADS"));
            }
            return customerConfigurations;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
    }

//    public CustomersAdsGroupsModel getCustomerAdsGroup(Connection connection, String MSISDN) throws CommonException 
//    {
//        PreparedStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        ResultSet resultSet = null;
//        //Changed by kashif
//        int modX = Utility.getMsisdnLastTwoDigits(MSISDN);
//        int runId = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY));
//        try 
//        {
//            sql.append("SELECT * FROM ").append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.TABLE_NAME)
//                    .append(" WHERE ").append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.LAST_TWO_DIGITS).append(" = ? AND ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.RUN_ID).append(" = ? AND ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.MSISDN).append(" = ?");
//            statement = connection.prepareStatement(sql.toString());
//            statement.setInt(1, modX);
//            statement.setInt(2, runId);
//            statement.setString(3, MSISDN);
//            resultSet = statement.executeQuery();
//            CustomersAdsGroupsModel customersAdsGroups=null;
//            if (resultSet.next()) {
//                customersAdsGroups = new CustomersAdsGroupsModel();
//                customersAdsGroups.setAdsGroupId(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.ADS_GROUP_ID));
//                customersAdsGroups.setLastTwoDigits(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.LAST_TWO_DIGITS));
//                customersAdsGroups.setMsisdn(resultSet.getString(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.MSISDN));
//                customersAdsGroups.setRunId(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.RUN_ID));
//            }
//            return customersAdsGroups;
//        }
//        catch (SQLException e) 
//        {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
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
//                if (statement != null) 
//                {
//                    statement.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//            try 
//            {
//                if (resultSet != null) 
//                {
//                    resultSet.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
//    public CustomersSmsGroupsModel getCustomerSmsGroup(Connection connection, String MSISDN) throws CommonException 
//    {
//        PreparedStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        ResultSet resultSet = null;
//        //Changed by kashif
//        int modX = Utility.getMsisdnLastTwoDigits(MSISDN);
//        int runId = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY));
//        
//        try 
//        {
//            sql.append("SELECT * FROM ").append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME)
//                    .append(" WHERE ").append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.LAST_TWO_DIGITS).append(" = ? AND ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.RUN_ID).append(" = ? AND ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.MSISDN).append(" = ?");
//            statement = connection.prepareStatement(sql.toString());
//            statement.setInt(1, modX);
//            statement.setInt(2, runId);
//            statement.setString(3, MSISDN);
//            resultSet = statement.executeQuery();
//            CustomersSmsGroupsModel customersSmsGroups = null;
//            if (resultSet.next()) {
//                customersSmsGroups = new CustomersSmsGroupsModel();
//                customersSmsGroups.setGroupId(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID));
//                customersSmsGroups.setLastTwoDigits(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_GROUPS.LAST_TWO_DIGITS));
//                customersSmsGroups.setMsisdn(resultSet.getString(DBStruct.VFE_CS_CUSTOMERS_GROUPS.MSISDN));
//                customersSmsGroups.setRunId(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_GROUPS.RUN_ID));
//            }
//            return customersSmsGroups;
//        }
//        catch (SQLException e) 
//        {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
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
//                if (statement != null) 
//                {
//                    statement.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//            try 
//            {
//                if (resultSet != null) 
//                {
//                    resultSet.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
//    public void updateAdsCounters(Connection connection, String MSISDN) throws CommonException 
//    {
//        PreparedStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        ResultSet resultSet = null;
//        try 
//        {
//            sql.append("UPDATE ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.TABLE_NAME)
//                    .append(" SET ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_ADS).append(" = ")
//                    .append("CASE WHEN ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_ADS).append(" = 0 ").append("THEN ").append("0 ")
//                    .append("ELSE ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.DAILY_THRESHOLD_ADS).append(" - 1 END , ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_ADS).append(" = ")
//                    .append("CASE WHEN ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_ADS).append(" = 0 ").append("THEN ").append("0 ")
//                    .append("ELSE ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.WEEKLY_THRESHOLD_ADS).append(" - 1 END , ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_ADS).append(" = ")
//                    .append("CASE WHEN ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_ADS).append(" = 0 ").append("THEN ").append("0 ")
//                    .append("ELSE ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MONTHLY_THRESHOLD_ADS).append(" - 1 END ")
//                    .append(" WHERE ").append(DBStruct.VFE_CS_CUSTOMERS_CONFIGURATIONS.MSISDN).append(" = ").append(MSISDN);
//            statement = connection.prepareStatement(sql.toString());
//            resultSet = statement.executeQuery();
//        }
//        catch (SQLException e) 
//        {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
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
//                if (statement != null) 
//                {
//                    statement.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//            try 
//            {
//                if (resultSet != null) 
//                {
//                    resultSet.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
//    public void updateCustomerSmsQuotaCounter(Connection connection, String MSISDN) throws CommonException {
//        PreparedStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        ResultSet resultSet = null;
//        //Changed by kashif
//        int modX = Utility.getMsisdnLastTwoDigits(MSISDN);
//        try {
//            sql.append("MERGE INTO ").append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.TABLE_NAME)
//                    .append(" USING dual ON ( ").append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSISDN).append(" = ? and ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSGS_DATE).append("= trunc(SYSTIMESTAMP) )")
//                    .append("WHEN MATCHED THEN UPDATE SET ").append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSG_COUNT).append(" = ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSG_COUNT).append(" + 1 ")
//                    .append("WHEN NOT MATCHED THEN INSERT (").append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.ID).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSISDN).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSGS_DATE).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSG_COUNT).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.ADS_COUNT).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSISDN_MOD_X).append(")")
//                    .append(" VALUES (").append(Utility.getNextId(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.SEQ, connection))
//                    .append(",?,SYSTIMESTAMP,1,0,?)");
//            statement = connection.prepareStatement(sql.toString());
//            statement.setString(1, MSISDN);
//            statement.setString(2, MSISDN);
//            statement.setInt(3, modX);
//            resultSet = statement.executeQuery();
//        }
//        catch (SQLException e) 
//        {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
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
//                if (statement != null) 
//                {
//                    statement.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//            try 
//            {
//                if (resultSet != null) 
//                {
//                    resultSet.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
//    public void updateCustomerAdsQuotaCounter(Connection connection, String MSISDN) throws CommonException {
//        PreparedStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        ResultSet resultSet = null;
//        //Changed by kashif
//        int modX = Utility.getMsisdnLastTwoDigits(MSISDN);
//        try {
//            sql.append("MERGE INTO ").append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.TABLE_NAME)
//                    .append(" USING dual ON ( ").append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSISDN).append(" = ? and ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSGS_DATE).append("= trunc(SYSTIMESTAMP) )")
//                    .append("WHEN MATCHED THEN UPDATE SET ").append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.ADS_COUNT).append(" = ")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.ADS_COUNT).append(" + 1 ")
//                    .append("WHEN NOT MATCHED THEN INSERT (").append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.ID).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSISDN).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSGS_DATE).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSG_COUNT).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.ADS_COUNT).append(",")
//                    .append(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.MSISDN_MOD_X).append(")")
//                    .append(" VALUES (").append(Utility.getNextId(DBStruct.VFE_CS_CUSTOMERS_STATISTICS.SEQ, connection))
//                    .append(",?,SYSTIMESTAMP,0,1,?)");
//            statement = connection.prepareStatement(sql.toString());
//            statement.setString(1, MSISDN);
//            statement.setString(2, MSISDN);
//            statement.setInt(3, modX);
//            resultSet = statement.executeQuery();
//        }
//        catch (SQLException e) 
//        {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
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
//                if (statement != null) 
//                {
//                    statement.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//            try 
//            {
//                if (resultSet != null) 
//                {
//                    resultSet.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
//
//    public SmsCountersModel handleCustomerSmsCounters(Connection connection, SmsCountersModel smsCounters, int guardPeriod, String transId) throws CommonException, InterfacesBusinessException {
//        CallableStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        ResultSet resultSet = null;
//        try {
//            sql.append("{CALL ").append(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.PROCDURE_NAME).append(" (?,?,?,?,?,?,?,?,?)}");
//            statement = connection.prepareCall(sql.toString());
//            statement.setString(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.MSISDN, smsCounters.getMsisdn());
//            statement.setInt(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.DAILY_QUOTA, smsCounters.getDailyQuota());
//            statement.setInt(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.WEEKLY_QUOTA, smsCounters.getWeeklyQuota());
//            statement.setInt(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.MONTHLY_QUOTA, smsCounters.getMonthlyQuota());
//            statement.setByte(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.MESSAGE_CATEGORY, smsCounters.getMessageCategory());
//            statement.setInt(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.GUARD_PERIOD, guardPeriod);
//            statement.registerOutParameter(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.v_LOG, Types.VARCHAR);
//            statement.registerOutParameter(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.CAN_SEND_SMS, Types.NUMERIC);
//            statement.registerOutParameter(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.MSG_VIOLAT_FLAG, Types.NUMERIC);
//            statement.setQueryTimeout(Integer.parseInt(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.QUERY_TIMEOUT)));
//            statement.executeUpdate();
//            CommonLogger.businessLogger.info(transId + " || " + statement.getString(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.v_LOG));
//            smsCounters.setCanSendSms(statement.getBoolean(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.CAN_SEND_SMS));
//            smsCounters.setMsgViolatFlag(statement.getByte(DBStruct.HANDLE_CUST_SMS_COUNTERS_PROC.MSG_VIOLAT_FLAG));
//            return smsCounters;
//        }
//        catch (SQLTimeoutException e) 
//        {
//            CommonLogger.businessLogger.info("Query Timed Out... User Is Locked For Another Action || " + e.getMessage());
//            InterfacesBusinessException ibe =  new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, ErrorCodes.SEND_SMS.OPERATION_TIMED_OUT_USER_IS_LOCKED
//                    , e.getMessage());
//            CommonLogger.businessLogger.info(transId + " || " + ibe.getDetailedMessage());
//            throw ibe;
//        }
//        catch (SQLException e) 
//        {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
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
//                if (statement != null) 
//                {
//                    statement.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//            try 
//            {
//                if (resultSet != null) 
//                {
//                    resultSet.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
//    public AdsCountersModel handleCustomerAdsCounters(Connection connection, AdsCountersModel adsCounters, String transId) throws CommonException, InterfacesBusinessException {
//        CallableStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        ResultSet resultSet = null;
//        try {
//            sql.append("{CALL ").append(DBStruct.HANDLE_CUST_ADS_COUNTERS_PROC.PROCDURE_NAME).append(" (?,?,?,?,?,?,?)}");
//            statement = connection.prepareCall(sql.toString());
//            statement.setString(1, adsCounters.getMsisdn());
//            statement.setInt(2, adsCounters.getDailyQuota());
//            statement.setInt(3, adsCounters.getWeeklyQuota());
//            statement.setInt(4, adsCounters.getMonthlyQuota());
//            statement.setByte(5, adsCounters.getMessageCategory());
//            statement.registerOutParameter(6, Types.VARCHAR);
//            statement.registerOutParameter(7, Types.NUMERIC);
//            statement.setQueryTimeout(Integer.parseInt(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.QUERY_TIMEOUT)));
//            statement.execute();
//            CommonLogger.businessLogger.info(transId + " || " + statement.getString(6));
//            adsCounters.setCanSendSms(statement.getByte(7));
//            return adsCounters;
//        }
//        catch (SQLTimeoutException e) 
//        {
//            CommonLogger.businessLogger.info("Query Timed Out... User Is Locked For Another Action || " + e.getMessage());
//            InterfacesBusinessException ibe =  new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, ErrorCodes.SEND_SMS.OPERATION_TIMED_OUT_USER_IS_LOCKED
//                    , e.getMessage());
//            CommonLogger.businessLogger.info(transId + " || " + ibe.getDetailedMessage());
//            throw ibe;
//        }
//        catch (SQLException e) 
//        {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
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
//                if (statement != null) 
//                {
//                    statement.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//            try 
//            {
//                if (resultSet != null) 
//                {
//                    resultSet.close();
//                }
//            } 
//            catch (SQLException e) 
//            {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
    public boolean handleCustomerAdsCounters(Connection con, AdsCountersModel adsCountersModel, String monthlyString, String todayColumn, int start, int end, String transId, InputModel input) throws CommonException, InterfacesBusinessException {
//        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Starting handleCustomerAdsCounters dao");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean sendAd = false;
        int lastTwoDigits = Integer.parseInt(adsCountersModel.getMsisdn().substring(10));
        try {
            String sql = "Select " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN + "," + monthlyString + "," + todayColumn + " todayColumn" + " FROM " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN + "=? AND " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN_MOD_X + "=? FOR UPDATE";
            String sqlUpdateCounters = "Update " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME + " set " + todayColumn + "=" + todayColumn + "+1  WHERE " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN + "=? AND " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN_MOD_X + "=?";
            String sqlInsertCounters = "Insert into " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME + "(" + DBStruct.VFE_CS_ADS_CUST_STATISTICS.ID + "," + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN + "," + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN_MOD_X + "," + todayColumn + ") VALUES (" + DBStruct.VFE_CS_ADS_CUST_STATISTICS.VFE_CS_ADS_CUST_STAT_SEQ + ".nextval,?,?,?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, adsCountersModel.getMsisdn());
            stmt.setInt(2, lastTwoDigits);

            stmt.setQueryTimeout(Defines.INTERFACES.QUERY_TIMEOUT_VALUE);

//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Sql: " + sql + " inputs 1:" + adsCountersModel.getMsisdn() + ",2:" + lastTwoDigits);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql)
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, adsCountersModel.getMsisdn())
                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, lastTwoDigits).build());

            rs = stmt.executeQuery();
            int j = 1;
            int Sum = 0;
            int weeklySum = 0;
            int monthlySum = 0;
            boolean customerExists = false;
            int todayCounter = 0;
            //CommonLogger.businessLogger.info(transId + " || Started Calculation Loop");
            while (rs.next()) {
                customerExists = true;
                for (int i = start;; i--) {
                    j++;
                    Sum = Sum + rs.getInt(j);
                    if (j == 8) {
                        weeklySum = Sum;
                        // break;
                    }
                    if (j == 31) {
                        monthlySum = Sum;
                        break;
                    }
                }

                todayCounter = rs.getInt("TODAYCOLUMN");

//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | MSISDN:" + adsCountersModel.getMsisdn() + ",todayCounter:" + todayCounter + ",weeklySum:" + weeklySum + ",monthlySum:" + monthlySum);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters Query")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN, adsCountersModel.getMsisdn())
                        .put(GeneralConstants.StructuredLogKeys.TODAY_COUNTER, todayCounter)
                        .put(GeneralConstants.StructuredLogKeys.WEEKLY_SUM, weeklySum)
                        .put(GeneralConstants.StructuredLogKeys.MONTHLY_SUM, monthlySum).build());
//                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | MSISDN:" + adsCountersModel.getMsisdn() + ",todayQouta:" + adsCountersModel.getDailyQuota() + ",weeklyQouta:" + adsCountersModel.getWeeklyQuota() + ",monthlyQouta:" + adsCountersModel.getMonthlyQuota());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters Query")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN, adsCountersModel.getMsisdn())
                        .put(GeneralConstants.StructuredLogKeys.TODAY_QUOTA, adsCountersModel.getDailyQuota())
                        .put(GeneralConstants.StructuredLogKeys.WEEKLY_QUOTA, adsCountersModel.getWeeklyQuota())
                        .put(GeneralConstants.StructuredLogKeys.MONTHLY_QUOTA, adsCountersModel.getMonthlyQuota()).build());
            }
            DataSourceManger.closeDBResources(rs, stmt); // eslam.ahmed | 5-5-2020
            if (customerExists) {
                if (todayCounter < adsCountersModel.getDailyQuota()) {
                    if (weeklySum < adsCountersModel.getWeeklyQuota()) {
                        if (monthlySum < adsCountersModel.getMonthlyQuota()) {
//                            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | MSISDN: " + adsCountersModel.getMsisdn() + " exists in " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME);
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters DB info")
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, adsCountersModel.getMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.TABLE_NAME, DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME).build());
                            stmt = con.prepareStatement(sqlUpdateCounters);
                            stmt.setString(1, adsCountersModel.getMsisdn());
                            stmt.setInt(2, lastTwoDigits);
//                            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | SQL:" + sqlInsertCounters + ",INPUTS : 1:" + adsCountersModel.getMsisdn() + ",2:" + lastTwoDigits);
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters DB info")
                                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlInsertCounters)
                                    .put(GeneralConstants.StructuredLogKeys.MSISDN, adsCountersModel.getMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, lastTwoDigits).build());

                            if (stmt.executeUpdate() > 0) {
//                                CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | MSISDN: " + adsCountersModel.getMsisdn() + " Updated in " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME + " Successfully");
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Table name updated successfully")
                                        .put(GeneralConstants.StructuredLogKeys.MSISDN, adsCountersModel.getMsisdn())
                                        .put(GeneralConstants.StructuredLogKeys.TABLE_NAME, DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME).build());
                                sendAd = true;
                                return sendAd;
                            }
//                            else
//                            {
//                                CommonLogger.businessLogger.info(transId + " || Error While Updating MSISDN: " + adsCountersModel.getMsisdn() + " in " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME);
//                                sendAd = false;
//                            }
                        } else {
                            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE, ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.MONTHLY_ADs_THRESHOLD_REACHED_FOR_CUSTOMER, "monthlySum: " + monthlySum + " | MonthlyQuota:" + adsCountersModel.getMonthlyQuota());
                            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
                            throw ibe;
                        }
                    } else {
                        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE, ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.WEEKLY_ADs_THRESHOLD_REACHED_FOR_CUSTOMER, "weeklySum: " + weeklySum + " | MonthlyQuota:" + adsCountersModel.getWeeklyQuota());
                        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
                        throw ibe;
                    }
                } else {
                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE, ErrorCodes.ADVERTISEMENT_CONSULT_INTERFACE.DAILY_ADs_THRESHOLD_REACHED_FOR_CUSTOMER, "todayCounter: " + todayCounter + " | MonthlyQuota:" + adsCountersModel.getDailyQuota());
                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
                    throw ibe;
                }
            } else {
//                synchronized(Defines.adsCustomerStatsSyncObject)
//                {
//                    stmt.clearParameters();
                stmt = con.prepareStatement("Select Count(" + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN + ") FROM " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN + "=? AND " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN_MOD_X + "=?");
                stmt.setString(1, adsCountersModel.getMsisdn());
                stmt.setInt(2, lastTwoDigits);
                rs = stmt.executeQuery();

                while (rs.next()) {
                    //System.err.println(" " + rs.getInt(1));
                    if (rs.getInt(1) > 0) {
                        return handleCustomerAdsCounters(con, adsCountersModel, monthlyString, todayColumn, start, end, transId, input);
                    } else {
                        try {
                            insertCustomerSMSStatsRecord(transId, sqlInsertCounters, adsCountersModel);
                        } catch (SQLException e) {
                            if (!e.getMessage().toLowerCase().contains("unique constraint")) {
                                CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught SQLException---->handleCustomerAdsCounters Dao-->" + e);
                                CommonLogger.errorLogger.error(transId + "|| csMsgId: " + input.getCsMsgId() + " | Caught SQLException---->handleCustomerAdsCounters Dao-->" + e, e);
                                throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
                            }
//                            CommonLogger.businessLogger.info(transId + "|| csMsgId: " + input.getCsMsgId() + " | Customer Already Exists... Retrying Update... " + e);
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer already exists, retrying update").build(), e);
                        }
                        return handleCustomerAdsCounters(con, adsCountersModel, monthlyString, todayColumn, start, end, transId, input);
                    }
                }

//                    CommonLogger.businessLogger.info(transId + " || MSISDN: " + adsCountersModel.getMsisdn() + " doesn't exist in " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME);
//                    stmt = con.prepareStatement(sqlInsertCounters);
//                    stmt.setString(1, adsCountersModel.getMsisdn());
//                    stmt.setInt(2, lastTwoDigits);
//                    stmt.setInt(3, 1);
//                    CommonLogger.businessLogger.info(transId + " || SQL:"+sqlInsertCounters+",INPUTS : 1:"+adsCountersModel.getMsisdn()+",2:"+lastTwoDigits);
//
//                    if (stmt.executeUpdate() > 0) 
//                    {
//                        CommonLogger.businessLogger.info(transId + " || MSISDN: " + adsCountersModel.getMsisdn() + " inserted in " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME + " Successfully");
//                        sendAd = true;
//                        return sendAd;
//                    }
                //                else
                //                {
                //                    CommonLogger.businessLogger.info(transId + " || Error While Adding MSISDN: " + adsCountersModel.getMsisdn() + " into " + DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME);
                //                    sendAd = false;
                //                }
//                }
            }
            return sendAd;
        } catch (InterfacesBusinessException ibe) {
            throw ibe;
        } catch (SQLTimeoutException e) {
            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Query Timed Out... User Is Locked For Another Action || " + e.getMessage());
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_ADVERTISMENT_CONSULT_INTERFACE, ErrorCodes.SEND_SMS.OPERATION_TIMED_OUT_USER_IS_LOCKED,
                    e.getMessage());
            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
            throw ibe;
        } catch (SQLException ex) {
            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught SQLException---->updateCustomerSMSStatistics Dao-->" + ex);
            CommonLogger.errorLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught SQLException---->updateCustomerSMSStatistics Dao-->" + ex, ex);
            //We need to change error code
            throw new CommonException(ex.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught Exception---->updateCustomerSMSStatistics Dao-->" + ex);
            CommonLogger.errorLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught Exception---->updateCustomerSMSStatistics Dao-->" + ex, ex);
            //We need to change error code
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught SQLException---->Closing Resultset-->" + ex);
                    CommonLogger.errorLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught SQLException---->Closing Resultset-->" + ex, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | handleCustomerAdsCounters dao ended in " + (System.currentTimeMillis() - startTime) + " msecs");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "handleCustomerAdsCounters dao ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        }
    }

    private static void insertCustomerSMSStatsRecord(String transId, String sqlInsertCounters, AdsCountersModel adsCountersModel) throws SQLException, CommonException {
        Connection conn = null;
        PreparedStatement stmt = null;
        int lastTwoDigits = Integer.parseInt(adsCountersModel.getMsisdn().substring(10));
        try {
            conn = DataSourceManger.getConnection();
            stmt = conn.prepareStatement(sqlInsertCounters);
            stmt.setString(1, adsCountersModel.getMsisdn());
            stmt.setInt(2, lastTwoDigits);
            stmt.setInt(3, 0);

            stmt.executeUpdate();
            conn.commit();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(transId + "Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(transId + "Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(transId + "Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(transId + "Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
        }
    }

    public void updateCustomerCampStats(Connection conn, CustomerCampaignsModel custCampModel) throws CommonException {
        PreparedStatement statement = null;
        StringBuilder sql;
        //Changed by kashif
        int modX = Utility.getMsisdnLastTwoDigits(custCampModel.getMsisdn());
        for (int retries = 0; retries < 10; retries++) {
            try {
                sql = new StringBuilder();
                sql.append("MERGE INTO ").append(DBStruct.VFE_CS_CAMP_CUST_STATS.TABLE_NAME)
                        .append(" USING dual ON ( ").append(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN).append(" = ? and ")
                        .append(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_ID).append("= ? and ")
                        .append(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN_MOD_X).append("= ? )")
                        .append("WHEN MATCHED THEN UPDATE SET ").append(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_COUNT).append(" = ")
                        .append(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_COUNT).append(" + 1 ")
                        .append("WHEN NOT MATCHED THEN INSERT (")
                        .append(DBStruct.VFE_CS_CAMP_CUST_STATS.ID).append(",")
                        .append(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN).append(",")
                        .append(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_COUNT).append(",")
                        .append(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_ID).append(",")
                        .append(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN_MOD_X).append(")")
                        .append(" VALUES (" + DBStruct.VFE_CS_CAMP_CUST_STATS.VFE_CS_CAMP_CUST_STATS_SEQ + ".NextVal,?,?,?,?)");

//                CommonLogger.businessLogger.info("Attempting Sql: " + sql.toString() + " | Retries: " + retries);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Query Stats")
                        .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString())
                        .put(GeneralConstants.StructuredLogKeys.RETRIES, retries).build());

                statement = conn.prepareStatement(sql.toString());

                int paramIndex = 1;
                statement.setString(paramIndex++, custCampModel.getMsisdn());
                statement.setInt(paramIndex++, custCampModel.getCampId());
                statement.setInt(paramIndex++, modX);
                statement.setString(paramIndex++, custCampModel.getMsisdn());
                statement.setInt(paramIndex++, 1);
                statement.setInt(paramIndex++, custCampModel.getCampId());
                statement.setInt(paramIndex++, modX);

                statement.executeUpdate();
                break;
            } catch (SQLException e) {
                if (retries < 9) {
                    continue;
                }
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
                throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
            } catch (Exception e) {
                if (retries < 9) {
                    continue;
                }
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error(e.getMessage());
                    CommonLogger.errorLogger.error(e.getMessage(), e);
                }
            }
        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerAdsStats(Connection conn, String logPrefix, String msisdn, int day) throws SQLException, CommonException {
        PreparedStatement stmt = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("Update ")
                    .append(DBStruct.VFE_CS_ADS_CUST_STATISTICS.TABLE_NAME);

            if (Integer.valueOf(SystemLookups.SYSTEM_PROPERTIES.get("TODAY_COLUMN_NUM")) > day) {
                sql.append(" Set DAY_?_" + SystemLookups.SYSTEM_PROPERTIES.get("YESTERDAY_COLUMN_NAME") + " = DAY_?_" + SystemLookups.SYSTEM_PROPERTIES.get("YESTERDAY_COLUMN_NAME") + " - 1 WHERE ");
            } else {
                sql.append(" Set DAY_?_COUNTER = DAY_?_COUNTER - 1 WHERE ");
            }

            sql.append(DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN)
                    .append(" = ? And ")
                    .append(DBStruct.VFE_CS_ADS_CUST_STATISTICS.MSISDN_MOD_X)
                    .append(" = ?");

            stmt = conn.prepareStatement(sql.toString());
            stmt.setInt(1, day);
            stmt.setInt(2, day);
            stmt.setString(3, msisdn);
            stmt.setInt(4, Integer.valueOf(msisdn.substring(msisdn.length() - 2)));

            stmt.executeUpdate();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + e);
            CommonLogger.errorLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + e, e);
            throw new CommonException(e.getMessage(), "");
        } catch (Exception e) {
            CommonLogger.businessLogger.error(logPrefix + "Caught Exception---->Closing Statement-->" + e);
            CommonLogger.errorLogger.error(logPrefix + "Caught Exception---->Closing Statement-->" + e, e);
            throw new CommonException(e.getMessage(), "");
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
        }
    }

    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
    public void decrementCustomerCampaignStats(Connection conn, String logPrefix, String msisdn, int campaignId) throws SQLException, CommonException {
        PreparedStatement stmt = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("Update ")
                    .append(DBStruct.VFE_CS_CAMP_CUST_STATS.TABLE_NAME)
                    .append(" Set ")
                    .append(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_COUNT)
                    .append(" = ")
                    .append(DBStruct.VFE_CS_CAMP_CUST_STATS.CAMP_COUNT)
                    .append(" - 1 WHERE ")
                    .append(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN)
                    .append(" = ? And ")
                    .append(DBStruct.VFE_CS_CAMP_CUST_STATS.MSISDN_MOD_X)
                    .append(" = ?");

            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, msisdn);
            stmt.setInt(2, Integer.valueOf(msisdn.substring(msisdn.length() - 2)));

            stmt.executeUpdate();
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + e);
            CommonLogger.errorLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + e, e);
            throw new CommonException(e.getMessage(), "");
        } catch (Exception e) {
            CommonLogger.businessLogger.error(logPrefix + "Caught Exception---->Closing Statement-->" + e);
            CommonLogger.errorLogger.error(logPrefix + "Caught Exception---->Closing Statement-->" + e, e);
            throw new CommonException(e.getMessage(), "");
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    CommonLogger.businessLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + ex);
                    CommonLogger.errorLogger.error(logPrefix + "Caught SQLException---->Closing Statement-->" + ex, ex);
                }
            }
        }
    }
}
