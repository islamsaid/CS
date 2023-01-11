/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.models;

/**
 *
 * @author esmail.anbar
 */
public class JobModel {

    private int modX;
    private String date;
    private String toDate;//Used when selecting partitions
    private int daysBeforeTimeOut;

    public int getModX() {
        return modX;
    }

    public void setModX(int modX) {
        this.modX = modX;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDaysBeforeTimeOut() {
        return daysBeforeTimeOut;
    }

    public void setDaysBeforeTimeOut(int daysBeforeTimeOut) {
        this.daysBeforeTimeOut = daysBeforeTimeOut;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "JobModel{" + "modX=" + modX + ", date=" + date + ", toDate=" + toDate + ", daysBeforeTimeOut=" + daysBeforeTimeOut + '}';
    }

}
