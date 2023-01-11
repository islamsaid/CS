/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

//import com.asset.contactstrategy.common.dao.DWHElementDAO;
import com.asset.contactstrategy.common.dao.LookupDao;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.models.ReportsViewModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Zain Al-Abedin
 */
public class LookupService {

    public ArrayList<LookupModel> loadIdLableLookups(String tableName, String idcolName, String lableColName, Connection connection) throws CommonException {
        try { 
            LookupDao lookUpsDAO = new LookupDao();
            ArrayList<LookupModel> lookupsList = lookUpsDAO.loadIdLableLookups(tableName, idcolName, lableColName, connection);
            return lookupsList;
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<OriginatorTypeModel> loadOriginatorLookup(String tableName, String idcolName, String lableColName, String allowedColName, Connection connection) throws CommonException {
        try { 
            LookupDao lookUpsDAO = new LookupDao();
            ArrayList<OriginatorTypeModel> lookupsList = lookUpsDAO.loadOriginatorLookup(tableName, idcolName, lableColName, allowedColName , connection);
            return lookupsList;
        } catch (CommonException e) {
            throw e;
        }
    }
    
    public ArrayList<LookupModel> getDisplayTypeOperators(int displayTypeId, Connection conn) throws CommonException {
        try {
            LookupDao lookUpsDAO = new LookupDao();
            return lookUpsDAO.getDisplayTypeOperators(displayTypeId, conn);
        } catch (CommonException e) {
            throw e;
        }
    }
    
    public ArrayList<ReportsViewModel> getReportsList(Connection conn) throws CommonException
    {
        try 
        {

            LookupDao lookUpDao = new LookupDao();
            ArrayList<ReportsViewModel> reportsList = lookUpDao.getReportsList(conn);
            return reportsList;
        } 
        catch (CommonException e) 
        {
            throw e;
        }
    }
    
    public HashMap<Integer, OriginatorTypeModel> getOriginatorsList(Connection conn) throws CommonException
    {
        try 
        {

            LookupDao lookUpDao = new LookupDao();
            HashMap<Integer, OriginatorTypeModel> originators = lookUpDao.getOriginatorsList(conn);
            return originators;
        } 
        catch (CommonException e) 
        {
            throw e;
        }
    }
}
