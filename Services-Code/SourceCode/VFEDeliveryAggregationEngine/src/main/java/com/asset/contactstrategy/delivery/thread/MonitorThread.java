/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.thread;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.delivery.manager.JobManager;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author kerollos.asaad
 */
public class MonitorThread implements Runnable {

    private int MONITOR_THREAD_SLEEP_TIME;
//    String threadName;
//    
//    public MonitorThread(String threadName)
//    {
//        this.threadName = threadName;
//    }

    @Override
    public void run() {
        Thread.currentThread().setName("MonitorThread");
//        CommonLogger.businessLogger.info("******************" + Thread.currentThread().getName() + " Has Started***************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + " has Ended").build());

//        StringBuilder monitorLog = new StringBuilder();
//        try {
//            System.out.println("Sleeping Monitor Thread");
//            Thread.sleep(6000000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(MonitorThread.class.getName()).log(Level.SEVERE, null, ex);
//        }
        while (!JobManager.ENGINE_SHUTDOWN_FLAG.get()) {
            MONITOR_THREAD_SLEEP_TIME = Integer.valueOf(JobManager.getInstance().getSystemProperties().get(Defines.DELIVERY_AGGREGATION_PROPERTIES.MONITOR_THREAD_SLEEP_TIME));
            //CommonLogger.businessLogger.info("***************************************Monitoring Started*******************************************");
            try {
//                monitorLog = new StringBuilder();
//                monitorLog.append("***************************************Monitoring Started*******************************************")
//                          .append("\n ##### Heap utilization statistics [MB] #######")
//                          .append("\n [MonitoringThread] Used Memory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Free Memory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Total Memory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
//                          .append("\n [MonitoringThread] Max Memory:" + (Runtime.getRuntime().maxMemory() / (1024 * 1024)));

                //.append("\n ###############################################")*
//                monitorLog//.append("\n ###############################################")
//                        //.append("\n #### Enqueue ").append(queue.getValue().getAppName()).append(" Data")
//                        .append("\n Update Status Java Queue Size: ").append(JobManager.getInstance().getJobQueue().size())
//                        .append(" || Queue Remaining Capacity: ").append(JobManager.getInstance().getJobQueue().remainingCapacity())
//                        .append("\n Timeout Status Java Queue Size: ").append(JobManager.getInstance().getJobTimeOutQueue().size())
//                        .append(" || Queue Remaining Capacity: ").append(JobManager.getInstance().getJobTimeOutQueue().remainingCapacity())
//                        //.append("\n ShutDownFlag Status: ").append(ConfigurationManager.shutdownFlagPool.get(queue.getKey()))
//
//                        .append("\n Update Thread Pool Active Count: ").append(((ThreadPoolExecutor) JobManager.updateThreadsPool).getActiveCount())
//                        .append(" || Current Pool Size: ").append(((ThreadPoolExecutor) JobManager.updateThreadsPool).getPoolSize())
//                        //.append(" || Largest Pool Size: ").append(((ThreadPoolExecutor) JobManager.updateThreadsPool).getLargestPoolSize())
//
//                        .append("\n TimeOut Thread Pool Active Count: ").append(((ThreadPoolExecutor) JobManager.timeOutPool).getActiveCount())
//                        .append(" || Current Pool Size: ").append(((ThreadPoolExecutor) JobManager.timeOutPool).getPoolSize())
//                        //.append(" || Largest Pool Size: ").append(((ThreadPoolExecutor) JobManager.timeOutPool).getLargestPoolSize())
//
//                        .append("\n DatabasePool Stats \n")
//                        //                          .append(Utility.getC3P0ConnectionPoolStats(DataSourceManger.getC3p0ConnectionPool()))
//                        .append(Utility.getHikariConnectionPoolStats(DataSourceManger.getHikariDataSourse()))
//                        .append("\n ###############################################");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Started. Heap Utilization Statistics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());
//                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Update Queue Stats")
//                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, JobManager.getInstance().getJobQueue().size())
//                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, JobManager.getInstance().getJobQueue().remainingCapacity()).build());
//                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "TimeOut Queue Stats")
//                        .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, JobManager.getInstance().getJobTimeOutQueue().size())
//                        .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, JobManager.getInstance().getJobTimeOutQueue().remainingCapacity()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updater ThreadPool Stats")
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) JobManager.updaterPool).getActiveCount())
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) JobManager.updaterPool).getPoolSize()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Decider ThreadPool Stats")
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) JobManager.deciderPool).getActiveCount())
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) JobManager.deciderPool).getPoolSize()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Selector ThreadPool Stats")
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) JobManager.selectorPool).getActiveCount())
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) JobManager.selectorPool).getPoolSize()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "TimeOut ThreadPool Stats")
                        .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, ((ThreadPoolExecutor) JobManager.timeOutPool).getActiveCount())
                        .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, ((ThreadPoolExecutor) JobManager.timeOutPool).getPoolSize()).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Database Pool Stats")
                        .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, Utility.getHikariConnectionPoolStats(DataSourceManger.getHikariDataSourse())).build());
//                if (((JobManager.getInstance().getJobQueue().size() / 
//                    Integer.valueOf(Initializer.propertiesFileBundle.getString(Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_QUEUE_SIZE))) * 100 > 
//                    Double.valueOf(JobManager.getInstance().getSystemProperties().get(Defines.DELIVERY_AGGREGATION_PROPERTIES.JAVA_QUEUE_MAX_PERCENTAGE))))
//                {
//                    HandleExceptionService.sendMOM(new CommonException("Update Job Queue Exceeded Critical Size Percentage", 
//                            ErrorCodes.JAVA_QUEUE_EXCEEDS_PERCENTAGE), GeneralConstants.SRC_ID_DELIVERY_AGGREGATION, 
//                            Integer.valueOf(JobManager.getInstance().getSystemProperties().get(Defines.JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY)),
//                            "");
//                }
//                
//                if (((JobManager.getInstance().getJobTimeOutQueue().size() / 
//                    Integer.valueOf(Initializer.propertiesFileBundle.getString(Defines.DELIVERY_AGGREGATION_PROPERTIES.TIMEOUT_POOL_QUEUE_SIZE))) * 100 > 
//                    Double.valueOf(JobManager.getInstance().getSystemProperties().get(Defines.DELIVERY_AGGREGATION_PROPERTIES.JAVA_QUEUE_MAX_PERCENTAGE))))
//                {
//                    HandleExceptionService.sendMOM(new CommonException("Timeout Job Queue Exceeded Critical Size Percentage", 
//                            ErrorCodes.JAVA_QUEUE_EXCEEDS_PERCENTAGE), GeneralConstants.SRC_ID_DELIVERY_AGGREGATION, 
//                            Integer.valueOf(JobManager.getInstance().getSystemProperties().get(Defines.JAVA_QUEUES_EXCEEDS_MOM_ERROR_SEVERITY)),
//                            "");
//                }
//                monitorLog.append("***************************************Monitoring Ended*********************************************");
//                CommonLogger.businessLogger.info(monitorLog.toString());
            } catch (Exception e) {
//                monitorLog.append("\n Error Reading The Rest of the Data " + e)
//                        .append("\n ###############################################")
//                        .append("***************************************Monitoring Ended*********************************************");
//                CommonLogger.businessLogger.info(monitorLog.toString());
                CommonLogger.errorLogger.error("Error Reading Data: " + e + " || " + e.getMessage(), e);
            }

            //CommonLogger.businessLogger.info("***************************************Monitoring Ended*********************************************");
            try {
                Thread.sleep(MONITOR_THREAD_SLEEP_TIME);
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error("Monitor Thread Caught InterruptedException---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught InterruptedException---->" + e, e);
            } catch (Exception e) {
                CommonLogger.businessLogger.error("Monitor Thread Caught Exception---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught Exception---->" + e, e);
            }
        }
//        CommonLogger.businessLogger.info("******************" + Thread.currentThread().getName() + " Has Finished***************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + " has Ended").build());
    }
}
