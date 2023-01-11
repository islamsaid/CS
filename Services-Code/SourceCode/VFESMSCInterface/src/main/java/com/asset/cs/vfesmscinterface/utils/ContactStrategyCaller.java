/**
 * created on: Jan 3, 2018 3:56:34 PM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.utils;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mohamed.morsy
 *
 */
public class ContactStrategyCaller {

    private final static String USER_AGENT = "Mozilla/5.0";

    private final static String contentType = "application/json;charset=UTF-8";

    private final static String method = "POST";

    public static String callContactStrategy(String json) throws CommonException {

        String requestUrl = Manager.systemProperities.get(Defines.SEND_SMS_SINGLE_HTTP_URL);

        int maxHttpHhits = Integer.parseInt(
                Manager.systemProperities.get(Defines.SMSC_INTERFACE_PROPERTIES.SMSC_INTERFACE_CS_MAX_HTTP_HITS));
        int conectTimeout = Integer.parseInt(
                Manager.systemProperities.get(Defines.SMSC_INTERFACE_PROPERTIES.SMSC_INTERFACE_CS_CONNECT_TIMEOUT));
        int readTimeout = Integer.parseInt(
                Manager.systemProperities.get(Defines.SMSC_INTERFACE_PROPERTIES.SMSC_INTERFACE_CS_READ_TIMEOUT));

        try {
            //json = new String(json.getBytes(), Data.ENC_UTF8);
            //json=URLEncoder.encode(json,Data.ENC_UTF8);
            CommonLogger.businessLogger.debug(json);
            return Utility.sendRestRequestWithRetries(maxHttpHhits, requestUrl, USER_AGENT, json, contentType, null,
                    method, conectTimeout, readTimeout, CommonLogger.businessLogger);
        } catch (CommonException e) {
            CommonLogger.businessLogger.error("error in callContactStrategy: " + e.getMessage());
            CommonLogger.errorLogger.error("error in callContactStrategy", e);
            throw e;
        } 
//        catch (UnsupportedEncodingException ex) {
//             throw new CommonException(0);
//        }
    }

}
