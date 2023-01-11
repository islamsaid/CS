package client;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.sendingsms.defines.Defines;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author islam.said
 */
public class Throttling {
    
    private final AtomicInteger smscThroughputCount;
    private long startTime;
    private final long smscThroughput;
    private final long smscWindowSize;
    private final Long smscVersionId;
    
    public Throttling(long smscThroughput, long smscWindowSize, Long smscVersionId){
        this.smscThroughput = smscThroughput;
        this.smscWindowSize = smscWindowSize;
        smscThroughputCount = new AtomicInteger(0);
        startTime = System.currentTimeMillis();
        this.smscVersionId = smscVersionId; 
    }
    
    public void incrementThroughputCount(){

        smscThroughputCount.getAndIncrement();
        
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Increment Throughput Count")
                .put(GeneralConstants.StructuredLogKeys.SMSC_VERSION_ID, this.smscVersionId)
                .put(GeneralConstants.StructuredLogKeys.CURRENT_THROUGHPUT_COUNT, this.smscThroughputCount)
                .put(GeneralConstants.StructuredLogKeys.SMSC_THROUGHPUT, this.smscThroughput)
                .build());        
    }
    
 
    public void checkThroughput() throws InterruptedException{
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSC Version ID: "+this.smscVersionId+" | Start Checking SMSC Throughput")
                .put(GeneralConstants.StructuredLogKeys.CURRENT_THROUGHPUT_COUNT, this.smscThroughputCount)
                .put(GeneralConstants.StructuredLogKeys.SMSC_THROUGHPUT, this.smscThroughput)
                .build());
        
        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        
        if (smscThroughputCount.get() == smscThroughput && timeDiff <= 1000){
            long sleepTime = 1000 - timeDiff;
            
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSC Version ID: "+this.smscVersionId+" | Reached SMSC Throuhput"
                    + " | Will Sleep for " + sleepTime + " msec")
                    .put(GeneralConstants.StructuredLogKeys.CURRENT_THROUGHPUT_COUNT, this.smscThroughputCount)
                    .put(GeneralConstants.StructuredLogKeys.SMSC_THROUGHPUT, this.smscThroughput)
                    .build());
            
            Thread.sleep(sleepTime);
            resetThroughput();
            
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSC Version ID: "+this.smscVersionId+" | SMSC Throuhput Count reseted")
                    .put(GeneralConstants.StructuredLogKeys.CURRENT_THROUGHPUT_COUNT, this.smscThroughputCount)
                    .put(GeneralConstants.StructuredLogKeys.SMSC_THROUGHPUT, this.smscThroughput)
                    .build());
        
        } else if (timeDiff > 1000){
            resetThroughput();
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSC Version ID: "+this.smscVersionId+" | 1 sec Receaded and SMSC Throuhput Count Reseted")
                    .put(GeneralConstants.StructuredLogKeys.CURRENT_THROUGHPUT_COUNT, this.smscThroughputCount)
                    .put(GeneralConstants.StructuredLogKeys.SMSC_THROUGHPUT, this.smscThroughput)
                    .build());
        }
        
        incrementThroughputCount();
        
    }
    
    private void resetThroughput(){
        startTime = System.currentTimeMillis();
        smscThroughputCount.set(0);
    }
    
    public void checkWindowSize(Hashtable SendRcvHash) throws InterruptedException{
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSC Version ID: "+this.smscVersionId+" | Start Checking the SMSC Window Size")
                .put(GeneralConstants.StructuredLogKeys.CURRENT_WINDOW_SIZE_COUNT, SendRcvHash.size())
                .put(GeneralConstants.StructuredLogKeys.SMSC_WINDOW_SIZE, this.smscWindowSize)
                .build());
        
        while (SendRcvHash.size() >= smscWindowSize){
            long sleepTime = Long.valueOf(Defines.fileConfigurations.get(Defines.WINDOW_SIZE_SLEEP_TIME));
            
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "SMSC Version ID: "+this.smscVersionId+" | Reached SMSC Window Size"
                    + " | Will Sleep for " + sleepTime + " msec")
                    .put(GeneralConstants.StructuredLogKeys.CURRENT_WINDOW_SIZE_COUNT, SendRcvHash.size())
                    .put(GeneralConstants.StructuredLogKeys.SMSC_WINDOW_SIZE, this.smscWindowSize)
                    .build());
            
            Thread.sleep(sleepTime);

        }
      
    }
    
}
