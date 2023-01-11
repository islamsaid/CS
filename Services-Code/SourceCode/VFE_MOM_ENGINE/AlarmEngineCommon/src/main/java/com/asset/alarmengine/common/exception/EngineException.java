/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.exception;

import com.asset.alarmengine.common.utilities.EngineUtils;

/**
 *
 * @author Mostafa Kashif
 */
public class EngineException extends Exception{
       protected Exception cause;
    protected Throwable causeThrw;
    protected int errorCode;
    protected String errorMessage;
    protected int initialErrorCode;
    private int errorPrefix=10;
    public EngineException() {
    }

    @Override
    public Exception getCause() {
        return cause;
    }

     public EngineException(int errorCode) {
        String error = String.valueOf(errorPrefix) + String.valueOf(errorCode);
        this.errorCode = Integer.parseInt(error);
        this.initialErrorCode = errorCode;
        this.errorMessage = EngineUtils.getErrorMessage(error);
    }

     public EngineException(int errorCode, String errorMessage) {
        String error = String.valueOf(errorPrefix) + String.valueOf(errorCode);
        this.errorCode = Integer.parseInt(error);
        this.initialErrorCode = errorCode;
        this.errorMessage = errorMessage;
    }
      

     public  EngineException(Exception e, int errorCode) {
        this.cause = e;
        String error = String.valueOf(errorPrefix) + String.valueOf(errorCode);
        this.errorCode = Integer.parseInt(error);
        this.initialErrorCode = errorCode;
        this.errorMessage = EngineUtils.getErrorMessage(error);
    }

     public EngineException(Exception e, int errorCode, String errorMessage) {
        this.cause = e;
        String error = String.valueOf(errorPrefix) + String.valueOf(errorCode);
        this.errorCode = Integer.parseInt(error);
        this.initialErrorCode = errorCode;
        this.errorMessage = errorMessage;
        
       
    }

     public EngineException(Throwable e, int errorCode) {
        this.causeThrw = e;
        String error = String.valueOf(errorPrefix) + String.valueOf(errorCode);
        this.errorCode = Integer.parseInt(error);
        this.initialErrorCode = errorCode;
        this.errorMessage = EngineUtils.getErrorMessage(error);
    }

     public EngineException(Throwable e, int errorCode, String errorMessage) {
        this.causeThrw = e;
        String error = String.valueOf(errorPrefix) + String.valueOf(errorCode);
        this.errorCode = Integer.parseInt(error);
        this.initialErrorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getInitialErrorCode() {
        return initialErrorCode;
    }

    public void setInitialErrorCode(int initialErrorCode) {
        this.initialErrorCode = initialErrorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCause(Exception cause) {
        this.cause = cause;
    }
    
}
