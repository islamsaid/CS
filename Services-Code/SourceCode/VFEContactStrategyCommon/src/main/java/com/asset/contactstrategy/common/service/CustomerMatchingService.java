/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.CustomersMatchingDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.MatchingInstanceModel;
import com.asset.contactstrategy.common.models.UploadProcedureResult;
import java.sql.Connection;

/**
 *
 * @author Zain Al-Abedin
 */
public class CustomerMatchingService {

    public MatchingInstanceModel getInstance(Connection connection, int srcId,String instanceId)  throws CommonException{
        CustomersMatchingDAO dao = new CustomersMatchingDAO();
         return dao.getInstance(connection,srcId,instanceId);
    }
    public void updateInstanceModel(Connection connection, MatchingInstanceModel instance)throws CommonException{
        CustomersMatchingDAO dao = new CustomersMatchingDAO();
         dao.updateInstanceModel(connection,instance);
    }
    public void updateInstancesRunId(Connection connection,int runId)throws CommonException{
         CustomersMatchingDAO dao = new CustomersMatchingDAO();
         dao.updateInstancesRunId(connection,runId);
    }
     public boolean allInstancesSuccessed(Connection connection,  int dwhRunId) throws CommonException {
        CustomersMatchingDAO dao = new CustomersMatchingDAO();
        return dao.allInstancesSuccessed(connection, dwhRunId);
    }
    public boolean allInstancesFinished(Connection connection,  int dwhRunId) throws CommonException {
        CustomersMatchingDAO dao = new CustomersMatchingDAO();
        return dao.allInstancesFinished(connection, dwhRunId);
    }
    public boolean CompareTimeStamps(Connection connection, String firstItemKey, int firstSrcId, String secondItemKey, int secondSrcId) throws CommonException {
        CustomersMatchingDAO dao = new CustomersMatchingDAO();
        return dao.CompareTimeStamps(connection, firstItemKey, firstSrcId, secondItemKey, secondSrcId);
    }
    public void addGroupsCustomersNewPartation(Connection connection, int partitionId, String tableName) throws CommonException {
       // CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "Starting addGroupsCustomersNewPartation");
        try {
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
            dao.addGroupsCustomersNewPartation(connection, partitionId,tableName);
         //  CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "End addGroupsCustomersNewPartation");
        } catch (Exception e) {
            throw e;
        }
    }

    public void dropGroupsCustomersPartation(Connection connection, int partitionId, String tableName) throws CommonException {
       // CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "Starting dropGroupsCustomersPartation");
        try {
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
            dao.dropGroupsCustomersPartation(connection, partitionId,tableName);
       //   CommonLogger.infoLogger.info(DWHProfileService.class.getName() + " || " + "End dropGroupsCustomersPartation");
        } catch (Exception e) {
            throw e;
        }
    }

    public long matchSMSGroupsCustomersByCriteria(Connection connection, int groupId, int runId, int lastMSISDNTwoDigits, String filterQuery) throws CommonException {
       
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
            return dao.matchSMSGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery);
           
       
    }

    public UploadProcedureResult matchSMSGroupsCustomersByUpload(Connection connection, int groupId, int runId, int FileId) throws CommonException {
   
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
           return dao.matchSMSGroupsCustomersByUpload(connection, groupId, runId, FileId);
          
    }
    
    public long matchADSGroupsCustomersByCriteria(Connection connection, int groupId, int runId, int lastMSISDNTwoDigits, String filterQuery) throws CommonException {
       
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
            return dao.matchADSGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery);
           
       
    }

    public UploadProcedureResult matchADSGroupsCustomersByUpload(Connection connection, int groupId, int runId, int FileId) throws CommonException {
   
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
           return dao.matchADSGroupsCustomersByUpload(connection, groupId, runId, FileId);
          
    }
    
    public UploadProcedureResult matchGroupsCustomersByUpload(Connection connection, int groupId, int runId, int FileId,  String groupTable, String groupIdColumnName, String groupIdCol) throws CommonException {
   
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
           return dao.matchGroupsCustomersByUpload(connection, groupId, runId, FileId,  groupTable, groupIdColumnName, groupIdCol );
          
    }
    
     public long matchGroupsCustomersByCriteria(Connection connection, long groupId, int runId, int lastMSISDNTwoDigits, String filterQuery, String fileTable, String customerTable, String groupIdCol) throws CommonException {
       
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
            return dao.matchGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery, fileTable,  customerTable, groupIdCol);
           
       
    }
    
     public long matchCampaignGroupsCustomersByCriteria(Connection connection, long groupId, int runId, int lastMSISDNTwoDigits, String filterQuery,long max_targeted_customers) throws CommonException {
       
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
            return dao.matchCampaignGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery,max_targeted_customers);
           
       
    }

    public UploadProcedureResult matchCampaignGroupsCustomersByUpload(Connection connection, long groupId, int runId, int FileId) throws CommonException {
   
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
           return dao.matchCampaignGroupsCustomersByUpload(connection, groupId, runId, FileId);
          
    }
    
    
      public UploadProcedureResult deleteSuspendCampaign(Connection connection, long groupId, int runId, long maxTargetedCustomers,long customersToSuspend) throws CommonException {
   
            CustomersMatchingDAO dao = new CustomersMatchingDAO();
           return dao.deleteSuspendCampaign(connection, groupId, runId, maxTargetedCustomers,customersToSuspend);
          
    }

}
