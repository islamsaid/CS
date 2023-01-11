/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kerollos.asaad
 */
@XmlRootElement
public class FileModel implements Serializable {

    int fileID;
    String fileData;
    String fileName;
    private Integer fileStatusId;
    private Integer groupFileTypeId;
    private String groupFileTypeString;
    private Date lastDateModified;

    //________________________________ Setters & Getters_________________________________________________________
    public int getFileID() {
        return fileID;
    }

    @XmlElement
    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    @XmlTransient
    public String getFileData() {
        return fileData;
    }

    @XmlTransient
    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    @XmlElement
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileStatusId() {
        return fileStatusId;
    }

    public void setFileStatusId(Integer fileStatusId) {
        this.fileStatusId = fileStatusId;
    }

    public Integer getGroupFileTypeId() {
        return groupFileTypeId;
    }

    public void setGroupFileTypeId(Integer groupFileTypeId) {
        this.groupFileTypeId = groupFileTypeId;
    }

    public Date getLastDateModified() {
        return lastDateModified;
    }

    public void setLastDateModified(Date lastDateModified) {
        this.lastDateModified = lastDateModified;
    }

    public String getGroupFileTypeString() {
        return groupFileTypeString;
    }

    public void setGroupFileTypeString(String groupFileTypeString) {
        this.groupFileTypeString = groupFileTypeString;
    }

    @Override
    public String toString() {
        return "WLFilesModel{" + "fileID=" + fileID + ", fileName=" + fileName + ", fileStatusId=" + fileStatusId + ", whiteListFileTypeId=" + groupFileTypeId + ", lastDateModified=" + lastDateModified + '}';
    }

}