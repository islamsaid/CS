/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.converters;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author kerollos.asaad
 */
@FacesConverter(forClass = DWHElementValueModel.class, value = "valuesConverter")
public class ValuesConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String dwhElementValueModel) {
        String methodName = "getAsObject";
        DWHElementValueModel fvm = new DWHElementValueModel();
        String[] dwhElementValueExpandad = dwhElementValueModel.split("~");
        fvm.setValueId(Integer.parseInt(dwhElementValueExpandad[0]));
        fvm.setElementId(Integer.parseInt(dwhElementValueExpandad[1]));
        fvm.setValueLabel(dwhElementValueExpandad[2]);
        String viewId = fc.getViewRoot().getViewId();
        try {
            if (viewId.contains("SmsGroupEditView")) {
                List<DWHElementValueModel> filterValueModel
                        = (List<DWHElementValueModel>) fc.getApplication().evaluateExpressionGet(fc, "#{groupsEditViewBean.newFilter.dwhElementModel.multiSelectionValues}", List.class);
                int index = filterValueModel.indexOf(fvm);
                if (index != -1) {
                    fvm = filterValueModel.get(index);
                }
            } else if (viewId.contains("AdsGroupEditView")) {
                List<DWHElementValueModel> filterValueModel
                        = (List<DWHElementValueModel>) fc.getApplication().evaluateExpressionGet(fc, "#{adsGroupsEditViewBean.newFilter.dwhElementModel.multiSelectionValues}", List.class);
                int index = filterValueModel.indexOf(fvm);
                if (index != -1) {
                    fvm = filterValueModel.get(index);
                }
            }else if (viewId.contains("CreateCampaign")) {
                List<DWHElementValueModel> filterValueModel
                        = (List<DWHElementValueModel>) fc.getApplication().evaluateExpressionGet(fc, "#{campaignManagementBean.newFilter.dwhElementModel.multiSelectionValues}", List.class);
                int index = filterValueModel.indexOf(fvm);
                if (index != -1) {
                    fvm = filterValueModel.get(index);
                }
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(GroupTypeConverter.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        }
        return fvm;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof DWHElementValueModel) {
            DWHElementValueModel fvm = (DWHElementValueModel) o;
            return fvm.toString();
        }
        return "";
    }
}
