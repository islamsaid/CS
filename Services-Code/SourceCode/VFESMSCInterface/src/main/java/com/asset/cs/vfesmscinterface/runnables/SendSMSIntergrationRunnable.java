/**
 * created on: Jan 11, 2017 09:01:04 PM created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.runnables;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.CsSmscInterfaceHistoryModel;
import com.asset.contactstrategy.common.models.TLVOptionalModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.exceptions.ParserException;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.models.SubmitSMModel;
import com.asset.cs.vfesmscinterface.models.TLVModel;
import com.asset.cs.vfesmscinterface.socket.Session;
import com.asset.cs.vfesmscinterface.utils.ContactStrategyCaller;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author mohamed.morsy
 *
 */
public class SendSMSIntergrationRunnable implements Runnable {

    private static int sid = 1;

    private int id = 0;

    private Session session;

    private InputModel inputModel;

    private Future<?> writerFuture;

    private SubmitSMModel submitSMModel;

    private CsSmscInterfaceHistoryModel csSmscInterfaceHistoryModel;

    public SendSMSIntergrationRunnable(Session session, SubmitSMModel submitSMModel, Future<?> writerFuture) {
        this.submitSMModel = submitSMModel;
        this.session = session;
        this.writerFuture = writerFuture;
        inputModel = new InputModel();
        csSmscInterfaceHistoryModel = new CsSmscInterfaceHistoryModel();
        id = (sid++) % 10000;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(session.getSessionId() + ":SendSMSIntergrationThread:" + id);
        // to log submitSMModel itself if parsing fail
        String json = submitSMModel.toString();
        String response = null;
        long executionTime = 0;
        try {

            prepareInputModel();

            json = Utility.gsonObjectToJSONStringWithDateFormat(inputModel, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            int sleepTime = Integer.parseInt(Manager.systemProperities
                    .get(Defines.SMSC_INTERFACE_PROPERTIES.SMSC_INTERFACE_WRITE_FUTURE_TIMEOUT));
            writerFuture.get(sleepTime, TimeUnit.MILLISECONDS);

            executionTime = System.currentTimeMillis();
            CommonLogger.businessLogger.error("start call ContactStrategy sending request: " + json);
            response = ContactStrategyCaller.callContactStrategy(json);
            executionTime = System.currentTimeMillis() - executionTime;
            CommonLogger.businessLogger.error("end call ContactStrategy request take: " + executionTime + " msec");

        } catch (ExecutionException | CancellationException | InterruptedException e) {
            String msg = "response to smsc had an exception, cancel call ContactStrategy: ";
            CommonLogger.businessLogger.error(msg + e);
            CommonLogger.errorLogger.error(msg, e);
            executionTime = 0;
            csSmscInterfaceHistoryModel.setRequestError(e.getMessage());
        } catch (TimeoutException e) {
            String msg = "response to smsc timeout, cancel call ContactStrategy: ";
            CommonLogger.businessLogger.error(msg + e);
            CommonLogger.errorLogger.error(msg, e);
            executionTime = 0;
            csSmscInterfaceHistoryModel.setRequestError(e.getMessage());
        } catch (CommonException e) {
            executionTime = System.currentTimeMillis() - executionTime;
            String msg = "error in sending to contactstrategy, executionTime: " + executionTime + " msec: ";
            CommonLogger.businessLogger.error(msg + e);
            CommonLogger.errorLogger.error(msg, e);
            csSmscInterfaceHistoryModel.setRequestError(e.getErrorMsg());
        } catch (ParserException e) {
            String msg = "error in prepaing contact strategy request, cancel call ContactStrategy: " + submitSMModel;
            CommonLogger.businessLogger.error(msg + e);
            CommonLogger.errorLogger.error(msg, e);
            executionTime = 0;
            csSmscInterfaceHistoryModel.setRequestError(e.getMessage());
        } catch (Throwable th) {
            String msg = "general error: ";
            CommonLogger.businessLogger.error(msg + th);
            CommonLogger.errorLogger.error(msg, th);
            csSmscInterfaceHistoryModel.setRequestError(th.getMessage());
            executionTime = 0;
        } finally {
            csSmscInterfaceHistoryModel.setRequest(json);
            csSmscInterfaceHistoryModel.setResponse(response);
            csSmscInterfaceHistoryModel.setExecutionTime((int) executionTime);
            csSmscInterfaceHistoryModel.setSessionId(session.getSessionId());
            csSmscInterfaceHistoryModel.setSmscMessageId(submitSMModel.getSmscMessageId());

            try {
                Manager.sendSmsIntegrationArchiverTaskExecutor.put(csSmscInterfaceHistoryModel);
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error("Interruption while adding in csSmsArchiverQueue, log model"
                        + csSmscInterfaceHistoryModel + ", " + e);
                CommonLogger.errorLogger.error("Interruption while adding in csSmsArchiverQueue", e);
            }
        }
    }

    private void prepareInputModel() throws ParserException {

        try {
            parseOptionalMessage();
            inputModel.setOriginatorMSISDN(submitSMModel.getSource().getAddress());
            byte originatorType;
            switch (submitSMModel.getSource().getTon()) {
                case Data.GSM_TON_NETWORK:
                    originatorType = 0;
                    break;
                case Data.GSM_TON_INTERNATIONAL:
                    originatorType = 1;
                    break;
                default:
                    originatorType = 2;
            }
            inputModel.setOriginatorType(originatorType);
            inputModel.setDestinationMSISDN(submitSMModel.getDestination().getAddress());
            inputModel.setLanguage((byte) (submitSMModel.getDataCoding() == Data.DCS_Arabic ? Defines.LANGUAGE_ARABIC
                    : Defines.LANGUAGE_ENGLISH));
            inputModel.setServiceType(submitSMModel.getServiceType());
            inputModel.setEsmClass(submitSMModel.getEsmClass());
            inputModel.setProtocolId(submitSMModel.getProtocolId());
            inputModel.setPriorityFlag(submitSMModel.getPriority());
            inputModel.setScheduleDeliveryTime(submitSMModel.getScheduleDeliveryTime() == null ? null
                    : submitSMModel.getScheduleDeliveryTime().toString());
            inputModel.setValidityPeriod(
                    submitSMModel.getValidityPeriod() == null ? null : submitSMModel.getValidityPeriod().toString());
            inputModel.setSmDefaultMsgId(submitSMModel.getSmDefaultMsgId());
            inputModel.setMessageType((byte) 0);
            inputModel.setRequestId(Utility.generateTransId("SMSC_REQ_"));

            ArrayList<TLVOptionalModel> list = new ArrayList<>();
            for (Short key : submitSMModel.getTlvs().keySet()) {
                TLVModel tlvModel = submitSMModel.getTlvs().get(key);

                TLVOptionalModel tlov = new TLVOptionalModel(tlvModel.getTag(), tlvModel.getLength(),
                        tlvModel.getType(), tlvModel.getValue() == null ? null : tlvModel.getValue() + "");
                list.add(tlov);
            }
            inputModel.setTlvs(list);
            inputModel.setSystemPassword(session.getClientModel().getPassword());
        } catch (Exception e) {
            throw e instanceof ParserException ? (ParserException) e : new ParserException(e);
        }
    }

    private void parseOptionalMessage() throws ParserException, UnsupportedEncodingException {

        String key;
        String value;
        String messageText;
        boolean addSystemName = true;
        boolean addLanguage = true;

        if (submitSMModel.getSmLength() != 0 && submitSMModel.getMessage() != null
                && !submitSMModel.getMessage().isEmpty()) {
            messageText = submitSMModel.getMessage();
        } else if (submitSMModel.getMessagePayload() != null && submitSMModel.getMessagePayload().getValue() != null
                && !submitSMModel.getMessagePayload().getValue().isEmpty()) {
            messageText = submitSMModel.getMessagePayload().getValue();
        } else {
            throw new ParserException("invalid params, neither message text nor message payload found, sm_length: "
                    + submitSMModel.getSmLength() + ", short_sm: '" + submitSMModel.getMessage() + "', message payload: "
                    + submitSMModel.getMessagePayload());
        }
		
		if (!messageText.toLowerCase().contains("templatesids") && !messageText.toLowerCase().contains("messagetext")) {
			inputModel.setMessageText(messageText);
			inputModel.setSystemName(session.getClientModel().getSystemName());
			return;
		}
		
        for (String tmpString : messageText.split("&")) {
            String[] tmpSplit = tmpString.split("=");
            if (tmpSplit.length == 2) {
                key = tmpSplit[0];
                value = tmpSplit[1];
            } else if (tmpSplit.length > 2 && tmpSplit[0].equalsIgnoreCase("TemplatesParameters")) {
                key = tmpSplit[0];
                value = tmpString.substring(tmpString.indexOf('=') + 1);
            } else {
                throw new ParserException("invalid optional params, no key value format string:" + tmpString);
            }
            switch (key.toLowerCase()) {
                case "systemname":
                    inputModel.setSystemName(value);
                    addSystemName = false;
                    break;
                case "donotapply":
                    inputModel.setDoNotApply(value != null && value.equalsIgnoreCase("true"));
                    break;
                case "optionalparam1":
                    inputModel.setOptionalParam1(value);
                    break;
                case "optionalparam2":
                    inputModel.setOptionalParam2(value);
                    break;
                case "optionalparam3":
                    inputModel.setOptionalParam3(value);
                    break;
                case "optionalparam4":
                    inputModel.setOptionalParam4(value);
                    break;
                case "optionalparam5":
                    inputModel.setOptionalParam5(value);
                    break;
                case "templatesids":
                    inputModel.setTemplatesIds(value);
                    break;
                case "templatesparameters":
                    inputModel.setTemplatesParameters(value);
                    break;
                case "messagetext":
                    inputModel.setMessageText(value);
                    break;
                case "messagepriority":
                    inputModel.setMessagePriorityText(value);
                    break;
                default:
                    CommonLogger.businessLogger.error("not known params in message text: " + tmpString);
                    break;
            }

        }
        if (addSystemName) {
            inputModel.setSystemName(session.getClientModel().getSystemName());
        }
        if (addLanguage) {
            inputModel.setLanguage((byte) (submitSMModel.getDataCoding() == Data.DCS_Arabic || submitSMModel.getDataCoding() == Data.DCS_Arabic1
                    ? Defines.LANGUAGE_ARABIC
                    : Defines.LANGUAGE_ENGLISH));
        }
    }
}
