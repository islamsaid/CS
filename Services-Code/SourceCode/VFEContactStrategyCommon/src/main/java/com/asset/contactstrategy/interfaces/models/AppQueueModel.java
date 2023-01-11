/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;


/**
 *
 * @author Yomna Naser
 */
public class AppQueueModel {
    
    private long id;
    private long appQueueId;
    private String appQueueName;
    private String queueName;
    private int dequeuePoolSize;
    private int senderPoolSize;
    private String schemaName;
    private String schemaPassword;
    private String databaseURL;
    private int creator;
    private int status;
    private int threshold;

    public AppQueueModel() {
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAppQueueId() {
        return appQueueId;
    }

    public void setAppQueueId(long appQueueId) {
        this.appQueueId = appQueueId;
    }

    public String getAppQueueName() {
        return appQueueName;
    }

    public void setAppQueueName(String appQueueName) {
        this.appQueueName = appQueueName;
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

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
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
    
}
