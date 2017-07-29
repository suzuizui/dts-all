package com.le.dts.server.store;

import java.util.List;

import com.le.dts.common.domain.store.JobServerRelation;
import com.le.dts.common.exception.AccessException;

/**
 * job和机器关系映射访问接口
 * @author tianyao.myc
 *
 */
public interface JobServerRelationAccess {

	/**
	 * 插入
	 * @param jobServerRelation
	 * @return
	 * @throws AccessException
	 */
	public long insert(JobServerRelation jobServerRelation) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<JobServerRelation> query(JobServerRelation query) throws AccessException;
	
	/**
	 * 根据JobId查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<JobServerRelation> queryByJobId(JobServerRelation query) throws AccessException;
	
	/**
	 * 获取备份服务器列表
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<String> queryBackupServerListJobId(JobServerRelation query) throws AccessException;
	
	/**
	 * 更新
	 * @param jobServerRelation
	 * @return
	 * @throws AccessException
	 */
	public int update(JobServerRelation jobServerRelation) throws AccessException;
	
	/**
	 * 删除
	 * @param jobServerRelation
	 * @return
	 * @throws AccessException
	 */
	public int delete(JobServerRelation jobServerRelation) throws AccessException;
	
}
