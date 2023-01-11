/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.task;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.delivery.models.JobModel;
import com.asset.contactstrategy.delivery.db.facade.DeliveryFacade;

/**
 *
 * @author mohamed.halawa
 */
public class SelectorWorker implements Runnable {

    private final JobModel jobModel;

    public SelectorWorker(JobModel jobModel) {
        this.jobModel = jobModel;
    }

    @Override
    public void run() {
        // to execute the optimitzed select statment
        // Call Select that streams all subparition data joined with Concat
//            SELECT VFE_CS_SMS_H.ROWID hsms_row_id
//            , VFE_CS_SMS_H.*
//            , VFE_CS_SMS_CONCAT_H.*
//    from VFE_CS_SMS_H
//    right join VFE_CS_SMS_CONCAT_H
//    on VFE_CS_SMS_H.CS_MSG_ID = VFE_CS_SMS_CONCAT_H.CS_MSG_ID
//    where VFE_CS_SMS_H.status IN (2,5) 
//    and TRUNC(VFE_CS_SMS_H.SUBMISSION_DATE) = '08-MAY-17'
//    and TRUNC(VFE_CS_SMS_CONCAT_H.SUBMISSION_DATE) = '08-MAY-17'
//    and VFE_CS_SMS_CONCAT_H.MSISDN_MOD_X = 0
//    and VFE_CS_SMS_H.MSISDN_MOD_X = 0;
        try {
            DeliveryFacade.getMsgWithConcats(jobModel);
        } catch (Exception ex) {
            CommonLogger.businessLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
        }
    }
}
