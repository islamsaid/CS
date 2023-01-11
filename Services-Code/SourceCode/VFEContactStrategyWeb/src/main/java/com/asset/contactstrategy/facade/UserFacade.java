/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.webmodels.UserWebModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mostafa Fouda
 */
public class UserFacade {

//    private int userType;
//
//    public UserFacade(UserModel userModel) {
//        userType = userModel.getUserType();
//    }
    public UserModel generateUserModelFromWebModel(UserWebModel userWebModel) {
        UserModel userModel = new UserModel();
        userModel.setId(userWebModel.getId());
        userModel.setUserType(userWebModel.getUserType());
        userModel.setUsername(userWebModel.getUsername());
        return userModel;
    }

    public ArrayList<UserWebModel> getUsers() throws CommonException {
        String functionName = "getUsers";
        MainService mainService = new MainService();
        try {
//            CommonLogger.businessLogger.debug(UserFacade.class.getName() + " || " + "Starting [getUsers]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getUsers")
                    .build());
            ArrayList<UserWebModel> userModels = new ArrayList<>();
            for (UserModel user : mainService.getUsersList()) {
                UserWebModel userWebModel = new UserWebModel();
                if (user.getUserType() == GeneralConstants.USER_TYPE_ADMINISTRATOR_VALUE) {
                    userWebModel.setId(user.getId());
                    userWebModel.setUserType(user.getUserType());
                    userWebModel.setUserTypeValue(SystemLookups.USERS_TYPE.get(GeneralConstants.USER_TYPE_ADMINISTRATOR_VALUE).getLable());
                    userWebModel.setUsername(user.getUsername());
                } else if (user.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {
                    userWebModel.setId(user.getId());
                    userWebModel.setUserType(user.getUserType());
                    userWebModel.setUserTypeValue(SystemLookups.USERS_TYPE.get(GeneralConstants.USER_TYPE_BUSINESS_VALUE).getLable());
                    userWebModel.setUsername(user.getUsername());
                } else if (user.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                    userWebModel.setId(user.getId());
                    userWebModel.setUserType(user.getUserType());
                    userWebModel.setUserTypeValue(SystemLookups.USERS_TYPE.get(GeneralConstants.USER_TYPE_OPERATIONAL_VALUE).getLable());
                    userWebModel.setUsername(user.getUsername());
                }
                userModels.add(userWebModel);
            }
//            CommonLogger.businessLogger.debug(UserFacade.class.getName() + " || " + "End [getUsers]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getUsers")
                    .build());
            return userModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getUsers]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), functionName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void deleteUser(UserWebModel userWebModel) throws CommonException {
        String functionName = "deleteUser";
        MainService mainService = new MainService();
        try {
            UserModel userModel = generateUserModelFromWebModel(userWebModel);
            mainService.deleteUser(userModel);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteUser]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), functionName);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void createUser(UserModel userModel) throws CommonException {
        try {
            MainService mainService = new MainService();
            mainService.insertUser(userModel);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createUser]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), "createUser");
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void editUser(UserModel userModel) throws CommonException {
        try {
            MainService mainService = new MainService();
            mainService.updateUser(userModel);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editUser]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), "editUser");
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public UserWebModel convertModelToWebModel(UserModel user) {
        UserWebModel userWebModel = new UserWebModel();
        userWebModel.setId(user.getId());
        userWebModel.setUserType(user.getUserType());
        if (user.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
            userWebModel.setUserTypeValue(GeneralConstants.USER_TYPE_OPERATIONAL);
        } else if (user.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {
            userWebModel.setUserTypeValue(GeneralConstants.USER_TYPE_BUSINESS);
        } else if (user.getUserType() == GeneralConstants.USER_TYPE_ADMINISTRATOR_VALUE) {
            userWebModel.setUserTypeValue(GeneralConstants.USER_TYPE_ADMINISTRATOR);
        }
        userWebModel.setUsername(user.getUsername());
        return userWebModel;
    }

    public MOMErrorsModel getMOMErrorModel(String errorCode, String methodName) {
        MOMErrorsModel mErrorsModel = new MOMErrorsModel();
        mErrorsModel.setEngineSrcID(GeneralConstants.SRC_ID_WEB_INTERFACE);
        mErrorsModel.setEntryDate(new Date());
        mErrorsModel.setErrorCode(errorCode);
        mErrorsModel.setModuleName(Constants.USER_MODULE);
        mErrorsModel.setFunctionName(methodName);
        mErrorsModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        return mErrorsModel;
    }

    // eslam.ahmed | 10-05-2020 | user existance check 
    public boolean isUserExist(String userName) throws CommonException {
        MainService mainService = new MainService();
        UserModel user = mainService.retrieveUser(userName);
        if (user == null) {
            return false;
        }
        return true;
    }

}
