/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.models;

import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author mohamed.osman
 */
public class UnbindResponseModel extends CommandResponseModel {

    public UnbindResponseModel() {
    }

    public UnbindResponseModel(BufferByte pdu, HeaderModel headerModel) {
        setPdu(pdu);
        setHeaderModel(headerModel);
    }

    @Override
    public String toString() {
        return "UnbindResponseModel [" + getHeaderModel() + ", Body {}]";
    }

}
