package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.models.SMSBulkFileModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.google.gson.internal.LinkedTreeMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author aya.moawed 2595
 */
public class SMSBulkFilesDAO {

    public void insertSMSFile(Connection connection, SMSBulkFileModel model) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Starting [insertSMSFile]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSFile Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("INSERT INTO ").append(DBStruct.VFE_CS_SMS_BULK_FILES_H.TABLE_NAME).append(" (")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.ID).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.FILE_NAME).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.USER_ID).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.TOTAL_RECORDS).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.SUCCESS_RECORDS).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.FAILED_RECORDS).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.RECORDS_INSERTION_STATUS).append(")")
                    .append(" VALUES (?,?,?,?,?,?,?)");

            statement = connection.prepareStatement(sql.toString());

            statement.setLong(1, model.getId());
            statement.setString(2, model.getName());
            statement.setInt(3, model.getUserID());
            statement.setInt(4, model.getTotalRecordCount());
            statement.setInt(5, model.getSuccessRecordCount());
            statement.setInt(6, model.getFailedRecordCount());
            statement.setInt(7, model.getStatus());

            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSFile Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Creation Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "End [insertSMSFile]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSFile Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.businessLogger.debug("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSFile]" + e);
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSFile]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.businessLogger.debug("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSFile]" + e.getMessage());
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSFile]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSFile]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void updateSMSBulkFileStatus(Connection connection, SMSBulkFileModel model) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Starting [updateSMSBulkFileStatus]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSMSBulkFileStatus Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("UPDATE  ").append(DBStruct.VFE_CS_SMS_BULK_FILES_H.TABLE_NAME).append(" SET ")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.RECORDS_INSERTION_STATUS)
                    .append(" = ? WHERE ")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.ID).append(" = ? ");

            statement = connection.prepareStatement(sql.toString());

            statement.setInt(1, model.getStatus());
            statement.setLong(2, model.getId());

//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSMSBulkFileStatus Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Creation Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "End [updateSMSBulkFileStatus]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSMSBulkFileStatus Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [updateSMSBulkFileStatus]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [updateSMSBulkFileStatus]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [updateSMSBulkFileStatus]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

    public void insertSMSFilesBatch(Connection connection, ArrayList<SMSBulkFileModel> models) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Starting [insertSMSFilesBatch]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSFilesBatch Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        long startTime = System.currentTimeMillis();

        try {
            sql = new StringBuilder();
            sql.append("INSERT INTO ").append(DBStruct.VFE_CS_SMS_BULK_FILES_H.TABLE_NAME).append(" (")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.ID).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.FILE_NAME).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.USER_ID).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.TOTAL_RECORDS).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.SUCCESS_RECORDS).append(",")
                    .append(DBStruct.VFE_CS_SMS_BULK_FILES_H.FAILED_RECORDS).append(")")
                    .append(" VALUES (?,?,?,?,?,?)");

            statement = connection.prepareStatement(sql.toString());

            for (SMSBulkFileModel model : models) {
                statement.setLong(1, model.getId());
                statement.setString(2, model.getName());
                statement.setInt(3, model.getUserID());
                statement.setInt(4, model.getTotalRecordCount());
                statement.setInt(5, model.getSuccessRecordCount());
                statement.setInt(6, model.getFailedRecordCount());
            }

//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSFilesBatch Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement.executeBatch();
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Creation Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "End [insertSMSFilesBatch]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSFilesBatch Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSFilesBatch]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSFilesBatch]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSFilesBatch]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }

//    public void insertSMSFilesBatch(Connection connection, ArrayList<SMSBulkFileModel> models) throws CommonException {
//      
//    }
    public void insertSMSBulkRecords(Connection conn, ArrayList<InputModel> models, String fileName) throws CommonException {
        PreparedStatement pstat = null;
        try {
            StringBuilder sql = new StringBuilder();
            Date submissionDate = new Date();
            sql.append("INSERT INTO ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME).append(" (")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.MSG_PARAMETERS).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.JSON_OBJECT).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.STATUS).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SUBMISSION_DATE).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SERVICE_NAME).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.FILE_NAME).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SERVICE_PASSWORD).append(") ")
                    .append(" VALUES (").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SEQ_NAME).append(".NEXTVAL ,?,?,?,?,?,?,?)");
            pstat = conn.prepareStatement(sql.toString());
            for (InputModel model : models) {
                String JSONString = Utility.inputModelToJSONString(model);
                pstat.setString(1, Arrays.toString(model.getRowLine()));
                pstat.setString(2, JSONString);
                pstat.setInt(3, Defines.VFE_CS_SMS_BULK_STATUS_LK.READY);
                pstat.setTimestamp(4, new Timestamp(submissionDate.getTime()));
                pstat.setString(5, model.getSystemName());
                pstat.setString(6, fileName);
                pstat.setString(7, model.getSystemPassword()); // CR 1901 | eslam.ahmed
                pstat.addBatch();
            }
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

//    public static void main(String []args){
//        StringBuilder sql = new StringBuilder();
//        long startTime = System.currentTimeMillis();
//        sql.append("SELECT COUNT(*) BulkCount FROM ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME).append(" WHERE ")
//                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.STATUS).append(" = ").append(Defines.VFE_CS_SMS_BULK_STATUS_LK.READY);
//        
//        System.out.println(sql.toString());
//    }
    public RequestPreparator getSMSBulkRecords(Connection connection, int threadNumber, int numberOfThreads, int batchSize, ArrayList<Long> resultIds) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Starting [getSMSBulkRecords]... with thread (" + threadNumber + ")");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSBulkRecords Started")
                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        StringBuilder jsonString = jsonString = new StringBuilder();
        ResultSet rs = null;
        String serviceName = "";
        String servicePassword = ""; // CR 1901 | eslam.ahmed
        long startTime = System.currentTimeMillis();
        RequestPreparator jSONResult = new RequestPreparator();
        try {
            sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME).append(" WHERE ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.STATUS).append(" = ").append(Defines.VFE_CS_SMS_BULK_STATUS_LK.READY).append(" AND ")
                    .append(" MOD(").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(",?)= ? ")
                    .append(" AND ROWNUM <=? AND ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SERVICE_NAME).append(" = ( SELECT * FROM(")
                    .append("SELECT ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SERVICE_NAME).append(" FROM ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME).append(" WHERE ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.STATUS).append(" = ").append(Defines.VFE_CS_SMS_BULK_STATUS_LK.READY).append(" AND ")
                    .append(" MOD(").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(",?)= ? ")
                    .append(" AND ROWNUM <=1 ").append(" ORDER BY ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SUBMISSION_DATE).append(" )) ORDER BY ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SUBMISSION_DATE);
//                    .append("FETCH FIRST ? ROWS ONLY "); //need Oracle 12.1

            statement = connection.prepareStatement(sql.toString());

            statement.setInt(1, numberOfThreads);
            statement.setInt(2, threadNumber);
            statement.setInt(3, batchSize);
            statement.setInt(4, numberOfThreads);
            statement.setInt(5, threadNumber);

//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + " [Query] with thread (" + threadNumber + ")" + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSBulkRecords Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString())
                    .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());

            rs = statement.executeQuery();
            String jsonObject;
            if (rs.next()) {
                serviceName = rs.getString(DBStruct.VFE_CS_SMS_BULK_RECORDS.SERVICE_NAME);
                servicePassword = rs.getString(DBStruct.VFE_CS_SMS_BULK_RECORDS.SERVICE_PASSWORD); // CR 1901 | eslam.ahmed
                jsonString.append("{\"ServiceName\": \"").append(serviceName)
                        .append("\",\"ServicePassword\": \"").append(servicePassword).append("\" , \"SMSs\": [");
                jsonObject = rs.getString(DBStruct.VFE_CS_SMS_BULK_RECORDS.JSON_OBJECT);
                jsonString.append("{\"SMS\":").append(jsonObject).append(" }, ");
                resultIds.add(rs.getLong(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID));
            }
            while (rs.next()) {
                jsonObject = rs.getString(DBStruct.VFE_CS_SMS_BULK_RECORDS.JSON_OBJECT);
                jsonString.append("{\"SMS\":").append(jsonObject).append(" }, ");
                resultIds.add(rs.getLong(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID));
            }

            if (!jsonString.toString().equals("")) {
                jSONResult.setJsonString(jsonString.deleteCharAt(jsonString.length() - 2).append("]}"));
            }
//            CommonLogger.businessLogger.debug("Batch of Msgs Retrieved with thread (" + threadNumber + ") :" + resultIds.toString());
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Selection time with thread (" + threadNumber + "):" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.trace(SMSBulkFilesDAO.class.getName() + " || Result with thread (" + threadNumber + "):" + jsonString);
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "End [getSMSBulkRecords]... with thread (" + threadNumber + ")");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Batch of Msgs Retrieved. Ending getSMSBulkRecords")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber)
                    .put(GeneralConstants.StructuredLogKeys.RESULT_ID, resultIds.toString())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime))
                    .put(GeneralConstants.StructuredLogKeys.JSON_STRING, jsonString).build());
            return jSONResult;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                            + ErrorCodes.DATABASE_ERROR + "---- > [getBulkRecordsCount]", ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getSMSBulkRecords]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }
//    public void updateSMSBulkRecords(Connection conn,HashMap<String, Object> interfaceResult, ArrayList<Long> resultIds) throws CommonException {
//    PreparedStatement pstat = null;
//        try {
//            StringBuilder sql=new StringBuilder();
//            
//                sql.append("UPDATE ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME).append(" SET ")
//                        .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.STATUS).append(" = ? ,")
//                        .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.HTTP_INTERFACE_STATUS).append(" = ? ")
//                        .append("WHERE ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(" = ?");
//               pstat = conn.prepareStatement(sql.toString());
//            int i=0;
//             for(Object smsObj :((java.util.ArrayList)interfaceResult.get("SMSs"))){
//                 LinkedTreeMap sms=(LinkedTreeMap) smsObj;
//                 LinkedTreeMap smsValues =(LinkedTreeMap) sms.get("SMS");
//                String csMsgId = (String) smsValues.get("csMsgId");
//                String messageStatus = (String) smsValues.get("messageStatus");  
//                Long resultId = resultIds.get(i);                
//                pstat.setInt(1, Defines.VFE_CS_SMS_BULK_STATUS_LK.FINISHED);
//                pstat.setString(2, messageStatus);
//                pstat.setLong(3, resultId);
//                pstat.addBatch();              
//                i++;
//             }
//            CommonLogger.businessLogger.debug("Batch of Msgs Updated :"+resultIds.toString() );
//            pstat.executeBatch();
//        } catch (SQLException e) {
//            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
//                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
//            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
//                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
//            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
//        } finally {
//            if (pstat != null) {
//                try {
//                    pstat.close();
//                } catch (SQLException e) {
//                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
//                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
//                }
//            }
//        }
//    }

    public void deleteSMSBulkRecords(Connection conn, ArrayList<Long> resultIds) throws CommonException {
        PreparedStatement pstat = null;
        try {
            StringBuilder sql = new StringBuilder();

            sql.append("DELETE FROM ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME)
                    .append(" WHERE ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(" = ?");
            pstat = conn.prepareStatement(sql.toString());
            for (int i = 0; i < resultIds.size(); i++) {
                Long resultId = resultIds.get(i);
                pstat.setLong(1, resultId);
                pstat.addBatch();
            }
//            CommonLogger.businessLogger.debug("Batch of Msgs Deleted FROM " + DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME + " :" + resultIds.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Batch of Msgs Deleted")
                    .put(GeneralConstants.StructuredLogKeys.TABLE_NAME, DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME)
                    .put(GeneralConstants.StructuredLogKeys.RESULT_ID, resultIds.toString()).build());
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

    public void insertSMSBulkRecordsArchive(Connection conn, HashMap interfaceResult, ArrayList<Long> resultIds) throws CommonException {
        PreparedStatement pstat = null;
        boolean isGlobalError = false;
        try {
            long startTime = System.currentTimeMillis();
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.TABLE_NAME).append(" (")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.ID).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.MSG_PARAMETERS).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.JSON_OBJECT).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.SUBMISSION_DATE).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.SERVICE_NAME).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.FILE_NAME).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.CS_MSG_ID).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.HTTP_INTERFACE_STATUS).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.STATUS).append(") ")
                    .append("SELECT ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.MSG_PARAMETERS).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.JSON_OBJECT).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SUBMISSION_DATE).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SERVICE_NAME).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.FILE_NAME).append(", ")
                    .append("? ,? ,? ").append(" FROM ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME)
                    .append(" WHERE ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(" = ?");

            pstat = conn.prepareStatement(sql.toString());
            int i = 0;
//          if (((String) interfaceResult.get(Defines.INTERFACES.ERROR_CODE)).equalsIgnoreCase(ErrorCodes.SEND_SMS.SUCCESS + "")
//                    && ((String) interfaceResult.get(Defines.INTERFACES.ERROR_DESCRIPTION)).equalsIgnoreCase(Defines.INTERFACES.STATUS_SUCCESS)) 
//                CommonLogger.businessLogger.info("Global error at batch ,ErrorDesc :"+interfaceResult.get("ErrorDesc") +" ,ErrorCode :"+interfaceResult.get("ErrorCode"));
            if (((Double) interfaceResult.get(Defines.INTERFACES.ERROR_CODE)) != ErrorCodes.SEND_SMS.SUCCESS
                    || !((String) interfaceResult.get(Defines.INTERFACES.ERROR_DESCRIPTION)).equalsIgnoreCase(Defines.INTERFACES.STATUS_SUCCESS)) {
                isGlobalError = true;
            }
            LinkedTreeMap sms = null;
            String csMsgId = "";
            String messageStatus;
            for (long resultId : resultIds) {
                if (!isGlobalError) {
                    sms = (LinkedTreeMap) ((ArrayList) interfaceResult.get("SMSs")).get(i);
                    LinkedTreeMap smsValues = (LinkedTreeMap) sms.get("SMS");
                    csMsgId = (String) smsValues.get("csMsgId");
                    messageStatus = (String) smsValues.get("messageStatus");
                } else {
                    messageStatus = "Global error at batch ,ErrorDesc :" + interfaceResult.get("ErrorDesc") + " ,ErrorCode :" + interfaceResult.get("ErrorCode");
                }
                pstat.setString(1, csMsgId);
                pstat.setString(2, messageStatus);
                pstat.setInt(3, Defines.VFE_CS_SMS_BULK_STATUS_LK.FINISHED);
                pstat.setLong(4, resultId);
                pstat.addBatch();
                i++;
            }
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Batch of Msgs Archived at " + (System.currentTimeMillis() - startTime) + " Millis:" + resultIds.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Batch of Msgs Archived")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime))
                    .put(GeneralConstants.StructuredLogKeys.RESULT_ID, resultIds.toString()).build());
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

    public void insertFailedSMSBulkRecordsArchive(Connection conn, ArrayList<Long> resultIds) throws CommonException {
        PreparedStatement pstat = null;
        try {
            long startTime = System.currentTimeMillis();
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.TABLE_NAME).append(" (")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.ID).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.MSG_PARAMETERS).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.JSON_OBJECT).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.SUBMISSION_DATE).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.SERVICE_NAME).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.FILE_NAME).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS_ARCHIVE.STATUS).append(") ")
                    .append("SELECT ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.MSG_PARAMETERS).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.JSON_OBJECT).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SUBMISSION_DATE).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.SERVICE_NAME).append(", ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.FILE_NAME).append(", ")
                    .append("? ").append(" FROM ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME)
                    .append(" WHERE ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.ID).append(" = ?");

            pstat = conn.prepareStatement(sql.toString());
            int i = 0;
//            if(!interfaceResult.get("ErrorDesc").isEmpty())
//                CommonLogger.businessLogger.info("Global error at batch ,ErrorDesc :"+interfaceResult.get("ErrorDesc") +" ,ErrorCode :"+interfaceResult.get("ErrorCode"));
            for (long resultId : resultIds) {

                pstat.setInt(1, Defines.VFE_CS_SMS_BULK_STATUS_LK.FAILED);
                pstat.setLong(2, resultId);
                pstat.addBatch();
                i++;
            }
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Batch of Failure Msgs Archived at " + (System.currentTimeMillis() - startTime) + " Millis:" + resultIds.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Batch of Failure Msgs Archived")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime))
                    .put(GeneralConstants.StructuredLogKeys.RESULT_ID, resultIds.toString()).build());
            pstat.executeBatch();
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    CommonLogger.businessLogger.error("SQLException while closing PreparedStatement--->" + e);
                    CommonLogger.errorLogger.error("SQLException while closing PreparedStatement--->" + e, e);
                }
            }
        }
    }

    public int getBulkRecordsCount(Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Starting [getBulkRecordsCount]");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getBulkRecordsCount Started").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet rs = null;
        int recordsCount = 0;
        long startTime = System.currentTimeMillis();
        RequestPreparator jSONResult = new RequestPreparator();
        try {
            sql = new StringBuilder();
            sql.append("SELECT COUNT(*) BulkCount FROM ").append(DBStruct.VFE_CS_SMS_BULK_RECORDS.TABLE_NAME).append(" WHERE ")
                    .append(DBStruct.VFE_CS_SMS_BULK_RECORDS.STATUS).append(" = ").append(Defines.VFE_CS_SMS_BULK_STATUS_LK.READY);

            statement = connection.prepareStatement(sql.toString());
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + " [Query] :" + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getBulkRecordsCount Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());

            rs = statement.executeQuery();
            if (rs.next()) {
                recordsCount = rs.getInt("BulkCount");
            }

//            CommonLogger.businessLogger.debug("Bulk records count = " + recordsCount);
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "Selection time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(SMSBulkFilesDAO.class.getName() + " || " + "End [getBulkRecordsCount]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getBulkRecordsCount Ended")
                    .put(GeneralConstants.StructuredLogKeys.RECORD_COUNT, recordsCount)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
            return recordsCount;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getBulkRecordsCount]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getBulkRecordsCount]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getBulkRecordsCount]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getBulkRecordsCount]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }
    }
}
