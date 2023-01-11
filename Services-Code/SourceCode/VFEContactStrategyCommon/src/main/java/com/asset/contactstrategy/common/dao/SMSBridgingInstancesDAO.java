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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author aya.moawed
 */
public class SMSBridgingInstancesDAO {

    public String getInstance(Connection conn, int instanceID, String key) throws CommonException {
        String ret;
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        try {
            query.append("SELECT ").append(key);
            query.append(" FROM ").append(DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.TABLE_NAME);
            query.append(" WHERE ").append(DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.INSTANCE_ID).append(" =?");
//            CommonLogger.businessLogger.info("getInstance || attempting Query: " + query.toString());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getInstance Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            statement = conn.prepareStatement(query.toString());
            statement.setInt(1, instanceID);
            rs = statement.executeQuery();
            if (rs.next()) {
                ret = rs.getString(key);
            } else {
                ret = null;
            }
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [getInstance] Failed Update Instance Property  KEY => " + instanceID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [getInstance] Failed Update Instance Property  KEY => " + instanceID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, statement);
        }
        return ret;
    }

    public void updateInstance(Connection conn, int instanceID, String key, String value) throws CommonException {
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append("UPDATE ").append(DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.TABLE_NAME).append(" SET ").append(key);
            query.append(" =? WHERE ").append(DBStruct.VFE_CS_SMS_BRIDGING_INSTANCES.INSTANCE_ID).append(" =?");
//            CommonLogger.businessLogger.info("updateInstance || attempting Query: " + query.toString());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateInstance Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            statement = conn.prepareStatement(query.toString());
            statement.setString(1, value);
            statement.setInt(2, instanceID);
            statement.executeUpdate();
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateInstance] Failed Update Instance Property  KEY => " + instanceID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateInstance] Failed Update Instance Property  KEY => " + instanceID, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, statement);
        }
    }
}
