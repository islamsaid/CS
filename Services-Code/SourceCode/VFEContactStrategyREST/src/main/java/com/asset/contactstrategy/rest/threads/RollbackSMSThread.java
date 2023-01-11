/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.threads;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.SMSHistoryModel;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author esmail.anbar
 */
public class RollbackSMSThread implements Runnable {

    private int threadNo;
    public static int pullTimeOut;
    private SmsBusinessModel rollbackInputModel;
    private MainService mainService;
    private ArrayList<SMSHistoryModel> smsToArchive;

    public RollbackSMSThread(int threadNo) {
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("RollbackSMSThread_" + threadNo);
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
        pullTimeOut = Defines.INTERFACES.ROLLBACK_THREAD_PULL_TIMEOUT_VALUE;
        mainService = new MainService();
        smsToArchive = new ArrayList<>();

        while (!ConfigurationManager.SHUTDOWN_FLAG || !ConfigurationManager.smsToBeRollbacked.isEmpty()
                || ConfigurationManager.concurrentRequests.get() > 0 || !ConfigurationManager.smsToBeValidated.isEmpty()) {
            try {
                rollbackInputModel = ConfigurationManager.smsToBeRollbacked.poll(pullTimeOut, TimeUnit.MILLISECONDS);

                if (rollbackInputModel != null) {
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, rollbackInputModel.getTransId());
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, rollbackInputModel.getCsMsgId());
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, rollbackInputModel.getDestinationMSISDN());
//                    CommonLogger.businessLogger.info(rollbackInputModel.getTransId() + " || Invoking Rollback Action For csMsgId: " + rollbackInputModel.getCsMsgId());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invoking Rollcack Action").build());
                    String quotaColumn = null;
                    if (rollbackInputModel.isUpdatedDoNotApply()) {
                        quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER;
                    } else if (rollbackInputModel.isUpdatedSystemMonitor()) {
                        quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER;
                    } else if (rollbackInputModel.isUpdatedSystemQuota()) {
                        quotaColumn = DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER;
                    }
                    MainService.updateServiceQuota(
                            SystemLookups.SERVICES.get(rollbackInputModel.getSystemName()).getServiceId(),
                            -1,
                            null,
                            quotaColumn);
//                    mainService.decrementServiceCounters(SystemLookups.SERVICES.get(rollbackInputModel.getSystemName()).getServiceId(), rollbackInputModel.isUpdatedDoNotApply(), rollbackInputModel.isUpdatedSystemMonitor(), rollbackInputModel.isUpdatedSystemQuota());
//                    CommonLogger.businessLogger.info(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | Decremented Service Counters...");
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Decremented Service Counters").build());

                    if (rollbackInputModel.isIsArchived()) {
                        mainService.updateArchivedMessageStatus(Long.valueOf(rollbackInputModel.getCsMsgId()), rollbackInputModel.getRollbackStatus(), Integer.parseInt(rollbackInputModel.getDestinationMSISDN().substring(rollbackInputModel.getDestinationMSISDN().length() - 2)));
//                        CommonLogger.businessLogger.info(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | Updated Archiving Status with : " + rollbackInputModel.getRollbackStatus() + "...");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated Archiving Status")
                                .put(GeneralConstants.StructuredLogKeys.ROLLBACK_STATUS, rollbackInputModel.getRollbackStatus()).build());
                    }

                    if (rollbackInputModel.isUpdatedCustomerStatistics()) {
                        mainService.decrementCustomerSMSStats(rollbackInputModel.getTransId(), rollbackInputModel.getDestinationMSISDN(), rollbackInputModel.getDayInSmsStats());
//                        CommonLogger.businessLogger.info(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | Decremented SMS Customer Statistics For Day: " + rollbackInputModel.getDayInSmsStats() + "...");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Decremented Customer Statistics")
                                .put(GeneralConstants.StructuredLogKeys.DAY_IN_SMS_STATS, rollbackInputModel.getDayInSmsStats()).build());
                    }

                    if (rollbackInputModel.getCampaignId() != null) {
                        mainService.decrementCustomerAdsStats(rollbackInputModel.getTransId(), rollbackInputModel.getDestinationMSISDN(), rollbackInputModel.getDayInSmsStats());
//                        CommonLogger.businessLogger.info(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | Decremented ADS Customer Statistics For Day: " + rollbackInputModel.getDayInSmsStats() + "...");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MAX_LOGGING_DB_ARRAY_SIZE, "Decremented ADS Customer Statistics")
                                .put(GeneralConstants.StructuredLogKeys.DAY_IN_SMS_STATS, rollbackInputModel.getDayInSmsStats()).build());
                        mainService.decrementCustomerCampaignStats(rollbackInputModel.getTransId(), rollbackInputModel.getDestinationMSISDN(), rollbackInputModel.getCampaignId());
//                        CommonLogger.businessLogger.info(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | Decremented Customer Campaign Counter For Campaign: " + rollbackInputModel.getCampaignId() + "...");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Decremented Customer Campaign Counter")
                                .put(GeneralConstants.StructuredLogKeys.CAMPAIGN_ID, rollbackInputModel.getCampaignId()).build());
                    }
//                    CommonLogger.businessLogger.info(rollbackInputModel.getTransId() + " || Finished Rollback Action For csMsgId: " + rollbackInputModel.getCsMsgId());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished Rollback Action").build());
                }
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            } catch (CommonException ex) {
//                if (rollbackInputModel != null) {
//                    CommonLogger.businessLogger.error(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | " + ex.getMessage());
//                CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, ex.getMessage()));
//                   CommonLogger.errorLogger.error(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | " + ex.getMessage(), ex);
//                CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, ex.getMessage()), ex);
//                } else {
                CommonLogger.businessLogger.error(ex.getMessage());
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
//                }
            } catch (Exception e) {
//                if (rollbackInputModel != null) {
//                    CommonLogger.businessLogger.error(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | " + e.getMessage());
//                CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage()));
//                    CommonLogger.errorLogger.error(rollbackInputModel.getTransId() + " || csMsgId: " + rollbackInputModel.getCsMsgId() + " | " + e.getMessage(), e);
//                CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage()), e);
//                } else {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
            } finally {
                ThreadContext.clearMap();
            }
        }
//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended || Queue Size: " + ConfigurationManager.smsToBeRollbacked.size()
//                + " || Queue Remaining Capacity: " + ConfigurationManager.smsToBeRollbacked.remainingCapacity()
//                + " || PullTimeOut: " + pullTimeOut);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Has Ended")
                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.smsToBeRollbacked.size())
                .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.smsToBeRollbacked.remainingCapacity())
                .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, pullTimeOut).build());
    }
}
