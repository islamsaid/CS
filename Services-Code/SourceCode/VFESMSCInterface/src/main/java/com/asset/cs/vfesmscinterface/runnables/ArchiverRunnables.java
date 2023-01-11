/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.runnables;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LogModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author mostafa.kashif
 */
public class ArchiverRunnables implements Runnable {

    private BlockingQueue<LogModel> queue = null;
    private static MainService mainService = new MainService();

    public ArchiverRunnables(BlockingQueue<LogModel> queue) {
        this.queue = queue;
        CommonLogger.businessLogger.debug("creating ArchiverThread");
    }

    @Override
    public void run() {
        try {
            CommonLogger.businessLogger.debug("Archiver started successfully");

            long startTime = System.currentTimeMillis();

            Thread.currentThread().setName("ArchiverThread");

            List<LogModel> logModels = new ArrayList<>();
            while (!Manager.isShutdown.get()) {
                LogModel logModel = queue.poll();
                if (logModel != null) {
                    logModels.add(logModel);
                }

                if (logModels.size() == Defines.SMSC_INTERFACE_PROPERTIES.LOG_BATCH_SIZE_VALUE) {
                    //insert into database
//                    CommonLogger.businessLogger.debug("start inserting logs into the database LOG_SIZE=" + logModels.size());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Interesting Logs into the Database")
                            .put(GeneralConstants.StructuredLogKeys.SIZE, logModels.size()).build());
                    long batchStartime = System.currentTimeMillis();
                    mainService.insertLog(logModels);
                    logModels.clear();
//                    CommonLogger.businessLogger.debug("Log batch inserted successfully in [" + (System.currentTimeMillis() - batchStartime) + "]ms");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Log batch inserted successfully")
                            .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - batchStartime)).build());
                }

                if (logModel == null && !logModels.isEmpty()) {
                    //insert into database
//                    CommonLogger.businessLogger.debug("[logModel==null]start inserting logs into the database LOG_SIZE=" + logModels.size());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "started inserting logs into database")
                            .put(GeneralConstants.StructuredLogKeys.SIZE, logModels.size()).build());
                    mainService.insertLog(logModels);
                    logModels.clear();
                    CommonLogger.businessLogger.debug("[logModel==null]Logs inserted successfully");
                } else {
                    long sleepTime = Long.valueOf(Manager.systemProperities.get(Defines.SMSC_INTERFACE_PROPERTIES.ARCHIVER_THREAD_SLEEP_TIME));
                    Thread.sleep(sleepTime);
                }
            }
            if (Manager.isShutdown.get() && !logModels.isEmpty()) {
//                CommonLogger.businessLogger.debug("[Manager not running]start inserting logs into the database LOG_SIZE=" + logModels.size());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started inserting Logs into database")
                        .put(GeneralConstants.StructuredLogKeys.SIZE, logModels.size()).build());
                mainService.insertLog(logModels);
                CommonLogger.businessLogger.debug("[Manager not running]Logs inserted successfully");
                logModels.clear();
            }

            CommonLogger.businessLogger.debug("Archiver closed successfully");
//            CommonLogger.businessLogger.debug("Archiver job time :" + (System.currentTimeMillis() - startTime));
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Archiver Job Timing")
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        } catch (InterruptedException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        } catch (CommonException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }
}
