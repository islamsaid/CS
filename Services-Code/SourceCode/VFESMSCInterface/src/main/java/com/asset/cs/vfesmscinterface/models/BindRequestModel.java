/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.models;

/**
 *
 * @author mohamed.osman
 */
public class BindRequestModel extends CommandRequestModel {

    private String systemID;
    private String password;
    private String systemType;
    private int interfaceVersion;
    private AddressModel addressModel;

    public BindRequestModel(HeaderModel headerModel) {
        super(headerModel);
    }

    public AddressModel getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(AddressModel addressModel) {
        this.addressModel = addressModel;
    }

    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public int getInterfaceVersion() {
        return interfaceVersion;
    }

    public void setInterfaceVersion(int interfaceVersion) {
        this.interfaceVersion = interfaceVersion;
    }

    @Override
    public String toString() {
        return "BindRequestModel{" + headerModel + ", body {systemID=" + systemID + ", password=" + password + ", systemType=" + systemType
                + ", interfaceVersion=" + interfaceVersion + ", addressModel=" + addressModel + "}}";
    }

}
