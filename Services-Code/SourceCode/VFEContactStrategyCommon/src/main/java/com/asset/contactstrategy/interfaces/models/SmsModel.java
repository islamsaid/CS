/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

/**
 *
 * @author hazem.fekry
 */
public class SmsModel {
    long seq_id;
    String appName;
    String origMsisdn;
    String dstMsisdn;
    String msgTxt;
    byte msgType;
    byte origType;
    byte langId;
    int nbtrials;
    int concMsgSequeunce;
    int concMsgCount;
    int concSarRefNum;
    String ipAddress;
    String trackingID;
    byte doNotApply;
    byte messageViolationFlag;
    byte systemCategory ;
    byte messageCategory ;
    int serviceId ;
    byte deliveryReport ;
    String optionalParam1;
    String optionalParam2;
    String optionalParam3; 
    String optionalParam4; 
    String optionalParam5;

    public SmsModel() {
    }

    public SmsModel(long seq_id, String appName, String origMsisdn, String dstMsisdn, String msgTxt, byte msgType, byte origType, byte langId, int nbtrials, int concMsgSequeunce, int concMsgCount, int concSarRefNum, String ipAddress, String trackingID, byte doNotApply, byte messageViolationFlag, byte systemCategory, byte messageCategory, int serviceId, byte deliveryReport, String optionalParam1, String optionalParam2, String optionalParam3, String optionalParam4, String optionalParam5) {
        this.seq_id = seq_id;
        this.appName = appName;
        this.origMsisdn = origMsisdn;
        this.dstMsisdn = dstMsisdn;
        this.msgTxt = msgTxt;
        this.msgType = msgType;
        this.origType = origType;
        this.langId = langId;
        this.nbtrials = nbtrials;
        this.concMsgSequeunce = concMsgSequeunce;
        this.concMsgCount = concMsgCount;
        this.concSarRefNum = concSarRefNum;
        this.ipAddress = ipAddress;
        this.trackingID = trackingID;
        this.doNotApply = doNotApply;
        this.messageViolationFlag = messageViolationFlag;
        this.systemCategory = systemCategory;
        this.messageCategory = messageCategory;
        this.serviceId = serviceId;
        this.deliveryReport = deliveryReport;
        this.optionalParam1 = optionalParam1;
        this.optionalParam2 = optionalParam2;
        this.optionalParam3 = optionalParam3;
        this.optionalParam4 = optionalParam4;
        this.optionalParam5 = optionalParam5;
    }

    public long getSeq_id() {
        return seq_id;
    }

    public void setSeq_id(long seq_id) {
        this.seq_id = seq_id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getOrigMsisdn() {
        return origMsisdn;
    }

    public void setOrigMsisdn(String origMsisdn) {
        this.origMsisdn = origMsisdn;
    }

    public String getDstMsisdn() {
        return dstMsisdn;
    }

    public void setDstMsisdn(String dstMsisdn) {
        this.dstMsisdn = dstMsisdn;
    }

    public String getMsgTxt() {
        return msgTxt;
    }

    public void setMsgTxt(String msgTxt) {
        this.msgTxt = msgTxt;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public byte getOrigType() {
        return origType;
    }

    public void setOrigType(byte origType) {
        this.origType = origType;
    }

    public byte getLangId() {
        return langId;
    }

    public void setLangId(byte langId) {
        this.langId = langId;
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

    public String getTrackingID() {
        return trackingID;
    }

    public void setTrackingID(String trackingID) {
        this.trackingID = trackingID;
    }

    public byte getDoNotApply() {
        return doNotApply;
    }

    public void setDoNotApply(byte doNotApply) {
        this.doNotApply = doNotApply;
    }

    public byte getMessageViolationFlag() {
        return messageViolationFlag;
    }

    public void setMessageViolationFlag(byte messageViolationFlag) {
        this.messageViolationFlag = messageViolationFlag;
    }

    public byte getSystemCategory() {
        return systemCategory;
    }

    public void setSystemCategory(byte systemCategory) {
        this.systemCategory = systemCategory;
    }

    public byte getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(byte messageCategory) {
        this.messageCategory = messageCategory;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public byte getDeliveryReport() {
        return deliveryReport;
    }

    public void setDeliveryReport(byte deliveryReport) {
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
    
    
}
