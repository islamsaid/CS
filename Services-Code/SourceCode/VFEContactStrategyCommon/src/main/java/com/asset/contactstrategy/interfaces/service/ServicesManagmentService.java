/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.service;

import com.asset.contactstrategy.common.dao.QueueDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.interfaces.dao.ServicesManagmentDAO;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.ServiceQuotaCounter;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Yomna Naser
 */
public class ServicesManagmentService {

    public HashMap<String, ServicesModel> getServices(Connection connection) throws CommonException {
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [getServices]... at " + new Date());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getServices Started")
                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());

        SystemLookups.QUEUE_LIST = new QueueDAO().getHashedApplicationById(connection);
        ServicesManagmentDAO servicesManagmentDAO = new ServicesManagmentDAO();
        HashMap<String, ServicesModel> services = servicesManagmentDAO.getServices(connection, SystemLookups.QUEUE_LIST);
//        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [getServices]... at " + new Date());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getServices Ended")
                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
        return services;
    }

//    public void updateSystemQuota(Connection connection, int systemID) throws CommonException {
//
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [updateSystemQuota]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSystemQuota Started")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//        ServicesManagmentDAO servicesManagmentDAO = new ServicesManagmentDAO();
//        servicesManagmentDAO.updateSystemQuota(connection, systemID);
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [updateSystemQuota]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSystemQuota Ended")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//    }
//
//    public int getDailyQoutaCounter(Connection connection, int serviceID) throws CommonException {
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [checkSystemQuota]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkSystemQuota Started")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//
//        ServicesManagmentDAO servicesManagmentDAO = new ServicesManagmentDAO();
//        int qoutaCounter = servicesManagmentDAO.getDailyQoutaCounter(connection, serviceID);
//
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [checkSystemQuota]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkSystemQuota Ended")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//        return qoutaCounter;
//    }
//
//    public long updateAndSelectServiceQouta(Connection connection, int serviceID, long serviceQouta) throws CommonException {
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [updateAndSelectServiceQouta]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateAndSelectServiceQouta Started")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//
//        long qoutaCounter = new ServicesManagmentDAO().updateAndSelectServiceQouta(connection, serviceID, serviceQouta);
//
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [updateAndSelectServiceQouta]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateAndSelectServiceQouta Ended")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//        return qoutaCounter;
//    }
//
//    public void updateSystemMonitorCounter(Connection connection, int systemID) throws CommonException {
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [updateSystemQuota]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSystemQuota Started")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//        ServicesManagmentDAO servicesManagmentDAO = new ServicesManagmentDAO();
//        servicesManagmentDAO.updateSystemMonitorCounter(connection, systemID);
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [updateSystemQuota]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateSystemQuota Ended")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//    }
//
//    public void updateDoNotApplyCounter(Connection connection, int systemID) throws CommonException {
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [updateDoNotApplyCounter]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateDoNotApplyCounter Started")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//        ServicesManagmentDAO servicesManagmentDAO = new ServicesManagmentDAO();
//        servicesManagmentDAO.updateDoNotApplyCounter(connection, systemID);
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [updateDoNotApplyCounter]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateDoNotApplyCounter Ended")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//    }
//
//    //Start | CSPhase1.5 | Esmail.Anbar | Adding RollBack Functions for SMS Operations
//    public void decrementServiceCounters(Connection connection, int systemID, boolean decrementDoNotApply,
//            boolean decrementMonitorCounter, boolean decrementDailyQouta) throws CommonException {
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "Starting Getting [decrementServiceCounters]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "decrementServiceCounters Started")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//        ServicesManagmentDAO servicesManagmentDAO = new ServicesManagmentDAO();
//        servicesManagmentDAO.decrementServiceCounters(connection, systemID, decrementDoNotApply, decrementMonitorCounter, decrementDailyQouta);
////        CommonLogger.businessLogger.debug(ServicesManagmentService.class.getName() + " || " + "End Getting [decrementServiceCounters]... at " + new Date());
//        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "decrementServiceCounters Ended")
//                .put(GeneralConstants.StructuredLogKeys.DATE, new Date()).build());
//    }
    public static int updateServiceQuota(Connection connection,
            Integer serviceId,
            Integer value,
            Long maxValue,
            String counterColumn) throws Exception {
        return ServicesManagmentDAO.updateServiceQuota(connection, serviceId, value, maxValue, counterColumn);
    }
    
    public static void updateServiceQuota(Connection connection,
            HashMap<Integer, ServiceQuotaCounter> serviceQuota) throws Exception {
        ServicesManagmentDAO.updateServiceQuota(connection, serviceQuota);
    }

    public static int getServiceQuota(Connection connection,
            Integer serviceId,
            String counterColumn) throws Exception {
        return ServicesManagmentDAO.getServiceQuota(connection, serviceId, counterColumn);
    }
    
    public static HashMap<Integer, ArrayList<ServiceQuotaCounter>> getServiceQuota(Connection connection) throws Exception {
        return ServicesManagmentDAO.getServiceQuota(connection);
    }

    public static int insertServiceQuota(Connection connection,
            Integer serviceId,
            Integer value,
            String counterColumn) throws Exception {
        return ServicesManagmentDAO.insertServiceQuota(connection, serviceId, value, counterColumn);
    }

    public static int adjustDuplicatedServiceQuota(Connection connection,
            Integer serviceId) throws Exception {
        return ServicesManagmentDAO.adjustDuplicatedServiceQuota(connection, serviceId);
    }
}
