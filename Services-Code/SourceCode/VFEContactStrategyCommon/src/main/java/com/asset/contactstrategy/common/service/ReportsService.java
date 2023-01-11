/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.ReportsDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.ReportsModel;
import java.sql.Connection;

/**
 *
 * @author Ahmed Hisham
 */
public class ReportsService {
    
     public int RecievedSMSPerCustomeService(Connection connection,ReportsModel reportsModel) throws CommonException{
          int count = 0;
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [retrievecount]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrievecount Started").build());

          ReportsDAO reportsDAO = new ReportsDAO();
          count = reportsDAO.getNumberOfRecievedSMSPerCustomer(reportsModel, connection);
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [retrievecount]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrievecount Ended").build());

          return count;
      }
     
    
     
      public int violationSMSPerCustomerService(Connection connection,ReportsModel reportsModel) throws CommonException{
          int count = 0;
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [violationSMSPerCustomer]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "violationSMSPerCustomer Started").build());

          ReportsDAO reportsDAO = new ReportsDAO();
          count = reportsDAO.getNumberOfViolationSMSPerCustomer(reportsModel, connection);
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [violationSMSPerCustomer]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "violationSMSPerCustomer Ended").build());

          return count;
      }
      
      public int recievedSMSByCSPService(Connection connection,ReportsModel reportsModel) throws CommonException{
          int count = 0;
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [recievedSMSByCSPService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "recievedSMSByCSPService Started").build());

          ReportsDAO reportsDAO = new ReportsDAO();
          count = reportsDAO.recievedSMSByCSPCount(reportsModel, connection);
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [recievedSMSByCSPService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "recievedSMSByCSPService Ended").build());

          return count;
      }
     
      public ReportsModel noOfSMSByPlatformService(Connection connection,ReportsModel reportsModel) throws CommonException{
          ReportsModel model; 
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [noOfSMSByPlatformService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "noOfSMSByPlatformService Started").build());

          ReportsDAO reportsDAO = new ReportsDAO();
          model = reportsDAO.getNoOfSMSByPlatform(reportsModel, connection);
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [noOfSMSByPlatformService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "noOfSMSByPlatformService Ended").build());

          return model;
      }
      
      public ReportsModel NoOfSMSToSMSCService(Connection connection,ReportsModel reportsModel) throws CommonException{
          ReportsModel model; 
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "Starting  [noOfSMSByPlatformService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "noOfSMSByPlatformService Started").build());

          ReportsDAO reportsDAO = new ReportsDAO();
          model = reportsDAO.getNoOfSMSToSMSC(reportsModel, connection);
//          CommonLogger.businessLogger.debug(ReportsService.class.getName() + " || " + "End [noOfSMSByPlatformService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "noOfSMSByPlatformService Ended").build());

          return model;
      }
    
}
