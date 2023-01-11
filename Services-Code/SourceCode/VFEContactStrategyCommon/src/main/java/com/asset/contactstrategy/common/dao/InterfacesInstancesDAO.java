/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author esmail.anbar
 */
public class InterfacesInstancesDAO 
{
    public int getInstaceReloadCounter(Connection conn, int instanceID) throws CommonException 
    {
        int itemValue;
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        
        try 
        {
            query.append("SELECT ")
                .append(DBStruct.VFE_CS_INTERFACES_INSTANCES.RELOAD_COUNTER)
                .append(" FROM ")
                .append(DBStruct.VFE_CS_INTERFACES_INSTANCES.TABLE_NAME)
                .append(" WHERE ")
                .append(DBStruct.VFE_CS_INTERFACES_INSTANCES.INSTANCE_ID)
                .append(" =?");
            
            //CommonLogger.businessLogger.info("getInstaceReloadCounter || Query: " + query.toString());
            statement = conn.prepareStatement(query.toString());
            statement.setInt(1, instanceID);
            rs = statement.executeQuery();
            if (rs.next()) {
                itemValue = rs.getInt(DBStruct.VFE_CS_INTERFACES_INSTANCES.RELOAD_COUNTER);
            } else {
                itemValue = -1;
            }
        } 
        catch (SQLException e) 
        {
            CommonLogger.errorLogger.error("Exception---->  for [getInstaceReloadCounter] Failed get  reload counter  instace => " + instanceID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } 
        catch (Exception e) 
        {
            CommonLogger.errorLogger.error("Exception---->  for [getInstaceReloadCounter] Failed get  reload counter  instace => " + instanceID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } 
        finally 
        {
            DataSourceManger.closeDBResources(rs, statement);
        }
        return itemValue;
    }

//    public HashMap<String, String> getInstancProperties(Connection conn, int instanceID) throws CommonException 
//    {
//        PreparedStatement preStmt = null;
//        ResultSet resultSet = null;
//        HashMap<String, String> properties = new HashMap<>();
//        try 
//        {
//            //CommonLogger.businessLogger.info("systemPropertiesDao.getSystemProperties() Invoked...");
//            String sql = "SELECT * FROM " + DBStruct.VFE_CS_INTERFACES_INSTANCES.TABLE_NAME + " WHERE "
//                    + DBStruct.VFE_CS_INTERFACES_INSTANCES.INSTANCE_ID + " =?";
//            CommonLogger.businessLogger.info("getInstancProperties || attempting Query: " + sql);
//            preStmt = conn.prepareStatement(sql);
//            preStmt.setInt(1, instanceID);
//            resultSet = preStmt.executeQuery();
//            
//            while (resultSet.next()) 
//            {
//                properties.put(DBStruct.VFE_CS_SENDING_SMS_INSTANCES.RELOAD_COUNTER, resultSet.getString(DBStruct.VFE_CS_SENDING_SMS_INSTANCES.RELOAD_COUNTER));
//            }
//        } 
//        catch (SQLException e) 
//        {
//            CommonLogger.errorLogger.error("Exception---->  for [getInstancProperties] Failed get  System Properties for  Group ID => " + instanceID, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//        } 
//        catch (Exception e) 
//        {
//            CommonLogger.errorLogger.error("Exception---->  for [getInstancProperties] Failed get  System Properties for Group ID => " + instanceID, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        }
//        finally 
//        {
//            DataSourceManger.closeDBResources(resultSet, preStmt);
//        }
//        // CommonLogger.businessLogger.info("systemPropertiesDao.getSystemPropertiesByGroupID() Ended...");
//        return properties;
//    }
//
//    public void updateInstanceShutdownFlag(Connection conn, int instanceID, String flag) throws CommonException {
//        PreparedStatement statement = null;
//        StringBuilder query = new StringBuilder();
//        try {
//            query.append("UPDATE " + DBStruct.VFE_CS_SENDING_SMS_INSTANCES.TABLE_NAME + " SET " + DBStruct.VFE_CS_SENDING_SMS_INSTANCES.SENDING_SMS_SHUTDOWN_FLAG);
//            query.append(" =? WHERE " + DBStruct.VFE_CS_SENDING_SMS_INSTANCES.INSTANCE_ID + " =?");
//            CommonLogger.businessLogger.info("updateInstanceShutdownFlag || attempting Query: " + query.toString());
//            statement = conn.prepareStatement(query.toString());
//            statement.setString(1, flag);
//            statement.setInt(2, instanceID);
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateInstanceShutdownFlag] Failed Update Instance Property  KEY => " + instanceID, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Exception---->  for [updateInstanceShutdownFlag] Failed Update Instance Property  KEY => " + instanceID, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            DataSourceManger.closeDBResources(null, statement);
//        }
//    }
    
    public void updateInstanceReloadCounter(Connection conn, int instanceID, int reloadCounter) throws CommonException 
    {
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        
        try 
        {
            query.append("UPDATE ")
                .append(DBStruct.VFE_CS_INTERFACES_INSTANCES.TABLE_NAME)
                .append(" SET ")
                .append(DBStruct.VFE_CS_INTERFACES_INSTANCES.RELOAD_COUNTER)
                .append(" =? WHERE ")
                .append(DBStruct.VFE_CS_INTERFACES_INSTANCES.INSTANCE_ID)
                .append(" =?");
            
            //CommonLogger.businessLogger.info("updateInstanceReloadCounter || attempting Query: " + query.toString());
            
            statement = conn.prepareStatement(query.toString());
            statement.setInt(1, reloadCounter);
            statement.setInt(2, instanceID);
            statement.executeUpdate();
        } 
        catch (SQLException e) 
        {
            CommonLogger.errorLogger.error("Exception---->  for [updateInstanceReloadCounter] Failed Update Instance Property  KEY => " + instanceID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } 
        catch (Exception e) 
        {
            CommonLogger.errorLogger.error("Exception---->  for [updateInstanceReloadCounter] Failed Update Instance Property  KEY => " + instanceID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } 
        finally 
        {
            DataSourceManger.closeDBResources(null, statement);
        }
    }
    
//    public String getInstanceShutdownFlag(Connection conn, int instanceID) throws CommonException {
//        String ret;
//        PreparedStatement statement = null;
//        StringBuilder query = new StringBuilder();
//        ResultSet rs = null;
//        try {
//            query.append("SELECT " + DBStruct.VFE_CS_SENDING_SMS_INSTANCES.SENDING_SMS_SHUTDOWN_FLAG);
//            query.append(" FROM " + DBStruct.VFE_CS_SENDING_SMS_INSTANCES.TABLE_NAME);
//            query.append(" WHERE " + DBStruct.VFE_CS_SENDING_SMS_INSTANCES.INSTANCE_ID + " =?");
//            CommonLogger.businessLogger.info("getInstanceShutdownFlag || attempting Query: " + query.toString());
//            statement = conn.prepareStatement(query.toString());
//            statement.setInt(1, instanceID);
//            rs = statement.executeQuery();
//            if (rs.next()) {
//                ret = rs.getString(DBStruct.VFE_CS_SENDING_SMS_INSTANCES.SENDING_SMS_SHUTDOWN_FLAG);
//            } else {
//                ret = null;
//            }
//        } catch (SQLException e) {
//            CommonLogger.errorLogger.error("Exception---->  for [getInstanceShutdownFlag] Failed Update Instance Property  KEY => " + instanceID, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Exception---->  for [getInstanceShutdownFlag] Failed Update Instance Property  KEY => " + instanceID, e);
//            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        } finally {
//            DataSourceManger.closeDBResources(null, statement);
//        }
//        return ret;
//    }
}
