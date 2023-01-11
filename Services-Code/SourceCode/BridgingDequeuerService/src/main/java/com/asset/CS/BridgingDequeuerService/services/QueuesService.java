/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.CS.BridgingDequeuerService.services;

import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.CS.BridgingDequeuerService.daos.QueuesDao;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.QueueModel;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author mostafa.kashif
 */

@Component
public class QueuesService {
    
    @Autowired
    private QueuesDao queuesDao;
    
    public ArrayList<SMSBridge> getSMSs(JdbcTemplate jdbcTemplate,String queueName, int batchSize, int waitTime, long threadNumber) throws CommonException{
        return queuesDao.getSMSs(jdbcTemplate, queueName, batchSize, waitTime, threadNumber);
    }
    
    public List<QueueModel> getProceduresQueues(JdbcTemplate jdbcTemplate) {
        return  queuesDao.getProceduresQueues(jdbcTemplate) ;
    }
    
}
