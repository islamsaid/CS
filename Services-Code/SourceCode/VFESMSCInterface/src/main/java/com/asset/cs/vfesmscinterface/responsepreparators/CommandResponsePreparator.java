package com.asset.cs.vfesmscinterface.responsepreparators;

import com.asset.cs.vfesmscinterface.constants.ConnectionStatusEnum;
import com.asset.cs.vfesmscinterface.models.CommandRequestModel;
import com.asset.cs.vfesmscinterface.models.CommandResponseModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.socket.Session;

/**
 *
 * @author mostafa.kashif
 */
public interface CommandResponsePreparator {

	public CommandResponseModel validateAndPrepareResponse(ConnectionStatusEnum status, CommandRequestModel command);

	default public int getResponseCommandId(HeaderModel headerModel) {
		return headerModel.getCommandID() | 0x80000000;
	}

	default public CommandResponseModel validateAndPrepareResponse(Session session, CommandRequestModel command) {
		return validateAndPrepareResponse(session.getConnectionStatus(), command);
	}
}
