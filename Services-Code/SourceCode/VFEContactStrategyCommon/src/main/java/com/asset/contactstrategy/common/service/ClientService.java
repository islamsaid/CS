/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.ClientDAO;
import com.asset.contactstrategy.common.dao.WebLoggerDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSCInterfaceClientModel;
import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * @author mohamed.osman
 */
public class ClientService {

    public HashMap<String, SMSCInterfaceClientModel> retrieveClients(Connection connection) throws CommonException {
        HashMap<String, SMSCInterfaceClientModel> clientsMap = null;
        ClientDAO clientDAO;
        try {
            clientsMap = new HashMap<>();
//            CommonLogger.businessLogger.debug(ClientService.class.getName() + " || " + "Starting  [retrieveClients]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveClients Started").build());
            clientDAO = new ClientDAO();
            clientsMap = clientDAO.retrieveClients(connection);
//            CommonLogger.businessLogger.debug(ClientService.class.getName() + " || " + "End [retrieveClients]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveClients Emded").build());
        } catch (CommonException e) {
            throw e;
        }
        return clientsMap;
    }

}
