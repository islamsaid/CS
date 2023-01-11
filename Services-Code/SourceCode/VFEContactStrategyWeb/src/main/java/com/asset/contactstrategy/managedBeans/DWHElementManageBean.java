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
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.facade.DWHElementFacade;
import com.asset.contactstrategy.facade.LookupFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import static com.asset.contactstrategy.managedBeans.DWHElementsListBean.getBEAN_NAME;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.DWHElementWebModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author Zain Al-Abedin
 */
@ManagedBean(name = "dwhElementManageBean")
@ViewScoped
public class DWHElementManageBean {

    private boolean createMode;
    private boolean editMode;
    private boolean viewMode;
    private boolean approvalMode;
    private DWHElementWebModel element;
    private DWHElementWebModel backupElement;

    private ArrayList<LookupModel> availableDatatypes;
    private ArrayList<LookupModel> availableDisplayTypes;
    private ArrayList<String> dates;
    private ArrayList<String> strings;
    private ArrayList<String> numbers;
    private ArrayList<String> usedColumns = new ArrayList<String>();
    private ArrayList<String> visibleColumnNames;

    private ArrayList<DWHElementWebModel> allElements;

    private boolean showMultiSelection = false;
    private UserModel userModel;

    public DWHElementManageBean() {
        try {
            CommonLogger.businessLogger.info("DWHElementManageBean" + " Starting  ");
            userModel = (UserModel) (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));

            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            Integer managementMode = (Integer) ec.getRequestMap().get(Constants.MANAGEMENT_MODE);
            if (managementMode != null) {
                if (managementMode.equals(Constants.CREATION_MODE)) {
                    setCreateMode(true);
                } else if (managementMode.equals(Constants.EDIT_MODE)) {
                    setEditMode(true);
                } else if (managementMode.equals(Constants.VIEW_MODE)) {
                    setViewMode(true);
                } else {
                    setApprovalMode(true);
                }
            } else {
                throw new Exception();
            }

            DWHElementFacade facade = new DWHElementFacade();

            allElements = facade.loadAllElementsFromDb();
            usedColumns = facade.loadDwhUsedColumns();

            if (isCreateMode()) {
                //Update
                DWHElementWebModel newDWHElementWebModel = new DWHElementWebModel();
                newDWHElementWebModel.setFileIndex(1);
                setElement(newDWHElementWebModel);
            } else {
                setElement((DWHElementWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY));
                backupElement = (DWHElementWebModel) element.clone();

            }

            usedColumns.remove(element.getDwhName());
            loadPageConstants();
            changeVisibleColumnName(element);
            displayTypeSelection();
            CommonLogger.businessLogger.info("DWHElementManageBean" + " End  ");
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + DWHElementManageBean.class.getName() + ".init]", ex);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
    }
//  @PostConstruct
//    public void init() {
//        try {
//            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//            Integer managementMode = (Integer) ec.getRequestMap().get(Constants.MANAGEMENT_MODE);
//            if (managementMode.equals(Constants.CREATION_MODE)) {
//                setCreateMode(true);
//            } else if (managementMode.equals(Constants.EDIT_MODE)) {
//                setEditMode(true);
//            } else if (managementMode.equals(Constants.VIEW_MODE)) {
//                setViewMode(true);
//            } else {
//                setApprovalMode(true);
//            }
//
//            DWHElementFacade facade = new DWHElementFacade();
//            usedColumns = facade.loadDwhUsedColumns();
// 
//            if (isCreateMode()) {
//                setElement(new DWHElementWebModel());
//            } else {
//                setElement((DWHElementWebModel) ec.getRequestMap().get(Constants.EDIT_MODEL_KEY));
//
//            }
//            loadPageConstants();
//            changeVisibleColumnName(element);
//
//        } catch (Exception ex) {
//            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueueListener]", ex);
//        }
//    }

    private static ArrayList<String> loadFieldNames(String prefix, int numberOfFields) {
        ArrayList<String> colNames = new ArrayList<String>();
        for (int i = 1; i <= numberOfFields; i++) {
            String columnName = prefix + i;
            colNames.add(columnName);
        }

        return colNames;
    }

    private void setColumnNames() throws Exception {
        try {
            dates = loadFieldNames(GeneralConstants.DATE_PREFIX, Defines.DWH_CUSTOMERS_COLUMNS_DATES);
            strings = loadFieldNames(GeneralConstants.STRING_PREFIX, Defines.DWH_CUSTOMERS_COLUMNS_STRINGS);
            strings.add(DBStruct.DWH_CUSTOMERS.MSISDN);
            strings.add(DBStruct.DWH_CUSTOMERS.SERVICE_CLASS);
            numbers = loadFieldNames(GeneralConstants.NUMBER_PREFIX, Defines.DWH_CUSTOMERS_COLUMNS_NUMBERS);
            numbers.add(DBStruct.DWH_CUSTOMERS.RATE_PLAN);
            numbers.add(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE);
            numbers.add(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Failed to load page constants!", e);

            throw e;
        }
    }

    private void loadPageConstants() throws Exception {
        try {
            LookupFacade lf = new LookupFacade();
            availableDatatypes = lf.loadIdLableLookups(DBStruct.LK_DATA_TYPE.TBL_NAME, DBStruct.LK_DATA_TYPE.DATA_TYPE_ID, DBStruct.LK_DATA_TYPE.DATA_TYPE_LABEL);
            availableDisplayTypes = lf.loadIdLableLookups(DBStruct.LK_DISPLAY_TYPE.TBL_NAME, DBStruct.LK_DISPLAY_TYPE.DISPLAY_TYPE_ID, DBStruct.LK_DISPLAY_TYPE.DISPLAY_TYPE_LABEL);
            setColumnNames();

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Failed to load page constants!", e);

            throw e;
        }
    }

    public String saveElement() {
        CommonLogger.businessLogger.info("Starting saveElement");
        String returnPage = null;
        try {
            if ((noEmptyFields() && isNotRedundant())) {
                CommonLogger.businessLogger.info("Element validated successfully ");
                DWHElementFacade facade = new DWHElementFacade();
                facade.insertNewElement(element);
//                CommonLogger.businessLogger.info("Element " + element.getName() + "has been Added successfully");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Element has been Added Successfully")
                        .put(GeneralConstants.StructuredLogKeys.ELEMENT_NAME, element.getName()).build());
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.getSessionMap().put(Constants.MANAGEMENT_MODE, Constants.CREATION_MODE);
                ec.getSessionMap().put(Constants.EDIT_MODEL_KEY, element.getDisplayName());
                returnPage = "DWHElementList.xhtml?faces-redirect=true";
                WebLogModel webLog = new WebLogModel();
                webLog.setOperationName("Add DWHElement");
                webLog.setPageName("DWHElementManage");
                webLog.setStringBefore(null);
                webLog.setStringAfter(element.toString());
                //get user from session
                webLog.setUserName(userModel.getUsername());
                WebLoggerFacade.insertWebLog(webLog);

            }
        } catch (Exception e) {
            Utility.showErrorMessage(null, "addItem.failure", " Element", element.getDisplayName());
        }
        return returnPage;
    }

    public String cancel() {
        return "DWHElementList.xhtml?faces-redirect=true";
    }

    public String updateElement() {
        CommonLogger.businessLogger.info("Starting updateElement");
        String returnPage = null;
        try {
            if ((noEmptyFields() && isNotRedundant())) {
                CommonLogger.businessLogger.info("Element validated successfully ");
                DWHElementFacade facade = new DWHElementFacade();
                facade.updateDwhElement(element);
//                CommonLogger.businessLogger.info("Element " + element.getName() + "has been Updated successfully");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " Element has been Added Successfully")
                        .put(GeneralConstants.StructuredLogKeys.ELEMENT_NAME, element.getName()).build());
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.getSessionMap().put(Constants.MANAGEMENT_MODE, Constants.EDIT_MODE);
                ec.getSessionMap().put(Constants.EDIT_MODEL_KEY, element.getDisplayName());
                returnPage = "DWHElementList.xhtml?faces-redirect=true";
                WebLogModel webLog = new WebLogModel();
                webLog.setOperationName("Update DWHElement");
                webLog.setPageName("DWHElementManage");
                webLog.setStringBefore(backupElement.toString());
                webLog.setStringAfter(element.toString());
                //get user from session
                webLog.setUserName(userModel.getUsername());
                WebLoggerFacade.insertWebLog(webLog);
            }
        } catch (Exception e) {
            Utility.showErrorMessage(null, "editItem.failure", " Element", element.getDisplayName());
        }
        return returnPage;
    }

    public void dataTypeSelectionListener() {

        try {
            changeVisibleColumnName(element);
        } catch (Exception e) {
            Utility.showErrorMessage(null, "general.error", e.getMessage());

        }
    }

    public void changeVisibleColumnName(DWHElementWebModel modelToConsider) {
        try {
            setColumnNames();
            if (modelToConsider.getDataTypeId() == 0) {
                visibleColumnNames = new ArrayList<>();
            }
            if (modelToConsider.getDataTypeId() == Defines.DWHELEMENT_DATA_TYPES.DATE) {
                visibleColumnNames = dates;

            } else if (modelToConsider.getDataTypeId() == Defines.DWHELEMENT_DATA_TYPES.NUMERIC) {
                visibleColumnNames = numbers;
            } else if (modelToConsider.getDataTypeId() == Defines.DWHELEMENT_DATA_TYPES.STRING) {
                visibleColumnNames = strings;
            }

            getVisibleColumnNames().removeAll(usedColumns);
            // getVisibleColumnNames().add(element.getDwhName());
        } catch (Exception e) {
            Utility.showErrorMessage(null, "general.error", e.getMessage());
        }
    }

    public void displayTypeSelection() {
        try {
            if (element.getDisplayTypeId() == Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION) {
                showMultiSelection = true;
                // visibleColumnNames = DBStruct.DWH_CUSTOMERS.STRINGS;
                if (element.getMultiSelectionValues() == null || element.getMultiSelectionValues().isEmpty()) {
                    if (element.getMultiSelectionValues() == null) {
                        element.setMultiSelectionValues(new ArrayList<DWHElementValueModel>());
                    }
                    element.getMultiSelectionValues().add(new DWHElementValueModel());
                }
            } else {
                //element.setMultiSelectionValues(null);
                showMultiSelection = false;
            }

        } catch (Exception e) {
            Utility.showErrorMessage(null, "general.error", e.getMessage());
        }
    }

//    public void multiSelectionEditListener() {
//        try {
//            if (editMode) {
//                element.setMultiSelectionValuesChanged(true);
//            }
//        } catch (Exception e) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Unkonw Error"));
//        }
//    }
    public String removeMultiSelectionOption(DWHElementValueModel value) {
        CommonLogger.businessLogger.info("Starting removeMultiSelectionOption");
        try {
            DWHElementFacade facade = new DWHElementFacade();

            facade.deleteMultiSelectionVale(value);

            if (element.getMultiSelectionValues() != null && !element.getMultiSelectionValues().isEmpty()) {
                if (element.getMultiSelectionValues().size() > 0) {
                    element.getMultiSelectionValues().remove(value);
                }
            }
//            CommonLogger.businessLogger.info("MultiSelection Value =>" + value.getValueLabel() + " has been removed successfully");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " MaltiSelection value has been removed Successfully")
                    .put(GeneralConstants.StructuredLogKeys.VALUE, value.getValueLabel()).build());
            return null;
        } catch (CommonException ex) {
            if (ex.getErrorCode().equalsIgnoreCase(ErrorCodes.DWH_ELEMENT_LOV_IN_USE)) {
                Utility.showErrorMessage(null, "deleteItem.failure", " MultiSelection Value ", value.getValueLabel() + " ,because It Is Used ");

            } else {
                Utility.showErrorMessage(null, "deleteItem.failure", " MultiSelection Value ", value.getValueLabel() + " , Unkonw Error ");
            }
        } catch (Exception e) {
            Utility.showErrorMessage(null, "deleteItem.failure", " MultiSelection Value ", value.getValueLabel() + " , Unkonw Error ");
        }
        return null;
    }

    public void addNewMultiSelectionOption() {
        try {
            if (element.getMultiSelectionValues() == null) {
                element.setMultiSelectionValues(new ArrayList<DWHElementValueModel>());
            }
            int size = element.getMultiSelectionValues().size();
            if (size != 0) {
                DWHElementValueModel lastUsedValue = element.getMultiSelectionValues().get(size - 1);
                if (lastUsedValue != null && lastUsedValue.getValueLabel() != null && !lastUsedValue.getValueLabel().equals("")) {
                    element.getMultiSelectionValues().add(new DWHElementValueModel());
                } else {
                    Utility.showErrorMessage(null, "addItem.failure", " MultiSelection Value ", " , Please fill the existing values first ");

                }
            } else {
                element.getMultiSelectionValues().add(new DWHElementValueModel());
            }
        } catch (Exception e) {
            Utility.showErrorMessage(null, "addItem.failure", " MultiSelection Value ", " , Unkonw Error ");
            CommonLogger.errorLogger.error("Error adding new multiselection option", e);

        }
    }

    private boolean noEmptyFields() {
        if (element.getName() != null && !element.getName().trim().equals("")
                && element.getDataTypeId() != 0
                && element.getDwhName() != null && !element.getDwhName().equals("0") && !element.getDwhName().trim().equals("")
                && element.getDisplayName() != null && !element.getDisplayName().trim().equals("")
                && element.getFileIndex() != 0) {

            if (element.getDescription().length() > 200) {
                Utility.showErrorMessage(null, "field.exceedSize", "Description", "200");
                return false;
            }

            if (element.getDisplayTypeId() == Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION) {
                if (element.getMultiSelectionValues().isEmpty()
                        && element.getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE)
                        && element.getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID)) {
                    Utility.showErrorMessage(null, "multiSelection.missedValues");

                    return false;
                } else {
                    for (DWHElementValueModel value : element.getMultiSelectionValues()) {
                        if (value.getValueLabel() == null || value.getValueLabel().trim().equals("")) {
                            Utility.showErrorMessage(null, "multiSelection.emptyValues");

                            return false;
                        }
                    }
                }
            } else if (element.getDisplayTypeId() == 0) {
                Utility.showErrorMessage(null, "fiel.pickfromList", "display type");

                return false;
            }
//            }
            return true;
        } else {
            Utility.showErrorMessage(null, Constants.MISSING_DATA);

            return false;
        }
    }

    private boolean isNotRedundant() {
        boolean isNotRedundant = true;
        DWHElementFacade ef = new DWHElementFacade();
        int removedIdx = 0;
        try {
            if (editMode) {
                removedIdx = ef.removeFromList(element, allElements);
            }
            for (DWHElementWebModel existingElement : allElements) {
                if (existingElement.getDwhName().equals(element.getDwhName().trim())) {
                    Utility.showErrorMessage(null, "anotherItem.hasSameValue", "Element", "DWH Column Name", element.getDwhName());

                    isNotRedundant = false;
                }
                if (existingElement.getName().equals(element.getName().trim())) {
                    Utility.showErrorMessage(null, "anotherItem.hasSameValue", "Element", "Attribute Name", element.getName());

                    isNotRedundant = false;
                }
                if (existingElement.getFileIndex() == element.getFileIndex()) {
                    Utility.showErrorMessage(null, "anotherItem.hasSameValue", "Element", "File Index", String.valueOf(element.getFileIndex()));

                    isNotRedundant = false;
                }
                if (existingElement.getDisplayName() != null && existingElement.getDisplayName().equals(element.getDisplayName().trim())) {
                    Utility.showErrorMessage(null, "anotherItem.hasSameValue", "Element", "Display Name", element.getDisplayName());

                    isNotRedundant = false;
                }
            }

        } catch (Exception e) {
            Utility.showErrorMessage(null, "validateItem.failure", "Element", " UNKOWN ERROR");

        }
        return isNotRedundant;
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

    public DWHElementWebModel getElement() {
        return element;
    }

    public void setElement(DWHElementWebModel element) {
        this.element = element;
    }

    public ArrayList<LookupModel> getAvailableDatatypes() {
        return availableDatatypes;
    }

    public ArrayList<LookupModel> getAvailableDisplayTypes() {
        return availableDisplayTypes;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    public ArrayList<String> getNumbers() {
        return numbers;
    }

    public ArrayList<String> getUsedColumns() {
        return usedColumns;
    }

    public ArrayList<String> getVisibleColumnNames() {
        return visibleColumnNames;
    }

    public boolean isShowMultiSelection() {
        return showMultiSelection;
    }

    public void setShowMultiSelection(boolean showMultiSelection) {
        this.showMultiSelection = showMultiSelection;
    }

}
