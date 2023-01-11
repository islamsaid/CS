/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

import com.asset.contactstrategy.common.models.EnqueueModelREST;
import com.asset.contactstrategy.common.models.SMS;
import java.util.Date;

/**
 *
 * @author esmail.anbar
 */
public class SmsBusinessModel {
    private EnqueueModelREST smsModel;
    //CSPhase1.5 | Esmail.Anbar | Adding Expiration Duration Update
    private Date expirationDate;
    //CSPhase1.5 | Esmail.Anbar | Adding Attributes for Rollback
    private boolean updatedSystemQuota;
    private boolean updatedSystemMonitor;
    private boolean updatedDoNotApply;
    private int dayInSmsStats;
    private boolean updatedCustomerStatistics;
    private Integer campaignId;
    private boolean isArchived;
    private String ipaddress;
    private String transId;
    private int rollbackStatus;
    private String csMsgId;
    private String destinationMSISDN;
    private String systemName;

    public SmsBusinessModel(EnqueueModelREST smsModel, String csMsgId, String destinationMSISDN, String systemName, Date expirationDate, boolean updatedSystemQuota, boolean updatedSystemMonitor, boolean updatedDoNotApply, int dayInSmsStats, boolean isArchived, boolean updatedCustomerStatistics, Integer campaignId, String ipaddress, String transId, int rollbackStatus) {
        this.csMsgId = csMsgId;
        this.destinationMSISDN = destinationMSISDN;
        this.systemName = systemName;
        this.smsModel = smsModel;
        this.expirationDate = expirationDate;
        this.updatedSystemQuota = updatedSystemQuota;
        this.updatedSystemMonitor = updatedSystemMonitor;
        this.updatedDoNotApply = updatedDoNotApply;
        this.dayInSmsStats = dayInSmsStats;
        this.isArchived = isArchived;
        this.updatedCustomerStatistics = updatedCustomerStatistics;
        this.campaignId = campaignId;
        this.ipaddress = ipaddress;
        this.transId = transId;
        this.rollbackStatus = rollbackStatus;
    }

    public EnqueueModelREST getSmsModel() {
        return smsModel;
    }

    public void setSmsModel(EnqueueModelREST smsModel) {
        this.smsModel = smsModel;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isUpdatedSystemQuota() {
        return updatedSystemQuota;
    }

    public void setUpdatedSystemQuota(boolean updatedSystemQuota) {
        this.updatedSystemQuota = updatedSystemQuota;
    }

    public boolean isUpdatedSystemMonitor() {
        return updatedSystemMonitor;
    }

    public void setUpdatedSystemMonitor(boolean updatedSystemMonitor) {
        this.updatedSystemMonitor = updatedSystemMonitor;
    }

    public boolean isUpdatedDoNotApply() {
        return updatedDoNotApply;
    }

    public void setUpdatedDoNotApply(boolean updatedDoNotApply) {
        this.updatedDoNotApply = updatedDoNotApply;
    }

    public int getDayInSmsStats() {
        return dayInSmsStats;
    }

    public void setDayInSmsStats(int dayInSmsStats) {
        this.dayInSmsStats = dayInSmsStats;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public int getRollbackStatus() {
        return rollbackStatus;
    }

    public void setRollbackStatus(int rollbackStatus) {
        this.rollbackStatus = rollbackStatus;
    }

    public String getCsMsgId() {
        return csMsgId;
    }

    public void setCsMsgId(String csMsgId) {
        this.csMsgId = csMsgId;
    }

    public String getDestinationMSISDN() {
        return destinationMSISDN;
    }

    public void setDestinationMSISDN(String destinationMSISDN) {
        this.destinationMSISDN = destinationMSISDN;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public boolean isIsArchived() {
        return isArchived;
    }

    public void setIsArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }

    public boolean isUpdatedCustomerStatistics() {
        return updatedCustomerStatistics;
    }

    public void setUpdatedCustomerStatistics(boolean updatedCustomerStatistics) {
        this.updatedCustomerStatistics = updatedCustomerStatistics;
    }
}
