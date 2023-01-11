/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.models;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author nancy.abdelgawad
 */
public class HServiceErrorModel {

    private int id;
    private String errorType;
    private int errorSeverity;
    private String errorTypeString;
    private String msisdn;
    private String errorParams;
    private String errorCode;
    private String errorMessage;
    private String txId;
    private Date errorTime;
    private Date insertionTime;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorParams() {
        return errorParams;
    }

    public void setErrorParams(String errorParams) {
        this.errorParams = errorParams;
    }

    public int getErrorSeverity() {
        return errorSeverity;
    }

    public void setErrorSeverity(int errorSeverity) {
        this.errorSeverity = errorSeverity;
    }

    public String getErrorTypeString() {
        return errorTypeString;
    }

    public void setErrorTypeString(String errorTypeString) {
        this.errorTypeString = errorTypeString;
    }

    public Date getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(Date errorTime) {
        this.errorTime = errorTime;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Date insertionTime) {
        this.insertionTime = insertionTime;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
