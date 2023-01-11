package com.asset.cs.sendingsms.threads;

import client.HashObject;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMS;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
/**
 *
 * @author islam.said
 */
public class RabbitmqDequeuer extends DefaultConsumer {

    private QueueModel appQueue;
    private ArrayBlockingQueue<HashObject> smsQueue;

    public RabbitmqDequeuer(Channel channel, QueueModel appQueue) {
        super(channel);
        this.appQueue = appQueue;
        this.smsQueue = Manager.deqeueurs_Senders_QMap.get(appQueue.getAppName());
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {

        try {
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Dequeuing Message from App Queue: " + appQueue.getAppName()).build());
            // convert message from json bytes to HashObject
            SMS sms = Utility.convertJsonBytesToSMS(body);
            // add message to the smsQueue
            HashObject smsObject = new HashObject();
            smsObject.setSMS(sms);
            smsObject.setDequeueTime(com.asset.cs.sendingsms.util.Utility.getDateTime());
            smsObject.setInstanceId(Integer.valueOf((String) Defines.fileConfigurations.get(Defines.INSTACE_ID)));
            try {
                smsQueue.put(smsObject);
            } catch (InterruptedException ex) {
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        "Unable to add SMS to smsQueue").build());
                try {
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Sending Negative Ack to RabbitMQ and requeue Message").build());
                    getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                } catch (IOException ex1) {
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "Exception --> Unable to send negative ack to Rabbit and requeue message \n" + ex1).build());
                    CommonLogger.errorLogger.error("Exception --> Unable to send negative ack to Rabbit and requeue message \n" + ex1);
                }
            }
        } catch (CommonException ex) {
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Unable to Dequeue message from App Queue: " + appQueue.getAppName()).build());
            try {
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        "Sending Negative Ack to RabbitMQ and requeue Message").build());
                getChannel().basicNack(envelope.getDeliveryTag(), false, true);
            } catch (IOException ex1) {
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        "Exception --> Unable to send negative ack to Rabbit and requeue message \n" + ex1).build());
                CommonLogger.errorLogger.error("Exception --> Unable to send negative ack to Rabbit and requeue message \n" + ex1);
            }
        }

        try {
            getChannel().basicAck(envelope.getDeliveryTag(), false);
        } catch (IOException ex) {
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Exception --> Unable to send ack to Rabbit \n" + ex).build());
            CommonLogger.errorLogger.error("Exception --> Unable to send ack to Rabbit \n" + ex);
        }

    }

}
