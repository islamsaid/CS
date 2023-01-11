/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.facade.ApprovalFacade;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.AdsGroupWebModel;
import com.asset.contactstrategy.webmodels.CampaignWebModel;
import com.asset.contactstrategy.webmodels.QueueWebModel;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import com.asset.contactstrategy.webmodels.ServiceWebModel;
import com.asset.contactstrategy.webmodels.SmsGroupWebModel;
import com.asset.contactstrategy.webmodels.UserWebModel;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Amal Magdy
 */
@ManagedBean
@ViewScoped
public class ApprovalBean implements Serializable {

    private ArrayList<SMSCWebModel> smscList;
    private ArrayList<ServiceWebModel> serviceList;
    private ArrayList<QueueWebModel> queueList;
    private ArrayList<SmsGroupWebModel> groupsList;
    private ArrayList<CampaignWebModel> campList;
    private ArrayList<AdsGroupWebModel> campGroupList;
    private ApprovalFacade approvalFacade;
    private UserWebModel userWebModel;

    @ManagedProperty(value = "#{ServiceManagmentBean}")
    private ServiceManagmentBean serviceManagmentBean;

    @ManagedProperty(value = "#{QueueBean}")
    private QueueBean queueBean;

    @ManagedProperty(value = "#{SMSCManagementBean}")
    private SMSCManagementBean smscBean;

    @ManagedProperty(value = "#{AdsGroupsEditViewBean}")
    private AdsGroupsEditViewBean adsGroupsEditViewBean;

    @ManagedProperty(value = "#{SmsGroupsEditViewBean}")
    private SmsGroupsEditViewBean smsGroupsEditViewBean;
    
    @ManagedProperty (value = "#{CampaignManagementBean}")
    private CampaignManagementBean campaignManagementBean;

    @PostConstruct
    public void init() {
        CommonLogger.businessLogger.info("ApprovalBean" + " Starting  ");
        UserFacade userFacade = new UserFacade();
        userWebModel = userFacade.convertModelToWebModel((UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));
        serviceManagmentBean = new ServiceManagmentBean();
        queueBean = new QueueBean();
        smscBean = new SMSCManagementBean();
        adsGroupsEditViewBean = new AdsGroupsEditViewBean();
        smsGroupsEditViewBean = new SmsGroupsEditViewBean();
        campaignManagementBean=new CampaignManagementBean();
        approvalFacade = new ApprovalFacade();
        getNonApprovedSMSC();
        getNonApprivedServiceList();
        getNonApprovedQueues();
        getNonApprovedSmsGroups();
        getNonApprovedCampaigns();
        getNonApprovedCampaignGroups();
    }

    public void getNonApprovedSMSC() {
        try {
            smscList = approvalFacade.getNonApprovedSmsc(userWebModel);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedSMSC]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        }catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ApprovalBean.class.getName() + ".getNonApprovedSMSC]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    private void getNonApprivedServiceList() {
        try {
            serviceList = approvalFacade.getNonApprovedServices();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprivedServiceList]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        }catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ApprovalBean.class.getName() + ". getNonApprivedServiceList ]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    private void getNonApprovedQueues() {
        try {
            queueList = approvalFacade.getNonApprovedQueues();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedQueues]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        }catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ApprovalBean.class.getName() + ".getNonApprovedQueues]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    private void getNonApprovedSmsGroups() {
        try {
            groupsList = approvalFacade.getNonApprovedSMSGroups();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedSMSGroups]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        }catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ApprovalBean.class.getName() + ".getNonApprovedSmsGroups]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    private void getNonApprovedCampaigns() {
        try {
            campList = approvalFacade.getNonApprovedCamps(userWebModel);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedCampaigns]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        }catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ApprovalBean.class.getName() + ".getNonApprovedCampaigns]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    private void getNonApprovedCampaignGroups() {
        try {
            campGroupList = approvalFacade.getNonApprovedCampaignGroups();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedCampaignGroups]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        }catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ApprovalBean.class.getName() + ".getNonApprovedCampaignGroups]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    public ArrayList<SMSCWebModel> getSmscList() {
        return smscList;
    }

    public void setSmscList(ArrayList<SMSCWebModel> smscList) {
        this.smscList = smscList;
    }

    public ArrayList<ServiceWebModel> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<ServiceWebModel> serviceList) {
        this.serviceList = serviceList;
    }

    public ArrayList<QueueWebModel> getQueueList() {
        return queueList;
    }

    public void setQueueList(ArrayList<QueueWebModel> queueList) {
        this.queueList = queueList;
    }

    public ArrayList<SmsGroupWebModel> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(ArrayList<SmsGroupWebModel> groupsList) {
        this.groupsList = groupsList;
    }

    public ArrayList<CampaignWebModel> getCampList() {
        return campList;
    }

    public void setCampList(ArrayList<CampaignWebModel> campList) {
        this.campList = campList;
    }

    public ArrayList<AdsGroupWebModel> getCampGroupList() {
        return campGroupList;
    }

    public void setCampGroupList(ArrayList<AdsGroupWebModel> campGroupList) {
        this.campGroupList = campGroupList;
    }

    public ServiceManagmentBean getServiceManagmentBean() {
        return serviceManagmentBean;
    }

    public void setServiceManagmentBean(ServiceManagmentBean serviceManagmentBean) {
        this.serviceManagmentBean = serviceManagmentBean;
    }

    public QueueBean getQueueBean() {
        return queueBean;
    }

    public void setQueueBean(QueueBean queueBean) {
        this.queueBean = queueBean;
    }

    public SMSCManagementBean getSmscBean() {
        return smscBean;
    }

    public void setSmscBean(SMSCManagementBean smscBean) {
        this.smscBean = smscBean;
    }

    public AdsGroupsEditViewBean getAdsGroupsEditViewBean() {
        return adsGroupsEditViewBean;
    }

    public void setAdsGroupsEditViewBean(AdsGroupsEditViewBean adsGroupsEditViewBean) {
        this.adsGroupsEditViewBean = adsGroupsEditViewBean;
    }

    public SmsGroupsEditViewBean getSmsGroupsEditViewBean() {
        return smsGroupsEditViewBean;
    }

    public void setSmsGroupsEditViewBean(SmsGroupsEditViewBean smsGroupsEditViewBean) {
        this.smsGroupsEditViewBean = smsGroupsEditViewBean;
    }

    public CampaignManagementBean getCampaignManagementBean() {
        return campaignManagementBean;
    }

    public void setCampaignManagementBean(CampaignManagementBean campaignManagementBean) {
        this.campaignManagementBean = campaignManagementBean;
    }

    public void approveService(ServiceWebModel serviceWebModel) {

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, serviceWebModel);
            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            serviceManagmentBean.init();
            serviceManagmentBean.approveService();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveService]", ex);
            Utility.showErrorMessage(null, "general.error", "in Approve Service");
        }
    }

    public void rejectService(ServiceWebModel serviceWebModel) {

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, serviceWebModel);
            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            serviceManagmentBean.init();
            serviceManagmentBean.rejectService();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectService]", ex);
            Utility.showErrorMessage(null, "general.error", "in Reject Service");
        }
    }

    public void approveQueue(QueueWebModel queueWebModel) {

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, queueWebModel);
            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            queueBean.init();
            queueBean.approveApplicationQueue();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveQueue]", ex);
            Utility.showErrorMessage(null, "general.error", "in Approve Queue");
        }
    }

    public void rejectQueue(QueueWebModel queueWebModel) {

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, queueWebModel);
            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            queueBean.init();
            queueBean.rejectApplicationQueue();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectQueue]", ex);
            Utility.showErrorMessage(null, "general.error", "in Reject Queue");
        }
    }

    public void approveSmsc(SMSCWebModel smscWebModel) {

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, smscWebModel);
            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            smscBean.init();
            smscBean.approveSMSCOperation();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveSmsc]", ex);
            Utility.showErrorMessage(null, "general.error", "in Approve SMSC");
        }
    }

    public void rejectSmsc(SMSCWebModel smscWebMoedl) {

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, smscWebMoedl);
            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            smscBean.init();
            smscBean.rejectSMSCOperation();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectSmsc]", ex);
            Utility.showErrorMessage(null, "general.error", "in Reject SMSC");
        }
    }

    public void approveSMSGroup(SmsGroupWebModel smsGroupWebModel) {

        try {
//            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, smsGroupWebModel);
//            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", smsGroupWebModel);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", true);

            smsGroupsEditViewBean.init();
            smsGroupsEditViewBean.approveGroup();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveSMSGroup]", ex);
            Utility.showErrorMessage(null, "general.error", "in Approve SMS Group");
        }
    }

    public void rejectSMSGroup(SmsGroupWebModel smsGroupWebModel) {

        try {
//            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, smsGroupWebModel);
//            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", smsGroupWebModel);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", true);

            smsGroupsEditViewBean.init();
            smsGroupsEditViewBean.rejectGroup();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectSMSGroup]", ex);
            Utility.showErrorMessage(null, "general.error", "in Reject SMS Group");
        }
    }

    public void approveAdsGroup(AdsGroupWebModel group) {

        try {
//            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, group);
//            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", group);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", true);

            adsGroupsEditViewBean.init();
            adsGroupsEditViewBean.approveGroup();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveAdsGroup]", ex);
            Utility.showErrorMessage(null, "general.error", "in Approve Ads Group");
        }
    }

    public void rejectAdsGroup(AdsGroupWebModel group) {

        try {
//            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, group);
//            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", group);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", true);

            adsGroupsEditViewBean.init();
            adsGroupsEditViewBean.rejectGroup();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectAdsGroup]", ex);
            Utility.showErrorMessage(null, "general.error", "in Reject Ads Group");
        }
    }

    public void approveCampaign(CampaignWebModel campaignWebModel) {

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, campaignWebModel);
            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            campaignManagementBean.init();
            campaignManagementBean.approveCampaignOperation();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveCampaign]", ex);
            Utility.showErrorMessage(null, "general.error", "in Approve Campaign");
        }
    }
    
    
    
    public void rejectCampaign(CampaignWebModel campaignWebModel) {

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, campaignWebModel);
            ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
            campaignManagementBean.init();
            campaignManagementBean.rejectCampaignOperation();
            init();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectCampaign]", ex);
            Utility.showErrorMessage(null, "general.error", "in Reject Campaign");
        }
    }
}
