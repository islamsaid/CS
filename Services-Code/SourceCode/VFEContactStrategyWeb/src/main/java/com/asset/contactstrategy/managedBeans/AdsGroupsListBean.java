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
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.facade.AdsGroupFacade;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.AdsGroupWebModel;
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
@ManagedBean(name = "adsGroupsBean")
@ViewScoped
public class AdsGroupsListBean implements Serializable {

    private static final String BEAN_NAME = "AdsGroupsListBean";
    private static final long serialVersionUID = 1L;
    private final HttpSession session;
    private UserModel loggedInUser = new UserModel();
    private ArrayList<AdsGroupWebModel> groupsList = new ArrayList<AdsGroupWebModel>();
    private boolean enableCreate = true;
    private final AdsGroupFacade groupFacade = new AdsGroupFacade();

    public AdsGroupsListBean() {
        String methodName = "AdsGroupsListBean";

//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        UserFacade userFacade = new UserFacade();
        loggedInUser = userFacade.convertModelToWebModel((UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));

        getGroupList();
        checkAlertMessages();

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void getGroupList() {
        String methodName = "getGroupList";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        try {
            groupsList = groupFacade.getAdsGroups();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupsListBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            FacesMessage errorMessage = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Database error", "Database error");
            FacesContext.getCurrentInstance().addMessage(null, errorMessage);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsListBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public void checkAlertMessages() {
        String methodName = "checkAlertMessages";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
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
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public String viewGroup(AdsGroupWebModel selectedGroup) {
        String methodName = "viewGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
//        CommonLogger.businessLogger.info(BEAN_NAME + " || " + methodName + "|| view group with ID is= " + selectedGroup.getVersionId());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, BEAN_NAME + " " + methodName + "View Group")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, selectedGroup.getVersionId()).build());
        Date stepStartDate = new Date();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", selectedGroup);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isView", true);
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("editable", selectedGroup.isEnableEdit());
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", selectedGroup.isEnableApprove());
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "AdsGroupEditView.xhtml?faces-redirect=true";
    }

    public String editGroup(AdsGroupWebModel selectedGroup) {
        String methodName = "editGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
//        CommonLogger.businessLogger.info(BEAN_NAME + " || " + methodName + "|| edit group with ID is= " + selectedGroup.getVersionId());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, BEAN_NAME + " " + methodName + "edit Group")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, selectedGroup.getVersionId()).build());
        Date stepStartDate = new Date();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", selectedGroup);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("editable", selectedGroup.isEnableEdit());
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", selectedGroup.isEnableApprove());
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "AdsGroupEditView.xhtml?faces-redirect=true";
    }

    public String approveGroup(AdsGroupWebModel selectedGroup) {
        String methodName = "approveGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
//        CommonLogger.businessLogger.info(BEAN_NAME + " || " + methodName + "|| approve group with ID is= " + selectedGroup);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, BEAN_NAME + " " + methodName + "edit Group")
                .put(GeneralConstants.StructuredLogKeys.GROUP_ID, selectedGroup.getVersionId()).build());
        Date stepStartDate = new Date();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("SelectedParameter", selectedGroup);
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("editable", selectedGroup.isEnableEdit());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("approvable", selectedGroup.isEnableApprove());
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "AdsGroupEditView.xhtml?faces-redirect=true";
    }

    public String createGroup() {
        String methodName = "createGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();

        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("creation", true);

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "AdsGroupEditView.xhtml?faces-redirect=true";
    }

    public void deleteGroup(AdsGroupWebModel selectedGroup) {
        String methodName = "deleteGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        try {
            groupFacade.deleteAdsGroup(selectedGroup, loggedInUser.getUserType());
            getGroupList();//Update
            Utility.showInfoMessage(null, Constants.ITEM_DELETED, "Group", selectedGroup.getGroupName());
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:groupsTable");
            if (dataTable != null) {
                dataTable.reset();
            }
            insertLog(methodName, selectedGroup);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupsListBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            Utility.showErrorMessage(null, Constants.DELETE_ITEM_FAILURE, "Group", selectedGroup.getGroupName());

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsListBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    /////////////////////////////
    /////Setters and Getters/////
    /////////////////////////////
    public ArrayList<AdsGroupWebModel> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(ArrayList<AdsGroupWebModel> groupsList) {
        this.groupsList = groupsList;
    }

    public boolean isEnableCreate() {
        return enableCreate;
    }

    public void setEnableCreate(boolean enableCreate) {
        this.enableCreate = enableCreate;
    }

    public void insertLog(String methodName, AdsGroupWebModel selectedGroup) {
        WebLogModel webLog = new WebLogModel();
        webLog.setOperationName("Delete Ads Group");
        webLog.setPageName("AdsGroupListView");
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
