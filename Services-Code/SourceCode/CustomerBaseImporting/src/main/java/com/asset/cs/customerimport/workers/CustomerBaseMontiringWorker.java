/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.workers;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.managers.ResourcesCachingManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nada.nagy
 */
public class CustomerBaseMontiringWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.workers.CustomerBaseMontiringWorker";
    private static SystemPropertiesModel systemPropertiesModel = null;
    private int configuredHours = ResourcesCachingManager.getIntValue(EngineDefines.FILE_RECEIVED_NUMBER_OF_HOURS);
    //private DateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SS a");
    private DateFormat format = new SimpleDateFormat(GeneralConstants.SYSTEM_PROPERTY_JAVA_DATE_FORMAT);

    private MainService mainServ = new MainService();

    public CustomerBaseMontiringWorker() {
        systemPropertiesModel = new SystemPropertiesModel();
        systemPropertiesModel.setItemKey(EngineDefines.DWH_LAST_ERROR_DATE);
        systemPropertiesModel.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        calculateReceivingTime();
    }

    public void run() {
        CommonLogger.businessLogger.info("--------- Starting customer base montiring thread : " + this.getName() + " ----------");
        String methodName = "run";
        long sleepTime = 300000; //default 5 Minute
//        try {
        try {
            sleepTime = ResourcesCachingManager.getLongValue(EngineDefines.CUSTOMER_BASE_MONITORING_THREAD_SLEEP_TIME);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error Starting Thread--->", ex);
            CommonLogger.businessLogger.error("Fatal Error  Starting Thread-->" + ex);
        }
        while (!isWorkerShutDownFlag()) {
            try {
                calculateReceivingTime();
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Fatal Error-->", e);
                CommonLogger.businessLogger.error("Fatal Error-->" + e);
                handleServiceException(e, methodName);
            } finally {
                try {
                    this.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    CommonLogger.errorLogger.error("InterruptedException -->", ex);
                    CommonLogger.businessLogger.error("InterruptedException-->" + ex);
                }
            }
        }
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Fatal Error-->", e);
//            CommonLogger.businessLogger.error("Fatal Error-->" + e);
//            handleServiceException(e, methodName);
//        }

    }

    public void calculateReceivingTime() {
        String methodName = "calculateReceivingTime";
        Connection connection = null;
        try {

            String lastErrorDateTime = mainServ.getSystemPropertyByKey(EngineDefines.DWH_LAST_ERROR_DATE, GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
            String lastUploadedDateTime = mainServ.getSystemPropertyByKey(EngineDefines.DWH_LAST_UPLOADED_DATE, GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);

            Date lastErrorDate = format.parse(lastErrorDateTime);
            Date lastUploadedDate = format.parse(lastUploadedDateTime);
            Date date = new Date();
            float lastErrorHours = (float) (lastErrorDate.getTime() / (1000 * 60 * 60));
            float nowHours = (float) (date.getTime() / (1000 * 60 * 60));
            float lastUploadedHours = (float) (lastUploadedDate.getTime() / (1000 * 60 * 60));

            if (nowHours - lastErrorHours > configuredHours && nowHours - lastUploadedHours > configuredHours) {
//                CommonLogger.businessLogger.info(" lastErrorHours [" + lastErrorDateTime + "]" + " nowHours [" + nowHours + "]" + " lastUploadedHours[" + lastErrorDateTime + "]");
//                CommonLogger.businessLogger.info("DWH File not received and no changes were made on current base");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DWH File not Recieved and No Changes were Made on Current Base")
                        .put(GeneralConstants.StructuredLogKeys.LAST_ERROR_HOURS, lastErrorDateTime)
                        .put(GeneralConstants.StructuredLogKeys.CURRENT_TIME, nowHours)
                        .put(GeneralConstants.StructuredLogKeys.LAST_UPLOAD_HOURS, lastErrorDateTime));
                CommonException campaignException = new CommonException("DWH File not received and no changes were made on current base", ErrorCodes.ERROR_NO_FILES_RECEIVED);
                //add record in service errors
                handleServiceException(campaignException, methodName);
                //update last error date in db
                // systemPropertiesModel.setItemValue(format.format(new Date()));
                systemPropertiesModel.setItemValue("");
                mainServ.updateTimeSystemProperty(systemPropertiesModel);

            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error-->", e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            handleServiceException(e, methodName);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Fatal Error-->", ex);
                CommonLogger.businessLogger.error("Fatal Error-->" + ex);
                handleServiceException(ex, methodName);
            }
        }
    }

    private void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        MOMErrorsModel errorModel = new MOMErrorsModel();
        if (e instanceof CommonException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_NO_FILES_RECEIVED_MOM_SEVERITY)));
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_LOW);
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getErrorMsg());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);
    }
}
