package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.Job;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.JobAccess;

/**
 * Job信息访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class JobAccess4Hbase implements JobAccess {

	/**
	 * 插入
	 */
	public long insert(Job job) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询
	 */
	public List<Job> query(Job query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 查询分组所有Job
	 */
	@Override
	public List<Job> queryJobByGroupId(Job query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据id查询Job
	 */
	@Override
	public Job queryJobById(Job query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 更新
	 */
	public int update(Job job) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 更新JobStatus
	 */
	@Override
	public int updateJobStatus(Job job) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	public int delete(Job job) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int countJob(Job query) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

}
