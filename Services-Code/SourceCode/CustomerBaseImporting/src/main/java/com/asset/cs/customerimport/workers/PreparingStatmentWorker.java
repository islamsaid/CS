/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.workers;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.LineModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.managers.CustomerImportingManager;
import static com.asset.cs.customerimport.managers.CustomerImportingManager.no_Writer_ThreadPools;
import com.asset.cs.customerimport.managers.ResourcesCachingManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Zain Al-Abedin
 */
public class PreparingStatmentWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.workers.PreparingStatmentWorker";
    private int workerDataQueueID = -1;
    private static Vector<DWHElementModel> dwhElementsList;
    private MainService mainServ = new MainService();

    public PreparingStatmentWorker() {
        String methodName = "Constructor";
        try {
            dwhElementsList = mainServ.getDWHElementsList();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Fatal Error PreparingStatmentWorker Constructor", ex);
            CommonLogger.businessLogger.error("Fatal Error PreparingStatmentWorker Constructor -->" + ex);
            CustomerImportingManager.setCatchingError(true);
            handleServiceException(ex, methodName);
        }
    }

    public void run() {
        Thread.currentThread().setName(this.getName());
        CommonLogger.businessLogger.info("--------- Starting PreparingStatmentWorker : " + this.getName() + " ----------");
        String methodName = "run";
        try {
            long sleepTime = ResourcesCachingManager.getLongValue(EngineDefines.PREPARING_STATMENT_THREAD_SLEEP_TIME);
            //String dwRowData = null;
            LineModel line = null;
            Vector<Object> insertStatment = null;
            while ((!CustomerImportingManager.isPreparingWorkersShutdownFlag()
                    || !CustomerImportingManager.readersDataQueue.isEmpty())
                    && CustomerImportingManager.isCatchingError() != true) /*Ensure that shut down flag is set and dwReaderDataList is empty*/ {
                line = CustomerImportingManager.readersDataQueue.poll(sleepTime, TimeUnit.MILLISECONDS);
                if (line == null) {
                    continue;
                }
                workerDataQueueID = -1;
                insertStatment = parseLine(line);
                if (insertStatment != null && !insertStatment.equals("")) {
                    line.setInsertData(insertStatment);
                    line.setLineData(null);//to free memory
                    if (!CustomerImportingManager.writersDataQueuesMap.containsKey(workerDataQueueID)) {
                        //kashif change log
//                        CommonLogger.businessLogger.info("FAILED To Get MSISDN Last Two Digits Worker Data Queue ID=[" + workerDataQueueID + "] LineNum=[" + line.getLineNum() + "] from file=[" + line.getFileName() + "]  LineDate=[" + line.getLineData() + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Failed to Get MSISDN last TWO Digits Worker Data")
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, workerDataQueueID)
                                .put(GeneralConstants.StructuredLogKeys.LINE_NUMBER, line.getLineNum())
                                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, line.getFileName())
                                .put(GeneralConstants.StructuredLogKeys.DATA, line.getLineData())
                                .put(GeneralConstants.StructuredLogKeys.STATUS, "failed").build());
//                        CommonLogger.errorLogger.error("FAILED To Get MSISDN Last Two Digits Worker Data Queue ID=[" + workerDataQueueID + "] LineNum=[" + line.getLineNum() + "] from file=[" + line.getFileName() + "]  LineDate=[" + line.getLineData() + "]");
                        //kashif change log
                        continue;
                    }
                    while (!CustomerImportingManager.writersDataQueuesMap.get(workerDataQueueID).offer(line) && !CustomerImportingManager.isCatchingError()) {
//                        CommonLogger.businessLogger.info("Worker Data Queue ID=[" + workerDataQueueID + "] Is Full," + this.getName() + " will sleep for[" + sleepTime + "]msec");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Worker Data is Full")
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_ID, workerDataQueueID)
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, this.getName())
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
                        this.sleep(sleepTime);
                    }

                }
            }

            CommonLogger.businessLogger.info("--------- Shutdown PreparingStatmentWorker : " + this.getName() + " ----------");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Exception -->", e);
            CommonLogger.businessLogger.error("Fatal Exception -->" + e);
            CustomerImportingManager.setCatchingError(true);
            setWorkerAlive(false);
            handleServiceException(e, methodName);
        } finally {
            CommonLogger.businessLogger.info("--------- Shutdown PreparingStatmentWorker : " + this.getName() + " ----------");
        }
    }

    private Vector<Object> parseLine(LineModel dwRowData) throws CommonException {
        String methodName = "parseLine";
        Vector<Object> insertStatment = null;
        String[] lineData = null;
        try {
            lineData = dwRowData.getLineData().split(EngineDefines.DW_FILE_SPLITTER, -1);

            if (lineData != null && lineData.length > 0) {
                insertStatment = prepareInsertStatment(dwRowData, lineData);
            }
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Parsing Error method: " + methodName + " LineNum=[" + dwRowData.getLineNum() + "] from file=[" + dwRowData.getFileName() + "]  LineDate=[" + dwRowData.getLineData() + "]", e);
            CommonLogger.businessLogger.error("Parsing Error method: " + methodName + " LineNum=[" + dwRowData.getLineNum() + "] from file=[" + dwRowData.getFileName() + "]  LineDate=[" + dwRowData.getLineData() + "]" + e);
            throw new CommonException("Parsing Error method: " + methodName + "  LineNum=[" + dwRowData.getLineNum() + "] from file=[" + dwRowData.getFileName() + "]  LineDate=[" + dwRowData.getLineData() + "]" + e, ErrorCodes.ERROR_IN_SPLITTING_DWH_LINE);
        }

        return insertStatment;
    }

    private Vector<Object> prepareInsertStatment(LineModel line, String[] lineData) throws CommonException {
        String methodName = "prepareInsertStatment";
        Vector<Object> valuesString = new Vector<>();
        String columnValue = "";
        String columnName = "";
        int dataType;
        String msisdn = "";
        int governmentId;
        int customerType;
        int lastMsisdnDigit = 0;
        LookupModel model = null;
        try {
            for (DWHElementModel dwhElementsModel : dwhElementsList) {
                if (dwhElementsModel.getFileIndex() <= lineData.length) {
                    columnValue = lineData[dwhElementsModel.getFileIndex() - 1];
                    if (dwhElementsModel != null) {
                        dataType = dwhElementsModel.getDataTypeId();
                        columnName = dwhElementsModel.getDwhName();
                        if (columnName.equals(DBStruct.DWH_CUSTOMERS.MSISDN))/*MSISDN Column*/ {
                            if (validateMSISDNFormatNew(columnValue)) {
                                if (columnValue.startsWith("00")) {
                                    msisdn = columnValue.substring(2);
                                } else if (columnValue.startsWith("01")) {
                                    msisdn = "2" + columnValue;
                                } else if (columnValue.startsWith("1")) {
                                    msisdn = "20" + columnValue;
                                } else if (columnValue.startsWith("+")) {
                                    msisdn = columnValue.substring(1);
                                } else {
                                    msisdn = columnValue;
                                }
                                lastMsisdnDigit = Integer.parseInt(columnValue.substring(msisdn.length() - 2));
                                workerDataQueueID = lastMsisdnDigit % CustomerImportingManager.no_Writer_ThreadPools;
                            } else {  //not valid msisdn
                                CommonLogger.businessLogger.error("Caught Invalid MSISDN=[" + columnValue + "]  at Line=[" + line.getLineNum() + "]  File=[" + line.getFileName() + "] ");
                                CommonLogger.errorLogger.error("Caught Invalid MSISDN=[" + columnValue + "]  at Line=[" + line.getLineNum() + "]  File=[" + line.getFileName() + "] ");
                                sendMom(ErrorCodes.ERROR_INVALID_MSISDN_FORMATE, "Caught Invalid MSISDN", "Line=[" + line.getLineNum() + "]  File=[" + line.getFileName() + "]", ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_INVALID_MSISDN_FORMATE_MOM_SEVERITY));
                                continue;
                            }
                            //Old Validation Code
//                            if (true || validateMSISDNFormat(columnValue)) {
//                                msisdn = columnValue;
//                                lastMsisdnDigit = Integer.parseInt(columnValue.substring(columnValue.length() - 2));
//                                workerDataQueueID = lastMsisdnDigit % CustomerImportingManager.no_Writer_ThreadPools;
//
//                            } else {
//                                throw new CommonException("Invalid Msisdn: " + columnValue + " in :" + this.getName() + " method: " + methodName, ErrorCodes.ERROR_IN_PREPARING_INSERT_STATMENT);
//                            }
                        }
                        if (columnName.equals(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID))/*Government Column*/ {
                            model = ResourcesCachingManager.governmentMap.get(columnValue);
                            if (model != null) {
                                governmentId = model.getId();
                                valuesString.add(new Integer(governmentId));
                            } else {
//                                CommonLogger.businessLogger.info("Government [" + columnValue + "] not exist in Governments Lookup found at Line=[" + line.getLineNum() + "] file=[" + line.getFileName() + "]");
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Government Doesnot exist in Government Lookup")
                                        .put(GeneralConstants.StructuredLogKeys.GOVERNMENT, columnValue)
                                        .put(GeneralConstants.StructuredLogKeys.LINE_NUMBER, line.getLineNum())
                                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, line.getFileName()).build());
                                valuesString.add(null);
                            }
                        } else if (columnName.equals(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE))/*Customer Type Column*/ {
                            model = ResourcesCachingManager.customerTypesMap.get(columnValue);
                            if (model != null) {
                                customerType = model.getId();
                                valuesString.add(new Integer(customerType));
                            } else {
//                                CommonLogger.businessLogger.info("Customer Type [" + columnValue + "] not exist in Customer Type Lookup found at Line=[" + line.getLineNum() + "] file=[" + line.getFileName() + "]");
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Doesnot exist in Government Lookup")
                                        .put(GeneralConstants.StructuredLogKeys.CUSTOMER_TYPE, columnValue)
                                        .put(GeneralConstants.StructuredLogKeys.LINE_NUMBER, line.getLineNum())
                                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, line.getFileName()).build());
                                valuesString.add(null);
                            }

                        } //                        else if (columnName.equals(DBStruct.C_DWH_PROFILE_DAILY_CAMPAIGN_SMS)) /*String type*/ {
                        //                            //   columnsString = columnsString + columnName + ",";
                        //                            if (columnValue == null || columnValue.equals("")) {
                        //                                //  valuesString = valuesString + "null,";
                        //                                valuesString.add(new String("N"));
                        //                            } else {
                        //                                //  columnValue = columnValue.replaceAll("'", "''");
                        //                                //  valuesString = valuesString + "'" + columnValue + "',";
                        //                                valuesString.add(new String(columnValue));
                        //                            }
                        //                        } 
                        else if (dataType == GeneralConstants.DWH_ELEMENT_STRING) /*String type*/ {
                            if (columnValue == null || columnValue.equals("")) {
                                valuesString.add(null);
                            } else {
                                valuesString.add(new String(columnValue));
                            }
                        } else if (dataType == GeneralConstants.DWH_ELEMENT_DATE)/*Date type*/ {
                            if (columnValue == null || columnValue.equals("")) {
                                valuesString.add(null);
                            } else {
                                try {
                                    SimpleDateFormat format = new SimpleDateFormat(GeneralConstants.DWH_DATE_FORMAT);
                                    Date parsed = format.parse(columnValue);
                                    java.sql.Date sql = new java.sql.Date(parsed.getTime());
                                    valuesString.add(sql);
                                } catch (ParseException e) {
                                    SimpleDateFormat format = new SimpleDateFormat(GeneralConstants.DWH_DATE_FORMAT_WITH_OUT_TIME);
                                    Date parsed = format.parse(columnValue);
                                    java.sql.Date sql = new java.sql.Date(parsed.getTime());
                                    valuesString.add(sql);
                                }
                            }
                        } else if (dataType == GeneralConstants.DWH_ELEMENT_NUMERIC)/*Numric Type*/ {
                            if (columnValue == null || columnValue.equals("")) {
                                valuesString.add(null);
                            } else {
                                valuesString.add(new Double(columnValue));
                            }
                        }
                    }
                } else {
                    valuesString.add(null);
//                    CommonLogger.businessLogger.info("dw elements more than the elements in the file element index:" + dwhElementsModel.getFileIndex());
//                    CommonLogger.businessLogger.debug("dw elements more than the elements in the file element index:" + dwhElementsModel.getFileIndex());
                }
            }
            valuesString.add(new Integer(lastMsisdnDigit));
            valuesString.add(new Integer(ResourcesCachingManager.runId));
            if ((dwhElementsList.size() + 2) != valuesString.size()) {
//                CommonLogger.businessLogger.info("! msisdn " + msisdn + " Elements list: " + dwhElementsList.size() + " values String" + valuesString.size());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DWH Elements List Info")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN, msisdn)
                        .put(GeneralConstants.StructuredLogKeys.SIZE, dwhElementsList.size())
                        .put(GeneralConstants.StructuredLogKeys.VALUE, valuesString.size()).build());
            }
        } catch (CommonException ex) {
            throw ex;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Parsing Error method: " + methodName + " LineNum=[" + line.getLineNum() + "] from file=[" + line.getFileName() + "]  LineDate=[" + line.getLineData() + "]", e);
            CommonLogger.businessLogger.error("Parsing Error method: " + methodName + " LineNum=[" + line.getLineNum() + "] from file=[" + line.getFileName() + "]  LineDate=[" + line.getLineData() + "]" + e);
            throw new CommonException("Parsing Error in :method: " + methodName + " LineNum=[" + line.getLineNum() + "] from file=[" + line.getFileName() + "]  LineDate=[" + line.getLineData() + "] " + e, ErrorCodes.ERROR_IN_PREPARING_INSERT_STATMENT);
        }
        CommonLogger.businessLogger.trace("Preparing LineNum=[" + line.getLineNum() + "] from file=[" + line.getFileName() + "]  LineDate=[" + line.getLineData() + "] Insert Data=[" + valuesString + "]");
        return valuesString;
    }
//    Old validation Function
//    private boolean validateMSISDNFormat(String msisdn) throws CommonException {
//        String methodName = "validateMSISDNFormat";
//        Matcher matcher = null;
//        try {
//            Pattern pattern = Pattern.compile(GeneralConstants.MSISDN_VALIDATE_PATTERN);
//            matcher = pattern.matcher(msisdn);
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Error in validating MSISDN=[" + msisdn + "] ", e);
//            throw new CommonException("Error in validating MSISDN in :" + this.getName() + " method: " + methodName + e, ErrorCodes.ERROR_IN_PREPARING_INSERT_STATMENT);
//        }
//        return matcher.matches();
//    }

    private boolean validateMSISDNFormatNew(String msisdn) throws CommonException {
        String methodName = "validateMSISDNFormat";
        Matcher matcher = null;
        try {
            Pattern pattern = Pattern.compile(GeneralConstants.MSISDN_VALIDATE_PATTERN);
            matcher = pattern.matcher(msisdn);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error in validating MSISDN=[" + msisdn + "] ", e);
            CommonLogger.businessLogger.error("Error in validating MSISDN=[" + msisdn + "] " + e);
            throw new CommonException("Error in validating MSISDN in :" + this.getName() + " method: " + methodName + e, ErrorCodes.ERROR_IN_PREPARING_INSERT_STATMENT);
        }
        return matcher.matches();
    }

    private void handleServiceException(Exception e, String methodName) {

        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_FAILED_EXTRACT_DWH_FILES_MOM_SEVERITY)));
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_FAILED_EXTRACT_DWH_FILES_MOM_SEVERITY)));

            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getErrorMsg());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

    private void sendMom(String errorCode, String errorMsg, String errorPar, String severity) {
        try {
            MOMErrorsModel errorModel = new MOMErrorsModel();
            errorModel.setPreceivedSeverity(Integer.parseInt(severity));
            errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
            errorModel.setErrorCode(errorCode);
            errorModel.setErrorMessage(errorMsg);
            errorModel.setErrorParams(errorPar);
            errorModel.setModuleName(CLASS_NAME);
            Utility.sendMOMAlarem(errorModel);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error--->", e);
            CommonLogger.businessLogger.error("Fatal Error--->" + e);
        }
    }

}
