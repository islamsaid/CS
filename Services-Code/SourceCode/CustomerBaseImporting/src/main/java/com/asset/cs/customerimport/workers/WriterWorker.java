/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.workers;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LineModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.managers.CustomerImportingManager;
import com.asset.cs.customerimport.managers.ResourcesCachingManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Zain Al-Abedin
 */
public class WriterWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.workers.WriterWorker";
    private BlockingQueue<LineModel> dataQueue = null;
    private int runId;
    private MainService mainServ = new MainService();
    Vector<LineModel> dwListbatch = null;
    LineModel dwRow = null;

    public WriterWorker(BlockingQueue queue, int runId) {

        this.dataQueue = queue;
        this.runId = runId;

    }

    public void run() {
        Thread.currentThread().setName(this.getName());

        CommonLogger.businessLogger.info("--------- Starting WriterWorker : " + this.getName() + " ----------");
        String methodName = "run";
        try {
            long sleepTime = ResourcesCachingManager.getLongValue(EngineDefines.WRITER_THREAD_SLEEP_TIME);
            int batchMinSize = ResourcesCachingManager.getIntValue(EngineDefines.WRITER_BATCH_MIN_SIZE);
            int batchMaxSize = ResourcesCachingManager.getIntValue(EngineDefines.WRITER_BATCH_MAX_SIZE);

            dwListbatch = new Vector<>();
            dwListbatch.ensureCapacity(batchMaxSize);
            while ((!CustomerImportingManager.isWriterWorkersShutdownFlag() || !dataQueue.isEmpty())
                    && CustomerImportingManager.isCatchingError() != true) /*Ensure that shut down flag is set and dwWriterDataList is empty*/ {
                dwRow = dataQueue.poll(sleepTime, TimeUnit.MILLISECONDS);
                if (dwRow == null) {
                    if (dwListbatch.size() >= batchMinSize) {
//                        CommonLogger.businessLogger.info(this.getName() + "-->Insert Batch with Size: " + dwListbatch.size() + "/" + dataQueue.size());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Insert Batch Stats")
                                .put(GeneralConstants.StructuredLogKeys.SIZE, dwListbatch.size())
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, dataQueue.size()).build());
                        writeDWBatch(CustomerImportingManager.SQLStatment, dwListbatch);
                        dwListbatch.clear();
                    }
                } else {
                    dwListbatch.add(dwRow);
                    if (dwListbatch.size() >= batchMaxSize) {
//                        CommonLogger.businessLogger.info(this.getName() + "-->Insert Batch with Size: " + dwListbatch.size() + "/" + dataQueue.size());
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Insert Batch Stats")
                                .put(GeneralConstants.StructuredLogKeys.SIZE, dwListbatch.size())
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, dataQueue.size()).build());
                        writeDWBatch(CustomerImportingManager.SQLStatment, dwListbatch);
                        dwListbatch.clear();
                    }
                }

            }
            if (!dwListbatch.isEmpty() && !CustomerImportingManager.isCatchingError()) {
//                CommonLogger.businessLogger.info(this.getName() + "-->Insert Last Batch with Size: " + dwListbatch.size() + "/" + dataQueue.size());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Insert Batch Stats")
                        .put(GeneralConstants.StructuredLogKeys.SIZE, dwListbatch.size())
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, dataQueue.size()).build());
                writeDWBatch(CustomerImportingManager.SQLStatment, dwListbatch);
                dwListbatch.clear();
            }

            CommonLogger.businessLogger.info("--------- Shutdown WriterWorker : " + this.getName() + " ----------");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error -->", e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            CustomerImportingManager.setCatchingError(true);
            handleServiceException(e, methodName);
        } finally {
            CommonLogger.businessLogger.info("--------- Shutdown WriterWorker : " + this.getName() + " ----------");
        }

    }

    private void writeDWBatch(String SQLStatment, Vector<LineModel> dwBatch) throws CommonException {

        mainServ.insertDWHProfileBatch(SQLStatment + " /* " + this.getName() + " */ ", dwBatch, ResourcesCachingManager.retryGetConnection);
    }

    private void handleServiceException(Exception e, String methodName) {

        CommonException campaignException = null;
        MOMErrorsModel errorModel = new MOMErrorsModel();
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_FAILED_EXTRACT_DWH_FILES_MOM_SEVERITY)));
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(GeneralConstants.DATABASE_MOM_ERROR_SEVERITY)));
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            errorModel.setPreceivedSeverity(Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_FAILED_EXTRACT_DWH_FILES_MOM_SEVERITY)));
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        //  
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getErrorMsg());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

}
