/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.models.ReportsViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author esmail.anbar
 */
@ManagedBean(name = "reportsListBean")
@ViewScoped
public class ReportsListBean {
    
    public ArrayList<ReportsViewModel> getReportsList()
    {
        return SystemLookups.REPORTS_LIST;
    }
    
}
