package client;

import com.asset.cs.sendingsms.controller.Controller;
import java.io.UnsupportedEncodingException;
import org.apache.logging.log4j.Logger;

/**
 * *****************************************************************************************
 */
/**
 * ******************************************************************************************
 * This class handles: - actual data sent on the wire. - it appends data, as
 * mentioned in the SMPP protocol, into a byte array that is eventually sent to
 * the SMPP Server. - as well as remove data from the received responses.
 *
 *****************************************************************
 */
/**
 * ***************************************************************************************
 */
public class BufferByte {

    private Logger debuglogger = null;
    private Logger errlogger = null;
    private byte[] buffer;
    private static final byte SZ_BYTE = 1;
    private static final byte SZ_SHORT = 2;
    private static final byte SZ_INT = 4;    // private static final byte SZ_LONG = 8;
    private static byte[] zero;
    // Null terminating for Cstring Type

    static {
        zero = new byte[1];
        zero[0] = 0;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public BufferByte() {
        buffer = null;
        this.errlogger = Controller.errorLogger;
        this.debuglogger = Controller.debugLogger;
    }

    public BufferByte(byte[] buffer) {
        this.buffer = buffer;
    }

    // put the Integer Type MSB first and LSB last in the buffer byte array (4
    // octets)
    public void appendInt(int data) {
        byte[] intBuf = new byte[SZ_INT];
        intBuf[3] = (byte) (data & 0xff);// LSB
        intBuf[2] = (byte) ((data >>> 8) & 0xff);
        intBuf[1] = (byte) ((data >>> 16) & 0xff);
        intBuf[0] = (byte) ((data >>> 24) & 0xff);// MSB
        appendBytes0(intBuf, SZ_INT);
    }

    // put the short Type MSB first and LSB last, (2 octets)
    public void appendShort(short data) {
        byte[] shortBuf = new byte[SZ_SHORT];
        shortBuf[1] = (byte) (data & 0xff);
        shortBuf[0] = (byte) ((data >>> 8) & 0xff);
        appendBytes0(shortBuf, SZ_SHORT);
    }

    // Byte Type (1 octet)
    public void appendByte(byte data) {
        byte[] byteBuf = new byte[SZ_BYTE];
        byteBuf[0] = data;
        appendBytes0(byteBuf, SZ_BYTE);
    }

    // check function to concatenate the different types
    // (integer,Cstring,string..) in one buffer byte array to be sent
    public void appendBytes(byte[] bytes, int count) {
        if (bytes != null) {
            if (count > bytes.length) {
                count = bytes.length;
            }
            appendBytes0(bytes, count);
        }
    }

    // function that actually copies the different type values in one array to
    // prepare it for sending
    private void appendBytes0(byte[] bytes, int count) {
        try {
            int len = length();
            byte[] newBuf = new byte[len + count];// create a temp byte array
            // with the new size
            if (len > 0) {// buffer array contains data,if(len=0) empty buffer
                System.arraycopy(buffer, 0, newBuf, 0, len);// fill the
                // temporary array
                // with buffer data
            }
            System.arraycopy(bytes, 0, newBuf, len, count);// then refill the
            // temp array with
            // the new byte data
            setBuffer(newBuf);// then refresh the main buffer
        } catch (Exception e) {
            debuglogger.error("Exception || " + e);
            errlogger.error("Exception || " + e, e);
        }
    }

    // put the Cstring type in tmp byte buffer with default encoding GSM7Bit
    public void appendCString(String string) {
        try {
            appendString0(string, true, null);// Data.ENC_GSM7BIT
        } catch (Exception e) {
            debuglogger.error("Exception || " + e);
            errlogger.error("Exception ||  " + e, e);
        }
    }

    // put the Cstring type in tmp byte buffer and determine the suitable
    // encoding
    public void appendCString(String string, String encoding) {
        try {
            appendString0(string, true, encoding);
        } catch (Exception e) {
            debuglogger.error("Exception || " + e);
            errlogger.error("Exception ||  " + e, e);
        }
    }

    // put the string type in tmp byte buffer
    public void appendString(String string) {
        try {
            appendString(string, null);// Data.ENC_GSM7BIT
        } catch (Exception e) {
            debuglogger.error("Exception || " + e);
            errlogger.error("Exception ||  " + e, e);
        }
    }

    public void appendString(String string, String encoding) {
        appendString0(string, false, encoding);
    }

    // function to put both the Cstring and String Types in a temporary array
    // (false string,True Cstring)
    private void appendString0(String string, boolean isCString, String encoding) {
        if ((string != null) && (string.length() > 0)) {

            byte[] stringBuf = null;
            byte[] tmpBuf = null;
            if (encoding != null) {
                try {
                    if (encoding == Data.ENC_UTF16) {// UTF16 adds in the
                        // byte array to bytes,
                        // in this part i remove
                        // them

                        tmpBuf = string.getBytes(encoding);
                        stringBuf = new byte[tmpBuf.length - 2];
                        System.arraycopy(tmpBuf, 2, stringBuf, 0,
                                stringBuf.length);
                    } else {
                        stringBuf = string.getBytes(encoding);
                    }
                }// end try
                catch (UnsupportedEncodingException e) {
//                    System.out.println("UnsupportedEncodingException ||  " + e.getMessage());
                    debuglogger.error("UnsupportedEncodingException || " + e);
                    errlogger.error("UnsupportedEncodingException ||  " + e, e);
                } catch (Exception e) {
                    debuglogger.error("Exception || " + e);
                    errlogger.error("Exception ||  " + e, e);
                }
            } else {
                stringBuf = string.getBytes(); // encoding = null
            }
            if ((stringBuf != null) && (stringBuf.length > 0)) {
                appendBytes0(stringBuf, stringBuf.length);
            }
        }

        if (isCString) {
            appendBytes0(zero, 1); // always append terminating zero
        }
    }

    public int length() {
        if (buffer == null) {
            return 0;
        } else {
            return buffer.length;
        }
    }

    /*------------------------------------------------------------------------------------*/    // Read the incoming data and remove the Cstring part from it
    public String removeCString() {
        int len = length();
        int zeroPos = 0;

        while ((zeroPos < len) && (buffer[zeroPos] != 0)) {// detect the Null
            // terminated zero
            // in buffer
            zeroPos++;
        }
        if (zeroPos < len) { // found terminating zero
            String result = null;
            if (zeroPos > 0) {
                try {
                    result = new String(buffer, 0, zeroPos, Data.ENC_ASCII);
                } catch (UnsupportedEncodingException e) {
                    debuglogger.error("UnsupportedEncodingException || " + e);
                    errlogger.error("UnsupportedEncodingException ||  " + e, e);
                } catch (Exception e) {
                    debuglogger.error("Exception || " + e);
                    errlogger.error("Exception ||  " + e, e);
                }
            } else {
                result = new String("");
            }

            removeBytes0(zeroPos + 1);
            return result;

        }
        return null;

    }

    // Read the incoming data and remove the string part from it
    public String removeString(int size, String encoding)
            throws UnsupportedEncodingException {
        int len = length();
        UnsupportedEncodingException encodingException = null;
        String result = null;
        if (len > 0) {
            try {
                if (encoding != null) {
                    result = new String(buffer, 0, size, encoding);
                } else {
                    result = new String(buffer, 0, size);
                }
            } catch (UnsupportedEncodingException e) {
                debuglogger.error("UnsupportedEncodingException || " + e);
                errlogger.error("UnsupportedEncodingException ||  " + e, e);
                //System.out.println("UnsupportedEncodingException ||  " + e);
                encodingException = e;
            } catch (Exception e) {
                debuglogger.error("Exception || " + e);
                errlogger.error("Exception ||  " + e, e);
            }
            removeBytes0(size);
        } else {
            result = new String("");
        }
        if (encodingException != null) {
            throw encodingException;
        }
        return result;

    }

    // just removes bytes from the buffer and doesnt return anything
    private void removeBytes0(int count) {
        // paolo@bulksms.com: seeing as how these libs return 32-bit unsigned
        // ints
        // (by using negative values), they should also cater for them in input.
        // However, it's unlikely to be happen often (mostly occurs with garbage
        // input), so I'm not fixing it, but just adding this quickly to stop
        // ArrayIndexOutOfBoundsExceptions with large values:
        if (count < 0) {
            count = Integer.MAX_VALUE;
            // logger.DEBUG("No. of bytes to remove= "+count);
        }
        int len = length();
        int lefts = len - count;

        if (lefts > 0) {
            byte[] newBuf = new byte[lefts];
            System.arraycopy(buffer, count, newBuf, 0, lefts);// put the
            // unremoved
            // values in the
            // newBuf array
            setBuffer(newBuf);// and then update the main buffer with the
            // values in newBuf
        } else {
            setBuffer(null);
        }
    }

    private BufferByte readBytes(int count) {
        int len = length();
        BufferByte result = null;
        if (count > 0) {
            if (len >= count) {
                byte[] resBuf = new byte[count];
                System.arraycopy(buffer, 0, resBuf, 0, count);
                result = new BufferByte(resBuf);
                return result;
            }
        } else {
            return result; // just null as wanted count = 0

        }
        return null;

    }

    // everything must be checked before calling this method
    // and count > 0
	/*------------------------------------------------------------------------------------*/
    // Read the incoming data and remove the Integer type part from it
    public int removeInt() {
        int result = readInt();
        removeBytes0(SZ_INT);
        return result;

    }

    // Read the incoming data and remove the Short type part from it
    public short removeShort() {
        short result = 0;
        byte[] resBuff = removeBytes(SZ_SHORT).getBuffer();
        result |= resBuff[0] & 0xff;
        result <<= 8;
        result |= resBuff[1] & 0xff;
        return result;

    }

    // Read the incoming data and remove the Byte type part from it
    public byte removeByte() {
        byte result = 0;
        byte[] resBuff = removeBytes(SZ_BYTE).getBuffer();
        result = resBuff[0];
        return result;

    }

    public int readInt() {
        int result = 0;
        int len = length();
        if (len >= SZ_INT) {
            result |= buffer[0] & 0xff;// first octet
            result <<= 8;
            result |= buffer[1] & 0xff;
            result <<= 8;
            result |= buffer[2] & 0xff;
            result <<= 8;
            result |= buffer[3] & 0xff;// last octet
            return result; // Concatenated integer result

        }
        // logger.DEBUG("Result returned= "+result);
        return result;

    }

    protected static short decodeUnsigned(byte signed) {
        if (signed >= 0) {
            return signed;
        } else {
            return (short) (256 + (short) signed);
        }
    }

    public BufferByte removeBytes(int count) {

        BufferByte result = readBytes(count);
        removeBytes0(count);
        return result;
    }
}// end class

