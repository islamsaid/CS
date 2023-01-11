package com.asset.cs.sendingsms.threads;

import com.asset.contactstrategy.common.controller.RabbitmqUtil;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModel;
import com.asset.contactstrategy.common.service.RabbitmqQueueService;
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import com.rabbitmq.client.Connection;
import java.util.ArrayList;

/**
 *
 * @author islam.said
 */
public class EnqueuerThread extends Thread {

    private final String queueName;
    private ArrayBlockingQueue<EnqueueModel> receiveSMSQueue = null;
    private Connection connection = null;
    private final int threadNumber;
    private int MAX_BATCH_SIZE = Integer.valueOf(Defines.fileConfigurations.get(Defines.ENQUEUER_MAX_BATCH_SIZE));
    private final int sleepTime = Integer.valueOf(Defines.fileConfigurations.get(Defines.ENQUEUER_SLEEP_TIME));
    private String prefix;

    public EnqueuerThread(ArrayBlockingQueue<EnqueueModel> receiveSMSQueue, String prefix, String queueName, int threadNumber) {
        this.receiveSMSQueue = receiveSMSQueue;
        this.queueName = queueName;
        this.threadNumber = threadNumber;
        this.prefix = prefix;
    }

    public void enqueueSMS(ArrayList<EnqueueModel> sms) throws IOException, CommonException {
        RabbitmqQueueService rabbitmqQueueService = new RabbitmqQueueService();
        rabbitmqQueueService.enqueueDeliveryBatch(sms, prefix + queueName, connection);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("enqueuer_" + prefix + this.queueName + "_" + this.threadNumber);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "ENQUEUER THREAD [" + Thread.currentThread().getName() + "] STARTED FOR APP QUEUE:" + prefix + this.queueName).build());
        try {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Getting RabbitMQ Connection...").build());
            this.connection = RabbitmqUtil.getConnection();
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Get RabbitMQ Connection Successfully").build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Failed to get connection to RabbitMQ: " + ex.getMessage(), ex);
        }
        while (!Defines.DATASM_RECEIVER_SHUTDOWN_FLAG && !Manager.appsThreadsShutdownMap.get(queueName)) {
            try {
                if (connection == null || (!connection.isOpen())) {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Getting RabbitMQ Connection...").build());
                    this.connection = RabbitmqUtil.getConnection();
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Get RabbitMQ Connection Successfully").build());
                }
                ArrayList<EnqueueModel> smsPatch = new ArrayList<>();

                while (smsPatch.size() <= MAX_BATCH_SIZE && !receiveSMSQueue.isEmpty()) {
                    EnqueueModel enqModelRest = receiveSMSQueue.poll();
                    smsPatch.add(enqModelRest);
                }

                if (smsPatch.size() > 0) {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Start Enqueueing Batch of size [" + smsPatch.size() + "] in " + prefix + queueName).build());
                    long startTime = System.currentTimeMillis();
                    enqueueSMS(smsPatch);
                    long endTime = System.currentTimeMillis();
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Batch of size [" + smsPatch.size() + "] enqueued in " + (endTime - startTime) + " msec").build());
                } else {
                    Thread.sleep(sleepTime);
                }

            } catch (CommonException | IOException | InterruptedException ex) {
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
            } catch (Throwable ex) {
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
            }
        }

        try {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Closing RabbitMQ Connection...").build());
            RabbitmqUtil.closeConnection(connection);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Failed to close connection to RabbitMQ: " + ex.getMessage(), ex);
        }
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "ENQUEUER THREAD [" + Thread.currentThread().getName() + "] FINISHED FOR APP QUEUE: " + prefix + this.queueName).build());
    }
}
