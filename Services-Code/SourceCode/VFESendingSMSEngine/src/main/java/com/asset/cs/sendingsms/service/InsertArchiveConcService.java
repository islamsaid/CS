/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.service;

import client.HashObject;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.cs.sendingsms.dao.InsertArchiveConcDAO;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class InsertArchiveConcService {

    public void insertArchiveConc(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        InsertArchiveConcDAO.insertArchiveConc(messages, conn);
    }
}
