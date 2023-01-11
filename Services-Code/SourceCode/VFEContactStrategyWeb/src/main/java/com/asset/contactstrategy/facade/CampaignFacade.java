/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.CustomersCampaignsModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.webmodels.CampaignWebModel;
import com.asset.contactstrategy.webmodels.ServiceWebModel;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author rania.magdy
 */
public class CampaignFacade {

    private UserModel userModel;

    public CampaignFacade(UserModel userModel1) {
        userModel = userModel1;
    }

    public ArrayList<CampaignWebModel> getCampaigns() throws CommonException {
        MainService mainService = new MainService();

        try {
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [getCampaigns]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getCampaigns").build());
            ArrayList<CampaignModel> campaignModels = mainService.getCampaigns();

            ArrayList<CampaignWebModel> campaignWebModels = new ArrayList<>();

            for (CampaignModel campaignModel : campaignModels) {

                CampaignWebModel campaignWebModel = convertModelToWebModel(campaignModel);
                checkCampaignStatus(campaignWebModel);
                if (campaignModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //check if operation has child
                    boolean campaignHasChild = false;
                    for (CampaignModel child : campaignModels) {
                        if (campaignModel.getCampaignId() == child.getCampaignId() && campaignModel.getVersionId() != child.getVersionId()) {
                            //operation has edited version
                            campaignHasChild = true;
                            campaignWebModel.setEnableDelete(true);
                            campaignWebModel.setEnableEdit(false);
                            campaignWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                            break;
                        }
                    }
                    if (!campaignHasChild) {
                        campaignWebModel.setEnableDelete(true);
                        campaignWebModel.setEnableEdit(true);
                        campaignWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                    }
                } else if (campaignModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {

                    campaignWebModel.setEnableDelete(true);
                    campaignWebModel.setEnableEdit(true);
                    campaignWebModel.setStatusName(GeneralConstants.STATUS_PENDING);
                } else {

                    campaignWebModel.setEnableDelete(false);
                    campaignWebModel.setEnableEdit(false);
                    campaignWebModel.setStatusName(GeneralConstants.STATUS_PENDING_FOR_DELETION);
                }
                if (campaignModel.getCampaignStatus() == GeneralConstants.CAMPAIGN_STATUS_STOPPED_VALUE) {
                    campaignWebModel.setEnableEdit(false);

                }
                campaignWebModels.add(campaignWebModel);
            }
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "End [getCampaigns]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getCampaigns").build());
            return campaignWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getCampaigns]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.GET_CAMPAIGN);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }

    }

    public void deleteCampaign(CampaignWebModel campaignWebModel) throws CommonException {
        MainService mainService = new MainService();
        campaignWebModel.setLastModifiedBy(userModel.getId());
        try {
            CampaignModel campaignModel = convertWebModelToModel(campaignWebModel);
            CampaignModel returnedModel = mainService.retrieveConnectedCampaign(campaignModel);
            if (userModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {//business
                if (returnedModel != null) {
                    //lw state bta3 returned child pending
                    //convert both for pending for deletion
                    if (returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        mainService.ChangeCampaignStatusToDelete(campaignModel);
                        //   mainService.ChangeSMSCStatusToDelete(returnedModel); //comment
                    } //lw state bta3 returned model approved yb2a d parent msh child
                    //delete directly
                    else if (returnedModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE || returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {//aw pending for deletion
                        mainService.deleteCampaign(campaignModel);
                    }
                } else {
                    //do not has child 
                    //convert for pending for deletion
                    if (campaignModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        mainService.deleteCampaign(campaignModel);
                    } else {
                        mainService.ChangeCampaignStatusToDelete(campaignModel);
                    }

                }
            } else if (userModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {//operational
                //lw state bta3 returned child pending
                //convert both for pending for deletion
                if (returnedModel != null) {
                    if (returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        mainService.deleteParentAndChildCampaign(campaignModel, returnedModel);
                    } //lw state bta3 returned model approved yb2a d parent msh child
                    //delete directly
                    else if (returnedModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE || returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) { //aw pending for deletion
                        mainService.deleteCampaign(campaignModel);
                    }

                } else {
                    mainService.deleteCampaign(campaignModel);
                }
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteCampaign]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.DELETE_CAMPAIGN);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteCampaign]", e);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ErrorCodes.GENERAL_ERROR, Constants.DELETE_CAMPAIGN);
            Utility.sendMOMAlarem(mErrorsModel);

            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        }
    }

    public CampaignWebModel createCampaign(CampaignWebModel campaignWebModel, ArrayList<ServiceWebModel> serviceList) throws CommonException {
        MainService mainService = new MainService();
        campaignWebModel.setLastModifiedBy(userModel.getId());
        try {
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [createCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting createCampaign").build());
            campaignWebModel.setCampaignStatus(GeneralConstants.CAMPAIGN_STATUS_RESUMED_VALUE);
            CampaignModel campaignModel = convertWebModelToModel(campaignWebModel);
            ArrayList<ServiceModel> services = convertWebServicesToServices(serviceList);
            //check unique name

            CampaignModel model = mainService.getCampaignByName(campaignModel.getCampaignName().trim());
            if (model == null) {
                CampaignModel modelPriority = mainService.getCampaignByPriority(campaignModel.getPriority());
                if (modelPriority == null) {
                    if (userModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {//business user
                        campaignModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                    } else if (userModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {//operational user
                        campaignModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                    }
                    campaignModel = mainService.createCampaign(campaignModel, services);
                    if (userModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                        com.asset.contactstrategy.common.utils.Utility.CountersReloader();
                    }
                    campaignWebModel = convertModelToWebModel(campaignModel);
//                    CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "End [createCampaign]...");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End createCampaign").build());
                    return campaignWebModel;
                } else {
                    CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                            + ErrorCodes.UNIQUE_PRIORITY + "---->  for [createCampaign]" + " Name of Campaign is used before");
                    throw new CommonException("Name of Campaign is used before", ErrorCodes.UNIQUE_PRIORITY);
                }
            } else {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_NAME + "---->  for [createCampaign]" + " Name of Campaign is used before");
                throw new CommonException("Name of Campaign is used before", ErrorCodes.UNIQUE_NAME);
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createCampaign]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.CREATE_CAMPAIGN);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void editCampaign(CampaignWebModel campaignWebModel) throws CommonException {
        MainService mainService = new MainService();
        campaignWebModel.setLastModifiedBy(userModel.getId());

        try {
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [editCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting editCampaign").build());

            CampaignModel campaignModel = convertWebModelToModel(campaignWebModel);

            CampaignModel returnedModel = mainService.retrieveConnectedCampaign(campaignModel);
            if (userModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {//business user
                if (returnedModel == null) {
                    if (campaignModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        mainService.editEditedCampaign(campaignModel);
                    } else {
                        mainService.editCampaign(campaignModel);
                    }
                } else { //edit editedVersion
                    mainService.editEditedCampaign(campaignModel);
                }
            } else if (userModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {//Operational user
                campaignModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                if (returnedModel == null) {
                    mainService.editEditedCampaign(campaignModel); //and change to approved if pending
                } else {
                    //delete returnedModel
                    returnedModel.setLastModifiedBy(campaignModel.getLastModifiedBy());
                    returnedModel.setArabicScript(campaignModel.getArabicScript());
                    returnedModel.setCampaignDescription(campaignModel.getCampaignDescription());
                    returnedModel.setCampaignName(campaignModel.getCampaignName());
                    returnedModel.setCampaignStatus(campaignModel.getCampaignStatus());
                    returnedModel.setControlPercentage(campaignModel.getControlPercentage());
                    returnedModel.setEditedDescription(campaignModel.getEditedDescription());
                    returnedModel.setEndDate(campaignModel.getEndDate());
                    returnedModel.setEnglishScript(campaignModel.getEnglishScript());
                    returnedModel.setFilterQuery(campaignModel.getFilterQuery());
                    returnedModel.setMaxNumberOfCommunications(campaignModel.getMaxNumberOfCommunications());
                    returnedModel.setMaxTargetedCustomers(campaignModel.getMaxTargetedCustomers());
                    returnedModel.setPriority(campaignModel.getPriority());
                    returnedModel.setStartDate(campaignModel.getStartDate());
                    returnedModel.setStatus(campaignModel.getStatus());
                    returnedModel.setFilterList(campaignModel.getFilterList());
                    returnedModel.setFilesModel(campaignModel.getFilesModel());

                    mainService.deleteCampaign(campaignModel);//delete edited campaign
                    mainService.editEditedCampaign(returnedModel);//and change in original campaign to approve
                }
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }

//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "End [editCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End editCampaign").build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editCampaign]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.EDIT_CAMPAIGN);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public ArrayList<FilterModel> retrieveCampaignFilters(long campaignId) throws CommonException {
        String methodName = "retrieveCampaignFilters";
        try {
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [retrieveCampaignFilters]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting retrieveCampaignFilters").build());

            MainService mainService = new MainService();
            ArrayList<FilterModel> list = mainService.retrieveCampaignFilters(campaignId);
            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "End [retrieveCampaignFilters]...");
            return list;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(CampaignFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public ArrayList<FileModel> retrieveCampaignFiles(long campaignId) throws CommonException {
        String methodName = "retrieveCampaignFiles";
        try {
            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [retrieveCampaignFiles]...");

            MainService mainService = new MainService();
            ArrayList<FileModel> list = mainService.retrieveCampaignFiles(campaignId);
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "End [retrieveCampaignFiles]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End retrieveCmpaignFilters").build());
            return list;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(CampaignFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void changeCampaignStatus(CampaignWebModel campaignWebModel) throws CommonException {
//        CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [changeCampaignStatus]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start changeCampaignStatus").build());
        try {
            MainService mainService = new MainService();
            CampaignModel campaignModel = convertWebModelToModel(campaignWebModel);
            mainService.ChangeCampaignStatus(campaignModel);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [changeCampaignStatus]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.CHANGE_CAMPAIGN_STATUS);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [changeCampaignStatus]", e);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ErrorCodes.GENERAL_ERROR, Constants.CHANGE_CAMPAIGN_STATUS);
            Utility.sendMOMAlarem(mErrorsModel);

            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        }
    }

    public String getFilterQuery(ArrayList<FilterModel> filterArray) throws CommonException {
        String methodName = "getFilterQuery";
        try {
            MainService mainService = new MainService();
            return mainService.getFilterQuery(filterArray);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void checkCampaignStatus(CampaignWebModel CampaignWebModel) {
        Date today = new Date();
        if (CampaignWebModel.getCampaignStatus() == GeneralConstants.CAMPAIGN_STATUS_STOPPED_VALUE) {
            CampaignWebModel.setEnablePause(false);
            CampaignWebModel.setEnableStart(false);
            CampaignWebModel.setEnableStop(false);
        } else if (CampaignWebModel.getCampaignStatus() == GeneralConstants.CAMPAIGN_STATUS_PAUSED_VALUE) {
            CampaignWebModel.setEnablePause(false);
            CampaignWebModel.setEnableStart(true);
            CampaignWebModel.setEnableStop(true);
        } else {
            CampaignWebModel.setEnablePause(true);
            CampaignWebModel.setEnableStart(false);
            CampaignWebModel.setEnableStop(true);
        }
//        if (CampaignWebModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE
//                || CampaignWebModel.getCampaignStatus() == GeneralConstants.CAMPAIGN_STATUS_STOPPED_VALUE
//                || CampaignWebModel.getEndDate().before(today)) {
//            CampaignWebModel.setEnablePause(false);
//            CampaignWebModel.setEnableStart(false);
//            CampaignWebModel.setEnableStop(false);
//        } else {
//            if (CampaignWebModel.getCampaignStatus() == GeneralConstants.CAMPAIGN_STATUS_RUNNING_VALUE) {
//                CampaignWebModel.setEnablePause(true);
//                CampaignWebModel.setEnableStart(false);
//                CampaignWebModel.setEnableStop(true);
//            } else if (CampaignWebModel.getCampaignStatus() == GeneralConstants.CAMPAIGN_STATUS_PAUSED_VALUE) {
//                CampaignWebModel.setEnablePause(false);
//                CampaignWebModel.setEnableStart(true);
//                CampaignWebModel.setEnableStop(true);
//            }
//        }
    }

    public CampaignWebModel convertModelToWebModel(CampaignModel campaignModel) throws CommonException {
        CampaignWebModel campaignWebModel = new CampaignWebModel();
        try {
            campaignWebModel.setArabicScript(campaignModel.getArabicScript());
            campaignWebModel.setCampaignDescription(campaignModel.getCampaignDescription());
            campaignWebModel.setCampaignId(campaignModel.getCampaignId());
            campaignWebModel.setCampaignName(campaignModel.getCampaignName());
            campaignWebModel.setCampaignStatus(campaignModel.getCampaignStatus());
            campaignWebModel.setControlPercentage(campaignModel.getControlPercentage());
            campaignWebModel.setEditedDescription(campaignModel.getEditedDescription());
            campaignWebModel.setEndDate(campaignModel.getEndDate());
            campaignWebModel.setEnglishScript(campaignModel.getEnglishScript());
            campaignWebModel.setFilterQuery(campaignModel.getFilterQuery());
            campaignWebModel.setVersionId(campaignModel.getVersionId());
            campaignWebModel.setLastModifiedBy(campaignModel.getLastModifiedBy());
            campaignWebModel.setMaxNumberOfCommunications(campaignModel.getMaxNumberOfCommunications());
            campaignWebModel.setMaxTargetedCustomers(campaignModel.getMaxTargetedCustomers());
            campaignWebModel.setPriority(campaignModel.getPriority());
            campaignWebModel.setStartDate(campaignModel.getStartDate());
            campaignWebModel.setStatus(campaignModel.getStatus());
            campaignWebModel.setFilterType(campaignModel.getFilterType());
            campaignWebModel.setLastModifiedByName(campaignModel.getLastModifiedByName());
            ArrayList<FileModel> fileList = new ArrayList<>();
            ArrayList<FilterModel> filterList = new ArrayList<>();
            if (campaignModel.getFilesModel() != null) {
                for (int i = 0; i < campaignModel.getFilesModel().size(); i++) {
                    fileList.add(campaignModel.getFilesModel().get(i));
                }
            }
            if (campaignModel.getFilterList() != null) {
                for (int i = 0; i < campaignModel.getFilterList().size(); i++) {
                    filterList.add(campaignModel.getFilterList().get(i));
                }
            }
            campaignWebModel.setFilesModel(fileList);
            campaignWebModel.setFilterList(filterList);
        } catch (Exception e) {
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        }
        return campaignWebModel;
    }

    public CampaignModel convertWebModelToModel(CampaignWebModel campaignWebModel) throws CommonException {
        CampaignModel campaignModel = new CampaignModel();
        try {
            campaignModel.setArabicScript(campaignWebModel.getArabicScript());
            campaignModel.setCampaignDescription(campaignWebModel.getCampaignDescription());
            campaignModel.setCampaignId(campaignWebModel.getCampaignId());
            campaignModel.setCampaignName(campaignWebModel.getCampaignName());
            campaignModel.setCampaignStatus(campaignWebModel.getCampaignStatus());
            campaignModel.setControlPercentage(campaignWebModel.getControlPercentage());
            campaignModel.setEditedDescription(campaignWebModel.getEditedDescription());
            campaignModel.setEndDate(campaignWebModel.getEndDate());
            campaignModel.setEnglishScript(campaignWebModel.getEnglishScript());
            campaignModel.setFilterQuery(campaignWebModel.getFilterQuery());
            campaignModel.setVersionId(campaignWebModel.getVersionId());
            campaignModel.setLastModifiedBy(campaignWebModel.getLastModifiedBy());
            campaignModel.setMaxNumberOfCommunications(campaignWebModel.getMaxNumberOfCommunications());
            campaignModel.setMaxTargetedCustomers(campaignWebModel.getMaxTargetedCustomers());
            campaignModel.setPriority(campaignWebModel.getPriority());
            campaignModel.setStartDate(campaignWebModel.getStartDate());
            campaignModel.setStatus(campaignWebModel.getStatus());
            campaignModel.setFilterType(campaignWebModel.getFilterType());
            campaignModel.setLastModifiedByName(campaignWebModel.getLastModifiedByName());
            ArrayList<FileModel> fileList = new ArrayList<>();
            ArrayList<FilterModel> filterList = new ArrayList<>();
            for (int i = 0; i < campaignWebModel.getFilesModel().size(); i++) {
                fileList.add(campaignWebModel.getFilesModel().get(i));

            }
            for (int i = 0; i < campaignWebModel.getFilterList().size(); i++) {
                filterList.add(campaignWebModel.getFilterList().get(i));

            }
            campaignModel.setFilesModel(fileList);
            campaignModel.setFilterList(filterList);
        } catch (Exception e) {
            throw new CommonException(e.getMessage(), ErrorCodes.GENERAL_ERROR);
        }
        return campaignModel;
    }

    public MOMErrorsModel getMOMErrorModel(String errorCode, String methodName) {
        MOMErrorsModel mErrorsModel = new MOMErrorsModel();
        mErrorsModel.setEngineSrcID(GeneralConstants.SRC_ID_WEB_INTERFACE);
        mErrorsModel.setEntryDate(new Date());
        mErrorsModel.setErrorCode(errorCode);
        mErrorsModel.setModuleName(Constants.CAMPAIGN_MODULE);
        mErrorsModel.setFunctionName(methodName);
        mErrorsModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        return mErrorsModel;
    }

    public ArrayList<ServiceModel> convertWebServicesToServices(ArrayList<ServiceWebModel> serviceModels) {
        ArrayList<ServiceModel> models = new ArrayList<>();
        for (int i = 0; i < serviceModels.size(); i++) {
            ServiceModel serviceModel = new ServiceModel();
            serviceModel.setServiceID(serviceModels.get(i).getServiceID());
            serviceModel.setVersionId(serviceModels.get(i).getVersionId());
            serviceModel.setServiceName(serviceModels.get(i).getServiceName());
            serviceModel.setSelectedInterfaceTypeID(serviceModels.get(i).getSelectedInterfaceTypeID());
            serviceModel.setSelectedServiceTypeID(serviceModels.get(i).getSelectedServiceTypeID());
            serviceModel.setSelectedServiceCategoryID(serviceModels.get(i).getSelectedServiceCategoryID());
            serviceModel.setDeliveryReport(serviceModels.get(i).isDeliveryReport());
            serviceModel.setConsultCounter(serviceModels.get(i).isConsultCounter());
            serviceModel.setAdsConsultCounter(serviceModels.get(i).isAdsConsultCounter());
            serviceModel.setSupportAds(serviceModels.get(i).isSupportAds());
            serviceModel.setHasWhiteList(serviceModels.get(i).isHasWhiteList());
            serviceModel.setAllowedSMS(serviceModels.get(i).getAllowedSMS());
            serviceModel.setSelectedApplicationQueueID(serviceModels.get(i).getSelectedApplicationQueueID());
            serviceModel.setStatus(serviceModels.get(i).getStatus());
            serviceModel.setCreator(serviceModels.get(i).getCreator());
            serviceModel.setWhiteListIPs(serviceModels.get(i).getWhiteListIPs());
            serviceModel.setDailyQuota(serviceModels.get(i).getDailyQuota());
            serviceModel.setSelectedServiceTypeModel(serviceModels.get(i).getSelectedServiceTypeModel());
            serviceModel.setSelectedServiceCategoryModel(serviceModels.get(i).getSelectedServiceCategoryModel());
            serviceModel.setSelectedInterfaceTypeModel(serviceModels.get(i).getSelectedInterfaceTypeModel());
            serviceModel.setSelectedApplicationQueueModel(serviceModels.get(i).getSelectedApplicationQueueModel());
            serviceModel.setAutoCreatdFlag(serviceModels.get(i).isAutoCreatdFlag());
            models.add(serviceModel);
        }
        return models;
    }

    public void approveCampaign(CampaignWebModel campaignWebModel) throws CommonException {
        MainService mainService = new MainService();

        try {
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [approveCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting approveCampaign").build());

            CampaignModel campaignModel = convertWebModelToModel(campaignWebModel);
            mainService.approveCampaign(campaignModel);
            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "End [approveCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End approveCampaign").build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveCampaign]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.APPROVE_CAMPAIGN_OPERATION);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void rejectCampaign(CampaignWebModel campaignWebModel) throws CommonException {
        MainService mainService = new MainService();

        try {
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [rejectCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting rejectCampaign").build());

            CampaignModel campaignModel = convertWebModelToModel(campaignWebModel);
            mainService.rejectCampaign(campaignModel);

//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "End [rejectCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End rejectCampaign").build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectCampaign]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.REJECT_CAMPAIGN_OPERATION);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public ArrayList<CustomersCampaignsModel> retrieveCampaignCustomers(Integer runId, Long campaignId, Integer suspendedNumber) throws CommonException {
        String methodName = "retrieveCampaignCustomers";
        MainService mainService = new MainService();
        try {
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "Starting [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting " + methodName).build());
            ArrayList<CustomersCampaignsModel> customers = mainService.getCustomerCampaignsByCampaign(runId, campaignId, suspendedNumber);
//            CommonLogger.businessLogger.debug(CampaignFacade.class.getName() + " || " + "End [" + methodName + "]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End " + methodName).build());
            return customers;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + methodName + "]" + ex);
//            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.);
//            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

}
