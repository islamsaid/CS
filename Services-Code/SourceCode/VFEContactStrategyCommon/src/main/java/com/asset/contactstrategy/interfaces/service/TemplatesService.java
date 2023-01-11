/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.service;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.interfaces.dao.TemplatesDAO;
import com.asset.contactstrategy.interfaces.models.TemplateModel;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author esmail.anbar
 */
public class TemplatesService {

    public HashMap<Integer, TemplateModel> getTemplates(Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Started Getting [getTemplates]... at " + new Date());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getTemplates Started")
                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
        HashMap<Integer, TemplateModel> templates = new TemplatesDAO().getAllTemplates(connection);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Ended Getting [getTemplates]... at " + new Date());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getTemplates Ended")
                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
        return templates;
    }
}
