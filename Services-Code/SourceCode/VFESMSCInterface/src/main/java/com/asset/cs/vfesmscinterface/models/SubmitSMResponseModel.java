package com.asset.cs.vfesmscinterface.models;

public class SubmitSMResponseModel extends CommandResponseModel {

    private String messageId;

    public SubmitSMResponseModel() {
        setHeaderModel(new HeaderModel());
    }

    public SubmitSMResponseModel(HeaderModel headerModel, String messageId) {
        setHeaderModel(headerModel);
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String toString() {
        return "SubmitSMResponseModel{ message_id= " + getMessageId() + ", headerModel=" + getHeaderModel() + "}";
    }
}
