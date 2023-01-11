/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.service;

import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.interfaces.dao.GroupsDAO;
import com.asset.contactstrategy.interfaces.models.AdsGroupModel;
import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * @author hazem.fekry
 */
public class GroupsService {
    public HashMap<Integer,SMSGroupModel> getGroupsList(Connection connection) throws CommonException 
    {
        GroupsDAO groupsDAO = new GroupsDAO();
        return groupsDAO.getGroupsList(connection);
    }
    public HashMap<Integer,AdsGroupModel> getAdsGroupsList(Connection connection) throws CommonException 
    {
        GroupsDAO groupsDAO = new GroupsDAO();
        return groupsDAO.getAdsGroupsList(connection);
    }
}
