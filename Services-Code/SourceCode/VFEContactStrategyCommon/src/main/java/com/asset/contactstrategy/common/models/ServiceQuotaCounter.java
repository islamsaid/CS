
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import com.asset.concurrent.model.SafeInteger;

/**
 *
 * @author eslam.ahmed
 */
public class ServiceQuotaCounter {
    private SafeInteger doNotApplyCounter;
    private SafeInteger monitorCounter;
    private SafeInteger dailyQuotaCounter;
    private Integer serviceId;
    private Integer serviceHistoryId;

    public ServiceQuotaCounter() {
        this.doNotApplyCounter = new SafeInteger();
        this.monitorCounter = new SafeInteger();
        this.dailyQuotaCounter = new SafeInteger();
    }

    public ServiceQuotaCounter(Integer serviceId, Integer serviceHistoryId) {
        this.serviceId = serviceId;
        this.serviceHistoryId = serviceHistoryId;
        this.doNotApplyCounter = new SafeInteger();
        this.monitorCounter = new SafeInteger();
        this.dailyQuotaCounter = new SafeInteger();
    }
    
    
    
    public SafeInteger getDoNotApplyCounter() {
        return doNotApplyCounter;
    }

    public void setDoNotApplyCounter(SafeInteger doNotApplyCounter) {
        this.doNotApplyCounter = doNotApplyCounter;
    }

    public SafeInteger getMonitorCounter() {
        return monitorCounter;
    }

    public void setMonitorCounter(SafeInteger monitorCounter) {
        this.monitorCounter = monitorCounter;
    }

    public SafeInteger getDailyQuotaCounter() {
        return dailyQuotaCounter;
    }

    public void setDailyQuotaCounter(SafeInteger dailyQuotaCounter) {
        this.dailyQuotaCounter = dailyQuotaCounter;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getServiceHistoryId() {
        return serviceHistoryId;
    }

    public void setServiceHistoryId(Integer serviceHistoryId) {
        this.serviceHistoryId = serviceHistoryId;
    }
    
    
    
}

