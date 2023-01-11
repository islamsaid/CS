/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.QueueModel;
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
import com.sun.org.apache.bcel.internal.generic.LOOKUPSWITCH;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.validation.ValidationException;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.picklist.PickList;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Yomna Naser
 */
@ManagedBean
@ViewScoped
public class QueueListBean implements Serializable {

    private ArrayList<QueueWebModel> queueList;
    private boolean createMode;
    ArrayList<LookupModel> operationStatus;
    private HashMap<Integer, LookupModel> queueTypes;
    private UserWebModel loggedInUser;
    private String queueType;

    @PostConstruct
    public void init() {
        try {
            getApplicationQueueList();
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + QueueListBean.class.getName() + ".init]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
    }

    public void getApplicationQueueList() {
        try {
            QueueFacade queueFacade = new QueueFacade();
            queueList = queueFacade.getApplicationQueues();
            operationStatus = SystemLookups.OPERATION_STATUS;
            setQueueTypes(SystemLookups.QUEUE_TYPE_LK);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueueList]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + QueueListBean.class.getName() + ".getApplicationQueueList]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public ArrayList<QueueWebModel> getQueueList() {
        return queueList;
    }

    public void setQueueList(ArrayList<QueueWebModel> queueList) {
        this.queueList = queueList;
    }

    public boolean isCreateMode() {
        return createMode;
    }

    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }

    public ArrayList<LookupModel> getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(ArrayList<LookupModel> operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String createApplicationQueueListener() {

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.CREATION_MODE);
        return "AppQueuePage";

    }

    public String editApplicationQueueListener(QueueWebModel queueModel) {

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.EDIT_MODE);
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, queueModel);
        return "AppQueuePage";
    }

    public String viewApplicationQueueListener(QueueWebModel queueModel) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.VIEW_MODE);
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, queueModel);
        return "AppQueuePage";
    }

    public void deleteApplicationQueue(QueueWebModel queueWebModel) {

        try {
            UserFacade userFacade = new UserFacade();
            loggedInUser = userFacade.convertModelToWebModel((UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));
            queueWebModel.setLastModifiedBy((int) loggedInUser.getId());
            QueueFacade queueFacade = new QueueFacade();
            queueFacade.deleteApplicationQueue(queueWebModel, loggedInUser);
            WebLogModel webLog = new WebLogModel();
            webLog.setOperationName("Delete Queue");
            webLog.setStringBefore(queueWebModel.toString());
            webLog.setStringAfter("");
            webLog.setPageName("Queue Management");
            //get user from session
            webLog.setUserName(loggedInUser.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            queueList = queueFacade.getApplicationQueues();
            Utility.showInfoMessage(null, Constants.ITEM_DELETED, "Application Queue", queueWebModel.getAppName());
//            CommonLogger.businessLogger.info("Queue Management Page | Deleted Queue " + queueWebModel.getAppName());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Queue Management Page, Queue Deleted")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueWebModel.getAppName()).build());
            RequestContext.getCurrentInstance().update("errors");
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:queusTable");
            if (dataTable != null) {
                dataTable.reset();
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteApplicationQueue]", ex);
            if (ex.getErrorCode() == ErrorCodes.INTEGRITY_CONSTRAINT_ERROR) {
                Utility.showErrorMessage(null, ex.getErrorCode(), ex.getErrorMsg(), "while deleting Application Queue " + queueWebModel.getAppName());
            } else {
                Utility.showErrorMessage(null, ex.getErrorCode(), "in deleting Application Queue" + queueWebModel.getAppName());
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + QueueListBean.class.getName() + ".deleteApplicationQueue]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public String approveApplicationQueue(QueueWebModel queueModel) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, queueModel);
        return "AppQueuePage";
    }

    /**
     * @return the queueTypes
     */
    public HashMap<Integer, LookupModel> getQueueTypes() {
        return queueTypes;
    }

    /**
     * @param queueTypes the queueTypes to set
     */
    public void setQueueTypes(HashMap<Integer, LookupModel> queueTypes) {
        this.queueTypes = queueTypes;
    }

    /**
     * @return the queueType
     */
    public String getQueueType() {
        return "Sender Engine Queue";
    }

    /**
     * @param queueType the queueType to set
     */
    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

}
