/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.converters;

import com.asset.contactstrategy.common.models.LookupModel;
import java.util.HashMap;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Yomna Naser
 */
@FacesConverter(value = "lookupConverter")
public class LookupsModelConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || (value.trim().length() == 0)) {
            return value;
        }
        LookupModel lookupModel = new LookupModel();
        HashMap<Integer,LookupModel> userTypes = context.getApplication().evaluateExpressionGet(context, "#{userManagementBean.userTypes}", HashMap.class);
        lookupModel = userTypes.get(Integer.parseInt(value));
        return lookupModel;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof LookupModel) {
            return ((LookupModel)value).getId()+"";
        } else {
            return "";
        }
    }

}
