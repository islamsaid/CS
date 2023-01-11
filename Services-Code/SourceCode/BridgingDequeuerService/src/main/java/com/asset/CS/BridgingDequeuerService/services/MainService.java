/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.services;

import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author mostafa.kashif
 */


public class MainService {

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public QueuesService getQueuesService() {
        return queuesService;
    }

    public void setQueuesService(QueuesService queuesService) {
        this.queuesService = queuesService;
    }
 @Autowired
    private QueuesService queuesService;
 
  @Autowired
    private SystemPropertiesService systemPropertiesService;

    public MainService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public QueueModel findOne(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM VFE_CS_APP_QUEUES WHERE APP_ID = ?", new DatabaseServiceRowMapper(), id);
    }

    private static final class DatabaseServiceRowMapper  implements RowMapper<QueueModel> {
    
        @Override
        public QueueModel mapRow(ResultSet rs, int i) throws SQLException {
            return new QueueModel(
                    rs.getString("APP_NAME"),
                    rs.getString("QUEUE_NAME"),
                    rs.getString("SCHEMA_NAME"));
        }
    }
    
     public ArrayList<SMSBridge> getSMSs(String queueName, int batchSize, int waitTime, long threadNumber) throws CommonException{
        return queuesService.getSMSs(jdbcTemplate, queueName, batchSize, waitTime, threadNumber);
    }
     
      public List<QueueModel> getProceduresQueues() {
        return  queuesService.getProceduresQueues(jdbcTemplate) ;
    }
    public HashMap<String, SystemPropertiesModel> getPropertiesPerInterface(int groupId) {
        return systemPropertiesService.getPropertiesPerInterface(jdbcTemplate, groupId);
    }
}