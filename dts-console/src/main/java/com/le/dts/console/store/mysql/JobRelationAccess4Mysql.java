package com.le.dts.console.store.mysql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.JobRelationAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 * Job依赖关系访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class JobRelationAccess4Mysql implements JobRelationAccess {

	@Autowired
	private SqlMapClients sqlMapClients;

	/**
	 * 插入
	 */
	public long insert(JobRelation jobRelation) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("JobRelation.insert", jobRelation);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	@Override
	public List<JobRelation> queryBefore(JobRelation query) throws AccessException {
		List<JobRelation> jobRelationList = null;
		try {
			jobRelationList = (List<JobRelation>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobRelation.queryBefore", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobRelationList;
	}

	@Override
	public List<JobRelation> queryAfter(JobRelation query) throws AccessException {
		List<JobRelation> jobRelationList = null;
		try {
			jobRelationList = (List<JobRelation>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobRelation.queryAfter", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobRelationList;
	}

	/**
	 * 更新
	 */
	public int update(JobRelation jobRelation) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobRelation.update", jobRelation);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	@Override
	public int updateResetFinishCount(JobRelation jobRelation)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobRelation.updateResetFinishCount", jobRelation);
		} catch (Throwable e) {
			throw new AccessException("[updateResetFinishCount]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	public int delete(JobRelation jobRelation) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("JobRelation.delete", jobRelation);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

	/**
	 * 删除所有关系
	 */
	@Override
	public int deleteAllRelation(JobRelation jobRelation)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("JobRelation.deleteAllRelation", jobRelation);
		} catch (Throwable e) {
			throw new AccessException("[deleteAllRelation]: error", e);
		}
		return result;
	}

}
