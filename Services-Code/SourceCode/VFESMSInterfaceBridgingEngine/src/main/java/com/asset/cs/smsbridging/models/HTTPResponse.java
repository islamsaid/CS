package com.asset.cs.smsbridging.models;

import java.util.ArrayList;

/**
 *
 * @author aya.moawed
 */
public class HTTPResponse {
    
    private String errorCode;
    private String errorDescription;
    private ArrayList<HTTPMsgResult> sms;
    private ArrayList<Long> smsMsgIds;

    public ArrayList<Long> getSmsMsgIds() {
        return smsMsgIds;
    }

    public void setSmsMsgIds(ArrayList<Long> smsMsgIds) {
        this.smsMsgIds = smsMsgIds;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public ArrayList<HTTPMsgResult> getSms() {
        return sms;
    }

    public void setSms(ArrayList<HTTPMsgResult> sms) {
        this.sms = sms;
    }
    
    
}
