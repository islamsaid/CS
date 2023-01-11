/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.ReportsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.calendar.Calendar;

@ViewScoped
@ManagedBean(name = "noRecievedSMSPerWM")
public class NoRecievedSMSPerWMBean implements Serializable {

    private ReportsModel reportsModel;
    private ArrayList<ReportsModel> reports;

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
                if(reports!=null){
                    reports.clear();
                }
                reportsModel.setCount(mainService.getRecievedSMSCountPerCustomer(reportsModel));
                if (reportsModel.getCount() != 0) {
                    reports = new ArrayList<>();
                    reports.add(reportsModel);
                } else {
                        reports = null;
                    }
                }
            
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + NoRecievedSMSPerWMBean.class.getName() + ".retrieveCountOfReciecvedSMS]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), "in generating Report");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + NoRecievedSMSPerWMBean.class.getName() + ".retrieveCountOfReciecvedSMS]", e);
            Utility.showErrorMessage(null, Constants.PAGE_INIT_FAILURE);
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

            String msisdn = (String) value;
            Date fromDate = (Date) fromDateCalendar.getValue();
            Date toDate = (Date) toDateCalendar.getValue();

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
