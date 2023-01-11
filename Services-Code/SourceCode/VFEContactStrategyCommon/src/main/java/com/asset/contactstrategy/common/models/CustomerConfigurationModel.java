/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;


public class CustomerConfigurationModel {
    
    private boolean doNotContact;
    private int dailyThreshold;
    private int weeklyThreshold;
    private int mounthlyThreshold;
    private int dailyCampain;
    private int weeklyCampain;
    private int mounthlyCampain;

    /**
     * @return the dailyThreshold
     */
    public int getDailyThreshold() {
        return dailyThreshold;
    }

    /**
     * @param dailyThreshold the dailyThreshold to set
     */
    public void setDailyThreshold(int dailyThreshold) {
        this.dailyThreshold = dailyThreshold;
    }

    /**
     * @return the weeklyThreshold
     */
    public int getWeeklyThreshold() {
        return weeklyThreshold;
    }

    /**
     * @param weeklyThreshold the weeklyThreshold to set
     */
    public void setWeeklyThreshold(int weeklyThreshold) {
        this.weeklyThreshold = weeklyThreshold;
    }

    /**
     * @return the mounthlyThreshold
     */
    public int getMounthlyThreshold() {
        return mounthlyThreshold;
    }

    /**
     * @param mounthlyThreshold the mounthlyThreshold to set
     */
    public void setMounthlyThreshold(int mounthlyThreshold) {
        this.mounthlyThreshold = mounthlyThreshold;
    }

    /**
     * @return the dailyCampain
     */
    public int getDailyCampain() {
        return dailyCampain;
    }

    /**
     * @param dailyCampain the dailyCampain to set
     */
    public void setDailyCampain(int dailyCampain) {
        this.dailyCampain = dailyCampain;
    }

    /**
     * @return the weeklyCampain
     */
    public int getWeeklyCampain() {
        return weeklyCampain;
    }

    /**
     * @param weeklyCampain the weeklyCampain to set
     */
    public void setWeeklyCampain(int weeklyCampain) {
        this.weeklyCampain = weeklyCampain;
    }

    /**
     * @return the mounthlyCampain
     */
    public int getMounthlyCampain() {
        return mounthlyCampain;
    }

    /**
     * @param mounthlyCampain the mounthlyCampain to set
     */
    public void setMounthlyCampain(int mounthlyCampain) {
        this.mounthlyCampain = mounthlyCampain;
    }

    /**
     * @return the doNotContact
     */
    public boolean isDoNotContact() {
        return doNotContact;
    }

    /**
     * @param doNotContact the doNotContact to set
     */
    public void setDoNotContact(boolean doNotContact) {
        this.doNotContact = doNotContact;
    }

  
    
    
}
