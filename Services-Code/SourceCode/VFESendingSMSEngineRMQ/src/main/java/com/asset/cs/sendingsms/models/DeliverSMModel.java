package com.asset.cs.sendingsms.models;

import client.BufferByte;
import com.asset.cs.sendingsms.models.optional.CallbackNum;
import com.asset.cs.sendingsms.models.optional.DestSubaddress;
import com.asset.cs.sendingsms.models.optional.DestinationPort;
import com.asset.cs.sendingsms.models.optional.ItsSessionInfo;
import com.asset.cs.sendingsms.models.optional.LanguageIndicator;
import com.asset.cs.sendingsms.models.optional.MessagePayload;
import com.asset.cs.sendingsms.models.optional.MessageState;
import com.asset.cs.sendingsms.models.optional.NetworkErrorCode;
import com.asset.cs.sendingsms.models.optional.PayloadType;
import com.asset.cs.sendingsms.models.optional.PrivacyIndicator;
import com.asset.cs.sendingsms.models.optional.ReceiptedMessageId;
import com.asset.cs.sendingsms.models.optional.SarMsgRefNum;
import com.asset.cs.sendingsms.models.optional.SarSegmentsSeqnum;
import com.asset.cs.sendingsms.models.optional.SarTotalSegments;
import com.asset.cs.sendingsms.models.optional.SourcePort;
import com.asset.cs.sendingsms.models.optional.SourceSubaddress;
import com.asset.cs.sendingsms.models.optional.UserMessageReference;
import com.asset.cs.sendingsms.models.optional.UserResponseCode;

/**
 *
 * @author islam.said
 */
public class DeliverSMModel extends CommandRequestModel {

    public DeliverSMModel(HeaderModel headerModel) {
        super(headerModel);
    }

    // Mdandatory Parameters
    private String serviceType;

    private AddressModel source;

    private AddressModel destination;

    private byte esmClass;

    private byte protocolId;

    private byte priorityFlag;

    private String scheduleDeliveryTime;

    private String validityPeriod;

    private byte registeredDelivery;

    private byte replaceIfPresentFlag;

    private byte dataCoding;

    private byte smDefaultMsgId;

    private byte smLength;

    private String shortMessage;

    // Optional Parameters
    private UserMessageReference userMessageReference;
    private SourcePort sourcePort;
    private DestinationPort destinationPort;
    private SarMsgRefNum sarMsgRefNum;
    private SarTotalSegments sarTotalSegments;
    private SarSegmentsSeqnum sarSegmentsSeqnum;
    private UserResponseCode userResponseCode;
    private PrivacyIndicator privacyIndicator;
    private PayloadType payloadType;
    private MessagePayload messagePayload;
    private CallbackNum callbackNum;
    private SourceSubaddress sourceSubaddress;
    private DestSubaddress destSubaddress;
    private LanguageIndicator languageIndicator;
    private ItsSessionInfo itsSessionInfo;
    private NetworkErrorCode networkErrorCode;
    private MessageState messageState;
    private ReceiptedMessageId receiptedMessageId;

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

    public byte getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(byte priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public String getScheduleDeliveryTime() {
        return scheduleDeliveryTime;
    }

    public void setScheduleDeliveryTime(String scheduleDeliveryTime) {
        this.scheduleDeliveryTime = scheduleDeliveryTime;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public byte getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(byte registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public byte getReplaceIfPresentFlag() {
        return replaceIfPresentFlag;
    }

    public void setReplaceIfPresentFlag(byte replaceIfPresentFlag) {
        this.replaceIfPresentFlag = replaceIfPresentFlag;
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

    public byte getSmLength() {
        return smLength;
    }

    public void setSmLength(byte smLength) {
        this.smLength = smLength;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public UserMessageReference getUserMessageReference() {
        return userMessageReference;
    }

    public void setUserMessageReference(BufferByte pdu) {
        this.userMessageReference = new UserMessageReference(pdu);
    }

    public SourcePort getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(BufferByte pdu) {
        this.sourcePort = new SourcePort(pdu);
    }

    public DestinationPort getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(BufferByte pdu) {
        this.destinationPort = new DestinationPort(pdu);
    }

    public SarMsgRefNum getSarMsgRefNum() {
        return sarMsgRefNum;
    }

    public void setSarMsgRefNum(BufferByte pdu) {
        this.sarMsgRefNum = new SarMsgRefNum(pdu);
    }

    public SarTotalSegments getSarTotalSegments() {
        return sarTotalSegments;
    }

    public void setSarTotalSegments(BufferByte pdu) {
        this.sarTotalSegments = new SarTotalSegments(pdu);
    }

    public SarSegmentsSeqnum getSarSegmentsSeqnum() {
        return sarSegmentsSeqnum;
    }

    public void setSarSegmentsSeqnum(BufferByte pdu) {
        this.sarSegmentsSeqnum = new SarSegmentsSeqnum(pdu);
    }

    public UserResponseCode getUserResponseCode() {
        return userResponseCode;
    }

    public void setUserResponseCode(BufferByte pdu) {
        this.userResponseCode = new UserResponseCode(pdu);
    }

    public PrivacyIndicator getPrivacyIndicator() {
        return privacyIndicator;
    }

    public void setPrivacyIndicator(BufferByte pdu) {
        this.privacyIndicator = new PrivacyIndicator(pdu);
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(BufferByte pdu) {
        this.payloadType = new PayloadType(pdu);
    }

    public MessagePayload getMessagePayload() {
        return messagePayload;
    }

    public void setMessagePayload(BufferByte pdu) {
        this.messagePayload = new MessagePayload(pdu);
    }

    public CallbackNum getCallbackNum() {
        return callbackNum;
    }

    public void setCallbackNum(BufferByte pdu) {
        this.callbackNum = new CallbackNum(pdu);
    }

    public SourceSubaddress getSourceSubaddress() {
        return sourceSubaddress;
    }

    public void setSourceSubaddress(BufferByte pdu) {
        this.sourceSubaddress = new SourceSubaddress(pdu);
    }

    public DestSubaddress getDestSubaddress() {
        return destSubaddress;
    }

    public void setDestSubaddress(BufferByte pdu) {
        this.destSubaddress = new DestSubaddress(pdu);
    }

    public LanguageIndicator getLanguageIndicator() {
        return languageIndicator;
    }

    public void setLanguageIndicator(BufferByte pdu) {
        this.languageIndicator = new LanguageIndicator(pdu);
    }

    public ItsSessionInfo getItsSessionInfo() {
        return itsSessionInfo;
    }

    public void setItsSessionInfo(BufferByte pdu) {
        this.itsSessionInfo = new ItsSessionInfo(pdu);
    }

    public NetworkErrorCode getNetworkErrorCode() {
        return networkErrorCode;
    }

    public void setNetworkErrorCode(BufferByte pdu) {
        this.networkErrorCode = new NetworkErrorCode(pdu);
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(BufferByte pdu) {
        this.messageState = new MessageState(pdu);
    }

    public ReceiptedMessageId getReceiptedMessageId() {
        return receiptedMessageId;
    }

    public void setReceiptedMessageId(BufferByte pdu) {
        this.receiptedMessageId = new ReceiptedMessageId(pdu);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("DeliverSMModel [");
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
        builder.append(priorityFlag);
        builder.append(", schedule_delivery_time=");
        builder.append(scheduleDeliveryTime);
        builder.append(", registered_delivery=");
        builder.append(registeredDelivery);
        builder.append(", data_coding=");
        builder.append(dataCoding);
        builder.append(", validity_period=");
        builder.append(validityPeriod);
        builder.append(", smDefaultMsgId=");
        builder.append(smDefaultMsgId);
        builder.append(", replaceIfPresentFlag=");
        builder.append(replaceIfPresentFlag);
        builder.append(", smLength=");
        builder.append(smLength);
        builder.append(", shortMessage=");
        builder.append(shortMessage);
        builder.append(", userMessageReference=");
        builder.append(userMessageReference);
        builder.append(", sourcePort=");
        builder.append(sourcePort);
        builder.append(", destinationPort=");
        builder.append(destinationPort);
        builder.append(", sarMsgRefNum=");
        builder.append(sarMsgRefNum);
        builder.append(", sarTotalSegments=");
        builder.append(sarTotalSegments);
        builder.append(", sarSegmentsSeqnum=");
        builder.append(sarSegmentsSeqnum);
        builder.append(", userResponseCode=");
        builder.append(userResponseCode);
        builder.append(", privacyIndicator=");
        builder.append(privacyIndicator);
        builder.append(", payloadType=");
        builder.append(payloadType);
        builder.append(", messagePayload=");
        builder.append(messagePayload);
        builder.append(", callbackNum=");
        builder.append(callbackNum);
        builder.append(", sourceSubaddress=");
        builder.append(sourceSubaddress);
        builder.append(", destSubaddress=");
        builder.append(destSubaddress);
        builder.append(", languageIndicator=");
        builder.append(languageIndicator);
        builder.append(", itsSessionInfo=");
        builder.append(itsSessionInfo);
        builder.append(", networkErrorCode=");
        builder.append(networkErrorCode);
        builder.append(", messageState=");
        builder.append(messageState);
        builder.append(", receiptedMessageId=");
        builder.append(receiptedMessageId);
        builder.append(" } ");

        return builder.toString();
    }
}
