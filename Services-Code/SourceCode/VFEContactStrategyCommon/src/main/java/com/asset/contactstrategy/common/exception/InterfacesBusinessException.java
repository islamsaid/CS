/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.exception;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.logger.CommonLogger;

/**
 *
 * @author esmail.anbar
 */
public class InterfacesBusinessException extends Exception
{
    private int srcId;
    private int errorCode;
    private String message;
    private String detail;

    public InterfacesBusinessException() {
    }

    public InterfacesBusinessException(int srcId, int errorCode, String detail) {
        this.srcId = srcId;
        this.errorCode = errorCode;
        try
        {
            if (detail != null && !detail.isEmpty())
                this.detail = detail;
            this.message = Defines.messagesBundle.getString(srcId + String.valueOf(this.errorCode));
        }
        catch (Exception e)
        {
            message = "Can't Retrieve Error Message From Properties File ... " + e.getMessage();
            CommonLogger.businessLogger.error("Can't Retrieve Error Message From Properties File ... " + e.getMessage());
            CommonLogger.errorLogger.error("Can't Retrieve Error Message From Properties File ... " + e.getMessage(), e);
        }
    }
    
    public String getDetailedMessage() 
    {
        //super.getMessage(); 
        return message + " ... " + detail;
    }
    
    @Override
    public String getMessage() 
    {
        //super.getMessage(); 
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public int getSrcId() {
        return srcId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getDetail() {
        return (detail!= null) ? detail : "";
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    
}
