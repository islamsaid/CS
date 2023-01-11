/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.threads;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author esmail.anbar
 */
public class SmsValidationThread implements Runnable {

    private int threadNo;
    public static int pullTimeOut;
    private InputModel smsValidationModel;
    private MainService mainService;

    public SmsValidationThread(int threadNo) {
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("SmsValidationThread_" + threadNo);
        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Started");
        pullTimeOut = Defines.INTERFACES.VALIDATION_THREAD_PULL_TIMEOUT_VALUE;
        mainService = new MainService();

        while (!ConfigurationManager.SHUTDOWN_FLAG || !ConfigurationManager.smsToBeValidated.isEmpty() || ConfigurationManager.concurrentRequests.get() > 0) {
            try {
                smsValidationModel = ConfigurationManager.smsToBeValidated.poll(pullTimeOut, TimeUnit.MILLISECONDS);

                if (smsValidationModel != null) {
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsValidationModel.getTransId());
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.MSG_ID, smsValidationModel.getCsMsgId() + "");
                    ThreadContext.put(GeneralConstants.StructuredLogKeys.MSISDN, smsValidationModel.getDestinationMSISDN());
//                    CommonLogger.businessLogger.info(smsValidationModel.getTransId() + " || Invoking Validation For csMsgId: " + smsValidationModel.getCsMsgId());
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Invoking Validation").build());
                    if (1 == mainService.sendSms(GeneralConstants.SRC_ID_SEND_SMS_BULK_OFFLINE_INTERFACE, smsValidationModel, smsValidationModel.getIpaddress(), smsValidationModel.getTransId(), ConfigurationManager.messagesToBeArchived)) {
                        enqueueMessage(smsValidationModel, smsValidationModel.getIpaddress(), SystemLookups.SERVICES.get(smsValidationModel.getSystemName()), smsValidationModel.getTransId());
                    }
                }
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            } catch (InterfacesBusinessException e) {
//                if (smsValidationModel != null) {
//                    CommonLogger.businessLogger.error(smsValidationModel.getTransId() + " || csMsgId: " + smsValidationModel.getCsMsgId() + " | " + e.getMessage());
//                    CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsValidationModel.getTransId())
//                            .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsValidationModel.getCsMsgId())
//                            .put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage()));
//                   CommonLogger.errorLogger.error(smsValidationModel.getTransId() + " || csMsgId: " + smsValidationModel.getCsMsgId() + " | " + e.getMessage(), e);
//                    CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsValidationModel.getTransId())
//                            .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsValidationModel.getCsMsgId())
//                            .put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage())
//                            .put(GeneralConstants.StructuredLogKeys.STACK_TRACE, e));
//                } else {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
            } catch (CommonException e) {
//                if (smsValidationModel != null) {
//                    CommonLogger.businessLogger.error(smsValidationModel.getTransId() + " || csMsgId: " + smsValidationModel.getCsMsgId() + " | " + e.getMessage());
//                    CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsValidationModel.getTransId())
//                            .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsValidationModel.getCsMsgId())
//                            .put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage()));
//                    CommonLogger.errorLogger.error(smsValidationModel.getTransId() + " || csMsgId: " + smsValidationModel.getCsMsgId() + " | " + e.getMessage(), e);
//                    CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsValidationModel.getTransId())
//                            .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsValidationModel.getCsMsgId())
//                            .put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage())
//                            .put(GeneralConstants.StructuredLogKeys.STACK_TRACE, e));
//                } else {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
            } catch (Exception e) {
//                if (smsValidationModel != null) {
//                    CommonLogger.businessLogger.error(smsValidationModel.getTransId() + " || csMsgId: " + smsValidationModel.getCsMsgId() + " | " + e.getMessage());
//                    CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsValidationModel.getTransId())
//                            .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsValidationModel.getCsMsgId())
//                            .put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage()));
//                    CommonLogger.errorLogger.error(smsValidationModel.getTransId() + " || csMsgId: " + smsValidationModel.getCsMsgId() + " | " + e.getMessage(), e);
//                    CommonLogger.errorLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.TRANS_ID, smsValidationModel.getTransId())
//                            .put(GeneralConstants.StructuredLogKeys.MSG_ID, smsValidationModel.getCsMsgId())
//                            .put(GeneralConstants.StructuredLogKeys.MESSAGE, e.getMessage())
//                            .put(GeneralConstants.StructuredLogKeys.STACK_TRACE, e));
//                } else {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
//                }
            } finally {
                ThreadContext.clearMap();
            }
        }

//        CommonLogger.businessLogger.info(Thread.currentThread().getName() + " Has Ended || Queue Size: " + ConfigurationManager.smsToBeValidated.size()
//                + " || Queue Remaining Capacity: " + ConfigurationManager.smsToBeValidated.remainingCapacity()
//                + " || PullTimeOut: " + pullTimeOut);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread Has Ended")
                .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, ConfigurationManager.smsToBeValidated.size())
                .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, ConfigurationManager.smsToBeValidated.remainingCapacity())
                .put(GeneralConstants.StructuredLogKeys.PULL_TIME_OUT, pullTimeOut).build());
    }

    public void enqueueMessage(InputModel input, String ipAddress, ServicesModel service, String transId) throws CommonException {
//       CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + "Enqueuing Message in Java Queue...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueuing Message in Java Queue").build());
        Utility.addMessageToQueues(transId, input, service, ipAddress, ConfigurationManager.smsToBeSent, ConfigurationManager.messagesToBeArchived);
//        CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + "Enqueued Message Successfully...");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueued Message Successfully").build());
    }
}
