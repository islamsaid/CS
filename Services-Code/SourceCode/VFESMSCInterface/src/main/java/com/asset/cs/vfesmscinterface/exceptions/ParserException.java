/**
 * created on: Jan 13, 2018 06:37:57 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.exceptions;

/**
 * @author mohamed.morsy
 *
 */
public class ParserException extends Exception {

    private int commadStatus;

    public ParserException(String message) {
        super(message);
    }

    public ParserException(Exception e) {
        super(e);
    }

    public ParserException(String message, int errorCode) {
        super(message);
        this.commadStatus = errorCode;
    }

    public int getCommandStatus() {
        return commadStatus;
    }

}
