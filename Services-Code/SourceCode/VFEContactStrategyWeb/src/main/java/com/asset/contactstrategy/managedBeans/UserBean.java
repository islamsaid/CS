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
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
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
 * @author Mostafa Fouda
 */
@ManagedBean(name = "userBean")
@ViewScoped
public class UserBean implements Serializable {

    private ArrayList<UserWebModel> usersList;
    private UserWebModel selectedUserWebModel;
    private UserModel userModel;

    @PostConstruct
    public void init() {
        //TO DO try 
        try {
            userModel = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE);
        } catch (Exception e) {
            userModel = new UserModel();
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [init User]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
        getUsersModelsList();
    }

    public ArrayList<UserWebModel> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<UserWebModel> usersList) {
        this.usersList = usersList;
    }

    public void getUsersModelsList() {
        try {
            UserFacade userFacade = new UserFacade();
            usersList = userFacade.getUsers();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getUsersList]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + UserBean.class.getName() + ".getUsersModelsList]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public String goToCreatePage() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.CREATION_MODE);
        //To Do
        return "UserManagementPage.xhtml";
    }

    public String goToEditPage(UserWebModel receiveduserWebModel) {
        selectedUserWebModel = receiveduserWebModel;
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, receiveduserWebModel);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.EDIT_MODE);
        //To Do
        return "UserManagementPage.xhtml";
    }

    public String goToViewPage(UserWebModel receiveduserWebModel) {
        selectedUserWebModel = receiveduserWebModel;
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, receiveduserWebModel);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.VIEW_MODE);
        //To Do
        return "UserManagementPage.xhtml";
    }

    public String deleteUser(UserWebModel receivedUser) {
        try {
//            CommonLogger.businessLogger.info(UserBean.class.getName() + " || " + "Start : deleteUser() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start DeleteUser()")
                    .build());
            UserFacade userFacade = new UserFacade();
            userFacade.deleteUser(receivedUser);
            init();
            Utility.showInfoMessage(null, Constants.ITEM_DELETED, "User", receivedUser.getUsername());

            WebLogModel webLog = new WebLogModel();
            webLog.setOperationName("Delete User");
            webLog.setPageName("User List");
            webLog.setStringBefore(receivedUser.toString());
            webLog.setStringAfter(null);
            webLog.setUserName(userModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:usersTable");
            if (dataTable != null) {
                dataTable.reset();
            }
//            CommonLogger.businessLogger.info(UserBean.class.getName() + " || " + "End : deleteUser() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End DeleteUser()")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteUser]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in deleting User");
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteUser]" + ex);
            Utility.showErrorMessage(null, Constants.DELETE_ITEM_FAILURE, " User", userModel.getUsername());
        }
        return null;
    }
}
