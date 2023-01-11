/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.service;

import client.HashObject;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.cs.sendingsms.dao.InsertArchiveDAO;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author kerollos.asaad
 */
public class InsertArchiveService {

    /*public void insertArchive(ArrayList<HashObject> messages, Connection conn) throws CommonException {
        InsertArchiveDAO.insertArchive(messages, conn);
    }*/
    
    public void updateArchive(ArrayList<HashObject> messages, Connection conn) throws CommonException{
        InsertArchiveDAO.updateArchive(messages, conn);
    }
}
