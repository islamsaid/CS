/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;
import java.util.Date;



public class ReportsModel implements Serializable{
    private Date fromDate;
    private Date toDate;
    private String msisdn;
    private String smscName;
    private int smscId;
    private String systemPriority;
    private int serviceId;
    private String serviceName;
    private int messageStatus;

    private int count;

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * @param toDate the toDate to set
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * @param msisdn the msisdn to set
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    
    /**
     * @return the smscName
     */
    public String getSmscName() {
        return smscName;
    }

    /**
     * @param smscName the smscName to set
     */
    public void setSmscName(String smscName) {
        this.smscName = smscName;
    }

    /**
     * @return the systemPriority
     */
    public String getSystemPriority() {
        return systemPriority;
    }

    /**
     * @param systemPriority the systemPriority to set
     */
    public void setSystemPriority(String systemPriority) {
        this.systemPriority = systemPriority;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the serviceId
     */
    public int getServiceId() {
        return serviceId;
    }

   
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return the messageStatus
     */
    public int getMessageStatus() {
        return messageStatus;
    }

    /**
     * @param messageStatus the messageStatus to set
     */
    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the smscId
     */
    public int getSmscId() {
        return smscId;
    }

    /**
     * @param smscId the smscId to set
     */
    public void setSmscId(int smscId) {
        this.smscId = smscId;
    }



 

 
}
