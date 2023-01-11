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
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
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
 * @author Zain Al-Abedin
 */
public class DWHElementDAO {

    public ArrayList<DWHElementValueModel> loadCustomersAttributes(Connection connection) throws CommonException {
        CommonLogger.businessLogger.info(" ||  loadCustomersAttributes");
        Statement st = null;
        long startime = System.currentTimeMillis();
        ResultSet rs = null;
        ArrayList<DWHElementValueModel> customersType = null;
        try {
            customersType = new ArrayList<DWHElementValueModel>();
            StringBuilder queryBuild = new StringBuilder();
            queryBuild.append("SELECT ");
            queryBuild.append(" e.").append(DBStruct.LK_CUSTOMER_TYPE.CUSTOMER_TYPE_ID);
            queryBuild.append(" , e.").append(DBStruct.LK_CUSTOMER_TYPE.CUSTOMER_TYPE_LABEL);
            queryBuild.append(" FROM ").append(DBStruct.LK_CUSTOMER_TYPE.TBL_NAME).append(" e");
            st = connection.createStatement();
            rs = st.executeQuery(queryBuild.toString());
            while (rs.next()) {
                DWHElementValueModel customerType = new DWHElementValueModel();
                customerType.setValueId(Integer.parseInt(rs.getString(DBStruct.LK_CUSTOMER_TYPE.CUSTOMER_TYPE_ID)));
                customerType.setValueLabel(rs.getString(DBStruct.LK_CUSTOMER_TYPE.CUSTOMER_TYPE_LABEL));
                customersType.add(customerType);
            }
//            CommonLogger.businessLogger.info(" || " + "Ended loadCustomersAttributes in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended loadCustomerattributes")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("SQLError =>" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error =>" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, st);
        }
        return customersType;
    }

    public ArrayList<DWHElementValueModel> loadGovernmentAttributes(Connection connection) throws CommonException {
        CommonLogger.businessLogger.info(" || loadGovernmentAttributes");
        Statement st = null;
        long startime = System.currentTimeMillis();
        ResultSet rs = null;
        ArrayList<DWHElementValueModel> governments = null;
        try {
            governments = new ArrayList<DWHElementValueModel>();
            StringBuilder queryBuild = new StringBuilder();
            queryBuild.append("SELECT ");
            queryBuild.append(" e.").append(DBStruct.LK_GOVERNMENT.GOVERNMENT_ID);
            queryBuild.append(" , e.").append(DBStruct.LK_GOVERNMENT.GOVERNMENT_NAME);
            queryBuild.append(" FROM ").append(DBStruct.LK_GOVERNMENT.TBL_NAME).append(" e");
            st = connection.createStatement();
            rs = st.executeQuery(queryBuild.toString());
            while (rs.next()) {
                DWHElementValueModel government = new DWHElementValueModel();
                government.setValueId(Integer.parseInt(rs.getString(DBStruct.LK_GOVERNMENT.GOVERNMENT_ID)));
                government.setValueLabel(rs.getString(DBStruct.LK_GOVERNMENT.GOVERNMENT_NAME));
                governments.add(government);
            }
//            CommonLogger.businessLogger.info(" || " + "Ended loadGovernmentAttributes in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended loadGovernomentAttributes")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("SQLError =>" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error =>" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, st);
        }
        return governments;
    }

    public ArrayList<DWHElementModel> loadAllElements(Connection connection) throws CommonException {

//        CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting loadAllElements");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting loadAllElements").build());
        Statement st = null;
        ResultSet rs = null;
        ArrayList<DWHElementModel> allElements = null;
        HashMap<Long, DWHElementModel> temp = null;
        long startime = System.currentTimeMillis();
        try {
            allElements = new ArrayList<DWHElementModel>();
            String query = buildRetreieveQuery();
//            CommonLogger.businessLogger.debug("Query => " + query);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadAllElements Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = connection.createStatement();
            rs = st.executeQuery(query);
            temp = new HashMap<Long, DWHElementModel>();
            while (rs.next()) {
                DWHElementModel currentElement = new DWHElementModel();
                currentElement.setElementId(rs.getLong(DBStruct.DWH_ELEMENTS.ELEMENT_ID));
                currentElement.setName(rs.getString(DBStruct.DWH_ELEMENTS.NAME));
                currentElement.setDataTypeId(rs.getInt(DBStruct.DWH_ELEMENTS.DATA_TYPE));
                currentElement.setDwhName(rs.getString(DBStruct.DWH_ELEMENTS.COLUMN_NAME));
                currentElement.setFileIndex(rs.getInt(DBStruct.DWH_ELEMENTS.FILE_INDEX));
                currentElement.setDescription(rs.getString(DBStruct.DWH_ELEMENTS.DESCRIPTION));

                currentElement.setDisplayTypeId(rs.getInt(DBStruct.DWH_ELEMENTS.DISPLAY_TYPE));
                currentElement.setDisplayName(rs.getString(DBStruct.DWH_ELEMENTS.DISPLAY_NAME));
                currentElement.setMandatory(rs.getString(DBStruct.DWH_ELEMENTS.MANDATORY).equalsIgnoreCase("true"));
                int LOV_valueId = rs.getInt(DBStruct.DWH_ELEMENT_LOV.VALUE_ID);
                if (LOV_valueId != 0) {
                    DWHElementValueModel multiSelectionValue = new DWHElementValueModel();
                    multiSelectionValue.setElementId(rs.getLong(DBStruct.DWH_ELEMENT_LOV.ELEMENT_ID));
                    multiSelectionValue.setValueId(LOV_valueId);
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
//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Ended loadAllElements in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadAllElements Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("SQLError =>" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error =>" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, st);
        }
        return allElements;

    }

    private String buildRetreieveQuery() throws Exception {
        StringBuilder queryBuild = new StringBuilder();
        queryBuild.append("SELECT ");
        queryBuild.append("e." + DBStruct.DWH_ELEMENTS.ELEMENT_ID);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.COLUMN_NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DESCRIPTION);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DATA_TYPE);

        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.MANDATORY);

        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.FILE_INDEX);
        queryBuild.append(", lov." + DBStruct.DWH_ELEMENT_LOV.VALUE_ID);
        queryBuild.append(", lov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL);
        queryBuild.append(" FROM " + DBStruct.DWH_ELEMENTS.TBL_NAME + " e");
        queryBuild.append(" LEFT OUTER JOIN " + DBStruct.DWH_ELEMENT_LOV.TBL_NAME + " lov ");
        queryBuild.append(" ON lov." + DBStruct.DWH_ELEMENT_LOV.ELEMENT_ID);
        queryBuild.append(" = e." + DBStruct.DWH_ELEMENTS.ELEMENT_ID);
        queryBuild.append(" GROUP BY ");
        queryBuild.append(" e." + DBStruct.DWH_ELEMENTS.ELEMENT_ID);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.COLUMN_NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DESCRIPTION);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_NAME);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DATA_TYPE);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE);

        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.FILE_INDEX);
        queryBuild.append(", lov." + DBStruct.DWH_ELEMENT_LOV.VALUE_ID);
        queryBuild.append(", lov." + DBStruct.DWH_ELEMENT_LOV.VALUE_LABEL);
        queryBuild.append(", e." + DBStruct.DWH_ELEMENTS.MANDATORY);
        return queryBuild.toString();
    }

    public Vector<DWHElementModel> getDWHElementsList(Connection connection) throws CommonException {
        String methodName = "getDWHElementsList";
        //  CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting getDWHElementsList");
        Statement st = null;
        ResultSet rs = null;
        Vector<DWHElementModel> allElements = new Vector<>();
        long startime = System.currentTimeMillis();
        DWHElementModel dwhElementsModel = null;

        Statement statment = null;
        StringBuilder query = new StringBuilder();
        ResultSet resultSet = null;

        try {
            // create Statement for querying database
            statment = connection.createStatement();
            query.append("SELECT " + DBStruct.DWH_ELEMENTS.ELEMENT_ID + ", " + DBStruct.DWH_ELEMENTS.FILE_INDEX + ", ");
            query.append(DBStruct.DWH_ELEMENTS.DATA_TYPE + ", " + DBStruct.DWH_ELEMENTS.COLUMN_NAME);
            query.append(" FROM " + DBStruct.DWH_ELEMENTS.TBL_NAME + " ORDER BY " + DBStruct.DWH_ELEMENTS.FILE_INDEX + " ASC");

            resultSet = statment.executeQuery(query.toString());
            while (resultSet.next()) {
                dwhElementsModel = new DWHElementModel();
                dwhElementsModel.setDwhName(resultSet.getString(DBStruct.DWH_ELEMENTS.COLUMN_NAME));
                dwhElementsModel.setDataTypeId(resultSet.getInt(DBStruct.DWH_ELEMENTS.DATA_TYPE));
                dwhElementsModel.setElementId(resultSet.getInt(DBStruct.DWH_ELEMENTS.ELEMENT_ID));
                dwhElementsModel.setFileIndex(resultSet.getInt(DBStruct.DWH_ELEMENTS.FILE_INDEX));

                allElements.add(dwhElementsModel);
            }

//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Ended getDWHElementsList in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getDWHElementsList Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("SQLError =>" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error =>" + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, st);
        }

        return allElements;
    }

    public void deleteElement(Connection connection, long elementId) throws CommonException {
        Statement st = null;
        long startime = System.currentTimeMillis();
        try {
//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting deleteElement");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteElement Started").build());
            String query = "DELETE FROM " + DBStruct.DWH_ELEMENTS.TBL_NAME + " WHERE " + DBStruct.DWH_ELEMENTS.ELEMENT_ID + " = " + elementId;
//            CommonLogger.businessLogger.debug("Query => " + query);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteElement Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = connection.createStatement();
            st.executeUpdate(query);
//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Ended deleteElement in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteElement Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [deleteElement] Failed to delete DWH Element ID=> " + elementId, e);
            if (e.getErrorCode() == ErrorCodes.INTEGRITY_ERROR) {
                throw new CommonException(e.getMessage(), ErrorCodes.INTEGRITY_CONSTRAINT_ERROR);
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, st);
        }
    }

    public ArrayList<String> loadDwhUsedColumns(Connection connection) throws CommonException {
//        CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting loadDwhUsedColumns");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadDwhUsedColumns Started").build());
        long startime = System.currentTimeMillis();
        Statement st = null;
        ResultSet rs = null;
        ArrayList<String> usedColumns = new ArrayList<String>();
        try {
            StringBuilder queryBuild = new StringBuilder();
            queryBuild.append("SELECT ");
            queryBuild.append(" e." + DBStruct.DWH_ELEMENTS.COLUMN_NAME);
            queryBuild.append(" FROM " + DBStruct.DWH_ELEMENTS.TBL_NAME + " e");
            String query = queryBuild.toString();
//            CommonLogger.businessLogger.debug("Query => " + query);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadDwhUsedColumns Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = connection.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                usedColumns.add(rs.getString(DBStruct.DWH_ELEMENTS.COLUMN_NAME));
            }
//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Ended loadDwhUsedColumns in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadDwhUsedColumns Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [loadDwhUsedColumns] Failed to load DWH Uswd Columns", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage() + " || " + e);
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage() + " || " + e, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, st);
        }

        return usedColumns;
    }

    public void insertNewElement(DWHElementModel element, Connection connection) throws CommonException {
        long startime = System.currentTimeMillis();
        PreparedStatement st = null;
        try {
//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting insertNewElement");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertNewElement Started").build());
            long id = CommonDAO.getNextId(connection, DBStruct.DWH_ELEMENTS.SEQ_NAME);
            element.setElementId(id);
            StringBuilder queryBuild = new StringBuilder();
            queryBuild.append("INSERT INTO " + DBStruct.DWH_ELEMENTS.TBL_NAME);
            queryBuild.append(" (" + DBStruct.DWH_ELEMENTS.ELEMENT_ID);
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.NAME);
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.DESCRIPTION);
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.COLUMN_NAME);
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.DISPLAY_NAME);
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.DATA_TYPE);
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE);

            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.FILE_INDEX + ")");
            queryBuild.append(" VALUES (");
            queryBuild.append(" ?");
            queryBuild.append(", ?");
            queryBuild.append(", ?");
            queryBuild.append(", ?");
            queryBuild.append(", ?");
            queryBuild.append(", " + element.getDataTypeId() + " ");
            queryBuild.append(", " + element.getDisplayTypeId() + " ");

            queryBuild.append("," + element.getFileIndex() + " ");
            queryBuild.append(")");
            String query = queryBuild.toString();
//            CommonLogger.businessLogger.debug("Query => " + query);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertNewElement Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = connection.prepareStatement(query);
            st.setLong(1, element.getElementId());
            st.setString(2, element.getName().trim());
            st.setString(3, element.getDescription().trim());
            st.setString(4, element.getDwhName().trim());
            st.setString(5, element.getDisplayName().trim());
            st.executeUpdate();
//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Ended insertNewElement in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertNewElement Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateElement] Failed Insert DWH Element => " + element.getDwhName() + " ID => " + element.getElementId() + " " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateElement] Failed Insert DWH Element => " + element.getDwhName() + " ID => " + element.getElementId() + " " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, st);
        }
    }

    public void updateElement(DWHElementModel element, Connection connection) throws CommonException {
        long startime = System.currentTimeMillis();
        PreparedStatement st = null;
        try {
//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Starting updateElement");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateElement Started").build());
            StringBuilder queryBuild = new StringBuilder();
            queryBuild.append("UPDATE " + DBStruct.DWH_ELEMENTS.TBL_NAME + " ");
            queryBuild.append("SET  " + DBStruct.DWH_ELEMENTS.NAME + " = ?");
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.DESCRIPTION + " = ?");
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.DATA_TYPE + " = " + element.getDataTypeId());
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.COLUMN_NAME + " = ?");
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.FILE_INDEX + " = " + element.getFileIndex());
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.DISPLAY_NAME + " = ?");
            queryBuild.append(", " + DBStruct.DWH_ELEMENTS.DISPLAY_TYPE + " = " + element.getDisplayTypeId());
            queryBuild.append(" WHERE " + DBStruct.DWH_ELEMENTS.ELEMENT_ID + " = " + element.getElementId());
            String query = queryBuild.toString();
//            CommonLogger.businessLogger.debug("Query => " + query);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateElement Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, query).build());
            st = connection.prepareStatement(query);
            st.setString(1, element.getName());
            st.setString(2, element.getDescription());
            st.setString(3, element.getDwhName());
            st.setString(4, element.getDisplayName());
            st.executeUpdate();
//            CommonLogger.businessLogger.info(DWHElementDAO.class.getName() + " || " + "Ended updateElement in [" + (System.currentTimeMillis() - startime) + "] msec");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateElement Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateElement] Failed Update DWH Element => " + element.getDwhName() + " ID => " + element.getElementId() + " " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Exception---->  for [updateElement] Failed Update DWH Element => " + element.getDwhName() + " ID => " + element.getElementId() + " " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(null, st);
        }
    }
}
