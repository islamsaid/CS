/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.webmodels;

import com.asset.contactstrategy.common.models.UserModel;

/**
 *
 * @author Yomna Naser
 */
public class UserWebModel extends UserModel{
    private String userTypeValue;

    public String getUserTypeValue() {
        return userTypeValue;
    }

    public void setUserTypeValue(String userTypeValue) {
        this.userTypeValue = userTypeValue;
    }

    @Override
    public String toString() {
        return super.getId()+","+super.getUsername()+","+super.getUserType()+"."; //To change body of generated methods, choose Tools | Templates.
    }
}
