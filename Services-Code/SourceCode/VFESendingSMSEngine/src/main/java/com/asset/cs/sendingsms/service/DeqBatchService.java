/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.service;

import client.HashObject;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.cs.sendingsms.dao.DeqBatchDAO;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class DeqBatchService {

    public ArrayList<HashObject> deqBatch(Connection con, String appId, int batchSize, int waitTime,QueueModel appQueue,int threadNumber) throws CommonException {
        return DeqBatchDAO.deqBatch(con, appId, batchSize, waitTime,appQueue,threadNumber);
    }
}
