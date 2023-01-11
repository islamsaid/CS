/**
 * created on: Dec 17, 2017 11:03:58 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.socket;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import java.io.DataInputStream;

/**
 * @author mohamed.morsy
 *
 */
public class Connection implements AutoCloseable {

    private Socket socket;

    private DataInputStream inputStream;

    private OutputStream outputStream;

    public Connection(Socket socket) throws IOException {

        this.socket = socket;

        this.inputStream = new DataInputStream(socket.getInputStream());

        this.outputStream = socket.getOutputStream();

//		CommonLogger.businessLogger
//				.debug("Connection with client ip:  " + socket.getInetAddress() + " started successfully");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Connection Started Successfully")
                .put(GeneralConstants.StructuredLogKeys.IP_ADDRRESS, socket.getInetAddress()).build());
    }

    public BufferByte readPDU() throws IOException {

        synchronized (inputStream) {

            byte[] bufferarray = new byte[4];

            inputStream.readFully(bufferarray);

            int commandLength = convertByteArrayToInt(bufferarray);

            if (commandLength == 0) {
                socket.close();
                throw new IOException("socket read 0 as commandlength");
            }

            byte[] buffer = new byte[commandLength];

            inputStream.readFully(buffer, 4, commandLength - 4);

            System.arraycopy(bufferarray, 0, buffer, 0, 4);

            return new BufferByte(buffer);
        }
    }

    public void writePDU(BufferByte pdu) throws IOException {
        synchronized (outputStream) {
            outputStream.write(pdu.getBuffer(), 0, pdu.length());
        }
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    private int convertByteArrayToInt(byte[] buffer) {
        int result = 0;
        for (int count = 0; count < 4; count++) {
            result <<= 8;
            result |= buffer[count] & 0xff;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Connection [ip=" + socket.getInetAddress() + "]";
    }

}
