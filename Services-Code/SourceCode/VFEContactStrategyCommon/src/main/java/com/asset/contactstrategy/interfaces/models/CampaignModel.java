/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

import java.util.Date;

/**
 *
 * @author hazem.fekry
 */
public class CampaignModel {
    
    private int campaignId;
    private String campaignName;
    private String campaignDescription;
    private Date campaignStart;
    private Date campaignEnd;
    private int maxcommunications;
    private String filterQuery;
    private int MaxTargeted;
    private int campaignPriority;
    private String arabicScript;
    private String englishScript;
    private int campaignStatus;
    private int controlPercentage;

    public CampaignModel() {
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignDescription() {
        return campaignDescription;
    }

    public void setCampaignDescription(String campaignDescription) {
        this.campaignDescription = campaignDescription;
    }

    public Date getCampaignStart() {
        return campaignStart;
    }

    public void setCampaignStart(Date campaignStart) {
        this.campaignStart = campaignStart;
    }

    public Date getCampaignEnd() {
        return campaignEnd;
    }

    public void setCampaignEnd(Date campaignEnd) {
        this.campaignEnd = campaignEnd;
    }

    public int getMaxcommunications() {
        return maxcommunications;
    }

    public void setMaxcommunications(int maxcommunications) {
        this.maxcommunications = maxcommunications;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public int getMaxTargeted() {
        return MaxTargeted;
    }

    public void setMaxTargeted(int MaxTargeted) {
        this.MaxTargeted = MaxTargeted;
    }

    public int getCampaignPriority() {
        return campaignPriority;
    }

    public void setCampaignPriority(int campaignPriority) {
        this.campaignPriority = campaignPriority;
    }

    public String getArabicScript() {
        return arabicScript;
    }

    public void setArabicScript(String arabicScript) {
        this.arabicScript = arabicScript;
    }

    public String getEnglishScript() {
        return englishScript;
    }

    public void setEnglishScript(String englishScript) {
        this.englishScript = englishScript;
    }

    public int getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(int campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    public int getControlPercentage() {
        return controlPercentage;
    }

    public void setControlPercentage(int controlPercentage) {
        this.controlPercentage = controlPercentage;
    }
    
    
    
}
