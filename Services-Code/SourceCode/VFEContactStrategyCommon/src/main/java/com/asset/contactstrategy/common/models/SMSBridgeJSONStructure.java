package com.asset.contactstrategy.common.models;

import java.util.ArrayList;

/**
 *
 * @author aya.moawed
 */
public class SMSBridgeJSONStructure {

    private String serviceName;
    private ArrayList<SMSBridge> sms;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ArrayList<SMSBridge> getSms() {
        return sms;
    }

    public void setSms(ArrayList<SMSBridge> sms) {
        this.sms = sms;
    }
    
    @Override
    public String toString() {
        return "" + "serviceName=" + serviceName + ", sms=" + sms ;
    }
}
