/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.MenuModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.utils.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yomna Naser
 */
public class MenuFacade {

    public ArrayList<MenuModel> getMenuList(int userType) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(MenuFacade.class.getName() + " || " + "Starting [getMenuList]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getMenuList")
                    .build());

            MainService mainService = new MainService();
            ArrayList<MenuModel> menuList = mainService.retrieveMenuList(userType);
            return menuList;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueues]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.GET_MENU);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public MOMErrorsModel getMOMErrorModel(String errorCode, String methodName) {
        MOMErrorsModel mErrorsModel = new MOMErrorsModel();
        mErrorsModel.setEngineSrcID(GeneralConstants.SRC_ID_WEB_INTERFACE);
        mErrorsModel.setEntryDate(new Date());
        mErrorsModel.setErrorCode(errorCode);
        mErrorsModel.setModuleName(Constants.MENU_MODULE);
        mErrorsModel.setFunctionName(methodName);
        mErrorsModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        return mErrorsModel;
    }

}
