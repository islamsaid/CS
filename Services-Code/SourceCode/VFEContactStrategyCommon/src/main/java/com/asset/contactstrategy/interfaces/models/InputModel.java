/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

import java.util.Date;
import java.util.List;

import com.asset.contactstrategy.common.models.TLVOptionalModel;

/**
 *
 * @author hazem.fekry
 */
public class InputModel {

    private transient long csMsgId;
    private String originatorMSISDN;
    private String destinationMSISDN;
    private String messageText;
    private byte originatorType;
    private byte messageType;
    private Byte language;
    private String systemName;
    private boolean doNotApply = false;
    private transient byte messagePriority;
    private String messagePriorityText;
    private transient int concatNumber;
    private transient boolean violateFlag = false;
    private String optionalParam1;
    private String optionalParam2;
    private String optionalParam3;
    private String optionalParam4;
    private String optionalParam5;
    // CSPhase1.5 | Esmail.Anbar | Adding Expiration Duration Update
    private transient Date expirationDate;
    private transient int expirationHours;
    // CSPhase1.5 | Esmail.Anbar | Adding Template Update
    private String templatesIds;
    private String templatesParameters;
    // CSPhase1.5 | Esmail.Anbar | Adding Attributes for Rollback
    private transient boolean updatedCustomerStatistics = false;
    private transient boolean updatedSystemQuota = false;
    private transient boolean updatedSystemMonitor = false;
    private transient boolean updatedDoNotApply = false;
    private transient int dayInSmsStats;
    private transient Integer campaignId;
    private transient String ipaddress;
    private transient String transId;
    private transient String[] rowLine;
    private transient String languageText;
    private transient String errorReason;
    private transient String fileName;

    private transient CustomersModel customer;
    private transient Date submissionDate;

    private List<TLVOptionalModel> tlvs;

    private String requestId;
    private String serviceType;
    private Byte esmClass;
    private Byte protocolId;
    private Byte priorityFlag;
    private String scheduleDeliveryTime;
    private String validityPeriod;
    private Byte smDefaultMsgId;
	
	private String systemPassword; // CR 1901 | eslam.ahmed

    public InputModel() {
    }

    public InputModel(String destinationMSISDN, String systemName) {
        this.destinationMSISDN = destinationMSISDN;
        this.systemName = systemName;
    }

    public InputModel(String destinationMSISDN, String systemName, byte language) {
        this.destinationMSISDN = destinationMSISDN;
        this.systemName = systemName;
        this.language = language;
    }

    public InputModel(String originatorMSISDN, String destinationMSISDN, String messageText, byte originatorType,
            byte messageType, Byte language, String systemName, boolean doNotApply, byte messagePriority,
            boolean violateFlag, String optionalParam1, String optionalParam2, String optionalParam3,
            String optionalParam4, String optionalParam5, Date expirationDate, String templatesIds,
            String templatesParameters) {
        this.originatorMSISDN = originatorMSISDN;
        this.destinationMSISDN = destinationMSISDN;
        this.messageText = messageText;
        this.originatorType = originatorType;
        this.messageType = messageType;
        this.language = language;
        this.systemName = systemName;
        this.doNotApply = doNotApply;
        this.messagePriority = messagePriority;
        this.violateFlag = violateFlag;
        this.optionalParam1 = optionalParam1;
        this.optionalParam2 = optionalParam2;
        this.optionalParam3 = optionalParam3;
        this.optionalParam4 = optionalParam4;
        this.optionalParam5 = optionalParam5;
        this.expirationDate = expirationDate;
        this.templatesIds = templatesIds;
        this.templatesParameters = templatesParameters;
    }

    public int getConcatNumber() {
        return concatNumber;
    }

    public void setConcatNumber(int concatNumber) {
        this.concatNumber = concatNumber;
    }

    public long getCsMsgId() {
        return csMsgId;
    }

    public void setCsMsgId(long csMsgId) {
        this.csMsgId = csMsgId;
    }

    public boolean isViolateFlag() {
        return violateFlag;
    }

    public void setViolateFlag(boolean violateFlag) {
        this.violateFlag = violateFlag;
    }

    public String getDestinationMSISDN() {
        return destinationMSISDN;
    }

    public void setDestinationMSISDN(String destinationMSISDN) {
        this.destinationMSISDN = destinationMSISDN;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Byte getLanguage() {
        return language;
    }
//
//    public void setLanguage(byte language) {
//        this.language = language;
//    }

    public String getOriginatorMSISDN() {
        return originatorMSISDN;
    }

    public void setOriginatorMSISDN(String originatorMSISDN) {
        this.originatorMSISDN = originatorMSISDN;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public byte getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(byte originatorType) {
        this.originatorType = originatorType;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public boolean isDoNotApply() {
        return doNotApply;
    }

    public void setDoNotApply(boolean doNotApply) {
        this.doNotApply = doNotApply;
    }

    public byte getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(byte messagePriority) {
        this.messagePriority = messagePriority;
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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getTemplatesIds() {
        return templatesIds;
    }

    public void setTemplatesIds(String templatesIds) {
        this.templatesIds = templatesIds;
    }

    public String getTemplatesParameters() {
        return templatesParameters;
    }

    public void setTemplatesParameters(String templatesParameters) {
        this.templatesParameters = templatesParameters;
    }

    public boolean isUpdatedSystemQuota() {
        return updatedSystemQuota;
    }

    public void setUpdatedSystemQuota(boolean updatedSystemQuota) {
        this.updatedSystemQuota = updatedSystemQuota;
    }

    public boolean isUpdatedSystemMonitor() {
        return updatedSystemMonitor;
    }

    public void setUpdatedSystemMonitor(boolean updatedSystemMonitor) {
        this.updatedSystemMonitor = updatedSystemMonitor;
    }

    public boolean isUpdatedDoNotApply() {
        return updatedDoNotApply;
    }

    public void setUpdatedDoNotApply(boolean updatedDoNotApply) {
        this.updatedDoNotApply = updatedDoNotApply;
    }

    public int getDayInSmsStats() {
        return dayInSmsStats;
    }

    public void setDayInSmsStats(int dayInSmsStats) {
        this.dayInSmsStats = dayInSmsStats;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public CustomersModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomersModel customer) {
        this.customer = customer;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public int getExpirationHours() {
        return expirationHours;
    }

    public void setExpirationHours(int expirationHours) {
        this.expirationHours = expirationHours;
    }

    /**
     * @return the messagePriorityText
     */
    public String getMessagePriorityText() {
        return messagePriorityText;
    }

    /**
     * @param messagePriorityText the messagePriorityText to set
     */
    public void setMessagePriorityText(String messagePriorityText) {
        this.messagePriorityText = messagePriorityText;
    }

    public String[] getRowLine() {
        return rowLine;
    }

    public void setRowLine(String[] rowLine) {
        this.rowLine = rowLine;
    }

    public String getLanguageText() {
        return languageText;
    }

    public void setLanguageText(String languageText) {
        this.languageText = languageText;
    }

    public boolean isUpdatedCustomerStatistics() {
        return updatedCustomerStatistics;
    }

    public void setUpdatedCustomerStatistics(boolean updatedCustomerStatistics) {
        this.updatedCustomerStatistics = updatedCustomerStatistics;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public void setLanguage(Byte language) {
        this.language = language;
    }

    public List<TLVOptionalModel> getTlvs() {
        return tlvs;
    }

    public void setTlvs(List<TLVOptionalModel> tlvs) {
        this.tlvs = tlvs;
    }
	
	public String getSystemPassword() {
        return systemPassword;
    }

    public void setSystemPassword(String systemPassword) {
        this.systemPassword = systemPassword;
    }

}
