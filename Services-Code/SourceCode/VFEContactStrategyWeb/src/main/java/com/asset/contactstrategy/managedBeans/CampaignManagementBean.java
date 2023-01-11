/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CustomersCampaignsModel;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import com.asset.contactstrategy.common.models.DWHFilterValueModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.facade.CampaignFacade;
import com.asset.contactstrategy.facade.DWHElementFacade;
import com.asset.contactstrategy.facade.LookupFacade;
import com.asset.contactstrategy.facade.ServiceManagmentFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.CampaignWebModel;
import com.asset.contactstrategy.webmodels.ServiceWebModel;
import java.io.InputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.password.Password;
import org.primefaces.component.selectmanymenu.SelectManyMenu;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author rania.magdy
 */
@ManagedBean(name = "campaignManagementBean")
@ViewScoped
public class CampaignManagementBean implements Serializable {

    private CampaignWebModel selectedCampaign;
    private Integer managementMode;
    private UserModel userModel;
    private CampaignWebModel oldSelectedCampaign;
    private ArrayList<ServiceWebModel> servicesList;
    private ArrayList<ServiceWebModel> selectedServices;
    private ArrayList<ServiceWebModel> oldSelectedServices;

    @ManagedProperty(value = "#{uploadAndParseFileBean}")
    private UploadAndParseFileBean uploadAndParseFileBean;

    //kiro component
    private ArrayList<LookupModel> filterTypesList = new ArrayList<LookupModel>();
    private ArrayList<LookupModel> operators = new ArrayList<LookupModel>();
    private FilterModel newFilter = new FilterModel();
    private boolean dateAttributeVisible = false;
    private boolean stringAttributeVisible = false;
    private boolean numberAttributeVisible = false;
    private boolean multiSelectAttributeVisible = false;
    private String query = new String();
    private Date startDate;
    private Date endDate;
    private Date currentDate = new Date();
    private Date minEndDate;
    private ArrayList<DWHElementModel> dwhElements = new ArrayList<DWHElementModel>();
    private ArrayList<DWHElementModel> attrValues = new ArrayList<DWHElementModel>();

    private Boolean exportedListIsEmpty = null;

    @PostConstruct
    public void init() {
        String methodName = "init";
        try {
            userModel = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE);

            startDate = new Date();
            endDate = new Date();
            cleanUploadedFiles();
            checkManagementMode();
            fillAttrValues();
            fillOperators();
            fillGroupTypes();
            //GET APPROVED SERVICES LIST
            getApprovedServices();
            getCampaignServices();
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
        //  TODO delete this
//        LookupModel filterType = new LookupModel();
//        filterType.setId(Defines.GROUP_TYPES.UPLOADED.getId());
//        filterType.setLable(Defines.GROUP_TYPES.UPLOADED.getLable());
    }

    public void checkManagementMode() {
        String methodName = "checkManagementMode";
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        CampaignFacade campaignFacade = new CampaignFacade(userModel);
        Integer newManagementMode = (Integer) ec.getRequestMap().get(Constants.MANAGEMENT_MODE);//Used for reinit cases
        if (newManagementMode != null) {
            managementMode = newManagementMode;
        }
        if (managementMode == Constants.EDIT_MODE) {
            if (newManagementMode != null) {
                this.selectedCampaign = (CampaignWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
            }
            selectedCampaign.setIsEditMode(true);

            oldSelectedCampaign = new CampaignWebModel(selectedCampaign);

        } else if (managementMode == Constants.VIEW_MODE) {
            if (newManagementMode != null) {
                this.selectedCampaign = (CampaignWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
            }
            selectedCampaign.setIsViewMode(true);

        } else if (managementMode == Constants.CREATION_MODE) {
            selectedCampaign = new CampaignWebModel();
            selectedCampaign.setMaxNumberOfCommunications(1);
            selectedCampaign.setMaxTargetedCustomers(1);
            selectedCampaign.setIsCreationMode(true);
            selectedCampaign.setFilterType(SystemLookups.GROUP_TYPES.get(GeneralConstants.GROUP_TYPE_UPLOADED_BASED));
            //Updates        
            selectedCampaign.setPriority(1);
            cleanUploadedFiles();
        }

        if (managementMode == Constants.APPROVAL_MODE) {
            if (newManagementMode != null) {
                this.selectedCampaign = (CampaignWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY);
            }
            selectedCampaign.setApprovalMode(true);

            oldSelectedCampaign = new CampaignWebModel(selectedCampaign);
        }
        if (managementMode != Constants.CREATION_MODE) {
            try {
                if (this.selectedCampaign.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_ELEMENT_FILTER) {
                    this.selectedCampaign.setFilterList(campaignFacade.retrieveCampaignFilters(this.selectedCampaign.getVersionId()));
                } else {
                    this.selectedCampaign.setFilesModel(campaignFacade.retrieveCampaignFiles(this.selectedCampaign.getVersionId()));
//                    UploadAndParseFileBean uploadAndParseFileBean = null;
//                    uploadAndParseFileBean = (UploadAndParseFileBean) Utility.getSessionVarFromContext("uploadAndParseFileBean");
                    if (uploadAndParseFileBean != null) {
                        uploadAndParseFileBean.setGroupFiles(new ArrayList<FileModel>());
                        uploadAndParseFileBean.setGroupFiles(this.selectedCampaign.getFilesModel());
                    }
                }
                if (managementMode == Constants.CREATION_MODE) {

                    cleanUploadedFiles();
                }
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(CampaignManagementBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + "." + methodName + "]", e);
                Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            }
        }
    }

    public void cleanUploadedFiles() {
        String methodName = "cleanUploadedFiles";
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has started")
                .build());
        Date stepStartDate = new Date();
        try {
            UploadAndParseFileBean uploadAndParseFileBean = null;
            uploadAndParseFileBean = (UploadAndParseFileBean) Utility.getSessionVarFromContext("uploadAndParseFileBean");
            if (uploadAndParseFileBean != null) {
                uploadAndParseFileBean.setGroupFiles(new ArrayList<FileModel>());
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error(CampaignManagementBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void fillGroupTypes() {
        String methodName = "fillGroupTypes";
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has started")
                .build());
        Date stepStartDate = new Date();
        Iterator it = SystemLookups.GROUP_TYPES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            filterTypesList.add((LookupModel) pair.getValue());
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    private void fillAttrValues() {
        String methodName = "fillAttrValues";
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has started")
                .build());
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
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void addAttribute() {
        String methodName = "addAttribute";
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has started")
                .build());
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
            if (this.selectedCampaign.getFilterList().contains(newFilter)) {
                curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "This filter has been selected before.", null);
                FacesContext.getCurrentInstance().addMessage(null, curMessage);
                emptyValue = true;
            }
            if (!emptyValue) {
                this.selectedCampaign.getFilterList().add(newFilter);
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignManagementBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
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
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void fillOperators() {
        String methodName = "fillOperators";
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has started")
                .build());
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
            CommonLogger.errorLogger.error(CampaignManagementBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public void calculateAttributeVisibilityControls(int displayTypeId) {
        String methodName = "calculateAttributeVisibilityControls";
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has started").build());
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
            CommonLogger.errorLogger.error(CampaignManagementBean.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        }
        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
    }

    public Date convertStringToDate(String date) throws ParseException {
        String methodName = "convertStringToDate";
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has started")
                .build());
        Date stepStartDate = new Date();

        Date thedate = new SimpleDateFormat("yyyy MM dd", Locale.ENGLISH).parse(date);

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return thedate;
    }

    public String deleteFilters(FilterModel dm) {
        String methodName = "deleteFilters";
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":started.");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has started")
                .build());
        Date stepStartDate = new Date();

        this.selectedCampaign.getFilterList().remove(dm);

        Date stepEndDate = new Date();
        long totalProcessingTime = stepEndDate.getTime() - stepStartDate.getTime();
//        CommonLogger.businessLogger.debug(CampaignManagementBean.class.getName() + " || " + methodName + ":ended in " + totalProcessingTime + " msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, methodName + " has ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, totalProcessingTime).build());
        return null;
    }

    public void getApprovedServices() {
        try {
            ServiceManagmentFacade managmentFacade = new ServiceManagmentFacade();
            servicesList = managmentFacade.getApprovedServices();

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApprovedServices]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + ".getApprovedServices]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public CampaignWebModel getSelectedCampaign() {
        return selectedCampaign;
    }

    public void setSelectedCampaign(CampaignWebModel selectedCampaign) {
        this.selectedCampaign = selectedCampaign;
    }

    public ArrayList<ServiceWebModel> getServicesList() {
        return servicesList;
    }

    public void setServicesList(ArrayList<ServiceWebModel> servicesList) {
        this.servicesList = servicesList;
    }

    public ArrayList<ServiceWebModel> getSelectedServices() {
        return selectedServices;
    }

    public void setSelectedServices(ArrayList<ServiceWebModel> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public ArrayList<LookupModel> getFilterTypesList() {
        return filterTypesList;
    }

    public void setFilterTypesList(ArrayList<LookupModel> filterTypesList) {
        this.filterTypesList = filterTypesList;
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

    public void setNewFilter(FilterModel newFilter) {
        this.newFilter = newFilter;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String value) {
        this.query = value;
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

    public String saveCampaign() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getMessageList().size() == 0) {
//            CommonLogger.businessLogger.info(CampaignManagementBean.class.getName() + " || " + "Start : saveCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start saveCampaign()")
                    .build());
            WebLogModel webLog = new WebLogModel();

            try {
                int st = validateFiltersAndFiles();
                if (st == 1) {
                    return null;
                }
                CampaignFacade campaignFacade = new CampaignFacade(userModel);
                if (this.selectedCampaign.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_FILE_FILTER) {
                    FacesContext fc = FacesContext.getCurrentInstance();
                    this.selectedCampaign.setFilesModel((ArrayList<FileModel>) fc.getApplication().evaluateExpressionGet(fc, "#{uploadAndParseFileBean.groupFiles}", ArrayList.class));
                    this.selectedCampaign.setFilterQuery(new String());
                } else {
                    this.selectedCampaign.setFilterQuery(campaignFacade.getFilterQuery(this.selectedCampaign.getFilterList()));
                    this.selectedCampaign.setFilesModel(new ArrayList<FileModel>());
                }

                if (managementMode == Constants.EDIT_MODE) {
                    campaignFacade.editCampaign(selectedCampaign);
                    Utility.showInfoMessage(null, Constants.ITEM_EDITED, "Campaign", selectedCampaign.getCampaignName());
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                    webLog.setOperationName("Update Campaign");
                    webLog.setStringBefore(oldSelectedCampaign.toString());
                    webLog.setStringAfter(selectedCampaign.toString());
                    webLog.setPageName("Campaign Management");
                    //get user from session
                    webLog.setUserName(userModel.getUsername());
                    WebLoggerFacade.insertWebLog(webLog);
                    cleanUploadedFiles();
                    return "CampaignList.xhtml";

                } else if (managementMode == Constants.CREATION_MODE) {

                    selectedCampaign = campaignFacade.createCampaign(selectedCampaign, selectedServices);
                    Utility.showInfoMessage(null, Constants.ITEM_ADDED, "Campaign", selectedCampaign.getCampaignName());
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                    webLog.setOperationName("Create Campaign");
                    webLog.setStringBefore(null);
                    webLog.setStringAfter(selectedCampaign.toString());
                    webLog.setPageName("Campaign Management");
                    //get user from session
                    webLog.setUserName(userModel.getUsername());
                    WebLoggerFacade.insertWebLog(webLog);
                    cleanUploadedFiles();
                    return "CampaignList.xhtml";

                }

                webLog.setPageName("Campaign Management");
                //get user from session
                webLog.setUserName(userModel.getUsername());
                WebLoggerFacade.insertWebLog(webLog);

            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [saveCampaign]", ex);

                if (ex.getErrorCode() == ErrorCodes.UNIQUE_NAME) {
                    Utility.showErrorMessage(null, ErrorCodes.UNIQUE_NAME, "Campaign", selectedCampaign.getCampaignName());

                } else {
                    Utility.showErrorMessage(null, ex.getErrorCode(), "in saving Campaign");
                }
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + ".saveCampaign]", e);
                Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            }
        }
//        CommonLogger.businessLogger.info(CampaignManagementBean.class.getName() + " || " + "End : saveCampaign() ");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " End saveCampaign()")
                .build());
        RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
        return null;
    }

    public int validateFiltersAndFiles() throws CommonException {
        ArrayList<FilterModel> filterList = selectedCampaign.getFilterList();

        ArrayList<FileModel> fileList = null;
        UploadAndParseFileBean uploadAndParseFileBean = null;
        uploadAndParseFileBean = (UploadAndParseFileBean) Utility.getSessionVarFromContext("uploadAndParseFileBean");
        if (uploadAndParseFileBean != null) {
            fileList = uploadAndParseFileBean.getGroupFiles();
        }
        if ((fileList == null || fileList.isEmpty()) && (filterList == null || filterList.isEmpty())) {
            Utility.showErrorMessage(null, Constants.MISSING_DATA);
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            return 1;
        }
        return 0;
    }

    public void requiredFieldsValidator(FacesContext context,
            UIComponent component, Object value) {

        String componentID = component.getId();
        if (componentID.equals("priorityID")) {
            boolean validationFailed = false;

            InputText nameComponent = (InputText) component.getAttributes().get("nameAttr");
            Calendar endComponent = (Calendar) component.getAttributes().get("endDateAttr");
            Calendar startComponent = (Calendar) component.getAttributes().get("startDateAttr");
            InputTextarea arabicScriptComponent = (InputTextarea) component.getAttributes().get("arabicAttr");
            InputTextarea englishScriptComponent = (InputTextarea) component.getAttributes().get("englishAttr");
            InputTextarea campaignDescriptionComponent = (InputTextarea) component.getAttributes().get("campDescAttr");
            Spinner communicationComponent = (Spinner) component.getAttributes().get("commAttr");
            Spinner targetedComponent = (Spinner) component.getAttributes().get("percentageAttr");
            SelectManyMenu servicesComponent = (SelectManyMenu) component.getAttributes().get("serviceAttr");
//            DataTable filesComponent = (DataTable) component.getAttributes().get("filesAttr");
//            DataTable filtersComponent = (DataTable) component.getAttributes().get("filtersAttr");

            Integer priority = (Integer) value;
            String campaignName = (String) nameComponent.getValue();
            Date endDate = (Date) endComponent.getValue();
            Date startDate = (Date) startComponent.getValue();
            String arabicScript = (String) arabicScriptComponent.getSubmittedValue() != null ? (String) arabicScriptComponent.getSubmittedValue() : (String) arabicScriptComponent.getValue();
            String englishScript = (String) englishScriptComponent.getSubmittedValue() != null ? (String) englishScriptComponent.getSubmittedValue() : (String) englishScriptComponent.getValue();
            String campaignDesc = (String) campaignDescriptionComponent.getSubmittedValue() != null ? (String) campaignDescriptionComponent.getSubmittedValue() : (String) campaignDescriptionComponent.getValue();
            Integer communications = Integer.parseInt(String.valueOf(communicationComponent.getSubmittedValue()));
            Integer targeted = Integer.parseInt(String.valueOf(targetedComponent.getSubmittedValue()));
            ArrayList<ServiceWebModel> services = (ArrayList<ServiceWebModel>) servicesComponent.getValue();
//            ArrayList<FilterModel> filters = (ArrayList<FilterModel>) filtersComponent.getValue();
//            ArrayList<FileModel> files = (ArrayList<FileModel>) filtersComponent.getValue();

//            System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
//            System.out.println("nameComponent || Value: " + nameComponent.getValue() + " | SubmittedValue: " + nameComponent.getSubmittedValue());
//            System.out.println("endComponent || Value: " + endComponent.getValue() + " | SubmittedValue: " + endComponent.getSubmittedValue());
//            System.out.println("startComponent || Value: " + startComponent.getValue() + " | SubmittedValue: " + startComponent.getSubmittedValue());
//            System.out.println("arabicScriptComponent || Value: " + arabicScriptComponent.getValue() + " | SubmittedValue: " + arabicScriptComponent.getSubmittedValue());
//            System.out.println("englishScriptComponent || Value: " + englishScriptComponent.getValue() + " | SubmittedValue: " + englishScriptComponent.getSubmittedValue());
//            System.out.println("campaignDescriptionComponent || Value: " + campaignDescriptionComponent.getValue() + " | SubmittedValue: " + campaignDescriptionComponent.getSubmittedValue());
//            System.out.println("communicationComponent || Value: " + communicationComponent.getValue() + " | SubmittedValue: " + communicationComponent.getSubmittedValue());
//            System.out.println("targetedComponent || Value: " + targetedComponent.getValue() + " | SubmittedValue: " + targetedComponent.getSubmittedValue());
//            System.out.println("servicesComponent || Value: " + servicesComponent.getValue() + " | SubmittedValue: " + servicesComponent.getSubmittedValue());
//            System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
            if (campaignName == null || (campaignName != null && campaignName.trim().equals(""))) {
                validationFailed = true;
            } else if (arabicScript == null || (arabicScript != null && arabicScript.trim().equals(""))) {
                validationFailed = true;
            } else if (priority == null || (priority != null && priority.equals(0))) {
                validationFailed = true;
            } else if (englishScript == null || (englishScript != null && englishScript.trim().equals(""))) {
                validationFailed = true;
            } else if (campaignDesc == null || (campaignDesc != null && campaignDesc.trim().equals(""))) {
                validationFailed = true;
            } else if (communications == null || (communications != null && communications.equals(0))) {
                validationFailed = true;
            } else if (targeted == null) {//|| (targeted != null && targeted.equals(0))) {
                validationFailed = true;
            }

            if (validationFailed) {
                Utility.showErrorMessage(null, Constants.MISSING_DATA);
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                return;
            }

            if (endDate.before(startDate)) {
                Utility.showErrorMessage(null, Constants.INVALID_DATE);
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                return;
            }
            if (services == null || services.size() == 0) {
                Utility.showErrorMessage(null, Constants.SELECT_SERVICES);
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                return;
            }

        }
    }

    public void resumeCampaign() {
        try {
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "Start : resumeCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Start resumeCampaign()")
                    .build());
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            selectedCampaign.setCampaignStatus(GeneralConstants.CAMPAIGN_STATUS_RESUMED_VALUE);
            selectedCampaign.setEnablePause(true);
            selectedCampaign.setEnableStart(false);
            selectedCampaign.setEnableStop(true);
            campaignFacade.changeCampaignStatus(selectedCampaign);
            init();
            Utility.showInfoMessage(null, Constants.RESUMED, "Campaign", selectedCampaign.getCampaignName());
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "End : resumeCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " End rsumeCampaign")
                    .build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [resumeCampaign]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in resuming Campaign");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + ".resumeCampaign]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }

    }

    public void stopCampaign() {
        try {
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "Start : stopCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Start stopCampaign()")
                    .build());
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            selectedCampaign.setCampaignStatus(GeneralConstants.CAMPAIGN_STATUS_STOPPED_VALUE);
            selectedCampaign.setEnablePause(false);
            selectedCampaign.setEnableStart(false);
            selectedCampaign.setEnableStop(false);
            campaignFacade.changeCampaignStatus(selectedCampaign);
            init();
            Utility.showInfoMessage(null, Constants.STOPPED, "Campaign", selectedCampaign.getCampaignName());
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "End : stopCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " End resumeCampaign")
                    .build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [stopCampaign]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in stopping Campaign");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + ".stopCampaign]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public void pauseCampaign() {
        try {
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "Start : pauseCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Start pauseCampaign")
                    .build());
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            selectedCampaign.setCampaignStatus(GeneralConstants.CAMPAIGN_STATUS_PAUSED_VALUE);
            selectedCampaign.setEnablePause(false);
            selectedCampaign.setEnableStart(true);
            selectedCampaign.setEnableStop(true);
            campaignFacade.changeCampaignStatus(selectedCampaign);
            //selectedCampaign = campaignFacade.get
            init();
            Utility.showInfoMessage(null, Constants.PAUSED, "Campaign", selectedCampaign.getCampaignName());
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "End : pauseCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " End pauseCampaign()")
                    .build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [pauseCampaign]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in pausing Campaign");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + ".pauseCampaign]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public String approveCampaignOperation() {
        try {
            if (!checkRequiredFieldsOnApproval()) {
                return null;
            }
            int status = selectedCampaign.getStatus();
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            if (this.selectedCampaign.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_FILE_FILTER) {
                FacesContext fc = FacesContext.getCurrentInstance();
                this.selectedCampaign.setFilesModel((ArrayList<FileModel>) fc.getApplication().evaluateExpressionGet(fc, "#{uploadAndParseFileBean.groupFiles}", ArrayList.class));
                this.selectedCampaign.setFilterQuery(new String());
            } else {
                this.selectedCampaign.setFilterQuery(campaignFacade.getFilterQuery(this.selectedCampaign.getFilterList()));
                this.selectedCampaign.setFilesModel(new ArrayList<FileModel>());
            }

            campaignFacade.approveCampaign(selectedCampaign);

            WebLogModel webLog = new WebLogModel();
            if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedCampaign.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_EDIT)) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedCampaign.getCampaignName(), "editing");
                webLog.setOperationName("Approve Campaign editing");
                webLog.setStringBefore(oldSelectedCampaign.toString());
                webLog.setStringAfter(selectedCampaign.toString());
            } else if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedCampaign.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_CREATION)) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedCampaign.getCampaignName(), "creation");
                webLog.setOperationName("Approve Campaign creation");
                webLog.setStringBefore(null);
                webLog.setStringAfter(selectedCampaign.toString());
            } else if (status == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                Utility.showInfoMessage(null, Constants.ITEM_APPROVAL, selectedCampaign.getCampaignName(), "deletion");
                webLog.setOperationName("Approve Campaign deletion");
                webLog.setStringBefore(selectedCampaign.toString());
                webLog.setStringAfter(null);
            }

            webLog.setPageName("Campaign Management");
            //get user from session
            webLog.setUserName(userModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            return "OperationsApproval.xhtml?faces-redirect=true";

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveCampaignOperation]", ex);
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            Utility.showErrorMessage(null, ex.getErrorCode(), "in approving");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + ".approveCampaignOperation]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
        }
        return null;
    }

    public String rejectCampaignOperation() {
        try {
            int status = selectedCampaign.getStatus();
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            if (this.selectedCampaign.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_FILE_FILTER) {
                FacesContext fc = FacesContext.getCurrentInstance();
                this.selectedCampaign.setFilesModel((ArrayList<FileModel>) fc.getApplication().evaluateExpressionGet(fc, "#{uploadAndParseFileBean.groupFiles}", ArrayList.class));
                this.selectedCampaign.setFilterQuery(new String());
            } else {
                this.selectedCampaign.setFilterQuery(campaignFacade.getFilterQuery(this.selectedCampaign.getFilterList()));
                this.selectedCampaign.setFilesModel(new ArrayList<FileModel>());
            }

            campaignFacade.rejectCampaign(selectedCampaign);

            WebLogModel webLog = new WebLogModel();
            if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedCampaign.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_EDIT)) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedCampaign.getCampaignName(), "editing");
                webLog.setOperationName("Reject Campaign editing");
                webLog.setStringBefore(oldSelectedCampaign.toString());
                webLog.setStringAfter(null);
            } else if (status == GeneralConstants.STATUS_PENDING_VALUE && selectedCampaign.getStatusName().equals(GeneralConstants.APPROVAL_STATUS_NAME_CREATION)) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedCampaign.getCampaignName(), "creation");
                webLog.setOperationName("Reject Campaign creation");
                webLog.setStringBefore(oldSelectedCampaign.toString());
                webLog.setStringAfter(null);
            } else if (status == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                Utility.showInfoMessage(null, Constants.ITEM_REJECTION, selectedCampaign.getCampaignName(), "deletion");
                webLog.setOperationName("Reject Campaign deletion");
                webLog.setStringBefore(oldSelectedCampaign.toString());
                oldSelectedCampaign.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                oldSelectedCampaign.setStatusName(GeneralConstants.STATUS_APPROVED);
                webLog.setStringAfter(oldSelectedCampaign.toString());
            }

            webLog.setPageName("Campaign Management");
            //get user from session
            webLog.setUserName(userModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            return "OperationsApproval.xhtml?faces-redirect=true";

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectCampaignOperation]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), "in approving");
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + ".rejectCampaignOperation]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
        }
        return null;
    }

    public Boolean renderExport() {
        String methodName = "renderExport";
        Boolean res = null;
        try {
            //Export is rendered only if ..
            Date current = new Date();
            if ((selectedCampaign.isIsViewMode())
                    && (selectedCampaign.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE)
                    && (selectedCampaign.getCampaignStatus() == GeneralConstants.CAMPAIGN_STATUS_RESUMED_VALUE)
                    && ((current.compareTo(selectedCampaign.getStartDate()) > 0) && (current.compareTo(selectedCampaign.getEndDate()) < 0))//Current date between campaign dates
                    ) {
                res = Boolean.TRUE;
            } else {
                res = Boolean.FALSE;
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
        return res;
    }

    //TODO a msg should appear in case there r no customers
    public StreamedContent downloadCSV(int suspendedNumber) {
        exportedListIsEmpty = Boolean.FALSE;
        String methodName = "downloadSuspendedCSV";
        String fieldName = "MSISDN";
        ArrayList<CustomersCampaignsModel> customers = null;
        try {
            //Get run id
            String runId = SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_RUN_ID_KEY);
            if (runId == null) {
                throw new CommonException("Run id is not there", ErrorCodes.NULL_POINTER_ERROR);
            }
            //Set File Name and ensure valid suspended number
            String fileName = null;
            if (suspendedNumber == Defines.CAMPAIGNS_CUSTOMERS_NOT_SUSPENDED) {
                fileName = "Unsuspended_Users_For_[" + selectedCampaign.getCampaignName() + "]_With_[" + runId + "]";
            } else if (suspendedNumber == Defines.CAMPAIGNS_CUSTOMERS_SUSPENDED) {
                fileName = "Suspended_Users_For_[" + selectedCampaign.getCampaignName() + "]_With_[" + runId + "]";
            } else {
                throw new CommonException("Invalid Suspension number ", Constants.INVALID_SUSPEND_NUMBER);
            }
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            customers = campaignFacade.retrieveCampaignCustomers(
                    Integer.parseInt(runId),
                    selectedCampaign.getCampaignId(),
                    suspendedNumber);
            if (customers == null) {
                throw new CommonException("CustomersCampaigns Returned Null", ErrorCodes.NULL_POINTER_ERROR);
            } else if (customers.isEmpty()) {
                exportedListIsEmpty = Boolean.TRUE;
            }
            HashMap<String, ArrayList<String>> csvValues = new HashMap<>();//prepare hashmap
            csvValues.put(fieldName, new ArrayList<String>());
            for (CustomersCampaignsModel customer : customers) {
                csvValues.get(fieldName).add(customer.getMsisdn());
            }
            //generate file and return inputstream holding it
            InputStream fileHolder = com.asset.contactstrategy.common.utils.Utility.generateExcelFile(csvValues);
            if (fileHolder != null) {
                //Return content
                return new DefaultStreamedContent(fileHolder,
                        Defines.EXCEL_GENERATION.ExcelContentType,
                        fileName + Defines.EXCEL_GENERATION.ExcelFileType);
            } else {
                throw new CommonException("InputStream returned null", ErrorCodes.NULL_POINTER_ERROR);
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + "." + methodName + "]", ex);
            FacesMessage curMessage = null;
            curMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occured while downloading the file", ex.getErrorMsg());
            FacesContext.getCurrentInstance().addMessage(null, curMessage);
            return null;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, Constants.DOWNLOAD_FAILURE);
            return null;
        }
    }

    public void generateMessage() {
        if ((exportedListIsEmpty != null) && (exportedListIsEmpty == Boolean.TRUE)) {
            Utility.showInfoMessage(null, Constants.CUSTOMER_CAMPAIGN_IS_EMPTY);
        } else if ((exportedListIsEmpty != null) && (exportedListIsEmpty == Boolean.FALSE)) {
            Utility.showInfoMessage(null, Constants.CUSTOMER_CAMPAIGN_IS_NOT_EMPTY);
        }
        exportedListIsEmpty = null;
    }

    public Integer getSuspended() {
        return Defines.CAMPAIGNS_CUSTOMERS_SUSPENDED;
    }

    public Integer getUnSuspended() {
        return Defines.CAMPAIGNS_CUSTOMERS_NOT_SUSPENDED;
    }

    public UploadAndParseFileBean getUploadAndParseFileBean() {
        return uploadAndParseFileBean;
    }

    public void setUploadAndParseFileBean(UploadAndParseFileBean uploadAndParseFileBean) {
        this.uploadAndParseFileBean = uploadAndParseFileBean;
    }

    //////Fix issues ////////////
    public void getCampaignServices() {
        try {
            ServiceManagmentFacade managmentFacade = new ServiceManagmentFacade();
            selectedServices = managmentFacade.getCampaignServices(selectedCampaign);
            System.out.println("");
//        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
//        SelectManyMenu component = (SelectManyMenu) viewRoot.findComponent("advanced");
//        component.getChildren()

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getCampaignServices]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignManagementBean.class.getName() + ".getCampaignServices]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }
    ///////////End Fix Issue

    public boolean checkRequiredFieldsOnApproval() {
        if (this.selectedCampaign.getEndDate() == null) {
            Utility.showErrorMessage(null, Constants.MISSING_DATA);
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            return false;
        }

        if (this.selectedCampaign.getEndDate().before(this.selectedCampaign.getStartDate())) {
            Utility.showErrorMessage(null, Constants.INVALID_DATE);
            RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
            return false;
        }

        // update : eslam.ahmed | check if the filter list is not empty
        if (this.selectedCampaign.getFilterType().getId() == GeneralConstants.GROUP_CUSTOMER_FILE_FILTER) {
            FacesContext fc = FacesContext.getCurrentInstance();
            this.selectedCampaign.setFilesModel((ArrayList<FileModel>) fc.getApplication().evaluateExpressionGet(fc, "#{uploadAndParseFileBean.groupFiles}", ArrayList.class));
            if ((this.selectedCampaign.getFilesModel() == null) || (this.selectedCampaign.getFilesModel().isEmpty())) {//Update
                Utility.showErrorMessage(null, Constants.FILTER_FILES_EMPTY, "Sms Group", selectedCampaign.getCampaignName());
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                return false;
            }
        } else {
            if ((this.selectedCampaign.getFilterList() == null) || (this.selectedCampaign.getFilterList().isEmpty())) {//Update
                Utility.showErrorMessage(null, Constants.FILTER_CRITEREA_EMPTY, "Sms Group", selectedCampaign.getCampaignName());
                RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
                return false;
            }
        }
        return true;
    }
}
