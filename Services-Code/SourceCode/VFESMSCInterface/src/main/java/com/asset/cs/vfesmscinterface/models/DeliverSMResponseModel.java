package com.asset.cs.vfesmscinterface.models;

/**
 *
 * @author islam.said
 */
public class DeliverSMResponseModel extends CommandRequestModel {

    private String messageId;

    public DeliverSMResponseModel(HeaderModel headerModel) {
        super(headerModel);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "DeliverSMResponseModel{ message_id= " + getMessageId() + ", headerModel=" + getHeaderModel() + "}";
    }
}
