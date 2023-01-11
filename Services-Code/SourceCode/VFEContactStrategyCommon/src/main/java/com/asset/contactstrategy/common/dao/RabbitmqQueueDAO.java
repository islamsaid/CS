package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModel;
import com.asset.contactstrategy.common.models.EnqueueModelREST;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.common.models.EnqueueModelDeliverSM;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.BuiltinExchangeType;

/**
 *
 * @author islam.said
 */
public class RabbitmqQueueDAO {

    public void createQueue(Channel channel, String queueName) throws CommonException {
        try {
            channel.exchangeDeclare(GeneralConstants.RABBITMQ_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, null);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, GeneralConstants.RABBITMQ_EXCHANGE, "");
        } catch (IOException ex) {
            CommonLogger.businessLogger.error("Exception---->  for [createQueue] Failed to create new RabbitMQ connection.", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.RABBITMQ.QUEUE_ERROR);
        }
    }

    public void deleteQueue(Channel channel, String queueName) throws CommonException {
        try {
            channel.queueDelete(queueName, false, false);
        } catch (IOException ex) {
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To delete queue " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_WEB_INTERFACE, ErrorCodes.RABBITMQ.QUEUE_ERROR, ex.getMessage());
        }
    }

    // Message Persistence is it optional??
    public void enqueuePatch(Channel channel, ArrayList<EnqueueModelREST> smsPatch, String queueName) throws CommonException {
        //Enabling Publisher Confirms on a Channel
        long start = System.currentTimeMillis();
        try {
            channel.confirmSelect();
            

            //add and declare queue
            channel.exchangeDeclare(GeneralConstants.RABBITMQ_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, null);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, GeneralConstants.RABBITMQ_EXCHANGE, "");
        } catch (IOException ex) {
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To enable confirmation " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST, ErrorCodes.RABBITMQ.CHANNEL_ERROR, ex.getMessage());
        }
        // Handling Publisher Confirms Asynchronously ??????
        //for (SMS sms : smsPatch) {
        for (EnqueueModelREST sms : smsPatch) {
            // convert message to json
            try {
                byte[] message = Utility.convertSmsToJsonBytes(sms);
                // publish message to the queue
                channel.basicPublish("", queueName, MessageProperties.PERSISTENT_BASIC, message);
            } catch (IOException ex) {
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        "Exception---->  for [enqueueBatch] Failed To send message batch " + ex).build());
                CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex, ex);
                throw new CommonException(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex, ex);
                throw new CommonException(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
            }
        }
        try {
            // wait for batsh confirmation
            channel.waitForConfirms();
        } catch (InterruptedException ex) {
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To confrim message batch " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST, ErrorCodes.RABBITMQ.CHANNEL_ERROR, ex.getMessage());
        }
        long elipsed = System.currentTimeMillis() - start;

//        CommonLogger.infoLogger.info("Message enqueued successfully in " + elipsed + " ms.");
    }

    public static void enqueueDeliveryBatch(Channel channel, ArrayList<EnqueueModel> smsPatch, String queueName) throws CommonException {
        long start = System.currentTimeMillis();
        try {
            channel.confirmSelect();
            channel.exchangeDeclare(GeneralConstants.RABBITMQ_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, null);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, GeneralConstants.RABBITMQ_EXCHANGE, "");
        } catch (IOException ex) {
            CommonLogger.errorLogger.error("Failed To enable confirmation " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST, ErrorCodes.RABBITMQ.CHANNEL_ERROR, ex.getMessage());
        }
        for (EnqueueModel sms : smsPatch) {
            try {
                byte[] message = null;
               if (sms instanceof EnqueueModelDeliverSM) {
                    message = Utility.convertEnqueueModelDeliverSMToJsonBytes((EnqueueModelDeliverSM) sms);
                }
                channel.basicPublish("", queueName, MessageProperties.PERSISTENT_BASIC, message);
            } catch (IOException | CommonException ex) {
                CommonLogger.errorLogger.error("Failed To send message batch " + ex, ex);
                throw new CommonException(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
            }
        }
        try {
            channel.waitForConfirms();
            long elipsed = System.currentTimeMillis() - start;
            CommonLogger.errorLogger.error("Message enqueued successfully in " + elipsed + " ms.");
        } catch (InterruptedException ex) {
            CommonLogger.errorLogger.error("Failed To confrim message batch " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_ENQUEUE_SMS_REST, ErrorCodes.RABBITMQ.CHANNEL_ERROR, ex.getMessage());
        }

    }

    public void registerDequeuer(Channel channel, Consumer consumer, String queueName, String consumerTag) throws CommonException {

        try {
            channel.exchangeDeclare(GeneralConstants.RABBITMQ_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, null);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, GeneralConstants.RABBITMQ_EXCHANGE, "");
            //channel.basicConsume(consumerTag, consumer);
            channel.basicConsume(queueName,
                    false, // auto ack
                    consumerTag,
                    false, //noLocal
                    false, //exclusive
                    new HashMap<>(),
                    consumer);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Consumer | consumerTag: " + consumerTag + " registered to queue : " + queueName).build());
        } catch (IOException ex) {
            CommonLogger.errorLogger.error("Exception---->  for [registerDequeuer] Failed To Register Consumer: " + consumerTag
                    + "to Queue: " + queueName + "\n" + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, ErrorCodes.RABBITMQ.DEQUEUE_ERROR, ex.getMessage());
        }
    }
}
