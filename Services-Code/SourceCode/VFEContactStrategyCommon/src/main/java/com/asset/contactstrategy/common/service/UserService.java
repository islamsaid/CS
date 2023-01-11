/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.CommonDAO;
import com.asset.contactstrategy.common.dao.UserDAO;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.UserModel;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author Mostafa Fouda
 */
public class UserService {

    public UserModel retrieveUser(Connection connection, String userName) throws CommonException {
        UserModel userModel = null;
        try {
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [retrieveUser]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveUser Started").build());

            UserDAO userDAO = new UserDAO();
            userModel = userDAO.retrieveUser(connection, userName);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End [retrieveUser]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveUser Ended").build());

        } catch (CommonException e) {
            throw e;
        }
        return userModel;
    }

    public ArrayList<UserModel> getUsersList(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(UserService.class.getName() + " || " + "Starting  [getUsersList]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getUsersList Started").build());

            UserDAO dao = new UserDAO();
            ArrayList<UserModel> smscModels = dao.getUsersList(connection);
//            CommonLogger.businessLogger.debug(UserService.class.getName() + " || " + "End [getUsersList]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getUsersList Ended").build());

            return smscModels;
        } catch (CommonException e) {
            throw e;
        }
    }

    public void deleteUser(Connection connection, UserModel userModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(UserService.class.getName() + " || " + "Starting  [deleteUser]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteUser Started").build());

            UserDAO userDAO = new UserDAO();
            userDAO.deleteUser(connection, userModel);
//            CommonLogger.businessLogger.debug(UserService.class.getName() + " || " + "End [deleteUser]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteUser Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }
    
    public void insertUser(Connection connection, UserModel user) throws CommonException{
        try {
//            CommonLogger.businessLogger.debug(UserService.class.getName() + " || " + "Starting  [insertUser]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertUser Started").build());

            UserDAO userDAO = new UserDAO();
            user.setId(CommonDAO.getNextId(connection, DBStruct.VFE_CS_USERS.USERS_SEQ));
            userDAO.insertUser(connection, user);
//            CommonLogger.businessLogger.debug(UserService.class.getName() + " || " + "End [insertUser]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertUser Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }
    
      public void updateUser(Connection connection, UserModel user) throws CommonException{
        try {
//            CommonLogger.businessLogger.debug(UserService.class.getName() + " || " + "Starting  [updateUser]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateUser Started").build());

            UserDAO userDAO = new UserDAO();
            userDAO.updateUser(connection, user);
//            CommonLogger.businessLogger.debug(UserService.class.getName() + " || " + "End [updateUser]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateUser Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }
}
