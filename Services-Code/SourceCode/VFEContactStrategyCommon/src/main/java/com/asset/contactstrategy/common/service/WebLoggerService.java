/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.DWHElementDAO;
import com.asset.contactstrategy.common.dao.WebLoggerDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;

import com.asset.contactstrategy.common.models.WebLogModel;
import java.sql.Connection;

/**
 *
 * @author Zain Al-Abedin
 */
public class WebLoggerService {

    public void insertWebLog(Connection connection, WebLogModel logModel) {
//        CommonLogger.businessLogger.debug(DWHElementService.class.getName() + " || " + "Starting insertWebLog");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertWebLog Started").build());

        WebLoggerDAO dao = new WebLoggerDAO();
        dao.insertLog(connection, logModel);
//        CommonLogger.businessLogger.debug(DWHElementService.class.getName() + " || " + "End insertWebLog");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertWebLog Ended").build());

    }

}
