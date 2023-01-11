/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kerollos.asaad
 */
@XmlRootElement
public class DWHFilterValueModel implements Serializable {

    private long filterValueId;
    private long filterId;
    private long valueId;
    private String valueLabel;

    public long getFilterId() {
        return filterId;
    }

    @XmlElement
    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }

    public long getFilterValueId() {
        return filterValueId;
    }

    @XmlElement
    public void setFilterValueId(long filterValueId) {
        this.filterValueId = filterValueId;
    }

    public long getValueId() {
        return valueId;
    }

    @XmlElement
    public void setValueId(long valueId) {
        this.valueId = valueId;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    @XmlElement
    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof DWHFilterValueModel) && (valueLabel != null)
                ? valueLabel.equals(((DWHFilterValueModel) other).valueLabel)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (valueLabel != null)
                ? (this.getClass().hashCode() + valueLabel.hashCode())
                : super.hashCode();
    }

    @Override
    public String toString() {
        return "DWHFilterValueModel{" + "filterValueId=" + filterValueId + ", filterId=" + filterId + ", valueId=" + valueId + '}';
    }

}
