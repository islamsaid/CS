/**
 * created on: Dec 17, 2017 12:04:47 PM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.constants;

/**
 * @author mohamed.morsy
 *
 */
public enum ConnectionStatusEnum {

	OPEN("OPEN"), BOUND("BOUND"), CLOSED("CLOSED");

	private String status;

	ConnectionStatusEnum(String status) {
		this.status = status;
	}

}
