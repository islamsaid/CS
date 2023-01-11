/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.services;

import com.asset.CS.BridgingDequeuerService.daos.SystemPropertiesDao;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author mostafa.kashif
 */
@Component
public class SystemPropertiesService {

    public HashMap<String, SystemPropertiesModel> getPropertiesPerInterface(JdbcTemplate jdbcTemplate, int groupId) {
        List<SystemPropertiesModel> propertiesModels = systemPropertiesDao.getPropertiesPerInterface(jdbcTemplate, groupId);
        HashMap<String, SystemPropertiesModel> propertiesMap = new HashMap<String, SystemPropertiesModel>();
        for (int i = 0; i < propertiesModels.size(); i++) {
            propertiesMap.put(propertiesModels.get(i).getItemKey(), propertiesModels.get(i));
        }
        return propertiesMap;
    }

    @Autowired
    private SystemPropertiesDao systemPropertiesDao;

}
