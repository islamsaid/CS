/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.interfaces.models;

/**
 *
 * @author Zain Al-Abedin
 */
public class RESTLogModel {
    
    private long ID;
    private String restName;
    private String inputURL;
    private String restOutput;
    private long responseTime;
    private String requestStatus;
    private String transId;
    private String requestBody;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getInputURL() {
        return inputURL;
    }

    public void setInputURL(String inputURL) {
        this.inputURL = inputURL;
    }

    public String getRestOutput() {
        return restOutput;
    }

    public void setRestOutput(String restOutput) {
        this.restOutput = restOutput;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
