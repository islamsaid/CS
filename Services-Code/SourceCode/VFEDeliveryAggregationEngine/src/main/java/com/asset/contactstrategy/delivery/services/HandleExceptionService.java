/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.services;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import java.sql.SQLException;

/**
 *
 * @author esmail.anbar
 */
public class HandleExceptionService {

    private Exception e;

    public HandleExceptionService(Exception e) {
        this.e = e;
    }

    public CommonException handleException() {
        CommonLogger.businessLogger.info("CommonException.handleException() Invoked...");
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        CommonException DeliveryAggregationException;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            DeliveryAggregationException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            DeliveryAggregationException = new CommonException("Sql exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.ERROR_SQL);
        } else if (e instanceof InterruptedException) {
            DeliveryAggregationException = new CommonException("Interrupted Exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof NullPointerException) {
            DeliveryAggregationException = new CommonException("Null Pointer Exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.NULL_POINTER_ERROR);
        }// Handle other exception types
        else {
            DeliveryAggregationException = new CommonException("Unknown error in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_DELIVERY_AGGREGATION);
        errorModel.setErrorCode(DeliveryAggregationException.getErrorCode());
        errorModel.setModuleName(stackTraceElement[2].getClassName());
        errorModel.setFunctionName(stackTraceElement[2].getMethodName());
        Utility.sendMOMAlarem(errorModel);

//        CommonLogger.businessLogger.info("Logging Error: " + DeliveryAggregationException.getErrorMsg(), e);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Logging Error " + DeliveryAggregationException.getErrorMsg()).build(), e);
        CommonLogger.businessLogger.info("CommonException.handleException() Ended...");
        return DeliveryAggregationException;
    }

    public static CommonException wrapCommonException(Exception e) {
        CommonLogger.businessLogger.debug("CommonException.handleException() Invoked...");

        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        CommonException DeliveryAggregationException;

        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            DeliveryAggregationException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            DeliveryAggregationException = new CommonException("Sql exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.ERROR_SQL);
        } else if (e instanceof InterruptedException) {
            DeliveryAggregationException = new CommonException("Interrupted Exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof NullPointerException) {
            DeliveryAggregationException = new CommonException("Null Pointer Exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.NULL_POINTER_ERROR);
        }// Handle other exception types
        else {
            DeliveryAggregationException = new CommonException("Unknown error in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        CommonLogger.businessLogger.debug("CommonException.handleException() Ended...");
        return DeliveryAggregationException;
    }

    public static void sendMOM(CommonException ce, int engSrcId, int preceivedSeverity, String errorParams) {
        CommonLogger.businessLogger.debug("CommonException.handleException() Invoked...");

        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();

        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(preceivedSeverity);
        errorModel.setEngineSrcID(engSrcId);
        errorModel.setErrorCode(ce.getErrorCode());
        errorModel.setModuleName(stackTraceElement[2].getClassName());
        errorModel.setFunctionName(stackTraceElement[2].getMethodName());
        errorModel.setErrorMessage(ce.getErrorMsg());
        errorModel.setErrorParams(errorParams);

        Utility.sendMOMAlarem(errorModel);

        CommonLogger.businessLogger.debug("CommonException.handleException() Ended...");
    }
}
