/**
 * 
 */
package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Mgouda
 * 
 */
public class ObjectSerialization {

    public static final String ObjectFilePath = "./BatchInQ/";

    /**
     * @param fileName
     */
    public static long getObject(String fileName) throws FileNotFoundException,
            IOException, ClassNotFoundException {

        File theFile = null;
        FileInputStream inStream = null;
        ObjectInputStream objInpStream = null;
        long inQnumber;
        theFile = new File(ObjectFilePath + fileName + ".txt");
        if (theFile.exists()) {
            inStream = new FileInputStream(theFile);
            objInpStream = new ObjectInputStream(inStream);
            inQnumber = objInpStream.readLong();
            objInpStream.close();
            inStream.close();
            theFile.delete();

            return inQnumber;
        }
        return 0;
    }

    public static void setObject(String fileName, long inQnumber)
            throws FileNotFoundException, IOException, ClassNotFoundException {

        File theFile = null;
        FileOutputStream outStream = null;
        ObjectOutputStream objOutStream = null;
        theFile = new File(ObjectFilePath + fileName + ".txt");
        if (theFile.exists()) {
            if (inQnumber == 0) {// at the end when decreasing the number to zero, remove the file
                theFile.delete();
                return;
            } else {
                theFile.delete();
            }
        }
        outStream = new FileOutputStream(theFile);
        objOutStream = new ObjectOutputStream(outStream);
        objOutStream.writeLong(inQnumber);
        objOutStream.close();
        outStream.close();
    }
}
