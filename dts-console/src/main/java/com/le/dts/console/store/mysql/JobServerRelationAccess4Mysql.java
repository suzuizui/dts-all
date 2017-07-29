package com.le.dts.console.store.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.console.store.JobServerRelationAccess;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.JobServerRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 * job和机器关系映射访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class JobServerRelationAccess4Mysql implements JobServerRelationAccess {

	
	@Autowired
	private SqlMapClients sqlMapClients;
	
	/**
	 * 插入
	 */
	@Override
	public long insert(JobServerRelation jobMachineRelation)
			throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("JobServerRelation.insert", jobMachineRelation);
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
		List<JobServerRelation> jobMachineRelationList = null;
		try {
			jobMachineRelationList = (List<JobServerRelation>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobServerRelation.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobMachineRelationList;
	}

	/**
	 * 根据JobId查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<JobServerRelation> queryByJobId(JobServerRelation query)
			throws AccessException {
		List<JobServerRelation> jobMachineRelationList = null;
		try {
			jobMachineRelationList = (List<JobServerRelation>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobServerRelation.queryByJobId", query);
		} catch (Throwable e) {
			throw new AccessException("[queryByJobId]: error", e);
		}
		return jobMachineRelationList;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(JobServerRelation jobMachineRelation)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobServerRelation.update", jobMachineRelation);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	@Override
	public int updateServer(String sourceServer, String targetServer)
			throws AccessException {
		
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("sourceServer", sourceServer);
		query.put("targetServer", targetServer);
		
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobServerRelation.updateServer", query);
		} catch (Throwable e) {
			throw new AccessException("[updateServer]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	@Override
	public int delete(JobServerRelation jobMachineRelation)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("JobServerRelation.delete", jobMachineRelation);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

}
