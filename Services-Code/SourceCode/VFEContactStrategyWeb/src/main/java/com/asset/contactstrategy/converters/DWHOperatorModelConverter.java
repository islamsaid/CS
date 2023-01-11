/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.converters;

import com.asset.contactstrategy.common.models.LookupModel;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author kerollos.asaad
 */
@FacesConverter(forClass = LookupModel.class, value = "dwhOperatorsConverter")
public class DWHOperatorModelConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String operatorValue) {
        String methodName = "getAsObject";
        LookupModel om = new LookupModel();
        String[] temp = operatorValue.split("~");
        om.setId(Integer.parseInt(temp[0]));
        om.setLable(temp[1]);
        String viewId = fc.getViewRoot().getViewId();
        try {
            if (viewId.contains("SmsGroupEditView")) {
                List<LookupModel> filterValueModel = (List<LookupModel>) fc.getApplication().evaluateExpressionGet(fc, "#{groupsEditViewBean.operators}", List.class);
                int index = filterValueModel.indexOf(om);
                if (index != -1) {
                    om = filterValueModel.get(index);
                }
            } else if (viewId.contains("AdsGroupEditView")) {
                List<LookupModel> filterValueModel = (List<LookupModel>) fc.getApplication().evaluateExpressionGet(fc, "#{adsGroupsEditViewBean.operators}", List.class);
                int index = filterValueModel.indexOf(om);
                if (index != -1) {
                    om = filterValueModel.get(index);
                }
            }else if (viewId.contains("CreateCampaign")) {
                List<LookupModel> filterValueModel = (List<LookupModel>) fc.getApplication().evaluateExpressionGet(fc, "#{campaignManagementBean.operators}", List.class);
                int index = filterValueModel.indexOf(om);
                if (index != -1) {
                    om = filterValueModel.get(index);
                }
            }
        } catch (Exception ex) {

        }
        return om;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o
    ) {
        if (o instanceof LookupModel) {
            LookupModel om = (LookupModel) o;
            return om.getId() + "~" + om.getLable();
        }
        return "";
    }
}
