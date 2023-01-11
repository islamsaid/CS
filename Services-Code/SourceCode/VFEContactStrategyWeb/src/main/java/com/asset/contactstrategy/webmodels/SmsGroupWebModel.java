/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.webmodels;

import com.asset.contactstrategy.common.models.SMSGroupModel;

/**
 *
 * @author kerollos.asaad
 */
public class SmsGroupWebModel extends SMSGroupModel {

    private boolean enableEdit;
    private boolean enableDelete;
    private boolean enableApprove;
    private String statusName;

    public SmsGroupWebModel() {
    }

    public SmsGroupWebModel(SmsGroupWebModel smsGroupWebModel){
        this.setVersionId(smsGroupWebModel.getVersionId());
        this.setGroupId(smsGroupWebModel.getGroupId());
        this.setCreatedBy(smsGroupWebModel.getCreatedBy());
        this.setDailyThreshold(smsGroupWebModel.getDailyThreshold());
        this.setDonotContact(smsGroupWebModel.getDonotContact());
        this.setEnableApprove(smsGroupWebModel.isEnableApprove());
        this.setEnableDelete(smsGroupWebModel.isEnableDelete());
        this.setEnableEdit(smsGroupWebModel.isEnableEdit());
        this.setFilesModel(smsGroupWebModel.getFilesModel());
        this.setFilterList(smsGroupWebModel.getFilterList());
        this.setFilterQuery(smsGroupWebModel.getFilterQuery());
        this.setGroupDescription(smsGroupWebModel.getGroupDescription());
        this.setGroupName(smsGroupWebModel.getGroupName());
        this.setGroupPriority(smsGroupWebModel.getGroupPriority());
        this.setGroupType(smsGroupWebModel.getGroupType());
        this.setGuardPeriod(smsGroupWebModel.getGuardPeriod());
        this.setMonthlyThreshold(smsGroupWebModel.getMonthlyThreshold());
        this.setStatus(smsGroupWebModel.getStatus());
        this.setStatusName(smsGroupWebModel.getStatusName());
        this.setWeeklyThreshold(smsGroupWebModel.getWeeklyThreshold());
    }
    
    public boolean isEnableEdit() {
        return enableEdit;
    }

    public void setEnableEdit(boolean enableEdit) {
        this.enableEdit = enableEdit;
    }

    public boolean isEnableDelete() {
        return enableDelete;
    }

    public void setEnableDelete(boolean enableDelete) {
        this.enableDelete = enableDelete;
    }

    public boolean isEnableApprove() {
        return enableApprove;
    }

    public void setEnableApprove(boolean enableApprove) {
        this.enableApprove = enableApprove;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
    
    @Override
    public String toString() {
        return super.toString();
    }

}
