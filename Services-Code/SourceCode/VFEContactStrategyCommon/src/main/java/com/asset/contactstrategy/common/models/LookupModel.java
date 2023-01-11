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
public class LookupModel {
    
     private Integer id = 0;
    private String lable;
    private String description;
    
    
    public LookupModel() {

    }

    public LookupModel(Integer id, String lable) {
        this.id = id;
        this.lable = lable;
    }

    public LookupModel(Integer id, String lable, String description) {
        this.id = id;
        this.lable = lable;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LookupModel that = (LookupModel) other;
        if (lable != null ? !lable.equals(that.lable) : that.lable != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (lable != null) ? (this.getClass().hashCode() + lable.hashCode()) : super.hashCode();

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "LookupModel{" + "id=" + id + ", lable=" + lable + ", description=" + description + '}';
    }
    
}
