/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author rania.magdy
 */
public class CampaignModel extends GroupsParentModel implements Serializable {

    private long versionId;
    private long campaignId;
    private String campaignName;
    private String campaignDescription;
    private String englishScript;
    private String arabicScript;
    private Date startDate;
    private Date endDate;
    private int campaignStatus;
    //private int status;
    private int controlPercentage;
    private int maxTargetedCustomers;
    private int maxNumberOfCommunications;
    private int priority;
    //private String filterQuery;
    private long lastModifiedBy;
    private String lastModifiedByName;
    private String editedDescription;
    private LookupModel filterType;
    //private ArrayList<FileModel> filesModel = new ArrayList<FileModel>();
    //private ArrayList<FilterModel> filterList = new ArrayList<FilterModel>();


    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
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

    public String getEnglishScript() {
        return englishScript;
    }

    public void setEnglishScript(String englishScript) {
        this.englishScript = englishScript;
    }

    public String getArabicScript() {
        return arabicScript;
    }

    public void setArabicScript(String arabicScript) {
        this.arabicScript = arabicScript;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /*public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }*/

    public int getControlPercentage() {
        return controlPercentage;
    }

    public void setControlPercentage(int controlPercentage) {
        this.controlPercentage = controlPercentage;
    }

    public int getMaxTargetedCustomers() {
        return maxTargetedCustomers;
    }

    public void setMaxTargetedCustomers(int maxTargetedCustomers) {
        this.maxTargetedCustomers = maxTargetedCustomers;
    }

    public int getMaxNumberOfCommunications() {
        return maxNumberOfCommunications;
    }

    public void setMaxNumberOfCommunications(int maxNumberOfCommunications) {
        this.maxNumberOfCommunications = maxNumberOfCommunications;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /*public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }*/

    public long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getEditedDescription() {
        return editedDescription;
    }

    public void setEditedDescription(String editedDescription) {
        this.editedDescription = editedDescription;
    }

    public int getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(int campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    public LookupModel getFilterType() {
        return filterType;
    }

    public void setFilterType(LookupModel filterType) {
        this.filterType = filterType;
    }

    /*public ArrayList<FileModel> getFilesModel() {
        return filesModel;
    }

    public void setFilesModel(ArrayList<FileModel> filesModel) {
        this.filesModel = filesModel;
    }*/

    /*public ArrayList<FilterModel> getFilterList() {
        return filterList;
    }

    public void setFilterList(ArrayList<FilterModel> filterList) {
        this.filterList = filterList;
    }*/

    public String getLastModifiedByName() {
        return lastModifiedByName;
    }

    public void setLastModifiedByName(String lastModifiedByName) {
        this.lastModifiedByName = lastModifiedByName;
    }

    @Override
    public String toString() {

        String delimiter = ";";
        StringBuilder sb = new StringBuilder("");
        sb.append(this.versionId);
        sb.append(delimiter);
        sb.append(this.campaignId);
        sb.append(delimiter);
        sb.append(this.campaignName);
        sb.append(delimiter);
        sb.append(this.campaignDescription);
        sb.append(delimiter);
        sb.append(this.englishScript);
        sb.append(delimiter);
        sb.append(this.arabicScript);
        sb.append(delimiter);
        sb.append(this.startDate);
        sb.append(delimiter);
        sb.append(this.endDate);
        sb.append(delimiter);
        sb.append(this.campaignStatus);
        sb.append(delimiter);
        //sb.append(this.status);
        sb.append(delimiter);
        sb.append(this.controlPercentage);
        sb.append(delimiter);
        sb.append(this.maxTargetedCustomers);
        sb.append(delimiter);
        sb.append(this.maxNumberOfCommunications);
        sb.append(delimiter);
        sb.append(this.priority);
        sb.append(delimiter);
        //sb.append(this.filterQuery);
        sb.append(delimiter);
        sb.append(this.lastModifiedBy);
        sb.append(delimiter);
        sb.append(this.editedDescription);

        return sb.toString();
    }

    /**
     * @return the versionId
     */
    public long getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

}
