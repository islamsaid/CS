/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.service;

import com.asset.alarmengine.common.dao.ServiceErrorsDao;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.models.HServiceErrorModel;
import com.asset.alarmengine.common.utilities.EngineUtils;
import java.sql.Connection;
import java.util.Vector;

/**
 *
 * @author Mostafa Kashif
 */
public class ServiceErrorsService {
    
    String CLASS_NAME="ServiceErrorsService";
       public void handleServiceError(HServiceErrorModel errorModel, EngineException engineException, String serviceErrorTblName, String idColumnName, String errorTypeColumnName, String errorSeverityColumnName,
            String errorParamsColumnName, String errorMsgColumnName, String insertionTimeColumnName, String idSeqName,Connection con) throws EngineException {
             String methodName = "handleServiceError";

        try {

            if (engineException != null) {
                errorModel.setErrorCode(String.valueOf(engineException.getErrorCode()));
                errorModel.setErrorMessage(EngineUtils.getStackTrace(engineException));
            }
          //  HServiceErrorDAO.insertHServiceError(conn, errorModel);
            ServiceErrorsDao.insertHServiceError(con, errorModel, serviceErrorTblName, idColumnName, errorTypeColumnName, errorSeverityColumnName, errorParamsColumnName, errorMsgColumnName, insertionTimeColumnName, idSeqName);
        } catch (Exception ex) {
            if (!(ex instanceof EngineException)) {
                new EngineException(ex, EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
            }
        }
    }
       
        public  Vector<HServiceErrorModel> getServiceErrorsByDate(Connection connection, String date, String severitiesCommaSeparated, String dateFormat,String serviceErrorTblName, String idColumnName, String errorTypeColumnName, String errorSeverityColumnName,
            String errorParamsColumnName, String errorMsgColumnName, String insertionTimeColumnName, String idSeqName,
            String lookupErrorTblName,String lookupErrorLabelColumnName,String lookupErrorIdColumnName) throws EngineException 
            {
              return  ServiceErrorsDao.getServiceErrorsByDate(connection, date, severitiesCommaSeparated, dateFormat, serviceErrorTblName, idColumnName, errorTypeColumnName, errorSeverityColumnName, errorParamsColumnName, errorMsgColumnName, insertionTimeColumnName, idSeqName, lookupErrorTblName, lookupErrorLabelColumnName, lookupErrorIdColumnName);
            }
}
