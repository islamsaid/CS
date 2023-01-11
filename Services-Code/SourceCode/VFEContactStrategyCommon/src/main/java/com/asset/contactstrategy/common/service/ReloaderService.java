/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.ReloaderDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import java.sql.Connection;

/**
 *
 * @author Mostafa Fouda
 */
public class ReloaderService {

    public void changeReloadCounterSendingSMS(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ReloaderService.class.getName() + " || " + "Starting  [changeReloadCounterSendingSMS]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendingSMS Started").build());
            ReloaderDAO reloaderDAO = new ReloaderDAO();
            reloaderDAO.changeReloadCounterSendingSMS(connection);
//            CommonLogger.businessLogger.debug(ReloaderService.class.getName() + " || " + "End [changeReloadCounterSendingSMS]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendingSMS Ended").build());
        } catch (CommonException e) {
            throw e;
        }
    }

    public void changeReloadCounterSendinginterfaces(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ReloaderService.class.getName() + " || " + "Starting  [changeReloadCounterSendinginterfaces]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendinginterfaces Started").build());
            ReloaderDAO reloaderDAO = new ReloaderDAO();
            reloaderDAO.changeReloadCounterSendinginterfaces(connection);
//            CommonLogger.businessLogger.debug(ReloaderService.class.getName() + " || " + "End [changeReloadCounterSendinginterfaces]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeReloadCounterSendinginterfaces Ended").build());
        } catch (CommonException e) {
            throw e;
        }
    }
}
