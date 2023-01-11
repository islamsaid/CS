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
public class InterfacesLogModel {
    
    private long ID;
    private String interfaceName;
    private String interfaceInputURL;
    private String interfaceOutput;
    private long responseTime;
    private String requestStatus;
    private String transId;
    private String requestBody;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceInputURL() {
        return interfaceInputURL;
    }

    public void setInterfaceInputURL(String interfaceInputURL) {
        this.interfaceInputURL = interfaceInputURL;
    }

    public String getInterfaceOutput() {
        return interfaceOutput;
    }

    public void setInterfaceOutput(String interfaceOutput) {
        this.interfaceOutput = interfaceOutput;
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

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
