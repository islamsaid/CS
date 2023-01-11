/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.ReportsModel;
import com.asset.contactstrategy.common.service.MainService;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


@ViewScoped
@ManagedBean(name="numberOfRecievedSMSReport")
public class NumberOfRecievedSMSReportBean implements Serializable{ 

     private ReportsModel reportsModel;
     private int count;

      public void retrieveCountOfReciecvedSMS() {
          try {
              MainService mainService = new MainService();
              count = mainService.getRecievedSMSCountPerCustomer(reportsModel);
              
          } catch (CommonException ex) {
              Logger.getLogger(NumberOfRecievedSMSReportBean.class.getName()).log(Level.SEVERE, null, ex);
          }
      }

      
    public ReportsModel getReportsModel() {
        if(reportsModel == null){
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

}
