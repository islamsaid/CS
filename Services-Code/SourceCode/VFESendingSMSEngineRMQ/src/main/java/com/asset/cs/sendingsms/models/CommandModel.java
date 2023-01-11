package com.asset.cs.sendingsms.models;

/**
 *
 * @author islam.said
 */
public abstract class CommandModel extends Command {

    protected HeaderModel headerModel;

    /**
     * not a part from SMPP protocol, generated from SMSC interface, delete old
     * parts which it's parts lost or rejected
     */
    private StringBuilder parserStatus;

    private int errorCommadStatus;

    public CommandModel() {
        headerModel = new HeaderModel();
        parserStatus = new StringBuilder();
        errorCommadStatus = 0;
    }

    public HeaderModel getHeaderModel() {
        return headerModel;
    }

    public void setHeaderModel(HeaderModel headerModel) {
        this.headerModel = headerModel;
    }

    public StringBuilder getParserStatus() {
        return parserStatus;
    }

    public void setParserStatus(StringBuilder parserStatus) {
        this.parserStatus = parserStatus;
    }

    public int getErrorCommadStatus() {
        return errorCommadStatus;
    }

    public void setErrorCommadStatus(int errorCommadStatus) {
        this.errorCommadStatus = errorCommadStatus;
    }
}
