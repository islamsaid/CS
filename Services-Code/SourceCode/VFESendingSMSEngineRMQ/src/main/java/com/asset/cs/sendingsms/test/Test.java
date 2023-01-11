/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.test;

import client.Data;
import com.asset.contactstrategy.common.models.TLVModelValueType;
import com.asset.contactstrategy.common.models.TLVOptionalModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.InputModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author john.habib
 */
public class Test {

    public static String prepareInputJsonModel() {
        Integer x=null;
        System.out.println(x + "");

        String jsonString = null;
        InputModel model = new InputModel();
        model.setCampaignId(1);
        model.setConcatNumber(1);
        model.setCsMsgId(1234);
        model.setCustomer(null);
        model.setDayInSmsStats(0);
        model.setDestinationMSISDN(jsonString);
        model.setDoNotApply(true);
        model.setErrorReason(jsonString);
        model.setEsmClass(Byte.MIN_VALUE);
        model.setExpirationDate(null);
        model.setExpirationHours(0);
        model.setIpaddress(jsonString);
        model.setLanguage(Byte.parseByte("1"));
        model.setLanguageText(jsonString);
        model.setMessagePriority(Byte.parseByte("1"));
        model.setMessageText("hi there");
        model.setMessageType(Byte.parseByte("0"));
        model.setOriginatorMSISDN("AfifyTany");
        model.setSystemName("noran system");
        model.setIpaddress("10.22.36.36");
        model.setOriginatorType(Byte.parseByte("2"));
        model.setPriorityFlag(Byte.parseByte("1"));
        model.setProtocolId(Byte.parseByte("1"));
        model.setValidityPeriod(null);
        model.setViolateFlag(true);
        model.setDestinationMSISDN("201099461535");
        List<TLVOptionalModel> tlvs = new ArrayList<TLVOptionalModel>();
        TLVOptionalModel tlvModel = new TLVOptionalModel(Data.OPT_PAR_CALLBACK_NUM, 5, TLVModelValueType.BYTE, 5 + "");
        tlvs.add(tlvModel);
        model.setTlvs(tlvs);

        jsonString = Utility.gsonObjectToJSONString(model);
        System.out.println(jsonString);
        return jsonString;
    }
}
