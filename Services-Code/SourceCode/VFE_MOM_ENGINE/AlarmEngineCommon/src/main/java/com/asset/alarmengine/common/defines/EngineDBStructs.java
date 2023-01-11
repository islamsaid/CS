/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.defines;

/**
 *
 * @author Mostafa Kashif
 */
public class EngineDBStructs {

    public static class SYSTEM_PROPERTIES {

        public static final String SYSTEM_PROPERTIES_TBL = "VFE_CS_SYSTEM_PROPERTIES";
        public static final String CONSTANT_KEY = "ITEM_KEY";
        public static final String DESCRIPTION = "DESCRIPTION";
        public static final String CONSTANT_VALUE = "ITEM_VALUE";
        public static final String GROUP_ID_COLUMN = "GROUP_ID"; // set it to null if table doesn't contain groups
        public static final String GROUP_ID = "813";            // set it to null if table doesn't contain groups
    }

    /////////////////////////////// H_SERVICE_ERRORS ////////////////////////////////////
    public static class SERVICE_ERRORS {

        public static final String SERVICE_ERRORS_TBL = "VFE_CS_MOM_ERRORS";
        public static final String SERVICE_ERRORS_SEQ = "VFE_CS_MOM_ERRORS_SEQ";
        public static final String ID = "ERROR_ID";
//        public static final String ERROR_TIME = "ENTRY_DATE";
        public static final String INSERTION_TIME = "ENTRY_DATE";
//        public static final String MSISDN = "MSISDN";
        public static final String ERROR_PARAMS = "ERROR_PARAMS";
        public static final String ERROR_TYPE = "SRC_ID";//src id
//        public static final String TX_ID = "TX_ID";
//        public static final String ERROR_CODE = "ERROR_CODE";
        public static final String ERROR_MESSAGE = "ERROR_MESSAGE";
        public static final String ERROR_SEVERITY = "PERCEIVED_SEVERITY";
    }
    //////////////////////////////// LOOKUP ERROR TYPES TABLE ///////////////////////////

    public static class LK_ERRORS_TYPES {

        public static final String LK_ERROR_TYPES_TBL = "VFE_CS_ERROR_CODES_LK";
        public static final String ERROR_TYPE_ID = "ERROR_CODE";
        public static final String ERROR_TYPE_LABEL = "ERROR_DESCRIPTION";
    }
}
