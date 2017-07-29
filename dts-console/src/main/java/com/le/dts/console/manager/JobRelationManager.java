package com.le.dts.console.manager;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.JobRelationAccess;








/**
 * Job依赖;
 * Created by luliang on 14/12/12.
 */
public class JobRelationManager implements Constants {

    private static final Log logger = LogFactory.getLog(JobRelationManager.class);

    @Autowired
    private JobRelationAccess jobRelationAccess;

    public Result<Long> createJobRelation(JobRelation jobRelation) {
        Result<Long> result = new Result<Long>();
        try {
            long id = jobRelationAccess.insert(jobRelation);
            if(id > 0) {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(id);
            } else {
                result.setResultCode(ResultCode.CREATE_JOB_RELATION_ERROR);
            }

        } catch (AccessException e) {
            logger.error("create job relation error!", e);
            result.setResultCode(ResultCode.CREATE_JOB_RELATION_ERROR);
        }
        return result;
    }

    /**
     * 查询前置任务;
     * @param jobRelation
     * @return
     */
    public Result<List<JobRelation>> queryBeforeJob(JobRelation jobRelation) {
        Result<List<JobRelation>> result = new Result<List<JobRelation>>();
        try {
            List<JobRelation> relations = jobRelationAccess.queryBefore(jobRelation);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(relations);
        } catch (AccessException e) {
            result.setResultCode(ResultCode.QUERY_JOB_RELATION_ERROR);
        }
        return result;
    }

    /**
     * 查询后置任务;
     * @param jobRelation
     * @return
     */
    public Result<List<JobRelation>> queryAfterJob(JobRelation jobRelation) {
        Result<List<JobRelation>> result = new Result<List<JobRelation>>();
        try {
            List<JobRelation> relations = jobRelationAccess.queryAfter(jobRelation);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(relations);
        } catch (AccessException e) {
            result.setResultCode(ResultCode.QUERY_JOB_RELATION_ERROR);
        }
        return result;
    }

    public Result<Integer> deleteJobRelation(JobRelation jobRelation) {
        Result<Integer> result = new Result<Integer>();
        try {
            int count = jobRelationAccess.delete(jobRelation);
            if(count <= 0) {
                result.setResultCode(ResultCode.DELETE_JOB_RELATION_ERROR);
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(count);
            }
        } catch (AccessException e) {
            logger.error("delete job relation error!", e);
            result.setResultCode(ResultCode.DELETE_JOB_RELATION_ERROR);
        }
        return result;
    }
    
    public void deleteAllRelation(long jobId) {
    	JobRelation jobRelation = new JobRelation();
    	jobRelation.setJobId(jobId);
    	int result = 0;
    	try {
    		result = jobRelationAccess.deleteAllRelation(jobRelation);
		} catch (Throwable e) {
			logger.error("[JobRelationManager]: deleteAllRelation error, jobId:" + jobId, e);
		}
    	logger.info("[JobRelationManager]: deleteAllRelation, result:" + result);
    }
    
    public void updateResetFinishCount(long beforeJobId) {
    	
    	JobRelation jobRelation = new JobRelation();
    	jobRelation.setBeforeJobId(beforeJobId);
    	
    	try {
			jobRelationAccess.updateResetFinishCount(jobRelation);
		} catch (Throwable e) {
			logger.error("[JobRelationManager]: updateResetFinishCount error, beforeJobId:" + beforeJobId, e);
		}
    }
    

}
