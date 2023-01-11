/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.webmodels;

import com.asset.contactstrategy.common.models.CampaignModel;

/**
 *
 * @author rania.magdy
 */
public class CampaignWebModel extends CampaignModel {

    private boolean enableEdit;
    private boolean enableDelete;
    private String statusName;
    private boolean isViewMode = false;
    private boolean isEditMode = false;
    private boolean isCreationMode = false;
    private boolean approvalMode = false;
    
    private String modifierName;
    private boolean enableStart;
    private boolean enablePause;
    private boolean enableStop;
    
    public CampaignWebModel(){
        
    }
   
    public CampaignWebModel(CampaignWebModel campaignWebModel){
        this.setArabicScript(campaignWebModel.getArabicScript());
        this.setCampaignDescription(campaignWebModel.getCampaignDescription());
        this.setCampaignId(campaignWebModel.getCampaignId());
        this.setCampaignName(campaignWebModel.getCampaignName());
        this.setCampaignStatus(campaignWebModel.getCampaignStatus());
        this.setControlPercentage(campaignWebModel.getControlPercentage());
        this.setEditedDescription(campaignWebModel.getEditedDescription());
        this.setEndDate(campaignWebModel.getEndDate());
        this.setEnglishScript(campaignWebModel.getEnglishScript());
        this.setFilterQuery(campaignWebModel.getFilterQuery());
        this.setVersionId(campaignWebModel.getVersionId());
        this.setLastModifiedBy(campaignWebModel.getLastModifiedBy());
        this.setMaxNumberOfCommunications(campaignWebModel.getMaxNumberOfCommunications());
        this.setMaxTargetedCustomers(campaignWebModel.getMaxTargetedCustomers());
        this.setPriority(campaignWebModel.getPriority());
        this.setStartDate(campaignWebModel.getStartDate());
        this.setStatus(campaignWebModel.getStatus());
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

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public boolean isIsViewMode() {
        return isViewMode;
    }

    public void setIsViewMode(boolean isViewMode) {
        this.isViewMode = isViewMode;
    }

    public boolean isIsEditMode() {
        return isEditMode;
    }

    public void setIsEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean isIsCreationMode() {
        return isCreationMode;
    }

    public void setIsCreationMode(boolean isCreationMode) {
        this.isCreationMode = isCreationMode;
    }

    public String getModifierName() {
        return modifierName;
    }

    public void setModifierName(String modifierName) {
        this.modifierName = modifierName;
    }

    public boolean isEnableStart() {
        return enableStart;
    }

    public void setEnableStart(boolean enableStart) {
        this.enableStart = enableStart;
    }

    public boolean isEnablePause() {
        return enablePause;
    }

    public void setEnablePause(boolean enablePause) {
        this.enablePause = enablePause;
    }

    public boolean isEnableStop() {
        return enableStop;
    }

    public void setEnableStop(boolean enableStop) {
        this.enableStop = enableStop;
    }

    public boolean isApprovalMode() {
        return approvalMode;
    }

    public void setApprovalMode(boolean approvalMode) {
        this.approvalMode = approvalMode;
    }
    
    

}
