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
public class CustomersAdsGroupsModel {

    private int runId ;
    private String msisdn ;
    private int lastTwoDigits ;
    private int adsGroupId ;
    
    public CustomersAdsGroupsModel() {
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getLastTwoDigits() {
        return lastTwoDigits;
    }

    public void setLastTwoDigits(int lastTwoDigits) {
        this.lastTwoDigits = lastTwoDigits;
    }

    public int getAdsGroupId() {
        return adsGroupId;
    }

    public void setAdsGroupId(int adsGroupId) {
        this.adsGroupId = adsGroupId;
    }
    
    
    
    
}
