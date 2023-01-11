/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import java.sql.Connection;
import java.util.List;

import com.asset.contactstrategy.common.dao.LogDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CsSmscInterfaceHistoryModel;
import com.asset.contactstrategy.common.models.LogModel;

/**
 *
 * @author mohamed.osman
 */
public class LogService {

    public void insertLog(Connection connection, List<LogModel> logModels) throws CommonException {
        try {
//			CommonLogger.businessLogger.debug(LogService.class.getName() + " || Starting  [insertLog]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertLog Started").build());
            LogDAO logDAO = new LogDAO();
            logDAO.insertLog(connection, logModels);
//            CommonLogger.businessLogger.debug(LogService.class.getName() + " || End [insertLog]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertLog End").build());
        } catch (CommonException e) {
            throw e;
        }
    }

    public void insertCsSmscInterfaceHistoryModel(Connection connection, List<CsSmscInterfaceHistoryModel> logModels)
            throws CommonException {
        try {
//            CommonLogger.businessLogger
//                    .debug(LogService.class.getName() + " || Starting  [insertCsSmscInterfaceHistoryModel]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertCsSmscInterfaceHistoryModel Started").build());
            LogDAO logDAO = new LogDAO();
            logDAO.insertCsSmscInterfaceHistoryModel(connection, logModels);
//            CommonLogger.businessLogger
//                    .debug(LogService.class.getName() + " ||  End [insertCsSmscInterfaceHistoryModel]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertCsSmscInterfaceHistoryModel End").build());
        } catch (CommonException e) {
            throw e;
        }
    }

}
