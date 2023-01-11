package com.asset.cs.sendingsms.models;

import client.BufferByte;

/**
 *
 * @author islam.said
 */
public class HeaderModel extends CommandParam {

    private int commandLength = 0;
    private int commandID = 0;
    private Integer commandStatus = -1;

    private int sequenceNumber = 0;

    public int getCommandLength() {
        return commandLength;
    }

    public void setCommandLength(int commandLength) {
        this.commandLength = commandLength;
    }

    public int getCommandID() {
        return commandID;
    }

    public void setCommandID(int commandID) {
        this.commandID = commandID;
    }

    public Integer getCommandStatus() {
        return commandStatus;
    }

    public void setCommandStatus(Integer commandStatus) {
        this.commandStatus = commandStatus;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public String toString() {
        return "HeaderModel:{" + "command_length=" + commandLength + ", command_id=" + Integer.toHexString(commandID) + ", command_status="
                + commandStatus + ", sequence_number=" + sequenceNumber + '}';
    }

    @Override
    public void parsePDU(BufferByte pdu) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean validateModel() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public BufferByte generateResponse() {
        // TODO Auto-generated method stub
        return null;
    }
}
