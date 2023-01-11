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
public class AdsCountersModel {
    private String msisdn ;
    private int dailyQuota ;
    private int weeklyQuota;
    private int monthlyQuota ;
    private byte messageCategory ;
    private boolean canSendSms ;
    private String errorCode ;

    public AdsCountersModel() {
    }

    public AdsCountersModel(String msisdn, int dailyQuota, int weeklyQuota, int monthlyQuota, byte messageCategory, boolean canSendSms) {
        this.msisdn = msisdn;
        this.dailyQuota = dailyQuota;
        this.weeklyQuota = weeklyQuota;
        this.monthlyQuota = monthlyQuota;
        this.messageCategory = messageCategory;
        this.canSendSms = canSendSms;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(int dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public int getWeeklyQuota() {
        return weeklyQuota;
    }

    public void setWeeklyQuota(int weeklyQuota) {
        this.weeklyQuota = weeklyQuota;
    }

    public int getMonthlyQuota() {
        return monthlyQuota;
    }

    public void setMonthlyQuota(int monthlyQuota) {
        this.monthlyQuota = monthlyQuota;
    }

    public byte getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(byte messageCategory) {
        this.messageCategory = messageCategory;
    }

    public boolean getCanSendSms() {
        return canSendSms;
    }

    public void setCanSendSms(boolean canSendSms) {
        this.canSendSms = canSendSms;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    
}


