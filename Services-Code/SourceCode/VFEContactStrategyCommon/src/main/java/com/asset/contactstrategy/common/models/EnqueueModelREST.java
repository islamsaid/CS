/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author esmail.anbar
 */
public class EnqueueModelREST {

    long csMsgId;
    String queueAppName;
    String originatorMSISDN;
    String destinationMSISDN;
    String messageText;
    int messageType;
    int originatorType;
    int language;
    int nbtrials;
    int concMsgSequeunce;
    int concMsgCount;
    int concSarRefNum;
    String ipAddress;
    int deliveryReport;
    String optionalParam1;
    String optionalParam2;
    String optionalParam3;
    String optionalParam4;
    String optionalParam5;
    Timestamp submissionDate;
    int expirationHours;
    private List<TLVOptionalModel> tlvs;
    private String requestId;
    private String serviceType;
    private Byte esmClass;
    private Byte protocolId;
    private Byte priorityFlag;
    private String scheduleDeliveryTime;
    private String validityPeriod;
    private Byte smDefaultMsgId;

    public EnqueueModelREST() {
    }

    public EnqueueModelREST(long csMsgId, String queueAppName, String originatorMSISDN,
            String destinationMSISDN, String messageText, int messageType, int originatorType, int language,
            int nbtrials, int concMsgSequeunce, int concMsgCount, int concSarRefNum, String ipAddress,
            int deliveryReport, String optionalParam1, String optionalParam2, String optionalParam3,
            String optionalParam4, String optionalParam5, Timestamp submissionDate, int expirationHours, List<TLVOptionalModel> tlvs,
            String requestId, String serviceType, Byte esmClass, Byte protocolId, Byte priorityFlag, String scheduleDeliveryTime,
            String validityPeriod, Byte smDefaultMsgId) {
        this.csMsgId = csMsgId;
        this.queueAppName = queueAppName;
        this.originatorMSISDN = originatorMSISDN;
        this.destinationMSISDN = destinationMSISDN;
        this.messageText = messageText;
        this.messageType = messageType;
        this.originatorType = originatorType;
        this.language = language;
        this.nbtrials = nbtrials;
        this.concMsgSequeunce = concMsgSequeunce;
        this.concMsgCount = concMsgCount;
        this.concSarRefNum = concSarRefNum;
        this.ipAddress = ipAddress;
        this.deliveryReport = deliveryReport;
        this.optionalParam1 = optionalParam1;
        this.optionalParam2 = optionalParam2;
        this.optionalParam3 = optionalParam3;
        this.optionalParam4 = optionalParam4;
        this.optionalParam5 = optionalParam5;
        this.submissionDate = submissionDate;
        this.expirationHours = expirationHours;
        //=========================================================
        //added by John
        this.tlvs = tlvs;
        this.requestId = requestId;
        //default null
        this.serviceType = serviceType;
        if (esmClass == null) {
            esmClass = 0;
        } else {
            this.esmClass = esmClass;
        }
        if (protocolId == null) {
            protocolId = 0;
        } else {
            this.protocolId = protocolId;
        }
        if (priorityFlag == null) {
            priorityFlag = 0;
        } else {
            this.priorityFlag = priorityFlag;
        }
        //default null
        this.scheduleDeliveryTime = scheduleDeliveryTime;
        //default null
        this.validityPeriod = validityPeriod;
        if (smDefaultMsgId == null) {
            this.smDefaultMsgId = 0;
        } else {
            this.smDefaultMsgId = smDefaultMsgId;
        }
    }

    public long getCsMsgId() {
        return csMsgId;
    }

    public void setCsMsgId(long csMsgId) {
        this.csMsgId = csMsgId;
    }

    public String getQueueAppName() {
        return queueAppName;
    }

    public void setQueueAppName(String queueAppName) {
        this.queueAppName = queueAppName;
    }

    public String getOriginatorMSISDN() {
        return originatorMSISDN;
    }

    public void setOriginatorMSISDN(String originatorMSISDN) {
        this.originatorMSISDN = originatorMSISDN;
    }

    public String getDestinationMSISDN() {
        return destinationMSISDN;
    }

    public void setDestinationMSISDN(String destinationMSISDN) {
        this.destinationMSISDN = destinationMSISDN;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(int originatorType) {
        this.originatorType = originatorType;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getNbtrials() {
        return nbtrials;
    }

    public void setNbtrials(int nbtrials) {
        this.nbtrials = nbtrials;
    }

    public int getConcMsgSequeunce() {
        return concMsgSequeunce;
    }

    public void setConcMsgSequeunce(int concMsgSequeunce) {
        this.concMsgSequeunce = concMsgSequeunce;
    }

    public int getConcMsgCount() {
        return concMsgCount;
    }

    public void setConcMsgCount(int concMsgCount) {
        this.concMsgCount = concMsgCount;
    }

    public int getConcSarRefNum() {
        return concSarRefNum;
    }

    public void setConcSarRefNum(int concSarRefNum) {
        this.concSarRefNum = concSarRefNum;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getDeliveryReport() {
        return deliveryReport;
    }

    public void setDeliveryReport(int deliveryReport) {
        this.deliveryReport = deliveryReport;
    }

    public String getOptionalParam1() {
        return optionalParam1;
    }

    public void setOptionalParam1(String optionalParam1) {
        this.optionalParam1 = optionalParam1;
    }

    public String getOptionalParam2() {
        return optionalParam2;
    }

    public void setOptionalParam2(String optionalParam2) {
        this.optionalParam2 = optionalParam2;
    }

    public String getOptionalParam3() {
        return optionalParam3;
    }

    public void setOptionalParam3(String optionalParam3) {
        this.optionalParam3 = optionalParam3;
    }

    public String getOptionalParam4() {
        return optionalParam4;
    }

    public void setOptionalParam4(String optionalParam4) {
        this.optionalParam4 = optionalParam4;
    }

    public String getOptionalParam5() {
        return optionalParam5;
    }

    public void setOptionalParam5(String optionalParam5) {
        this.optionalParam5 = optionalParam5;
    }

    public Timestamp getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Timestamp submissionDate) {
        this.submissionDate = submissionDate;
    }

    public int getExpirationHours() {
        return expirationHours;
    }

    public void setExpirationHours(int expirationHours) {
        this.expirationHours = expirationHours;
    }

    public List<TLVOptionalModel> getTlvs() {
        return tlvs;
    }

    public void setTlvs(List<TLVOptionalModel> tlvs) {
        this.tlvs = tlvs;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Byte getEsmClass() {
        return esmClass;
    }

    public void setEsmClass(Byte esmClass) {
        this.esmClass = esmClass;
    }

    public Byte getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Byte protocolId) {
        this.protocolId = protocolId;
    }

    public Byte getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(Byte priorityFlag) {
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

    public Byte getSmDefaultMsgId() {
        return smDefaultMsgId;
    }

    public void setSmDefaultMsgId(Byte smDefaultMsgId) {
        this.smDefaultMsgId = smDefaultMsgId;
    }

    @Override
    public String toString() {
        return "EnqueueModelREST{" + "csMsgId=" + csMsgId + ", queueAppName=" + queueAppName + ", originatorMSISDN=" + originatorMSISDN + ", destinationMSISDN=" + destinationMSISDN + ", messageText=" + messageText + ", messageType=" + messageType + ", originatorType=" + originatorType + ", language=" + language + ", nbtrials=" + nbtrials + ", concMsgSequeunce=" + concMsgSequeunce + ", concMsgCount=" + concMsgCount + ", concSarRefNum=" + concSarRefNum + ", ipAddress=" + ipAddress + ", deliveryReport=" + deliveryReport + ", optionalParam1=" + optionalParam1 + ", optionalParam2=" + optionalParam2 + ", optionalParam3=" + optionalParam3 + ", optionalParam4=" + optionalParam4 + ", optionalParam5=" + optionalParam5 + ", submissionDate=" + submissionDate + ", expirationHours=" + expirationHours + ", tlvs=" + tlvs + ", requestId=" + requestId + ", serviceType=" + serviceType + ", esmClass=" + esmClass + ", protocolId=" + protocolId + ", priorityFlag=" + priorityFlag + ", scheduleDeliveryTime=" + scheduleDeliveryTime + ", validityPeriod=" + validityPeriod + ", smDefaultMsgId=" + smDefaultMsgId + '}';
    }
}
