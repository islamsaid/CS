/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.workers;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.managerEngine.managers.Manager;
import org.apache.log4j.spi.ErrorCode;

/**
 *
 * @author Administrator
 */
public class KeepAliveWorker extends CCWorker {

    public void run() {
        while (!isWorkerShutDownFlag()) {
            try {
                if (!Manager.getInstancesWorker().isAlive()) {
                    CommonLogger.businessLogger.info("InstancesWorker Caught Exception and dead, it will start again");
                    sendMom(ErrorCodes.INSTANCES_MANAGER_DEAD, "InstancesWorker Caught Exception and dead, it will start again",
                            "com.asset.cs.managerEngine.workers.InstancesWorker", "");
                    InstancesWorker x = new InstancesWorker();
                    x.setName("InstancesWorker");
                    Manager.setInstancesWorker(x);
                    Manager.getInstancesWorker().start();
                }

                if (!Manager.getStatisticsWorker().isAlive()) {
                    CommonLogger.businessLogger.info("CustomerStatisticsWorker Caught Exception and dead, it will start again");
                    sendMom(ErrorCodes.STATISTICS_MANAGER_DEAD, "CustomerStatisticsWorker Caught Exception and dead, it will start again",
                            "com.asset.cs.managerEngine.workers.CustomersStatisticsWorker", "");
                    Manager.setStatisticsCatchingError(false);
                    CustomersStatisticsWorker x = new CustomersStatisticsWorker();
                    x.setName("CustomerStatisticsWorker");
                    Manager.setStatisticsWorker(x);
                    Manager.getStatisticsWorker().start();
                }
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Fatal Error -->", e);
                CommonLogger.businessLogger.error("Fatal Error -->"+ e);
            }
        }

    }

    private void sendMom(String errorCode, String errorMsg, String className, String errorParam) {
        MOMErrorsModel errorModel = new MOMErrorsModel();
        errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_CRITICAL);
        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_MANAGER_ENGINE);
        errorModel.setErrorCode(errorCode);
        errorModel.setErrorMessage(errorMsg);
        errorModel.setModuleName(className);
        errorModel.setErrorParams(errorParam);
        Utility.sendMOMAlarem(errorModel);
    }

}
