/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.sql.Timestamp;

/**
 *
 * @author Zain.Hamed
 */
public class MatchingInstanceModel {
    
    private int srcId;
    private String instanceId;
    private int runId;
    private int status;
    private boolean active;
    private Timestamp lastUpdateDate;
    private int subPartitionStart;
    private int subPartitionEnd;

    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

 

    public int getSubPartitionStart() {
        return subPartitionStart;
    }

    public void setSubPartitionStart(int subPartitionStart) {
        this.subPartitionStart = subPartitionStart;
    }

    public int getSubPartitionEnd() {
        return subPartitionEnd;
    }

    public void setSubPartitionEnd(int subPartitionEnd) {
        this.subPartitionEnd = subPartitionEnd;
    }

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
    
    
    
    
}
