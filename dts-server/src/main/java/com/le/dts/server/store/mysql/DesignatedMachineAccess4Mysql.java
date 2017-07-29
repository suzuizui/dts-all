package com.le.dts.server.store.mysql;

import java.util.List;

import com.le.dts.common.domain.store.DesignatedMachine;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.DesignatedMachineAccess;

/**
 * 指定机器访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class DesignatedMachineAccess4Mysql implements DesignatedMachineAccess, ServerContext {

	/**
	 * 插入
	 */
	@Override
	public long insert(DesignatedMachine designatedMachine)
			throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("DesignatedMachine.insert", designatedMachine);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	/**
	 * 根据JobId查询指定机器列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DesignatedMachine> queryByJobId(DesignatedMachine query)
			throws AccessException {
		List<DesignatedMachine> designatedMachineList = null;
		try {
			designatedMachineList = (List<DesignatedMachine>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("DesignatedMachine.queryByJobId", query);
		} catch (Throwable e) {
			throw new AccessException("[queryByJobId]: error", e);
		}
		return designatedMachineList;
	}

	/**
	 * 根据Id查询指定机器列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DesignatedMachine> queryDesignatedMachineListById(
			DesignatedMachine query) throws AccessException {
		List<DesignatedMachine> designatedMachineList = null;
		try {
			designatedMachineList = (List<DesignatedMachine>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("DesignatedMachine.queryDesignatedMachineListById", query);
		} catch (Throwable e) {
			throw new AccessException("[queryDesignatedMachineListById]: error", e);
		}
		return designatedMachineList;
	}

	/**
	 * 根据JobId删除指定机器列表
	 */
	@Override
	public int deleteByJobId(DesignatedMachine designatedMachine)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("DesignatedMachine.deleteByJobId", designatedMachine);
		} catch (Throwable e) {
			throw new AccessException("[deleteByJobId]: error", e);
		}
		return result;
	}

}
