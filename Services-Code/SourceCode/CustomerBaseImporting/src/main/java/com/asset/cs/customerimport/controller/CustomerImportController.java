/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.controller;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customerimport.managers.CustomerImportingManager;
import com.asset.cs.customerimport.managers.ResourcesCachingManager;
import com.asset.cs.customerimport.managers.ShutdownManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Zain Al-Abedin
 */
public class CustomerImportController {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.controller.CustomerImportController";

    public static void main(String[] args) {
        String methodName = "main";

        System.out.println("----------- Starting DWH Extractor Engine -------------");
        try {
            Defines.runningProjectId=GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE;
            Initializer.readPropertiesFile();
            Initializer.initializeLoggers();
            Initializer.initializeDataSource();
            ResourcesCachingManager.initialize();
            //TESTING Connection Timeout              
//            for (int i = 0; i < 100; i++) {
//                Connection x = DataSourceManger.getConnection(5);
//            }
            CustomerImportingManager.startExtractorManager();
        } catch (Exception e) {
            System.out.println("Catch Exception---->" + e);
            System.err.println("Catch Exception---->" + e);
            handleServiceException(e, methodName);
        }
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
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
