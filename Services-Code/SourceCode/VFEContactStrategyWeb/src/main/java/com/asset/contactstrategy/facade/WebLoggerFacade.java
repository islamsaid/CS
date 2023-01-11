/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.webmodels.DWHElementWebModel;

/**
 *
 * @author Zain Al-Abedin
 */
public class WebLoggerFacade {
    
     public static void insertWebLog (WebLogModel logModel) throws CommonException {   
        try {
            MainService mainService = new MainService();
            mainService.insertWebLog(logModel);
        }catch (Exception e) {
            CommonLogger.errorLogger.error(DWHElementFacade.class.getName() + " || insertWebLog" + e,ErrorCodes.UNKOWN_ERROR);
        }
            
    }
    
}
