/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.Threads;

import com.asset.CS.BridgingDequeuerService.beans.ManagerBean;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;

/**
 *
 * @author mostafa.kashif
 */
public class MonitorThread extends Thread {

    @Override
    public void run() {

        Thread.currentThread().setName("Monitoring Thread");
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
//        StringBuilder monitorLog = new StringBuilder();
        while (!ManagerBean.DEQUEUER_SERVICE_SHUTFOWN_FLAG.get()) {
//            monitorLog = new StringBuilder();
            try {
//                monitorLog.append("***************************************Monitoring Started*********************************************");
//                monitorLog.append("\n ##### Heap utilization statistics [MB] #######")
//                        .append("\n [MonitoringThread] Used Memory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
//                        .append("\n [MonitoringThread] Free Memory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
//                        .append("\n [MonitoringThread] Total Memory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
//                        .append("\n [MonitoringThread] Max Memory:" + (Runtime.getRuntime().maxMemory() / (1024 * 1024)))
//                        .append("\n ###############################################");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Started"));
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Heap utilization Statisics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());

//                monitorLog.append("\n SMSs Rest Controller Success Count= " + ManagerBean.smsControllerSuccessCount + ",Failure Count= " + ManagerBean.smsControllerFailureCount + ",Total Count= " + ManagerBean.smsControllerTotalCount);
//                monitorLog.append("\n Liveness Controller Success Count= " + ManagerBean.livenessControllerSuccessCount + ",Failure Count= " + ManagerBean.livenessControllerFailureCount + ",Total Count= " + ManagerBean.livenessControllerTotalCount);
//                monitorLog.append("\n Readiness Controller Success Count= " + ManagerBean.readinessControllerSuccessCount + ",Failure Count= " + ManagerBean.readinessControllerFailureCount + ",Total Count= " + ManagerBean.readinessControllerTotalCount);
//                monitorLog.append("\n Application Concurrent Count= " + ManagerBean.concurrentCount);
//                monitorLog.append("\n***************************************Monitoring Finished*********************************************");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Rest Controller Statistics")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ManagerBean.smsControllerSuccessCount)
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ManagerBean.smsControllerFailureCount)
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_COUNT, ManagerBean.smsControllerTotalCount).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Liveness Rest Controller Statistics")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ManagerBean.livenessControllerSuccessCount)
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ManagerBean.livenessControllerFailureCount)
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_COUNT, ManagerBean.livenessControllerTotalCount).build());

                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Readiness Rest Controller Statistics")
                        .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ManagerBean.readinessControllerSuccessCount)
                        .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ManagerBean.readinessControllerFailureCount)
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_COUNT, ManagerBean.readinessControllerTotalCount).build());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Application Rest Controller Statistics")
                        .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount).build());
                CommonLogger.businessLogger.info(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Finished");

//                CommonLogger.businessLogger.info(monitorLog.toString());
            } catch (Exception e) {
//                monitorLog.append("***************************************Monitoring Finished*********************************************");
//                CommonLogger.businessLogger.info(monitorLog.toString());
                CommonLogger.businessLogger.error("Monitor Thread Caught Exception---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught Exception---->" + e, e);
                CommonLogger.businessLogger.info(GeneralConstants.StructuredLogKeys.MESSAGE, "Monitoring Finished");
            }
            try {

                Thread.sleep(Long.valueOf(ManagerBean.systemPropertiesMap.get(Defines.DequeuerRestWebService.MONITOR_THREAD_SLEEP_TIME_DB_PROEPRTY_NAME).getItemValue()));
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error("Monitor Thread Caught InterruptedException---->" + e);
                CommonLogger.errorLogger.error("Monitor Thread Caught InterruptedException---->" + e, e);
            }
        }
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended");
    }
}
