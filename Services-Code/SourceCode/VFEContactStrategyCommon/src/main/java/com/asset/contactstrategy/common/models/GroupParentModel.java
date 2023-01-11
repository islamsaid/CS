/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class GroupParentModel extends GroupsParentModel implements Serializable {

    private int versionId;
    private String groupName;
    private String groupDescription;
    private int groupPriority;
    private int donotContact;
    private int dailyThreshold;
    private int weeklyThreshold;
    private int monthlyThreshold;
    private int guardPeriod;
    //private String filterQuery;
    private int groupId;
    //private int status;
    private int createdBy;
    private LookupModel groupType;
    //private ArrayList<FileModel> filesModel = new ArrayList<FileModel>();
    //private ArrayList<FilterModel> filterList = new ArrayList<FilterModel>();

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public int getGroupPriority() {
        return groupPriority;
    }

    public void setGroupPriority(int groupPriority) {
        this.groupPriority = groupPriority;
    }

    public int getDonotContact() {
        return donotContact;
    }

    public void setDonotContact(int donotContact) {
        this.donotContact = donotContact;
    }

    public int getDailyThreshold() {
        return dailyThreshold;
    }

    public void setDailyThreshold(int dailyThreshold) {
        this.dailyThreshold = dailyThreshold;
    }

    public int getWeeklyThreshold() {
        return weeklyThreshold;
    }

    public void setWeeklyThreshold(int weeklyThreshold) {
        this.weeklyThreshold = weeklyThreshold;
    }

    public int getMonthlyThreshold() {
        return monthlyThreshold;
    }

    public void setMonthlyThreshold(int monthlyThreshold) {
        this.monthlyThreshold = monthlyThreshold;
    }

    public int getGuardPeriod() {
        return guardPeriod;
    }

    public void setGuardPeriod(int guardPeriod) {
        this.guardPeriod = guardPeriod;
    }

    /*public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }*/

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /*public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }*/

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    /*public ArrayList<FilterModel> getFilterList() {
        return filterList;
    }

    public void setFilterList(ArrayList<FilterModel> filterList) {
        this.filterList = filterList;
    }*/

    public LookupModel getGroupType() {
        return groupType;
    }

    public void setGroupType(LookupModel groupType) {
        this.groupType = groupType;
    }

    /*public ArrayList<FileModel> getFilesModel() {
        return filesModel;
    }

    public void setFilesModel(ArrayList<FileModel> filesModel) {
        this.filesModel = filesModel;
    }*/

    @Override
    public String toString() {
        return  getVersionId() + ";" + groupName + ";" + groupDescription + ";" + groupPriority + ";" + donotContact + ";" + dailyThreshold + ";" + weeklyThreshold + ";" + monthlyThreshold + ";" + guardPeriod + ";" + groupId + ";" + createdBy + ";" + groupType ;
    }

    /**
     * @return the versionId
     */
    public int getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    
    
}
