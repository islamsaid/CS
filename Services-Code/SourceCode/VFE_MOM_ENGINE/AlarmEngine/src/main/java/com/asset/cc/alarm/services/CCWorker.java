/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cc.alarm.services;


import com.asset.alarmengine.common.defines.EngineDBStructs;
import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.alarmengine.common.models.HServiceErrorModel;
import com.asset.alarmengine.common.service.MainService;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dalia.Mohammad
 */
public class CCWorker extends Thread {
    
    private boolean workerShutDownFlag = false;

    public boolean isWorkerShutDownFlag() {
        return workerShutDownFlag;
    }

    public void setWorkerShutDownFlag(boolean workerShutDownFlag) {
        this.workerShutDownFlag = workerShutDownFlag;
    }
    
    protected void handleServiceException(Exception e, String methodName, String className) 
    {
        EngineException engineException = null;
        // Handle ContextualCampaignException 
        if (e instanceof EngineException)
            engineException = (EngineException) e;
        // Handle SQL Exception 
        else if (e instanceof SQLException)
            engineException = new EngineException(e, EngineErrorCodes.GENERAL_SQL_ERROR, "Sql exception in CLASS:" + className + " method: " + methodName);
        // Handle Parsing Exception
        else if (e instanceof ParseException)
        engineException = new EngineException(e,EngineErrorCodes.PARSE_ERROR, "Parsing exception in CLASS:" + className + " method: " + methodName);
        // Handle other exception types
        else 
            engineException = new EngineException(e,EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + className + " method: " + methodName);
        
        HServiceErrorModel errorModel = new HServiceErrorModel();
        errorModel.setErrorType(EngineDefines.ERROR_TYPE_ALARM_ENGINE); //TODOH
        errorModel.setErrorSeverity(EngineDefines.ERROR_SEVERITY_CRITICAL);
        errorModel.setErrorTime(new Date());
        /*Add Service Error To DB*/
   //     HServiceErrorService errorService = new HServiceErrorService();
     //   errorService.handleServiceError(errorModel, campaignException);
       //kashif changes
        MainService mainService=new MainService();
        try {        
            mainService.handleServiceError(errorModel, engineException,EngineDBStructs.SERVICE_ERRORS.SERVICE_ERRORS_TBL , EngineDBStructs.SERVICE_ERRORS.ID, EngineDBStructs.SERVICE_ERRORS.ERROR_TYPE, EngineDBStructs.SERVICE_ERRORS.ERROR_SEVERITY, EngineDBStructs.SERVICE_ERRORS.ERROR_PARAMS, EngineDBStructs.SERVICE_ERRORS.ERROR_MESSAGE, EngineDBStructs.SERVICE_ERRORS.INSERTION_TIME, EngineDBStructs.SERVICE_ERRORS.SERVICE_ERRORS_SEQ);
        } catch (EngineException ex) {
                EngineLogger.debugLogger.error("Exception in handleServiceException--->"+e);
            EngineLogger.errorLogger.error("Exception in handleServiceException--->"+e,e);
       }
        
         
    }
    
}

