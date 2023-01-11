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
import com.asset.contactstrategy.facade.CampaignFacade;
import com.asset.contactstrategy.facade.WebLoggerFacade;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import com.asset.contactstrategy.webmodels.CampaignWebModel;
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
 * @author rania.magdy
 */
@ManagedBean(name = "campaignBean")
@ViewScoped
public class CampaignBean implements Serializable {

    ArrayList<CampaignWebModel> campaignList;
    private UserModel userModel;
    ArrayList<LookupModel> operationStatus;

    @PostConstruct
    public void init() {
        String methodName = "init";
        try {
            userModel = (UserModel) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(com.asset.contactstrategy.defines.Defines.USER_SESSION_MODULE);

//        userModel.setUserType(GeneralConstants.USER_TYPE_BUSINESS_VALUE);
//        userModel.setId(1);
            operationStatus = SystemLookups.OPERATION_STATUS;
            getCampaignsList();
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignBean.class.getName() + "." + methodName + "]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
        }
    }

    public void getCampaignsList() {
        try {
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            campaignList = campaignFacade.getCampaigns();

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getCampaignList]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode());
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignBean.class.getName() + ".getCampaignList]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public ArrayList<CampaignWebModel> getCampaignList() {
        return campaignList;
    }

    public void setCampaignList(ArrayList<CampaignWebModel> campaignList) {
        this.campaignList = campaignList;
    }

    public ArrayList<LookupModel> getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(ArrayList<LookupModel> operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String goToCreatePage() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.CREATION_MODE);
        return "CreateCampaign.xhtml";
    }

    public String goToEditPage(CampaignWebModel receivedCampaign) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, receivedCampaign);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.EDIT_MODE);
        return "CreateCampaign.xhtml";
    }

    public String goToViewPage(CampaignWebModel receivedCampaign) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, receivedCampaign);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.VIEW_MODE);
        return "CreateCampaign.xhtml";
    }

    public String goToApprovePage(CampaignWebModel receivedCampaign) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put(Constants.EDIT_MODEL_KEY, receivedCampaign);
        ec.getRequestMap().put(Constants.MANAGEMENT_MODE, Constants.APPROVAL_MODE);
        return "CreateCampaign.xhtml";
    }

    public void resumeCampaign(CampaignWebModel receivedCampaign) {
        try {
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "Start : resumeCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start resumeCampaign")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, CampaignBean.class.getName()).build());
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            receivedCampaign.setCampaignStatus(GeneralConstants.CAMPAIGN_STATUS_RESUMED_VALUE);
            campaignFacade.changeCampaignStatus(receivedCampaign);
            init();
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "End : resumeCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End resumeCampaign")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, CampaignBean.class.getName()).build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [resumeCampaign]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in resuming Campaign");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignBean.class.getName() + ".resumeCampaign]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }

    }

    public void stopCampaign(CampaignWebModel receivedCampaign) {
        try {
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "Start : stopCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start stopCampaign")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, CampaignBean.class.getName()).build());
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            receivedCampaign.setCampaignStatus(GeneralConstants.CAMPAIGN_STATUS_STOPPED_VALUE);
            campaignFacade.changeCampaignStatus(receivedCampaign);
            init();
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "End : stopCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End stopCampaign")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, CampaignBean.class.getName()).build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [stopCampaign]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in stopping Campaign");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignBean.class.getName() + ".stopCampaign]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public void pauseCampaign(CampaignWebModel receivedCampaign) {
        try {
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "Start : pauseCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start pauseCampaign")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, CampaignBean.class.getName()).build());
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            receivedCampaign.setCampaignStatus(GeneralConstants.CAMPAIGN_STATUS_PAUSED_VALUE);
            campaignFacade.changeCampaignStatus(receivedCampaign);
            init();
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "End : pauseCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End stopCampaign")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, CampaignBean.class.getName()).build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [pauseCampaign]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in pausing Campaign");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignBean.class.getName() + ".pauseCampaign]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
    }

    public String deleteCampaign(CampaignWebModel receivedCampaign) {
        try {
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "Start : deleteCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start deleteCampaign")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, CampaignBean.class.getName()).build());
            CampaignFacade campaignFacade = new CampaignFacade(userModel);
            campaignFacade.deleteCampaign(receivedCampaign);
            init();
            Utility.showInfoMessage(null, Constants.ITEM_DELETED, "Campaign", receivedCampaign.getCampaignName());

            WebLogModel webLog = new WebLogModel();
            webLog.setOperationName("Delete Campaign");
            webLog.setPageName("Campaign List");
            webLog.setStringBefore(receivedCampaign.toString());
            webLog.setStringAfter(null);
            //get user from session
            webLog.setUserName(userModel.getUsername());
            WebLoggerFacade.insertWebLog(webLog);
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:campaignTable");
            if (dataTable != null) {
                dataTable.reset();
            }
//            CommonLogger.businessLogger.info(CampaignBean.class.getName() + " || " + "End : deleteCampaign() ");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End deleteCampaign")
                    .put(GeneralConstants.StructuredLogKeys.BEAN_NAME, CampaignBean.class.getName()).build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteCampaign]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), " in deleting Campaign");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CampaignBean.class.getName() + ".deleteCampaign]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR, " ");
        }
        return null;
    }

    public String retrieveStartVisibility(CampaignWebModel campaign) {
        if (!campaign.getStatusName().equals(GeneralConstants.STATUS_PENDING_FOR_DELETION)) {
            return "visible";
        } else {
            return "hidden";
        }
    }

    public String retrievePauseVisibility(CampaignWebModel campaign) {
        if (!campaign.getStatusName().equals(GeneralConstants.STATUS_PENDING_FOR_DELETION)) {
            return "visible";
        } else {
            return "hidden";
        }
    }

    public String retrieveStopVisibility(CampaignWebModel campaign) {
        if (!campaign.getStatusName().equals(GeneralConstants.STATUS_PENDING_FOR_DELETION)) {
            return "visible";
        } else {
            return "hidden";
        }
    }
}
