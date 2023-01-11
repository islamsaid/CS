/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.daos;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author mostafa.kashif
 */

@Component
public class SystemPropertiesDao {

    public List<SystemPropertiesModel> getPropertiesPerInterface(JdbcTemplate jdbcTemplate, int groupId) {
        return jdbcTemplate.query("select " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + "," + DBStruct.SYSTEM_PROPERITES.ITEM_VALUE + "," + DBStruct.SYSTEM_PROPERITES.ITEM_KEY + " from "+DBStruct.SYSTEM_PROPERITES.TABLE_NAME+" where " + DBStruct.SYSTEM_PROPERITES.GROUP_ID + "= ?", new Integer []{groupId}, new BeanPropertyRowMapper(SystemPropertiesModel.class));
    }

}
