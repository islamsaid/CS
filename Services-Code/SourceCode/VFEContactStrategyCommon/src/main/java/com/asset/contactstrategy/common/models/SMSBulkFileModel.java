package com.asset.contactstrategy.common.models;

import com.asset.contactstrategy.interfaces.models.InputModel;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author aya.moawed
 * 2595
 */
public class SMSBulkFileModel {
    private String name;
    private Integer failedRecordCount;
    private Integer successRecordCount; 
    private Integer totalRecordCount;
    private Integer userID;
    private Integer id;
    private int status;
    private ArrayList<InputModel> dataRecords;
    private ArrayList<InputModel> failuredataRecords;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getFailedRecordCount() {
        return failedRecordCount;
    }

    public void setFailedRecordCount(Integer failedRecordCount) {
        this.failedRecordCount = failedRecordCount;
    }

    public Integer getSuccessRecordCount() {
        return successRecordCount;
    }

    public void setSuccessRecordCount(Integer successRecordCount) {
        this.successRecordCount = successRecordCount;
    }

    public Integer getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(Integer totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public ArrayList<InputModel> getDataRecords() {
        return dataRecords;
    }

    public void setDataRecords(ArrayList<InputModel> dataRecords) {
        this.dataRecords = dataRecords;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final SMSBulkFileModel other = (SMSBulkFileModel) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    /**
     * @return the failuredataRecords
     */
    public ArrayList<InputModel> getFailuredataRecords() {
        return failuredataRecords;
    }

    /**
     * @param failuredataRecords the failuredataRecords to set
     */
    public void setFailuredataRecords(ArrayList<InputModel> failuredataRecords) {
        this.failuredataRecords = failuredataRecords;
    }

}
