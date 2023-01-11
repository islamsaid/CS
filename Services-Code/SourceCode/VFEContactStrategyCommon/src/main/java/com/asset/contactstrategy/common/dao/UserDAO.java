/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.UserModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Mostafa Fouda
 */
public class UserDAO {

    public UserModel retrieveUser(Connection connection, String userName) throws CommonException {
//        CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "[retrieveUser] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveUser Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        UserModel userModel = null;
        try {
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.VFE_CS_USERS.TABLE_NAME)
                    .append(" where ").append(DBStruct.VFE_CS_USERS.USER_NAME)
                    .append(" = ?").append(" and ")
                    .append(DBStruct.VFE_CS_USERS.USER_DELETED).append(" = ").append(Defines.USER_NOT_DELETED);
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveUser Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            statement.setString(1, userName);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userModel = new UserModel();
                userModel.setId(resultSet.getInt(DBStruct.VFE_CS_USERS.USER_ID));
                userModel.setUsername(resultSet.getString(DBStruct.VFE_CS_USERS.USER_NAME));
                userModel.setUserType(resultSet.getInt(DBStruct.VFE_CS_USERS.USER_TYPE));
            }
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "Retriving Data time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [retrieveUser] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveUser Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveUser]" + e);
            throw new CommonException(e.getMessage(), e.getErrorCode());
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveUser]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveUser]" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveUser]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
            }
        }
        return userModel;
    }

    public ArrayList<UserModel> getUsersList(Connection connection) throws CommonException {

//        CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [getUsersList] started...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getUsersList Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;

        try {
            sql = new StringBuilder();
            sql.append("Select * from ").append(DBStruct.VFE_CS_USERS.TABLE_NAME)
                    .append(" where ").append(DBStruct.VFE_CS_USERS.USER_DELETED).append("=").append(Defines.USER_NOT_DELETED);
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getUsersList Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getUsersList Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

            ArrayList<UserModel> users = new ArrayList<>();
            while (resultSet.next()) {
                UserModel usermodel = new UserModel();
                usermodel.setId(resultSet.getInt(DBStruct.VFE_CS_USERS.USER_ID));
                usermodel.setUsername(resultSet.getString(DBStruct.VFE_CS_USERS.USER_NAME));
                usermodel.setUserType(resultSet.getInt(DBStruct.VFE_CS_USERS.USER_TYPE));
                users.add(usermodel);
            }
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "Users List Size :" + users.size());
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [getUsersList] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getUsersList Ended")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, users.size()).build());

            return users;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [getUsersList]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getUsersList]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [getUsersList]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveUser]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
            }
        }
    }

    public void deleteUser(Connection connection, UserModel userModel) throws CommonException {
//        CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "[deleteUser] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteUser Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        try {
            sql = new StringBuilder();
//            sql.append("delete  from ").append(DBStruct.VFE_CS_USERS.TABLE_NAME)
//                    .append(" where ")
//                    .append(DBStruct.VFE_CS_USERS.USER_ID).append(" = ")
//                    .append(userModel.getId());
            sql.append("Update ").append(DBStruct.VFE_CS_USERS.TABLE_NAME)
                    .append(" set ").append(DBStruct.VFE_CS_USERS.USER_DELETED).append(" = ").append(Defines.USER_DELETED)
                    .append(" where ").append(DBStruct.VFE_CS_USERS.USER_ID).append(" = ").append(userModel.getId());
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteUser Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [deleteUser] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteUser Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [deleteUser]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteUser]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [deleteUser]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteUser]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
            }
        }
    }

    public void insertUser(Connection connection, UserModel user) throws CommonException {

//        CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "[insertUser] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertUser Started").build());
        long startTime = System.currentTimeMillis();
        String methodName = "insertUser";
        PreparedStatement statement = null;
        StringBuilder sql = null;
        try {
            sql = new StringBuilder();
            sql.append(" Insert into ").append(DBStruct.VFE_CS_USERS.TABLE_NAME).append(" ( ")
                    .append(DBStruct.VFE_CS_USERS.USER_ID).append(" , ")
                    .append(DBStruct.VFE_CS_USERS.USER_NAME).append(" , ")
                    .append(DBStruct.VFE_CS_USERS.USER_TYPE).append(" ) ")
                    .append(" Values (?,?,?)");

//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertUser Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setInt(3, user.getUserType());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [insertUser] ended...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertUser Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            if (e.getErrorCode() == GeneralConstants.UNIQUENESS_VIOLATION) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_NAME + "---->  for [" + methodName + "]" + " name " + user.getUsername() + " is used before");
                throw new CommonException("Name of AdsGroup is used before", ErrorCodes.UNIQUE_NAME);
            } else {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---->  for [insertUser]", e);
                throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertUser]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [insertUser]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertUser]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
            }
        }
    }

    public void updateUser(Connection connection, UserModel user) throws CommonException {

//        CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "[updateUser] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateUser Started").build());
        long startTime = System.currentTimeMillis();
        PreparedStatement statement = null;
        StringBuilder sql = null;
        try {
            sql = new StringBuilder();
            sql.append(" Update ").append(DBStruct.VFE_CS_USERS.TABLE_NAME).append(" set ")
                    .append(DBStruct.VFE_CS_USERS.USER_TYPE).append(" = ? where ")
                    .append(DBStruct.VFE_CS_USERS.USER_ID).append(" = ?");

//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [Query] " + sql.toString());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateUser Query")
                .put(GeneralConstants.StructuredLogKeys.QUERY, sql.toString()).build());
            statement = connection.prepareStatement(sql.toString());
            statement.setLong(1, user.getUserType());
            statement.setLong(2, user.getId());
            statement.executeUpdate();
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
//            CommonLogger.businessLogger.debug(UserDAO.class.getName() + " || " + " [updateUser] ended...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateUser ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---->  for [updateUser]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateUser]" + ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + " while closing statement---->  for [insertUser]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertUser]" + ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.GENERAL_ERROR);
            }
        }
    }
}
