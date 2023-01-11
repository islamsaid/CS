/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yomna Naser
 */
public class QueueModel implements Serializable {

    private long versionId;

    public QueueModel() {

    }

    public QueueModel(String appName, String queueName, String schemaName) {
        this.appName = appName;
        this.queueName = queueName;
        this.schemaName = schemaName;
    }
    private long appId;
    private String appName;
    private String queueName;
    private int dequeuePoolSize;
    private int senderPoolSize;
    private String schemaName;
    private String schemaPassword;
    private String databaseUrl;
    private int creator;
    private String creatorName;
    private Integer lastModifiedBy;
    private String lastModifiedByName;
    private int status;
    private int threshold;
    private boolean timeWindowFlag;
    private Integer timeWindowFromHour;
    private Integer timeWindowFromMin;
    private Integer timeWindowToHour;
    private Integer timeWindowToMin;
    private List<SMSCModel> smscModels;
    private String editedVersionDescription;
    private String tableSpaceName;
    private String encryptedSchemaPassword;
    private int queueType;

    public String getEncryptedSchemaPassword() {
        return encryptedSchemaPassword;
    }

    public void setEncryptedSchemaPassword(String encryptedSchemaPassword) {
        this.encryptedSchemaPassword = encryptedSchemaPassword;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getDequeuePoolSize() {
        return dequeuePoolSize;
    }

    public void setDequeuePoolSize(int dequeuePoolSize) {
        this.dequeuePoolSize = dequeuePoolSize;
    }

    public int getSenderPoolSize() {
        return senderPoolSize;
    }

    public void setSenderPoolSize(int senderPoolSize) {
        this.senderPoolSize = senderPoolSize;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaPassword() {
        return schemaPassword;
    }

    public void setSchemaPassword(String schemaPassword) {
        this.schemaPassword = schemaPassword;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Integer getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Integer lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedByName() {
        return lastModifiedByName;
    }

    public void setLastModifiedByName(String lastModifiedByName) {
        this.lastModifiedByName = lastModifiedByName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isTimeWindowFlag() {
        return timeWindowFlag;
    }

    public void setTimeWindowFlag(boolean timeWindowFlag) {
        this.timeWindowFlag = timeWindowFlag;
    }

    public Integer getTimeWindowFromHour() {
        return timeWindowFromHour;
    }

    public void setTimeWindowFromHour(Integer timeWindowFromHour) {
        this.timeWindowFromHour = timeWindowFromHour;
    }

    public Integer getTimeWindowFromMin() {
        return timeWindowFromMin;
    }

    public void setTimeWindowFromMin(Integer timeWindowFromMin) {
        this.timeWindowFromMin = timeWindowFromMin;
    }

    public Integer getTimeWindowToHour() {
        return timeWindowToHour;
    }

    public String getTableSpaceName() {
        return tableSpaceName;
    }

    public void setTimeWindowToHour(Integer timeWindowToHour) {
        this.timeWindowToHour = timeWindowToHour;
    }

    public Integer getTimeWindowToMin() {
        return timeWindowToMin;
    }

    public void setTimeWindowToMin(Integer timeWindowToMin) {
        this.timeWindowToMin = timeWindowToMin;
    }

    public List<SMSCModel> getSmscModels() {
        return smscModels;
    }

    public void setSmscModels(List<SMSCModel> smscModels) {
        this.smscModels = smscModels;
    }

    public String getEditedVersionDescription() {
        return editedVersionDescription;
    }

    public void setEditedVersionDescription(String editedVersionDescription) {
        this.editedVersionDescription = editedVersionDescription;
    }

    public void setTableSpaceName(String tableSpaceName) {
        this.tableSpaceName = tableSpaceName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        QueueModel that = (QueueModel) other;
        if (that.getVersionId() != this.getVersionId()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String delimiter = ";";
        StringBuilder sb = new StringBuilder("");
        sb.append(this.versionId);
        sb.append(delimiter);
        sb.append(this.appId);
        sb.append(delimiter);
        sb.append(this.appName);
        sb.append(delimiter);
        sb.append(this.creator);
        sb.append(delimiter);
        sb.append(this.databaseUrl);
        sb.append(delimiter);
        sb.append(this.dequeuePoolSize);
        sb.append(delimiter);
        sb.append(this.queueName);
        sb.append(delimiter);
        sb.append(this.schemaName);
        sb.append(delimiter);
        sb.append(this.schemaPassword);
        sb.append(delimiter);
        sb.append(this.senderPoolSize);
        sb.append(delimiter);
        sb.append(this.status);
        sb.append(delimiter);
        sb.append(this.threshold);
        sb.append(delimiter);
        sb.append(this.timeWindowFlag);
        sb.append(delimiter);
        sb.append(this.timeWindowFromHour);
        sb.append(delimiter);
        sb.append(this.timeWindowFromMin);
        sb.append(delimiter);
        sb.append(this.timeWindowToHour);
        sb.append(delimiter);
        sb.append(this.timeWindowToMin);
        sb.append(delimiter);
        sb.append(this.tableSpaceName);
        sb.append(delimiter);
        if (smscModels != null && !smscModels.isEmpty()) {
            sb.append("{");
            String innerDelimiter = ",";
            for (SMSCModel value : smscModels) {
                sb.append(innerDelimiter);
                sb.append(value.getVersionId());
            }
            sb.append("}");
        }
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        QueueModel clonnedModel = new QueueModel();
        clonnedModel.setVersionId(this.versionId);
        clonnedModel.setAppId(this.appId);
        clonnedModel.setAppName(this.appName);
        clonnedModel.setQueueName(this.queueName);
        clonnedModel.setDequeuePoolSize(this.dequeuePoolSize);
        clonnedModel.setSenderPoolSize(this.senderPoolSize);
        clonnedModel.setSchemaName(this.schemaName);
        clonnedModel.setSchemaPassword(this.schemaPassword);
        clonnedModel.setDatabaseUrl(this.databaseUrl);
        clonnedModel.setCreator(this.creator);
        clonnedModel.setCreatorName(this.creatorName);
        clonnedModel.setLastModifiedBy(this.lastModifiedBy);
        clonnedModel.setLastModifiedByName(this.lastModifiedByName);
        clonnedModel.setStatus(this.status);
        clonnedModel.setTimeWindowFlag(this.timeWindowFlag);
        clonnedModel.setTimeWindowFromHour(this.timeWindowFromHour);
        clonnedModel.setTimeWindowFromMin(this.timeWindowFromMin);
        clonnedModel.setTimeWindowToHour(this.timeWindowToHour);
        clonnedModel.setTimeWindowToMin(this.timeWindowToMin);
        //clonnedModel.setSmsQueueSize(this.); // retrieved from system properties.
        clonnedModel.setThreshold(this.threshold);
        clonnedModel.setTableSpaceName(this.tableSpaceName);
        clonnedModel.setSmscModels((ArrayList<SMSCModel>) this.smscModels);
        clonnedModel.setQueueType(this.queueType);
        return clonnedModel;
    }

    /**
     * @return the versionId
     */
    public long getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    /**
     * @return the queueType
     */
    public int getQueueType() {
        return queueType;
    }

    /**
     * @param queueType the queueType to set
     */
    public void setQueueType(int queueType) {
        this.queueType = queueType;
    }
}
