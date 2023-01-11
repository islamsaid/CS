/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import com.asset.contactstrategy.common.models.DWHFilterValueModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.webmodels.DWHElementWebModel;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author Zain Al-Abedin
 */
public class DWHElementFacade {

    public ArrayList<DWHElementValueModel> loadCustomersAttributes() throws CommonException {

        ArrayList<DWHElementValueModel> usedColumns = new ArrayList<>();
        try {
            MainService mainService = new MainService();
            usedColumns = mainService.loadCustomersAttributes();

        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || loadCustomersAttributes" + e, ErrorCodes.LOAD_DWH_ELEMENTS_DB_ERROR);
            throw e;
        }
        return usedColumns;
    }

    public ArrayList<DWHElementValueModel> loadGovernmentAttributes() throws CommonException {

        ArrayList<DWHElementValueModel> usedColumns = new ArrayList<>();
        try {
            MainService mainService = new MainService();
            usedColumns = mainService.loadGovernmentAttributes();

        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || loadGovernmentAttributes" + e, ErrorCodes.LOAD_DWH_ELEMENTS_DB_ERROR);
            throw e;
        }
        return usedColumns;
    }

    public ArrayList<DWHElementWebModel> loadAllElementsFromDb() throws CommonException {
//        CommonLogger.businessLogger.debug(DWHElementFacade.class.getName() + " || " + "Starting loadAllElementsFromDb");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting loadAllElementsFromDb")
                .build());
        ArrayList<DWHElementModel> allElements = null;
        ArrayList<DWHElementWebModel> allWebElements = new ArrayList<DWHElementWebModel>();
        try {
            MainService mainService = new MainService();
            allElements = mainService.loadAllDWHElements();

            for (DWHElementModel element : allElements) {
                DWHElementWebModel webElement = new DWHElementWebModel(element);
                webElement.setDisplayTypeName(SystemLookups.DWH_DISPLAY_TYPES.get(element.getDisplayTypeId()).getLable());
                allWebElements.add(webElement);
            }

//            CommonLogger.businessLogger.debug(DWHElementFacade.class.getName() + " || " + "End loadAllElementsFromDb");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End loadAllElementsFromDb")
                    .build());

        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || loadAllElementsFromDb" + e, ErrorCodes.LOAD_DWH_ELEMENTS_DB_ERROR);
            throw e;
        }
        return allWebElements;
    }

    public ArrayList<String> loadDwhUsedColumns() throws CommonException {

        ArrayList<String> usedColumns = new ArrayList<>();
        try {
            MainService mainService = new MainService();
            usedColumns = mainService.loadDwhUsedColumns();

        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || loadDwhUsedColumns" + e, ErrorCodes.LOAD_DWH_ELEMENTS_DB_ERROR);
            throw e;
        }
        return usedColumns;
    }

    public void deleteElement(DWHElementWebModel webElement) throws CommonException {
//        CommonLogger.businessLogger.debug(DWHElementFacade.class.getName() + " || " + "Starting deleteElement");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting deleteElement")
                .build());
        try {
            MainService mainService = new MainService();
            mainService.deleteElement(webElement);
        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || deleteElement" + e, ErrorCodes.DELET_DWH_ELEMENT_DB_ERROR);
            throw e;
        }

    }

    public void deleteMultiSelectionVale(DWHElementValueModel elementLOValue) throws CommonException {
//        CommonLogger.businessLogger.debug(DWHElementFacade.class.getName() + " || " + "Starting deleteMultiSelectionVale");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting deleteMultiSelectionVale")
                .build());
        try {
            MainService mainService = new MainService();
            mainService.deleteMultiSelectionVale(elementLOValue);
        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || deleteMultiSelectionVale" + e, ErrorCodes.UNKOWN_ERROR);
            throw e;
        }
    }

    public void insertNewElement(DWHElementWebModel webElement) throws CommonException {
//        CommonLogger.businessLogger.debug(DWHElementFacade.class.getName() + " || " + "Starting insertNewElement");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting insertNewElement")
                .build());
        try {
            MainService mainService = new MainService();
            mainService.insertNewElement(webElement);
        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || insertNewElement" + e, ErrorCodes.UNKOWN_ERROR);
            throw e;
        }

    }

    public void updateDwhElement(DWHElementWebModel webElement) throws CommonException {
//        CommonLogger.businessLogger.debug(DWHElementFacade.class.getName() + " || " + "Starting updateDwhElement");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting updateDwhElement")
                .build());
        try {
            MainService mainService = new MainService();
            mainService.updateDwhElement(webElement);
        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || updateDwhElement" + e, ErrorCodes.UNKOWN_ERROR);
            throw e;
        }

    }

    public int removeFromList(DWHElementWebModel element, ArrayList<DWHElementWebModel> elementList) {
        int idx = -1;
        DWHElementWebModel removedElement = null;
        try {
            for (int x = 0; x < elementList.size(); x++) {
                DWHElementWebModel listElement = elementList.get(x);
                if (listElement.equals(element)) {
                    idx = x;
                    break;
                }
            }
            if (idx != -1) {
                removedElement = elementList.remove(idx);
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || removeFromList" + e, ErrorCodes.UNKOWN_ERROR);
            throw e;
        }
        return idx;
    }

    // added by kerollos.asaad
    public ArrayList<DWHFilterValueModel> convertElementValuesToFilterValues(ArrayList<DWHElementValueModel> elementValues) {
        ArrayList<DWHFilterValueModel> filterValues = null;
        try {
            for (DWHElementValueModel value : elementValues) {
                if (filterValues == null) {
                    filterValues = new ArrayList<DWHFilterValueModel>();
                }
                DWHFilterValueModel filterValue = new DWHFilterValueModel();
                filterValue.setValueId(value.getValueId());
                filterValue.setValueLabel(value.getValueLabel());
                filterValues.add(filterValue);
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || convertElementValuesToFilterValues" + e, ErrorCodes.UNKOWN_ERROR);
            throw e;
        }
        return filterValues;
    }

}
