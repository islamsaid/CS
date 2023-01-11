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
public class UnSupportedRequestModel extends CommandRequestModel {

	public UnSupportedRequestModel(HeaderModel headerModel) {
		super(headerModel);

		if (headerModel == null) {
			this.headerModel = new HeaderModel();
			this.headerModel.setCommandID(0);
			this.headerModel.setCommandLength(0);
			this.headerModel.setCommandStatus(0);
			this.headerModel.setSequenceNumber(0);
		}
	}

	@Override
	public String toString() {
		return "UnSupportedRequestModel{" + headerModel + '}';
	}

}
