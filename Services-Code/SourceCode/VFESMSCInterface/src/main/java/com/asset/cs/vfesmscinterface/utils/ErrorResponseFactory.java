/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.utils;

import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.models.BindResponseModel;
import com.asset.cs.vfesmscinterface.models.CommandResponseModel;
import com.asset.cs.vfesmscinterface.models.EnquireResponseModel;
import com.asset.cs.vfesmscinterface.models.GenericNackResponseModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.models.SubmitSMResponseModel;
import com.asset.cs.vfesmscinterface.models.UnbindResponseModel;

/**
 *
 * @author john.habib
 */
public class ErrorResponseFactory {

	/**
	 * Command Id: Negative Acknowledgement
	 */
	public static final int GENERIC_NACK = 0x80000000;

	/**
	 * Command Id: Bind transmitter
	 */
	public static final int BIND_TRANSMITTER = 0x00000002;

	/**
	 * Command Id: Bind transmitter response
	 */
	public static final int BIND_TRANSMITTER_RESP = 0x80000002;

	/**
	 * Command Id: Submit message
	 */
	public static final int SUBMIT_SM = 0x00000004;

	/**
	 * Command Id: Submit message response
	 */
	public static final int SUBMIT_SM_RESP = 0x80000004;

	/**
	 * Command Id: Unbind
	 */
	public static final int UNBIND = 0x00000006;

	/**
	 * Command Id: Unbind response
	 */
	public static final int UNBIND_RESP = 0x80000006;

	/**
	 * Command Id: Bind transceiver
	 */
	public static final int BIND_TRANSCEIVER = 0x00000009;

	/**
	 * Command Id: Bind transceiever response.
	 */
	public static final int BIND_TRANSCEIVER_RESP = 0x80000009;

	/**
	 * Command Id: Enquire Link
	 */
	public static final int ENQUIRE_LINK = 0x00000015;

	/**
	 * Command Id: Enquire link respinse
	 */
	public static final int ENQUIRE_LINK_RESP = 0x80000015;

	/**
	 * Command Id: alert notification.
	 */
	public static final int ALERT_NOTIFICATION = 0x00000102;

	public static CommandResponseModel getErrorParserModel(HeaderModel headerModel) {

		switch (headerModel.getCommandID()) {
		case ENQUIRE_LINK_RESP:
			return new EnquireResponseModel();
		case BIND_TRANSMITTER_RESP:
		case BIND_TRANSCEIVER_RESP:
			return new BindResponseModel();
		case SUBMIT_SM_RESP:
			return new SubmitSMResponseModel();
		case UNBIND_RESP:
			return new UnbindResponseModel();
		default:
			return new GenericNackResponseModel();
		}
	}

	public static BufferByte getBufferByte(HeaderModel headerModel) {

		BufferByte pdu = new BufferByte();

		headerModel.setCommandLength(Data.PDU_HEADER_SIZE);
		pdu.appendInt(Data.PDU_HEADER_SIZE);

		pdu.appendInt(headerModel.getCommandID());

		pdu.appendInt(headerModel.getCommandStatus());

		pdu.appendInt(headerModel.getSequenceNumber());

		return pdu;
	}

}
