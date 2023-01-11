/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.UtilityDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import java.sql.Connection;

/**
 *
 * @author Esmail.Anbar
 */
public class UtilityService {

    public boolean checkDatasource(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(UtilityService.class.getName() + " || " + "Starting Getting [checkDatasource]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkDatasource Started").build());

            return new UtilityDAO().checkDatasource(connection);
        } catch (CommonException e) {
            throw e;
        } finally {
//            CommonLogger.businessLogger.debug(UtilityService.class.getName() + " || " + "End Getting [checkDatasource]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkDatasource Ended").build());

        }
    }

}
