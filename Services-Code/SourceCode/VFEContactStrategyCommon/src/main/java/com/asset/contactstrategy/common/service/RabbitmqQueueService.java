package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.controller.RabbitmqUtil;
import com.asset.contactstrategy.common.dao.RabbitmqQueueDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModel;
import com.asset.contactstrategy.common.models.EnqueueModelREST;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import java.util.ArrayList;

/**
 *
 * @author islam.said
 */
public class RabbitmqQueueService {

    public void enqueueBatch(ArrayList<EnqueueModelREST> smsPatch, String queueName) throws CommonException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = RabbitmqUtil.getConnection();
            channel = RabbitmqUtil.getChannel(connection);
            RabbitmqQueueDAO rabbitmqQueueDAO = new RabbitmqQueueDAO();
            rabbitmqQueueDAO.enqueuePatch(channel, smsPatch, queueName);
        } finally {
            // update: close the channel then the connection --eslam.ahmed
            try {
                RabbitmqUtil.closeChannel(channel);
            } catch (CommonException ex) {
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,ex.getMessage()).build());
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
            }

            try {
                RabbitmqUtil.closeConnection(connection);
            } catch (CommonException ex) {
                 CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,ex.getMessage()).build());
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
            }
            // end update
        }

    }  

    public void enqueueDeliveryBatch(ArrayList<EnqueueModel> smsPatch, String queueName, Connection connection) throws CommonException{
        Channel channel = null;
        try{
            channel = RabbitmqUtil.getChannel(connection);
            RabbitmqQueueDAO.enqueueDeliveryBatch(channel, smsPatch, queueName);
        }finally{
            try {
                RabbitmqUtil.closeChannel(channel);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
            }
        }
    }
    public void createQueue(String queueName) throws CommonException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = RabbitmqUtil.getConnection();
            channel = RabbitmqUtil.getChannel(connection);
            RabbitmqQueueDAO rabbitmqQueueDAO = new RabbitmqQueueDAO();
            rabbitmqQueueDAO.createQueue(channel, queueName);
        } finally {
            RabbitmqUtil.closeChannel(channel);
            RabbitmqUtil.closeConnection(connection);
        }
    }

    public void deleteQueue(String queueName) throws CommonException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = RabbitmqUtil.getConnection();
            channel = RabbitmqUtil.getChannel(connection);
            RabbitmqQueueDAO rabbitmqQueueDAO = new RabbitmqQueueDAO();
            rabbitmqQueueDAO.deleteQueue(channel, queueName);
        } finally {
            RabbitmqUtil.closeChannel(channel);
            RabbitmqUtil.closeConnection(connection);
        }

    }

    public void registerDequeuer(Channel channel, Consumer consumer, String queueName, String consumerTag) throws CommonException {
        RabbitmqQueueDAO rabbitmqQueueDAO = new RabbitmqQueueDAO();
        rabbitmqQueueDAO.registerDequeuer(channel, consumer, queueName, consumerTag);
    }
}
