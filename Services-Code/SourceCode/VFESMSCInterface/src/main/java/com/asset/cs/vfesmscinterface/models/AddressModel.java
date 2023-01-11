/**
 * created on: Dec 28, 2017 09:28:45 AM created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.models;

import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

public class AddressModel extends CommandParam {

    private byte ton;

    private byte npi;

    private String address;

    public AddressModel(BufferByte pdu) {
        super(pdu);
    }

    @Override
    public void parsePDU(BufferByte pdu) {
        setTon(pdu.removeByte());
        setNpi(pdu.removeByte());
        setAddress(pdu.removeCString());
    }

    @Override
    public boolean validateModel() {
        return validateTon() && validateModel() && validateNpi();
    }

    public boolean validateTon() {
        return (getTon() == Data.GSM_TON_UNKNOWN_INT || getTon() == Data.GSM_TON_INTERNATIONAL_INT
                || getTon() == Data.GSM_TON_NATIONAL_INT || getTon() == Data.GSM_TON_NETWORK_INT
                || getTon() == Data.GSM_TON_SUBSCRIBER_INT || getTon() == Data.GSM_TON_ALPHANUMERIC_INT
                || getTon() == Data.GSM_TON_ABBREVIATED_INT);
    }

    public boolean validateNpi() {
        return (getNpi() == Data.GSM_NPI_UNKNOWN_INT || getNpi() == Data.GSM_NPI_ISDN_INT
                || getNpi() == Data.GSM_NPI_X121_INT || getNpi() == Data.GSM_NPI_TELEX_INT
                || getNpi() == Data.GSM_NPI_LAND_MOBILE_INT || getNpi() == Data.GSM_NPI_NATIONAL_INT
                || getNpi() == Data.GSM_NPI_PRIVATE_INT || getNpi() == Data.GSM_NPI_ERMES_INT
                || getNpi() == Data.GSM_NPI_INTERNET_IP_INT || getNpi() == Data.GSM_NPI_CLIENT_ID_INT);
    }

    public boolean validateAdress(int maxLength, boolean allowNullValue) {
        if (allowNullValue) {
            return address == null || address.length() <= maxLength;
        } else {
            return address != null && address.length() <= maxLength;
        }
    }

    @Override
    public BufferByte generateResponse() {
        throw new UnsupportedOperationException("generate response");
    }

    public int hashCode() {
        StringBuffer buf = new StringBuffer();
        buf.append(Integer.toString(ton)).append(':');
        buf.append(Integer.toString(npi)).append(':');
        if (address != null) {
            buf.append(address);
        }

        return buf.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof AddressModel) {
            AddressModel a = (AddressModel) obj;
            return (a.ton == ton) && (a.npi == npi) && (a.address.equals(address));
        } else {
            return false;
        }
    }

    public String toString() {
        return new StringBuilder(25).append("addr_ton: ").append(Integer.toString(ton)).append(',')
                .append("addr_npi: ").append(Integer.toString(npi))
                .append(',').append("address_range: ")
                .append(address).toString();
    }

    public byte getTon() {
        return ton;
    }

    public void setTon(byte ton) {
        this.ton = ton;
    }

    public byte getNpi() {
        return npi;
    }

    public void setNpi(byte npi) {
        this.npi = npi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
