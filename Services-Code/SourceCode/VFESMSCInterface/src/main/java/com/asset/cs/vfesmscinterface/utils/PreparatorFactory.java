/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.utils;

import com.asset.cs.vfesmscinterface.exceptions.UnSupportedCommandException;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.responsepreparators.BindResponsePreparator;
import com.asset.cs.vfesmscinterface.responsepreparators.CommandResponsePreparator;
import com.asset.cs.vfesmscinterface.responsepreparators.EnquireResponsePreparator;
import com.asset.cs.vfesmscinterface.responsepreparators.GenericNackResponsePreparator;
import com.asset.cs.vfesmscinterface.responsepreparators.SubmitSMResponsePreparator;
import com.asset.cs.vfesmscinterface.responsepreparators.UnbindResponsePreparator;

/**
 *
 * @author Mohamed
 */
public class PreparatorFactory {

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
     * Command Id: Bind receiver
     */
    public static final int BIND_RECEIVER = 0x00000001;

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

    private static BindResponsePreparator bindResponsePreparator;

    private static UnbindResponsePreparator unbindResponsePreparator;

    private static EnquireResponsePreparator enquireResponsePreparator;

    private static SubmitSMResponsePreparator submitSMResponsePreparator;

    private static GenericNackResponsePreparator genericNackResponsePreparator;

    public static CommandResponsePreparator getResponsePreparator(HeaderModel headerModel)
            throws UnSupportedCommandException {

        switch (headerModel.getCommandID()) {
            case ENQUIRE_LINK:
                return getEnquireResponsePreparator();
            case BIND_TRANSMITTER:
            case BIND_TRANSCEIVER:
            case BIND_RECEIVER:
                return getBindResponsePreparator();
            case UNBIND:
                return getUnbindResponsePreparator();
            case SUBMIT_SM:
                return getSubmitSMResponsePreparator();
            case GENERIC_NACK:
                return getGenericNackResponsePreparator();
            default:
                throw new UnSupportedCommandException("Unsupported Command id: " + headerModel.getCommandID());
        }
    }

    public static BindResponsePreparator getBindResponsePreparator() {
        if (bindResponsePreparator == null) {
            bindResponsePreparator = new BindResponsePreparator();
        }
        return bindResponsePreparator;
    }

    public static UnbindResponsePreparator getUnbindResponsePreparator() {
        if (unbindResponsePreparator == null) {
            unbindResponsePreparator = new UnbindResponsePreparator();
        }
        return unbindResponsePreparator;
    }

    public static EnquireResponsePreparator getEnquireResponsePreparator() {
        if (enquireResponsePreparator == null) {
            enquireResponsePreparator = new EnquireResponsePreparator();
        }
        return enquireResponsePreparator;
    }

    public static SubmitSMResponsePreparator getSubmitSMResponsePreparator() {
        if (submitSMResponsePreparator == null) {
            submitSMResponsePreparator = new SubmitSMResponsePreparator();
        }
        return submitSMResponsePreparator;
    }

    public static GenericNackResponsePreparator getGenericNackResponsePreparator() {
        if (genericNackResponsePreparator == null) {
            genericNackResponsePreparator = new GenericNackResponsePreparator();
        }
        return genericNackResponsePreparator;
    }

}
