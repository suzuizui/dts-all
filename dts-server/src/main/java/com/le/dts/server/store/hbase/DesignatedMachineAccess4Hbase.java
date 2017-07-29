package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.DesignatedMachine;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.DesignatedMachineAccess;

/**
 * 指定机器访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class DesignatedMachineAccess4Hbase implements DesignatedMachineAccess {

	/**
	 * 插入
	 */
	@Override
	public long insert(DesignatedMachine designatedMachine)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 根据JobId查询指定机器列表
	 */
	@Override
	public List<DesignatedMachine> queryByJobId(DesignatedMachine query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据Id查询指定机器列表
	 */
	@Override
	public List<DesignatedMachine> queryDesignatedMachineListById(
			DesignatedMachine query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据JobId删除指定机器列表
	 */
	@Override
	public int deleteByJobId(DesignatedMachine designatedMachine)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

}
