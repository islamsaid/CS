/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.AdsGroupDAO;
import com.asset.contactstrategy.common.dao.CampaignDAO;
import com.asset.contactstrategy.common.dao.SMSCDAO;
import com.asset.contactstrategy.common.dao.SMSGroupDAO;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.CustomersCampaignsModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.interfaces.models.CustomerCampaignsModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author rania.magdy
 */
public class CampaignService {

    public boolean checkFileStatus(Connection connection, int fileId) throws CommonException {
        CampaignDAO groupDAO = new CampaignDAO();
        return groupDAO.checkFileStatus(connection, fileId);
    }

    public void updateFileStatus(Connection connection, int fileId, int fileStatus) throws CommonException {
        CampaignDAO groupDAO = new CampaignDAO();
        groupDAO.updateFileStatus(connection, fileId, fileStatus);
    }

    public void updateFilesStatus(Connection connection) throws CommonException {
        CampaignDAO groupDAO = new CampaignDAO();
        groupDAO.updateFilesStatus(connection);
    }

    public Vector<CampaignModel> getApprovedActiveCampaignsList(Connection conn) throws CommonException {
        CampaignDAO groupDAO = new CampaignDAO();
        return groupDAO.getApprovedActiveCampaignsList(conn);
    }

    public ArrayList<CampaignModel> getCampaigns(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class.getName() + " || " + "Starting  [getCampaigns]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaigns Started").build());
            CampaignDAO campaignDAO = new CampaignDAO();
            ArrayList<CampaignModel> campaignModels = campaignDAO.getCampaigns(connection);
//            CommonLogger.businessLogger.debug(CampaignService.class.getName() + " || " + "End [getCampaigns]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaigns Ended").build());

            return campaignModels;
        } catch (CommonException e) {
            throw e;
        }
    }

    public CampaignModel retrieveConnectedCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {
        CampaignModel returnedModel = null;
        try {

//            CommonLogger.businessLogger.debug(CampaignService.class.getName() + " || " + "Starting  [retrieveConnectedCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaigns Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();
            returnedModel = campaignDAO.retrieveConnectedCampaign(connection, campaignModel);
//            CommonLogger.businessLogger.debug(CampaignService.class.getName() + " || " + "End [retrieveConnectedCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "retrieveConnectedCampaign Ended").build());

        } catch (CommonException e) {
            throw e;
        }
        return returnedModel;
    }

    public void deleteCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class.getName() + " || " + "Starting  [deleteCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCampaign Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();

            campaignDAO.deleteCampaignServices(connection, campaignModel);//2
            campaignDAO.deleteCampaignFiles(campaignModel.getVersionId(), connection);//3
            //campaignDAO.deleteCampaign(connection, campaignModel);//1
            for (FilterModel el : campaignModel.getFilterList()) {
                campaignDAO.deleteDwhFiltersListOfValues(el.getFilterId(), connection);//4
            }
            campaignDAO.deleteDWHElementsPerCampaign(campaignModel.getVersionId(), connection);//5
            campaignDAO.deleteCampaign(connection, campaignModel);//1
//            CommonLogger.businessLogger.debug(CampaignService.class.getName() + " || " + "End [deleteCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteCampaign Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public void ChangeCampaignStatusToDelete(Connection connection, CampaignModel campaignModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "Starting  [ChangeCampaignStatusToDelete]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatusToDelete Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();

            campaignDAO.ChangeCampaignStatusToDelete(connection, campaignModel);

//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "End [ChangeCampaignStatusToDelete]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatusToDelete Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public void editCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "Starting  [editCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editCampaign Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();

            campaignDAO.editCampaign(connection, campaignModel);

//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "End [editCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editCampaign Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public void editEditedCampaign(Connection connection, CampaignModel campaignModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "Starting  [editEditedCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedCampaign Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();

            campaignDAO.editEditedCampaign(connection, campaignModel);

//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "End [editEditedCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "editEditedCampaign Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public CampaignModel createCampaign(Connection connection, CampaignModel campaignModel, ArrayList<ServiceModel> serviceList) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "Starting  [createCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createCampaign Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();
            campaignModel = campaignDAO.createCampaign(connection, campaignModel);

            campaignDAO.insertCampaignServices(connection, campaignModel, serviceList);

//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "End [createCampaign]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "createCampaign Ended").build());

            return campaignModel;
        } catch (CommonException e) {
            throw e;
        }
    }

    public void ChangeCampaignStatus(Connection connection, CampaignModel campaignModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "Starting  [ChangeCampaignStatus]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatus Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();

            campaignDAO.ChangeCampaignStatus(connection, campaignModel);

//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "End [ChangeCampaignStatus]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "ChangeCampaignStatus Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public CampaignModel getCampaignByName(Connection connection, String name) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "Starting  [getCampaignByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByName Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();
            CampaignModel campaignModel = campaignDAO.getCampaignByName(connection, name);

//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "End [getCampaignByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByName Ended").build());

            return campaignModel;
        } catch (CommonException e) {
            throw e;
        }
    }

    public CampaignModel getCampaignByPriority(Connection connection, int priority) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "Starting  [getCampaignByPriority]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByPriority Started").build());

            CampaignDAO campaignDAO = new CampaignDAO();
            CampaignModel campaignModel = campaignDAO.getCampaignByPriority(connection, priority);

//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "End [getCampaignByPriority]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getCampaignByPriority Ended").build());

            return campaignModel;
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<FilterModel> retrieveCampaignFilters(long campaignId, Connection conn) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "Starting  [retrieveCampaignFilters]...");

            CampaignDAO campaignDAO = new CampaignDAO();
            ArrayList<FilterModel> list = campaignDAO.retrieveCampaignFilters(campaignId, conn);

//            CommonLogger.businessLogger.debug(CampaignService.class
//                    .getName() + " || " + "End [retrieveCampaignFilters]...");
            return list;
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<FileModel> retrieveCampaignFiles(long campaignId, Connection conn) throws CommonException {
        try {
            CommonLogger.businessLogger.debug(CampaignService.class
                    .getName() + " || " + "Starting  [retrieveCampaignFiles]...");

            CampaignDAO campaignDAO = new CampaignDAO();
            ArrayList<FileModel> list = campaignDAO.retrieveCampaignFiles(campaignId, conn);

            CommonLogger.businessLogger.debug(CampaignService.class
                    .getName() + " || " + "End [retrieveCampaignFiles]...");

            return list;
        } catch (CommonException e) {
            throw e;
        }
    }

    public void ChangeCampaignStatusToApproved(Connection connection, CampaignModel campaignModel) throws CommonException {
        try {
            CommonLogger.businessLogger.debug(CampaignService.class
                    .getName() + " || " + "Starting  [ChangeCampaignStatusToApproved]...");
            CampaignDAO campaignDAO = new CampaignDAO();

            campaignDAO.ChangeCampaignStatusToApproved(connection, campaignModel);

            CommonLogger.businessLogger.debug(CampaignService.class
                    .getName() + " || " + "End [ChangeCampaignStatusToApproved]...");
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<CustomersCampaignsModel> getCustomerCampaignsByCampaign(Connection connection, Integer runId, Long campaignId, Integer suspendedNumber) throws CommonException {
        String methodName = "getCustomerCampaignsByCampaign";
        try {
            CommonLogger.businessLogger.debug(CampaignService.class
                    .getName() + " || " + "Starting  [" + methodName + "]...");
            CampaignDAO campaignDAO = new CampaignDAO();
            ArrayList<CustomersCampaignsModel> result = campaignDAO.getCustomerCampaignsByCampaign(connection, runId, campaignId, suspendedNumber);
            CommonLogger.businessLogger.debug(CampaignService.class.getName() + " || " + "End [" + methodName + "]...");
            return result;
        } catch (CommonException e) {
            throw e;
        }
    }

}
