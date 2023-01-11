/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.facade.QueueFacade;
import com.asset.contactstrategy.facade.SMSCFacade;
import com.asset.contactstrategy.facade.ServiceManagmentFacade;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.QueueWebModel;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import com.asset.contactstrategy.webmodels.ServiceWebModel;
import com.asset.contactstrategy.webmodels.UserWebModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.validation.ValidationException;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.password.Password;
import org.primefaces.component.picklist.PickList;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Amal Magdy
 */
@ManagedBean
@ViewScoped
public class ServiceManagmentBean implements Serializable {

    private ServiceManagmentFacade serviceManagmentFacade;
    private ServiceWebModel serviceWebModel;
    private boolean createMode;
    private boolean editMode;
    private boolean viewMode;
    private boolean approvalMode;
    private List<LookupModel> interfaceTypeList;
    private List<LookupModel> servicePrivileges;
    private List<OriginatorTypeModel> originatorTypes;
    private List<LookupModel> serviceCategoryList;
    private HashMap<Integer, LookupModel> originatorValues;
    private List<LookupModel> serviceTypeList;
    private List<QueueWebModel> queueList;
    private List<QueueWebModel> approvedQueueList;
    private List<QueueWebModel> approvedQueueListSender;
    private List<QueueWebModel> approvedQueueListProcedure;
    private QueueWebModel queueModel;
    private DualListModel<SMSCWebModel> smscDualList;
    private String serviceIP = "";
    private UserWebModel userWebModel;
    private ArrayList<SMSCWebModel> smscListSelected;
    private QueueFacade queueFacade;
    private boolean dialogDisplayed;
    boolean queueValidationFailed = false;
    boolean serviceValidationFailed = false;
    private ServiceModel oldServiceModel;
    private SMSCFacade smscFacade;
    private int originatorMaxLength;
    private long SERVICE_MONITOR_TYPE = Defines.INTERFACES.SYSTEM_TYPE_MONITOR;
    private boolean changePassword;

    @PostConstruct
    public void init() //  public ServiceManagmentBean()
    {
        try {
            CommonLogger.businessLogger.info("Starting ServiceManagmentBean");
            CommonLogger.businessLogger.info("Gathering Data For Bean");
            setOriginatorMaxLength(100);
            UserFacade userFacade = new UserFacade();
            userWebModel = userFacade.convertModelToWebModel((UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            Integer managementMode = (Integer) ec.getRequestMap().get(Constants.MANAGEMENT_MODE);
            if (managementMode != null) {
                if (managementMode.equals(Constants.CREATION_MODE)) {
                    serviceWebModel = new ServiceWebModel();
                    serviceWebModel.setAllowedSMS(1);
                    serviceWebModel.setDailyQuota(1);
                    serviceWebModel.setSelectedServiceTypeID(SERVICE_MONITOR_TYPE);
                    oldServiceModel = (ServiceWebModel) serviceWebModel.clone();
                    setCreateMode(true);
                } else if (managementMode.equals(Constants.EDIT_MODE)) {
                    this.serviceWebModel = (ServiceWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
                    oldServiceModel = (ServiceWebModel) serviceWebModel.clone();
                    setEditMode(true);
                } else if (managementMode.equals(Constants.APPROVAL_MODE)) {
                    this.serviceWebModel = (ServiceWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
                    oldServiceModel = (ServiceWebModel) serviceWebModel.clone();
                    setApprovalMode(true);
                } else if (managementMode.equals(Constants.VIEW_MODE)) {
                    this.serviceWebModel = (ServiceWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
                    setViewMode(true);
                } else {
                    setApprovalMode(true);
                }
            }
            serviceManagmentFacade = new ServiceManagmentFacade();
            queueModel = new QueueWebModel();
            queueFacade = new QueueFacade();
            //loadlookups
            //TODO clone lookups
            interfaceTypeList = SystemLookups.interfaceTypeList;
            originatorTypes = SystemLookups.ORIGINATOR_TYPE_LK;
            servicePrivileges = SystemLookups.SERVICE_PRIVILEGES_LK;
            setOriginatorValues(SystemLookups.ORIGINATOR_VALUES_LK);
            generateServiceCategoryLK();
            generateServiceTypeLK();
            //loadAppQueues
            getApprovedApplicationQueueList();
            getSeparateQueuesLists();

            //////////////////////////////
            ////////////////////////
            // Queue Handling
            smscFacade = new SMSCFacade(new UserModel());
            ArrayList<SMSCWebModel> smscModels = smscFacade.getApprovedSMSCs();

            if (createMode) {
                initQueueModel();
                smscDualList = new DualListModel<>(smscModels, new ArrayList<SMSCWebModel>());
            } else {
                //TODO:load from DB queueModel = (QueueWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
                queueModel = queueFacade.convertModelToWebModel(serviceWebModel.getSelectedApplicationQueueModel());
                smscListSelected = queueFacade.getApplicationQueuesSMSCs(queueModel.getVersionId());
                smscModels.removeAll(smscListSelected);
                smscDualList = new DualListModel<>(smscModels, smscListSelected);
            }
            serviceWebModel.setCreator((int) userWebModel.getId());
            queueModel.setCreator((int) userWebModel.getId());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [ServiceManagmetnBean at init]", ex);
            Utility.showErrorMessage(null, "pageInit.error");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SmsGroupsEditViewBean.class.getName() + ".checkEditParameter]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public void initQueueModel() {
        if (smscDualList != null) {
            smscDualList.getTarget().clear();
        }
        queueModel = new QueueWebModel();
        queueModel.setDequeuePoolSize(1);
        queueModel.setThreshold(1);
        queueModel.setSenderPoolSize(1);
        queueModel.setCreator((int) userWebModel.getId());
    }

    public void requiredFieldsValidator(FacesContext context,
            UIComponent component, Object value) {
        try {
            CommonLogger.businessLogger.info("Started Validating Fields");
            String componentID = component.getId();
            //Queue Creation validation
            if (componentID.equals("app_queue_name") && dialogDisplayed) {

                Spinner dequeuePoolSizeComponent = (Spinner) component.getAttributes().get("dequeuePoolSizeAttr");
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

                String appQueueName = value.toString();
                Integer dequeuePoolSize = (Integer) dequeuePoolSizeComponent.getValue();
                Integer senderPoolSize = (Integer) senderPoolSizeComponent.getValue();
                DualListModel dualListModel = (DualListModel) smscComponent.getValue();
                List items = dualListModel.getTarget();
                Boolean timeWindowFlag = (Boolean) timeWindowFlagComponent.getValue();
                Integer timeWindowFromHr = null;
                Integer timwWindowFromMin = null;
                Integer timeWindowToHr = null;
                Integer timeWindowToMin = null;
                if (timeWindowFlag) {
                    String submittedTimeWindowFromHr = timeWindowFromHrComp.getSubmittedValue().toString();
                    String submittedTimeWindowFromMin = timwWindowFromMinComp.getSubmittedValue().toString();
                    String submittedTimeWindowToHr = timeWindowToHrComp.getSubmittedValue().toString();
                    String submittedTimeWindowToMin = timeWindowToMinComp.getSubmittedValue().toString();
                    if (submittedTimeWindowFromHr != null && !submittedTimeWindowFromHr.equals("")) {
                        timeWindowFromHr = Integer.parseInt(submittedTimeWindowFromHr);
                    } else {
                        timeWindowFromHr = null;
                    }
                    if (submittedTimeWindowFromMin != null && !submittedTimeWindowFromMin.equals("")) {
                        timwWindowFromMin = Integer.parseInt(submittedTimeWindowFromMin);
                    } else {
                        timwWindowFromMin = null;
                    }
                    if (submittedTimeWindowToHr != null && !submittedTimeWindowToHr.equals("")) {
                        timeWindowToHr = Integer.parseInt(submittedTimeWindowToHr);
                    } else {
                        timeWindowToHr = null;
                    }
                    if (submittedTimeWindowToMin != null && !submittedTimeWindowToMin.equals("")) {
                        timeWindowToMin = Integer.parseInt(submittedTimeWindowToMin);
                    } else {
                        timeWindowToMin = null;
                    }
                }
                String databaseURL = (String) databaseURLComp.getSubmittedValue();
                String schemaName = (String) schemaNameComp.getSubmittedValue();
                String schemaPassword = (String) schemaPasswordComp.getSubmittedValue();
                String tableSpaceName = (String) tableSpaceNameComp.getSubmittedValue();

                queueValidationFailed = false;
                if (appQueueName == null || (appQueueName != null && appQueueName.equals(""))) {
                    queueValidationFailed = true;
                } else if (dequeuePoolSize == null || (dequeuePoolSize != null && dequeuePoolSize.equals(0))) {
                    queueValidationFailed = true;
                } else if (senderPoolSize == null || (senderPoolSize != null && senderPoolSize.equals(0))) {
                    queueValidationFailed = true;
                } else if (items == null || (items != null && items.size() == 0)) {
                    queueValidationFailed = true;
                } else if (databaseURL == null || (databaseURL != null && databaseURL.equals(""))) {
                    queueValidationFailed = true;
                } else if (schemaName == null || (schemaName != null && schemaName.equals(""))) {
                    queueValidationFailed = true;
                } else if (schemaPassword == null || (schemaPassword != null && schemaPassword.equals(""))) {
                    queueValidationFailed = true;
                } else if (tableSpaceName == null || (tableSpaceName != null && tableSpaceName.equals(""))) {
                    queueValidationFailed = true;
                } else if (timeWindowFlag != null && timeWindowFlag) {

                    if (timeWindowFromHr == null || (timeWindowFromHr != null && ((timeWindowFromHr < 0) || (timeWindowFromHr > 23)))) {
                        queueValidationFailed = true;
                    } else if (timwWindowFromMin == null || (timwWindowFromMin != null && ((timwWindowFromMin < 0) || (timwWindowFromMin > 59)))) {
                        queueValidationFailed = true;
                    } else if (timeWindowToHr == null || (timeWindowToHr != null && ((timeWindowToHr < 0) || (timeWindowToHr > 23)))) {
                        queueValidationFailed = true;
                    } else if (timeWindowToMin == null || (timeWindowToMin != null && ((timeWindowToMin < 0) || (timeWindowToMin > 59)))) {
                        queueValidationFailed = true;
                    }
                }
                if (queueValidationFailed) {
                    Utility.showErrorMessage("form:errors", Constants.MISSING_DATA);
                }
                if ((databaseURL != null && !databaseURL.equals(""))
                        && (!databaseURL.matches("^jdbc:[a-zA-Z]+:[a-zA-Z]+:@[/][/]((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])):\\d{1,5}[/][a-zA-Z]+$"))) {
                    queueValidationFailed = true;
                    Utility.showErrorMessage("form:errors", Constants.INVALED_URL, "Database");
                }
                if (timeWindowFlag != null && timeWindowFlag) {
                    if ((timeWindowFromHr != null) && (timwWindowFromMin != null) && (timeWindowToHr != null) && (timeWindowToMin != null)
                            && ((timeWindowFromHr > timeWindowToHr) || ((timeWindowFromHr == timeWindowToHr) && (timwWindowFromMin >= timeWindowToMin)))) {
                        queueValidationFailed = true;
                        Utility.showErrorMessage("form:errors", Constants.INVALID_TIME);
                    }
                }

            }//Service Creation validation
            else if (componentID.equals("system_name") && !dialogDisplayed) {
                Spinner dailyQuotaComp = (Spinner) component.getAttributes().get("dailyQuotaAttr");
                Spinner maxConcatSMSComp = (Spinner) component.getAttributes().get("maxConcatSMSAttr");
                SelectOneMenu interfaceComp = (SelectOneMenu) component.getAttributes().get("interfaceAttr");
                SelectOneMenu typeComp = (SelectOneMenu) component.getAttributes().get("typeAttr");
                Password passwordComp = (Password) component.getAttributes().get("passwordAttr");
                Password confirmPasswordComp = (Password) component.getAttributes().get("confirmPasswordAttr");

                Integer dailyQuotaValue = (Integer) dailyQuotaComp.getValue();
                Integer maxConcatSMSValue = (Integer) maxConcatSMSComp.getValue();
                Integer chosenInterfaceId = Integer.parseInt((String) interfaceComp.getSubmittedValue());
                String systemName = (String) value;
                String password = (String) passwordComp.getSubmittedValue();
                String confirmPassword = (String) confirmPasswordComp.getSubmittedValue();

                LookupModel interfaceValue = null;
                for (LookupModel model : interfaceTypeList) {
                    if (model.getId() == chosenInterfaceId) {
                        int index = interfaceTypeList.indexOf(model);
                        interfaceValue = interfaceTypeList.get(index);
                        break;
                    }
                }
                Integer chosenTypeId = Integer.parseInt((String) typeComp.getSubmittedValue());
                LookupModel typeValue = null;
                for (LookupModel model : serviceTypeList) {
                    if (model.getId() == chosenTypeId) {
                        int index = serviceTypeList.indexOf(model);
                        typeValue = serviceTypeList.get(index);
                        break;
                    }
                }

                boolean serviceValidationFailed = false;
                if (dailyQuotaValue == null || (dailyQuotaValue != null && dailyQuotaValue.equals(0))) {
                    serviceValidationFailed = true;
                } else if (maxConcatSMSValue == null || (maxConcatSMSValue != null && maxConcatSMSValue.equals(0))) {
                    serviceValidationFailed = true;
                } else if (typeValue == null || (typeValue != null && typeValue.equals(new LookupModel()))) {
                    serviceValidationFailed = true;
                } else if (interfaceValue == null || (interfaceValue != null && interfaceValue.equals(new LookupModel()))) {
                    serviceValidationFailed = true;
                } else if (queueModel == null || (queueModel != null && queueModel.equals(new QueueWebModel()))) {
                    serviceValidationFailed = true;
                } else if (systemName == null || (systemName != null && systemName.equals(""))) {
                    serviceValidationFailed = true;
                } else if ((password == null || (password != null && password.equals(""))) && (isCreateMode() || (isEditMode() && changePassword))) {
                    serviceValidationFailed = true;
                } else if ((confirmPassword == null || (confirmPassword != null && confirmPassword.equals(""))) && (isCreateMode() || (isEditMode() && changePassword))) {
                    serviceValidationFailed = true;
                }
                if (serviceValidationFailed) {
                    Utility.showErrorMessage(null, Constants.MISSING_DATA);
                }
            }
            if (serviceWebModel.isHasWhiteList() && serviceWebModel.getWhiteListIPs().isEmpty() && !componentID.equals("app_queue_name")) {
                Utility.showErrorMessage(null, Constants.EMPTY_WHITE_LIST);
            }

            if (editMode && (queueModel == null || (queueModel != null && queueModel.equals(new QueueWebModel())))) {
                Utility.showErrorMessage(null, Constants.MISSING_DATA);
            }
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SmsGroupsEditViewBean.class.getName() + ".requiredFieldsValidator]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    //TODO Approved queues onlllly or not ??
    public void getApprovedApplicationQueueList() {
        try {
            queueList = queueFacade.getApplicationQueues();
            approvedQueueList = new ArrayList<>();
            if (queueList != null) {
                for (QueueWebModel queueWebModel : queueList) {
                    if (queueWebModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                        approvedQueueList.add(queueWebModel);
                    }
                }
            } else {
                queueList = new ArrayList();
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueueList]", ex);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueueList]", e);

        }
    }

    public void displayDialog() {
        if (serviceWebModel.isAutoCreatdFlag()) {
            if (createMode) {
                if (queueModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    initQueueModel();
                }
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("form:dialog");
                context.update("form:selectedQueuePanel");
                context.update("form:auto_create_flag");
                context.execute("PF('dlg1').show();");
                dialogDisplayed = true;
            }
        } else {
            handleQueueModel();
            dialogDisplayed = false;
        }
    }

    /**
     * checkQueueName returns false in case of 1-Auto Flag in service is false
     * 2-Queue Name Doesn't exist if the service auto flag is true returns true
     * if Auto Flag in service is true and the queue name exists
     */
    private Boolean checkQueueName() throws CommonException {
        Boolean queueExists = false;
        if (serviceWebModel.isAutoCreatdFlag()) {
            try {
                queueExists = queueFacade.checkApplicationQueueNameExists(queueModel.getAppName().trim());
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [checkQueueName]" + ex);
                throw ex;
            }
        }
        return queueExists;
    }

    private Boolean checkServiceName() throws CommonException {
        boolean serviceExists = false;
        try {
            serviceExists = serviceManagmentFacade.checkServiceName(serviceWebModel.getServiceName().trim());
            return serviceExists;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [checkServiceName]" + ex);
            throw ex;
        }
    }

    public String createService() {
        CommonLogger.businessLogger.info("Starting CreateService");
        try {
            if (!serviceValidationFailed) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                if (facesContext.getMessageList().size() == 0) {
                    if (!validateOriginatorValue()) {
                        return null;
                    }

                    // CR | eslam.ahmed | check confirm password
                    if (!serviceWebModel.getPassword().equals(serviceWebModel.getPasswordConfirm())) {
                        Utility.showErrorMessage(null, Constants.CONFIRM_PASSWORD);
                        RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                        return null;
                    }

                    WebLogModel webLog = new WebLogModel();
                    //Update Unique service name and queue if it is auto created
                    Boolean nameServiceExist = checkServiceName();
                    Boolean nameQueueExist = checkQueueName();
                    if (nameServiceExist && nameQueueExist) {
                        Utility.showErrorMessage(null, Constants.UNIQUE_NAME_ERROR, "SERVICE", serviceWebModel.getServiceName());
                        Utility.showErrorMessage(null, Constants.UNIQUE_NAME_ERROR, "QUEUE ", queueModel.getAppName());
                        RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                        return null;
                    } else if (nameServiceExist) {
                        Utility.showErrorMessage(null, Constants.UNIQUE_NAME_ERROR, "SERVICE", serviceWebModel.getServiceName());
                        RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                        return null;
                    } else if (nameQueueExist) {
                        Utility.showErrorMessage(null, Constants.UNIQUE_NAME_ERROR, "QUEUE ", queueModel.getAppName());
                        RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                        return null;
                    } else if (!nameServiceExist && !nameQueueExist) {
                        serviceManagmentFacade.createService(queueModel, (ArrayList<SMSCWebModel>) smscDualList.getTarget(), serviceWebModel, serviceWebModel.getWhiteListIPs(), userWebModel);
//                        CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Created Successfully");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service created Successfully")
                                .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
                        Utility.showInfoMessage(null, Constants.ITEM_ADDED, "SERVICE", serviceWebModel.getServiceName());
                        if (serviceWebModel.isAutoCreatdFlag()) {
                            Utility.showInfoMessage(null, Constants.ITEM_ADDED, "QUEUE", queueModel.getQueueName());
                        }
                        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                    }
                    webLog.setOperationName("Create Service");
                    webLog.setStringBefore(null);
                    webLog.setStringAfter(serviceWebModel.toString());
                    webLog.setPageName("Service Management");
                    //get user from session
                    webLog.setUserName(userWebModel.getUsername());
                    WebLoggerFacade.insertWebLog(webLog);
                    return "ServiceList.xhtml?faces-redirect=true";
                }
            }
            return null;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  createService" + ex);
            if (ex.getErrorCode() == ErrorCodes.TABLESPACE_NOT_EXIST) {
                Utility.showErrorMessage(null, ErrorCodes.TABLESPACE_NOT_EXIST);
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            } else if (ex.getErrorId() == ErrorCodes.QUEUE_TABLE_ALREADY_EXISTS) {
                Utility.showErrorMessage(null, Constants.UNIQUE_NAME_ERROR, "QUEUE ", queueModel.getAppName());
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            } else if (ex.getErrorCode() == ErrorCodes.GET_CONNECTION_ERROR) {
                Utility.showErrorMessage(null, ErrorCodes.GET_CONNECTION_ERROR, ex.getMessage());
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            } else {
                Utility.showErrorMessage(null, "general.error", ex.getMessage());
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ServiceManagmentBean.class.getName() + ".createService]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
        }
        return null;
    }

    public String editService() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext.getMessageList().size() == 0) {
                WebLogModel webLog = new WebLogModel();
                if (!validateOriginatorValue()) {
                    return null;
                }

                // CR | eslam.ahmed | check confirm password
                if (changePassword
                        && (serviceWebModel.getPassword() == null
                        || serviceWebModel.getPasswordConfirm() == null
                        || !serviceWebModel.getPassword().equals(serviceWebModel.getPasswordConfirm()))) {
                    Utility.showErrorMessage(null, Constants.CONFIRM_PASSWORD);
                    RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                    return null;
                }

                if (!changePassword) {
                    serviceWebModel.setPassword(null);
                }

                serviceManagmentFacade.editService(queueModel, (ArrayList<SMSCWebModel>) smscDualList.getTarget(), serviceWebModel, serviceWebModel.getWhiteListIPs(), userWebModel);
//                CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Edited Successfully");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Edited Successfully")
                        .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
                Utility.showInfoMessage(null, Constants.ITEM_EDITED, "SERVICE", serviceWebModel.getServiceName());
                if (serviceWebModel.isAutoCreatdFlag()) {
                    Utility.showInfoMessage(null, Constants.ITEM_ADDED, "QUEUE", queueModel.getQueueName());
                }
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                webLog.setOperationName("Update Service");
                webLog.setStringBefore(oldServiceModel.toString());
                webLog.setStringAfter(serviceWebModel.toString());
                webLog.setPageName("Service Management");
                //get user from session
                webLog.setUserName(userWebModel.getUsername());
                WebLoggerFacade.insertWebLog(webLog);
                return "ServiceList.xhtml?faces-redirect=true";
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  editService" + ex);
            Utility.showErrorMessage(null, "general.error", ex.getMessage());
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ServiceManagmentBean.class.getName() + ".editService]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
        }
        return null;
    }

    public void createQueue() {

        if (!queueValidationFailed) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg1').hide();");
            context.update("form:selectedQueuePanel");
            dialogDisplayed = false;
        }
    }

    public void getSeparateQueuesLists() {
        approvedQueueListSender = new ArrayList<>();
        approvedQueueListProcedure = new ArrayList<>();
        for (QueueWebModel queueModel : approvedQueueList) {
            if (queueModel.getQueueType() == 1) {
                approvedQueueListSender.add(queueModel);
            } else if (queueModel.getQueueType() == 2) {
                approvedQueueListProcedure.add(queueModel);
            }
        }
    }

    public void cancelCreateQueue() {
        //serviceWebModel.setAutoCreatdFlag(false);
        handleQueueModel();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg1').hide();");
        context.update("form:selectedQueuePanel");
        context.update("form:auto_create_flag");
        context.update("form:dialog");
        dialogDisplayed = false;
    }

    public boolean validateOriginatorValue() {
//        boolean validate = true;
//        String value;
        if ((serviceWebModel.getServicePrivilege() != 1)) {
//            for (OriginatorTypeModel originator : originatorTypes) {
//                if (serviceWebModel.getOriginatorType() == originator.getOriginatorType()) {
//                    originatorMaxLength = originator.getAllowedLength();
//                    value = originatorValues.get(serviceWebModel.getOriginatorValue()).getLable();
//                    validate = value.length() <= originatorMaxLength;
//                    if (validate) {
//                        break;
//                    } else {
//                        Utility.showErrorMessage(null, Constants.VALIDATE_ORIGINATOR_LENGTH);
//                        break;
//                    }
//                }
//            }
            if (!com.asset.contactstrategy.common.utils.Utility.checkOrignatorType(originatorValues.get(serviceWebModel.getOriginatorValue()).getLable(), serviceWebModel.getOriginatorType())) {
                Utility.showErrorMessage(null, Constants.VALIDATE_ORIGINATOR_LENGTH);
                return false;
            }
        }
        return true;
    }

    public boolean validateOriginatorType() {
        if ((serviceWebModel.getServicePrivilege() != 1)) {

            if (!com.asset.contactstrategy.common.utils.Utility.checkOrignatorType(originatorValues.get(serviceWebModel.getOriginatorValue()).getLable(), serviceWebModel.getOriginatorType())) {
                Utility.showErrorMessage(null, Constants.VALIDATE_ORIGINATOR_LENGTH);
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                return false;
            }
        }
        return true;
    }

    public void addToWhiteList() throws ValidatorException {
        if (serviceIP != null && !serviceIP.trim().equalsIgnoreCase("")) {
            if (!serviceIP.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")) {
                Utility.showErrorMessage(null, Constants.INVALID_IP);
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                return;
            }
            serviceWebModel.getWhiteListIPs().add(serviceIP);
//            CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Added IpAddress " + serviceIP + " Successfully");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Added IPAddress Successfully")
                    .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName())
                    .put(GeneralConstants.StructuredLogKeys.IP_ADDRRESS, serviceIP).build());
            serviceIP = "";
        }
    }

    public void removeFromWhiteList(String ip) {
        serviceWebModel.getWhiteListIPs().remove(ip);
//        CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Removed IpAddress " + ip + " Successfully");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Removed IPAddress Successfully")
                .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName())
                .put(GeneralConstants.StructuredLogKeys.IP_ADDRRESS, serviceIP).build());
    }

    public void handleQueueModel() {
        try {
            Long id = serviceWebModel.getSelectedApplicationQueueID();
            queueModel = null;
            for (QueueWebModel queue : approvedQueueList) {
                if (id == queue.getVersionId()) {
                    queueModel = queue;
                    break;
                }
            }
            if (queueModel == null) {
                initQueueModel();
            }
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("form:dialog");
            context.update("form:selectedQueuePanel");

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SmsGroupsEditViewBean.class
                    .getName() + ".requiredFieldsValidator]", e);
            Utility.showErrorMessage(
                    null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public void serviceRequiredFieldsValidator(FacesContext context,
            UIComponent component, Object value) throws ValidationException {

        String componentID = component.getId();
        if (componentID.equals("system_name") && !dialogDisplayed) {
            if (serviceWebModel.getServiceName() == null || serviceWebModel.getServiceName().trim().equals("")) {
                Utility.showErrorMessage(null, Constants.MISSING_DATA, " Enter Service Name");
            }
            if (serviceWebModel.getDailyQuota() <= 0) {
                Utility.showErrorMessage(null, Constants.INVALID_DAILY_QUOTA, " Daily Quota value cannot be 0");
            }

            if (serviceWebModel.getAllowedSMS() <= 0 || serviceWebModel.getAllowedSMS() >= 10) {
                Utility.showErrorMessage(null, Constants.INVALID_ALLOWED_SMS, " No of allowed sms from 1 to 9");
            }

            if ((queueModel == null) || ((queueModel != null) && (queueModel.equals(new QueueWebModel())))) {
                Utility.showErrorMessage(null, Constants.MISSING_DATA, " Choose or create a queue.");
            }

        }
        RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
    }

    public String approveService() {
        try {

            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext.getMessageList().size() == 0) {
                WebLogModel webLog = new WebLogModel();
                serviceManagmentFacade.approveService(queueModel, (ArrayList<SMSCWebModel>) smscDualList.getTarget(), this.serviceWebModel, this.serviceWebModel.getWhiteListIPs(), userWebModel);

                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, "SERVICE", this.serviceWebModel.getServiceName());
                if (this.serviceWebModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                    webLog.setOperationName("Approve Service Deletion");
                    webLog.setStringBefore(this.serviceWebModel.toString());
                    webLog.setStringAfter(null);
//                    CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Approved Deletion Successfully");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Approved Deletion Successfully")
                            .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
                } else if (this.serviceWebModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE && serviceWebModel.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_CREATION)) {
                    Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, serviceWebModel.getServiceName(), "creation");
                    webLog.setOperationName("Approve Service creation");
                    webLog.setStringBefore(null);
                    webLog.setStringAfter(serviceWebModel.toString());
//                    CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Approved Creation Successfully");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Approved Creation Successfully")
                            .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
                } else if (this.serviceWebModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE && serviceWebModel.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_EDIT)) {
                    webLog.setOperationName("Approve Pending Service");
                    webLog.setStringBefore(serviceManagmentFacade.getParentServiceVersion(this.serviceWebModel.getServiceID()).toString());
                    webLog.setStringAfter(this.serviceWebModel.toString());

//                    CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Approved Pending Successfully");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Approved  Pending Successfully")
                            .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
                }
                //get user from session
                webLog.setPageName("opertions Approval");
                webLog.setUserName(userWebModel.getUsername());
                WebLoggerFacade.insertWebLog(webLog);
                return "OperationsApproval.xhtml?faces-redirect=true";
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  approveService" + ex);
            Utility.showErrorMessage(null, "general.error", ex.getMessage());

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ServiceManagmentBean.class
                    .getName() + ".approveService]", e);
            Utility.showErrorMessage(
                    null, ErrorCodes.GENERAL_ERROR, " ");
        }
        return null;
    }

    public String rejectService() {
        try {
            WebLogModel webLog = new WebLogModel();
//            if (serviceWebModelForRejection == null) {
//
//                serviceWebModel = (ServiceWebModel) oldServiceModel;
//            } else {

//                userWebModel = new UserWebModel();
//                //userWebModel.setUserType(GeneralConstants.USER_TYPE_BUSINESS_VALUE);
//                userWebModel.setUserType(GeneralConstants.USER_TYPE_OPERATIONAL_VALUE);
//                serviceManagmentFacade = new ServiceManagmentFacade();
//                queueFacade = new QueueFacade();
//                smscFacade = new SMSCFacade(userWebModel);
//                this.serviceWebModel = serviceWebModelForRejection;
//
//            //}
//            ArrayList<SMSCWebModel> smscModels = smscFacade.getApprovedSMSCs();
//            queueModel = queueFacade.convertModelToWebModel(serviceWebModel.getSelectedApplicationQueueModel());
//            smscListSelected = queueFacade.getApplicationQueuesSMSCs(queueModel.getId());
//            smscModels.removeAll(smscListSelected);
//            smscDualList = new DualListModel<>(smscModels, smscListSelected);
            serviceWebModel = (ServiceWebModel) oldServiceModel;
            serviceManagmentFacade.rejectService(queueModel, (ArrayList<SMSCWebModel>) smscDualList.getTarget(), this.serviceWebModel, this.serviceWebModel.getWhiteListIPs(), userWebModel);

            Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, "SERVICE", this.serviceWebModel.getServiceName());
            if (this.serviceWebModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE && serviceWebModel.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_EDIT)) {
                webLog.setOperationName("Reject Pending Service");
                webLog.setStringBefore(this.serviceWebModel.toString());
                webLog.setStringAfter(null);
//                CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Rejected Pending Successfully");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Rejected Prending Successfully")
                        .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
            } else if (this.serviceWebModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE && serviceWebModel.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_CREATION)) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, serviceWebModel.getServiceName(), "creation");
                webLog.setOperationName("Reject Service creation");
                webLog.setStringBefore(oldServiceModel.toString());
                webLog.setStringAfter(null);
//                CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Rejected creation Successfully");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Rejected Creation Successfully")
                        .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
            } else if (this.serviceWebModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                webLog.setOperationName("Reject Service Deletion");
//                webLog.setStringBefore(this.serviceWebModel.toString());
//                serviceWebModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
//                webLog.setStringAfter(this.serviceWebModel.toString());
                webLog.setStringAfter(this.oldServiceModel.toString());
                oldServiceModel.setStatus(GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE);
                webLog.setStringAfter(this.oldServiceModel.toString());

                CommonLogger.businessLogger.info("Service " + serviceWebModel.getServiceName() + " Rejected Deletion Successfully");
                 CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service Rejected Deletion Successfully")
                    .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
            }
            //get user from session
            webLog.setPageName("opertions Approval");
            webLog.setUserName(userWebModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            return "OperationsApproval.xhtml?faces-redirect=true";
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  rejectService" + ex);
            Utility.showErrorMessage(null, "general.error", ex.getMessage());

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ServiceManagmentBean.class
                    .getName() + ".rejectService]", e);
            Utility.showErrorMessage(
                    null, ErrorCodes.GENERAL_ERROR, " ");
        }
        return null;
    }

    public void generateServiceCategoryLK() {
        serviceCategoryList = new ArrayList<LookupModel>();
        LookupModel lookupNormal = new LookupModel();
        lookupNormal.setLable("normal");
        lookupNormal.setId(Defines.INTERFACES.SYSTEM_CATEGORY_NORMAL);
        lookupNormal.setDescription(Defines.INTERFACES.SYSTEM_CATEGORY_NORMAL + "");
        LookupModel lookupHigh = new LookupModel();
        lookupHigh.setLable("high");
        lookupHigh.setDescription(Defines.INTERFACES.SYSTEM_CATEGORY_HIGH + "");
        lookupHigh.setId(Defines.INTERFACES.SYSTEM_CATEGORY_HIGH);
        serviceCategoryList.add(lookupNormal);
        serviceCategoryList.add(lookupHigh);
    }

    public void generateServiceTypeLK() {
        serviceTypeList = new ArrayList<LookupModel>();
        LookupModel lookupMonitor = new LookupModel();
        lookupMonitor.setLable("monitor");
        lookupMonitor.setDescription(Defines.INTERFACES.SYSTEM_TYPE_MONITOR + "");
        lookupMonitor.setId(Defines.INTERFACES.SYSTEM_TYPE_MONITOR);
        LookupModel lookupControl = new LookupModel();
        lookupControl.setLable("control");
        lookupControl.setDescription(Defines.INTERFACES.SYSTEM_TYPE_CONTROL + "");
        lookupControl.setId(Defines.INTERFACES.SYSTEM_TYPE_CONTROL);
        serviceTypeList.add(lookupMonitor);
        serviceTypeList.add(lookupControl);
    }

    public ServiceWebModel getServiceWebModel() {
        return serviceWebModel;
    }

    public void setServiceWebModel(ServiceWebModel serviceWebModel) {
        this.serviceWebModel = serviceWebModel;
    }

    public boolean isCreateMode() {
        return createMode;
    }

    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isViewMode() {
        return viewMode;
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;
    }

    public boolean isApprovalMode() {
        return approvalMode;
    }

    public void setApprovalMode(boolean approvalMode) {
        this.approvalMode = approvalMode;
    }

    public List<LookupModel> getInterfaceTypeList() {
        return interfaceTypeList;
    }

    public void setInterfaceTypeList(List<LookupModel> interfaceTypeList) {
        this.interfaceTypeList = interfaceTypeList;
    }

    public List<LookupModel> getServiceCategoryList() {
        return serviceCategoryList;
    }

    public void setServiceCategoryList(List<LookupModel> serviceCategoryList) {
        this.serviceCategoryList = serviceCategoryList;
    }

    public List<LookupModel> getServiceTypeList() {
        return serviceTypeList;
    }

    public void setServiceTypeList(List<LookupModel> serviceTypeList) {
        this.serviceTypeList = serviceTypeList;
    }

    public List<QueueWebModel> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<QueueWebModel> queueList) {
        this.queueList = queueList;
    }

    public QueueWebModel getQueueModel() {
        return queueModel;
    }

    public void setQueueModel(QueueWebModel queueModel) {
        this.queueModel = queueModel;
    }

    public DualListModel<SMSCWebModel> getSmscDualList() {
        return smscDualList;
    }

    public void setSmscDualList(DualListModel<SMSCWebModel> smscDualList) {
        this.smscDualList = smscDualList;
    }

    public String getServiceIP() {
        return serviceIP;
    }

    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }

    public UserWebModel getUserWebModel() {
        return userWebModel;
    }

    public void setUserWebModel(UserWebModel userWebModel) {
        this.userWebModel = userWebModel;
    }

    public ArrayList<SMSCWebModel> getSmscListSelected() {
        return smscListSelected;
    }

    public void setSmscListSelected(ArrayList<SMSCWebModel> smscListSelected) {
        this.smscListSelected = smscListSelected;
    }

    public List<QueueWebModel> getApprovedQueueList() {
        return approvedQueueList;
    }

    public void setApprovedQueueList(List<QueueWebModel> approvedQueueList) {
        this.approvedQueueList = approvedQueueList;
    }

    public long getSERVICE_MONITOR_TYPE() {
        return SERVICE_MONITOR_TYPE;
    }

    public void setSERVICE_MONITOR_TYPE(long SERVICE_MONITOR_TYPE) {
        this.SERVICE_MONITOR_TYPE = SERVICE_MONITOR_TYPE;
    }

    /**
     * @return the servicePrivileges
     */
    public List<LookupModel> getServicePrivileges() {
        return servicePrivileges;
    }

    /**
     * @param servicePrivileges the servicePrivileges to set
     */
    public void setServicePrivileges(List<LookupModel> servicePrivileges) {
        this.servicePrivileges = servicePrivileges;
    }

    /**
     * @return the originatorValues
     */
    public HashMap<Integer, LookupModel> getOriginatorValues() {
        return originatorValues;
    }

    /**
     * @param originatorValues the originatorValues to set
     */
    public void setOriginatorValues(HashMap<Integer, LookupModel> originatorValues) {
        this.originatorValues = originatorValues;
    }

    /**
     * @return the originatorTypes
     */
    public List<OriginatorTypeModel> getOriginatorTypes() {
        return originatorTypes;
    }

    /**
     * @param originatorTypes the originatorTypes to set
     */
    public void setOriginatorTypes(List<OriginatorTypeModel> originatorTypes) {
        this.originatorTypes = originatorTypes;
    }

    /**
     * @return the originatorMaxLength
     */
    public int getOriginatorMaxLength() {
        return originatorMaxLength;
    }

    /**
     * @param originatorMaxLength the originatorMaxLength to set
     */
    public void setOriginatorMaxLength(int originatorMaxLength) {
        this.originatorMaxLength = originatorMaxLength;
    }

    /**
     * @return the approvedQueueListSender
     */
    public List<QueueWebModel> getApprovedQueueListSender() {
        return approvedQueueListSender;
    }

    /**
     * @param approvedQueueListSender the approvedQueueListSender to set
     */
    public void setApprovedQueueListSender(List<QueueWebModel> approvedQueueListSender) {
        this.approvedQueueListSender = approvedQueueListSender;
    }

    /**
     * @return the approvedQueueListProcedure
     */
    public List<QueueWebModel> getApprovedQueueListProcedure() {
        return approvedQueueListProcedure;
    }

    /**
     * @param approvedQueueListProcedure the approvedQueueListProcedure to set
     */
    public void setApprovedQueueListProcedure(List<QueueWebModel> approvedQueueListProcedure) {
        this.approvedQueueListProcedure = approvedQueueListProcedure;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

}
