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
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.common.utils.ZipUtil;
import com.asset.cs.customerimport.constants.EngineDefines;
import com.asset.cs.customerimport.managers.CustomerImportingManager;
import com.asset.cs.customerimport.managers.ResourcesCachingManager;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.ThreadContext;
import org.apache.poi.util.IOUtils;

/**
 *
 * @author Zain Al-Abedin
 */
public class FilesWorker extends CCWorker {

    private static final String CLASS_NAME = "com.asset.cs.customerimport.workers.FilesWorker";

    private File dwFile = null;
    private File dwFolder = null;
    private String folderZipPath = null;
    private String folderUnzipPath = null;
    private ArrayList<String> zipFileNames = null;

    private long executionTime;
    private int processID;
    private DateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SS a");

    private MainService mainServ = new MainService();

    public void run() {
        CommonLogger.businessLogger.info("--------- Starting FilesWorker : " + this.getName() + " ----------");
        String methodName = "run";
        Connection connection = null;
        String failureHistoricalPath = null;
        String successHistoricalPath = null;
        long sleepTimeInMs = 300000; //default 5 Minute

        try {
//            long sleepTimeInHrs = ResourcesCachingManager.getLongValue(EngineDefines.FILES_WORKER_THREAD_SLEEP_TIME);
//            sleepTimeInMs = sleepTimeInHrs * 60 /*(sec)*/ * 1000 /*(ms)*/;

            //Zain Change
            long sleepTimeInMinutes = ResourcesCachingManager.getLongValue(EngineDefines.FILES_WORKER_THREAD_SLEEP_TIME);
            sleepTimeInMs = sleepTimeInMinutes * 60 /*(sec)*/ * 1000 /*(ms)*/;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error Starting Thread--->", ex);
            CommonLogger.businessLogger.error("Fatal Error  Starting Thread-->" + ex);
        }
        while (!isWorkerShutDownFlag()) {
            try {
                setWorkerAlive(true);
                ResourcesCachingManager.loadResourcesAndConfigurations();
                folderZipPath = ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FOLDER_PATH);
                folderUnzipPath = ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_UNZIP_FOLDER_PATH);
                successHistoricalPath = ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_SUCCESS_HISTORY_PATH);
                failureHistoricalPath = ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_FAILURE_HISTORY_PATH);
                String[] zipNames = ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FILE_NAMES).split(EngineDefines.DW_FILE_NAME_SPLITTER);
                //For Testing
//                successHistoricalPath = "D:/DWH/History/success";
//                failureHistoricalPath = "D:/DWH/History/failed";
//                folderZipPath = "D:/DWH/Zipped";
//                folderUnzipPath = "D:/DWH/Unzipped";
//                String[] zipNames = {"DWH_Records_1.gzip"};
                if (ResourcesCachingManager.getConfigurationValue(EngineDefines.REMOTE_FILE_DIRECTORY).equalsIgnoreCase("true")) {
                    getRemoteFiles(ResourcesCachingManager.getConfigurationValue(EngineDefines.TRANSFER_PROTOCOL));
                }
                zipFileNames = new ArrayList<>();
                for (String zipFileName : zipNames) {
                    if (zipFileName.equals("")) {
                        continue;
                    }
                    zipFileName = zipFileName.trim();
                    zipFileNames.add(zipFileName);
                }
                //   zipFileNames = new ArrayList<String>(Arrays.asList(zipNames));
                CustomerImportingManager.setCatchingError(false);
                if (checkNewFolder()) {
//                    CommonLogger.businessLogger.info("==============================================================================================");
//                    CommonLogger.businessLogger.info("================================== Start to work on on folder: " + dwFolder.getName() + " For RUN_ID=[ " + (ResourcesCachingManager.runId) + "]==================================");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start to Work on Folder")
                            .put(GeneralConstants.StructuredLogKeys.FOLDER, dwFolder.getName())
                            .put(GeneralConstants.StructuredLogKeys.RUN_ID, (ResourcesCachingManager.runId)).build());
//                    CommonLogger.businessLogger.info("==============================================================================================");
                    executionTime = 0;
                    processID = 0;
                    if (isRunningFlagValueEmpty()) {
                        CommonLogger.businessLogger.info("Running Flag Is Empty");
                        executionTime = System.currentTimeMillis();
                        addNewPartation();
                        updateRunningFlag();
                        CustomerImportingManager.startPreparingWritingWorkers();
                        while (!(zipFileNames.isEmpty() || CustomerImportingManager.isCatchingError())) {
                            if (checkDWFiles()) {
                                processID++;
//                                CommonLogger.businessLogger.info("found file [" + dwFile.getName() + "] and will start a reader for it");
//                                CommonLogger.businessLogger.info("will start the reader with process id ==> " + processID);
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found File and will start a reader for it")
                                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, dwFile.getName())
                                        .put(GeneralConstants.StructuredLogKeys.PROCESS_ID, processID).build());
                                CustomerImportingManager.startReaderWorker(processID, dwFile);

                            } else {
                                CommonLogger.businessLogger.info("No new file for the current time");
                                this.sleep(ResourcesCachingManager.getLongValue(EngineDefines.NO_NEW_FILES_SLEEP_TIME));
                                if (isExpiredFolder(dwFolder.lastModified())) {
                                    CommonLogger.errorLogger.error("Folder expiry period reached.Incomplete run , not all files are recieved");
                                    CommonLogger.businessLogger.error("Folder expiry period reached.Incomplete run , not all files are recieved FolderLastUpdateDate=[" + dwFolder.lastModified() + "]");
                                    CustomerImportingManager.setCatchingError(true);
                                    break;
                                }
                            }
                        }
                        CommonLogger.businessLogger.info("Finished running");
                        CustomerImportingManager.shutdownReaderWorkers();
                        CustomerImportingManager.shutdownPreparingWorkers();
                        CustomerImportingManager.shutdownWriterWorkers();
                        waitForFolder();
                        if (CustomerImportingManager.isCatchingError()) {
//                            CommonLogger.businessLogger.info("==============================================================================================");
//                            CommonLogger.businessLogger.info("==================================Failed to Extract DWH Files For Run_ID[" + (ResourcesCachingManager.runId) + "] ==================================");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Failed to Extract dWH Files. The Run Failed and will be Moved to History")
                                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, (ResourcesCachingManager.runId)).build());
//                            CommonLogger.businessLogger.info("The run failed and will be moved to history");
//                            CommonLogger.businessLogger.info("==============================================================================================");
                            moveDWHToHistoricalFolder(folderZipPath, failureHistoricalPath);
                            renameFiles("failed");
                        } else {
                            CommonLogger.businessLogger.info("The run completed successfully and will be moved to history");
                            CustomerImportingManager.updateRunningAndActiveParitions();
                            moveDWHToHistoricalFolder(folderZipPath, successHistoricalPath);
                            renameFiles("success");
                            executionTime = System.currentTimeMillis() - executionTime;

//                            CommonLogger.businessLogger.info("==============================================================================================");
//                            CommonLogger.businessLogger.info("================================== Extracted DWH Files For Run_ID[" + (ResourcesCachingManager.runId) + "] in  " + executionTime + "msec ==================================");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Extracted DWH Files")
                                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, (ResourcesCachingManager.runId))
                                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, executionTime).build());
//                            CommonLogger.businessLogger.info("==============================================================================================");

                        }
                    } else if (!CustomerImportingManager.isAnyWorkerAlive()) {
                        CommonLogger.businessLogger.info("All Workers Dead");
                        executionTime = System.currentTimeMillis();
                        dropCorruptedPartation();
                        addNewPartation();
                        updateRunningFlag();
                        CustomerImportingManager.startPreparingWritingWorkers();
                        while (!(zipFileNames.isEmpty() || CustomerImportingManager.isCatchingError())) {
                            if (checkDWFiles()) {
                                processID++;
//                                CommonLogger.businessLogger.info("found file [" + dwFile.getName() + "] and will start a reader for it");
//                                CommonLogger.businessLogger.info("will start the reader with process id ==> " + processID);
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found File, and will start a reader for it")
                                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, dwFile.getName())
                                        .put(GeneralConstants.StructuredLogKeys.PROCESS_ID, processID).build());
                                CustomerImportingManager.startReaderWorker(processID, dwFile);

                            } else {
                                CommonLogger.businessLogger.info("No new file for the current time");
                                this.sleep(ResourcesCachingManager.getLongValue(EngineDefines.NO_NEW_FILES_SLEEP_TIME));
                                if (isExpiredFolder(dwFolder.lastModified())) {
                                    CommonLogger.errorLogger.error("Folder expiry period reached.Incomplete run , not all files are recieved ");
                                    CommonLogger.businessLogger.error("Folder expiry period reached.Incomplete run , not all files are recieved FolderLastUpdateDate=[" + dwFolder.lastModified() + "]");
                                    CustomerImportingManager.setCatchingError(true);
                                    break;
                                }
                            }
                        }
                        CommonLogger.businessLogger.info("Finished running");
                        CustomerImportingManager.shutdownReaderWorkers();
                        CustomerImportingManager.shutdownPreparingWorkers();
                        CustomerImportingManager.shutdownWriterWorkers();
                        waitForFolder();
                        if (CustomerImportingManager.isCatchingError()) {
//                            CommonLogger.businessLogger.info("==============================================================================================");
//                            CommonLogger.businessLogger.info("==================================Failed to Extract DWH Files For Run_ID[" + (ResourcesCachingManager.runId) + "] ==================================");
//                            CommonLogger.businessLogger.info("The run failed and will be moved to history");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Failed to Extract DWH Files. The Run Failed and will be Moved to History")
                                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, (ResourcesCachingManager.runId)).build());
//                            CommonLogger.businessLogger.info("==============================================================================================");

                            moveDWHToHistoricalFolder(folderZipPath, failureHistoricalPath);
                            renameFiles("failed");
                        } else {
                            CommonLogger.businessLogger.info("The run completed successfully and will be moved to history");
                            CustomerImportingManager.updateRunningAndActiveParitions();

                            moveDWHToHistoricalFolder(folderZipPath, successHistoricalPath);
                            renameFiles("success");
                            executionTime = System.currentTimeMillis() - executionTime;
//                            CommonLogger.businessLogger.info("==============================================================================================");
//                            CommonLogger.businessLogger.info("================================== Extracted DWH Files For Run_ID[" + (ResourcesCachingManager.runId) + "] in  " + executionTime + "msec ==================================");
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Extracted DWH Files")
                                    .put(GeneralConstants.StructuredLogKeys.RUN_ID, (ResourcesCachingManager.runId))
                                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, executionTime).build());
//                            CommonLogger.businessLogger.info("==============================================================================================");
                        }
                    }

                }
                CommonLogger.businessLogger.info("Files Worker will sleep for [" + sleepTimeInMs + "]msec");
                // CustomerImportingManager.getSystemMonitor().setWorkerShutDownFlag(true);
                this.sleep(sleepTimeInMs);
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Fatal Error --->", e);
                CommonLogger.businessLogger.error("Fatal Error-->" + e);
                CustomerImportingManager.setCatchingError(true);
                if (failureHistoricalPath != null) // change to folder name 
                {
                    moveDWHToHistoricalFolder(folderZipPath, failureHistoricalPath);
                }
                handleServiceException(e, methodName, null);
//                handleServiceException(new CommonException("ShutDown Customer Base Importing Engine", ErrorCodes.SHUTDOWN_CUSTOMER_BASE_IMPORTING), methodName, ResourcesCachingManager.getConfigurationValue(GeneralConstants.SHUTDOWN_ENGINE_MOM_ERROR_SEVERITY));
//                ShutdownManager.shutDown();
            }
        }

    }

    private boolean isRunningFlagValueEmpty() {
        String runningValue = ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_RUNNING_DWH_PROFILE_PARTITION);
        if (runningValue == null) {
            return true;
        }
        return false;
    }

    private void addNewPartation() throws CommonException {
        mainServ.addDWHProfileNewPartation(ResourcesCachingManager.runId);
    }

    private void dropCorruptedPartation() throws CommonException {
        mainServ.dropDWHProfilePartation(ResourcesCachingManager.runId);
    }

    private void updateRunningFlag() throws CommonException {
        SystemPropertiesModel model = new SystemPropertiesModel();
        model.setItemKey(EngineDefines.KEY_RUNNING_DWH_PROFILE_PARTITION);
        model.setItemValue(String.valueOf(ResourcesCachingManager.runId));
        model.setGroupId(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        mainServ.updateSystemProperty(model);
    }

    private void waitForFolder() {
        boolean finished = false;
        while (!finished) {
            finished = true;
            //ZAIN if file exist with wrong name 
            for (File f : dwFolder.listFiles()) {
                if (!isValidFileModifiedDate(f.lastModified())) {
                    finished = false;
                }
            }

        }
    }

    private boolean isValidFileModifiedDate(Long filelastModified) {
        Date filelastModifiedDate = new Date(filelastModified);
        Date date = new Date();
        /*in milliseconds*/
        Long diff = date.getTime() - filelastModifiedDate.getTime();
        /*Minutes Difference*/
        Long diffMinutes = diff / (60 * 1000) % 60;
        if (diffMinutes < ResourcesCachingManager.getIntValue(EngineDefines.FILE_LAST_MODIFIED_TIME_MIN)) {
            return false;
        }
        return true;
    }

    private boolean isExpiredFolder(Long folderlastModified) {
        Date folderlastModifiedDate = new Date(folderlastModified);
        Date date = new Date();
        /*in milliseconds*/
        Long diff = date.getTime() - folderlastModifiedDate.getTime();
        /*Minutes Difference*/
        Long diffMinutes = diff / (60 * 1000) % 60;
        if (diffMinutes < ResourcesCachingManager.getIntValue(EngineDefines.FOLDER_EXPIRY_PERIOD_MIN)) {
            return false;
        }
        return true;
    }

    private boolean checkDWFiles() throws InterruptedException, CommonException {
        String methodName = "checkDWFiles";
        int counter = 0;
        boolean filefound = false;
        for (String fileName : zipFileNames) {
            checkDWFile(fileName);
            if (dwFile != null) {
                filefound = true;
                break;
            } else {
                counter++;
            }
        }
        if (filefound) {
            zipFileNames.remove(counter);
            return true;
        }
        return false;
    }

    private void checkDWFile(String zipFileName) throws CommonException, InterruptedException {
//        CommonLogger.businessLogger.info("checkDWFile started for File=[" + zipFileName + "]");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "CheckDWFile started")
                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, zipFileName).build());
        String methodName = "checkDWFile";
        String fileUnzipPath = null;
        File file = null;
        if (folderZipPath == null || folderUnzipPath == null) {
            dwFile = null;
        }
        /*DW Extractor File*/
        String filePath = folderZipPath + File.separatorChar + zipFileName;
        file = new File(filePath);
        if (file.exists()) {
//            if (!isValidFileModifiedDate(file.lastModified())) {
//                dwFile = null;
//            }
            try {
                /*UnZip File*/
                String zipExtension = ResourcesCachingManager.getStringValue(EngineDefines.DW_ZIP_FILE_EXTENSION);
                String unzipExtension = ResourcesCachingManager.getStringValue(EngineDefines.DW_UNZIP_FILE_EXTENSION);
                File unzipPath = new File(folderUnzipPath);
                if (!unzipPath.exists()) {
                    unzipPath.mkdir();
                }
                fileUnzipPath = folderUnzipPath + File.separatorChar + (zipFileName.replace(zipExtension, unzipExtension));
                ZipUtil.unzip(filePath, fileUnzipPath);
            } catch (IOException ex) {
                throw new CommonException("UnZipping Error in CLASS:" + CLASS_NAME + " method: " + methodName + ex, ErrorCodes.UNZIPPPING_ERROR);
            }
            file = new File(fileUnzipPath);
            if (file.exists()) {
                dwFile = file;
            } else {
                dwFile = null;
            }
        } else {
            dwFile = null;
        }
//        CommonLogger.businessLogger.info("checkDWFile ended for File=[" + zipFileName + "]");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChecDWFile ended")
                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, zipFileName).build());
    }

    private boolean checkNewFolder() {
        String methodName = "checkNewFolder";
        File folder = null;

        if (folderZipPath == null || folderUnzipPath == null) {
            return false;
        }
        /*DW Extractor File*/
        folder = new File(folderZipPath);

        if (folder.exists()) {
            dwFolder = folder;
            return true;
        }
        return false;
    }

    private void moveDWHToHistoricalFolder(String sourcePath, String destinationFolderPath) {
        CommonLogger.businessLogger.info("--------- Moving dwh folder to historical folder  ----------");

        String methodName = "moveDWHToHistoricalFolder";
        File sourceFile = null;

        try {
            sourceFile = new File(sourcePath);

            if (!new File(destinationFolderPath).exists()) {
                new File(destinationFolderPath).mkdirs();
            }
            String fileName = getFolderName(sourcePath);
            File unzippedFolder = new File(folderUnzipPath);
            removeDirectory(unzippedFolder);
            archiveFile(sourcePath, destinationFolderPath, fileName);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Fatal Error---Method=[" + methodName + "] SourcePath=[" + sourcePath + "]  DestinationFolderPath=[" + destinationFolderPath + "]", ex);
            CommonLogger.businessLogger.error("Fatal Error---Method=[" + methodName + "] SourcePath=[" + sourcePath + "]  DestinationFolderPath=[" + destinationFolderPath + "]" + ex);
            handleServiceException(new CommonException("Failed To Move Files Into Historical ", ErrorCodes.ERROR_IN_MOVING_DWH_FILE_TO_HISTORY), methodName, ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_MOVE_FILES_HISTORICAL_MOM_SEVERITY));
            // throw new CommonException("Error in CLASS:" + CLASS_NAME + " method: " + methodName + ex, ErrorCodes.ERROR_IN_MOVING_DWH_FILE_TO_HISTORY);
        }
        CommonLogger.businessLogger.info("--------- DWH File moved to historical folder successfully  ----------");
    }

    private String getFolderName(String filePath) {
        String path = "";

        int index = -1;

        index = filePath.lastIndexOf("/");
        if (index == -1) {
            index = filePath.lastIndexOf("\\");
        }

        path = filePath.substring((index + 1));

        return path;
    }

    public static synchronized boolean archiveFile(String originalPath,
            String newPath,
            String folderName) {
        String command = null;
        String destFile = null;
        Process p = null;
        int retrials = 5;
        String msg = null;
        String methodId = "[archiveFile]";

        try {
            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
            String[] windowsCommand;
            destFile = folderName + "-" + s.format(Calendar.getInstance().getTime());
            if (ResourcesCachingManager.getStringValue(EngineDefines.OPERATING_SYSTEM).equalsIgnoreCase(EngineDefines.UNIX)) {
                command
                        = ResourcesCachingManager.getStringValue(EngineDefines.MOVE_COMMAND_UNIX) + " " + originalPath + " " + newPath
                        + File.separatorChar + destFile;
            } else if (ResourcesCachingManager.getStringValue(EngineDefines.OPERATING_SYSTEM).equalsIgnoreCase(EngineDefines.WINDOWS)) {
                command
                        = ResourcesCachingManager.getStringValue(EngineDefines.MOVE_COMMAND_WINDOWS) + " " + originalPath + " " + newPath
                        + File.separatorChar + destFile;

            }
            windowsCommand = new String[]{"cmd.exe", "/c", command};
//            CommonLogger.businessLogger.debug("Archive command>>>>>>>>>>>>>" + command);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archive command")
                    .put(GeneralConstants.StructuredLogKeys.COMMAND, command).build());
            //command = "cmd MOVE D:\\ctp\\working_folder\\hdr.11282013.052400.dat D:\\ctp\\working_folder\\InProgress-hdr.11282013.052400.dat";
//            CommonLogger.businessLogger.debug("Going to execute >> " + command);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Going to execute")
                    .put(GeneralConstants.StructuredLogKeys.COMMAND, command).build());

            while (retrials > 0) {
                retrials--;
                try {

                    // Runtime.getRuntime().exec(command2);
//          
                    if (ResourcesCachingManager.getStringValue(EngineDefines.OPERATING_SYSTEM).equalsIgnoreCase(EngineDefines.UNIX)) {
                        p = Runtime.getRuntime().exec(command);
                    } else if (ResourcesCachingManager.getStringValue(EngineDefines.OPERATING_SYSTEM).equalsIgnoreCase(EngineDefines.WINDOWS)) {
                        p = Runtime.getRuntime().exec(windowsCommand);
                    }
                    if (p != null) {
                        p.waitFor();
                    }
                    return true;
                } catch (Exception e) {
                    msg
                            = "Exception in archiveFile thrown ==>" + e.getMessage();
                    CommonLogger.businessLogger.debug(methodId + msg);
                    CommonLogger.errorLogger.error(methodId + msg, e);
                }
            }

        } catch (Exception e) {
            msg
                    = "Exception in archiveFile thrown ==>" + e.getMessage();
            CommonLogger.businessLogger.debug(methodId + msg);
            CommonLogger.errorLogger.error(methodId + msg, e);
        }
        return true;
    }

    public static boolean removeDirectory(File directory) {

        // System.out.println("removeDirectory " + directory);
        if (directory == null) {
            return false;
        }
        if (!directory.exists()) {
            return true;
        }
        if (!directory.isDirectory()) {
            return false;
        }

        String[] list = directory.list();

        // directory is empty.
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File entry = new File(directory, list[i]);

                //        System.out.println("\tremoving entry " + entry);
                if (entry.isDirectory()) {
                    if (!removeDirectory(entry)) {
                        return false;
                    }
                } else if (!entry.delete()) {
                    return false;
                }
            }
        }

        return directory.delete();
    }

    private void handleServiceException(Exception e, String methodName, String Severity) {

        MOMErrorsModel errorModel = new MOMErrorsModel();
        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof InterruptedException) {
            campaignException = new CommonException("Interrupted exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.INTERRUPTED_ERROR);
        } else if (e instanceof SQLException) {
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        if (Severity != null) {
            errorModel.setPreceivedSeverity(Integer.parseInt(Severity));
        } else {
            errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_MEDIUM);
        }

        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_CUSTOMER_IMPORT_ENGINE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setErrorMessage(campaignException.getErrorMsg());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        Utility.sendMOMAlarem(errorModel);

    }

    private void getRemoteFiles(String protocol) throws CommonException {
        String HOST = ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_HOST);
        int PORT = Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_PORT));
        String USER = ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_USER);
        String PASS = ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_PASS);
        String WORKING_DIR = ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_WORKING_DIR);
        String FILES_LIST_Concatinated = ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FILE_NAMES);
        String[] FILES_LIST = FILES_LIST_Concatinated.split(",");
        String FILE_for_error_log = null;

        makeAllDirectories();

        if (protocol.equalsIgnoreCase("ftp")) {
//            String server = "10.0.20.18";
//            int port = 21;
//            String user = "admin";
//            String pass = "admin123";

            FTPClient ftpClient = new FTPClient();
            try {

                ftpClient.connect(HOST, PORT);
                ftpClient.login(USER, PASS);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                boolean success;

                for (String FILE : FILES_LIST) {
                    FILE_for_error_log = FILE;
                    String remoteFile = WORKING_DIR + "/" + FILE;
                    File downloadFile = new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FOLDER_PATH));
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                    success = ftpClient.retrieveFile(remoteFile, outputStream);
                    outputStream.close();

                    if (success) {
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File downloaded successfully")
                                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, FILE).build());
//                        System.out.println("File '" + FILE + "' has been downloaded successfully.");
                    }
                }

                success = ftpClient.completePendingCommand();
//                System.out.println("FTP STATUS SUCCESS: " + success);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "FTP status")
                        .put(GeneralConstants.StructuredLogKeys.STATUS, success).build());

            } catch (Exception ex) {
                CommonLogger.businessLogger.error("Exception found while tranfering file: " + FILE_for_error_log);
                CommonLogger.errorLogger.error("Exception found while tranfering file: " + FILE_for_error_log, ex);

                CommonLogger.businessLogger.info("Deleting Copied files if any");
                for (String FILE : FILES_LIST) {
                    File f = new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FOLDER_PATH) + "/" + FILE);
                    if (f.delete()) {
//                        CommonLogger.businessLogger.info(f.getName() + " is deleted!");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File is Deleted Successfully")
                                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, f.getName()).build());
                    } else {
//                        CommonLogger.businessLogger.info("Delete operation is failed.");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Delete Operation status")
                                .put(GeneralConstants.StructuredLogKeys.STATUS, "failed").build());
                    }
                }
            } finally {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (Exception ex) {
                    CommonLogger.businessLogger.error("Exception found while tranfer");
                    CommonLogger.errorLogger.error("Exception found while tranfer", ex);
                }
            }
        } else if (protocol.equalsIgnoreCase("sftp")) {
            Session session = null;
            Channel channel = null;
            ChannelSftp channelSftp = null;
            FileOutputStream fOut = null;
            CommonLogger.businessLogger.info("preparing the host information for sftp.");

            try {
                ThreadContext.put(GeneralConstants.StructuredLogKeys.HOST, HOST);
                ThreadContext.put(GeneralConstants.StructuredLogKeys.PORT, PORT + "");
                ThreadContext.put(GeneralConstants.StructuredLogKeys.USER, USER);
                JSch jsch = new JSch();
                session = jsch.getSession(USER, HOST, PORT);
                session.setPassword(PASS);
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
//                CommonLogger.businessLogger.info("Connected to Host: " + HOST + " | Port: " + PORT + " | User: " + USER);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Connected Successfully to SFTP Server").build());
                channel = session.openChannel("sftp");
                channel.connect();
//                CommonLogger.businessLogger.info("sftp channel opened and connected for host: " + HOST + " | Port: " + PORT + " | User: " + USER);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SFTP channel opened and connected").build());
                channelSftp = (ChannelSftp) channel;
                channelSftp.cd(WORKING_DIR);

//                CommonLogger.businessLogger.info("Started copying files from Host: " + HOST + " | Port: " + PORT + " | User: " + USER);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started Copying Files").build());
                for (String SFTP_FILE : FILES_LIST) {
                    FILE_for_error_log = SFTP_FILE;
                    File f = new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FOLDER_PATH) + "/" + SFTP_FILE);
                    if (f.createNewFile()) {
//                        CommonLogger.businessLogger.info("File: " + SFTP_FILE + " is created!");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SFTP File is created")
                                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, SFTP_FILE).build());
                    } else {
//                        CommonLogger.businessLogger.info("File: " + SFTP_FILE + " already exists.");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SFTP File alreay Exists")
                                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, SFTP_FILE).build());
                    }
                    fOut = new FileOutputStream(f);
                    IOUtils.copy(channelSftp.get(SFTP_FILE), fOut);
                    fOut.close();
                }

//                CommonLogger.businessLogger.info("File/s transfered successfully from Host: " + HOST + " | Port: " + PORT + " | User: " + USER);
//                CommonLogger.businessLogger.info("Renaming files in Host Directory | Host: " + HOST + " | Port: " + PORT + " | User: " + USER);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File/s transfered Successfully").build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Renaming Files in Host Directory").build());
                for (String SFTP_FILE : FILES_LIST) {
//                    CommonLogger.businessLogger.info("Renaming file from: '" + SFTP_FILE + "' to '" + SFTP_FILE + "_processing'");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Renaming SFTP File")
                            .put(GeneralConstants.StructuredLogKeys.FILE_NAME, SFTP_FILE)
                            .put(GeneralConstants.StructuredLogKeys.NEW_FILE_NAME, SFTP_FILE + "_processing").build());
                    channelSftp.rename(SFTP_FILE, SFTP_FILE + "_processing");
                }
//                CommonLogger.businessLogger.info("Renamed files successfully in Host Directory | Host: " + HOST + " | Port: " + PORT + " | User: " + USER);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Renamed Files Succesfully").build());
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("Exception found while tranfering file: " + FILE_for_error_log);
                CommonLogger.errorLogger.error("Exception found while tranfering file: " + FILE_for_error_log, ex);

                if (fOut != null) {
                    try {
                        fOut.close();
                    } catch (IOException e) {
                        CommonLogger.businessLogger.error("failed to close FileOutputStream | " + e.getMessage());
                    }
                }

                CommonLogger.businessLogger.info("Deleting Copied files if any");
                for (String SFTP_FILE : FILES_LIST) {
                    File f = new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FOLDER_PATH) + "/" + SFTP_FILE);
                    if (f.delete()) {
                        CommonLogger.businessLogger.info(f.getName() + " is deleted!");
                    } else {
                        CommonLogger.businessLogger.info("Delete operation is failed.");
                    }
                }
            } finally {
                if (fOut != null) {
                    try {
                        fOut.close();
                    } catch (IOException ex) {
                        CommonLogger.businessLogger.error("failed to close FileOutputStream | " + ex.getMessage());
                    }
                }
                channelSftp.exit();
                CommonLogger.businessLogger.info("sftp Channel exited.");
                channel.disconnect();
                CommonLogger.businessLogger.info("Channel disconnected.");
                session.disconnect();
                CommonLogger.businessLogger.info("Host Session disconnected.");
                ThreadContext.remove(GeneralConstants.StructuredLogKeys.HOST);
                ThreadContext.remove(GeneralConstants.StructuredLogKeys.PORT);
                ThreadContext.remove(GeneralConstants.StructuredLogKeys.USER);
            }
        }

        File f = new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FOLDER_PATH));
        if (f.listFiles().length == 0) {
            CommonLogger.businessLogger.info("Removing Folders Zipped & Unzipped");
            removeDirectory(f);
            removeDirectory(new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_UNZIP_FOLDER_PATH)));
        }
    }

    private void makeAllDirectories() throws CommonException {
        createDir(new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FOLDER_PATH)));
        createDir(new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_UNZIP_FOLDER_PATH)));
        createDir(new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_SUCCESS_HISTORY_PATH)));
        createDir(new File(ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_FAILURE_HISTORY_PATH)));
    }

    private void createDir(File dir) throws CommonException {
        try {
            if (!dir.exists()) {
                if (dir.mkdirs()) {
//                    CommonLogger.businessLogger.info("Multiple directories are created for: " + dir.getAbsolutePath());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Multiple directories are created")
                            .put(GeneralConstants.StructuredLogKeys.PATH, dir.getAbsolutePath()).build());
                } else {
                    CommonLogger.businessLogger.error("Failed to create multiple directories for: " + dir.getAbsolutePath());
                    throw new CommonException("Failed to create multiple directories for: " + dir.getAbsolutePath(), ErrorCodes.GENERAL_ERROR);
                }
            }
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Failed to create multiple directories for: " + dir.getAbsolutePath());
            CommonLogger.errorLogger.error("Failed to create multiple directories for: " + dir.getAbsolutePath(), e);
            throw new CommonException("Failed to create multiple directories for: " + dir.getAbsolutePath(), ErrorCodes.GENERAL_ERROR);
        }
    }

    private void renameFiles(String backUpTag) {
        String SFTP_HOST = ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_HOST);
        int SFTP_PORT = Integer.parseInt(ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_PORT));
        String SFTP_USER = ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_USER);
        String SFTP_PASS = ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_PASS);
        String SFTP_WORKING_DIR = ResourcesCachingManager.getConfigurationValue(EngineDefines.SFTP_WORKING_DIR);
        String SFTP_FILES_LIST_Concatinated = ResourcesCachingManager.getConfigurationValue(EngineDefines.KEY_DWH_ZIP_FILE_NAMES);
        String[] SFTP_FILES_LIST = SFTP_FILES_LIST_Concatinated.split(",");

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        CommonLogger.businessLogger.info("preparing the host information for sftp.");

        try {
            ThreadContext.put(GeneralConstants.StructuredLogKeys.HOST, SFTP_HOST);
            ThreadContext.put(GeneralConstants.StructuredLogKeys.PORT, SFTP_PORT + "");
            ThreadContext.put(GeneralConstants.StructuredLogKeys.USER, SFTP_USER);
            JSch jsch = new JSch();
            session = jsch.getSession(SFTP_USER, SFTP_HOST, SFTP_PORT);
            session.setPassword(SFTP_PASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
//            CommonLogger.businessLogger.info("Connected to Host: " + SFTP_HOST + " | Port: " + SFTP_PORT + " | User: " + SFTP_USER);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Connected to SFTP Host").build());
            channel = session.openChannel("sftp");
            channel.connect();
//            CommonLogger.businessLogger.info("sftp channel opened and connected for host: " + SFTP_HOST + " | Port: " + SFTP_PORT + " | User: " + SFTP_USER);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SFTP Channel opened and connected").build());
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTP_WORKING_DIR);

//            CommonLogger.businessLogger.info("Renaming files in Host Directory | Host: " + SFTP_HOST + " | Port: " + SFTP_PORT + " | User: " + SFTP_USER);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Renaming Files in Host Directory").build());
            for (String SFTP_FILE : SFTP_FILES_LIST) {
//                CommonLogger.businessLogger.info("Renaming file from: '" + SFTP_FILE + "_processing' to '" + SFTP_FILE + "_" + backUpTag + "'");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Renaming File")
                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, SFTP_FILE + "_processing")
                        .put(GeneralConstants.StructuredLogKeys.NEW_FILE_NAME, SFTP_FILE + "_" + backUpTag).build());
                channelSftp.rename(SFTP_FILE + "_processing", SFTP_FILE + "_" + backUpTag);
            }
//            CommonLogger.businessLogger.info("Renamed files successfully in Host Directory | Host: " + SFTP_HOST + " | Port: " + SFTP_PORT + " | User: " + SFTP_USER);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Renamed Files Successfully in Host Directory").build());
        } catch (Exception ex) {
            CommonLogger.businessLogger.error("Exception found while tranfer the response.");
            CommonLogger.errorLogger.error("Exception found while tranfer the response.", ex);
        } finally {
            channelSftp.exit();
            CommonLogger.businessLogger.info("sftp Channel exited.");
            channel.disconnect();
            CommonLogger.businessLogger.info("Channel disconnected.");
            session.disconnect();
            CommonLogger.businessLogger.info("Host Session disconnected.");
            ThreadContext.remove(GeneralConstants.StructuredLogKeys.HOST);
            ThreadContext.remove(GeneralConstants.StructuredLogKeys.PORT);
            ThreadContext.remove(GeneralConstants.StructuredLogKeys.USER);
        }
    }
}
