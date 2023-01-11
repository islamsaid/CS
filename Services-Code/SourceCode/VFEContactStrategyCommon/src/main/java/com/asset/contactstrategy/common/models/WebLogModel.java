/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.common.models;

/**
 *
 * @author Zain Al-Abedin
 */
public class WebLogModel {
    
    private String userName;
    private String stringBefore;
    private String stringAfter;
    private String pageName;
    private String operationName;
    private long ID;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStringBefore() {
        return stringBefore;
    }

    public void setStringBefore(String stringBefore) {
        this.stringBefore = stringBefore;
    }

    public String getStringAfter() {
        return stringAfter;
    }

    public void setStringAfter(String stringAfter) {
        this.stringAfter = stringAfter;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }
    
    
 
}
