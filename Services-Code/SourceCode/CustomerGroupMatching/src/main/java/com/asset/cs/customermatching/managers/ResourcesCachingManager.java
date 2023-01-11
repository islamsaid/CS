/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.managers;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.MatchingInstanceModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.customermatching.constants.EngineDefines;
import com.asset.cs.customermatching.workers.ConfigurationWorker;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zain Al-Abedin
 */
public class ResourcesCachingManager {
    
    private static final String CLASS_NAME = "com.asset.cs.customermatching.managers.ResourcesCachingManager";
    
    private static HashMap propertiesMap = new HashMap();
    private static HashMap congigurationsMap = new HashMap();
    
    private static MainService mainServ = new MainService();
    
    private static int importRunId;
    
    public static String configurationInstanceId;
    
    public static AtomicBoolean shutdownEngineFlag = new AtomicBoolean(false);
    
    public static void initialize() throws CommonException {
        
        CommonLogger.businessLogger.info("intialize ResourcesCachingManager");
        try {
            Initializer.readPropertiesFile(propertiesMap);
            //Initializer.initializeLoggers();
            //Initializer.initializeDataSource();
            configurationInstanceId = getStringValue(EngineDefines.INSTANCE_ID);
            loadResourcesAndConfigurations();
            
            resetEngineShutDownFlag();
            
            ConfigurationWorker configurationWorker = new ConfigurationWorker();
            configurationWorker.setName("ConfigurationWorker");
            configurationWorker.start();
            
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Caught Exception in CLASS:" + CLASS_NAME + " method: initialize "+ ex, ex);
            CommonLogger.businessLogger.error("Fatal Error-->"+ ex);
            throw new CommonException("Caught Exception in CLASS:" + CLASS_NAME + " method: initialize " + ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        
    }
    
    public static void loadResourcesAndConfigurations() throws CommonException {
        String methodName = "loadResourcesAndConfigurations";
        try {
            CommonLogger.businessLogger.info("Start Loading resources and configurations");
            loadDBConfigurations();
            MatchingInstanceModel model = mainServ.getMatchingInstance(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE, configurationInstanceId);
            CustomerMatchingManager.setInstance(model);
            setImportRunId(Integer.parseInt(mainServ.getSystemPropertyByKey(GeneralConstants.KEY_ACTIVE_DWH_PROFILE_PARTITION, GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE)));
            CommonLogger.businessLogger.info("Resources and configurations loaded Successfully");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Caught Exception in CLASS:" + CLASS_NAME + " method: " + methodName + e, e);
            CommonLogger.businessLogger.error("Fatal Error-->"+ e);
            throw new CommonException("Caught Exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
    }
    
    private static void resetEngineShutDownFlag() throws CommonException {
        SystemPropertiesModel model = new SystemPropertiesModel();
        model.setItemKey(GeneralConstants.KEY_ENGINE_SHUTDOWN_FLAG);
        model.setItemValue(GeneralConstants.SHUTDOWN_FLAG_FALSE);
        model.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE);
        mainServ.updateSystemProperty(model);
        
    }
    
//    public static void checkEngineShutDownFlag() throws CommonException {
//        String shutDownEngineFlag = getConfigurationValue(GeneralConstants.KEY_ENGINE_SHUTDOWN_FLAG);
//        if (shutDownEngineFlag.equalsIgnoreCase(GeneralConstants.SHUTDOWN_FLAG_TRUE)) {
//            shutdownEngineFlag = true;
//        }
//        
//    }
    
    public static void loadDBConfigurations() throws CommonException {
        CommonLogger.businessLogger.info("Load DataBase Configuration For Engine");
        HashMap congigurations = new HashMap();
        congigurations = mainServ.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_CUSTOMER_GROUPS_MATCHING_ENGINE);
        synchronized (congigurationsMap) {
            congigurationsMap.clear();
            congigurationsMap.putAll(congigurations);
        }
    }
    
    public static String getStringValue(String key) {
        if (propertiesMap != null) {
            return propertiesMap.get(key).toString();
        } else {
            return null;
        }
    }
    
    public static int getIntValue(String key) {
        if (propertiesMap != null) {
            return Integer.valueOf(propertiesMap.get(key).toString()).intValue();
        } else {
            return 0;
        }
    }
    
    public static long getLongValue(String key) {
        if (propertiesMap != null) {
            return Long.valueOf(propertiesMap.get(key).toString()).longValue();
        } else {
            return 0;
        }
    }
    
    public static String getConfigurationValue(String key) {
        if (congigurationsMap != null) {
            if (congigurationsMap.get(key) != null) {
                return congigurationsMap.get(key).toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public static int getImportRunId() {
        return importRunId;
    }
    
    public static void setImportRunId(int aImportRunId) {
        importRunId = aImportRunId;
    }
}
