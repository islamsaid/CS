/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.api;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Esmail.Anbar
 */
@Path("/Ready")
public class ReadyCheck {

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRequest() {
        CommonLogger.businessLogger.info("Started Ready Servlet");
//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet() + " | Ready Accumlator Count: " + ConfigurationManager.readyCheckAccumulator.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ready Check API Status")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.readyCheckAccumulator.incrementAndGet()).build());
        ConcurrentHashMap<String, ArrayBlockingQueue> queues = new ConcurrentHashMap<>();
        ArrayList<Float> queuePercentages = new ArrayList<>();

        queues.put("requestsToBeLogged", ConfigurationManager.requestsToBeLogged);
        queuePercentages.add(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE);
        queues.put("messagesToBeArchived", ConfigurationManager.messagesToBeArchived);
        queuePercentages.add(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE);
        queues.put("smsToBeRollbacked", ConfigurationManager.smsToBeRollbacked);
        queuePercentages.add(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE);
        queues.put("smsToBeValidated", ConfigurationManager.smsToBeValidated);
        queuePercentages.add(Defines.INTERFACES.MAX_SMS_VALIDATION_QUEUE_OCCUPANCY_RATE_VALUE);

        for (Map.Entry<Long, ArrayBlockingQueue<SmsBusinessModel>> entry : ConfigurationManager.smsToBeSent.entrySet()) {
            queues.put("Id:" + entry.getKey(), entry.getValue());
            queuePercentages.add(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE);
        }

        if (!Utility.readyCheck(ConfigurationManager.concurrentRequests, Defines.INTERFACES.MAX_CONCURRENT_REQUESTS_VALUE, queues, queuePercentages, CommonLogger.businessLogger)) {
//            CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet() + " | Ready Failed Count: " + ConfigurationManager.readyCheckFailedCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ready Check Failed")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.readyCheckFailedCount.incrementAndGet()).build());
            CommonLogger.businessLogger.info("Ended Ready Servlet");
            return Response.serverError().build();
        }

//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet() + " | Ready Success Count: " + ConfigurationManager.readyCheckSuccessCount.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ready Check Success")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.readyCheckSuccessCount.incrementAndGet()).build());
        CommonLogger.businessLogger.info("Ended Ready Servlet");
        return Response.ok().build();
    }
}
