/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.common.models;

import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.common.models.RESTResponseModel;
import java.util.ArrayList;

/**
 *
 * @author mostafa.kashif
 */
public class DequeuerResponseModel {
    
    private  ArrayList<SMSBridge> sms;

    public ArrayList<SMSBridge> getSms() {
        return sms;
    }

    public void setSms(ArrayList<SMSBridge> sms) {
        this.sms = sms;
    }

    
    private String code;
    private String description;
    private String transId;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public DequeuerResponseModel() {
    }

  

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
