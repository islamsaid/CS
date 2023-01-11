/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.controller;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customermatching.managers.CustomerMatchingManager;
import com.asset.cs.customermatching.managers.ResourcesCachingManager;
import com.asset.cs.customermatching.managers.ShutdownManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zain Al-Abedin
 */
public class CustomerMatchingController {

    private static final String CLASS_NAME = "com.asset.cs.customermatching.controller.CustomerMatchingController";

    public static void main(String[] args) {
        String methodName = "main";

        System.out.println("----------- Starting Groups Customer Matching Engine -------------");
        try {
//            if (args[0].equalsIgnoreCase("startup")) {
                Initializer.readPropertiesFile();
                Initializer.initializeLoggers();
                Initializer.initializeDataSource();
                loadLookups();
                ResourcesCachingManager.initialize();
                
                CustomerMatchingManager.startCustomerMatchingManager();
//            } else if (args[0].equalsIgnoreCase("shutdown")) {
//                ShutdownManager.shutDown();
//
//            }
        } catch (Exception e) {
            System.out.println("Catch Exception---->" + e);
            System.err.println("Catch Exception---->" + e);
            handleServiceException(e, methodName);
        }
    }

    private static void loadLookups() throws CommonException {
        MainService mainServ = new MainService();
        ArrayList<LookupModel> lookupsList;
        try {
            lookupsList = mainServ.loadIdLableLookups(DBStruct.VFE_CS_FILTER_TYPE_LK.TABLE_NAME, DBStruct.VFE_CS_FILTER_TYPE_LK.GROUP_TYPE_ID, DBStruct.VFE_CS_FILTER_TYPE_LK.GROUP_TYPE_LABEL);
            SystemLookups.GROUP_TYPES = mainServ.lookupListToMap(lookupsList);
        } catch (CommonException ex) {          
            CommonLogger.errorLogger.error("Failed to Load LookUps",ex);
            throw ex;
        }
        
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getErrorMsg());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }
}
