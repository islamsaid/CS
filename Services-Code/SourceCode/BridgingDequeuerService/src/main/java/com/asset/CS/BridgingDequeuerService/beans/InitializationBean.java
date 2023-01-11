/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.CS.BridgingDequeuerService.beans;

import javax.annotation.PostConstruct;
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;

/**
 *
 * @author mostafa.kashif
 */

public class InitializationBean {
    
       
	@PostConstruct
	public void initIt() throws Exception {
            Defines.runningProjectId=GeneralConstants.SRC_ID_DEQUEUER_WEB_SERVICE;
    	           Initializer.readPropertiesFile();
                   Initializer.initializeLoggers();
                   Initializer.initializeDataSource();
                   Initializer.attachShutDownHook(CommonLogger.businessLogger, ManagerBean.DEQUEUER_SERVICE_SHUTFOWN_FLAG,new ManagerBean(), "Dequeuer Web Service");
                 //  this.startReloadThread();
                   
	}
        
//          private  void startReloadThread() {
//       // this.reloadingThread=(ReloadingThread)SpringConfiguration.applicationContext.getBean(ReloadingThread.class);
//          this.reloadingThread.start();
//    }

}
