/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.common.models;

/**
 *
 * @author Zain Al-Abedin
 */
public class UploadProcedureResult {
    
    private long affectedRows;
    private long msisdnCount;

    public long getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(long affectedRows) {
        this.affectedRows = affectedRows;
    }

    public long getMsisdnCount() {
        return msisdnCount;
    }

    public void setMsisdnCount(long msisdnCount) {
        this.msisdnCount = msisdnCount;
    }
    
    
    
}
