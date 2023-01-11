package client;

/********************************************************************************************/
/********************************************************************************************

Class contains the SMPP header variables

 ******************************************************************/
/** *************************************************************************************** */
// class presenting the header of the SMPP protocol
public class MessageHeader {

    public int commandLength = 0;
    public int commandID = 0;
    public int commandStatus = -1;
    public int sequenceNumber = 0;
    // added part not in header
    public String retVal = null;
    // constructor
    public MessageHeader() {
    }
}