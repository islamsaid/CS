/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.exception;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import java.sql.SQLException;

/**
 *
 * @author hazem.fekry
 */
public class CommonException extends Exception {

    private int errorId;
    private String errorMsg;
    private String errorCode;
    private int prefix;
    private String javaErrorMessage;

    public CommonException(int prefix, String errorCode, String javaErrorMessage) {
         //Zain.Hamed
        //super(javaErrorMessage);  //May Be Null
        this.prefix = prefix;
        this.errorCode = errorCode;
        if (javaErrorMessage != null && !javaErrorMessage.equals("")) {
            this.javaErrorMessage = javaErrorMessage;
        }
        this.errorMsg = Defines.messagesBundle.getString(prefix + errorCode);
    }

    public CommonException(int prefix, int errorCode, String javaErrorMessage) {
        //Zain.Hamed
        //super(javaErrorMessage);  //May Be Null
        this.prefix = prefix;
        this.errorCode = String.valueOf(errorCode);
        if (javaErrorMessage != null && !javaErrorMessage.equals("")) {
            this.javaErrorMessage = javaErrorMessage;
        }
        this.errorMsg = Defines.messagesBundle.getString(prefix + this.errorCode);
    }

    public CommonException(String errorMsg, int errorId) {
        //Zain.Hamed
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorId = errorId;
    }

    public CommonException(String errorMsg, String errorCode) {
        //Zain.Hamed    
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public int getPrefix() {
        return prefix;
    }

    public void setPrefix(int prefix) {
        this.prefix = prefix;
    }

    public String getJavaErrorMessage() {
        return javaErrorMessage;
    }

    public void setJavaErrorMessage(String javaErrorMessage) {
        this.javaErrorMessage = javaErrorMessage;
    }

    public CommonException(int errorId) {
        this.errorId = errorId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static CommonException handleException(Exception e) {
        CommonLogger.businessLogger.info("CommonException.handleException() Invoked...");
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        CommonException commonException;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            commonException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            commonException = new CommonException("Sql exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.ERROR_SQL);
        } else if (e instanceof InterruptedException) {
            commonException = new CommonException("Interrupted Exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof NullPointerException) {
            commonException = new CommonException("Null Pointer Exception in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.NULL_POINTER_ERROR);
        }// Handle other exception types
        else {
            commonException = new CommonException("Unknown error in CLASS:" + stackTraceElement[2].getClassName() + " method: " + stackTraceElement[2].getMethodName() + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_DELIVERY_AGGREGATION);
        errorModel.setErrorCode(commonException.getErrorCode());
        errorModel.setModuleName(stackTraceElement[2].getClassName());
        errorModel.setFunctionName(stackTraceElement[2].getMethodName());
        Utility.sendMOMAlarem(errorModel);

        CommonLogger.businessLogger.info("Logging Error: " + commonException.getErrorMsg(), e);
        CommonLogger.businessLogger.info("CommonException.handleException() Ended...");
        return commonException;
    }
}
