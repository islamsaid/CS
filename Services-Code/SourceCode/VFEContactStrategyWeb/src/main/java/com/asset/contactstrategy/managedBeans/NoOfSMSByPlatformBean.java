/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.ReportsModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.selectonemenu.SelectOneMenu;

/**
 *
 * @author Ahmed Hisham
 */
@ManagedBean(name = "noOfSMSByPlatform")
@ViewScoped
public class NoOfSMSByPlatformBean {

    private ReportsModel reportsModel;
    private ArrayList<ServiceModel> services;
    private HashMap<Integer, String> messageStatus;
    private ArrayList<ReportsModel> reports;

    @PostConstruct
    public void init() {
        try {
            reportsModel = new ReportsModel();
            MainService mainService = new MainService();
            generateMessageStatusLK();
            services = mainService.getServices();

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + NoOfSMSByPlatformBean.class.getName() + ".init]", e);
            Utility.showErrorMessage(null, ErrorCodes.GENERAL_ERROR," ");
        }
    }

    public void generateMessageStatusLK() {
        messageStatus = new HashMap<>();
        messageStatus.put(Defines.VFE_CS_SMS_H_STATUS_LK.DELIVERED, "Delivered");
        messageStatus.put(Defines.VFE_CS_SMS_H_STATUS_LK.NOT_DELIVERED, "Not Delivered");
        messageStatus.put(Defines.VFE_CS_SMS_H_STATUS_LK.ENQUEUED, "Enqueued");
        messageStatus.put(Defines.VFE_CS_SMS_H_STATUS_LK.SENT_TO_SMSC, "Sent to SMSC");
        messageStatus.put(Defines.VFE_CS_SMS_H_STATUS_LK.TIMED_OUT, "Time Out");
        messageStatus.put(Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_CS_RULES, "Failed CS Rules");
        messageStatus.put(Defines.VFE_CS_SMS_H_STATUS_LK.EXPIRED, "Expired");
        messageStatus.put(Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE, "Failed Enqueue");
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
                reportsModel = mainService.recievedNoOfSMSByPlatform(reportsModel);
                if (reportsModel != null) {
                    reports = new ArrayList<>();
                    reports.add(reportsModel);
                } else {
                    reports = null;
                }

            }

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + NoOfSMSByPlatformBean.class.getName() + ".retrieveCountOfReciecvedSMS]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), "in generating Report");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + NoOfSMSByPlatformBean.class.getName() + ".retrieveCountOfReciecvedSMS]", e);
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

    public ArrayList<ReportsModel> getReports() {
        return reports;
    }

    public void setReports(ArrayList<ReportsModel> reports) {
        this.reports = reports;
    }

    public void requiredFieldsValidator(FacesContext context,
            UIComponent component, Object value) {

        if (component.getId().equals("MSISDN_ID")) {

            Calendar fromDateCalendar = (Calendar) component.getAttributes().get("fromDateAttr");
            Calendar toDateCalendar = (Calendar) component.getAttributes().get("toDateAttr");
            SelectOneMenu serviceNameSelect = (SelectOneMenu) component.getAttributes().get("serviceNameAttr");
            SelectOneMenu messageStatusSelect = (SelectOneMenu) component.getAttributes().get("messageStatusAttr");

            String msisdn = (String) value;
            Date fromDate = (Date) fromDateCalendar.getValue();
            Date toDate = (Date) toDateCalendar.getValue();
            String serviceName = (String) serviceNameSelect.getSubmittedValue();
            String messageStatus = (String) messageStatusSelect.getSubmittedValue();

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
            if (messageStatus == null || messageStatus.length() == 0) {
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

    /**
     * @return the messageStatus
     */
    public HashMap<Integer, String> getMessageStatus() {
        return messageStatus;
    }

    /**
     * @param messageStatus the messageStatus to set
     */
    public void setMessageStatus(HashMap<Integer, String> messageStatus) {
        this.messageStatus = messageStatus;
    }

}
