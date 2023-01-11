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
public class CustomerConfigurationsModel {

    private String msisdn;
    private int doNotContact;
    private int dailyThresholdSms;
    private int weeklyThresholdSms;
    private int monthlyThresholdSms;
    private int dailyThresholdAds;
    private int weeklyThresholdAds;
    private int monthlyThresholdAds;
    private int lastTwoDigits;
    private int guardPeriod;

    public int getGuardPeriod() {
        return guardPeriod;
    }

    public void setGuardPeriod(int guardPeriod) {
        this.guardPeriod = guardPeriod;
    }
    public CustomerConfigurationsModel() {
    }

    public CustomerConfigurationsModel(String msisdn, int doNotContact, int dailyThresholdSms, int weeklyThresholdSms, int monthlyThresholdSms, int dailyThresholdAds, int weeklyThresholdAds, int monthlyThresholdAds, int lastTwoDigits) {
        this.msisdn = msisdn;
        this.doNotContact = doNotContact;
        this.dailyThresholdSms = dailyThresholdSms;
        this.weeklyThresholdSms = weeklyThresholdSms;
        this.monthlyThresholdSms = monthlyThresholdSms;
        this.dailyThresholdAds = dailyThresholdAds;
        this.weeklyThresholdAds = weeklyThresholdAds;
        this.monthlyThresholdAds = monthlyThresholdAds;
        this.lastTwoDigits = lastTwoDigits;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getDoNotContact() {
        return doNotContact;
    }

    public void setDoNotContact(int doNotContact) {
        this.doNotContact = doNotContact;
    }

    public int getDailyThresholdSms() {
        return dailyThresholdSms;
    }

    public void setDailyThresholdSms(int dailyThresholdSms) {
        this.dailyThresholdSms = dailyThresholdSms;
    }

    public int getWeeklyThresholdSms() {
        return weeklyThresholdSms;
    }

    public void setWeeklyThresholdSms(int weeklyThresholdSms) {
        this.weeklyThresholdSms = weeklyThresholdSms;
    }

    public int getMonthlyThresholdSms() {
        return monthlyThresholdSms;
    }

    public void setMonthlyThresholdSms(int monthlyThresholdSms) {
        this.monthlyThresholdSms = monthlyThresholdSms;
    }

    public int getDailyThresholdAds() {
        return dailyThresholdAds;
    }

    public void setDailyThresholdAds(int dailyThresholdAds) {
        this.dailyThresholdAds = dailyThresholdAds;
    }

    public int getWeeklyThresholdAds() {
        return weeklyThresholdAds;
    }

    public void setWeeklyThresholdAds(int weeklyThresholdAds) {
        this.weeklyThresholdAds = weeklyThresholdAds;
    }

    public int getMonthlyThresholdAds() {
        return monthlyThresholdAds;
    }

    public void setMonthlyThresholdAds(int monthlyThresholdAds) {
        this.monthlyThresholdAds = monthlyThresholdAds;
    }

    public int getLastTwoDigits() {
        return lastTwoDigits;
    }

    public void setLastTwoDigits(int lastTwoDigits) {
        this.lastTwoDigits = lastTwoDigits;
    }
    
    
}
