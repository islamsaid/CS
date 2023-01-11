/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.DBQueueDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.SMS;
import com.asset.contactstrategy.common.models.SMSBridgeDBObject;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author esmail.anbar
 */
public class DBQueueService {
    
    public void sendMessageBatch(Connection conn, SMS[] dbArray, String queueName, StringBuilder csMsgIds) throws CommonException
    {
        DBQueueDAO dao = new DBQueueDAO();
        //SMS[] dbArray = new SMS[objectsArray.size()];
//        SMS[] dbArray = objectsArray.toArray(new SMS[objectsArray.size()]);
//        SMS[] dbArray = new SMS[objectsArray.size()];
//        for (int i = 0; i < dbArray.length; i++)
//            dbArray[i] = objectsArray.get(i).getSmsModel();
        dao.sendMessageBatch(conn, queueName, dbArray, csMsgIds);
    }
    
    public void sendSingleMessage(Connection conn, SMS smsObject, String queueName, StringBuilder csMsgIds) throws CommonException
    {
        DBQueueDAO dao = new DBQueueDAO();
        //SMS[] dbArray = new SMS[objectsArray.size()];
        SMS[] dbArray = new SMS[1];
        dbArray[0] = smsObject;
        dao.sendMessageBatch(conn, queueName, dbArray, csMsgIds);
    }
    
    
    public void enqueueBatch(ArrayList<SMSBridgeDBObject> sms,String queueName, Connection con) throws CommonException { 
        DBQueueDAO dao = new DBQueueDAO();
        dao.enqueueBatch(con,queueName,sms);
    }
}
