/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

/**
 *
 * @author Hazem>fekry
 */
public class AdsGroupModel {

    private int id;
    private int groupId;
    private String groupName;
    private String groupDescription;
    private int groupPriority;
    private int dailyThreshold;
    private int weeklyThreshold;
    private int monthlyThreshold;
    private String filterQuery;
    private int status;
    private int creator;
    private int groupEditId;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

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

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public int getGroupEditId() {
        return groupEditId;
    }

    public void setGroupEditId(int groupEditId) {
        this.groupEditId = groupEditId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }
    
    
}
