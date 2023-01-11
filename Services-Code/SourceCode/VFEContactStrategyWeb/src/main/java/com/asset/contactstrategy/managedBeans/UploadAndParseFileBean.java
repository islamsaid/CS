/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author kerollos.asaad
 */
@ManagedBean(name = "uploadAndParseFileBean")
@SessionScoped
public class UploadAndParseFileBean {

    private ArrayList<FileModel> groupFiles = new ArrayList<FileModel>();
    private int uploadSizeLimit = 5; //initially only
    private static final int MEGABYTE_CONVERSION = 1000000;
    private String temp = new String();
    private Boolean counterReachedMax = false;
    private Boolean hasInvalidContent = false;

    public UploadAndParseFileBean() throws CommonException {
        MainService mainservice = new MainService();
        this.temp = mainservice.getSystemPropertyByKey(Defines.UPLOAD_FILE_SIZE_LIMIT_KEY, GeneralConstants.SRC_ID_WEB_INTERFACE);
        if (temp != null && !temp.equals("")) {
            uploadSizeLimit = Integer.parseInt(temp) * MEGABYTE_CONVERSION;//bytes to megabytes
        }
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
//        CommonLogger.businessLogger.debug(UploadAndParseFileBean.class.getName() + " || " + "Starting handleFileUpload() ");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting handleUploadFile()")
                .build());
//        CommonLogger.businessLogger.debug("Uploading File : " + event.getFile().getFileName());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Uploading File")
                .put(GeneralConstants.StructuredLogKeys.FILE_NAME, event.getFile().getFileName()).build());
        if (temp != null && !temp.equals("")) {
            uploadSizeLimit = Integer.parseInt(temp);
        }
        FacesMessage msg = new FacesMessage(event.getFile().getFileName() + " is successfully uploaded.", event.getFile().getFileName() + " is uploaded.");
        Boolean nameExist = false;
        for (FileModel file : this.groupFiles) {
            if (file.getFileName().equals(event.getFile().getFileName())) {
                nameExist = true;
            }
        }
        if (nameExist) {
            Utility.showErrorMessage(null, Constants.UNIQUE_NAME_ERROR, "File with name ", event.getFile().getFileName());
            return;
        }
        FileModel wl = new FileModel();
        String data = this.parseFiles(event.getFile().getContents());
        wl.setFileData(data);
        File f = new File(event.getFile().getFileName());
        wl.setFileName(f.getName());
        wl.setFileName(extractFileName(wl.getFileName()));
        if (counterReachedMax) {
            Utility.showInfoMessage(null, Constants.MAXIMUM_REACHED, wl.getFileName(), Constants.MAX_NO_OF_RECORD_IN_FILE + " so the rest of the records will be ignored ");
        }
        if (hasInvalidContent && data.equals("")) {//File has single invalid msisdn
            Utility.showErrorMessage(null, Constants.INVALID_CONTENT, wl.getFileName(), " file has invalid format");
            return;
        } else if (hasInvalidContent) {
            Utility.showInfoMessage(null, Constants.INVALID_CONTENT, wl.getFileName(), " file has invalid records");
        }
        if (data.equals("")) {//File is empty
            Utility.showErrorMessage(null, Constants.EMPTY, wl.getFileName());
            return;
        }
        if (this.groupFiles == null) {
            this.groupFiles = new ArrayList<FileModel>();
        }

        this.groupFiles.add(wl);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void removeFile(FileModel file) {
        if (this.getGroupFiles() != null) {
            this.getGroupFiles().remove(file);
        }
    }

    public String parseFiles(byte[] csvFile) throws IOException {
        String methodName = "parseFiles";
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        this.hasInvalidContent = Boolean.FALSE;
        StringBuilder files = new StringBuilder();
        Integer recordCounter = 0;
        this.counterReachedMax = Boolean.FALSE;
        try {

            ByteArrayInputStream stream = new ByteArrayInputStream(csvFile);
            InputStream is = stream;
            br = new BufferedReader(new InputStreamReader(is));
            while (((line = br.readLine()) != null) && (!this.counterReachedMax)) {
                // use comma as separator
                String[] msisdn = line.split(csvSplitBy);
                for (int i = 0; ((i < msisdn.length) && (!this.counterReachedMax)); i++) {
                    String value = "";
                    try {//A) VALIDATE MSISDN
                        //CSPhase1.5 | Esmail.Anbar | Updating Code to use new Validation Function
                        value = com.asset.contactstrategy.common.utils.Utility.validateMSISDNFormatSMPP(msisdn[i].trim());
                    } catch (CommonException invalidmsisdn) {
                        this.hasInvalidContent = Boolean.TRUE;
                        CommonLogger.errorLogger.error(UploadAndParseFileBean.class.getName() + " || "
                                + "Caught invalid MSISDN with value" + msisdn[i]);
                        continue;
                    }
                    recordCounter++;//B) DONE ? APPEND THE VALUE
                    files.append(msisdn[i] + ",");
                    if (recordCounter.equals(Constants.MAX_NO_OF_RECORD_IN_FILE)) {//C) CHECK MAXIMUM APPENDENCE
                        this.counterReachedMax = Boolean.TRUE;
                    }
                }
            }
            if ((files.toString().equals("")) && (this.hasInvalidContent == Boolean.FALSE)) {//File Is Empty
                files.append("");
            } else if ((files.toString().equals("")) && (this.hasInvalidContent == Boolean.TRUE)) {//File has invalid single msisdn
                files.append("");
            } else {
                files = files.deleteCharAt(files.length() - 1);
            }
        } catch (FileNotFoundException e) {
            CommonLogger.errorLogger.error(UploadAndParseFileBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + e);
        } catch (IOException e) {
            CommonLogger.errorLogger.error(UploadAndParseFileBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + e);
        } catch (Exception e) {
            CommonLogger.errorLogger.error(UploadAndParseFileBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    CommonLogger.errorLogger.error(UploadAndParseFileBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + e);
                }
            }
            return files.toString();
        }
    }

    public String retrieveUploadFileSizeLimit() {
        String stringValue = "";
        stringValue = String.valueOf(this.uploadSizeLimit);
        //String temp = (String) Utility.getDbConfig(Defines.ADM_SYSTEM_PROPERTIES.UPLOAD_FILE_SIZE_LIMIT);
        //String temp = "15";
        if (temp != null && !temp.equals("")) {
            this.uploadSizeLimit = Integer.parseInt(temp) * MEGABYTE_CONVERSION;
            stringValue = String.valueOf(this.uploadSizeLimit);

        }
        return stringValue;
    }

    //================================== Setters &Getters=======================================
    public int getUploadSizeLimit() {
        return uploadSizeLimit;
    }

    public void setUploadSizeLimit(int uploadSizeLimit) {
        this.uploadSizeLimit = uploadSizeLimit;
    }

    public ArrayList<FileModel> getGroupFiles() {
        return this.groupFiles;
    }

    public void setGroupFiles(ArrayList<FileModel> groupFiles) {
        this.groupFiles = groupFiles;
    }

    public static String extractFileName(String filePathName) {
        if (filePathName == null) {
            return null;
        }

        int slashPos = filePathName.lastIndexOf('\\');
        if (slashPos == -1) {
            slashPos = filePathName.lastIndexOf('/');
        }

        return filePathName.substring(slashPos > 0 ? slashPos + 1 : 0);
    }
}
