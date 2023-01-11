/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MatchingInstanceModel;
import com.asset.contactstrategy.common.models.UploadProcedureResult;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Zain Al-Abedin
 */
public class CustomersMatchingDAO {

    public MatchingInstanceModel getInstance(Connection connection, int srcId, String instanceId) throws CommonException {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        long startime = System.currentTimeMillis();
        MatchingInstanceModel instance = null;
        try {
            String query = " Select * FROM " + DBStruct.INSTANCES.TABLE_NAME + " WHERE "
                    + DBStruct.INSTANCES.ENGINE_SRC_ID + " = ? AND  LOWER("
                    + DBStruct.INSTANCES.INSTANCE_ID + ")  like  LOWER(?)";
            pstm = connection.prepareStatement(query);
            pstm.setInt(1, srcId);
            pstm.setString(2, instanceId);
            rs = pstm.executeQuery();
            while (rs.next()) {
                instance = new MatchingInstanceModel();
                instance.setSrcId(srcId);
                instance.setInstanceId(rs.getString(DBStruct.INSTANCES.INSTANCE_ID).trim());
                int active = rs.getInt(DBStruct.INSTANCES.ACTIVE);
                boolean activeBoolean = active == GeneralConstants.TRUE ? true : false;
                instance.setActive(activeBoolean);
                instance.setLastUpdateDate(rs.getTimestamp(DBStruct.INSTANCES.LAST_UPDATE_DATE));
                instance.setRunId(rs.getInt(DBStruct.INSTANCES.RUN_ID));
                instance.setStatus(rs.getInt(DBStruct.INSTANCES.STATUS));
                instance.setSubPartitionEnd(rs.getInt(DBStruct.INSTANCES.SUB_PARTITION_END));
                instance.setSubPartitionStart(rs.getInt(DBStruct.INSTANCES.SUB_PARTITION_START));
            }
//            CommonLogger.businessLogger.info("Get Matching Instance Model For Engine=["+srcId+"] InstanceID=["+instanceId+"] "
//                    + "in ["+(System.currentTimeMillis()-startime)+"]msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Get Matching Instance Model For Engine")
                    .put(GeneralConstants.StructuredLogKeys.ENGINE, srcId)
                    .put(GeneralConstants.StructuredLogKeys.INSTANCE_ID, instanceId)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [getInstance] Failed To Get Model For InstanceID=[" + instanceId + "] "
                    + " Engine=[" + srcId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [getInstance] Failed To Get Model For InstanceID=[" + instanceId + "] "
                    + " Engine=[" + srcId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, pstm);
        }
        return instance;

    }

    public void updateInstanceModel(Connection connection, MatchingInstanceModel instance) throws CommonException {
        PreparedStatement pstm = null;
        long startime = System.currentTimeMillis();
        try {
            String query = " Update " + DBStruct.INSTANCES.TABLE_NAME + " SET "
                    + DBStruct.INSTANCES.RUN_ID + " = ? , "
                    + DBStruct.INSTANCES.STATUS + " = ? , "
                    + DBStruct.INSTANCES.LAST_UPDATE_DATE
                    + " = systimestamp "
                    + "  Where  "
                    + DBStruct.INSTANCES.ENGINE_SRC_ID + " = ? AND LOWER("
                    + DBStruct.INSTANCES.INSTANCE_ID + " )  like  LOWER(?) ";
            pstm = connection.prepareStatement(query);
            pstm.setInt(1, instance.getRunId());
            pstm.setInt(2, instance.getStatus());
            pstm.setInt(3, instance.getSrcId());
            pstm.setString(4, instance.getInstanceId().trim());
            pstm.executeUpdate();
//            CommonLogger.businessLogger.info("Successfully Update Model InstanceID=[" + instance.getInstanceId()
//                    + "] Engine=[" + instance.getSrcId() + "] in [" + (System.currentTimeMillis() - startime) + "]msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successfully Update Model")
                    .put(GeneralConstants.StructuredLogKeys.ENGINE, instance.getSrcId())
                    .put(GeneralConstants.StructuredLogKeys.INSTANCE_ID, instance.getInstanceId())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateInstancesRunId] Failed To Update Model For "
                    + "InstanceID=[" + instance.getInstanceId() + "] Engine=[" + instance.getSrcId() + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateInstancesRunId] Failed To Update Model For "
                    + "InstanceID=[" + instance.getInstanceId() + "] Engine=[" + instance.getSrcId() + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstm);
        }
    }

    public void updateInstancesRunId(Connection connection, int runId) throws CommonException {
        PreparedStatement pstm = null;
        boolean finished = false;
        try {
            String query = "Update " + DBStruct.INSTANCES.TABLE_NAME + " Set " + DBStruct.INSTANCES.RUN_ID + " = ?"
                    + " Where " + DBStruct.INSTANCES.ACTIVE + " =? ";
            pstm = connection.prepareStatement(query);
            pstm.setInt(1, runId);
            pstm.setInt(2, GeneralConstants.TRUE);
            pstm.executeUpdate();
//            CommonLogger.businessLogger.info(" Update Instances Run_ID=[" + runId + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Update Instance")
                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, runId).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateInstancesRunId] Failed To Update Instances Run_ID=[" + runId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateInstancesRunId] Failed To Update Instances Run_ID=[" + runId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, pstm);
        }

    }

    public boolean allInstancesSuccessed(Connection connection, int dwhRunId) throws CommonException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean success = true;
        try {
            String query = "select * from " + DBStruct.INSTANCES.TABLE_NAME + "  where " + DBStruct.INSTANCES.RUN_ID + " =? "
                    + " AND " + DBStruct.INSTANCES.ACTIVE + " =?  AND " + DBStruct.INSTANCES.STATUS + " <> ? ";
            pstm = connection.prepareStatement(query.toString());
            pstm.setInt(1, dwhRunId);
            pstm.setInt(2, GeneralConstants.TRUE);
            pstm.setInt(3, GeneralConstants.TRUE);
            rs = pstm.executeQuery();

            if (rs.next()) {
                success = false;
            }

            return success;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [allInstancesSuccessed] Failed To Check Instances For DWH RUN_ID=[" + dwhRunId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [allInstancesSuccessed] Failed To Check Instances For DWH RUN_ID=[" + dwhRunId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, pstm);
        }

    }

    public boolean allInstancesFinished(Connection connection, int dwhRunId) throws CommonException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean finished = true;
        try {
            String query = "select * from " + DBStruct.INSTANCES.TABLE_NAME + "  where " + DBStruct.INSTANCES.RUN_ID + " < ? "
                    + " AND " + DBStruct.INSTANCES.ACTIVE + " =? ";
            pstm = connection.prepareStatement(query.toString());
            pstm.setInt(1, dwhRunId);
            pstm.setInt(2, GeneralConstants.TRUE);
            rs = pstm.executeQuery();

            if (rs.next()) {
                finished = false;
            }
            return finished;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [allInstancesFinished] Failed To Check Instances For DWH RUN_ID=[" + dwhRunId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [allInstancesFinished] Failed To Check Instances For DWH RUN_ID=[" + dwhRunId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, pstm);
        }

    }

    public boolean CompareTimeStamps(Connection connection, String firstItemKey, int firstSrcId, String secondItemKey, int secondSrcId) throws CommonException {
        long startime = System.currentTimeMillis();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean larger = false;
        try {
            String query = "SELECT CASE "
                    + "WHEN TO_TIMESTAMP ( (select " + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE + "  from " + DBStruct.SYSTEM_PROPERITES.TABLE_NAME
                    + "  where  " + DBStruct.SYSTEM_PROPERITES.ITEM_KEY + " =? and " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + "=?  ),'"
                    + GeneralConstants.SYSTEM_PROPERTY_DATE_FORMAT + "') > "
                    + "TO_TIMESTAMP ( (select " + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE + " from " + DBStruct.SYSTEM_PROPERITES.TABLE_NAME
                    + " where  " + DBStruct.SYSTEM_PROPERITES.ITEM_KEY + " =? and " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + "=?  ),'"
                    + GeneralConstants.SYSTEM_PROPERTY_DATE_FORMAT + "') "
                    + "THEN 1 ELSE 0 END R FROM dual;";
            pstm = connection.prepareStatement(query.toString());
            pstm.setString(1, firstItemKey);
            pstm.setInt(2, firstSrcId);
            pstm.setString(3, secondItemKey);
            pstm.setInt(4, secondSrcId);
            rs = pstm.executeQuery();
            while (rs.next()) {
                int result = rs.getInt(1);
                if (result == 1) {
                    larger = true;
                }
            }

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [CompareTimeStamps] Failed To Compare FirstKey=[" + firstItemKey + "] "
                    + "secondKey=[" + secondItemKey + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [CompareTimeStamps] Failed To Compare FirstKey=[" + firstItemKey + "] "
                    + "secondKey=[" + secondItemKey + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, pstm);
        }
        return larger;
    }

    public void addGroupsCustomersNewPartation(Connection connection, int partitionId, String tableName) throws CommonException {
        long startime = System.currentTimeMillis();
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting addGroupsCustomersNewPartation");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting addGroupsCustomersNewPartation").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_ADD_SMS_GROUPS_CUSTOMERS_PARTITION;
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?)");
            callableStatement.setInt(1, partitionId);
            callableStatement.setString(2, tableName);
            callableStatement.executeQuery();
//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended addGroupsCustomersNewPartation from Table=[" + tableName + "] PartitionID=[" + partitionId + "] in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended addGroupsCustomersNewPartation")
                    .put(GeneralConstants.StructuredLogKeys.TABLE_NAME, tableName)
                    .put(GeneralConstants.StructuredLogKeys.PARTITION_ID, partitionId)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [addGroupsCustomersNewPartation] patitionId=[" + partitionId + "] Table=[" + tableName + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [addGroupsCustomersNewPartation] patitionId=[" + partitionId + "] Table=[" + tableName + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
    }

    public void dropGroupsCustomersPartation(Connection connection, int partitionId, String tableName) throws CommonException {
        long startime = System.currentTimeMillis();
//        CommonLogger.businesssLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting dropGroupsCustomersPartation");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting dropGroupsCustomersPartition").build());

        CallableStatement callableStatement = null;

        try {
            String procedureName = DBStruct.PRD_DROP_SMS_GROUPS_CUSTOMERS_PARTITION;

            // create Statement for querying database
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?)");

            callableStatement.setInt(1, partitionId);
            callableStatement.setString(2, tableName);

            callableStatement.executeQuery();

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended dropGroupsCustomersPartation from Table=[" + tableName + "] PartitionID=[" + partitionId + "] in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended dropGroupsCustomersPartation")
                    .put(GeneralConstants.StructuredLogKeys.TABLE_NAME, tableName)
                    .put(GeneralConstants.StructuredLogKeys.PARTITION_ID, partitionId)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [dropGroupsCustomersPartation] patitionId=[" + partitionId + "] Table=[" + tableName + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [dropGroupsCustomersPartation] patitionId=[" + partitionId + "] Table=[" + tableName + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }

    }

    public long matchSMSGroupsCustomersByCriteria(Connection connection, int groupId, int runId, int lastMSISDNTwoDigits, String filterQuery) throws CommonException {
        long startime = System.currentTimeMillis();
        long affectedRows = 0;
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting matchSMSGroupsCustomersByCriteria");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting matchSMSGroupCustumersByCriteria").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_MATCH_SMS_GROUPS_CUSTOMERS_CRITERIA;
            // create Statement for querying database

            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?)");

            callableStatement.setInt(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setInt(3, lastMSISDNTwoDigits);
            callableStatement.setString(4, filterQuery);
            callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            affectedRows = callableStatement.getLong(5);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended matchSMSGroupsCustomersByCriteria in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended matchSMSGroupsCustomersByCriteria")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "] filterQuery=[" + filterQuery + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "] filterQuery=[" + filterQuery + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return affectedRows;

    }

    public long matchADSGroupsCustomersByCriteria(Connection connection, int groupId, int runId, int lastMSISDNTwoDigits, String filterQuery) throws CommonException {
        long startime = System.currentTimeMillis();
        long affectedRows = 0;
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting matchADSGroupsCustomersByCriteria");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting matchADSGroupsCustomersByCriteria").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_MATCH_ADS_GROUPS_CUSTOMERS_CRITERIA;
            // create Statement for querying database

            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?)");

            callableStatement.setInt(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setInt(3, lastMSISDNTwoDigits);
            callableStatement.setString(4, filterQuery);
            callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            affectedRows = callableStatement.getLong(5);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended matchADSGroupsCustomersByCriteria in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End matchADSGroupsCustomersByCriteria")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "] filterQuery=[" + filterQuery + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchADSGroupsCustomersByCriteria]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return affectedRows;

    }

    public long matchCampaignGroupsCustomersByCriteria(Connection connection, long groupId, int runId, int lastMSISDNTwoDigits, String filterQuery, long max_targeted_customers) throws CommonException {
        long startime = System.currentTimeMillis();
        long affectedRows = 0;
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting matchCampaignGroupsCustomersByCriteria");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start matchCampaignGroupsCustomersByCriteria").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_MATCH_CAMPAIGN_GROUPS_CUSTOMERS_CRITERIA;
            // create Statement for querying database

            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?,?)");

            callableStatement.setLong(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setInt(3, lastMSISDNTwoDigits);
            callableStatement.setString(4, filterQuery);
            callableStatement.setLong(5, max_targeted_customers);
            callableStatement.registerOutParameter(6, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            affectedRows = callableStatement.getLong(6);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended matchCampaignGroupsCustomersByCriteria in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End matchCampaignGroupsCustomersByCriteria")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "] filterQuery=[" + filterQuery + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "] filterQuery=[" + filterQuery + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return affectedRows;

    }

    public UploadProcedureResult matchSMSGroupsCustomersByUpload(Connection connection, int groupId, int runId, int FileId) throws CommonException {
        long startime = System.currentTimeMillis();
        long affectedRows = 0;
        long msisdnsCount = 0;
        UploadProcedureResult result = new UploadProcedureResult();
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting matchSMSGroupsCustomersByUpload");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start matchSMSGroupsCustomersByUpload").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_MATCH_SMS_GROUPS_CUSTOMERS_UPLOAD;
            // create Statement for querying database
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?)");

            callableStatement.setInt(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setInt(3, FileId);

            callableStatement.registerOutParameter(4, java.sql.Types.NUMERIC);
            callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            affectedRows = callableStatement.getLong(4);
            msisdnsCount = callableStatement.getLong(5);
            result.setAffectedRows(affectedRows);
            result.setMsisdnCount(msisdnsCount);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended matchSMSGroupsCustomersByUpload in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End matchSMSGroupsCustomersByUpload")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " fileId=[" + FileId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " fileId=[" + FileId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return result;
    }

    // called for sms/ads groups.
    public UploadProcedureResult matchGroupsCustomersByUpload(Connection connection, long groupId, int runId, int FileId, String fileTable, String customerTable, String groupIdCol) throws CommonException {
        long startime = System.currentTimeMillis();
        long affectedRows = 0;
        long msisdnsCount = 0;
        UploadProcedureResult result = new UploadProcedureResult();
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting matchGroupsCustomersByUpload");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start matchGroupsCustomersByUpload").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_MATCH_GROUPS_CUSTOMERS_UPLOAD;
            // create Statement for querying database
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?,?,?,?)");

            callableStatement.setLong(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setInt(3, FileId);
            callableStatement.setString(4, fileTable);
            callableStatement.setString(5, customerTable);
            callableStatement.setString(6, groupIdCol);

            callableStatement.registerOutParameter(7, java.sql.Types.NUMERIC);
            callableStatement.registerOutParameter(8, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            affectedRows = callableStatement.getLong(7);
            msisdnsCount = callableStatement.getLong(8);
            result.setAffectedRows(affectedRows);
            result.setMsisdnCount(msisdnsCount);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended matchGroupsCustomersByUpload in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended matchGroupsCustomersByUpload")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchGroupsCustomersByUpload] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " fileId=[" + FileId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchGroupsCustomersByUpload] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " fileId=[" + FileId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return result;
    }

    // called for sms/ads groups.
    public long matchGroupsCustomersByCriteria(Connection connection, long groupId, int runId, int lastMSISDNTwoDigits, String filterQuery, String groupTable, String groupIdColumnName, String customerTable) throws CommonException {
        long startime = System.currentTimeMillis();
        long affectedRows = 0;
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting matchGroupsCustomersByCriteria");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start matchGroupsCustomersByCriteria").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_MATCH_GROUPS_CUSTOMERS_CRITERIA;
            // create Statement for querying database

            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?,?,?,?)");

            callableStatement.setLong(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setInt(3, lastMSISDNTwoDigits);
            callableStatement.setString(4, filterQuery);
            callableStatement.setString(5, groupTable);
            callableStatement.setString(6, groupIdColumnName);
            callableStatement.setString(7, customerTable);
            callableStatement.registerOutParameter(8, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            affectedRows = callableStatement.getLong(8);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended matchGroupsCustomersByCriteria in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended matchGroupsCustomersByCriteria")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " LastMSISDNTwoDigits=[" + lastMSISDNTwoDigits + "] filterQuery=[" + filterQuery + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchGroupsCustomersByCriteria] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return affectedRows;
    }

    public UploadProcedureResult matchADSGroupsCustomersByUpload(Connection connection, int groupId, int runId, int FileId) throws CommonException {
        long startime = System.currentTimeMillis();
        long affectedRows = 0;
        long msisdnsCount = 0;
        UploadProcedureResult result = new UploadProcedureResult();
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting matchADSGroupsCustomersByUpload");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start matchADSGroupsCustomersByUpload").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_MATCH_ADS_GROUPS_CUSTOMERS_UPLOAD;
            // create Statement for querying database
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?)");

            callableStatement.setInt(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setInt(3, FileId);

            callableStatement.registerOutParameter(4, java.sql.Types.NUMERIC);
            callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            affectedRows = callableStatement.getLong(4);
            msisdnsCount = callableStatement.getLong(5);
            result.setAffectedRows(affectedRows);
            result.setMsisdnCount(msisdnsCount);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended matchADSGroupsCustomersByUpload in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended matchADSGroupsCustomersByUpload")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " fileId=[" + FileId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " fileId=[" + FileId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return result;
    }

    public UploadProcedureResult matchCampaignGroupsCustomersByUpload(Connection connection, long groupId, int runId, int FileId) throws CommonException {
        long startime = System.currentTimeMillis();
        long affectedRows = 0;
        long msisdnsCount = 0;
        UploadProcedureResult result = new UploadProcedureResult();
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting matchCampaignGroupsCustomersByUpload");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start matchCampaignGroupsCustomersByUpload").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_MATCH_CAMPAIGN_GROUPS_CUSTOMERS_UPLOAD;
            // create Statement for querying database
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?)");

            callableStatement.setLong(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setInt(3, FileId);

            callableStatement.registerOutParameter(4, java.sql.Types.NUMERIC);
            callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            affectedRows = callableStatement.getLong(4);
            msisdnsCount = callableStatement.getLong(5);
            result.setAffectedRows(affectedRows);
            result.setMsisdnCount(msisdnsCount);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended matchCampaignGroupsCustomersByUpload in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End matchCampaignGroupsCustomersByUpload")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " fileId=[" + FileId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " fileId=[" + FileId + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return result;
    }

    public UploadProcedureResult deleteSuspendCampaign(Connection connection, long groupId, int runId, long maxTargetedCustomers, long customersToSuspend) throws CommonException {
        long startime = System.currentTimeMillis();
        long deletedRows = 0;
        long matchedCustomers = 0;
        UploadProcedureResult result = new UploadProcedureResult();
//        CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Starting deleteSuspendCampaign");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start deleteSuspendCampaign").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_DELETE_EXTRA_CAMPAIGN_CUSTOMERS;
            // create Statement for querying database
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?,?,?,?,?,?)");

            callableStatement.setLong(1, groupId);
            callableStatement.setInt(2, runId);
            callableStatement.setLong(3, maxTargetedCustomers);
            callableStatement.setLong(4, customersToSuspend);

            callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC);
            callableStatement.registerOutParameter(6, java.sql.Types.NUMERIC);

            callableStatement.executeQuery();

            deletedRows = callableStatement.getLong(5);
            matchedCustomers = callableStatement.getLong(6);
            result.setAffectedRows(deletedRows);
            result.setMsisdnCount(matchedCustomers);

//            CommonLogger.businessLogger.info(CustomersMatchingDAO.class.getName() + " || " + "Ended deleteSuspendCampaign in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start deleteSuspendCampaign")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " maxTargetedCustomers=[" + maxTargetedCustomers + "] customersToSuspend=[" + customersToSuspend + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [matchSMSGroupsCustomersByCriteria] groupId=[" + groupId + "] runId=[" + runId + "] "
                    + " maxTargetedCustomers=[" + maxTargetedCustomers + "] customersToSuspend=[" + customersToSuspend + "]" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
        return result;
    }

}
