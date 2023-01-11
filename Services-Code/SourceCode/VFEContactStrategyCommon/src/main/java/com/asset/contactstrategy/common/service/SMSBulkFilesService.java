package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.CommonDAO;
import com.asset.contactstrategy.common.dao.SMSBulkFilesDAO;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.models.SMSBulkFileModel;
import com.asset.contactstrategy.interfaces.models.InputModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author aya.moawed 2595
 */
public class SMSBulkFilesService {

    public void insertSMSFile(Connection connection, SMSBulkFileModel model) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "Starting Getting [insertSMSFile]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSFile Started").build());

            SMSBulkFilesDAO dao = new SMSBulkFilesDAO();
            model.setId(CommonDAO.getNextId(connection, DBStruct.VFE_CS_SMS_BULK_FILES_H.ID_SEQ));
            dao.insertSMSFile(connection, model);
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "End Getting [insertSMSFile]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSFile Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public void updateSMSBulkFileStatus(Connection connection, SMSBulkFileModel model) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "Starting Getting [updateSMSBulkFileStatus]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSMSBulkFileStatus Started").build());

            SMSBulkFilesDAO dao = new SMSBulkFilesDAO();
            dao.updateSMSBulkFileStatus(connection, model);
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "End Getting [updateSMSBulkFileStatus]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSMSBulkFileStatus Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public void insertSMSBulkRecords(Connection connection, ArrayList<InputModel> model, String fileName) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "Starting Getting [insertSMSBulkRecords]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSBulkRecords Started").build());

            SMSBulkFilesDAO dao = new SMSBulkFilesDAO();
            dao.insertSMSBulkRecords(connection, model, fileName);
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "End Getting [insertSMSBulkRecords]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSBulkRecords Ended").build());

        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        }
    }

    public RequestPreparator getSMSBulkRecords(Connection connection, int threadNumber, int numberOfThreads, int batchSize, ArrayList<Long> resultIds) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "Starting Getting [getSMSBulkRecords]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSBulkRecords Started").build());

            SMSBulkFilesDAO dao = new SMSBulkFilesDAO();
            RequestPreparator smsBulkRecords = dao.getSMSBulkRecords(connection, threadNumber, numberOfThreads, batchSize, resultIds);
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "End Getting [getSMSBulkRecords]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSBulkRecords Ended").build());

            return smsBulkRecords;
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        }
    }

    public void insertSMSBulkRecordsArchive(Connection connection, HashMap<String, ArrayList> interfaceResult, ArrayList<Long> resultIds) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "Starting Getting [insertSMSBulkRecordsArchive]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSBulkRecordsArchive Started").build());

            SMSBulkFilesDAO dao = new SMSBulkFilesDAO();
            dao.insertSMSBulkRecordsArchive(connection, interfaceResult, resultIds);
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "End Getting [insertSMSBulkRecordsArchive]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSBulkRecordsArchive Ended").build());

        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecordsArchive]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        }
    }
    
    public void insertFailedSMSBulkRecordsArchive(Connection connection, ArrayList<Long> resultIds) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "Starting Getting [insertSMSBulkRecordsArchive]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSBulkRecordsArchive Started").build());

            SMSBulkFilesDAO dao = new SMSBulkFilesDAO();
            dao.insertFailedSMSBulkRecordsArchive(connection, resultIds);
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "End Getting [insertSMSBulkRecordsArchive]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertSMSBulkRecordsArchive Ended").build());

        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [insertSMSBulkRecordsArchive]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        }
    }

    public void deleteSMSBulkRecords(Connection connection, ArrayList<Long> resultIds) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "Starting Getting [deleteSMSBulkRecords]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSBulkRecords Started").build());

            SMSBulkFilesDAO dao = new SMSBulkFilesDAO();
            dao.deleteSMSBulkRecords(connection, resultIds);
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "End Getting [deleteSMSBulkRecords]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSBulkRecords Ended").build());

        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [deleteSMSBulkRecords]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        }
    }

    public int getBulkRecordsCount(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "Starting Getting [getBulkRecordsCount]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getBulkRecordsCount Started").build());

            SMSBulkFilesDAO dao = new SMSBulkFilesDAO();
            int bulkRecordsCount = dao.getBulkRecordsCount(connection);
//            CommonLogger.businessLogger.debug(SMSBulkFilesService.class.getName() + " || " + "End Getting [getBulkRecordsCount]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getBulkRecordsCount Ended").build());

            return bulkRecordsCount;
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getBulkRecordsCount]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        }
    }
}
