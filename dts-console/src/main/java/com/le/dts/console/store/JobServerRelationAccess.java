package com.le.dts.console.store;

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
	 * @param jobMachineRelation
	 * @return
	 * @throws AccessException
	 */
	public long insert(JobServerRelation jobMachineRelation) throws AccessException;
	
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
	 * 更新
	 * @param jobMachineRelation
	 * @return
	 * @throws AccessException
	 */
	public int update(JobServerRelation jobMachineRelation) throws AccessException;
	
	public int updateServer(String sourceServer, String targetServer) throws AccessException;
	
	/**
	 * 删除
	 * @param jobMachineRelation
	 * @return
	 * @throws AccessException
	 */
	public int delete(JobServerRelation jobMachineRelation) throws AccessException;
	
}
