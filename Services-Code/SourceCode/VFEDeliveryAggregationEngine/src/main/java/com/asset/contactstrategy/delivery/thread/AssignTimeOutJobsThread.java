/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.thread;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.delivery.db.facade.PartitionManagerFacade;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.services.DeliveryAggregationServices;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author esmail.anbar
 */
public class AssignTimeOutJobsThread implements Runnable {

//    String threadName;
//    
//    public AssignTimeOutJobsThread(String threadName)
//    {
//        this.threadName = threadName;
//    }
    @Override
    public void run() {
        Thread.currentThread().setName("AssignTimeOutJobsThread");
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");

//        DeliveryAggregationServices deliveryAggregationServices = new DeliveryAggregationServices();
//        int sleepTime = Integer.valueOf(Initializer.propertiesFileBundle.getString
//            (Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_AFTER_JOBS_UPDATE));
        MainService mainService = new MainService();
        long timeToSleep;

        while (!JobManager.ENGINE_SHUTDOWN_FLAG.get()) {
            try {
                DeliveryAggregationServices.assignTimeOutJobs();

                if (Defines.DELIVERY_AGGREGATION_PROPERTIES.AUTOMATICALLY_DROP_CONCAT_PARTITIONS_VALUE) {
                    Date date = null;
                    try {
                        CommonLogger.businessLogger.info("Automatically drop concat partitions is set to true...");

                        Calendar cInstance = Calendar.getInstance();
                        cInstance.setTimeInMillis(mainService.getCurrentDatabaseTime().getTime());

                        Map<String, String> systemProperties = JobManager.getInstance().getSystemProperties();

                        int daysBeforeTimeOut = Integer.parseInt(systemProperties.get(Defines.DELIVERY_AGGREGATION_PROPERTIES.DAYS_BEFORE_SMS_TIMEOUT));

                        cInstance.add(Calendar.DATE, -daysBeforeTimeOut);
                        cInstance.set(Calendar.HOUR_OF_DAY, 0);
                        cInstance.set(Calendar.MINUTE, 0);
                        cInstance.set(Calendar.SECOND, 0);
                        cInstance.set(Calendar.MILLISECOND, 0);

                        date = cInstance.getTime();
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Attempting to drop conact history table partitions older than")
                                .put(GeneralConstants.StructuredLogKeys.DATE, date).build());
                        PartitionManagerFacade.dropPartitionsOlderThan(DBStruct.VFE_CS_SMS_CONCAT_H.TABLE_NAME, date);
                    } catch (Exception e) {
                        CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Failed to drop conact history table partitions older than")
                                .put(GeneralConstants.StructuredLogKeys.DATE, date).build(), e);
                    }
                }

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(mainService.getCurrentDatabaseTime().getTime());

                Timestamp currentTimeStamp = new Timestamp(c.getTimeInMillis());

                c.add(Calendar.DATE, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);

                Timestamp futureTimeStamp = new Timestamp(c.getTimeInMillis());

                timeToSleep = futureTimeStamp.getTime() - currentTimeStamp.getTime();
//                CommonLogger.businessLogger.info("Sleeping for " + timeToSleep + " milliseconds till 12:00am Database Time next day || " 
//                        + currentTimeStamp + " | " + futureTimeStamp);
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "AssignTimeOutJobsThread is sleeping till 12:00am Database Time next day")
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, timeToSleep)
                        .put(GeneralConstants.StructuredLogKeys.CURRENT_TIME, currentTimeStamp)
                        .put(GeneralConstants.StructuredLogKeys.FUTURE_TIME, futureTimeStamp).build());

                while (timeToSleep > 0) {
                    //CommonLogger.businessLogger.info("Time to Sleep: " + timeToSleep);
                    Thread.sleep(120000);
                    if (Defines.STRING_TRUE.equalsIgnoreCase(JobManager.ENGINE_SHUTDOWN_FLAG.toString())) {
                        break;
                    }
                    timeToSleep -= 120000;
                }
            } catch (CommonException e) {
                CommonLogger.businessLogger.error(e + " || " + e.getErrorMsg());
                CommonLogger.errorLogger.error(e + " || " + e.getErrorMsg(), e);
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e + " || " + e.getMessage());
                CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
            }
        }
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }

}
