/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

/**
 *
 * @author esmail.anbar
 */
public class RetrieveMessageInputModel {
    
    private String msisdn;
    private long messageID;
    private String systemName;
        private String submissionDate;//2595 smpp
    private String systemPassword; // CR 1901 | eslam.ahmed

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    
    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    } 

    public String getSystemPassword() {
        return systemPassword;
    }

    public void setSystemPassword(String systemPassword) {
        this.systemPassword = systemPassword;
    }
    
}
