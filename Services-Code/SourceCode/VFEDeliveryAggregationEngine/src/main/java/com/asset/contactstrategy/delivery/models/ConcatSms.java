/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.models;

import java.util.Date;

/**
 *
 * @author mohamed.halawa
 */
public class ConcatSms implements Cloneable {

    private Long msgId;
    private Date deliveryDate;
    private String msgText;
    private String msisdn;
    private Integer msisdnModx;
    private Date sendingDate;
    private Integer smscId;
    private String smscMsgId;
    private Integer status;
    private Date submissionDate;
    private String trimmedSmscMsgId;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Integer getMsisdnModx() {
        return msisdnModx;
    }

    public void setMsisdnModx(Integer msisdnModx) {
        this.msisdnModx = msisdnModx;
    }

    public Date getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }

    public Integer getSmscId() {
        return smscId;
    }

    public void setSmscId(Integer smscId) {
        this.smscId = smscId;
    }

    public String getSmscMsgId() {
        return smscMsgId;
    }

    public void setSmscMsgId(String smscMsgId) {
        this.smscMsgId = smscMsgId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getTrimmedSmscMsgId() {
        return trimmedSmscMsgId;
    }

    public void setTrimmedSmscMsgId(String trimmedSmscMsgId) {
        this.trimmedSmscMsgId = trimmedSmscMsgId;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

}
