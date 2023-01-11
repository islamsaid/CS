/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kerollos.asaad
 */
public class FilterModel implements Serializable {

    private long filterId;
    private int groupId;
    private String firstOperand;
    private String secondOperand;
    private LookupModel operatorModel;
    private DWHElementModel dwhElementModel;
    private ArrayList<DWHElementValueModel> selectedElementValues; //to be converted to filter Values
    private ArrayList<DWHFilterValueModel> filterValues;

    public FilterModel() {
        this.dwhElementModel = new DWHElementModel();
        this.operatorModel = new LookupModel();
        this.selectedElementValues = new ArrayList<DWHElementValueModel>();
        this.filterValues = new ArrayList<DWHFilterValueModel>();
        this.firstOperand = new String();
        this.secondOperand = new String();
    }

    public ArrayList<DWHElementValueModel> getSelectedElementValues() {
        return selectedElementValues;
    }

    public void setSelectedElementValues(ArrayList<DWHElementValueModel> selectedElementValues) {
        this.selectedElementValues = selectedElementValues;
    }

    public DWHElementModel getDwhElementModel() {
        return dwhElementModel;
    }

    public void setDwhElementModel(DWHElementModel dWHElementModel) {
        this.dwhElementModel = dWHElementModel;
    }

    public long getFilterId() {
        return filterId;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }

    public ArrayList<DWHFilterValueModel> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(ArrayList<DWHFilterValueModel> filterValues) {
        this.filterValues = filterValues;
    }

    public String getFirstOperand() {
        return firstOperand;
    }

    public void setFirstOperand(String firstOperand) {
        this.firstOperand = firstOperand;
    }

    public String getSecondOperand() {
        return secondOperand;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setSecondOperand(String secondOperand) {
        this.secondOperand = secondOperand;
    }

    public LookupModel getOperatorModel() {
        return operatorModel;
    }

    @XmlElement
    public void setOperatorModel(LookupModel operatorModel) {
        this.operatorModel = operatorModel;
    }

    @Override
    public String toString() {
        return "FilterModel{" + "filterId=" + filterId + ", groupId=" + groupId + ", firstOperand=" + firstOperand + ", secondOperand=" + secondOperand + ", operatorModel=" + operatorModel + ", dwhElementModel=" + dwhElementModel + ", selectedElementValues=" + selectedElementValues + ", filterValues=" + filterValues + '}';
    }
}
