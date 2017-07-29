package com.le.dts.server.manager;

import java.util.List;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.JobRelationAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.exception.AccessException;

/**
 * Job依赖;
 * Created by luliang on 14/12/12.
 */
public class JobRelationManager  implements ServerContext, Constants {

    private static final Log logger = LogFactory.getLog(JobRelationManager.class);

    public Result<Long> createJobRelation(JobRelation jobRelation) {
        Result<Long> result = new Result<Long>();
        try {
            long id = getJobRelationAccess().insert(jobRelation);
            if(id > 0) {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(id);
            }
            result.setResultCode(ResultCode.CREATE_JOB_RELATION_ERROR);
        } catch (AccessException e) {
            logger.error("create job relation error!", e);
            result.setResultCode(ResultCode.CREATE_JOB_RELATION_ERROR);
        }
        return result;
    }

    public Result<Integer> updateJobRelation(JobRelation jobRelation) {
        Result<Integer> result = new Result<Integer>();
        try {
            int count = getJobRelationAccess().updateFinishCount(jobRelation);
            if(count > 0) {
                result.setData(count);
                result.setResultCode(ResultCode.SUCCESS);
            } else {
                result.setResultCode(ResultCode.UPDATE_JOB_RELATION_ERROR);
            }
        } catch (AccessException e) {
            result.setResultCode(ResultCode.UPDATE_JOB_RELATION_ERROR);
        }
        return result;
    }

    public Result<Integer> resetJobRelation(JobRelation jobRelation) {
        Result<Integer> result = new Result<Integer>();
        try {
            int count = getJobRelationAccess().resetFinishCount(jobRelation);
            if(count > 0) {
                result.setData(count);
                result.setResultCode(ResultCode.SUCCESS);
            } else {
                result.setResultCode(ResultCode.UPDATE_JOB_RELATION_ERROR);
            }
        } catch (AccessException e) {
            result.setResultCode(ResultCode.UPDATE_JOB_RELATION_ERROR);
        }
        return result;
    }


    /**
     * 查询关系;
     * @param jobRelation
     * @return
     */
    public Result<JobRelation> queryRelation(JobRelation jobRelation) {
        Result<JobRelation> result = new Result<JobRelation>();
        try {
            List<JobRelation> relations = getJobRelationAccess().queryRelation(jobRelation);
            result.setResultCode(ResultCode.SUCCESS);
            if(relations.size() > 0) {
                result.setData(relations.get(0));
            }
        } catch (AccessException e) {
            result.setResultCode(ResultCode.QUERY_JOB_RELATION_ERROR);
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
            List<JobRelation> relations = getJobRelationAccess().queryBefore(jobRelation);
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
            List<JobRelation> relations = getJobRelationAccess().queryAfter(jobRelation);
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
            int count = getJobRelationAccess().delete(jobRelation);
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

    public Result<Boolean> checkAllBeforeDone(JobRelation jobRelation) {
        Result<Boolean> result = new Result<Boolean>();
        // 检查Job依赖的Job是否都执行完了;
        Result<List<JobRelation>> jobRelationBeforeListReslult = jobRelationManager.queryBeforeJob(jobRelation);
        if(jobRelationBeforeListReslult.getResultCode() != ResultCode.SUCCESS) {
            result.setResultCode(jobRelationBeforeListReslult.getResultCode());
            result.setData(false);
            return result;
        }
        List<JobRelation> relationList = jobRelationBeforeListReslult.getData();
        for(JobRelation jobRelation1: relationList) {
            if(jobRelation1.getFinishCount() == 0) {
                result.setResultCode(ResultCode.DEPENDENCE_JOB_WAIT);
                result.setData(false);
                return result;
            }
        }
        result.setData(true);
        return result;
    }

    public JobRelationAccess getJobRelationAccess() {
        return store.getJobRelationAccess();
    }

}
