/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.SMSGroupDAO;
import com.asset.contactstrategy.common.exception.CommonException;
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
public class SMSGroupService {

    public boolean checkFileStatus(Connection connection, int fileId) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.checkFileStatus(connection, fileId);
    }

    public void updateFileStatus(Connection connection, int fileId, int fileStatus) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.updateFileStatus(connection, fileId, fileStatus);
    }

    public void updateFilesStatus(Connection connection) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.updateFilesStatus(connection);
    }

    public Vector<SMSGroupModel> getApprovedGroupsList(Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.getApprovedGroupsList(conn);
    }

    public ArrayList<SMSGroupModel> getSmsGroupsList(Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.getGroupsList(conn);
    }

    public void addNewSmsGroup(SMSGroupModel group, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.addNewGroup(group, conn);
    }

    public void addNewEditedSmsGroup(SMSGroupModel group, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.addNewEditedGroup(group, conn);
    }

    public void editSmsGroup(SMSGroupModel group, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.editGroup(group, conn);
    }

    public boolean smsGroupHasChild(SMSGroupModel group, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.groupHasChild(group, conn);
    }

    public SMSGroupModel getSmsGroupParent(int groupEditID, int id, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.getGroupParent(groupEditID, id, conn);
    }

    public void deleteSmsGroup(int groupID, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.deleteGroup(groupID, conn);
    }

    public void deleteSmsGroupVersions(int groupID, Connection conn) throws CommonException {

        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.deleteGroupVersions(groupID, conn);

    }

    public void changeSmsGroupStatus(int groupID, int status, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.changeGroupStatus(groupID, status, conn);
    }

    public void changeSmsGroupEditedID(int groupID, int parentGroupID, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.changeGroupStatus(groupID, parentGroupID, conn);
    }

    public ArrayList<DWHElementModel> loadAllCommercialElements(Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.loadAllCommercialElements(conn);
    }

    public ArrayList<FilterModel> retrieveSmsGroupFilters(int groupId, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.retrieveGroupFilters(groupId, conn);
    }

    public ArrayList<FileModel> retrieveSmsGroupFiles(int groupId, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.retrieveGroupFiles(groupId, conn);
    }

    public void deleteSmsGroupFiltersAndLOV(SMSGroupModel group, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        for (FilterModel el : group.getFilterList()) {
            groupDAO.deleteDwhFiltersListOfValues(el.getFilterId(), conn);
        }
        groupDAO.deleteDWHElementsPerGroup((int) group.getVersionId(), conn);
    }

    public void deleteSmsGroupFiles(int groupId, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        groupDAO.deleteGroupFiles(groupId, conn);
    }

    public SMSGroupModel getSmsGroupById(int groupId, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.getGroupById(groupId, conn);
    }

    public SMSGroupModel getSmsGroupChild(SMSGroupModel parent, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.getGroupChild(parent, conn);
    }

    public SMSGroupModel getSmsGroupByPriority(int groupPriority, String groupName, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.getGroupByPriority(groupPriority, groupName, conn);
    }

    public SMSGroupModel getSmsGroupByName(String groupName, Connection conn) throws CommonException {
        SMSGroupDAO groupDAO = new SMSGroupDAO();
        return groupDAO.getGroupByName(groupName, conn);
    }

}
