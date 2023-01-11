package com.asset.cs.sendingsms.models;

import java.util.HashMap;

/**
 *
 * @author islam.said
 */
public abstract class CommandRequestModel extends CommandModel {

    private HashMap<Short, TLVModel> tlvs;

    public CommandRequestModel(HeaderModel headerModel) {
        setHeaderModel(headerModel);
        tlvs = new HashMap<>();
    }

    public CommandRequestModel(HeaderModel headerModel, HashMap<Short, TLVModel> tlvs) {
        this.headerModel = headerModel;
        this.tlvs = tlvs;
    }

    public HashMap<Short, TLVModel> getTlvs() {
        return tlvs;
    }

    public void setTlvs(HashMap<Short, TLVModel> tlvs) {
        this.tlvs = tlvs;
    }
}
