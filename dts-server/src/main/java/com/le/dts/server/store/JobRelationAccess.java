package com.le.dts.server.store;

import java.util.List;

import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.exception.AccessException;

/**
 * Job依赖关系访问接口
 * @author tianyao.myc
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
     * 查询关系;
     * @param query
     * @return
     * @throws AccessException
     */
    public List<JobRelation> queryRelation(JobRelation query) throws AccessException;
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

    /**
     * 更新
     * @param jobRelation
     * @return
     * @throws AccessException
     */
    public int updateFinishCount(JobRelation jobRelation) throws AccessException;

    /**
     * 复位设置
     * @param jobRelation
     * @return
     * @throws AccessException
     */
    public int resetFinishCount(JobRelation jobRelation) throws AccessException;
	
	/**
	 * 删除
	 * @param jobRelation
	 * @return
	 * @throws AccessException
	 */
	public int delete(JobRelation jobRelation) throws AccessException;
	
}
