/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.common.models;

import java.util.Date;

/**
 *
 * @author mostafa.kashif
 */
public class SMSBridge  {

    private Long msgId;
    private String queueName;
    private String serviceName;
    private String originatorMSISDN;
    private String destinationMSISDN;
    private String msgText;
    private Integer msgType;
    private Integer originatorType;
    private String languageId;
    private String ipAddress;
    private String doNotApply;
    private String messagePriority;
    private String templateId;
    private String templateParameters;
    private String optionalParam1;
    private String optionalParam2;
    private String optionalParam3;
    private String optionalParam4;
    private String optionalParam5;
    private Date submissionDate;
    private String status;// status after it is sent to http
    private String servicePassword; // CR 1901 | eslam.ahmed
    
    private SMSBridge() {
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(Integer originatorType) {
        this.originatorType = originatorType;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDoNotApply() {
        return doNotApply;
    }

    public void setDoNotApply(String doNotApply) {
        this.doNotApply = doNotApply;
    }

    public String getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(String messagePriority) {
        this.messagePriority = messagePriority;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateParameters() {
        return templateParameters;
    }

    public void setTemplateParameters(String templateParameters) {
        this.templateParameters = templateParameters;
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

    public String getServicePassword() {
        return servicePassword;
    }

    public void setServicePassword(String servicePassword) {
        this.servicePassword = servicePassword;
    }
    
    

    //2595
    public SMSBridge(java.math.BigDecimal msgID,//0
            String queueName,//1
            String serviceName,//2
            String origMsisdn,//3
            String dstMsisdn,//4
            String msgTxt,//5
            java.math.BigDecimal msgType,//6
            java.math.BigDecimal origType,//7
            String langId,//8
            String ipAddress,//9
            String doNotApply,//10
            String msgPriority,//11
            String templateId,//12
            String templateParams,//13
            String optionalParameter1,//14
            String optionalParameter2,//15
            String optionalParameter3,//16
            String optionalParameter4,//17
            String optionalParameter5,/*18*/
            Date submissionDate
    )  {
        this.msgId=msgID.longValue();
        this.queueName=queueName;
        this.serviceName=serviceName;
        this.originatorMSISDN=origMsisdn;
        this.destinationMSISDN=dstMsisdn;
        this.msgText=msgTxt;
        this.msgType=msgType!=null ? msgType.intValue(): null;
        this.originatorType=origType !=null ? origType.intValue(): null;
        this.languageId=langId;
        this.ipAddress=ipAddress;
        this.doNotApply=doNotApply;
        this.messagePriority=msgPriority;
        this.templateId=templateId;
        this.templateParameters=templateParams;
        this.optionalParam1=optionalParameter1;
        this.optionalParam2=optionalParameter2;
        this.optionalParam3=optionalParameter3;
        this.optionalParam4=optionalParameter4;
        this.optionalParam5=optionalParameter5;
        this.submissionDate=submissionDate;
    }

    /* ORAData interface */
    /* accessor methods */

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SMSBridge obj = new SMSBridge();
        obj.setMsgId(msgId);
        obj.setTemplateParameters(templateParameters);
        obj.setTemplateId(templateId);
        obj.setStatus(status);
        obj.setServiceName(serviceName);
        obj.setQueueName(queueName);
        obj.setOriginatorType(originatorType);
        obj.setOriginatorMSISDN(originatorMSISDN);
        obj.setOptionalParam5(optionalParam5);
        obj.setMsgType(msgType);
        obj.setMsgText(msgText);
        obj.setMessagePriority(messagePriority);
        obj.setLanguageId(languageId);
        obj.setIpAddress(ipAddress);
        obj.setDoNotApply(doNotApply);
        obj.setDestinationMSISDN(destinationMSISDN);
        obj.setSubmissionDate(submissionDate);
        return obj;
    }

    @Override
    public String toString() {
        return "{" + "msgId=" + msgId + ", queueName=" + queueName + ", serviceName=" + serviceName + "}";//+ ", originatorMSISDN=" + originatorMSISDN + ", destinationMSISDN=" + destinationMSISDN + ", msgText=" + msgText + ", msgType=" + msgType + ", originatorType=" + originatorType + ", languageId=" + languageId + ", ipAddress=" + ipAddress + ", doNotApply=" + doNotApply + ", messagePriority=" + messagePriority + ", templateId=" + templateId + ", templateParameters=" + templateParameters + ", optionalParam1=" + optionalParam1 + ", optionalParam2=" + optionalParam2 + ", optionalParam3=" + optionalParam3 + ", optionalParam4=" + optionalParam4 + ", optionalParam5=" + optionalParam5 + ", submissionDate=" + submissionDate + "}\n";
    }

}

