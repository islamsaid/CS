/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

/**
 *
 * @author hazem.fekry
 */
public class CustomersModel {
    private String msisdn;
    private int ratePlan;
    private int customerType;
    private int governmentId;
    private int adsGroupId;
    private int smsGroupId;
    private int lastTwoDigits;
    //CSPhase1.5 | Esmail.Anbar | Adding Language Property to DWH
    private String language;

    public CustomersModel() {
    }
    
    public boolean hasSmsGroupId()
    {
//        if(smsGroupId == null)
//            return false;
//        else
            return true;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public int getRatePlan() {
        return ratePlan;
    }

    public void setRatePlan(int ratePlan) {
        this.ratePlan = ratePlan;
    }

    public int getCustomerType() {
        return customerType;
    }

    public void setCustomerType(int customerType) {
        this.customerType = customerType;
    }

    public int getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(int governmentId) {
        this.governmentId = governmentId;
    }
    
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getAdsGroupId() {
        return adsGroupId;
    }

    public void setAdsGroupId(int adsGroupId) {
        this.adsGroupId = adsGroupId;
    }

    public int getSmsGroupId() {
        return smsGroupId;
    }

    public void setSmsGroupId(int smsGroupId) {
        this.smsGroupId = smsGroupId;
    }       

    public int getLastTwoDigits() {
        return lastTwoDigits;
    }

    public void setLastTwoDigits(int lastTwoDigits) {
        this.lastTwoDigits = lastTwoDigits;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
