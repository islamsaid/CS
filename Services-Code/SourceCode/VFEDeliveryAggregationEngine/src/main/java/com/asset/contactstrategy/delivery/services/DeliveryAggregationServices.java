/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.services;

import com.asset.contactstrategy.common.service.SMSHistoryService;
import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.service.SystemPropertiesService;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.models.JobModel;
import java.sql.Connection;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author esmail.anbar
 */
public class DeliveryAggregationServices {

//    public void updateDeliveryStatus(int modX, String date) throws CommonException {
////        CommonLogger.businessLogger.debug("DeliveryAggregationServices.updateDeliveryStatus() Invoked...");
//        Connection conn = null;
//        try {
//            conn = DataSourceManger.getConnection();
//            //conn.setAutoCommit(false);
//
//            SMSHistoryService service = new SMSHistoryService();
//            service.updateDeliveryStatus(conn, modX, date);
//
//            DataSourceManger.commitConnection(conn);
//            //CommonLogger.businessLogger.info("Commited Successfully");
//        } catch (CommonException ce) {
//            DataSourceManger.rollBack(conn);
//            CommonLogger.businessLogger.error(ce + " || " + ce.getMessage());
//            CommonLogger.errorLogger.error(ce + " || " + ce.getMessage(), ce);
//            HandleExceptionService.sendMOM(ce, GeneralConstants.SRC_ID_DELIVERY_AGGREGATION,
//                    Integer.parseInt(JobManager.getInstance().getSystemProperties().get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "modX:" + modX + ", date:" + date);
//            throw ce;
//        } catch (Exception e) {
//            DataSourceManger.rollBack(conn);
//            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
//            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
//            throw HandleExceptionService.wrapCommonException(e);
//        } finally {
//            if (conn != null) {
//                try {
//                    DataSourceManger.closeConnection(conn);
//                } catch (CommonException ex) {
//                    CommonLogger.errorLogger.error(ex);
//                }
//            }
//        }
////        CommonLogger.businessLogger.debug("DeliveryAggregationServices.updateDeliveryStatus() Ended...");
//    }

    /*    public void updateOtherStatus(int modX, String date) throws CommonException
    {
//        CommonLogger.businessLogger.debug("DeliveryAggregationServices.updateOtherStatus() Invoked...");
        Connection conn = null;
        try 
        {
            conn = DataSourceManger.getConnection();
            //conn.setAutoCommit(false);
            
            SMSHistoryService service = new SMSHistoryService();
            service.updateOtherStatus(conn, modX, date);
            
            DataSourceManger.commitConnection(conn);
            //CommonLogger.businessLogger.info("Commited Successfully");
        }
        catch (CommonException ce)
        {
            DataSourceManger.rollBack(conn);
            CommonLogger.businessLogger.error(ce + " || " + ce.getMessage());
            CommonLogger.errorLogger.error(ce + " || " + ce.getMessage(), ce);
            HandleExceptionService.sendMOM(ce, GeneralConstants.SRC_ID_DELIVERY_AGGREGATION, 
                    Integer.parseInt(JobManager.getInstance().getSystemProperties().get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "modX:" + modX + ", date:" + date);
            throw ce;
        }
        catch (Exception e) 
        {
            DataSourceManger.rollBack(conn);
            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
            throw HandleExceptionService.wrapCommonException(e);
        }     
        finally
        {
            if (conn != null)
            {
                try 
                {
                    DataSourceManger.closeConnection(conn);
                } 
                catch (CommonException ex) 
                {
                    CommonLogger.errorLogger.error(ex);
                }
            }
        }
//        CommonLogger.businessLogger.debug("DeliveryAggregationServices.updateOtherStatus() Ended...");
    }
     */
//    public static void updateTimeOutStatus(int modX, String date) throws CommonException {
////        CommonLogger.businessLogger.debug("DeliveryAggregationServices.updateTimeOutStatus() Invoked...");
//        Connection conn = null;
//        try {
//            conn = DataSourceManger.getConnection();
//            //conn.setAutoCommit(false);
//
////            SMSHistoryService service = new SMSHistoryService();
//            SMSHistoryService.updateTimeOutStatus(conn, modX, date);
//
//            DataSourceManger.commitConnection(conn);
//            //CommonLogger.businessLogger.info("Commited Successfully");
//        } catch (CommonException ce) {
//            DataSourceManger.rollBack(conn);
//            CommonLogger.businessLogger.error(ce + " || " + ce.getMessage());
//            CommonLogger.errorLogger.error(ce + " || " + ce.getMessage(), ce);
//            HandleExceptionService.sendMOM(ce, GeneralConstants.SRC_ID_DELIVERY_AGGREGATION,
//                    Integer.parseInt(JobManager.getInstance().getSystemProperties().get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "modX:" + modX + ", date:" + date);
//            throw ce;
//        } catch (Exception e) {
//            DataSourceManger.rollBack(conn);
//            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
//            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
//            throw HandleExceptionService.wrapCommonException(e);
//        } finally {
//            if (conn != null) {
//                try {
//                    DataSourceManger.closeConnection(conn);
//                } catch (CommonException ex) {
//                    CommonLogger.errorLogger.error(ex);
//                }
//            }
//        }
////        CommonLogger.businessLogger.debug("DeliveryAggregationServices.updateTimeOutStatus() Ended...");
//    }

    public static void assignUpdateJobs() throws Exception {
//        CommonLogger.businessLogger.debug("DeliveryAggregationServices.assignUpdateJobs() Invoked...");
//        Connection conn = null;
//        try 
//        {
//            conn = DataSourceManger.getConnection();
        //conn.setAutoCommit(false);

//            AssignmentServices assignmentServices = new AssignmentServices();
        AssignmentServices.assignUpdateJobs();

        //DataSourceManger.commitConnection(conn);
        //CommonLogger.businessLogger.info("Commited Successfully");
//        }
//        catch (CommonException ce)
//        {
//            CommonLogger.businessLogger.error(ce + " || " + ce.getMessage());
//            CommonLogger.errorLogger.error(ce + " || " + ce.getMessage(), ce);
//            HandleExceptionService.sendMOM(ce, GeneralConstants.SRC_ID_DELIVERY_AGGREGATION, 
//                    Integer.parseInt(JobManager.getInstance().getSystemProperties().get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
//                    "");
//            throw ce;
//        }
//        catch (Exception e) 
//        {
//            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
//            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
//            throw HandleExceptionService.wrapCommonException(e);
//        } 
//        finally
//        {
//            if (conn != null)
//            {
//                try 
//                {
//                    DataSourceManger.closeConnection(conn);
//                } 
//                catch (CommonException ex) 
//                {
//                    CommonLogger.errorLogger.error(ex);
//                }
//            }
//        }
//        CommonLogger.businessLogger.debug("DeliveryAggregationServices.assignUpdateJobs() Ended...");
    }

    public static void assignTimeOutJobs() throws CommonException {
//        CommonLogger.businessLogger.debug("DeliveryAggregationServices.assignTimeOutJobs() Invoked...");
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            //conn.setAutoCommit(false);

//            AssignmentServices assignmentServices = new AssignmentServices();
            AssignmentServices.assignTimeOutJobs(conn);

            //DataSourceManger.commitConnection(conn);
            //CommonLogger.businessLogger.info("Commited Successfully");
        } catch (CommonException ce) {
            CommonLogger.businessLogger.error(ce + " || " + ce.getMessage());
            CommonLogger.errorLogger.error(ce + " || " + ce.getMessage(), ce);
            HandleExceptionService.sendMOM(ce, GeneralConstants.SRC_ID_DELIVERY_AGGREGATION,
                    Integer.parseInt(JobManager.getInstance().getSystemProperties().get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "");
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
            throw HandleExceptionService.wrapCommonException(e);
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (CommonException ex) {
                    CommonLogger.errorLogger.error(ex);
                }
            }
        }
//        CommonLogger.businessLogger.debug("DeliveryAggregationServices.assignTimeOutJobs() Ended...");
    }

    public void assignSystemProperties() throws CommonException {
//        CommonLogger.businessLogger.debug("DeliveryAggregationServices.assignSystemProperties() Invoked...");
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();

            SystemPropertiesService systemPropertiesService = new SystemPropertiesService();
            JobManager.getInstance().setSystemProperties(
                    systemPropertiesService.getSystemPropertiesByGroupID(conn, GeneralConstants.SRC_ID_DELIVERY_AGGREGATION));
            //CommonLogger.businessLogger.info("Commited Successfully");
        } catch (CommonException ce) {
            CommonLogger.businessLogger.error(ce + " || " + ce.getMessage());
            CommonLogger.errorLogger.error(ce + " || " + ce.getMessage(), ce);
            HandleExceptionService.sendMOM(ce, GeneralConstants.SRC_ID_DELIVERY_AGGREGATION,
                    Integer.parseInt(JobManager.getInstance().getSystemProperties().get(Defines.DATABASE_MOM_ERROR_SEVERITY)),
                    "");
            throw ce;
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e + " || " + e.getMessage());
            CommonLogger.errorLogger.error(e + " || " + e.getMessage(), e);
            throw HandleExceptionService.wrapCommonException(e);
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (CommonException ex) {
                    CommonLogger.errorLogger.error(ex);
                }
            }
        }
//        CommonLogger.businessLogger.debug("DeliveryAggregationServices.assignSystemProperties() Ended...");
    }
    

    
}
