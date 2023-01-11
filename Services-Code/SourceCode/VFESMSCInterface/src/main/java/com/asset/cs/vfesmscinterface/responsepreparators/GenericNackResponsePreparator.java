/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.responsepreparators;

import com.asset.cs.vfesmscinterface.constants.ConnectionStatusEnum;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.models.CommandRequestModel;
import com.asset.cs.vfesmscinterface.models.CommandResponseModel;
import com.asset.cs.vfesmscinterface.models.GenericNackResponseModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author Eman.fawzy
 */
public class GenericNackResponsePreparator implements CommandResponsePreparator {

	@Override
	public CommandResponseModel validateAndPrepareResponse(ConnectionStatusEnum status, CommandRequestModel command) {

		CommandResponseModel genericNackResponseModel = new GenericNackResponseModel();

		BufferByte bufferByte = new BufferByte();

		bufferByte.appendInt(Data.PDU_HEADER_SIZE);
		bufferByte.appendInt(command.getHeaderModel().getCommandID());
		bufferByte.appendInt(command.getHeaderModel().getCommandStatus());
		bufferByte.appendInt(command.getHeaderModel().getSequenceNumber());

		genericNackResponseModel.setPdu(bufferByte);
		genericNackResponseModel.setHeaderModel(command.getHeaderModel());
		return genericNackResponseModel;
	}

}
