/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.webmodels;

import com.asset.contactstrategy.common.models.SMSCModel;
import java.io.Serializable;

/**
 *
 * @author rania.magdy
 */
public class SMSCWebModel extends SMSCModel implements Serializable {

    private boolean enableEdit;
    private boolean enableDelete;
    private String statusName;
    private String passwordConfirm;
    private String oldPassword;
    private boolean isViewMode = false;
    private boolean isEditMode = false;
    private boolean isCreationMode = false;
    private boolean approvalMode = false;

    public SMSCWebModel() {
    }

    public SMSCWebModel(SMSCWebModel model) {
        this.setDescription(model.getDescription());
        this.setIp(model.getIp());
        this.setPassword(model.getPassword());
        this.setSMSCname(model.getSMSCname());
        this.setSystemType(model.getSystemType());
        this.setUsername(model.getUsername());
        this.setVersionId(model.getVersionId());
        this.setPort(model.getPort());
        this.setSMSCid(model.getSMSCid());
        this.setStatus(model.getStatus());

    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public boolean isIsCreationMode() {
        return isCreationMode;
    }

    public void setIsCreationMode(boolean isCreationMode) {
        this.isCreationMode = isCreationMode;
    }

    public boolean isIsEditMode() {
        return isEditMode;
    }

    public void setIsEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean isIsViewMode() {
        return isViewMode;
    }

    public void setIsViewMode(boolean isViewMode) {
        this.isViewMode = isViewMode;
    }

    public boolean isApprovalMode() {
        return approvalMode;
    }

    public void setApprovalMode(boolean approvalMode) {
        this.approvalMode = approvalMode;
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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SMSCWebModel) {
            if (((SMSCWebModel) obj).getVersionId()== this.getVersionId()) {
                return true;
            }
        }
        return false;
    }

}
