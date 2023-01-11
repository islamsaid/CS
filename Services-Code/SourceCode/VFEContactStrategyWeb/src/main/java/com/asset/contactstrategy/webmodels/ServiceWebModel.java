/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.webmodels;

import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.ServiceModel;

/**
 *
 * @author Amal Magdy
 */
public class ServiceWebModel extends ServiceModel {

    private boolean enableEdit;
    private boolean enableDelete;
    private String statusName;
//    private boolean autoCreatdFlag;
    
    // CR 1901 | eslam.ahmed
    private String password;
    private String passwordConfirm;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServiceWebModel other = (ServiceWebModel) obj;
        if (!this.getServiceName().equals(other.getServiceName())) {
            return false;
        } else if (this.getServiceID() != other.getServiceID()) {
            return false;
        }
        return true;
    }

}
