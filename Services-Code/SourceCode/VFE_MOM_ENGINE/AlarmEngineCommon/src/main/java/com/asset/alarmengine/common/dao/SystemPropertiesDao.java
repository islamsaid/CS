/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.dao;

import com.asset.alarmengine.common.controller.DBpoolManager;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 *
 * @author Mostafa Kashif
 */
public class SystemPropertiesDao {

    public static String getSystemProperty(String systemPropertiesTblName, String outputValueColumnName, String propKeyIDColumnName, String propKeyID, Connection con) throws EngineException {
        EngineLogger.debugLogger.debug("Starting getting system property for property_id:" + propKeyID);
        long time = System.currentTimeMillis();
        Statement statement = null;
        ResultSet rs = null;
        String sql = "";
        String propValue = "";
        try {
            //Esmail.Anbar || Fixing System Property Reading "ITEM_KEY" instead of "ITEM_ID" | 3/10/2017
            sql = "Select * from " + systemPropertiesTblName
                    + " where " + propKeyIDColumnName + " = '" + propKeyID + "'";
            statement = con.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                propValue = rs.getString(outputValueColumnName);
            }
            //Esmail.Anbar || Fixing System Property Reading "ITEM_KEY" instead of "ITEM_ID" | 3/10/2017
            if (propValue == null)
                EngineLogger.debugLogger.error("PropertyKey: " + propKeyID + " was found null...");
            else if (propValue.isEmpty())
                EngineLogger.debugLogger.error("PropertyKey: " + propKeyID + " was not found...");
            return propValue;
        } catch (SQLException e) {
            EngineLogger.debugLogger.error("SQLException while getting System Property---->" + e);
            EngineLogger.errorLogger.error("SQLException while getting System Property---->" + e);
            throw new EngineException(e, EngineErrorCodes.SQL_ERROR_IN_DAO);
        } catch (Exception e) {
            EngineLogger.debugLogger.error("Exception while getting System Property---->" + e);
            EngineLogger.errorLogger.error("Exception while getting System Property---->" + e);
            throw new EngineException(e, EngineErrorCodes.GENERAL_ERROR_IN_DAO);
        } finally {
            DBpoolManager.close(rs);
            DBpoolManager.close(statement);
            EngineLogger.debugLogger.debug("Function Executed in Time: " + (System.currentTimeMillis() - time) + " msecs");
        }
    }

    public static void updateSystemProperty(String systemPropertiesTblName, String ValueColumnName, String propKeyIDColumnName, String value, String propKeyID, Connection con) throws EngineException {
        EngineLogger.debugLogger.debug("Starting updating propID:" + propKeyID + " with value:" + value);
        long time = System.currentTimeMillis();
        Statement statement = null;
        String sql = "";
        try {
            sql = "Update " + systemPropertiesTblName
                    + " set " + ValueColumnName + " = '" + value
                    + "' where " + propKeyIDColumnName + " = '" + propKeyID + "'";

            statement = con.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            EngineLogger.debugLogger.error("SQLException while updating System Property---->" + e);
            EngineLogger.errorLogger.error("SQLException while updating System Property---->" + e);

            throw new EngineException(e, EngineErrorCodes.SQL_ERROR_IN_DAO);
        } catch (Exception e) {
            EngineLogger.debugLogger.error("Exception while updating System Property---->" + e);
            EngineLogger.errorLogger.error("Exception while updating System Property---->" + e);

            throw new EngineException(e, EngineErrorCodes.GENERAL_ERROR_IN_DAO);
        } finally {
            DBpoolManager.close(statement);
            EngineLogger.debugLogger.debug("Function Executed in Time: " + (System.currentTimeMillis() - time) + " msecs");

        }
    }

    public static HashMap<String, String> loadAllProperties(String systemPropertiesTblName, String ValueColumnName, String propKeyIDColumnName,String groupIdColumnName,String groupId, Connection con) throws EngineException {
        EngineLogger.debugLogger.debug("Starting loading configurations...");
        HashMap<String, String> properties = new HashMap<String, String>();
        long executionTime = System.currentTimeMillis();
        String methodName = "loadEngineConfig";
        EngineLogger.debugLogger.debug("Method started...");
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        ResultSet resultSet = null;

        try {
            // create Statement for querying database
            query.append("SELECT *");
            query.append(" FROM " ).append(systemPropertiesTblName);
            if(groupIdColumnName!=null &&groupId!=null){
                 query.append(" WHERE ").append(groupIdColumnName).append(" = ").append(groupId);
            }

            statement = con.prepareStatement(query.toString());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                properties.put(resultSet.getString(propKeyIDColumnName), resultSet.getString(ValueColumnName));
            }
        } catch (SQLException e) {
            EngineLogger.debugLogger.error("SQLException while loading all System Property---->" + e);
            EngineLogger.errorLogger.error("SQLException while loading all System Property---->" + e);

            throw new EngineException(e, EngineErrorCodes.SQL_ERROR_IN_DAO);
        } catch (Exception e) {
            EngineLogger.debugLogger.error("Exception while loading all System Property---->" + e);
            EngineLogger.errorLogger.error("Exception while loading all System Property---->" + e);

            throw new EngineException(e, EngineErrorCodes.GENERAL_ERROR_IN_DAO);
        } finally {
            DBpoolManager.close(resultSet);
            DBpoolManager.close(statement);
            EngineLogger.debugLogger.debug("Function Executed in Time: " + (System.currentTimeMillis() - executionTime) + " msecs");

        }
        return properties;
    }
}
