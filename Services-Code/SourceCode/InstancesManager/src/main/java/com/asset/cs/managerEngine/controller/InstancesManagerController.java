/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.controller;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.managerEngine.constants.EngineDefines;
import com.asset.cs.managerEngine.managers.Manager;
import com.asset.cs.managerEngine.managers.ResourcesCachingManager;
import com.asset.cs.managerEngine.workers.CustomersStatisticsWorker;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zain Al-Abedin
 */
public class InstancesManagerController {

    private static final String CLASS_NAME = "com.asset.cs.managerEngine.controller.InstancesManagerController";

    public static void main(String[] args) {
        String methodName = "main";

//        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { 
//            public void uncaughtException(Thread t, Throwable e) { 
//                StringWriter sw = new StringWriter();
//                e.printStackTrace(new PrintWriter(sw));
//                String stacktrace = sw.toString();
//                System.out.println(stacktrace);
//            }
//        });     
        System.out.println("----------- Starting Instances Manager Engine -------------");
//        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Instances Manager Engine").build());
        try {
            Initializer.readPropertiesFile();
            Initializer.initializeLoggers();
            Initializer.initializeDataSource();
            loadLookups();
            ResourcesCachingManager.initialize();
            Manager.startInstancesManager();
        } catch (Exception e) {
            System.out.println("Catch Exception---->" + e);
            System.err.println("Catch Exception---->" + e);
            handleServiceException(e, methodName);
        }
    }

    private static void loadLookups() throws CommonException {
        MainService mainServ = new MainService();
        ArrayList<LookupModel> lookupsList = mainServ.loadIdLableLookups(DBStruct.VFE_CS_FILTER_TYPE_LK.TABLE_NAME, DBStruct.VFE_CS_FILTER_TYPE_LK.GROUP_TYPE_ID, DBStruct.VFE_CS_FILTER_TYPE_LK.GROUP_TYPE_LABEL);
        SystemLookups.GROUP_TYPES = mainServ.lookupListToMap(lookupsList);
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);

        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        // errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_MANAGER_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getErrorMsg());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }
}
