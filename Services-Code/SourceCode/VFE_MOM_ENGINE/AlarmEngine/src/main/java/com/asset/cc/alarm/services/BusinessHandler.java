/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cc.alarm.services;

import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.alarmengine.common.models.HServiceErrorModel;
import com.asset.alarmengine.common.service.MainService;
import com.asset.alarmengine.common.defines.EngineDBStructs.SERVICE_ERRORS;
import com.asset.alarmengine.common.defines.EngineDBStructs.LK_ERRORS_TYPES;
import com.asset.cc.alarm.constants.Defines;
import com.asset.cc.alarm.managers.ShutdownManager;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mahmoud.abdou
 */
public class BusinessHandler {

    private static final String CLASS_NAME = "com.asset.cc.alarm.services.BusinessHandler";
    private Connection connection = null;
    private Vector<HServiceErrorModel> records;
    private static HashMap propertiesMap = new HashMap();
    private HashMap<String, String> dbProperties;
    private File file;
    private FileInputStream fis;
    private BufferedWriter writer;
    private FTPClient ftpClient;
    private String errorCode;
    private String errorMessage;
    private int errorSeverity;
 //   private int errorType = GeneralConstants.ERROR_TYPE_ALARM_ENGINE;
   //kashif changes
    private String errorType = EngineDefines.ERROR_TYPE_ALARM_ENGINE;
    ////
    private String errorParams;
    private boolean result;

    public BusinessHandler(Connection connection, HashMap<String, String> dbProperties) {
        this.connection = connection;
        this.dbProperties = dbProperties;
    }

    public void setDbProperties(HashMap<String, String> dbProperties) {
        this.dbProperties = dbProperties;
    }

    public boolean WriteAlarmFiles() {

        EngineLogger.debugLogger.info("---------- Started Bussiness handler-------------");

        String methodName = "WriteAlarmFiles";
        try {

        //    records = HServiceErrorDAO.getServiceErrorsByDate(connection, dbProperties.get(Defines.KEY_LAST_ALARM), dbProperties.get(Defines.SEVERITIES_COMMA_SEPARATED),
          //          dbProperties.get(Defines.ALARM_ENGINE_TIMESTAMP_FORMAT));
            //kashif changes
            MainService mainService=new MainService();
            records = mainService.getServiceErrorsByDate(dbProperties.get(String.valueOf(EngineDefines.KEY_LAST_ALARM_CONSTANT_KEY)), dbProperties.get(String.valueOf(EngineDefines.SEVERITIES_COMMA_SEPARATED_CONSTANT_KEY)),  dbProperties.get(String.valueOf(EngineDefines.ALARM_ENGINE_TIMESTAMP_FORMAT_CONSTANT_KEY)), SERVICE_ERRORS.SERVICE_ERRORS_TBL, SERVICE_ERRORS.ID, SERVICE_ERRORS.ERROR_TYPE, SERVICE_ERRORS.ERROR_SEVERITY
                    , SERVICE_ERRORS.ERROR_PARAMS, SERVICE_ERRORS.ERROR_MESSAGE, SERVICE_ERRORS.INSERTION_TIME, SERVICE_ERRORS.SERVICE_ERRORS_SEQ, LK_ERRORS_TYPES.LK_ERROR_TYPES_TBL, LK_ERRORS_TYPES.ERROR_TYPE_LABEL, LK_ERRORS_TYPES.ERROR_TYPE_ID);
            
            ///
            if (!records.isEmpty()) {
                EngineLogger.debugLogger.info("---Processing '" + records.size() + "' records---");

                EngineLogger.debugLogger.info("---Connecting to FTP server---");

                connectToFtp();

                if (ftpClient.connected()) {
                    EngineLogger.debugLogger.info("---Connected to FTP server successfully---");
                    writeRecordsToFiles(records);
                    result = true;
                } else {
                    EngineLogger.debugLogger.error("---Connection to FTP server failed---");
                    result = false;
                }

            } else {
                EngineLogger.debugLogger.info("---No records found to process---");
                result = true;
            }

        } catch (Exception ex) {
            handleServiceException(ex, methodName);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                handleServiceException(ex, methodName);
            }

        }
        EngineLogger.debugLogger.info("---------- Finished Bussiness handler-------------");
        return result;
    }

    private void writeRecordsToFiles(Vector<HServiceErrorModel> records) throws IOException, FTPException, EngineException {

        EngineLogger.debugLogger.info("---------- Started Writing to FTP-------------");
        for (HServiceErrorModel model : records) {

            writeRecordToFile(model, "Alarm_" + model.getId());
//            file = File.createTempFile("temp", ".txt");
//
//            PrintStream printStream = new PrintStream(file);
//            printStream.println(propertiesMap.get(Defines.FILE_NODE).toString());
//            printStream.println("" + model.getErrorSeverity());
//            printStream.println(model.getErrorTypeString());
//            printStream.println(model.getErrorTypeString());
//            printStream.println(propertiesMap.get(Defines.FILE_LOCATION_OF_NODE).toString());
//            printStream.println(propertiesMap.get(Defines.FILE_OTHERS).toString());
//            printStream.println(model.getErrorMessage());
//
//            InputStream in = new FileInputStream(file);
//            byte[] data = new byte[in.available()];
//            in.read(data);
//            file.delete();
//            ftpClient.put(data, dbProperties.get(Defines.FTP_SERVER_REMOTE_PATH) + "/Alarm_" + model.getId() + ".txt");

        }

        EngineLogger.debugLogger.info("---------- Finished Writing to FTP-------------");
    }

    public void writeRecordToFile(HServiceErrorModel model, String fileName) throws IOException, FTPException, EngineException {

//        LogUtil.info("---------- Started Writing to FTP-------------");
        if (propertiesMap == null || propertiesMap.isEmpty()) {
            propertiesMap = ShutdownManager.getPropertiesMap();
        }
        if (ftpClient == null || !ftpClient.connected()) {
            connectToFtp();
        }
        file = File.createTempFile("temp", ".txt");

        PrintStream printStream = new PrintStream(file);
        printStream.println(propertiesMap.get(EngineDefines.FILE_NODE).toString());
        printStream.println("" + model.getErrorSeverity());
        printStream.println(model.getErrorTypeString());
        printStream.println(model.getErrorTypeString());
        printStream.println(propertiesMap.get(EngineDefines.FILE_LOCATION_OF_NODE).toString());
        printStream.println(propertiesMap.get(EngineDefines.FILE_OTHERS).toString());
        int maxChars = Integer.valueOf(propertiesMap.get(EngineDefines.FILE_ERROR_MESSAGE_MAX_CHARS).toString());
        if(model.getErrorParams()!=null&&!model.getErrorParams().trim().isEmpty())
        {
        model.setErrorMessage(model.getErrorMessage().concat(",").concat(model.getErrorParams()));
        }
        if(model.getErrorMessage()!=null){
        if (model.getErrorMessage().length() > maxChars) {
                printStream.println(model.getErrorMessage().substring(0, maxChars));
            } else {
                printStream.println(model.getErrorMessage());
            }
        }
        InputStream in = new FileInputStream(file);
        byte[] data = new byte[in.available()];
        in.read(data);
        file.delete();
//        ftpClient.put(data, dbProperties.get(Defines.FTP_SERVER_REMOTE_PATH) + "/Alarm_" + model.getId() + ".txt");
        ftpClient.put(data, dbProperties.get(String.valueOf(EngineDefines.FTP_SERVER_REMOTE_PATH_CONSTANT_KEY)) + "/" + fileName + ".txt");

//        LogUtil.info("---------- Finished Writing to FTP-------------");
    }

    private void connectToFtp() throws EngineException {
        ftpClient = new FTPClient();
        try {
            ftpClient.setRemoteHost(dbProperties.get(String.valueOf(EngineDefines.FTP_SERVER_IP_CONSTANT_KEY)));
            ftpClient.setRemotePort(Integer.valueOf(dbProperties.get(String.valueOf(EngineDefines.FTP_SERVER_PORT_CONSTANT_KEY))).intValue());
            ftpClient.connect();
            ftpClient.login(dbProperties.get(String.valueOf(EngineDefines.FTP_SERVER_USER_CONSTANT_KEY)), dbProperties.get(String.valueOf(EngineDefines.FTP_SERVER_PASSWORD_CONSTANT_KEY)));
        } catch (Exception ex) {
            throw new EngineException(EngineErrorCodes.FTP_CONNECTION_ERROR, "Exception while connecting to FTP server, Message:" + ex.getMessage());
        }
    }

    private void handleServiceException(Exception e, String methodName) {
        EngineException engineException = null;

        if (e != null) {
            // Handle ContextualCampaignException 
            if (e instanceof EngineException) {
                engineException = (EngineException) e;
            } // Handle SQL Exception 
            else if (e instanceof SQLException) {
                engineException = new EngineException(e, EngineErrorCodes.GENERAL_SQL_ERROR, "Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName);
            } // Handle other exception types
            else {
                engineException = new EngineException(e,EngineErrorCodes.GENERAL_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
            }
        }
      //  errorSeverity = GeneralConstants.ERROR_SEVERITY_CRITICAL;
        //kashif changes
        errorSeverity= EngineDefines.ERROR_SEVERITY_CRITICAL;
        //
        HServiceErrorModel errorModel = getErrorModel();

        /*Add Service Error To DB*/
     //   HServiceErrorService errorService = new HServiceErrorService();
          //kashif changes
        MainService mainService=new MainService();
        try {
            //
            mainService.handleServiceError(errorModel, engineException,SERVICE_ERRORS.SERVICE_ERRORS_TBL , SERVICE_ERRORS.ID, SERVICE_ERRORS.ERROR_TYPE, SERVICE_ERRORS.ERROR_SEVERITY, SERVICE_ERRORS.ERROR_PARAMS, SERVICE_ERRORS.ERROR_MESSAGE, SERVICE_ERRORS.INSERTION_TIME, SERVICE_ERRORS.SERVICE_ERRORS_SEQ);
         //   errorService.handleServiceError(errorModel, campaignException);
        } catch (EngineException ex) {
               EngineLogger.debugLogger.error("Exception in handleServiceException--->"+e);
            EngineLogger.errorLogger.error("Exception in handleServiceException--->"+e,e);
      
        }
    }

    private HServiceErrorModel getErrorModel() {
        HServiceErrorModel model = new HServiceErrorModel();

        model.setErrorCode(errorCode);
        model.setErrorMessage(errorMessage);
        model.setErrorParams(errorParams);
        model.setErrorSeverity(errorSeverity);
        model.setErrorTime(new Date());
        model.setErrorType(errorType);
        model.setMsisdn("-1");
        model.setTxId("-1");

        return model;
    }
}
