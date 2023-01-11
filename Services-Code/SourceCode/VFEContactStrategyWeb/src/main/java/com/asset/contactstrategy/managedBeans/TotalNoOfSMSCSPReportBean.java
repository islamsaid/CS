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
import javax.faces.event.ComponentSystemEvent;
import org.primefaces.component.calendar.Calendar;

@ManagedBean(name = "totalNoOfSMSCSP")
@ViewScoped
public class TotalNoOfSMSCSPReportBean implements Serializable {

    private ReportsModel reportsModel;
    private int count;
    private ArrayList<ReportsModel> reports;

    public void retrieveCountOfReciecvedToSMSCSP() {
        try {
            if (FacesContext.getCurrentInstance().getMessageList().size() == 0) {
                MainService mainService = new MainService();
                reportsModel.setCount(mainService.recievedSMSByCSPCount(reportsModel));
                if (reportsModel.getCount() != 0) {
                    reports = new ArrayList<>();
                    reports.add(reportsModel);
                } else {
                    reports = null;
                }
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + TotalNoOfSMSCSPReportBean.class.getName() + ".retrieveCountOfReciecvedToSMSCSP]", ex);
            Utility.showErrorMessage(null, ex.getErrorCode(), "in generating Report");
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + TotalNoOfSMSCSPReportBean.class.getName() + ".retrieveCountOfReciecvedToSMSCSP]", e);
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

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    public void validateReq(ComponentSystemEvent event) {

        FacesContext fc = FacesContext.getCurrentInstance();
        UIComponent components = event.getComponent();

        Calendar fromDateCal = (Calendar) components.findComponent("fromDateID");
        Calendar toDateCal = (Calendar) components.findComponent("toDateID");

        Date fromDate = (Date) fromDateCal.getValue();
        Date toDate = (Date) toDateCal.getValue();

        boolean validationFailed = false;
        if (fromDate == null) {
            validationFailed = true;
        }
        if (toDate == null) {
            validationFailed = true;
        }

        if ((toDate!=null)&&(fromDate!=null)&&(toDate.before(fromDate))) {
            Utility.showErrorMessage(null, Constants.INVALID_DATE);
        }
        if (validationFailed) {
            reports = new ArrayList<>();
            Utility.showErrorMessage(null, Constants.MISSING_DATA);
            return;
        }
    }

    /**
     * @return the reports
     */
    public ArrayList<ReportsModel> getReports() {
        return reports;
    }

    /**
     * @param reports the reports to set
     */
    public void setReports(ArrayList<ReportsModel> reports) {
        this.reports = reports;
    }

}
