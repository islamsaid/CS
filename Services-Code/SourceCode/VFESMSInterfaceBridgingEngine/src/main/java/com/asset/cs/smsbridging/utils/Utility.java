package com.asset.cs.smsbridging.utils;

import com.asset.cs.smsbridging.defines.SMSBridgingDefines;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author aya.moawed
 */
public class Utility {

    public static String getDateTime() {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return (df.format(now));
    }

    public static int getMsisdnModX(String MSISDN) {
        return Integer.parseInt(MSISDN.substring(2)) % SMSBridgingDefines.MOD_X;
    }
}
