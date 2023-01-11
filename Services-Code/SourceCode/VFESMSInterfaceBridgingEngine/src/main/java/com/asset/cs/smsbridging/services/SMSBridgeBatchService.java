package com.asset.cs.smsbridging.services;

import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.SMSBridgeDBObject;
import com.asset.cs.smsbridging.daos.SMSBridgeBatchDAO;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author aya.moawed
 */
public class SMSBridgeBatchService {

//    public ArrayList<SMSBridgeDBObject> dequeueBatch(Connection connection, String appQueueName, int batchSize, int waitTime, int threadNumber) throws CommonException {
//        SMSBridgeBatchDAO dao = new SMSBridgeBatchDAO();
//        return dao.dequeueBatch(connection, appQueueName, batchSize, waitTime, threadNumber);
//    }

//    public void enqueueBatch(ArrayList<SMSBridgeDBObject> sms,String queueName, Connection con) throws CommonException { 
//        SMSBridgeBatchDAO dao = new SMSBridgeBatchDAO();
//        dao.enqueueBatch(con,queueName,sms);
//    }

}
