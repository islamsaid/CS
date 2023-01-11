/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.db.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.delivery.models.Partition;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author esmail.anbar
 */
public class PartitionManagerDao {

    public static ArrayList<Partition> getPartitionsOlderThan(Connection conn, String tableName, Date date) throws Exception {

        String query = "Select * From USER_OBJECTS Where object_type = 'TABLE PARTITION' and object_name = ? and created < ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Partition> partitions = new ArrayList<>();
        Partition partition;

        try {
            int i = 1;
            ps = conn.prepareStatement(query);
            ps.setString(i++, tableName);
            ps.setDate(i++, new java.sql.Date(date.getTime()));
            rs = ps.executeQuery();

            while (rs.next()) {
                partition = new Partition();
                partition.setCreated(rs.getDate("CREATED"));
                partition.setLastDDLTime(rs.getDate("LAST_DDL_TIME"));
                partition.setObjectId(rs.getString("OBJECT_ID"));
                partition.setObjectName(rs.getString("OBJECT_NAME"));
                partition.setObjectType(rs.getString("OBJECT_TYPE"));
                partition.setStatus(rs.getString("STATUS"));
                partition.setSubObjectName(rs.getString("SUBOBJECT_NAME"));
                partition.setTimestamp(rs.getString("TIMESTAMP"));
                partitions.add(partition);
            }
            return partitions;
        } finally {
            DataSourceManger.closeDBResources(rs, ps);
        }
    }

    public static void dropPartition(Connection conn, String tableName, String partitionName) throws Exception {

        String query = "ALTER TABLE " + tableName + " DROP PARTITION " + partitionName;

        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            stmt.execute(query);
        } finally {
            DataSourceManger.closeDBResources(null, stmt);
        }
    }
}
