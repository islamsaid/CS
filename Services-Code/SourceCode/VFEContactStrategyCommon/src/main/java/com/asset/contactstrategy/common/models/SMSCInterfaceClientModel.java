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
public class SMSCInterfaceClientModel {

	private int id;
	private String password;
	private String systemName;
	private String systemId;
	private String systemType;
	private Integer csCalledThreads;
	private Integer csSubmitSmsQueueSize;

	public Integer getCsCalledThreads() {
		return csCalledThreads;
	}

	public void setCsCalledThreads(Integer csCalledThreads) {
		this.csCalledThreads = csCalledThreads;
	}

	public Integer getCsSubmitSmsQueueSize() {
		return csSubmitSmsQueueSize;
	}

	public void setCsSubmitSmsQueueSize(Integer csSubmitSmsQueueSize) {
		this.csSubmitSmsQueueSize = csSubmitSmsQueueSize;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public String toString() {
		return "SMSCInterfaceClientModel [id=" + id + ", password=" + password + ", systemName=" + systemName
				+ ", systemId=" + systemId + ", systemType=" + systemType + ", csCalledThreads=" + csCalledThreads
				+ ", csSubmitSmsQueueSize=" + csSubmitSmsQueueSize + "]";
	}

}
