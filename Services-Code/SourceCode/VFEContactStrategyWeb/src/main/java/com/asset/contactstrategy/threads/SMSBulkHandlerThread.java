/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.threads;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.SMSBulkFileModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.interfaces.models.InputModel;
import java.util.ArrayList;


/**
 *
 * @author hazem.fekry
 */
public class SMSBulkHandlerThread extends Thread {

    private final SMSBulkFileModel fileModel;
    private final MainService mainService;

    public SMSBulkHandlerThread(SMSBulkFileModel fileModel) {
        this.fileModel = fileModel;
        mainService = new MainService();
    }

    public SMSBulkFileModel getFileModel() {
        return fileModel;
    }

    @Override
    public void run() {
        CommonLogger.businessLogger.info("SMSBulkHandlerThread Started");
        Thread.currentThread().setName("HandlerThread:"+fileModel.getName());
        if (fileModel != null) {
            Boolean bulkEndingStatus = false;
            try {
                handleFileModelInsertion();
                bulkEndingStatus = handleSavingRecords();
                handleFileModelStatus(bulkEndingStatus);
        CommonLogger.businessLogger.info("SMSBulkHandlerThread Ended");
            } catch (CommonException ex) {
                fileModel.setStatus(SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.FAILED).getId());
                CommonLogger.businessLogger.debug("exception at SMSBulkHandlerThread ---->"+ex.getErrorMsg());
                CommonLogger.errorLogger.error("exception at SMSBulkHandlerThread ---->",ex);
            }
        }
    }

    private void handleFileModelInsertion() throws CommonException {
        fileModel.setStatus(SystemLookups.FILE_STATUS.get(3).getId());
        mainService.insertSMSBulkFile(fileModel);
    }

    private Boolean handleSavingRecords() throws CommonException {
        //handle failure to return false
//        for(){
//        
//        }
        int batchSize =Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SMS_BULK_MAX_BATCH_SIZE));
        int startIndex=0;
        
        ArrayList<InputModel> dataRecords = fileModel.getDataRecords();
        ArrayList<InputModel> subList;
        while(startIndex+batchSize<=dataRecords.size()){
            subList=new ArrayList<>(dataRecords.subList(startIndex, batchSize+startIndex));
            mainService.insertSMSBulkRecords(subList,fileModel.getName());
            startIndex+=batchSize;
        }
        if(startIndex<dataRecords.size()){
            subList=new ArrayList<>(dataRecords.subList(startIndex, dataRecords.size()));
            mainService.insertSMSBulkRecords(subList,fileModel.getName());
        }

        return true;
    }

    private void handleFileModelStatus(Boolean bulkStatus) throws CommonException {
  
        if (bulkStatus) {
            fileModel.setStatus(SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.FINISHED).getId());
        } else {
            fileModel.setStatus(SystemLookups.FILE_STATUS.get(Defines.LK_FILE_STATUS.FAILED).getId());
        }
        mainService.updateSMSBulkFileStatus(fileModel);
    }

}
