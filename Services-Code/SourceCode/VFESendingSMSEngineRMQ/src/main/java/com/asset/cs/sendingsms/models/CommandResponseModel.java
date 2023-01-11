package com.asset.cs.sendingsms.models;

import client.BufferByte;
import java.util.HashMap;

/**
 *
 * @author islam.said
 */
public abstract class CommandResponseModel extends CommandModel {

    private BufferByte pdu;

    private HeaderModel headerModel;
    private HashMap<Short, TLVModel> tlvs;

    public HeaderModel getHeaderModel() {
        return headerModel;
    }

    public abstract String toString();
//    {
//        return "CommandResponseModel{" + "pdu=" + pdu + ", headerModel=" + headerModel + ", tlvs=" + tlvs + '}';
//    }

    public void setHeaderModel(HeaderModel headerModel) {
        this.headerModel = headerModel;
    }

    public HashMap<Short, TLVModel> getTlvs() {
        return tlvs;
    }

    public void setTlvs(HashMap<Short, TLVModel> tlvs) {
        this.tlvs = tlvs;
    }

    public BufferByte getPdu() {
        return pdu;
    }

    public void setPdu(BufferByte pdu) {
        this.pdu = pdu;
    }
}
