/**
 * created on: Dec 14, 2017 9:49:07 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.utils;

import com.asset.cs.vfesmscinterface.exceptions.UnSupportedCommandException;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.parsers.BindParser;
import com.asset.cs.vfesmscinterface.parsers.CommandRequestParser;
import com.asset.cs.vfesmscinterface.parsers.DeliverSMResponseParser;
import com.asset.cs.vfesmscinterface.parsers.EnquireParser;
import com.asset.cs.vfesmscinterface.parsers.SubmitSMParser;
import com.asset.cs.vfesmscinterface.parsers.TLVParser;
import com.asset.cs.vfesmscinterface.parsers.UnbindParser;

/**
 * @author mohamed.morsy
 *
 */
public class ParserFactory {

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

    /**
     * Command Id: Deliver message response
     */
    public static final int DELIVER_SM_RESP = 0x80000005;

    private static SubmitSMParser submitSMParser;

    private static BindParser bindParser;

    private static UnbindParser unbindParser;

    private static EnquireParser enquireParser;

    private static TLVParser tlvParser;

    private static DeliverSMResponseParser deliverSMResponseParser;

    public static CommandRequestParser getCommandParser(HeaderModel headerModel) throws UnSupportedCommandException {

        switch (headerModel.getCommandID()) {
            case ENQUIRE_LINK:
                return getEnquireParser();
            case BIND_TRANSMITTER:
            case BIND_TRANSCEIVER:
                return getBindParser();
            case SUBMIT_SM:
                return getSbmitParser();
            case UNBIND:
                return getUnbindParser();
            case DELIVER_SM_RESP:
                return getDeliverSMResponseParser();
            default:
                throw new UnSupportedCommandException("Unsupported Command id: " + headerModel.getCommandID());
        }
    }

    public static SubmitSMParser getSbmitParser() {
        if (submitSMParser == null) {
            submitSMParser = new SubmitSMParser();
        }
        return submitSMParser;
    }

    public static BindParser getBindParser() {
        if (bindParser == null) {
            bindParser = new BindParser();
        }
        return bindParser;
    }

    public static UnbindParser getUnbindParser() {
        if (unbindParser == null) {
            unbindParser = new UnbindParser();
        }
        return unbindParser;
    }

    public static EnquireParser getEnquireParser() {
        if (enquireParser == null) {
            enquireParser = new EnquireParser();
        }
        return enquireParser;
    }

    public static TLVParser getTLVParser() {
        if (tlvParser == null) {
            tlvParser = new TLVParser();
        }
        return tlvParser;
    }

    public static DeliverSMResponseParser getDeliverSMResponseParser() {
        if (deliverSMResponseParser == null) {
            deliverSMResponseParser = new DeliverSMResponseParser();
        }
        return deliverSMResponseParser;
    }
}
