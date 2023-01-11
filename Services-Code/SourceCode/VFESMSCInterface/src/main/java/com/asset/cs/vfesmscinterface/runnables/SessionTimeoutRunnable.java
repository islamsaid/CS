package com.asset.cs.vfesmscinterface.runnables;

import static com.asset.cs.vfesmscinterface.initializer.Manager.systemProperities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.models.SubmitSMModel;
import com.asset.cs.vfesmscinterface.socket.Session;
import com.asset.cs.vfesmscinterface.utils.Executor;

/**
 *
 * @author mostafa.kashif
 */
public class SessionTimeoutRunnable implements Runnable {

    @Override
    public void run() {

        Thread.currentThread().setName("SessionTimeoutThread");

        while (!Manager.isShutdown.get() && !Thread.interrupted()) {
            try {

                LocalDateTime now = LocalDateTime.now();

                CommonLogger.businessLogger.debug("started at " + now);

                Long sessionTimeout = Long
                        .valueOf(systemProperities.get(Defines.SMSC_INTERFACE_PROPERTIES.SESSION_TIME_OUT));

                checkSession(now, sessionTimeout);

                checkOrphanMessages(now, sessionTimeout);

                Thread.sleep(sessionTimeout);
            } catch (InterruptedException e) {
                CommonLogger.businessLogger.debug("InterruptedException");
            } catch (Exception e) {
                CommonLogger.businessLogger.error("Gen Exception" + e);
                CommonLogger.errorLogger.error("Gen Exception", e);
            }
        }
    }

    private void checkSession(LocalDateTime now, Long sessionTimeout) {
        /**
         * 05 - Aug - 2020 The ConcurrentHashMap converted into a synchronized
         * hash map to avoid the problem of un-removed closed sessions from the
         * sessionMap All iterators over the sessionMap are put inside a
         * synchronized block of the sessionMap
         */
        synchronized (Manager.sessionMap) {
            LocalDateTime sessionTime;
            for (Map.Entry<String, Session> it : Manager.sessionMap.entrySet()) {
                try {
                    Session session = it.getValue();

                    switch (session.getConnectionStatus()) {
                        case OPEN:
                        case CLOSED:
                            sessionTime = session.getSessionCreationTime();
                            break;
                        case BOUND:
                            sessionTime = session.getLastReadTime();
                            break;
                        default:
                            // to prevent call close session while unbinding
                            continue;
                    }
                    Duration duration = Duration.between(sessionTime, now);

                    if (duration.toMillis() >= sessionTimeout) {
//					CommonLogger.businessLogger.debug(session.getSessionId() + " last appearance " + duration.toMillis()
//							+ " session will be closed");
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Session will be closed")
                                .put(GeneralConstants.StructuredLogKeys.SESSION_ID, session.getSessionId())
                                .put(GeneralConstants.StructuredLogKeys.LAST_APPEARANCE, duration.toMillis()).build());
                        Manager.closeSession(session);
                    }
                } catch (Exception e) {
                    CommonLogger.businessLogger.error("Exception occuered: " + e);
                    CommonLogger.errorLogger.error("Exception occuered", e);
                }
            }
        }
    }

    private void checkOrphanMessages(LocalDateTime now, Long sessionTimeout) {
        /**
         * 05 - Aug - 2020 The ConcurrentHashMap converted into a synchronized
         * hash map to avoid the problem of un-removed closed sessions from the
         * sessionMap All iterators over the sessionMap are put inside a
         * synchronized block of the sessionMap
         */
        synchronized (Session.getDividedMessageMap()) {
//            for (Map.Entry<String, Session> it : Manager.sessionMap.entrySet()) {
//                Session session = it.getValue();
                for (Map.Entry<String, ArrayList<SubmitSMModel>> dicidedMapIterator : Session.getDividedMessageMap()
                        .entrySet()) {
                    ArrayList<SubmitSMModel> list = dicidedMapIterator.getValue();
                    if (list.isEmpty()) {
                        Session.getDividedMessageMap().remove(dicidedMapIterator.getKey());
                    } else {
                        Duration duration = Duration.between(list.get(0).getLastUpdate(), now);
                        if (duration.toMillis() >= sessionTimeout) {
                            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Orphan Message will be Removed")
                                    .put(GeneralConstants.StructuredLogKeys.SESSION, dicidedMapIterator.getKey())
                                    .put(GeneralConstants.StructuredLogKeys.SAR_MSG_REF_NUM, list.get(0).getSarMsgRefNum().getValue()).build());
                            Session.getDividedMessageMap().remove(dicidedMapIterator.getKey());
                        }
                    }
                }
//           }
        }
    }
    
    
//    private void checkOrphanMessages(LocalDateTime now, Long sessionTimeout) {
//        for (Map.Entry<String, Session> it : Manager.sessionMap.entrySet()) {
//            Session session = it.getValue();
//            for (Map.Entry<Integer, ArrayList<SubmitSMModel>> dicidedMapIterator : session.getDividedMessageMap()
//                    .entrySet()) {
//                ArrayList<SubmitSMModel> list = dicidedMapIterator.getValue();
//                if (list.isEmpty()) {
//                    session.getDividedMessageMap().remove(dicidedMapIterator.getKey());
//                } else {
//                    Duration duration = Duration.between(list.get(0).getLastUpdate(), now);
//                    if (duration.toMillis() >= sessionTimeout) {
////                        CommonLogger.businessLogger
////                                .debug("Orphan Message in session: " + it.getKey() + " with SarMsgRefNum: "
////                                        + list.get(0).getSarMsgRefNum().getValue() + " will be removed.");
//                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Orphan Message will be Removed")
//                                .put(GeneralConstants.StructuredLogKeys.SESSION, it.getKey())
//                                .put(GeneralConstants.StructuredLogKeys.SAR_MSG_REF_NUM, list.get(0).getSarMsgRefNum().getValue()).build());
//                        session.getDividedMessageMap().remove(dicidedMapIterator.getKey());
//                    }
//                }
//            }
//        }
//    }
    
    

}
