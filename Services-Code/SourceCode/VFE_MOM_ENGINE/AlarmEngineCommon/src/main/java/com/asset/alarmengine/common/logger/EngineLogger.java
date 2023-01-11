/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.logger;

import org.apache.log4j.Logger;

/**
 *
 * @author Mostafa Kashif
 */
public class EngineLogger {
    
	public static Logger debugLogger = null;
	public static Logger errorLogger = null;
	public EngineLogger() {
		initLoggers();
	}

	public static void initLoggers() {
		try {
			debugLogger = Logger.getLogger("ENGINEDebug");
			errorLogger = Logger.getLogger("ENGINEError");
		} catch (Exception e) {
			System.out.println("Failed to initialize Loggers!");
			e.printStackTrace();
		}
	}



	
}
