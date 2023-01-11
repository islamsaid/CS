/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.utils;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.defines.Defines;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author Zain Al-Abedin
 */
public class Utility {

    public static ArrayList<SelectItem> lookupsToSelectItems(ArrayList<LookupModel> lookups) throws CommonException {
        ArrayList<SelectItem> selectItems = null;
        try {
            if (lookups != null) {
                selectItems = new ArrayList<SelectItem>();
                for (LookupModel lk : lookups) {
                    SelectItem si = new SelectItem(lk.getId(), lk.getLable());
                    if (lk.getDescription() != null && !lk.getDescription().equals("")) {
                        si.setDescription(lk.getDescription());
                    }
                    selectItems.add(si);
                }
            }
        } catch (Exception e) {
            throw new CommonException("Failed to convert list of lookups to select items", ErrorCodes.UNKOWN_ERROR);
        }
        return selectItems;
    }

    public static String getErrorMessage(String error) {
        String errorMessage = Defines.ERROR_BUNDLE.getString(error);
        return errorMessage;
    }

    //used in cases like : Queue abc has been added successfully
    //messageKey= added msg key
    //values = Queue, abc
    public static void showInfoMessage(String component, String messageKey, String... values) {
        ResourceBundle messageBundle = Defines.RESOURCES_BUNDLE;

        String infoMsg = MessageFormat.format(messageBundle.getString(messageKey), (Object[]) values);

        FacesMessage infoMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                infoMsg, infoMsg);
        FacesContext.getCurrentInstance().addMessage(component, infoMessage);
    }

    public static void showErrorMessage(String component, String messageKey, String... values) {
        ResourceBundle messageBundle = Defines.RESOURCES_BUNDLE;
        String dError = MessageFormat.format(messageBundle.getString(messageKey), (Object[]) values);
        FacesMessage errorMessage = new FacesMessage(
                FacesMessage.SEVERITY_ERROR, dError, dError);
        FacesContext.getCurrentInstance().addMessage(component, errorMessage);
    }

    public static void showInfoMessage(String component, String messageKey) {
        ResourceBundle messageBundle = Defines.RESOURCES_BUNDLE;
        String infoMsg = messageBundle.getString(messageKey);
        FacesMessage infoMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                infoMsg, infoMsg);
        FacesContext.getCurrentInstance().addMessage(component, infoMessage);
    }

    public static void showErrorMessage(String component, String messageKey) {
        ResourceBundle messageBundle = Defines.RESOURCES_BUNDLE;
        String dError = messageBundle.getString(messageKey);
        FacesMessage errorMessage = new FacesMessage(
                FacesMessage.SEVERITY_ERROR, dError, dError);
        FacesContext.getCurrentInstance().addMessage(component, errorMessage);
    }

    //2595
    public static void showWarningMessage(String component, String messageKey, String... values) {
        ResourceBundle messageBundle = Defines.RESOURCES_BUNDLE;
        String dError = MessageFormat.format(messageBundle.getString(messageKey), (Object[]) values);
        FacesMessage errorMessage = new FacesMessage(
                FacesMessage.SEVERITY_WARN, dError, dError);
        FacesContext.getCurrentInstance().addMessage(component, errorMessage);
    }
    
    public static Object getSessionVarFromContext(String varKey) throws CommonException {
        Object obj = null;
        try {
            Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            if (sessionMap != null) {
                obj = sessionMap.get(varKey);
                if (obj == null) {
//                    LBALogger.printErrorLog("Session var " + varKey + " not found!");
                }
            } else {
                throw new CommonException("Session var is empty", ErrorCodes.UNKOWN_ERROR);
            }
        } catch (Exception e) {
            throw new CommonException("Failed to get session var from context", ErrorCodes.UNKOWN_ERROR);
        }
        return obj;
    }

}
