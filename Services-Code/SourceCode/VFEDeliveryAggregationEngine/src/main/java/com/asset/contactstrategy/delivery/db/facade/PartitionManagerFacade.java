/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.db.facade;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.delivery.db.service.PartitionManagerService;
import com.asset.contactstrategy.delivery.manager.JobManager;
import com.asset.contactstrategy.delivery.models.Partition;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author esmail.anbar
 */
public class PartitionManagerFacade {

    public static void dropPartitionsOlderThan(String tableName, Date date) throws Exception {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            int daysBeforeTimeOut = Integer.parseInt(JobManager.getInstance().getSystemProperties()
                    .get(Defines.DELIVERY_AGGREGATION_PROPERTIES.DAYS_BEFORE_SMS_TIMEOUT));
            ArrayList<Partition> partitions = PartitionManagerService.getPartitionsOlderThan(conn, tableName, date);
            if (partitions != null && partitions.size() > daysBeforeTimeOut) {
                for (Partition partition : partitions) {
                    PartitionManagerService.dropPartition(conn, tableName, partition.getSubObjectName());
                }
            }
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
}
