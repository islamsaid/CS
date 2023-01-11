package com.asset.cs.sendingsms.models;

import java.sql.Timestamp;

/**
 *
 * @author islam.said
 */
public class ReceivedSMSLogModel {

    private CommandRequestModel commandRequestModel;
    private CommandResponseModel commandResponseModel;
    private Timestamp requestDate;
    private String transId;

    public ReceivedSMSLogModel() {
    }

    public ReceivedSMSLogModel(CommandRequestModel commandRequestModel, CommandResponseModel commandResponseModel, Timestamp requestDate, String transId) {
        this.commandRequestModel = commandRequestModel;
        this.commandResponseModel = commandResponseModel;
        this.requestDate = requestDate;
        this.transId = transId;
    }

    public CommandRequestModel getDataSMModel() {
        return commandRequestModel;
    }

    public CommandResponseModel getDataSMResponseModel() {
        return commandResponseModel;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }
}
