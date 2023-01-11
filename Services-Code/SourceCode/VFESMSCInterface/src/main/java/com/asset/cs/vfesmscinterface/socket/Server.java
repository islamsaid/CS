package com.asset.cs.vfesmscinterface.socket;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import java.io.IOException;
import java.net.ServerSocket;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;

/**
 *
 * @author mohamed.morsy
 *
 */
public class Server {

    private Integer port;

    private ServerSocket serverSocket = null;

    public Server(Integer port) throws Exception {

        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
//			CommonLogger.businessLogger.debug("Server startet, Port : " + serverSocket.getLocalPort());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Server Started")
                    .put(GeneralConstants.StructuredLogKeys.PORT, serverSocket.getLocalPort()).build());
        } catch (IOException ioe) {
            CommonLogger.businessLogger.error("Can not bind to port : " + port);
            CommonLogger.errorLogger.error("Can not bind to port : " + port, ioe);
            throw new Exception("Can not bind to port : " + port);
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Can not bind to port : " + port);
            CommonLogger.errorLogger.error("Can not bind to port : " + port, e);
            throw new Exception("Can not bind to port : " + port);
        }

    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                CommonLogger.businessLogger.error("ServerSocket closed.");
                CommonLogger.errorLogger.error("ServerSocket closed.", e);
            }
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
