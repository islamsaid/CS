/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.models;

/**
 *
 * @author Mohamed
 */
public class EnquireRequestModel extends CommandRequestModel {

    public EnquireRequestModel(HeaderModel headerModel) {
        super(headerModel);
    }

    @Override
    public String toString() {
        return "EnquireRequestModel {" + headerModel + "}";
    }

}
