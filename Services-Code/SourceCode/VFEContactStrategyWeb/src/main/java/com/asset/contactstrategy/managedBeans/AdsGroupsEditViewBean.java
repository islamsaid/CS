/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHFilterValueModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.facade.DWHElementFacade;
import com.asset.contactstrategy.facade.LookupFacade;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.facade.AdsGroupFacade;
import com.asset.contactstrategy.facade.QueueFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.webmodels.AdsGroupWebModel;
import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.Serializable;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import org.apache.log4j.spi.ErrorCode;
import org.primefaces.component.selectoneradio.SelectOneRadio;

/**
 *
 * @author kerollos.asaad
 */
@ManagedBean(name = "adsGroupsEditViewBean")
@ViewScoped
public class AdsGroupsEditViewBean implements Serializable {

    private static final String BEAN_NAME = "AdsGroupsEditViewBean";
    private static final long serialVersionUID = 1L;
    private AdsGroupWebModel selectedGroup = new AdsGroupWebModel();
    private ArrayList<DWHElementModel> dwhElements = new ArrayList<DWHElementModel>();
    private ArrayList<DWHElementModel> attrValues = new ArrayList<DWHElementModel>();
    private ArrayList<LookupModel> groupTypesList = new ArrayList<LookupModel>();
    private ArrayList<LookupModel> operators = new ArrayList<LookupModel>();
    private FilterModel newFilter = new FilterModel();
    private AdsGroupFacade groupFacade = new AdsGroupFacade();
    private UserModel loggedInUser = new UserModel();
    private boolean donotContactFlag = false;
    private boolean editable = false;
    private boolean approvable = false;
    private boolean creation = false;
    private boolean editFlag = false;
    private boolean viewFlag = false;
    private boolean dateAttributeVisible = false;
    private boolean stringAttributeVisible = false;
    private boolean numberAttributeVisible = false;
    private boolean multiSelectAttributeVisible = false;
    private String query = new String();
    private Date startDate;
    private Date endDate;
    private Date currentDate = new Date();
    private Date minEndDate;
    private AdsGroupWebModel oldAdsGroupWebModel;
    @ManagedProperty(value = "#{uploadAndParseFileBean}")
    private UploadAndParseFileBean uploadAndParseFileBean;

    @PostConstruct
    public void init() {
        String methodName = "init";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        try {
            loggedInUser = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE);

            startDate = new Date();
            endDate = new Date();
            checkEditParameter();
            fillAttrValues();
            fillOperators();
            fillGroupTypes();
            donotContactFlag = (selectedGroup.getDonotContact() == GeneralConstants.TRUE);
            editFlag = (creation || editable || approvable);
            if (creation) {
                selectedGroup.setDailyThreshold(1);
            }
            if (!editFlag && !viewFlag) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("AdsGroupsList.xhtml");
                return;
            }

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsEditViewBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());

    }

    public void checkEditParameter() {
        String methodName = "checkEditParameter";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        try {
            if (flash != null) {
                if (flash.containsKey("SelectedParameter")) {
                    this.selectedGroup = (AdsGroupWebModel) flash.get("SelectedParameter");
                    if (this.selectedGroup.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                        this.selectedGroup.setFilterList(this.groupFacade.retrieveAdsGroupFilters(this.selectedGroup.getVersionId()));
                    } else {
                        this.selectedGroup.setFilesModel(this.groupFacade.retrieveAdsGroupFiles(this.selectedGroup.getVersionId()));
                        if (uploadAndParseFileBean != null) {
                            uploadAndParseFileBean.setGroupFiles(new ArrayList<FileModel>());
                            uploadAndParseFileBean.setGroupFiles(this.selectedGroup.getFilesModel());
                        }
                    }
                    oldAdsGroupWebModel = new AdsGroupWebModel(selectedGroup);
                    if (flash.containsKey("editable")) {
                        editable = (boolean) flash.get("editable");
                    }
                    if (flash.containsKey("approvable")) {
                        approvable = (boolean) flash.get("approvable");
                    }
                    if (flash.containsKey("isView")) {
                        viewFlag = (boolean) flash.get("isView");
                    }
                }
                if (flash.containsKey("creation")) {
                    creation = (boolean) flash.get("creation");
                    selectedGroup.setGroupPriority(1);
                    cleanUploadedFiles();
                }
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsEditViewBean.class.getName() + "." + methodName + "]", e);
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

    public void cleanUploadedFiles() {
        String methodName = "cleanUploadedFiles";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        try {
            UploadAndParseFileBean uploadAndParseFileBean = null;
            uploadAndParseFileBean = (UploadAndParseFileBean) Utility.getSessionVarFromContext("uploadAndParseFileBean");
            if (uploadAndParseFileBean != null) {
                uploadAndParseFileBean.setGroupFiles(new ArrayList<FileModel>());
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsEditViewBean.class.getName() + "." + methodName + "]", e);
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

    public void fillGroupTypes() {
        String methodName = "fillGroupTypes";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        Iterator it = SystemLookups.GROUP_TYPES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            groupTypesList.add((LookupModel) pair.getValue());
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void editGroup() throws CommonException {
        String methodName = "editGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        WebLogModel webLog = new WebLogModel();
        webLog.setOperationName("Edit Ads Group");
        webLog.setPageName("AdsGroupEditView");
        webLog.setUserName(loggedInUser.getUsername());
        Date stepStartDate = new Date();
        webLog.setStringBefore(oldAdsGroupWebModel.toString());
        groupFacade.editAdsGroup(selectedGroup, loggedInUser.getUserType());
        webLog.setStringAfter(selectedGroup.toString());
        WebLoggerFacade.insertWebLog(webLog);
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void loadAllCommercialElements() {
        String methodName = "loadAllCommercialElements";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        try {
            dwhElements = groupFacade.loadAllCommercialElements();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsEditViewBean.class.getName() + "." + methodName + "]", e);
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

    private void fillAttrValues() {
        String methodName = "fillAttrValues";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        try {
            DWHElementFacade def = new DWHElementFacade();
            attrValues.addAll(def.loadAllElementsFromDb());
            if (attrValues != null && attrValues.size() > 0) {
                getNewFilter().setDwhElementModel(attrValues.get(0));
            }
            ArrayList<DWHElementValueModel> governments = def.loadGovernmentAttributes();
            ArrayList<DWHElementValueModel> customerTypes = def.loadCustomersAttributes();
            for (DWHElementModel element : attrValues) {
                if (element.getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID)) {
                    int ind = attrValues.indexOf(element);
                    attrValues.get(ind).getMultiSelectionValues().clear();
                    attrValues.get(ind).getMultiSelectionValues().addAll(governments);

                }
                if (element.getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE)) {
                    int ind = attrValues.indexOf(element);
                    attrValues.get(ind).getMultiSelectionValues().clear();
                    attrValues.get(ind).getMultiSelectionValues().addAll(customerTypes);

                }
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsEditViewBean.class.getName() + "." + methodName + "]", e);
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

    public void addAttribute() {
        String methodName = "addAttribute";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        FacesMessage curMessage = null;
        try {
            boolean emptyValue = false;
            if (this.newFilter.getDwhElementModel().getDisplayTypeId() == Defines.DWHELEMENT_DISPLAY_TYPES.DATE) {
                Format format = new SimpleDateFormat("yyyy MM dd");
                newFilter.setFirstOperand(format.format(this.getStartDate()));
                newFilter.setSecondOperand(format.format(this.getEndDate()));
            } else if (this.newFilter.getDwhElementModel().getDisplayTypeId() == Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION) {
                DWHElementFacade dwhf = new DWHElementFacade();
                ArrayList<DWHFilterValueModel> selectedFilterValues = dwhf.convertElementValuesToFilterValues(newFilter.getSelectedElementValues());
                try {
                    if (selectedFilterValues.size() > 0) {
                        newFilter.setFilterValues(selectedFilterValues);
                    } else {
                        curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Value must be selected.", null);
                        FacesContext.getCurrentInstance().addMessage(null, curMessage);
                        emptyValue = true;
                    }
                } catch (Exception e) {
                    curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Value must be selected " + e.getMessage(), null);
                    FacesContext.getCurrentInstance().addMessage(null, curMessage);
                    emptyValue = true;
                }
            } else {
                try {
                    if (this.getQuery().length() > 0) {
                        newFilter.setFirstOperand(this.getQuery());
                    } else {
                        curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Value Can't be Empty.", null);
                        FacesContext.getCurrentInstance().addMessage(null, curMessage);
                        emptyValue = true;
                    }
                } catch (Exception e) {
                    curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Value can't be empty " + e.getMessage(), null);
                    FacesContext.getCurrentInstance().addMessage(null, curMessage);
                    emptyValue = true;
                }
            }
            if (this.selectedGroup.getFilterList().contains(newFilter)) {
                curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "This filter has been selected before.", null);
                FacesContext.getCurrentInstance().addMessage(null, curMessage);
                emptyValue = true;
            }
            if (!emptyValue) {
                this.selectedGroup.getFilterList().add(newFilter);
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        } finally {
            this.newFilter = new FilterModel();
            if (this.attrValues != null && this.attrValues.size() > 0) {
                this.newFilter.setDwhElementModel(this.attrValues.get(0));
            }
            this.startDate = new Date();
            this.endDate = new Date();
            this.query = new String();
            this.fillOperators();
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void fillOperators() {
        String methodName = "fillOperators";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        try {
            LookupFacade lookupFacadeObject = new LookupFacade();
            operators = lookupFacadeObject.getDisplayTypeOperators(this.getNewFilter().getDwhElementModel().getDisplayTypeId());
            if (operators.size() > 0) {
                getNewFilter().setOperatorModel(operators.get(0));
            }
            calculateAttributeVisibilityControls(this.getNewFilter().getDwhElementModel().getDisplayTypeId());
            if (this.getNewFilter().getDwhElementModel().getDisplayTypeId() == Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION) {
                this.newFilter.getSelectedElementValues().clear();
                this.newFilter.getSelectedElementValues().add(this.getNewFilter().getDwhElementModel().getMultiSelectionValues().get(0));
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void calculateAttributeVisibilityControls(int displayTypeId) {
        String methodName = "calculateAttributeVisibilityControls";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        try {
            //LookupModel operator = null;
            if (displayTypeId == Defines.DWHELEMENT_DISPLAY_TYPES.DATE) {
                dateAttributeVisible = true;
                stringAttributeVisible = false;
                numberAttributeVisible = false;
                multiSelectAttributeVisible = false;
                //operator = Statics.Operator.get(Defines.OPERATORS.BETWEEN);
            } else if (displayTypeId == Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION) {
                dateAttributeVisible = false;
                stringAttributeVisible = false;
                numberAttributeVisible = false;
                multiSelectAttributeVisible = true;
                //operator = Statics.Operator.get(Defines.OPERATORS.IN);
            } else if (displayTypeId == Defines.DWHELEMENT_DISPLAY_TYPES.NUMERIC) {
                dateAttributeVisible = false;
                stringAttributeVisible = false;
                numberAttributeVisible = true;
                multiSelectAttributeVisible = false;
                //operator = Statics.Operator.get(Defines.OPERATORS.NOT_EQUAL);
            } else {
                dateAttributeVisible = false;
                stringAttributeVisible = true;
                numberAttributeVisible = false;
                multiSelectAttributeVisible = false;
                //operator = Statics.Operator.get(Defines.OPERATORS.EQUALS);
            }
            //newFilter.setOperatorModel(operator);
            if (displayTypeId != Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION && newFilter.getSelectedElementValues() != null) {
                newFilter.getSelectedElementValues().clear();
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public Date convertStringToDate(String date) throws ParseException {
        String methodName = "convertStringToDate";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();

        Date thedate = new SimpleDateFormat("yyyy MM dd", Locale.ENGLISH).parse(date);

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return thedate;
    }

    public String deleteFilters(FilterModel dm) {
        String methodName = "deleteFilters";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();

        this.selectedGroup.getFilterList().remove(dm);

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return null;
    }

    public void createGroup() throws CommonException {
        String methodName = "createGroup";
        WebLogModel webLog = new WebLogModel();
        webLog.setOperationName("Create Ads Group");
        webLog.setPageName("AdsGroupEditView");
        webLog.setUserName(loggedInUser.getUsername());
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        groupFacade.addNewAdsGroup(selectedGroup, loggedInUser.getUserType());
        webLog.setStringAfter(selectedGroup.toString());
        webLog.setStringBefore(null);
        WebLoggerFacade.insertWebLog(webLog);
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public String approveGroup() {
        String methodName = "approveGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        FacesMessage curMessage = null;
        WebLogModel webLog = new WebLogModel();
        int status = selectedGroup.getStatus();
        String ret = "OperationsApproval.xhtml?faces-redirect=true";
        if ((!checkRequiredFieldsOnApproval()) || (!validThresholds())) {
            return null;
        }
        try {
            groupFacade.approveAdsGroup(selectedGroup);
            if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedGroup.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_EDIT)) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedGroup.getGroupName(), "editing");
                webLog.setOperationName("Approve Ads group editing");
                webLog.setStringBefore(oldAdsGroupWebModel.toString());
                webLog.setStringAfter(selectedGroup.toString());
            } else if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedGroup.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_CREATION)) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedGroup.getGroupName(), "creation");
                webLog.setOperationName("Approve Ads group creation");
                webLog.setStringBefore(null);
                webLog.setStringAfter(oldAdsGroupWebModel.toString());
            } else if (status == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedGroup.getGroupName(), "deletion");
                webLog.setOperationName("Approve Ads group deletion");
                webLog.setStringBefore(oldAdsGroupWebModel.toString());
                webLog.setStringAfter(null);
            }

            webLog.setPageName("Operations Approval");
            //get user from session
            webLog.setUserName(loggedInUser.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occured while approving " + this.selectedGroup.getGroupName(), null);
            FacesContext.getCurrentInstance().addMessage(null, curMessage);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            ret = null;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsEditViewBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return ret;
    }

    public String rejectGroup() {
        String methodName = "rejectGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();
        FacesMessage curMessage = null;
        WebLogModel webLog = new WebLogModel();
        int status = selectedGroup.getStatus();
        String ret = "OperationsApproval.xhtml?faces-redirect=true";
        try {
            groupFacade.rejectAdsGroup(selectedGroup);
            if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedGroup.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_EDIT)) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedGroup.getGroupName(), "editing");
                webLog.setOperationName("Reject Ads group editing");
                webLog.setStringBefore(oldAdsGroupWebModel.toString());
                webLog.setStringAfter(selectedGroup.toString());
            } else if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedGroup.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_CREATION)) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedGroup.getGroupName(), "creation");
                webLog.setOperationName("Reject Ads group creation");
                webLog.setStringBefore(oldAdsGroupWebModel.toString());
                webLog.setStringAfter(null);
            } else if (status == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedGroup.getGroupName(), "deletion");
                webLog.setOperationName("Reject Ads group deletion");
                webLog.setStringBefore(oldAdsGroupWebModel.toString());
                webLog.setStringAfter(selectedGroup.toString());
            }

            webLog.setPageName("Operations Approval");
            //get user from session
            webLog.setUserName(loggedInUser.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occured while rejecting " + this.selectedGroup.getGroupName(), null);
            FacesContext.getCurrentInstance().addMessage(null, curMessage);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            ret = null;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + AdsGroupsEditViewBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return ret;
    }

    public boolean validThresholds() {
        if (this.selectedGroup.getDailyThreshold() > this.selectedGroup.getWeeklyThreshold()
                || this.selectedGroup.getDailyThreshold() > this.selectedGroup.getMonthlyThreshold()) {
            Utility.showErrorMessage(null, Constants.ERROR_DAILY_THRESHOLD);
            return false;
        }
        if (this.selectedGroup.getWeeklyThreshold() > this.selectedGroup.getMonthlyThreshold()) {
            Utility.showErrorMessage(null, Constants.ERROR_MONTHLY_THRESHOLD);
            return false;
        }
        return true;
    }

    public boolean checkRequiredFieldsOnApproval() {
        // update : eslam.ahmed | check if the filter list is not empty
        if (this.selectedGroup.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_FILE_FILTER) {
            FacesContext fc = FacesContext.getCurrentInstance();
            this.selectedGroup.setFilesModel((ArrayList<FileModel>) fc.getApplication().evaluateExpressionGet(fc, "#{uploadAndParseFileBean.groupFiles}", ArrayList.class));
            if ((this.selectedGroup.getFilesModel() == null) || (this.selectedGroup.getFilesModel().isEmpty())) {//Update
                Utility.showErrorMessage(null, Constants.FILTER_FILES_EMPTY, "Sms Group", selectedGroup.getGroupName());
                return false;
            }
        } else {
            if ((this.selectedGroup.getFilterList() == null) || (this.selectedGroup.getFilterList().isEmpty())) {//Update
                Utility.showErrorMessage(null, Constants.FILTER_CRITEREA_EMPTY, "Sms Group", selectedGroup.getGroupName());
                return false;
            }
        }
        return true;
    }

    public String saveGroup() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getMessageList().size() == 0) {
            String methodName = "saveGroup";
//            CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                    .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
            Date stepStartDate = new Date();
            FacesMessage curMessage = null;
            String ret = "AdsGroupsList.xhtml?faces-redirect=true";
            try {
                //            this.selectedGroup.setStatus((loggedInUser.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE)
                //                    ? GeneralConstants.STATUS_PENDING_VALUE : GeneralConstants.STATUS_APPROVED_VALUE);
                this.selectedGroup.setCreatedBy((int) loggedInUser.getId());
                // eslam.ahmed
                if (!validThresholds()) {
                    return null;
                }

                if (this.selectedGroup.getGroupType().getId() == GeneralConstants.GROUP_CUSTOMER_FILE_FILTER) {
                    FacesContext fc = FacesContext.getCurrentInstance();
                    this.selectedGroup.setFilesModel((ArrayList<FileModel>) fc.getApplication().evaluateExpressionGet(fc, "#{uploadAndParseFileBean.groupFiles}", ArrayList.class));
                    if ((this.selectedGroup.getFilesModel() == null) || (this.selectedGroup.getFilesModel().isEmpty())) {//Update
                        Utility.showErrorMessage(null, Constants.FILTER_FILES_EMPTY, "Ads Group", selectedGroup.getGroupName());
                        ret = null;
                        return ret;
                    }
                    this.selectedGroup.setFilterQuery(new String());
                } else {
                    if ((this.selectedGroup.getFilterList() == null) || (this.selectedGroup.getFilterList().isEmpty())) {//Update
                        Utility.showErrorMessage(null, Constants.FILTER_CRITEREA_EMPTY, "Ads Group", selectedGroup.getGroupName());
                        ret = null;
                        return ret;
                    }
                    this.selectedGroup.setFilterQuery(this.groupFacade.getFilterQuery(this.selectedGroup.getFilterList()));
                    this.selectedGroup.setFilesModel(new ArrayList<FileModel>());
                }
                if (editable) {
                    editGroup();
                    //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("ResetMessage", this.selectedGroup.getGroupName() + "edited successfully");
                    Utility.showInfoMessage(null, Constants.ITEM_EDITED, "Group", this.selectedGroup.getGroupName());
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                } else if (creation) {
                    createGroup();
                    Utility.showInfoMessage(null, Constants.ITEM_ADDED, "Group", this.selectedGroup.getGroupName());
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                }
                cleanUploadedFiles();
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(AdsGroupsEditViewBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
                String mode = (creation) ? "adding" : "editing";
                //Uniqueness Exception handling
                if ((ex.getErrorCode() == ErrorCodes.UNIQUE_PRIORITY) || (ex.getErrorCode() == ErrorCodes.UNIQUE_NAME)) {

                    if (ex.getErrorCode() == ErrorCodes.UNIQUE_PRIORITY) {
                        Utility.showErrorMessage(null, ErrorCodes.UNIQUE_PRIORITY, "Ads Group", selectedGroup.getGroupName());
                    }
                    if (ex.getErrorCode() == ErrorCodes.UNIQUE_NAME) {
                        Utility.showErrorMessage(null, ErrorCodes.UNIQUE_NAME, "Ads Group", selectedGroup.getGroupName());
                    }
                } else { //Other Exception
                    curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occured while " + mode + " " + this.selectedGroup.getGroupName(), null);
                    FacesContext.getCurrentInstance().addMessage(null, curMessage);
                }
                ret = null;
            }
            Date stepEndDate = new Date();
            long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//            CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                    .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
            return ret;
        }
        return null;
    }

    public String cancelGroup() {
        String methodName = "cancelGroup";
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has started")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName).build());
        Date stepStartDate = new Date();

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(BEAN_NAME + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bean Method has Ended")
                .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, BEAN_NAME)
                .put(GeneralConstants.StructuredLogKeys.METHOD_NAME, methodName)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return "AdsGroupsList.xhtml?faces-redirect=true";
    }

    ////Fix issueID:110734
    public void requiredFieldsValidator(FacesContext context,
            UIComponent component, Object value) {

        String componentID = component.getId();
        if (componentID.equals("groupName")) {
            String groupName = (String) value;
            SelectOneRadio filterCritera = (SelectOneRadio) component.getAttributes().get("filterCriterieAttr");
            LookupModel filterCreiteraModel = (LookupModel) filterCritera.getValue();

            boolean validationFailed = false;
            if (groupName == null || (groupName != null && groupName.trim().equals(""))) {
                validationFailed = true;
            } else if (filterCreiteraModel == null) {
                validationFailed = true;
            }
            if (validationFailed) {
                Utility.showErrorMessage(null, Constants.MISSING_DATA);
                return;
            }

        }

    }

    ////End Fix
    /////////////////////////////
    /////Setters and Getters/////
    /////////////////////////////
    public AdsGroupWebModel getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(AdsGroupWebModel selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean getApprovable() {
        return approvable;
    }

    public void setApprovable(boolean approvable) {
        this.approvable = approvable;
    }

    public boolean isEditFlag() {
        return editFlag;
    }

    public void setEditFlag(boolean editFlag) {
        this.editFlag = editFlag;
    }

    public boolean getDonotContactFlag() {
        return donotContactFlag;
    }

    public void setDonotContactFlag(boolean donotContactFlag) {
        this.donotContactFlag = donotContactFlag;
        selectedGroup.setDonotContact((donotContactFlag) ? GeneralConstants.TRUE : GeneralConstants.FALSE);
    }

    public AdsGroupFacade getGroupFacade() {
        return groupFacade;
    }

    public void setGroupFacade(AdsGroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    public boolean isCreation() {
        return creation;
    }

    public void setCreation(boolean creation) {
        this.creation = creation;
    }

    public ArrayList<DWHElementModel> getDwhElements() {
        return dwhElements;
    }

    public void setDwhElements(ArrayList<DWHElementModel> dwhElements) {
        this.dwhElements = dwhElements;
    }

    public ArrayList<DWHElementModel> getAttrValues() {
        return attrValues;
    }

    public void setAttrValues(ArrayList<DWHElementModel> attrValues) {
        this.attrValues = attrValues;
    }

    public ArrayList<LookupModel> getOperators() {
        return operators;
    }

    public void setOperators(ArrayList<LookupModel> operators) {
        this.operators = operators;
    }

    public FilterModel getNewFilter() {
        return newFilter;
    }

    public void setNewFilter(FilterModel filterModel) {
        this.newFilter = filterModel;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String value) {
        this.query = value;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getMinEndDate() {
        return minEndDate;
    }

    public void setMinEndDate(Date minEndDate) {
        this.minEndDate = minEndDate;
    }

    public boolean isDateAttributeVisible() {
        return dateAttributeVisible;
    }

    public void setDateAttributeVisible(boolean dateAttributeVisible) {
        this.dateAttributeVisible = dateAttributeVisible;
    }

    public boolean isStringAttributeVisible() {
        return stringAttributeVisible;
    }

    public void setStringAttributeVisible(boolean stringAttributeVisible) {
        this.stringAttributeVisible = stringAttributeVisible;
    }

    public boolean isNumberAttributeVisible() {
        return numberAttributeVisible;
    }

    public void setNumberAttributeVisible(boolean numberAttributeVisible) {
        this.numberAttributeVisible = numberAttributeVisible;
    }

    public boolean isMultiSelectAttributeVisible() {
        return multiSelectAttributeVisible;
    }

    public void setMultiSelectAttributeVisible(boolean multiSelectAttributeVisible) {
        this.multiSelectAttributeVisible = multiSelectAttributeVisible;
    }

    public ArrayList<LookupModel> getGroupTypesList() {
        return groupTypesList;
    }

    public void setGroupTypesList(ArrayList<LookupModel> groupTypesList) {
        this.groupTypesList = groupTypesList;
    }

    public UploadAndParseFileBean getUploadAndParseFileBean() {
        return uploadAndParseFileBean;
    }

    public void setUploadAndParseFileBean(UploadAndParseFileBean uploadAndParseFileBean) {
        this.uploadAndParseFileBean = uploadAndParseFileBean;
    }

}
