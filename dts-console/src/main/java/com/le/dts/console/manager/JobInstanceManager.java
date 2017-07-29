package com.le.dts.console.manager;

import java.util.Date;
import java.util.List;

import com.le.dts.console.store.JobInstanceSnapshotAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.common.exception.DtsException;
import com.le.dts.common.util.TimeUtil;
import com.le.dts.console.page.JobHistoryPageQuery;

/**
 * Created by Moshan on 14-11-19.
 * JobInstanceManager creates job instances in db and contests ZK locks
 * of job instances for current server.
 */
public class JobInstanceManager {

    private static final Log logger = LogFactory.getLog(JobInstanceManager.class);

    @Autowired
    private JobInstanceSnapshotAccess jobInstanceAccess;
    
    public void insertJobInstance(Job job, Date fireTime) throws AccessException {
        JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
        jobInstanceSnapshot.setJobId(job.getId());
        jobInstanceSnapshot.setDescription(job.getDescription());
        String strFireTime = TimeUtil.date2SecondsString(fireTime);
        jobInstanceSnapshot.setFireTime(strFireTime);
        jobInstanceSnapshot.setStatus(Constants.JOB_INSTANCE_STATUS_NEW);
        jobInstanceSnapshot.setGmtCreate(new Date());
        jobInstanceSnapshot.setGmtModified(new Date());
        getJobInstanceSnapshotAccess().insert(jobInstanceSnapshot);
    }
    
    public Result<List<JobInstanceSnapshot>> queryWorkingJobInstance(long jobId) {
    	Result<List<JobInstanceSnapshot>> result = new Result<List<JobInstanceSnapshot>>();
    	
    	try {
			List<JobInstanceSnapshot> snapShotList = jobInstanceAccess.queryWorking(jobId);
			result.setData(snapShotList);
			result.setResultCode(ResultCode.SUCCESS);
		} catch (AccessException e) {
            logger.error("query working job snapshot error,", e);
			result.setResultCode(ResultCode.QUERY_JOB_INSTANCE_ERROR);
		}
    	return result;
    }
    
    public Result<Integer> queryJobInstanceCount(long jobId) {
    	Result<Integer> result = new Result<Integer>();
    	try {
			int count = jobInstanceAccess.queryJobInstanceCount(jobId);
			if(count < 0) {
				result.setResultCode(ResultCode.QUERY_JOB_INSTANCE_ERROR);
			} else {
				result.setData(count);
				result.setResultCode(ResultCode.SUCCESS);
			}
		} catch (AccessException e) {
			result.setResultCode(ResultCode.QUERY_JOB_INSTANCE_ERROR);
		}
    	return result;
    }
    
    public Result<List<JobInstanceSnapshot>> pageQuery(JobHistoryPageQuery query) {
    	Result<List<JobInstanceSnapshot>> result = new Result<List<JobInstanceSnapshot>>();
    	
    	try {
			List<JobInstanceSnapshot> snapShotList = jobInstanceAccess.pageQuery(query);
			result.setData(snapShotList);
			result.setResultCode(ResultCode.SUCCESS);
		} catch (AccessException e) {
			result.setResultCode(ResultCode.QUERY_JOB_INSTANCE_ERROR);
		}
    	return result;
    }
    

    public JobInstanceSnapshot findJobInstance(Job job, Date fireTime) {
        try {
            return getJobInstanceSnapshotAccess().findByJobIdAndFireTime(job.getId(),
                    TimeUtil.date2SecondsString(fireTime));
        } catch (AccessException e) {
        	logger.error("[JobInstanceManager]:findJobInstance error!");
            throw new DtsException("Failed to find job instance", e);
        }
    }
    
    public Result<Boolean> setGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot, byte[] globalArguments) {
    	Result<Boolean> result = new Result<Boolean>(false);
    	
    	int updateResult = 0;
    	try {
			updateResult = jobInstanceAccess.update(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: setGlobalArguments error, jobInstanceSnapshot:" + jobInstanceSnapshot.toString(), e);
		}
    	if(updateResult <= 0) {
    		result.setResultCode(ResultCode.FAILURE);
    		return result;
    	}
    	
    	result.setData(true);
    	result.setResultCode(ResultCode.SUCCESS);
    	return result;
    }
    
    public Result<byte[]> getGlobalArguments(JobInstanceSnapshot query) {
    	Result<byte[]> result = new Result<byte[]>();
    	
    	JobInstanceSnapshot jobInstanceSnapshot = null;
    	try {
			jobInstanceSnapshot = jobInstanceAccess.queryInstanceGlobal(query);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: setGlobalArguments error, jobInstanceSnapshot:" + jobInstanceSnapshot.toString(), e);
		}
    	
    	if(null == jobInstanceSnapshot) {
        	result.setResultCode(ResultCode.SUCCESS);
			return result;
    	}
    	
    	result.setData(jobInstanceSnapshot.getInstanceGlobal());
    	result.setResultCode(ResultCode.SUCCESS);
    	return result;
    }

    public JobInstanceSnapshotAccess getJobInstanceSnapshotAccess() {
        return this.jobInstanceAccess;
    }

}
