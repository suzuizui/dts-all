package com.le.dts.console.store;

import java.util.List;

import com.le.dts.common.domain.store.Job;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.page.JobPageQuery;

/**
 * Job信息访问接口
 * @author tianyao.myc
 *
 */
public interface JobAccess {

	/**
	 * 插入
	 * @param job
	 * @return
	 * @throws AccessException
	 */
	public long insert(Job job) throws AccessException;
	
	/**
	 * 查询
	 * @param jobPage
	 * @return
	 * @throws AccessException
	 */
	public List<Job> pageQuery(JobPageQuery jobPage) throws AccessException;
	
	public List<Job> query(Job jobPage) throws AccessException;
	
	/**
	 * 根据id查询Job
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public Job queryJobById(Job query) throws AccessException;
	
	/**
	 * 计数Job;
	 * @param query
	 * @return
	 */
	public int countJob(Job query) throws AccessException;

    /**
     * 计数启用Job;
     * @return
     */
    public long countEnableJob() throws AccessException;

    /**
     * 查询启用Job;
     * @param offset
     * @param length
     * @param bulkId
     * @param bulkAmout
     * @return
     * @throws AccessException
     */
    public List<Job> queryEnableList(long offset, int length, int bulkId, int bulkAmout) throws AccessException;
    
    public List<Job> queryAllEnableJobList(long offset) throws AccessException;
	
	/**
	 * 更新
	 * @param job
	 * @return
	 * @throws AccessException
	 */
	public int update(Job job) throws AccessException;

    /**
     * 更新
     * @param job
     * @return
     * @throws AccessException
     */
    public int updateJobArguments(Job job) throws AccessException;
	
	/**
	 * 更新JobStatus
	 * @param job
	 * @return
	 * @throws AccessException
	 */
	public int updateJobStatus(Job job) throws AccessException;
	
	/**
	 * 删除
	 * @param job
	 * @return
	 * @throws AccessException
	 */
	public int delete(Job job) throws AccessException;
	
}
