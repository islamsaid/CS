/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.webmodels;

import com.asset.contactstrategy.common.models.AdsGroupModel;

/**
 *
 * @author kerollos.asaad
 */
public class AdsGroupWebModel extends AdsGroupModel {

    private boolean enableEdit;
    private boolean enableDelete;
    private boolean enableApprove;
    private String statusName;
    
    public AdsGroupWebModel(){
        
    }
    
    public AdsGroupWebModel(AdsGroupWebModel adsGroupWebModel){
        this.setVersionId(adsGroupWebModel.getVersionId());
        this.setGroupId(adsGroupWebModel.getVersionId());
        this.setCreatedBy(adsGroupWebModel.getCreatedBy());
        this.setDailyThreshold(adsGroupWebModel.getDailyThreshold());
        this.setDonotContact(adsGroupWebModel.getDonotContact());
        this.setEnableApprove(adsGroupWebModel.isEnableApprove());
        this.setEnableDelete(adsGroupWebModel.isEnableDelete());
        this.setEnableEdit(adsGroupWebModel.isEnableEdit());
        this.setFilesModel(adsGroupWebModel.getFilesModel());
        this.setFilterList(adsGroupWebModel.getFilterList());
        this.setFilterQuery(adsGroupWebModel.getFilterQuery());
        this.setGroupDescription(adsGroupWebModel.getGroupDescription());
        this.setGroupName(adsGroupWebModel.getGroupName());
        this.setGroupPriority(adsGroupWebModel.getGroupPriority());
        this.setGroupType(adsGroupWebModel.getGroupType());
        this.setGuardPeriod(adsGroupWebModel.getGuardPeriod());
        this.setMonthlyThreshold(adsGroupWebModel.getMonthlyThreshold());
        this.setStatus(adsGroupWebModel.getStatus());
        this.setStatusName(adsGroupWebModel.getStatusName());
        this.setWeeklyThreshold(adsGroupWebModel.getWeeklyThreshold());
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
