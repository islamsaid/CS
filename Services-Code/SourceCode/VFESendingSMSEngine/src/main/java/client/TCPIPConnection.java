package client;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.sendingsms.controller.Controller;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import javax.net.SocketFactory;
import org.apache.logging.log4j.Logger;

/**
 * *****************************************************************************************
 */
/**
 * ******************************************************************************************
 * This class contains all the TCP/IP functionalities 1)Socket creation
 * 2)connecting to (IP,Port) 3)Send 4)Receive case of disconnection a return
 * error code is propagated to the upper levels to handle the reconnection (Main
 * invokes smpp Class for reconnection)
 * ****************************************************************
 */
/**
 * ***************************************************************************************
 */
public class TCPIPConnection {

    /*-------------Global Variables---------------*/
    private Socket socket = null;
    private String IP;
    private String Port;
    private Logger infologger = null;
    private Logger debuglogger = null;
    private Logger errlogger = null;
    private boolean opened;

    /*------------------------------------------------------------*/
    private int receiveBufferSize;
    private byte[] receiveBuffer;    // private static final int DFLT_MAX_RECEIVE_SIZE = 128 * 1024;
    private static final int DFLT_RECEIVE_BUFFER_SIZE = 4 * 1024;    // private int maxReceiveSize = DFLT_MAX_RECEIVE_SIZE;
    /*------------------------------------------------------------*/
    private long commsTimeout = Data.COMMS_TIMEOUT;
    private long receiveTimeout = Data.CONNECTION_RECEIVE_TIMEOUT;
    private static final int DFLT_IO_BUF_SIZE = 2 * 1024;
    private int ioBufferSize = DFLT_IO_BUF_SIZE;
    private BufferedInputStream inputStream = null;
    private BufferedOutputStream outputStream = null;
    private SocketFactory socketFactory = SocketFactory.getDefault();

    // overloaded constructor
    public TCPIPConnection(String IP, String Port) {

        this.IP = IP;
        this.Port = Port;
        this.infologger = Controller.infoLogger;
        this.debuglogger = Controller.debugLogger;
        this.errlogger = Controller.errorLogger;
        opened = false;
        debuglogger.info("SMSCIP || " + IP);
        debuglogger.info("Port || " + Port);
        setReceiveBufferSize(DFLT_RECEIVE_BUFFER_SIZE);
    }

    private void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
        receiveBuffer = new byte[receiveBufferSize];
    }

    // function that opens connection with smsc and provides retrial mechanism
    public void open() {// throws IOException, InterruptedException {

        if (open(IP, Port)) {
            opened = true;
        }
    }

    private boolean open(String HostIP, String HostPort) {
        try {

            if (socket != null) {
                socket = null;
            }
            if (!opened) {
                int Port = (new Integer(HostPort)).intValue();
                socket = socketFactory.createSocket(HostIP, Port);
                debuglogger.info("Socket Connect to IP || " + HostIP + " || Port || " + Port);
                initialiseIOStreams(socket);
                debuglogger.info("Socket Opened having status IsConnected || " + socket.isConnected());
                return true;
            }// end if

            return false;
        }// end try
        catch (IOException e) {
            // debuglogger.error("IOException opening TCPIPConnection || IP || " + HostIP + " || Port || " + Port + " || " + e);
            errlogger.error("IOException opening TCPIPConnection || IP || " + HostIP + " || Port || " + Port + " || " + e, e);
            return false;
        } catch (Exception e) {
            // debuglogger.error("Exception opening TCPIPConnection || IP || " + HostIP + " || Port || " + Port + " || " + e);
            errlogger.error("Exception opening TCPIPConnection || IP || " + HostIP + " || Port || " + Port + " || " + e, e);
            return false;
        }
    }

    public void close() {
        try {

            debuglogger.info("Closing socket Connection | IP: " + IP + " | Port: " + Port);

            if (opened) {
                if (inputStream != null) {
                    inputStream.close();
                }
                debuglogger.info("Closing InputStream | Socket IP: " + IP + " | Port: " + Port);
                if (outputStream != null) {
                    outputStream.close();
                }
                debuglogger.info("Closing OutputStream | Socket IP: " + IP + " | Port: " + Port);
                if (socket != null) {
                    socket.close();
                }
                debuglogger.info("Closing socket | IP: " + IP + " | Port: " + Port + " | isClosed: " + socket.isClosed());
                inputStream = null;
                outputStream = null;
                socket = null;
                opened = false;
            } else {
                debuglogger.info("Socket IP: " + IP + " | Port: " + Port + " | Connection not opened...already closed");
            }
        } catch (IOException e) {
            // debuglogger.error("IOException closing socket || " + e);
            errlogger.error("IOException closing socket || " + e, e);
        } catch (Exception e) {
            // debuglogger.error("Exception closing socket || " + e);
            errlogger.error("Exception closing socket || " + e, e);
        }

    }

    private void initialiseIOStreams(Socket socket) throws IOException {
        inputStream = new BufferedInputStream(socket.getInputStream(), ioBufferSize);
        outputStream = new BufferedOutputStream(socket.getOutputStream(), ioBufferSize);
        debuglogger.info("Initializing Socket InputStream and OutputStreams");
    }

    public boolean isOpen() {
        return opened;
    }

    public synchronized void setCommsTimeout(long commsTimeout) {
        this.commsTimeout = commsTimeout;
    }

    public synchronized void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public synchronized int send(BufferByte data, String AppID, int sequenceNumber) {
        try {
//            debuglogger.debug(Thread.currentThread().getName() + " | Start Sending | Socket of IP: " + IP + " Port: " + Port 
//                    + " | AppID: " + AppID + " | sequenceNumber: " + sequenceNumber);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName() + " Start Sending")
                    .put(GeneralConstants.StructuredLogKeys.SOCKET_IP, IP)
                    .put(GeneralConstants.StructuredLogKeys.PORT, Port)
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, AppID)
                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, sequenceNumber).build());
            long time = System.currentTimeMillis();
            if (data == null) {
                //debuglogger.debug("Object passed is null");
//                debuglogger.debug(Thread.currentThread().getName() + "Socket | IP: " + IP + " |Port: " + Port
//                        + " | AppID: " + AppID + " | sequenceNumber: " + sequenceNumber + " | Object passed to  is null");
                debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Object Passed to is Null")
                        .put(GeneralConstants.StructuredLogKeys.SOCKET_IP, IP)
                        .put(GeneralConstants.StructuredLogKeys.PORT, Port)
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, AppID)
                        .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, sequenceNumber).build());
                return -2; //nullpointer exception
            }
            debuglogger.debug("ThreadName || " + Thread.currentThread().getName());
            socket.setTcpNoDelay(true);

            socket.setSoTimeout((int) commsTimeout);// setting socket timeout

//            debuglogger.info(Thread.currentThread().getName() + "Socket | IP: " + IP + " |Port: " + Port
//                    + " | AppID: " + AppID + " | sequenceNumber: " + sequenceNumber + " | Writing on smsc socket...");
            outputStream.write(data.getBuffer(), 0, data.length());

//            debuglogger.info(Thread.currentThread().getName() + "Socket | IP: " + IP + " |Port: " + Port
//                    + " | AppID: " + AppID + " | sequenceNumber: " + sequenceNumber
//                    + " | Write on smsc socket time: " + (System.currentTimeMillis() - time));
            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Write on SMSC Socket")
                    .put(GeneralConstants.StructuredLogKeys.SOCKET_IP, IP)
                    .put(GeneralConstants.StructuredLogKeys.PORT, Port)
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, AppID)
                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, sequenceNumber)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - time)).build());

            // sequenceNumber parameter being != -1 in case only of submit_sm, logs added there.
            // sequenceNumber parameter being == -1 in case of deliver_sm_resp.
            /*if (sequenceNumber != -1) {
             infologger.info("S || " + AppID + " || " + sequenceNumber);
             debuglogger.debug("S || " + AppID + " || " + sequenceNumber);
             }*/
//            debuglogger.info(Thread.currentThread().getName() + "Socket | IP: " + IP + " |Port: " + Port
//                    + " | AppID: " + AppID + " | sequenceNumber: " + sequenceNumber + " | Write on smsc socket outputStream flushing...");
            debuglogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Write on smsc socket outputStream flushing")
                    .put(GeneralConstants.StructuredLogKeys.SOCKET_IP, IP)
                    .put(GeneralConstants.StructuredLogKeys.PORT, Port)
                    .put(GeneralConstants.StructuredLogKeys.APP_ID, AppID)
                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, sequenceNumber).build());
            outputStream.flush();
//            debuglogger.info(Thread.currentThread().getName() + "Socket | IP: " + IP + " |Port: " + Port
//                    + " | AppID: " + AppID + " | sequenceNumber: " + sequenceNumber + " | flushed Successfully...");
//            debuglogger.info(Thread.currentThread().getName() +  "Socket | IP: "+IP + " |Port: "+ Port 
//                        +" | AppID: " + AppID + " | sequenceNumber: " + sequenceNumber +" | Write on smsc socket outputStream flushed...");
            return 1;
        } catch (IOException e) {
            // debuglogger.error("IOException flushing data || " + e);
            errlogger.error("IOException flushing data || " + e, e);
            opened = false;
            return -1;// socket connection failure
        } catch (NullPointerException e) {
            // debuglogger.error("NullPointerException || " + e);
            errlogger.error("NullPointerException || " + e, e);
            return -2;
        } catch (Exception e) {
            // debuglogger.error("NullPointerException || " + e);
            errlogger.error("NullPointerException || " + e, e);
            return -3; // failure
        }
    }

    public int receive(BufferByte data) {// passed parameter is by reference
        int ByteToRead = 0;
        int bytesRead = 0;
        byte bufferarray[] = new byte[16];

        try {
            socket.setSoTimeout((int) receiveTimeout);
//            debuglogger.debug("Socket | IP: " + IP + " |Port: " + Port + " | RCV WAIT");
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "RCV Wait")
                    .put(GeneralConstants.StructuredLogKeys.SOCKET_IP, IP)
                    .put(GeneralConstants.StructuredLogKeys.PORT, Port).build());
            bytesRead = inputStream.read(bufferarray, 0, 16);
//            debuglogger.debug("Socket | IP: " + IP + " |Port: " + Port + " | Receiving start");
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Receiving Start")
                    .put(GeneralConstants.StructuredLogKeys.SOCKET_IP, IP)
                    .put(GeneralConstants.StructuredLogKeys.PORT, Port).build());
            if (bytesRead > 0) {
                data.appendBytes(bufferarray, bytesRead);
                for (int count = 0; count < 4; count++) {
                    ByteToRead <<= 8;
                    ByteToRead |= bufferarray[count] & 0xff;//
                }
                if (ByteToRead > 0) {
                    byte tmpBuff[] = new byte[ByteToRead - 16];
                    bytesRead = inputStream.read(tmpBuff);
                    data.appendBytes(tmpBuff, bytesRead);
                }
            } else {
                return (-1);
            }
        } catch (SocketTimeoutException e) {
//            debuglogger.debug("No data received", e);
            debuglogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "No Data Received").build(), e);
            return (2);
        } catch (IOException e) {
            // debuglogger.error("IOException || " + e);
            errlogger.error("IOException || " + e, e);
            opened = false;
            return (-1);// connection failure

        } catch (NullPointerException e) {
            // debuglogger.error("NullPointerException || " + e);
            errlogger.error("NullPointerException || " + e, e);
            return (-2);
        } catch (Exception e) {
            // debuglogger.error("Exception || " + e);
            errlogger.error("Exception || " + e, e);
            return (-3);// failure
        }
        return (1); // success
    }
}// end class

