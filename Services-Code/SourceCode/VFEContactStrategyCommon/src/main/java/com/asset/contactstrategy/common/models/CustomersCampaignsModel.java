/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;

/**
 *
 * @author Aya Moawed
 */
public class CustomersCampaignsModel implements Serializable{
    
    private Long campaignId;
    private String msisdn;
    private Integer suspended;
    private Integer lastMSISDNTwoDigits;
    private Integer runId;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Integer getSuspended() {
        return suspended;
    }

    public void setSuspended(Integer suspended) {
        this.suspended = suspended;
    }

    public Integer getLastMSISDNTwoDigits() {
        return lastMSISDNTwoDigits;
    }

    public void setLastMSISDNTwoDigits(Integer lastMSISDNTwoDigits) {
        this.lastMSISDNTwoDigits = lastMSISDNTwoDigits;
    }

    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }
    
    
}
