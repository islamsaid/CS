package com.asset.contactstrategy.common.models;

import java.util.HashMap;

/**
 *
 * @author islam.said
 */
public class EnqueueModelDeliverSM extends EnqueueModel {

    private byte protocolId;
    private byte priorityFlag;
    private String scheduleDeliveryTime;
    private String validityPeriod;
    private byte replaceIfPresentFlag;
    private byte smDefaultMsgId;
    private byte smLength;
    private String shortMessage;

    public EnqueueModelDeliverSM() {
        super();
    }

    public EnqueueModelDeliverSM(byte protocolId, byte priorityFlag, String scheduleDeliveryTime, String validityPeriod, byte replaceIfPresentFlag, byte smDefaultMsgId, byte smLength, String shortMessage, String serviceType, byte soureceAddrTon, byte sourceAddrNpi, String sourceAddr, byte destAddrTon, byte destAddrNpi, String destAddr, byte esmClass, byte registeredDelivery, byte dataCoding, HashMap<Short, TLVOptionalModel> tlvs, String transId) {
        super(serviceType, soureceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destAddr, esmClass, registeredDelivery, dataCoding, tlvs, transId);
        this.protocolId = protocolId;
        this.priorityFlag = priorityFlag;
        this.scheduleDeliveryTime = scheduleDeliveryTime;
        this.validityPeriod = validityPeriod;
        this.replaceIfPresentFlag = replaceIfPresentFlag;
        this.smDefaultMsgId = smDefaultMsgId;
        this.smLength = smLength;
        this.shortMessage = shortMessage;
    }

    public EnqueueModelDeliverSM(byte protocolId, byte priorityFlag, String scheduleDeliveryTime, String validityPeriod, byte replaceIfPresentFlag, byte smDefaultMsgId, byte smLength, String shortMessage, String serviceType, byte soureceAddrTon, byte sourceAddrNpi, String sourceAddr, byte destAddrTon, byte destAddrNpi, String destAddr, byte esmClass, byte registeredDelivery, byte dataCoding, String transId) {
        super(serviceType, soureceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destAddr, esmClass, registeredDelivery, dataCoding, transId);
        this.protocolId = protocolId;
        this.priorityFlag = priorityFlag;
        this.scheduleDeliveryTime = scheduleDeliveryTime;
        this.validityPeriod = validityPeriod;
        this.replaceIfPresentFlag = replaceIfPresentFlag;
        this.smDefaultMsgId = smDefaultMsgId;
        this.smLength = smLength;
        this.shortMessage = shortMessage;
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

    public byte getReplaceIfPresentFlag() {
        return replaceIfPresentFlag;
    }

    public void setReplaceIfPresentFlag(byte replaceIfPresentFlag) {
        this.replaceIfPresentFlag = replaceIfPresentFlag;
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

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("EnqueueModelDeliverSM [");
        builder.append(" service_type=");
        builder.append(getServiceType());
        builder.append(", soureceAddrTon=");
        builder.append(getSoureceAddrTon());
        builder.append(", sourceAddrNpi=");
        builder.append(getSourceAddrNpi());
        builder.append(", sourceAddr=");
        builder.append(getSourceAddr());
        builder.append(", destAddrTon=");
        builder.append(getDestAddrTon());
        builder.append(", destAddrNpi=");
        builder.append(getDestAddrNpi());
        builder.append(", destAddr=");
        builder.append(getDestAddr());
        builder.append(", esm_class=");
        builder.append(getEsmClass());
        builder.append(", registered_delivery=");
        builder.append(getRegisteredDelivery());
        builder.append(", data_coding=");
        builder.append(getDataCoding());
        builder.append(", protocolId=");
        builder.append(getProtocolId());
        builder.append(", priorityFlag=");
        builder.append(getPriorityFlag());
        builder.append(", scheduleDeliveryTime=");
        builder.append(getScheduleDeliveryTime());
        builder.append(", validityPeriod=");
        builder.append(getValidityPeriod());
        builder.append(", replaceIfPresentFlag=");
        builder.append(getReplaceIfPresentFlag());
        builder.append(", smDefaultMsgId=");
        builder.append(getSmDefaultMsgId());
        builder.append(", smLength=");
        builder.append(getSmLength());
        builder.append(", shortMessage=");
        builder.append(getShortMessage());
        builder.append(", TLVs=[ ");
        getTlvs().forEach((Short key, TLVOptionalModel value) -> {
            builder.append(value.toString());
        });
        builder.append(" ]");
        return builder.toString();
    }
}
