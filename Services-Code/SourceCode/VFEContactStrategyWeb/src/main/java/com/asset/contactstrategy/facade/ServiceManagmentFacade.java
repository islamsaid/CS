/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.service.QueueService;
import com.asset.contactstrategy.common.service.ServiceManagmentService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.webmodels.QueueWebModel;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import com.asset.contactstrategy.webmodels.ServiceWebModel;
import com.asset.contactstrategy.webmodels.UserWebModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amal Magdy
 */
public class ServiceManagmentFacade {

    public ArrayList<ServiceWebModel> getServices() throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting getServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getServices")
                    .build());
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
                serviceWebModel.setCreatorName(serviceModel.getCreatorName());
                serviceWebModel.setWhiteListIPs(serviceModel.getWhiteListIPs());
                serviceWebModel.setDailyQuota(serviceModel.getDailyQuota());
                serviceWebModel.setSmsProcedureQueueId(serviceModel.getSmsProcedureQueueId());
                serviceWebModel.setProcedureMaxBatchSize(serviceModel.getProcedureMaxBatchSize());
                serviceWebModel.setServicePrivilege(serviceModel.getServicePrivilege());
                serviceWebModel.setOriginatorType(serviceModel.getOriginatorType());
                serviceWebModel.setOriginatorValue(serviceModel.getOriginatorValue());
                serviceWebModel.setSelectedServiceTypeModel(serviceModel.getSelectedServiceTypeModel());
                serviceWebModel.setSelectedServiceCategoryModel(serviceModel.getSelectedServiceCategoryModel());
                serviceWebModel.setSelectedInterfaceTypeModel(serviceModel.getSelectedInterfaceTypeModel());
                serviceWebModel.setSelectedApplicationQueueModel(serviceModel.getSelectedApplicationQueueModel());
                serviceWebModel.setAutoCreatdFlag(serviceModel.isAutoCreatdFlag());
                serviceWebModel.setHashedPassword(serviceModel.getHashedPassword());

                if (serviceModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //check if operation has child
                    boolean serviceHasChild = false;
                    for (ServiceModel child : serviceModels) {
                        if (serviceModel.getServiceID() == child.getServiceID() && serviceModel.getVersionId() != child.getVersionId()) {
                            //operation has edited version
                            serviceHasChild = true;
                            serviceWebModel.setEnableDelete(true);
                            serviceWebModel.setEnableEdit(false);
                            serviceWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                            break;
                        }
                    }
                    if (!serviceHasChild) {
                        serviceWebModel.setEnableDelete(true);
                        serviceWebModel.setEnableEdit(true);
                        serviceWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                    }
                } else if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {

                    serviceWebModel.setEnableDelete(true);
                    serviceWebModel.setEnableEdit(true);
                    serviceWebModel.setStatusName(GeneralConstants.STATUS_PENDING);

                } else {

                    serviceWebModel.setEnableDelete(false);
                    serviceWebModel.setEnableEdit(false);
                    serviceWebModel.setStatusName(GeneralConstants.STATUS_PENDING_FOR_DELETION);

                }
                serviceWebModels.add(serviceWebModel);
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End getServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getServices")
                    .build());
            return serviceWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getServices" + ex);
            throw ex;
        }

    }

    public void createService(QueueWebModel queueWebModel, ArrayList<SMSCWebModel> smscWebModels, ServiceWebModel serviceWebModel, List<String> serviceWhitelistIPs, UserWebModel userWebModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting [createService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting createService")
                    .build());
            QueueModel queueModel = null;
            ArrayList<SMSCModel> smscModels = null;
            if (serviceWebModel.isAutoCreatdFlag()) {
                queueModel = (queueWebModel);
                queueModel.setQueueType(Defines.VFE_CS_QUEUES_TYPE_LK.SMS_SENDER_QUEUES);// Update | Eslam.ahmed | set QueueType to ApplicationQueue
                smscModels = new ArrayList<>();
                SMSCFacade smscFacade = new SMSCFacade(new UserModel());
                for (SMSCWebModel smscWebModel : smscWebModels) {
                    SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                    smscModels.add(smscModel);
                }
                if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {
                    queueModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                } else if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                    queueModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                }
            }

            //Handling serviceModel
            ServiceModel serviceModel = serviceWebModel;

            // CR 1901| eslam.ahmed
            serviceModel.setHashedPassword(Utility.getMacSha512Hash(serviceWebModel.getPassword(), SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY)));

            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {
                serviceModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                serviceModel.setServiceName(serviceModel.getServiceName() + "_created");
            } else if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                serviceModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
            }
//Adding serviceModel,whiteList and Queue if autocrateFlag is true
            MainService mainService = new MainService();
            mainService.createService(queueModel, smscModels, serviceModel, serviceWhitelistIPs);
            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End [createService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End createService")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createService]" + ex);
            throw ex;
        }
    }

    public void editService(QueueWebModel queueWebModel, ArrayList<SMSCWebModel> smscWebModels, ServiceWebModel serviceWebModel, List<String> serviceWhitelistIPs, UserWebModel userWebModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting [editService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting editService")
                    .build());
            QueueModel queueModel = null;
            ArrayList<SMSCModel> smscModels = null;
            if (serviceWebModel.isAutoCreatdFlag()) {
                queueModel = (queueWebModel);
                smscModels = new ArrayList<>();
                SMSCFacade smscFacade = new SMSCFacade(new UserModel());
                for (SMSCWebModel smscWebModel : smscWebModels) {
                    SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                    smscModels.add(smscModel);
                }

            }

            //Handling serviceModel
            ServiceModel serviceModel = serviceWebModel;

            // CR 1901 | eslam.ahmed
            if (serviceWebModel.getPassword() != null && !serviceWebModel.getPassword().equals("")) {
                serviceModel.setHashedPassword(Utility.getMacSha512Hash(serviceWebModel.getPassword(), SystemLookups.SYSTEM_PROPERTIES.get(Defines.HASH_KEY_PROPERTY)));
            }

            MainService mainService = new MainService();
            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {

                if (serviceModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    mainService.editApprovedService(queueModel, smscModels, serviceModel, serviceWhitelistIPs);
                } else if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    mainService.editPendingService(queueModel, smscModels, serviceModel, serviceWhitelistIPs);

                }
            } else if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                mainService.approveService(queueModel, smscModels, serviceModel, serviceWhitelistIPs);
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }

//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End [editService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End editService")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editService]" + ex);
            throw ex;
        }
    }

    public void deleteService(QueueWebModel queueWebModel, ArrayList<SMSCWebModel> smscWebModels, ServiceWebModel serviceWebModel, List<String> serviceWhitelistIPs, UserWebModel userWebModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting [deleteService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting deleteService")
                    .build());
            QueueModel queueModel = null;
            ArrayList<SMSCModel> smscModels = null;
//            if (serviceWebModel.isAutoCreatdFlag()) {
//                queueModel = (queueWebModel);
//                smscModels = new ArrayList<>();
//                SMSCFacade smscFacade = new SMSCFacade(new UserModel());
//                for (SMSCWebModel smscWebModel : smscWebModels) {
//                    SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
//                    smscModels.add(smscModel);
//                }
//
//            }

            //Handling serviceModel
            ServiceModel serviceModel = serviceWebModel;
            MainService mainService = new MainService();
            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {

                if (serviceModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    mainService.deleteApprovedService(serviceModel, serviceWhitelistIPs);
                } else if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    mainService.deletePendingService(queueModel, smscModels, serviceModel, serviceWhitelistIPs);

                }
            } else if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                mainService.deleteAllServiceVersions(queueModel, smscModels, serviceModel, serviceWhitelistIPs);
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }

//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End [deleteService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End deleteService")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteService]" + ex);
            throw ex;
        }
    }

    public void approveService(QueueWebModel queueWebModel, ArrayList<SMSCWebModel> smscWebModels, ServiceWebModel serviceWebModel, List<String> serviceWhitelistIPs, UserWebModel userWebModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting [approveService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting approveService")
                    .build());
            QueueModel queueModel = null;
            ArrayList<SMSCModel> smscModels = null;
            if (serviceWebModel.isAutoCreatdFlag()) {
                queueModel = (queueWebModel);
                smscModels = new ArrayList<>();
                SMSCFacade smscFacade = new SMSCFacade(new UserModel());
                for (SMSCWebModel smscWebModel : smscWebModels) {
                    SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                    smscModels.add(smscModel);
                }

            }

            //Handling serviceModel
            ServiceModel serviceModel = serviceWebModel;
            MainService mainService = new MainService();
            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    mainService.approveService(queueModel, smscModels, serviceModel, serviceWhitelistIPs);
                } else if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                    mainService.deleteAllServiceVersions(null, null, serviceModel, serviceWhitelistIPs);
                }

            }
            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End [approveService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End approveService")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveService]" + ex);
            throw ex;
        }
    }

    public void rejectService(QueueWebModel queueWebModel, ArrayList<SMSCWebModel> smscWebModels, ServiceWebModel serviceWebModel, List<String> serviceWhitelistIPs, UserWebModel userWebModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting [rejectService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting rejectService")
                    .build());
            QueueModel queueModel = null;
            ArrayList<SMSCModel> smscModels = null;
            if (serviceWebModel.isAutoCreatdFlag()) {
                queueModel = (queueWebModel);
                smscModels = new ArrayList<>();
                SMSCFacade smscFacade = new SMSCFacade(new UserModel());
                for (SMSCWebModel smscWebModel : smscWebModels) {
                    SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                    smscModels.add(smscModel);
                }

            }

            //Handling serviceModel
            ServiceModel serviceModel = serviceWebModel;
            MainService mainService = new MainService();
            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                mainService.rejectService(queueModel, smscModels, serviceModel, serviceWhitelistIPs);

            }

//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End [rejectService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End rejectService")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectService]" + ex);
            throw ex;
        }
    }

    public ServiceModel getParentServiceVersion(long serviceID) throws CommonException {
        Connection connection = null;
        ServiceModel approvedServiceVersion = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            approvedServiceVersion = serviceManagmentService.getParentServiceVersion(connection, serviceID);

        } catch (Exception ex) {
            if (connection != null) {
                DataSourceManger.rollBack(connection);
            }
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getParentServiceVersion]", ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }

        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getParentServiceVersion]", ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }

        }
        return approvedServiceVersion;
    }

    public ArrayList<ServiceWebModel> getApprovedServices() throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting getApprovedServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getApprovedServices")
                    .build());
            MainService mainService = new MainService();
            ArrayList<ServiceModel> serviceModels = mainService.getApprovedServices(true);

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
                serviceWebModel.setSmsProcedureQueueId(serviceModel.getSmsProcedureQueueId());
                serviceWebModel.setSelectedServiceTypeModel(serviceModel.getSelectedServiceTypeModel());
                serviceWebModel.setSelectedServiceCategoryModel(serviceModel.getSelectedServiceCategoryModel());
                serviceWebModel.setSelectedInterfaceTypeModel(serviceModel.getSelectedInterfaceTypeModel());
                serviceWebModel.setProcedureMaxBatchSize(serviceModel.getProcedureMaxBatchSize());
                serviceWebModel.setServicePrivilege(serviceModel.getServicePrivilege());
                serviceWebModel.setOriginatorType(serviceModel.getOriginatorType());
                serviceWebModel.setOriginatorValue(serviceModel.getOriginatorValue());
                serviceWebModel.setSelectedApplicationQueueModel(serviceModel.getSelectedApplicationQueueModel());
                serviceWebModel.setAutoCreatdFlag(serviceModel.isAutoCreatdFlag());
                serviceWebModel.setHashedPassword(serviceModel.getHashedPassword());
                if (serviceModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //check if operation has child
                    boolean serviceHasChild = false;
                    for (ServiceModel child : serviceModels) {
                        if (serviceModel.getServiceID() == child.getServiceID() && serviceModel.getVersionId() != child.getVersionId()) {
                            //operation has edited version
                            serviceHasChild = true;
                            serviceWebModel.setEnableDelete(true);
                            serviceWebModel.setEnableEdit(false);
                            serviceWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                            break;
                        }
                    }
                    if (!serviceHasChild) {
                        serviceWebModel.setEnableDelete(true);
                        serviceWebModel.setEnableEdit(true);
                        serviceWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                    }
                }
                serviceWebModels.add(serviceWebModel);
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End getApprovedServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getApprovedServices")
                    .build());
            return serviceWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getApprovedServices" + ex);
            throw ex;
        }

    }

    public Boolean checkServiceName(String serviceName) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting checkServiceName...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting checkServiceName")
                    .build());
            MainService mainService = new MainService();
            Boolean serviceModel = mainService.checkServiceName(serviceName);

//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End checkServiceName...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End checkServiceName")
                    .build());
            return serviceModel;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getServices" + ex);
            throw ex;
        }

    }

    //////////Fix Issue //////////////
    public ArrayList<ServiceWebModel> getCampaignServices(CampaignModel campaignModel) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "Starting getCampaignServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getCampaignServices")
                    .build());
            MainService mainService = new MainService();
            ArrayList<ServiceModel> serviceModels = mainService.getCampaignServices(campaignModel);

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
                serviceWebModel.setSmsProcedureQueueId(serviceModel.getSmsProcedureQueueId());
                serviceWebModel.setSelectedServiceTypeModel(serviceModel.getSelectedServiceTypeModel());
                serviceWebModel.setServicePrivilege(serviceModel.getServicePrivilege());
                serviceWebModel.setOriginatorType(serviceModel.getOriginatorType());
                serviceWebModel.setOriginatorValue(serviceModel.getOriginatorValue());
                serviceWebModel.setProcedureMaxBatchSize(serviceModel.getProcedureMaxBatchSize());
                serviceWebModel.setSelectedServiceCategoryModel(serviceModel.getSelectedServiceCategoryModel());
                serviceWebModel.setSelectedInterfaceTypeModel(serviceModel.getSelectedInterfaceTypeModel());
                serviceWebModel.setSelectedApplicationQueueModel(serviceModel.getSelectedApplicationQueueModel());
                serviceWebModel.setAutoCreatdFlag(serviceModel.isAutoCreatdFlag());

                if (serviceModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //check if operation has child
                    boolean serviceHasChild = false;
                    for (ServiceModel child : serviceModels) {
                        if (serviceModel.getServiceID() == child.getServiceID() && serviceModel.getVersionId() != child.getVersionId()) {
                            //operation has edited version
                            serviceHasChild = true;
                            serviceWebModel.setEnableDelete(true);
                            serviceWebModel.setEnableEdit(false);
                            serviceWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                            break;
                        }
                    }
                    if (!serviceHasChild) {
                        serviceWebModel.setEnableDelete(true);
                        serviceWebModel.setEnableEdit(true);
                        serviceWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                    }
                }
                serviceWebModels.add(serviceWebModel);
            }
//            CommonLogger.businessLogger.debug(ServiceManagmentFacade.class.getName() + " || " + "End getCampaignServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getCampaignServices")
                    .build());
            return serviceWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCampaignServices" + ex);
            throw ex;
        }

    }
    ////////////End Fix Issues
}
