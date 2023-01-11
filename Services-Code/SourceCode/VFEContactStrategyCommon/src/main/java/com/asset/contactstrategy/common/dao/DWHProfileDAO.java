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
import com.asset.contactstrategy.common.models.LineModel;
import com.asset.contactstrategy.interfaces.models.CustomersModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Zain Al-Abedin
 */
public class DWHProfileDAO {

    public void insertDWHProfileBatch(Connection connection, String SQLStatment, Vector<LineModel> dwListbatch) throws CommonException {
        long startime = System.currentTimeMillis();
        //   CommonLogger.businessLogger.debug(DWHProfileDAO.class.getName() + " || " + "Starting insertDWHProfileBatch");
        PreparedStatement preparedStatement = null;
        int batchSize = 0;
        long executeTime = -1;
        try {
            preparedStatement = connection.prepareStatement(SQLStatment);
            batchSize = dwListbatch.size();
            for (LineModel customerProfile : dwListbatch) {
                try {
                    for (int i = 0; i < customerProfile.getInsertData().size(); i++) {
                        Object object = customerProfile.getInsertData().get(i);
                        if (object != null) {
                            if (object instanceof Double) {
                                preparedStatement.setDouble(i + 1, (Double) object);
                            } else if (object instanceof String) {
                                preparedStatement.setString(i + 1, (String) object);
                            } else if (object instanceof Date) {
                                java.sql.Timestamp test = new java.sql.Timestamp(((Date) object).getTime());
                                preparedStatement.setTimestamp(i + 1, test);
                            } else if (object instanceof Integer) {
                                preparedStatement.setInt(i + 1, (Integer) object);
                            } else {
//                                CommonLogger.businessLogger.debug(DWHProfileDAO.class.getName() + " || " + "insertDWHProfileBatch Line=[" + customerProfile.getLineNum() + "] from File=[" + customerProfile.getFileName() + "] Contains unknown object -->" + object.toString());
//                                CommonLogger.businessLogger.info(DWHProfileDAO.class.getName() + " || " + "insertDWHProfileBatch Line=[" + customerProfile.getLineNum() + "] from File=[" + customerProfile.getFileName() + "] Contains unknown object -->" + object.toString());
                                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " insertDWHProfileBatch, Line Contains Unknown Object")
                                        .put(GeneralConstants.StructuredLogKeys.LINE_NUMBER, customerProfile.getLineNum())
                                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, customerProfile.getFileName())
                                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " insertDWHProfileBatch, Line Contains Unknown Object")
                                        .put(GeneralConstants.StructuredLogKeys.LINE_NUMBER, customerProfile.getLineNum())
                                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, customerProfile.getFileName())
                                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
                            }
                        } else {
                            preparedStatement.setNull(i + 1, Types.NULL);
                        }
                    }
                } catch (Exception e) {
                    CommonLogger.errorLogger.error("Error Inserting Line=[" + customerProfile.getLineNum() + "] from File=[" + customerProfile.getFileName() + "] Line Date=[" + customerProfile.getInsertData() + "]", e);
                    CommonLogger.businessLogger.error("Error Inserting Line=[" + customerProfile.getLineNum() + "] from File=[" + customerProfile.getFileName() + "] Line Date=[" + customerProfile.getInsertData() + "]", e);
                    throw new CommonException("Error Inserting Line=[" + customerProfile.getLineNum() + "] from File=[" + customerProfile.getFileName() + "]" + e.getMessage(), ErrorCodes.ERROR_IN_INSERTING_DWH_LINE);
                }
                customerProfile.setInsertData(null);//getInsertData().clear();//why do we clear here???
                preparedStatement.addBatch();
            }

            executeTime = System.currentTimeMillis();
            int[] garbage = preparedStatement.executeBatch(); // insert remaining records           
            preparedStatement.clearBatch();//we should clear here :D:D.
            //  connection.commit();
        } catch (SQLException e) {
//            CommonLogger.businessLogger.info("Connection Timeout After [" + (System.currentTimeMillis() - executeTime) + "]msec  executeTime=[" + executeTime + "]");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Connection Timeout")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - executeTime)).build());
            CommonLogger.errorLogger.error("Exception---->  for [insertDWHProfileBatch] ", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [insertDWHProfileBatch] ", e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, preparedStatement);
        }
//        CommonLogger.businessLogger.debug(" || Inserting Customer Profile Batch with size" + batchSize + " in [" + (System.currentTimeMillis() - startime) + "] msec");
//        CommonLogger.businessLogger.info(" ||  Inserting Customer Profile Batch with size[" + batchSize + "] in [" + (System.currentTimeMillis() - startime) + "] msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Inserting CustomerProfile Batch")
                .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, batchSize)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " insertDWHProfileBatch, Line Contains Unknown Object")
                .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, batchSize)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());

    }

    public void addDWHProfileNewPartation(Connection connection, int partitionId) throws CommonException {
        long startime = System.currentTimeMillis();
//        CommonLogger.businessLogger.debug(DWHProfileDAO.class.getName() + " || " + "Starting addDWHProfileNewPartation");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " addDWHProfileNewPartition Started").build());
        CallableStatement callableStatement = null;
        try {
            String procedureName = DBStruct.PRD_ADD_PROFILE_PARTITION;
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?)");
            callableStatement.setInt(1, partitionId);
            callableStatement.executeUpdate();
//            CommonLogger.businessLogger.debug(DWHProfileDAO.class.getName() + " || " + "Ended addDWHProfileNewPartation in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " addDWHProfileNewPartition Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [addDWHProfileNewPartation]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [addDWHProfileNewPartation] ", e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }
    }

    public void dropDWHProfilePartation(Connection connection, int partitionId) throws CommonException {
        long startime = System.currentTimeMillis();
//        CommonLogger.businessLogger.debug(DWHProfileDAO.class.getName() + " || " + "Starting dropDWHProfilePartation");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " dropDWHProfilePartation Started").build());

        CallableStatement callableStatement = null;

        try {
            String procedureName = DBStruct.PRD_DROP_PROFILE_PARTITION;

            // create Statement for querying database
            callableStatement
                    = connection.prepareCall("call "
                            + procedureName + "(?)");

            callableStatement.setInt(1, partitionId);

            callableStatement.executeUpdate();

//            CommonLogger.businessLogger.debug(DWHProfileDAO.class.getName() + " || " + "Ended dropDWHProfilePartation in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " dropDWHProfilePartation Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [addDWHProfileNewPartation]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [addDWHProfileNewPartation] ", e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, null, callableStatement);
        }

    }

    public CustomersModel getCustomer(Connection connection, String MSISDN) throws CommonException {
        CommonLogger.businessLogger.debug("Starting getCustomer");

        PreparedStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;

        int last2Digits = Integer.parseInt(MSISDN.substring(MSISDN.length() - 2, MSISDN.length()));
        int runId = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY));
        String langColumn = SystemLookups.SYSTEM_PROPERTIES.get(Defines.LANGUAGE_COLUMN);
        String arabicLang = SystemLookups.SYSTEM_PROPERTIES.get(Defines.ARABIC_LANG);
        String englishLang = SystemLookups.SYSTEM_PROPERTIES.get(Defines.ENGLISH_LANG);
        try {
            sql.append("SELECT * FROM ").append(DBStruct.DWH_CUSTOMERS.TBL_NAME).append(" ")
                    .append(" WHERE ").append(DBStruct.DWH_CUSTOMERS.MSISDN).append(" = ? AND ")
                    .append(DBStruct.DWH_CUSTOMERS.LAST_MSISDN_TWO_DIGITS).append(" = ? AND ")
                    .append(DBStruct.DWH_CUSTOMERS.RUN_ID).append(" = ? ");

//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + csMsgId + " | Attempting Query: " + sql.toString() + " | MSISDN: " + MSISDN + "| RunId:" + runId);
//            CommonLogger.businessLogger.info("Attempting Query: " + sql.toString() + " | MSISDN: " + MSISDN + "| RunId:" + runId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " getCustomer Stats")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString())
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, MSISDN)
                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, runId).build());

            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, MSISDN);
            statement.setInt(2, last2Digits);
            statement.setInt(3, runId);

            resultSet = statement.executeQuery();
            CustomersModel customersModel = null;

            if (resultSet.next()) {
                customersModel = new CustomersModel();
                customersModel.setMsisdn(resultSet.getString(DBStruct.DWH_CUSTOMERS.MSISDN));
                customersModel.setLastTwoDigits(resultSet.getInt(DBStruct.DWH_CUSTOMERS.LAST_MSISDN_TWO_DIGITS));
                customersModel.setGovernmentId(resultSet.getInt(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID));
                customersModel.setCustomerType(resultSet.getInt(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE));
                customersModel.setRatePlan(resultSet.getInt(DBStruct.DWH_CUSTOMERS.RATE_PLAN));
//
//                CommonLogger.businessLogger.info("Customer MSISDN: " + customersModel.getMsisdn()
//                        + " | Customer LAST_MSISDN_TWO_DIGITS: " + customersModel.getLastTwoDigits()
//                        + " | Customer GOVERNMENT_ID: " + customersModel.getGovernmentId()
//                        + " | Customer CUSTOMER_TYPE: " + customersModel.getCustomerType()
//                        + " | Customer RATE_PLAN: " + customersModel.getRatePlan());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " CustomerModel info")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN, customersModel.getMsisdn())
                        .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customersModel.getLastTwoDigits())
                        .put(GeneralConstants.StructuredLogKeys.GOVERNMENT_ID, customersModel.getGovernmentId())
                        .put(GeneralConstants.StructuredLogKeys.RATE_PLAN, customersModel.getRatePlan()).build());
                if (langColumn != null) {
                    customersModel.setLanguage(resultSet.getString(langColumn));
                    if (customersModel.getLanguage() == null || (!customersModel.getLanguage().equalsIgnoreCase(englishLang) && !customersModel.getLanguage().equalsIgnoreCase(arabicLang))) {
                        customersModel.setLanguage(arabicLang);
                    }

//                    if (customersModel.getLanguage() == null)
//                        customersModel.setLanguage(arabicLang);
                } else {
                    customersModel.setLanguage(arabicLang);
                }

            }
//            else
//            {
//                throw new CommonException("Customer Not Found in DWH", ErrorCodes.GETTING_USER);
//            }
            return customersModel;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for getCustomer()" + e);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomer()", e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomer()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomer()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }
    }

    public CustomersModel getCustomerWithGroups(Connection connection, String MSISDN) throws CommonException {
        CommonLogger.businessLogger.debug("Starting getCustomer");

        CustomersModel customer = getCustomer(connection, MSISDN);

        if (customer == null) {
//            CommonLogger.businessLogger.debug("Customer with MSISDN: " + MSISDN + " not found in DWH");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Not Found in DWH")
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, MSISDN).build());
            customer = new CustomersModel();
        }

        PreparedStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;

        int last2Digits = Integer.valueOf(MSISDN.substring(MSISDN.length() - 2, MSISDN.length()));
        int runId = Integer.valueOf(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY));

        try {
            sql.append("SELECT ").append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID)
                    .append(" FROM ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME)
                    .append(" WHERE ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.MSISDN).append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.LAST_TWO_DIGITS).append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.RUN_ID).append(" = ? ");

//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + csMsgId + " Attempting Query: " + sql.toString() + " | MSISDN: " + MSISDN + "| RunId:" + runId);
//            CommonLogger.businessLogger.info("Attempting Query: " + sql.toString() + " | MSISDN: " + MSISDN + "| RunId:" + runId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCustomerWithGroups Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString())
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, MSISDN)
                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, runId).build());

            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, MSISDN);
            statement.setInt(2, last2Digits);
            statement.setInt(3, runId);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                customer.setSmsGroupId(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID));
//                CommonLogger.businessLogger.info("Customer ADS_GROUP_ID: " + customer.getAdsGroupId());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Group info")
                        .put(GeneralConstants.StructuredLogKeys.GROUP_ID, customer.getAdsGroupId()).build());
            }

            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomer()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomer()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            sql = new StringBuilder();
            sql.append("SELECT ").append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.ADS_GROUP_ID)
                    .append(" FROM ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.TABLE_NAME)
                    .append(" WHERE ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.MSISDN).append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.LAST_TWO_DIGITS).append(" = ? AND ")
                    .append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.RUN_ID).append(" = ? ");

//            CommonLogger.businessLogger.info("Attempting Query: " + sql.toString() + " | MSISDN: " + MSISDN + "| RunId:" + runId);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCustomerWithGroups Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString())
                    .put(GeneralConstants.StructuredLogKeys.MSISDN, MSISDN)
                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, runId).build());

            statement.close();
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, MSISDN);
            statement.setInt(2, last2Digits);
            statement.setInt(3, runId);

            resultSet.close();
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                customer.setAdsGroupId(resultSet.getInt(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.ADS_GROUP_ID));
//                CommonLogger.businessLogger.info("Customer ADS_GROUP_ID: " + customer.getAdsGroupId());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Group Info")
                        .put(GeneralConstants.StructuredLogKeys.GROUP_ID, customer.getAdsGroupId()).build());
            }
            return customer;
        } catch (SQLException e) {
            CommonLogger.businessLogger.error("Error while Getting Customer SMS Group" + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error while Getting Customer SMS Group" + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomer()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomer()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }
    }

    public HashMap<String, CustomersModel> getCustomersMap(Connection conn, ArrayList<String> MSISDNs) throws CommonException {
        long time = System.currentTimeMillis();
        if (MSISDNs.isEmpty()) {
            throw new CommonException("Empty Msisdn ArrayList... ", ErrorCodes.ERROR_SQL);
        }
        PreparedStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;
//        HashMap<String, String> customersVsLanguage = new HashMap<>();
        HashMap<String, CustomersModel> customersMap = new HashMap<>();

//        int last2Digits = Integer.parseInt(MSISDN.substring(MSISDN.length() - 2, MSISDN.length()));
        int runId = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY));
        String langColumn = SystemLookups.SYSTEM_PROPERTIES.get(Defines.LANGUAGE_COLUMN);
        String arabicLang = SystemLookups.SYSTEM_PROPERTIES.get(Defines.ARABIC_LANG);
        String englishLang = SystemLookups.SYSTEM_PROPERTIES.get(Defines.ENGLISH_LANG);
        int statementIndex = 1;
        String customerLanguage;

        try {
            if (langColumn == null) {
                throw new CommonException("Missing SystemProperties Database Configuration... " + Defines.LANGUAGE_COLUMN, ErrorCodes.ERROR_SQL);
            }
            sql.append("SELECT A.").append(langColumn)
                    .append(",A.").append(DBStruct.DWH_CUSTOMERS.MSISDN)
                    .append(",A.").append(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE)
                    .append(",A.").append(DBStruct.DWH_CUSTOMERS.RATE_PLAN)
                    .append(",A.").append(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID)
                    .append(",A.").append(DBStruct.DWH_CUSTOMERS.LAST_MSISDN_TWO_DIGITS)
                    .append(",B.").append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID).append(" SMS_GROUP")
                    .append(",C.").append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.ADS_GROUP_ID).append(" ADS_GROUP")
                    .append(" FROM ").append(DBStruct.DWH_CUSTOMERS.TBL_NAME).append(" A ")
                    .append("INNER JOIN ").append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME).append(" B ON ")
                    .append("A.").append(DBStruct.DWH_CUSTOMERS.MSISDN).append(" = B.").append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.MSISDN)
                    .append(" AND A.").append(DBStruct.DWH_CUSTOMERS.RUN_ID).append(" = B.").append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.RUN_ID)
                    .append(" AND A.").append(DBStruct.DWH_CUSTOMERS.LAST_MSISDN_TWO_DIGITS).append(" = B.").append(DBStruct.VFE_CS_CUSTOMERS_GROUPS.LAST_TWO_DIGITS)
                    .append(" INNER JOIN ").append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.TABLE_NAME).append(" C ON ")
                    .append("A.").append(DBStruct.DWH_CUSTOMERS.MSISDN).append(" = C.").append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.MSISDN)
                    .append(" AND A.").append(DBStruct.DWH_CUSTOMERS.RUN_ID).append(" = C.").append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.RUN_ID)
                    .append(" AND A.").append(DBStruct.DWH_CUSTOMERS.LAST_MSISDN_TWO_DIGITS).append(" = C.").append(DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.LAST_TWO_DIGITS)
                    .append(" WHERE A.").append(DBStruct.DWH_CUSTOMERS.RUN_ID).append(" = ? AND A.").append(DBStruct.DWH_CUSTOMERS.MSISDN).append(" in (");

            for (long i = 0; i < MSISDNs.size(); i++) {
                if (i != 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }

            sql.append(") AND A." + DBStruct.DWH_CUSTOMERS.LAST_MSISDN_TWO_DIGITS).append(" in (");

            for (long i = 0; i < MSISDNs.size(); i++) {
                if (i != 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }

            sql.append(")");

//            CommonLogger.businessLogger.info("Attempting Query: " + sql.toString() + " | MSISDN: " + MSISDN + "| RunId:" + runId);
//            CommonLogger.businessLogger.info("Attempting Query: " + sql.toString() + " | MSISDN: " + MSISDN + "| RunId:" + runId);
            statement = conn.prepareStatement(sql.toString());
            statement.setInt(statementIndex++, runId);
            for (int i = 0; i < MSISDNs.size(); i++) {
                statement.setString(statementIndex++, MSISDNs.get(i));
            }

            for (int i = 0; i < MSISDNs.size(); i++) {
                statement.setString(statementIndex++, MSISDNs.get(i).substring(MSISDNs.get(i).length() - 2));
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                customerLanguage = resultSet.getString(langColumn);

                if (customerLanguage == null || (!customerLanguage.equalsIgnoreCase(englishLang) && !customerLanguage.equalsIgnoreCase(arabicLang))) {
                    customerLanguage = arabicLang;
                }

//                customersVsLanguage.put(resultSet.getString(DBStruct.DWH_CUSTOMERS.MSISDN), customerLanguage);
                CustomersModel customerModel = new CustomersModel();
                customerModel.setAdsGroupId(resultSet.getInt("ADS_GROUP"));
                customerModel.setCustomerType(resultSet.getInt(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE));
                customerModel.setGovernmentId(resultSet.getInt(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID));
                customerModel.setLanguage(customerLanguage);
                customerModel.setLastTwoDigits(resultSet.getInt(DBStruct.DWH_CUSTOMERS.LAST_MSISDN_TWO_DIGITS));
                customerModel.setMsisdn(resultSet.getString(DBStruct.DWH_CUSTOMERS.MSISDN));
                customerModel.setRatePlan(resultSet.getInt(DBStruct.DWH_CUSTOMERS.RATE_PLAN));
                customerModel.setSmsGroupId(resultSet.getInt("SMS_GROUP"));
                customersMap.put(customerModel.getMsisdn(), customerModel);
            }

//            CommonLogger.businessLogger.info("Finished getCustomersLanguageHashMap() for " + MSISDNs.size() + " customers in " + (System.currentTimeMillis() - time) + "msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished getCustomersLanguageHashMap()")
                    .put(GeneralConstants.StructuredLogKeys.MSISDN_COUNT, MSISDNs.size())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time)).build()
            );
            return customersMap;

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomersLanguageHashMap()" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomersLanguageHashMap()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCustomersLanguageHashMap()" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }
    }
}
