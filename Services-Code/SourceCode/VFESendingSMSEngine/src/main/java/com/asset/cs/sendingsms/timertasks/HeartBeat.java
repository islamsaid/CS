/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.timertasks;

import client.SMPP;
import client.SMPP;
import com.asset.contactstrategy.common.logger.CommonLogger;
import java.util.TimerTask;

/**
 *
 * @author kerollos.asaad
 */
public class HeartBeat extends TimerTask {

    private SMPP smppSession;

    public HeartBeat(SMPP smppSession) {
        this.smppSession = smppSession;
    }

    public void run() {
        CommonLogger.businessLogger.info("****Starting HeartBeat TimerTask*******");
        try {
            if (smppSession.getConnection() != null && smppSession.getConnectionStatus() == false) {
                smppSession.ConnectToFTD(); // reconnect
            }
            if (smppSession.getConnection() != null && smppSession.getConnectionStatus() == true) {
                smppSession.Enquire_link();
            }
        } catch (Exception ex) {
            // CommonLogger.businessLogger.error("HeartBeat Caugth Exception--->" + ex);
            CommonLogger.errorLogger.error("HeartBeat Caugth Exception--->" + ex, ex);
        }
        CommonLogger.businessLogger.info("****Finished HeartBeat TimerTask*******");
    }
}
