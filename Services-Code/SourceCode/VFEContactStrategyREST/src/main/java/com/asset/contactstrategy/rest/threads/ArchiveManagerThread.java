/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.threads;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSHistoryModel;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author esmail.anbar
 */
public class ArchiveManagerThread implements Runnable {

    private ArrayList<SMSHistoryModel> msgs;
    private SMSHistoryModel model;
    static int MAX_ARCHIVING_DB_ARRAY_SIZE = Defines.INTERFACES.MAX_ARCHIVING_DB_ARRAY_SIZE_VALUE;
    static int MAX_NUM_OF_RETRIES_ATHREAD = Defines.INTERFACES.MAX_NUM_OF_RETRIES_ATHREAD_VALUE;
    int threadNo;
    static int pullTimeOut;

    public ArchiveManagerThread(int threadNo) 
    {
        this.threadNo = threadNo;
    }
    
    @Override
    public void run() 
    {
        Thread.currentThread().setName("ArchivingThread_" + threadNo);
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
        pullTimeOut = Defines.INTERFACES.ARCHIVING_THREAD_PULL_TIMEOUT_VALUE;
        MainService mainService = new MainService();
        while(!ConfigurationManager.SHUTDOWN_FLAG || !ConfigurationManager.messagesToBeArchived.isEmpty()
            || ConfigurationManager.concurrentRequests.get() > 0 || !ConfigurationManager.smsToBeValidated.isEmpty())
        {
            try 
            {
                for (int i = 0; i < MAX_ARCHIVING_DB_ARRAY_SIZE; i++)
                {
                    if (ConfigurationManager.messagesToBeArchived != null)
                    {
                        if (msgs == null || msgs.isEmpty()) {
                            model = ConfigurationManager.messagesToBeArchived.poll(pullTimeOut, TimeUnit.MILLISECONDS);
                        } else {
                            model = ConfigurationManager.messagesToBeArchived.poll();

                        }
                        if (model == null)
                            break;
                        else
                        {
                            if (msgs == null)
                                msgs = new ArrayList<>();
                            msgs.add(model);
                            continue;
                        } 
                    }
                }
                if (msgs == null || msgs.isEmpty())
                        continue;
                else
                {
                    for (int i = 0; i < MAX_NUM_OF_RETRIES_ATHREAD; i++)
                    {
                        try
                        {
                            CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Try no: " + i);
                            mainService.archiveMessage(msgs);
                            break;
                        }
                        catch(CommonException ce)
                        {
                            CommonLogger.businessLogger.error(ce.getErrorMsg());
                            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
                        }
                        catch(Exception e)
                        {
                            CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
                            CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
                        }
                    }
                    msgs.clear();
                }
            }
            catch (InterruptedException e) 
            {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
            catch (Exception e)
            {
                CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
                CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
            }
        }
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended || Queue Size: " + ConfigurationManager.messagesToBeArchived.size() 
//                + " || Queue Remaining Capacity: " + ConfigurationManager.messagesToBeArchived.remainingCapacity() 
//                + " || PullTimeOut: " + pullTimeOut + " || MAX_NUM_OF_RETRIES_ATHREAD: " + MAX_NUM_OF_RETRIES_ATHREAD + " || MAX_ARCHIVING_DB_ARRAY_SIZE: "
//                + MAX_ARCHIVING_DB_ARRAY_SIZE);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName()+" has Ended")
        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.messagesToBeArchived.size())
        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.messagesToBeArchived.remainingCapacity())
        .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, pullTimeOut)
        .put(GeneralConstants.StructuredLogKeys.THREAD_MAX_RETRIES, MAX_NUM_OF_RETRIES_ATHREAD)
        .put(GeneralConstants.StructuredLogKeys.MAX_LOGGING_DB_ARRAY_SIZE, MAX_ARCHIVING_DB_ARRAY_SIZE).build());
        //CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }
    
}
