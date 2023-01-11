/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.utilities;

import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.exception.EngineException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 *
 * @author Mostafa Kashif
 */
public class EngineUtils {

    public static String getErrorMessage(String errorCode) {
        String errorMessage = "";
        try {
            errorMessage = EngineDefines.errorMessagesBundle.getString(errorCode);
        } catch (Exception e) {
            errorMessage = "Unknown Error!! (key not found in messages resource)";
        }
        return errorMessage;
    }

    public static HashMap<String, String> convertResourceBundleToMap(ResourceBundle resource) {
        HashMap<String, String> map = new HashMap<String, String>();
        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, resource.getString(key));
        }
        return map;
    }
    
    public static String getStackTrace(EngineException e) {
        String errorMessage = null;
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        errorMessage = stackTrace.toString();

        if (errorMessage != null && errorMessage.length() > 4000) {
            errorMessage = errorMessage.substring(0, 4000);
        }

        return errorMessage;
    }
    
     public static String getStringValueFromMap(String key,HashMap map) {
        if (map != null) {
            return map.get(key).toString();
        } else {
            return null;
        }
    }
}
