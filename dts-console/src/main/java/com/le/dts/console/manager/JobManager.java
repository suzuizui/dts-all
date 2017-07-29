package com.le.dts.console.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobServerRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.common.util.CheckUtil;
import com.le.dts.console.page.JobPageQuery;
import com.le.dts.console.store.JobAccess;
import com.le.dts.console.store.JobServerRelationAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 * Job管理器
 * @author tianyao.myc
 *
 */
public class JobManager implements Constants {

	private static final Log logger = LogFactory.getLog(JobManager.class);
	
	/** Job信息访问接口 */
	@Autowired
	private JobAccess jobAccess;
	
	/** job和机器关系映射访问接口 */
	@Autowired
	private JobServerRelationAccess jobServerRelationAccess;
	
	@Autowired
	private SqlMapClients sqlMapClients;
	
    /**
     * 创建内部Job和机器之间的映射关系
     * @param job
     * @return
     */
    public Result<Boolean> createJobServerRelation(Job job, String ip) {
    	Result<Boolean> createResult = new Result<Boolean>(false);
		
		Result<Boolean> checkResult = CheckUtil.checkJob(job);
		if(! checkResult.getData().booleanValue()) {
			createResult.setData(checkResult.getData());
			createResult.setResultCode(checkResult.getResultCode());
			return createResult;
		}

		JobServerRelation jobMachineRelation = new JobServerRelation();
		jobMachineRelation.setGmtCreate(new Date());
		jobMachineRelation.setGmtModified(new Date());
		jobMachineRelation.setJobId(job.getId());
		jobMachineRelation.setServer(ip);
		
		long insertResult;
		try {
			insertResult = this.jobServerRelationAccess.insert(jobMachineRelation);
			if(insertResult <= 0) {
				logger.error("[JobManager]: insert jobMachineRelation error, job:" + job.toString());
				createResult.setResultCode(ResultCode.CREATE_INTERNAL_JOB_MAPPING_FAILURE);
				return createResult;
			}
		} catch (AccessException e) {
			logger.error("[JobManager]: insert jobMachineRelation error, job:" + job.toString());
			createResult.setResultCode(ResultCode.CREATE_INTERNAL_JOB_MAPPING_FAILURE);
			return createResult;
		}
		
		createResult.setData(true);
		createResult.setResultCode(ResultCode.SUCCESS);
		return createResult;
    }
    
    /**
     * 是用JobId查询关系;
     * @param query
     * @return
     */
    public Result<List<JobServerRelation>> queryJobServerRelation(JobServerRelation query) {
    	Result<List<JobServerRelation>> result = new Result<List<JobServerRelation>>();
    	try {
			List<JobServerRelation> relationList = jobServerRelationAccess.queryByJobId(query);
			if(relationList.size() < 0) {
				result.setResultCode(ResultCode.NO_JOB_SERVER_RELATION_ERROR);
				return result;
			}
			result.setData(relationList);
			result.setResultCode(ResultCode.SUCCESS);
		} catch (AccessException e) {
			logger.error("[queryJobServerRelation]:queryJobServerRelation error!", e);
			result.setResultCode(ResultCode.QUERY_JOB_SERVER_ERROR);
			return result;
		}
    	return result;
    }
    
    public Result<Boolean> deleteJobServerRelation(Job job) {
    	Result<Boolean> deleteRelationResult = new Result<Boolean>(false);
    	JobServerRelation jobMachineRelation = new JobServerRelation();
		jobMachineRelation.setJobId(job.getId());
    	long deleteResult;
		try {
			deleteResult = this.jobServerRelationAccess.delete(jobMachineRelation);
			if(deleteResult < 0) {
				logger.error("[JobManager]: delete jobMachineRelation error, job:" + job.toString());
				deleteRelationResult.setData(false);
				deleteRelationResult.setResultCode(ResultCode.DELETE_JOB_MATION_RELATION);
				return deleteRelationResult;
			}
		} catch (AccessException e) {
			logger.error("[JobManager]: delete jobMachineRelation error, job:" + job.toString());
			deleteRelationResult.setData(false);
			deleteRelationResult.setResultCode(ResultCode.DELETE_JOB_MATION_RELATION);
			return deleteRelationResult;
		}
		
		deleteRelationResult.setData(true);
		deleteRelationResult.setResultCode(ResultCode.SUCCESS);
		return deleteRelationResult;
    }
    
	/**
	 * 创建内部Job
	 * @param job
	 * @return
	 */
	public Result<Boolean> createInternalJob(Job job) {
		Result<Boolean> createResult = new Result<Boolean>(false);
		return createResult;
	}
	
	/**
	 * 启用内存job实例
	 * @param job
	 * @return
	 */
	public Result<Boolean> enableInternalJob(Job job) {
		job.setStatus(JOB_STATUS_ENABLE);
		return this.updateInternalJobStatus(job);
	}
	
	/**
	 * 禁用内存job实例
	 * @param job
	 * @return
	 */
	public Result<Boolean> disableInternalJob(Job job) {
		job.setStatus(JOB_STATUS_DISABLE);
		return this.updateInternalJobStatus(job);
	}
	
	/**
	 * 创建持久化Job
	 * @param job
	 * @return
	 */
	public Result<Long> createPersistenceJob(Job job) {
		Result<Long> createResult = new Result<Long>(0L);
		
		Result<Boolean> checkResult = CheckUtil.checkJob(job);
		if(!checkResult.getData().booleanValue()) {
			createResult.setResultCode(checkResult.getResultCode());
			return createResult;
		}
		
		long id = 0L;
		try {
			id = this.jobAccess.insert(job);
		} catch (Throwable e) {
			logger.error("[JobManager]: createPersistenceJob insert error, job:" + job.toString(), e);
			createResult.setResultCode(ResultCode.CREATE_PERSISTENCE_JOB_INSERT_FAILURE);
			return createResult;
		}
		
		createResult.setData(id);
		createResult.setResultCode(ResultCode.CREATE_PERSISTENCE_JOB_SUCCESS);
		return createResult;
	}
	
	/**
	 * 启用持久化job
	 * @param job
	 * @return
	 */
	public Result<Integer> enablePersistenceJob(Job job) {
		job.setStatus(JOB_STATUS_ENABLE);
		return this.updatePersistenceJobStatus(job);
	}
	
	/**
	 * 禁用持久化job
	 * @param job
	 * @return
	 */
	public Result<Integer> disablePersistenceJob(Job job) {
		job.setStatus(JOB_STATUS_DISABLE);
		return this.updatePersistenceJobStatus(job);
	}
	
	/**
	 * 查询内部Job
	 * @param query
	 * @return
	 */
	public Result<Job> queryInternalJob(Job query) {
		Result<Job> queryResult = new Result<Job>();
		return queryResult;
	}
	
	/**
	 * 查询持久化Job
	 * @param jobPage
	 * @return
	 */
	public Result<List<Job>> queryPersistenceJob(JobPageQuery jobPage) {
		Result<List<Job>> queryResult = new Result<List<Job>>();
		try {
			List<Job> jobs = jobAccess.pageQuery(jobPage);
			queryResult.setData(jobs);
			queryResult.setResultCode(ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS);
		} catch (Throwable e) {
			
			logger.error("[JobManager]: queryPersistenceJob error, jobPage:" + jobPage, e);
			
			queryResult.setResultCode(ResultCode.QUERY_PERSISTENCE_JOB_IS_NULL);
			return queryResult;
		}
		return queryResult;
	}
	
	public Result<Job> queryJobById(Job query) {
		Result<Job> queryResult = new Result<Job>();
		try {
			Job jobs = jobAccess.queryJobById(query);
			queryResult.setData(jobs);
			queryResult.setResultCode(ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS);
		} catch (AccessException e) {
			queryResult.setResultCode(ResultCode.QUERY_PERSISTENCE_JOB_IS_NULL);
			return queryResult;
		}
		return queryResult;
	}
	
	public Job getJobById(long jobId) {
    	Job job = null;
    	try {
    		Job query = new Job();
    		query.setId(jobId);
			job = jobAccess.queryJobById(query);
		} catch (Throwable e) {
			logger.error("[JobManager]: getJobById error, jobId:" + jobId, e);
		}
    	return job;
    }
	
	public Result<Integer> countClientClusterJobs(Job job) {
		Result<Integer> result = new Result<Integer>();
		try {
			int count = jobAccess.countJob(job);
			result.setResultCode(ResultCode.SUCCESS);
			result.setData(count);
		} catch (Throwable e) {
			
			logger.error("[JobManager]: countClientClusterJobs error, job:" + job, e);
			
			result.setData(0);
			result.setResultCode(ResultCode.QUERY_JOB_COUNT_ERROR);
		}
		return result;
	}
	
	/**
	 * 更新内部Job
	 * @param job
	 * @return
	 */
	public Result<Boolean> updateInternalJob(Job job) {
		Result<Boolean> updateResult = new Result<Boolean>(false);
		
		Result<Boolean> checkResult = CheckUtil.checkJob(job);
		if(! checkResult.getData().booleanValue()) {
			updateResult.setData(checkResult.getData());
			updateResult.setResultCode(checkResult.getResultCode());
			return updateResult;
		}
		
		Result<Boolean> deleteResult = deleteInternalJob(job);
		if(! deleteResult.getData().booleanValue()) {
			updateResult.setData(deleteResult.getData());
			updateResult.setResultCode(deleteResult.getResultCode());
			return updateResult;
		}
		
		Result<Boolean> createResult = createInternalJob(job);
		if(! createResult.getData().booleanValue()) {
			updateResult.setData(createResult.getData());
			updateResult.setResultCode(createResult.getResultCode());
			return updateResult;
		}
		
		updateResult.setData(true);
		updateResult.setResultCode(ResultCode.UPDATE_INTERNAL_JOB_SUCCESS);
		return updateResult;
	}
	
	/**
	 * 更新内部JobStatus
	 * @param job
	 * @return
	 */
	public Result<Boolean> updateInternalJobStatus(Job job) {
		Result<Boolean> updateResult = new Result<Boolean>(false);
		
		updateResult.setResultCode(ResultCode.UPDATE_INTERNAL_JOB_SUCCESS);
		return updateResult;
	}
	
	/**
	 * 更新持久化Job
	 * @param job
	 * @return
	 */
	public Result<Integer> updatePersistenceJob(Job job) {
		Result<Integer> updateResult = new Result<Integer>(0);
		
		Result<Boolean> checkResult = CheckUtil.checkJob(job);
		if(! checkResult.getData().booleanValue()) {
			updateResult.setResultCode(checkResult.getResultCode());
			return updateResult;
		}
		
		int result = 0;
		try {
			result = this.jobAccess.update(job);
		} catch (Throwable e) {
			logger.error("[JobManager]: updatePersistenceJob error, job:" + job.toString(), e);
			updateResult.setResultCode(ResultCode.UPDATE_JOB_FAILURE);
			return updateResult;
		}
		
		updateResult.setData(result);
		updateResult.setResultCode(ResultCode.SUCCESS);
		return updateResult;
	}

    public Result<Integer> updateJobArguments(Job job) {
        Result<Integer> updateResult = new Result<Integer>(0);

        int result = 0;
        try {
            result = this.jobAccess.updateJobArguments(job);
        } catch (Throwable e) {
            logger.error("[JobManager]: updateJobArguments error, job:" + job.toString(), e);
            updateResult.setResultCode(ResultCode.UPDATE_JOB_FAILURE);
            return updateResult;
        }

        updateResult.setData(result);
        updateResult.setResultCode(ResultCode.SUCCESS);
        return updateResult;
    }
	
	/**
	 * 更新持久化JobStatus
	 * @param job
	 * @return
	 */
	public Result<Integer> updatePersistenceJobStatus(Job job) {
		Result<Integer> updateResult = new Result<Integer>(0);
		
		Result<Boolean> checkResult = CheckUtil.checkJob(job);
		if(! checkResult.getData().booleanValue()) {
			updateResult.setResultCode(checkResult.getResultCode());
			return updateResult;
		}
		
		int result = 0;
		try {
			result = this.jobAccess.updateJobStatus(job);
		} catch (Throwable e) {
			logger.error("[JobManager]: updatePersistenceJobStatus error, job:" + job.toString(), e);
			updateResult.setResultCode(ResultCode.UPDATE_PERSISTENCE_JOB_FAILURE);
			return updateResult;
		}
		
		updateResult.setData(result);
		updateResult.setResultCode(ResultCode.SUCCESS);
		return updateResult;
	}
	
	/**
	 * 删除内部Job
	 * @param job
	 * @return
	 */
	public Result<Boolean> deleteInternalJob(Job job) {
		Result<Boolean> deleteResult = new Result<Boolean>(false);
		return deleteResult;
	}
	
	/**
	 * 删除持久化Job
	 * @param job
	 * @return
	 */
	public Result<Long> deletePersistenceJob(Job job) {
		Result<Long> deleteResult = new Result<Long>(0L);
		long result = 0;
		try {
			result = this.jobAccess.delete(job);
		} catch (Throwable e) {
			logger.error("[JobManager]: deletePersistenceJob error, job:" + job.toString(), e);
			deleteResult.setResultCode(ResultCode.DELETE_PERSISTENCE_JOB_FAILURE);
			return deleteResult;
		}
		deleteResult.setData(result);
		deleteResult.setResultCode(ResultCode.DELETE_PERSISTENCE_JOB_SUCCESS);
		return deleteResult;
	}
	
	/**
	 * 获取备份机器列表
	 * @param job
	 * @return
	 */
	public List<JobServerRelation> getBackupServerList(Job job) {
		JobServerRelation query = new JobServerRelation();
		query.setJobId(job.getId());
		List<JobServerRelation> jobMachineRelationList = null;
		try {
			jobMachineRelationList = this.jobServerRelationAccess.queryByJobId(query);
		} catch (Throwable e) {
			logger.error("[JobManager]: getBackupMachineList error, job:" + job.toString(), e);
		}
		if(null == jobMachineRelationList) {
			jobMachineRelationList = new ArrayList<JobServerRelation>();
		}
		return jobMachineRelationList;
	}
	
}
