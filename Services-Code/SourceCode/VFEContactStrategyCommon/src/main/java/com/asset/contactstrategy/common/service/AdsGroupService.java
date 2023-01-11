/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.AdsGroupDAO;
import com.asset.contactstrategy.common.dao.CampaignDAO;
import com.asset.contactstrategy.common.dao.SMSGroupDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.AdsGroupModel;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author kerollos.asaad
 */
public class AdsGroupService {

    public boolean checkFileStatus(Connection connection, int fileId) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.checkFileStatus(connection, fileId);
    }

    public void updateFileStatus(Connection connection, int fileId, int fileStatus) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.updateFileStatus(connection, fileId, fileStatus);
    }

    public void updateFilesStatus(Connection connection) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.updateFilesStatus(connection);
    }

    public Vector<AdsGroupModel> getApprovedGroupsList(Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.getApprovedGroupsList(conn);
    }

    public ArrayList<AdsGroupModel> getAdsGroupsList(Connection conn) throws CommonException {
        AdsGroupDAO campDAO = new AdsGroupDAO();
        return campDAO.getAdsGroupsList(conn);
    }

    public void addNewAdsGroup(AdsGroupModel group, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.addNewAdsGroup(group, conn);
    }

    public void addNewEditedAdsGroup(AdsGroupModel group, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.addNewEditedAdsGroup(group, conn);
    }

    public void editAdsGroup(AdsGroupModel group, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.editAdsGroup(group, conn);
    }

    public boolean adsGroupHasParent(int groupId, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.adsGroupHasParent(groupId, conn);
    }

    public AdsGroupModel getAdsGroupParent(int groupEditID, int id, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.getAdsGroupParent(groupEditID, id, conn);
    }

    public void deleteAdsGroup(int groupID, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.deleteAdsGroup(groupID, conn);
    }

    public void deleteAdsGroupVersions(int groupID, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.deleteAdsGroupVersions(groupID, conn);
    }

    public void changeAdsGroupStatus(int groupID, int status, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.changeAdsGroupStatus(groupID, status, conn);
    }

    public void changeAdsGroupEditedID(int groupID, int parentGroupID, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.changeAdsGroupStatus(groupID, parentGroupID, conn);
    }

    public ArrayList<DWHElementModel> loadAllCommercialElements(Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.loadAllCommercialElements(conn);
    }

    public ArrayList<FilterModel> retrieveAdsGroupFilters(int groupId, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.retrieveAdsGroupFilters(groupId, conn);
    }

    public ArrayList<FileModel> retrieveAdsGroupFiles(int groupId, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.retrieveAdsGroupFiles(groupId, conn);
    }

    public void deleteAdsGroupFiltersAndLOV(AdsGroupModel group, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        for (FilterModel el : group.getFilterList()) {
            groupDAO.deleteDwhFiltersListOfValues(el.getFilterId(), conn);
        }
        groupDAO.deleteDWHElementsPerAdsGroup((int) group.getVersionId(), conn);
    }

    public void deleteAdsGroupFiles(int groupId, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        groupDAO.deleteAdsGroupFiles(groupId, conn);
    }

    public AdsGroupModel getAdsGroupById(int groupId, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.getAdsGroupById(groupId, conn);
    }

    public AdsGroupModel getAdsGroupChild(AdsGroupModel parent, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.getAdsGroupChild(parent, conn);
    }

    //Update
    public AdsGroupModel getAdsGroupByPriority(int groupPriority , String groupName, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.getAdsGroupByPriority(groupPriority,groupName, conn);
    }

    public AdsGroupModel getAdsGroupByName(String groupName, Connection conn) throws CommonException {
        AdsGroupDAO groupDAO = new AdsGroupDAO();
        return groupDAO.getAdsGroupByName(groupName, conn);
    }
}
