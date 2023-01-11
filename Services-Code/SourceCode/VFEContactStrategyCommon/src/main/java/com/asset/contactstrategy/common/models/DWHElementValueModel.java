/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.common.models;

import java.io.Serializable;

/**
 *
 * @author Zain Al-Abedin
 */
public class DWHElementValueModel implements Serializable, Cloneable {
    
    private long valueId;
    private long elementId;
    private String valueLabel;

    
    public long getValueId() {
        return valueId;
    }

   
    public void setValueId(long valueId) {
        this.valueId = valueId;
    }

    
    public long getElementId() {
        return elementId;
    }

    
    public void setElementId(long elementId) {
        this.elementId = elementId;
    }

    
    public String getValueLabel() {
        return valueLabel;
    }

    
    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }
    
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public boolean equals(Object other) {
//        if (this == other) {
//            return true;
//        }

        if (other == null || !other.getClass().equals(this.getClass())) {
            return false;
        }
        if (this.toString().equals(other.toString())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String delimiter = "~";
        return this.valueId + delimiter + this.elementId + delimiter + this.valueLabel;
    }

    @Override
    public int hashCode() {
        return (valueLabel != null)
                ? (this.getClass().hashCode() + valueLabel.hashCode())
                : super.hashCode();
    }
    
}
