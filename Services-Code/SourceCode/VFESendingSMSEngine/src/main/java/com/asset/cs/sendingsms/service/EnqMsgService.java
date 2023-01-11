/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.service;

import client.HashObject;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.cs.sendingsms.dao.EnqMsgDAO;
import java.sql.Connection;

/**
 *
 * @author kerollos.asaad
 */
public class EnqMsgService {

    public boolean enqMsg(HashObject SMSObject, Connection con) throws CommonException {
        return EnqMsgDAO.enqMsg(SMSObject, con);
    }
}
