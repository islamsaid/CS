/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class GroupsParentModel implements Serializable {

    private int status;
    private String filterQuery;
    private ArrayList<FileModel> filesModel = new ArrayList<FileModel>();
    private ArrayList<FilterModel> filterList = new ArrayList<FilterModel>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public ArrayList<FileModel> getFilesModel() {
        return filesModel;
    }

    public void setFilesModel(ArrayList<FileModel> filesModel) {
        this.filesModel = filesModel;
    }

    public ArrayList<FilterModel> getFilterList() {
        return filterList;
    }

    public void setFilterList(ArrayList<FilterModel> filterList) {
        this.filterList = filterList;
    }

}
