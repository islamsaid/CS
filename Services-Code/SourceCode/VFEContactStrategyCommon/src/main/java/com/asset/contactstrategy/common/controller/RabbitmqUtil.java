package com.asset.contactstrategy.common.controller;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author islam.said
 */
public class RabbitmqUtil {

    private static ConnectionFactory factory;

    public static void init() {
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Initializing RabbitMQ ConnectionFactory").build());
        StringBuilder log = new StringBuilder();
        log.append("\nRABBITMQ_USERNAME : ").append(Defines.RABBITMQ_USERNAME).append("\n");
        log.append("RABBITMQ_PASSWORD : ").append(Defines.RABBITMQ_PASSWORD).append("\n");
        log.append("RABBITMQ_VIRTUAL_HOST : ").append(Defines.RABBITMQ_VIRTUAL_HOST).append("\n");
        log.append("RABBITMQ_HOST : ").append(Defines.RABBITMQ_HOST).append("\n");
        log.append("RABBITMQ_PORT : ").append(Defines.RABBITMQ_PORT).append("\n");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Rabbit-MQ Connection Properties: " + log.toString()).build());
        factory = new ConnectionFactory();
        factory.setUsername(Defines.RABBITMQ_USERNAME);
        factory.setPassword(Defines.RABBITMQ_PASSWORD);
        factory.setVirtualHost(Defines.RABBITMQ_VIRTUAL_HOST);
        factory.setHost(Defines.RABBITMQ_HOST);
        factory.setPort(Defines.RABBITMQ_PORT);

    }

    public static Connection getConnection() throws CommonException {
        if (factory == null) {
            init();
        }
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (TimeoutException | IOException ex) {
            CommonLogger.errorLogger.error("Exception: Failed to get RabbitMQ connection.");
            throw new CommonException(ex.getMessage(), ErrorCodes.RABBITMQ.CONNECTION_ERROR);
        }
        return connection;
    }


    public static void closeConnection(Connection connection) throws CommonException {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ex) {
                CommonLogger.errorLogger.error("Exception: Failed to close RabbitMQ connection.");
                throw new CommonException(ex.getMessage(), ErrorCodes.RABBITMQ.CONNECTION_ERROR);
            }
        }

    }

    public static void closeChannel(Channel channel) throws CommonException {
        if (channel != null) {
            try {
                channel.close();
            } catch (TimeoutException | IOException ex) {
                CommonLogger.errorLogger.error("Exception: Failed to close RabbitMQ connection.");
                throw new CommonException(ex.getMessage(), ErrorCodes.RABBITMQ.CONNECTION_ERROR);
            }
        }

    }

    public static Channel getChannel(Connection connection) throws CommonException {

        try {
            return connection.createChannel();
        } catch (IOException ex) {
            CommonLogger.errorLogger.error("Exception: Failed to Create RabbitMQ Channel.");
            throw new CommonException(ex.getMessage(), ErrorCodes.RABBITMQ.CHANNEL_ERROR);
        }

    }
}
