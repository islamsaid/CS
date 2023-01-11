/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.facade.QueueFacade;
import com.asset.contactstrategy.facade.SMSCFacade;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.QueueWebModel;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import com.asset.contactstrategy.webmodels.UserWebModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.picklist.PickList;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Yomna Naser
 */
@ManagedBean
@ViewScoped
public class QueueBean implements Serializable {

    private QueueWebModel queueModel;
    private HashMap<Integer, LookupModel> queueTypeLK;
    private QueueWebModel oldQueueModel;
    private boolean createMode;
    private boolean editMode;
    private boolean viewMode;
    private boolean queueTypeView;
    private boolean approvalMode;
    private boolean disableTimeWindowFields;
    private DualListModel<SMSCWebModel> smscDualList;
    private ArrayList<SMSCWebModel> smscListSelected;
    private QueueFacade queueFacade;
    private SMSCFacade smscFacade;

    private UserWebModel loggedInUser;

    @PostConstruct
    public void init() {
        try {
            CommonLogger.businessLogger.info("Started Queue Bean");
            CommonLogger.businessLogger.info("Gathering Data For Bean");
            UserFacade userFacade = new UserFacade();
            loggedInUser = userFacade.convertModelToWebModel((UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            Integer managementMode = (Integer) ec.getRequestMap().get(Constants.MANAGEMENT_MODE);
            //if(managementMode != null){
            if (managementMode.equals(Constants.CREATION_MODE)) {
                createMode = true;
            } else if (managementMode.equals(Constants.EDIT_MODE)) {
                editMode = true;
            } else if (managementMode.equals(Constants.VIEW_MODE)) {
                viewMode = true;
            } else if (managementMode.equals(Constants.APPROVAL_MODE)) {
                approvalMode = true;
            }
            //}else
            // createMode = true;

            queueFacade = new QueueFacade();
            smscFacade = new SMSCFacade(new UserModel());
            ArrayList<SMSCWebModel> smscModels = smscFacade.getApprovedSMSCs();
            setQueueTypeLK(SystemLookups.QUEUE_TYPE_LK);

            if (createMode) {
                queueModel = new QueueWebModel();
                queueModel.setDequeuePoolSize(1);
                queueModel.setThreshold(1);
                queueModel.setSenderPoolSize(1);
                queueModel.setCreator((int) loggedInUser.getId());
                queueModel.setCreatorName(loggedInUser.getUsername());
                queueModel.setTimeWindowFromHour(0);
                queueModel.setTimeWindowFromMin(0);
                queueModel.setTimeWindowToHour(0);
                queueModel.setTimeWindowToMin(0);
                smscDualList = new DualListModel<>(smscModels, new ArrayList<SMSCWebModel>());
            } else {
                queueModel = (QueueWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
                queueModel.setQueueType(getQueueModel().getQueueType());
                oldQueueModel = new QueueWebModel(queueModel);
                smscListSelected = queueFacade.getApplicationQueuesSMSCs(queueModel.getVersionId());
                smscModels.removeAll(smscListSelected);
                smscDualList = new DualListModel<>(smscModels, smscListSelected);
                ArrayList<SMSCModel> smscModelsSelected = new ArrayList<SMSCModel>();
                for (SMSCWebModel sMSCWebModel : smscListSelected) {
                    SMSCModel smscModel = smscFacade.convertWebModelToModel(sMSCWebModel);
                    smscModelsSelected.add(smscModel);
                }
                queueModel.setSmscModels(smscModelsSelected);

                if (editMode) {
                    change();
                    queueModel.setLastModifiedBy((int) loggedInUser.getId());
                    queueModel.setLastModifiedByName(loggedInUser.getUsername());
                }
            }

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueueListener]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + QueueBean.class.getName() + ".createApplicationQueueListener]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
    }

    public void change() {
        if (queueModel.getQueueType() == 2) {
            setQueueTypeView(true);
        } else {
            setQueueTypeView(false);
        }
    }

    public QueueWebModel getQueueModel() {
        return queueModel;
    }

    public void setQueueModel(QueueWebModel queueModel) {
        this.queueModel = queueModel;
    }

    public boolean isCreateMode() {
        return createMode;
    }

    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }

    public boolean isApprovalMode() {
        return approvalMode;
    }

    public void setApprovalMode(boolean approvalMode) {
        this.approvalMode = approvalMode;
    }

    public boolean isViewMode() {
        return viewMode;
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isDisableTimeWindowFields() {
        return disableTimeWindowFields;
    }

    public void setDisableTimeWindowFields(boolean disableTimeWindowFields) {
        this.disableTimeWindowFields = disableTimeWindowFields;
    }

    public DualListModel<SMSCWebModel> getSmscDualList() {
        return smscDualList;
    }

    public void setSmscDualList(DualListModel<SMSCWebModel> smscDualList) {
        this.smscDualList = smscDualList;
    }

    public ArrayList<SMSCWebModel> getSmscListSelected() {
        return smscListSelected;
    }

    public void setSmscListSelected(ArrayList<SMSCWebModel> smscListSelected) {
        this.smscListSelected = smscListSelected;
    }

    public void requiredFieldsValidator(FacesContext context,
            UIComponent component, Object value) {
        CommonLogger.businessLogger.info("Started Field Validation For Queue Bean");
        String componentID = component.getId();
        if (componentID.equals("dequeue_pool_size")) {

            InputText appQueueNameComponent = (InputText) component.getAttributes().get("appQueueNameAttr");
            Spinner senderPoolSizeComponent = (Spinner) component.getAttributes().get("senderPoolSizeAttr");
            PickList smscComponent = (PickList) component.getAttributes().get("smscAttr");
            SelectBooleanCheckbox timeWindowFlagComponent = (SelectBooleanCheckbox) component.getAttributes().get("timeWindowFlagAttr");
            Spinner timeWindowFromHrComp = (Spinner) component.getAttributes().get("timeWindowFromHrAttr");
            Spinner timwWindowFromMinComp = (Spinner) component.getAttributes().get("timeWindowFromMinAttr");
            Spinner timeWindowToHrComp = (Spinner) component.getAttributes().get("timeWindowToHrAttr");
            Spinner timeWindowToMinComp = (Spinner) component.getAttributes().get("timeWindowToMinAttr");
            InputText databaseURLComp = (InputText) component.getAttributes().get("databaseURLAttr");
            InputText schemaNameComp = (InputText) component.getAttributes().get("schemaNameAttr");
            InputText schemaPasswordComp = (InputText) component.getAttributes().get("schemaPasswordAttr");
            InputText tableSpaceNameComp = (InputText) component.getAttributes().get("tableSpaceNameAttr");

            Integer dequeuePoolSize = (Integer) value;
            String appQueueName = (String) appQueueNameComponent.getValue();
            Integer senderPoolSize = (senderPoolSizeComponent.getSubmittedValue() != null) ? Integer.parseInt(String.valueOf(senderPoolSizeComponent.getSubmittedValue())) : Integer.parseInt(String.valueOf(senderPoolSizeComponent.getValue()));
            DualListModel dualListModel = (DualListModel) smscComponent.getValue();
            List items = dualListModel.getTarget();
            Boolean timeWindowFlag = (Boolean) timeWindowFlagComponent.getValue();
            Integer timeWindowFromHr = (timeWindowFromHrComp.getValue() == null && (timeWindowFromHrComp.getSubmittedValue() == null || timeWindowFromHrComp.getSubmittedValue().equals(""))) ? 0 : timeWindowFromHrComp.getSubmittedValue() == null ? Integer.parseInt(String.valueOf(timeWindowFromHrComp.getValue())) : Integer.parseInt(String.valueOf(timeWindowFromHrComp.getSubmittedValue()));
            Integer timwWindowFromMin = (timwWindowFromMinComp.getValue() == null && (timwWindowFromMinComp.getSubmittedValue() == null || timwWindowFromMinComp.getSubmittedValue().equals(""))) ? 0 : timwWindowFromMinComp.getSubmittedValue() == null ? Integer.parseInt(String.valueOf(timwWindowFromMinComp.getValue())) : Integer.parseInt(String.valueOf(timwWindowFromMinComp.getSubmittedValue()));
            Integer timeWindowToHr = (timeWindowToHrComp.getValue() == null && (timeWindowToHrComp.getSubmittedValue() == null || timeWindowToHrComp.getSubmittedValue().equals(""))) ? 0 : timeWindowToHrComp.getSubmittedValue() == null ? Integer.parseInt(String.valueOf(timeWindowToHrComp.getValue())) : Integer.parseInt(String.valueOf(timeWindowToHrComp.getSubmittedValue()));
            Integer timeWindowToMin = (timeWindowToMinComp.getValue() == null && (timeWindowToMinComp.getSubmittedValue() == null || timeWindowToMinComp.getSubmittedValue().equals(""))) ? 0 : timeWindowToMinComp.getSubmittedValue() == null ? Integer.parseInt(String.valueOf(timeWindowToMinComp.getValue())) : Integer.parseInt(String.valueOf(timeWindowToMinComp.getSubmittedValue()));
            String databaseURL = (createMode) ? (String) databaseURLComp.getSubmittedValue() : (String) databaseURLComp.getValue();
            String schemaName = (createMode) ? (String) schemaNameComp.getSubmittedValue() : (String) schemaNameComp.getValue();
            String schemaPassword = (createMode) ? (String) schemaPasswordComp.getSubmittedValue() : (String) schemaPasswordComp.getValue();
            String tableSpaceName = (createMode) ? (String) tableSpaceNameComp.getSubmittedValue() : (String) tableSpaceNameComp.getValue();

            if (createMode && tableSpaceName == null) {
                tableSpaceName = (String) tableSpaceNameComp.getValue();
            }

//            System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
//            System.out.println("appQueueNameComponent || Value: " + appQueueNameComponent.getValue() + " | SubmittedValue: " + appQueueNameComponent.getSubmittedValue());
//            System.out.println("senderPoolSizeComponent || Value: " + senderPoolSizeComponent.getValue() + " | SubmittedValue: " + senderPoolSizeComponent.getSubmittedValue());
//            System.out.println("smscComponent || Value: " + smscComponent.getValue() + " | SubmittedValue: " + smscComponent.getSubmittedValue());
//            System.out.println("timeWindowFlagComponent || Value: " + timeWindowFlagComponent.getValue() + " | SubmittedValue: " + timeWindowFlagComponent.getSubmittedValue());
//            System.out.println("timeWindowFromHrComp || Value: " + timeWindowFromHrComp.getValue() + " | SubmittedValue: " + timeWindowFromHrComp.getSubmittedValue());
//            System.out.println("timwWindowFromMinComp || Value: " + timwWindowFromMinComp.getValue() + " | SubmittedValue: " + timwWindowFromMinComp.getSubmittedValue());
//            System.out.println("timeWindowToHrComp || Value: " + timeWindowToHrComp.getValue() + " | SubmittedValue: " + timeWindowToHrComp.getSubmittedValue());
//            System.out.println("timeWindowToMinComp || Value: " + timeWindowToMinComp.getValue() + " | SubmittedValue: " + timeWindowToMinComp.getSubmittedValue());
//            System.out.println("databaseURLComp || Value: " + databaseURLComp.getValue() + " | SubmittedValue: " + databaseURLComp.getSubmittedValue());
//            System.out.println("schemaNameComp || Value: " + schemaNameComp.getValue() + " | SubmittedValue: " + schemaNameComp.getSubmittedValue());
//            System.out.println("schemaPasswordComp || Value: " + schemaPasswordComp.getValue() + " | SubmittedValue: " + schemaPasswordComp.getSubmittedValue());
//            System.out.println("tableSpaceNameComp || Value: " + tableSpaceNameComp.getValue() + " | SubmittedValue: " + tableSpaceNameComp.getSubmittedValue());
//            System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
            boolean validationFailed = false;
            if (dequeuePoolSize == null || (dequeuePoolSize != null && dequeuePoolSize.equals(""))) {
                validationFailed = true;
            } else if (appQueueName == null || (appQueueName != null && appQueueName.equals(0))) {
                validationFailed = true;
            } else if (senderPoolSize == null || (senderPoolSize != null && senderPoolSize.equals(0)) && queueModel.getQueueType() != 2) {
                validationFailed = true;
            } else if (items == null || (items != null && items.size() == 0) && queueModel.getQueueType() != 2) {
                validationFailed = true;
            } else if (databaseURL == null || (databaseURL != null && databaseURL.equals(""))) {
                validationFailed = true;
            } else if (schemaName == null || (schemaName != null && schemaName.equals(""))) {
                validationFailed = true;
            } else if (schemaPassword == null || (schemaPassword != null && schemaPassword.equals(""))) {
                validationFailed = true;
            } else if (createMode && (tableSpaceName == null || (tableSpaceName != null && tableSpaceName.equals(""))) && queueModel.getQueueType() != 2) {
                validationFailed = true;
            } else if (timeWindowFlag != null && timeWindowFlag) {

                if (timeWindowFromHr == null || (timeWindowFromHr != null && ((timeWindowFromHr < 0) || (timeWindowFromHr > 23)))) {
                    validationFailed = true;
                } else if (timwWindowFromMin == null || (timwWindowFromMin != null && ((timwWindowFromMin < 0) || (timwWindowFromMin > 59)))) {
                    validationFailed = true;
                } else if (timeWindowToHr == null || (timeWindowToHr != null && ((timeWindowToHr < 0) || (timeWindowToHr > 23)))) {
                    validationFailed = true;
                } else if (timeWindowToMin == null || (timeWindowToMin != null && ((timeWindowToMin < 0) || (timeWindowToMin > 59)))) {
                    validationFailed = true;
                }
            }

            if (validationFailed) {
                Utility.showErrorMessage(null, Constants.MISSING_DATA);
            }

            if ((databaseURL != null && !databaseURL.equals(""))
//                        && (!databaseURL.matches("^jdbc:[a-zA-Z]+:[a-zA-Z]+:@[/][/]((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])):\\d{1,5}[/][a-zA-Z]+$"))) {
                    && (!databaseURL.matches("jdbc:oracle:thin:@//(.*):([0-9]+)/([a-zA-Z0-9]*)"))) {
                Utility.showErrorMessage(null, Constants.INVALED_URL, "Database");
            }
            if (timeWindowFlag != null && timeWindowFlag) {
                if ((timeWindowFromHr != null) && (timwWindowFromMin != null) && (timeWindowToHr != null) && (timeWindowToMin != null)
                        && ((timeWindowFromHr > timeWindowToHr) || ((timeWindowFromHr == timeWindowToHr) && (timwWindowFromMin >= timeWindowToMin)))) {
                    Utility.showErrorMessage(null, Constants.INVALID_TIME);
                }
            }

        }
        RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");

    }

    public boolean checkQueueNameExists() throws CommonException {
        boolean queueExists = false;
        try {
            queueExists = queueFacade.checkApplicationQueueNameExists(queueModel.getAppName().trim());
            return queueExists;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [checkQueueNameExists]" + ex);
            throw ex;
        }

    }

    public String saveApplicationQueue() {
        if (createMode) {
            return createApplicationQueue();
        } else if (editMode) {
            return editApplicationQueue();
        }
        return null;
    }

    public String createApplicationQueue() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getMessageList().size() == 0) {
            try {

                if (checkQueueNameExists()) {
                    Utility.showErrorMessage(null, Constants.UNIQUE_NAME_ERROR, "Application Queue", queueModel.getAppName());
                    RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                } else {
                    queueFacade.createApplicationQueue(queueModel, (ArrayList<SMSCWebModel>) smscDualList.getTarget(), loggedInUser);
                    ArrayList<SMSCModel> smscSelectedModels = new ArrayList<>();
                    for (SMSCWebModel smscWebModel : smscDualList.getTarget()) {
                        SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                        smscSelectedModels.add(smscModel);
                    }
                    queueModel.setSmscModels(smscSelectedModels);
                    WebLogModel webLog = new WebLogModel();
                    webLog.setOperationName("Create Queue");
                    webLog.setStringBefore("");
                    webLog.setStringAfter(queueModel.toString());
                    webLog.setPageName("Queue Management");
                    //get user from session
                    webLog.setUserName(loggedInUser.getUsername());
                    WebLoggerFacade.insertWebLog(webLog);
                    Utility.showInfoMessage(null, Constants.ITEM_ADDED, "Application Queue", queueModel.getAppName());
//                    CommonLogger.businessLogger.info("Queue " + queueModel.getAppName() + " Created Successfully");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "QueueModel Created Successfully")
                            .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueModel.getAppName()).build());
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                    return "AppQueueList.xhtml?faces-redirect=true";
                }
            } catch (CommonException ex) {
                if (ex.getErrorId() == ErrorCodes.QUEUE_TABLE_ALREADY_EXISTS) {
                    Utility.showErrorMessage(null, Constants.UNIQUE_NAME_ERROR, "QUEUE ", queueModel.getAppName());
                    RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                } else {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueue]" + ex);
                    Utility.showErrorMessage(null, ex.getErrorCode(), "in saving Application Queue");
                }
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + QueueBean.class.getName() + ".createApplicationQueue]", e);
                Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            }
        }
        return null;
    }

    public String editApplicationQueue() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getMessageList().size() == 0) {
            try {
                queueFacade.editApplicationQueue(queueModel, (ArrayList<SMSCWebModel>) smscDualList.getTarget(), loggedInUser);
                ArrayList<SMSCModel> smscSelectedModels = new ArrayList<>();
                for (SMSCWebModel smscWebModel : smscDualList.getTarget()) {
                    SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                    smscSelectedModels.add(smscModel);
                }
                queueModel.setSmscModels(smscSelectedModels);
                WebLogModel webLog = new WebLogModel();
                webLog.setOperationName("Edit Queue");
                webLog.setStringBefore(oldQueueModel.toString());
                webLog.setStringAfter(queueModel.toString());
                webLog.setPageName("Queue Management");
                //get user from session
                webLog.setUserName(loggedInUser.getUsername());
                WebLoggerFacade.insertWebLog(webLog);
                Utility.showInfoMessage(null, Constants.ITEM_EDITED, "Application Queue", queueModel.getAppName());
//                CommonLogger.businessLogger.info("Queue " + queueModel.getAppName() + " Edited Successfully");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "QueueModel Edited Successfully")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueModel.getAppName()).build());
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                return "AppQueueList.xhtml?faces-redirect=true";
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editApplicationQueue]" + ex);
                Utility.showErrorMessage(null, ex.getErrorCode(), "in editing Application Queue");
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + QueueBean.class.getName() + ".editApplicationQueue]", e);
                Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            }
        }
        return null;
    }

    public String approveApplicationQueue() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getMessageList().size() == 0) {

            try {
                ArrayList<SMSCModel> smscSelectedModels = new ArrayList<>();
                for (SMSCWebModel smscWebModel : smscDualList.getTarget()) {
                    SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                    smscSelectedModels.add(smscModel);
                }
                queueModel.setSmscModels(smscSelectedModels);
                queueFacade.approveApplicationQueue(queueModel);

                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, "Application Queue", queueModel.getAppName());
//                CommonLogger.businessLogger.info("Queue " + queueModel.getAppName() + " Approved Successfully");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "QueueModel Approved Successfully")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueModel.getAppName()).build());
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                return "OperationsApproval.xhtml?faces-redirect=true";

            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveApplicationQueue]" + ex);
                Utility.showErrorMessage(null, ex.getErrorCode(), "in approving Application Queue");
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + QueueBean.class.getName() + ".approveApplicationQueue]", e);
                Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            }
        }
        return null;
    }

    public String rejectApplicationQueue() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getMessageList().size() == 0) {

            try {
//                ArrayList<SMSCModel> smscSelectedModels = new ArrayList<>();
//                for (SMSCWebModel smscWebModel : smscDualList.getTarget()) {
//                    SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
//                    smscSelectedModels.add(smscModel);
//                }
//                queueModel.setSmscModels(smscSelectedModels);
                queueFacade.rejectApplicationQueue(oldQueueModel);

                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, "Application Queue", queueModel.getAppName());
//                CommonLogger.businessLogger.info("Queue " + queueModel.getAppName() + " Rejected Successfully");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "QueueModel Rejected Successfully")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueModel.getAppName()).build());
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                return "OperationsApproval.xhtml?faces-redirect=true";

            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveApplicationQueue]" + ex);
                Utility.showErrorMessage(null, ex.getErrorCode(), "in approving Application Queue");
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + QueueBean.class.getName() + ".approveApplicationQueue]", e);
                Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            }
        }
        return null;
    }

    /**
     * @return the queueTypeView
     */
    public boolean isQueueTypeView() {
        return queueTypeView;
    }

    /**
     * @param queueTypeView the queueTypeView to set
     */
    public void setQueueTypeView(boolean queueTypeView) {
        this.queueTypeView = queueTypeView;
    }

    /**
     * @return the queueTypeLK
     */
    public HashMap<Integer, LookupModel> getQueueTypeLK() {
        return queueTypeLK;
    }

    /**
     * @param queueTypeLK the queueTypeLK to set
     */
    public void setQueueTypeLK(HashMap<Integer, LookupModel> queueTypeLK) {
        this.queueTypeLK = queueTypeLK;
    }

}
