/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.ReportsModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.selectonemenu.SelectOneMenu;

@ViewScoped
@ManagedBean(name = "noOfSMSToSMSC")
public class NoOfSMSToSMSCBean implements Serializable {

    private ReportsModel reportsModel;
    private ArrayList<ServiceModel> services;
    private ArrayList<SMSCModel> smscNames;
    private ArrayList<ReportsModel> reports;

    @PostConstruct
    public void init() {
        try {

            reportsModel = new ReportsModel();
            reports = new ArrayList<ReportsModel>();
            MainService mainService = new MainService();
            services = mainService.getServices();
            smscNames = mainService.getSMSCs();
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + NoOfSMSToSMSCBean.class.getName() + ".init]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    public void retrieveCountOfReciecvedSMS() {
        try {
            String msisdnValue = null;
            if((reportsModel.getMsisdn()==null)||((reportsModel.getMsisdn()!=null)&&(reportsModel.getMsisdn().equals("")))){
                return;
            }
            try {//A) VALIDATE MSISDN
                //CSPhase1.5 | Esmail.Anbar | Updating Code to use new Validation Function
                msisdnValue = com.asset.contactstrategy.common.utils.Utility.validateMSISDNFormatSMPP(reportsModel.getMsisdn());
            } catch (CommonException invalidmsisdn) {
                CommonLogger.errorLogger.error(UploadAndParseFileBean.class.getName() + " || "
                        + "Caught invalid MSISDN with value" + reportsModel.getMsisdn());
            }
            if (msisdnValue == null) {
                Utility.showErrorMessage(null, Constants.INVALID_MSISDN);
                return;
            } else {
                reportsModel.setMsisdn(msisdnValue);
            }
            if (FacesContext.getCurrentInstance().getMessageList().size() == 0) {
                MainService mainService = new MainService();
                reportsModel = mainService.getNoOfSMSToSMSC(reportsModel);
                if (reportsModel != null) {
                    reports = new ArrayList<>();
                    reports.add(reportsModel);
                } else {
                    reports = null;
                }
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + NoOfSMSToSMSCBean.class.getName() + ".retrieveCountOfReciecvedSMS]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), "in generating Report");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + NoOfSMSToSMSCBean.class.getName() + ".retrieveCountOfReciecvedSMS]", e);
            Utility.showErrorMessage(null, Constants.RETRIEVE_REPORT);
        }
    }

    public ReportsModel getReportsModel() {
        if (reportsModel == null) {
            reportsModel = new ReportsModel();
        }
        return reportsModel;
    }

    public void setReportsModel(ReportsModel reportsModel) {
        this.reportsModel = reportsModel;
    }

    /**
     * @return the services
     */
    public ArrayList<ServiceModel> getServices() {
        return services;
    }

    /**
     * @param services the services to set
     */
    public void setServices(ArrayList<ServiceModel> services) {
        this.services = services;
    }

    /**
     * @return the smscNames
     */
    public ArrayList<SMSCModel> getSmscName() {
        return smscNames;
    }

    /**
     * @param smscName the smscNames to set
     */
    public void setSmscName(ArrayList<SMSCModel> smscName) {
        this.smscNames = smscName;
    }

    public void setReports(ArrayList<ReportsModel> reports) {
        this.reports = reports;
    }

    public ArrayList<ReportsModel> getReports() {
        return reports;
    }

    public void requiredFieldsValidator(FacesContext context,
            UIComponent component, Object value) {

        if (component.getId().equals("MSISDN_ID")) {

            Calendar fromDateCalendar = (Calendar) component.getAttributes().get("fromDateAttr");
            Calendar toDateCalendar = (Calendar) component.getAttributes().get("toDateAttr");
            SelectOneMenu serviceNameSelect = (SelectOneMenu) component.getAttributes().get("serviceNameAttr");
            SelectOneMenu smscNameSelect = (SelectOneMenu) component.getAttributes().get("smscNameAttr");

            String msisdn = (String) value;
            Date fromDate = (Date) fromDateCalendar.getValue();
            Date toDate = (Date) toDateCalendar.getValue();
            String serviceName = (String) serviceNameSelect.getSubmittedValue();
            String smscName = (String) smscNameSelect.getSubmittedValue();

            boolean validationFailed = false;
            if (msisdn == null || msisdn.length() == 0) {
                validationFailed = true;
            }
            if (fromDate == null) {
                validationFailed = true;
            }
            if (toDate == null) {
                validationFailed = true;
            }
            if (smscName == null || smscName.length() == 0) {
                validationFailed = true;
            }
            if (serviceName == null || serviceName.length() == 0) {
                validationFailed = true;
            }
            if (validationFailed) {
                reports = new ArrayList<>();
                Utility.showErrorMessage(null, Constants.MISSING_DATA);
                return;
            }
            if (toDate.before(fromDate)) {
                Utility.showErrorMessage(null, Constants.INVALID_DATE);
            }

        }

    }

}
