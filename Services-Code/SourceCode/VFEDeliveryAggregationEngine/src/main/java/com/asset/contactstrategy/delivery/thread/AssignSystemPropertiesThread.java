/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.thread;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.services.DeliveryAggregationServices;

/**
 *
 * @author esmail.anbar
 */
public class AssignSystemPropertiesThread implements Runnable {

//    String threadName;
//    
//    public AssignSystemPropertiesThread(String threadName)
//    {
//        this.threadName = threadName;
//    }
    @Override
    public void run() {
        Thread.currentThread().setName("AssignSystemPropertiesThread");
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");

        DeliveryAggregationServices deliveryAggregationServices = new DeliveryAggregationServices();
        int sleepTime = Defines.DELIVERY_AGGREGATION_PROPERTIES.SLEEP_TIME_PER_SYSTEMPROPERTIES_UPDATE_VALUE;

        while (!JobManager.ENGINE_SHUTDOWN_FLAG.get()) {
            try {
                deliveryAggregationServices.assignSystemProperties();
//                CommonLogger.businessLogger.info("Sleeping for " + sleepTime + " till next System Properties Update");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "AssignSystemPropertiesThread is sleeping till next system properties update")
                        .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
                Thread.sleep(sleepTime);
            } catch (CommonException e) {
                CommonLogger.businessLogger.error(e + " || " + e.getErrorMsg());
                CommonLogger.errorLogger.error(e + " || " + e.getErrorMsg(), e);
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e + " || " + e.getMessage());
                CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
            }
        }
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended || Sleep Time: " + sleepTime);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + "has Ended")
                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTime).build());
    }

}
