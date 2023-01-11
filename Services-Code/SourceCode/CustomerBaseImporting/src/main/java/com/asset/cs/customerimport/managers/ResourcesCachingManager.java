/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.managers;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.workers.ConfigurationWorker;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zain Al-Abedin
 */
public class ResourcesCachingManager {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.managers.ResourcesCachingManager";

    private static Vector<DWHElementModel> dwhElementsList = new Vector<DWHElementModel>();
    public static HashMap<String, LookupModel> governmentMap = new HashMap<String, LookupModel>();
    public static HashMap<String, LookupModel> customerTypesMap = new HashMap<String, LookupModel>();

    private static HashMap propertiesMap = new HashMap();
    private static HashMap congigurationsMap = new HashMap();

    private static MainService mainServ = new MainService();

    public static int runId;
    
    public static int retryGetConnection;
    
    public static AtomicBoolean shutdownEngineFlag = new AtomicBoolean(false);

    public static ConfigurationWorker configurationWorker;
    
    public static void initialize() throws CommonException {

        CommonLogger.businessLogger.info("intialize ResourcesCachingManager");
        try {
            Initializer.readPropertiesFile(propertiesMap);
            loadResourcesAndConfigurations();
            //resetEngineShutDownFlag();           
            configurationWorker = new ConfigurationWorker();
            configurationWorker.setName("ConfigurationWorker");
            configurationWorker.start();
            
            
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Caught Exception in CLASS:" + CLASS_NAME + " method: initialize " , ex);
             CommonLogger.businessLogger.error("Fatal Error-->"+ex);
            throw new CommonException("Caught Exception in CLASS:" + CLASS_NAME + " method: initialize " + ex.getMessage(),ErrorCodes.UNKOWN_ERROR);
           
        }

    }

    public static void loadResourcesAndConfigurations() throws CommonException {
        String methodName = "loadResourcesAndConfigurations";
        try {
            // Log
            CommonLogger.businessLogger.info("Start Loading resources and configurations");
            // Load DWH Elements
            loadDWHElementsMap();
            
            loadGovernmentMap();
            
            loadCustomerTypestMap();
            // Load DB Configurations
            loadDBConfigurations();
            runId = Integer.parseInt(getConfigurationValue(GeneralConstants.KEY_ACTIVE_DWH_PROFILE_PARTITION)) + 1;            
            retryGetConnection = Integer.parseInt(getConfigurationValue(EngineDefines.KEY_GET_CONNECTION_RETRY_NUM));
            // Log
            CommonLogger.businessLogger.info("Resources and configurations loaded Successfully");
        } catch (Exception e) {
             CommonLogger.errorLogger.error("Caught Exception in CLASS:" + CLASS_NAME + " method: "+methodName , e);
             CommonLogger.businessLogger.error("FATAL ERROR--->"+e);
            throw new CommonException("Caught Exception in CLASS:" + CLASS_NAME + " method: "+methodName + e.getMessage(),ErrorCodes.UNKOWN_ERROR);
           
        }
    }

    
//     public static void checkEngineShutDownFlag()throws CommonException
//    {
//        String shutDownEngineFlag = getConfigurationValue(EngineDefines.KEY_ENGINE_SHUTDOWN_FLAG);
//        if(shutDownEngineFlag.equalsIgnoreCase(GeneralConstants.SHUTDOWN_FLAG_TRUE))
//             shutdownEngineFlag = true;
//        
//    }
    
//     private static void resetEngineShutDownFlag() throws CommonException
//    {   
//        SystemPropertiesModel model = new SystemPropertiesModel();
//        model.setItemKey(EngineDefines.KEY_ENGINE_SHUTDOWN_FLAG);
//        model.setItemValue(GeneralConstants.SHUTDOWN_FLAG_FALSE);
//        model.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
//        mainServ.updateSystemProperty(model);
//        
//    }
//     
    private static void loadDWHElementsMap() throws CommonException {
        Vector<DWHElementModel> elementsList = new Vector<DWHElementModel>();
        elementsList = mainServ.getDWHElementsList();
        synchronized (getDwhElementsList()) {
            getDwhElementsList().removeAllElements();
            getDwhElementsList().addAll(elementsList);
            
        }
    }

    private static void loadGovernmentMap() throws CommonException {
        ArrayList<LookupModel> governments = new ArrayList<LookupModel>();
        governments = mainServ.loadIdLableLookups(DBStruct.LK_GOVERNMENT.TBL_NAME, DBStruct.LK_GOVERNMENT.GOVERNMENT_ID, DBStruct.LK_GOVERNMENT.GOVERNMENT_NAME);
        synchronized (governmentMap) {
            governmentMap.clear();
            for (LookupModel model : governments) {
                governmentMap.put(model.getLable(), model);
            }
        }
    }

    private static void loadCustomerTypestMap() throws CommonException {
        ArrayList<LookupModel> customerTypes = new ArrayList<LookupModel>();
        customerTypes = mainServ.loadIdLableLookups(DBStruct.LK_CUSTOMER_TYPE.TBL_NAME, DBStruct.LK_CUSTOMER_TYPE.CUSTOMER_TYPE_ID, DBStruct.LK_CUSTOMER_TYPE.CUSTOMER_TYPE_LABEL);
        synchronized (customerTypesMap) {
            customerTypesMap.clear();
            for (LookupModel model : customerTypes) {
                customerTypesMap.put(model.getLable(), model);
            }
        }
    }

    public static void loadDBConfigurations() throws CommonException {
        CommonLogger.businessLogger.info("Load DataBase Configuration For Engine");
        HashMap congigurations = new HashMap();
        congigurations = mainServ.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        synchronized (congigurationsMap) {
            congigurationsMap.clear();
            congigurationsMap.putAll(congigurations);
        }
    }

    public static String getStringValue(String key) 
    {
        if (propertiesMap != null)
            return propertiesMap.get(key).toString();
        else 
            return null;
    }
    public static int getIntValue(String key) 
    {
        if (propertiesMap != null)
            return Integer.valueOf(propertiesMap.get(key).toString()).intValue();
        else 
            return 0;
    }
    public static long getLongValue(String key) 
    {
        if (propertiesMap != null)
            return Long.valueOf(propertiesMap.get(key).toString()).longValue();
        
        else 
            return 0;
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

    public static Vector<DWHElementModel> getDwhElementsList() {
        return dwhElementsList;
    }

   

    
    
}
