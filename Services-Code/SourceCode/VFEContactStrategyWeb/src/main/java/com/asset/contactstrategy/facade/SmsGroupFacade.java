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
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.webmodels.SmsGroupWebModel;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author kerollos.asaad
 */
public class SmsGroupFacade {

    private static final String moduleName = "SMSGroups";

    public ArrayList<SmsGroupWebModel> getSmsGroups() throws CommonException {
        String methodName = "getSmsGroups";
        ArrayList<SmsGroupWebModel> ret = new ArrayList<>();
        try {
            MainService mainService = new MainService();
            ArrayList<SMSGroupModel> groupsList = mainService.getSmsGroupsList();
            for (SMSGroupModel groupModel : groupsList) {
                SmsGroupWebModel groupWebModel = new SmsGroupWebModel();
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
                    boolean smscHasChild = false;
                    for (SMSGroupModel child : groupsList) {
                        if (groupModel.getGroupId() == child.getGroupId() && groupModel.getVersionId() != child.getVersionId()) {
                            //operation has edited version
                            smscHasChild = true;
                            groupWebModel.setEnableDelete(true);
                            groupWebModel.setEnableEdit(false);
                            groupWebModel.setEnableApprove(false);
                            groupWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                            break;
                        }
                    }
                    if (!smscHasChild) {
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
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void addNewSmsGroup(SMSGroupModel group, int actorType) throws CommonException {
        String methodName = "addNewSmsGroup";
        try {
            MainService mainService = new MainService();
            //Update check smsgroup with similar name
            SMSGroupModel modelWithName = mainService.getSmsGroupByName(group.getGroupName().trim());
            if (modelWithName != null) { //check for name
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_NAME + "---->  for [" + methodName + "]" + " name " + group.getGroupName() + " is used before");
                throw new CommonException("Name of SmsGroup is used before", ErrorCodes.UNIQUE_NAME);
            }
            //Update check smsgroup unique priority
            SMSGroupModel modelWithPriority = mainService.getSmsGroupByPriority(group.getGroupPriority(),group.getGroupName());
            if (modelWithPriority != null) { //check for priority
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_PRIORITY + "---->  for [" + methodName + "]" + " priority " + group.getGroupPriority() + " is used before");
                throw new CommonException("Priority of SmsGroup is used before", ErrorCodes.UNIQUE_PRIORITY);
            }
            if (((modelWithName == null) //check for name
                    || ((modelWithName.getGroupName() == null) && (modelWithName.getVersionId() == 0)))
                    && ((modelWithPriority == null) //check for priority
                    || ((modelWithPriority.getGroupName() == null) && (modelWithPriority.getVersionId() == 0)))) {
                mainService.addNewSmsGroup(group, actorType);
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void editSmsGroup(SMSGroupModel group, int actorType) throws CommonException {
        String methodName = "editSmsGroup";
        try {
            MainService mainService = new MainService();
            SMSGroupModel modelWithPriority = mainService.getSmsGroupByPriority(group.getGroupPriority(),group.getGroupName());
            if (modelWithPriority != null) { //check for priority
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_PRIORITY + "---->  for [" + methodName + "]" + " priority " + group.getGroupPriority() + " is used before");
                throw new CommonException("Priority of SmsGroup is used before", ErrorCodes.UNIQUE_PRIORITY);
            }
            mainService.editSmsGroup(group, actorType);

            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void approveSmsGroup(SMSGroupModel group) throws CommonException {
        String methodName = "approveSmsGroup";
        try {
            MainService mainService = new MainService();
            mainService.approveSmsGroup(group);
            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
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
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
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
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public ArrayList<FilterModel> retrieveSmsGroupFilters(int groupId) throws CommonException {
        String methodName = "retrieveSmsGroupFilters";
        try {
            MainService mainService = new MainService();
            return mainService.retrieveSmsGroupFilters(groupId);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public ArrayList<FileModel> retrieveSmsGroupFiles(int groupId) throws CommonException {
        String methodName = "retrieveSmsGroupFiles";
        try {
            MainService mainService = new MainService();
            return mainService.retrieveSmsGroupFiles(groupId);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void deleteSmsGroup(SmsGroupWebModel selectedGroup, int actorType) throws CommonException {
        String methodName = "deleteSmsGroup";
        try {
            if (selectedGroup.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                selectedGroup.setFilterList(retrieveSmsGroupFilters(selectedGroup.getVersionId()));
            } else {
                selectedGroup.setFilesModel(retrieveSmsGroupFiles(selectedGroup.getVersionId()));
            }
            MainService mainService = new MainService();
            mainService.deleteSmsGroup(selectedGroup, actorType);
            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
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
        mErrorsModel.setModuleName(SmsGroupFacade.moduleName);
        mErrorsModel.setFunctionName(methodName);
        mErrorsModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        return mErrorsModel;
    }

    public void rejectSmsGroup(SMSGroupModel group) throws CommonException {
        String methodName = "rejectSmsGroup";
        try {
            MainService mainService = new MainService();
            mainService.rejectSmsGroup(group);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(SmsGroupFacade.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), methodName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

}
