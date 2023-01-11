/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.db.service;

import com.asset.contactstrategy.delivery.models.JobModel;
import com.asset.contactstrategy.delivery.db.dao.DeliveryDao;
import com.asset.contactstrategy.delivery.models.FullSms;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author mohamed.halawa
 */
public class DeliveryService {

    public static void getMsgWithConcats(Connection conn, JobModel jobModel) throws Exception {
        DeliveryDao.getMsgWithConcats(conn, jobModel);
    }

    public static int[] updateToDelivered(Connection conn, ArrayList<FullSms> fullSmsBatch) throws Exception {
        return DeliveryDao.updateToDelivered(conn, fullSmsBatch);
    }

    public static int[] updateToPartialDelivery(Connection conn, ArrayList<FullSms> fullSmsBatch) throws Exception {
        return DeliveryDao.updateToPartialDelivery(conn, fullSmsBatch);
    }

    public static int[] updateToNotDelivered(Connection conn, ArrayList<FullSms> fullSmsBatch) throws Exception {
        return DeliveryDao.updateToNotDelivered(conn, fullSmsBatch);
    }

    public static int[] updateToSentToSmsc(Connection conn, ArrayList<FullSms> fullSmsBatch) throws Exception {
        return DeliveryDao.updateToSentToSmsc(conn, fullSmsBatch);
    }

    public static int updateToTimedOut(Connection conn, JobModel jobModel) throws Exception {
        return DeliveryDao.updateToTimedOut(conn, jobModel);
    }
}
