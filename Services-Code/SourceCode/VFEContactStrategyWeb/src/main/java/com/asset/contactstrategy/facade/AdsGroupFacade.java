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
import com.asset.contactstrategy.common.models.AdsGroupModel;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.webmodels.AdsGroupWebModel;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author kerollos.asaad
 */
public class AdsGroupFacade {

    private static final String moduleName = "AdsGroupFacade";

    public ArrayList<AdsGroupWebModel> getAdsGroups() throws CommonException {
        String methodName = "getAdsGroups";
        ArrayList<AdsGroupWebModel> ret = new ArrayList<>();
        try {
            MainService mainService = new MainService();
            ArrayList<AdsGroupModel> groupsList = mainService.getAdsGroupsList();
            for (AdsGroupModel groupModel : groupsList) {
                AdsGroupWebModel groupWebModel = new AdsGroupWebModel();
                groupWebModel.setVersionId(groupModel.getVersionId());
                groupWebModel.setCreatedBy(groupModel.getCreatedBy());
                groupWebModel.setDailyThreshold(groupModel.getDailyThreshold());
                groupWebModel.setDonotContact(groupModel.getDonotContact());
                groupWebModel.setFilterQuery(groupModel.getFilterQuery());
                groupWebModel.setGroupDescription(groupModel.getGroupDescription());
                groupWebModel.setGroupId(groupModel.getGroupId());
                groupWebModel.setGroupName(groupModel.getGroupName());
                groupWebModel.setGroupPriority(groupModel.getGroupPriority());
                groupWebModel.setGuardPeriod(groupModel.getGuardPeriod());
                groupWebModel.setMonthlyThreshold(groupModel.getMonthlyThreshold());
                groupWebModel.setStatus(groupModel.getStatus());
                groupWebModel.setWeeklyThreshold(groupModel.getWeeklyThreshold());
                groupWebModel.setGroupType(groupModel.getGroupType());
                if (groupWebModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //check if operation has child
                    boolean hasChild = false;
                    for (AdsGroupModel child : groupsList) {
                        if (groupModel.getGroupId() == child.getGroupId() && groupModel.getVersionId()!= child.getVersionId()) {
                            //operation has edited version
                            hasChild = true;
                            groupWebModel.setEnableDelete(true);
                            groupWebModel.setEnableEdit(false);
                            groupWebModel.setEnableApprove(false);
                            groupWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                            break;
                        }
                    }
                    if (!hasChild) {
                        groupWebModel.setEnableDelete(true);
                        groupWebModel.setEnableEdit(true);
                        groupWebModel.setEnableApprove(false);
                        groupWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                    }
                } else if (groupWebModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    groupWebModel.setEnableDelete(true);
                    groupWebModel.setEnableEdit(true);
                    groupWebModel.setEnableApprove(true);
                    groupWebModel.setStatusName(GeneralConstants.STATUS_PENDING);
                } else {
                    groupWebModel.setEnableDelete(false);
                    groupWebModel.setEnableEdit(false);
                    groupWebModel.setEnableApprove(true);
                    groupWebModel.setStatusName(GeneralConstants.STATUS_PENDING_FOR_DELETION);
                }
                ret.add(groupWebModel);
            }
            return ret;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void addNewAdsGroup(AdsGroupWebModel group, int actorType) throws CommonException {
        String methodName = "addNewAdsGroup";
        try {
            MainService mainService = new MainService();
            //Update check adsgroup with similar name (unique constaints)
            AdsGroupModel modelWithName = mainService.getAdsGroupByName(group.getGroupName().trim());
            if (modelWithName != null) { //check for name 
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_NAME + "---->  for [" + methodName + "]" + " name " + group.getGroupName() + " is used before");
                throw new CommonException("Name of AdsGroup is used before", ErrorCodes.UNIQUE_NAME);
            }
            //Update check adsgroup with similar priority (unique constaints)
            AdsGroupModel modelWithPriority = mainService.getAdsGroupByPriority(group.getGroupPriority(),group.getGroupName());
            if (modelWithPriority != null) { //check for priority
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_PRIORITY + "---->  for [" + methodName + "]" + " priority " + group.getGroupPriority() + " is used before");
                throw new CommonException("Priority of AdsGroup is used before", ErrorCodes.UNIQUE_PRIORITY);
            }
            
            if (((modelWithName == null) //check for name
                    || ((modelWithName.getGroupName() == null) && (modelWithName.getVersionId()== 0)))
                    && ((modelWithPriority == null) //check for priority
                    || ((modelWithPriority.getGroupName() == null) && (modelWithPriority.getVersionId()== 0)))) {
                mainService.addNewAdsGroup(group, actorType);
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }
            
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void editAdsGroup(AdsGroupModel group, int actorType) throws CommonException {
        String methodName = "editAdsGroup";
        try {
            MainService mainService = new MainService();
            //Update check adsgroup with similar priority (unique constaints)
            AdsGroupModel modelWithPriority = mainService.getAdsGroupByPriority(group.getGroupPriority(), group.getGroupName());
            if (modelWithPriority == null) {
                mainService.editAdsGroup(group, actorType);
            } else { //check for priority
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_PRIORITY + "---->  for [" + methodName + "]" + " priority " + group.getGroupPriority() + " is used before");
                throw new CommonException("Priority of AdsGroup is used before", ErrorCodes.UNIQUE_PRIORITY);
            }
            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void approveAdsGroup(AdsGroupModel group) throws CommonException {
        String methodName = "approveAdsGroup";
        try {
            // Rough/vulnerable design, this shall be changed using only single connection through one MainService method.
            // until things come together.
            MainService mainService = new MainService();
            mainService.approveAdsGroup(group);
            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
//            mainService.changeGroupStatus(group.getGroupId(), GeneralConstants.STATUS_APPROVED_VALUE);
//            mainService.deleteGroup(group.getGroupId());
//            mainService.changeGroupEditedID(group.getGroupId(), group.getGroupEditId());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public ArrayList<DWHElementModel> loadAllCommercialElements() throws CommonException {
        String methodName = "loadAllCommercialElements";
        try {
            MainService mainService = new MainService();
            return mainService.loadAllCommercialElements();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
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

    public ArrayList<FilterModel> retrieveAdsGroupFilters(int groupId) throws CommonException {
        String methodName = "retrieveAdsGroupFilters";
        try {
            MainService mainService = new MainService();
            return mainService.retrieveAdsGroupFilters(groupId);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public ArrayList<FileModel> retrieveAdsGroupFiles(int groupId) throws CommonException {
        String methodName = "retrieveAdsGroupFiles";
        try {
            MainService mainService = new MainService();
            return mainService.retrieveAdsGroupFiles(groupId);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void deleteAdsGroup(AdsGroupWebModel selectedGroup, int actorType) throws CommonException {
        String methodName = "deleteAdsGroup";
        try {
            if (selectedGroup.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                selectedGroup.setFilterList(retrieveAdsGroupFilters(selectedGroup.getVersionId()));
            } else {
                selectedGroup.setFilesModel(retrieveAdsGroupFiles(selectedGroup.getVersionId()));
            }
            MainService mainService = new MainService();
            mainService.deleteAdsGroup(selectedGroup, actorType);
            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public MOMErrorsModel getMOMErrorModel(String errorCode, String methodName) {
        MOMErrorsModel mErrorsModel = new MOMErrorsModel();
        mErrorsModel.setEngineSrcID(GeneralConstants.SRC_ID_WEB_INTERFACE);
        mErrorsModel.setEntryDate(new Date());
        mErrorsModel.setErrorCode(errorCode);
        mErrorsModel.setModuleName(AdsGroupFacade.moduleName);
        mErrorsModel.setFunctionName(methodName);
        mErrorsModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        return mErrorsModel;
    }

    public void rejectAdsGroup(AdsGroupModel group) throws CommonException {
        String methodName = "rejectAdsGroup";
        try {
            MainService mainService = new MainService();
            mainService.rejectAdsGroup(group);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }
}
