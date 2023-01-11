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
import com.asset.contactstrategy.facade.ServiceManagmentFacade;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.ServiceWebModel;
import com.asset.contactstrategy.webmodels.UserWebModel;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author Amal Magdy
 */
@ManagedBean
@ViewScoped
public class ServiceListBean implements Serializable {

    private ArrayList<ServiceWebModel> serviceList;
    private UserWebModel userWebModel;

    @PostConstruct
    public void init() {
        try {
            //TODO get user from model
            userWebModel = new UserWebModel();
            UserFacade userFacade = new UserFacade();
            userWebModel = userFacade.convertModelToWebModel((UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));
            getApplicationServiceList();

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueueListener]", ex);
            Utility.showErrorMessage(null, "pageInit.error");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ServiceListBean.class.getName() + ".createApplicationQueueListener]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }

    }

    public void getApplicationServiceList() throws CommonException {
        try {
            ServiceManagmentFacade serviceFacade = new ServiceManagmentFacade();
            serviceList = serviceFacade.getServices();
        } catch (CommonException ex) {
            throw ex;
        }
    }

    public ArrayList<ServiceWebModel> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<ServiceWebModel> serviceList) {
        this.serviceList = serviceList;
    }

    public UserWebModel getUserWebModel() {
        return userWebModel;
    }

    public void setUserWebModel(UserWebModel userWebModel) {
        this.userWebModel = userWebModel;
    }

    public String goToEditPage(ServiceWebModel serviceWebModel) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, serviceWebModel);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.EDIT_MODE);
        return "ServiceManagment.xhtml";
    }

    public String goToViewPage(ServiceWebModel serviceWebModel) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, serviceWebModel);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.VIEW_MODE);
        return "ServiceManagment.xhtml";
    }

    public String goToApprovalPage(ServiceWebModel serviceWebModel) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, serviceWebModel);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
        return "ServiceManagment.xhtml";
    }

    public String goToCreatePage() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.CREATION_MODE);
        return "ServiceManagment.xhtml";
    }

    public String deleteService(ServiceWebModel serviceWebModel) {
        try {
            WebLogModel webLog = new WebLogModel();
            ServiceManagmentFacade serviceManagmentFacade = new ServiceManagmentFacade();
            //TODO: handle delete parameters
            serviceManagmentFacade.deleteService(null, null, serviceWebModel, serviceWebModel.getWhiteListIPs(), userWebModel);
            init();
            Utility.showInfoMessage(null, Constants.ITEM_DELETED, "SERVICE", serviceWebModel.getServiceName());
            webLog.setOperationName("Delete Service");
            webLog.setStringBefore(serviceWebModel.toString());
            webLog.setStringAfter(null);
            webLog.setPageName("Service List");
            //get user from session
            webLog.setUserName(userWebModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);

            CommonLogger.businessLogger.info("Service List Page | Deleted Service " + serviceWebModel.getServiceName());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Service List Page Deleted")
                    .put(GeneralConstants.StructuredLogKeys.SERVICE_NAME, serviceWebModel.getServiceName()).build());
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:servicesList");
            if (dataTable != null) {
                dataTable.reset();
            }
            // return "ServiceList.xhtml?faces-redirect=true";
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  deleteService" + ex);
            if (ex.getErrorCode().equals(ErrorCodes.INTEGRITY_CONSTRAINT_ERROR)) {
                Utility.showErrorMessage(null, ex.getErrorCode(), " Campaigns ", " while deleting Service " + serviceWebModel.getServiceName());
            } else {
                Utility.showErrorMessage(null, ex.getErrorCode(), " in deleting Service");
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + ServiceListBean.class.getName() + ".deleteService]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        return null;
    }

}
