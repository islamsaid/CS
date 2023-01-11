/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.service;

import com.asset.alarmengine.common.dao.SystemPropertiesDao;
import com.asset.alarmengine.common.exception.EngineException;
import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * @author Mostafa Kashif
 */
public class SystemPropertiesService {
    

    //Esmail.Anbar || Fixing System Property Reading "ITEM_KEY" instead of "ITEM_ID" | 3/10/2017
     public  String getSystemProperty(String systemPropertiesTblName,String outputValueColumnName,String propKeyIDColumnName, String propKeyID, Connection con) throws EngineException {
     return SystemPropertiesDao.getSystemProperty(systemPropertiesTblName, outputValueColumnName, propKeyIDColumnName, propKeyID, con);
     }
     //Esmail.Anbar || Fixing System Property Reading "ITEM_KEY" instead of "ITEM_ID" | 3/10/2017
     public void updateSystemProperty(String systemPropertiesTblName, String ValueColumnName, String propKeyIDColumnName, String value, String propKeyID, Connection con) throws EngineException {
        SystemPropertiesDao.updateSystemProperty(systemPropertiesTblName, ValueColumnName, propKeyIDColumnName, value, propKeyID, con);
     }
     //Esmail.Anbar || Fixing System Property Reading "ITEM_KEY" instead of "ITEM_ID" | 3/10/2017
     public HashMap<String, String> loadAllProperties(String systemPropertiesTblName, String ValueColumnName, String propKeyIDColumnName,String groupIdColumnName,String groupId, Connection con) throws EngineException {
        return SystemPropertiesDao.loadAllProperties(systemPropertiesTblName, ValueColumnName, propKeyIDColumnName,groupIdColumnName,groupId, con);
     }
}
