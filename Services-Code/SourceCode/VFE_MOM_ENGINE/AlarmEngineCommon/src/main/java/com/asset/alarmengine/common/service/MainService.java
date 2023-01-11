/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.service;

import com.asset.alarmengine.common.controller.DBpoolManager;
import com.asset.alarmengine.common.defines.EngineDBStructs;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.alarmengine.common.models.HServiceErrorModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Mostafa Kashif
 */
public class MainService {

    public String getSystemProperty(String systemPropertiesTblName, String outputValueColumnName, String propKeyIDColumnName, String propKeyID) throws EngineException {
        Connection con = null;
        SystemPropertiesService configService = new SystemPropertiesService();
        try {
            DBpoolManager dbManager = DBpoolManager.getInstance();
            con = dbManager.getConnection();
            return configService.getSystemProperty(systemPropertiesTblName, outputValueColumnName, propKeyIDColumnName, propKeyID, con);

        } catch (Exception ex) {
            EngineLogger.debugLogger.error("Exception in getSystemProperty---->" + ex);
            EngineLogger.errorLogger.error("Exception in getSystemProperty---->" + ex);
            throw new EngineException(ex, EngineErrorCodes.GENERAL_ERROR, "ERROR getSystemProperty");
        } finally {
            try {
                con.commit();
            } catch (SQLException e) {
                EngineLogger.debugLogger.error("Exception in commiting---->" + e);
                EngineLogger.errorLogger.error("Exception in commiting---->" + e);
            }
            DBpoolManager.closeConnection(con);
        }
    }

    public void updateSystemProperty(String systemPropertiesTblName, String valueColumnName, String propKeyIDColumnName, String value, String propKeyID) throws EngineException {
        Connection con = null;
        SystemPropertiesService configService = new SystemPropertiesService();
        try {
            DBpoolManager dbManager = DBpoolManager.getInstance();
            con = dbManager.getConnection();
            configService.updateSystemProperty(systemPropertiesTblName, valueColumnName, propKeyIDColumnName, value, propKeyID, con);

        } catch (Exception ex) {
            EngineLogger.debugLogger.error("Exception in updateSystemProperty---->" + ex);
            EngineLogger.errorLogger.error("Exception in updateSystemProperty---->" + ex);
            throw new EngineException(ex, EngineErrorCodes.GENERAL_ERROR, "ERROR updateSystemProperty");
        } finally {
            try {
                con.commit();
            } catch (SQLException e) {
                EngineLogger.debugLogger.error("Exception in commiting---->" + e);
                EngineLogger.errorLogger.error("Exception in commiting---->" + e);
            }
            DBpoolManager.closeConnection(con);
        }
    }

    public HashMap<String, String> loadAllProperties(String systemPropertiesTblName, String ValueColumnName, String propKeyIDColumnName,String groupIdColumnName,String groupId) throws EngineException {
        Connection con = null;
        SystemPropertiesService configService = new SystemPropertiesService();
        try {
            DBpoolManager dbManager = DBpoolManager.getInstance();
            con = dbManager.getConnection();
            return configService.loadAllProperties(systemPropertiesTblName, ValueColumnName, propKeyIDColumnName,groupIdColumnName,groupId, con);

        } catch (Exception ex) {
            EngineLogger.debugLogger.error("Exception in updateSystemProperty---->" + ex);
            EngineLogger.errorLogger.error("Exception in updateSystemProperty---->" + ex);
            throw new EngineException(ex, EngineErrorCodes.GENERAL_ERROR, "ERROR updateSystemProperty");
        } finally {
            try {
                con.commit();
            } catch (SQLException e) {
                EngineLogger.debugLogger.error("Exception in commiting---->" + e);
                EngineLogger.errorLogger.error("Exception in commiting---->" + e);
            }
            DBpoolManager.closeConnection(con);
        }
    }
    
    public void handleServiceError(HServiceErrorModel errorModel, EngineException engineException, String serviceErrorTblName, String idColumnName, String errorTypeColumnName, String errorSeverityColumnName,
             String errorParamsColumnName, String errorMsgColumnName,  String insertionTimeColumnName, String idSeqName) throws EngineException {
        Connection con = null;
        ServiceErrorsService errorsService = new ServiceErrorsService();
        try {
            DBpoolManager dbManager = DBpoolManager.getInstance();
            con = dbManager.getConnection();
            errorsService.handleServiceError(errorModel, engineException, serviceErrorTblName, idColumnName, errorTypeColumnName, errorSeverityColumnName, errorParamsColumnName, errorMsgColumnName, insertionTimeColumnName, idSeqName, con);

        } catch (Exception ex) {
            EngineLogger.debugLogger.error("Exception in updateSystemProperty---->" + ex);
            EngineLogger.errorLogger.error("Exception in updateSystemProperty---->" + ex);
            throw new EngineException(ex, EngineErrorCodes.GENERAL_ERROR, "ERROR updateSystemProperty");
        } finally {
            try {
                con.commit();
            } catch (SQLException e) {
                EngineLogger.debugLogger.error("Exception in commiting---->" + e);
                EngineLogger.errorLogger.error("Exception in commiting---->" + e);
                    throw new EngineException(e, EngineErrorCodes.GENERAL_ERROR, "ERROR updateSystemProperty");
        
            }
            DBpoolManager.closeConnection(con);
        }
    }

    public static Vector<HServiceErrorModel> getServiceErrorsByDate(String date, String severitiesCommaSeparated, String dateFormat, String serviceErrorTblName, String idColumnName, String errorTypeColumnName, String errorSeverityColumnName,
             String errorParamsColumnName, String errorMsgColumnName, String insertionTimeColumnName, String idSeqName,
            String lookupErrorTblName, String lookupErrorLabelColumnName, String lookupErrorIdColumnName) throws EngineException {
        Connection con = null;
        ServiceErrorsService errorsService = new ServiceErrorsService();
        try {
            DBpoolManager dbManager = DBpoolManager.getInstance();
            con = dbManager.getConnection();
            return errorsService.getServiceErrorsByDate(con, date, severitiesCommaSeparated, dateFormat, serviceErrorTblName, idColumnName, errorTypeColumnName, errorSeverityColumnName,  errorParamsColumnName, errorMsgColumnName, insertionTimeColumnName, idSeqName, lookupErrorTblName, lookupErrorLabelColumnName, lookupErrorIdColumnName);

        } catch (Exception ex) {
            EngineLogger.debugLogger.error("Exception in updateSystemProperty---->" + ex);
            EngineLogger.errorLogger.error("Exception in updateSystemProperty---->" + ex);
            throw new EngineException(ex, EngineErrorCodes.GENERAL_ERROR, "ERROR updateSystemProperty");
        } finally {
            try {
                con.commit();
            } catch (SQLException e) {
                EngineLogger.debugLogger.error("Exception in commiting---->" + e);
                EngineLogger.errorLogger.error("Exception in commiting---->" + e);
            }
            DBpoolManager.closeConnection(con);
        }

    }
}
