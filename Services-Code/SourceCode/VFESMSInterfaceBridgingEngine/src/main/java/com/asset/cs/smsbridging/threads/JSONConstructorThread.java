package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.models.SMSBridgeJSONStructure;
import com.asset.cs.smsbridging.models.ServiceNeedsHolder;
import java.util.Iterator;

/**
 *
 * @author aya.moawed 2595 waits in case the json queue is full
 */
public class JSONConstructorThread extends Thread {

    private int threadNo;

    public JSONConstructorThread(int threadNo) {
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("jsonconstructor_" + threadNo);
//        CommonLogger.businessLogger.debug("*****************JSON CONSTRUCTOR THREAD STARTED WITH NAME:" + Thread.currentThread().getName() + "***********************");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "JSON Constructor Thread Started").build());
        ServiceNeedsHolder currServiceHolder = null;
        SMSBridgeJSONStructure jsonStructure = null;
        RequestPreparator result = null;
        while (!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.get() || !Manager.workingServices.isEmpty()) {
            try {
                Iterator<ServiceNeedsHolder> iterator = Manager.workingServices.values().iterator();
                while (iterator.hasNext()) {
                    currServiceHolder = iterator.next();
                    jsonStructure = currServiceHolder.getQueueForDequeuerResult().poll();
                    if (jsonStructure != null) {
                        break;
                    }
                }
                if (jsonStructure != null) {
//                    CommonLogger.businessLogger.debug("Starting json constructing for a list of [" + jsonStructure.getSms().size() + "] retrieved from the java array blocking queue of the service with id [" + jsonStructure.getServiceName() + "] and SMSs information:" + Utility.convertSMSBridgeListToString(jsonStructure.getSms()).toString());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting JSON Constructing retrieved from the java array blocking queue of the servic")
                            .put(GeneralConstants.StructuredLogKeys.SERVIC_ID, jsonStructure.getServiceName())
                            .put(GeneralConstants.StructuredLogKeys.SMS_INFO, Utility.convertSMSBridgeListToString(jsonStructure.getSms()).toString()).build());
                    long beginTime = System.currentTimeMillis();
                    if (SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.CALL_SEND_SMS_INTERFACE_FLAG_PROPERTY).equalsIgnoreCase("true")) {
                        result = Utility.transformSMSToSimpleRequestPreparator(jsonStructure);
                    } else {
                        result = Utility.transformSMSJSONStructureToJSONResult(jsonStructure);
                    }

                    long endTime = System.currentTimeMillis();
//                    CommonLogger.businessLogger.debug(" constructing done in " + (endTime - beginTime) + " msec");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Constructing Done")
                            .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (endTime - beginTime)).build());
//                    CommonLogger.businessLogger.info("Transformed SMS JSON Result[" + result.getJsonString() + "],Simple Result[" + result.getSimpleRequest() + "]");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Transformed SMS JSON Result")
                            .put(GeneralConstants.StructuredLogKeys.SMS_SCRIPT, result.getJsonString())
                            .put(GeneralConstants.StructuredLogKeys.REQUEST_INFO, result.getSimpleRequest()).build());
                    Manager.queueForJSONRequests.put(result);
                } else {
                    try {
//                        CommonLogger.businessLogger.info("JSON Constructor Thread " + Thread.currentThread().getName() + "  is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_JSON_CONSTRUCTOR_THREAD_SLEEP_TIME) + " msecs ");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "JSON Constructor Thread is going to sleep")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_JSON_CONSTRUCTOR_THREAD_SLEEP_TIME)).build());
                        Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_JSON_CONSTRUCTOR_THREAD_SLEEP_TIME)));
                    } catch (InterruptedException ex) {
                        CommonLogger.businessLogger.error("JSON Constructor Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                        CommonLogger.errorLogger.error("JSON Constructor Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
                    }
                }
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("JSON Constructor Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("JSON Constructor Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
            }
        }
//        CommonLogger.businessLogger.info("*****************JSON Constructor " + Thread.currentThread().getName() + "   THREAD FINISHED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "JSON Constructor Thread Finished").build());
    }

}
