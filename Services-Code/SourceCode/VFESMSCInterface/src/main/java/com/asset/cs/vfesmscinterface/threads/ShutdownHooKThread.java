/**
 * created on: Jan 13, 2018 11:52:52 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.threads;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.vfesmscinterface.initializer.Manager;

/**
 * @author mohamed.morsy
 *
 */
public class ShutdownHooKThread extends Thread {

	@Override
	public void run() {
		Thread.currentThread().setName("ShutdownHooKThread");
		CommonLogger.businessLogger.debug("ShutdownHooKThread started successfully");
		try {
			Manager.shutdownSystem();
		} catch (Throwable th) {
			CommonLogger.businessLogger.error(th.getMessage());
			CommonLogger.errorLogger.error(th.getMessage(), th);
		}
		CommonLogger.businessLogger.debug("ShutdownHooKThread closed successfully");
	}

}
