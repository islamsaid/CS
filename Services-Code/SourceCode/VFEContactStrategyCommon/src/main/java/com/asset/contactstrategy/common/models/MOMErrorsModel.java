/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.util.Date;

/**
 *
 * @author rania.magdy
 */
public class MOMErrorsModel {

    private long id;
    private Date entryDate;
    private String errorCode; //src id
    private int preceivedSeverity;
    private String moduleName;
    private String functionName;
    private int engineSrcID;
    private String errorParams="";
    private String errorMessage="";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getPreceivedSeverity() {
        return preceivedSeverity;
    }

    public void setPreceivedSeverity(int preceivedSeverity) {
        this.preceivedSeverity = preceivedSeverity;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getEngineSrcID() {
        return engineSrcID;
    }

    public void setEngineSrcID(int engineSrcID) {
        this.engineSrcID = engineSrcID;
    }

    public String getErrorParams() {
        return errorParams;
    }

    public void setErrorParams(String errorParams) {
        this.errorParams = errorParams;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
