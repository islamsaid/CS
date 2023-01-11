/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.interfaces.models.AdsGroupModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author hazem.fekry
 */
public class GroupsDAO {

    public HashMap<Integer, SMSGroupModel> getGroupsList(Connection conn) throws CommonException {
//        CommonLogger.businessLogger.debug(CustomersDAO.class.getName() + " || " + "Start [getGroupsList]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getGroupList Started").build());
        PreparedStatement pstat = null;
        HashMap<Integer, SMSGroupModel> groupMap = new HashMap<>();
        ResultSet rs = null;
        String sql;
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_SMS_GROUPS.TABLE_NAME
                    + " WHERE " + DBStruct.VFE_CS_SMS_GROUPS.STATUS + " = " + GeneralConstants.STATUS_APPROVED_VALUE;
            pstat = conn.prepareStatement(sql);
            rs = pstat.executeQuery();
            while (rs.next()) {
                SMSGroupModel tmpGroup = new SMSGroupModel();
                tmpGroup.setVersionId(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.VERSION_ID));
                tmpGroup.setDailyThreshold(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.DAILY_THRESHOLD));
                tmpGroup.setDonotContact(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.DO_NOT_CONTACT));
                tmpGroup.setFilterQuery(rs.getString(DBStruct.VFE_CS_SMS_GROUPS.FILTER_QUERY));
                tmpGroup.setGroupDescription(rs.getString(DBStruct.VFE_CS_SMS_GROUPS.GROUP_DESCRIPTION));
                tmpGroup.setGroupId(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.GROUP_ID));
                tmpGroup.setGroupName(rs.getString(DBStruct.VFE_CS_SMS_GROUPS.GROUP_NAME));
                tmpGroup.setGroupPriority(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.GROUP_PRIORITY));
                tmpGroup.setGuardPeriod(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.GUARD_PERIOD));
                tmpGroup.setMonthlyThreshold(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.MONTHLY_THRESHOLD));
                tmpGroup.setStatus(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.STATUS));
                tmpGroup.setWeeklyThreshold(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.WEEKLY_THRESHOLD));
                tmpGroup.setCreatedBy(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.CREATED_BY));
                groupMap.put(rs.getInt(DBStruct.VFE_CS_SMS_GROUPS.VERSION_ID), tmpGroup);
            }
//            CommonLogger.businessLogger.debug(CustomersDAO.class.getName() + " || " + "End [getGroupsList]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getGroupList Ended").build());

            return groupMap;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
    }

    public HashMap<Integer, AdsGroupModel> getAdsGroupsList(Connection conn) throws CommonException {
//        CommonLogger.businessLogger.debug(CustomersDAO.class.getName() + " || " + "Start [getAdsGroupsList]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAdsGroupsList Started").build());

        PreparedStatement pstat = null;
        HashMap<Integer, AdsGroupModel> adsGroupMap = new HashMap<>();
        ResultSet rs = null;
        String sql;
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_ADS_GROUPS.TABLE_NAME
                    + " WHERE " + DBStruct.VFE_CS_ADS_GROUPS.STATUS + " = " + GeneralConstants.STATUS_APPROVED_VALUE;
            pstat = conn.prepareStatement(sql);
            rs = pstat.executeQuery();
            while (rs.next()) {
                AdsGroupModel tmpGroup = new AdsGroupModel();
                tmpGroup.setId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID));
                tmpGroup.setGroupId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_ID));
                tmpGroup.setGroupName(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_NAME));
                tmpGroup.setGroupDescription(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.GROUP_DESCRIPTION));
                tmpGroup.setGroupPriority(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.GROUP_PRIORITY));
                tmpGroup.setDailyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.DAILY_THRESHOLD));
                tmpGroup.setWeeklyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.WEEKLY_THRESHOLD));
                tmpGroup.setMonthlyThreshold(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.MONTHLY_THRESHOLD));
                tmpGroup.setFilterQuery(rs.getString(DBStruct.VFE_CS_ADS_GROUPS.FILTER_QUERY));
                tmpGroup.setStatus(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.STATUS));
                tmpGroup.setCreator(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.CREATED_BY));
//                tmpGroup.setGroupEditId(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.EDITED_VERSION_DESCRIPTION));
                adsGroupMap.put(rs.getInt(DBStruct.VFE_CS_ADS_GROUPS.VERSION_ID), tmpGroup);
            }
//            CommonLogger.businessLogger.debug(CustomersDAO.class.getName() + " || " + "End [getAdsGroupsList]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getAdsGroupsList Ended").build());

            return adsGroupMap;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
    }
}
