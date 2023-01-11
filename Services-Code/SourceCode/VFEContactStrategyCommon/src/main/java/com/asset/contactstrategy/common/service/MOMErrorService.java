/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.MOMErrorsDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import java.sql.Connection;

/**
 *
 * @author Mostafa Fouda
 */
public class MOMErrorService {

    public MOMErrorService() {
    }

    public void insertMOMError(Connection connection, MOMErrorsModel mOMErrorsModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [insertMOMError]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertMOMError Started").build());
            MOMErrorsDAO mOMErrorsDAO = new MOMErrorsDAO();
//            if (mOMErrorsModel.getErrorMessage() == null || mOMErrorsModel.getErrorMessage().trim().length() == 0) {
//                mOMErrorsModel.setErrorMessage("N/A");
//            }
            mOMErrorsDAO.insertMOMError(connection, mOMErrorsModel);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End [insertMOMError]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertMOMError Ended").build());
        } catch (CommonException e) {
            throw e;
        }
    }
}
