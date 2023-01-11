package com.asset.contactstrategy.common.models;

import java.util.ArrayList;

/**
 *
 * @author aya.moawed
 */
public class RequestPreparator {

    private ArrayList<SMSBridge> sms;
    private StringBuilder jsonString;
    private String simpleRequest;

    public String getSimpleRequest() {
        return simpleRequest;
    }

    public void setSimpleRequest(String simpleRequest) {
        this.simpleRequest = simpleRequest;
    }

    public StringBuilder getJsonString() {
        return jsonString;
    }

    public void setJsonString(StringBuilder jsonString) {
        this.jsonString = jsonString;
    }

    public ArrayList<SMSBridge> getSms() {
        return sms;
    }

    public void setSms(ArrayList<SMSBridge> sms) {
        this.sms = sms;
    }
    @Override
    public String toString() {
        return "json Request String=" + jsonString;
    }

}
