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
public class SystemPropertiesModel {

    private String itemKey;
    private String itemValue;
    private int groupId;

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        String delimiter = ";";
        StringBuilder sb = new StringBuilder("");
        sb.append(this.groupId);
        sb.append(delimiter);
        sb.append(this.itemKey);
        sb.append(delimiter);
        sb.append(this.itemValue);
        sb.append(delimiter);
        return sb.toString();
    }
}
