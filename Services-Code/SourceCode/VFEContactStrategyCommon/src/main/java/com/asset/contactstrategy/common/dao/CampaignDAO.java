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
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHFilterValueModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.common.models.CustomersCampaignsModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author rania.magdy
 */
public class CampaignDAO {

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
            sql = "SELECT * FROM " + DBStruct.VFE_CS_CAMPAIGNS_FILES.TABLE_NAME
                    + " Where " + DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_ID + " =? AND "
                    + DBStruct.VFE_CS_CAMPAIGNS_FILES.FILE_STATUS_ID + "<>?  FOR UPDATE";

            // CommonLogger.businessLogger.debug(SMSGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            pstat = connection.prepareStatement(sql.toString());
            pstat.setInt(1, fileId);
            pstat.setInt(2, GeneralConstants.FILE_STATUS_PROCESSING_VALUE);

            rs = pstat.executeQuery();
            while (rs.next()) {
                valid = true;
                updateSql = "Update " + DBStruct.VFE_CS_CAMPAIGNS_FILES.TABLE_NAME
                        + " SET " + DBStruct.VFE_CS_CAMPAIGNS_FILES.FILE_STATUS_ID
                        + " =? WHERE " + DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_ID + " =?";
                updatePstat = connection.prepareCall(updateSql);
                updatePstat.setInt(1, GeneralConstants.FILE_STATUS_PROCESSING_VALUE);
                updatePstat.setInt(2, fileId);
                updatePstat.executeUpdate();
                connection.commit();
            }
//            CommonLogger.businessLogger.info(CampaignDAO.class.getName() + " || " + "End [checkFileStatus] in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkFileStatus Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());

        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [checkFileStatus]" + ex, ex);

            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [checkFileStatus]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, pstat);
            DataSourceManger.closeDBResources(null, updatePstat); // eslama.ahmed | 5-5-2020

        }
        return valid;
    }

    public void updateFileStatus(Connection connection, int fileId, int fileStatus) throws CommonException {
        // CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting updateFileStatus");
        long startime = System.currentTimeMillis();
        PreparedStatement pstat = null;
        String sql = "";
        try {
            sql = " Update " + DBStruct.VFE_CS_CAMPAIGNS_FILES.TABLE_NAME + " SET "
                    + DBStruct.VFE_CS_CAMPAIGNS_FILES.FILE_STATUS_ID + " =?  where "
                    + DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_ID + " = ? ";
            pstat = connection.prepareStatement(sql.toString());
            pstat.setInt(1, fileStatus);
            pstat.setInt(2, fileId);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.info(CampaignDAO.class.getName() + " || " + "Ended updateFileStatus in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended updateFileStatus")
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
            sql = " Update " + DBStruct.VFE_CS_CAMPAIGNS_FILES.TABLE_NAME + " SET "
                    + DBStruct.VFE_CS_CAMPAIGNS_FILES.FILE_STATUS_ID + " = ? ";
            pstat = connection.prepareStatement(sql.toString());
            pstat.setInt(1, GeneralConstants.FILE_STATUS_FINISHED_VALUE);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.info(CampaignDAO.class.getName() + " || " + "Ended updateFilesStatus in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended updateFileStatus")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateFilesStatus] Failed Campaigns Files Status" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->   for [updateFilesStatus] Failed Campaigns Files Status" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstat);
        }
    }

    /**
     * @param conn
     * @return
     * @throws CommonException
     */
    public Vector<CampaignModel> getApprovedActiveCampaignsList(Connection conn) throws CommonException {
        // CommonLogger.businessLogger.info(SMSGroupDAO.class.getName() + " || " + "Start [getApprovedActiveCampaignsList]...");
        Vector<CampaignModel> ret = new Vector<>();
        long startime = System.currentTimeMillis();
        PreparedStatement pstat = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = "SELECT * FROM " + DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME
                    + " Where " + DBStruct.VFE_CS_CAMPAIGNS.STATUS + " =? "
                    + " AND " + DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS + " =? "
                    + " AND sysdate Between " + DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START + " AND " + DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END
                    + "  ORDER BY " + DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY + " desc ";
            //  CommonLogger.businessLogger.debug(SMSGroupDAO.class.getName() + " || " + "Query [" + sql + "]");
            pstat = conn.prepareStatement(sql.toString());
            pstat.setInt(1, GeneralConstants.STATUS_APPROVED_VALUE);
            pstat.setInt(2, GeneralConstants.CAMPAIGN_STATUS_RESUMED_VALUE);
            rs = pstat.executeQuery();
            while (rs.next()) {
                CampaignModel tmpGroup = new CampaignModel();
                tmpGroup.setVersionId(rs.getInt(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID));
                tmpGroup.setFilterQuery(rs.getString(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY));
                tmpGroup.setCampaignId(rs.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID));
                tmpGroup.setCampaignName(rs.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME));
                tmpGroup.setPriority(rs.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY));
                tmpGroup.setStatus(rs.getInt(DBStruct.VFE_CS_CAMPAIGNS.STATUS));
                tmpGroup.setMaxTargetedCustomers(rs.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED));
                tmpGroup.setControlPercentage(rs.getInt(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE));
                tmpGroup.setFilterType(SystemLookups.GROUP_TYPES.get((int) rs.getInt(DBStruct.VFE_CS_CAMPAIGNS.FILTER_TYPE)));
                ret.add(tmpGroup);
            }
//            CommonLogger.businessLogger.info(SMSGroupDAO.class.getName() + " || " + "End [getApprovedActiveCampaignsList] in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getApprovedActiveCampaignList")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(SMSGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [getApprovedActiveCampaignsList]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(SMSGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [getApprovedActiveCampaignsList]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, pstat);

        }
        return ret;
    }

    public ArrayList<CampaignModel> getCampaigns(Connection connection) throws CommonException {

        //  CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [getCampaigns] started...");
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;

        try {
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(" inner join ").append(DBStruct.VFE_CS_USERS.TABLE_NAME)
                    .append(" on ").append(DBStruct.VFE_CS_CAMPAIGNS.LAST_MODIFIED_BY).append(" = ")
                    .append(DBStruct.VFE_CS_USERS.USER_ID);
            sql.append(" order by ").append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID);
            //      CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();

            ArrayList<CampaignModel> campaignModels = new ArrayList<>();
            while (resultSet.next()) {
                CampaignModel campaignModel = new CampaignModel();
                campaignModel.setVersionId(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID));
                campaignModel.setCampaignId(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID));
                campaignModel.setCampaignName(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME));
                campaignModel.setCampaignDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_DESCRIPTION));
                campaignModel.setStartDate(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START));
                campaignModel.setEndDate(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END));
                campaignModel.setMaxNumberOfCommunications(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_COMMUNICATIONS));
                campaignModel.setMaxTargetedCustomers(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED));
                campaignModel.setFilterQuery(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY));
                campaignModel.setPriority(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY));
                campaignModel.setArabicScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ARABIC_SCRIPT));
                campaignModel.setEnglishScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ENGLISH_SCRIPT));
                campaignModel.setCampaignStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS));
                campaignModel.setControlPercentage(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE));
                campaignModel.setLastModifiedBy(resultSet.getLong(DBStruct.VFE_CS_CAMPAIGNS.LAST_MODIFIED_BY));
                campaignModel.setLastModifiedByName(resultSet.getString(DBStruct.VFE_CS_USERS.USER_NAME));
                campaignModel.setStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.STATUS));
                campaignModel.setEditedDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.EDITED_VERSION_DESCRIPTION));
                campaignModel.setFilterType(SystemLookups.GROUP_TYPES.get((int) resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.FILTER_TYPE)));

                if (campaignModel.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    campaignModel.setFilterList(this.retrieveCampaignFilters(campaignModel.getVersionId(), connection));
                } else {
                    campaignModel.setFilesModel(this.retrieveCampaignFiles(campaignModel.getVersionId(), connection));
                }
                campaignModels.add(campaignModel);
            }
//            CommonLogger.businessLogger.info(CampaignDAO.class.getName() + " || Campaign List Size :[" + campaignModels.size() + "]  Retriving Date time [:" + (System.currentTimeMillis() - startTime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Campaign List stat")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, campaignModels.size())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
            //  CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + ");
            // CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [getCampaigns] ended...");

            return campaignModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [getCampaigns]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [getCampaigns]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                resultSet.close(); // eslam.ahmed | 5-5-2020
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [getCampaigns]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public CampaignModel retrieveConnectedCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[retrieveConnectedCampaign] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedCampaign Started").build());
        // boolean hasChild = false;
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        CampaignModel returnedModel = null;
        try {

            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(" where ").append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID)
                    .append(" = ").append(campaignModel.getCampaignId()).append(" and ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID).append(" <> ")
                    .append(campaignModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.VFE_CS_CAMPAIGNS.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedCampaign Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                returnedModel = new CampaignModel();
                returnedModel.setVersionId(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID));
                returnedModel.setCampaignId(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID));
                returnedModel.setCampaignName(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME));
                returnedModel.setArabicScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ARABIC_SCRIPT));
                returnedModel.setCampaignDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_DESCRIPTION));
                returnedModel.setCampaignStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS));
                returnedModel.setControlPercentage(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE));
                returnedModel.setEditedDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.EDITED_VERSION_DESCRIPTION));
                returnedModel.setEndDate(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END));
                returnedModel.setEnglishScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ENGLISH_SCRIPT));
                returnedModel.setFilterQuery(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY));
                returnedModel.setLastModifiedBy(resultSet.getLong(DBStruct.VFE_CS_CAMPAIGNS.LAST_MODIFIED_BY));
                returnedModel.setMaxNumberOfCommunications(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_COMMUNICATIONS));
                returnedModel.setMaxTargetedCustomers(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED));
                returnedModel.setPriority(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY));
                returnedModel.setStartDate(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START));
                returnedModel.setStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.STATUS));
                returnedModel.setFilterType(SystemLookups.GROUP_TYPES.get((int) resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.FILTER_TYPE)));

                if (returnedModel.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    returnedModel.setFilterList(this.retrieveCampaignFilters(returnedModel.getVersionId(), connection));
                } else {
                    returnedModel.setFilesModel(this.retrieveCampaignFiles(returnedModel.getVersionId(), connection));
                }
            }
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Retrieving Date Time")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [retrieveConnectedCampaign] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedCampaign Ended").build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + " ---->  for [retrieveConnectedCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + " ---->  for [retrieveConnectedCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [retrieveConnectedCampaign]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
        return returnedModel;
    }

    public void deleteCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[deleteCampaign] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCampaign Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("delete  from ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(" where ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID).append(" = ")
                    .append(campaignModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCampaign query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [deleteCampaign] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCampaign Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [deleteCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [deleteCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [deleteCampaign]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void deleteCampaignServices(Connection connection, CampaignModel campaignModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[deleteCampaignServices] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCampaignServices Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("delete  from ").append(DBStruct.CAMPAIGNS_SERVICES.TABLE_NAME)
                    .append(" where ")
                    .append(DBStruct.CAMPAIGNS_SERVICES.CAMPAIGN_ID).append(" = ")
                    .append(campaignModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCampaignServices Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [deleteCampaignServices] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCampaignServices Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [deleteCampaignServices]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [deleteCampaignServices]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [deleteCampaignServices]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void ChangeCampaignStatusToDelete(Connection connection, CampaignModel campaignModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[ChangeCampaignStatusToDelete] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatusToDelete Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("update ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(" set ").append(DBStruct.VFE_CS_CAMPAIGNS.STATUS).append(" = ")
                    .append(GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE)
                    .append(" where ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID).append(" = ")
                    .append(campaignModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatusToDelete Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [ChangeCampaignStatusToDelete] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatusToDelete Enced")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [ChangeCampaignStatusToDelete]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [ChangeCampaignStatusToDelete]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [ChangeCampaignStatusToDelete]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void editCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {

//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[editCampaign] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editCampaign Started").build());
        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        long startTime = System.currentTimeMillis();

        long id;
        try {
            id = CommonDAO.getNextId(connection,
                    DBStruct.VFE_CS_CAMPAIGNS.ID_SEQUENCE);
            long groupID = campaignModel.getCampaignId();
            sqlQuery = new StringBuilder();

            sqlQuery.append("INSERT INTO ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME);
            sqlQuery.append(" ( ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.ARABIC_SCRIPT).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_DESCRIPTION)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.EDITED_VERSION_DESCRIPTION)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.ENGLISH_SCRIPT)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.LAST_MODIFIED_BY)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.MAX_COMMUNICATIONS)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.STATUS)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.FILTER_TYPE);
            sqlQuery.append(" ) ");
            sqlQuery.append(" VALUES ");
            sqlQuery.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            statement = connection.prepareStatement(sqlQuery.toString());
            statement.setLong(1, id);
            statement.setLong(2, groupID);
            statement.setString(3, campaignModel.getArabicScript());
            statement.setString(4, campaignModel.getCampaignDescription());
            Object endDate = new java.sql.Timestamp(campaignModel.getEndDate().getTime());
            statement.setObject(5, endDate);
            statement.setString(6, campaignModel.getCampaignName());
            statement.setInt(7, campaignModel.getPriority());
            Object startDate = new java.sql.Timestamp(campaignModel.getStartDate().getTime());
            statement.setObject(8, startDate);
            statement.setInt(9, campaignModel.getCampaignStatus());
            statement.setInt(10, campaignModel.getControlPercentage());
            statement.setString(11, campaignModel.getEditedDescription());
            statement.setString(12, campaignModel.getEnglishScript());
            if (campaignModel.getFilterType().getId() == Defines.GROUP_TYPES.UPLOADED.getId()) {
                campaignModel.setFilterQuery(" 1=1");
            }
            statement.setString(13, campaignModel.getFilterQuery());
            statement.setLong(14, campaignModel.getLastModifiedBy());
            statement.setInt(15, campaignModel.getMaxNumberOfCommunications());
            statement.setInt(16, campaignModel.getMaxTargetedCustomers());
            statement.setInt(17, GeneralConstants.STATUS_PENDING_VALUE);
            statement.setInt(18, campaignModel.getFilterType().getId());

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sqlQuery.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editCampaign Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery).build());

            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Updating Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editCampaign executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            if (campaignModel.getFilterType().getId() == Defines.GROUP_TYPES.CRITERIA_BASED.getId()) {
                for (int i = 0; i < campaignModel.getFilterList().size(); i++) {
                    insertDWHElementsPerCampaign(id, campaignModel.getFilterList().get(i), connection);
                }
            } else {
                for (int i = 0; i < campaignModel.getFilesModel().size(); i++) {
                    int GroupFileGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_ID_SEQ, connection);
                    String filesQuery = this.buildInsertCampaignFiles();
                    PreparedStatement preparedStatement = connection.prepareStatement(filesQuery);
                    preparedStatement.setLong(1, id);
                    preparedStatement.setInt(2, GroupFileGeneratedKey);
                    preparedStatement.setString(3, campaignModel.getFilesModel().get(i).getFileData());
                    preparedStatement.setString(4, campaignModel.getFilesModel().get(i).getFileName());
                    // Set the newly created file with status CREATED.
                    preparedStatement.setInt(5, SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.CREATED).getId());//2595 id was put in defines
                    preparedStatement.execute();
                    preparedStatement.close(); // eslam.ahmed | 5-5-2020
                }
            }

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[editCampaign] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editCampaign Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [editCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [editCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [editCampaign]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void editEditedCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[editEditedCampaign] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedCampaign Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("update ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(" set ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.ARABIC_SCRIPT).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_DESCRIPTION).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.EDITED_VERSION_DESCRIPTION).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.ENGLISH_SCRIPT).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.LAST_MODIFIED_BY).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.MAX_COMMUNICATIONS).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED).append(" = ")
                    .append(" ? ").append(" , ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.STATUS).append(" = ")
                    .append(" ? ")
                    .append(" where ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID).append(" = ")
                    .append(" ? ");
            statement = connection.prepareStatement(sql.toString());

            statement.setString(1, campaignModel.getArabicScript());
            statement.setString(2, campaignModel.getCampaignDescription());
            Object endDate = new java.sql.Timestamp(campaignModel.getEndDate().getTime());
            statement.setObject(3, endDate);
            statement.setInt(4, campaignModel.getPriority());
            Object startDate = new java.sql.Timestamp(campaignModel.getStartDate().getTime());
            statement.setObject(5, startDate);
            statement.setInt(6, campaignModel.getCampaignStatus());
            statement.setInt(7, campaignModel.getControlPercentage());
            statement.setString(8, campaignModel.getEditedDescription());
            statement.setString(9, campaignModel.getEnglishScript());
            if (campaignModel.getFilterType() != null && campaignModel.getFilterType().getId() == Defines.GROUP_TYPES.UPLOADED.getId()) {
                campaignModel.setFilterQuery(" 1=1");
            }
            statement.setString(10, campaignModel.getFilterQuery());
            statement.setLong(11, campaignModel.getLastModifiedBy());
            statement.setInt(12, campaignModel.getMaxNumberOfCommunications());
            statement.setInt(13, campaignModel.getMaxTargetedCustomers());
            statement.setInt(14, campaignModel.getStatus());
            statement.setLong(15, campaignModel.getVersionId());

            /////////////
            if (campaignModel.getFilterType() != null && campaignModel.getFilterType().getId() == Defines.GROUP_TYPES.UPLOADED.getId()) {
                this.deleteCampaignFiles(campaignModel.getVersionId(), connection);
                for (int i = 0; i < campaignModel.getFilesModel().size(); i++) {
                    int campaignFileGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_ID_SEQ, connection);
                    String filesQuery = this.buildInsertCampaignFiles();
                    PreparedStatement preparedStatement = connection.prepareStatement(filesQuery);
                    preparedStatement.setLong(1, campaignModel.getVersionId());
                    preparedStatement.setInt(2, campaignFileGeneratedKey);
                    preparedStatement.setString(3, campaignModel.getFilesModel().get(i).getFileData());
                    preparedStatement.setString(4, campaignModel.getFilesModel().get(i).getFileName());
                    // Set the newly created file status with CREATED.
                    preparedStatement.setInt(5, SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.CREATED).getId());//2595 id was put in defines
                    preparedStatement.execute();
                    preparedStatement.close(); // eslam.ahmed | 5-5-2020
                }
            } else {
                ArrayList<FilterModel> oldFilters = new ArrayList<FilterModel>();
                oldFilters = this.retrieveCampaignFilters(campaignModel.getVersionId(), connection);
                for (FilterModel filterModelDwh : oldFilters) {
                    this.deleteDwhFiltersListOfValues(filterModelDwh.getFilterId(), connection);
                }
                this.deleteDWHElementsPerCampaign(campaignModel.getVersionId(), connection);
                for (FilterModel dwhFilterModel : campaignModel.getFilterList()) {
                    this.insertDWHElementsPerCampaign(campaignModel.getVersionId(), dwhFilterModel, connection);
                }
            }

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedCampaign Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());

            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [editEditedCampaign] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editCampaign Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [editEditedCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [editEditedCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [editEditedCampaign]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public CampaignModel createCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {

//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[createCampaign] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createCampaign Started").build());

        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        long startTime = System.currentTimeMillis();

        long id;
        try {
            id = CommonDAO.getNextId(connection,
                    DBStruct.VFE_CS_CAMPAIGNS.ID_SEQUENCE);
            long groupID = CommonDAO.getNextId(connection,
                    DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID_SEQUENCE);
            sqlQuery = new StringBuilder();
            campaignModel.setVersionId(id);
            campaignModel.setCampaignId(groupID);
            sqlQuery.append("INSERT INTO ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME);
            sqlQuery.append(" ( ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.ARABIC_SCRIPT).append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_DESCRIPTION)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.EDITED_VERSION_DESCRIPTION)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.ENGLISH_SCRIPT)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.LAST_MODIFIED_BY)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.MAX_COMMUNICATIONS)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.STATUS)
                    .append(", ");
            sqlQuery.append(DBStruct.VFE_CS_CAMPAIGNS.FILTER_TYPE);
            sqlQuery.append(" ) ");
            sqlQuery.append(" VALUES ");
            sqlQuery.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            statement = connection.prepareStatement(sqlQuery.toString());
            statement.setLong(1, id);
            statement.setLong(2, groupID);
            statement.setString(3, campaignModel.getArabicScript());
            statement.setString(4, campaignModel.getCampaignDescription());
            Object endDate = new java.sql.Timestamp(campaignModel.getEndDate().getTime());
            statement.setObject(5, endDate);
            statement.setString(6, campaignModel.getCampaignName());
            statement.setInt(7, campaignModel.getPriority());
            Object startDate = new java.sql.Timestamp(campaignModel.getStartDate().getTime());
            statement.setObject(8, startDate);
            statement.setInt(9, campaignModel.getCampaignStatus());
            statement.setInt(10, campaignModel.getControlPercentage());
            statement.setString(11, campaignModel.getEditedDescription());
            statement.setString(12, campaignModel.getEnglishScript());
            if (campaignModel.getFilterType().getId() == Defines.GROUP_TYPES.UPLOADED.getId()) {
                campaignModel.setFilterQuery("1=1");
            }
            statement.setString(13, campaignModel.getFilterQuery());
            statement.setLong(14, campaignModel.getLastModifiedBy());
            statement.setInt(15, campaignModel.getMaxNumberOfCommunications());
            statement.setInt(16, campaignModel.getMaxTargetedCustomers());
            statement.setInt(17, campaignModel.getStatus());
            statement.setInt(18, campaignModel.getFilterType().getId());

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sqlQuery.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createCampaign Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery).build());

            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Updating Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createCampaign executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            if (campaignModel.getFilterType().getId() == Defines.GROUP_TYPES.CRITERIA_BASED.getId()) {
                for (int i = 0; i < campaignModel.getFilterList().size(); i++) {
                    insertDWHElementsPerCampaign(campaignModel.getVersionId(), campaignModel.getFilterList().get(i), connection);
                }
            } else {
                for (int i = 0; i < campaignModel.getFilesModel().size(); i++) {
                    int GroupFileGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_ID_SEQ, connection);
                    String filesQuery = this.buildInsertCampaignFiles();
                    PreparedStatement preparedStatement = connection.prepareStatement(filesQuery);
                    preparedStatement.setLong(1, campaignModel.getVersionId());
                    preparedStatement.setInt(2, GroupFileGeneratedKey);
                    preparedStatement.setString(3, campaignModel.getFilesModel().get(i).getFileData());
                    preparedStatement.setString(4, campaignModel.getFilesModel().get(i).getFileName());
                    // Set the newly created file with status CREATED.
                    preparedStatement.setInt(5, SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.CREATED).getId());//2595 id was put in defines
                    preparedStatement.execute();
                    preparedStatement.close(); // eslam.ahmed | 5-5-2020
                }
            }
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [createCampaign] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createCampaign Ended").build());

            return campaignModel;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [createCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [createCampaign]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [createCampaign]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void ChangeCampaignStatus(Connection connection, CampaignModel campaignModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[ChangeCampaignStatus] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatus Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("update ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(" set ").append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS).append(" = ")
                    .append(campaignModel.getCampaignStatus())
                    .append(" where ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID).append(" = ")
                    .append(campaignModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatus Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());

            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [ChangeCampaignStatus] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatus Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [ChangeCampaignStatus]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [ChangeCampaignStatus]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [ChangeCampaignStatusToDelete]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void insertCampaignServices(Connection connection, CampaignModel campaignModel, ArrayList<ServiceModel> serviceList) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[insertCampaignServices] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertCampaignServices Started").build());

        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        long startTime = System.currentTimeMillis();

        long id;
        try {
            sqlQuery = new StringBuilder();
            sqlQuery.append("Insert into ").append(DBStruct.CAMPAIGNS_SERVICES.TABLE_NAME).append(" ( ")
                    .append(DBStruct.CAMPAIGNS_SERVICES.CAMPAIGN_ID).append(",")
                    .append(DBStruct.CAMPAIGNS_SERVICES.SERVICE_ID).append(" ) VALUES (?,?)");
            statement = connection.prepareStatement(sqlQuery.toString());

            for (int i = 0; i < serviceList.size(); i++) {

                statement.setLong(1, campaignModel.getVersionId());
                statement.setLong(2, serviceList.get(i).getVersionId());
                statement.addBatch();
            }
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sqlQuery.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertCampaignServices Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery).build());

            statement.executeBatch();

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Updating Date time :" + (System.currentTimeMillis() - startTime));
//
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [insertCampaignServices] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertCampaignServices Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [insertCampaignServices]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [insertCampaignServices]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [insertCampaignServices]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public CampaignModel getCampaignByName(Connection connection, String name) throws
            CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [getCampaignByName] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByName Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        CampaignModel campaignModel = null;
        try {
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME);
            sql.append(" where ").append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME).append(" = '").append(name).append("'");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByName Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByName executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            if (resultSet.next()) {
                campaignModel = new CampaignModel();
                campaignModel.setVersionId(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID));
                campaignModel.setCampaignId(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID));
                campaignModel.setCampaignName(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME));
                campaignModel.setCampaignDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_DESCRIPTION));
                campaignModel.setStartDate(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START));
                campaignModel.setEndDate(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END));
                campaignModel.setMaxNumberOfCommunications(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_COMMUNICATIONS));
                campaignModel.setMaxTargetedCustomers(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED));
                campaignModel.setFilterQuery(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY));
                campaignModel.setPriority(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY));
                campaignModel.setArabicScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ARABIC_SCRIPT));
                campaignModel.setEnglishScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ENGLISH_SCRIPT));
                campaignModel.setCampaignStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS));
                campaignModel.setControlPercentage(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE));
                campaignModel.setLastModifiedBy(resultSet.getLong(DBStruct.VFE_CS_CAMPAIGNS.LAST_MODIFIED_BY));
                campaignModel.setStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.STATUS));
                campaignModel.setEditedDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.EDITED_VERSION_DESCRIPTION));
                campaignModel.setFilterType(SystemLookups.GROUP_TYPES.get((int) resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.FILTER_TYPE)));

                if (campaignModel.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    campaignModel.setFilterList(this.retrieveCampaignFilters(campaignModel.getVersionId(), connection));
                } else {
                    campaignModel.setFilesModel(this.retrieveCampaignFiles(campaignModel.getVersionId(), connection));
                }
            }
            //CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "SMSC List Size :" + smscModels.size());
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [getCampaignByName] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByName Ended").build());

            return campaignModel;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [getCampaignByName]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [getCampaignByName]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [getCampaignByName]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public CampaignModel getCampaignByPriority(Connection connection, int priority) throws
            CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [getCampaignByPriority] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByPriority Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        CampaignModel campaignModel = null;
        try {
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME);
            sql.append(" where ").append(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY).append(" = ").append(priority);
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByPriority Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByPriority executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            if (resultSet.next()) {
                campaignModel = new CampaignModel();
                campaignModel.setVersionId(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID));
                campaignModel.setCampaignId(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_ID));
                campaignModel.setCampaignName(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_NAME));
                campaignModel.setCampaignDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_DESCRIPTION));
                campaignModel.setStartDate(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_START));
                campaignModel.setEndDate(resultSet.getTimestamp(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_END));
                campaignModel.setMaxNumberOfCommunications(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_COMMUNICATIONS));
                campaignModel.setMaxTargetedCustomers(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.MAX_TARGETED));
                campaignModel.setFilterQuery(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.FILTER_QUERY));
                campaignModel.setPriority(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_PRIORITY));
                campaignModel.setArabicScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ARABIC_SCRIPT));
                campaignModel.setEnglishScript(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.ENGLISH_SCRIPT));
                campaignModel.setCampaignStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CAMPAIGN_STATUS));
                campaignModel.setControlPercentage(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.CONTROL_PERCENTAGE));
                campaignModel.setLastModifiedBy(resultSet.getLong(DBStruct.VFE_CS_CAMPAIGNS.LAST_MODIFIED_BY));
                campaignModel.setStatus(resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.STATUS));
                campaignModel.setEditedDescription(resultSet.getString(DBStruct.VFE_CS_CAMPAIGNS.EDITED_VERSION_DESCRIPTION));
                campaignModel.setFilterType(SystemLookups.GROUP_TYPES.get((int) resultSet.getInt(DBStruct.VFE_CS_CAMPAIGNS.FILTER_TYPE)));
                if (campaignModel.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    campaignModel.setFilterList(this.retrieveCampaignFilters(campaignModel.getVersionId(), connection));
                } else {
                    campaignModel.setFilesModel(this.retrieveCampaignFiles(campaignModel.getVersionId(), connection));
                }
            }
            //CommonLogger.businessLogger.debug(SMSCDAO.class.getName() + " || " + "SMSC List Size :" + smscModels.size());
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [getCampaignByPriority] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByPriority Ended").build());

            return campaignModel;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [getCampaignByPriority]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [getCampaignByPriority]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [getCampaignByPriority]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    public void deleteDwhFiltersListOfValues(long filterId, Connection conn) throws CommonException {
        String methodName = "deleteDwhFiltersListOfValues";
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Started").build());
        PreparedStatement pstat = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append("DELETE FROM ").append(DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.TABLE_NAME);
            query.append(" WHERE ").append(DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_ID).append(" = ?");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Query [" + query.toString() + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstat = conn.prepareStatement(query.toString());
            pstat.setLong(1, filterId);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Ended").build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void deleteDWHElementsPerCampaign(long filterId, Connection conn) throws CommonException {
        String methodName = "deleteDWHElementsPerCampaign";
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Started").build());
        PreparedStatement pstmt = null;
        String query = new String();
        try {
            query = "DELETE FROM " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.TABLE_NAME + " WHERE " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.CAMPAIGN_ID + " = ?";
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Query [" + query + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, filterId);
            pstmt.executeUpdate();
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Ended").build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    private void insertDWHElementsPerCampaign(long campaignId, FilterModel filterModel, Connection conn) throws CommonException {
        String methodName = "insertDWHElementsPerCampaign";
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Started").build());
        PreparedStatement st = null;
        try {
            int wadmDwhElementFilterGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.ELEMENT_FILTER_SEQ, conn);
            String admDwhElementFilterQuery = buildInsertDwhElementsQuery(wadmDwhElementFilterGeneratedKey, campaignId, filterModel);
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Query [" + admDwhElementFilterQuery + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, admDwhElementFilterQuery).build());
            st = conn.prepareStatement(admDwhElementFilterQuery);
            st.setString(1, filterModel.getFirstOperand());
            st.execute();
            for (int i = 0; i < filterModel.getFilterValues().size(); i++) {
                if (!filterModel.getFilterValues().isEmpty()) {
                    insertListOfValuesPerDwhElement(wadmDwhElementFilterGeneratedKey, filterModel.getFilterValues().get(i), conn);
                }
            }
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Ended").build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

    private void insertListOfValuesPerDwhElement(int filterID, DWHFilterValueModel dwhFilterValueModel, Connection conn) throws CommonException {
        String methodName = "insertListOfValuesPerDwhElement";
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Started").build());
        Statement st = null;
        try {
            int dwhElementValueGeneratedKey = (int) Utility.getNextId(DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.ELEMENT_FILTER_LOV_SEQ, conn);
            String listOfValuesPerDwhElementQuery = buildInsertListOfValuesPerDwhElementQuery(dwhElementValueGeneratedKey, filterID, dwhFilterValueModel);
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Query [" + listOfValuesPerDwhElementQuery + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, listOfValuesPerDwhElementQuery).build());
            st = conn.prepareStatement(listOfValuesPerDwhElementQuery);
            st.execute(listOfValuesPerDwhElementQuery);
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Ended").build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    private String buildInsertListOfValuesPerDwhElementQuery(int dwhElementValueGeneratedKey, int filterID, DWHFilterValueModel dwhFilterValueModel) throws Exception {

        StringBuilder queryBuild = new StringBuilder();
        queryBuild.append("INSERT INTO " + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.TABLE_NAME);
        queryBuild.append(" (" + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.VALUE_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_VALUE_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_ID);
        queryBuild.append(" )VALUES (");
        queryBuild.append(" " + dwhFilterValueModel.getValueId() + " ");
        queryBuild.append(", " + dwhElementValueGeneratedKey + " ");
        queryBuild.append(", " + filterID + " ");
        queryBuild.append(")");
        return queryBuild.toString();
    }

    private String buildInsertDwhElementsQuery(int wadmDwhElementFilterGeneratedKey, long campaignId, FilterModel filterModel) throws Exception {
        StringBuilder queryBuild = new StringBuilder();
        queryBuild.append("INSERT INTO " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.TABLE_NAME);
        queryBuild.append(" (" + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.ELEMENT_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FILTER_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.OPERATOR_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FIRST_OPERAND);
        queryBuild.append(", " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.SECOND_OPERAND);
        queryBuild.append(", " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.CAMPAIGN_ID);
        queryBuild.append(") VALUES (");
        queryBuild.append(" " + filterModel.getDwhElementModel().getElementId() + " ");
        queryBuild.append(", " + wadmDwhElementFilterGeneratedKey + " ");
        queryBuild.append(", " + filterModel.getOperatorModel().getId() + " ");
        queryBuild.append(", ?");
        queryBuild.append(", '" + filterModel.getSecondOperand() + "'");
        queryBuild.append(", " + campaignId + " ");
        queryBuild.append(")");
        return queryBuild.toString();
    }

    public ArrayList<FilterModel> retrieveCampaignFilters(long campaignId, Connection conn) throws CommonException {
        String methodName = "retrieveCampaignFilters";
        //  CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        Statement st = null;
        ResultSet rs = null;
        ArrayList<FilterModel> ret = null;
        try {
            String query = buildFiltersRetrievalQuery(campaignId);
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Query [" + query + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = conn.prepareStatement(query);
            rs = st.executeQuery(query);
            ret = new ArrayList<FilterModel>();
            HashMap<Long, FilterModel> tempFilters = new HashMap<Long, FilterModel>();
            while (rs.next()) {
                FilterModel currentFilter = tempFilters.get(rs.getLong(DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FILTER_ID));
                if (currentFilter == null) {

                    currentFilter = new FilterModel();
                    currentFilter.setFilterId(rs.getInt(DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FILTER_ID));
                    currentFilter.setGroupId(rs.getInt(DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.CAMPAIGN_ID));
                    currentFilter.setFirstOperand(rs.getString(DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FIRST_OPERAND));
                    currentFilter.setSecondOperand(rs.getString(DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.SECOND_OPERAND));
                    Integer operatorId = null;
                    LookupModel filterOperator = null;

                    if (rs.getObject(DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.OPERATOR_ID) != null) {
                        operatorId = rs.getInt(DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.OPERATOR_ID);
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
                        filterValue.setFilterValueId(rs.getInt(DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_VALUE_ID));
                        filterValue.setValueId(rs.getInt(DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.VALUE_ID));
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
                    filterValue.setFilterValueId(rs.getInt(DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_VALUE_ID));
                    filterValue.setValueId(rs.getInt(DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.VALUE_ID));
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
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Ended").build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
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
                CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
        return ret;
    }

    private String buildInsertCampaignFiles() throws Exception {
        StringBuilder queryBuild = new StringBuilder();
        queryBuild.append("INSERT INTO " + DBStruct.VFE_CS_CAMPAIGNS_FILES.TABLE_NAME);
        queryBuild.append(" (" + DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_ID);
        queryBuild.append(" ," + DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_ID);
        queryBuild.append(", " + DBStruct.VFE_CS_CAMPAIGNS_FILES.FILE_BYTES);
        queryBuild.append(", " + DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_NAME);
        queryBuild.append(", " + DBStruct.VFE_CS_CAMPAIGNS_FILES.FILE_STATUS_ID);
        queryBuild.append(") VALUES ( ?,?,?,?,?)");
        return queryBuild.toString();
    }

    public void deleteCampaignFiles(long campaignId, Connection conn) throws CommonException {
        String methodName = "deleteCampaignFiles";
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Started").build());
        PreparedStatement pstat = null;
        StringBuilder query = new StringBuilder();
        try {
            query.append("DELETE FROM ").append(DBStruct.VFE_CS_CAMPAIGNS_FILES.TABLE_NAME);
            query.append(" WHERE ").append(DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_ID).append(" = ?");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Query [" + query.toString() + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstat = conn.prepareStatement(query.toString());
            pstat.setLong(1, campaignId);
            pstat.executeUpdate();
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Ended").build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstat != null) {
                    pstat.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public String buildFiltersRetrievalQuery(long campaignId) throws Exception {
        StringBuilder sb = new StringBuilder("");
        String query = "";

        sb.append("select f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FILTER_ID
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.ELEMENT_ID
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.OPERATOR_ID
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FIRST_OPERAND
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.SECOND_OPERAND
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.CAMPAIGN_ID
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_COMM
                + ", elmnts." + DBStruct.DWH_ELEMENTS.FILE_INDEX
                + ", elmnts." + DBStruct.DWH_ELEMENTS.COLUMN_NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DESCRIPTION
                + ", elmnts." + DBStruct.DWH_ELEMENTS.NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DATA_TYPE
                + ", filterlov." + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_VALUE_ID
                + ", filterlov." + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.VALUE_ID
                + ", elmntlov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL
                + " FROM " + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.TABLE_NAME + " f LEFT JOIN " + DBStruct.DWH_ELEMENTS.TBL_NAME + " elmnts "
                + " ON f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.ELEMENT_ID + " = elmnts." + DBStruct.DWH_ELEMENTS.ELEMENT_ID
                + " LEFT JOIN " + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.TABLE_NAME + " filterlov "
                + " ON f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_ID + " = filterlov." + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_ID
                + " LEFT JOIN " + DBStruct.DWH_ELEMENT_LOV.TBL_NAME + " elmntlov "
                + " ON filterlov." + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.VALUE_ID + " = elmntlov." + DBStruct.DWH_ELEMENT_LOV.VALUE_ID
                + " WHERE f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.CAMPAIGN_ID + " = " + campaignId
                + "GROUP BY "
                + "f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FILTER_ID
                + ",f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.ELEMENT_ID
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.OPERATOR_ID
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.FIRST_OPERAND
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.SECOND_OPERAND
                + ", f." + DBStruct.VFE_CS_CAM_ELEMENT_FILTERS.CAMPAIGN_ID
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE
                + ",  elmnts." + DBStruct.DWH_ELEMENTS.DISPLAY_COMM
                + ", elmnts." + DBStruct.DWH_ELEMENTS.FILE_INDEX
                + ", elmnts." + DBStruct.DWH_ELEMENTS.COLUMN_NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DESCRIPTION
                + ", elmnts." + DBStruct.DWH_ELEMENTS.NAME
                + ", elmnts." + DBStruct.DWH_ELEMENTS.DATA_TYPE
                + ", filterlov." + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.VALUE_ID
                + ", filterlov." + DBStruct.VFE_CS_CAM_ELEMENT_FILTER_LOV.FILTER_VALUE_ID
                + ", elmntlov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL);
        query = sb.toString();

        return query;
    }

    public ArrayList<FileModel> retrieveCampaignFiles(long campaignId, Connection conn) throws CommonException {
        String methodName = "retrieveCampaignFiles";
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Start [" + methodName + "]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Started").build());
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<FileModel> ret = new ArrayList<>();
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM ").append(DBStruct.VFE_CS_CAMPAIGNS_FILES.TABLE_NAME);
            query.append(" WHERE ").append(DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_ID).append(" = ?");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Query [" + query + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setLong(1, campaignId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (ret == null) {
                    ret = new ArrayList<FileModel>();
                }
                FileModel currentFile = new FileModel();
                currentFile.setFileID(rs.getInt(DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_ID));
                currentFile.setFileName(rs.getString(DBStruct.VFE_CS_CAMPAIGNS_FILES.CAMPAIGN_FILE_NAME));
                ret.add(currentFile);
            }
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Ended").build());
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
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
                CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
        return ret;
    }

    public void ChangeCampaignStatusToApproved(Connection connection, CampaignModel campaignModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "[ChangeCampaignStatusToDelete] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatusToDelete Started").build());

        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;

        try {

            sql = new StringBuilder();
            sql.append("update ").append(DBStruct.VFE_CS_CAMPAIGNS.TABLE_NAME)
                    .append(" set ").append(DBStruct.VFE_CS_CAMPAIGNS.STATUS).append(" = ")
                    .append(GeneralConstants.STATUS_APPROVED_VALUE)
                    .append(" where ")
                    .append(DBStruct.VFE_CS_CAMPAIGNS.VERSION_ID).append(" = ")
                    .append(campaignModel.getVersionId());
            //.append(" and ")
//                    .append(DBStruct.SMSC.STATUS).append(" = 2");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatusToDelete Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();

//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [ChangeCampaignStatusToApproved] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatusToDelete Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [ChangeCampaignStatusToApproved]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [ChangeCampaignStatusToApproved]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();

            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [ChangeCampaignStatusToApproved]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    /**
     * Called to retrieve the customers of a campaign .
     *
     * @param connection
     * @param runId
     * @param campaignId
     * @param suspendedNumber
     * @return list of CustomersCampaignsModel
     * @throws CommonException
     */
    public ArrayList<CustomersCampaignsModel> getCustomerCampaignsByCampaign(Connection connection, Integer runId, Long campaignId, Integer suspendedNumber) throws CommonException {
        String methodName = "getCustomerCampaignsByCampaign";
//        CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [" + methodName + "] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sqlQuery = null;
        ResultSet resultSet = null;

        try {
            sqlQuery = new StringBuilder();
            sqlQuery.append("Select * from ").append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.TABLE_NAME)
                    .append(" where ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.RUN_ID).append(" = ?")
                    .append(" and ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.CAMPAIGN_ID).append(" = ?")
                    .append(" and ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.SUSPENDED).append(" = ?");
            statement = connection.prepareStatement(sqlQuery.toString());
            statement.setInt(1, runId);
            statement.setLong(2, campaignId);
            statement.setInt(3, suspendedNumber);
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [Query] " + sqlQuery.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sqlQuery).build());
            resultSet = statement.executeQuery();

            ArrayList<CustomersCampaignsModel> customerCampaignModels = new ArrayList<>();
            while (resultSet.next()) {
                CustomersCampaignsModel customerCampaignsModel = new CustomersCampaignsModel();
                customerCampaignsModel.setCampaignId(resultSet.getLong(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.CAMPAIGN_ID));
                customerCampaignsModel.setLastMSISDNTwoDigits(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.MSISDN_LAST_TWO_DIGITS));
                customerCampaignsModel.setMsisdn(resultSet.getString(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.MSISDN));
                customerCampaignsModel.setRunId(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.RUN_ID));
                customerCampaignsModel.setSuspended(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_CAMPAIGNS.SUSPENDED));
                customerCampaignModels.add(customerCampaignsModel);
            }
//            CommonLogger.businessLogger.info(CampaignDAO.class.getName() + " || Customer Campaign List Size :[" + customerCampaignModels.size() + "]  Retrieving Date time [:" + (System.currentTimeMillis() - startTime) + "] msec");
//            CommonLogger.businessLogger.debug(CampaignDAO.class.getName() + " || " + " [" + methodName + "] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, customerCampaignModels.size())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            return customerCampaignModels;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [" + methodName + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GENERAL_ERROR + "---->  for [" + methodName + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

}
