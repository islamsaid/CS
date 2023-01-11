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
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.ldap.common.LDAPIntegration;
import com.asset.ldap.model.LdapUserModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.picklist.PickList;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Yomna Naser
 */
@ManagedBean
@ViewScoped
public class UserManagementBean {

    private UserModel user;
    private boolean editMode;
    private boolean createMode;
    private boolean viewMode;
    private UserFacade userFacade;
    private HashMap<Integer, LookupModel> userTypes;
    private LookupModel userType;

    @PostConstruct
    public void init() {
        try {
            userTypes = new HashMap<Integer, LookupModel>();
            userTypes.putAll(SystemLookups.USERS_TYPE);
            userTypes.remove(GeneralConstants.USER_TYPE_ADMINISTRATOR_VALUE);
            userFacade = new UserFacade();
            Integer managementMode = (Integer) FacesContext.getCurrentInstance().getExternalContext().
                    getRequestMap().get(Constants.MANAGEMENT_MODE);
            if (managementMode.equals(Constants.CREATION_MODE)) {
                createMode = true;
                editMode = false;
                viewMode = false;
                user = new UserModel();
            } else if (managementMode.equals(Constants.EDIT_MODE)) {
                editMode = true;
                createMode = false;
                viewMode = false;
                user = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
                        .get(Constants.EDIT_MODEL_KEY);
                userType = userTypes.get(user.getUserType());
            } else {
                viewMode = true;
                createMode = false;
                editMode = false;
                user = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
                        .get(Constants.EDIT_MODEL_KEY);
                userType = userTypes.get(user.getUserType());
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [UserManagementBean]" + ex);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isCreateMode() {
        return createMode;
    }

    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }

    public boolean isViewMode() {
        return viewMode;
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;
    }

    public HashMap<Integer, LookupModel> getUserTypes() {
        return userTypes;
    }

    public void setUserTypes(HashMap<Integer, LookupModel> userTypes) {
        this.userTypes = userTypes;
    }

    public LookupModel getUserType() {
        return userType;
    }

    public void setUserType(LookupModel userType) {
        this.userType = userType;
    }

    public String saveUser() {
        if (createMode) {
            return createUser();
        } else {
            return editUser();
        }
    }

    public void validateReq(ComponentSystemEvent event) {

        FacesContext fc = FacesContext.getCurrentInstance();
        UIComponent components = event.getComponent();

        SelectOneMenu userTypeSel = (SelectOneMenu) components.findComponent("userTypeID");
        InputText usernameInput = (InputText) components.findComponent("usernameID");

        LookupModel userType = (LookupModel) userTypeSel.getValue();
        String username = (String) usernameInput.getValue();

        boolean validationFailed = false;
        if (userType == null) {
            validationFailed = true;
        }
        if (username == null || username.length() == 0) {
            validationFailed = true;
        }

        if (validationFailed) {
            Utility.showErrorMessage(null, Constants.MISSING_DATA);
            return;
        }
    }

    public String createUser() {
        try { // eslam.ahmed | 10-05-2020 | add user existance check 
            if (userFacade.isUserExist(user.getUsername())) {
                Utility.showErrorMessage(null, ErrorCodes.USER_ALREADY_EXIST);
                return "";
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createUser]" + ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), "Error in creating User , ", user.getUsername());
        }
        if (FacesContext.getCurrentInstance().getMessageList().size() == 0) {
            LdapUserModel retUser = null;
            if (Defines.LDAPLOOKUP.get(Defines.LDAP_AUTHENTICATION_PROPERTY_NAME).equals("true")) {
                try {
                    retUser = authenticateUser();
                } catch (Exception ex) {
//                    CommonLogger.businessLogger.debug("LDAP server returned exception[" + Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME) + "] for user [" + user.getUsername() + "]");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "LDAP Server returned Exception")
                            .put(GeneralConstants.StructuredLogKeys.SERVER_NAME, Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME))
                            .put(GeneralConstants.StructuredLogKeys.USER_NAME, user.getUsername()).build());
                    CommonLogger.errorLogger.error("LDAP server returned exception" + ex);
                    //FacesContext.getCurrentInstance().addMessage("message", new FacesMessage("LDAP server returned exception", null));
                    Utility.showErrorMessage(null, ErrorCodes.LOGIN_LDAB_EXCEPTION);
                    return "";
                }
                if (retUser == null) {
//                    CommonLogger.businessLogger.debug("LDAP server timed out[" + Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME) + "] for user [" + user.getUsername() + "]");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "LDAP Server Timed Out Exception")
                            .put(GeneralConstants.StructuredLogKeys.SERVER_NAME, Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME))
                            .put(GeneralConstants.StructuredLogKeys.USER_NAME, user.getUsername()).build());
                    //FacesContext.getCurrentInstance().addMessage("message", new FacesMessage("LDAP server timed out", null));
                    Utility.showErrorMessage(null, ErrorCodes.LOGIN_LDAB_TIMEOUT);
                    return "";
                } else {
//                    CommonLogger.businessLogger.debug("LDAP server logged in[" + Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME) + "] for user [" + user.getUsername() + "]");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "LDAP Server Logged In Exception")
                            .put(GeneralConstants.StructuredLogKeys.SERVER_NAME, Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME))
                            .put(GeneralConstants.StructuredLogKeys.USER_NAME, user.getUsername()).build());
                }
            }
            try {
                user.setUserType(userType.getId());
                userFacade.createUser(user);
                Utility.showInfoMessage(null, Constants.ITEM_ADDED, "User", user.getUsername());
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                return "UsersList.xhtml?faces-redirect=true";
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createUser]" + ex);
                Utility.showErrorMessage(null, ex.getErrorCode(), "Error in creating User , ", user.getUsername());
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createUser]" + ex);
                Utility.showErrorMessage(null, Constants.ADD_ITEM_FAILURE, " User", user.getUsername());
            }
        }
        return null;

    }

    public String editUser() {

        if (FacesContext.getCurrentInstance().getMessageList().size() == 0) {

            try {
                user.setUserType(userType.getId());
                userFacade.editUser(user);
                Utility.showInfoMessage(null, Constants.ITEM_EDITED, "User", user.getUsername());
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                return "UsersList.xhtml?faces-redirect=true";
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editUser]" + ex);
                Utility.showErrorMessage(null, ex.getErrorCode(), "in editing User");
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editUser]" + ex);
                Utility.showErrorMessage(null, Constants.EDIT_ITME_FAILURE, " User", user.getUsername());
            }
        }
        return null;

    }

    public LdapUserModel authenticateUser() throws Exception {
        LDAPIntegration ldapIntegration = new LDAPIntegration(Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME), Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_DN_PROPERTY_NAME));
        LdapUserModel logedinUser = ldapIntegration.authenticateUser(user.getUsername(), "", Defines.LDAPLOOKUP.get(Defines.LDAP_USERS_SB_PROPERTY_NAME));
        return logedinUser;
    }

}
