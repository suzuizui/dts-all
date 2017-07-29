package com.le.dts.console.manager;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.JobOperation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.JobOperationAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by luliang on 15/1/15.
 */
public class JobOperationManager {

    private static final Log logger = LogFactory.getLog(JobOperationManager.class);

    @Autowired
    private JobOperationAccess jobOperationAccess;

    public Result<Long> insertOperation(JobOperation jobOperation) {
        Result<Long> result = new Result<Long>();
        try {

            long id = jobOperationAccess.insert(jobOperation);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(id);
        } catch (AccessException e) {
            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
            logger.error("[create]:error.", e);
        }
        return result;
    }

    public Result<List<JobOperation>> queryByServer(JobOperation jobOperation) {
        Result<List<JobOperation>> result = new Result<List<JobOperation>>();
        try {

            List<JobOperation> jobOperationList = jobOperationAccess.queryByServer(jobOperation);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(jobOperationList);
        } catch (AccessException e) {
            result.setResultCode(ResultCode.QUERY_JOB_OPERATION_ERROR);
            logger.error("[query]:error.", e);
        }
        return result;
    }

    public Result<Integer> deleteById(JobOperation jobOperation) {
        Result<Integer> result = new Result<Integer>();
        try {

            int count = jobOperationAccess.deleteById(jobOperation);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(count);
        } catch (AccessException e) {
            result.setResultCode(ResultCode.DELETE_JOB_OPERATION_ERROR);
            logger.error("[delete]:error.", e);
        }
        return result;
    }
}
