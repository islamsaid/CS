/**
 * created on: Dec 14, 2017 9:47:04 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.runnables;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_ROPTPARNOTALLWD;

import java.util.concurrent.Future;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LogModel;
import com.asset.contactstrategy.common.models.SMSCInterfaceClientModel;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.exceptions.ParserException;
import com.asset.cs.vfesmscinterface.exceptions.UnSupportedCommandException;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.models.BindResponseModel;
import com.asset.cs.vfesmscinterface.models.CommandModel;
import com.asset.cs.vfesmscinterface.models.CommandRequestModel;
import com.asset.cs.vfesmscinterface.models.CommandResponseModel;
import com.asset.cs.vfesmscinterface.models.DeliverSMResponseModel;
import com.asset.cs.vfesmscinterface.models.GenericNackRequestModel;
import com.asset.cs.vfesmscinterface.models.GenericNackResponseModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.models.SubmitSMModel;
import com.asset.cs.vfesmscinterface.models.SubmitSMResponseModel;
import com.asset.cs.vfesmscinterface.models.UnSupportedRequestModel;
import com.asset.cs.vfesmscinterface.models.UnbindRequestModel;
import com.asset.cs.vfesmscinterface.models.UnbindResponseModel;
import com.asset.cs.vfesmscinterface.parsers.CommandRequestParser;
import com.asset.cs.vfesmscinterface.responsepreparators.CommandResponsePreparator;
import com.asset.cs.vfesmscinterface.socket.Session;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import com.asset.cs.vfesmscinterface.utils.ErrorResponseFactory;
import com.asset.cs.vfesmscinterface.utils.PreparatorFactory;
import com.asset.cs.vfesmscinterface.utils.Util;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author mohamed.osman
 * @author eman.fawzy
 * @author mohamed.morsy
 *
 */
public class HandlerRunnable implements Runnable {

    private BufferByte pdu;

    private Session session;

    public HandlerRunnable(Session session, BufferByte pdu) {
        this.pdu = pdu;
        this.session = session;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(session.getSessionId() + ":HandlerThread");
        String logPdu = "";
        Future<?> writerFutre;
        CommandRequestModel commandModel = null;
        HeaderModel headerModel = null;
        CommandResponseModel commandResponseModel = null;
        try {
            logPdu = pdu.toString();
//            CommonLogger.businessLogger.debug("Request PDU: {" + logPdu + "}");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "PDU Reequesting")
                    .put(GeneralConstants.StructuredLogKeys.REQUEST_PDU, logPdu).build());
            headerModel = CommandRequestParser.parsePDUHeader(pdu);
            commandModel = CommandRequestParser.parsePDU(pdu, headerModel);

             if (session.isExceedingWindowSize()) {
                throw new RejectedExecutionException("exceeded session window size.");
            }
            
            if (!commandModel.getParserStatus().toString().isEmpty()) {
                throw (commandModel.getErrorCommadStatus() == 0
                        ? new Exception(commandModel.getParserStatus().toString())
                        : new ParserException(commandModel.getParserStatus().toString(), Data.ESME_ROPTPARNOTALLWD));
            }

            if (pdu.hasNext()) {
                throw new ParserException("invalid command length, buffer not ended after parsing",
                        Data.ESME_RINVCMDLEN);
            }

            logging(logPdu, commandModel, "");
            if (!(commandModel instanceof DeliverSMResponseModel)) {
                commandResponseModel = generateResponse(commandModel);
//            CommonLogger.businessLogger.debug("Response PDU: {" + commandResponseModel.getPdu().toString() + "}");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "PDU Responsing")
                        .put(GeneralConstants.StructuredLogKeys.RESPONSE_PDU, commandResponseModel.getPdu().toString()).build());
                logging(commandResponseModel.getPdu().toString(), commandResponseModel,
                        commandModel.getParserStatus().toString());

                writerFutre = session.submitToWriter(commandResponseModel.getPdu());

                if (commandResponseModel instanceof BindResponseModel) {
                    BindResponseModel bindResponseModel = (BindResponseModel) commandResponseModel;
                    bindSession(bindResponseModel);
                } else if (commandResponseModel instanceof UnbindResponseModel) {
                    unbindSession(commandResponseModel);
                } else if (commandResponseModel instanceof SubmitSMResponseModel) {
                    submitSm(commandModel, commandResponseModel, writerFutre);
                }
            }
        } catch (Exception e) {
            CommonLogger.businessLogger.debug(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            String loggingString = e.getMessage();

            // Prepare general system error response
            HeaderModel errorRespHeader = new HeaderModel();

            if (e instanceof UnSupportedCommandException) {
                errorRespHeader.setCommandID(Data.GENERIC_NACK);
                errorRespHeader.setCommandStatus(Data.ESME_RINVCMDID);
                errorRespHeader.setSequenceNumber(headerModel.getSequenceNumber());
            } else {
                if (e instanceof ParserException) {
                    errorRespHeader.setCommandStatus(((ParserException) e).getCommandStatus());
                } else {
                    errorRespHeader.setCommandStatus(Data.ESME_RSYSERR);
                    loggingString = "General System Error, " + loggingString;
                }
                if (headerModel == null) {
                    errorRespHeader.setCommandID(Data.GENERIC_NACK);
                    errorRespHeader.setSequenceNumber(0);
                } else {
                    errorRespHeader.setCommandID(Util.getResponseCommandId(headerModel.getCommandID()));
                    errorRespHeader.setSequenceNumber(headerModel.getSequenceNumber());
                }
            }

            if (commandModel == null) {
                commandModel = new UnSupportedRequestModel(headerModel);
            }

            logging(logPdu, commandModel, "");

            commandResponseModel = ErrorResponseFactory.getErrorParserModel(errorRespHeader);
            commandResponseModel.setHeaderModel(errorRespHeader);
            commandResponseModel.setPdu(ErrorResponseFactory.getBufferByte(errorRespHeader));

            logging(commandResponseModel.getPdu().toString(), commandResponseModel, loggingString);
            session.submitToWriter(commandResponseModel.getPdu());
        }
    }

    private void logging(String pdu, CommandModel commandModel, String requestError) {
//        CommonLogger.businessLogger.debug("[logging] started:" + commandModel.toString());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Logging Started")
                .put(GeneralConstants.StructuredLogKeys.COMMAND, commandModel.toString()).build());
        long startime = System.currentTimeMillis();
        LogModel logModel = new LogModel();
        logModel.setSessionId(session.getSessionId());
        logModel.setCommandName(commandModel.getClass().getSimpleName());
        logModel.setCommandType(
                (commandModel instanceof CommandResponseModel) ? Data.COMMAND_TYPE_RESP : Data.COMMAND_TYPE_REQ);
        logModel.setRequestError(requestError);
        logModel.setCommandStatus(String.valueOf(commandModel.getHeaderModel().getCommandStatus()));
        logModel.setSequenceNumber(commandModel.getHeaderModel().getSequenceNumber());
        logModel.setParse(commandModel.toString());
        logModel.setPdu(pdu);
        Manager.archiverTaskExecutor.safePut(logModel);
        Manager.archiverTaskExecutor.getQueueSize();
//        CommonLogger.businessLogger.info("[Done adding to logQue] in [" + (System.currentTimeMillis() - startime) + "]ms , Queue Size: " 
//                + Manager.archiverTaskExecutor.getQueueSize());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Done Adding LogQueue")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startime))
                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, Manager.archiverTaskExecutor.getQueueSize()).build());
    }

    public GenericNackResponseModel prepareGenericNackResponse(HeaderModel headerModel) {
        CommandResponsePreparator commandResponsePreparator = PreparatorFactory.getGenericNackResponsePreparator();
        GenericNackRequestModel requestModel = new GenericNackRequestModel(headerModel);
        return (GenericNackResponseModel) commandResponsePreparator
                .validateAndPrepareResponse(session.getConnectionStatus(), requestModel);
    }

    private CommandResponseModel generateResponse(CommandRequestModel commandModel) throws UnSupportedCommandException {
        CommandResponsePreparator commandResponsePreparator = PreparatorFactory
                .getResponsePreparator(commandModel.getHeaderModel());
        return commandResponsePreparator.validateAndPrepareResponse(session, commandModel);
    }

    public void bindSession(BindResponseModel bindResponseModel) {
        if (bindResponseModel.getHeaderModel().getCommandStatus() == Data.ESME_ROK_INT) {
            CommonLogger.businessLogger.debug("binding session...");
            SMSCInterfaceClientModel client = Manager.clientMap.get(bindResponseModel.getSystemId());
            session.bindSession(client);
            CommonLogger.businessLogger.debug("session binded...");
        }
    }

    public void submitSm(CommandRequestModel commandRequestodel, CommandResponseModel commandResponseModel,
            Future<?> writerFuture) {
        if (commandResponseModel.getHeaderModel().getCommandStatus() == Data.ESME_ROK_INT) {
            session.sendSms((SubmitSMModel) commandRequestodel, writerFuture);
        }
    }

    public void unbindSession(CommandResponseModel commandResponseModel) {
        if (commandResponseModel.getHeaderModel().getCommandStatus() == Data.ESME_ROK_INT) {
            CommonLogger.businessLogger.debug("unbinding session...");
            Manager.unbindSession(session);
            CommonLogger.businessLogger.debug("session unbinded...");
        }
    }
}
