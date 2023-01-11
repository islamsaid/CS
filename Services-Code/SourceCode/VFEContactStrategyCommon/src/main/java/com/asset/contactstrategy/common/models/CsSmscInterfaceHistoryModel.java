/**
 * created on: Jan 10, 2018 9:21:31 AM
 * created by: mohamed.morsy
 */
package com.asset.contactstrategy.common.models;

import java.util.Date;

/**
 * @author mohamed.morsy
 *
 */
public class CsSmscInterfaceHistoryModel {

	private long id;

	private String sessionId;

	private String smscMessageId;

	private String request;

	private String response;

	private int executionTime;

	private Date requestDate;

	private String requestError;

	public CsSmscInterfaceHistoryModel() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSmscMessageId() {
		return smscMessageId;
	}

	public void setSmscMessageId(String smscMessageId) {
		this.smscMessageId = smscMessageId;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public int getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(int executionTime) {
		this.executionTime = executionTime;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getRequestError() {
		return requestError;
	}

	public void setRequestError(String requestError) {
		this.requestError = requestError;
	}

	@Override
	public String toString() {
		return "CsSmscInterfaceHistoryModel [id=" + id + ", sessionId=" + sessionId + ", smscMessageId=" + smscMessageId
				+ ", request=" + request + ", response=" + response + ", executionTime=" + executionTime
				+ ", requestDate=" + requestDate + ", requestError=" + requestError + "]";
	}
}
