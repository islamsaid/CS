/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customermatching.workers;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.customermatching.constants.EngineDefines;
import com.asset.cs.customermatching.managers.CustomerMatchingManager;
import static com.asset.cs.customermatching.managers.CustomerMatchingManager.adsMSISDNSQueue;
import static com.asset.cs.customermatching.managers.CustomerMatchingManager.adsfilesQueue;
import static com.asset.cs.customermatching.managers.CustomerMatchingManager.smsMSISDNSQueue;
import static com.asset.cs.customermatching.managers.CustomerMatchingManager.smsfilesQueue;
import com.asset.cs.customermatching.managers.ResourcesCachingManager;
import com.asset.cs.customermatching.models.StatusModel;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author kerollos.asaad
 */
public class ManagerWorker extends Thread {

    public static final String CLASS_NAME = "ManagerWorker";
    long sleepTimeInMs = 300000; //default 5 Minute

    @Override
    public void run() {
        while (!ResourcesCachingManager.shutdownEngineFlag.get()) {
            try {
                ResourcesCachingManager.loadResourcesAndConfigurations();
                if (checkNewDWHImport()) {

                    // Log
                    CommonLogger.businessLogger.info("Initializing Customer Matching Manager");

                    // SMS Worker 
                    CustomerMatchingManager.smsStatusModel = new StatusModel();
                    CustomerMatchingManager.smsStatusModel.setFinished(false);
                    CustomerMatchingManager.smsStatusModel.setStatus(GeneralConstants.FALSE);
                    //CustomerMatchingManager.smsfilesQueue = new ArrayBlockingQueue<>(100);
                    //CustomerMatchingManager.smsMSISDNSQueue = new ArrayBlockingQueue<>(100);
                    CustomerMatchingManager.smsGroupsWorker = new GroupsWorker(GeneralConstants.SMS_GROUP);
                    CustomerMatchingManager.smsGroupsWorker.setName("smsGroupsWorker");
                    CustomerMatchingManager.smsGroupsWorker.start();

                    // ADS Worker 
                    CustomerMatchingManager.adsStatusModel = new StatusModel();
                    CustomerMatchingManager.adsStatusModel.setFinished(false);
                    CustomerMatchingManager.adsStatusModel.setStatus(GeneralConstants.FALSE);
                    CustomerMatchingManager.adsGroupsWorker = new GroupsWorker(GeneralConstants.ADS_GROUP);
                    CustomerMatchingManager.adsGroupsWorker.setName("adsGroupsWorker");
                    CustomerMatchingManager.adsGroupsWorker.start();
                    //CustomerMatchingManager.adsfilesQueue = new ArrayBlockingQueue<>(100);
                    //CustomerMatchingManager.adsMSISDNSQueue = new ArrayBlockingQueue<>(100);

                    // CAMP Worker
                    CustomerMatchingManager.campStatusModel = new StatusModel();
                    CustomerMatchingManager.campStatusModel.setFinished(false);
                    CustomerMatchingManager.campStatusModel.setStatus(GeneralConstants.FALSE);
                    CustomerMatchingManager.campGroupsWorker = new GroupsWorker(GeneralConstants.CAMPAIGN_GROUP);
                    CustomerMatchingManager.campGroupsWorker.setName("campGroupsWorker");
                    CustomerMatchingManager.campGroupsWorker.start();
                    //CustomerMatchingManager.campfilesQueue = new ArrayBlockingQueue<>(100);
                    //CustomerMatchingManager.campMSISDNSQueue = new ArrayBlockingQueue<>(100);

                    CustomerMatchingManager.smsGroupsWorker.join();
                    CustomerMatchingManager.adsGroupsWorker.join();
                    CustomerMatchingManager.campGroupsWorker.join();

                    if (CustomerMatchingManager.smsStatusModel.getStatus() == GeneralConstants.TRUE
                            && CustomerMatchingManager.adsStatusModel.getStatus() == GeneralConstants.TRUE
                            && CustomerMatchingManager.campStatusModel.getStatus() == GeneralConstants.TRUE) {
                        CustomerMatchingManager.getInstance().setStatus(GeneralConstants.TRUE);//represents Success
                    } else {
                        CustomerMatchingManager.getInstance().setStatus(GeneralConstants.FALSE);//represents Failure
                    }
                    CustomerMatchingManager.mainServ.updateMatchingInstanceModel(CustomerMatchingManager.getInstance());
                    CommonLogger.businessLogger.info("Matching Manager Initialized Successfully");

                } else {
                    try {
                        long sleepTimeInMinutes = ResourcesCachingManager.getLongValue(EngineDefines.GROUPS_THREAD_SLEEP_TIME);
                        sleepTimeInMs = sleepTimeInMinutes * 60 /*(sec)*/ * 1000 /*(ms)*/;
                    } catch (Exception e) {
                        CommonLogger.errorLogger.error("Fatal Error Reading Sleep Time --->", e);
                        CommonLogger.businessLogger.error("Fatal Error  Reading Sleep Time -->" + e);
                    }
                    if (!ResourcesCachingManager.shutdownEngineFlag.get()) {
//                        CommonLogger.businessLogger.info("Engine Will Sleep For [" + sleepTimeInMs + "]msec");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Engine Will Sleep")
                                .put(GeneralConstants.StructuredLogKeys.SLEEP_TIME, sleepTimeInMs).build());
                        this.sleep(sleepTimeInMs);
                    }
                }
            } catch (CommonException e) {
                CommonLogger.errorLogger.error("Fatal Error-->" + e, e);
                CommonLogger.businessLogger.error("Fatal Error-->" + e);
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Fatal Error-->" + e, e);
                CommonLogger.businessLogger.error("Fatal Error-->" + e);
            }
        }
    }

    public boolean checkNewDWHImport() {
        if (CustomerMatchingManager.getInstance().getRunId() < ResourcesCachingManager.getImportRunId()) {
            CustomerMatchingManager.getInstance().setRunId(ResourcesCachingManager.getImportRunId());
            return true;
        } else {
            return false;
        }
    }
}
