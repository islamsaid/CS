/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.models;

import com.asset.cs.vfesmscinterface.utils.BufferByte;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author mostafa.kashif
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
