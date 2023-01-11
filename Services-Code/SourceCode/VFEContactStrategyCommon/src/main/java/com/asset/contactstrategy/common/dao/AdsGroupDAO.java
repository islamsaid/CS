/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.AdsGroupModel;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import com.asset.contactstrategy.common.models.DWHFilterValueModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.utils.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author kerollos.asaad
 */
public class AdsGroupDAO {

    public boolean checkFileStatus(Connection connection, int fileId) throws CommonException {
        long startime = System.currentTimeMillis();
        boolean valid = false;
        //  CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting checkFileStatus");
        PreparedStatement pstat = null;
        PreparedStatement updatePstat = null;
        ResultSet rs = null;
        String sql = "";
        String updateSql = "";
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUP_FILES.TABLE_NAME
                    + " Where " + DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_FILE_ID + " =? AND "
                    + DBStruct.VFE_CS_ADS_GROUP_FILES.FILE_STATUS_ID + "<>?  FOR UPDATE";

            // CommonLogger.businessLogger.debug(SMSGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            pstat = connection.prepareStatement(sql.toString());
            pstat.setInt(1, fileId);
            pstat.setInt(2, GeneralConstants.FILE_STATUS_PROCESSING_VALUE);
            rs = pstat.executeQuery();
            while (rs.next()) {
                valid = true;
                updateSql = "Update " + DBStruct.VFE_CS_ADS_GROUP_FILES.TABLE_NAME
                        + " SET " + DBStruct.VFE_CS_ADS_GROUP_FILES.FILE_STATUS_ID
                        + " =? WHERE " + DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_FILE_ID + " =?";
                updatePstat = connection.prepareCall(updateSql);
                updatePstat.setInt(1, GeneralConstants.FILE_STATUS_PROCESSING_VALUE);
                updatePstat.setInt(2, fileId);
                updatePstat.executeUpdate();
                connection.commit();
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || fileID=" + fileId + " || fileStatus=" + valid);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File Stats")
                    .put(GeneralConstants.StructuredLogKeys.FILE_ID, fileId)
                    .put(GeneralConstants.StructuredLogKeys.STATUS, valid).build());
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || " + "End [checkFileStatus] in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End File Status")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());

        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [checkFileStatus]" + ex, ex);

            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [checkFileStatus]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, pstat);
            DataSourceManger.closeDBResources(null, updatePstat); // eslam.ahmed | 5-5-2020
        }
        return valid;
    }

    public void updateFileStatus(Connection connection, int fileId, int fileStatus) throws CommonException {
        // CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting updateFileStatus");
        long startime = System.currentTimeMillis();
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = " Update " + DBStruct.VFE_CS_ADS_GROUP_FILES.TABLE_NAME + " SET "
                    + DBStruct.VFE_CS_ADS_GROUP_FILES.FILE_STATUS_ID + " =?  where "
                    + DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_FILE_ID + " = ? ";
            pstat = connection.prepareStatement(sql.toString());
            pstat.setInt(1, fileStatus);
            pstat.setInt(2, fileId);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || fileID=" + fileId + " || fileStatus=" + fileStatus);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateFileStatus Stats")
                    .put(GeneralConstants.StructuredLogKeys.FILE_ID, fileId)
                    .put(GeneralConstants.StructuredLogKeys.STATUS, fileStatus).build());
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || " + "Ended updateFileStatus in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End updateFileStatus")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateFileStatus] Failed Campaigns File Status" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->   for [updateFileStatus] Failed Campaigns File Status" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstat);
        }
    }

    public void updateFilesStatus(Connection connection) throws CommonException {
        // CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting updateFilesStatus");
        long startime = System.currentTimeMillis();
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = " Update " + DBStruct.VFE_CS_ADS_GROUP_FILES.TABLE_NAME + " SET "
                    + DBStruct.VFE_CS_ADS_GROUP_FILES.FILE_STATUS_ID + " = ? ";
            pstat = connection.prepareStatement(sql.toString());
            pstat.setInt(1, GeneralConstants.FILE_STATUS_FINISHED_VALUE);
            pstat.executeUpdate();

//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || " + "Ended updateFilesStatus in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End File Status")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateFilesStatus] Failed Campaigns Files Status", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->   for [updateFilesStatus] Failed Campaigns Files Status", e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstat);
        }
    }

    /**
     * Called for getting all Approved VFE_CS_SMS_GROUPS.
     *
     * @param conn
     * @return
     * @throws CommonException
     */
    public Vector<AdsGroupModel> getApprovedGroupsList(Connection conn) throws CommonException {
//        CommonLogger.businessLogger.info(SMSGroupDAO.class.getName() + " || " + "Start [getApprovedGroupsList]...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "start getApprovedGroupList").build());
        Vector<AdsGroupModel> ret = new Vector<>();
        long startime = System.currentTimeMillis();
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME
                    + " Where " + DBStruct.VFE_CS_ADS_GROUPS.STATUS + " =? "
                    + " ORDER BY " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY + " desc ";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedGroupList Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql.toString());
            pstat.setInt(1, GeneralConstants.STATUS_APPROVED_VALUE);
            rs = pstat.executeQuery();
            while (rs.next()) {
                AdsGroupModel tmpGroup = new AdsGroupModel();
                tmpGroup.setVersionId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID));
                tmpGroup.setFilterQuery(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY));
                tmpGroup.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID));
                tmpGroup.setGroupName(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME));
                tmpGroup.setGroupPriority(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY));
                tmpGroup.setStatus(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.STATUS));
                tmpGroup.setGroupType(SystemLookups.GROUP_TYPES.get((int) rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID)));
                ret.add(tmpGroup);
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || groupSize=" + ret.size());
//            CommonLogger.businessLogger.info(SMSGroupDAO.class.getName() + " || " + "End [getApprovedGroupsList] in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedGroup List stats")
                    .put(GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, ret.size()).build());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getApprovedGroupList")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(SMSGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [getApprovedGroupsList]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(SMSGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [getApprovedGroupsList]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, pstat);

        }
        return ret;
    }

    /**
     * Called for getting all the defined VFE_CS_ADS_GROUPS.
     *
     * @param conn
     * @return
     * @throws CommonException
     */
    public ArrayList<AdsGroupModel> getAdsGroupsList(Connection conn) throws CommonException {
        String methodName = "getCampGroupsList";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting " + methodName).build());
        ArrayList<AdsGroupModel> ret = new ArrayList<>();
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " ORDER BY " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID;
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAdsGroupList Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql.toString());
            rs = pstat.executeQuery();
            while (rs.next()) {
                AdsGroupModel tmpGroup = new AdsGroupModel();
                tmpGroup.setVersionId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID));
                tmpGroup.setDailyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD));
                tmpGroup.setDonotContact(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT));
                tmpGroup.setFilterQuery(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY));
                tmpGroup.setGroupDescription(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION));
                tmpGroup.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID));
                tmpGroup.setGroupName(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME));
                tmpGroup.setGroupPriority(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY));
                tmpGroup.setGuardPeriod(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD));
                tmpGroup.setMonthlyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD));
                tmpGroup.setStatus(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.STATUS));
                tmpGroup.setWeeklyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD));
                tmpGroup.setCreatedBy(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY));
                tmpGroup.setGroupType(SystemLookups.GROUP_TYPES.get((int) rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID)));
                ret.add(tmpGroup);
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || groupSize=" + ret.size());
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUPS_LIST_SIZE, ret.size()).build());
            return ret;
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * Called for adding new group information.
     *
     * @param group
     * @param conn
     * @throws CommonException
     */
    public void addNewAdsGroup(AdsGroupModel group, Connection conn) throws CommonException {
        String methodName = "addNewAdsGroup";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = "INSERT INTO " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + "(" + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + ", "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME + ", " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION + ", " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY + ", "
                    + DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT + ", " + DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD + ", " + DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD + ", "
                    + DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD + ", " + DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD + ", " + DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY + ", "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + ", " + DBStruct.VFE_CS_ADS_GROUPS.STATUS + ", " + DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY + ", " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID + ")"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "addNewGroup Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            group.setVersionId((int) Utility.getNextId(DBStruct.VFE_CS_ADS_GROUPS.VFE_CS_ADS_GROUPS_SEQ, conn));
            group.setGroupId((int) Utility.getNextId(DBStruct.VFE_CS_ADS_GROUPS.VFE_CS_ADS_GROUPS_SEC_SEQ, conn));

            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, group.getVersionId());
            pstat.setString(2, group.getGroupName());
            pstat.setString(3, group.getGroupDescription());
            pstat.setInt(4, group.getGroupPriority());
            pstat.setInt(5, group.getDonotContact());
            pstat.setInt(6, group.getDailyThreshold());
            pstat.setInt(7, group.getWeeklyThreshold());
            pstat.setInt(8, group.getMonthlyThreshold());
            pstat.setInt(9, group.getGuardPeriod());
            if (group.getGroupType().getId() == Defines.GROUP_TYPES.UPLOADED.getId()) {
                group.setFilterQuery(" 1=1");
            }
            pstat.setString(10, group.getFilterQuery());
            pstat.setInt(11, group.getGroupId());
            pstat.setInt(12, group.getStatus());
            pstat.setInt(13, group.getCreatedBy());
            pstat.setInt(14, group.getGroupType().getId());
            pstat.executeUpdate();
            if (group.getGroupType().getId() == Defines.GROUP_TYPES.CRITERIA_BASED.getId()) {
                for (int i = 0; i < group.getFilterList().size(); i++) {
                    insertDWHElementsPerAdsGroup(group.getVersionId(), group.getFilterList().get(i), conn);
                }
            } else {
                for (int i = 0; i < group.getFilesModel().size(); i++) {
                    int GroupFileGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_ADS_GROUP_FILES.VFE_CS_ADS_GROUP_FILE_ID_SEQ, conn);
                    String filesQuery = this.buildInsertCampGroupFiles();
                    PreparedStatement preparedStatement = conn.prepareStatement(filesQuery);
                    preparedStatement.setInt(1, group.getVersionId());
                    preparedStatement.setInt(2, GroupFileGeneratedKey);
                    preparedStatement.setString(3, group.getFilesModel().get(i).getFileData());
                    preparedStatement.setString(4, group.getFilesModel().get(i).getFileName());
                    // Set the newly created file with status CREATED.
                    preparedStatement.setInt(5, SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.CREATED).getId());//2595 id was put in defines
                    preparedStatement.executeUpdate();

                    preparedStatement.close(); // eslam.ahmed | 5-5-2020
                }
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || addedNewGroup with ID=" + group.getVersionId());
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, group.getVersionId()).build());

        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void addNewEditedAdsGroup(AdsGroupModel group, Connection conn) throws CommonException {
        String methodName = "addNewEditedAdsGroup";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = "INSERT INTO " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + "(" + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + ", "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME + ", " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION + ", " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY + ", "
                    + DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT + ", " + DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD + ", " + DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD + ", "
                    + DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD + ", " + DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD + ", " + DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY + ", "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + ", " + DBStruct.VFE_CS_ADS_GROUPS.STATUS + ", " + DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY + ", " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID + ")"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "addNewEditedAdsGroup SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            group.setVersionId((int) Utility.getNextId(DBStruct.VFE_CS_ADS_GROUPS.VFE_CS_ADS_GROUPS_SEQ, conn));

            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, group.getVersionId());
            pstat.setString(2, group.getGroupName());
            pstat.setString(3, group.getGroupDescription());
            pstat.setInt(4, group.getGroupPriority());
            pstat.setInt(5, group.getDonotContact());
            pstat.setInt(6, group.getDailyThreshold());
            pstat.setInt(7, group.getWeeklyThreshold());
            pstat.setInt(8, group.getMonthlyThreshold());
            pstat.setInt(9, group.getGuardPeriod());
            if (group.getGroupType().getId() == Defines.GROUP_TYPES.UPLOADED.getId()) {
                group.setFilterQuery(" 1=1");
            }
            pstat.setString(10, group.getFilterQuery());
            pstat.setInt(11, group.getGroupId());
            pstat.setInt(12, group.getStatus());
            pstat.setInt(13, group.getCreatedBy());
            pstat.setInt(14, group.getGroupType().getId());
            pstat.executeUpdate();
            if (group.getGroupType().getId() == Defines.GROUP_TYPES.CRITERIA_BASED.getId()) {
                for (int i = 0; i < group.getFilterList().size(); i++) {
                    insertDWHElementsPerAdsGroup(group.getVersionId(), group.getFilterList().get(i), conn);
                }
            } else {
                for (int i = 0; i < group.getFilesModel().size(); i++) {
                    int GroupFileGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_ADS_GROUP_FILES.VFE_CS_ADS_GROUP_FILE_ID_SEQ, conn);
                    String filesQuery = this.buildInsertCampGroupFiles();
                    PreparedStatement preparedStatement = conn.prepareStatement(filesQuery);
                    preparedStatement.setInt(1, group.getVersionId());
                    preparedStatement.setInt(2, GroupFileGeneratedKey);
                    preparedStatement.setString(3, group.getFilesModel().get(i).getFileData());
                    preparedStatement.setString(4, group.getFilesModel().get(i).getFileName());
                    // Set the newly created file with status CREATED.
                    preparedStatement.setInt(5, SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.CREATED).getId());//2595 id was put in defines
                    preparedStatement.executeUpdate();

                    preparedStatement.close(); // eslam.ahmed | 5-5-2020
                }
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || add edited version of group with ID " + group.getVersionId() + " which has parent " + group.getGroupId());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Add Edited Version, which has parent")
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, group.getVersionId())
                    .put(GeneralConstants.StructuredLogKeys.GROUP_NAME, group.getGroupId()).build());
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     *
     * @param group
     * @param conn
     * @throws CommonException
     */
    public void editAdsGroup(AdsGroupModel group, Connection conn) throws CommonException {
        String methodName = "editAdsGroup";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = "UPDATE " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " SET " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME + " = ?, "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION + " = ?, " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY + " = ?, "
                    + DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT + " = ?, " + DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD + " = ?, "
                    + DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD + " = ?, " + DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD + " = ?, "
                    + DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD + " = ?, " + DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY + " = ?, "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + " = ?, " + DBStruct.VFE_CS_ADS_GROUPS.STATUS + " = ?, "
                    + DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY + " = ? WHERE " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editAdsGroup SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());

            pstat = conn.prepareStatement(sql);
            pstat.setString(1, group.getGroupName());
            pstat.setString(2, group.getGroupDescription());
            pstat.setInt(3, group.getGroupPriority());
            pstat.setInt(4, group.getDonotContact());
            pstat.setInt(5, group.getDailyThreshold());
            pstat.setInt(6, group.getWeeklyThreshold());
            pstat.setInt(7, group.getMonthlyThreshold());
            pstat.setInt(8, group.getGuardPeriod());
            if (group.getGroupType().getId() == Defines.GROUP_TYPES.UPLOADED.getId()) {
                group.setFilterQuery("1=1");
            }
            pstat.setString(9, group.getFilterQuery());
            pstat.setInt(10, group.getGroupId());
            pstat.setInt(11, group.getStatus());
            pstat.setInt(12, group.getCreatedBy());
            pstat.setInt(13, group.getVersionId());
            pstat.executeUpdate();
            ///////////////////////

            if (group.getGroupType().getId() == Defines.GROUP_TYPES.UPLOADED.getId()) {
                this.deleteAdsGroupFiles(group.getVersionId(), conn);
                for (int i = 0; i < group.getFilesModel().size(); i++) {
                    int GroupFileGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_ADS_GROUP_FILES.VFE_CS_ADS_GROUP_FILE_ID_SEQ, conn);
                    String filesQuery = this.buildInsertCampGroupFiles();
                    PreparedStatement preparedStatement = conn.prepareStatement(filesQuery);
                    preparedStatement.setInt(1, group.getVersionId());
                    preparedStatement.setInt(2, GroupFileGeneratedKey);
                    preparedStatement.setString(3, group.getFilesModel().get(i).getFileData());
                    preparedStatement.setString(4, group.getFilesModel().get(i).getFileName());
                    // Set the newly created file status with CREATED.
                    preparedStatement.setInt(5, SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.CREATED).getId());//2595 id was put in defines
                    preparedStatement.executeUpdate();

                    preparedStatement.close(); // eslam.ahmed | 5-5-2020
                }
            } else {
                ArrayList<FilterModel> oldFilters = new ArrayList<FilterModel>();
                oldFilters = this.retrieveAdsGroupFilters(group.getVersionId(), conn);
                for (FilterModel filterModelDwh : oldFilters) {
                    this.deleteDwhFiltersListOfValues(filterModelDwh.getFilterId(), conn);
                }
                this.deleteDWHElementsPerAdsGroup((int) group.getVersionId(), conn);
                for (FilterModel dwhFilterModel : group.getFilterList()) {
                    this.insertDWHElementsPerAdsGroup((int) group.getVersionId(), dwhFilterModel, conn);
                }
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || edit group with ID=" + group.getVersionId());
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, group.getVersionId()).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * This method is used to ensure whether there's a child(s) (editing
     * versions, deleting requests) for a certain group or not.
     *
     * @param groupID
     * @param conn
     * @return
     * @throws CommonException
     */
    public boolean adsGroupHasParent(int groupID, Connection conn) throws CommonException {
        String methodName = "adsGroupHasParent";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " WHERE "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + " = ? AND " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + " != ?";
//            CommonLogger.businessLogger.debug(SMSGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "adsGroupHasParent SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, groupID);
            pstat.setInt(2, groupID);
            rs = pstat.executeQuery();
//            CommonLogger.businessLogger.debug(SMSGroupDAO.class.getName() + " || " + "End " + methodName + "...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
            return (rs.next());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * Called to retrieve the group by it's ID.
     *
     * @param groupId
     * @param conn
     * @return
     * @throws CommonException
     */
    public AdsGroupModel getAdsGroupById(int groupId, Connection conn) throws CommonException {
        String methodName = "getAdsGroupById";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        AdsGroupModel ret = new AdsGroupModel();
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAdsGroupById SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, groupId);
            rs = pstat.executeQuery();
            if (rs.next()) {
                ret.setVersionId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID));
                ret.setCreatedBy(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY));
                ret.setDailyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD));
                ret.setDonotContact(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT));
                ret.setFilterQuery(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY));
                ret.setGroupDescription(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION));
                ret.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID));
                ret.setGroupName(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME));
                ret.setGroupPriority(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY));
                ret.setGuardPeriod(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD));
                ret.setMonthlyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD));
                ret.setStatus(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.STATUS));
                ret.setWeeklyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD));
                ret.setGroupType(SystemLookups.GROUP_TYPES.get((int) rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID)));
                if (ret.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    ret.setFilterList(this.retrieveAdsGroupFilters(ret.getVersionId(), conn));
                } else {
                    ret.setFilesModel(this.retrieveAdsGroupFiles(ret.getVersionId(), conn));
                }
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || edit group with ID=" + groupId);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupId).build());
            return ret;
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * Called to retrieve the approved version of a specific group.
     *
     * @param groupEditID
     * @param conn
     * @return
     * @throws CommonException
     */
    public AdsGroupModel getAdsGroupParent(int groupEditID, int id, Connection conn) throws CommonException {
        String methodName = "getCampGroupParent";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        AdsGroupModel ret = new AdsGroupModel();
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + " != ? AND " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + " = ? AND " + DBStruct.VFE_CS_ADS_GROUPS.STATUS + " <> " + GeneralConstants.STATUS_PENDING_VALUE;
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAdsGroupParent SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, id);
            pstat.setInt(2, groupEditID);
            rs = pstat.executeQuery();
            ret.setVersionId(-1);
            if (rs.next()) {
                ret.setVersionId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID));
                ret.setCreatedBy(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY));
                ret.setDailyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD));
                ret.setDonotContact(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT));
                ret.setFilterQuery(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY));
                ret.setGroupDescription(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION));
                ret.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID));
                ret.setGroupName(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME));
                ret.setGroupPriority(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY));
                ret.setGuardPeriod(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD));
                ret.setMonthlyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD));
                ret.setStatus(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.STATUS));
                ret.setWeeklyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD));
                ret.setGroupType(SystemLookups.GROUP_TYPES.get((int) rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID)));
                if (ret.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    ret.setFilterList(this.retrieveAdsGroupFilters(ret.getVersionId(), conn));
                } else {
                    ret.setFilesModel(this.retrieveAdsGroupFiles(ret.getVersionId(), conn));
                }
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || get group with ID=" + id);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, id).build());
            return ret;
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * deletes specific group.
     *
     * @param groupID
     * @param conn
     * @throws CommonException
     */
    public void deleteAdsGroup(int groupID, Connection conn) throws CommonException {
        String methodName = "deleteCampGroup";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = "DELETE FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAdsGroup SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, groupID);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || delete group with ID=" + groupID);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupID).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * This method used to delete group and all it's versions in
     * VFE_CS_ADS_GROUPS database table.
     *
     * @param groupID
     * @param conn
     * @throws CommonException
     */
    public void deleteAdsGroupVersions(int groupID, Connection conn) throws CommonException {
        String methodName = "deleteCampGroupVersions";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = "DELETE  FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + " = ? or "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAdsGroupVersions SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, groupID);
            pstat.setInt(2, groupID);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || delete group versions with ID=" + groupID);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupID).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    private void insertDWHElementsPerAdsGroup(int groupId, FilterModel filterModel, Connection conn) throws CommonException {
        String methodName = "insertDWHElementsPerCampGroup";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build()
        );
        PreparedStatement st = null;
        try {
            int wadmDwhElementFilterGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.ELEMENT_FILTER_SEQ, conn);
            String admDwhElementFilterQuery = buildInsertDwhElementsQuery(wadmDwhElementFilterGeneratedKey, groupId, filterModel);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + admDwhElementFilterQuery + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertDwhElementsPerAdsGroup SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, admDwhElementFilterQuery).build());
            st = conn.prepareStatement(admDwhElementFilterQuery);
            st.setString(1, filterModel.getFirstOperand());
            st.executeUpdate();
            for (int i = 0; i < filterModel.getFilterValues().size(); i++) {
                if (!filterModel.getFilterValues().isEmpty()) {
                    insertListOfValuesPerDwhElement(wadmDwhElementFilterGeneratedKey, filterModel.getFilterValues().get(i), conn);
                }
            }
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    private void insertListOfValuesPerDwhElement(int filterID, DWHFilterValueModel dwhFilterValueModel, Connection conn) throws CommonException {
        String methodName = "insertListOfValuesPerDwhElement";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        Statement st = null;
        try {
            int dwhElementValueGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.ELEMENT_FILTER_LOV_SEQ, conn);
            String listOfValuesPerDwhElementQuery = buildInsertListOfValuesPerDwhElementQuery(dwhElementValueGeneratedKey, filterID, dwhFilterValueModel);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + listOfValuesPerDwhElementQuery + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertListOfValuesPerDwhElement SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, listOfValuesPerDwhElementQuery).build());
            st = conn.prepareStatement(listOfValuesPerDwhElementQuery);
            st.executeUpdate(listOfValuesPerDwhElementQuery);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * Called for changing group status.
     *
     * @param groupID
     * @param status
     * @param conn
     * @throws CommonException
     */
    public void changeAdsGroupStatus(int groupID, int status, Connection conn) throws CommonException {
        String methodName = "changeCampGroupStatus";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = "UPDATE " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " SET " + DBStruct.VFE_CS_ADS_GROUPS.STATUS + " = ? WHERE " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeAdsGroupStatus SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, status);
            pstat.setInt(2, groupID);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || change group status with ID=" + groupID + " with status=" + status);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupID)
                    .put(GeneralConstants.StructuredLogKeys.STATUS, status).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public ArrayList<FilterModel> retrieveAdsGroupFilters(int groupId, Connection conn) throws CommonException {
        String methodName = "retrieveCampGroupFilters";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        Statement st = null;
        ResultSet rs = null;
        ArrayList<FilterModel> ret = null;
        try {
            String query = buildFiltersRetrievalQuery(groupId);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + query + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveAdsGroupFilters SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = conn.prepareStatement(query);
            rs = st.executeQuery(query);
            ret = new ArrayList<FilterModel>();
            HashMap<Long, FilterModel> tempFilters = new HashMap<Long, FilterModel>();
            while (rs.next()) {
                FilterModel currentFilter = tempFilters.get(rs.getLong(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FILTER_ID));
                if (currentFilter == null) {

                    currentFilter = new FilterModel();
                    currentFilter.setFilterId(rs.getInt(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FILTER_ID));
                    currentFilter.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.GROUP_ID));
                    currentFilter.setFirstOperand(rs.getString(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FIRST_OPERAND));
                    currentFilter.setSecondOperand(rs.getString(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.SECOND_OPERAND));
                    Integer operatorId = null;
                    LookupModel filterOperator = null;

                    if (rs.getObject(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.OPERATOR_ID) != null) {
                        operatorId = rs.getInt(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.OPERATOR_ID);
                        filterOperator = SystemLookups.OPERATORS.get(operatorId);
                    }
                    if (filterOperator != null) {
                        currentFilter.setOperatorModel(filterOperator);
                    }
                    DWHElementModel element = new DWHElementModel();
                    element.setElementId(rs.getInt(DBStruct.DWH_ELEMENTS.ELEMENT_ID));
                    element.setName(rs.getString(DBStruct.DWH_ELEMENTS.NAME));
                    element.setDataTypeId(rs.getInt(DBStruct.DWH_ELEMENTS.DATA_TYPE));
                    element.setDwhName(rs.getString(DBStruct.DWH_ELEMENTS.COLUMN_NAME));
                    element.setFileIndex(rs.getInt(DBStruct.DWH_ELEMENTS.FILE_INDEX));
                    element.setDescription(rs.getString(DBStruct.DWH_ELEMENTS.DESCRIPTION));
                    //element.setDisplayToCom(rs.getBoolean(DBStruct.DWH_ELEMENTS.DISPLAY_COMM));
                    element.setDisplayTypeId(rs.getInt(DBStruct.DWH_ELEMENTS.DISPLAY_TYPE));
                    element.setDisplayName(rs.getString(DBStruct.DWH_ELEMENTS.DISPLAY_NAME));

                    currentFilter.setDwhElementModel(element);
                    if (rs.getObject(DBStruct.DWH_ELEMENT_LOV.VALUE_ID) != null) {
                        DWHFilterValueModel filterValue = new DWHFilterValueModel();
                        filterValue.setFilterId(currentFilter.getFilterId());
                        filterValue.setFilterValueId(rs.getInt(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_VALUE_ID));
                        filterValue.setValueId(rs.getInt(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.VALUE_ID));
                        if (currentFilter.getDwhElementModel().getElementId() == Defines.DWH_ELEMENTS.GOVERNMENT) {
                            String govLabel = SystemLookups.GOVERNMENTS.get((int) filterValue.getValueId()).getLable();
                            filterValue.setValueLabel(govLabel);
                        } else if (currentFilter.getDwhElementModel().getElementId() == Defines.DWH_ELEMENTS.CUSTOMER_TYPE) {
                            String customerTypeLbl = SystemLookups.CUSTOMER_TYPES.get((int) filterValue.getValueId()).getLable();
                            filterValue.setValueLabel(customerTypeLbl);
                        } else {
                            filterValue.setValueLabel(rs.getString(DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL));
                        }
                        currentFilter.getFilterValues().add(filterValue);
                    }
                    tempFilters.put(currentFilter.getFilterId(), currentFilter);
                    ret.add(currentFilter);
                } else {
                    DWHFilterValueModel filterValue = new DWHFilterValueModel();
                    filterValue.setFilterId(currentFilter.getFilterId());
                    filterValue.setFilterValueId(rs.getInt(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_VALUE_ID));
                    filterValue.setValueId(rs.getInt(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.VALUE_ID));
                    if (currentFilter.getDwhElementModel().getElementId() == Defines.DWH_ELEMENTS.GOVERNMENT) {
                        String govLabel = SystemLookups.GOVERNMENTS.get((int) filterValue.getValueId()).getLable();
                        filterValue.setValueLabel(govLabel);
                    } else if (currentFilter.getDwhElementModel().getElementId() == Defines.DWH_ELEMENTS.CUSTOMER_TYPE) {
                        String customerTypeLbl = SystemLookups.CUSTOMER_TYPES.get((int) filterValue.getValueId()).getLable();
                        filterValue.setValueLabel(customerTypeLbl);
                    } else {
                        filterValue.setValueLabel(rs.getString(DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL));
                    }
                    currentFilter.getFilterValues().add(filterValue);
                }

            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || retrieve group filter with ID=" + groupId);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName)
                    .put(GeneralConstants.StructuredLogKeys.GROUP_ID, groupId).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
        return ret;
    }

    /**
     *
     * @param groupID
     * @param parentGroupID
     * @param conn
     * @throws CommonException
     */
    public void changeAdsGroupEditedID(int groupID, int parentGroupID, Connection conn) throws CommonException {
        String methodName = "changeCampGroupEditedID";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = "UPDATE " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " SET " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + " = ? WHERE " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "changeAdsGroupEditedID SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, groupID);
            pstat.setInt(2, parentGroupID);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public ArrayList<DWHElementModel> loadAllCommercialElements(Connection conn) throws CommonException {
        String methodName = "loadAllCommercialElements";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        Statement st = null;
        ResultSet rs = null;
        ArrayList<DWHElementModel> allElements = null;
        HashMap<Long, DWHElementModel> temp = null;
        try {
            allElements = new ArrayList<DWHElementModel>();
            String sql = buildRetreieveCommercialElementsQuery();
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadAllComercialElements SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            temp = new HashMap<Long, DWHElementModel>();
            while (rs.next()) {
                DWHElementModel currentElement = new DWHElementModel();
                currentElement.setElementId(rs.getLong(DBStruct.DWH_ELEMENTS.ELEMENT_ID));
                currentElement.setName(rs.getString(DBStruct.DWH_ELEMENTS.NAME));
                currentElement.setDataTypeId(rs.getInt(DBStruct.DWH_ELEMENTS.DATA_TYPE));
                currentElement.setDwhName(rs.getString(DBStruct.DWH_ELEMENTS.COLUMN_NAME));
                currentElement.setFileIndex(rs.getInt(DBStruct.DWH_ELEMENTS.FILE_INDEX));
                currentElement.setDescription(rs.getString(DBStruct.DWH_ELEMENTS.DESCRIPTION));
                //currentElement.setDisplayToCom(rs.getBoolean(DBStruct.DWH_ELEMENTS.DISPLAY_COMM));
//                if (currentElement.isDisplayToCom()) {
                currentElement.setDisplayTypeId(rs.getInt(DBStruct.DWH_ELEMENTS.DISPLAY_TYPE));
                currentElement.setDisplayName(rs.getString(DBStruct.DWH_ELEMENTS.DISPLAY_NAME));
                if (currentElement.getDisplayTypeId() == Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION) {
                    DWHElementValueModel multiSelectionValue = new DWHElementValueModel();
                    multiSelectionValue.setElementId(rs.getLong(DBStruct.DWH_ELEMENTS.ELEMENT_ID));
                    multiSelectionValue.setValueId(rs.getInt(DBStruct.DWH_ELEMENT_LOV.VALUE_ID));
                    multiSelectionValue.setValueLabel(rs.getString(DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL));
                    DWHElementModel tempElement = temp.get(currentElement.getElementId());
                    if (tempElement != null) {
                        ArrayList<DWHElementValueModel> multiSelectionValues = tempElement.getMultiSelectionValues();
                        if (multiSelectionValues == null) {
                            multiSelectionValues = new ArrayList<DWHElementValueModel>();
                            temp.get(currentElement.getElementId()).setMultiSelectionValues(multiSelectionValues);
                        }
                        multiSelectionValues.add(multiSelectionValue);
                    } else {
                        tempElement = currentElement;
                        tempElement.setMultiSelectionValues(new ArrayList<DWHElementValueModel>());
                        tempElement.getMultiSelectionValues().add(multiSelectionValue);
                        temp.put(tempElement.getElementId(), tempElement);
                    }
                }
//                }
                DWHElementModel tempElement = temp.get(currentElement.getElementId());
                // This is a just in case check elemntIds are not supposed to be duplicated unless in the case of multi-selection
                if (tempElement == null) {
                    tempElement = currentElement;
                    temp.put(tempElement.getElementId(), tempElement);
                }
            }
            if (temp != null && !temp.isEmpty()) {
                allElements = new ArrayList<DWHElementModel>();
                Utility.mapToArrayList(temp, allElements);
                temp.clear();
                temp = null;
            }
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
        return allElements;
    }

    public void deleteDwhFiltersListOfValues(long filterId, Connection conn) throws CommonException {
        String methodName = "deleteDwhFiltersListOfValues";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append("DELETE FROM ").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.TABLE_NAME);
            query.append(" WHERE ").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_ID).append(" = ?");
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + query.toString() + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteDwhFiltersListOfValues SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstat = conn.prepareStatement(query.toString());
            pstat.setLong(1, filterId);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void deleteAdsGroupFiles(int groupId, Connection conn) throws CommonException {
        String methodName = "deleteCampGroupFiles";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append("DELETE FROM ").append(DBStruct.VFE_CS_ADS_GROUP_FILES.TABLE_NAME);
            query.append(" WHERE ").append(DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_ID).append(" = ?");
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + query.toString() + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAdsGroupFiles SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstat = conn.prepareStatement(query.toString());
            pstat.setLong(1, groupId);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void deleteDWHElementsPerAdsGroup(int filterId, Connection conn) throws CommonException {
        String methodName = "deleteDWHElementsPerCampGroup";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstmt = null;
        String query = new String();
        try {
            query = "DELETE FROM " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.GROUP_ID + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + query + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteDWHElementsPerAdsGroup SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, filterId);
            pstmt.executeUpdate();
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void insertDwhElements(int groupId, FilterModel filterModel, Connection conn) throws CommonException {
        String methodName = "insertDwhElements";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start ").build());
        PreparedStatement pstmt = null;
        StringBuilder query = new StringBuilder();
        int dwhElementValueGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.ELEMENT_FILTER_SEQ, conn);
        filterModel.setFilterId(dwhElementValueGeneratedKey);
        try {
            query.append("INSERT INTO ").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.TABLE_NAME);
            query.append(" (").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.ELEMENT_ID);
            query.append(", ").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FILTER_ID);
            query.append(", ").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.OPERATOR_ID);
            query.append(", ").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FIRST_OPERAND);
            query.append(", ").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.SECOND_OPERAND);
            query.append(", ").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.GROUP_ID);
            query.append(") VALUES (?,?,?,?,?,?,?)");
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + query + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertDwhElements SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setLong(1, filterModel.getDwhElementModel().getElementId());
            pstmt.setInt(2, dwhElementValueGeneratedKey);
            pstmt.setInt(3, filterModel.getOperatorModel().getId());
            pstmt.setString(4, filterModel.getFirstOperand());
            pstmt.setString(5, filterModel.getSecondOperand());
            pstmt.setInt(6, groupId);
            pstmt.executeUpdate();
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public ArrayList<FileModel> retrieveAdsGroupFiles(int groupId, Connection conn) throws CommonException {
        String methodName = "retrieveCampGroupFiles";
//        CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<FileModel> ret = new ArrayList<>();
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM ").append(DBStruct.VFE_CS_ADS_GROUP_FILES.TABLE_NAME);
            query.append(" WHERE ").append(DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_ID).append(" = ?");
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + query + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "    public ArrayList<FileModel> retrieveAdsGroupFiles(int groupId, Connection conn) throws CommonException {\n"
                    + " SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (ret == null) {
                    ret = new ArrayList<FileModel>();
                }
                FileModel currentFile = new FileModel();
                currentFile.setFileID(rs.getInt(DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_FILE_ID));
                currentFile.setFileName(rs.getString(DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_FILE_NAME));
                ret.add(currentFile);
            }
            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
        return ret;
    }

    private String buildInsertListOfValuesPerDwhElementQuery(int dwhElementValueGeneratedKey, int filterID, DWHFilterValueModel dwhFilterValueModel) throws Exception {

        StringBuilder queryBuild = new StringBuilder();
        queryBuild.append("INSERT INTO " + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.TABLE_NAME);
        queryBuild.append(" (" + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.VALUE_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_VALUE_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_ID);
        queryBuild.append(" )VALUES (");
        queryBuild.append(" " + dwhFilterValueModel.getValueId() + " ");
        queryBuild.append(", " + dwhElementValueGeneratedKey + " ");
        queryBuild.append(", " + filterID + " ");
        queryBuild.append(")");
        return queryBuild.toString();
    }

    private String buildInsertDwhElementsQuery(int wadmDwhElementFilterGeneratedKey, int groupId, FilterModel filterModel) throws Exception {
        StringBuilder queryBuild = new StringBuilder();
        queryBuild.append("INSERT INTO " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.TABLE_NAME);
        queryBuild.append(" (" + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.ELEMENT_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FILTER_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.OPERATOR_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FIRST_OPERAND);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.SECOND_OPERAND);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.GROUP_ID);
        queryBuild.append(") VALUES (");
        queryBuild.append(" " + filterModel.getDwhElementModel().getElementId() + " ");
        queryBuild.append(", " + wadmDwhElementFilterGeneratedKey + " ");
        queryBuild.append(", " + filterModel.getOperatorModel().getId() + " ");
        queryBuild.append(", ?");
        queryBuild.append(", '" + filterModel.getSecondOperand() + "'");
        queryBuild.append(", " + groupId + " ");
        queryBuild.append(")");
        return queryBuild.toString();
    }

    private String buildInsertCampGroupFiles() throws Exception {
        StringBuilder queryBuild = new StringBuilder();
        queryBuild.append("INSERT INTO " + DBStruct.VFE_CS_ADS_GROUP_FILES.TABLE_NAME);
        queryBuild.append(" (" + DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_ID);
        queryBuild.append(" ," + DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_FILE_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_GROUP_FILES.FILE_BYTES);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_GROUP_FILES.GROUP_FILE_NAME);
        queryBuild.append(", " + DBStruct.VFE_CS_ADS_GROUP_FILES.FILE_STATUS_ID);
        queryBuild.append(") VALUES ( ?,?,?,?,?)");
        return queryBuild.toString();
    }

    private String buildRetreieveCommercialElementsQuery() throws Exception {
        StringBuilder queryBuild = new StringBuilder();
        queryBuild.append("SELECT ");
        queryBuild.append("e." + DBStruct.DWH_ELEMENTS.ELEMENT_ID);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.COLUMN_NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DESCRIPTION);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DATA_TYPE);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_COMM);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.FILE_INDEX);
        queryBuild.append(", lov." + DBStruct.DWH_ELEMENT_LOV.VALUE_ID);
        queryBuild.append(", lov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL);
        queryBuild.append(" FROM " + DBStruct.DWH_ELEMENTS.TBL_NAME + " e");
        queryBuild.append(" LEFT OUTER JOIN " + DBStruct.DWH_ELEMENTS.TBL_NAME + " lov ");
        queryBuild.append(" ON lov." + DBStruct.DWH_ELEMENTS.ELEMENT_ID);
        queryBuild.append(" = e." + DBStruct.DWH_ELEMENTS.ELEMENT_ID);

        queryBuild.append(" GROUP BY ");
        queryBuild.append(" e." + DBStruct.DWH_ELEMENTS.ELEMENT_ID);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.COLUMN_NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DESCRIPTION);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DATA_TYPE);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_COMM);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.FILE_INDEX);
        queryBuild.append(", lov." + DBStruct.DWH_ELEMENT_LOV.VALUE_ID);
        queryBuild.append(", lov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL);
        queryBuild.append(" HAVING ");
        queryBuild.append(DBStruct.DWH_ELEMENTS.DISPLAY_COMM + "= 1");
        return queryBuild.toString();
    }

    public String buildFiltersRetrievalQuery(int groupId) throws Exception {
        StringBuilder sb = new StringBuilder("");
        String query = "";

        sb.append("select f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FILTER_ID
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.ELEMENT_ID
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.OPERATOR_ID
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FIRST_OPERAND
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.SECOND_OPERAND
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.GROUP_ID
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_COMM
                + ", elmnts." + DBStruct.DWH_ELEMENTS.FILE_INDEX
                + ", elmnts." + DBStruct.DWH_ELEMENTS.COLUMN_NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DESCRIPTION
                + ", elmnts." + DBStruct.DWH_ELEMENTS.NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DATA_TYPE
                + ", filterlov." + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_VALUE_ID
                + ", filterlov." + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.VALUE_ID
                + ", elmntlov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL
                + " FROM " + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.TABLE_NAME + " f LEFT JOIN " + DBStruct.DWH_ELEMENTS.TBL_NAME + " elmnts "
                + " ON f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.ELEMENT_ID + " = elmnts." + DBStruct.DWH_ELEMENTS.ELEMENT_ID
                + " LEFT JOIN " + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.TABLE_NAME + " filterlov "
                + " ON f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_ID + " = filterlov." + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_ID
                + " LEFT JOIN " + DBStruct.DWH_ELEMENT_LOV.TBL_NAME + " elmntlov "
                + " ON filterlov." + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.VALUE_ID + " = elmntlov." + DBStruct.DWH_ELEMENT_LOV.VALUE_ID
                + " WHERE f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.GROUP_ID + " = " + groupId
                + "GROUP BY "
                + "f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FILTER_ID
                + ",f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.ELEMENT_ID
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.OPERATOR_ID
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.FIRST_OPERAND
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.SECOND_OPERAND
                + ", f." + DBStruct.VFE_CS_ADS_ELEMENT_FILTERS.GROUP_ID
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE
                + ",  elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_COMM
                + ", elmnts." + DBStruct.DWH_ELEMENTS.FILE_INDEX
                + ", elmnts." + DBStruct.DWH_ELEMENTS.COLUMN_NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DESCRIPTION
                + ", elmnts." + DBStruct.DWH_ELEMENTS.NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DATA_TYPE
                + ", filterlov." + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.VALUE_ID
                + ", filterlov." + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_VALUE_ID
                + ", elmntlov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL);
        query = sb.toString();

        return query;
    }

    public String buildRetreiveFilterMultiSelectionValuesQuery(int filterId) throws Exception {
        StringBuilder sb = new StringBuilder("");
        String query = "";
        sb.append("SELECT flov.").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_VALUE_ID)
                .append(", flov.").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.VALUE_ID)
                .append(", flov.").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_ID)
                .append(", elov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL + " from ")
                .append(DBStruct.DWH_ELEMENT_LOV.TBL_NAME)
                .append(" flov," + DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.TABLE_NAME
                        + " elov " + " where flov.").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.VALUE_ID)
                .append(" = elov." + DBStruct.DWH_ELEMENT_LOV.VALUE_ID
                        + " and flov.").append(DBStruct.VFE_CS_ADS_ELEMENT_FILTER_LOV.FILTER_ID).append(" = ").append(filterId);
        query = sb.toString();
        return query;
    }

    public AdsGroupModel getAdsGroupChild(AdsGroupModel parent, Connection conn) throws CommonException {
        String methodName = "getCampGroupParent";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        AdsGroupModel ret = new AdsGroupModel();
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID + " <> ? and " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAdsGroupChild SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, parent.getVersionId());
            pstat.setInt(2, parent.getGroupId());
            rs = pstat.executeQuery();
            ret.setVersionId(-1);
            if (rs.next()) {
                ret.setVersionId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID));
                ret.setCreatedBy(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY));
                ret.setDailyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD));
                ret.setDonotContact(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT));
                ret.setFilterQuery(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY));
                ret.setGroupDescription(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION));
                ret.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID));
                ret.setGroupName(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME));
                ret.setGroupPriority(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY));
                ret.setGuardPeriod(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD));
                ret.setMonthlyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD));
                ret.setStatus(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.STATUS));
                ret.setWeeklyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD));
                ret.setGroupType(SystemLookups.GROUP_TYPES.get((int) rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID)));
                if (ret.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    ret.setFilterList(this.retrieveAdsGroupFilters(ret.getVersionId(), conn));
                } else {
                    ret.setFilesModel(this.retrieveAdsGroupFiles(ret.getVersionId(), conn));
                }
            }
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
            return ret;
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * Called to retrieve the group by it's priority. It is used to determine
     * weather or not the priority exists before.
     *
     * @param groupPriority
     * @param conn
     * @return
     * @throws CommonException
     */
    public AdsGroupModel getAdsGroupByPriority(int groupPriority, String groupName, Connection conn) throws CommonException {
        String methodName = "getAdsGroupByPriority";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        AdsGroupModel ret = null;
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY + " = ? AND "
                    + DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME + " != ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAdsGroupByPriority SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, groupPriority);
            pstat.setString(2, groupName);
            rs = pstat.executeQuery();
            if (rs.next()) {
                ret = new AdsGroupModel();
                ret.setVersionId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID));
                ret.setCreatedBy(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY));
                ret.setDailyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD));
                ret.setDonotContact(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT));
                ret.setFilterQuery(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY));
                ret.setGroupDescription(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION));
                ret.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID));
                ret.setGroupName(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME));
                ret.setGroupPriority(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY));
                ret.setGuardPeriod(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD));
                ret.setMonthlyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD));
                ret.setStatus(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.STATUS));
                ret.setWeeklyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD));
                ret.setGroupType(SystemLookups.GROUP_TYPES.get((int) rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID)));
                if (ret.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    ret.setFilterList(this.retrieveAdsGroupFilters(ret.getVersionId(), conn));
                } else {
                    ret.setFilesModel(this.retrieveAdsGroupFiles(ret.getVersionId(), conn));
                }
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || " + "get ads group with priority=" + groupPriority);
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Get Ads group with priority")
                    .put(GeneralConstants.StructuredLogKeys.PRIORITY, groupPriority).build());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
            return ret;
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * Called to retrieve the group by it's name. It is used to determine
     * weather or not the name exists before.
     *
     * @param groupName
     * @param conn
     * @return
     * @throws CommonException
     */
    public AdsGroupModel getAdsGroupByName(String groupName, Connection conn) throws CommonException {
        String methodName = "getAdsGroupByName";
//        CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start " + methodName).build());
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        AdsGroupModel ret = null;
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME + " = ?";
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAdsGroupByName SQL Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, groupName);
            rs = pstat.executeQuery();
            if (rs.next()) {
                ret = new AdsGroupModel();
                ret.setVersionId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID));
                ret.setCreatedBy(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY));
                ret.setDailyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD));
                ret.setDonotContact(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DO_NOT_CONTACT));
                ret.setFilterQuery(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY));
                ret.setGroupDescription(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION));
                ret.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID));
                ret.setGroupName(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME));
                ret.setGroupPriority(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY));
                ret.setGuardPeriod(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GUARD_PERIOD));
                ret.setMonthlyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD));
                ret.setStatus(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.STATUS));
                ret.setWeeklyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD));
                ret.setGroupType(SystemLookups.GROUP_TYPES.get((int) rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_TYPE_ID)));
                if (ret.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    ret.setFilterList(this.retrieveAdsGroupFilters(ret.getVersionId(), conn));
                } else {
                    ret.setFilesModel(this.retrieveAdsGroupFiles(ret.getVersionId(), conn));
                }
            }
//            CommonLogger.businessLogger.info(AdsGroupDAO.class.getName() + " || " + "get ads group with name=" + groupName);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "get ads group with name")
                    .put(GeneralConstants.StructuredLogKeys.GROUP_NAME, groupName).build());
//            CommonLogger.businessLogger.debug(AdsGroupDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
            return ret;
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(AdsGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }
}
