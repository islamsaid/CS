/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.DWHElementDAO;
import com.asset.contactstrategy.common.dao.DWHElementValueDao;
import com.asset.contactstrategy.common.dao.QueueDAO;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import com.asset.contactstrategy.common.models.QueueModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Zain Al-Abedin
 */
public class DWHElementService {

    public ArrayList<DWHElementValueModel> loadCustomersAttributes(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting loadCustomersAttributes");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadCustomersAttributes Started").build());

            DWHElementDAO ElementDAO = new DWHElementDAO();
            ArrayList<DWHElementValueModel> elementsList = ElementDAO.loadCustomersAttributes(connection);
//           CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End loadCustomersAttributes");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadCustomersAttributes Ended").build());

            return elementsList;
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<DWHElementValueModel> loadGovernmentAttributes(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting loadGovernmentAttributes");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadGovernmentAttributes Started").build());

            DWHElementDAO ElementDAO = new DWHElementDAO();
            ArrayList<DWHElementValueModel> elementsList = ElementDAO.loadGovernmentAttributes(connection);
//           CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End loadGovernmentAttributes");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadGovernmentAttributes Ended").build());

            return elementsList;
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<DWHElementModel> loadAllElements(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting loadAllElements");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadAllElements Started").build());

            DWHElementDAO ElementDAO = new DWHElementDAO();
            ArrayList<DWHElementModel> elementsList = ElementDAO.loadAllElements(connection);
//           CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End loadAllElements");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "loadAllElements Ended").build());

            return elementsList;
        } catch (CommonException e) {
            throw e;
        }
    }

    public Vector<DWHElementModel> getDWHElementsList(Connection connection) throws CommonException {
        try {
            //  CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting getDWHElementsList");
            DWHElementDAO ElementDAO = new DWHElementDAO();
            Vector<DWHElementModel> elementsList = ElementDAO.getDWHElementsList(connection);
            //   CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End getDWHElementsList");
            return elementsList;
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<String> loadDwhUsedColumns(Connection connection) throws CommonException {
        try {
            //  CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting loadDwhUsedColumns");
            DWHElementDAO ElementDAO = new DWHElementDAO();
            ArrayList<String> usedColumns = ElementDAO.loadDwhUsedColumns(connection);
            //  CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End loadDwhUsedColumns");
            return usedColumns;
        } catch (CommonException e) {
            throw e;
        }

    }

    public void deleteElement(Connection connection, DWHElementModel element) throws CommonException {
//       CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting deleteElement");
//        CommonLogger.businessLogger.debug("Delete DWH Element => " + element.toString());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteElement Started")
                .put(GeneralConstants.StructuredLogKeys.ELEMENT_NAME, element.toString()).build());

        try {
            DWHElementDAO dao = new DWHElementDAO();
            DWHElementValueDao valuesDao = new DWHElementValueDao();
            valuesDao.deleteListOfValues(connection, element.getElementId());
            dao.deleteElement(connection, element.getElementId());
//            CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End deleteElement");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteElement Ended").build());
        } catch (CommonException e) {
            throw e;
        }
    }

    public void insertNewDWHElement(DWHElementModel element, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting insertNewDWHElement");
//        CommonLogger.businessLogger.debug("Insert DWH Element => " + element.toString());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertNewDWHElement Started")
                .put(GeneralConstants.StructuredLogKeys.ELEMENT_NAME, element.toString()).build());
        try {
            DWHElementDAO dao = new DWHElementDAO();
            dao.insertNewElement(element, connection);
//            CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End insertNewDWHElement");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertNewDWHElement Ended")
                    .put(GeneralConstants.StructuredLogKeys.ELEMENT_NAME, element.toString()).build());
        } catch (Exception e) {
            throw e;
        }
    }

    public void insertElementLOV(DWHElementModel element, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting insertElementLOV");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertElementLOV Started").build());

        String elementId = null;
        try {

            DWHElementValueDao dao = new DWHElementValueDao();
            dao.insertListOfValues(element, connection);
//            CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End insertElementLOV");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertElementLOV Ended").build());

        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteMultiSelectionValue(DWHElementValueModel elementLOValue, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting deleteMultiSelectionValue");
//        CommonLogger.businessLogger.debug("Update DWH Element Multi-Selection Value => " + elementLOValue.toString());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteMultiSelectionValue Started")
                .put(GeneralConstants.StructuredLogKeys.ELEMENT_NAME, elementLOValue.toString()).build());

        try {
            DWHElementValueDao lovDao = new DWHElementValueDao();
            lovDao.deleteMultiSelectionValue(elementLOValue, connection);
//            CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End deleteMultiSelectionValue");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteMultiSelectionValue Ended").build());
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateDwhElement(DWHElementModel element, Connection connection) throws CommonException {
//        CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "Starting updateDwhElement");
//        CommonLogger.businessLogger.debug("Update DWH Element => " + element.toString());
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateDwhElement Started")
                .put(GeneralConstants.StructuredLogKeys.ELEMENT_NAME, element.toString()).build());
        try {
            DWHElementDAO dao = new DWHElementDAO();
            dao.updateElement(element, connection);
            DWHElementValueDao lovDao = new DWHElementValueDao();
            if (element.getDisplayTypeId() == Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION) {
                lovDao.updateMergeListOfValues(element, connection);
                //lovDao.deleteListOfValues(connection, element.getElementId());
                //  insertElementLOV(element, connection);
            }
//            CommonLogger.businessLogger.info(DWHElementService.class.getName() + " || " + "End updateDwhElement");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateDwhElement Ended").build());
        } catch (Exception e) {
            throw e;
        }
    }
}
