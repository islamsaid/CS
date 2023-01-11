/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.ServiceQuotaCounter;
import com.asset.contactstrategy.interfaces.models.AppQueueModel;
import com.asset.contactstrategy.interfaces.models.CampaignModel;
import com.asset.contactstrategy.interfaces.models.ServiceWhitelistModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hazem.fekry
 */
public class ServicesManagmentDAO {

    public HashMap<String, ServicesModel> getServices(Connection connection, HashMap<Long, QueueModel> queueMap) throws CommonException {

        HashMap<String, ServicesModel> services = new HashMap<>();
        HashMap<Integer, List<ServiceWhitelistModel>> whiteListMap = getWhiteList(connection);
        //HashMap<Integer, AppQueueModel> appQueueMap = getAppQueueModel(connection);
        HashMap<Integer, ArrayList<CampaignModel>> servicesCampaignsMap = getCampaigns(connection);
//        //CSPhase1.5 | Esmail.Anbar | Adding Template Update
//        HashMap<Integer, HashMap<Integer, TemplateModel>> templates = getServicesTemplates(connection);
        //CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkService...");
        PreparedStatement statement = null;
        StringBuilder sql;
        ResultSet resultSet = null;

        try {
            sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(DBStruct.SERVICE.TABLE_NAME).append(" Where ").append(DBStruct.SERVICE.STATUS).append("=1");
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();

            int serviceId;
            String serviceName;
            while (resultSet.next()) {
                serviceId = resultSet.getInt(DBStruct.SERVICE.SERVICE_ID);
                serviceName = resultSet.getString(DBStruct.SERVICE.SERVICE_NAME);
                ServicesModel servicesModel = new ServicesModel();
                servicesModel.setServiceName(serviceName);
                servicesModel.setDailyQuota(resultSet.getLong(DBStruct.SERVICE.DAILY_QUOTA));
                servicesModel.setInterfaceType(resultSet.getInt(DBStruct.SERVICE.INTERFACE_TYPE));
                servicesModel.setSystemType(resultSet.getInt(DBStruct.SERVICE.SYSTEM_TYPE));
                servicesModel.setDeliveryReport(resultSet.getInt(DBStruct.SERVICE.DELIVERY_REPORT));
                servicesModel.setSystemCategory(resultSet.getInt(DBStruct.SERVICE.SYSTEM_CATEGORY));
                servicesModel.setConsultCounter(resultSet.getInt(DBStruct.SERVICE.CONSULT_COUNTER));
                servicesModel.setAdsConsultCounter(resultSet.getInt(DBStruct.SERVICE.ADS_CONSULT_COUNTER));
                servicesModel.setSupportAds(resultSet.getInt(DBStruct.SERVICE.SUPPORT_ADS));
                servicesModel.setAppId(resultSet.getInt(DBStruct.SERVICE.APP_ID));
                servicesModel.setAllowedSms(resultSet.getInt(DBStruct.SERVICE.ALLOWED_SMS));
                servicesModel.setHasWhitelist(resultSet.getInt(DBStruct.SERVICE.HAS_WHITELIST));
                servicesModel.setServiceId(serviceId);
                servicesModel.setVersionId(resultSet.getInt(DBStruct.SERVICE.VERSION_ID));
                servicesModel.setStatus(resultSet.getInt(DBStruct.SERVICE.STATUS));
                servicesModel.setCreator(resultSet.getInt(DBStruct.SERVICE.CREATOR));
                servicesModel.setPrivilegeLevel(resultSet.getInt(DBStruct.SERVICE.PRIVILEGE_LEVEL));
                servicesModel.setOriginator(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR));
                servicesModel.setOriginatorType(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR_TYPE));
                servicesModel.setWhiteListModel(whiteListMap.get(servicesModel.getVersionId()));
                //servicesModel.setAppQueueModel(appQueueMap.get(serviceId));
                servicesModel.setCampiagnModel(servicesCampaignsMap.get(servicesModel.getVersionId()));
                servicesModel.setQueueModel(queueMap.get(Long.valueOf(resultSet.getInt(DBStruct.SERVICE.APP_ID))));
                // CR 1901 | eslam.ahmed
                servicesModel.setHashedPassword(resultSet.getBytes(DBStruct.SERVICE.PASSWORD));
//                //CSPhase1.5 | Esmail.Anbar | Adding Template Update
//                servicesModel.setTemplates(templates.get(servicesModel.getVersionId()));
                services.put(serviceName, servicesModel);
            }
            return services;
            //CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkService...");

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

    private HashMap<Integer, List<ServiceWhitelistModel>> getWhiteList(Connection connection) throws CommonException {
        //CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkService...");
        PreparedStatement statement = null;
        String sql;
        ResultSet resultSet = null;
        HashMap<Integer, List<ServiceWhitelistModel>> whiteListMap = new HashMap<>();
        List<ServiceWhitelistModel> whiteList;
        try {
            sql = "SELECT * FROM " + DBStruct.SERVICE_WHITELIST.TABLE_NAME;
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            int currentId;
            while (resultSet.next()) {
                currentId = resultSet.getInt(DBStruct.SERVICE_WHITELIST.SERVICE_ID);
                ServiceWhitelistModel whitelistModel = new ServiceWhitelistModel(currentId, resultSet.getString(DBStruct.SERVICE_WHITELIST.IP_ADDRESS));
                whiteList = whiteListMap.get(currentId);
                if (whiteList == null) {
                    whiteList = new ArrayList<>();
                }
                whiteList.add(whitelistModel);
                whiteListMap.put(currentId, whiteList);
            }
            return whiteListMap;
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

    private HashMap<Integer, AppQueueModel> getAppQueueModel(Connection connection) throws CommonException {
        //CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkService...");
        PreparedStatement statement = null;
        String sql;
        ResultSet resultSet = null;
        HashMap<Integer, AppQueueModel> appQueueMap = new HashMap<>();
        try {
            sql = "SELECT * FROM " + DBStruct.APPQUEUE.TABLE_NAME;
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            AppQueueModel appQueueModel;
            while (resultSet.next()) {
                appQueueModel = new AppQueueModel();
                appQueueModel.setAppQueueId(resultSet.getLong(1));
                appQueueModel.setAppQueueName(resultSet.getString(2));
                appQueueModel.setDequeuePoolSize(resultSet.getInt(3));
                appQueueModel.setSenderPoolSize(resultSet.getInt(4));
                appQueueModel.setSchemaName(resultSet.getString(5));
                appQueueModel.setDatabaseURL(resultSet.getString(6));
                appQueueModel.setSchemaPassword(resultSet.getString(7));
                appQueueModel.setId(resultSet.getLong(8));
                appQueueModel.setStatus(resultSet.getInt(9));
                appQueueModel.setCreator(resultSet.getInt(10));
                appQueueModel.setQueueName(resultSet.getString(11));
                appQueueMap.put(resultSet.getInt(1), appQueueModel);
            }

            return appQueueMap;
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

    public HashMap<Integer, ArrayList<CampaignModel>> getCampaigns(Connection connection) throws CommonException {
        //CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkService...");
        PreparedStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;
        HashMap<Integer, ArrayList<CampaignModel>> ServicescampaignsMap = new HashMap<>();
        ArrayList<CampaignModel> campaignsList;

        try {
            sql.append("SELECT ")
                    .append(DBStruct.CAMPAIGNS_SERVICES.TABLE_NAME)
                    .append(".")
                    .append(DBStruct.CAMPAIGNS_SERVICES.SERVICE_ID)
                    .append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(".* FROM ")
                    .append(DBStruct.CAMPAIGNS_SERVICES.TABLE_NAME)
                    .append(" INNER JOIN ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(" ON ")
                    .append(DBStruct.CAMPAIGNS_SERVICES.TABLE_NAME)
                    .append(".")
                    .append(DBStruct.CAMPAIGNS_SERVICES.CAMPAIGN_ID)
                    .append(" = ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(".")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID)
                    .append(" WHERE ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START)
                    .append(" <= SYSTIMESTAMP AND ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END)
                    .append(" >= SYSTIMESTAMP AND ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS)
                    .append(" NOT IN (?,?)")
                    .append(" ORDER BY ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY)
                    .append(" DESC");

            statement = connection.prepareStatement(sql.toString());
            statement.setInt(1, GeneralConstants.CAMPAIGN_STATUS_PAUSED_VALUE);
            statement.setInt(2, GeneralConstants.CAMPAIGN_STATUS_STOPPED_VALUE);
            resultSet = statement.executeQuery();
            CampaignModel campaignModel;
            int campaignId;
            int serviceId;
            while (resultSet.next()) {
                campaignModel = new CampaignModel();
                campaignId = resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID);
                serviceId = resultSet.getInt(DBStruct.CAMPAIGNS_SERVICES.SERVICE_ID);
                campaignModel.setCampaignId(campaignId);
                campaignModel.setCampaignName(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME));
                campaignModel.setCampaignDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_DESCRIPTION));
                campaignModel.setCampaignStart(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START));
                campaignModel.setCampaignEnd(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END));
                campaignModel.setMaxcommunications(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_COMMUNICATIONS));
                campaignModel.setFilterQuery(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY));
                campaignModel.setMaxTargeted(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED));
                campaignModel.setCampaignPriority(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY));
                campaignModel.setArabicScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ARABIC_SCRIPT));
                campaignModel.setEnglishScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ENGLISH_SCRIPT));
                campaignModel.setCampaignStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS));
                campaignModel.setControlPercentage(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE));

                campaignsList = ServicescampaignsMap.get(serviceId);
                if (campaignsList == null) {
                    campaignsList = new ArrayList<>();
                }
                campaignsList.add(campaignModel);
                ServicescampaignsMap.put(serviceId, campaignsList);
            }
            return ServicescampaignsMap;
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

//    public void updateSystemQuota(Connection connection, int systemID) throws CommonException {
//        PreparedStatement statement = null;
//        StringBuilder sql = null;
//        java.sql.Date todayDate = new java.sql.Date(new Date().getTime());
//
//        for (int retries = 0; retries < 10; retries++) {
//            try {
//                sql = new StringBuilder();
//                sql.append("MERGE INTO ").append(DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME)
//                        .append(" USING dual ON ( ").append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID).append(" = ? and Trunc(")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE).append(")= Trunc(?) )")
//                        .append("WHEN MATCHED THEN UPDATE SET ").append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER).append(" = ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER).append(" + 1 ")
//                        .append("WHEN NOT MATCHED THEN INSERT (").append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID).append(",")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE).append(",")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER).append(")")
//                        .append(" VALUES (?,Trunc(?),?)");
//                statement = connection.prepareStatement(sql.toString());
//                statement.setInt(1, systemID);
//                statement.setDate(2, todayDate);
//                statement.setInt(3, systemID);
//                statement.setDate(4, todayDate);
//                statement.setInt(5, 1);
//                statement.executeUpdate();
//                break;
//            } catch (SQLException e) {
//                if (retries < 9) {
//                    continue;
//                }
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//                throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//            } catch (Exception e) {
//                if (retries < 9) {
//                    continue;
//                }
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//            } finally {
//                try {
//                    if (statement != null) {
//                        statement.close();
//                    }
//                } catch (SQLException e) {
//                    CommonLogger.businessLogger.error(e.getMessage());
//                    CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
//            }
//        }
//    }
//
//    public void updateSystemMonitorCounter(Connection connection, int systemID) throws CommonException {
//        PreparedStatement statement = null;
//        StringBuilder sql = null;
//        java.sql.Date todayDate = new java.sql.Date(new Date().getTime());
//
//        for (int retries = 0; retries < 10; retries++) {
//            try {
//                sql = new StringBuilder();
//                sql.append("MERGE INTO ").append(DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME)
//                        .append(" USING dual ON ( ").append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID).append(" = ? and Trunc(")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE).append(")= Trunc(?) )")
//                        .append("WHEN MATCHED THEN UPDATE SET ").append(DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER).append(" = ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER).append(" + 1 ")
//                        .append("WHEN NOT MATCHED THEN INSERT (").append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID).append(",")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE).append(",")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER).append(")")
//                        .append(" VALUES (?,Trunc(?),?)");
//                statement = connection.prepareStatement(sql.toString());
//                statement.setInt(1, systemID);
//                statement.setDate(2, todayDate);
//                statement.setInt(3, systemID);
//                statement.setDate(4, todayDate);
//                statement.setInt(5, 1);
//                statement.executeUpdate();
//                break;
//            } catch (SQLException e) {
//                if (retries < 9) {
//                    continue;
//                }
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//                throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//            } catch (Exception e) {
//                if (retries < 9) {
//                    continue;
//                }
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//            } finally {
//                try {
//                    if (statement != null) {
//                        statement.close();
//                    }
//                } catch (SQLException e) {
//                    CommonLogger.businessLogger.error(e.getMessage());
//                    CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
//            }
//        }
//    }
//
//    public void updateDoNotApplyCounter(Connection connection, int systemID) throws CommonException {
//        PreparedStatement statement = null;
//        StringBuilder sql = null;
//        java.sql.Date todayDate = new java.sql.Date(new Date().getTime());
//
//        for (int retries = 0; retries < 10; retries++) {
//            try {
//                sql = new StringBuilder();
//                sql.append("MERGE INTO ").append(DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME)
//                        .append(" USING dual ON ( ").append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID).append(" = ? and Trunc(")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE).append(")= Trunc(?) )")
//                        .append("WHEN MATCHED THEN UPDATE SET ").append(DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER).append(" = ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER).append(" + 1 ")
//                        .append("WHEN NOT MATCHED THEN INSERT (").append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID).append(",")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE).append(",")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER).append(")")
//                        .append(" VALUES (?,Trunc(?),?)");
//                statement = connection.prepareStatement(sql.toString());
//                statement.setInt(1, systemID);
//                statement.setDate(2, todayDate);
//                statement.setInt(3, systemID);
//                statement.setDate(4, todayDate);
//                statement.setInt(5, 1);
//                statement.executeUpdate();
//                break;
//            } catch (SQLException e) {
//                if (retries < 9) {
//                    continue;
//                }
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//                throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//            } catch (Exception e) {
//                if (retries < 9) {
//                    continue;
//                }
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//            } finally {
//                try {
//                    if (statement != null) {
//                        statement.close();
//                    }
//                } catch (SQLException e) {
//                    CommonLogger.businessLogger.error(e.getMessage());
//                    CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
//            }
//        }
//    }
//
//    public int getDailyQoutaCounter(Connection connection, int serviceID) throws CommonException {
//        PreparedStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        ResultSet resultSet = null;
//        java.sql.Date todayDate = new java.sql.Date(new Date().getTime());
//        try {
//            sql.append("SELECT ")
//                    .append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)
//                    .append(" FROM ")
//                    .append(DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME)
//                    .append(" WHERE ")
//                    .append(DBStruct.SERVICE.SERVICE_ID)
//                    .append(" = ? AND Trunc(")
//                    .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE)
//                    .append(") = Trunc(?)");
//
//            statement = connection.prepareStatement(sql.toString());
//            statement.setInt(1, serviceID);
//            statement.setDate(2, todayDate);
//
//            resultSet = statement.executeQuery();
//
//            if (resultSet.next()) {
//                return resultSet.getInt(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER);
//            }
//            return 0;
//        } catch (SQLException e) {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//        } catch (Exception e) {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            try {
//                if (statement != null) {
//                    statement.close();
//                }
//            } catch (SQLException e) {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//            try {
//                if (resultSet != null) {
//                    resultSet.close();
//                }
//            } catch (SQLException e) {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
//
//    public long updateAndSelectServiceQouta(Connection connection, int serviceID, long serviceQouta) throws CommonException {
//        CallableStatement statement = null;
//        StringBuilder sql = null;
//        java.sql.Date todayDate = new java.sql.Date(new Date().getTime());
//        ResultSet resultSet = null;
//        boolean hasRecord;
//        long qouta = -1;
//
//        for (int retries = 0; retries < 10; retries++) {
//            hasRecord = false;
//            try {
//                sql = new StringBuilder();
//                sql.append("Select ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)
//                        .append(" from ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME)
//                        .append(" Where ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID)
//                        .append(" = ? AND Trunc(")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE)
//                        .append(") = Trunc(?) For Update");
//
////                CommonLogger.businessLogger.info("Attempting SQL: " + sql.toString());
//                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Attempting SQL Query")
//                        .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
//
//                statement = connection.prepareCall(sql.toString());
//                statement.setInt(1, serviceID);
//                statement.setDate(2, todayDate);
//                resultSet = statement.executeQuery();
//
//                while (resultSet.next()) {
//                    qouta = resultSet.getLong(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER);
//                    hasRecord = true;
//                }
//
//                try {
//                    resultSet.close();
//                    statement.close();
//                } catch (SQLException e) {
//                    CommonLogger.businessLogger.error(e.getMessage());
//                    CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
//
//                if (!hasRecord) {//Insert Record
////                    CommonLogger.businessLogger.info("No Records Found for Service " + serviceID + " Today... Adding A New Record");
//                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No Records Found for Service Today... Adding a New Record")
//                            .put(GeneralConstants.StructuredLogKeys.SERVIC_ID, serviceID).build());
//
////                    statement.close();
//                    sql = new StringBuilder();
//                    sql.append("Insert Into ")
//                            .append(DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME)
//                            .append("(")
//                            .append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID)
//                            .append(", ")
//                            .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE)
//                            .append(", ")
//                            .append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)
//                            .append(") Values (?, Trunc(?), ?)");
//
//                    statement = connection.prepareCall(sql.toString());
//                    statement.setInt(1, serviceID);
//                    statement.setDate(2, todayDate);
//                    statement.setLong(3, 1);
//                    statement.executeUpdate();
//                    qouta = 1;
//                } else {//Update Record
//                    if (qouta < serviceQouta) {
////                        CommonLogger.businessLogger.info("Records Found for Service " + serviceID + " Today and incrementing Qouta: " + qouta);
//                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Records Found for Service today and incrementing Qouta")
//                                .put(GeneralConstants.StructuredLogKeys.SERVIC_ID, serviceID)
//                                .put(GeneralConstants.StructuredLogKeys.QOUTA, qouta).build());
//
////                        statement.close();
//                        sql = new StringBuilder();
//                        sql.append("Update ")
//                                .append(DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME)
//                                .append(" Set ")
//                                .append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)
//                                .append(" = ")
//                                .append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)
//                                .append(" + 1 WHERE ")
//                                .append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID)
//                                .append(" = ? AND Trunc(")
//                                .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE)
//                                .append(") = Trunc(?)");
//
//                        statement = connection.prepareCall(sql.toString());
//                        statement.setInt(1, serviceID);
//                        statement.setDate(2, todayDate);
//                        statement.executeUpdate();
//                        qouta++;
//                    } else {
////                        CommonLogger.businessLogger.info("Records Found for Service " + serviceID + " Today and Maximum Qouta Reached: " + qouta);
//                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Records Found for Service today and maximum Qouta Reached")
//                                .put(GeneralConstants.StructuredLogKeys.SERVIC_ID, serviceID)
//                                .put(GeneralConstants.StructuredLogKeys.QOUTA, qouta).build());
//                        qouta = -1;
//                    }
//                }
//                break;
//            } catch (SQLException e) {
//                if (retries < 9) {
//                    continue;
//                }
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//                throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//            } catch (Exception e) {
//                if (retries < 9) {
//                    continue;
//                }
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//            } finally {
//                try {
//                    if (resultSet != null) {
//                        resultSet.close();
//                    }
//                    if (statement != null) {
//                        statement.close();
//                    }
//                } catch (SQLException e) {
//                    CommonLogger.businessLogger.error(e.getMessage());
//                    CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
//            }
//        }
//        return qouta;
//    }
//
////    public long updateAndSelectServiceQouta(Connection connection, int serviceID, long serviceQouta) throws CommonException 
////    {
////        CallableStatement statement = null;
////        StringBuilder sql = new StringBuilder();
////        java.sql.Date todayDate = new java.sql.Date(new Date().getTime());
////        try 
////        {
////            sql.append("CALL " + DBStruct.STORED_PROCEDURES.UPD_AND_SEL_SERVICE_QOUTA.UPD_AND_SEL_SERVICE_QOUTA + " (?, ?, ?, ?)");
////            
////            statement = connection.prepareCall(sql.toString());
////            statement.setInt(1, serviceID);
////            statement.setDate(2, todayDate);
////            statement.setLong(3, serviceQouta);
////            statement.registerOutParameter(4, Types.NUMERIC);
////            
////            statement.executeUpdate();
////            
////            return statement.getLong(4);
////        }
////        catch (SQLException e) 
////        {
////            CommonLogger.businessLogger.error(e.getMessage());
////            CommonLogger.errorLogger.error(e.getMessage(), e);
////            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
////        }
////        catch (Exception e) 
////        {
////            CommonLogger.businessLogger.error(e.getMessage());
////            CommonLogger.errorLogger.error(e.getMessage(), e);
////            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
////        }
////        finally 
////        {
////            try 
////            {
////                if (statement != null) 
////                {
////                    statement.close();
////                }
////            } 
////            catch (SQLException e) 
////            {
////                CommonLogger.businessLogger.error(e.getMessage());
////                CommonLogger.errorLogger.error(e.getMessage(), e);
////            }
////        }
////    }
//    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
//    public void decrementServiceCounters(Connection connection, int systemID, boolean decrementDoNotApply,
//            boolean decrementMonitorCounter, boolean decrementDailyQouta) throws CommonException {
//        PreparedStatement statement = null;
//        StringBuilder sql = new StringBuilder();
//        boolean firstCounter = true;
//
//        if (!decrementDoNotApply && !decrementMonitorCounter && !decrementDailyQouta) {
//            throw new CommonException("All decrement flags are false... Entered Flags:- decrementDoNotApply: " + decrementDoNotApply
//                    + " | decrementMonitorCounter: " + decrementMonitorCounter + " | decrementDailyQouta: " + decrementDailyQouta, "");
//        }
//
//        try {
//            sql.append("Update ")
//                    .append(DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME)
//                    .append(" Set ");
//
//            if (decrementDoNotApply) {
//                firstCounter = false;
//                sql.append(DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER)
//                        .append(" = ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER)
//                        .append(" - 1 ");
//            }
//
//            if (decrementMonitorCounter) {
//                if (!firstCounter) {
//                    sql.append(" , ");
//                } else {
//                    firstCounter = false;
//                }
//                sql.append(DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER)
//                        .append(" = ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER)
//                        .append(" - 1 ");
//            }
//
//            if (decrementDailyQouta) {
//                if (!firstCounter) {
//                    sql.append(" , ");
//                } else {
//                    firstCounter = false;
//                }
//                sql.append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)
//                        .append(" = ")
//                        .append(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)
//                        .append(" - 1 ");
//            }
//
//            sql.append("WHERE ")
//                    .append(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID)
//                    .append(" = ? AND Trunc(")
//                    .append(DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE)
//                    .append(") = Trunc(?)");
//
//            statement = connection.prepareStatement(sql.toString());
//            statement.setInt(1, systemID);
//            statement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//        } catch (Exception e) {
//            CommonLogger.businessLogger.error(e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            try {
//                if (statement != null) {
//                    statement.close();
//                }
//            } catch (SQLException e) {
//                CommonLogger.businessLogger.error(e.getMessage());
//                CommonLogger.errorLogger.error(e.getMessage(), e);
//            }
//        }
//    }
    public static int updateServiceQuota(Connection connection,
            Integer serviceId,
            Integer value,
            Long maxValue,
            String counterColumn) throws Exception {

        PreparedStatement statement = null;
        try {
//            sql.append("WHERE ")
//                    .append(DBStruct.ADM_SERVICE_HISTORY.SERVICE_ID)
//                    .append(" = ? AND Trunc(")
//                    .append(DBStruct.ADM_SERVICE_HISTORY.SENDING_DATE)
//                    .append(") = Trunc(?)");
            String query = "Update " + DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME + " Set "
                    + counterColumn + " = " + counterColumn + " + ?"
                    + " Where " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID + " = ?"
                    + " And Trunc(" + DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE + ") = Trunc(SYSDATE)";
            if (maxValue != null) {
                query += " And " + counterColumn + " < ?";
            }
            statement = connection.prepareStatement(query);
            int i = 1;
            statement.setInt(i++, value);
            statement.setInt(i++, serviceId);
            if (maxValue != null) {
                statement.setLong(i++, maxValue);
            }

            return statement.executeUpdate();
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

    public static void updateServiceQuota(Connection connection,
            HashMap<Integer, ServiceQuotaCounter> serviceQuota) throws Exception {

        PreparedStatement statement = null;
        try {
            String query = "Update " + DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME + " Set "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER + " = " + DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER + " + ?, "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER + " = " + DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER + " + ?, "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER + " = " + DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER + " + ? "
                    + " Where " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID + " = ?"
                    + " And Trunc(" + DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE + ") = Trunc(SYSDATE)";
            statement = connection.prepareStatement(query);
            for (Map.Entry<Integer, ServiceQuotaCounter> entry : serviceQuota.entrySet()) {
                int i = 1;
                statement.setInt(i++, entry.getValue().getDailyQuotaCounter().get());
                statement.setInt(i++, entry.getValue().getMonitorCounter().get());
                statement.setInt(i++, entry.getValue().getDoNotApplyCounter().get());
                statement.setInt(i++, entry.getKey());
                statement.addBatch();
            }

            statement.executeBatch();
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

    public static int getServiceQuota(Connection connection,
            Integer serviceId,
            String counterColumn) throws Exception {

        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
//            sql.append("WHERE ")
//                    .append(DBStruct.ADM_SERVICE_HISTORY.SERVICE_ID)
//                    .append(" = ? AND Trunc(")
//                    .append(DBStruct.ADM_SERVICE_HISTORY.SENDING_DATE)
//                    .append(") = Trunc(?)");
            String query = "Select Count(" + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID + ") as Count From "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME
                    + " Where " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID + " = ?"
                    + " And Trunc(" + DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE + ") = Trunc(SYSDATE)";
            statement = connection.prepareStatement(query);
            int i = 1;
            statement.setInt(i++, serviceId);

            rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("Count");
            }
            return 0;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
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

    public static HashMap<Integer, ArrayList<ServiceQuotaCounter>> getServiceQuota(Connection connection) throws Exception {

        PreparedStatement statement = null;
        ResultSet rs = null;
        HashMap<Integer, ArrayList<ServiceQuotaCounter>> serviceQuota = new HashMap<>();

        try {
            String query = "Select * From " + DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME
                    + " Where Trunc(" + DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE + ") = Trunc(SYSDATE)"
                    + " Order By " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_HISTORY_ID;
            statement = connection.prepareStatement(query);

//            int i = 1;
//            statement.setInt(i++, serviceId);
            rs = statement.executeQuery();

            while (rs.next()) {
                ServiceQuotaCounter serviceQuotaCounter = new ServiceQuotaCounter(rs.getInt(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID), rs.getInt(DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_HISTORY_ID));
                serviceQuotaCounter.getDailyQuotaCounter().set(rs.getInt(DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER));
                serviceQuotaCounter.getDoNotApplyCounter().set(rs.getInt(DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER));
                serviceQuotaCounter.getMonitorCounter().set(rs.getInt(DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER));
                if (!serviceQuota.containsKey(serviceQuotaCounter.getServiceId())) {
                    serviceQuota.put(serviceQuotaCounter.getServiceId(), new ArrayList<>());
                }
                serviceQuota.get(serviceQuotaCounter.getServiceId()).add(serviceQuotaCounter);
            }

            return serviceQuota;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
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

    public static int insertServiceQuota(Connection connection,
            Integer serviceId,
            Integer value,
            String counterColumn) throws Exception {

        PreparedStatement statement = null;
        try {
//            sql.append("WHERE ")
//                    .append(DBStruct.ADM_SERVICE_HISTORY.SERVICE_ID)
//                    .append(" = ? AND Trunc(")
//                    .append(DBStruct.ADM_SERVICE_HISTORY.SENDING_DATE)
//                    .append(") = Trunc(?)");
            String query = "Insert Into " + DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME + " ("
                    + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_HISTORY_ID + ", "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID + ", "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE + ", "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER + ", "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER + ", "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER;
//            if (!counterColumn.equals(DBStruct.ADM_SERVICE_HISTORY.DAILY_QUOTA_COUNTER)) {
//                query += ", " + DBStruct.ADM_SERVICE_HISTORY.DAILY_QUOTA_COUNTER;
//            }
//            if (!counterColumn.equals(DBStruct.ADM_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER)) {
//                query += ", " + DBStruct.ADM_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER;
//            }
//            if (!counterColumn.equals(DBStruct.ADM_SERVICE_HISTORY.MONITOR_COUNTER)) {
//                query += ", " + DBStruct.ADM_SERVICE_HISTORY.MONITOR_COUNTER;
//            }
            query += ") Values (" + DBStruct.VFE_CS_SERVICE_HISTORY.VFE_CS_SERVICE_HISTORY_SEQ + ".nextval, ?, SYSDATE, 0, 0, 0)";
            statement = connection.prepareStatement(query);
            int i = 1;
            statement.setInt(i++, serviceId);

            return statement.executeUpdate();
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

    public static int adjustDuplicatedServiceQuota(Connection connection,
            Integer serviceId) throws Exception {

        PreparedStatement statement = null;
        try {
//            sql.append("WHERE ")
//                    .append(DBStruct.ADM_SERVICE_HISTORY.SERVICE_ID)
//                    .append(" = ? AND Trunc(")
//                    .append(DBStruct.ADM_SERVICE_HISTORY.SENDING_DATE)
//                    .append(") = Trunc(?)");

            /*
                DELETE
                FROM
                        ADM_SERVICE_HISTORY
                WHERE
                        SERVICE_HISTORY_ID IN (
                        SELECT SERVICE_HISTORY_ID FROM (
                        SELECT
                                SERVICE_HISTORY_ID, ROWNUM AS row_num
                        FROM
                                ADM_SERVICE_HISTORY
                        WHERE
                                SERVICE_ID = 2069
                                AND TRUNC(SENDING_DATE) = TRUNC(SYSDATE)
                        ORDER BY
                                SERVICE_HISTORY_ID ASC)
                        WHERE row_num > 1)
             */
            String query = "Delete From " + DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME
                    + " Where " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_HISTORY_ID
                    + " In (SELECT " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_HISTORY_ID
                    + " FROM ( Select " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_HISTORY_ID + ", ROWNUM AS row_num From "
                    + DBStruct.VFE_CS_SERVICE_HISTORY.TABLE_NAME
                    + " Where " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_ID + " = ?"
                    + " And TRUNC(" + DBStruct.VFE_CS_SERVICE_HISTORY.SENDING_DATE + ") = TRUNC(SYSDATE)"
                    //                    + " And rownum > 2"
                    + " Order By " + DBStruct.VFE_CS_SERVICE_HISTORY.SERVICE_HISTORY_ID + " ASC )WHERE row_num > 1)";
            statement = connection.prepareStatement(query);
            int i = 1;
            statement.setInt(i++, serviceId);

            return statement.executeUpdate();
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
