/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.workers;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customermatching.constants.EngineDefines;
import com.asset.cs.customermatching.managers.CustomerMatchingManager;
import com.asset.cs.customermatching.managers.ResourcesCachingManager;
import com.asset.cs.customermatching.managers.ShutdownManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author nancy.abdelgawad
 */
public class ConfigurationWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customermatching.workers.ConfigurationWorker";

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
                } catch (CommonException e) {
                    CommonLogger.errorLogger.error("Fatal Error -->"+e, e);
                    CommonLogger.businessLogger.error("Fatal Error-->"+e);
                    handleServiceException(e, methodName);
                }

            }

           // shutdownCustomerMatchinEngine();
            CommonLogger.businessLogger.info("--------- Shutdown ConfigurationWorker : " + this.getName() + " ----------");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error -->"+e, e);
            CommonLogger.businessLogger.error("Fatal Error-->"+e);
            //handleServiceException(e, methodName);
        }

    }

//    private void shutdownCustomerMatchinEngine() {
//        CommonLogger.businessLogger.info("--------- Starting shutdown SMS Groups Customers Matching Engine ------------");
//        ShutdownManager.shutDown();
//        //  CustomerMatchingManager.shutDownCustomerMatchingEngine();
//    }

    private void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof InterruptedException) {
            campaignException = new CommonException("Interrupted exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof SQLException) {
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        MOMErrorsModel errorModel = new MOMErrorsModel();
        //errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setPreceivedSeverity(Integer.valueOf(ResourcesCachingManager.getConfigurationValue(GeneralConstants.SHUTDOWN_ENGINE_MOM_ERROR_SEVERITY)));
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getMessage());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

}
