package com.asset.cs.smsbridging.models;

import java.util.Date;

/**
 *
 * @author aya.moawed
 * http result per message
 */
public class HTTPMsgResult {
    
    private Long msgID;
    private String status;
    private String queueName;
    private Date submissionDate;

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public int getMsisdnLastTwoDigits() {
        return msisdnLastTwoDigits;
    }

    public void setMsisdnLastTwoDigits(int msisdnLastTwoDigits) {
        this.msisdnLastTwoDigits = msisdnLastTwoDigits;
    }
    private int msisdnLastTwoDigits;

    public Long getMsgID() {
        return msgID;
    }

    public void setMsgID(Long msgID) {
        this.msgID = msgID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    
    
}
