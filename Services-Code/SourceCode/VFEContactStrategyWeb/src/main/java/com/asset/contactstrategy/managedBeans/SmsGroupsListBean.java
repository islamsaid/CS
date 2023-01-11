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
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.facade.SmsGroupFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.AdsGroupWebModel;
import com.asset.contactstrategy.webmodels.SmsGroupWebModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.http.HttpSession;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author kerollos.asaad
 */
@ManagedBean(name = "groupsBean")
@ViewScoped
public class SmsGroupsListBean implements Serializable {

    private static final String BEAN_NAME = "SmsGroupsListBean";
    private static final long serialVersionUID = 1L;
    private HttpSession session;
    private UserModel loggedInUser = new UserModel();
    private ArrayList<SmsGroupWebModel> groupsList = new ArrayList<SmsGroupWebModel>();
    private boolean enableCreate = true;
    private SmsGroupFacade groupFacade = new SmsGroupFacade();

    public SmsGroupsListBean() {
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "SmsGroupsListBean:started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "smsGroupListBean started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME).build());
        Date stepStartDate = new Date();
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        loggedInUser = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE);

        getGroupList();
        checkAlertMessages();

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "SmsGroupsListBean:ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "smsGroupListBean Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void getGroupList() {
        try {
            groupsList = groupFacade.getSmsGroups();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getGroupList]", ex);
            FacesMessage errorMessage = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Database error", "Database error");
            FacesContext.getCurrentInstance().addMessage(null, errorMessage);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SmsGroupsListBean.class.getName() + ".getGroupList]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public void checkAlertMessages() {
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "checkAlertMessages:started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkAlertMessages started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME).build());
        Date stepStartDate = new Date();
        String Message = "";
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash != null && flash.containsKey("ResetMessage")) {
            Message = (String) flash.get("ResetMessage");
            FacesMessage curMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, Message, null);
            FacesContext.getCurrentInstance().addMessage(null, curMessage);
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "checkAlertMessages:ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkAlertMessages Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public String viewGroup(SmsGroupWebModel selectedGroup) {
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "viewGroup:started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "viewGroup started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME).build());
//        CommonLogger.businessLogger.info(BEAN_NAME + " || " + "viewGroup || view group with ID is= " + selectedGroup.getVersionId());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, BEAN_NAME + "view group")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, selectedGroup.getVersionId()).build());
        Date stepStartDate = new Date();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", selectedGroup);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isView", true);
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("editable", selectedGroup.isEnableEdit());
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", selectedGroup.isEnableApprove());
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "viewGroup:ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "viewGroup Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "SmsGroupEditView.xhtml?faces-redirect=true";
    }

    public String editGroup(SmsGroupWebModel selectedGroup) {
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "editGroup:started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editGroup started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME).build());
//        CommonLogger.businessLogger.info(BEAN_NAME + " || " + "editGroup || edit group with ID is= " + selectedGroup.getVersionId());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, BEAN_NAME + "edit group")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, selectedGroup.getVersionId()).build());
        Date stepStartDate = new Date();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", selectedGroup);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("editable", selectedGroup.isEnableEdit());
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", selectedGroup.isEnableApprove());
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "editGroup:ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editGroup Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "SmsGroupEditView.xhtml?faces-redirect=true";
    }

    public String approveGroup(SmsGroupWebModel selectedGroup) {
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "approveGroup:started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "approveGroup started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME).build());
//        CommonLogger.businessLogger.info(BEAN_NAME + " || " + "approveGroup || approve group with ID is= " + selectedGroup);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, BEAN_NAME + "approve group")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, selectedGroup.getVersionId()).build());
        Date stepStartDate = new Date();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", selectedGroup);
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("editable", selectedGroup.isEnableEdit());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", selectedGroup.isEnableApprove());
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "approveGroup:ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "approveGroup Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "SmsGroupEditView.xhtml?faces-redirect=true";
    }

    public String createGroup() {
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "createGroup:started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createGroup started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME).build());
        Date stepStartDate = new Date();

        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("creation", true);

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "createGroup:ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createGroup Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "SmsGroupEditView.xhtml?faces-redirect=true";
    }

    public void deleteGroup(SmsGroupWebModel selectedGroup) {
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "deleteGroup:started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteGroup started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME).build());
        Date stepStartDate = new Date();
        try {
            //Update Retrieve All Information Of Sms Group
            if (selectedGroup.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                selectedGroup.setFilterList(this.groupFacade.retrieveSmsGroupFilters(selectedGroup.getVersionId()));
            } else {
                selectedGroup.setFilesModel(this.groupFacade.retrieveSmsGroupFiles(selectedGroup.getVersionId()));
            }
            groupFacade.deleteSmsGroup(selectedGroup, loggedInUser.getUserType());
            getGroupList();
            Utility.showInfoMessage(null, Constants.ITEM_DELETED, "Sms Group", selectedGroup.getGroupName());
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:groupsTable");
            if (dataTable != null) {
                dataTable.reset();
            }
            insertLog("deleteGroup", selectedGroup);
        } catch (CommonException ex) {
            Logger.getLogger(SmsGroupsListBean.class.getName()).log(Level.SEVERE, null, ex);
            Utility.showErrorMessage(null, Constants.DELETE_ITEM_FAILURE, " Sms Group", selectedGroup.getGroupName());
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SmsGroupsListBean.class.getName() + ".deleteGroup]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("creation", true);
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + "deleteGroup:ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createGroup Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    /////////////////////////////
    /////Setters and Getters/////
    /////////////////////////////
    public ArrayList<SmsGroupWebModel> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(ArrayList<SmsGroupWebModel> groupsList) {
        this.groupsList = groupsList;
    }

    public boolean isEnableCreate() {
        return enableCreate;
    }

    public void setEnableCreate(boolean enableCreate) {
        this.enableCreate = enableCreate;
    }

    public void insertLog(String methodName, SmsGroupWebModel selectedGroup) {
        WebLogModel webLog = new WebLogModel();
        webLog.setOperationName("Delete SMS Group");
        webLog.setPageName("SMS Group List");
        webLog.setUserName(loggedInUser.getUsername());
        webLog.setStringAfter(null);
        webLog.setStringBefore(selectedGroup.toString());
        try {
            WebLoggerFacade.insertWebLog(webLog);
        } catch (CommonException ex) {
            Logger.getLogger(AdsGroupsListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Date stepEndDate = new Date();
    }

}
