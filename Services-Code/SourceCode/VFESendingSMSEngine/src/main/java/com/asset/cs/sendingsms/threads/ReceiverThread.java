/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.threads;

import client.BufferByte;
import client.SMPP;
import client.TCPIPConnection;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.sendingsms.controller.Controller;
import com.asset.cs.sendingsms.controller.Manager;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author kerollos.asaad
 */
public class ReceiverThread extends Thread {

    private boolean killThread = false;
    private SMPP smppSession = null;
    private TCPIPConnection connection = null;
    private Logger debuglogger = null;
    private Logger errlogger = null;

    public ReceiverThread(SMPP smppSession, TCPIPConnection connection) {
        this.smppSession = smppSession;
        this.connection = connection;
        this.debuglogger = Controller.debugLogger;
        this.errlogger = Controller.errorLogger;
    }

    public void stopThread() {
        this.killThread = true;
        debuglogger.debug("Receiver thread called to be killed");
    }

    @Override
    public void run() {
//        CommonLogger.businessLogger.info("*****************RECEIVER THREAD STARTED FOR APP_ID:" + smppSession.getSmppApp().getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reciever Thread Started")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, smppSession.getSmppApp().getAppName()).build());
        BufferByte data;
        while ((!killThread) || (smppSession.getSendRcvHash().size() != 0)) {
            data = new BufferByte();
            try {
                long time = System.currentTimeMillis();
                connection.setReceiveTimeout(5000);// no time receiving
                // TimeOut.
                // connectoin failure (-1)
                // nullpointer exception in case of forced disconnection(-2)
                int retVal = connection.receive(data);
                if (retVal == -1 || retVal == -2) {
//                    debuglogger.debug("[RECEIVER THREAD] APP_ID:" + smppSession.getSmppApp().getAppName() + " | Connection failed");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Connection Failed")
                            .put(GeneralConstants.StructuredLogKeys.APP_ID, smppSession.getSmppApp().getAppName())
                            .put(GeneralConstants.StructuredLogKeys.STATUS, "Failed").build());
                    return;// kill receiver thread
                }
                if (data.getBuffer() != null) {
                    smppSession.ParsePDU(data);
                }
                if (retVal == 1) {
//                    debuglogger.debug("[RECEIVER THREAD] APP_ID:" + smppSession.getSmppApp().getAppName() + " | Receiving End in " + (System.currentTimeMillis() - time) + " msecs");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Recieving Ended")
                            .put(GeneralConstants.StructuredLogKeys.APP_ID, smppSession.getSmppApp().getAppName())
                            .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time))
                            .put(GeneralConstants.StructuredLogKeys.STATUS, "Success").build());
                }
            }// end try
            catch (OutOfMemoryError oome) {
                // debuglogger.error("[RECEIVER THREAD] APP_ID:" + smppSession.getSmppApp().getAppName() + " | OutOfMemoryError | " + oome.getMessage());
                errlogger.error("[RECEIVER THREAD] APP_ID:" + smppSession.getSmppApp().getAppName() + " | OutOfMemoryError | " + oome, oome);
            } catch (Exception e) {
                // debuglogger.error("[RECEIVER THREAD] APP_ID:" + smppSession.getSmppApp().getAppName() + " | Exception | " + e.getMessage());
                errlogger.error("[RECEIVER THREAD] APP_ID:" + smppSession.getSmppApp().getAppName() + " | Exception | " + e, e);
            }
            data = null;// dispose object
        }
//        CommonLogger.businessLogger.info("*****************RECEIVER THREAD FINISHED FOR APP_ID:" + smppSession.getSmppApp().getAppName() + "***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reciever Thread Finished")
                .put(GeneralConstants.StructuredLogKeys.APP_ID, smppSession.getSmppApp().getAppName()).build());
    }

}
