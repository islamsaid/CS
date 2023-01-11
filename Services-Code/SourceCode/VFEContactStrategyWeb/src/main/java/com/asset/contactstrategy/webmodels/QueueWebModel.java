/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.webmodels;

import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Yomna Naser
 */
public class QueueWebModel extends QueueModel{

    private boolean enableEdit;
    private boolean enableDelete;
    private String statusLabel;

    public QueueWebModel() {
    }
    
    public QueueWebModel(QueueWebModel queueWebModel) {
        this.setVersionId(queueWebModel.getVersionId());
        this.setAppId(queueWebModel.getAppId());
        this.setAppName(queueWebModel.getAppName());
        this.setCreator(queueWebModel.getCreator());
        this.setCreatorName(queueWebModel.getCreatorName());
        this.setDatabaseUrl(queueWebModel.getDatabaseUrl());
        this.setDequeuePoolSize(queueWebModel.getDequeuePoolSize());
        this.setEnableDelete(queueWebModel.isEnableDelete());
        this.setEnableEdit(queueWebModel.isEnableEdit());
        this.setQueueName(queueWebModel.getQueueName());
        this.setSchemaName(queueWebModel.getSchemaName());
        this.setSchemaPassword(queueWebModel.getSchemaPassword());
        this.setSenderPoolSize(queueWebModel.getSenderPoolSize());
        this.setSmscModels(queueWebModel.getSmscModels());
        this.setStatus(queueWebModel.getStatus());
        this.setStatusLabel(queueWebModel.getStatusLabel());
        this.setThreshold(queueWebModel.getThreshold());
        this.setTimeWindowFlag(queueWebModel.isTimeWindowFlag());
        this.setTimeWindowFromHour(queueWebModel.getTimeWindowFromHour());
        this.setTimeWindowFromMin(queueWebModel.getTimeWindowFromMin());
        this.setTimeWindowToHour(queueWebModel.getTimeWindowToHour());
        this.setTimeWindowToMin(queueWebModel.getTimeWindowToMin());
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

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueueWebModel other = (QueueWebModel) obj;
        if (this.getVersionId()!= other.getVersionId()) {
            return false;
        }
        if (this.getAppId() != other.getAppId()) {
            return false;
        }
        if (!Objects.equals(this.getAppName(), other.getAppName())) {
            return false;
        }
//        if (this.getDequeuePoolSize() != other.getDequeuePoolSize()) {
//            return false;
//        }
//        if (this.getSenderPoolSize() != other.getSenderPoolSize()) {
//            return false;
//        }
        if (!Objects.equals(this.getSchemaName(), other.getSchemaName())) {
            return false;
        }
        if (!Objects.equals(this.getSchemaPassword(), other.getSchemaPassword())) {
            return false;
        }
        if (!Objects.equals(this.getDatabaseUrl(), other.getDatabaseUrl())) {
            return false;
        }
//        if (this.getThreshold() != other.getThreshold()) {
//            return false;
//        }
//        if (!Objects.equals(this.getTableSpaceName(), other.getTableSpaceName())) {
//            return false;
//        }
        return true;
    }
    
    
    
}
