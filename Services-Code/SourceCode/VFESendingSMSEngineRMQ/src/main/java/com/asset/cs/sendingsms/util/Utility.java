/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.util;

import com.asset.cs.sendingsms.defines.Defines;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author kerollos.asaad
 */
public class Utility {

    public static String getErrorMessage(String errorCode) {
        String errorMessage = "";
        try {
            errorMessage = Defines.errorMessagesBundle.getString(errorCode);
        } catch (Exception e) {
            errorMessage = "Unknown Error!! (key not found in messages resource)";
        }
        return errorMessage;
    }

    public static String getDateTime() {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return (df.format(now));
    }

    public static int getMsisdnModX(String MSISDN) {
        return Integer.parseInt(MSISDN.substring(MSISDN.length() - 2));
    }

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
