/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.validators;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.SMSCInterfaceClientModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.models.BindRequestModel;

import java.util.HashMap;

/**
 *
 * @author mohamed.osman
 */
public class CommonValidator {

    public static int validatePassword(String systemPassword, SMSCInterfaceClientModel client) {
        CommonLogger.businessLogger.debug("[validatePassword] started...");
        if (!"".equals(systemPassword)) {
            if (systemPassword.equals(client.getPassword())) {
                CommonLogger.businessLogger.debug("[validatePassword] valid password...");
                CommonLogger.businessLogger.debug("[validatePassword] ended...");
                return Data.ESME_ROK_INT;
            } else {
                CommonLogger.businessLogger.debug("[validatePassword] invalid password...");
                CommonLogger.businessLogger.debug("[validatePassword] ended...");
                return Data.ESME_RINVPASWD_INT;
            }

        } else {
            CommonLogger.businessLogger.debug("[validatePassword] password field is null...");
            if (Defines.SMSC_INTERFACE_PROPERTIES.AllOW_INSECURE_ACCESS_FLAG_VALUE.equalsIgnoreCase("TRUE")) {
                CommonLogger.businessLogger.debug("[validatePassword] insecure access is enabled...");
                CommonLogger.businessLogger.debug("[validatePassword] ended...");
                return Data.ESME_ROK_INT;
            } else {
                CommonLogger.businessLogger.debug("[validatePassword] insecure access disabled...");
                CommonLogger.businessLogger.debug("[validatePassword] system error...");
                CommonLogger.businessLogger.debug("[validatePassword] ended...");
                return Data.ESME_RSYSERR_INT;
            }
        }
    }

    public static int validateSystemType(String systemType, SMSCInterfaceClientModel client) {
        CommonLogger.businessLogger.debug("[validateSystemType] started...");
        if (systemType.equals(client.getSystemType())) {
            CommonLogger.businessLogger.debug("[validateSystemType] valid system type...");
            CommonLogger.businessLogger.debug("[validateSystemType] ended...");
            return Data.ESME_ROK_INT;
        } else {
            CommonLogger.businessLogger.debug("[validateSystemType] invalid system type...");
            CommonLogger.businessLogger.debug("[validateSystemType] ended...");
            return Data.ESME_RINVSYSTYP_INT;
        }
    }

    public static int validateClient(BindRequestModel command) {

        CommonLogger.businessLogger.debug("[validateClient] started...");
        HashMap<String, SMSCInterfaceClientModel> clients;
        SMSCInterfaceClientModel client;
        clients = Manager.clientMap;
        ServiceModel service = null;
        int status;
        if (!clients.isEmpty()) {
            
            if (clients.containsKey(command.getSystemID())) {
                CommonLogger.businessLogger.debug("[validateClient] valid System ID...");
                client = clients.get(command.getSystemID());
                for (ServiceModel s : Manager.services){
                    if (s.getServiceName().equals(client.getSystemName())){
                        service = s;
                    }
                }
                //status = validatePassword(command.getPassword(), client);
                status = validateServicePassword(command.getPassword(), service); // CR 1901 | eslam.ahmed
                client.setPassword(command.getPassword());
                
                if (status == Data.ESME_ROK_INT) {
                    if (Defines.SMSC_INTERFACE_PROPERTIES.SYSTEM_TYPE_VALIDATION_FLAG_VALUE.equalsIgnoreCase("TRUE")) {
                        CommonLogger.businessLogger.debug("[validateClient] system type validation is enabled...");
                        status = validateSystemType(command.getSystemType(), client);
                        CommonLogger.businessLogger.debug("[validateClient] ended...");
                        return status;
                    } else {
                        CommonLogger.businessLogger.debug("[validateClient] system type validation is disabled...");
                        CommonLogger.businessLogger.debug("[validateClient] ended...");
                        return status;
                    }
                } else {
                    CommonLogger.businessLogger.debug("[validateClient] ended...");
                    return status;
                }

            } else {
                CommonLogger.businessLogger.debug("[validateClient] Invalid System ID...");
                CommonLogger.businessLogger.debug("[validateClient] ended...");
                return Data.ESME_RINVSYSID_INT;
            }
        } else {
            CommonLogger.businessLogger.debug("[validateClient] Invalid System ID...");
            CommonLogger.businessLogger.debug("[validateClient] ended...");
            return Data.ESME_RINVSYSID_INT;
        }

    }

    public static int validateInterfaceVersion(BindRequestModel command) {
        CommonLogger.businessLogger.debug("[validateInterfaceVersion] started...");
        if (command.getInterfaceVersion() == Data.SMPP_V34_INT ||command.getInterfaceVersion()==0) {
            CommonLogger.businessLogger.debug("[validateInterfaceVersion] valid interface version...");
            CommonLogger.businessLogger.debug("[validateInterfaceVersion] ended...");
            return Data.ESME_ROK_INT;
        }
        CommonLogger.businessLogger.debug("[validateInterfaceVersion] invalid interface version...");
        CommonLogger.businessLogger.debug("[validateInterfaceVersion] ended...");
        return Data.ESME_RBINDFAIL_INT;

    }

    public static int validateAddressSource(BindRequestModel command) {
        CommonLogger.businessLogger.debug("[validateAddressSource] started...");
        if (command.getAddressModel().getTon() == Data.GSM_TON_UNKNOWN_INT
                || command.getAddressModel().getTon() == Data.GSM_TON_INTERNATIONAL_INT
                || command.getAddressModel().getTon() == Data.GSM_TON_NATIONAL_INT
                || command.getAddressModel().getTon() == Data.GSM_TON_NETWORK_INT
                || command.getAddressModel().getTon() == Data.GSM_TON_SUBSCRIBER_INT
                || command.getAddressModel().getTon() == Data.GSM_TON_ALPHANUMERIC_INT
                || command.getAddressModel().getTon() == Data.GSM_TON_ABBREVIATED_INT) {
            CommonLogger.businessLogger.debug("[validateAddressSource] valid ton address...");

            if (command.getAddressModel().getNpi() == Data.GSM_NPI_UNKNOWN_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_ISDN_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_X121_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_TELEX_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_LAND_MOBILE_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_NATIONAL_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_PRIVATE_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_ERMES_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_INTERNET_IP_INT
                    || command.getAddressModel().getNpi() == Data.GSM_NPI_CLIENT_ID_INT) {
                CommonLogger.businessLogger.debug("[validateAddressSource] valid npi address...");
                CommonLogger.businessLogger.debug("[validateAddressSource] ended...");
                return Data.ESME_ROK_INT;

            } else {
                CommonLogger.businessLogger.debug("[validateAddressSource] invalid npi address...");
                CommonLogger.businessLogger.debug("[validateAddressSource] ended...");
                return Data.ESME_RINVSRCNPI_INT;
            }

        } else {
            CommonLogger.businessLogger.debug("[validateAddressSource] invalid ton address...");
            CommonLogger.businessLogger.debug("[validateAddressSource] ended...");
            return Data.ESME_RINVSRCTON_INT;

        }
    }
    
    public static int validateServicePassword(String systemPassword, ServiceModel service){
        CommonLogger.businessLogger.debug("[validatePassword] started...");
        if (!"".equals(systemPassword)) {
            boolean validServicePassword = false;
            try{
                validServicePassword = Utility.validateServicePassword(service.getHashedPassword(), systemPassword, Manager.systemProperities.get(Defines.HASH_KEY_PROPERTY));
            }catch(CommonException ex){
                CommonLogger.errorLogger.error("[validateServicePassword] | system error | " + ex.getErrorMsg());
                return Data.ESME_RSYSERR_INT;
            }
             
            if (validServicePassword) {
                CommonLogger.businessLogger.debug("[validatePassword] valid password...");
                CommonLogger.businessLogger.debug("[validatePassword] ended...");
                return Data.ESME_ROK_INT;
            } else {
                CommonLogger.businessLogger.debug("[validatePassword] invalid password...");
                CommonLogger.businessLogger.debug("[validatePassword] ended...");
                return Data.ESME_RINVPASWD_INT;
            }

        } else {
            CommonLogger.businessLogger.debug("[validatePassword] password field is null...");
            if (Defines.SMSC_INTERFACE_PROPERTIES.AllOW_INSECURE_ACCESS_FLAG_VALUE.equalsIgnoreCase("TRUE")) {
                CommonLogger.businessLogger.debug("[validatePassword] insecure access is enabled...");
                CommonLogger.businessLogger.debug("[validatePassword] ended...");
                return Data.ESME_ROK_INT;
            } else {
                CommonLogger.businessLogger.debug("[validatePassword] insecure access disabled...");
                CommonLogger.businessLogger.debug("[validatePassword] system error...");
                CommonLogger.businessLogger.debug("[validatePassword] ended...");
                return Data.ESME_RSYSERR_INT;
            }
        }
        
    }
    
      public static int validateMaxSessions() {
        CommonLogger.businessLogger.debug("[validateMaxSessions] started...");
        if (Manager.sessionMap.size() < Defines.SMSC_INTERFACE_PROPERTIES.MAX_SESSIONS_VALUE) {
            CommonLogger.businessLogger.debug("[validateMaxSessions] session accepted...");
            CommonLogger.businessLogger.debug("[validateMaxSessions] ended...");
            return Data.ESME_ROK_INT;
        } else {
            CommonLogger.businessLogger.debug("[validateMaxSessions] exceeded max number of sessions....");
            CommonLogger.businessLogger.debug("[validateMaxSessions] ended...");
            return Data.ESME_RBINDFAIL_INT;
        }
    }

}
