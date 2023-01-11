/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.models;

import com.asset.contactstrategy.common.models.SMSCInterfaceClientModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author mohamed.osman
 */
public class BindResponseModel extends CommandResponseModel {

    public BindResponseModel() {
    }

    @Override
    public String toString() {
        return  "BindResponseModel{"+getHeaderModel().toString()+" Body:{" + "SystemId=" + SystemId + (scInterfaceVersion == null ? "" : ", scInterfaceVersion=" + scInterfaceVersion) + "}}";
    }

    public String SystemId;

    public String getSystemId() {
        return SystemId;
    }

    public void setSystemId(String SystemId) {
        this.SystemId = SystemId;
    }

    public TLVModel scInterfaceVersion;

    public TLVModel getScInterfaceVersion() {
        return scInterfaceVersion;
    }

    public void setScInterfaceVersion(TLVModel scInterfaceVersion) {
        this.scInterfaceVersion = scInterfaceVersion;
    }

}
