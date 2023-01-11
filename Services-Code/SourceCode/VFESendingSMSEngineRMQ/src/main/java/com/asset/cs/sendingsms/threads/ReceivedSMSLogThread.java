package com.asset.cs.sendingsms.threads;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.models.ReceivedSMSLogModel;
import com.asset.cs.sendingsms.service.ReceivedSMSLogService;
import java.util.ArrayList;

/**
 *
 * @author islam.said
 */
public class ReceivedSMSLogThread implements Runnable{
    private final int threadNumber;
    private final int sleepTime = Integer.valueOf(Defines.fileConfigurations.get(Defines.RECEIVED_SMS_LOG_SLEEP_TIME));

    public ReceivedSMSLogThread(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public void insertReceivedSMS(ArrayList<ReceivedSMSLogModel> receivedSMSLogs) {
        try {            
            ReceivedSMSLogService.insertReceivedSMS(receivedSMSLogs);
        } catch (CommonException e) {
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("ReceievedSMSLogThread_" + "_" + this.threadNumber);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "ReceievedSMSLog THREAD [" + Thread.currentThread().getName() + "] STARTED").build());

        while (!Defines.DATASM_RECEIVER_SHUTDOWN_FLAG || !Manager.receivedSMSLogQueue.isEmpty()) {

            try {
                ArrayList<ReceivedSMSLogModel> receivedSMSLogs = new ArrayList<>();

                while (receivedSMSLogs.size() <= 10 && !Manager.receivedSMSLogQueue.isEmpty()) {
                    ReceivedSMSLogModel receivedSMSLogModel = Manager.receivedSMSLogQueue.poll();
                    receivedSMSLogs.add(receivedSMSLogModel);
                }
                if (receivedSMSLogs.size() > 0) {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Inserting log batch of size [" + receivedSMSLogs.size() + "]").build());
                    long startTime = System.currentTimeMillis();
                    insertReceivedSMS(receivedSMSLogs);
                    long endTime = System.currentTimeMillis();
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Batch of size [" + receivedSMSLogs.size() + "] inserted in " + (endTime - startTime) + "msec").build());
                } else {
                    Thread.sleep(sleepTime);
                }
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e.getMessage(), e);
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }

        }

        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "ReceievedSMSLog THREAD " + Thread.currentThread().getName() + " FINISHED").build());

    }
}
