/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import com.asset.contactstrategy.common.dao.QueueDAO;
import com.asset.contactstrategy.common.logger.CommonLogger;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Zain Al-Abedin
 */
public class DWHElementModel implements Serializable, Cloneable {

    private long elementId;
    private String name;
    private int dataTypeId;
    private String dwhName;
    private int fileIndex;
    private String description;

    private String displayName;
    private int displayTypeId = -1;

    private ArrayList<DWHElementValueModel> multiSelectionValues = new ArrayList<DWHElementValueModel>();
    private ArrayList<DWHElementValueModel> deletedValues = new ArrayList<DWHElementValueModel>();//for page edit purposes only
    private boolean multiSelectionValuesChanged;//for page edit purposes only
    
    private boolean mandatory;

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public long getElementId() {
        return elementId;
    }

    public void setElementId(long elementId) {
        this.elementId = elementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public String getDwhName() {
        return dwhName;
    }

    public void setDwhName(String dwhName) {
        this.dwhName = dwhName;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getDisplayTypeId() {
        return displayTypeId;
    }

    public void setDisplayTypeId(int displayTypeId) {
        this.displayTypeId = displayTypeId;
    }

    public ArrayList<DWHElementValueModel> getMultiSelectionValues() {
        return multiSelectionValues;
    }

    public void setMultiSelectionValues(ArrayList<DWHElementValueModel> multiSelectionValues) {
        this.multiSelectionValues = multiSelectionValues;
    }

    public ArrayList<DWHElementValueModel> getDeletedValues() {
        return deletedValues;
    }

    public void setDeletedValues(ArrayList<DWHElementValueModel> deletedValues) {
        this.deletedValues = deletedValues;
    }

    public boolean isMultiSelectionValuesChanged() {
        return multiSelectionValuesChanged;
    }

    public void setMultiSelectionValuesChanged(boolean multiSelectionValuesChanged) {
        this.multiSelectionValuesChanged = multiSelectionValuesChanged;
    }

    //=========================Overrides =========================
    public boolean exactEquals(DWHElementModel other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DWHElementModel that = (DWHElementModel) other;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) {
            return false;
        }

        if (this.dataTypeId != other.dataTypeId) {
            return false;
        }
        if (!this.description.equals(other.description)) {
            return false;
        }
        if (!this.displayName.equals(other.displayName)) {
            return false;
        }
        if (!this.dwhName.equals(other.dwhName)) {
            return false;
        }
        if (this.displayTypeId != other.displayTypeId) {
            return false;
        }
        if (this.elementId != other.elementId) {
            return false;
        }
        if (this.fileIndex != other.fileIndex) {
            return false;
        }

        if (this.multiSelectionValues != null && other.multiSelectionValues != null) {
            for (int x = 0; x < this.multiSelectionValues.size(); x++) {
                DWHElementValueModel msv = this.multiSelectionValues.get(x);
                if (!msv.equals(other.getMultiSelectionValues().get(x))) {
                    return false;
                }
            }
        }
        if (!this.name.equals(other.name)) {
            return false;
        }

        return true;
    }

    @Override
    public Object clone() {
        DWHElementModel clonedElement = null;
        try {
            clonedElement = (DWHElementModel) super.clone();
            if (this.multiSelectionValues != null && !this.multiSelectionValues.isEmpty()) {
                clonedElement.setMultiSelectionValues(new ArrayList<DWHElementValueModel>());
                for (DWHElementValueModel value : this.multiSelectionValues) {
                    clonedElement.getMultiSelectionValues().add((DWHElementValueModel) value.clone());
                }
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementModel.class.getName() + " || " + "Failed to clone DWH Element Model", e);
        }
        return clonedElement;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DWHElementModel that = (DWHElementModel) other;

        if (elementId != that.elementId && elementId != 0) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (displayName != null) ? (this.getClass().hashCode() + displayName.hashCode()) : super.hashCode();
    }

    @Override
    public String toString() {
        String delimiter = ";";
        StringBuilder sb = new StringBuilder("");
        sb.append("elementId:");
        sb.append(this.elementId);
        sb.append(delimiter);
        sb.append("displayName:");
        sb.append(this.displayName);
        sb.append(delimiter);
        sb.append("dataTypeId:");
        sb.append(this.dataTypeId);
        sb.append(delimiter);
        sb.append("dwhName:");
        sb.append(this.dwhName);
        sb.append(delimiter);
        sb.append("fileIndex:");
        sb.append(this.fileIndex);
        sb.append(delimiter);
        sb.append("multiSelectionValues:");
        if (this.multiSelectionValues != null && !this.multiSelectionValues.isEmpty()) {
            sb.append("{");
            String innerDelimiter = "";
            for (DWHElementValueModel value : this.multiSelectionValues) {
                sb.append(innerDelimiter);
                sb.append(value.getValueLabel());
                delimiter = ",";
            }
            sb.append("}");
        }
        return sb.toString();
    }

}
