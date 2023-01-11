/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Kerollos Asaad
 */
public class CommonLogger {

//    public static Logger businessLogger = null;
    public static Logger businessLogger = null;
    public static Logger errorLogger = null;
    public static Logger livenessLogger = null;

    public CommonLogger() {
        initLoggers();
    }

    public static void initLoggers() {
        try {
//            if (businessLogger == null) {
//                businessLogger = LogManager.getLogger("debugLogger");
//            }
            if (businessLogger == null) {
                businessLogger = LogManager.getLogger("businessLogger");
            }
            if (errorLogger == null) {
                errorLogger = LogManager.getLogger("errorLogger");
            }
            if (livenessLogger == null) {
                livenessLogger = LogManager.getLogger("livenessLogger");
            }
        } // end try // end try // end try // end try
        catch (Exception ex) {
            System.out.println("Loggers Initialization Failed...");
            ex.printStackTrace();
        } // end catch
    } // end initLoggers

}
