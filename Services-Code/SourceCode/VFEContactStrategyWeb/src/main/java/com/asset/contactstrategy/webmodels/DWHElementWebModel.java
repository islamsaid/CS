/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.webmodels;

import com.asset.contactstrategy.common.models.DWHElementModel;

/**
 *
 * @author Zain Al-Abedin
 */
public class DWHElementWebModel extends DWHElementModel{
    
    private String displayTypeName;

    public DWHElementWebModel()
    {
    }
    public DWHElementWebModel(DWHElementModel base)
    {
        this.setElementId(base.getElementId());
        this.setName(base.getName());
        this.setDataTypeId(base.getDataTypeId());
        this.setDwhName(base.getDwhName());
        this.setFileIndex(base.getFileIndex());
        this.setDescription(base.getDescription());
      
        this.setDisplayName(base.getDisplayName());
        this.setDisplayTypeId(base.getDisplayTypeId());
       
        this.setMultiSelectionValues(base.getMultiSelectionValues());
        this.setDeletedValues(base.getDeletedValues());
        this.setMultiSelectionValuesChanged(base.isMultiSelectionValuesChanged());
        this.setMandatory(base.isMandatory());
    }
    
    public String getDisplayTypeName() {
        return displayTypeName;
    }

    public void setDisplayTypeName(String displayTypeName) {
        this.displayTypeName = displayTypeName;
    }
    
    
    
}
