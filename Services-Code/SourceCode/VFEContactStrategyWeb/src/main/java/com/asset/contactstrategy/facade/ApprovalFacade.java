/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.webmodels.AdsGroupWebModel;
//import com.asset.contactstrategy.webmodels.CampaignGroupWebModel;
import com.asset.contactstrategy.webmodels.CampaignWebModel;
import com.asset.contactstrategy.webmodels.QueueWebModel;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import com.asset.contactstrategy.webmodels.ServiceWebModel;
import com.asset.contactstrategy.webmodels.SmsGroupWebModel;
import java.util.ArrayList;

/**
 *
 * @author Amal Magdy
 */
public class ApprovalFacade {

    public ArrayList<ServiceWebModel> getNonApprovedServices() throws CommonException {

        try {
            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "Starting getNonApprovedServices()...");
            MainService mainService = new MainService();
            ArrayList<ServiceModel> serviceModels = mainService.getServices();

            ArrayList<ServiceWebModel> serviceWebModels = new ArrayList<>();

            for (ServiceModel serviceModel : serviceModels) {

                ServiceWebModel serviceWebModel = new ServiceWebModel();
                serviceWebModel.setServiceID(serviceModel.getServiceID());
                serviceWebModel.setVersionId(serviceModel.getVersionId());
                serviceWebModel.setServiceName(serviceModel.getServiceName());
                serviceWebModel.setSelectedInterfaceTypeID(serviceModel.getSelectedInterfaceTypeID());
                serviceWebModel.setSelectedServiceTypeID(serviceModel.getSelectedServiceTypeID());
                serviceWebModel.setSelectedServiceCategoryID(serviceModel.getSelectedServiceCategoryID());
                serviceWebModel.setDeliveryReport(serviceModel.isDeliveryReport());
                serviceWebModel.setConsultCounter(serviceModel.isConsultCounter());
                serviceWebModel.setAdsConsultCounter(serviceModel.isAdsConsultCounter());
                serviceWebModel.setSupportAds(serviceModel.isSupportAds());
                serviceWebModel.setHasWhiteList(serviceModel.isHasWhiteList());
                serviceWebModel.setAllowedSMS(serviceModel.getAllowedSMS());
                serviceWebModel.setSelectedApplicationQueueID(serviceModel.getSelectedApplicationQueueID());
                serviceWebModel.setStatus(serviceModel.getStatus());
                serviceWebModel.setCreator(serviceModel.getCreator());
                serviceWebModel.setWhiteListIPs(serviceModel.getWhiteListIPs());
                serviceWebModel.setDailyQuota(serviceModel.getDailyQuota());
                serviceWebModel.setSelectedServiceTypeModel(serviceModel.getSelectedServiceTypeModel());
                serviceWebModel.setSelectedServiceCategoryModel(serviceModel.getSelectedServiceCategoryModel());
                serviceWebModel.setSelectedInterfaceTypeModel(serviceModel.getSelectedInterfaceTypeModel());
                serviceWebModel.setSelectedApplicationQueueModel(serviceModel.getSelectedApplicationQueueModel());
                serviceWebModel.setAutoCreatdFlag(serviceModel.isAutoCreatdFlag());

                if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    boolean serviceHasParent = false;
                    for (ServiceModel parent : serviceModels) {
                        if (serviceModel.getServiceID() == parent.getServiceID() && serviceModel.getVersionId() != parent.getVersionId()) {
                            serviceHasParent = true;
                            break;
                        }
                    }
                    if (serviceHasParent) {
                        serviceWebModel.setStatusName("Edited");

                    } else {
                        serviceWebModel.setStatusName("Created");

                    }
                } else if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {

                    serviceWebModel.setStatusName("Deleted");

                } else {
                    continue;
                }
                serviceWebModels.add(serviceWebModel);
            }
//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "End getNonApprovedServices()...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getNonApprovedService()...").build());
            return serviceWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getNonApprovedServices()" + ex);
            throw ex;
        }

    }

    public ArrayList<SMSCWebModel> getNonApprovedSmsc(UserModel userModel) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "Starting [getNonApprovedSmsc]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getNonApprovedSMSc").build());
            SMSCFacade sMSCFacade = new SMSCFacade(userModel);
            ArrayList<SMSCWebModel> smscWebModels = sMSCFacade.getSMSCs();
            ArrayList<SMSCWebModel> nonApprovedModels = new ArrayList<>();

            for (SMSCWebModel smscWebModel : smscWebModels) {

                if (smscWebModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    boolean smscHasParent = false;
                    for (SMSCWebModel parent : smscWebModels) {
                        if (smscWebModel.getSMSCid() == parent.getSMSCid() && smscWebModel.getVersionId() != parent.getVersionId()) {
                            smscHasParent = true;
                            break;
                        }
                    }
                    if (smscHasParent) {
                        smscWebModel.setStatusName("Edited");

                    } else {
                        smscWebModel.setStatusName("Created");

                    }
                } else if (smscWebModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {

                    smscWebModel.setStatusName("Deleted");

                } else {
                    continue;
                }
                nonApprovedModels.add(smscWebModel);
            }

//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "End [getNonApprovedSmsc]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getNonApprovedSmsc").build());
            return nonApprovedModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedSmsc]" + ex);
            throw ex;
        }
    }

    public ArrayList<QueueWebModel> getNonApprovedQueues() throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "Starting [QueueWebModel]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting QueueWebModel")
                    .put(GeneralConstants.StructuredLogKeys.INTERFACE_NAME, ApprovalFacade.class.getName()).build());
            QueueFacade queueFacade = new QueueFacade();
            ArrayList<QueueWebModel> queueWebModels = queueFacade.getApplicationQueues();
            ArrayList<QueueWebModel> nonApprovedModels = new ArrayList<>();

            for (QueueWebModel queueWebModel : queueWebModels) {

                if (queueWebModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    boolean queueHasParent = false;
                    for (QueueWebModel parent : queueWebModels) {
                        if (queueWebModel.getAppId() == parent.getAppId() && queueWebModel.getVersionId() != parent.getVersionId()) {
                            queueHasParent = true;
                            break;
                        }
                    }
                    if (queueHasParent) {
                        queueWebModel.setStatusLabel("Edited");

                    } else {
                        queueWebModel.setStatusLabel("Created");

                    }
                } else if (queueWebModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {

                    queueWebModel.setStatusLabel("Deleted");

                } else {
                    continue;
                }
                nonApprovedModels.add(queueWebModel);
            }

//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "End [QueueWebModel]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End QueueWebModel").build());
            return nonApprovedModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [QueueWebModel]" + ex);
            throw ex;
        }
    }

    public ArrayList<SmsGroupWebModel> getNonApprovedSMSGroups() throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "Starting [getNonApprovedSMSGroups]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getNonApprovedSMSGroups").build());
            SmsGroupFacade smsGroupFacade = new SmsGroupFacade();
            ArrayList<SmsGroupWebModel> smsGroupWebModels = smsGroupFacade.getSmsGroups();
            ArrayList<SmsGroupWebModel> nonApprovedModels = new ArrayList<>();

            for (SmsGroupWebModel model : smsGroupWebModels) {

                if (model.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    boolean queueHasParent = false;
                    for (SmsGroupWebModel parent : smsGroupWebModels) {
                        if (model.getGroupId() == parent.getGroupId() && model.getVersionId() != parent.getVersionId()) {
                            queueHasParent = true;
                            break;
                        }
                    }
                    if (queueHasParent) {
                        model.setStatusName("Edited");

                    } else {
                        model.setStatusName("Created");

                    }
                } else if (model.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {

                    model.setStatusName("Deleted");

                } else {
                    continue;
                }
                nonApprovedModels.add(model);
            }

//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "End [getNonApprovedSMSGroups]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getNonApprovedSMSGroups").build());
            return nonApprovedModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedSMSGroups]" + ex);
            throw ex;
        }
    }

    public ArrayList<AdsGroupWebModel> getNonApprovedCampaignGroups() throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "Starting [getNonApprovedCampaignGroups]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getNonApprovedCampaignGroups").build());
            AdsGroupFacade campaignGroupFacade = new AdsGroupFacade();
            ArrayList<AdsGroupWebModel> campaignGroupWebModels = campaignGroupFacade.getAdsGroups();
            ArrayList<AdsGroupWebModel> nonApprovedModels = new ArrayList<>();

            for (AdsGroupWebModel model : campaignGroupWebModels) {

                if (model.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    boolean queueHasParent = false;
                    for (AdsGroupWebModel parent : campaignGroupWebModels) {
                        if (model.getGroupId() == parent.getGroupId() && model.getVersionId() != parent.getVersionId()) {
                            queueHasParent = true;
                            break;
                        }
                    }
                    if (queueHasParent) {
                        model.setStatusName("Edited");

                    } else {
                        model.setStatusName("Created");

                    }
                } else if (model.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {

                    model.setStatusName("Deleted");

                } else {
                    continue;
                }
                nonApprovedModels.add(model);
            }

//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "End [getNonApprovedCampaignGroups]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getNonApprovedCampaignGroups").build());
            return nonApprovedModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedCampaignGroups]" + ex);
            throw ex;
        }
    }

    public ArrayList<CampaignWebModel> getNonApprovedCamps(UserModel userModel) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "Starting [getNonApprovedCamps]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getNonApprovedCamps").build());
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            ArrayList<CampaignWebModel> CampaignWebModels = campaignFacade.getCampaigns();
            ArrayList<CampaignWebModel> nonApprovedModels = new ArrayList<>();

            for (CampaignWebModel model : CampaignWebModels) {

                if (model.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    boolean queueHasParent = false;
                    for (CampaignWebModel parent : CampaignWebModels) {
                        if (model.getCampaignId() == parent.getCampaignId() && model.getVersionId() != parent.getVersionId()) {
                            queueHasParent = true;
                            break;
                        }
                    }
                    if (queueHasParent) {
                        model.setStatusName("Edited");

                    } else {
                        model.setStatusName("Created");

                    }
                } else if (model.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {

                    model.setStatusName("Deleted");

                } else {
                    continue;
                }
                nonApprovedModels.add(model);
            }

//            CommonLogger.businessLogger.debug(ApprovalFacade.class.getName() + " || " + "End [getNonApprovedCamps]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getNonApprovedCamps").build());
            return nonApprovedModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getNonApprovedCamps]" + ex);
            throw ex;
        }
    }

}
