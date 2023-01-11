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
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author esmail.anbar
 */
public class SystemPropertiesDao {
    
    private static final String CLASS_NAME = "com.asset.contactstrategy.common.dao.SystemPropertiesDao";
    
    public void incrementFinishedInstances(Connection conn, SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        long startime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        try {
            //CommonLogger.businessLogger.info(SystemPropertiesDao.class.getName() + " || " + "Starting incrementFinishedInstances");
            query.append("UPDATE " + DBStruct.SYSTEM_PROPERITES.TABLE_NAME + " SET " + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE);
            query.append(" = ").append(DBStruct.SYSTEM_PROPERITES.ITEM_VALUE).append("+1");
            query.append(" WHERE UPPER(" + DBStruct.SYSTEM_PROPERITES.ITEM_KEY).append(")");
            query.append(" LIKE UPPER(?)");
            query.append(" AND " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + " =?");
            statement = conn.prepareStatement(query.toString());
            statement.setString(1, SystemPropertiesModel.getItemKey());
            statement.setInt(2, SystemPropertiesModel.getGroupId());
            statement.executeUpdate();
            // CommonLogger.businessLogger.info(SystemPropertiesDao.class.getName() + " || " + "Ended incrementFinishedInstances in [" + (System.currentTimeMillis() - startime) + "] msec");
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateSystemProperty] Failed increment Finished Instances  KEY => " + SystemPropertiesModel.getItemKey() + " Group ID => " + SystemPropertiesModel.getGroupId(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateSystemProperty] Failed increment Finished Instances  KEY => " + SystemPropertiesModel.getItemKey() + " Group ID => " + SystemPropertiesModel.getGroupId(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, statement);
        }
    }
    
    public void updateTimeSystemProperty(Connection conn, SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        long startime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        try {
            // CommonLogger.businessLogger.info(SystemPropertiesDao.class.getName() + " || " + "Starting updateSystemProperty");
            query.append("UPDATE " + DBStruct.SYSTEM_PROPERITES.TABLE_NAME + " SET " + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE);
            query.append(" =  to_char(systimestamp , '").append(GeneralConstants.SYSTEM_PROPERTY_DATE_FORMAT);
            query.append(" ') WHERE UPPER(" + DBStruct.SYSTEM_PROPERITES.ITEM_KEY);
            query.append(")");
            query.append(" LIKE UPPER(?)");
            query.append(" AND " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + " =?");
            statement = conn.prepareStatement(query.toString());
            statement.setString(1, SystemPropertiesModel.getItemKey());
            statement.setInt(2, SystemPropertiesModel.getGroupId());
            statement.executeUpdate();
//           CommonLogger.businessLogger.info(SystemPropertiesDao.class.getName() + " || " + "Ended updateSystemProperty in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSystemProperty Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
            
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateTimeSystemProperty] Failed updateTimeSystemProperty  KEY => " + SystemPropertiesModel.getItemKey() + " Group ID => " + SystemPropertiesModel.getGroupId(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateTimeSystemProperty] Failed updateTimeSystemProperty  KEY => " + SystemPropertiesModel.getItemKey() + " Group ID => " + SystemPropertiesModel.getGroupId(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, statement);
        }
    }
    
    public void updateSystemProperty(Connection conn, SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        long startime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        try {
            // CommonLogger.businessLogger.info(SystemPropertiesDao.class.getName() + " || " + "Starting updateSystemProperty");
            query.append("UPDATE " + DBStruct.SYSTEM_PROPERITES.TABLE_NAME + " SET " + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE);
            query.append(" = ? WHERE UPPER(" + DBStruct.SYSTEM_PROPERITES.ITEM_KEY).append(")");
            query.append(" LIKE UPPER(?)");
            query.append(" AND " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + " =?");
            statement = conn.prepareStatement(query.toString());
            statement.setString(1, SystemPropertiesModel.getItemValue());
            statement.setString(2, SystemPropertiesModel.getItemKey());
            statement.setInt(3, SystemPropertiesModel.getGroupId());
            statement.executeUpdate();
            //  CommonLogger.businessLogger.info(SystemPropertiesDao.class.getName() + " || " + "Ended updateSystemProperty in [" + (System.currentTimeMillis() - startime) + "] msec");
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateSystemProperty] Failed Update System Properties  KEY => " + SystemPropertiesModel.getItemKey() + " Group ID => " + SystemPropertiesModel.getGroupId(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateSystemProperty] Failed Update System Properties  KEY => " + SystemPropertiesModel.getItemKey() + " Group ID => " + SystemPropertiesModel.getGroupId(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, statement);
        }
    }
    
    public String getSystemPropertyByKey(Connection conn, String key, int groupID) throws CommonException {
        // CommonLogger.businessLogger.info(SystemPropertiesDao.class.getName() + " || " + "Starting getSystemPropertyByKey");
        String itemValue = "";
        long startime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        try {
            query.append("SELECT " + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE);
            query.append(" FROM " + DBStruct.SYSTEM_PROPERITES.TABLE_NAME);
            query.append(" WHERE UPPER(" + DBStruct.SYSTEM_PROPERITES.ITEM_KEY).append(")");
            query.append(" LIKE UPPER(?)");
            query.append(" AND  " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + " =?");
            statement = conn.prepareStatement(query.toString());
            statement.setString(1, key);
            statement.setInt(2, groupID);
            rs = statement.executeQuery();
            if (rs.next()) {
                itemValue = rs.getString(DBStruct.SYSTEM_PROPERITES.ITEM_VALUE);
            } else {
                itemValue = null;
            }
//            CommonLogger.businessLogger.info("Ended getSystemPropertyByKey Key=["+key+"] SrcID=["+groupID+"] in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSystemPropertyByKey Ended")
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupID)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [getSystemPropertyByKey] Failed get  System Property  KEY => " + key + " Group ID => " + groupID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [getSystemPropertyByKey] Failed get  System Property  KEY => " + key + " Group ID => " + groupID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, statement);
        }
        return itemValue;
        
    }

    // loads system properties by group id and global settings.
    public HashMap<String, String> getSystemPropertiesByGroupID(Connection conn, int groupID) throws CommonException {
        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        HashMap<String, String> properties = new HashMap<>();
        try {
            // CommonLogger.businessLogger.info("systemPropertiesDao.getSystemProperties() Invoked...");
            String SQLquery = "Select " + DBStruct.SYSTEM_PROPERITES.ITEM_KEY + ", " + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE
                    + ", " + DBStruct.SYSTEM_PROPERITES.GROUP_NAME
                    + " from " + DBStruct.SYSTEM_PROPERITES.TABLE_NAME
                    + " Where " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + "=? OR " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + "=?";
            //  CommonLogger.businessLogger.info("Attempting Query: " + SQLquery);
            preStmt = conn.prepareStatement(SQLquery);
            
            preStmt.setInt(1, groupID);
            preStmt.setInt(2, GeneralConstants.SRC_ID_GLOABL_SETTINGS);
            
            resultSet = preStmt.executeQuery();
            //Esmail.Anbar | 13/7/2017 | Adding Properties Logging
//            CommonLogger.businessLogger.info("======================================== Started Reading Properties from Database by Group IDs: 1 and " + groupID);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reading Properties from Database Started")
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, "1 and " + groupID).build());
            while (resultSet.next()) {
                properties.put(resultSet.getString(DBStruct.SYSTEM_PROPERITES.ITEM_KEY), resultSet.getString(DBStruct.SYSTEM_PROPERITES.ITEM_VALUE));
                CommonLogger.businessLogger.info("Key: " + resultSet.getString(DBStruct.SYSTEM_PROPERITES.ITEM_KEY)
                        + " | Value: " + resultSet.getString(DBStruct.SYSTEM_PROPERITES.ITEM_VALUE)
                        + " | Group: " + resultSet.getString(DBStruct.SYSTEM_PROPERITES.GROUP_NAME));
            }
//            CommonLogger.businessLogger.info("======================================== Ended Reading Properties from Database by Group ID: 1 and " + groupID);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reading Properties from Database Ended")
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, "1 and " + groupID).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [getSystemPropertiesByGroupID] Failed get  System Properties for  Group ID => " + groupID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [getSystemPropertiesByGroupID] Failed get  System Properties for Group ID => " + groupID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(resultSet, preStmt);
        }
        // CommonLogger.businessLogger.info("systemPropertiesDao.getSystemPropertiesByGroupID() Ended...");
        return properties;
    }
    
    public HashMap<String, String> getAllSystemProperties(Connection conn) throws CommonException {
        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        HashMap<String, String> properties = new HashMap<>();
        try {
            CommonLogger.businessLogger.info("systemPropertiesDao.getSystemProperties() Invoked...");
            String SQLquery = "Select " + DBStruct.SYSTEM_PROPERITES.ITEM_KEY + ", " + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE
                    + " from " + DBStruct.SYSTEM_PROPERITES.TABLE_NAME;
//            CommonLogger.businessLogger.info("Attempting Query: " + SQLquery);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAllSystemQueries Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, SQLquery).build());
            preStmt = conn.prepareStatement(SQLquery);
            resultSet = preStmt.executeQuery();
            //Esmail.Anbar | 13/7/2017 | Adding Properties Logging
//            CommonLogger.businessLogger.info("======================================== Started Reading Properties from Database");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reading Properties from Database Started").build());
            while (resultSet.next()) {
                properties.put(resultSet.getString("ITEM_KEY"), resultSet.getString("ITEM_VALUE"));
//                CommonLogger.businessLogger.info("Key: " + resultSet.getString(DBStruct.SYSTEM_PROPERITES.ITEM_KEY)
//                        + " | Value: " + resultSet.getString(DBStruct.SYSTEM_PROPERITES.ITEM_VALUE));
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DB Info")
                        .put(GeneralConstants.StructuredLogKeys.KEY, resultSet.getString(DBStruct.SYSTEM_PROPERITES.ITEM_KEY))
                        .put(GeneralConstants.StructuredLogKeys.VALUE, resultSet.getString(DBStruct.SYSTEM_PROPERITES.ITEM_VALUE)).build());
            }
//            CommonLogger.businessLogger.info("======================================== Ended Reading Properties from Database");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reading Properties from Database Ended").build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [getAllSystemProperties] Failed get  System Properties ", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [getAllSystemProperties] Failed get  System Properties ", e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(resultSet, preStmt);
        }
        CommonLogger.businessLogger.info("systemPropertiesDao.getAllSystemProperties() Ended...");
        return properties;
    }
}
