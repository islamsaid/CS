/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import java.sql.Timestamp;

/**
 *
 * @author esmail.anbar
 */
public class SMSHistoryModel implements Cloneable {

    private long CS_MSG_ID;
    private String MSISDN;
    private Timestamp SUBMISSION_DATE;
    private Timestamp SENDING_DATE;
    private String MESSAGE_TEXT;
    private int STATUS;
    private Timestamp DELIVERY_DATE;
    protected int CONCATENATION_COUNT;
    private int DELIVERED_COUNT;
    private int OTHER_STATUS_COUNT;
    private int SMSC_ID;
    private int SENDER_ENGINE_ID;
    private int MESSAGE_TYPE;
    private int DO_NOT_APPLY;
    private int MESSAGE_VIOLATION_FLAG;
    private int SYSTEM_CATEGORY;
    private int MESSAGE_CATEGORY;
    private int MSISDN_MOD_X;
    private int SERVICE_ID;
    private String ORIGINATOR;
    private String OPTIONAL_PARAM_1;
    private String OPTIONAL_PARAM_2;
    private String OPTIONAL_PARAM_3;
    private String OPTIONAL_PARAM_4;
    private String OPTIONAL_PARAM_5;
    protected int SMSC_CONCATENATION_COUNT;
    private String APP_QUEUE_NAME;
    private int deliveryReport;

    public SMSHistoryModel(long CS_MSG_ID, String MSISDN, Timestamp SUBMISSION_DATE, Timestamp SENDING_DATE, String MESSAGE_TEXT, int STATUS, Timestamp DELIVERY_DATE, int CONCATENATION_COUNT, int DELIVERED_COUNT, int OTHER_STATUS_COUNT, int SMSC_ID, int SENDER_ENGINE_ID, int MESSAGE_TYPE, int DO_NOT_APPLY, int MESSAGE_VIOLATION_FLAG, int SYSTEM_CATEGORY, int MESSAGE_CATEGORY, int MSISDN_MOD_X, int SERVICE_ID, String ORIGINATOR, String OPTIONAL_PARAM_1, String OPTIONAL_PARAM_2, String OPTIONAL_PARAM_3, String OPTIONAL_PARAM_4, String OPTIONAL_PARAM_5, int SMSC_CONCATENATION_COUNT, String APP_QUEUE_NAME, int deliveryReport) {
        this.CS_MSG_ID = CS_MSG_ID;
        this.MSISDN = MSISDN;
        this.SENDING_DATE = SENDING_DATE;
        this.SUBMISSION_DATE = SUBMISSION_DATE;
        this.MESSAGE_TEXT = MESSAGE_TEXT;
        this.STATUS = STATUS;
        this.DELIVERY_DATE = DELIVERY_DATE;
        this.CONCATENATION_COUNT = CONCATENATION_COUNT;
        this.DELIVERED_COUNT = DELIVERED_COUNT;
        this.OTHER_STATUS_COUNT = OTHER_STATUS_COUNT;
        this.SMSC_ID = SMSC_ID;
        this.SENDER_ENGINE_ID = SENDER_ENGINE_ID;
        this.MESSAGE_TYPE = MESSAGE_TYPE;
        this.DO_NOT_APPLY = DO_NOT_APPLY;
        this.MESSAGE_VIOLATION_FLAG = MESSAGE_VIOLATION_FLAG;
        this.SYSTEM_CATEGORY = SYSTEM_CATEGORY;
        this.MESSAGE_CATEGORY = MESSAGE_CATEGORY;
        this.MSISDN_MOD_X = MSISDN_MOD_X;
        this.SERVICE_ID = SERVICE_ID;
        this.ORIGINATOR = ORIGINATOR;
        this.OPTIONAL_PARAM_1 = OPTIONAL_PARAM_1;
        this.OPTIONAL_PARAM_2 = OPTIONAL_PARAM_2;
        this.OPTIONAL_PARAM_3 = OPTIONAL_PARAM_3;
        this.OPTIONAL_PARAM_4 = OPTIONAL_PARAM_4;
        this.OPTIONAL_PARAM_5 = OPTIONAL_PARAM_5;
        this.SMSC_CONCATENATION_COUNT = SMSC_CONCATENATION_COUNT;
        this.APP_QUEUE_NAME = APP_QUEUE_NAME;
        this.deliveryReport = deliveryReport;
    }

    public SMSHistoryModel() {
    }

    public String getORIGINATOR() {
        return ORIGINATOR;
    }

    public void setORIGINATOR(String ORIGINATOR) {
        this.ORIGINATOR = ORIGINATOR;
    }

    public String getOPTIONAL_PARAM_1() {
        return OPTIONAL_PARAM_1;
    }

    public void setOPTIONAL_PARAM_1(String OPTIONAL_PARAM_1) {
        this.OPTIONAL_PARAM_1 = OPTIONAL_PARAM_1;
    }

    public String getOPTIONAL_PARAM_2() {
        return OPTIONAL_PARAM_2;
    }

    public void setOPTIONAL_PARAM_2(String OPTIONAL_PARAM_2) {
        this.OPTIONAL_PARAM_2 = OPTIONAL_PARAM_2;
    }

    public String getOPTIONAL_PARAM_3() {
        return OPTIONAL_PARAM_3;
    }

    public void setOPTIONAL_PARAM_3(String OPTIONAL_PARAM_3) {
        this.OPTIONAL_PARAM_3 = OPTIONAL_PARAM_3;
    }

    public String getOPTIONAL_PARAM_4() {
        return OPTIONAL_PARAM_4;
    }

    public void setOPTIONAL_PARAM_4(String OPTIONAL_PARAM_4) {
        this.OPTIONAL_PARAM_4 = OPTIONAL_PARAM_4;
    }

    public String getOPTIONAL_PARAM_5() {
        return OPTIONAL_PARAM_5;
    }

    public void setOPTIONAL_PARAM_5(String OPTIONAL_PARAM_5) {
        this.OPTIONAL_PARAM_5 = OPTIONAL_PARAM_5;
    }

    public int getSMSC_CONCATENATION_COUNT() {
        return SMSC_CONCATENATION_COUNT;
    }

    public void setSMSC_CONCATENATION_COUNT(int SMSC_CONCATENATION_COUNT) {
        this.SMSC_CONCATENATION_COUNT = SMSC_CONCATENATION_COUNT;
    }

    public String getAPP_QUEUE_NAME() {
        return APP_QUEUE_NAME;
    }

    public void setAPP_QUEUE_NAME(String APP_QUEUE_NAME) {
        this.APP_QUEUE_NAME = APP_QUEUE_NAME;
    }

    public long getCS_MSG_ID() {
        return CS_MSG_ID;
    }

    public void setCS_MSG_ID(long CS_MSG_ID) {
        this.CS_MSG_ID = CS_MSG_ID;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }

    public Timestamp getSENDING_DATE() {
        return SENDING_DATE;
    }

    public void setSENDING_DATE(Timestamp SENDING_DATE) {
        this.SENDING_DATE = SENDING_DATE;
    }

    public String getMESSAGE_TEXT() {
        return MESSAGE_TEXT;
    }

    public void setMESSAGE_TEXT(String MESSAGE_TEXT) {
        this.MESSAGE_TEXT = MESSAGE_TEXT;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public Timestamp getDELIVERY_DATE() {
        return DELIVERY_DATE;
    }

    public void setDELIVERY_DATE(Timestamp DELIVERY_DATE) {
        this.DELIVERY_DATE = DELIVERY_DATE;
    }

    public int getCONCATENATION_COUNT() {
        return CONCATENATION_COUNT;
    }

    public void setCONCATENATION_COUNT(int CONCATENATION_COUNT) {
        this.CONCATENATION_COUNT = CONCATENATION_COUNT;
    }

    public int getDELIVERED_COUNT() {
        return DELIVERED_COUNT;
    }

    public void setDELIVERED_COUNT(int DELIVERED_COUNT) {
        this.DELIVERED_COUNT = DELIVERED_COUNT;
    }

    public int getOTHER_STATUS_COUNT() {
        return OTHER_STATUS_COUNT;
    }

    public void setOTHER_STATUS_COUNT(int OTHER_STATUS_COUNT) {
        this.OTHER_STATUS_COUNT = OTHER_STATUS_COUNT;
    }

    public int getSMSC_ID() {
        return SMSC_ID;
    }

    public void setSMSC_ID(int SMSC_ID) {
        this.SMSC_ID = SMSC_ID;
    }

    public int getSENDER_ENGINE_ID() {
        return SENDER_ENGINE_ID;
    }

    public void setSENDER_ENGINE_ID(int SENDER_ENGINE_ID) {
        this.SENDER_ENGINE_ID = SENDER_ENGINE_ID;
    }

    public int getMESSAGE_TYPE() {
        return MESSAGE_TYPE;
    }

    public void setMESSAGE_TYPE(int MESSAGE_TYPE) {
        this.MESSAGE_TYPE = MESSAGE_TYPE;
    }

    public int getDO_NOT_APPLY() {
        return DO_NOT_APPLY;
    }

    public void setDO_NOT_APPLY(int DO_NOT_APPLY) {
        this.DO_NOT_APPLY = DO_NOT_APPLY;
    }

    public int getMESSAGE_VIOLATION_FLAG() {
        return MESSAGE_VIOLATION_FLAG;
    }

    public void setMESSAGE_VIOLATION_FLAG(int MESSAGE_VIOLATION_FLAG) {
        this.MESSAGE_VIOLATION_FLAG = MESSAGE_VIOLATION_FLAG;
    }

    public int getSYSTEM_CATEGORY() {
        return SYSTEM_CATEGORY;
    }

    public void setSYSTEM_CATEGORY(int SYSTEM_CATEGORY) {
        this.SYSTEM_CATEGORY = SYSTEM_CATEGORY;
    }

    public int getMESSAGE_CATEGORY() {
        return MESSAGE_CATEGORY;
    }

    public void setMESSAGE_CATEGORY(int MESSAGE_CATEGORY) {
        this.MESSAGE_CATEGORY = MESSAGE_CATEGORY;
    }

    public int getMSISDN_MOD_X() {
        return MSISDN_MOD_X;
    }

    public void setMSISDN_MOD_X(int MSISDN_MOD_X) {
        this.MSISDN_MOD_X = MSISDN_MOD_X;
    }

    public int getSERVICE_ID() {
        return SERVICE_ID;
    }

    public void setSERVICE_ID(int SERVICE_ID) {
        this.SERVICE_ID = SERVICE_ID;
    }

    public int getDeliveryReport() {
        return deliveryReport;
    }

    public void setDeliveryReport(int deliveryReport) {
        this.deliveryReport = deliveryReport;
    }

    public Timestamp getSUBMISSION_DATE() {
        return SUBMISSION_DATE;
    }

    public void setSUBMISSION_DATE(Timestamp SUBMISSION_DATE) {
        this.SUBMISSION_DATE = SUBMISSION_DATE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
