package com.asset.cs.sendingsms.timertasks;

import client.HashObject;
import com.asset.cs.sendingsms.controller.Controller;
import com.asset.cs.sendingsms.defines.Defines;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimerTask;
import org.apache.logging.log4j.Logger;

/**
 * *****************************************************************************************
 */
/**
 * ******************************************************************************************
 * The Sender puts the sent SMS details in a hashtable, once a reponse is
 * received it get removed from the hashtable.
 *
 * This class is made to maintain the Hashtable stability and cleaning. In case
 * of timed-out requests some sent SMS's may not get a response, thus not
 * removed from hashtable so in case of time-out this class(from which a thread
 * is instantiated, and invoked each time period)cleans the Hashtable and
 * removes the timed-out requests.
 *
 *****************************************************************
 */
/**
 * ***************************************************************************************
 */
public class GarbageCollector extends TimerTask {

    private Logger infologger = null;
    private Logger debugLogger = null;
    private Logger errlogger = null;
    private Hashtable SendRcvHash = null;
    private Hashtable RcvDelHash = null;

    public GarbageCollector(Hashtable SendRcvHash, Hashtable RcvDelHash) {
        this.infologger = Controller.infoLogger;
        this.debugLogger = Controller.debugLogger;
        this.errlogger = Controller.errorLogger;
        this.SendRcvHash = SendRcvHash;
        this.RcvDelHash = RcvDelHash;
    }

    public void garbageCollectionProcess(Hashtable requiredHash) {

    }

    public void run() {

        this.debugLogger.info("Checking Window sendRec hash");
        //garbageCollectionProcess(this.SendRcvHash);
        if (SendRcvHash != null) {
            this.debugLogger.info("sendRec hash size = " + SendRcvHash.size());
        } else {
            this.debugLogger.info("sendRec hash size = null");
        }
        Date logTime;
        long CurrentTime;
        long elapsedTime;
        int result;
        int hours, minutes, seconds;
        long timeout;
        Enumeration num = SendRcvHash.keys();
        while (num.hasMoreElements()) {
            try {
                int key = (Integer) num.nextElement();
                HashObject value = (HashObject) SendRcvHash.get(key);
                if (value == null) {
                    continue;
                }
                //DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
                CurrentTime = getCurrentTime();
                // logTime = df.parse(value.log.substring(11, 23));
                logTime = df.parse(value.getSendingTime());
                Calendar lCalendar = new GregorianCalendar();
                lCalendar.setTime(logTime);

                elapsedTime = CurrentTime - lCalendar.getTimeInMillis();
                result = (int) (elapsedTime / 1000);
                hours = result / 3600;
                result = result - (hours * 3600);
                minutes = result / 60;
                result = result - (minutes * 60);
                seconds = result;

                //msgs retained for more than on min in HashTable
                //(meaning no response has been arrived), are to be removed
                //Time-out most propably
                timeout = Long.valueOf(Defines.databaseConfigurations.get(Defines.SENDING_SMS_TIMEOUT));
                //    if (minutes >= 1) {
                if (minutes >= timeout) {
                    infologger.info("T || sendRec || " + value.getSMS().getAppId() + " || " + key + " || " + value.getLog());
                    debugLogger.debug("T || sendRec || " + value.getSMS().getAppId() + " || " + key + " || " + value.getLog());
                    SendRcvHash.remove(key);
                }
            }// end try
            catch (ParseException e) {
                debugLogger.error("ParseException || " + e.getMessage());
                errlogger.error("ParseException || " + e.getMessage(), e);
            }// end catch
            catch (Exception e) {
                debugLogger.error("Exception || " + e.getMessage());
                errlogger.error("Exception || " + e.getMessage(), e);
            }
        }// end while
        debugLogger.info("Window OK sendRec hash");
        if (SendRcvHash != null) {
            this.debugLogger.info("sendRec hash after garbage collecting size = " + SendRcvHash.size());
        } else {
            this.debugLogger.info("sendRec hash after garbage size = null");
        }

        ///////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        this.debugLogger.info("Checking Window rcvDel hash");
        if (RcvDelHash != null) {
            this.debugLogger.info("rcvDel hash size = " + RcvDelHash.size());
        } else {
            this.debugLogger.info("rcvDel hash size = null");
        }

        //garbageCollectionProcess(this.RcvDelHash);
        num = this.RcvDelHash.keys();
        while (num.hasMoreElements()) {
            try {
                String key = (String) num.nextElement();
                HashObject value = (HashObject) RcvDelHash.get(key);
                if (value == null) {
                    continue;
                }
                //DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
                CurrentTime = getCurrentTime();
                // logTime = df.parse(value.log.substring(11, 23));
                logTime = df.parse(value.getSubmitSMRespTime());
                Calendar lCalendar = new GregorianCalendar();
                lCalendar.setTime(logTime);

                elapsedTime = CurrentTime - lCalendar.getTimeInMillis();
                result = (int) (elapsedTime / 1000);
                hours = result / 3600;
                result = result - (hours * 3600);
                minutes = result / 60;
                result = result - (minutes * 60);
                seconds = result;

                //msgs retained for more than on min in HashTable
                //(meaning no response has been arrived), are to be removed
                //Time-out most propably
                timeout = Long.valueOf(Defines.databaseConfigurations.get(Defines.DELIVER_SMS_TIMEOUT));
                //    if (minutes >= 1) {
                if (minutes >= timeout) {
                    infologger.info("T || rcvDel || " + value.getSMS().getAppId() + " || " + key + " || " + value.getLog());
                    debugLogger.debug("T || rcvDel || " + value.getSMS().getAppId() + " || " + key + " || " + value.getLog());
                    RcvDelHash.remove(key);
                }

            }// end try
            catch (ParseException e) {
                debugLogger.error("ParseException || " + e.getMessage());
                errlogger.error("ParseException || " + e.getMessage(), e);
            }// end catch
            catch (Exception e) {
                debugLogger.error("Exception || " + e.getMessage());
                errlogger.error("Exception || " + e.getMessage(), e);
            }
        }// end while

        debugLogger.info("Window OK rcvDel hash");
        if (RcvDelHash != null) {
            this.debugLogger.info("rcvDel hash after garbage size = " + RcvDelHash.size());
        } else {
            this.debugLogger.info("rcvDel hash after garbage size = null");
        }
    }// end function

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
