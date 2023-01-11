/**
 * created on: Dec 17, 2017 11:03:42 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.socket;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModel;
import com.asset.contactstrategy.common.models.SMSCInterfaceClientModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.vfesmscinterface.constants.ConnectionStatusEnum;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.models.SubmitSMModel;
import com.asset.cs.vfesmscinterface.runnables.HandlerRunnable;
import com.asset.cs.vfesmscinterface.runnables.ReaderRunnable;
import com.asset.cs.vfesmscinterface.runnables.SendSMSIntergrationRunnable;
import com.asset.cs.vfesmscinterface.runnables.SenderRunnable;
import com.asset.cs.vfesmscinterface.runnables.WriterRunnable;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import com.asset.cs.vfesmscinterface.utils.Executor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author mohamed.morsy
 *
 */
public class Session {

    private String sessionId;

    private Connection connection;

    private SMSCInterfaceClientModel clientModel;

    private Executor writerExecutor;

    private Executor handlerExecutor;

    private Executor sendSmsIntegrationExecutor;

    private LocalDateTime lastReadTime;

    private LocalDateTime sessionCreationTime;

    private ReaderRunnable readerRunnable;

    private ExecutorService sessionExecutor;

    private Boolean loggingEnabled;

    private AtomicLong submitSMMessageIdAtomic;

    private ConnectionStatusEnum connectionStatus;

    private static ConcurrentHashMap<String, ArrayList<SubmitSMModel>> dividedMessageMap = new ConcurrentHashMap<>();
    
    private static ReentrantLock lock = new ReentrantLock();
    
    private SenderRunnable senderRunnable;

    public Session(Socket socket, int handlerQueueCapacity, int numOfHandlerThreads, int writerQueueCapacity,
            int numOfWriterThreads) throws IOException {
        loggingEnabled = true;
        this.connection = new Connection(socket);
        sessionId = Utility.generateTransId("SMSC_SESSION_");
        readerRunnable = new ReaderRunnable(this);
        connectionStatus = ConnectionStatusEnum.OPEN;
        sessionCreationTime = LocalDateTime.now();
        submitSMMessageIdAtomic = new AtomicLong(1);
        dividedMessageMap = new ConcurrentHashMap<>();
        writerExecutor = new Executor(writerQueueCapacity, numOfWriterThreads);
        handlerExecutor = new Executor(handlerQueueCapacity, numOfHandlerThreads);
        sessionExecutor = Executors.newCachedThreadPool();
        sessionExecutor.execute(readerRunnable);
//		CommonLogger.businessLogger.debug("Session with client ip:  " + socket.getInetAddress() + " created successfully");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Session Created Successfully")
                .put(GeneralConstants.StructuredLogKeys.IP_ADDRRESS, socket.getInetAddress()).build());
    }

    public void executeToHandler(BufferByte pdu) {
        handlerExecutor.execute(new HandlerRunnable(this, pdu));
    }

    public Future<?> submitToWriter(BufferByte pdu) {
        return writerExecutor.submit(new WriterRunnable(this, pdu));
    }

    public void sendSms(SubmitSMModel submitSMModel, Future<?> writerFuture) {
        if (submitSMModel.isSingelMessage()) {
            sendSmsIntegrationExecutor.execute(new SendSMSIntergrationRunnable(this, submitSMModel, writerFuture));
        } else {
            handleDividedMessage(submitSMModel, writerFuture);
        }
    }

    public void bindSession(SMSCInterfaceClientModel clientMdel) {
        this.connectionStatus = ConnectionStatusEnum.BOUND;
        this.clientModel = clientMdel;
        this.sendSmsIntegrationExecutor = new Executor(clientMdel.getCsSubmitSmsQueueSize(),
                clientMdel.getCsCalledThreads());
        ArrayBlockingQueue<EnqueueModel> queue = Manager.dequeuedDeliverRequests.get((Manager.serviceQueueMap.get(clientModel.getSystemName()).getAppName()));
        senderRunnable = new SenderRunnable(this, queue);
        sessionExecutor.execute(senderRunnable);
    }

    public void unbindSession() {
        CommonLogger.businessLogger.debug("session will close, due to "
                + (Manager.isShutdown.get() ? "shutdown" : "unbind") + " request: " + toLogging());
        loggingEnabled = false;
        this.connectionStatus = ConnectionStatusEnum.CLOSED;
        sessionExecutor.shutdownNow(); // close reader - if not blocked - else will closed in connection close
        handlerExecutor.shutdown();// stop receive a new requests
        awaitTermination(handlerExecutor);
        writerExecutor.shutdown(); // stop receive a new requests
        if (sendSmsIntegrationExecutor != null) {
            sendSmsIntegrationExecutor.shutdown();// stop receive a new requests
        }
        // wait all operation on connection before close connection
        awaitTermination(writerExecutor);
        try {
            // force close connection
            connection.close();
        } catch (IOException e) {
            // disable exception logging while closing session
        }
    }

    public void closeSession() {
        CommonLogger.businessLogger.debug("session will close " + toLogging());
        loggingEnabled = false;
        this.connectionStatus = ConnectionStatusEnum.CLOSED;
        sessionExecutor.shutdownNow();
        handlerExecutor.shutdownNow();
        writerExecutor.shutdownNow();
        if (sendSmsIntegrationExecutor != null) {
            sendSmsIntegrationExecutor.shutdown();
        }
        try {
            connection.close();
        } catch (IOException e) {
            // disable exception logging while closing session
        }
    }

    public String getNextMessageId() {
        return Long.toHexString(submitSMMessageIdAtomic.incrementAndGet());
    }

    ////////////////////// helpers methods//////////////////////////
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[sessionId: ").append(sessionId).append(", ").append(connection).append(", ")
                .append("connectionStatus: ").append(connectionStatus).append(", ")
                .append(prepareClientLogInformation()).append("sessionCreationTime: ").append(sessionCreationTime)
                .append(", ").append("sessionLastReadTime: ").append(lastReadTime).append(", ")
                .append("session threads: [handlerExecutor: ").append(handlerExecutor).append(", ")
                .append("writerExecutor: ").append(writerExecutor).append(",  sendSmsIntegrationExecutor: ")
                .append(sendSmsIntegrationExecutor).append(", dividedMessageMap: ").append(dividedMessageMap)
                .append("]]");
        return stringBuilder.toString();
    }

    public String toLogging() {
        return new StringBuilder().append("sessionId: ").append(sessionId).toString();
    }

    private String prepareClientLogInformation() {
        if (clientModel == null) {
            return "[no client information], ";
        } else {
            return new StringBuilder().append("[clientId: ").append(clientModel.getId()).append(", ")
                    .append("systemId: ").append(clientModel.getSystemId()).append(", ").append("systemName: ")
                    .append(clientModel.getSystemName()).append("], ").toString();
        }
    }

    private void executeToSendSmsIntegration(SubmitSMModel submitSMModel, Future<?> writerFuture) {
        long time = System.currentTimeMillis();
//        CommonLogger.businessLogger
//                .debug(String.format("sms with smscMessageId: %s start queueing", submitSMModel.getSmscMessageId()));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Start Queing")
                .put(GeneralConstants.StructuredLogKeys.MSG_ID, submitSMModel.getSmscMessageId()).build());
        sendSmsIntegrationExecutor.execute(new SendSMSIntergrationRunnable(this, submitSMModel, writerFuture));
//        CommonLogger.businessLogger.debug(String.format("sms with smscMessageId: %s start queueing, queueing time: %d",
//                submitSMModel.getSmscMessageId(), (System.currentTimeMillis() - time)));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Start Queueing")
                .put(GeneralConstants.StructuredLogKeys.MSG_ID, submitSMModel.getSmscMessageId())
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time)).build());
    }

    private void handleDividedMessage(SubmitSMModel submitSMModel, Future<?> writerFuture) {
        String messageRefrenceStr = submitSMModel.getSarMsgRefNum().getValue() + "_" + submitSMModel.getDestination().getAddress();
        submitSMModel.updatetLastUpdate();
        boolean addedToMap = false;

        if (submitSMModel.getSarTotalSegments().getValue() == 1) {
            // a divided message for 1 part, just a joke
            CommonLogger.businessLogger.debug(String.format(
                    "messageRefrenceNumber: %d, divided message but with SarTotalSegments = 1, start send to contact strategy",
                    messageRefrenceStr));
            executeToSendSmsIntegration(submitSMModel, writerFuture);
            return;
        }
        ArrayList<SubmitSMModel> submitSMModelList = dividedMessageMap.get(messageRefrenceStr);
        if (submitSMModelList == null || submitSMModelList.isEmpty()) {
            lock.lock();
            try {
                submitSMModelList = dividedMessageMap.get(messageRefrenceStr);
                if (submitSMModelList == null || submitSMModelList.isEmpty()) {
                    submitSMModelList = new ArrayList<>();
                    submitSMModelList.add(submitSMModel);
                    dividedMessageMap.put(messageRefrenceStr, submitSMModelList);
                    addedToMap = true;
                    CommonLogger.businessLogger
                            .debug(String.format("message with messageRefrenceStr: %s, part num: %s received and queued",
                                    messageRefrenceStr, submitSMModel.getSarSegmentsSeqnum().getValue()));
                }
            } finally {
                lock.unlock();
            }
        }

        if (!addedToMap) {

            synchronized (submitSMModelList) {
                for (SubmitSMModel sm : submitSMModelList) {
                    sm.updatetLastUpdate();
                    if (sm.getSarSegmentsSeqnum().equals(submitSMModel.getSarSegmentsSeqnum())) {
                        CommonLogger.businessLogger.debug(String.format(
                                "message with messageRefrenceStr: %s, received a message part with an existing SarSegmentsSeqnum: %d, discard this part",
                                messageRefrenceStr, submitSMModel.getSarSegmentsSeqnum().getValue()));
                        return;
                    } else if (!sm.getSarTotalSegments().equals(submitSMModel.getSarTotalSegments())) {
                        CommonLogger.businessLogger.debug(String.format(
                                "message with messageRefrenceStr: %d, diffrent SarTotalSegments in the same list, cancel this message and all it's parts",
                                submitSMModel.getSarTotalSegments().getValue()));
                        dividedMessageMap.remove(messageRefrenceStr);
                        return;
                    }
                }

                submitSMModelList.add(submitSMModel);
            }

            int sarSegmentsSeqnum = submitSMModel.getSarSegmentsSeqnum().getValue();

            CommonLogger.businessLogger.debug(String.format(
                    "message with messageRefrenceStr: %s, part num: %d received %s", messageRefrenceStr,
                    sarSegmentsSeqnum, (sarSegmentsSeqnum == submitSMModelList.size() ? "in sequance and queued"
                    : "out of sequance and queued")));

            synchronized (submitSMModelList) {
                if (submitSMModelList.size() == submitSMModel.getSarTotalSegments().getValue()
                        && dividedMessageMap.containsKey(messageRefrenceStr)) {

                    CommonLogger.businessLogger.debug(String.format("message with messageRefrenceStr: %s all parts received, start combining message parts", messageRefrenceStr));
                    SubmitSMModel commbiendSubmitSMModel = combineMessage(submitSMModelList);
                    executeToSendSmsIntegration(commbiendSubmitSMModel, writerFuture);
                    dividedMessageMap.remove(messageRefrenceStr);
                }
            }

        }
    }
    
//    private void handleDividedMessage(SubmitSMModel submitSMModel, Future<?> writerFuture) {
//        int messageRefrenceNumber = submitSMModel.getSarMsgRefNum().getValue();
//        submitSMModel.updatetLastUpdate();
//
//        if (!dividedMessageMap.containsKey(messageRefrenceNumber)) {
//            if (submitSMModel.getSarTotalSegments().getValue() == 1) {
//                // a divided message for 1 part, just a joke
//                CommonLogger.businessLogger.debug(String.format(
//                        "messageRefrenceNumber: %d, divided message but with SarTotalSegments = 1, start send to contact strategy",
//                        messageRefrenceNumber));
//                executeToSendSmsIntegration(submitSMModel, writerFuture);
//            } else {
//                CommonLogger.businessLogger
//                        .debug(String.format("message with messageRefrenceNumber: %d, part num: 1 received and queued",
//                                messageRefrenceNumber));
//                ArrayList<SubmitSMModel> submitSMModelList = new ArrayList<>();
//                submitSMModelList.add(submitSMModel);
//                dividedMessageMap.put(messageRefrenceNumber, submitSMModelList);
//            }
//        } else {
//            ArrayList<SubmitSMModel> submitSMModelList = dividedMessageMap.get(messageRefrenceNumber);
//
//            for (SubmitSMModel sm : submitSMModelList) {
//                sm.updatetLastUpdate();
//                if (sm.getSarSegmentsSeqnum().equals(submitSMModel.getSarSegmentsSeqnum())) {
//                    CommonLogger.businessLogger.debug(String.format(
//                            "message with messageRefrenceNumber: %d, received a message part with an existing SarSegmentsSeqnum: %d, discard this part",
//                            messageRefrenceNumber, submitSMModel.getSarSegmentsSeqnum().getValue()));
//                    return;
//                } else if (!sm.getSarTotalSegments().equals(submitSMModel.getSarTotalSegments())) {
//                    CommonLogger.businessLogger.debug(String.format(
//                            "message with messageRefrenceNumber: %d, diffrent SarTotalSegments in the same list, cancel this message and all it's parts",
//                            submitSMModel.getSarTotalSegments().getValue()));
//                    dividedMessageMap.remove(messageRefrenceNumber);
//                    return;
//                }
//            }
//
//            submitSMModelList.add(submitSMModel);
//
//            int sarSegmentsSeqnum = submitSMModel.getSarSegmentsSeqnum().getValue();
//
//            CommonLogger.businessLogger.debug(String.format(
//                    "message with messageRefrenceNumber: %d, part num: %d received %s", messageRefrenceNumber,
//                    sarSegmentsSeqnum, (sarSegmentsSeqnum == submitSMModelList.size() ? "in sequance and queued"
//                    : "out of sequance and queued")));
//
//            if (submitSMModelList.size() == submitSMModel.getSarTotalSegments().getValue()) {
//                CommonLogger.businessLogger.debug(String.format(
//                        "message with messageRefrenceNumber: %d all parts received, start combining message parts",
//                        messageRefrenceNumber));
//                SubmitSMModel commbiendSubmitSMModel = combineMessage(submitSMModelList);
//                executeToSendSmsIntegration(commbiendSubmitSMModel, writerFuture);
//                dividedMessageMap.remove(messageRefrenceNumber);
//            }
//        }
//    }

    private SubmitSMModel combineMessage(ArrayList<SubmitSMModel> submitSMModelList) {

        // sort message parts to get the same original message
        Collections.sort(submitSMModelList, new Comparator<SubmitSMModel>() {
            @Override
            public int compare(SubmitSMModel o1, SubmitSMModel o2) {
                return o1.getSarSegmentsSeqnum().getValue() - o2.getSarSegmentsSeqnum().getValue();
            }
        });

        SubmitSMModel combiendSubmitSmModel = new SubmitSMModel(submitSMModelList.get(0));

        StringBuilder smscMessageIdBuilder = new StringBuilder(submitSMModelList.get(0).getSmscMessageId());

        StringBuilder messageBuilder = new StringBuilder(combiendSubmitSmModel.getMessage());
        for (int i = 1; i < submitSMModelList.size(); i++) {
            messageBuilder.append(submitSMModelList.get(i).getMessage());
            //smscMessageIdBuilder.append(",").append(submitSMModelList.get(i).getSmscMessageId()); // Eslam.ahmed | CR: SMS_multipart_UDH | 2020-06-21 | Commented

        }
        String message = messageBuilder.toString();

        combiendSubmitSmModel.setMessage(message);

        int smLength = message.getBytes().length - (combiendSubmitSmModel.getDataCoding() == Data.DCS_Arabic ? 2 : 0);

        combiendSubmitSmModel.setSmLength(smLength);

        combiendSubmitSmModel.setSmscMessageId(smscMessageIdBuilder.toString());

        return combiendSubmitSmModel;
    }

    private void awaitTermination(Executor executor) {
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            CommonLogger.businessLogger.error("interrupted exception while waiting excecutor to close: " + e.getMessage());
            CommonLogger.errorLogger.error("interrupted exception while waiting excecutor to close", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public ConnectionStatusEnum getConnectionStatus() {
        return connectionStatus;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getLastReadTime() {
        return lastReadTime;
    }

    public void updateLastReadTime() {
        this.lastReadTime = LocalDateTime.now();
    }

    public LocalDateTime getSessionCreationTime() {
        return sessionCreationTime;
    }

    public Boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public SMSCInterfaceClientModel getClientModel() {
        return clientModel;
    }

    public static ConcurrentHashMap<String, ArrayList<SubmitSMModel>> getDividedMessageMap() {
        return dividedMessageMap;
    }
    
    public boolean isExceedingWindowSize() {
        return handlerExecutor.getQueueSize() > Defines.SMSC_INTERFACE_PROPERTIES.WINDOW_SIZE_PER_SESSION_VALUE;
    }

}
