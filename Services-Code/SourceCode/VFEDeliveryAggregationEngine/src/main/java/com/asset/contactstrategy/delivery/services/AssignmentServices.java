/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.services;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.models.JobModel;
import com.asset.contactstrategy.delivery.task.SelectorWorker;
import com.asset.contactstrategy.delivery.task.TimeoutWorker;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author esmail.anbar
 */
public class AssignmentServices {

    public static void assignUpdateJobs() throws Exception {
        CommonLogger.businessLogger.debug("AssignmentServices.assignUpdateJobs() Invoked...");
        MainService mainService = new MainService();
//        try {
        ThreadPoolExecutor selectorPool = JobManager.getInstance().getSelectorPool();

        Map<String, String> systemProperties
                = JobManager.getInstance().getSystemProperties();

        int maxMODX = Defines.MOD_X;
        int daysBeforeTimeOut = Integer.parseInt(systemProperties.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.DAYS_BEFORE_SMS_TIMEOUT));
        JobModel jobModel;

        //Get today's Date
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(mainService.getCurrentDatabaseTime().getTime());
        String date = dateFormat.format(cal.getTime());
        String toDate = dateFormat.format(new Date(cal.getTimeInMillis() + 86400000));

        for (int i = 0; i < daysBeforeTimeOut; i++) {
            for (int j = 0; j < maxMODX; j++) {
                jobModel = new JobModel();
                jobModel.setModX(j);
                jobModel.setDate(date);
                jobModel.setToDate(toDate);
                jobModel.setDaysBeforeTimeOut(daysBeforeTimeOut);
//                    jobQueue.put(jobModel);
                selectorPool.execute(new SelectorWorker(jobModel));
                JobManager.updateJobsInitialized = true;
                //CommonLogger.businessLogger.info("Update Value Added= " + j + ":" + date + ":" + daysBeforeTimeOut);
            }

            //Going a day earilier from the given date
            cal.add(Calendar.DATE, -1);
            date = dateFormat.format(cal.getTime());
            toDate = dateFormat.format(new Date(cal.getTimeInMillis() + 86400000));
        }
        CommonLogger.businessLogger.debug("AssignmentServices.assignUpdateJobs() Ended...");
//        } catch (Exception e) {
//            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
//            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
//            throw new CommonException(e + " || " + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//        }

    }

    public static void assignTimeOutJobs(Connection conn) throws CommonException {
        CommonLogger.businessLogger.debug("AssignmentServices.assignTimeOutJobs() Invoked...");
        MainService mainService = new MainService();
        try {
            ThreadPoolExecutor timeoutExecutor = JobManager.getInstance().timeOutPool;

            Map<String, String> systemProperties
                    = JobManager.getInstance().getSystemProperties();

            int maxMODX = Defines.MOD_X;
            int daysBeforeTimeOut = Integer.parseInt(systemProperties.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.DAYS_BEFORE_SMS_TIMEOUT));
            int maxTimeOutDays = Integer.parseInt(systemProperties.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.MAX_TIMEOUT_DAYS));
            JobModel jobModel;

            //Get today's Date
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-YY");
            Calendar cal = Calendar.getInstance();

            cal.setTimeInMillis(mainService.getCurrentDatabaseTime().getTime());
            cal.add(Calendar.DATE, -daysBeforeTimeOut);

            String date = dateFormat.format(cal.getTime());

            for (int i = 0; i < maxTimeOutDays; i++) {
                for (int j = 0; j < maxMODX; j++) {
                    jobModel = new JobModel();
                    jobModel.setModX(j);
                    jobModel.setDate(date);
                    jobModel.setDaysBeforeTimeOut(daysBeforeTimeOut);
                    timeoutExecutor.execute(new TimeoutWorker(jobModel));
                    JobManager.timeOutJobsInitialized = true;
//                    CommonLogger.businessLogger.info("TimeOut Value Added= " + j + ":" + date + ":" + daysBeforeTimeOut);
//                    CommonLogger.businessLogger.info(JobManager.timeOutJobsInitialized + " || " + JobManager.updateJobsInitialized);
                }

                //Going a day earilier from the given date
                cal.add(Calendar.DATE, -1);
                date = dateFormat.format(cal.getTime());
            }
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
            throw new CommonException(e + " || " + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        CommonLogger.businessLogger.debug("AssignmentServices.assignTimeOutJobs() Ended...");
    }

}
