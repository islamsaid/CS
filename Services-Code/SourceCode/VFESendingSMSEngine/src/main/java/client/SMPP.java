package client;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.cs.sendingsms.timertasks.GarbageCollector;
import com.asset.cs.sendingsms.timertasks.HeartBeat;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.TLVModelValueType;
import com.asset.contactstrategy.common.models.TLVOptionalModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.cs.sendingsms.controller.Controller;
import com.asset.cs.sendingsms.controller.Manager;
import com.asset.cs.sendingsms.defines.Defines;
import com.asset.cs.sendingsms.defines.ErrorCodes;
import com.asset.cs.sendingsms.service.MainService;
import com.asset.cs.sendingsms.threads.DeliverResponseThread;
import com.asset.cs.sendingsms.threads.ReceiverThread;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import org.apache.logging.log4j.Logger;

/**
 * *****************************************************************************************
 */
/**
 * ******************************************************************************************
 * This class contains all the SMPP v3.4 functionality 1) Bind 2) Submit_SM 3)
 * Enquire_Link(Thread) 4) Unbind Operation Mode: Asynchronos Tailored to adapt
 * the behavior of the : FTD (First Time Delivery)
 *
 * - A receiver thread (instantiated from Reciever Class) used to seperate the
 * sender from receiver to overcome the FTD delayed response - a hash table is
 * used to maintain a link between the sender thread and the receiver where
 * SMS_Sent (Sender) are inserted, and SMS_Resp (Receiver) are removed. -
 * GarbageCollector thread (instantiated from GarbageCollector Class) used to
 * monitor the Hash Table and remove time-out requests - Class SMPP connection
 * is used to implement a fixed structure of the given FTD's to connect to and
 * to facilitate the switching between them in case of any problem.
 * ****************************************************************
 */
/**
 * ***************************************************************************************
 */
public class SMPP {

    private TCPIPConnection connection = null;
    private SMPPConnection smppConneciton;

    public TCPIPConnection getConnection() {
        return connection;
    }

    public void setConnection(TCPIPConnection connection) {
        this.connection = connection;
    }
    private Controller main;
    private int sequenceNumber;    // private boolean isOpen;
    private int successfullMessages = 0;
    /*----------------read from ini file----------*/
    private SMPPConnection[] IPArray = null;
    private int IPCount;
    private String KeepAlive = null;
    //private int windowSize;
    private int sendRecvHashWindowSize;
    private int recvDelHashWindowSize;
    private int garbageCollectorTime;
    /*--------------------------------------------*/
    private boolean threadActive; // heart beat thread flag
    private boolean GarbageThreadActive;
    private Timer HeartBeatTimer = null;
    private HeartBeat HeartBeatTask = null;
    private Timer GarbageCollectorTimer = null;
    private GarbageCollector GarbageCollectorTask = null;
    private Logger debuglogger = null; // debuglogger object
    private Logger errlogger = null;
    private Logger infologger = null;
    private DeliverResponseThread delvresp = null; // deliver_sm_resp responsible thread
    private ReceiverThread recv = null; // receiver thread
    /*----------------------------------*/
    private Hashtable SendRcvHash = null;

    public Hashtable getSendRcvHash() {
        return SendRcvHash;
    }

//    private Hashtable RcvDelHash = null; // edit1 needed to construct a new hashmap between receiver (when recieve deliver_sm) and deliver_sm_resp thread.
//                                         // edit2 moved to manager, as deliver_sm being sent not necessarily on the same smcs opened session.
//    public Hashtable getRcvDelHash() {
//        return RcvDelHash;
//    }
    private boolean isSuccessConnection = true;
    private int lastConnection = 0;
    private int conc_msg_count = 0;
    private byte conc_msg_sequeunce = 0;
    private int conc_msg_length_fixed = 0;
    private short conc_sar_ref_num = 0;
    private boolean conc = false;
    private QueueModel smppApp;

    public QueueModel getSmppApp() {
        return smppApp;
    }

    public void setSmppApp(QueueModel smppApp) {
        this.smppApp = smppApp;
    }
    //Added to concat threadNUmber to garbagecollector,receiver,heartbeat threads
    private int threadNumber;

    /**
     * Constructor
     *
     * @param Section, defines the section of the ini file to read from
     * @param main, instance of MainClass
     * @param debuglogger, debuglogger object for logging
     *
     * public SMPP(Controller main) { this.debuglogger = Controller.infoLogger;
     * this.debuglogger = Controller.debugLogger; this.errlogger =
     * Controller.errorLogger; this.main = main; SessionInit(); // read related
     * data from ini file sequenceNumber = 0; // initialize the sequence number
     * threadActive = false; // indicate heart beat thread Active/Inactive
     * GarbageThreadActive = false; }
     */
    public SMPP(QueueModel smppApp, int threadNumber) {
        this.debuglogger = Controller.debugLogger;
        this.infologger = Controller.infoLogger;
        this.errlogger = Controller.errorLogger;
        this.smppApp = smppApp;
        this.threadNumber = threadNumber;
        init();
        sequenceNumber = 0; // initialize the sequence number
        threadActive = false; // indicate heart beat thread Active/Inactive
        GarbageThreadActive = false;
    }

    public SMPP(QueueModel smppApp, int threadNumber, String session) {
        this.infologger = Controller.infoLogger;
        this.debuglogger = Controller.debugLogger;
        this.errlogger = Controller.errorLogger;
        this.smppApp = smppApp;
        this.threadNumber = threadNumber;

        IPArray = new SMPPConnection[1];
        IPArray[0] = new SMPPConnection();

        StringTokenizer tmpsession = new StringTokenizer(session, ",");

        tmpsession.nextToken();
        IPArray[0].IP = tmpsession.nextToken().toString();
        IPArray[0].Port = tmpsession.nextToken().toString();
        IPArray[0].System_Type = tmpsession.nextToken().toString();
        IPArray[0].System_ID = tmpsession.nextToken().toString();
        IPArray[0].Password = tmpsession.nextToken().toString();
        IPArray[0].Smsc_id = tmpsession.nextToken().toString();

        this.KeepAlive = Defines.fileConfigurations.get(Defines.KEEP_ALIVE);
        //this.windowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.WINDOWS_SIZE));
        this.recvDelHashWindowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.RecvDelHashWindowSize));
        this.sendRecvHashWindowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.SendRecvHashWindowSize));
        this.garbageCollectorTime = Integer.valueOf(Defines.fileConfigurations.get(Defines.GARBAGE_COLLECTIOR_TIME));
//        debuglogger.debug("KeepAlive || " + KeepAlive);
        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "keeping alive")
                .put(GeneralConstants.StructuredLogKeys.KEEP_ALIVE, KeepAlive).build());

        sequenceNumber = 0; // initialize the sequence number

        threadActive = false; // indicate heart beat thread Active/Inactive

        GarbageThreadActive = false;
    }

    public void init() {
        try {
            this.IPCount = this.smppApp.getSmscModels().size();
            IPArray = new SMPPConnection[IPCount];
            for (int i = 0; i < IPCount; i++) {
                IPArray[i] = new SMPPConnection();
                IPArray[i].IP = this.smppApp.getSmscModels().get(i).getIp();
                IPArray[i].Port = String.valueOf(this.smppApp.getSmscModels().get(i).getPort());
                IPArray[i].System_ID = this.smppApp.getSmscModels().get(i).getUsername();
                IPArray[i].Password = this.smppApp.getSmscModels().get(i).getPassword();
                IPArray[i].System_Type = this.smppApp.getSmscModels().get(i).getSystemType();
                IPArray[i].Smsc_id = String.valueOf(this.smppApp.getSmscModels().get(i).getVersionId());
            }
            this.KeepAlive = Defines.fileConfigurations.get(Defines.KEEP_ALIVE);
            this.recvDelHashWindowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.RecvDelHashWindowSize));
            this.sendRecvHashWindowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.SendRecvHashWindowSize));
            //this.windowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.WINDOWS_SIZE));
            this.garbageCollectorTime = Integer.valueOf(Defines.fileConfigurations.get(Defines.GARBAGE_COLLECTIOR_TIME));
//            debuglogger.debug("KeepAlive || " + KeepAlive);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "keeping alive")
                    .put(GeneralConstants.StructuredLogKeys.KEEP_ALIVE, KeepAlive).build());
        } catch (Exception e) {
            debuglogger.error("Exception in init ||  " + e);
            errlogger.error("Exception in init ||  " + e, e);
        }
    }

    // read related variables from ini file
    /**
     * function to read from the ini file, specifying the related variables to
     * read
     *
     * @param Section, the section of the ini to read from
     */
    public void SessionInit() {
        try {
            //this.IPCount = Integer.valueOf(Defines.fileConfigurations.get(Defines.COUNT)); // commented as ip count won't be configured from file.
            IPArray = new SMPPConnection[IPCount];
            for (int i = 0; i < IPCount; i++) {
                StringTokenizer stToken = new StringTokenizer(Defines.fileConfigurations.get(Defines.DATABASE_CREDENTIALS), ",");
                IPArray[i] = new SMPPConnection();
                IPArray[i].IP = stToken.nextToken();
                IPArray[i].Port = stToken.nextToken();
                IPArray[i].System_ID = stToken.nextToken();
                IPArray[i].Password = stToken.nextToken();
                IPArray[i].System_Type = stToken.nextToken();
            }
            this.KeepAlive = Defines.fileConfigurations.get(Defines.KEEP_ALIVE);
            //this.windowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.WINDOWS_SIZE));
            this.recvDelHashWindowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.RecvDelHashWindowSize));
            this.sendRecvHashWindowSize = Integer.valueOf(Defines.fileConfigurations.get(Defines.SendRecvHashWindowSize));
            this.garbageCollectorTime = Integer.valueOf(Defines.fileConfigurations.get(Defines.GARBAGE_COLLECTIOR_TIME));
//            debuglogger.debug("KeepAlive || " + KeepAlive);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "keeping alive")
                    .put(GeneralConstants.StructuredLogKeys.KEEP_ALIVE, KeepAlive).build());
        } catch (Exception e) {
            debuglogger.error("Exception in SessionInit ||  " + e);
            errlogger.error("Exception in SessionInit ||  " + e, e);
        }
    }

    /**
     * Function that uses the TCPIPConnection object (connection) to check the
     * connection status
     *
     * @return(connection status)
     */
    public boolean getConnectionStatus() {
        return (connection.isOpen());
    }

    public void ConnectToFTD() {
        boolean connected = false;
        //      while ((!connected) && (!main.getExitFlagStatus())) {//changed as main not used
        long beginTime = System.currentTimeMillis();
        while ((!connected) && (!Defines.ENGINE_SHUTDOWN_FLAG.get())) {
            try {

                smppConneciton = getNextConnection();

                if (Connect(smppConneciton.IP, smppConneciton.Port)) {

                    /*----------Now Bind-----------*/
//                    debuglogger.info("Now Binding to SMSC");
//                    debuglogger.info("System_ID || " + smppConneciton.System_ID);
//                    debuglogger.info("Password || " + smppConneciton.Password);
//                    debuglogger.info("System_Type || " + smppConneciton.System_Type);
                    debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Now Binding to SMSC")
                            .put(GeneralConstants.StructuredLogKeys.SYSTEM_ID, smppConneciton.System_ID)
                            .put(GeneralConstants.StructuredLogKeys.PASSWORD, smppConneciton.Password)
                            .put(GeneralConstants.StructuredLogKeys.SYSTEM_TYPE, smppConneciton.System_Type).build());

//Bind Transmitter/ Receiver / Transciever
                    if (Bind_Transceiver(smppConneciton.System_ID,
                            smppConneciton.Password,
                            smppConneciton.System_Type,
                            Data.GSM_TON_INTERNATIONAL,
                            Data.GSM_NPI_ISDN)) // bind
                    // with
                    // smsc
                    // as
                    // transceiver
                    {
//                        debuglogger.info("System_ID: " + smppConneciton.System_ID + "Binded Successfully");
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Binded Successfully")
                                .put(GeneralConstants.StructuredLogKeys.SYSTEM_ID, smppConneciton.System_ID).build());
                        startAsynchFunctions();
                        isSuccessConnection = true;
                        connected = true; // if success exit function

                    } // end if bind check

                } // end if connection check

                if (!connected) {
                    long threshold = (1000 * (60 * Integer.valueOf(Defines.fileConfigurations.get((String) Defines.SMSC_NOT_CONNECTED_MOM_ALARM)))); // sending alarm each 5 minutes
                    long diffTime = System.currentTimeMillis() - beginTime;
                    if (diffTime > threshold) {
                        MOMErrorsModel errorModel = new MOMErrorsModel();
                        //errorModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
                        errorModel.setPreceivedSeverity(Integer.valueOf(Defines.databaseConfigurations.get(Defines.SMSC_CONNECTION_MOM_ERROR_SEVERITY)));
                        errorModel.setEngineSrcID(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE);
                        errorModel.setErrorCode(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE + String.valueOf(ErrorCodes.GENERAL_ERROR_FAIL_TO_CONNECT_SMSC));
                        errorModel.setModuleName("Sending SMS Engine");
                        errorModel.setFunctionName("ConnectToFTD");
                        errorModel.setErrorMessage(com.asset.contactstrategy.common.defines.Defines.messagesBundle.getString(errorModel.getErrorCode()));
                        errorModel.setErrorParams(smppConneciton.IP);
                        Utility.sendMOMAlarem(errorModel);
                        beginTime = System.currentTimeMillis();
                    }
                    Thread.sleep(1000);
                    isSuccessConnection = false;
                }
            } // end try
            catch (Exception e) {
                debuglogger.error("Exception in ConnectToFTD || IP || " + smppConneciton.IP + " || Port || " + smppConneciton.Port + " || " + e);
                errlogger.error("Exception in ConnectToFTD || IP || " + smppConneciton.IP + " || Port || " + smppConneciton.Port + " || " + e, e);
            } // end catch
        } // end while
    } // end function

    public void Close() {
        try {
//            debuglogger.info("starting closing Session for " + smppApp.getAppName() + " with smsc " + smppConneciton.IP);
            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Closing Session")
                    .put(GeneralConstants.StructuredLogKeys.APP_NAME, smppApp.getAppName())
                    .put(GeneralConstants.StructuredLogKeys.IP_ADDRRESS, smppConneciton.IP).build());
            //Added because if smsc not connected SendRcvHash is null
            if (SendRcvHash != null) {
//                debuglogger.info("SendRecvHash size || " + SendRcvHash.size());
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRecvHash stats")
                        .put(GeneralConstants.StructuredLogKeys.SIZE, SendRcvHash.size()).build());
            }
//            if (Manager.RcvDelHash != null) {
//                debuglogger.info("RcvDelHash size || " + Manager.RcvDelHash.size());
//            }
            if (Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()) != null) {
//                debuglogger.info("RcvDelHash size || " + Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size());
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRecvHash stats")
                        .put(GeneralConstants.StructuredLogKeys.SIZE, Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size()).build());
            }
            if (recv != null) { // stop receiving Thread
                recv.stopThread();
                if (recv.isAlive()) {
                    recv.join();
                }
                recv = null;
            }
            if (delvresp != null) { // stop deliver response thread.
                delvresp.stopThread();
                if (delvresp.isAlive()) {
                    delvresp.join();
                }
                delvresp = null;
            }
            //Added because if smsc not connected SendRcvHash is null
            if (SendRcvHash != null) {
//                debuglogger.info("SendRecvHash size || " + SendRcvHash.size());
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRecvHash stats")
                        .put(GeneralConstants.StructuredLogKeys.SIZE, SendRcvHash.size()).build());
            }
//            if (Manager.RcvDelHash != null) {
//                debuglogger.info("RcvDelHash size || " + Manager.RcvDelHash.size());
//            }

            if (Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()) != null) {
//                debuglogger.info("RcvDelHash size || " + Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size());
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRecvHash stats")
                        .put(GeneralConstants.StructuredLogKeys.SIZE, Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size()).build());
            }
            if (threadActive == true) {
                stopHeartBeatThread();
            }
            if (GarbageThreadActive == true) {
                stopGarbageCollectorThread();
            }
            if (getConnectionStatus()) {
                unbind();
            }
            if (connection.isOpen() == true) {
                connection.close();
            }
            if (HeartBeatTimer != null) {
                HeartBeatTimer.cancel();
            }
            if (GarbageCollectorTimer != null) {
                GarbageCollectorTimer.cancel();
            }
//            debuglogger.info("ending closing session, session closed successfully");
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending Closing Session, Session Closed Successfully").build());

        } catch (Exception e) {
            debuglogger.error("Exception in closing session || " + e);
            errlogger.error("Exception in closing session || " + e, e);
        }
    }

    /**
     * The below parameters specify the DST MSISDN type 1) alphanumeric 2)
     * shortcode 3) international
     *
     * @param DstTON
     * @param DstNPI
     *
     * @param SMSObject, the de-queued SMS object
     * @return(the sending operation status) 1, success -1, socket connection
     * failure
     */
    public int Submit_SM(byte DstTON, byte DstNPI, HashObject SMSObject) throws SQLException, Exception {
        SMSObject.SMS.setSmsc_id(Integer.valueOf(smppConneciton.Smsc_id)); // Added to keep track of the smsc used to send sms.
        long startTime = System.currentTimeMillis();

        String MessageText = null;
        String AppID = null;
        HashObject ConcSMSObject;
//        debuglogger.debug("Params || DstTON || " + DstTON + " || DstNPI || " + DstNPI + " || AppID || " + SMSObject.SMS.getAppId());
        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMPP Parameters")
                .put(GeneralConstants.StructuredLogKeys.DST_TON, DstTON)
                .put(GeneralConstants.StructuredLogKeys.DST_NPI, DstNPI)
                .put(GeneralConstants.StructuredLogKeys.APP_ID, SMSObject.SMS.getAppId()).build());
        conc = false;
        BufferByte SubmitPDU;
        int returnValue = 1;
        conc_msg_count = 1;

        if (conc_sar_ref_num >= 255) {
            debuglogger.debug("Resetting conc_sar_ref_num || " + 0);
            conc_sar_ref_num = 0;
        }
        switch (SMSObject.SMS.getMsgType().intValue()) {//Msg_Type

            case 0://Text

                switch (SMSObject.SMS.getLangId().intValue()) {//Lang_ID

                    case 0: //US_ASCII

                        if (SMSObject.SMS.getMsgTxt().length() > 160) {
                            conc_msg_length_fixed = 153;
                            conc = true;
                            conc_sar_ref_num++;
                            conc_msg_count = (int) Math.ceil((double) SMSObject.SMS.getMsgTxt().length() / conc_msg_length_fixed);
                        }
                        break;
                    case 1: //Unicode

                        if (SMSObject.SMS.getMsgTxt().length() > 70) {
                            conc_msg_length_fixed = 67;
                            conc = true;
                            conc_sar_ref_num++;
                            conc_msg_count = (int) Math.ceil((double) SMSObject.SMS.getMsgTxt().length() / conc_msg_length_fixed);
                        }
                        break;
                }
                break;
            default://binary message

                if (SMSObject.SMS.getMsgTxt().length() > 140) {
                    conc_msg_length_fixed = 134;
                    conc = true;
                    conc_sar_ref_num++;
                    conc_msg_count = (int) Math.ceil((double) SMSObject.SMS.getMsgTxt().length() / conc_msg_length_fixed);
                }
                break;
        }
        MessageText = SMSObject.SMS.getMsgTxt();
        SMSObject.SMS.setConcMsgCount(BigDecimal.valueOf(conc_msg_count)); // because of else condition in construct pdu.
        SMSObject.SMS.setSmsc_msg_count(conc_msg_count);
        // Begin : edited for handling arabic encoding
        /*
         String appId = SMSObject.SMS.getAppId() ;
         String origMsisdn = SMSObject.SMS.getOrigMsisdn() ;
         String destMsisdn = SMSObject.SMS.getDstMsisdn() ;
         debuglogger.debug( "Message decoding, Original Message : " + MessageText );
         opsLogger.debug( "Message decoding, Original Message : " + MessageText );
         System.out.println( "Message decoding, Original Message : " + MessageText );
         opsLogger.debug( "Message decoding, Dest MSISDN : " + destMsisdn ) ;
         appId = Util.decodeString( appId ) ;
         MessageText = Util.decodeString( MessageText ) ;
         origMsisdn = Util.decodeString( origMsisdn ) ;
         destMsisdn = Util.decodeString( destMsisdn ) ;
         opsLogger.debug( "Message decoding, Converted Message : " + MessageText );
         System.out.println( "Message decoding, Converted Message : " + MessageText );
         debuglogger.debug( "Message decoding, Converted Message : " + MessageText ) ;
         //*/
        // End : edited for handling arabic encoding
        AppID = SMSObject.SMS.getAppId();
        conc_msg_sequeunce = 0;
        int minVal;
        for (int i = 0; i < conc_msg_count; i++) {
            if (conc) {
                ConcSMSObject = null;
                ConcSMSObject = new HashObject();

                ConcSMSObject.id = SMSObject.id;
                ConcSMSObject.type = SMSObject.type;
                ConcSMSObject.enqueueTime = SMSObject.enqueueTime;
                ConcSMSObject.priority = SMSObject.priority;
                ConcSMSObject.log = SMSObject.log;
                ConcSMSObject.setBatchId(SMSObject.getBatchId());
                ConcSMSObject.setMsgid(SMSObject.getMsgid());
                ConcSMSObject.SMS.setSeqId(SMSObject.getSMS().getSeqId());
                ConcSMSObject.SMS.setAppId(SMSObject.SMS.getAppId());
                ConcSMSObject.SMS.setDstMsisdn(SMSObject.SMS.getDstMsisdn());
                ConcSMSObject.SMS.setOrigMsisdn(SMSObject.SMS.getOrigMsisdn());
                ConcSMSObject.SMS.setOrigType(SMSObject.SMS.getOrigType());
                ConcSMSObject.SMS.setMsgType(SMSObject.SMS.getMsgType());
                ConcSMSObject.SMS.setLangId(SMSObject.SMS.getLangId());
                ConcSMSObject.SMS.setNbtrials(SMSObject.SMS.getNbtrials());
                ConcSMSObject.setDequeueTime(SMSObject.getDequeueTime());
                ConcSMSObject.SMS.setReceiptRequested(SMSObject.SMS.getReceiptRequested());
                ConcSMSObject.SMS.setIPAddress(SMSObject.SMS.getIPAddress());
                ConcSMSObject.SMS.setSmsc_id(SMSObject.SMS.getSmsc_id());
                ConcSMSObject.SMS.setOptionalParameter1(SMSObject.SMS.getOptionalParameter1());
                ConcSMSObject.SMS.setOptionalParameter2(SMSObject.SMS.getOptionalParameter2());
                ConcSMSObject.SMS.setOptionalParameter3(SMSObject.SMS.getOptionalParameter3());
                ConcSMSObject.SMS.setOptionalParameter4(SMSObject.SMS.getOptionalParameter4());
                ConcSMSObject.SMS.setOptionalParameter5(SMSObject.SMS.getOptionalParameter5());
                ConcSMSObject.SMS.setTlvOptionalParams(SMSObject.SMS.getTlvOptionalParams());
                ConcSMSObject.SMS.setServiceType(SMSObject.SMS.getServiceType());
                ConcSMSObject.SMS.setEsmClass(SMSObject.SMS.getEsmClass());
                ConcSMSObject.SMS.setProtocolId(SMSObject.SMS.getProtocolId());
                ConcSMSObject.SMS.setPriorityFlag(SMSObject.SMS.getPriorityFlag());
                ConcSMSObject.SMS.setScheduleDeliveryTime(SMSObject.SMS.getScheduleDeliveryTime());
                ConcSMSObject.SMS.setValidityPeriod(SMSObject.SMS.getValidityPeriod());
                ConcSMSObject.SMS.setsmDefaultMsgId(SMSObject.SMS.getsmDefaultMsgId());
                //ConcSMSObject.SMS.setConcMsgCount(SMSObject.SMS.getConcMsgCount());
                //ConcSMSObject.SMS.setConcMsgSequeunce(SMSObject.SMS.getConcMsgSequeunce());
                //ConcSMSObject.SMS.setConcSarRefNum(SMSObject.SMS.getConcSarRefNum());

                minVal = Math.min(MessageText.length() - (i * conc_msg_length_fixed), conc_msg_length_fixed);
                ConcSMSObject.SMS.setMsgTxt(MessageText.substring(i * (conc_msg_length_fixed), (i * conc_msg_length_fixed) + minVal));
                SubmitPDU = ConstructSubmitPDU(DstTON, DstNPI, ConcSMSObject);
                ConcSMSObject.SMS.setSmsc_seq_num(this.sequenceNumber);
                ConcSMSObject.setSendingTime(com.asset.cs.sendingsms.util.Utility.getDateTime());
                PutInSendRecvHash(ConcSMSObject);
            } // end if
            else { //Single SMS
                /*
                 SMSObject.SMS.setAppId(appId);
                 SMSObject.SMS.setMsgTxt(MessageText);
                 SMSObject.SMS.setOrigMsisdn(origMsisdn);
                 SMSObject.SMS.setDstMsisdn(destMsisdn);
                 //*/

                SubmitPDU = ConstructSubmitPDU(DstTON, DstNPI, SMSObject);

                SMSObject.SMS.setSmsc_seq_num(this.sequenceNumber);
                SMSObject.setSendingTime(com.asset.cs.sendingsms.util.Utility.getDateTime());
                PutInSendRecvHash(SMSObject);
            } // end else

            stopHeartBeatThread();
            Defines.smscThrottlingMap.get(Long.parseLong(smppConneciton.Smsc_id)).checkThroughput();
            returnValue = connection.send(SubmitPDU, AppID, getSequenceNumber()); // send submit PDU

//            infologger.info("submit_sm || " + SMSObject.getSMS().getSeqId() + " || " + smppApp.getAppName() + " || " + sequenceNumber + " || " + returnValue);
            infologger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "submit_SM")
                    .put(GeneralConstants.StructuredLogKeys.ID, SMSObject.getSMS().getSeqId())
                    .put(GeneralConstants.StructuredLogKeys.APP_NAME, smppApp.getAppName())
                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, returnValue).build());
//            debuglogger.info("submit_sm || " + SMSObject.getSMS().getSeqId() + " || " + smppApp.getAppName() + " || " + sequenceNumber + " || " + returnValue);

            try {
                startHeartBeatThread();
            } catch (Exception ex) {
                // debuglogger.error("Exception in Submit_SM in startHeartBeatThread ----->" + ex);
                errlogger.error("Exception in Submit_SM in startHeartBeatThread ----->" + ex, ex);
            }

        } // end loop
        // System.out.println("Time from SMSC : " + (System.currentTimeMillis() - startTime));
//        debuglogger.info("Time from SMSC : " + (System.currentTimeMillis() - startTime));
        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Time For SMSC")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        return (returnValue);
    } // end function

    // for testing
//    public void printHashTable() {
//        System.out.println("////////////////////////");
//        System.out.println("////////////Hashtable1/////////////");
//        Enumeration num = Manager.RcvDelHash.keys();
//        while (num.hasMoreElements()) {
//            int qq = (int) num.nextElement();
//            HashObject value = (HashObject) Manager.RcvDelHash.get(qq);
//            System.out.println(value.getSMS().getSmsc_msg_id() + " -- " + qq + " -- " + Manager.RcvDelHash.size());
//        }
//        System.out.println("---------------------------");
//        System.out.println("---------------------------");
//    }
    public void printHashTable2() {
        System.out.println("***************************");
        System.out.println("***********Hashtable2/************" + this.threadNumber);
        Enumeration num = SendRcvHash.keys();
        while (num.hasMoreElements()) {
            int qq = (int) num.nextElement();
            HashObject value = (HashObject) SendRcvHash.get(qq);
            System.out.println(value.getSMS().getSmsc_msg_id() + " -- " + qq + " -- " + SendRcvHash.size());
        }
        System.out.println("+++++++++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++++++++");
    }

    public void Enquire_link() {

        BufferByte EnqPDU;
        EnqPDU = ConstructEnqPDU();
        connection.send(EnqPDU, "Echo", -1);

    } // end function
    // parse the received responses

    /**
     * @param DataResp, the received response
     * @return(value identifying the reponse status)
     */
    public boolean ParsePDU(BufferByte DataResp) {
        /*----------------------------*/
        MessageHeader header = new MessageHeader(); // global variable
        // instantiated here
        /*----------------------------*/

        DataResp.removeInt(); // commandLength

        header.commandID = DataResp.removeInt();
        header.commandStatus = DataResp.removeInt();
        header.sequenceNumber = DataResp.removeInt();
        header.retVal = DataResp.removeCString();

        String valueCommandStatus = Integer.toHexString(header.commandStatus);
        String valueCommand_id = Integer.toHexString(header.commandID);

        // Kerollos Asaad 5/6/2016
        /**
         * Check if the command_id is a deliver_sm receipt. If so, we
         * acknowledge the receipt and then send back deliver_sm_resp.
         */
        if (Integer.parseInt(valueCommand_id) == Data.DELIVER_SM) {
            //logger.debug("all || 0x" + DataResp.removeCString());
            //logger.debug("source address ton || 0x" +  Integer.toHexString(DataResp.removeInt()));
            //logger.debug("all || 0x" + DataResp.removeCString());  
//            debuglogger.debug("source_addr_ton || 0x" + DataResp.removeByte());
//            debuglogger.debug("source_addr_npi|| 0x" + DataResp.removeByte());
//            debuglogger.debug("source_addr || 0x" + DataResp.removeCString());
//            debuglogger.debug("dest_addr_to || 0x" + DataResp.removeByte());
//            debuglogger.debug("dest_addr_npi || 0x" + DataResp.removeByte());
//            debuglogger.debug("destination_addr || 0x" + DataResp.removeCString());
//            debuglogger.debug("esm_classd || 0x" + DataResp.removeByte());
//            debuglogger.debug("protocol_id || 0x" + DataResp.removeByte());
//            debuglogger.debug("priority_flag || 0x" + DataResp.removeByte());
//            debuglogger.debug("schedule_delivery_time || 0x" + DataResp.removeCString());
//            debuglogger.debug("validity_period || 0x" + DataResp.removeCString());
//            debuglogger.debug("registered_delivery || 0x" + DataResp.removeByte());
//            debuglogger.debug("replace_if_present_flag || 0x" + DataResp.removeByte());
//            debuglogger.debug("data_coding|| 0x" + DataResp.removeByte());
//            debuglogger.debug("sm_default_msg_id || 0x" + DataResp.removeByte());
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "R")
                    .put(GeneralConstants.StructuredLogKeys.REQUEST_TYPE, "deliver_sm_resp")
                    .put(GeneralConstants.StructuredLogKeys.SOURCE_ADDRESS_TON, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.SOURCE_ADDRESS_NPI, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.SOURCE_ADDRESS, DataResp.removeCString())
                    .put(GeneralConstants.StructuredLogKeys.DESTINATION_ADDRESS_TON, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.DESTINATION_ADDRESS_NPI, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.DESTINATION_ADDRESS, DataResp.removeCString())
                    .put(GeneralConstants.StructuredLogKeys.ESM_CLASS, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.PROTOCOL_ID, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.PRIORITY_FLAG, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.SCHEDULE_DELIVERY_TIME, DataResp.removeCString())
                    .put(GeneralConstants.StructuredLogKeys.VALIDITY_PERIOD, DataResp.removeCString())
                    .put(GeneralConstants.StructuredLogKeys.REGISTERED_DELIVERY, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.REPLACE_IF_PRESENT_FLAG, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.DATA_CODING, DataResp.removeByte())
                    .put(GeneralConstants.StructuredLogKeys.SM_DEFAULT_MSG_ID, DataResp.removeByte()).build());
            //debuglogger.debug("sm_length || 0x" + DataResp.removeByte());
            int lengthForDeliveryString = DataResp.removeByte();
            // String latestMsg = DataResp.removeCString();
            String latestMsg = null;
            String id = null;
            String stat = null;
            try {
                latestMsg = DataResp.removeString(lengthForDeliveryString, Data.ENC_ASCII);
            } catch (UnsupportedEncodingException ex) {
                debuglogger.error("UnsupportedEncodingException in getting message from deliver_sm||  " + ex);
                errlogger.error("UnsupportedEncodingException in getting message from deliver_sm ||  " + ex, ex);

            }
            if (latestMsg != null) {
                id = latestMsg.substring(latestMsg.indexOf(Defines.ID_STRING + ":") + 3, latestMsg.indexOf(" " + Defines.SUB_STRING));
//                debuglogger.info("message_id || " + id);
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Getting Message ID")
                        .put(GeneralConstants.StructuredLogKeys.MSG_ID, id).build());
                stat = latestMsg.substring(latestMsg.indexOf(Defines.STAT_STRING + ":") + 5, latestMsg.indexOf(" " + Defines.ERR_STRING));
            }
            boolean readFromOptional = true;
            byte status = 0;
            String optionalId = null;
            try {

                while (DataResp.length() > 0) {
//                    debuglogger.info("Starting reading optional parameters");
                    debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Reading Optional Parameters").build());
                    int decimalTag = DataResp.removeShort();
//                    debuglogger.info("Paramter with decimal tag:" + decimalTag);
                    debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Decimal Optional Paramete")
                            .put(GeneralConstants.StructuredLogKeys.DECIMAL_TAG, decimalTag).build());
                    int length = DataResp.removeShort();
//                    debuglogger.info("Paramter with decimal length:" + length);
                    debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Decimal Optional Parameter")
                            .put(GeneralConstants.StructuredLogKeys.LENGTH, length).build());

                    if (decimalTag == 1063) {
                        status = DataResp.removeByte();
//                        debuglogger.info("Starting reading message state tag with value:" + status);
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Reading Message State Tag")
                                .put(GeneralConstants.StructuredLogKeys.VALUE, status).build());
                    } else if (decimalTag == 30) {
                        try {
                            optionalId = DataResp.removeString(length, Data.ENC_ASCII).trim();
//                            debuglogger.info("Starting reading message id tag with value:" + optionalId);
                            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Reading Message id Tag")
                                    .put(GeneralConstants.StructuredLogKeys.VALUE, optionalId).build());
                        } catch (UnsupportedEncodingException ex) {
                            debuglogger.error("UnsupportedEncodingException in getting message from deliver_sm||  " + ex);
                            errlogger.error("UnsupportedEncodingException in getting message from deliver_sm ||  " + ex, ex);
                        }
                    } else {
                        try {
//                            debuglogger.info("Reading parameter  with value:" + DataResp.removeString(length, Data.ENC_ASCII));
                            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Reading Parameter")
                                    .put(GeneralConstants.StructuredLogKeys.VALUE, DataResp.removeString(length, Data.ENC_ASCII)).build());
                        } catch (UnsupportedEncodingException ex) {
                            debuglogger.error("UnsupportedEncodingException in getting message from deliver_sm||  " + ex);
                            errlogger.error("UnsupportedEncodingException in getting message from deliver_sm ||  " + ex, ex);
                        }
                    }
                    if (optionalId != null && status >= 1) {
//                        debuglogger.info("Finished reading optional id and status break reading of optional parameters");
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished Reading Optional ID and Stataus Break Reading of Optional Parameters").build());
                        break;
                    }
                }
                if (optionalId == null || optionalId.isEmpty() == true || optionalId.length() == 0 || status <= 0 || status >= 9) {
                    readFromOptional = false;
                }
            } catch (Exception e) {
                debuglogger.error("Exception in deliver_sm_resp warmup ||  " + e);
                errlogger.error("Exception in deliver_sm_resp warmup ||  " + e, e);
                readFromOptional = false;
            }
            if (readFromOptional == true) {
                id = optionalId;
                stat = "" + String.valueOf(status);
//                debuglogger.info("message_status_optional || " + status);
//                debuglogger.info("message_id_optional || " + optionalId);
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Optional Parameters")
                        .put(GeneralConstants.StructuredLogKeys.STATUS, status)
                        .put(GeneralConstants.StructuredLogKeys.OPTIONAL_ID, optionalId).build());
            } else {

//                debuglogger.info("Converting decimal message id: " + id + " to hexa || " + Integer.toHexString(Integer.valueOf(id)));
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Converting Decimal Message to Hexa")
                        .put(GeneralConstants.StructuredLogKeys.MSG_ID, id)
                        .put(GeneralConstants.StructuredLogKeys.HEX_MSG_ID, Integer.toHexString(Integer.valueOf(id))).build());
                id = Integer.toHexString(Integer.valueOf(id));
            }

            try {
                HashObject SMSObject = RemoveFromRecvDelHash(id, readFromOptional);
                if (SMSObject == null) {
                    SMSObject = new HashObject();
                    SMSObject.getSMS().setSeqId(BigDecimal.valueOf(Long.valueOf(Defines.NOT_FOUND_SEQ_ID)));
                    SMSObject.getSMS().setSmsc_msg_id(id);
                    SMSObject.getSMS().setSmsc_id(Integer.valueOf(smppConneciton.Smsc_id));
                    SMSObject.setOptionalMsgId(readFromOptional);
                    //     System.out.println("FOUND NULL IN RECVDEL HASHMAP --- " + id);
//                    debuglogger.info("FOUND NULL IN RECVDEL HASHMAP --- " + id);
                    debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found Null In RECVDEL HashMap")
                            .put(GeneralConstants.StructuredLogKeys.ID, id).build());
                }
//                infologger.info("deliver_sm || " + SMSObject.getSMS().getSeqId() + " || 0x" + latestMsg);
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deliver_sm stats")
                        .put(GeneralConstants.StructuredLogKeys.SEQ_ID, SMSObject.getSMS().getSeqId())
                        .put(GeneralConstants.StructuredLogKeys.LATEST_MSG, "0x" + latestMsg).build());
                SMSObject.getSMS().setSmsc_seq_num(header.sequenceNumber);
                //SMSObject.getSMS().setIsDelivered(stat.equals(Defines.DELIVRD_STRING));
                if (readFromOptional == false) {
                    SMSObject.getSMS().setSmsc_sms_status(Defines.messageStatus.get(stat));
                } else {
                    SMSObject.getSMS().setSmsc_sms_status(Integer.valueOf(stat));
                }
                Manager.receiver_DeliveryResp_QMap.get(smppApp.getAppName()).add(SMSObject); // ISSUE 25/10, put sms objects for each session on it's blocking queue.
                Manager.deliverResp_updateStatusCountH_Q.add(SMSObject);
            } catch (Exception e) {
                debuglogger.error("Exception in deliver_sm_resp warmup ||  " + e);
                errlogger.error("Exception in deliver_sm_resp warmup ||  " + e, e);
            }
            //if(Defines.DELIVER_SM_RESP_FLAG.equalsIgnoreCase("true"))
            //    DeliverSmResponse(header.sequenceNumber, id);
        }
        //        debuglogger.debug("Resp-Command_id || 0x" + valueCommand_id);
        //        debuglogger.debug("Resp-CommandStatus || " + valueCommandStatus.toUpperCase());
        //        debuglogger.debug("Resp-SequenceNumber || " + header.sequenceNumber);
        //        debuglogger.debug("Resp-header.retVal || " + header.retVal);
        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Resp status")
                .put(GeneralConstants.StructuredLogKeys.COMMAND_ID, valueCommand_id)
                .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, valueCommandStatus.toUpperCase())
                .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, header.sequenceNumber)
                .put(GeneralConstants.StructuredLogKeys.HEADER_RET_VAL, header.retVal).build());

        return (HandleParsedPDU(header));
    }

    /**
     * @return(sendrecv hashtable)
     */
    public Hashtable getHashtable() {
        return (SendRcvHash);
    }

    private boolean HandleParsedPDU(MessageHeader PDUHeader) {
        Integer valueCommand_id = 0;
        try {
            valueCommand_id = new Integer(Integer.toHexString(PDUHeader.commandID));
            switch (valueCommand_id) {
                /*----------------------------------------------------------------------*/
                case Data.BIND_TRANSMITTER_RESP:
                    if (PDUHeader.commandStatus == Data.ESME_ROK) {
//                        debuglogger.info("Bind Success systemID || " + PDUHeader.retVal);
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Success")
                                .put(GeneralConstants.StructuredLogKeys.SYSTEM_ID, PDUHeader.retVal).build());
                        return (true);
                    } else {
//                        debuglogger.info("Bind Failed || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase());
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Failed")
                                .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase()).build());
                        return (false);
                    }
                // break;
                /*----------------------------------------------------------------------*/
                // Kerollos Asaad 5/6/2016
                case Data.BIND_TRANSCEIVER_RESP:
                    if (PDUHeader.commandStatus == Data.ESME_ROK) {
//                        debuglogger.info("Bind Success systemID || " + PDUHeader.retVal);
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Success")
                                .put(GeneralConstants.StructuredLogKeys.SYSTEM_ID, PDUHeader.retVal).build());
                        return (true);
                    } else {
//                        debuglogger.info("Bind Failed || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase());
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Failed")
                                .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase()).build());
                        return (false);
                    }
                case Data.DELIVER_SM:
                    //printHashTable();
                    if (PDUHeader.commandStatus == Data.ESME_ROK) {
//                        infologger.info("R || " + SMSObject.SMS.getAppId() + " || " + PDUHeader.sequenceNumber + " || SUCCESS || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase() + " || msgID || " + PDUHeader.retVal
//                                + " || " + SMSObject.log);
//                        debuglogger.info("Bind Success systemID || " + PDUHeader.retVal);
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Success")
                                .put(GeneralConstants.StructuredLogKeys.SYSTEM_ID, PDUHeader.retVal).build());
                        return (true);
                    } else {
//                        debuglogger.info("Bind Failed || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase());
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Failed")
                                .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase()).build());
                        return (false);
                    }
                // break;
                /*----------------------------------------------------------------------*/
                case Data.SUBMIT_SM_RESP:
                    //printHashTable2();
                    HashObject SMSObject = RemoveFromSendRecvHash(PDUHeader.sequenceNumber);
                    if (SMSObject == null) {
                        boolean ret = (PDUHeader.commandStatus == Data.ESME_ROK);
//                        infologger.info("submit_sm_resp || NOT FOUND IN SENDRECV HASH || " + PDUHeader.sequenceNumber + " || "
//                                + Integer.toHexString(PDUHeader.commandStatus).toUpperCase() + " || hexaDec msgID || " + PDUHeader.retVal);
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "submit_sm_resp Not Found in SENDRECV Hash")
                                .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, PDUHeader.sequenceNumber)
                                .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase())
                                .put(GeneralConstants.StructuredLogKeys.MSG_ID, PDUHeader.retVal).build());
//                        debuglogger.info("NOT FOUND IN SENDRECV HASH || " + PDUHeader.sequenceNumber);
//                        debuglogger.debug("submit_sm_resp || NOT FOUND IN SENDRECV HASH || " + PDUHeader.sequenceNumber + " || SUCCESS || "
//                                + Integer.toHexString(PDUHeader.commandStatus).toUpperCase()
//                                + " || hexaDec msgID || " + PDUHeader.retVal);

                        if (ret == true) {
                            successfullMessages++;
//                            debuglogger.debug("SuccessFull sent MSG || " + successfullMessages);
                            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Sent Successfully")
                                    .put(GeneralConstants.StructuredLogKeys.STATUS, successfullMessages).build());
                        }
                        return ret;
                    }

                    if (PDUHeader.commandStatus == Data.ESME_ROK) {
                        // long decimalRetVal = Long.parseLong(PDUHeader.retVal, 16);
                        BigInteger bigIntRetVal = new BigInteger(PDUHeader.retVal, 16); // eslam.ahmed | 5-5-2020
                        long decimalRetVal = bigIntRetVal.longValue();

                        String conversionRequired = "";
                        if ((Defines.databaseConfigurations.get(Defines.HEXADECIMAL_CONVERTER)).equals("true")) {
                            // set smsc message id with decimal representation value, for sms_concat_h processes conditions.
                            SMSObject.SMS.setSmsc_msg_id(String.valueOf(decimalRetVal));
                            conversionRequired = "required";
                        } else {
                            SMSObject.SMS.setSmsc_msg_id(PDUHeader.retVal);
                            conversionRequired = "not required";
                            boolean trimmed = false;
//                        ArrayList<String> prefixs=new ArrayList<>();
//                        for (String prefix : prefixs) {
//                            if(PDUHeader.retVal.startsWith(prefix)){
//                                SMSObject.SMS.setSmsc_msg_id_trimmed(PDUHeader.retVal.substring(prefix.length()));
                            Collection<LookupModel> prefixs = SystemLookups.VFE_PREFIX.values();
                            for (LookupModel prefix : prefixs) {
                                if (PDUHeader.retVal.startsWith(prefix.getLable())) {
                                    SMSObject.SMS.setSmsc_msg_id_trimmed(PDUHeader.retVal.substring(prefix.getLable().length()));

                                    trimmed = true;
                                }
                            }
                            if (!trimmed && PDUHeader.retVal.length() > 2) {
                                SMSObject.SMS.setSmsc_msg_id_trimmed(PDUHeader.retVal.substring(2));
                            }
                        }
//                        infologger.info("submit_sm_resp || " + SMSObject.getSMS().getSeqId() + " || " + SMSObject.SMS.getAppId() + " || "
//                                + PDUHeader.sequenceNumber + " || SUCCESS || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase()
//                                + " || hexaDec msgID || " + PDUHeader.retVal + " || " + conversionRequired + " Decimal msgID || " + decimalRetVal + " || hexaDec trimmedMsgID || " + SMSObject.SMS.getSmsc_msg_id_trimmed() + " || " + SMSObject.log + " || QUEUE_MSG_ID || " + SMSObject.getMsgid() + " || BATCH_ID|| " + SMSObject.getBatchId());
                        infologger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "submit_sm_resp Success")
                                .put(GeneralConstants.StructuredLogKeys.SEQ_ID, SMSObject.getSMS().getSeqId())
                                .put(GeneralConstants.StructuredLogKeys.APP_ID, SMSObject.SMS.getAppId())
                                .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, PDUHeader.sequenceNumber)
                                .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase())
                                .put(GeneralConstants.StructuredLogKeys.HEX_MSG_ID, PDUHeader.retVal)
                                .put(GeneralConstants.StructuredLogKeys.CONVERSION_REQUIRED, conversionRequired)
                                .put(GeneralConstants.StructuredLogKeys.DECIMAL_MSG_ID, decimalRetVal)
                                .put(GeneralConstants.StructuredLogKeys.HEX_DEC_MSG_ID, SMSObject.SMS.getSmsc_msg_id_trimmed())
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_MSG_ID, SMSObject.getMsgid())
                                .put(GeneralConstants.StructuredLogKeys.BATCH_ID, SMSObject.getBatchId()).build());
//                        debuglogger.info(SMSObject.SMS.getAppId() + " || " + PDUHeader.sequenceNumber);
//                        debuglogger.debug("submit_sm_resp || " + SMSObject.SMS.getAppId() + " || " + PDUHeader.sequenceNumber + " || SUCCESS || "
//                                + Integer.toHexString(PDUHeader.commandStatus).toUpperCase()
//                                + " || hexaDec msgID || " + PDUHeader.retVal + " || " + conversionRequired + " Decimal msgID || " + decimalRetVal + " || hexaDec trimmedMsgID || " + SMSObject.SMS.getSmsc_msg_id_trimmed() + " || " + SMSObject.log + " || QUEUE_MSG_ID || " + SMSObject.getMsgid() + " || BATCH_ID|| " + SMSObject.getBatchId());

                        successfullMessages++;
                        if ((Defines.databaseConfigurations.get(Defines.HEXADECIMAL_CONVERTER)).equals("true")) {
                            // as submit_sm_resp contains message id in hexadecimal in smsc otherwise smpp simulator.
                            // true for smsc usage.
                            SMSObject.setSubmitSMRespTime(com.asset.cs.sendingsms.util.Utility.getDateTime());
                            PutInRecvDelHash(String.valueOf(decimalRetVal), SMSObject);
                        } else {
                            // false for smsc simulator usage.
                            SMSObject.setSubmitSMRespTime(com.asset.cs.sendingsms.util.Utility.getDateTime());
                            PutInRecvDelHash(PDUHeader.retVal, SMSObject);
                        }
                        if (SMSObject.SMS.getReceiptRequested().compareTo(BigDecimal.ONE) == 0) {
                            // In case delivery report requested, insert SMS in sms_concat_h.
                            Manager.receiver_ArchiveConcH_Q.put(SMSObject);
                        } // else { // In case delivery report NOT requested, we don't have to insert SMS in sms_concat_h};

//                        debuglogger.debug("SuccessFull sent MSG || " + successfullMessages);
                        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Successful Sent MSG")
                                .put(GeneralConstants.StructuredLogKeys.STATUS, successfullMessages).build());
                        return (true);
                    } else {
                        int trials = SMSObject.SMS.getNbtrials().intValue();
                        if (trials < Integer.valueOf(Defines.databaseConfigurations.get(Defines.NB_TRIALS))) { // retry sending 4 times only and then stop
//                            infologger.info("submit_sm_resp || " + SMSObject.getSMS().getSeqId() + " || " + SMSObject.SMS.getAppId() + " || " + PDUHeader.sequenceNumber + " || FAIL || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase()
//                                    + " || " + SMSObject.log + " || QUEUE_MSG_ID || " + SMSObject.getMsgid() + " || BATCH_ID|| " + SMSObject.getBatchId());
                            infologger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "submit_sm_resp Fail")
                                    .put(GeneralConstants.StructuredLogKeys.SEQ_ID, SMSObject.getSMS().getSeqId())
                                    .put(GeneralConstants.StructuredLogKeys.APP_ID, SMSObject.SMS.getAppId())
                                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, PDUHeader.sequenceNumber)
                                    .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase())
                                    .put(GeneralConstants.StructuredLogKeys.QUEUE_MSG_ID, SMSObject.getMsgid())
                                    .put(GeneralConstants.StructuredLogKeys.BATCH_ID, SMSObject.getBatchId()).build());

//                            debuglogger.info(SMSObject.SMS.getAppId() + " || " + PDUHeader.sequenceNumber);

//                            debuglogger.debug("submit_sm_resp || " + SMSObject.SMS.getAppId() + " || " + PDUHeader.sequenceNumber + " || FAIL || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase()
//                                    + " || " + SMSObject.log + " || QUEUE_MSG_ID || " + SMSObject.getMsgid() + " || BATCH_ID|| " + SMSObject.getBatchId());
//                            debuglogger.info("Enqueuing message again for resending");
                            infologger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enqueuing Message Again for Resending").build());
                            SMSObject.SMS.setNbtrials(new BigDecimal(trials + 1));
//                            debuglogger.debug("NBTrials || " + trials);
                            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "NBE Stats")
                                    .put(GeneralConstants.StructuredLogKeys.NBE_TRIALS, trials).build());
                            if (SMSObject.type == 1) {
                                // to keep track of invalid Batch messages in Q
                                // We don't need to increaseNoInq as we don't use files anymore
                                // main.increaseNoInQ(SMSObject.id);
                                // SMPPSenderUtils.increaseNoInQ(smppApp);
                            }
                            //              main.Enqueue(SMSObject);
                            MainService mainService = new MainService();
                            mainService.enqMsg(SMSObject, Manager.connectionPerQueue.get(this.smppApp.getAppName()));
                        } else {
//                            debuglogger.info("submit_sm_resp || " + SMSObject.SMS.getAppId() + " || " + PDUHeader.sequenceNumber + " || FAIL || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase()
//                                    + " || DB_ENQ_TIME || " + SMSObject.getEnqueueTime()
//                                    + " || DEQ_TIME || " + SMSObject.getDequeueTime()
//                                    + " || SMSC_ID || " + smppConneciton.System_ID
//                                    + " || SMSC_IP || " + smppConneciton.IP
//                                    + " || SENDER_IP = " + SMSObject.SMS.getIPAddress()
//                                    //+ " || TrackingId = " + SMSObject.SMS.getTrackingId()
//                                    + " || OptionalParameter1 = " + SMSObject.SMS.getOptionalParameter1()
//                                    + " || OptionalParameter2 = " + SMSObject.SMS.getOptionalParameter2()
//                                    + " || OptionalParameter3 = " + SMSObject.SMS.getOptionalParameter3()
//                                    + " || OptionalParameter4 = " + SMSObject.SMS.getOptionalParameter4()
//                                    + " || OptionalParameter5 = " + SMSObject.SMS.getOptionalParameter5()
//                                    + " || MsgCount = " + SMSObject.SMS.getConcMsgCount().intValue()
//                                    + " || MsgSequeunce = " + SMSObject.SMS.getConcMsgSequeunce().intValue()
//                                    + " || Encoding = " + SMSObject.SMS.getLangId().intValue()
//                                    + " || SrcNumber = " + SMSObject.SMS.getOrigMsisdn()
//                                    + " || DstNumber = " + SMSObject.SMS.getDstMsisdn()
//                                    + " || Msg_Type = " + SMSObject.SMS.getMsgType().intValue()
//                                    + " || NBTrials = -1"
//                                    + " || Priority = " + SMSObject.priority
//                                    + " || SarRefNum = " + SMSObject.SMS.getConcSarRefNum().intValue()
//                                    + " || Message = " + SMSObject.SMS.getMsgTxt() + " || QUEUE_MSG_ID || " + SMSObject.getMsgid() + " || BATCH_ID|| " + SMSObject.getBatchId());
                            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, SMSObject.SMS.getMsgTxt())
                                    .put(GeneralConstants.StructuredLogKeys.APP_ID, SMSObject.SMS.getAppId())
                                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, PDUHeader.sequenceNumber)
                                    .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase())
                                    .put(GeneralConstants.StructuredLogKeys.ENQ_TIME, SMSObject.getEnqueueTime())
                                    .put(GeneralConstants.StructuredLogKeys.DEQ_TIME, SMSObject.getDequeueTime())
                                    .put(GeneralConstants.StructuredLogKeys.SMSC_ID, smppConneciton.System_ID)
                                    .put(GeneralConstants.StructuredLogKeys.SMSC_IP, smppConneciton.IP)
                                    .put(GeneralConstants.StructuredLogKeys.SENDER_IP, SMSObject.SMS.getIPAddress())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_1, SMSObject.SMS.getOptionalParameter1())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_2, SMSObject.SMS.getOptionalParameter2())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_3, SMSObject.SMS.getOptionalParameter3())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_4, SMSObject.SMS.getOptionalParameter4())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_5, SMSObject.SMS.getOptionalParameter5())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_COUNT, SMSObject.SMS.getConcMsgCount().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_SEQUENCE, SMSObject.SMS.getConcMsgSequeunce().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.ENCODING, SMSObject.SMS.getLangId().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.SRC_NUMBER, SMSObject.SMS.getOrigMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.DEST_NUMBER, SMSObject.SMS.getDstMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_TYPE, SMSObject.SMS.getMsgType().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.PRIORITY, SMSObject.priority)
                                    .put(GeneralConstants.StructuredLogKeys.SAR_MSG_REF_NUM, SMSObject.SMS.getConcSarRefNum().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.QUEUE_MSG_ID, SMSObject.getMsgid())
                                    .put(GeneralConstants.StructuredLogKeys.BATCH_ID, SMSObject.getBatchId()).build());

//                            infologger.info("submit_sm_resp || " + SMSObject.getSMS().getSeqId() + " || " + SMSObject.SMS.getAppId() + " || " + PDUHeader.sequenceNumber + " || FAIL || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase()
//                                    + " || DB_ENQ_TIME || " + SMSObject.getEnqueueTime()
//                                    + " || DEQ_TIME || " + SMSObject.getDequeueTime()
//                                    + " || SMSC_ID || " + smppConneciton.System_ID
//                                    + " || SMSC_IP || " + smppConneciton.IP
//                                    + " || SENDER_IP = " + SMSObject.SMS.getIPAddress()
//                                    //+ " || TrackingId = " + SMSObject.SMS.getTrackingId()
//                                    + " || OptionalParameter1 = " + SMSObject.SMS.getOptionalParameter1()
//                                    + " || OptionalParameter2 = " + SMSObject.SMS.getOptionalParameter2()
//                                    + " || OptionalParameter3 = " + SMSObject.SMS.getOptionalParameter3()
//                                    + " || OptionalParameter4 = " + SMSObject.SMS.getOptionalParameter4()
//                                    + " || OptionalParameter5 = " + SMSObject.SMS.getOptionalParameter5()
//                                    + " || MsgCount = " + SMSObject.SMS.getConcMsgCount().intValue()
//                                    + " || MsgSequeunce = " + SMSObject.SMS.getConcMsgSequeunce().intValue()
//                                    + " || Encoding = " + SMSObject.SMS.getLangId().intValue()
//                                    + " || SrcNumber = " + SMSObject.SMS.getOrigMsisdn()
//                                    + " || DstNumber = " + SMSObject.SMS.getDstMsisdn()
//                                    + " || Msg_Type = " + SMSObject.SMS.getMsgType().intValue()
//                                    + " || NBTrials = -1"
//                                    + " || Priority = " + SMSObject.priority
//                                    + " || SarRefNum = " + SMSObject.SMS.getConcSarRefNum().intValue()
//                                    + " || Message = " + SMSObject.SMS.getMsgTxt() + " || QUEUE_MSG_ID || " + SMSObject.getMsgid() + " || BATCH_ID|| " + SMSObject.getBatchId());
                            infologger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "submit_sm_resp Fail")
                                    .put(GeneralConstants.StructuredLogKeys.SEQ_ID, SMSObject.getSMS().getAppId())
                                    .put(GeneralConstants.StructuredLogKeys.APP_ID, SMSObject.SMS.getAppId())
                                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, PDUHeader.sequenceNumber)
                                    .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase())
                                    .put(GeneralConstants.StructuredLogKeys.ENQ_TIME, SMSObject.getEnqueueTime())
                                    .put(GeneralConstants.StructuredLogKeys.DEQ_TIME, SMSObject.getDequeueTime())
                                    .put(GeneralConstants.StructuredLogKeys.SMSC_ID, smppConneciton.System_ID)
                                    .put(GeneralConstants.StructuredLogKeys.SMSC_IP, smppConneciton.IP)
                                    .put(GeneralConstants.StructuredLogKeys.SENDER_IP, SMSObject.SMS.getIPAddress())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_1, SMSObject.SMS.getOptionalParameter1())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_2, SMSObject.SMS.getOptionalParameter2())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_3, SMSObject.SMS.getOptionalParameter3())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_4, SMSObject.SMS.getOptionalParameter4())
                                    .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_5, SMSObject.SMS.getOptionalParameter5())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_COUNT, SMSObject.SMS.getConcMsgCount().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_SEQUENCE, SMSObject.SMS.getConcMsgSequeunce().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.ENCODING, SMSObject.SMS.getLangId().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.SRC_NUMBER, SMSObject.SMS.getOrigMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.DEST_NUMBER, SMSObject.SMS.getDstMsisdn())
                                    .put(GeneralConstants.StructuredLogKeys.MSG_TYPE, SMSObject.SMS.getMsgType().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.PRIORITY, SMSObject.priority)
                                    .put(GeneralConstants.StructuredLogKeys.SAR_MSG_REF_NUM, SMSObject.SMS.getConcSarRefNum().intValue())
                                    .put(GeneralConstants.StructuredLogKeys.QUEUE_MSG_ID, SMSObject.getMsgid())
                                    .put(GeneralConstants.StructuredLogKeys.BATCH_ID, SMSObject.getBatchId()).build());
                            SMSObject.getSMS().setSmsc_sms_status(Defines.messageStatus.get(Defines.UNKNOWN_STATUS_STRING)); // message is in invalid state.
                            Manager.receiver_ArchiveConcH_Q.put(SMSObject);
                            Manager.deliverResp_updateStatusCountH_Q.put(SMSObject);
                        }
                        // the next if condition part is added in case the ftd
                        // looses connection with the SMSC and the application
                        // was already bound to it, the FTD replies to any message
                        // sent in this case with B(invalid dest address)
                        // to handle this case i disconnect from the FTD, and by the
                        // arrival of a new SMS reconnection is re-established
                        if (PDUHeader.commandStatus == Data.ESME_RINVDSTADR) {
                            debuglogger.warn("Forcing disconnection");
                            connection.close();
                        } // end if

                        return (false);
                    } // end else
                // break;
                /*----------------------------------------------------------------------*/
                case Data.ENQUIRE_LINK_RESP:
                    if (PDUHeader.commandStatus == Data.ESME_ROK) {
//                        debuglogger.info("Alive signal sent");
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Alive Signal sent").build());
                        return (true);
                    } else {
//                        debuglogger.info("Alive signal failed || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase());
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Alive Signal Failed")
                                .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase()).build());

                        return (false);
                    }
                // break;
                /*----------------------------------------------------------------------*/
                case Data.UNBIND_RESP:
                    if (PDUHeader.commandStatus == Data.ESME_ROK) {
//                        debuglogger.info("Unbind success");
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Unbind Success").build());

                        return (true);
                    } else {
//                        debuglogger.info("Unbind failed || " + Integer.toHexString(PDUHeader.commandStatus).toUpperCase());
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Unbind Failed")
                                .put(GeneralConstants.StructuredLogKeys.COMMAND_STATUS, Integer.toHexString(PDUHeader.commandStatus).toUpperCase()).build());

                        return (false);
                    }
                // break;
                /*----------------------------------------------------------------------*/
                default:
//                    debuglogger.info("Unknown response");
                    debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Unknown Response").build());
                    return false;
                // break;
            } // end switch

        } // end try
        /*----------------------------------------------------------------------*/ catch (SQLException e) {
            // debuglogger.error("SqlException in HandleParsedPDU || " + e);
            errlogger.error("SqlException in HandleParsedPDU || " + e, e);
            return (false);
        } /*----------------------------------------------------------------------*/ catch (Exception e) {
            // debuglogger.error("Exception in HandleParsedPDU || " + e);
            errlogger.error("Exception in HandleParsedPDU || " + e, e);
            return (false);
        }
    }

    // TODO refactor
    private HashObject RemoveFromSendRecvHash(int seqNumber) {
        try {
            // commented in cs 29Aug2016.
            /*int count = 0;
             while (count < 3) {
             if (SendRcvHash.containsKey(seqNumber)) {
             HashObject retVal = (HashObject) SendRcvHash.get(seqNumber);
             SendRcvHash.remove(seqNumber);
             debuglogger.debug("SendRcvHash || Key=" + seqNumber);
             return retVal;
             } else {
             debuglogger.warn("SendRcvHash || NOT FOUND, Key || " + seqNumber);
             count++;
             debuglogger.debug("SendRcvHash || CurrentThread || " + Thread.currentThread().getName());
             Thread.sleep(50);
             }
             }*/
            if (SendRcvHash.containsKey(seqNumber)) {
                HashObject retVal = (HashObject) SendRcvHash.get(seqNumber);
                SendRcvHash.remove(seqNumber);
//                debuglogger.debug("SendRcvHash || Key = " + seqNumber);
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRCVHash")
                        .put(GeneralConstants.StructuredLogKeys.KEY, seqNumber).build());
                return retVal;
            } else {
//                debuglogger.debug("SendRcvHash || NOT FOUND || Key || " + seqNumber);
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRCVHash not found")
                        .put(GeneralConstants.StructuredLogKeys.KEY, seqNumber).build());
                return null;
            }
        } catch (Exception e) {
            // debuglogger.error("Exception in removing from SendRcvHash || " + e);
            errlogger.error("Exception in removing from SendRcvHash || " + e, e);
            return null;
        }
    }

    // for testing
    private HashObject getFromSendRecvHash(int seqNumber) {
        try {
            int count = 0;
            while (count < 3) {
                if (SendRcvHash.containsKey(seqNumber)) {
                    HashObject retVal = (HashObject) SendRcvHash.get(seqNumber);
                    // SendRcvHash.remove(seqNumber);
//                    debuglogger.debug("SendRcvHash || Key=" + seqNumber);
                    debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRCVHash")
                            .put(GeneralConstants.StructuredLogKeys.KEY, seqNumber).build());
                    return retVal;
                } else {
                    debuglogger.warn("SendRcvHash || NOT FOUND, Key || " + seqNumber);
                    count++;
                    debuglogger.debug("SendRcvHash || CurrentThread || " + Thread.currentThread().getName());
                    Thread.sleep(50);
                }
            }
            return null;
        } catch (Exception e) {
            // debuglogger.error("SendRcvHash || Exception || " + e);
            errlogger.error("SendRcvHash || Exception || " + e, e);
            return null;
        }
    }

    private void PutInSendRecvHash(HashObject SMSObject) {

        boolean windowFlag = false;

        while (SendRcvHash.size() >= sendRecvHashWindowSize) {
            /*if (windowFlag) {
             debuglogger.info("SendRcvHash || Window FULL OUT, length || " + SendRcvHash.size());
             windowFlag = false;
             break;
             }*/
            try {
//                debuglogger.info("SendRcvHash || Window FULL, length || " + SendRcvHash.size());
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRcvHash Window Full")
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, SendRcvHash.size()).build());
                windowFlag = true;
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // debuglogger.error("SendRcvHash || InterruptedException || " + e);
                errlogger.error("SendRcvHash || InterruptedException ||  " + e, e);
            } catch (Exception e) {
                // debuglogger.error("SendRcvHash || Exception || " + e);
                errlogger.error("SendRcvHash || Exception || " + e, e);
            }
        }
        if (windowFlag == true) {
            windowFlag = false;
//            debuglogger.info("SendRcvHash || Window NOT FULL, length || " + SendRcvHash.size());
            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRcvHash Window Not Full")
                    .put(GeneralConstants.StructuredLogKeys.LENGTH, SendRcvHash.size()).build());
        }

//        debuglogger.debug("SendRcvHash || CommandID || " + Integer.toHexString(Data.SUBMIT_SM));
        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRCVHash info")
                .put(GeneralConstants.StructuredLogKeys.COMMAND_ID, Integer.toHexString(Data.SUBMIT_SM)).build());

        try {
            SMSObject.log
                    = //  getDateTime()
                    "SendRcvHash || DB_ENQ_TIME || " + SMSObject.getEnqueueTime()
                    + " || DEQ_TIME || " + SMSObject.getDequeueTime()
                    + " || SMSC_ID || " + smppConneciton.System_ID
                    + " || SMSC_IP || " + smppConneciton.IP
                    + " || SENDER_IP = " + SMSObject.SMS.getIPAddress()
                    //+ " || TrackingId = " + SMSObject.SMS.getTrackingId()
                    + " || OptionalParameter1 = " + SMSObject.SMS.getOptionalParameter1()
                    + " || OptionalParameter2 = " + SMSObject.SMS.getOptionalParameter2()
                    + " || OptionalParameter3 = " + SMSObject.SMS.getOptionalParameter3()
                    + " || OptionalParameter4 = " + SMSObject.SMS.getOptionalParameter4()
                    + " || OptionalParameter5 = " + SMSObject.SMS.getOptionalParameter5()
                    + " || MsgCount = " + SMSObject.SMS.getConcMsgCount().intValue()
                    + " || MsgSequeunce = " + SMSObject.SMS.getConcMsgSequeunce().intValue()
                    + " || Encoding = " + SMSObject.SMS.getLangId().intValue()
                    + " || SrcNumber = " + SMSObject.SMS.getOrigMsisdn()
                    + " || DstNumber = " + SMSObject.SMS.getDstMsisdn()
                    + " || Msg_Type = " + SMSObject.SMS.getMsgType().intValue()
                    + " || NBTrials = " + SMSObject.SMS.getNbtrials()
                    + " || Priority = " + SMSObject.priority
                    + " || SarRefNum = " + SMSObject.SMS.getConcSarRefNum().intValue()
                    + " || Message = " + SMSObject.SMS.getMsgTxt()
                    + " || MessageCount = " + SMSObject.SMS.getSmsc_msg_count()
                    + " || DeliveryRequest = " + SMSObject.SMS.getReceiptRequested();
            // if a record exists with the same sequence
            // remove this record, most probably timed-out record
            Defines.smscThrottlingMap.get(Long.parseLong(smppConneciton.Smsc_id)).checkWindowSize(SendRcvHash);
            if (SendRcvHash.contains(sequenceNumber)) {
                SendRcvHash.remove(sequenceNumber);
            }
            SendRcvHash.put(sequenceNumber, (HashObject) SMSObject);
        } catch (Exception e) {
            // debuglogger.error("Exception in putting in SendRcvHash || " + e.getMessage());
            errlogger.error("Exception in putting in SendRcvHash || " + e.getMessage());
        }
    }

    private HashObject RemoveFromRecvDelHash(String smscMssgId, boolean hexadecimalId) {
        try {
            // commented in cs 29Aug2016.
            /*int count = 0;
             while (count < 3) {
             if (Manager.RcvDelHash.containsKey(smscMssgId)) {
             HashObject retVal = (HashObject) Manager.RcvDelHash.get(smscMssgId);
             Manager.RcvDelHash.remove(smscMssgId);
             System.out.println("remove from RcvDelHash -- " + smscMssgId);
             debuglogger.debug("RcvDelHash || Key=" + smscMssgId);
             return retVal;
             } else {
             debuglogger.debug("RcvDelHash || NOT FOUND, Key || " + smscMssgId);
             count++;
             debuglogger.debug("RcvDelHash || CurrentThread || " + Thread.currentThread().getName());
             Thread.sleep(50);
             }
             }*/

            synchronized (Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName())) {
                if (hexadecimalId) {
                    if (Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).containsKey(smscMssgId)) {
                        HashObject retVal = (HashObject) Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).get(smscMssgId);
                        Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).remove(smscMssgId);
                        //System.out.println("remove from RcvDelHash -- " + smscMssgId);
//                        debuglogger.info("RcvDelHash || Key=" + smscMssgId);
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RcvDelHash Key Found")
                                .put(GeneralConstants.StructuredLogKeys.KEY, smscMssgId).build());
                        return retVal;
                    } else {
//                        debuglogger.info("RcvDelHash || NOT FOUND, Key || " + smscMssgId);
                        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Remove from RcvDelHash")
                                .put(GeneralConstants.StructuredLogKeys.KEY, smscMssgId).build());
                        return null;
                    }
                } else {
                    Set<String> keys = Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).keySet();
                    for (String key : keys) {
                        //  System.out.println("Value of "+key+" is: "+hm.get(key));
                        if (key.matches(".*" + smscMssgId)) {
                            HashObject retVal = (HashObject) Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).get(key);
                            Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).remove(key);
//                            debuglogger.info("RcvDelHash || Key=" + smscMssgId);
                            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RcvDelHash Found")
                                    .put(GeneralConstants.StructuredLogKeys.KEY, smscMssgId).build());
                            return retVal;
                        }
                    }
//                    debuglogger.info("RcvDelHash || NOT FOUND, Key || " + smscMssgId);
                    debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RcvDelHash Not Found")
                            .put(GeneralConstants.StructuredLogKeys.KEY, smscMssgId).build());
                    return null;
                }
            }
        } catch (Exception e) {
            // debuglogger.error("Exception in removing from RecvDelHash || " + e);
            errlogger.error("Exception in removing from RecvDelHash || " + e, e);
            return null;
        }
    }
//
//    private HashObject getFromRecvDelHash(int seqNumber) {
//        try {
//            int count = 0;
//            while (count < 3) {
//                if (Manager.RcvDelHash.containsKey(seqNumber)) {
//                    HashObject retVal = (HashObject) Manager.RcvDelHash.get(seqNumber);
//                    // SendRcvHash.remove(seqNumber);
//                    debuglogger.debug("RcvDelHash || Key=" + seqNumber);
//                    return retVal;
//                } else {
//                    debuglogger.warn("RcvDelHash || NOT FOUND, Key || " + seqNumber);
//                    count++;
//                    debuglogger.debug("RcvDelHash || CurrentThread || " + Thread.currentThread().getName());
//                    Thread.sleep(50);
//                }
//            }
//            return null;
//        } catch (Exception e) {
//            debuglogger.error("Exception ||  " + e.getMessage());
//            errlogger.error("Exception ||  " + e.getMessage());
//            return null;
//        }
//    }

    private void PutInRecvDelHash(String key, HashObject SMSObject) {
        boolean windowFlag = false;
        while (Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size() >= recvDelHashWindowSize) {
            if (windowFlag) {
//                debuglogger.info("RcvDelHash || Window FULL OUT, length || " + Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size());
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RcvDelHash Window Full Out")
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size()).build());
                windowFlag = false;
                break;
            }
            try {
//                debuglogger.info("RcvDelHash || Window FULL, length || " + Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size());
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RcvDelHash Window Full")
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size()).build());
                windowFlag = true;
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // debuglogger.error("RcvDelHash || InterruptedException ||  " + e);
                errlogger.error("RcvDelHash || InterruptedException ||  " + e, e);
            } catch (Exception e) {
                // debuglogger.error("RcvDelHash || Exception ||  " + e);
                errlogger.error("RcvDelHash || Exception ||  " + e, e);
            }
        }
        if (windowFlag == true) {
            windowFlag = false;
//            debuglogger.info("RcvDelHash || Window NOT FULL, length || " + Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size());
            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RcvDelHash Window Not Full")
                    .put(GeneralConstants.StructuredLogKeys.LENGTH, Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).size()).build());
        }

//        debuglogger.debug("RcvDelHash || CommandID || " + Integer.toHexString(Data.SUBMIT_SM));
        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRCVHash info")
                .put(GeneralConstants.StructuredLogKeys.COMMAND_ID, Integer.toHexString(Data.SUBMIT_SM)).build());

        try {
            SMSObject.log
                    = //  getDateTime()
                    " RcvDelHash || DB_ENQ_TIME || " + SMSObject.getEnqueueTime()
                    + " || DEQ_TIME || " + SMSObject.getDequeueTime()
                    + " || SMSC_ID || " + smppConneciton.System_ID
                    + " || SMSC_IP || " + smppConneciton.IP
                    + " || SENDER_IP = " + SMSObject.SMS.getIPAddress()
                    //+ " || TrackingId = " + SMSObject.SMS.getTrackingId()
                    + " || OptionalParameter1 = " + SMSObject.SMS.getOptionalParameter1()
                    + " || OptionalParameter2 = " + SMSObject.SMS.getOptionalParameter2()
                    + " || OptionalParameter3 = " + SMSObject.SMS.getOptionalParameter3()
                    + " || OptionalParameter4 = " + SMSObject.SMS.getOptionalParameter4()
                    + " || OptionalParameter5 = " + SMSObject.SMS.getOptionalParameter5()
                    + " || MsgCount = " + SMSObject.SMS.getConcMsgCount().intValue()
                    + " || MsgSequeunce = " + SMSObject.SMS.getConcMsgSequeunce().intValue()
                    + " || Encoding = " + SMSObject.SMS.getLangId().intValue()
                    + " || SrcNumber = " + SMSObject.SMS.getOrigMsisdn()
                    + " || DstNumber = " + SMSObject.SMS.getDstMsisdn()
                    + " || Msg_Type = " + SMSObject.SMS.getMsgType().intValue()
                    + " || NBTrials = " + SMSObject.SMS.getNbtrials()
                    + " || Priority = " + SMSObject.priority
                    + " || SarRefNum = " + SMSObject.SMS.getConcSarRefNum().intValue()
                    + " || Message = " + SMSObject.SMS.getMsgTxt()
                    + " || MessageCount = " + SMSObject.SMS.getSmsc_msg_count()
                    + " || DeliveryRequest = " + SMSObject.SMS.getReceiptRequested();
            // if a record exists with the same sequence
            // remove this record, most probably timed-out record
            if (Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).contains(key)) {
                Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).remove(key);
                //System.out.println("remove from RcvDelHash -- " + key);
            }
            Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).put(key, (HashObject) SMSObject);
        } catch (Exception e) {
            // debuglogger.error("Exception in putting in RcvDelHash || " + e.getMessage());
            errlogger.error("Exception in putting in RcvDelHash || " + e.getMessage());
        }
    }

    private BufferByte ConstructSubmitPDU(byte DstTON, byte DstNPI, HashObject SMSObject) {

        String Message = null;
        byte[] MessageBytes = null;
        /*----------------------*/
        BufferByte data = null;
        MessageHeader header = null;

        String service_type; // used to indicate the sms application service

        byte source_addr_ton; // Type of number of source address

        byte source_addr_npi; // Numbering plan indicator for source address

        String source_addr; // address of SME which originated this message

        byte dest_addr_ton; // Type of number for destination

        byte dest_addr_npi; // Numbering number plan for destination

        String destination_addr; // destination address of the short message
        // (number of the recepient MS)

        byte esm_class = 0; // Indicates Message mode & message type

        byte protocol_id;
        byte priority_flag; // Designates the priority level of this message

        String schedule_delivery_time; // set to null for immediate message
        // delivery

        String validity_period;
        byte registered_delivery; // Indicator to signify if an SMSC delivery
        // receipt or an SME ack is required

        byte replace_if_present_flag; // flag indicating if submitted message
        // should replace an existing message

        byte data_coding = 0; // deDEBUGs the encoding scheme of the short
        // message user data

        byte sm_default_msg_id; // set to NULL

        byte sm_length; // length in octets of the short_message user data

        String short_message; // message

        Gsm7BitCharset GSM7Bit;

        int length = 0;
        byte SrcNPI;
        byte SrcTON;
        /*--------------------------------------------------------------------------------------------------------------*/
        if (SMSObject == null) {
//            debuglogger.info("Object passed is null");
            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Object Passed is Null").build());
            return null;//nullpointer exception

        }

        try {
//            debuglogger.info("Constructing Submit PDU");
            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Constructing Submit PDU").build());
            String Encoding = null;
            header = new MessageHeader();
            header.commandID = Data.SUBMIT_SM;
            header.commandStatus = 0x00;
            header.sequenceNumber = assignSequenceNumber();
            /*----------------Body-----------------------------*/
            SrcNPI = 1;
            SrcTON = 1;

            switch (SMSObject.SMS.getOrigType().intValue()) {
                case 0: // Shortcode

                    SrcNPI = Data.GSM_NPI_PRIVATE;
                    SrcTON = Data.GSM_TON_NETWORK;
                    break;
                case 1: // international

                    SrcNPI = Data.GSM_NPI_ISDN;
                    SrcTON = Data.GSM_TON_INTERNATIONAL;
                    break;
                case 2: // Alphanumeric

                    SrcNPI = Data.GSM_NPI_ISDN;
                    SrcTON = Data.GSM_TON_ALPHANUMERIC;
                    break;
            }
//            debuglogger.debug("AppID || " + SMSObject.SMS.getAppId());
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRCVHash info")
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, SMSObject.SMS.getAppId()).build());
//            debuglogger.debug("Priority || " + SMSObject.priority);
//            debuglogger.debug("MessageType || "
//                    + SMSObject.SMS.getMsgType().intValue());
//            debuglogger.debug("OrigType || "
//                    + SMSObject.SMS.getOrigType().intValue());
//            debuglogger.debug("NBtrials || "
//                    + SMSObject.SMS.getNbtrials().intValue());
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRCVHash info")
                    .put(GeneralConstants.StructuredLogKeys.MSG_TYPE, SMSObject.SMS.getMsgType().intValue())
                    .put(GeneralConstants.StructuredLogKeys.ORIG_TYPE, SMSObject.SMS.getOrigType().intValue())
                    .put(GeneralConstants.StructuredLogKeys.NBE_TRIALS, SMSObject.SMS.getNbtrials().intValue()).build());
//            

            service_type = SMSObject.SMS.getServiceType() == null ? null : SMSObject.SMS.getServiceType();
            source_addr_ton = SrcTON;
//            debuglogger.debug("SrcTON || " + SrcTON);
            source_addr_npi = SrcNPI;
//            debuglogger.debug("SrcNPI || " + SrcNPI);
            source_addr = SMSObject.SMS.getOrigMsisdn();
//            debuglogger.debug("Src Number || " + SMSObject.SMS.getOrigMsisdn());
            dest_addr_ton = DstTON;
//            debuglogger.debug("DstTON || " + DstTON);
            dest_addr_npi = DstNPI;
//            debuglogger.debug("DstNPI || " + DstNPI);
            destination_addr = SMSObject.SMS.getDstMsisdn();
//            debuglogger.debug("Dst Number || " + SMSObject.SMS.getDstMsisdn());
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMS Stats")
                    .put(GeneralConstants.StructuredLogKeys.SRC_TON, SrcTON)
                    .put(GeneralConstants.StructuredLogKeys.SRC_NPI, SrcNPI)
                    .put(GeneralConstants.StructuredLogKeys.SRC_NUMBER, SMSObject.SMS.getOrigMsisdn())
                    .put(GeneralConstants.StructuredLogKeys.DST_TON, DstTON)
                    .put(GeneralConstants.StructuredLogKeys.DST_NPI, DstNPI)
                    .put(GeneralConstants.StructuredLogKeys.DEST_NUMBER, SMSObject.SMS.getDstMsisdn()).build());
            /*---------------------------------------------*/
            switch (SMSObject.SMS.getMsgType().intValue()) {

                case 0: // text messages

                    esm_class = SMSObject.SMS.getEsmClass() == null ? Data.SM_ESM_DEFAULT : Byte.decode(SMSObject.SMS.getEsmClass().toString());

                    switch (SMSObject.SMS.getLangId().intValue()) {
                        case 0: // english message

                            /*GSM7Bit = new Gsm7BitCharset("GSM7Bit", null);
                            Message = new String(GSM7Bit.encode(SMSObject.SMS.getMsgTxt()).array());*/
                            Message = new String(SMSObject.SMS.getMsgTxt().getBytes(Data.ENC_UTF8), Data.ENC_UTF8);
                            length = Message.getBytes().length;
                            data_coding = Data.DCS_English; // case default

                            break;

                        case 1: // arabic message
                            // Old
                            //Message = new String(SMSObject.SMS.getMsgTxt().getBytes(), Data.ENC_CP1256);
                            // After supporting AR8ISO58859P6
                            Message = SMSObject.SMS.getMsgTxt();
                            if (((String) Defines.fileConfigurations.get(Defines.DATABASE_VERSION)).equalsIgnoreCase(Defines.DATABASE_WESTERN)) {
//                                debuglogger.debug("database is WESTERN start encoding sms");
                                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "database is WESTERN start encoding sms").build());
//                                Message = new String(SMSObject.SMS.getMsgTxt().getBytes("Cp1252"), Data.ENC_CP1256);
                                Message = new String(SMSObject.SMS.getMsgTxt().getBytes("UTF16"), Data.ENC_UTF16);
                            }

                            debuglogger.debug(Message);
                            //Message = new String(SMSObject.SMS.getMsgTxt().getBytes(), Data.ENC_ISO8859_6);
                            Encoding = Data.ENC_UTF16;
                            length = Message.getBytes(Encoding).length - 2; // remove
                            // the extra
                            // mark
                            // values of
                            // UTF16 (2
                            // values)

                            data_coding = Data.DCS_Arabic; // case UCS2

                            break;
                    }
                    break;
                /*case 8:

                 esm_class
                 = Data.SM_ESM_DEFAULT;
                 Encoding = Data.ENC_ISO8859_1;
                 long longLength = SMSObject.SMS.getMessageBlob().getLength();
                 length = new Integer(String.valueOf(longLength));
                 MessageBytes = new byte[length];

                 SMSObject.SMS.getMessageBlob().getBinaryStream().read(MessageBytes);
                 data_coding = Data.DCS_Binary; // 8-bit binary

                 break;
                 default: // Binary Message

                 Message = SMSObject.SMS.getMsgTxt();
                 esm_class
                 = Data.SM_ESM_DEFAULT;
                 Encoding = Data.ENC_ISO8859_1;
                 length = Message.getBytes(Encoding).length;
                 data_coding = Data.DCS_Binary; // 8-bit binary

                 break;*/
            }
            protocol_id = SMSObject.SMS.getProtocolId() == null ? Data.DFLT_PROTOCOLID : Byte.decode(SMSObject.SMS.getProtocolId().toString());
            priority_flag = SMSObject.SMS.getPriorityFlag() == null ? Data.SM_NOPRIORITY : Byte.decode(SMSObject.SMS.getPriorityFlag().toString());
            schedule_delivery_time = SMSObject.SMS.getScheduleDeliveryTime() == null ? null : SMSObject.SMS.getScheduleDeliveryTime();
            validity_period = SMSObject.SMS.getValidityPeriod() == null ? null : SMSObject.SMS.getValidityPeriod();
            // Kerollos Asaad
            if (SMSObject.SMS.getReceiptRequested().compareTo(BigDecimal.ONE) == 0) {
                registered_delivery = Data.SM_SMSC_RECEIPT_REQUESTED;
            } else {
                registered_delivery = Data.SM_SMSC_RECEIPT_NOT_REQUESTED;
            }
            //registered_delivery = Data.SM_SMSC_RECEIPT_REQUESTED; // change
            replace_if_present_flag = Data.SM_NOREPLACE;

//            debuglogger.debug("Encoding || " + SMSObject.SMS.getLangId().intValue());
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRCVHash info")
                    .put(GeneralConstants.StructuredLogKeys.ENCODING, SMSObject.SMS.getLangId().intValue()).build());
            sm_default_msg_id = SMSObject.SMS.getsmDefaultMsgId() == null ? Data.DFLT_DFLTMSGID : Byte.decode(SMSObject.SMS.getsmDefaultMsgId().toString());
            sm_length = (byte) length;
//            debuglogger.debug("SM_Length || " + length);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SM_Stats")
                    .put(GeneralConstants.StructuredLogKeys.LENGTH, length).build());
            short_message = Message;
//            debuglogger.debug("Message || " + short_message);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, short_message).build());
            /*-----------------------------------------------------*/
            BufferByte BodyData = new BufferByte();

            BodyData.appendCString(service_type);
            BodyData.appendByte(source_addr_ton);
            BodyData.appendByte(source_addr_npi);
            BodyData.appendCString(source_addr);
            BodyData.appendByte(dest_addr_ton);
            BodyData.appendByte(dest_addr_npi);
            BodyData.appendCString(destination_addr);
            BodyData.appendByte(esm_class);
            BodyData.appendByte(protocol_id);
            BodyData.appendByte(priority_flag);
            BodyData.appendCString(schedule_delivery_time);
            BodyData.appendCString(validity_period);
            BodyData.appendByte(registered_delivery);
            BodyData.appendByte(replace_if_present_flag);
            BodyData.appendByte(data_coding);
            BodyData.appendByte(sm_default_msg_id);
            BodyData.appendByte(sm_length);

//            debuglogger.debug("Encoding || " + Encoding);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Encoding Data")
                    .put(GeneralConstants.StructuredLogKeys.ENCODING, Encoding).build());
            if (SMSObject.SMS.getMsgType().intValue() != 8) {
                BodyData.appendString(short_message, Encoding);
            } else {
                BodyData.appendBytes(MessageBytes, length);
            }

            //Optional parameters
            if (SMSObject.SMS.getMsgType().intValue() > 0) {//Binary section

                short dstPort = 0x0000;
                short srcPort = 0x0000;
                switch (SMSObject.SMS.getMsgType().intValue()) {
                    case 1://RingTone

                        dstPort = 0x1581;
                        break;
                    case 2://Operator Logo

                        dstPort = 0x1582;
                        break;
                    case 3://CLI Icon

                        dstPort = 0x1583;
                        break;
                    case 4://Picture msg

                        dstPort = 0x158A;
                        break;
                    case 5://vCard msg

                        dstPort = 0x23F4;
                        break;
                    case 6://vCalendar msg

                        dstPort = 0x23F5;
                        break;
                    case 7: //WAP Push

                        srcPort = 0x23F0;
                        dstPort = 0x0B84;
                        break;
                    case 8: //binary message

                        srcPort = 0x23F0;
                        dstPort = 0x0B84;
                        break;
                }
                //SRC_PORT
//                debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SRC_PORT).toUpperCase() + " || Length || " + (short) 2 + " || Value || " + srcPort);
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SRC Port")
                        .put(GeneralConstants.StructuredLogKeys.TAG, Integer.toHexString(Data.OPT_PAR_SRC_PORT).toUpperCase())
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, (short) 2)
                        .put(GeneralConstants.StructuredLogKeys.VALUE, srcPort).build());
                BodyData.appendShort(Data.OPT_PAR_SRC_PORT);
                BodyData.appendShort((short) 2);
                BodyData.appendShort((short) srcPort);

                //DST_PORT
//                debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_DST_PORT).toUpperCase() + " || Length || " + (short) 2 + " || Value || " + dstPort);
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SRC Port")
                        .put(GeneralConstants.StructuredLogKeys.TAG, Integer.toHexString(Data.OPT_PAR_DST_PORT).toUpperCase())
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, (short) 2)
                        .put(GeneralConstants.StructuredLogKeys.VALUE, dstPort).build());
                BodyData.appendShort(Data.OPT_PAR_DST_PORT);
                BodyData.appendShort((short) 2);
                BodyData.appendShort((short) dstPort);
            }
            if (conc) {//concatenated part
                //sar_msg_ref_num

//                debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SAR_MSG_REF_NUM).toUpperCase() + " || Length || " + (short) 2 + " || Value || " + conc_sar_ref_num);
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "conc_sar_ref_num")
                        .put(GeneralConstants.StructuredLogKeys.TAG, Integer.toHexString(Data.OPT_PAR_SAR_MSG_REF_NUM).toUpperCase())
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, (short) 2)
                        .put(GeneralConstants.StructuredLogKeys.VALUE, conc_sar_ref_num).build());
                BodyData.appendShort(Data.OPT_PAR_SAR_MSG_REF_NUM);
                BodyData.appendShort((short) 2);
                BodyData.appendShort((short) conc_sar_ref_num);

                //sar_segment_seqnum
                //debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SAR_SEG_SNUM).toUpperCase() + " || Length || " + (short) Short.toString((short) conc_msg_sequeunce).getBytes().length + " || Value || " + ++conc_msg_sequeunce);
                //BodyData.appendShort((short) Short.toString((short) conc_msg_sequeunce).getBytes().length);
//                debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SAR_SEG_SNUM).toUpperCase() + " || Length || " + (short) 1 + " || Value || " + ++conc_msg_sequeunce);
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "conc_msg_sequence")
                        .put(GeneralConstants.StructuredLogKeys.TAG, Integer.toHexString(Data.OPT_PAR_SAR_SEG_SNUM).toUpperCase())
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, (short) 2)
                        .put(GeneralConstants.StructuredLogKeys.VALUE, ++conc_msg_sequeunce).build());
                BodyData.appendShort(Data.OPT_PAR_SAR_SEG_SNUM);
                BodyData.appendShort((short) 1);
                BodyData.appendByte((byte) conc_msg_sequeunce);

                //sar_total_segments
                //debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SAR_TOT_SEG).toUpperCase() + " || Length || " + (short) Short.toString((short) conc_msg_count).getBytes().length + " || Value || " + conc_msg_count);
                //BodyData.appendShort((short) Short.toString((short) conc_msg_count).getBytes().length);
//                debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SAR_TOT_SEG).toUpperCase() + " || Length || " + (short) 1 + " || Value || " + conc_msg_count);
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "conc_msg_count")
                        .put(GeneralConstants.StructuredLogKeys.TAG, Integer.toHexString(Data.OPT_PAR_SAR_TOT_SEG).toUpperCase())
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, (short) 2)
                        .put(GeneralConstants.StructuredLogKeys.VALUE, conc_msg_count).build());
                BodyData.appendShort(Data.OPT_PAR_SAR_TOT_SEG);
                BodyData.appendShort((short) 1);
                BodyData.appendByte((byte) conc_msg_count);

//                if (SMSObject.SMS.getConcMsgCount().intValue() == 0 &&
//                        SMSObject.SMS.getConcMsgSequeunce().intValue() == 0 &&
//                        SMSObject.SMS.getConcSarRefNum().intValue() == 0) {
                SMSObject.SMS.setConcMsgCount(new BigDecimal(conc_msg_count));
                SMSObject.SMS.setConcMsgSequeunce(new BigDecimal(conc_msg_sequeunce));
                SMSObject.SMS.setConcSarRefNum(new BigDecimal(conc_sar_ref_num));
                //}

            } //the else section is entered when a conc msg fails and gets enqueued
            //then it will pass through this part
            else if (SMSObject.SMS.getConcMsgCount().intValue() != 0
                    && SMSObject.SMS.getConcMsgSequeunce().intValue() != 0
                    && SMSObject.SMS.getConcSarRefNum().intValue() != 0) {

//                debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SAR_MSG_REF_NUM).toUpperCase() + " || Length || " + (short) 2 + " || Value || " + SMSObject.SMS.getConcSarRefNum().shortValue());
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "conc_sar_ref_num")
                        .put(GeneralConstants.StructuredLogKeys.TAG, Integer.toHexString(Data.OPT_PAR_SAR_MSG_REF_NUM).toUpperCase())
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, (short) 2)
                        .put(GeneralConstants.StructuredLogKeys.VALUE, SMSObject.SMS.getConcSarRefNum().shortValue()).build());
                BodyData.appendShort(Data.OPT_PAR_SAR_MSG_REF_NUM);
                BodyData.appendShort((short) 2);
                BodyData.appendShort((short) SMSObject.SMS.getConcSarRefNum().shortValue());

                //sar_segment_seqnum
//                debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SAR_SEG_SNUM).toUpperCase() + " || Length || " + (short) Short.toString((short) SMSObject.SMS.getConcMsgSequeunce().shortValue()).getBytes().length + " || Value || " + SMSObject.SMS.getConcMsgSequeunce().byteValue());
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "conc_msg_sequence")
                        .put(GeneralConstants.StructuredLogKeys.TAG, Integer.toHexString(Data.OPT_PAR_SAR_SEG_SNUM).toUpperCase())
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, Short.toString((short) SMSObject.SMS.getConcMsgSequeunce().shortValue()).getBytes().length)
                        .put(GeneralConstants.StructuredLogKeys.VALUE, SMSObject.SMS.getConcMsgSequeunce().byteValue()).build());
                BodyData.appendShort(Data.OPT_PAR_SAR_SEG_SNUM);
                BodyData.appendShort((short) Short.toString((short) SMSObject.SMS.getConcMsgSequeunce().shortValue()).getBytes().length);
                BodyData.appendByte((byte) SMSObject.SMS.getConcMsgSequeunce().byteValue());

                //sar_total_segments
//                debuglogger.debug("Tag || 0x0" + Integer.toHexString(Data.OPT_PAR_SAR_TOT_SEG).toUpperCase() + " || Length || " + (short) Short.toString((short) SMSObject.SMS.getConcMsgCount().shortValue()).getBytes().length + " || Value || " + SMSObject.SMS.getConcMsgCount().byteValue());
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "conc_msg_count")
                        .put(GeneralConstants.StructuredLogKeys.TAG, Integer.toHexString(Data.OPT_PAR_SAR_TOT_SEG).toUpperCase())
                        .put(GeneralConstants.StructuredLogKeys.LENGTH, (short) Short.toString((short) SMSObject.SMS.getConcMsgCount().shortValue()).getBytes().length)
                        .put(GeneralConstants.StructuredLogKeys.VALUE, SMSObject.SMS.getConcMsgCount().byteValue()).build());
                BodyData.appendShort(Data.OPT_PAR_SAR_TOT_SEG);
                BodyData.appendShort((short) Short.toString((short) SMSObject.SMS.getConcMsgCount().shortValue()).getBytes().length);
                BodyData.appendByte((byte) SMSObject.SMS.getConcMsgCount().byteValue());
            }
            if (SMSObject.SMS.getTlvOptionalParams() != null && !SMSObject.SMS.getTlvOptionalParams().equals("null") && !SMSObject.SMS.getTlvOptionalParams().equals("")) {
                List<TLVOptionalModel> optionalModelsList = Utility.gsonTlvsJSONStringToList(SMSObject.SMS.getTlvOptionalParams());
//                optionalModelsList.addAll(Utility.gsonJSONStringToList(SMSObject.SMS.getTlvOptionalParams()));

                for (TLVOptionalModel tlvModel : optionalModelsList) {
                    BodyData.appendShort(tlvModel.getTag());
                    BodyData.appendShort((short) tlvModel.getLength());
                    if (tlvModel.getType() == TLVModelValueType.BYTE) {
                        BodyData.appendByte(Byte.parseByte(tlvModel.getValue()));
                    } else if (tlvModel.getType() == TLVModelValueType.CSTRING) {
                        BodyData.appendCString((String) tlvModel.getValue());
                    } else if (tlvModel.getType() == TLVModelValueType.INT) {
                        BodyData.appendInt(Integer.parseInt(tlvModel.getValue()));
                    } else if (tlvModel.getType() == TLVModelValueType.SHORT) {
                        BodyData.appendShort(Short.parseShort(tlvModel.getValue()));
                    } else if (tlvModel.getType() == TLVModelValueType.STRING) {
                        BodyData.appendString(tlvModel.getValue());
                    } else if (tlvModel.getType() == TLVModelValueType.NOVALUE) {
                        // do nothing
                    } else {
                        throw new Exception("unknown type in optional params: " + tlvModel.getType());
                    }
                }

            }
            // calculate the commandLength
            header.commandLength = BodyData.getBuffer().length + Data.PDU_HEADER_SIZE;
//            debuglogger.debug("CommandLength || " + header.commandLength);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Calculating Command Length")
                    .put(GeneralConstants.StructuredLogKeys.COMMAND_LENGTH, header.commandLength).build());
            data = new BufferByte();
            data.appendInt(header.commandLength);
            data.appendInt(header.commandID);
            data.appendInt(header.commandStatus);
            data.appendInt(header.sequenceNumber);
            data.appendBytes(BodyData.getBuffer(),
                    BodyData.getBuffer().length);
        } catch (Exception e) {
            // debuglogger.error("Exception in constructing submit PDU || " + e);
            errlogger.error("Exception in constructing submit PDU || " + e, e);
            return null;
        } // end catch

        return data;
    }

    private BufferByte ConstructEnqPDU() {

        BufferByte data = null;
        MessageHeader header = null;

        try {
            header = new MessageHeader();
            header.commandLength = Data.PDU_HEADER_SIZE;
            header.commandID = Data.ENQUIRE_LINK;
            header.commandStatus = 0x00;
            header.sequenceNumber = assignSequenceNumber();
            /*-----------------------------------------------------*/
            data = new BufferByte();

            data.appendInt(header.commandLength);
            data.appendInt(header.commandID);
            data.appendInt(header.commandStatus);
            data.appendInt(header.sequenceNumber);
            //  data.appendByte((byte) 0);

        } catch (Exception e) {
            // debuglogger.error("Exception in enquiring || " + e);
            errlogger.error("Exception in enquiring || " + e, e);
            return null;
        } // end catch

        return data;
    } // end function

    private boolean unbind() {
        BufferByte data;
        BufferByte UnbindPDU;
        int state;

        UnbindPDU = ConstructUnbindPDU();
        connection.send(UnbindPDU, "Unbind", -1);

        data = new BufferByte();
        state = connection.receive(data);
        if (state == -1) {
            return false; // reconnect

        } else if (state == 1) {
            if (ParsePDU(data)) {
                return true; // UnBind success

            } else {
                return false; // Unbind failure

            }
        }
        return false;
    } // end function

    private BufferByte ConstructUnbindPDU() {
        BufferByte data = null;
        MessageHeader header = null;
        try {

            header = new MessageHeader();
            header.commandLength = Data.PDU_HEADER_SIZE;
            header.commandID = Data.UNBIND;
            header.commandStatus = 0x00;
            header.sequenceNumber = assignSequenceNumber();
            /*-----------------------------------------------------*/
            data = new BufferByte();

            data.appendInt(header.commandLength);
            data.appendInt(header.commandID);
            data.appendInt(header.commandStatus);
            data.appendInt(header.sequenceNumber);
            // data.appendByte((byte) 0);

        } // end try
        catch (Exception e) {
            // debuglogger.error("Exception in unbind || " + e);
            errlogger.error("Exception in unbind || " + e, e);
        } // end catch

        return data;
    }

    private void startAsynchFunctions() {
        if (threadActive == false) {
            startHeartBeatThread(); // start heart beat thread
        }
//        debuglogger.info("HeartBeat Thread started");
        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "HeartBeat Thread Started").build());
        if (recv != null) { // case connection faliure stop receiving thread when trying to reconnect
            recv.stopThread();
            // wait until receiver thread is terminated
            if (recv.isAlive()) {
                try {
                    recv.join(); // blocking till end of thread

                } catch (InterruptedException e) {
                    // debuglogger.error("InterruptedException in starting async functions || receiver thread || " + e);
                    errlogger.error("InterruptedException in starting async functions || receiver thread || " + e, e);
                } catch (Exception e) {
                    // debuglogger.error("Exception in starting async functions || receiver thread || " + e);
                    errlogger.error("Exception in starting async functions || receiver thread || " + e, e);
                }
            }
            recv = null; // dispose receiver thread object
        }
        // start receiver thread
        recv = new ReceiverThread(this, connection); // initiate receiver
        recv.setName("Receiver_" + smppApp.getAppName() + "_" + threadNumber);
        recv.start(); // start the thread
        ///// start deliver_sm_resp responsible thread.
        if (delvresp != null) {
            delvresp.stopThread();
            // wait until receiver thread is terminated
            if (delvresp.isAlive()) {
                try {
                    delvresp.join(); // blocking till end of thread

                } catch (InterruptedException e) {
                    // debuglogger.error("InterruptedException in starting async functions || delivery response thread || " + e);
                    errlogger.error("InterruptedException in starting async functions || delivery response thread || " + e, e);
                } catch (Exception e) {
                    // debuglogger.error("Exception in starting async functions || delivery response thread || " + e);
                    errlogger.error("Exception in starting async functions || delivery response thread || " + e, e);
                }
            }
            delvresp = null; // dispose receiver thread object
        }
        delvresp = new DeliverResponseThread(this); // initiate receiver
        delvresp.setName("deliverResponse_" + smppApp.getAppName() + "_" + threadNumber);
        delvresp.start(); // start the thread
        //
        if (SendRcvHash == null) {
            SendRcvHash = new Hashtable(sendRecvHashWindowSize); // intialize hashtable
        } else // if already initialized then its reconnection, thus empty hashtable
        {
            EmptySendRecvHashToLog();// empty hashtable in reconnections

        }

        if (Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()) == null) {
            //Manager.RcvDelHash = ; // intialize hashtable
            Manager.receiver_deleteHashTable_QMap.put(this.smppApp.getAppName(), new Hashtable(recvDelHashWindowSize));
        } else // if already initialized then its reconnection, thus empty hashtable
        {
            EmptyRecvDelHashToLog();// empty hashtable in reconnections

        }
        if (GarbageThreadActive == false) {
            startGarbageCollectorThread();
        }
//        debuglogger.info("GarbageCollector Thread started");
        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "GarbageCollector Thread Started").build());
    }

    // Stops the timer
    private void stopHeartBeatThread() {
        if (threadActive == true) {
            if (HeartBeatTask != null) {
//                debuglogger.debug("Stopping Thread HeartBeat Task");
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Stoping Thread HearBeat Task").build());
                HeartBeatTask.cancel();
                HeartBeatTask = null;
            } // end if

            threadActive = false;
        } // end if

    } // end function

    private void startHeartBeatThread() {
//        debuglogger.info("Starting Heartbeat Task and Timer");
        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting HeartBeat Task and Timer").build());
        try {
            if (threadActive == false) {
                int Keepalive = (new Integer(KeepAlive)).intValue();

                if (HeartBeatTimer == null) {
                    //    HeartBeatTimer = new Timer("Enquire_Link");
                    HeartBeatTimer = new Timer("Enquire_Link_" + smppApp.getAppName() + "_" + threadNumber);
                }
                if (HeartBeatTask == null) {
                    HeartBeatTask = new HeartBeat(this); // do this
                    // initial delay

                }

                //Eman El-Sayed
                if (Keepalive > 0) {

                    HeartBeatTimer.schedule(HeartBeatTask, (Keepalive * 60 * 1000) - 500,
                            (Keepalive * 60
                            * 1000) - 500); // subsequent rate*/
                } else {
                    HeartBeatTimer.schedule(HeartBeatTask, Keepalive * 60 * 1000,
                            Keepalive * 60
                            * 1000); // subsequent rate*/
                }

                /* HeartBeatTimer.schedule(HeartBeatTask, Keepalive * 60 * 1000,
                 Keepalive * 60 *
                 1000); // subsequent rate*/
                threadActive = true;
            } // end if
        } catch (Exception ex) {
            // debuglogger.error("Exception in startHeartBeatThread ----->" + ex);
            errlogger.error("Exception in startHeartBeatThread ----->" + ex, ex);
            if (ex.getMessage().equalsIgnoreCase("Timer already cancelled.")) {
//                debuglogger.info("Timer is cancelled,It will start again");
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Timer is Cancelled, it will Start Again").build());
                HeartBeatTimer = new Timer("Enquire_Link_" + smppApp.getAppName() + "_" + threadNumber);
//                debuglogger.info("Timer started again");
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Timer Started Again").build());
            } else {
//                debuglogger.info("Throwing exception as it is not timer already cancelled exception.");
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Throwing Exception as it is not Timer Already Cancelled Exception").build());
                throw ex;
            }

        }
    } // end function

    private void stopGarbageCollectorThread() {
        if (GarbageThreadActive == true) {
            if (GarbageCollectorTask != null) {
//                debuglogger.debug("Stopping Thread GarbageCollector Task");
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Stopping Thread GarbageCollector Task").build());
//                debuglogger.info("*****************GARBAGE COLLECTOR THREAD FINISHED ***********************");
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Garbage Collector Thread Finished").build());
                GarbageCollectorTask.cancel();
                GarbageCollectorTask = null;
            }
            GarbageThreadActive = false;
        } // end if

    } // end function

    private void startGarbageCollectorThread() {
//        debuglogger.debug("Starting Garbage Collector Task and Timer");
        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting Garbage Collector Task and Timer").build());

        if (GarbageThreadActive == false) {
            if (GarbageCollectorTimer == null) {
                // GarbageCollectorTimer = new Timer("GarbageCollector");
                GarbageCollectorTimer = new Timer("GarbageCollector_" + smppApp.getAppName() + "_" + threadNumber);

            }
            if (GarbageCollectorTask == null) {
                GarbageCollectorTask = new GarbageCollector(SendRcvHash, Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName())); // do this initial delay
            }
            GarbageCollectorTimer.schedule(GarbageCollectorTask,
                    garbageCollectorTime * 60 * 1000,
                    garbageCollectorTime * 60
                    * 1000); // subsequent rate*/
//            debuglogger.info("*****************GARBAGE COLLECTOR THREAD STARTED ***********************");
            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Garbage Collector Thread Started").build());
            GarbageThreadActive = true;
        } // end if

    } // end function

    private boolean Bind_Transceiver(String SystemID, String Password, String SystemType, byte AddrTON, byte AddrNPI) {

        BufferByte BindTranmitPDU, BindTranmitRespPDU;
        int state;
        BindTranmitPDU = ConstructBindTransmitPDU(SystemID, Password, SystemType, AddrTON, AddrNPI);
        connection.setCommsTimeout(500);
        connection.setReceiveTimeout(5000);

        if (connection.send(BindTranmitPDU, "Bind", -1) == 1) {
            BindTranmitRespPDU = new BufferByte();
            state = connection.receive(BindTranmitRespPDU);

            if (state == -1) {
                return false; // reconnect

            } else if (state == 1) {
                if (ParsePDU(BindTranmitRespPDU)) {
                    return true;
                } else {
                    return false; // retry sending

                }
            } // end else
            else {
                return false; // retry sending

            }
        } // end if connected check
        else {
            return false; // reconnect

        }
    } // end function

    private BufferByte ConstructBindTransmitPDU(String SystemID,
            String Password,
            String SystemType,
            byte AddrTON, byte AddrNPI) {
        String system_id;
        String password;
        String system_type;
        byte interface_version;
        byte addr_ton;
        byte addr_npi;
        String address_range;
        BufferByte data = null;
        MessageHeader header = null;
        /*--------------------------------------------------------*/

        try {

            header = new MessageHeader();
            header.commandID = Data.BIND_TRANSCEIVER; //Data.BIND_TRANSMITTER; // Data.BIND_TRANSCEIVER;

            header.commandStatus = 0x00;
            header.sequenceNumber = assignSequenceNumber();
            system_id = SystemID;
            password = Password;
            system_type = SystemType;
            interface_version = Data.SMPP_V34;
            addr_ton = AddrTON;
            addr_npi = AddrNPI;
            address_range = null;
            /*----------------------------------------------------*/
            BufferByte BodyData = new BufferByte();

            BodyData.appendCString(system_id);
            BodyData.appendCString(password);
            BodyData.appendCString(system_type);
            BodyData.appendByte(interface_version);
            BodyData.appendByte(addr_ton);
            BodyData.appendByte(addr_npi);
            BodyData.appendString(address_range);
            BodyData.appendByte((byte) 0);
            header.commandLength = BodyData.getBuffer().length + Data.PDU_HEADER_SIZE;
            data = new BufferByte();
            data.appendInt(header.commandLength);
            data.appendInt(header.commandID);
            data.appendInt(header.commandStatus);
            data.appendInt(header.sequenceNumber);
            data.appendBytes(BodyData.getBuffer(),
                    BodyData.getBuffer().length);
        } // end try
        catch (Exception e) {
            // System.out.println("Exception in bind " + e.toString());
            // debuglogger.error("Exception in bind || " + e);
            errlogger.error("Exception in bind || " + e, e);
            return null;
        } // end catch

        return data;
    }

    public int assignSequenceNumber() {
        if (sequenceNumber < 0x7FFFFFFF) { // sequence_number range from
            // 0x00000001 - 0x7FFFFFFF
            sequenceNumber++;
//            debuglogger.debug("SequenceNumber || " + sequenceNumber);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "sequence number range from 0x00000001 - 0x7FFFFFFF")
                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, sequenceNumber).build());
            return (sequenceNumber);
        } else {
            sequenceNumber = 0;
//            debuglogger.debug("SequenceNumber || " + sequenceNumber);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Getting Sequence Number")
                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, sequenceNumber).build());
            return (sequenceNumber);
        }

    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public byte get_sar_total_sequenceNumber() {
        return conc_msg_sequeunce;
    }

    private String getDateTime() {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return (df.format(now));
    }
    // close the connection with the SMSC

    private SMPPConnection getNextConnection() {
        if (isSuccessConnection == false) {
            if (lastConnection < IPCount - 1) {
                lastConnection++;
            } else {
                lastConnection = 0;
            }
        }
        return (IPArray[lastConnection]);
    }
    // Connect to SMSC

    private boolean Connect(String IP, String Port) throws Exception {
        if (connection != null) {
            connection = null;
        }
        connection = new TCPIPConnection(IP, Port); // initialize
        // connection object
//        debuglogger.debug("Instantiating Connection object");
        debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Instantiating Connection Object").build());
        if (connection.isOpen() == false) {
            // System.out.println("Attempting to Connect to SMSC");
            connection.open(); // open connection (socket)
        }
        return (getConnectionStatus());
    }

    private void EmptySendRecvHashToLog() {
        try {
            Enumeration num = SendRcvHash.keys();
            while (num.hasMoreElements()) {
                int key = (Integer) num.nextElement();
                HashObject value = (HashObject) SendRcvHash.get(key);
//                debuglogger.info(value.SMS.getAppId() + " || " + key + " || " + value.log);
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SendRcvHash Info")
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, value.SMS.getAppId())
                        .put(GeneralConstants.StructuredLogKeys.KEY, value.log).build());
            } // end while
            SendRcvHash.clear(); // clear hashtable
        } catch (Exception e) {
            // debuglogger.error("Exception in emptying SendRecvHash || " + e);
            errlogger.error("Exception in emptying SendRecvHash || " + e, e);
        }
    }

    private void EmptyRecvDelHashToLog() {
        try {
            Enumeration num = Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).keys();
            while (num.hasMoreElements()) {
                long key = (Integer) num.nextElement();
                HashObject value = (HashObject) Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).get(key);
//                debuglogger.info(value.SMS.getAppId() + " || " + key + " || " + value.log);
                debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RecvDelHash Info")
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, value.SMS.getAppId())
                        .put(GeneralConstants.StructuredLogKeys.KEY, value.log).build());
            } // end while
            Manager.receiver_deleteHashTable_QMap.get(this.smppApp.getAppName()).clear(); // clear hashtable
        } catch (Exception e) {
            // debuglogger.error("Exception in emptying RecvDelHash || " + e);
            errlogger.error("Exception in emptying RecvDelHash || " + e, e);
        }
    }

    // Kerollos Asaad 5/6/2016
    /**
     * Delivering deliver_sm_resp which occurs after acknowledging deliver_sm
     * receipt.
     *
     * @param deliversmseqnum the delivered sms queue number.
     * @param messageID message ID
     * @return a boolean of binding success, true for unbind success and false
     * otherwise.
     */
    public boolean deliver_sm_resp(int deliversmseqnum, String messageID, long seq_id) {
        //BufferByte data;
        BufferByte PDU;
        PDU = ConstructDeliverPDU(deliversmseqnum, messageID);
        int state = connection.send(PDU, smppApp.getAppName(), -1);
//        infologger.info("deliver_sm_resp || " + seq_id + " || " + smppApp.getAppName() + " || " + messageID + " || " + state);
        infologger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "R")
                .put(GeneralConstants.StructuredLogKeys.REQUEST_TYPE, "deliver_sm_resp")
                .put(GeneralConstants.StructuredLogKeys.SEQ_ID, seq_id)
                .put(GeneralConstants.StructuredLogKeys.APP_NAME, smppApp.getAppName())
                .put(GeneralConstants.StructuredLogKeys.MSG_ID, messageID)
                .put(GeneralConstants.StructuredLogKeys.STATE, state).build());
//        debuglogger.info("deliver_sm_resp || " + smppApp.getAppName() + " || " + messageID + " || " + state);
//        debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deliver_sm_resp")
//                .put(GeneralConstants.StructuredLogKeys.APP_NAME, smppApp.getAppName())
//                .put(GeneralConstants.StructuredLogKeys.MSG_ID, messageID)
//                .put(GeneralConstants.StructuredLogKeys.STATE, state).build());
        return (state == 1);
    } // end function 

    // Kerollos Asaad 5/6/2016
    /**
     * Constructing deliver_sm_resp PDU.
     *
     * @param deliversmseqnum he delivered sms queue number.
     * @param messageid message ID
     * @return a BufferByte of PDU of deliver_sm_resp.
     */
    private BufferByte ConstructDeliverPDU(int deliversmseqnum, String messageid) {
        BufferByte data = null;
        MessageHeader header = null;
        try {
            // constructing PDU header 
            header = new MessageHeader();
            header.commandLength = Data.PDU_HEADER_SIZE;
            header.commandID = Data.DELIVER_SM_RESP;
            header.commandStatus = 0x00;
            header.sequenceNumber = deliversmseqnum;
            //header.retVal=messageid;
            //BodyData.appendCString(messageid);
            /*-----------------------------------------------------*/
            // constructing PDU body
            BufferByte BodyData = new BufferByte();
            BodyData.appendCString(messageid);
            header.commandLength = BodyData.getBuffer().length + Data.PDU_HEADER_SIZE;
            data = new BufferByte();
            data.appendInt(header.commandLength);
            data.appendInt(header.commandID);
            data.appendInt(header.commandStatus);
            data.appendInt(header.sequenceNumber);
            //data.appendCString(header.retVal);
            data.appendBytes(BodyData.getBuffer(), BodyData.getBuffer().length);
            //data.appendByte((byte) 0);
        } // end try
        catch (Exception e) {
            // debuglogger.error("Exception in ConstructDeliverPDU || " + e);
            errlogger.error("Exception in ConstructDeliverPDU || " + e, e);
        } // end catch
        return data;
    }
}
