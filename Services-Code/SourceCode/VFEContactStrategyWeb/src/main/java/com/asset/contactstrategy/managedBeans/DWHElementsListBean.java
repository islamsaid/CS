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
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.facade.DWHElementFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.DWHElementWebModel;
import com.asset.contactstrategy.webmodels.QueueWebModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author Zain Al-Abedin
 */
@ManagedBean(name = "dwhElementsListBean")
@ViewScoped
public class DWHElementsListBean {

    private ArrayList<DWHElementWebModel> allElements;
    private DWHElementModel selectedElement;
    private String deleteDlgWidgetVar;
    private UserModel userModel;
    private static String CLASS_NAME = "com.asset.contactstrategy.managedBeans.DWHElementsListBean";

//    public DWHElementsListBean() {
//        CommonLogger.businessLogger.info(getBEAN_NAME());
//
//        try {
//            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//            Integer managementMode = (Integer) ec.getSessionMap().remove(Constants.MANAGEMENT_MODE);
//            String elementName = (String) ec.getSessionMap().remove(Constants.EDIT_MODEL_KEY);
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "hi", "hi"));
////            if (managementMode != null) {
////                if (managementMode.equals(Constants.CREATION_MODE)) {
// //                   Utility.showInfoMessage(null, "addItem.success", "Element ",elementName);
////                    //context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Element " + elementName + " added successfully", null));
////                } else if (managementMode.equals(Constants.EDIT_MODE)) {
////                    Utility.showInfoMessage(null, "editItem.success", "Element ",elementName);
////                    //context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Element " + elementName + " Updated successfully", null));
////                }
////            }
//            DWHElementFacade facade = new DWHElementFacade();
//            allElements = facade.loadAllElementsFromDb();
//            deleteDlgWidgetVar = "deleteElementConfirmDlg";
//        } catch (CommonException ex) {
//            CommonLogger.errorLogger.error(getBEAN_NAME() + " || " + ex.getErrorMsg());
//        }
//        CommonLogger.businessLogger.info(getBEAN_NAME() + " || " + "Retrieve DWHElements List size = " + getAllElements().size());
//
//    }
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
//            CommonLogger.businessLogger.info(getBEAN_NAME() + " Starting ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Bean")
                    .build());
            try {
                userModel = (UserModel) (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));

                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                Integer managementMode = (Integer) ec.getSessionMap().remove(Constants.MANAGEMENT_MODE);
                String elementName = (String) ec.getSessionMap().remove(Constants.EDIT_MODEL_KEY);
                if (managementMode != null) {
                    if (managementMode.equals(Constants.CREATION_MODE)) {
                        Utility.showInfoMessage(null, "addItem.success", "Element ", elementName);
                    } else if (managementMode.equals(Constants.EDIT_MODE)) {
                        Utility.showInfoMessage(null, "editItem.success", "Element ", elementName);
                    }
                }
                DWHElementFacade facade = new DWHElementFacade();
                allElements = facade.loadAllElementsFromDb();
                deleteDlgWidgetVar = "deleteElementConfirmDlg";

            } catch (CommonException ex) {
                Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
                CommonLogger.errorLogger.error(getBEAN_NAME() + " || " + ex.getErrorMsg());
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + DWHElementsListBean.class.getName() + ".init]", e);
                Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
            }
//            CommonLogger.businessLogger.info(getBEAN_NAME() + " || " + "Retrieve DWHElements List size = " + getAllElements().size());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Retrieve DWHElements List")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, getAllElements().size()).build());
        }
    }

    public String createDWHElementListener() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.CREATION_MODE);
        return "DWHElementManage.xhtml";
    }

    public String viewDWHElementListener(DWHElementWebModel element) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.VIEW_MODE);
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, element);
        return "DWHElementManage.xhtml";
    }

    public String editDWHElementListener(DWHElementWebModel element) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.EDIT_MODE);
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, element);
        return "DWHElementManage.xhtml";
    }

    public void deleteDWHElementListner(DWHElementWebModel element) {
        DWHElementFacade facade = new DWHElementFacade();
        try {
            facade.deleteElement(element);
            allElements = facade.loadAllElementsFromDb();
            Utility.showInfoMessage(null, "deleteItem.success", "Element ", element.getDisplayName());
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:elementsTable");
            if (dataTable != null) {
                dataTable.reset();
            }
            WebLogModel webLog = new WebLogModel();
            webLog.setOperationName("Delete DWHElement");
            webLog.setPageName("DWHElementList");
            webLog.setStringBefore(element.toString());
            webLog.setStringAfter(null);
            //get user from session
            webLog.setUserName(userModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteDWHElementListner]", ex);
            if (ex.getErrorCode().equals(ErrorCodes.INTEGRITY_CONSTRAINT_ERROR)) {
                Utility.showErrorMessage(null, Constants.DELETE_ITEM_FAILURE, " Element ", element.getDisplayName() + " , the item is already being used ");

            } else {
                Utility.showErrorMessage(null, Constants.DELETE_ITEM_FAILURE, " Element ", element.getDisplayName() + " , Unkonw Error ");
            }

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + DWHElementsListBean.class.getName() + ".deleteDWHElementListner]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    private static void handleServiceException(Exception e, String methodName) {
        CommonException campaignException = null;
        // Handle ContextualCampaignException 
        if (e instanceof CommonException) {
            campaignException = (CommonException) e;
        } // Handle SQL Exception 
        else if (e instanceof SQLException) {
            campaignException = new CommonException("Sql exception in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.ERROR_SQL);
        } // Handle other exception types
        else {
            campaignException = new CommonException("Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName + e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }

        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_LOW);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_WEB_INTERFACE);
        errorModel.setErrorCode(campaignException.getErrorCode());
        errorModel.setModuleName(CLASS_NAME);
        errorModel.setFunctionName(methodName);
        com.asset.contactstrategy.common.utils.Utility.sendMOMAlarem(errorModel);

    }

    public String test() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "hi", "hi"));
        return null;
    }

    public ArrayList<DWHElementWebModel> getAllElements() {
        return allElements;
    }

    public DWHElementModel getSelectedElement() {
        return selectedElement;
    }

    public void setSelectedElement(DWHElementModel selectedElement) {
        this.selectedElement = selectedElement;
    }

    public static String getBEAN_NAME() {
        return CLASS_NAME;
    }

    public static void setBEAN_NAME(String aBEAN_NAME) {
        CLASS_NAME = aBEAN_NAME;
    }

}
