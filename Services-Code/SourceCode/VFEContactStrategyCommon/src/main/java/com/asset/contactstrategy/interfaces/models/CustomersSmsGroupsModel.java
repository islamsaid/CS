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
public class CustomersSmsGroupsModel {

    private int runId ;
    private String msisdn ;
    private int lastTwoDigits ;
    private int groupId ;
    
    public CustomersSmsGroupsModel() {
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    
    
    
}
