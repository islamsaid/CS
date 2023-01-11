/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.delivery.models;

import com.asset.contactstrategy.common.models.SMSHistoryModel;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author mohamed.halawa
 */
public class FullSms extends SMSHistoryModel implements Cloneable {

    private String rowId;
    private ArrayList<ConcatSms> concats = new ArrayList<>();
    private int enroute = 0;
    private int delivered = 0;
    private int expired = 0;
    private int deleted = 0;
    private int unDeliverable = 0;
    private int accepted = 0;
    private int unknown = 0;
    private int rejected = 0;
    private boolean hasAnyStatus = false;

    public ArrayList<ConcatSms> getConcats() {
        return concats;
    }

    public void addToConcats(ConcatSms concat) {
        concats.add(concat);
        if (concat.getStatus() != null && concat.getStatus() != 0) {
            hasAnyStatus = true;
            switch (concat.getStatus()) {
                case 1:
                    enroute++;
                    break;
                case 2:
                    delivered++;
                    break;
                case 3:
                    expired++;
                    break;
                case 4:
                    deleted++;
                    break;
                case 5:
                    unDeliverable++;
                    break;
                case 6:
                    accepted++;
                    break;
                case 7:
                    unknown++;
                    break;
                case 8:
                    rejected++;
                    break;
            }
        }
    }

    public int getAllStatusExceptDeliveredCount() {
        return enroute
                + expired
                + deleted
                + unDeliverable
                + accepted
                + unknown
                + rejected;
    }

    public void calculateSendingDate() {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        for (ConcatSms concat : concats) {
            if (concat.getSendingDate() != null
                    && concat.getSendingDate().getTime() < t.getTime()) {
                t = new Timestamp(concat.getSendingDate().getTime());
            }
        }
        setSENDING_DATE(t);
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public boolean isSmsComplete() {
        return super.CONCATENATION_COUNT == concats.size();
    }

    public int getEnroute() {
        return enroute;
    }

    public int getDelivered() {
        return delivered;
    }

    public int getExpired() {
        return expired;
    }

    public int getDeleted() {
        return deleted;
    }

    public int getUnDeliverable() {
        return unDeliverable;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getUnknown() {
        return unknown;
    }

    public int getRejected() {
        return rejected;
    }

    public boolean isHasAnyStatus() {
        return hasAnyStatus;
    }

    public void setHasAnyStatus(boolean hasAnyStatus) {
        this.hasAnyStatus = hasAnyStatus;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FullSms cloned = (FullSms) super.clone(); //To change body of generated methods, choose Tools | Templates.
        cloned.concats = new ArrayList<>();
        return cloned;
    }
}
