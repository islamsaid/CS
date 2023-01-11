/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.service.MainService;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Zain Al-Abedin
 */
public class LookupFacade {

    public ArrayList<LookupModel> loadIdLableLookups(String tableName, String idColName, String lableColName) throws CommonException {
        CommonLogger.businessLogger.debug("Starting");
        ArrayList<LookupModel> lookups = null;
        long startime = System.currentTimeMillis();
        try {
            MainService mainService = new MainService();
            lookups = mainService.loadIdLableLookups(tableName, idColName, lableColName);
        } catch (CommonException ex) {
            throw new CommonException("Failed to retrieve lookups for table " + tableName, ErrorCodes.UNKOWN_ERROR);
        }
        return lookups;
    }

    public HashMap<Integer, LookupModel> loadLookupsMap(String tableName, String idColName, String lableColName) throws CommonException {
        CommonLogger.businessLogger.debug("Starting");
        HashMap<Integer, LookupModel> lookups = null;
        try {
            MainService mainService = new MainService();
            ArrayList<LookupModel> lookupList = new ArrayList<LookupModel>();
            lookupList = mainService.loadIdLableLookups(tableName, idColName, lableColName);
            lookups=mainService.lookupListToMap(lookupList);
            //lookups = this.lookupListToMap(lookupList);
        } catch (CommonException e) {
            throw new CommonException("Failed to retrieve lookups for table " + tableName, ErrorCodes.UNKOWN_ERROR);
        }
        return lookups;
    }
    
    public ArrayList<OriginatorTypeModel> loadOriginatorLookup(String tableName, String idcolName, String lableColName, String allowedColName) throws CommonException {
        CommonLogger.businessLogger.debug("Starting");
         ArrayList<OriginatorTypeModel> lookupList ;
        try {
            MainService mainService = new MainService();
            lookupList = new ArrayList<OriginatorTypeModel>();
            lookupList = mainService.loadOriginatorLookup(tableName,idcolName,lableColName,allowedColName);
        } catch (CommonException e) {
            throw new CommonException("Failed to retrieve lookups for table " + tableName, ErrorCodes.UNKOWN_ERROR);
        }
        return lookupList;
    }

    
    public HashMap<Integer, LookupModel> lookupListToMap(ArrayList<LookupModel> list) {
        HashMap<Integer, LookupModel> map = new HashMap<Integer, LookupModel>();
        if (list != null) {
            for (LookupModel lk : list) {
                map.put(lk.getId(), lk);
            }
        }
        return map;
    }

    public ArrayList<LookupModel> getDisplayTypeOperators(int displayTypeId) throws CommonException {
        final String METHOD_NAME = "getDisplayTypeOperators";
        try {
            MainService mainService = new MainService();
            return mainService.getDisplayTypeOperators(displayTypeId);
        } catch (CommonException e) {
            throw new CommonException(LookupFacade.class.getName() + " Failed to retrieve lookups " + METHOD_NAME, ErrorCodes.UNKOWN_ERROR);
        }
    }

}
