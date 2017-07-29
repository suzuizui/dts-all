package com.le.dts.console.store.mysql;

import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.FlowInstance;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.FlowInstanceAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 * 流程实例访问接口实现
 * @author tianyao.myc
 *
 */
public class FlowInstanceAccess4Mysql implements FlowInstanceAccess {

	@Autowired
	private SqlMapClients sqlMapClients;
	
	/**
	 * 插入
	 */
	@Override
	public long insert(FlowInstance flowInstance) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("FlowInstance.insert", flowInstance);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	/**
	 * 查询FlowInstance
	 */
	@Override
	public FlowInstance query(FlowInstance query) throws AccessException {
		FlowInstance flowInstance = null;
		try {
			flowInstance = (FlowInstance)sqlMapClients.getSqlMapClientMeta()
					.queryForObject("FlowInstance.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return flowInstance;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(FlowInstance flowInstance) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("FlowInstance.update", flowInstance);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

}
