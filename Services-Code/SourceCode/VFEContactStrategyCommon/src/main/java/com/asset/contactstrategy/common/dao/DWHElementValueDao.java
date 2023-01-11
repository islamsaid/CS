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
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Zain Al-Abedin
 */
public class DWHElementValueDao {

    public void deleteListOfValues(Connection connection, long elementId) throws CommonException {
        Statement st = null;
        long startime = System.currentTimeMillis();
        try {
//            CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Starting deleteListOfValues");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Started").build());
            String query = "DELETE FROM " + DBStruct.DWH_ELEMENT_LOV.TBL_NAME + " WHERE " + DBStruct.DWH_ELEMENT_LOV.ELEMENT_ID + " = " + elementId;
//            CommonLogger.businessLogger.debug("Query => " + query);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = connection.createStatement();
            st.executeUpdate(query);
//            CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Ended deleteListOfValues in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            if (e.getMessage().contains("ORA-02292")) {
                CommonLogger.errorLogger.error("Exception---->  for [deleteListOfValues] Failed to delete MultiSelection Value because Values for element with ID => " + elementId + "] " + e, e);
                throw new CommonException(e.getMessage(), ErrorCodes.DWH_ELEMENT_IN_USE);
            } else {
                CommonLogger.errorLogger.error("Exception---->  for [deleteListOfValues] Failed to delete MultiSelection Values for element with ID => " + elementId + "] ", e);
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }

        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, st);
        }
    }

    public void updateMergeListOfValues(DWHElementModel element, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Starting updateMergeListOfValues");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateMergeListOfValues Started").build());
        Statement st = null;
        long startime = System.currentTimeMillis();
        DWHElementValueModel currentValue = null;
        try {
            for (DWHElementValueModel value : element.getMultiSelectionValues()) {
                st = connection.createStatement();
                currentValue = value;
                value.setElementId(element.getElementId());
                StringBuilder queryBuild = new StringBuilder();
                queryBuild.append("MERGE INTO " + DBStruct.DWH_ELEMENT_LOV.TBL_NAME + " lov USING ")
                        .append(" (SELECT 'anything' FROM DUAL ) dummy ").append(" ON (")
                        .append(" lov.").append(DBStruct.DWH_ELEMENT_LOV.VALUE_ID).append(" = ").append(value.getValueId())
                        .append(" AND").append(" lov.").append(DBStruct.DWH_ELEMENT_LOV.ELEMENT_ID).append(" = ").append(value.getElementId())
                        .append(" )")
                        .append(" WHEN MATCHED THEN UPDATE SET ").append(DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL).append(" = '").append(value.getValueLabel())
                        .append("' WHERE ").append(DBStruct.DWH_ELEMENT_LOV.VALUE_ID).append(" = ").append(value.getValueId())
                        .append("  WHEN NOT MATCHED THEN INSERT (")
                        .append(DBStruct.DWH_ELEMENT_LOV.VALUE_ID)
                        .append(", " + DBStruct.DWH_ELEMENT_LOV.ELEMENT_ID)
                        .append(", " + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL + ")")
                        .append(" VALUES (")
                        .append(DBStruct.DWH_ELEMENT_LOV.SEQ_NAME + ".NEXTVAL ")
                        .append(",").append(value.getElementId())
                        .append(",'").append(value.getValueLabel()).append("'")
                        .append(")");

                String query = queryBuild.toString();
//                CommonLogger.businessLogger.debug(query);
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateMergeListOfValues Query")
                        .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
                st.executeUpdate(query);
                st.close();
                st = null;
            }
//            CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Ended updateMergeListOfValues in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateMergeListOfValues Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateMergeListOfValues] Failed to Update  MultiSelection Values for value => " + currentValue.getValueLabel() + " element with ID => " + currentValue.getElementId() + " " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateMergeListOfValues] Failed to Update  MultiSelection Values for value => " + currentValue.getValueLabel() + " element with ID => " + currentValue.getElementId() + " " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, st);
        }

    }

    public void deleteMultiSelectionValue(DWHElementValueModel elementLOValue, Connection connection) throws CommonException {
        Statement st = null;
        long startime = System.currentTimeMillis();
//        CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Starting deleteListOfValues");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Started").build());
        try {
            String query = "DELETE FROM " + DBStruct.DWH_ELEMENT_LOV.TBL_NAME + " WHERE " + DBStruct.DWH_ELEMENT_LOV.VALUE_ID + " = " + elementLOValue.getValueId();
//            CommonLogger.businessLogger.debug("Query => " + query);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = connection.createStatement();
            st.executeUpdate(query);
            st.close();
//            CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Ended deleteMultiSelectionValue in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            if (e.getMessage().contains("ORA-02292")) {
                CommonLogger.errorLogger.error("Exception---->  for [deleteListOfValues] Failed to delete MultiSelection Value because value is used  Value with Id => [" + elementLOValue.getValueId() + "] " + e, e);
                throw new CommonException(e.getMessage(), ErrorCodes.DWH_ELEMENT_LOV_IN_USE);
            } else {
                CommonLogger.errorLogger.error("Exception---->  for [deleteListOfValues] Failed to delete MultiSelection Value  with Id => [" + elementLOValue.getValueId() + "] " + e, e);
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, st);
        }

    }

    public ArrayList<DWHElementValueModel> deleteListOfValues(ArrayList<DWHElementValueModel> elementLOValues, Connection connection) throws CommonException {
        Statement st = null;
        long elementId = 0;
        long startime = System.currentTimeMillis();
        ArrayList<DWHElementValueModel> inUseElements = null;
        try {
//            CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Starting deleteListOfValues");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Started").build());
            inUseElements = new ArrayList<DWHElementValueModel>();
            for (DWHElementValueModel singleValue : elementLOValues) {
                try {
                    elementId = singleValue.getElementId();
                    String query = "DELETE FROM " + DBStruct.DWH_ELEMENT_LOV.TBL_NAME + " WHERE " + DBStruct.DWH_ELEMENT_LOV.VALUE_ID + " = " + singleValue.getValueId();
//                    CommonLogger.businessLogger.debug("Query => " + query);
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Query")
                            .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
                    st = connection.createStatement();
                    st.executeUpdate(query);
                    st.close();
                    st = null;

                } catch (SQLException e) {
                    if (e.getMessage().contains("ORA-02292")) {
                        inUseElements.add(singleValue);
                    } else {
                        throw e;
                    }
                }
            }

//            CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Ended deleteListOfValues in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteListOfValues Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [deleteListOfValues] Failed to delete MultiSelection Values for element with Id => [" + elementId + "] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [deleteListOfValues] Failed to delete MultiSelection Values for element with Id => [" + elementId + "] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, st);
        }
        return inUseElements;
    }

    public void insertListOfValues(DWHElementModel element, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Starting insertListOfValues");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertListOfValues Started").build());
        Statement st = null;
        long startime = System.currentTimeMillis();
        DWHElementValueModel currentValue = null;
        try {
            for (DWHElementValueModel value : element.getMultiSelectionValues()) {
                st = connection.createStatement();
                value.setElementId(element.getElementId());
                StringBuilder queryBuild = new StringBuilder();
                queryBuild.append("INSERT INTO " + DBStruct.DWH_ELEMENT_LOV.TBL_NAME + " (");
                queryBuild.append(DBStruct.DWH_ELEMENT_LOV.VALUE_ID);
                queryBuild.append(", " + DBStruct.DWH_ELEMENT_LOV.ELEMENT_ID);
                queryBuild.append(", " + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL + ")");

                queryBuild.append(" VALUES (");

                queryBuild.append(DBStruct.DWH_ELEMENT_LOV.SEQ_NAME + ".NEXTVAL ");

                queryBuild.append("," + value.getElementId());
                queryBuild.append(",'" + value.getValueLabel() + "'");
                queryBuild.append(")");
                currentValue = value;

                String query = queryBuild.toString();
//                CommonLogger.businessLogger.debug("Query => " + query);
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertListOfValues Query")
                        .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
                st.executeUpdate(query);
                st.close();
                st = null;
            }
//            CommonLogger.businessLogger.debug(DWHElementValueDao.class.getName() + " || " + "Ended insertListOfValues in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertListOfValues Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [insertListOfValues] Failed to Insert MultiSelection Values for element with Id => [" + element.getElementId() + "] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [insertListOfValues] Failed to Insert MultiSelection Values for element with Id => [" + element.getElementId() + "] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, st);
        }
    }

}
