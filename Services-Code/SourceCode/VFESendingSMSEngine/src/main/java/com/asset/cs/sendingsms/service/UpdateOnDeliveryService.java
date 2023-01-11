/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.service;

import client.HashObject;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.cs.sendingsms.dao.UpdateOnDeliveryDAO;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class UpdateOnDeliveryService {

    public void updateCounterCachedBatch(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        UpdateOnDeliveryDAO.updateCounterCachedBatch(messages, conn);
    }

    public void updateStatusCachedBatch(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        UpdateOnDeliveryDAO.updateStatusCachedBatch(messages, conn);
    }
public void updateCounterBatch(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        UpdateOnDeliveryDAO.updateCounterBatch(messages, conn);
    }

    public void updateStatusBatch(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        UpdateOnDeliveryDAO.updateStatusBatch(messages, conn);
    }
    public boolean updateStatus(HashObject message, Connection conn) throws CommonException {
        return UpdateOnDeliveryDAO.updateStatus(message, conn);
    }

    public boolean updateCoutner(HashObject message, Connection conn) throws CommonException {
        return UpdateOnDeliveryDAO.updateCounter(message, conn);
    }

    public void selectCsId(HashObject message, Connection conn) throws CommonException {
        UpdateOnDeliveryDAO.selectCsId(message, conn);
    }

}
