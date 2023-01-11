/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.converters;

import com.asset.contactstrategy.common.logger.CommonLogger;
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
@FacesConverter(forClass = LookupModel.class, value = "GroupTypeConverter")
public class GroupTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String operatorValue) {
        LookupModel om = new LookupModel();
        om.setLable(operatorValue);
        String viewId = fc.getViewRoot().getViewId();
        String methodName = "getAsObject";
        try {
            if (viewId.contains("SmsGroupEditView")) {
                List<LookupModel> filterValueModel = (List<LookupModel>) fc.getApplication().evaluateExpressionGet(fc, "#{groupsEditViewBean.groupTypesList}", List.class);
                int index = filterValueModel.indexOf(om);
                if (index != -1) {
                    om = filterValueModel.get(index);
                }
            } else if (viewId.contains("AdsGroupEditView")) {
                List<LookupModel> filterValueModel = (List<LookupModel>) fc.getApplication().evaluateExpressionGet(fc, "#{adsGroupsEditViewBean.groupTypesList}", List.class);
                int index = filterValueModel.indexOf(om);
                if (index != -1) {
                    om = filterValueModel.get(index);
                }
            }else if (viewId.contains("CreateCampaign")) {
                List<LookupModel> filterValueModel = (List<LookupModel>) fc.getApplication().evaluateExpressionGet(fc, "#{campaignManagementBean.filterTypesList}", List.class);
                int index = filterValueModel.indexOf(om);
                if (index != -1) {
                    om = filterValueModel.get(index);
                }
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(GroupTypeConverter.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        }
        return om;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof LookupModel) {
            LookupModel om = (LookupModel) o;
            return om.getLable();
        }
        return "";
    }
}
