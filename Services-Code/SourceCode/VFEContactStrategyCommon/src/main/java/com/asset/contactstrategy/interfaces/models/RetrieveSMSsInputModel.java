/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

import java.sql.Timestamp;

/**
 *
 * @author esmail.anbar
 */
public class RetrieveSMSsInputModel {
    
    private String msisdn;
    private Timestamp from;
    private Timestamp to;
    private String systemName;
    private String smsScript;
    private String systemPassword;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Timestamp getFrom() {
        return from;
    }

    public void setFrom(Timestamp from) {
        this.from = from;
    }

    public Timestamp getTo() {
        return to;
    }

    public void setTo(Timestamp to) {
        this.to = to;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSmsScript() {
        return smsScript;
    }

    public void setSmsScript(String smsScript) {
        this.smsScript = smsScript;
    }

    public String getSystemPassword() {
        return systemPassword;
    }

    public void setSystemPassword(String systemPassword) {
        this.systemPassword = systemPassword;
    }
    
    
}
