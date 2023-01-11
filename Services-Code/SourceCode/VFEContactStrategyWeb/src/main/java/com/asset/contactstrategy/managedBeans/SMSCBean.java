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
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.facade.SMSCFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import java.io.Serializable;
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
 * @author rania.magdy
 */
@ManagedBean(name = "smscBean")
@ViewScoped
public class SMSCBean implements Serializable {

    private ArrayList<SMSCWebModel> smscList;
    private SMSCWebModel selectedSMSC;
    private UserModel userModel;
    ArrayList<LookupModel> operationStatus;

    @PostConstruct
    public void init() {
        try {
            userModel = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE);
//        userModel.setUserType(GeneralConstants.USER_TYPE_OPERATIONAL_VALUE);
//        userModel.setId(1);
            operationStatus = SystemLookups.OPERATION_STATUS;
            getSMSCList();
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSCBean.class.getName() + ".init]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
    }

    public void getSMSCList() {
        try {
            SMSCFacade smscFacade = new SMSCFacade(userModel);
            smscList = smscFacade.getSMSCs();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getSMSCList]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSCBean.class.getName() + ".getSMSCList]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public ArrayList<LookupModel> getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(ArrayList<LookupModel> operationStatus) {
        this.operationStatus = operationStatus;
    }

    public ArrayList<SMSCWebModel> getSmscList() {
        return smscList;
    }

    public void setSmscList(ArrayList<SMSCWebModel> smscList) {
        this.smscList = smscList;
    }

    public SMSCWebModel getSelectedSMSC() {
        return selectedSMSC;
    }

    public void setSelectedSMSC(SMSCWebModel selectedSMSC) {
        this.selectedSMSC = selectedSMSC;
    }

    public String goToEditPage(SMSCWebModel receivedSMSC) {
        selectedSMSC = receivedSMSC;
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, receivedSMSC);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.EDIT_MODE);
        return "CreateSMSC.xhtml";
    }

    public String goToViewPage(SMSCWebModel receivedSMSC) {
        selectedSMSC = receivedSMSC;
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, receivedSMSC);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.VIEW_MODE);
        return "CreateSMSC.xhtml";
    }

    public String goToCreatePage() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.CREATION_MODE);
        return "CreateSMSC.xhtml";
    }

    public String goToSmscApprovalPage(SMSCWebModel receivedSMSC) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, receivedSMSC);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
        return "CreateSMSC.xhtml";
    }

    public String deleteSMSC(SMSCWebModel receivedSMSC) {
        try {
//            CommonLogger.businessLogger.info(SMSCBean.class.getName() + " || " + "Start : deleteSMSC() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start deleteSMSC()")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, SMSCBean.class.getName()).build());
            SMSCFacade sMSCFacade = new SMSCFacade(userModel);
            sMSCFacade.deleteSMSC(receivedSMSC);
            init();
            Utility.showInfoMessage(null, Constants.ITEM_DELETED, "SMSC", receivedSMSC.getSMSCname());

            WebLogModel webLog = new WebLogModel();
            webLog.setOperationName("Delete SMSC");
            webLog.setPageName("SMSC List");
            webLog.setStringBefore(receivedSMSC.toString());
            webLog.setStringAfter(null);
            //get user from session
            webLog.setUserName(userModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:smscTable");
            if (dataTable != null) {
                dataTable.reset();
            }
//            CommonLogger.businessLogger.info(SMSCBean.class.getName() + " || " + "End : deleteSMSC() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End deleteSMSC()").build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteSMSC]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in deleting SMSC");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + SMSCBean.class.getName() + ".deleteSMSC]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        return null;
    }
}
