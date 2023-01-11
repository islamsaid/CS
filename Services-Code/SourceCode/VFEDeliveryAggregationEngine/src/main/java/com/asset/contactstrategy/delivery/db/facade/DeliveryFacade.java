/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.db.facade;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.delivery.models.JobModel;
import com.asset.contactstrategy.delivery.db.service.DeliveryService;
import com.asset.contactstrategy.delivery.models.FullSms;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author mohamed.halawa
 */
public class DeliveryFacade {

    public static void getMsgWithConcats(JobModel jobModel) throws Exception {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            DeliveryService.getMsgWithConcats(conn, jobModel);
            conn.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(conn);
            throw e;
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(ex);
            }
        }
    }

    public static int[] updateToDelivered(ArrayList<FullSms> fullSmsBatch) throws Exception {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            int[] count = DeliveryService.updateToDelivered(conn, fullSmsBatch);
            conn.commit();
            return count;
        } catch (Exception e) {
            DataSourceManger.rollBack(conn);
            throw e;
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(ex);
            }
        }
    }

    public static int[] updateToPartialDelivery(ArrayList<FullSms> fullSmsBatch) throws Exception {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            int[] count = DeliveryService.updateToPartialDelivery(conn, fullSmsBatch);
            conn.commit();
            return count;
        } catch (Exception e) {
            DataSourceManger.rollBack(conn);
            throw e;
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(ex);
            }
        }
    }

    public static int[] updateToNotDelivered(ArrayList<FullSms> fullSmsBatch) throws Exception {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            int[] count = DeliveryService.updateToNotDelivered(conn, fullSmsBatch);
            conn.commit();
            return count;
        } catch (Exception e) {
            DataSourceManger.rollBack(conn);
            throw e;
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(ex);
            }
        }
    }

    public static int[] updateToSentToSmsc(ArrayList<FullSms> fullSmsBatch) throws Exception {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            int[] count = DeliveryService.updateToSentToSmsc(conn, fullSmsBatch);
            conn.commit();
            return count;
        } catch (Exception e) {
            DataSourceManger.rollBack(conn);
            throw e;
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(ex);
            }
        }
    }

    public static int updateToTimedOut(JobModel jobModel) throws Exception {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            int count = DeliveryService.updateToTimedOut(conn, jobModel);
            conn.commit();
            return count;
        } catch (Exception e) {
            DataSourceManger.rollBack(conn);
            throw e;
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error(ex);
            }
        }
    }
}
