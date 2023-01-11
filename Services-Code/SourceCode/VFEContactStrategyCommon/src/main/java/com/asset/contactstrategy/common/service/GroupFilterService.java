/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.DWHFilterValueModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.LookupModel;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class GroupFilterService {

    public String getFilterQuery(ArrayList<FilterModel> filterArray) throws CommonException {
        String methodName = "getFilterQuery";
        try {
            String result = " 1=1 ";
            FilterModel filterModel = null;
            LookupModel dWHOperatorModel = null;
            DWHFilterValueModel dWHFilterValueModel = null;
            ArrayList<DWHFilterValueModel> filterValues = null;
            // loop on filters
            for (int i = 0; i < filterArray.size(); i++) {
                filterModel = filterArray.get(i);
                dWHOperatorModel = filterModel.getOperatorModel();
                if (dWHOperatorModel.getId() == Defines.EQUAL_OPERATOR
                        || dWHOperatorModel.getId() == Defines.NOT_EQUAL_OPERATOR
                        || dWHOperatorModel.getId() == Defines.GREATER_THAN_OPERATOR
                        || dWHOperatorModel.getId() == Defines.LESS_THAN_OPERATOR
                        || dWHOperatorModel.getId() == Defines.GREATER_EQUAL_OPERATOR
                        || dWHOperatorModel.getId() == Defines.LESS_EQUAL_OPERATOR) {
                    result += " AND tdp." + filterModel.getDwhElementModel().getDwhName() + " " + dWHOperatorModel.getLable() + " '" + filterModel.getFirstOperand() + "'";
                } else if (dWHOperatorModel.getId() == Defines.IN_OPERATOR) {

                    if ( filterModel.getDwhElementModel().getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE) || filterModel.getDwhElementModel().getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID)) {
                        result += " AND tdp." + filterModel.getDwhElementModel().getDwhName() + " " + dWHOperatorModel.getLable() + "(";
                    } else {
                        result += " AND tdp." + filterModel.getDwhElementModel().getDwhName() + " " + dWHOperatorModel.getLable() + "('";
                    }
                    filterValues = filterModel.getFilterValues();

                    for (int j = 0; j < filterValues.size(); j++) {
                        dWHFilterValueModel = filterValues.get(j);
                        if ( filterModel.getDwhElementModel().getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE) || filterModel.getDwhElementModel().getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID)) {
                            result += dWHFilterValueModel.getValueId() + ",";
                        } else {
                            result += dWHFilterValueModel.getValueLabel() + "','";
                        }
                    }
                    if ( filterModel.getDwhElementModel().getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.CUSTOMER_TYPE) || filterModel.getDwhElementModel().getDwhName().equalsIgnoreCase(DBStruct.DWH_CUSTOMERS.GOVERNMENT_ID)) {
                        result = result.substring(0, result.length() - 1);
                    } else {
                        result = result.substring(0, result.length() - 2);

                    }
                    result += ")";
                } else if (dWHOperatorModel.getId() == Defines.BETWEEN_OPERATOR) {
                    result += " AND tdp." + filterModel.getDwhElementModel().getDwhName() + "  >= TO_DATE('" + filterModel.getFirstOperand() + "','yyyy mm dd')";
                    result += " AND tdp." + filterModel.getDwhElementModel().getDwhName() + "  <= TO_DATE('" + filterModel.getSecondOperand() + "','yyyy mm dd')";
                }
            }

            return result;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(GroupFilterService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
    }
}
