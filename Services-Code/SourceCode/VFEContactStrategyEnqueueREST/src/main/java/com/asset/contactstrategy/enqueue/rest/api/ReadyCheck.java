/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.enqueue.rest.api;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.enqueue.rest.common.ConfigurationManager;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
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
//        CommonLogger.businessLogger.info("Started Ready Servlet");
//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet() + " | Ready Accumlator Count: " + ConfigurationManager.readyCheckAccumulator.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started Ready Servlet")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.readyCheckAccumulator.incrementAndGet()).build());
        ConcurrentHashMap<String, ArrayBlockingQueue> queues = new ConcurrentHashMap<>();
        queues.put("loggingQueue", ConfigurationManager.loggingQueue);

        ArrayList<Float> queuePercentages = new ArrayList<>();
        queuePercentages.add(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE);

        if (!Utility.readyCheck(ConfigurationManager.concurrentRequests, Defines.INTERFACES.MAX_CONCURRENT_REQUESTS_VALUE, queues, queuePercentages, CommonLogger.businessLogger)) {
//            CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet() + " | Ready Failed Count: " + ConfigurationManager.readyCheckFailedCount.incrementAndGet());
//            CommonLogger.businessLogger.info("Ended Ready Servlet");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended Ready Servlet and Failed")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.readyCheckFailedCount.incrementAndGet()).build());
            return Response.serverError().build();
        }

//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet() + " | Ready Success Count: " + ConfigurationManager.readyCheckSuccessCount.incrementAndGet());
//        CommonLogger.businessLogger.info("Ended Ready Servlet");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended Ready Servlet and Successful")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.readyCheckSuccessCount.incrementAndGet()).build());
        return Response.ok().build();
    }
}
