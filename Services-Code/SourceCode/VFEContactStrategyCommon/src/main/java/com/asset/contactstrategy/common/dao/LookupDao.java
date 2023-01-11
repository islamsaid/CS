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
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.models.ReportsViewModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Zain Al-Abedin
 */
public class LookupDao {

    public ArrayList<LookupModel> loadIdLableLookups(String tableName, String idcolName, String lableColName, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.info("Starting tblName=>" + tableName + " columns(" + idcolName + "," + lableColName + ")");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting loadIdLableLookups")
                .put(GeneralConstants.StructuredLogKeys.TABLE_NAME, tableName)
                .put(GeneralConstants.StructuredLogKeys.COLUMNS, idcolName + ", " + lableColName).build());
        Statement st = null;
        ResultSet rs = null;
        ArrayList<LookupModel> lookupsRetrieved = null;
        try {

            st = connection.createStatement();
            String query = "SELECT " + idcolName + " , " + lableColName + " FROM " + tableName;
            rs = st.executeQuery(query);
            while (rs.next()) {
                if (lookupsRetrieved == null) {
                    lookupsRetrieved = new ArrayList<LookupModel>();
                }
                LookupModel lkModel = new LookupModel();
                lkModel.setId(rs.getInt(idcolName));
                lkModel.setLable(rs.getString(lableColName));
                lookupsRetrieved.add(lkModel);
            }
        } catch (SQLException e) {
            throw new CommonException("SQLError while retrieving lookups from " + tableName, ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            throw new CommonException("Error while retrieving lookups from " + tableName, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, st);
        }
        return lookupsRetrieved;
    }

    public ArrayList<OriginatorTypeModel> loadOriginatorLookup(String tableName, String idcolName, String lableColName, String allowedColName, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.info("Starting tblName=>" + tableName + " columns(" + idcolName + "," + lableColName + "," + allowedColName + ")");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting loadOriginatorLookup")
                .put(GeneralConstants.StructuredLogKeys.TABLE_NAME, tableName)
                .put(GeneralConstants.StructuredLogKeys.COLUMNS, idcolName + ", " + lableColName + ", " + allowedColName).build());
        Statement st = null;
        ResultSet rs = null;
        ArrayList<OriginatorTypeModel> lookupsRetrieved = null;
        try {

            st = connection.createStatement();
            String query = "SELECT " + idcolName + " , " + lableColName + " , " + allowedColName + " FROM " + tableName;
            rs = st.executeQuery(query);
            while (rs.next()) {
                if (lookupsRetrieved == null) {
                    lookupsRetrieved = new ArrayList<OriginatorTypeModel>();
                }
                OriginatorTypeModel originatorTypeModel = new OriginatorTypeModel();
                originatorTypeModel.setOriginatorType(rs.getInt(idcolName));
                originatorTypeModel.setOriginatorName(rs.getString(lableColName));
                originatorTypeModel.setAllowedLength(rs.getInt(allowedColName));
                lookupsRetrieved.add(originatorTypeModel);
            }
        } catch (SQLException e) {
            throw new CommonException("SQLError while retrieving lookups from " + tableName, ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            throw new CommonException("Error while retrieving lookups from " + tableName, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, st);
        }
        return lookupsRetrieved;
    }

    public ArrayList<LookupModel> getDisplayTypeOperators(int displayTypeId, Connection conn) throws CommonException {
        final String METHOD_NAME = "getDisplayTypeOperators";
//        CommonLogger.businessLogger.info("Starting method " + METHOD_NAME + " for type id " + displayTypeId);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Method")
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, METHOD_NAME)
                .put(GeneralConstants.StructuredLogKeys.TYPE, displayTypeId).build());

        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<LookupModel> lookupsRetrieved = new ArrayList<LookupModel>();
        try {
            String query = "SELECT lo." + DBStruct.LK_OPERATOR.OPERATOR_ID + " , "
                    + "lo." + DBStruct.LK_OPERATOR.OPERATOR_VALUE
                    + " FROM " + DBStruct.LK_OPERATOR.TBL_NAME + " lo, "
                    + DBStruct.LK_DISPLAY_TYPE_OPERATORS.TBL_NAME + " ldto"
                    + " WHERE ldto." + DBStruct.LK_DISPLAY_TYPE_OPERATORS.OPERATOR_ID + " = "
                    + "lo." + DBStruct.LK_OPERATOR.OPERATOR_ID
                    + " AND " + DBStruct.LK_DISPLAY_TYPE_OPERATORS.DISPLAY_TYPE_ID + " = ?";

            ps = conn.prepareStatement(query);
            ps.setInt(1, displayTypeId);

            rs = ps.executeQuery();
            while (rs.next()) {
                LookupModel lkModel = new LookupModel();
                lkModel.setId(rs.getInt(DBStruct.LK_OPERATOR.OPERATOR_ID));
                lkModel.setLable(rs.getString(DBStruct.LK_OPERATOR.OPERATOR_VALUE));
                lookupsRetrieved.add(lkModel);
            }
        } catch (SQLException e) {
            throw new CommonException("SQLError while retrieving lookups from " + DBStruct.LK_OPERATOR.TBL_NAME, ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            throw new CommonException("SQLError while retrieving lookups from " + DBStruct.LK_OPERATOR.TBL_NAME, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(rs, ps);
        }
        return lookupsRetrieved;
    }

    /**
     *
     * @param conn
     * @return
     * @throws com.asset.contactstrategy.common.exception.CommonException
     */
    public ArrayList<ReportsViewModel> getReportsList(Connection conn) throws CommonException {
        CommonLogger.businessLogger.debug("Starting method getReportsList");

        StringBuilder sqlQuery = new StringBuilder();
        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        ArrayList<ReportsViewModel> reportsList = new ArrayList<>();

        sqlQuery.append("Select * From ")
                .append(DBStruct.VFE_CS_REPORTS_LIST_LK.TABLE_NAME);

        try {
            preStmt = conn.prepareStatement(sqlQuery.toString());

            resultSet = preStmt.executeQuery();

            while (resultSet.next()) {
                ReportsViewModel report = new ReportsViewModel();
                report.setReportName(resultSet.getString(DBStruct.VFE_CS_REPORTS_LIST_LK.REPORT_NAME));
                report.setReportLink(resultSet.getString(DBStruct.VFE_CS_REPORTS_LIST_LK.REPORT_LINK));
                reportsList.add(report);
            }
        } catch (SQLException e) {
            throw new CommonException("SQLError while retrieving lookups from " + DBStruct.VFE_CS_REPORTS_LIST_LK.TABLE_NAME, e.getErrorCode());
        } catch (Exception e) {
            throw new CommonException("SQLError while retrieving lookups from " + DBStruct.VFE_CS_REPORTS_LIST_LK.TABLE_NAME, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(resultSet, preStmt);
        }
        return reportsList;
    }

    public HashMap<Integer, OriginatorTypeModel> getOriginatorsList(Connection conn) throws CommonException {
        CommonLogger.businessLogger.debug("Starting method getOriginator");

        StringBuilder sqlQuery = new StringBuilder();
        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        OriginatorTypeModel model = null;
        HashMap<Integer, OriginatorTypeModel> originators = new HashMap<>();

        sqlQuery.append("Select * From ")
                .append(DBStruct.VFE_CS_ORIGINATORS_LK.TABLE_NAME);

        try {
            preStmt = conn.prepareStatement(sqlQuery.toString());

            resultSet = preStmt.executeQuery();

            while (resultSet.next()) {
                model = new OriginatorTypeModel();
                model.setOriginatorType(resultSet.getInt(DBStruct.VFE_CS_ORIGINATORS_LK.ORGINATOR_TYPE));
                model.setAllowedLength(resultSet.getInt(DBStruct.VFE_CS_ORIGINATORS_LK.ALLOWED_LENGTH));
                model.setOriginatorName(resultSet.getString(DBStruct.VFE_CS_ORIGINATORS_LK.ORGINATOR_NAME));
                originators.put(model.getOriginatorType(), model);
            }
        } catch (SQLException e) {
            CommonLogger.businessLogger.info(e.getMessage());
            CommonLogger.businessLogger.error(e.getMessage(), e);
            throw new CommonException("SQLError while retrieving lookups from " + DBStruct.VFE_CS_REPORTS_LIST_LK.TABLE_NAME, e.getErrorCode());
        } catch (Exception e) {
            CommonLogger.businessLogger.info(e.getMessage());
            CommonLogger.businessLogger.error(e.getMessage(), e);
            throw new CommonException("SQLError while retrieving lookups from " + DBStruct.VFE_CS_REPORTS_LIST_LK.TABLE_NAME, ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(resultSet, preStmt);
        }
        return originators;
    }
}
