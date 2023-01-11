/**
 * created on: Jan 13, 2018 4:46:45 PM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.utils;

/**
 * @author mohamed.morsy
 *
 */
public class Util {

	public static int convertSignedByteToInt(byte value) {
		// to convert from signed byte to unsigned
		return value < 0 ? value + 256 : value;
	}

	public static int convertSignedShortToInt(short value) {
		// to convert from signed short to unsigned
		return value < 0 ? value + 65536 : value;
	}

	public static int getResponseCommandId(int commandId) {
		return commandId | 0x80000000;
	}

}
