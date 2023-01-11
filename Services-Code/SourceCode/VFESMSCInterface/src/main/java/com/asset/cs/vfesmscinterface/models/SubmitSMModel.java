package com.asset.cs.vfesmscinterface.models;

import java.time.LocalDateTime;
import java.util.HashMap;

import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.models.optional.AlertOnMessageDelivery;
import com.asset.cs.vfesmscinterface.models.optional.CallbackNum;
import com.asset.cs.vfesmscinterface.models.optional.CallbackNumAtag;
import com.asset.cs.vfesmscinterface.models.optional.CallbackNumPresInd;
import com.asset.cs.vfesmscinterface.models.optional.DestAddrSubunit;
import com.asset.cs.vfesmscinterface.models.optional.DestSubaddress;
import com.asset.cs.vfesmscinterface.models.optional.DestinationPort;
import com.asset.cs.vfesmscinterface.models.optional.DisplayTime;
import com.asset.cs.vfesmscinterface.models.optional.ItsReplyType;
import com.asset.cs.vfesmscinterface.models.optional.ItsSessionInfo;
import com.asset.cs.vfesmscinterface.models.optional.LanguageIndicator;
import com.asset.cs.vfesmscinterface.models.optional.MessagePayload;
import com.asset.cs.vfesmscinterface.models.optional.MoreMessagesToSend;
import com.asset.cs.vfesmscinterface.models.optional.MsMsgWaitFacilities;
import com.asset.cs.vfesmscinterface.models.optional.MsValidity;
import com.asset.cs.vfesmscinterface.models.optional.NumberOfMessages;
import com.asset.cs.vfesmscinterface.models.optional.PayloadType;
import com.asset.cs.vfesmscinterface.models.optional.PrivacyIndicator;
import com.asset.cs.vfesmscinterface.models.optional.SarMsgRefNum;
import com.asset.cs.vfesmscinterface.models.optional.SarSegmentsSeqnum;
import com.asset.cs.vfesmscinterface.models.optional.SarTotalSegments;
import com.asset.cs.vfesmscinterface.models.optional.SmsSignal;
import com.asset.cs.vfesmscinterface.models.optional.SourceAddrSubunit;
import com.asset.cs.vfesmscinterface.models.optional.SourcePort;
import com.asset.cs.vfesmscinterface.models.optional.SourceSubaddress;
import com.asset.cs.vfesmscinterface.models.optional.UserMessageReference;
import com.asset.cs.vfesmscinterface.models.optional.UserResponseCode;
import com.asset.cs.vfesmscinterface.models.optional.UssdServiceOp;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 * Submit a message to the SMSC for delivery to a single destination. Relevant
 * inherited fields from SMPPPacket: <br>
 *
 * @author Oran Kelly
 * @version 1.0
 */
public class SubmitSMModel extends CommandRequestModel {

    /**
     * not a part from SMPP protocol, generated from SMSC interface, to be send
     * to contact strategy
     */
    private String smscMessageId;

    /**
     * not a part from SMPP protocol, generated from SMSC interface, to
     * distinguish between single or divided message
     */
    private boolean isSingelMessage;

    /**
     * not a part from SMPP protocol, generated from SMSC interface, delete old
     * parts which it's parts lost or rejected
     */
    private LocalDateTime lastUpdate;

    private String serviceType;

    private AddressModel source;

    private AddressModel destination;

    private byte esmClass;

    private byte protocolId;

    private byte priority;

    private SMPPDate scheduleDeliveryTime;

    private SMPPDate validityPeriod;

    private byte registeredDelivery;

    private byte replaceIfPresent;

    private byte dataCoding;

    private byte smDefaultMsgId;

    // datatype of smLength is byte in SMMP protocol
    // but for signed problem and in combined message sm length
    // will be greater than byte limit we store it as integer
    private int smLength;

    private String message;

    private AlertOnMessageDelivery alertOnMessageDelivery;
    private CallbackNum callbackNum;
    private CallbackNumAtag callbackNumAtag;
    private CallbackNumPresInd callbackNumPresInd;
    private DestAddrSubunit destAddrSubunit;
    private DestinationPort destinationPort;
    private DestSubaddress destSubaddress;
    private DisplayTime displayTime;
    private ItsReplyType itsReplyType;
    private ItsSessionInfo itsSessionInfo;
    private LanguageIndicator languageIndicator;
    private MessagePayload messagePayload;
    private MoreMessagesToSend moreMessagesToSend;
    private MsMsgWaitFacilities msMsgWaitFacilities;
    private MsValidity msValidity;
    private NumberOfMessages numberOfMessages;
    private PayloadType payloadType;
    private PrivacyIndicator privacyIndicator;
    private SarMsgRefNum sarMsgRefNum;
    private SarSegmentsSeqnum sarSegmentsSeqnum;
    private SarTotalSegments sarTotalSegments;
    private SmsSignal smsSignal;
    private SourceAddrSubunit sourceAddrSubunit;
    private SourcePort sourcePort;
    private SourceSubaddress sourceSubaddress;
    private UserMessageReference userMessageReference;
    private UserResponseCode userResponseCode;
    private UssdServiceOp ussdServiceOp;

    public SubmitSMModel(HeaderModel headerModel) {
        super(headerModel);
    }

    /**
     * used in combine divided messages doesn't copy all fields due to
     * requirements
     *
     * @param submitSMModel
     * @param headerModel
     * @param smscMessageId
     */
    public SubmitSMModel(SubmitSMModel submitSMModel) {
        super(null); // not used in combine message

        this.serviceType = submitSMModel.serviceType;
        this.source = submitSMModel.source;
        this.destination = submitSMModel.destination;
        this.esmClass = submitSMModel.esmClass;
        this.protocolId = submitSMModel.protocolId;
        this.priority = submitSMModel.priority;
        this.scheduleDeliveryTime = submitSMModel.scheduleDeliveryTime;
        this.validityPeriod = submitSMModel.validityPeriod;
        this.registeredDelivery = submitSMModel.registeredDelivery;
        this.replaceIfPresent = submitSMModel.replaceIfPresent;
        this.dataCoding = submitSMModel.dataCoding;
        this.smDefaultMsgId = submitSMModel.smDefaultMsgId;
        this.message = submitSMModel.message;
        this.alertOnMessageDelivery = submitSMModel.alertOnMessageDelivery;
        this.callbackNum = submitSMModel.callbackNum;
        this.callbackNumAtag = submitSMModel.callbackNumAtag;
        this.callbackNumPresInd = submitSMModel.callbackNumPresInd;
        this.destAddrSubunit = submitSMModel.destAddrSubunit;
        this.destinationPort = submitSMModel.destinationPort;
        this.destSubaddress = submitSMModel.destSubaddress;
        this.displayTime = submitSMModel.displayTime;
        this.itsReplyType = submitSMModel.itsReplyType;
        this.itsSessionInfo = submitSMModel.itsSessionInfo;
        this.languageIndicator = submitSMModel.languageIndicator;
        this.moreMessagesToSend = submitSMModel.moreMessagesToSend;
        this.msMsgWaitFacilities = submitSMModel.msMsgWaitFacilities;
        this.msValidity = submitSMModel.msValidity;
        this.numberOfMessages = submitSMModel.numberOfMessages;
        this.privacyIndicator = submitSMModel.privacyIndicator;
        this.smsSignal = submitSMModel.smsSignal;
        this.sourceAddrSubunit = submitSMModel.sourceAddrSubunit;
        this.sourcePort = submitSMModel.sourcePort;
        this.sourceSubaddress = submitSMModel.sourceSubaddress;
        this.userMessageReference = submitSMModel.userMessageReference;
        this.userResponseCode = submitSMModel.userResponseCode;
        this.ussdServiceOp = submitSMModel.ussdServiceOp;

        this.setTlvs(submitSMModel.getTlvs());

        // due to requirements, discard the following values
        getTlvs().remove(Data.OPT_PAR_SAR_MSG_REF_NUM);
        getTlvs().remove(Data.OPT_PAR_SAR_TOT_SEG);
        getTlvs().remove(Data.OPT_PAR_SAR_SEG_SNUM);
        getTlvs().remove(Data.OPT_PAR_PAYLOAD_TYPE);
        getTlvs().remove(Data.OPT_PAR_MSG_PAYLOAD);
        // this.payloadType = submitSMModel.payloadType;
        // this.smLength = submitSMModel.smLength;
        // this.messagePayload = submitSMModel.messagePayload;
        // this.sarMsgRefNum = submitSMModel.sarMsgRefNum;
        // this.sarSegmentsSeqnum = submitSMModel.sarSegmentsSeqnum;
        // this.sarTotalSegments = submitSMModel.sarTotalSegments;
    }

    public void setAlertOnMessageDelivery(BufferByte pdu) {
        alertOnMessageDelivery = new AlertOnMessageDelivery(pdu);
    }

    public void setCallbackNum(BufferByte pdu) {
        callbackNum = new CallbackNum(pdu);
    }

    public void setCallbackNumAtag(BufferByte pdu) {
        callbackNumAtag = new CallbackNumAtag(pdu);
    }

    public void setCallbackNumPresInd(BufferByte pdu) {
        callbackNumPresInd = new CallbackNumPresInd(pdu);
    }

    public void setDestAddrSubunit(BufferByte pdu) {
        destAddrSubunit = new DestAddrSubunit(pdu);
    }

    public void setDestinationPort(BufferByte pdu) {
        destinationPort = new DestinationPort(pdu);
    }

    public void setDestSubaddress(BufferByte pdu) {
        destSubaddress = new DestSubaddress(pdu);
    }

    public void setDisplayTime(BufferByte pdu) {
        displayTime = new DisplayTime(pdu);
    }

    public void setItsReplayType(BufferByte pdu) {
        itsReplyType = new ItsReplyType(pdu);
    }

    public void setItsSessionInfo(BufferByte pdu) {
        itsSessionInfo = new ItsSessionInfo(pdu);
    }

    public void setLanguageIndicator(BufferByte pdu) {
        languageIndicator = new LanguageIndicator(pdu);
    }

    public void setMessagePayload(BufferByte pdu) {
        messagePayload = new MessagePayload(pdu);
    }

    public void setMoreMessagesToSend(BufferByte pdu) {
        moreMessagesToSend = new MoreMessagesToSend(pdu);
    }

    public void setMsMsgWaitFacilities(BufferByte pdu) {
        msMsgWaitFacilities = new MsMsgWaitFacilities(pdu);
    }

    public void setMsValidity(BufferByte pdu) {
        msValidity = new MsValidity(pdu);
    }

    public void setNumberOfMessages(BufferByte pdu) {
        numberOfMessages = new NumberOfMessages(pdu);
    }

    public void setPayloadType(BufferByte pdu) {
        payloadType = new PayloadType(pdu);
    }

    public void setPrivacyIndicator(BufferByte pdu) {
        privacyIndicator = new PrivacyIndicator(pdu);
    }

    public void setSarMsgRefNum(BufferByte pdu) {
        sarMsgRefNum = new SarMsgRefNum(pdu);
    }

    public void setSarSegmentsSeqnum(BufferByte pdu) {
        sarSegmentsSeqnum = new SarSegmentsSeqnum(pdu);
    }

    public void setSarTotalSegments(BufferByte pdu) {
        sarTotalSegments = new SarTotalSegments(pdu);
    }

    public void setSmsSignal(BufferByte pdu) {
        smsSignal = new SmsSignal(pdu);
    }

    public void setSourceAddrSubunit(BufferByte pdu) {
        sourceAddrSubunit = new SourceAddrSubunit(pdu);
    }

    public void setSourcePort(BufferByte pdu) {
        sourcePort = new SourcePort(pdu);
    }

    public void setSourceSubaddress(BufferByte pdu) {
        sourceSubaddress = new SourceSubaddress(pdu);
    }

    public void setUserMessageReference(BufferByte pdu) {
        userMessageReference = new UserMessageReference(pdu);
    }

    public void setUserResponseCode(BufferByte pdu) {
        userResponseCode = new UserResponseCode(pdu);
    }

    public void setUssdServiceOp(BufferByte pdu) {
        ussdServiceOp = new UssdServiceOp(pdu);
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public AddressModel getSource() {
        return source;
    }

    public void setSource(AddressModel source) {
        this.source = source;
    }

    public AddressModel getDestination() {
        return destination;
    }

    public void setDestination(AddressModel destination) {
        this.destination = destination;
    }

    public byte getEsmClass() {
        return esmClass;
    }

    public void setEsmClass(byte esmClass) {
        this.esmClass = esmClass;
    }

    public byte getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(byte protocolId) {
        this.protocolId = protocolId;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public SMPPDate getScheduleDeliveryTime() {
        return scheduleDeliveryTime;
    }

    public void setScheduleDeliveryTime(SMPPDate scheduleDeliveryTime) {
        this.scheduleDeliveryTime = scheduleDeliveryTime;
    }

    public SMPPDate getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(SMPPDate validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public byte getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(byte registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public byte getReplaceIfPresent() {
        return replaceIfPresent;
    }

    public void setReplaceIfPresent(byte replaceIfPresent) {
        this.replaceIfPresent = replaceIfPresent;
    }

    public byte getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(byte dataCoding) {
        this.dataCoding = dataCoding;
    }

    public byte getSmDefaultMsgId() {
        return smDefaultMsgId;
    }

    public void setSmDefaultMsgId(byte smDefaultMsgId) {
        this.smDefaultMsgId = smDefaultMsgId;
    }

    /**
     * datatype of smLength is byte in SMMP protocol but for signed problem and
     * in combined message sm length will be greater than byte limit we store it
     * as integer
     *
     * @return
     */
    public int getSmLength() {
        return smLength;
    }

    /**
     * datatype of smLength is byte in SMMP protocol but for signed problem and
     * in combined message sm length will be greater than byte limit we store it
     * as integer
     *
     * @param smLength
     */
    public void setSmLength(int smLength) {
        this.smLength = smLength;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertOnMessageDelivery getAlertOnMessageDelivery() {
        return alertOnMessageDelivery;
    }

    public CallbackNum getCallbackNum() {
        return callbackNum;
    }

    public CallbackNumAtag getCallbackNumAtag() {
        return callbackNumAtag;
    }

    public CallbackNumPresInd getCallbackNumPresInd() {
        return callbackNumPresInd;
    }

    public DestAddrSubunit getDestAddrSubunit() {
        return destAddrSubunit;
    }

    public DestinationPort getDestinationPort() {
        return destinationPort;
    }

    public DestSubaddress getDestSubaddress() {
        return destSubaddress;
    }

    public DisplayTime getDisplayTime() {
        return displayTime;
    }

    public ItsReplyType getItsReplyType() {
        return itsReplyType;
    }

    public ItsSessionInfo getItsSessionInfo() {
        return itsSessionInfo;
    }

    public LanguageIndicator getLanguageIndicator() {
        return languageIndicator;
    }

    public MessagePayload getMessagePayload() {
        return messagePayload;
    }

    public MoreMessagesToSend getMoreMessagesToSend() {
        return moreMessagesToSend;
    }

    public MsMsgWaitFacilities getMsMsgWaitFacilities() {
        return msMsgWaitFacilities;
    }

    public MsValidity getMsValidity() {
        return msValidity;
    }

    public NumberOfMessages getNumberOfMessages() {
        return numberOfMessages;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public PrivacyIndicator getPrivacyIndicator() {
        return privacyIndicator;
    }

    public SarMsgRefNum getSarMsgRefNum() {
        return sarMsgRefNum;
    }

    public SarSegmentsSeqnum getSarSegmentsSeqnum() {
        return sarSegmentsSeqnum;
    }

    public SarTotalSegments getSarTotalSegments() {
        return sarTotalSegments;
    }

    public SmsSignal getSmsSignal() {
        return smsSignal;
    }

    public SourceAddrSubunit getSourceAddrSubunit() {
        return sourceAddrSubunit;
    }

    public SourcePort getSourcePort() {
        return sourcePort;
    }

    public SourceSubaddress getSourceSubaddress() {
        return sourceSubaddress;
    }

    public UserMessageReference getUserMessageReference() {
        return userMessageReference;
    }

    public UserResponseCode getUserResponseCode() {
        return userResponseCode;
    }

    public UssdServiceOp getUssdServiceOp() {
        return ussdServiceOp;
    }

    /**
     * generated from smsc interface and not a part from SMPP protocol, to be
     * send to contact strategy
     *
     * @return
     */
    public String getSmscMessageId() {
        return smscMessageId;
    }

    /**
     * generated from smsc interface and not a part from SMPP protocol, to be
     * send to contact strategy
     *
     * @param smscMessageId
     */
    public void setSmscMessageId(String smscMessageId) {
        this.smscMessageId = smscMessageId;
    }

    /**
     * not a part from smsc protocol, generated from smsc interface, to
     * distinguish between single or divided message
     *
     * @return
     */
    public boolean isSingelMessage() {
        return isSingelMessage;
    }

    /**
     * not a part from smsc protocol, generated from smsc interface, to
     * distinguish between single or divided message
     *
     * @param isSingelMessage
     */
    public void setIisSingelMessage(boolean isSingelMessage) {
        this.isSingelMessage = isSingelMessage;
    }

    /**
     * not a part from smsc protocol, generated from smsc interface, delete old
     * parts which it's parts lost or rejected
     *
     * @return
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * not a part from smsc protocol, generated from smsc interface, delete old
     * parts which it's parts lost or rejected
     *
     * @param lastUpdate
     */
    public void updatetLastUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("SubmitSMModel [");
        builder.append(headerModel);
        builder.append(", Body:{");
        builder.append(" service_type=");
        builder.append(serviceType);
        builder.append(", source=");
        builder.append(source);
        builder.append(", destination=");
        builder.append(destination);
        builder.append(", esm_class=");
        builder.append(esmClass);
        builder.append(", protocol_id=");
        builder.append(protocolId);
        builder.append(", priority_flag=");
        builder.append(priority);
        builder.append(", schedule_delivery_time=");
        builder.append(scheduleDeliveryTime);
        builder.append(", validity_period=");
        builder.append(validityPeriod);
        builder.append(", registered_delivery=");
        builder.append(registeredDelivery);
        builder.append(", replace_if_present=");
        builder.append(replaceIfPresent);
        builder.append(", data_coding=");
        builder.append(dataCoding);
        builder.append(", sm_default_msg_id=");
        builder.append(smDefaultMsgId);
        builder.append(", sm_length=");
        builder.append(smLength);
        builder.append(", message=");
        builder.append(message);
        builder.append(", alert_on_message_delivery=");
        builder.append(alertOnMessageDelivery);
        builder.append(", callback_num=");
        builder.append(callbackNum);
        builder.append(", callback_num_atag=");
        builder.append(callbackNumAtag);
        builder.append(", callback_num_pres_ind=");
        builder.append(callbackNumPresInd);
        builder.append(", dest_addr_subunit=");
        builder.append(destAddrSubunit);
        builder.append(", destination_port=");
        builder.append(destinationPort);
        builder.append(", dest_subaddress=");
        builder.append(destSubaddress);
        builder.append(", display_time=");
        builder.append(displayTime);
        builder.append(", its_reply_type=");
        builder.append(itsReplyType);
        builder.append(", its_session_info=");
        builder.append(itsSessionInfo);
        builder.append(", language_indicator=");
        builder.append(languageIndicator);
        builder.append(", message_payload=");
        builder.append(messagePayload);
        builder.append(", more_messages_to_send=");
        builder.append(moreMessagesToSend);
        builder.append(", ms_msg_wait_facilities=");
        builder.append(msMsgWaitFacilities);
        builder.append(", ms_validity=");
        builder.append(msValidity);
        builder.append(", number_of_messages=");
        builder.append(numberOfMessages);
        builder.append(", payload_type=");
        builder.append(payloadType);
        builder.append(", privacy_indicator=");
        builder.append(privacyIndicator);
        builder.append(", sar_msg_ref_num=");
        builder.append(sarMsgRefNum);
        builder.append(", sar_segment_seqnum=");
        builder.append(sarSegmentsSeqnum);
        builder.append(", sar_total_segments=");
        builder.append(sarTotalSegments);
        builder.append(", sms_signal=");
        builder.append(smsSignal);
        builder.append(", source_addr_subunit=");
        builder.append(sourceAddrSubunit);
        builder.append(", source_port=");
        builder.append(sourcePort);
        builder.append(", source_subaddress=");
        builder.append(sourceSubaddress);
        builder.append(", user_message_reference=");
        builder.append(userMessageReference);
        builder.append(", user_response_code=");
        builder.append(userResponseCode);
        builder.append(", ussd_service_op=");
        builder.append(ussdServiceOp);
        builder.append("}]");
        return builder.toString();
    }

}
