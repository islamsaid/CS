/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.InterfaceTypeModel;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.ServiceCategoryModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.models.ServiceTypeModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Amal Magdy
 */
public class ServiceManagmentDAO {

    public Boolean checkCampaignServiceByService(ServiceModel serviceModel, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkCampaignServiceByService...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkCampaignServiceByService Started").build());

        PreparedStatement statement = null;
        StringBuilder sql;
        ResultSet resultSet = null;

        try {
            sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(DBStruct.CAMPAIGNS_SERVICES.TABLE_NAME)
                    .append(" WHERE ").append(DBStruct.CAMPAIGNS_SERVICES.SERVICE_ID).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, serviceModel.getVersionId());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
//                CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkCampaignServiceByService...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkCampaignServiceByService Ended").build());
                return true;
            } else {
//                CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkCampaignServiceByService...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkCampaignServiceByService Ended").build());
                return false;
            }
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkCampaignServiceByService()" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkCampaignServiceByService()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkCampaignServiceByService()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }
    }

    public ArrayList<ServiceModel> getServices(Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting getServices...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getServices Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        ResultSet resultSetWhiteList = null;

        try {

            sql = new StringBuilder();
            sql.append("SELECT s.* ,")
                    .append("       t.TYPE_NAME ,\n")
                    .append("       i.INTERFACE_NAME ,\n")
                    .append("       c.CATEGORY_NAME ,\n")
                    .append("       q.* ,\n")
                    .append("       u.* \n")
                    .append("       from    VFE_CS_SERVICES s \n")
                    .append("       inner join VFE_CS_SERVICE_TYPE t on s.SYSTEM_TYPE=t.TYPE_ID \n")
                    .append("       inner join VFE_CS_INTERFACE_TYPE i on S.INTERFACE_TYPE=I.INTERFACE_ID\n")
                    .append("       inner join VFE_CS_SERVICE_CATEGORY c on S.SYSTEM_CATEGORY=C.CATEGORY_ID\n")
                    .append("       inner join VFE_CS_APP_QUEUES q on S.APP_ID=Q.VERSION_ID ")
                    .append(" inner join ").append(DBStruct.VFE_CS_USERS.TABLE_NAME).append(" u on S.CREATOR = u.USER_ID ");
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();

            ArrayList<ServiceModel> serviceModels = new ArrayList<>();
            while (resultSet.next()) {
                ServiceModel serviceModel = new ServiceModel();
                ServiceTypeModel selectedServiceTypeModel = new ServiceTypeModel();
                ServiceCategoryModel selectedServiceCategoryModel = new ServiceCategoryModel();
                InterfaceTypeModel selectedInterfaceTypeModel = new InterfaceTypeModel();
                QueueModel selectedApplicationQueueModel = new QueueModel();
                serviceModel.setServiceID(resultSet.getInt(DBStruct.SERVICE.SERVICE_ID));
                serviceModel.setServiceName(resultSet.getString(DBStruct.SERVICE.SERVICE_NAME));
                serviceModel.setSelectedInterfaceTypeID(resultSet.getInt(DBStruct.SERVICE.INTERFACE_TYPE));
                serviceModel.setSelectedServiceTypeID(resultSet.getInt(DBStruct.SERVICE.SYSTEM_TYPE));
                serviceModel.setSelectedServiceCategoryID(resultSet.getInt(DBStruct.SERVICE.SYSTEM_CATEGORY));
                serviceModel.setDeliveryReport((resultSet.getInt(DBStruct.SERVICE.DELIVERY_REPORT)) == 1 ? true : false);
                serviceModel.setConsultCounter((resultSet.getInt(DBStruct.SERVICE.CONSULT_COUNTER)) == 1 ? true : false);
                serviceModel.setAdsConsultCounter((resultSet.getInt(DBStruct.SERVICE.ADS_CONSULT_COUNTER)) == 1 ? true : false);
                serviceModel.setSupportAds((resultSet.getInt(DBStruct.SERVICE.SUPPORT_ADS)) == 1 ? true : false);
                serviceModel.setHasWhiteList((resultSet.getInt(DBStruct.SERVICE.HAS_WHITELIST)) == 1 ? true : false);
                serviceModel.setAutoCreatdFlag((resultSet.getInt(DBStruct.SERVICE.AUTO_CREATE_FLAG)) == 1 ? true : false);
                serviceModel.setAllowedSMS(resultSet.getInt(DBStruct.SERVICE.ALLOWED_SMS));
                serviceModel.setDailyQuota(resultSet.getInt(DBStruct.SERVICE.DAILY_QUOTA));
                serviceModel.setProcedureMaxBatchSize(resultSet.getInt(DBStruct.SERVICE.MAX_BATCH_SIZE));
                serviceModel.setSmsProcedureQueueId(resultSet.getInt(DBStruct.SERVICE.SMS_PROCEDURE_QUEUE_ID));
                serviceModel.setSelectedApplicationQueueID(resultSet.getInt(DBStruct.SERVICE.APP_ID));
                serviceModel.setCreatorName(resultSet.getString(DBStruct.VFE_CS_USERS.USER_NAME));

                //setting selected lookups
                selectedInterfaceTypeModel.setId(resultSet.getInt(DBStruct.SERVICE.INTERFACE_TYPE));
                selectedInterfaceTypeModel.setLable(resultSet.getString("INTERFACE_NAME"));
                selectedServiceTypeModel.setId(resultSet.getInt(DBStruct.SERVICE.SYSTEM_TYPE));
                selectedServiceTypeModel.setLable(resultSet.getString("TYPE_NAME"));
                selectedServiceCategoryModel.setId(resultSet.getInt(DBStruct.SERVICE.SYSTEM_CATEGORY));
                selectedServiceCategoryModel.setLable(resultSet.getString("CATEGORY_NAME"));
                //Handling Queue Model
//                selectedApplicationQueueModel.setAppId(resultSet.getInt(DBStruct.SERVICE.APP_ID));
//                selectedApplicationQueueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                selectedApplicationQueueModel.setVersionId(resultSet.getInt(DBStruct.APPQUEUE.VERSION_ID));
                selectedApplicationQueueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
                selectedApplicationQueueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                selectedApplicationQueueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
                selectedApplicationQueueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
                selectedApplicationQueueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
                selectedApplicationQueueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
                selectedApplicationQueueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
                selectedApplicationQueueModel.setDatabaseUrl(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
                selectedApplicationQueueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
                selectedApplicationQueueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));

                selectedApplicationQueueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
                    selectedApplicationQueueModel.setTimeWindowFlag(true);
                } else {
                    selectedApplicationQueueModel.setTimeWindowFlag(false);
                }
                selectedApplicationQueueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
                selectedApplicationQueueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
                selectedApplicationQueueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
                selectedApplicationQueueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));

                serviceModel.setSelectedInterfaceTypeModel(selectedInterfaceTypeModel);
                serviceModel.setSelectedServiceCategoryModel(selectedServiceCategoryModel);
                serviceModel.setSelectedServiceTypeModel(selectedServiceTypeModel);
                serviceModel.setSelectedApplicationQueueModel(selectedApplicationQueueModel);
                serviceModel.setVersionId(resultSet.getInt(DBStruct.SERVICE.VERSION_ID));
                serviceModel.setStatus(resultSet.getInt(DBStruct.SERVICE.STATUS));
                serviceModel.setCreator(resultSet.getInt(DBStruct.SERVICE.CREATOR));
                serviceModel.setServicePrivilege(resultSet.getInt(DBStruct.SERVICE.PRIVILEGE_LEVEL));
                serviceModel.setOriginatorType(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR_TYPE));
                serviceModel.setOriginatorValue(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR));
                serviceModel.setHashedPassword(resultSet.getBytes(DBStruct.SERVICE.PASSWORD));
//                //TODO: load whitelist
                sql = new StringBuilder();
                sql.append("SELECT * from ")
                        .append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(" where ")
                        .append(DBStruct.SERVICE_WHITELIST.SERVICE_ID).append(" =? ");
//                        .append("and ")
//                        .append(DBStruct.SERVICE_WHITELIST.ID).append(" =? ");

                // DataSourceManger.closeDBResources(null, statement); // eslam.ahmed | 5-5-2020
                statement = connection.prepareStatement(sql.toString());
                statement.setLong(1, serviceModel.getVersionId());
                //statement.setLong(2, serviceModel.getId());
                resultSetWhiteList = statement.executeQuery();
                while (resultSetWhiteList.next()) {
                    serviceModel.getWhiteListIPs().add(resultSetWhiteList.getString(DBStruct.SERVICE_WHITELIST.IP_ADDRESS));
                }
                ////

                serviceModels.add(serviceModel);
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End getServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getServices Ended").build());

            return serviceModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [getServices]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [getServices]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [getServices]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public boolean checkService(Connection connection, String serviceName) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkService...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkService Started").build());
        PreparedStatement statement = null;
        StringBuilder sql;
        ResultSet resultSet = null;
        boolean exist = false;

        try {
            sql = new StringBuilder();
            sql.append("SELECT ").append(DBStruct.SERVICE.SERVICE_ID)
                    .append(" FROM ").append(DBStruct.SERVICE.TABLE_NAME)
                    .append(" WHERE ").append(DBStruct.SERVICE.SERVICE_NAME).append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, serviceName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
//                CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkService...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkService Ended").build());

                exist = true;
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkService...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkService Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkService()" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkService()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkService()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            return exist;
        }
    }

    public boolean checkIpAddress(Connection connection, String serviceName, String ipAddress) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkIpAddress...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkService Starting").build());

        PreparedStatement statement = null;
        StringBuilder sql;
        ResultSet resultSet = null;

        try {
            sql = new StringBuilder();
            sql.append("SELECT ").append(DBStruct.SERVICE.SERVICE_ID)
                    .append(" , ").append(DBStruct.SERVICE_WHITELIST.SERVICE_ID)
                    .append(" FROM ").append(DBStruct.SERVICE.TABLE_NAME)
                    .append(" INNER JOIN ").append(DBStruct.SERVICE_WHITELIST.TABLE_NAME)
                    .append(" ON (").append(DBStruct.SERVICE.TABLE_NAME).append(".").append(DBStruct.SERVICE.SERVICE_ID)
                    .append(" = ").append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(".").append(DBStruct.SERVICE_WHITELIST.SERVICE_ID)
                    .append(") WHERE ").append(DBStruct.SERVICE.TABLE_NAME).append(".").append(DBStruct.SERVICE.SERVICE_NAME).append(" = ? ")
                    .append(" AND ").append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(".").append(DBStruct.SERVICE_WHITELIST.IP_ADDRESS).append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, serviceName);
            statement.setString(2, ipAddress);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
//                CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkIpAddress...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkIpAddress Ended").build());

                return true;
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkIpAddress...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkIpAddress Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkIpAddress()" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkIpAddress()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkIpAddress()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            return false;
        }
    }

    public void insertService(Connection connection, ServiceModel serviceModel) throws CommonException {
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting [insertService]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertService Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {
            sql = new StringBuilder();
            sql.append("INSERT INTO ").append(DBStruct.SERVICE.TABLE_NAME).append(" (")
                    .append(DBStruct.SERVICE.SERVICE_NAME).append(",")
                    .append(DBStruct.SERVICE.DAILY_QUOTA).append(",")
                    .append(DBStruct.SERVICE.INTERFACE_TYPE).append(",")
                    .append(DBStruct.SERVICE.SYSTEM_TYPE).append(",")
                    .append(DBStruct.SERVICE.DELIVERY_REPORT).append(",")
                    .append(DBStruct.SERVICE.SYSTEM_CATEGORY).append(",")
                    .append(DBStruct.SERVICE.CONSULT_COUNTER).append(",")
                    .append(DBStruct.SERVICE.ADS_CONSULT_COUNTER).append(",")
                    .append(DBStruct.SERVICE.SUPPORT_ADS).append(",")
                    .append(DBStruct.SERVICE.APP_ID).append(",")
                    .append(DBStruct.SERVICE.ALLOWED_SMS).append(",")
                    .append(DBStruct.SERVICE.HAS_WHITELIST).append(",")
                    .append(DBStruct.SERVICE.AUTO_CREATE_FLAG).append(",")
                    .append(DBStruct.SERVICE.SERVICE_ID).append(",")
                    .append(DBStruct.SERVICE.VERSION_ID).append(",")
                    .append(DBStruct.SERVICE.STATUS).append(",")
                    .append(DBStruct.SERVICE.CREATOR).append(",")
                    .append(DBStruct.SERVICE.ORIGINATOR).append(",")
                    .append(DBStruct.SERVICE.ORIGINATOR_TYPE).append(",")
                    .append(DBStruct.SERVICE.PRIVILEGE_LEVEL).append(",")
                    .append(DBStruct.SERVICE.MAX_BATCH_SIZE).append(",")
                    .append(DBStruct.SERVICE.SMS_PROCEDURE_QUEUE_ID).append(",")
                    .append(DBStruct.SERVICE.PASSWORD).append(")")
                    .append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, serviceModel.getServiceName());
            statement.setInt(2, serviceModel.getDailyQuota());
            statement.setLong(3, serviceModel.getSelectedInterfaceTypeID());
            statement.setLong(4, serviceModel.getSelectedServiceTypeID());
            statement.setInt(5, serviceModel.isDeliveryReport() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setLong(6, serviceModel.getSelectedServiceCategoryID());
            statement.setInt(7, serviceModel.isConsultCounter() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(8, serviceModel.isAdsConsultCounter() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(9, serviceModel.isSupportAds() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setLong(10, serviceModel.getSelectedApplicationQueueID());
            statement.setInt(11, serviceModel.getAllowedSMS());
            statement.setInt(12, serviceModel.isHasWhiteList() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(13, serviceModel.isAutoCreatdFlag() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setLong(14, serviceModel.getServiceID());
            statement.setLong(15, serviceModel.getVersionId());
            statement.setInt(16, serviceModel.getStatus());
            statement.setInt(17, serviceModel.getCreator());
            if (serviceModel.getServicePrivilege() == 1) {
                statement.setNull(18, Types.INTEGER);
                statement.setNull(19, Types.INTEGER);
            } else {
                statement.setInt(18, serviceModel.getOriginatorValue());
                statement.setInt(19, serviceModel.getOriginatorType());

            }
            statement.setInt(20, serviceModel.getServicePrivilege());
            statement.setInt(21, serviceModel.getProcedureMaxBatchSize());
            if (serviceModel.getSmsProcedureQueueId() == -1) {
                statement.setNull(22, Types.INTEGER);
            } else {
                statement.setInt(22, serviceModel.getSmsProcedureQueueId());
            }
            statement.setBytes(23, serviceModel.getHashedPassword());

            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End [insertService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertService Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [insertService]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [insertService]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [insertService]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    //TODO: add both ids of service //Done
    public void insertServiceWhiteList(Connection connection, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting [insertServiceWhiteList]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertServiceWhiteList Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {
//            sql = new StringBuilder();
//            sql.append("Delete From ").append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(" where ")
//                    .append(DBStruct.SERVICE_WHITELIST.SERVICE_ID).append(" = ?");
//            statement = connection.prepareStatement(sql.toString());
//            statement.setLong(1, serviceModel.getServiceID());
//            statement.executeQuery();

            deleteOneServiceWhietListVersion(connection, serviceModel);
            sql = new StringBuilder();
            sql.append("Insert into ").append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(" ( ")
                    .append(DBStruct.SERVICE_WHITELIST.SERVICE_ID).append(",")
                    //.append(DBStruct.SERVICE_WHITELIST.ID).append(",")
                    .append(DBStruct.SERVICE_WHITELIST.IP_ADDRESS).append(" ) VALUES (?,?)");
            statement = connection.prepareStatement(sql.toString());

            for (String ip : serviceWhitelistIPs) {
                //statement.setLong(1, serviceModel.getServiceID());
                statement.setLong(1, serviceModel.getVersionId());
                statement.setString(2, ip);
                statement.addBatch();
            }
            statement.executeBatch();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End [insertServiceWhiteList]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertServiceWhiteList Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [insertServiceWhiteList]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [insertServiceWhiteList]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [insertServiceWhiteList]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void deleteAllServiceVersions(Connection connection, ServiceModel serviceModel) throws CommonException {
        String methodName = "deleteAllServiceVersions";
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting [deleteAllServiceVersions]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAllServiceVersions Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.SERVICE.TABLE_NAME).append(" where ")
                    .append(DBStruct.SERVICE.SERVICE_ID).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, serviceModel.getServiceID());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End [deleteAllServiceVersions]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAllServiceVersions Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void deleteALLServiceWhietListVersions(Connection connection, ServiceModel serviceModel) throws CommonException {
        String methodName = "deleteALLServiceWhietListVersions";
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting [deleteAllServiceWhietListVersions]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAllServiceWhietListVersions Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(" where ")
                    .append(DBStruct.SERVICE_WHITELIST.SERVICE_ID).append(" = ?");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, serviceModel.getVersionId());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End [deleteAllServiceWhietListVersions]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAllServiceWhietListVersions Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void deleteOneServiceWhietListVersion(Connection connection, ServiceModel serviceModel) throws CommonException {
        String methodName = "deleteOneServiceWhietListVersion";
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting [deleteOneServiceWhietListVersions]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteOneServiceWhietListVersions Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(" where ")
                    .append(DBStruct.SERVICE_WHITELIST.SERVICE_ID).append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            //statement.setLong(1, serviceModel.getServiceID());
            statement.setLong(1, serviceModel.getVersionId());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End [deleteOneServiceWhietListVersions]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteOneServiceWhietListVersions Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void updateOneServiceVersion(Connection connection, ServiceModel serviceModel) throws CommonException {
        String methodName = "updateOneServiceVersion";
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting [updateOneServiceVersion]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateOneServiceVersion Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {
            sql = new StringBuilder();
            sql.append("Update ").append(DBStruct.SERVICE.TABLE_NAME).append(" set ")
                    .append(DBStruct.SERVICE.SERVICE_NAME).append(" = ? ,")
                    .append(DBStruct.SERVICE.DAILY_QUOTA).append(" = ? ,")
                    .append(DBStruct.SERVICE.INTERFACE_TYPE).append(" = ? ,")
                    .append(DBStruct.SERVICE.SYSTEM_TYPE).append(" = ? ,")
                    .append(DBStruct.SERVICE.DELIVERY_REPORT).append(" = ? ,")
                    .append(DBStruct.SERVICE.SYSTEM_CATEGORY).append(" = ? ,")
                    .append(DBStruct.SERVICE.CONSULT_COUNTER).append(" = ? ,")
                    .append(DBStruct.SERVICE.ADS_CONSULT_COUNTER).append(" = ? ,")
                    .append(DBStruct.SERVICE.SUPPORT_ADS).append(" = ? ,")
                    .append(DBStruct.SERVICE.APP_ID).append(" = ? ,")
                    .append(DBStruct.SERVICE.ALLOWED_SMS).append(" = ? ,")
                    .append(DBStruct.SERVICE.HAS_WHITELIST).append(" = ? ,")
                    .append(DBStruct.SERVICE.AUTO_CREATE_FLAG).append(" = ? ,")
                    .append(DBStruct.SERVICE.STATUS).append(" = ? ,")
                    .append(DBStruct.SERVICE.CREATOR).append(" = ? ,")
                    .append(DBStruct.SERVICE.ORIGINATOR).append(" = ? ,")
                    .append(DBStruct.SERVICE.ORIGINATOR_TYPE).append(" = ? ,")
                    .append(DBStruct.SERVICE.PRIVILEGE_LEVEL).append(" = ? ,")
                    .append(DBStruct.SERVICE.SMS_PROCEDURE_QUEUE_ID).append(" = ? ,")
                    .append(DBStruct.SERVICE.MAX_BATCH_SIZE).append(" = ? ,")
                    .append(DBStruct.SERVICE.PASSWORD).append(" = ? ")
                    .append(" where ").append(DBStruct.SERVICE.SERVICE_ID).append(" = ?")
                    .append(" and ").append(DBStruct.SERVICE.VERSION_ID).append(" = ?");

            statement = connection.prepareStatement(sql.toString());
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, serviceModel.getServiceName());
            statement.setInt(2, serviceModel.getDailyQuota());
            statement.setLong(3, serviceModel.getSelectedInterfaceTypeID());
            statement.setLong(4, serviceModel.getSelectedServiceTypeID());
            statement.setInt(5, serviceModel.isDeliveryReport() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setLong(6, serviceModel.getSelectedServiceCategoryID());
            statement.setInt(7, serviceModel.isConsultCounter() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(8, serviceModel.isAdsConsultCounter() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(9, serviceModel.isSupportAds() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setLong(10, serviceModel.getSelectedApplicationQueueID());
            statement.setInt(11, serviceModel.getAllowedSMS());
            statement.setInt(12, serviceModel.isHasWhiteList() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(13, serviceModel.isAutoCreatdFlag() ? GeneralConstants.TRUE : GeneralConstants.FALSE);
            statement.setInt(14, serviceModel.getStatus());
            statement.setInt(15, serviceModel.getCreator());
            if (serviceModel.getServicePrivilege() == 1) {
                statement.setNull(16, Types.INTEGER);
                statement.setNull(17, Types.INTEGER);
            } else {
                statement.setLong(16, serviceModel.getOriginatorValue());
                statement.setLong(17, serviceModel.getOriginatorType());
            }

            statement.setLong(18, serviceModel.getServicePrivilege());
            if (serviceModel.getSmsProcedureQueueId() == -1) {
                statement.setNull(19, Types.INTEGER);
            } else {
                statement.setInt(19, serviceModel.getSmsProcedureQueueId());
            }
            statement.setInt(20, serviceModel.getProcedureMaxBatchSize());

            statement.setBytes(21, serviceModel.getHashedPassword());
            statement.setLong(22, serviceModel.getServiceID());
            statement.setLong(23, serviceModel.getVersionId());

            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End [updateOneServiceVersion]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateOneServiceVersion Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void deleteOneServiceVersion(Connection connection, ServiceModel serviceModel) throws CommonException {
        String methodName = "deleteOneServiceVersion";
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting [deleteOneServiceVersion]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteOneServiceVersion Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {
            sql = new StringBuilder();
            sql.append("Delete From ").append(DBStruct.SERVICE.TABLE_NAME).append(" where ")
                    .append(DBStruct.SERVICE.SERVICE_ID).append(" = ? and ").append(DBStruct.SERVICE.VERSION_ID).append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, serviceModel.getServiceID());
            statement.setLong(2, serviceModel.getVersionId());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End [deleteOneServiceVersion]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteOneServiceVersion Ended").build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public ServiceModel getParentServiceVersion(Connection connection, long serviceID) throws CommonException {
        String methodName = "getParentServiceVersion";
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting getParentServiceVersion...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getParentServiceVersion Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;

        try {

            sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(DBStruct.SERVICE.TABLE_NAME).append(" where ").append(DBStruct.SERVICE.SERVICE_ID).append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, serviceID);
            resultSet = statement.executeQuery();
            ServiceModel serviceModel = null;

            while (resultSet.next()) {
                int status = resultSet.getInt(DBStruct.SERVICE.STATUS);
                if (status == GeneralConstants.STATUS_APPROVED_VALUE || status == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {

                    serviceModel = new ServiceModel();
                    serviceModel.setServiceID(resultSet.getInt(DBStruct.SERVICE.SERVICE_ID));
                    serviceModel.setServiceName(resultSet.getString(DBStruct.SERVICE.SERVICE_NAME));
                    serviceModel.setSelectedInterfaceTypeID(resultSet.getInt(DBStruct.SERVICE.INTERFACE_TYPE));
                    serviceModel.setSelectedServiceTypeID(resultSet.getInt(DBStruct.SERVICE.SYSTEM_TYPE));
                    serviceModel.setSelectedServiceCategoryID(resultSet.getInt(DBStruct.SERVICE.SYSTEM_CATEGORY));
                    serviceModel.setDeliveryReport((resultSet.getInt(DBStruct.SERVICE.DELIVERY_REPORT)) == 1 ? true : false);
                    serviceModel.setConsultCounter((resultSet.getInt(DBStruct.SERVICE.CONSULT_COUNTER)) == 1 ? true : false);
                    serviceModel.setAdsConsultCounter((resultSet.getInt(DBStruct.SERVICE.ADS_CONSULT_COUNTER)) == 1 ? true : false);
                    serviceModel.setSupportAds((resultSet.getInt(DBStruct.SERVICE.SUPPORT_ADS)) == 1 ? true : false);
                    serviceModel.setHasWhiteList((resultSet.getInt(DBStruct.SERVICE.HAS_WHITELIST)) == 1 ? true : false);
                    serviceModel.setHasWhiteList((resultSet.getInt(DBStruct.SERVICE.AUTO_CREATE_FLAG)) == 1 ? true : false);
                    serviceModel.setAllowedSMS(resultSet.getInt(DBStruct.SERVICE.ALLOWED_SMS));
                    serviceModel.setDailyQuota(resultSet.getInt(DBStruct.SERVICE.DAILY_QUOTA));
                    serviceModel.setSmsProcedureQueueId(resultSet.getInt(DBStruct.SERVICE.SMS_PROCEDURE_QUEUE_ID));
                    serviceModel.setSelectedApplicationQueueID(resultSet.getInt(DBStruct.SERVICE.APP_ID));
                    serviceModel.setVersionId(resultSet.getInt(DBStruct.SERVICE.VERSION_ID));
                    serviceModel.setStatus(resultSet.getInt(DBStruct.SERVICE.STATUS));
                    serviceModel.setCreator(resultSet.getInt(DBStruct.SERVICE.CREATOR));
                    serviceModel.setProcedureMaxBatchSize(resultSet.getInt(DBStruct.SERVICE.MAX_BATCH_SIZE));
                    serviceModel.setServicePrivilege(resultSet.getInt(DBStruct.SERVICE.PRIVILEGE_LEVEL));
                    serviceModel.setOriginatorType(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR_TYPE));
                    serviceModel.setOriginatorValue(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR));

                }
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End getParentServiceVersion...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getParentServiceVersion Ended").build());

            return serviceModel;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                resultSet.close();
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public ArrayList<ServiceModel> getApprovedServices(Connection connection, boolean serviceSupportAds) throws CommonException {
        String methodName = "getApprovedServices";
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting getApprovedServices...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedServices Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        ResultSet resultSetWhiteList = null;

        try {

            sql = new StringBuilder();
            sql.append("SELECT s.* ,")
                    .append("       t.TYPE_NAME ,\n")
                    .append("       i.INTERFACE_NAME ,\n")
                    .append("       c.CATEGORY_NAME ,\n")
                    .append("       q.* \n")
                    .append("       from    VFE_CS_SERVICES s \n")
                    .append("       inner join VFE_CS_SERVICE_TYPE t on s.SYSTEM_TYPE=t.TYPE_ID \n")
                    .append("       inner join VFE_CS_INTERFACE_TYPE i on S.INTERFACE_TYPE=I.INTERFACE_ID\n")
                    .append("       inner join VFE_CS_SERVICE_CATEGORY c on S.SYSTEM_CATEGORY=C.CATEGORY_ID\n")
                    .append("       inner join VFE_CS_APP_QUEUES q on S.APP_ID=Q.VERSION_ID where s.STATUS=1");
            if (serviceSupportAds) {
                sql.append(" and s.SUPPORT_ADS=1");
            }

            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();

            ArrayList<ServiceModel> serviceModels = new ArrayList<>();
            while (resultSet.next()) {
                ServiceModel serviceModel = new ServiceModel();
                ServiceTypeModel selectedServiceTypeModel = new ServiceTypeModel();
                ServiceCategoryModel selectedServiceCategoryModel = new ServiceCategoryModel();
                InterfaceTypeModel selectedInterfaceTypeModel = new InterfaceTypeModel();
                QueueModel selectedApplicationQueueModel = new QueueModel();
                serviceModel.setServiceID(resultSet.getInt(DBStruct.SERVICE.SERVICE_ID));
                serviceModel.setServiceName(resultSet.getString(DBStruct.SERVICE.SERVICE_NAME));
                serviceModel.setSelectedInterfaceTypeID(resultSet.getInt(DBStruct.SERVICE.INTERFACE_TYPE));
                serviceModel.setSelectedServiceTypeID(resultSet.getInt(DBStruct.SERVICE.SYSTEM_TYPE));
                serviceModel.setSelectedServiceCategoryID(resultSet.getInt(DBStruct.SERVICE.SYSTEM_CATEGORY));
                serviceModel.setDeliveryReport((resultSet.getInt(DBStruct.SERVICE.DELIVERY_REPORT)) == 1 ? true : false);
                serviceModel.setConsultCounter((resultSet.getInt(DBStruct.SERVICE.CONSULT_COUNTER)) == 1 ? true : false);
                serviceModel.setAdsConsultCounter((resultSet.getInt(DBStruct.SERVICE.ADS_CONSULT_COUNTER)) == 1 ? true : false);
                serviceModel.setSupportAds((resultSet.getInt(DBStruct.SERVICE.SUPPORT_ADS)) == 1 ? true : false);
                serviceModel.setHasWhiteList((resultSet.getInt(DBStruct.SERVICE.HAS_WHITELIST)) == 1 ? true : false);
                serviceModel.setAutoCreatdFlag((resultSet.getInt(DBStruct.SERVICE.AUTO_CREATE_FLAG)) == 1 ? true : false);
                serviceModel.setAllowedSMS(resultSet.getInt(DBStruct.SERVICE.ALLOWED_SMS));
                serviceModel.setDailyQuota(resultSet.getInt(DBStruct.SERVICE.DAILY_QUOTA));
                serviceModel.setSmsProcedureQueueId(resultSet.getInt(DBStruct.SERVICE.SMS_PROCEDURE_QUEUE_ID));
                serviceModel.setSelectedApplicationQueueID(resultSet.getInt(DBStruct.SERVICE.APP_ID));

                //setting selected lookups
                selectedInterfaceTypeModel.setId(resultSet.getInt(DBStruct.SERVICE.INTERFACE_TYPE));
                selectedInterfaceTypeModel.setLable(resultSet.getString("INTERFACE_NAME"));
                selectedServiceTypeModel.setId(resultSet.getInt(DBStruct.SERVICE.SYSTEM_TYPE));
                selectedServiceTypeModel.setLable(resultSet.getString("TYPE_NAME"));
                selectedServiceCategoryModel.setId(resultSet.getInt(DBStruct.SERVICE.SYSTEM_CATEGORY));
                selectedServiceCategoryModel.setLable(resultSet.getString("CATEGORY_NAME"));
                //Handling Queue Model
//                selectedApplicationQueueModel.setAppId(resultSet.getInt(DBStruct.SERVICE.APP_ID));
//                selectedApplicationQueueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                selectedApplicationQueueModel.setVersionId(resultSet.getInt(DBStruct.APPQUEUE.VERSION_ID));
                selectedApplicationQueueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
                selectedApplicationQueueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
                selectedApplicationQueueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
                selectedApplicationQueueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
                selectedApplicationQueueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
                selectedApplicationQueueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
                selectedApplicationQueueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
                selectedApplicationQueueModel.setDatabaseUrl(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
                selectedApplicationQueueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
                selectedApplicationQueueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));
                selectedApplicationQueueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
                    selectedApplicationQueueModel.setTimeWindowFlag(true);
                } else {
                    selectedApplicationQueueModel.setTimeWindowFlag(false);
                }
                selectedApplicationQueueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
                selectedApplicationQueueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
                selectedApplicationQueueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
                selectedApplicationQueueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));

                serviceModel.setSelectedInterfaceTypeModel(selectedInterfaceTypeModel);
                serviceModel.setSelectedServiceCategoryModel(selectedServiceCategoryModel);
                serviceModel.setSelectedServiceTypeModel(selectedServiceTypeModel);
                serviceModel.setSelectedApplicationQueueModel(selectedApplicationQueueModel);
                serviceModel.setVersionId(resultSet.getInt(DBStruct.SERVICE.VERSION_ID));
                serviceModel.setStatus(resultSet.getInt(DBStruct.SERVICE.STATUS));
                serviceModel.setCreator(resultSet.getInt(DBStruct.SERVICE.CREATOR));
                serviceModel.setProcedureMaxBatchSize(resultSet.getInt(DBStruct.SERVICE.MAX_BATCH_SIZE));
                serviceModel.setServicePrivilege(resultSet.getInt(DBStruct.SERVICE.PRIVILEGE_LEVEL));
                serviceModel.setOriginatorType(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR_TYPE));
                serviceModel.setOriginatorValue(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR));
                serviceModel.setHashedPassword(resultSet.getBytes(DBStruct.SERVICE.PASSWORD));
//                //TODO: load whitelist
                sql = new StringBuilder();
                sql.append("SELECT * from ")
                        .append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(" where ")
                        .append(DBStruct.SERVICE_WHITELIST.SERVICE_ID).append(" =? ");
//                        .append("and ")
//                        .append(DBStruct.SERVICE_WHITELIST.ID).append(" =? ");

//                DataSourceManger.closeDBResources(null, statement); // eslam.ahmed | 5-5-2020
                statement = connection.prepareStatement(sql.toString());
                //statement.setLong(1, serviceModel.getServiceID());
                statement.setLong(1, serviceModel.getVersionId());
                resultSetWhiteList = statement.executeQuery();
                while (resultSetWhiteList.next()) {
                    serviceModel.getWhiteListIPs().add(resultSetWhiteList.getString(DBStruct.SERVICE_WHITELIST.IP_ADDRESS));
                }
                ////

                serviceModels.add(serviceModel);
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End getApprovedServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedServices Ended").build());

            return serviceModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                resultSet.close();
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    /**
     * Returns service name if the queue has a reference coming from a service
     *
     */
    public String checkQueueReference(Connection connection, Long queueId) throws CommonException {

//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting checkQueueReference...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkQueueReference Started").build());

        PreparedStatement statement = null;
        StringBuilder sql;
        ResultSet resultSet = null;

        try {
            sql = new StringBuilder();
            sql.append("SELECT  ").append(DBStruct.SERVICE.SERVICE_NAME)
                    .append(" FROM ").append(DBStruct.SERVICE.TABLE_NAME).append(" WHERE ")
                    .append(DBStruct.SERVICE.APP_ID).append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, queueId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
//                CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkQueueReference...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkQueueReference Ended").build());

                return resultSet.getString(DBStruct.SERVICE.SERVICE_NAME);
            } else {
//                CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End checkQueueReference...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkQueueReference Ended").build());

                return null;
            }
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkQueueReference()" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkQueueReference()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkQueueReference()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }
    }

    ///////////Fix Issue ////////
    public ArrayList<ServiceModel> getCampaignServices(Connection connection, CampaignModel campaignModel) throws CommonException {
        String methodName = "getCampaignServices";
//        CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "Starting getApprovedServices...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedServices Started").build());

        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        ResultSet resultSetWhiteList = null;

        try {

            sql = new StringBuilder();
            sql.append("SELECT s.* ")
                    .append("       from    " + DBStruct.VFE_CS_SERVICES.TABLE_NAME + " s ")
                    .append("       where s." + DBStruct.VFE_CS_SERVICES.VERSION_ID + " IN ( ")
                    .append("       SELECT ")
                    .append(DBStruct.CAMPAIGNS_SERVICES.SERVICE_ID)
                    .append(" from ")
                    .append(DBStruct.CAMPAIGNS_SERVICES.TABLE_NAME)
                    .append(" where ")
                    .append(DBStruct.CAMPAIGNS_SERVICES.CAMPAIGN_ID)
                    .append(" = ? )");

            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, campaignModel.getVersionId());
            resultSet = statement.executeQuery();

            ArrayList<ServiceModel> serviceModels = new ArrayList<>();
            while (resultSet.next()) {
                ServiceModel serviceModel = new ServiceModel();
                ServiceTypeModel selectedServiceTypeModel = new ServiceTypeModel();
                ServiceCategoryModel selectedServiceCategoryModel = new ServiceCategoryModel();
                InterfaceTypeModel selectedInterfaceTypeModel = new InterfaceTypeModel();
                QueueModel selectedApplicationQueueModel = new QueueModel();
                serviceModel.setServiceID(resultSet.getInt(DBStruct.SERVICE.SERVICE_ID));
                serviceModel.setServiceName(resultSet.getString(DBStruct.SERVICE.SERVICE_NAME));
                serviceModel.setSelectedInterfaceTypeID(resultSet.getInt(DBStruct.SERVICE.INTERFACE_TYPE));
                serviceModel.setSelectedServiceTypeID(resultSet.getInt(DBStruct.SERVICE.SYSTEM_TYPE));
                serviceModel.setSelectedServiceCategoryID(resultSet.getInt(DBStruct.SERVICE.SYSTEM_CATEGORY));
                serviceModel.setDeliveryReport((resultSet.getInt(DBStruct.SERVICE.DELIVERY_REPORT)) == 1 ? true : false);
                serviceModel.setConsultCounter((resultSet.getInt(DBStruct.SERVICE.CONSULT_COUNTER)) == 1 ? true : false);
                serviceModel.setAdsConsultCounter((resultSet.getInt(DBStruct.SERVICE.ADS_CONSULT_COUNTER)) == 1 ? true : false);
                serviceModel.setSupportAds((resultSet.getInt(DBStruct.SERVICE.SUPPORT_ADS)) == 1 ? true : false);
                serviceModel.setHasWhiteList((resultSet.getInt(DBStruct.SERVICE.HAS_WHITELIST)) == 1 ? true : false);
                serviceModel.setAutoCreatdFlag((resultSet.getInt(DBStruct.SERVICE.AUTO_CREATE_FLAG)) == 1 ? true : false);
                serviceModel.setAllowedSMS(resultSet.getInt(DBStruct.SERVICE.ALLOWED_SMS));
                serviceModel.setDailyQuota(resultSet.getInt(DBStruct.SERVICE.DAILY_QUOTA));
                serviceModel.setSmsProcedureQueueId(resultSet.getInt(DBStruct.SERVICE.SMS_PROCEDURE_QUEUE_ID));
                serviceModel.setProcedureMaxBatchSize(resultSet.getInt(DBStruct.SERVICE.MAX_BATCH_SIZE));
                serviceModel.setServicePrivilege(resultSet.getInt(DBStruct.SERVICE.PRIVILEGE_LEVEL));
                serviceModel.setOriginatorType(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR_TYPE));
                serviceModel.setOriginatorValue(resultSet.getInt(DBStruct.SERVICE.ORIGINATOR));
                serviceModel.setSelectedApplicationQueueID(resultSet.getInt(DBStruct.SERVICE.APP_ID));

//                //setting selected lookups
//                selectedInterfaceTypeModel.setId(resultSet.getInt(DBStruct.SERVICE.INTERFACE_TYPE));
//                selectedInterfaceTypeModel.setLable(resultSet.getString("INTERFACE_NAME"));
//                selectedServiceTypeModel.setId(resultSet.getInt(DBStruct.SERVICE.SYSTEM_TYPE));
//                selectedServiceTypeModel.setLable(resultSet.getString("TYPE_NAME"));
//                selectedServiceCategoryModel.setId(resultSet.getInt(DBStruct.SERVICE.SYSTEM_CATEGORY));
//                selectedServiceCategoryModel.setLable(resultSet.getString("CATEGORY_NAME"));
//                //Handling Queue Model
////                selectedApplicationQueueModel.setAppId(resultSet.getInt(DBStruct.SERVICE.APP_ID));
////                selectedApplicationQueueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
//                selectedApplicationQueueModel.setId(resultSet.getInt(DBStruct.APPQUEUE.ID));
//                selectedApplicationQueueModel.setAppId(resultSet.getInt(DBStruct.APPQUEUE.APP_ID));
//                selectedApplicationQueueModel.setAppName(resultSet.getString(DBStruct.APPQUEUE.APP_NAME));
//                selectedApplicationQueueModel.setQueueName(resultSet.getString(DBStruct.APPQUEUE.QUEUE_NAME));
//                selectedApplicationQueueModel.setDequeuePoolSize(resultSet.getInt(DBStruct.APPQUEUE.DEQUEUER_POOL_SIZE));
//                selectedApplicationQueueModel.setSenderPoolSize(resultSet.getInt(DBStruct.APPQUEUE.SENDER_POOL_SIZE));
//                selectedApplicationQueueModel.setSchemaName(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_NAME));
//                selectedApplicationQueueModel.setSchemaPassword(resultSet.getString(DBStruct.APPQUEUE.SCHEMA_PASSWORD));
//                selectedApplicationQueueModel.setDatabaseURL(resultSet.getString(DBStruct.APPQUEUE.DATABASE_URL));
//                selectedApplicationQueueModel.setCreator(resultSet.getInt(DBStruct.APPQUEUE.CREATOR));
//                selectedApplicationQueueModel.setStatus(resultSet.getInt(DBStruct.APPQUEUE.STATUS));
//                selectedApplicationQueueModel.setThreshold(resultSet.getInt(DBStruct.APPQUEUE.THRESHOLD));
//                if (resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FLAG) == GeneralConstants.TRUE) {
//                    selectedApplicationQueueModel.setTimeWindowFlag(true);
//                } else {
//                    selectedApplicationQueueModel.setTimeWindowFlag(false);
//                }
//                selectedApplicationQueueModel.setTimeWindowFromHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_HOUR));
//                selectedApplicationQueueModel.setTimeWindowFromMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_FROM_MIN));
//                selectedApplicationQueueModel.setTimeWindowToHour(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_HOUR));
//                selectedApplicationQueueModel.setTimeWindowToMin(resultSet.getInt(DBStruct.APPQUEUE.TIME_WINDOW_TO_MIN));
//
//                serviceModel.setSelectedInterfaceTypeModel(selectedInterfaceTypeModel);
//                serviceModel.setSelectedServiceCategoryModel(selectedServiceCategoryModel);
//                serviceModel.setSelectedServiceTypeModel(selectedServiceTypeModel);
//                serviceModel.setSelectedApplicationQueueModel(selectedApplicationQueueModel);
//                serviceModel.setId(resultSet.getInt(DBStruct.SERVICE.ID));
//                serviceModel.setStatus(resultSet.getInt(DBStruct.SERVICE.STATUS));
//                serviceModel.setCreator(resultSet.getInt(DBStruct.SERVICE.CREATOR));
////                //TODO: load whitelist
//                sql = new StringBuilder();
//                sql.append("SELECT * from ")
//                        .append(DBStruct.SERVICE_WHITELIST.TABLE_NAME).append(" where ")
//                        .append(DBStruct.SERVICE_WHITELIST.SERVICE_ID).append(" =? ")
//                        .append("and ")
//                        .append(DBStruct.SERVICE_WHITELIST.ID).append(" =? ");
//                statement = connection.prepareStatement(sql.toString());
//                statement.setLong(1, serviceModel.getServiceID());
//                statement.setLong(2, serviceModel.getId());
//                resultSetWhiteList = statement.executeQuery();
//                while (resultSetWhiteList.next()) {
//                    serviceModel.getWhiteListIPs().add(resultSetWhiteList.getString(DBStruct.SERVICE_WHITELIST.IP_ADDRESS));
//                }
//                ////
                serviceModels.add(serviceModel);
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentDAO.class.getName() + " || " + "End getCampaignServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedServices Ended").build());

            return serviceModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                resultSet.close();
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    /////////End Fix issue //////////
}
