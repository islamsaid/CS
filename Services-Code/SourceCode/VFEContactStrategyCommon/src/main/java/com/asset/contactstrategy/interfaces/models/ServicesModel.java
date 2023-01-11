/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

import com.asset.contactstrategy.common.models.QueueModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Hazem Fekry
 */
public class ServicesModel {
    private String serviceName;
    private long dailyQuota;
    private int interfaceType;
    private int systemType;
    private int deliveryReport;
    private int systemCategory;
    private int consultCounter;
    private int adsConsultCounter;
    private int supportAds;
    private int appId;
    private int allowedSms;
    private int hasWhitelist;
    private int serviceId;
    private int versionId;
    private int status;
    private int creator;
    private List<ServiceWhitelistModel> whiteListModel;
    private ArrayList<CampaignModel> campiagnModel;
    private QueueModel queueModel;
    private int privilegeLevel;
    private int originator;
    private int originatorType;
    private byte[] hashedPassword;
//    //CSPhase1.5 | Esmail.Anbar | Adding Template Update
//    private HashMap<Integer, TemplateModel> templates;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(long dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public int getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(int interfaceType) {
        this.interfaceType = interfaceType;
    }

    public int getSystemType() {
        return systemType;
    }

    public void setSystemType(int systemType) {
        this.systemType = systemType;
    }

    public int getDeliveryReport() {
        return deliveryReport;
    }

    public void setDeliveryReport(int deliveryReport) {
        this.deliveryReport = deliveryReport;
    }

    public int getSystemCategory() {
        return systemCategory;
    }

    public void setSystemCategory(int systemCategory) {
        this.systemCategory = systemCategory;
    }

    public int getConsultCounter() {
        return consultCounter;
    }

    public void setConsultCounter(int consultCounter) {
        this.consultCounter = consultCounter;
    }

    public int getAdsConsultCounter() {
        return adsConsultCounter;
    }

    public void setAdsConsultCounter(int adsConsultCounter) {
        this.adsConsultCounter = adsConsultCounter;
    }

    public int getSupportAds() {
        return supportAds;
    }

    public void setSupportAds(int supportAds) {
        this.supportAds = supportAds;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getAllowedSms() {
        return allowedSms;
    }

    public void setAllowedSms(int allowedSms) {
        this.allowedSms = allowedSms;
    }

    public int getHasWhitelist() {
        return hasWhitelist;
    }

    public void setHasWhitelist(int hasWhitelist) {
        this.hasWhitelist = hasWhitelist;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public List<ServiceWhitelistModel> getWhiteListModel() {
        return whiteListModel;
    }

    public void setWhiteListModel(List<ServiceWhitelistModel> whiteListModel) {
        this.whiteListModel = whiteListModel;
    }

    public ArrayList<CampaignModel> getCampiagnModel() {
        return campiagnModel;
    }

    public void setCampiagnModel(ArrayList<CampaignModel> campiagnModel) {
        this.campiagnModel = campiagnModel;
    }

    public QueueModel getQueueModel() {
        return queueModel;
    }

    public void setQueueModel(QueueModel queueModel) {
        this.queueModel = queueModel;
    }

//    public HashMap<Integer, TemplateModel> getTemplates() {
//        return templates;
//    }
//
//    public void setTemplates(HashMap<Integer, TemplateModel> templates) {
//        this.templates = templates;
//    }

    /**
     * @return the versionId
     */
    public int getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public void setPrivilegeLevel(int privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    public int getOriginator() {
        return originator;
    }

    public void setOriginator(int originator) {
        this.originator = originator;
    }

    public int getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(int originatorType) {
        this.originatorType = originatorType;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    
}
