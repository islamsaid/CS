/**
 * created on: Dec 18, 2017 8:47:04 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.runnables;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.constants.ConnectionStatusEnum;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.socket.Connection;
import com.asset.cs.vfesmscinterface.socket.Session;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 * @author mohamed.morsy
 *
 */
public class ReaderRunnable implements Runnable {

    private Session session;

    private Connection connection;

    long executionTime;

    public ReaderRunnable(Session session) {
        this.session = session;
        this.connection = session.getConnection();
    }

    @Override
    public void run() {

        CommonLogger.businessLogger.debug("reader started successfully");
        Thread.currentThread().setName(session.getSessionId() + ":ReaderThread");

        while (!Manager.isShutdown.get() && !Thread.interrupted()) {
            try {

//				CommonLogger.businessLogger.debug(session.toLogging() + " waiting to read PDU.");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Waiting to Read PDU")
                        .put(GeneralConstants.StructuredLogKeys.SESSION, session.toLogging()).build());
                BufferByte pdu = connection.readPDU();
//                CommonLogger.businessLogger.debug(session.toLogging() + " reader read " + pdu);
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "PDU Reader")
                        .put(GeneralConstants.StructuredLogKeys.SESSION, session.toLogging())
                        .put(GeneralConstants.StructuredLogKeys.PDU, pdu).build());

                session.updateLastReadTime();

                executionTime = System.currentTimeMillis();
//                CommonLogger.businessLogger.debug(session.toLogging() + " submitting pdu to handlerExecutor.");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Submitting PDU to handlerExecutor")
                        .put(GeneralConstants.StructuredLogKeys.SESSION, session.toLogging()).build());
                session.executeToHandler(pdu);
//                CommonLogger.businessLogger.debug(session.toLogging() + " pdu submited to handlerExecutor "
//                        + (System.currentTimeMillis() - executionTime) + " msec");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "PDU Submitted to handlerExecutor")
                        .put(GeneralConstants.StructuredLogKeys.SESSION, session.toLogging())
                        .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - executionTime)).build());

            } catch (Exception e) {
                if (session.isLoggingEnabled()) {
                    CommonLogger.businessLogger.error("error in read from socket: " + e.getMessage());
                    CommonLogger.errorLogger.error("error in read from socket", e);
                }

                // prevent dead lock in closing session
                if (session.getConnectionStatus() != ConnectionStatusEnum.CLOSED) {
//                    CommonLogger.businessLogger.debug("socket closed will terminate session: " + session.getSessionId());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Socket Closed will Terminate Session")
                            .put(GeneralConstants.StructuredLogKeys.SESSION_ID, session.getSessionId()).build());
                    Manager.closeSession(session);
                }
            }
        }

        CommonLogger.businessLogger.debug("reader closed successfully");
    }

}
