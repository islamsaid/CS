/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.controller;

import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.alarmengine.common.utilities.EngineUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Mostafa Kashif
 */
public class Initializer {

    public static void initializePropertiesFiles() throws EngineException {

        long time = System.currentTimeMillis();
        try {
            EngineDefines.errorMessagesBundle = ResourceBundle.getBundle(EngineDefines.ERROR_MESSAGES_FILE_NAME);
        } catch (Exception ex) {
            System.err.println("Exception in errormessagebundle---->" + ex);
            System.out.println("Exception in errormessagebundle---->" + ex);
            throw new EngineException(ex, EngineErrorCodes.ERROR_MESSAGES_PROPERTIES_FILE_FAILED);
        }
     try{
            EngineDefines.propertiesMap = EngineUtils.convertResourceBundleToMap(ResourceBundle.getBundle(EngineDefines.ENGINE_PROPERTIES_FILE_NAME));
           // EngineDefines.propertiesMap=(HashMap) System.getenv();
//                for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
//            EngineDefines.propertiesMap.put(entry.getKey(), entry.getValue());
//        }

        } catch (Exception ex) {
            System.err.println("Exception in propertiesFileBundle---->" + ex);
            System.out.println("Exception in propertiesFileBundle---->" + ex);
            throw new EngineException(ex, EngineErrorCodes.ERROR_MESSAGES_PROPERTIES_FILE_FAILED);
        }
    }

    public static void initializeLoggers() {
        //ResourceBundle bundle=ResourceBundle.getBundle(SMPPDefines.PROPERTIES_FILE_NAME);
        String log4jPath = EngineUtils.getStringValueFromMap(EngineDefines.LOG4J_PATH, EngineDefines.propertiesMap);
        PropertyConfigurator.configure(log4jPath);
        EngineLogger.initLoggers();


    }
}
