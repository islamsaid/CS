/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.CommonDAO;
import com.asset.contactstrategy.common.dao.ServiceManagmentDAO;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.models.QueueModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Amal Magdy
 */
public class ServiceManagmentService {

    public ArrayList<ServiceModel> getServices(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting getServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getServices Started").build());

            ServiceManagmentDAO ServiceManagmentDAO = new ServiceManagmentDAO();
            ArrayList<ServiceModel> serviceModels = ServiceManagmentDAO.getServices(connection);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End getServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getServices Ended").build());

            return serviceModels;
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<ServiceModel> getApprovedServices(Connection connection,boolean serviceSupportAds) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting getApprovedServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedServices Started").build());

            ServiceManagmentDAO ServiceManagmentDAO = new ServiceManagmentDAO();
            ArrayList<ServiceModel> serviceModels = ServiceManagmentDAO.getApprovedServices(connection,serviceSupportAds);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End getApprovedServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApprovedServices Ended").build());

            return serviceModels;
        } catch (CommonException e) {
            throw e;
        }
    }

    public boolean checkService(Connection connection, String serviceName) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting checkService...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkService Started").build());

            ServiceManagmentDAO ServiceManagmentDAO = new ServiceManagmentDAO();
            boolean serviceAvalibility = ServiceManagmentDAO.checkService(connection, serviceName);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End checkService...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkService Ended").build());

            return serviceAvalibility;
        } catch (CommonException e) {
            throw e;
        }
    }

    public boolean checkIpAddress(Connection connection, String serviceName, String ipAddress) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting checkIpAddress...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkIpAddress Started").build());

            ServiceManagmentDAO ServiceManagmentDAO = new ServiceManagmentDAO();
            boolean IpAddress = ServiceManagmentDAO.checkIpAddress(connection, serviceName, ipAddress);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End checkIpAddress...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkIpAddress Ended").build());

            return IpAddress;
        } catch (CommonException e) {
            throw e;
        }
    }

    void insertService(Connection connection, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting [insertService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertService Started").build());

            ServiceManagmentDAO serviceManagmentDAO = new ServiceManagmentDAO();
//            serviceModel.setId(CommonDAO.getNextId(connection, DBStruct.SERVICE.SEQUENCE_SEC));
//            serviceModel.setServiceID(CommonDAO.getNextId(connection, DBStruct.SERVICE.SEQUENCE));
            serviceManagmentDAO.insertService(connection, serviceModel);
            serviceManagmentDAO.insertServiceWhiteList(connection, serviceModel, serviceWhitelistIPs);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End [insertService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertService Ended").build());

        } catch (CommonException e) {
            throw e;
        }

    }

    void deleteAllServiceVersions(Connection connection, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting [deleteAllServiceVersions]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAllServiceVersions Started").build());

            ServiceManagmentDAO serviceManagmentDAO = new ServiceManagmentDAO();
            serviceManagmentDAO.deleteALLServiceWhietListVersions(connection, serviceModel);
            serviceManagmentDAO.deleteAllServiceVersions(connection, serviceModel);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End [deleteAllServiceVersions]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAllServiceVersions Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    void updateService(Connection connection, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting [updateService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateService Started").build());

            ServiceManagmentDAO serviceManagmentDAO = new ServiceManagmentDAO();
            serviceManagmentDAO.updateOneServiceVersion(connection, serviceModel);
            serviceManagmentDAO.insertServiceWhiteList(connection, serviceModel, serviceWhitelistIPs);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End [updateService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateService Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }
    
    void updateServiceReInsertWhiteList(Connection connection, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting [updateService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateServiceReInsertWhiteList Started").build());

            ServiceManagmentDAO serviceManagmentDAO = new ServiceManagmentDAO();
            serviceManagmentDAO.updateOneServiceVersion(connection, serviceModel);
            serviceManagmentDAO.deleteALLServiceWhietListVersions(connection, serviceModel);
            serviceManagmentDAO.insertServiceWhiteList(connection, serviceModel, serviceWhitelistIPs);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End [updateService]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateServiceReInsertWhiteList Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    void deleteService(Connection connection, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting [deleteAllServiceVersions]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAllServiceVersions Started").build());

            ServiceManagmentDAO serviceManagmentDAO = new ServiceManagmentDAO();
            serviceManagmentDAO.deleteOneServiceVersion(connection, serviceModel);
            serviceManagmentDAO.deleteOneServiceWhietListVersion(connection, serviceModel);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End [deleteAllServiceVersions]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteAllServiceVersions Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public ServiceModel getParentServiceVersion(Connection connection, long serviceID) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting getParentServiceVersion...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getParentServiceVersion Started").build());

            ServiceManagmentDAO ServiceManagmentDAO = new ServiceManagmentDAO();
            ServiceModel serviceModel = ServiceManagmentDAO.getParentServiceVersion(connection, serviceID);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End getParentServiceVersion...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getParentServiceVersion Ended").build());

            return serviceModel;
        } catch (CommonException e) {
            throw e;
        }
    }

    public String hasReferencetoQueue(Connection connection, QueueModel queueModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting hasReferencetoQueue...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "hasReferencetoQueue Started").build());

            ServiceManagmentDAO serviceManagmentDAO = new ServiceManagmentDAO();
            String referenceFound = serviceManagmentDAO.checkQueueReference(connection, queueModel.getVersionId());
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End hasReferencetoQueue...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "hasReferencetoQueue Ended").build());

            return referenceFound;
        } catch (CommonException e) {
            throw e;
        }
    }

    //////////////Fix issue ////////////////
    public ArrayList<ServiceModel> getCampaignServices(Connection connection, CampaignModel campaignModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting getCampaignServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignServices Started").build());

            ServiceManagmentDAO ServiceManagmentDAO = new ServiceManagmentDAO();
            ArrayList<ServiceModel> serviceModels = ServiceManagmentDAO.getCampaignServices(connection, campaignModel);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End getCampaignServices...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignServices Ended").build());

            return serviceModels;
        } catch (CommonException e) {
            throw e;
        }
    }

    ///////////////End Fix Issue /////////////////
    public Boolean checkCampaignServiceByService(ServiceModel service, Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "Starting checkCampaignServiceByService...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkCampaignServiceByService Started").build());

            ServiceManagmentDAO serviceManagmentDAO = new ServiceManagmentDAO();
            Boolean campaignServiceExists = serviceManagmentDAO.checkCampaignServiceByService(service,connection);
//            CommonLogger.businessLogger.debug(ServiceManagmentService.class.getName() + " || " + "End checkCampaignServiceByService...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "checkCampaignServiceByService Ended").build());

            return campaignServiceExists;
        } catch (CommonException e) {
            throw e;
        }
    }
}
