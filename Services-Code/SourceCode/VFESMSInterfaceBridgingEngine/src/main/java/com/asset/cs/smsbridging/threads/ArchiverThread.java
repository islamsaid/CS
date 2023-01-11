package com.asset.cs.smsbridging.threads;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.smsbridging.controller.Manager;
import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import com.asset.cs.smsbridging.models.HTTPMsgResult;
import com.asset.cs.smsbridging.models.HTTPResponse;
import com.asset.cs.smsbridging.services.SMSBridgingMainService;
import java.util.ArrayList;

/**
 *
 * @author aya.moawed
 */
public class ArchiverThread extends Thread {

    private int threadNo;
    private SMSBridgingMainService mainService;
    private HTTPResponse response;

    public ArchiverThread(int threadNo) {
        this.threadNo = threadNo;
        mainService = new SMSBridgingMainService();
    }

    @Override
    public void run() {

        Thread.currentThread().setName("archiver" + threadNo);
//        CommonLogger.businessLogger.info("*****************ARCHIVER THREAD STARTED WITH NAME:" + Thread.currentThread().getName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchivingThread Started").build());
        response = new HTTPResponse();
        response.setSmsMsgIds(new ArrayList<Long>());
        response.setSms(new ArrayList<HTTPMsgResult>());
        while (!SMSBridgingDefines.ENGINE_SHUTDOWN_FLAG.get() || !Manager.queueForHTTPResult.isEmpty() || !Manager.queueForJSONRequests.isEmpty() || !Manager.workingServices.isEmpty() || !response.getSms().isEmpty()) {
            try {
                HTTPResponse httpResponse = Manager.queueForHTTPResult.poll();
                if (httpResponse != null) {

//                    CommonLogger.businessLogger.info("Handling Batch with Total SIZE =" + httpResponse.getSmsMsgIds().size());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Handling Batch")
                            .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, httpResponse.getSmsMsgIds().size()).build());
                    if (SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.CALL_SEND_SMS_INTERFACE_FLAG_PROPERTY).equalsIgnoreCase("true")) {
                        response.getSms().add(httpResponse.getSms().get(0));
                        response.getSmsMsgIds().add(httpResponse.getSmsMsgIds().get(0));

                        if (response.getSmsMsgIds().size() == Integer.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.ARCHIVE_BATCH_SIZE_PROPERTY))) {
                            mainService.updateStatusForSMSBatch(response.getSms(), response.getSmsMsgIds());
                            response.getSmsMsgIds().clear();
                            response.getSms().clear();
                        }
                    } else {
                        mainService.updateStatusForSMSBatch(httpResponse.getSms(), httpResponse.getSmsMsgIds());
                    }
                } else {

                    try {
                        if (SMSBridgingDefines.propertiesFileConfigs.get(SMSBridgingDefines.CALL_SEND_SMS_INTERFACE_FLAG_PROPERTY).equalsIgnoreCase("true")) {
                            if (response.getSmsMsgIds().size() > 0) {
                                mainService.updateStatusForSMSBatch(response.getSms(), response.getSmsMsgIds());
                                response.getSmsMsgIds().clear();
                                response.getSms().clear();
                            }
                        }
//                        CommonLogger.businessLogger.info("ARCHIVER Thread " + Thread.currentThread().getName() + " is going to sleep for " + SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_ARCHIVER_THREAD_SLEEP_TIME) + " msecs ");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archiver Thread is Sleeping")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_ARCHIVER_THREAD_SLEEP_TIME)).build());
                        Thread.sleep(Long.valueOf(SMSBridgingDefines.databaseConfigs.get(SMSBridgingDefines.SMS_BRIDGE_ARCHIVER_THREAD_SLEEP_TIME)));
                    } catch (InterruptedException ex) {
                        CommonLogger.businessLogger.error("ARCHIVER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                        CommonLogger.errorLogger.error("ARCHIVER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
                    }
                }
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error("ARCHIVER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("ARCHIVER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("ARCHIVER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex);
                CommonLogger.errorLogger.error("ARCHIVER Thread  " + Thread.currentThread().getName() + "  caught Exception--->" + ex, ex);
            }
        }
//        CommonLogger.businessLogger.info("*****************Archiver " + Thread.currentThread().getName() + "   THREAD FINISHED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ArchiverThread Finished").build());
    }
}
