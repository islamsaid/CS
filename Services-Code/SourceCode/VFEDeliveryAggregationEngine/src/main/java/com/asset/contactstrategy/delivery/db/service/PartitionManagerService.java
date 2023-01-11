/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.db.service;

import com.asset.contactstrategy.delivery.db.dao.PartitionManagerDao;
import com.asset.contactstrategy.delivery.models.Partition;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author esmail.anbar
 */
public class PartitionManagerService {

    public static ArrayList<Partition> getPartitionsOlderThan(Connection conn, String tableName, Date date) throws Exception {
        return PartitionManagerDao.getPartitionsOlderThan(conn, tableName, date);
    }

    public static void dropPartition(Connection conn, String tableName, String partitionName) throws Exception {
        PartitionManagerDao.dropPartition(conn, tableName, partitionName);
    }
}
