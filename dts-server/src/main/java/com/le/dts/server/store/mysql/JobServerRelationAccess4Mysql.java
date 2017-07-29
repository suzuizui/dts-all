package com.le.dts.server.store.mysql;

import java.util.List;

import com.le.dts.common.domain.store.JobServerRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.JobServerRelationAccess;

/**
 * job和机器关系映射访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class JobServerRelationAccess4Mysql implements JobServerRelationAccess, ServerContext {

	/**
	 * 插入
	 */
	@Override
	public long insert(JobServerRelation jobServerRelation)
			throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("JobServerRelation.insert", jobServerRelation);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	/**
	 * 查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<JobServerRelation> query(JobServerRelation query)
			throws AccessException {
		List<JobServerRelation> jobServerRelationList = null;
		try {
			jobServerRelationList = (List<JobServerRelation>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobServerRelation.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobServerRelationList;
	}

	/**
	 * 根据JobId查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<JobServerRelation> queryByJobId(JobServerRelation query)
			throws AccessException {
		List<JobServerRelation> jobServerRelationList = null;
		try {
			jobServerRelationList = (List<JobServerRelation>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobServerRelation.queryByJobId", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobServerRelationList;
	}

	/**
	 * 获取备份服务器列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> queryBackupServerListJobId(JobServerRelation query)
			throws AccessException {
		List<String> serverList = null;
		try {
			serverList = (List<String>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobServerRelation.queryBackupServerListJobId", query);
		} catch (Throwable e) {
			throw new AccessException("[queryBackupServerListJobId]: error", e);
		}
		return serverList;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(JobServerRelation jobServerRelation)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobServerRelation.update", jobServerRelation);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	@Override
	public int delete(JobServerRelation jobServerRelation)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("JobServerRelation.delete", jobServerRelation);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

}
