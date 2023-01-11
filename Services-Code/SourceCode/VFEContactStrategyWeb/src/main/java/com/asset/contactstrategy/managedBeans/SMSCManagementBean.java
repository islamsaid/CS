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
import com.asset.contactstrategy.facade.SMSCFacade;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.log4j.spi.ErrorCode;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.password.Password;
import org.primefaces.context.RequestContext;

/**
 *
 * @author rania.magdy
 */
@ManagedBean(name = "smscManagementBean")
@ViewScoped
public class SMSCManagementBean implements Serializable {

    private SMSCWebModel selectedSMSC;
    private Integer managementMode;
    private String rightOldPassword;
    private UserModel userModel;
    private SMSCWebModel oldSelectedSMSC;

    @PostConstruct
    public void init() {
//        userModel = new UserModel();
//        userModel.setUserType(GeneralConstants.USER_TYPE_OPERATIONAL_VALUE);
//        userModel.setId(1);
        try {
            userModel = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE);

            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            managementMode = (Integer) ec.getRequestMap().get(Constants.MANAGEMENT_MODE);
            if (managementMode == Constants.EDIT_MODE) {
                this.selectedSMSC = (SMSCWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
                selectedSMSC.setIsEditMode(true);
                rightOldPassword = selectedSMSC.getPassword();
                oldSelectedSMSC = new SMSCWebModel(selectedSMSC);
            } else if (managementMode == Constants.VIEW_MODE) {
                this.selectedSMSC = (SMSCWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
                selectedSMSC.setIsViewMode(true);

            } else if (managementMode == Constants.CREATION_MODE) {
                selectedSMSC = new SMSCWebModel();
                selectedSMSC.setIsCreationMode(true);
            } else if (managementMode == Constants.APPROVAL_MODE) {
                this.selectedSMSC = (SMSCWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
                selectedSMSC.setApprovalMode(true);
                if (selectedSMSC.getDescription() == null) {
                    selectedSMSC.setDescription("");
                }
                rightOldPassword = selectedSMSC.getPassword();
                oldSelectedSMSC = new SMSCWebModel(selectedSMSC);
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSCManagementBean.class.getName() + ".init]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
    }

    public SMSCWebModel getSelectedSMSC() {
        return selectedSMSC;
    }

    public void setSelectedSMSC(SMSCWebModel selectedSMSC) {
        this.selectedSMSC = selectedSMSC;
    }

    public String saveSMSC() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getMessageList().size() == 0) {
            CommonLogger.businessLogger.info(SMSCManagementBean.class.getName() + " || " + "Start : saveSMSC() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start saveSMSC()")
                    .build());
            WebLogModel webLog = new WebLogModel();

            try {
                SMSCFacade smscFacade = new SMSCFacade(userModel);
                if (managementMode == Constants.EDIT_MODE) {
                    if (selectedSMSC.getPassword() == null || selectedSMSC.getPassword().equals("")) {
                        selectedSMSC.setPassword(rightOldPassword);
                    }
                    smscFacade.editSMSC(selectedSMSC);
                    Utility.showInfoMessage(null, Constants.ITEM_EDITED, "SMSC", selectedSMSC.getSMSCname());
                    webLog.setOperationName("Update SMSC");
                    webLog.setStringBefore(oldSelectedSMSC.toString());
                    webLog.setStringAfter(selectedSMSC.toString());
                    webLog.setPageName("SMSC Management");
                    //get user from session
                    webLog.setUserName(userModel.getUsername());
                    WebLoggerFacade.insertWebLog(webLog);
                    return "SMSCList.xhtml";

                } else if (managementMode == Constants.CREATION_MODE) {
                    if (selectedSMSC.getPassword().equals(selectedSMSC.getPasswordConfirm())) {
                        selectedSMSC = smscFacade.createSMSC(selectedSMSC);
                        Utility.showInfoMessage(null, Constants.ITEM_ADDED, "SMSC", selectedSMSC.getSMSCname());
                        webLog.setOperationName("Create SMSC");
                        webLog.setStringBefore(null);
                        webLog.setStringAfter(selectedSMSC.toString());
                        webLog.setPageName("SMSC Management");
                        //get user from session
                        webLog.setUserName(userModel.getUsername());
                        WebLoggerFacade.insertWebLog(webLog);
                        return "SMSCList.xhtml";
                    } else {
                        Utility.showErrorMessage(null, Constants.CONFIRM_PASSWORD);
                    }
                }
                webLog.setPageName("SMSC Management");
                //get user from session
                webLog.setUserName(userModel.getUsername());
                WebLoggerFacade.insertWebLog(webLog);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [saveSMSC]", ex);

                if (ex.getErrorCode() == ErrorCodes.UNIQUE_NAME) {
                    Utility.showErrorMessage(null, ErrorCodes.UNIQUE_NAME, "SMSC", selectedSMSC.getSMSCname());

                } else {
                    Utility.showErrorMessage(null, ex.getErrorCode(), "in saving SMSC");
                }
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSCManagementBean.class.getName() + ".saveSMSC]", e);
                Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            }
        }
//        CommonLogger.businessLogger.info(SMSCManagementBean.class.getName() + " || " + "End : saveSMSC() ");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start saveSMSC()")
                .build());

        return null;
    }

    public String approveSMSCOperation() {
        try {
            int status = selectedSMSC.getStatus();
            SMSCFacade smscFacade = new SMSCFacade(userModel);
            smscFacade.approveSMSCOperation(selectedSMSC);

            WebLogModel webLog = new WebLogModel();
            if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedSMSC.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_EDIT)) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedSMSC.getSMSCname(), "editing");
                webLog.setOperationName("Approve SMSC editing");
                webLog.setStringBefore(oldSelectedSMSC.toString());
                webLog.setStringAfter(selectedSMSC.toString());
            } else if (selectedSMSC.getStatus() == GeneralConstants.STATUS_PENDING_VALUE && selectedSMSC.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_CREATION)) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedSMSC.getSMSCname(), "creation");
                webLog.setOperationName("Approve SMSC creation");
                webLog.setStringBefore(null);
                webLog.setStringAfter(selectedSMSC.toString());
            } else if (status == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedSMSC.getSMSCname(), "deletion");
                webLog.setOperationName("Approve SMSC deletion");
                webLog.setStringBefore(selectedSMSC.toString());
                webLog.setStringAfter(null);
            }

            webLog.setPageName("SMSC Management");
            //get user from session
            webLog.setUserName(userModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            return "OperationsApproval.xhtml?faces-redirect=true";

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveSMSCOperation]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), "in approving");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSCManagementBean.class.getName() + ".approveSMSCOperation]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        return null;
    }

    public void openChangePasswordDialog() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('changePasswordDialog').show();");
    }

    public void closeChangePasswordDialog() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (selectedSMSC.getOldPassword() != null || !selectedSMSC.getOldPassword().equals("")) {
            if (selectedSMSC.getOldPassword().equals(rightOldPassword)) {
                if (selectedSMSC.getPassword().equals(selectedSMSC.getPasswordConfirm())
                        && !selectedSMSC.getPassword().equals("")) {

                    context.execute("PF('changePasswordDialog').hide();");

                } else {
                    Utility.showErrorMessage("form:passwordDialogMessages", Constants.CONFIRM_PASSWORD);
                }
            } else {
                Utility.showErrorMessage("form:passwordDialogMessages", Constants.INVALID_OLD_PASSWORD);
            }
        } else {
            Utility.showErrorMessage("form:passwordDialogMessages", Constants.MISSING_OLD_PASSWORD);
        }
    }

    public void requiredFieldsValidator(FacesContext context,
            UIComponent component, Object value) {

        String componentID = component.getId();
        if (componentID.equals("SMSCIP")) {
            boolean validationFailed = false;

            InputText nameComponent = (InputText) component.getAttributes().get("nameAttr");
            InputText portComponent = (InputText) component.getAttributes().get("portAttr");
            InputText systemTypeComponent = (InputText) component.getAttributes().get("systemTypeAttr");
            InputText usernameComponent = (InputText) component.getAttributes().get("usernameAttr");

            if (selectedSMSC.isIsCreationMode()) {
                Password passwordComponent = (Password) component.getAttributes().get("passwordAttr");
                Password confirmPasswordComponent = (Password) component.getAttributes().get("confirmPasswordAttr");
                String password = (String) passwordComponent.getSubmittedValue();
                String confirmPassword = (String) confirmPasswordComponent.getSubmittedValue();

//                System.out.println("passwordComponent || Value: " + passwordComponent.getValue() + " | SubmittedValue: " + passwordComponent.getSubmittedValue());
//                System.out.println("confirmPasswordComponent || Value: " + confirmPasswordComponent.getValue() + " | SubmittedValue: " + confirmPasswordComponent.getSubmittedValue());
                if (password == null || (password != null && password.trim().equals(""))) {
                    validationFailed = true;
                } else if (confirmPassword == null || (confirmPassword != null && confirmPassword.trim().equals(""))) {
                    validationFailed = true;
                } else if (!password.equals(confirmPassword)) {
                    validationFailed = true;
                    Utility.showErrorMessage(null, Constants.CONFIRM_PASSWORD);
                    return;
                }
            }
            String ip = value.toString();
            String SMSCName = (String) nameComponent.getValue();
            Integer port = (String.valueOf(portComponent.getSubmittedValue()).isEmpty()) ? null : Integer.parseInt(String.valueOf(portComponent.getSubmittedValue()));
            String systemType = (String) systemTypeComponent.getSubmittedValue();
            String username = (String) usernameComponent.getSubmittedValue();

//            System.out.println("nameComponent || Value: " + nameComponent.getValue() + " | SubmittedValue: " + nameComponent.getSubmittedValue());
//            System.out.println("portComponent || Value: " + portComponent.getValue() + " | SubmittedValue: " + portComponent.getSubmittedValue());
//            System.out.println("systemTypeComponent || Value: " + systemTypeComponent.getValue() + " | SubmittedValue: " + systemTypeComponent.getSubmittedValue());
//            System.out.println("usernameComponent || Value: " + usernameComponent.getValue() + " | SubmittedValue: " + usernameComponent.getSubmittedValue());
            if (SMSCName == null || (SMSCName != null && SMSCName.trim().equals(""))) {
                validationFailed = true;
            } else if (ip == null || (ip != null && ip.trim().equals(""))) {
                validationFailed = true;
            } else if (port == null || (port != null && port.equals(0))) {
                validationFailed = true;
            } else if (systemType == null || (systemType != null && systemType.trim().equals(""))) {
                validationFailed = true;
            } else if (username == null || (username != null && username.trim().equals(""))) {
                validationFailed = true;
            }

            if (validationFailed) {
                Utility.showErrorMessage(null, Constants.MISSING_DATA);
                return;
            }
            if (!ip.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")) {
                Utility.showErrorMessage(null, Constants.INVALID_IP);
            }
        }
    }

    public String rejectSMSCOperation() {
        try {
            int status = selectedSMSC.getStatus();
            SMSCFacade smscFacade = new SMSCFacade(userModel);
            smscFacade.rejectSMSCOperation(oldSelectedSMSC);

            WebLogModel webLog = new WebLogModel();
            if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedSMSC.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_EDIT)) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedSMSC.getSMSCname(), "editing");
                webLog.setOperationName("Reject SMSC editing");
                webLog.setStringBefore(oldSelectedSMSC.toString());
                webLog.setStringAfter(null);
            } else if (selectedSMSC.getStatus() == GeneralConstants.STATUS_PENDING_VALUE && selectedSMSC.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_CREATION)) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedSMSC.getSMSCname(), "creation");
                webLog.setOperationName("Reject SMSC creation");
                webLog.setStringBefore(oldSelectedSMSC.toString());
                webLog.setStringAfter(null);
            } else if (status == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedSMSC.getSMSCname(), "deletion");
                webLog.setOperationName("Reject SMSC deletion");
                webLog.setStringAfter(oldSelectedSMSC.toString());
                oldSelectedSMSC.setStatus(GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE);
                webLog.setStringBefore(oldSelectedSMSC.toString());
            }

            webLog.setPageName("SMSC Management");
            //get user from session
            webLog.setUserName(userModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            return "OperationsApproval.xhtml?faces-redirect=true";

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectSMSCOperation]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), "in rejection");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSCManagementBean.class.getName() + ".rejectSMSCOperation]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        return null;
    }

}
