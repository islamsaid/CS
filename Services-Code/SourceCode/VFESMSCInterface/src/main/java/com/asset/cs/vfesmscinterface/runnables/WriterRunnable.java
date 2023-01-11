/**
 * created on: Dec 18, 2017 5:11:04 PM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.runnables;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import java.io.IOException;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.constants.ConnectionStatusEnum;
import com.asset.cs.vfesmscinterface.exceptions.RunnableException;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.socket.Connection;
import com.asset.cs.vfesmscinterface.socket.Session;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import java.time.LocalDateTime;

/**
 * @author mohamed.morsy
 *
 */
public class WriterRunnable implements Runnable {

    private BufferByte pdu;

    private Session session;

    private Connection connection;

    public WriterRunnable(Session session, BufferByte pdu) {
        this.pdu = pdu;
        this.session = session;
        this.connection = session.getConnection();
    }

    @Override
    public void run() {
        Thread.currentThread().setName(session.getSessionId() + ":WriterThread");
        try {

            long time = System.currentTimeMillis();
//			CommonLogger.businessLogger.debug(String.format("%s writing a response.", session.toLogging()));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Writing Aresponse")
                    .put(GeneralConstants.StructuredLogKeys.SESSION, session.toLogging()).build());
            connection.writePDU(pdu);
//            CommonLogger.businessLogger.debug(String.format("%s write response sent, writing time: %d msec.",
//                    session.toLogging(), (System.currentTimeMillis() - time)));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Write Response Sent")
                    .put(GeneralConstants.StructuredLogKeys.SESSION, session.toLogging())
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time)).build());

        } catch (IOException e) {

            if (session.isLoggingEnabled()) {
                CommonLogger.businessLogger.error("error in writing to socket: " + e.getMessage());
                CommonLogger.errorLogger.error("error in writing to socket", e);
            }

            // prevent dead lock in closing session
            if (session.getConnectionStatus() != ConnectionStatusEnum.CLOSED) {
//                CommonLogger.businessLogger.debug("socket closed will terminate " + session);
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Socket closed will terminate")
                        .put(GeneralConstants.StructuredLogKeys.SESSION, session).build());
                Manager.closeSession(session);
            }

            // will stop sending this request to contact strategy
            throw new RunnableException(e.getMessage());

        } catch (Exception e) {
            CommonLogger.businessLogger.error("error: " + e.getMessage());
            CommonLogger.errorLogger.error("error", e);

            // will stop sending this request to contact strategy
            throw new RunnableException(e.getMessage());
        }

    }

}
