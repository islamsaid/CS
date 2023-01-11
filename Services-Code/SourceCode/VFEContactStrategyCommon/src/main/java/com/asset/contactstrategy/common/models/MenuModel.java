/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;

/**
 *
 * @author Yomna Naser
 */
public class MenuModel implements Serializable {
    
    private long id;
    private String pageName;
    private String pageURL;
    private int userType;
    private String icoStyleClass;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getPageName() {
        return pageName;
    }
    
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
    
    public String getPageURL() {
        return pageURL;
    }
    
    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }
    
    public int getUserType() {
        return userType;
    }
    
    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getIcoStyleClass() {
        return icoStyleClass;
    }

    public void setIcoStyleClass(String icoStyleClass) {
        this.icoStyleClass = icoStyleClass;
    }
    
    @Override
    public String toString() {
        
        StringBuilder menuStr = new StringBuilder();
        menuStr.append("{").append(this.id).append(",").append(this.pageName).append(",")
                .append(this.pageURL).append(",").append(this.userType).append(",").append(this.icoStyleClass).append("}");
        return menuStr.toString();       
    }
    
}
