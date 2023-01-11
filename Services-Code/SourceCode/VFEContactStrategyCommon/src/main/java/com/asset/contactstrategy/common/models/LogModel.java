/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

/**
 *
 * @author mohamed.osman
 */
public class LogModel {

	private int id;
	private String sessionId;
	private String commandName;
	private String commandType;
	private int sequenceNumber;
	private String pdu;
	private String parse;
	private String commandStatus;
	private String requestError;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getPdu() {
		return pdu;
	}

	public void setPdu(String pdu) {
		this.pdu = pdu;
	}

	public String getParse() {
		return parse;
	}

	public void setParse(String parse) {
		this.parse = parse;
	}

	public String getCommandStatus() {
		return commandStatus;
	}

	public void setCommandStatus(String commandStatus) {
		this.commandStatus = commandStatus;
	}

	public String getRequestError() {
		return requestError;
	}

	public void setRequestError(String requestError) {
		this.requestError = requestError;
	}

	@Override
	public String toString() {
		return "LogModel [id=" + id + ", sessionId=" + sessionId + ", commandName=" + commandName + ", commandType="
				+ commandType + ", sequenceNumber=" + sequenceNumber + ", pdu=" + pdu + ", parse=" + parse
				+ ", commandStatus=" + commandStatus + ", requestError=" + requestError + "]";
	}
}
