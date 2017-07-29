package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.JobServerRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.JobServerRelationAccess;

/**
 * job和机器关系映射访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class JobServerRelationAccess4Hbase implements JobServerRelationAccess {

	/**
	 * 插入
	 */
	@Override
	public long insert(JobServerRelation jobServerRelation)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询
	 */
	@Override
	public List<JobServerRelation> query(JobServerRelation query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据JobId查询
	 */
	@Override
	public List<JobServerRelation> queryByJobId(JobServerRelation query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取备份服务器列表
	 */
	@Override
	public List<String> queryBackupServerListJobId(JobServerRelation query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(JobServerRelation jobServerRelation)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	@Override
	public int delete(JobServerRelation jobServerRelation)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

}
