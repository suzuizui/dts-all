package com.le.dts.console.store;

import java.util.List;

import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.exception.AccessException;

/**
 * Job依赖关系访问接口
 * @author 子柒;
 *
 */
public interface JobRelationAccess {

	/**
	 * 插入
	 * @param jobRelation
	 * @return
	 * @throws AccessException
	 */
	public long insert(JobRelation jobRelation) throws AccessException;

	/**
	 * 查询前置
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<JobRelation> queryBefore(JobRelation query) throws AccessException;

	/**
	 * 查询后置
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<JobRelation> queryAfter(JobRelation query) throws AccessException;

	/**
	 * 更新
	 * @param jobRelation
	 * @return
	 * @throws AccessException
	 */
	public int update(JobRelation jobRelation) throws AccessException;
	
	public int updateResetFinishCount(JobRelation jobRelation) throws AccessException;

	/**
	 * 删除
	 * @param jobRelation
	 * @return
	 * @throws AccessException
	 */
	public int delete(JobRelation jobRelation) throws AccessException;
	
	/**
	 * 删除所有关系
	 * @param jobRelation
	 * @return
	 * @throws AccessException
	 */
	public int deleteAllRelation(JobRelation jobRelation) throws AccessException;
	
}
