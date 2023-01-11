/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.SMSCDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSCModel;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author rania.magdy
 */
public class SMSCService {
    
     public ArrayList<SMSCModel> getSMSCs(Connection connection) throws CommonException{
        try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [getSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCs Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            ArrayList<SMSCModel> smscModels = smscDAO.getSMSCs(connection);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [getSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCs Ended").build());

            return smscModels;
        } catch(CommonException e){
            throw e;
        }
    }
     public void editSMSC(Connection connection,SMSCModel sMSCModel) throws CommonException{
        try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [editSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editSMSC Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            smscDAO.editSMSC(connection,sMSCModel);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [editSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editSMSC Ended").build());

        } catch(CommonException e){
            throw e;
        }
    }
     public void editEditedSMSC(Connection connection,SMSCModel sMSCModel) throws CommonException{
           try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [editEditedSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedSMSC Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            smscDAO.editEditedSMSC(connection,sMSCModel);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [editEditedSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedSMSC ended").build());

        } catch(CommonException e){
            throw e;
        }
     }
      public SMSCModel createSMSC(Connection connection,SMSCModel sMSCModel) throws CommonException{
        try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [createSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createSMSC Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            sMSCModel=smscDAO.createSMSC(connection,sMSCModel);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [createSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createSMSC Ended").build());

            return sMSCModel;
        } catch(CommonException e){
            throw e;
        }
    }
      public SMSCModel retrieveConnectedSMSC(Connection connection,SMSCModel sMSCModel) throws CommonException{
          SMSCModel returnedModel=null; 
          try{
               
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [retrieveConnectedSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedSMSC Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
             returnedModel= smscDAO.retrieveConnectedSMSC(connection, sMSCModel);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [retrieveConnectedSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedSMSC Ended").build());

        } catch(CommonException e){
            throw e;
        }
          return returnedModel;
      }
      
      public void deleteSMSC(Connection connection,SMSCModel sMSCModel) throws CommonException{
          try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [deleteSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSC Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            smscDAO.deleteSMSCsQueue(connection, sMSCModel); // 1
            smscDAO.deleteSMSC(connection,sMSCModel); // 2
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [deleteSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSC Ended").build());

        } catch(CommonException e){
            throw e;
        }
      }
      
      public void deleteSMSCsQueue(Connection connection,SMSCModel sMSCModel) throws CommonException{
          try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [deleteSMSCsQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSCsQueue Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            smscDAO.deleteSMSCsQueue(connection,sMSCModel);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [deleteSMSCsQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteSMSCsQueue Ended").build());

        } catch(CommonException e){
            throw e;
        }
      }
      
      public void ChangeSMSCStatusToDelete(Connection connection,SMSCModel sMSCModel) throws CommonException{
            try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [ChangeSMSCStatusToDelete]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeSMSCStatusToDelete Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            smscDAO.ChangeSMSCStatusToDelete(connection,sMSCModel);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [ChangeSMSCStatusToDelete]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeSMSCStatusToDelete Ended").build());

        } catch(CommonException e){
            throw e;
        }
      }
     public ArrayList<SMSCModel> getApprovedSMSCs(Connection connection) throws CommonException{
          try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [getApprovedSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedSMSCs Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            ArrayList<SMSCModel> smscModels = smscDAO.getApprovedSMSCs(connection);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [getApprovedSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedSMSCs Ended").build());

            return smscModels;
        } catch(CommonException e){
            throw e;
        }
         
     }
     
     
     public SMSCModel getSMSCByName(Connection connection,String name)throws CommonException{
          try{
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "Starting  [getSMSCByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCByName Started").build());

            SMSCDAO smscDAO = new SMSCDAO();
            SMSCModel smscModel = smscDAO.getSMSCByName(connection,name);
//            CommonLogger.businessLogger.debug(SMSCService.class.getName() + " || " + "End [getSMSCByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getSMSCByName Ended").build());

            return smscModel;
        } catch(CommonException e){
            throw e;
        }
     }
}
