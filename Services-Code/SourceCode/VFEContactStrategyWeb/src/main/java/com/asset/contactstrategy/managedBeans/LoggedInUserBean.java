/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.utils.Utility;
import com.asset.ldap.common.LDAPIntegration;
import com.asset.ldap.model.LdapUserModel;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author osama.mabrouk
 */
@ManagedBean
@SessionScoped
public class LoggedInUserBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userName;
    private String userPassword;

    public String validateUsernamePassword() {
        LdapUserModel retUser = null;
        if (Defines.LDAPLOOKUP.get(Defines.LDAP_AUTHENTICATION_PROPERTY_NAME).equals("true")) {
            try {
                retUser = authenticateUser();
            } catch (Exception ex) {
//                CommonLogger.businessLogger.debug("LDAP server returned exception[" + Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME) + "] for user [" + userName + "]");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " LDAP Server returned Exception")
                        .put(GeneralConstants.StructuredLogKeys.SERVER_NAME, Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME))
                        .put(GeneralConstants.StructuredLogKeys.USER_NAME, userName).build());
                CommonLogger.errorLogger.error("LDAP server returned exception" + ex);
                //FacesContext.getCurrentInstance().addMessage("message", new FacesMessage("LDAP server returned exception", null));
                Utility.showErrorMessage(null, ErrorCodes.LOGIN_LDAB_EXCEPTION);
                return "Login";
            }
            if (retUser == null) {
//                CommonLogger.businessLogger.debug("LDAP server timed out[" + Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME) + "] for user [" + userName + "]");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " LDAP Server Timed Out")
                        .put(GeneralConstants.StructuredLogKeys.SERVER_NAME, Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME))
                        .put(GeneralConstants.StructuredLogKeys.USER_NAME, userName).build());
                //FacesContext.getCurrentInstance().addMessage("message", new FacesMessage("LDAP server timed out", null));
                Utility.showErrorMessage(null, ErrorCodes.LOGIN_LDAB_TIMEOUT);
                return "Login";
            } else {
//                CommonLogger.businessLogger.debug("LDAP server logged in[" + Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME) + "] for user [" + userName + "]");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " LDAP Server Logged In")
                        .put(GeneralConstants.StructuredLogKeys.SERVER_NAME, Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME))
                        .put(GeneralConstants.StructuredLogKeys.USER_NAME, userName).build());
            }
        }
        MainService mainService = new MainService();
        try {
            UserModel userModel = new UserModel();
            userModel = mainService.retrieveUser(userName);
            if (userModel == null || userModel.getUsername().equals("")) {
                Utility.showErrorMessage(null, ErrorCodes.GETTING_USER);
                return "Login";
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE, userModel);
        } catch (CommonException ex) {
//            CommonLogger.businessLogger.debug(ErrorCodes.GETTING_USER + " [" + userName + "]");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "User couldnot login")
                    .put(GeneralConstants.StructuredLogKeys.ERROR_CODE, ErrorCodes.GETTING_USER)
                    .put(GeneralConstants.StructuredLogKeys.USER_NAME, userName).build());
            CommonLogger.errorLogger.error("General exception" + ex);
            //FacesContext.getCurrentInstance().addMessage("message", new FacesMessage("LDAP server returned exception", null));
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
            return "Login";
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + LoggedInUserBean.class.getName() + ".validateUsernamePassword]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        return "Home";
    }

    public LdapUserModel authenticateUser() throws Exception {
        LDAPIntegration ldapIntegration = new LDAPIntegration(Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME), Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_DN_PROPERTY_NAME));
        LdapUserModel logedinUser = ldapIntegration.authenticateUser(userName, userPassword, Defines.LDAPLOOKUP.get(Defines.LDAP_USERS_SB_PROPERTY_NAME));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, " LDAP Server Logged In [return User: " + logedinUser +"]").put(GeneralConstants.StructuredLogKeys.SERVER_NAME, Defines.LDAPLOOKUP.get(Defines.LDAP_SERVER_URL_PROPERTY_NAME)).build());
        return logedinUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
