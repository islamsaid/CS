/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.workers;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.managers.CustomerImportingManager;
import com.asset.cs.customerimport.managers.ResourcesCachingManager;
import com.asset.cs.customerimport.managers.ShutdownManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author nancy.abdelgawad
 */
public class ConfigurationWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cc.dwExtractor.services.ConfigurationWorker";

    public ConfigurationWorker() {

    }

    public void run() {
        CommonLogger.businessLogger.info("--------- Starting ConfigurationWorker : " + this.getName() + " ----------");
        String methodName = "run";
        try {
            long sleepTime = ResourcesCachingManager.getLongValue(EngineDefines.CONFUGURATION_THREAD_SLEEP_TIME);
            while (true) {
                try {
                    ResourcesCachingManager.loadDBConfigurations();

                   // ResourcesCachingManager.checkEngineShutDownFlag();

                    if(ResourcesCachingManager.shutdownEngineFlag.get()){
                        break;
                    }
                    this.sleep(sleepTime);
                } catch (Exception e) {
                    CommonLogger.errorLogger.error("Fatal Error--->", e);
                    CommonLogger.businessLogger.error("Fatal Error--->" + e);
                    handleServiceException(e, methodName);
                }

            }

           // shutdownDWHExtractorEngine();
            CommonLogger.businessLogger.info("--------- Shutdown ConfigurationWorker : " + this.getName() + " ----------");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error -->", e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            handleServiceException(e, methodName);
        } finally {
            CommonLogger.businessLogger.info("--------- Shutdown ConfigurationWorker : " + this.getName() + " ----------");
        }

    }

//    private void shutdownDWHExtractorEngine() {
//        CommonLogger.businessLogger.info("--------- Starting shutdown DWHExtractorEngine ------------");
//        //CustomerImportingManager.shutdownCustomerImportingManager();
//        ShutdownManager.shutDown();
//
//    }

    private void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_MEDIUM);
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
