/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.MenuModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.facade.MenuFacade;
import com.asset.contactstrategy.facade.UserFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.UserWebModel;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Yomna Naser
 */
@ManagedBean
@ViewScoped
public class HomeBean implements Serializable{

    private ArrayList<MenuModel> menuList;
    private MenuFacade menuFacade;
    private UserFacade userFacade;
    private UserWebModel loggedInUser;

    @PostConstruct
    public void init()  {
        try {
            userFacade = new UserFacade();
            loggedInUser = userFacade.convertModelToWebModel((UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE));
            menuFacade = new MenuFacade();
            menuList = menuFacade.getMenuList(loggedInUser.getUserType());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueueList]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + HomeBean.class.getName() + ".getApplicationQueueList]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    public ArrayList<MenuModel> getMenuList() {
        return menuList;
    }

    public void setMenuList(ArrayList<MenuModel> menuList) {
        this.menuList = menuList;
    }

    public UserWebModel getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UserWebModel loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
    
    public String navigateToPage(MenuModel menuModel){
        //add selected menu id to session
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put(Constants.ACTIVE_MENU, menuModel.getId());
        return menuModel.getPageURL()+"?faces-redirect=true";
    }
    public String navigateToPage(){
        //add selected menu id to session
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put(Constants.ACTIVE_MENU, 0);
        return "Home";
    }
    public String logoutUser() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueueList]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
        return "Login?faces-redirect=true";
    }
}
