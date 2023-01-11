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
public class CustomerCampaignsModel {
    private int id;
    private String msisdn;
    private int campId;
    private int campCount;
    private int msisdnModX;
    private CampaignModel customerCampaign;

    public CustomerCampaignsModel() {
    }

    public CustomerCampaignsModel(int id, String msisdn, int campId, int campCount, int msisdnModX) {
        this.id = id;
        this.msisdn = msisdn;
        this.campId = campId;
        this.campCount = campCount;
        this.msisdnModX = msisdnModX;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getCampId() {
        return campId;
    }

    public void setCampId(int campId) {
        this.campId = campId;
    }

    public int getCampCount() {
        return campCount;
    }

    public void setCampCount(int campCount) {
        this.campCount = campCount;
    }

    public int getMsisdnModX() {
        return msisdnModX;
    }

    public void setMsisdnModX(int msisdnModX) {
        this.msisdnModX = msisdnModX;
    }

    public CampaignModel getCustomerCampaign() {
        return customerCampaign;
    }

    public void setCustomerCampaign(CampaignModel customerCampaign) {
        this.customerCampaign = customerCampaign;
    }
        
}
