/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.converters;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.webmodels.DWHElementWebModel;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author kerollos.asaad
 */
@FacesConverter(forClass = DWHElementWebModel.class, value = "dwhElementModelConverter")
public class DWHElementModelConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String displayName) {
        String methodName = "getAsObject";
        String[] temp = displayName.split("~");
        DWHElementWebModel em = new DWHElementWebModel();
        em.setDisplayName(temp[0]);
        em.setElementId(Long.parseLong(temp[1]));
        String viewId = fc.getViewRoot().getViewId();
        try {
            if (viewId.contains("SmsGroupEditView")) {
                List<DWHElementWebModel> element = (List<DWHElementWebModel>) fc.getApplication().evaluateExpressionGet(fc, "#{groupsEditViewBean.attrValues}", List.class);
                int index = element.indexOf(em);
                if (index != -1) {
                    em = element.get(index);
                }
            } else if (viewId.contains("AdsGroupEditView")) {
                List<DWHElementWebModel> element = (List<DWHElementWebModel>) fc.getApplication().evaluateExpressionGet(fc, "#{adsGroupsEditViewBean.attrValues}", List.class);
                int index = element.indexOf(em);
                if (index != -1) {
                    em = element.get(index);
                }
            }else if (viewId.contains("CreateCampaign")) {
                List<DWHElementWebModel> element = (List<DWHElementWebModel>) fc.getApplication().evaluateExpressionGet(fc, "#{campaignManagementBean.attrValues}", List.class);
                int index = element.indexOf(em);
                if (index != -1) {
                    em = element.get(index);
                }
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(GroupTypeConverter.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
        }
        return em;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof DWHElementWebModel) {
            DWHElementWebModel em = (DWHElementWebModel) o;
            return em.getDisplayName() + "~" + em.getElementId();
        }
        return "";
    }
}
