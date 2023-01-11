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
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author rania.magdy
 */
public class MOMErrorsDAO {

    public void insertMOMError(Connection connection, MOMErrorsModel mErrorsModel) throws CommonException {

//        CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "[insertMOMError] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertMOMError Started").build());
        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        long startTime = System.currentTimeMillis();

        long id;
        try {
            id = CommonDAO.getNextId(connection,
                    DBStruct.VFE_CS_MOM_ERRORS.VFE_CS_MOM_ERRORS_SEQ);
            sqlQuery = new StringBuilder();

            sqlQuery.append("INSERT INTO ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.TABLE_NAME);
            sqlQuery.append(" ( ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.ERROR_ID).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.SRC_ID).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.PERCEIVED_SEVERITY).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.MODULE_NAME).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.FUNCTION_NAME).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.ENGINE_SRC_ID).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.ERROR_MESSAGE).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_MOM_ERRORS.ERROR_PARAMS);
            sqlQuery.append(" ) ");
            sqlQuery.append(" VALUES ");
            sqlQuery.append(" (?,?,?,?,?,?,?,?) ");
            statement = connection.prepareStatement(sqlQuery.toString());
            statement.setLong(1, id);
            statement.setString(2, mErrorsModel.getErrorCode());
            statement.setInt(3, mErrorsModel.getPreceivedSeverity());
            statement.setString(4, mErrorsModel.getModuleName());
            statement.setString(5, mErrorsModel.getFunctionName());
            statement.setInt(6, mErrorsModel.getEngineSrcID());
            statement.setString(7, mErrorsModel.getErrorMessage());
            statement.setString(8, mErrorsModel.getErrorParams());
//            CommonLogger.businessLogger.trace(SMSCDAO.class.getName() + " || " + " [Query] " + sqlQuery.toString());
            CommonLogger.businessLogger.trace(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertMOMError Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery.toString()).build());

//            CommonLogger.businessLogger.debug("Start Inserting MOMError: Id " + id + " | ErrorCode: " + mErrorsModel.getErrorCode() + " | PreceivedSeverity:" + mErrorsModel.getPreceivedSeverity() + " | ModuleName" + mErrorsModel.getModuleName()
//                    + " | FunctionName: " + mErrorsModel.getFunctionName() + " | EngineSrcID: " + mErrorsModel.getEngineSrcID() + " | ErrorMessage: " + mErrorsModel.getErrorMessage() + " | ErrorParams: " + mErrorsModel.getErrorParams());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Inserting MOMError")
                    .put(GeneralConstants.StructuredLogKeys.ID, id)
                    .put(GeneralConstants.StructuredLogKeys.ERROR_CODE, mErrorsModel.getErrorCode())
                    .put(GeneralConstants.StructuredLogKeys.PRECEIVED_SEVERITY, mErrorsModel.getPreceivedSeverity())
                    .put(GeneralConstants.StructuredLogKeys.MODULE_NAME, mErrorsModel.getModuleName())
                    .put(GeneralConstants.StructuredLogKeys.FUNCTION_NAME, mErrorsModel.getFunctionName())
                    .put(GeneralConstants.StructuredLogKeys.ENGINE_SOURCE_ID, mErrorsModel.getEngineSrcID())
                    .put(GeneralConstants.StructuredLogKeys.ERROR_MESSAGE, mErrorsModel.getErrorMessage())
                    .put(GeneralConstants.StructuredLogKeys.ERROR_PARAMS, mErrorsModel.getErrorParams()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug("Inserted in: " + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "MOMError Insertion Timing")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            CommonLogger.businessLogger.debug("Inserting MOMError ended...");

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [insertMOMError]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, statement);
        }

    }

}
