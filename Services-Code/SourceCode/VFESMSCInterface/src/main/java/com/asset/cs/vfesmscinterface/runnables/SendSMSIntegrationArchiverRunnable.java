/**
 * created on: Jan 9, 2018 6:17:52 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.runnables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CsSmscInterfaceHistoryModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.cs.vfesmscinterface.initializer.Manager;

/**
 * @author mohamed.morsy
 *
 */
public class SendSMSIntegrationArchiverRunnable implements Runnable {

    private static int sid = 1;

    private static MainService mainService = new MainService();

    private int id;

    private List<CsSmscInterfaceHistoryModel> logBatchList;

    private BlockingQueue<CsSmscInterfaceHistoryModel> queue = null;

    public SendSMSIntegrationArchiverRunnable(BlockingQueue<CsSmscInterfaceHistoryModel> queue) {
        this.queue = queue;
        this.id = sid++;
        logBatchList = new ArrayList<>();
    }

    @Override
    public void run() {
        long time;
        int drainedBatchSize = 0;
        Thread.currentThread().setName("SendSMSIntegrationArchiver" + id);
        CommonLogger.businessLogger.debug("SendSMSIntegrationArchiver started successfully");
        while (!Manager.isShutdown.get() || !queue.isEmpty()) {
            try {
                drainedBatchSize = queue.drainTo(logBatchList,
                        Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_LOG_BATCH_SIZE_VALUE);

                if (drainedBatchSize != 0) {
//					CommonLogger.businessLogger
//							.debug("start inserting CsSmsInterfaceHistoryModel, batch count=" + drainedBatchSize);
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Inserting CSSmsInterfaceHistoryModel")
                            .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, drainedBatchSize).build());
                    time = System.currentTimeMillis();
                    mainService.insertCsSmsInterfaceHistoryModel(logBatchList);
//                    CommonLogger.businessLogger
//                            .debug("end inserting CsSmsInterfaceHistoryModel inserted successfully, logging time :"
//                                    + (System.currentTimeMillis() - time) + " msec");
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End Inserting CsSMSInterfaceHistoryModel Inserted successfully")
                            .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time)).build());
                    logBatchList.clear();
                } else {
                    time = Long.valueOf(Manager.systemProperities
                            .get(Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_ARCHIVER_THREAD_SLEEP_TIME));
                    TimeUnit.MILLISECONDS.sleep(time);
                }
            } catch (Exception e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
        CommonLogger.businessLogger.debug("SendSMSIntegrationArchiver closed successfully");
    }

}
