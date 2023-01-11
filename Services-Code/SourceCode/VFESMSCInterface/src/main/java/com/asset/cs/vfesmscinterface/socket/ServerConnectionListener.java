package com.asset.cs.vfesmscinterface.socket;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.initializer.Manager;

/**
 *
 * @author mohamed.morsy
 *
 */
public class ServerConnectionListener implements Runnable {

    private ServerSocket server = null;

    public ServerConnectionListener(ServerSocket server) {
        this.server = server;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("ServerConnectionListener");

        while (!Thread.interrupted() && !Manager.isShutdown.get()) {
            try {
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        "Waiting for a client ...").build());
                Socket socket = server.accept();
//				CommonLogger.businessLogger.debug("connection with client ip: " + socket.getInetAddress());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Connection With client")
                        .put(GeneralConstants.StructuredLogKeys.IP_ADDRRESS, socket.getInetAddress()).build());

                if (!Manager.isShutdown.get()) {
                    Manager.createNewSession(socket);
                } else {
                    CommonLogger.businessLogger.debug("received a connection while closing, kill the connection.");
                    socket.close();
                }
            } catch (SocketException se) {
                CommonLogger.businessLogger.error("ServerSocket closed.");
            } catch (Exception th) {
                CommonLogger.businessLogger.error("Server accept error." + th);
                CommonLogger.errorLogger.error("Server accept error ", th);
            }
        }
    }
}
