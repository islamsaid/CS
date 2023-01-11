/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.MenuDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MenuModel;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author Yomna Naser
 */
public class MenuService {

    public ArrayList<MenuModel> getMenuList(Connection connection, int userType) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(MenuService.class.getName() + " || " + "Starting Getting [getMenuList]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getMenuList Started")
                    .put(GeneralConstants.StructuredLogKeys.INTERFACE_NAME, MenuService.class.getName()).build());
            MenuDAO menuDAO = new MenuDAO();
            ArrayList<MenuModel> menuList = menuDAO.getMenuList(connection, userType);
//            CommonLogger.businessLogger.debug(MenuService.class.getName() + " || " + "End Getting [getMenuList]...");
  CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getMenuList Ended")
                    .put(GeneralConstants.StructuredLogKeys.INTERFACE_NAME, MenuService.class.getName()).build());
            return menuList;
        } catch (CommonException e) {
            throw e;
        }
    }

}
