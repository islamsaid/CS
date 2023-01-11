/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.DWHElementDAO;
import com.asset.contactstrategy.common.dao.SystemPropertiesDao;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author esmail.anbar
 */
public class SystemPropertiesService {

    public HashMap<String, String> getSystemPropertiesByGroupID(Connection conn, int groupID) throws CommonException {
        // CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "Starting getSystemPropertiesByGroupID");
        SystemPropertiesDao systemPropertiesDao = new SystemPropertiesDao();
        //  CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "End getSystemPropertiesByGroupID");
        return systemPropertiesDao.getSystemPropertiesByGroupID(conn, groupID);
    }

    public HashMap<String, String> getAllSystemProperties(Connection conn) throws CommonException {
        // CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "Starting getAllSystemProperties");
        SystemPropertiesDao systemPropertiesDao = new SystemPropertiesDao();
        //  CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "End getAllSystemProperties");
        return systemPropertiesDao.getAllSystemProperties(conn);
    }

    public void incrementFinishedInstances(Connection connection, SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        try {
            //  CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "Starting incrementFinishedInstances");
//            CommonLogger.businessLogger.debug("Update System Property => " + SystemPropertiesModel.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update System")
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_PROPERTIES, SystemPropertiesModel).build());

            SystemPropertiesDao systemPropertiesDao = new SystemPropertiesDao();
            systemPropertiesDao.incrementFinishedInstances(connection, SystemPropertiesModel);
            //   CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "End incrementFinishedInstances");
        } catch (CommonException e) {
            throw e;
        }
    }

    public void updateTimeSystemProperty(Connection connection, SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        try {
            // CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "Starting updateSystemProperty");
//            CommonLogger.businessLogger.debug("Update Time System Property => " + SystemPropertiesModel.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Time")
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_PROPERTIES, SystemPropertiesModel).build());

            SystemPropertiesDao systemPropertiesDao = new SystemPropertiesDao();
            systemPropertiesDao.updateTimeSystemProperty(connection, SystemPropertiesModel);
            // CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "End updateSystemProperty");
        } catch (CommonException e) {
            throw e;
        }
    }

    public void updateSystemProperty(Connection connection, SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        try {
            // CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "Starting updateSystemProperty");
//            CommonLogger.businessLogger.debug("Update System Property => " + SystemPropertiesModel.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update System Property")
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_PROPERTIES, SystemPropertiesModel).build());

            SystemPropertiesDao systemPropertiesDao = new SystemPropertiesDao();
            systemPropertiesDao.updateSystemProperty(connection, SystemPropertiesModel);
            // CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "End updateSystemProperty");
        } catch (CommonException e) {
            throw e;
        }
    }

    public String getSystemPropertyByKey(Connection conn, String key, int groupID) throws CommonException {
        String ItemValue = null;
        try {
            // CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "Starting updateSystemProperty");
            SystemPropertiesDao systemPropertiesDao = new SystemPropertiesDao();
            ItemValue = systemPropertiesDao.getSystemPropertyByKey(conn, key, groupID);
            //  CommonLogger.businessLogger.info(SystemPropertiesService.class.getName() + " || " + "End updateSystemProperty");

        } catch (CommonException e) {
            throw e;
        }
        return ItemValue;
    }

}
