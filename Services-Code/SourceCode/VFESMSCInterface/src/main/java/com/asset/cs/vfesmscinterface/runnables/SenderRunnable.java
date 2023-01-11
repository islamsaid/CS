package com.asset.cs.vfesmscinterface.runnables;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModel;
import com.asset.contactstrategy.common.models.EnqueueModelDeliverSM;
import com.asset.contactstrategy.common.models.LogModel;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.requestpreparator.DeliverSMRequestPreparator;
import com.asset.cs.vfesmscinterface.socket.Session;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author islam.said
 */
public class SenderRunnable implements Runnable {

    private Session session;

    private ArrayBlockingQueue<EnqueueModel> queue;

    private int sequenceNumber;

    public SenderRunnable(Session session, ArrayBlockingQueue<EnqueueModel> queue) {
        this.session = session;
        this.queue = queue;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(session.getSessionId() + ":SenderThread:" + session.getClientModel().getSystemName()
                + ":" + session.getClientModel().getSystemId());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sender Thread Start").build());
        while (!Manager.isShutdown.get() && queue != null) {
            BufferByte bufferByte = null;
            EnqueueModel enqueueModel = null;
            try {
                enqueueModel = queue.poll();

                if (enqueueModel != null && enqueueModel instanceof EnqueueModelDeliverSM) {

                    EnqueueModelDeliverSM enqueueModelDeliverSM = (EnqueueModelDeliverSM) enqueueModel;
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            "TransId [" + enqueueModelDeliverSM.getTransId() + "] "
                            + "| EnqueueModelDeliverSM : " + enqueueModelDeliverSM.toString()).build());
                    bufferByte = DeliverSMRequestPreparator.constructPDU(enqueueModelDeliverSM, assignSequenceNumber());

                }
                if (bufferByte != null) {
                    session.submitToWriter(bufferByte);
                    logging(bufferByte.toString(), "", sequenceNumber, enqueueModel);
                }
            } catch (Exception e) {
                CommonLogger.errorLogger.error(e.getMessage(), e);
                if (bufferByte != null && enqueueModel != null) {
                    logging(bufferByte.toString(), e.getMessage(), sequenceNumber, enqueueModel);
                }
            }
        }
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sender Thread Shutdown").build());
    }

    private int assignSequenceNumber() {
        if (sequenceNumber < 0x7FFFFFFF) { // sequence_number range from
            // 0x00000001 - 0x7FFFFFFF
            sequenceNumber++;
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "SequenceNumber || " + sequenceNumber).build());
            return (sequenceNumber);
        } else {
            sequenceNumber = 0;
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "SequenceNumber || " + sequenceNumber).build());
            return (sequenceNumber);
        }

    }

    private void logging(String pdu, String requestError, int sequenceNumber, EnqueueModel enqueueModel) {
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "[logging] started:" + enqueueModel.toString()).build());
        long startime = System.currentTimeMillis();
        LogModel logModel = new LogModel();
        logModel.setSessionId(session.getSessionId());
        if (enqueueModel instanceof EnqueueModelDeliverSM) {
            logModel.setCommandName("DeliverSMModel");
        }
        logModel.setCommandType(Data.COMMAND_TYPE_REQ);
        logModel.setRequestError(requestError);
        logModel.setCommandStatus("");
        logModel.setSequenceNumber(sequenceNumber);
        logModel.setParse(enqueueModel.toString());
        logModel.setPdu(pdu);
        Manager.archiverTaskExecutor.safePut(logModel);
        Manager.archiverTaskExecutor.getQueueSize();
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "[Done adding to logQue] in [" + (System.currentTimeMillis() - startime) + "]ms , Queue Size: "
                + Manager.archiverTaskExecutor.getQueueSize()).build());
    }

}
