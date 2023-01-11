/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.interfaces.models;

/**
 *
 * @author mostafa.kashif
 */
public class CustomerConfigAndGrpModel extends CustomerConfigurationsModel{
    
    private int groupId;
    private boolean hasCustomConfig;
    private boolean exitsInGroup;
    private boolean existsInDWH;
    private boolean violationFlag;

    public boolean isViolationFlag() {
        return violationFlag;
    }

    public void setViolationFlag(boolean violationFlag) {
        this.violationFlag = violationFlag;
    }

    public boolean isHasCustomConfig() {
        return hasCustomConfig;
    }

    public void setHasCustomConfig(boolean hasCustomConfig) {
        this.hasCustomConfig = hasCustomConfig;
    }

    public boolean isExitsInGroup() {
        return exitsInGroup;
    }

    public void setExitsInGroup(boolean exitsInGroup) {
        this.exitsInGroup = exitsInGroup;
    }

    public boolean isExistsInDWH() {
        return existsInDWH;
    }

    public void setExistsInDWH(boolean existsInDWH) {
        this.existsInDWH = existsInDWH;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
}
