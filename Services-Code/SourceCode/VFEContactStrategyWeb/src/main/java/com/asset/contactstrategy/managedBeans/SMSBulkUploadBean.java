package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.models.SMSBulkFileModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.facade.ServiceManagmentFacade;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.threads.SMSBulkHandlerThread;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.ServiceWebModel;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author aya.moawed 2595
 */
@ManagedBean
@ViewScoped
public class SMSBulkUploadBean implements Serializable {

    private String serviceName;//service name chosen
    private String servicePassword; // CR 1901 | eslam.ahmed
    private boolean show;
    private ArrayList<ServiceWebModel> serviceList;//service lists
    private String originatorValue;//originator entered
    private Boolean doNotApplyFlag;//do not apply entered
    private String nextPg;//next page upon cancel or success
    private int uploadSizeLimit;//size limit per file
    private Integer uploadedFilesLimit;//number of files to upload
    private int uploadedRecordsPerFileLimit;//limit of number of records to be parsed per file
    private MainService mainservice = new MainService();
    private ArrayList<OriginatorTypeModel> originatorTypes;//types of originators
    private Integer originatorType;//chosen originatorValue type
    private ArrayList<SMSBulkFileModel> uploadedFiles;
    private ArrayList<InputModel> failureSMSRows;
    private boolean serviceFilled;
    private boolean originatorMSISDNFilled;
    private boolean originatorTypeFilled;
    private boolean editableService;
    private UserModel loggedInUser;
    private Integer msisdnLength;
    private AtomicInteger numberOfUploadedFiles;
    private int originatorMaxLength;
    private HashMap<Integer, LookupModel> originatorValues;

    @PostConstruct
    public void init() {
        try {
            CommonLogger.businessLogger.info("Initializing SMSBulkUpload page ..");
            nextPg = "SMSBulkUpload.xhtml";
            doNotApplyFlag = false;
            serviceName = null;
            servicePassword = null;
            originatorTypes = new ArrayList<OriginatorTypeModel>();
            originatorTypes.addAll(SystemLookups.ORIGINATORS_LIST.values());
            originatorValues = SystemLookups.ORIGINATOR_VALUES_LK;
            retrieveApplicationServiceList();
            msisdnLength = Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.MSISDN_LENGTH));
            cleanUploadedFiles();
            serviceFilled = false;
            originatorMSISDNFilled = false;
            originatorTypeFilled = false;
            editableService = true;
            UserFacade userFacade = new UserFacade();
            loggedInUser = userFacade.convertModelToWebModel((UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));
            numberOfUploadedFiles = new AtomicInteger(0);
            failureSMSRows = new ArrayList<>();
        } catch (CommonException ex) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for [SMSBulkUpload]" + ex);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [SMSBulkUpload]", ex);
            Utility.showErrorMessage(null, "pageInit.error");
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for [" + SMSBulkUploadBean.class.getName() + ".init]" + e);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSBulkUploadBean.class.getName() + ".init]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
//            CommonLogger.businessLogger.error("Error Occured ::=====>" + e);
        } finally {
            CommonLogger.businessLogger.info("Ending initializing SMSBulkUpload page ..");
        }
    }

    public void cleanUploadedFiles() {
        CommonLogger.businessLogger.info("initializing uploaded files in cleanUploadedFiles method");
        try {
            uploadedFiles = new ArrayList<>();

            uploadedRecordsPerFileLimit = 50000;
            uploadedFilesLimit = 4;
            String temp = mainservice.getSystemPropertyByKey(Defines.UPLOAD_FILE_SIZE_LIMIT_KEY, GeneralConstants.SRC_ID_WEB_INTERFACE);
            if (temp != null && !temp.equals("")) {
                uploadSizeLimit = Integer.parseInt(temp) * 1000000;//bytes to megabytes
            }
            WebLogModel webLog = new WebLogModel();
            webLog.setOperationName("Cleaned Uploaded Files");
            webLog.setStringBefore("");
            webLog.setStringAfter("");
            webLog.setPageName("SMS Bulk Upload Page");
            webLog.setUserName(loggedInUser.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
        } catch (CommonException ex) {
            CommonLogger.businessLogger.error(SMSBulkUploadBean.class.getName() + " || " + "Getting Caught Exception---->  for [cleanUploadedFiles]" + ex);
            CommonLogger.errorLogger.error(SMSBulkUploadBean.class.getName() + " || " + "Getting Caught Exception---->  for [cleanUploadedFiles]", ex);
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for [" + SMSBulkUploadBean.class.getName() + ".cleanUploadedFiles]" + e);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSBulkUploadBean.class.getName() + ".cleanUploadedFiles]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        } finally {
            CommonLogger.businessLogger.info("finished initializing uploaded files in cleanUploadedFiles method");
        }
    }

    public void retrieveApplicationServiceList() throws CommonException {
        CommonLogger.businessLogger.info("initializing service list in retrieveApplicationServiceList method");
        try {
            ServiceManagmentFacade serviceFacade = new ServiceManagmentFacade();
            serviceList = serviceFacade.getServices();
        } catch (CommonException ex) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for [" + SMSBulkUploadBean.class.getName() + ".retrieveApplicationServiceList]" + ex);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSBulkUploadBean.class.getName() + ".retrieveApplicationServiceList]", ex);
            throw ex;
        } finally {
            CommonLogger.businessLogger.info("finished initializing service list in retrieveApplicationServiceList method");
        }
    }

    public String save() throws CommonException {
        CommonLogger.businessLogger.info("saving SMS bulk upload then returning to home page in save method");

        // CR 1901 | eslam.ahmed
        if (servicePassword == null) {
            CommonLogger.businessLogger.info("service password not set..");
            Utility.showErrorMessage(null, Constants.EMPTY, " Service password");
            return null;
        }
        ServiceWebModel service = null;
        for (ServiceWebModel serviceModel : serviceList) {
            if (serviceModel.getServiceName().equals(serviceName)) {
                service = serviceModel;
            }
        }
        boolean validPassword = com.asset.contactstrategy.common.utils.Utility
                .validateServicePassword(service.getHashedPassword(), servicePassword, SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY));
        if (!validPassword) {
            CommonLogger.businessLogger.info("invalid service password");
            Utility.showErrorMessage(null, Constants.INVALID_SERVICE_PASSWORD, "Service password");
            return null;
        }
        // end CR 1901 | eslam.ahmed

        if (numberOfUploadedFiles.get() <= 0) {
            Utility.showErrorMessage(null, Constants.NO_FILES);
            RequestContext.getCurrentInstance().execute("PF('waitingMsgFunDlg').hide();");
            return null;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String ret = "" + nextPg + "?faces-redirect=true";
        try {
            String fileNames = "File Names: ";
            CommonLogger.businessLogger.info("finished initializing service list in retrieveApplicationServiceList method");
            if (facesContext.getMessageList().isEmpty()) {
                Date stepStartDate = new Date();
                if (!validateOriginatorValue() || originatorType == null) {
                    RequestContext.getCurrentInstance().execute("PF('waitingMsgFunDlg').hide();");
//                    Utility.showErrorMessage(null, Constants.VALIDATE_ORIGINATOR_LENGTH); 
                    return null;
                }
                int i = 0;
                StringBuffer failedFiles = new StringBuffer();
                ArrayList<SMSBulkHandlerThread> handlerThreads = new ArrayList<>();
                for (SMSBulkFileModel file : uploadedFiles) {
                    fileNames += file.getName() + " | ";
                    file.setUserID(Integer.parseInt(loggedInUser.getId() + ""));
                    ArrayList<InputModel> singleFileContent = file.getDataRecords();
                    if (singleFileContent.isEmpty()) {
                        continue;
                    }
                    for (InputModel singleRow : singleFileContent) {
                        singleRow.setOriginatorMSISDN(originatorValue);
                        singleRow.setDoNotApply(doNotApplyFlag);
                        singleRow.setSystemName(serviceName);
                        singleRow.setSystemPassword(servicePassword); // CR 1901 | eslam.ahmed
                        singleRow.setOriginatorType(Byte.parseByte(originatorType + ""));
                    }
                    handlerThreads.add(new SMSBulkHandlerThread(file));
                    handlerThreads.get(i).start();
                    i++;
                }
                if (handlerThreads.isEmpty()) {
                    Utility.showErrorMessage(null, Constants.NO_SUCCESSFUL_RECORDS);
                    RequestContext.getCurrentInstance().execute("PF('waitingMsgFunDlg').hide();");
                    return null;
                }
                for (SMSBulkHandlerThread thread : handlerThreads) {
                    thread.join();
                    if (thread.getFileModel().getStatus() == SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.FAILED).getId()) {
                        failedFiles.append(thread.getFileModel().getName()).append(",");
                    }
                }
                if (failedFiles.length() == 0) {
                    Utility.showInfoMessage(null, Constants.ITEM_ADDED, "SMS Bulk ", "Files");
                } else {
                    Utility.showErrorMessage(null, Constants.ADD_ITEM_FAILURE, "SMS Bulk ", "Files", failedFiles.toString().substring(0, failedFiles.toString().lastIndexOf(",")) + " failed to be parsed and saved");
                    ret = null;
                }
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                Date stepEndDate = new Date();
                long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//                CommonLogger.businessLogger.info("Saving SMS Bulk ended in " + totalProcessingTime + " msec");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Saving SMS Bulk ended")
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
                RequestContext.getCurrentInstance().execute("PF('waitingMsgFunDlg').hide();");
                WebLogModel webLog = new WebLogModel();
                webLog.setOperationName("Saved SMS Bulk for " + uploadedFiles.size() + " files");
                webLog.setStringBefore("");
                webLog.setStringAfter(fileNames);
                webLog.setPageName("SMS Bulk Upload Page");
                webLog.setUserName(loggedInUser.getUsername());
                WebLoggerFacade.insertWebLog(webLog);
                return ret;
            }
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(SMSBulkUploadBean.class.getName() + " || " + "Getting Caught Exception---->  for [save]" + ex);
            CommonLogger.errorLogger.error(SMSBulkUploadBean.class.getName() + " || " + "Getting Caught Exception---->  for [save]", ex);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " Error occured while saving SMS bulks");
            ret = null;
        }
        RequestContext.getCurrentInstance().execute("PF('waitingMsgFunDlg').hide();");
        return null;
    }

    public void removeFile(SMSBulkFileModel file) {
//        CommonLogger.businessLogger.info("removing file " + file + " from files uploaded");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Removing File from Files Uploaded")
                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, file).build());
        if (uploadedFiles != null) {
            uploadedFiles.remove(file);
            numberOfUploadedFiles.decrementAndGet();
            if (uploadedFiles.isEmpty()) {
                editableService = true;
            }

            for (int i = 0; i < failureSMSRows.size(); i++) {
                if (failureSMSRows.get(i).getFileName().equals(file.getName())) {
                    failureSMSRows.remove(i);
                    i--;
                }
            }

        }
    }

    public void handleSMSBulkFileUpload(FileUploadEvent event) {
        long currTime = new Date().getTime();
        try {
//            CommonLogger.businessLogger.info("Uploading File : " + event.getFile().getFileName() + " in method handleSMSBulkFileUpload");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Uploading File in Method handleSMSBulkFileUpload")
                    .put(GeneralConstants.StructuredLogKeys.FILE_NAME, event.getFile().getFileName()).build());
            if (numberOfUploadedFiles.get() < uploadedFilesLimit) {
                numberOfUploadedFiles.incrementAndGet();
                boolean nameExist = false;

                for (SMSBulkFileModel model : uploadedFiles) {
                    if (model.getName().equalsIgnoreCase(event.getFile().getFileName())) {
                        nameExist = true;
                        break;
                    }
                }
                if (nameExist) {
                    numberOfUploadedFiles.decrementAndGet();
                    Utility.showWarningMessage(null, Constants.UNIQUE_NAME_ERROR, "File with name ", event.getFile().getFileName());
                    CommonLogger.businessLogger.info("file already exists .. ending uploading of file");
                    return;
                }

            } else {
                Utility.showWarningMessage(null, Constants.MAXIMUM_REACHED, " Number of uploaded files", uploadedFilesLimit + "");
                return;
            }
            if (uploadedFiles != null) {
                FacesMessage msg = new FacesMessage(event.getFile().getFileName() + " is successfully uploaded.", event.getFile().getFileName() + " is uploaded.");

                boolean parsedSuccessfully = parseFile(event.getFile().getFileName(), event.getFile().getInputstream());//B) APPEND FILE CONTENT TO SMSINPUTS VARIABLE

                if (parsedSuccessfully) {
                    Utility.showInfoMessage(null, Constants.ITEM_ADDED, event.getFile().getFileName(), "file");
                    WebLogModel webLog = new WebLogModel();
                    webLog.setOperationName("Uploaded File " + event.getFile().getFileName() + " Successfully");
                    webLog.setStringBefore("");
                    webLog.setStringAfter("");
                    webLog.setPageName("SMS Bulk Upload Page");
                    webLog.setUserName(loggedInUser.getUsername());
                    WebLoggerFacade.insertWebLog(webLog);
                }
            } else {
                Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, "");
            }
        } catch (Exception ex) {
            CommonLogger.businessLogger.error("Getting Caught Exception---->  for [" + SMSBulkUploadBean.class.getName() + ".handleSMSBulkFileUpload]" + ex);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSBulkUploadBean.class.getName() + ".handleSMSBulkFileUpload]", ex);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        } finally {
//            CommonLogger.businessLogger.info("ending uploading of file [" + event.getFile().getFileName() + "] in time " + (new Date().getTime() - currTime));
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending Uploading of File")
                    .put(GeneralConstants.StructuredLogKeys.FILE_NAME, event.getFile().getFileName())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (new Date().getTime() - currTime)));
        }
    }

    private boolean parseFile(String fileName, InputStream inputFile) throws IOException, CommonException {
//        CommonLogger.businessLogger.info("parsing file " + fileName + " in parseFile method");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Parsing File in parseFile Method")
                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, fileName).build());
        ServiceWebModel service = null;
        CommonLogger.businessLogger.info("STEP 1 .. CHECKING SERVICE NAME ..");
        if (serviceName == null || serviceName.trim().isEmpty()) {
            editableService = true;
        } else {
            for (ServiceWebModel serviceModel : serviceList) {
                if (serviceModel.getServiceName().equals(serviceName)) {
                    service = serviceModel;
                    editableService = false;
                }
            }
        }
        if (service == null || editableService) {
            CommonLogger.businessLogger.info("service not set..");
            Utility.showWarningMessage(null, Constants.EMPTY, " Service name");
            return false;
        }
//        CommonLogger.businessLogger.info("service name is [" + serviceName + "]");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service is set")
                .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceName).build());
        SMSBulkFileModel model = null;
        int recordCount = 0;
        int successCount = 0;
        int failureCount = 0;
        boolean lineParsingSuccess = false;
        String validatedMSISDN = null;
        InputModel inputModel = null;
        InputModel failureInputModel = null;
        ArrayList<InputModel> smsRows = new ArrayList<>();
        CommonLogger.businessLogger.info("STEP 2 .. PARSE FILE TO SET OF ROWS");
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        CsvParser parser = new CsvParser(settings);
        List<String[]> allRows = parser.parseAll(inputFile);
        CommonLogger.businessLogger.info("STEP 3 .. SET VALUES FOR EACH RECORD");
        for (int rowIndex = 0; (rowIndex < allRows.size()) && (recordCount < uploadedRecordsPerFileLimit); rowIndex++) {
            String[] row = null;
            try {
                failureInputModel = new InputModel();
                row = allRows.get(rowIndex);
//                CommonLogger.businessLogger.info("Record Count [" + recordCount + "] Data [" + Arrays.toString(row) + "]");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Setting Record Values")
                        .put(GeneralConstants.StructuredLogKeys.RECORD_COUNT, recordCount)
                        .put(GeneralConstants.StructuredLogKeys.DATA, Arrays.toString(row)).build());
                inputModel = new InputModel();
                lineParsingSuccess = true;
                /*
                 o	MSISDN.             0
                 o	Message Script      1
                 o	Message Language.   2 => mandatory number
                 o	Message Priority.   3 = > optional string val(normal||high) only
                 o	Optional Param1.    4
                 o	Optional Param2.    5
                 o	Optional Param3.    6
                 o	Optional Param4.    7
                 o	Optional Param5.    8
                 */
                if (row.length >= 3) {
                    inputModel.setRowLine(row);
                    if (!row[0].trim().isEmpty()) {//1  MSISDN
                        validatedMSISDN = "";
                        try {
                            validatedMSISDN = com.asset.contactstrategy.common.utils.Utility.validateMSISDNFormatSMPP(row[0].trim());
                        } catch (CommonException invalidmsisdn) {
                            failureInputModel.setErrorReason("Error in MSISDN");
                            CommonLogger.businessLogger.error(SMSBulkUploadBean.class.getName() + " || "
                                    + "Caught invalid destination MSISDN with value" + row[0] + " " + invalidmsisdn);
                            CommonLogger.errorLogger.error(SMSBulkUploadBean.class.getName() + " || "
                                    + "Caught invalid destination MSISDN with value" + row[0], invalidmsisdn);
                            lineParsingSuccess = false;
                        }
                        if (validatedMSISDN != null && !validatedMSISDN.trim().isEmpty()) {
                            inputModel.setDestinationMSISDN(validatedMSISDN);
                        }
                    }

                    if (lineParsingSuccess && !row[2].trim().isEmpty()) {//3  Message Language
                        try {
                            if (Integer.parseInt(row[2]) == Defines.LANGUAGE_ENGLISH || Integer.parseInt(row[2]) == Defines.LANGUAGE_ARABIC) {
                                inputModel.setLanguage(Byte.parseByte(row[2]));
                            } else {
//                                CommonLogger.businessLogger.info(fileName + " file has invalid mandatory message language in the record number " + recordCount);
                                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File has invalid mandatory message language")
                                        .put(GeneralConstants.StructuredLogKeys.FILE_NAME, fileName)
                                        .put(GeneralConstants.StructuredLogKeys.RECORD_COUNT, recordCount).build());
                                lineParsingSuccess = false;
                                failureInputModel.setErrorReason("Error in Language");
                            }
                        } catch (Exception e) {
                            failureInputModel.setErrorReason("Error in Language");
//                            CommonLogger.businessLogger.info(fileName + " file has invalid mandatory message language in the record number " + recordCount);
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File has invalid mandatory message language")
                                    .put(GeneralConstants.StructuredLogKeys.FILE_NAME, fileName)
                                    .put(GeneralConstants.StructuredLogKeys.RECORD_COUNT, recordCount).build());
                            lineParsingSuccess = false;
                        }
                    }

                    if (lineParsingSuccess && !row[1].trim().isEmpty()) {//2  Message Script
                        int numOfMsgs = com.asset.contactstrategy.common.utils.Utility.calcConcMsgCount(row[1].length(), Byte.parseByte(row[2]));
                        if (service != null && numOfMsgs > service.getAllowedSMS()) {
//                            CommonLogger.businessLogger.info(fileName + " file has invalid mandatory Message Script in the record number " + recordCount);
                            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File has invalid mandatory message script language")
                                    .put(GeneralConstants.StructuredLogKeys.FILE_NAME, fileName)
                                    .put(GeneralConstants.StructuredLogKeys.RECORD_COUNT, recordCount).build());
                            lineParsingSuccess = false;
                            failureInputModel.setErrorReason("Error in Message Text");
                        } else {
                            inputModel.setConcatNumber(numOfMsgs);
                        }
                        inputModel.setMessageText(row[1]);
                    }

                } else {

//                    CommonLogger.businessLogger.info(fileName + " file has missing mandatory content at record number " + recordCount);
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File has missing mandatory content")
                            .put(GeneralConstants.StructuredLogKeys.FILE_NAME, fileName)
                            .put(GeneralConstants.StructuredLogKeys.RECORD_COUNT, recordCount).build());
                    lineParsingSuccess = false;
                    failureInputModel.setErrorReason("Missing Mandatory Parameters");
                }
                if (lineParsingSuccess && row.length >= 4 && row[3] != null) {
                    inputModel.setMessagePriorityText(row[3].trim());
                }
                if (lineParsingSuccess && row.length >= 4 && row[3] != null && !row[3].trim().isEmpty()) {//4  Message Priority
                    if (row[3].equalsIgnoreCase(Defines.HIGH_PRIORITY)) {
                        inputModel.setMessagePriority(Defines.INTERFACES.MESSAGE_PRIORITY_HIGH);
                    } else if (row[3].equalsIgnoreCase(Defines.NORMAL_PRIORITY)) {
                        inputModel.setMessagePriority(Defines.INTERFACES.MESSAGE_PRIORITY_NORMAL);
                    } else {
//                        CommonLogger.businessLogger.info(fileName + " file has invalid mandatory Message Priority in the record number " + recordCount);
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "File has invalid mandatory message priority")
                                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, fileName)
                                .put(GeneralConstants.StructuredLogKeys.RECORD_COUNT, recordCount).build());
                        lineParsingSuccess = false;
                        failureInputModel.setErrorReason("Error in Message Priority");
                    }
                } else {
                    inputModel.setMessagePriority(Byte.parseByte("0"));
                }

                if (lineParsingSuccess && row.length >= 5 && !row[4].trim().isEmpty()) {
                    inputModel.setOptionalParam1(row[4]);//5  Optional Param1
                }
                if (lineParsingSuccess && row.length >= 6 && !row[5].trim().isEmpty()) {
                    inputModel.setOptionalParam2(row[5]);//6  Optional Param2
                }
                if (lineParsingSuccess && row.length >= 7 && !row[6].trim().isEmpty()) {
                    inputModel.setOptionalParam3(row[6]);//7  Optional Param3
                }
                if (lineParsingSuccess && row.length >= 8 && !row[7].trim().isEmpty()) {
                    inputModel.setOptionalParam4(row[7]);//8   Optional Param4
                }
                if (lineParsingSuccess && row.length >= 9 && !row[8].trim().isEmpty()) {
                    inputModel.setOptionalParam5(row[8]);//9  Optional Param5
                }

                //            if (lineParsingSuccess && row.length >= 10 && !row[9].trim().isEmpty()) {//10  Expiration Duration
                //                try {
                //                    int expiration = Integer.parseInt(row[9]);
                //                    Calendar c = Calendar.getInstance();
                //                    c.add(Calendar.HOUR, expiration);
                //                    inputModel.setExpirationDate(c.getTime());
                //                } catch (Exception e) {
                //                    CommonLogger.businessLogger.debug(fileName + " file has invalid expiration duration" + recordCount);
                //                    lineParsingSuccess = false;
                //                }
                //            }
                if (lineParsingSuccess) {
                    successCount++;
                    smsRows.add(inputModel);
                } else {
                    archiveFailed(failureCount, failureInputModel, row);
                    failureInputModel.setFileName(fileName);
                    failureCount++;
                    //                if (!isShow()) {
                    //                    setShow(true);
                    //                }
                    //                failureCount++;
                    //                failureInputModel.setDestinationMSISDN(row[0]);
                    //                failureInputModel.setLanguageText(row[2]);
                    //                failureInputModel.setMessageText(row[1]);
                    //                failureInputModel.setMessagePriorityText(row[3]);
                    //                if (row.length >= 5) {
                    //                    failureInputModel.setOptionalParam1(row[4]);
                    //                }
                    //                if (row.length >= 6) {
                    //                    failureInputModel.setOptionalParam2(row[5]);
                    //                }
                    //                if (row.length >= 7) {
                    //                    failureInputModel.setOptionalParam3(row[6]);
                    //                }
                    //                if (row.length >= 8) {
                    //                    failureInputModel.setOptionalParam4(row[7]);
                    //                }
                    //                if (row.length >= 9) {
                    //                    failureInputModel.setOptionalParam5(row[8]);
                    //                }
                    ////                failureInputModel.setExpirationDate(null);
                    //                failureSMSRows.add(failureInputModel);
                }
            } catch (Exception e) {
                failureInputModel.setFileName(fileName);
                archiveFailed(failureCount, failureInputModel, row);
                failureCount++;
            } finally {
                recordCount++;
            }
        }

        if (recordCount == uploadedRecordsPerFileLimit) {
            Utility.showWarningMessage(null, Constants.MAXIMUM_REACHED, fileName, uploadedRecordsPerFileLimit + " records. The rest of the records were ignored.");
        }
        model = new SMSBulkFileModel();
        model.setName(fileName);
        model.setTotalRecordCount(recordCount);
        model.setDataRecords(smsRows);
        model.setFailedRecordCount(failureCount);
        model.setSuccessRecordCount(successCount);
        uploadedFiles.add(model);

        return true;
    }

    public void archiveFailed(int failureCount, InputModel failureInputModel, String[] row) {
        if (!isShow()) {
            setShow(true);
        }
        failureInputModel.setDestinationMSISDN(row[0]);
        failureInputModel.setLanguageText(row[2]);
        failureInputModel.setMessageText(row[1]);
        failureInputModel.setMessagePriorityText(row[3]);
        if (row.length >= 5) {
            failureInputModel.setOptionalParam1(row[4]);
        }
        if (row.length >= 6) {
            failureInputModel.setOptionalParam2(row[5]);
        }
        if (row.length >= 7) {
            failureInputModel.setOptionalParam3(row[6]);
        }
        if (row.length >= 8) {
            failureInputModel.setOptionalParam4(row[7]);
        }
        if (row.length >= 9) {
            failureInputModel.setOptionalParam5(row[8]);
        }
//                failureInputModel.setExpirationDate(null);
        failureSMSRows.add(failureInputModel);
    }

    public String cancel() {
        CommonLogger.businessLogger.info("canceling SMS bulk upload and returning to home page in cancel method");
        return "" + nextPg + "?faces-redirect=true";
    }

    public void handleServiceFilled() {
        if (serviceName != null && !serviceName.trim().isEmpty()) {
            serviceFilled = true;
        } else {
            serviceFilled = false;
        }
    }

    public boolean validateOriginatorValue() {

        if (originatorValue == null) {//Esmail.Anbar | CS_Phase_1.5 | Supporting if Originator Value is Null
            Utility.showErrorMessage(null, Constants.MISSING_DATA);
            return false;
        } else if (originatorType != null) {
            if (!com.asset.contactstrategy.common.utils.Utility.checkOrignatorType(originatorValue, originatorType)) {
                Utility.showErrorMessage(null, Constants.VALIDATE_ORIGINATOR_LENGTH);
                return false;
            }
        }
//            else 
//        }
        return true;
    }

    public void handleOriginatorType() {
        if (originatorType != null) {
            originatorTypeFilled = true;
            if (originatorValue != null) {
                if (!com.asset.contactstrategy.common.utils.Utility.checkOrignatorType(originatorValue, originatorType)) {
                    originatorType = null;
                    Utility.showErrorMessage(null, Constants.VALIDATE_ORIGINATOR_LENGTH);
                }
            } else {
                Utility.showErrorMessage(null, Constants.MISSING_DATA);
            }
        } else {
            originatorTypeFilled = false;

        }
    }

    public int getUploadSizeLimit() {
        return uploadSizeLimit;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ArrayList<ServiceWebModel> getServiceList() throws CommonException {
        retrieveApplicationServiceList();
        return serviceList;
    }

    public void setServiceList(ArrayList<ServiceWebModel> serviceList) {
        this.serviceList = serviceList;
    }

    public Boolean getDoNotApplyFlag() {
        return doNotApplyFlag;
    }

    public void setDoNotApplyFlag(Boolean doNotApplyFlag) {
        this.doNotApplyFlag = doNotApplyFlag;
    }

    public Integer getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(Integer originatorType) {
        this.originatorType = originatorType;
    }

    public ArrayList<OriginatorTypeModel> getOriginatorTypes() {
        return originatorTypes;
    }

    public void setOriginatorTypes(ArrayList<OriginatorTypeModel> originatorTypes) {
        this.originatorTypes = originatorTypes;
    }

    public String getNextPg() {
        return nextPg;
    }

    public void setNextPg(String nextPg) {
        this.nextPg = nextPg;
    }

    public int getUploadedFilesLimit() {
        return uploadedFilesLimit;
    }

    public void setUploadedFilesLimit(int uploadedFilesLimit) {
        this.uploadedFilesLimit = uploadedFilesLimit;
    }

    public int getUploadedRecordsPerFileLimit() {
        return uploadedRecordsPerFileLimit;
    }

    public void setUploadedRecordsPerFileLimit(int uploadedRecordsPerFileLimit) {
        this.uploadedRecordsPerFileLimit = uploadedRecordsPerFileLimit;
    }

    public ArrayList<SMSBulkFileModel> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(ArrayList<SMSBulkFileModel> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public boolean isEditableService() {
        return editableService;
    }

    public void setEditableService(boolean editableService) {
        this.editableService = editableService;
    }

    public boolean getStartUploading() {
        return originatorMSISDNFilled && originatorTypeFilled && serviceFilled;
    }

    public int getMsisdnLength() {
        return msisdnLength;
    }

    /**
     * @return the originatorValues
     */
    public HashMap<Integer, LookupModel> getOriginatorValues() {
        return originatorValues;
    }

    /**
     * @param originatorValues the originatorValues to set
     */
    public void setOriginatorValues(HashMap<Integer, LookupModel> originatorValues) {
        this.originatorValues = originatorValues;
    }

    /**
     * @return the originatorValue
     */
    public String getOriginatorValue() {
        return originatorValue;
    }

    /**
     * @param originatorValue the originatorValue to set
     */
    public void setOriginatorValue(String originatorValue) {
        this.originatorValue = originatorValue;
    }

    /**
     * @return the originatorMaxLength
     */
    public int getOriginatorMaxLength() {
        return originatorMaxLength;
    }

    /**
     * @param originatorMaxLength the originatorMaxLength to set
     */
    public void setOriginatorMaxLength(int originatorMaxLength) {
        this.originatorMaxLength = originatorMaxLength;
    }

    /**
     * @return the failureSMSRows
     */
    public ArrayList<InputModel> getFailureSMSRows() {
        return failureSMSRows;
    }

    /**
     * @param failureSMSRows the failureSMSRows to set
     */
    public void setFailureSMSRows(ArrayList<InputModel> failureSMSRows) {
        this.failureSMSRows = failureSMSRows;
    }

    /**
     * @return the show
     */
    public boolean isShow() {
        return show;
    }

    /**
     * @param show the show to set
     */
    public void setShow(boolean show) {
        this.show = show;
    }

    public String getServicePassword() {
        return servicePassword;
    }

    public void setServicePassword(String servicePassword) {
        this.servicePassword = servicePassword;
    }

}
