/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.SMSHistoryDao;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.SMSHistoryModel;
import com.asset.contactstrategy.interfaces.models.RetrieveMessageInputModel;
import com.asset.contactstrategy.interfaces.models.RetrieveSMSsInputModel;
import com.asset.contactstrategy.interfaces.models.RetrieveSMSsOutputModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author esmail.anbar
 */
public class SMSHistoryService {
    
//    public void updateDeliveryStatus(Connection conn, int modX, String date) throws CommonException
//    {
////        CommonLogger.businessLogger.debug("VFECSSMSHistoryService.updateDeliveryStatus() Invoked...");
//        SMSHistoryDao vFECStrategySMSHistoryDAO = new SMSHistoryDao();
//        vFECStrategySMSHistoryDAO.updateDeliveryStatus(conn, modX, date);
//    }
    
/*    public void updateOtherStatus(Connection conn, int modX, String date) throws CommonException
    {
//        CommonLogger.businessLogger.debug("VFECSSMSHistoryService.updateOtherStatus() Invoked...");
        SMSHistoryDao vFECStrategySMSHistoryDAO = new SMSHistoryDao();
        vFECStrategySMSHistoryDAO.updateOtherStatus(conn, modX, date);
    }
*/  
//    public static void updateTimeOutStatus(Connection conn, int modX, String date) throws CommonException
//    {
////        CommonLogger.businessLogger.debug("VFECSSMSHistoryService.updateTimeOutStatus() Invoked...");
////        SMSHistoryDao vFECStrategySMSHistoryDAO = new SMSHistoryDao();
//        SMSHistoryDao.updateTimeOutStatus(conn, modX, date);
//    }
    
    public String getMessageStatus(Connection conn, long MessageID, String MSISDN, HashMap<Integer, String> statusHashMap) throws CommonException
    {
//        CommonLogger.businessLogger.debug("VFECSSMSHistoryService.getMessageStatus() Invoked...");
        SMSHistoryDao vFECStrategySMSHistoryDAO = new SMSHistoryDao();
        return vFECStrategySMSHistoryDAO.getMessageStatus(conn, MessageID, MSISDN, statusHashMap);
    }
    
    public void archiveMsg(Connection conn, ArrayList<SMSHistoryModel> msgs) throws CommonException{
//        CommonLogger.businessLogger.debug("VFECSSMSHistoryService.archiveMsg() Invoked...");
        SMSHistoryDao sMSHistoryDAO = new SMSHistoryDao();
        sMSHistoryDAO.archiveMsg(conn, msgs);
    }
    
    public RetrieveSMSsOutputModel getMessagesForMSISDNwithDaySpan(Connection conn, RetrieveSMSsInputModel inputModel, HashMap<Integer, String> statusHashMap) throws CommonException
    {
//        CommonLogger.businessLogger.debug("VFECSSMSHistoryService.getMessageStatus() Invoked...");
        SMSHistoryDao vFECStrategySMSHistoryDAO = new SMSHistoryDao();
        return vFECStrategySMSHistoryDAO.getMessagesForMSISDNwithDaySpan(conn, inputModel, statusHashMap);
    }
        public String getMessageStatus(Connection conn, RetrieveMessageInputModel input, HashMap<Integer, String> statusHashMap) throws CommonException
    {
//        CommonLogger.businessLogger.debug("VFECSSMSHistoryService.getMessageStatus() Invoked...");
        SMSHistoryDao vFECStrategySMSHistoryDAO = new SMSHistoryDao();
        return vFECStrategySMSHistoryDAO.getMessageStatus(conn, input, statusHashMap);
    }
    
    public void updateArchivedMessageStatus(Connection conn, long csMsgId, int status, int msisdnModX) throws CommonException
    {
//        CommonLogger.businessLogger.debug("VFECSSMSHistoryService.updateArchivedMessageStatus() Invoked...");
        new SMSHistoryDao().updateArchivedMessageStatus(conn, csMsgId, status, msisdnModX);
    }
}
