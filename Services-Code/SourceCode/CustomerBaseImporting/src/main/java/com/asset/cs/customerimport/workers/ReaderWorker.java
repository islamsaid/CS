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
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.managers.CustomerImportingManager;
import com.asset.cs.customerimport.managers.ResourcesCachingManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

/**
 *
 * @author Zain Al-Abedin
 */
public class ReaderWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.workers.ReaderWorker";

    private File dwFile = null;
    private String fileZipPath = null;
    private String fileUnzipPath = null;

    public ReaderWorker(File dwFile) {
        this.dwFile = dwFile;
    }

    public void run() {
        Thread.currentThread().setName(this.getName());
        CommonLogger.businessLogger.info("--------- Starting ReaderWorker : " + this.getName() + " ----------");
        String methodName = "run";
        try {
            long executionTime = System.currentTimeMillis();
            if (CustomerImportingManager.isCatchingError() != true) {
//                CommonLogger.businessLogger.info("will start to work on File: " + dwFile.getName() + " .....................");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ReaderWorker is starting to Work on File")
                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, dwFile.getName()).build());
                executionTime = 0;
                executionTime = System.currentTimeMillis();
                //  CommonLogger.businessLogger.info("Start in processing File: " + dwFile.getName() + " .....................");
                if (dwFile.length() == 0) {
                    CommonLogger.errorLogger.error("Error while processing File: " + dwFile.getName() + " ==> file is empty.");
                    CommonLogger.businessLogger.error("Error while processing File: " + dwFile.getName() + " ==> file is empty.");
                    CustomerImportingManager.setCatchingError(true);
                } else {
                    readDWFile();
                    // CommonLogger.businessLogger.info("Read DWH File in [" + (System.currentTimeMillis() - executionTime) + "] ms");
                }
            }

            CommonLogger.businessLogger.info("--------- Shutdown ReaderWorker : " + this.getName() + " ----------");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Fatal Error -->", e);
            CommonLogger.businessLogger.error("Fatal Error-->" + e);
            CustomerImportingManager.setCatchingError(true);
            setWorkerAlive(false);
            handleServiceException(e, methodName);
        } finally {
            CommonLogger.businessLogger.info("--------- Shutdown ReaderWorker : " + this.getName() + " ----------");
        }

    }

    private void readDWFile() throws CommonException {
        String methodName = "readDWFile";

        String fileLine = null;

        long executionTime;

        FileInputStream fstream = null;
        BufferedReader br = null;
        boolean firstLine = true;
        try {
            fstream = new FileInputStream(dwFile);
            br = new BufferedReader(new InputStreamReader(fstream));

            executionTime = System.currentTimeMillis();

            long sleepTime = ResourcesCachingManager.getLongValue(EngineDefines.READER_THREAD_SLEEP_TIME);
            //   int maxThreshold = ResourcesCachingManager.getIntValue(EngineDefines.READER_THRESHOLD_MAX_SIZE);
            //  int minThreshold = ResourcesCachingManager.getIntValue(EngineDefines.READER_THRESHOLD_MIN_SIZE);
            /*Read File Line By Line*/
            long lineNum = 0;
            while ((fileLine = br.readLine()) != null && !CustomerImportingManager.isCatchingError()) {
                lineNum++;
                if (fileLine.equalsIgnoreCase("")) {
                    CommonLogger.businessLogger.error("Find Empty Line LineNum=[" + lineNum + "] from file=[" + dwFile.getName() + "]");
                    CommonLogger.errorLogger.error("Find Empty Line LineNum=[" + lineNum + "] from file=[" + dwFile.getName() + "]");
                    continue;
                }
                LineModel line = new LineModel();
                line.setFileName(dwFile.getName());
                line.setLineData(fileLine);
                line.setLineNum(lineNum);
                while (!CustomerImportingManager.readersDataQueue.offer(line) && !CustomerImportingManager.isCatchingError()) {
//                    CommonLogger.businessLogger.info("Reader Queue Is Full, Reader Worker will sleep for[" + sleepTime + "]msec");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reader Queue is Full, Reader wotker will Sleep")
                            .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
                    this.sleep(sleepTime);
                }
//                CustomerImportingManager.readersDataQueue.put(fileLine);
//                if (CustomerImportingManager.readersDataQueue.size() > maxThreshold) {
//                    while (CustomerImportingManager.readersDataQueue.size() > minThreshold && !CustomerImportingManager.isCatchingError()) {
//                        CommonLogger.businessLogger.debug("readersDataQueue size " + CustomerImportingManager.readersDataQueue.size() + ", will sleep for [" + sleepTime + "] msec");
//                        this.sleep(sleepTime);
//                    }
//
//                }

            }
            executionTime = System.currentTimeMillis() - executionTime;
//            CommonLogger.businessLogger.info(this.getName() + "-->Reading DWH File in [" + executionTime + "]ms");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Read DWH File")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, executionTime).build());
        } catch (Exception e) {
            throw new CommonException("Reading Error in CLASS:" + CLASS_NAME + " method: " + methodName + e, ErrorCodes.ERROR_IN_READING_DWH_FILE);
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
                if (fstream != null) {
                    fstream.close();
                    fstream = null;
                }
            } catch (Exception e) {
                throw new CommonException("Unkown Error in CLASS:" + CLASS_NAME + " method: " + methodName + e, ErrorCodes.UNKOWN_ERROR);
            }
        }
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

        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setErrorMessage(campaignException.getErrorMsg());
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

}
