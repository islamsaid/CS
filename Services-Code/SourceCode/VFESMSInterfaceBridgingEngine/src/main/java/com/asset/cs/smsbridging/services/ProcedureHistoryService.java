package com.asset.cs.smsbridging.services;

import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.cs.smsbridging.daos.ProcedureHistoryDAO;
import com.asset.cs.smsbridging.models.HTTPMsgResult;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author aya.moawed
 */
public class ProcedureHistoryService {

    public void updateStatusForSMSBatch(Connection con, ArrayList<HTTPMsgResult> msgsStatus,ArrayList<Long> smsMsgIds) throws CommonException {

        ProcedureHistoryDAO dao = new ProcedureHistoryDAO();
        dao.updateStatusForSMSBatch(con,msgsStatus,smsMsgIds);
    }

}
