/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MenuModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Yomna Naser
 */
public class MenuDAO {

    public ArrayList<MenuModel> getMenuList(Connection connection, int userType) throws CommonException {

//        CommonLogger.businessLogger.debug(MenuDAO.class.getName() + " || " + "Starting [getMenuList]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getMenuList").build());
        PreparedStatement statement = null;
        StringBuilder sql = null;
        ResultSet resultSet = null;
        long startTime = System.currentTimeMillis();
        try {
            sql = new StringBuilder();
            sql.append("Select * From ").append(DBStruct.VFE_CS_MENU.TABLE_NAME)
                    .append(" Where ").append(DBStruct.VFE_CS_MENU.USER_TYPE)
                    .append(" = ? ");
            statement = connection.prepareStatement(sql.toString());
            statement.setInt(1, userType);
//            CommonLogger.businessLogger.debug(MenuDAO.class.getName() + " || " + " [Query] " + sql.toString());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getMenuList Query")
                    .put(GeneralConstants.StructuredLogKeys.QUERY, sql).build());
            resultSet = statement.executeQuery();
//            CommonLogger.businessLogger.debug(MenuDAO.class.getName() + " || " + "Retriving Date time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getMenuList Executed")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
            ArrayList<MenuModel> menuList = new ArrayList<>();
            while (resultSet.next()) {
                MenuModel menuModel = new MenuModel();
                menuModel.setId(resultSet.getInt(DBStruct.VFE_CS_MENU.ID));
                menuModel.setPageName(resultSet.getString(DBStruct.VFE_CS_MENU.PAGE_NAME));
                menuModel.setPageURL(resultSet.getString(DBStruct.VFE_CS_MENU.PAGE_URL));
                menuModel.setUserType(resultSet.getInt(DBStruct.VFE_CS_MENU.USER_TYPE));
                menuModel.setIcoStyleClass(resultSet.getString(DBStruct.VFE_CS_MENU.ICO_STYLE_CLASS));
                menuList.add(menuModel);
            }
//            CommonLogger.businessLogger.debug(MenuDAO.class.getName() + " || " + "Menu List Size :" + menuList.size());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getMenuList MenuList stats")
                    .put(GeneralConstants.StructuredLogKeys.LIST_SIZE, menuList.size()).build());
//            CommonLogger.businessLogger.debug(MenuDAO.class.getName() + " || " + "End [getMenuList]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended getMenuList").build());
            return menuList;
        } catch (SQLException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getMenuList]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.DATABASE_ERROR + "---- > [getMenuList]", e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } finally {
            try {
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getMenuList]", ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.DATABASE_ERROR);
            }
        }

    }

}
