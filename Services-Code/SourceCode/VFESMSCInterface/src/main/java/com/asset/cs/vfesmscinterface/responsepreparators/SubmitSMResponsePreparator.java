/**
 * created on: Dec 14, 2017 8:54:45 AM created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.responsepreparators;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import static com.asset.cs.vfesmscinterface.constants.Data.*;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVDSTADR;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVDSTNPI;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVDSTTON;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVESMCLASS;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVPRTFLG;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVREPFLAG;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVSERTYP;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVSRCADR;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVSRCNPI;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RINVSRCTON;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_ROK;
import static com.asset.cs.vfesmscinterface.constants.Data.ESME_RSYSERR;
import static com.asset.cs.vfesmscinterface.constants.Data.PDU_HEADER_SIZE;
import static com.asset.cs.vfesmscinterface.constants.Data.SM_ADDR_LEN;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.constants.ConnectionStatusEnum;
import com.asset.cs.vfesmscinterface.models.CommandRequestModel;
import com.asset.cs.vfesmscinterface.models.CommandResponseModel;
import com.asset.cs.vfesmscinterface.models.SubmitSMModel;
import com.asset.cs.vfesmscinterface.models.SubmitSMResponseModel;
import com.asset.cs.vfesmscinterface.models.TLVModel;
import com.asset.cs.vfesmscinterface.socket.Session;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import java.util.HashMap;

/**
 * @author mohamed.morsy
 *
 */
public class SubmitSMResponsePreparator implements CommandResponsePreparator {

    @Override
    public CommandResponseModel validateAndPrepareResponse(Session session, CommandRequestModel command) {

        CommandResponseModel commandResponseModel = validateAndPrepareResponse(session.getConnectionStatus(), command);

        if (commandResponseModel == null) {
            commandResponseModel = prepareSucessResponse(command, session.getNextMessageId());
        }

        return commandResponseModel;
    }

    @Override
    public CommandResponseModel validateAndPrepareResponse(ConnectionStatusEnum status, CommandRequestModel command) {

        switch (status) {
            case BOUND:
                SubmitSMModel submitSMModel = (SubmitSMModel) command;

                if (!validateServiceType(submitSMModel.getServiceType())) {
//                    CommonLogger.businessLogger.debug("invalid service type: " + submitSMModel.getServiceType());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid Service")
                            .put(GeneralConstants.StructuredLogKeys.TYPE, submitSMModel.getServiceType()).build());
                    return prepareFailureResponse(command, ESME_RINVSERTYP);
                }

                if (!submitSMModel.getSource().validateTon()) {
//                    CommonLogger.businessLogger.debug("invalid source address ton: " + submitSMModel.getSource().getTon());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid Source Address")
                            .put(GeneralConstants.StructuredLogKeys.SOURCE_ADDRESS, submitSMModel.getSource().getTon()).build());
                    return prepareFailureResponse(command, ESME_RINVSRCTON);
                }

                if (!submitSMModel.getSource().validateNpi()) {
//                    CommonLogger.businessLogger.debug("invalid source address npi: " + submitSMModel.getSource().getNpi());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid Source Address")
                            .put(GeneralConstants.StructuredLogKeys.SOURCE_ADDRESS, submitSMModel.getSource().getNpi()).build());
                    return prepareFailureResponse(command, ESME_RINVSRCNPI);
                }

                if (!submitSMModel.getSource().validateAdress(SM_ADDR_LEN, true)) {
//                    CommonLogger.businessLogger.debug("invalid source address : " + submitSMModel.getSource().getAddress());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid Source Address")
                            .put(GeneralConstants.StructuredLogKeys.SOURCE_ADDRESS, submitSMModel.getSource().getAddress()).build());
                    return prepareFailureResponse(command, ESME_RINVSRCADR);
                }

                if (!submitSMModel.getDestination().validateTon()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid destination address ton: " + submitSMModel.getDestination().getTon());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid destination Address")
                            .put(GeneralConstants.StructuredLogKeys.DESTINATION_ADDRESS, submitSMModel.getDestination().getTon()).build());
                    return prepareFailureResponse(command, ESME_RINVDSTTON);
                }

                if (!submitSMModel.getDestination().validateNpi()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid destination address npi: " + submitSMModel.getDestination().getNpi());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid destination Address")
                            .put(GeneralConstants.StructuredLogKeys.DESTINATION_ADDRESS, submitSMModel.getDestination().getNpi()).build());
                    return prepareFailureResponse(command, ESME_RINVDSTNPI);
                }

                if (!submitSMModel.getDestination().validateAdress(SM_ADDR_LEN, false)) {
//                    CommonLogger.businessLogger
//                            .debug("invalid destination address : " + submitSMModel.getDestination().getAddress());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid destination Address")
                            .put(GeneralConstants.StructuredLogKeys.DESTINATION_ADDRESS, submitSMModel.getDestination().getAddress()).build());
                    return prepareFailureResponse(command, ESME_RINVDSTADR);
                }

                if (!validateEsmClass(submitSMModel.getEsmClass())) {
//                    CommonLogger.businessLogger.debug("invalid esm class : " + submitSMModel.getEsmClass());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid esm Class")
                            .put(GeneralConstants.StructuredLogKeys.ESM_CLASS, submitSMModel.getEsmClass()).build());
                    return prepareFailureResponse(command, ESME_RINVESMCLASS);
                }

                // no validation on protocol id
                if (!validatePriorityFlag(submitSMModel.getPriority())) {
//                    CommonLogger.businessLogger.debug("invalid priority_flag : " + submitSMModel.getPriority());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid Priority Flag")
                            .put(GeneralConstants.StructuredLogKeys.PRIORITY, submitSMModel.getPriority()).build());
                    return prepareFailureResponse(command, ESME_RINVPRTFLG);
                }

                // no validate schedule_delivery_time
                // no validation on registered_delivery
                if (!validatereplaceIfPresentFlag(submitSMModel.getReplaceIfPresent())) {
//                    CommonLogger.businessLogger
//                            .debug("invalid replace_if_present_flag : " + submitSMModel.getReplaceIfPresent());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid replace_if_present_flag")
                            .put(GeneralConstants.StructuredLogKeys.REPLACE_IF_PRESENT_FLAG, submitSMModel.getReplaceIfPresent()).build());
                    return prepareFailureResponse(command, ESME_RINVREPFLAG);
                }

                if (!validateDataCoding(submitSMModel.getDataCoding())) {
//                    CommonLogger.businessLogger.debug("invalid data_coding : " + submitSMModel.getDataCoding());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid data_coding ")
                            .put(GeneralConstants.StructuredLogKeys.DATA_CODING, submitSMModel.getDataCoding()).build());
                    return prepareFailureResponse(command, ESME_RSYSERR);
                }

                // no validation for sm_default_msg_id
                // sm_length
                if (!validateSmLength(submitSMModel.getSmLength())) {
//                    CommonLogger.businessLogger.debug("invalid sm_length : " + submitSMModel.getDataCoding());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid sm_length ")
                            .put(GeneralConstants.StructuredLogKeys.DATA_CODING, submitSMModel.getDataCoding()).build());
                    return prepareFailureResponse(command, ESME_RSYSERR);
                }

                if (submitSMModel.getMessagePayload() != null && (submitSMModel.getSmLength() != 0 || !submitSMModel.getMessage().isEmpty())) {
//                    CommonLogger.businessLogger.debug("not allowed to send message payload with short sm, messagePayload: " + submitSMModel.getMessagePayload() + ", length: " + submitSMModel.getSmLength() + ", shortsm: " + submitSMModel.getMessage());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Not Allowed to Send Message Payload with short sm")
                            .put(GeneralConstants.StructuredLogKeys.MSG_PAYLOAD, submitSMModel.getMessagePayload())
                            .put(GeneralConstants.StructuredLogKeys.LENGTH, submitSMModel.getSmLength())
                            .put(GeneralConstants.StructuredLogKeys.SMS_SCRIPT, submitSMModel.getMessage()).build());
                    return prepareFailureResponse(command, ESME_ROPTPARNOTALLWD);
                }

                // begin optional params
                if (submitSMModel.getAlertOnMessageDelivery() != null
                        && !submitSMModel.getAlertOnMessageDelivery().validateModel()) {
//                    CommonLogger.businessLogger.debug(
//                            "invalid AlertOnMessageDelivery: " + submitSMModel.getAlertOnMessageDelivery().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "invalid AlertOnMessageDelivery")
                            .put(GeneralConstants.StructuredLogKeys.ALERT_MSG_DELIVERY, submitSMModel.getAlertOnMessageDelivery().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getCallbackNum() != null && !submitSMModel.getCallbackNum().validateModel()) {
//                    CommonLogger.businessLogger.debug("invalid CallbackNum: " + submitSMModel.getCallbackNum().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid CallbackNum")
                            .put(GeneralConstants.StructuredLogKeys.CALLBACK_NUM, submitSMModel.getCallbackNum().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getCallbackNumAtag() != null && !submitSMModel.getCallbackNumAtag().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid CallbackNumAtag: " + submitSMModel.getCallbackNumAtag().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid CallbackNumATag")
                            .put(GeneralConstants.StructuredLogKeys.CALLBACK_NUM_A_TAG, submitSMModel.getCallbackNum().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getCallbackNumPresInd() != null
                        && !submitSMModel.getCallbackNumPresInd().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid CallbackNumPresInd: " + submitSMModel.getCallbackNumPresInd().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid CallbackNumPresInd")
                            .put(GeneralConstants.StructuredLogKeys.CALLBACK_NUM_PRES_IND, submitSMModel.getCallbackNumPresInd().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getDestAddrSubunit() != null && !submitSMModel.getDestAddrSubunit().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid DestAddrSubunit: " + submitSMModel.getDestAddrSubunit().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid DestyAddRSubmit")
                            .put(GeneralConstants.StructuredLogKeys.DEST_ADDR_SUBMIT, submitSMModel.getDestAddrSubunit().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getDestinationPort() != null && !submitSMModel.getDestinationPort().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid DestinationPort: " + submitSMModel.getDestinationPort().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid DestPort")
                            .put(GeneralConstants.StructuredLogKeys.GET_DEST_PORT, submitSMModel.getDestinationPort().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getDestSubaddress() != null && !submitSMModel.getDestSubaddress().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid DestSubaddress: " + submitSMModel.getDestSubaddress().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid DestSubAddress")
                            .put(GeneralConstants.StructuredLogKeys.DEST_SUB_ADDRESS, submitSMModel.getDestSubaddress().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getDisplayTime() != null && !submitSMModel.getDisplayTime().validateModel()) {
//                    CommonLogger.businessLogger.debug("invalid DisplayTime: " + submitSMModel.getDisplayTime().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid DisplayTime")
                            .put(GeneralConstants.StructuredLogKeys.DISPLAY_TIME, submitSMModel.getDisplayTime().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getItsReplyType() != null && !submitSMModel.getItsReplyType().validateModel()) {
//                    CommonLogger.businessLogger.debug("invalid ItsReplayType: " + submitSMModel.getItsReplyType().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid ItsReplayType")
                            .put(GeneralConstants.StructuredLogKeys.ITS_REPLAY_TYPE, submitSMModel.getItsReplyType().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getItsSessionInfo() != null && !submitSMModel.getItsSessionInfo().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid ItsSessionInfo: " + submitSMModel.getItsSessionInfo().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid ItsSessionInfo")
                            .put(GeneralConstants.StructuredLogKeys.ITS_SESSION_INFO, submitSMModel.getItsSessionInfo().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getLanguageIndicator() != null && !submitSMModel.getLanguageIndicator().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid LanguageIndicator: " + submitSMModel.getLanguageIndicator().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid LanguageIndicator")
                            .put(GeneralConstants.StructuredLogKeys.LANGUAGE_INDICATOR, submitSMModel.getLanguageIndicator().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getMessagePayload() != null && !submitSMModel.getMessagePayload().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid MessagePayload: " + submitSMModel.getMessagePayload().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid MessagePayload")
                            .put(GeneralConstants.StructuredLogKeys.MSG_PAYLOAD, submitSMModel.getMessagePayload().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getMoreMessagesToSend() != null
                        && !submitSMModel.getMoreMessagesToSend().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid MoreMessagesToSend: " + submitSMModel.getMoreMessagesToSend().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid MoreToSend")
                            .put(GeneralConstants.StructuredLogKeys.MORE_MESSAGE_TO_SEND, submitSMModel.getMoreMessagesToSend().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getMsMsgWaitFacilities() != null
                        && !submitSMModel.getMsMsgWaitFacilities().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid MsMsgWaitFacilities: " + submitSMModel.getMsMsgWaitFacilities().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid MsMsgWaitFacility")
                            .put(GeneralConstants.StructuredLogKeys.MS_MSG_WAIT_FACILITY, submitSMModel.getMsMsgWaitFacilities().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getMsValidity() != null && !submitSMModel.getMsValidity().validateModel()) {
//                    CommonLogger.businessLogger.debug("invalid MsValidity: " + submitSMModel.getMsValidity().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid MsValidity")
                            .put(GeneralConstants.StructuredLogKeys.MS_VALIDITY, submitSMModel.getMsValidity().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getNumberOfMessages() != null && !submitSMModel.getNumberOfMessages().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid NumberOfMessages: " + submitSMModel.getNumberOfMessages().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid NumberOfMessages")
                            .put(GeneralConstants.StructuredLogKeys.NUMBER_OF_MESSAGES, submitSMModel.getNumberOfMessages().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getPayloadType() != null && !submitSMModel.getPayloadType().validateModel()) {
//                    CommonLogger.businessLogger.debug("invalid PayloadType: " + submitSMModel.getPayloadType().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid PayloadType")
                            .put(GeneralConstants.StructuredLogKeys.PAYLOAD_TYPE, submitSMModel.getPayloadType().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getPrivacyIndicator() != null && !submitSMModel.getPrivacyIndicator().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid PrivacyIndicator: " + submitSMModel.getPrivacyIndicator().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid PrivacyIndicator")
                            .put(GeneralConstants.StructuredLogKeys.PRIVACY_INDICATOR, submitSMModel.getPrivacyIndicator().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }

                if (submitSMModel.getSarMsgRefNum() != null && submitSMModel.getSarSegmentsSeqnum() != null
                        && submitSMModel.getSarTotalSegments() != null) {
                    if (!submitSMModel.getSarMsgRefNum().validateModel()) {
//                        CommonLogger.businessLogger
//                                .debug("invalid SarMsgRefNum: " + submitSMModel.getSarMsgRefNum().getValue());
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid SarMsgRefNum")
                                .put(GeneralConstants.StructuredLogKeys.SAR_MSG_REF_NUM, submitSMModel.getSarMsgRefNum().getValue()).build());
                        return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                    }
                    if (!submitSMModel.getSarSegmentsSeqnum().validateModel()) {
//                        CommonLogger.businessLogger
//                                .debug("invalid SarSegmentsSeqnum: " + submitSMModel.getSarSegmentsSeqnum().getValue());
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid SarSegmentSeqNum")
                                .put(GeneralConstants.StructuredLogKeys.SAR_SEGMENT_REF_NUM, submitSMModel.getSarSegmentsSeqnum().getValue()).build());
                        return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                    }
                    if (!submitSMModel.getSarTotalSegments().validateModel()) {
//                        CommonLogger.businessLogger
//                                .debug("invalid SarTotalSegments: " + submitSMModel.getSarTotalSegments().getValue());
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid SarTotalSegments")
                                .put(GeneralConstants.StructuredLogKeys.SAR_TOTAL_SEGMENTS, submitSMModel.getSarTotalSegments().getValue()).build());
                        return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                    }
                    submitSMModel.setIisSingelMessage(false);
                } else if (submitSMModel.getSarMsgRefNum() == null && submitSMModel.getSarSegmentsSeqnum() == null
                        && submitSMModel.getSarTotalSegments() == null) {
                    submitSMModel.setIisSingelMessage(true);
                } else {
                    CommonLogger.businessLogger.debug(String.format(
                            "missing one of the following SarTotalSegments: %s, SarSegmentsSeqnum: %s, SarMsgRefNum: %s",
                            submitSMModel.getSarTotalSegments(), submitSMModel.getSarSegmentsSeqnum(),
                            submitSMModel.getSarMsgRefNum()));
                    return prepareFailureResponse(command, ESME_RMISSINGOPTPARAM);
                }

                if (submitSMModel.getSmsSignal() != null && !submitSMModel.getSmsSignal().validateModel()) {
//                    CommonLogger.businessLogger.debug("invalid SmsSignal: " + submitSMModel.getSmsSignal().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid SmsSignal")
                            .put(GeneralConstants.StructuredLogKeys.SMS_SIGNAL, submitSMModel.getSmsSignal().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getSourceAddrSubunit() != null && !submitSMModel.getSourceAddrSubunit().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid SourceAddrSubunit: " + submitSMModel.getSourceAddrSubunit().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid SourceAddrSubmit")
                            .put(GeneralConstants.StructuredLogKeys.SOURCE_ADDR_SUBMIT, submitSMModel.getSourceAddrSubunit().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getSourcePort() != null && !submitSMModel.getSourcePort().validateModel()) {
//                    CommonLogger.businessLogger.debug("invalid SourcePort: " + submitSMModel.getSourcePort().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid SourcePort")
                            .put(GeneralConstants.StructuredLogKeys.SOURCE_PORT, submitSMModel.getSourcePort().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getSourceSubaddress() != null && !submitSMModel.getSourceSubaddress().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid SourceSubaddress: " + submitSMModel.getSourceSubaddress().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid SourceSUbAddress")
                            .put(GeneralConstants.StructuredLogKeys.SOURCE_SUB_ADDRESS, submitSMModel.getSourceSubaddress().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getUserMessageReference() != null
                        && !submitSMModel.getUserMessageReference().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid UserMessageReference: " + submitSMModel.getUserMessageReference().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid UserMessagereference")
                            .put(GeneralConstants.StructuredLogKeys.USER_MESSAGE_REFERENCE, submitSMModel.getUserMessageReference().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getUserResponseCode() != null && !submitSMModel.getUserResponseCode().validateModel()) {
//                    CommonLogger.businessLogger
//                            .debug("invalid UserResponseCode: " + submitSMModel.getUserResponseCode().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid UserResponseCode")
                            .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, submitSMModel.getUserResponseCode().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }
                if (submitSMModel.getUssdServiceOp() != null && !submitSMModel.getUssdServiceOp().validateModel()) {
//                    CommonLogger.businessLogger.debug("invalid UssdServiceOp: " + submitSMModel.getUssdServiceOp().getValue());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invalid UssdServiceOp")
                            .put(GeneralConstants.StructuredLogKeys.USSD_SERVICE_OP, submitSMModel.getUssdServiceOp().getValue()).build());
                    return prepareFailureResponse(command, ESME_RINVOPTPARAMVAL);
                }

                // end optional params
                return null;
            default:
                return prepareFailureResponse(command, ESME_RSYSERR);
        }
    }

    private CommandResponseModel prepareSucessResponse(CommandRequestModel command, String messageId) {

        ((SubmitSMModel) command).setSmscMessageId(messageId);

        int HEADER_SIZE = PDU_HEADER_SIZE + messageId.length() + 1; // 1 for null character

        CommandResponseModel commandResponseModel = prepareHeaderResponse(HEADER_SIZE,
                getResponseCommandId(command.getHeaderModel()), ESME_ROK, command.getHeaderModel().getSequenceNumber(),
                command.getTlvs());

        ((SubmitSMResponseModel) commandResponseModel).setMessageId(messageId);
        commandResponseModel.getPdu().appendCString(messageId);

        return commandResponseModel;
    }

    private CommandResponseModel prepareFailureResponse(CommandRequestModel command, int commandStatus) {

        return prepareHeaderResponse(PDU_HEADER_SIZE, getResponseCommandId(command.getHeaderModel()), commandStatus,
                command.getHeaderModel().getSequenceNumber(), command.getTlvs());
    }

    private CommandResponseModel prepareHeaderResponse(int commandLength, int responseCommandId, int commandStatus,
            int sequenceNumber, HashMap<Short, TLVModel> tlvs) {
        CommandResponseModel submitSMResponseModel = new SubmitSMResponseModel();
        BufferByte pdu = new BufferByte();

        pdu.appendInt(commandLength);
        submitSMResponseModel.getHeaderModel().setCommandLength(commandLength);

        pdu.appendInt(responseCommandId);
        submitSMResponseModel.getHeaderModel().setCommandID(responseCommandId);

        pdu.appendInt(commandStatus);
        submitSMResponseModel.getHeaderModel().setCommandStatus(commandStatus);

        pdu.appendInt(sequenceNumber);
        submitSMResponseModel.getHeaderModel().setSequenceNumber(sequenceNumber);

        submitSMResponseModel.setPdu(pdu);
//                submitSMResponseModel.setTlvs(tlvs);
        return submitSMResponseModel;
    }

    private boolean validateServiceType(String serviceType) {
        return serviceType == null || serviceType.equalsIgnoreCase("") || serviceType.equalsIgnoreCase("CMT")
                || serviceType.equalsIgnoreCase("CPT") || serviceType.equalsIgnoreCase("VMN")
                || serviceType.equalsIgnoreCase("VMA") || serviceType.equalsIgnoreCase("WAP")
                || serviceType.equalsIgnoreCase("USSD");
    }

    private boolean validateEsmClass(byte esmClass) {
        // this implementation just for submit_sm, submit_multi and data_sm only
        String esmClassString = String.format("%8s", Integer.toBinaryString(esmClass)).replace(' ', '0');
        String changePart = esmClassString.substring(2, 6);
        return changePart.equals("0000") || changePart.equals("0010") || changePart.equals("0100");
    }

    private boolean validatePriorityFlag(byte priorityFlag) {
        return priorityFlag >= 0 && priorityFlag <= 3;
    }

    private boolean validatereplaceIfPresentFlag(byte replaceIfPresentFlag) {
        return replaceIfPresentFlag >= 0 && replaceIfPresentFlag <= 1;
    }

    private boolean validateDataCoding(byte dataCoding) {
        return dataCoding >= 0 && dataCoding <= 15;
    }

    private boolean validateSmLength(int smLength) {
        return smLength >= 0 && smLength <= 254;
    }

}
