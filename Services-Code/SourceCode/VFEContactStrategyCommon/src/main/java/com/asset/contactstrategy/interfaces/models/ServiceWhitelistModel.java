/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

/**
 *
 * @author hazem.fekry
 */
public class ServiceWhitelistModel {
    private int serviceId;
    private String ipAddress;

    public ServiceWhitelistModel() {
    }

    public ServiceWhitelistModel(int serviceId, String ipAddress) {
        this.serviceId = serviceId;
        this.ipAddress = ipAddress;
    }

    
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    
    
}
