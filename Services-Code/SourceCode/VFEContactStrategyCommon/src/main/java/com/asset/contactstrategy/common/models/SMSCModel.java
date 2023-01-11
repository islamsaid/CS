/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;

/**
 *
 * @author rania.magdy
 */
public class SMSCModel implements Serializable {

    private long versionId;
    private long SMSCid;
    private String SMSCname;
    private String ip;
    private Integer port;
    private String username;
    private String password;
    private String systemType;
    private long creator;
    private int status;
    private String description;
    private Integer windowSize;
    private Integer throughput;


    public long getSMSCid() {
        return SMSCid;
    }

    public void setSMSCid(long SMSCid) {
        this.SMSCid = SMSCid;
    }

    public String getSMSCname() {
        return SMSCname;
    }

    public void setSMSCname(String SMSCname) {
        this.SMSCname = SMSCname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public long getCreator() {
        return creator;
    }

    public void setCreator(long creator) {
        this.creator = creator;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Integer windowSize) {
        this.windowSize = windowSize;
    }

    public Integer getThroughput() {
        return throughput;
    }

    public void setThroughput(Integer throughput) {
        this.throughput = throughput;
    }
    
    @Override
    public String toString() {

        String delimiter = ";";
        StringBuilder sb = new StringBuilder("");
        sb.append(this.getVersionId());
        sb.append(delimiter);
        sb.append(this.SMSCid);
        sb.append(delimiter);
        sb.append(this.SMSCname);
        sb.append(delimiter);
        sb.append(this.ip);
        sb.append(delimiter);
        sb.append(this.port);
        sb.append(delimiter);
        sb.append(this.username);
        sb.append(delimiter);
        sb.append(this.password);
        sb.append(delimiter);
        sb.append(this.systemType);
        sb.append(delimiter);
        sb.append(this.status);
        sb.append(delimiter);
        sb.append(this.creator);
        sb.append(delimiter);
        sb.append(this.description);
        sb.append(delimiter);
        sb.append(this.windowSize);
        sb.append(delimiter);
        sb.append(this.throughput);
        return sb.toString();
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
}
