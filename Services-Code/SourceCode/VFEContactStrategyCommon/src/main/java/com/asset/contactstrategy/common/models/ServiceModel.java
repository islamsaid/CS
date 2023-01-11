/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

import com.asset.contactstrategy.common.logger.CommonLogger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Amal Magdy
 */
public class ServiceModel implements Serializable,Cloneable{
    
    private long serviceID;
    private String serviceName;
    
    private int dailyQuota;
    private int allowedSMS;
    
//    //selected models
    private ServiceTypeModel selectedServiceTypeModel;
    private ServiceCategoryModel selectedServiceCategoryModel;
    private InterfaceTypeModel selectedInterfaceTypeModel;
    private QueueModel selectedApplicationQueueModel;
    private List<WhiteListModel> selectedWhiteListModels;
    
    
        //selected models
    private long selectedServiceTypeID;
    private long selectedServiceCategoryID;
    private long selectedInterfaceTypeID;
    private long selectedApplicationQueueID;
    private List<String> whiteListIPs=new ArrayList<>();
    private int smsProcedureQueueId;
    
    //list models
    //TODO at bean
    private List <ServiceTypeModel> serviceTypeList;
    private List <ServiceCategoryModel> serviceCategoryList;
    private List <InterfaceTypeModel> interfaceTypeList;
    private List <QueueModel> applicationQueueList;
    private List <WhiteListModel> whiteList;
    
    private boolean deliveryReport;
    private boolean consultCounter;
    private boolean adsConsultCounter;
    private boolean supportAds;
    private boolean hasWhiteList;
    private boolean autoCreatdFlag;
    private int originatorType;
    private int originatorValue;
    private int servicePrivilege; 

    
    
    private int creator;
    private String creatorName;
    private int status;
    private long versionId;
    private int procedureMaxBatchSize;

   
    private byte[] hashedPassword;
    
    
    
    public long getServiceID() {
        return serviceID;
    }

    public void setServiceID(long serviceID) {
        this.serviceID = serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(int dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public int getAllowedSMS() {
        return allowedSMS;
    }

    public void setAllowedSMS(int allowedSMS) {
        this.allowedSMS = allowedSMS;
    }

    public List<ServiceTypeModel> getServiceTypeList() {
        return serviceTypeList;
    }

    public void setServiceTypeList(List<ServiceTypeModel> serviceTypeList) {
        this.serviceTypeList = serviceTypeList;
    }

    public List<ServiceCategoryModel> getServiceCategoryList() {
        return serviceCategoryList;
    }

    public void setServiceCategoryList(List<ServiceCategoryModel> serviceCategoryList) {
        this.serviceCategoryList = serviceCategoryList;
    }

    public List<InterfaceTypeModel> getInterfaceTypeList() {
        return interfaceTypeList;
    }

    public void setInterfaceTypeList(List<InterfaceTypeModel> interfaceTypeList) {
        this.interfaceTypeList = interfaceTypeList;
    }

    public List<QueueModel> getApplicationQueueList() {
        return applicationQueueList;
    }

    public void setApplicationQueueList(List<QueueModel> applicationQueueList) {
        this.applicationQueueList = applicationQueueList;
    }

    public List<WhiteListModel> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<WhiteListModel> whiteList) {
        this.whiteList = whiteList;
    }

    public boolean isDeliveryReport() {
        return deliveryReport;
    }

    public void setDeliveryReport(boolean deliveryReport) {
        this.deliveryReport = deliveryReport;
    }

    public boolean isConsultCounter() {
        return consultCounter;
    }

    public void setConsultCounter(boolean consultCounter) {
        this.consultCounter = consultCounter;
    }

    public boolean isAdsConsultCounter() {
        return adsConsultCounter;
    }

    public void setAdsConsultCounter(boolean adsConsultCounter) {
        this.adsConsultCounter = adsConsultCounter;
    }

    public boolean isSupportAds() {
        return supportAds;
    }

    public void setSupportAds(boolean supportAds) {
        this.supportAds = supportAds;
    }

    public boolean isHasWhiteList() {
        return hasWhiteList;
    }

    public void setHasWhiteList(boolean hasWhiteList) {
        this.hasWhiteList = hasWhiteList;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getSelectedServiceTypeID() {
        return selectedServiceTypeID;
    }

    public void setSelectedServiceTypeID(long selectedServiceTypeID) {
        this.selectedServiceTypeID = selectedServiceTypeID;
    }

    public long getSelectedServiceCategoryID() {
        return selectedServiceCategoryID;
    }

    public void setSelectedServiceCategoryID(long selectedServiceCategoryID) {
        this.selectedServiceCategoryID = selectedServiceCategoryID;
    }

    public long getSelectedInterfaceTypeID() {
        return selectedInterfaceTypeID;
    }

    public void setSelectedInterfaceTypeID(long selectedInterfaceTypeID) {
        this.selectedInterfaceTypeID = selectedInterfaceTypeID;
    }

    public long getSelectedApplicationQueueID() {
        return selectedApplicationQueueID;
    }

    public void setSelectedApplicationQueueID(long selectedApplicationQueueID) {
        this.selectedApplicationQueueID = selectedApplicationQueueID;
    }

    public List<String> getWhiteListIPs() {
        return whiteListIPs;
    }

    public void setWhiteListIPs(List<String> whiteListIPs) {
        this.whiteListIPs = whiteListIPs;
    }
    
    public ServiceTypeModel getSelectedServiceTypeModel() {
        return selectedServiceTypeModel;
    }

    public void setSelectedServiceTypeModel(ServiceTypeModel selectedServiceTypeModel) {
        this.selectedServiceTypeModel = selectedServiceTypeModel;
    }

    public ServiceCategoryModel getSelectedServiceCategoryModel() {
        return selectedServiceCategoryModel;
    }

    public void setSelectedServiceCategoryModel(ServiceCategoryModel selectedServiceCategoryModel) {
        this.selectedServiceCategoryModel = selectedServiceCategoryModel;
    }

    public InterfaceTypeModel getSelectedInterfaceTypeModel() {
        return selectedInterfaceTypeModel;
    }

    public void setSelectedInterfaceTypeModel(InterfaceTypeModel selectedInterfaceTypeModel) {
        this.selectedInterfaceTypeModel = selectedInterfaceTypeModel;
    }

    public QueueModel getSelectedApplicationQueueModel() {
        return selectedApplicationQueueModel;
    }

    public void setSelectedApplicationQueueModel(QueueModel selectedApplicationQueueModel) {
        this.selectedApplicationQueueModel = selectedApplicationQueueModel;
    }

    public List<WhiteListModel> getSelectedWhiteListModels() {
        return selectedWhiteListModels;
    }

    public void setSelectedWhiteListModels(List<WhiteListModel> selectedWhiteListModels) {
        this.selectedWhiteListModels = selectedWhiteListModels;
    }

    public boolean isAutoCreatdFlag() {
        return autoCreatdFlag;
    }

    public void setAutoCreatdFlag(boolean autoCreatdFlag) {
        this.autoCreatdFlag = autoCreatdFlag;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

 @Override
    public String toString() {
        String delimiter = ";";
        StringBuilder sb = new StringBuilder("");
        sb.append(versionId);
        sb.append(delimiter);
        sb.append(serviceID);
        sb.append(delimiter);
        sb.append(serviceName);
        sb.append(delimiter);
        sb.append(dailyQuota);
        sb.append(delimiter);
        sb.append(selectedServiceTypeID);
        
        sb.append(delimiter);
        sb.append(selectedServiceCategoryID);
        sb.append(delimiter);
        sb.append(selectedInterfaceTypeID);
        sb.append(delimiter);
        sb.append(deliveryReport);
        sb.append(delimiter);
        sb.append(consultCounter);
        sb.append(delimiter);
        sb.append(adsConsultCounter);
        sb.append(delimiter);
        sb.append(supportAds);
        sb.append(delimiter);
        sb.append(selectedApplicationQueueID);
        sb.append(delimiter);
        sb.append(allowedSMS);
        sb.append(delimiter);
        sb.append(hasWhiteList);
        
        sb.append(delimiter);
        if (whiteListIPs != null && !whiteListIPs.isEmpty()) {
            sb.append("{");
            String innerDelimiter = ",";
            for (String value : whiteListIPs) {
                sb.append(innerDelimiter);
                sb.append(value);
            }
            sb.append("}");
        }
        return sb.toString();
    }

    
    public Object clone() {
        ServiceModel clonedElement = null;
        try {
            clonedElement = (ServiceModel) super.clone();
            if (whiteListIPs != null && !whiteListIPs.isEmpty()) {
                clonedElement.setWhiteListIPs(new ArrayList<String>());
                for (String value : whiteListIPs) {
                    clonedElement.getWhiteListIPs().add(value);
                }
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementModel.class.getName() + " || " + "Failed to clone Service Model",e);
        }
        return clonedElement;
    }

    /**
     * @return the versionId
     */
    public long getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    /**
     * @return the originatorType
     */
    public int getOriginatorType() {
        return originatorType;
    }

    /**
     * @param originatorType the originatorType to set
     */
    public void setOriginatorType(int originatorType) {
        this.originatorType = originatorType;
    }

    /**
     * @return the originatorValue
     */
    public int getOriginatorValue() {
        return originatorValue;
    }

    /**
     * @param originatorValue the originatorValue to set
     */
    public void setOriginatorValue(int originatorValue) {
        this.originatorValue = originatorValue;
    }

    /**
     * @return the servicePrivilege
     */
    public int getServicePrivilege() {
        return servicePrivilege;
    }

    /**
     * @param servicePrivilege the servicePrivilege to set
     */
    public void setServicePrivilege(int servicePrivilege) {
        this.servicePrivilege = servicePrivilege;
    }

    /**
     * @return the smsProcedureQueueId
     */
    public int getSmsProcedureQueueId() {
        return smsProcedureQueueId;
    }

    /**
     * @param smsProcedureQueueId the smsProcedureQueueId to set
     */
    public void setSmsProcedureQueueId(int smsProcedureQueueId) {
        this.smsProcedureQueueId = smsProcedureQueueId;
    }

    /**
     * @return the procedureMaxBatchSize
     */
    public int getProcedureMaxBatchSize() {
        return procedureMaxBatchSize;
    }

    /**
     * @param procedureMaxBatchSize the procedureMaxBatchSize to set
     */
    public void setProcedureMaxBatchSize(int procedureMaxBatchSize) {
        this.procedureMaxBatchSize = procedureMaxBatchSize;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
