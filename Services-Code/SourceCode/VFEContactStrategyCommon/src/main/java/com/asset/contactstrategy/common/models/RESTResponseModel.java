/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

/**
 *
 * @author esmail.anbar
 */
public class RESTResponseModel {
    
    private String code;
    private String description;
    private String transId;

    public RESTResponseModel() {
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
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

    @Override
    public String toString() {
        return "code: " + code + " | description: " + description + " | transId: " + transId;
    }
}
