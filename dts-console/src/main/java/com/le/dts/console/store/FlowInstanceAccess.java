package com.le.dts.console.store;

import com.le.dts.common.domain.store.FlowInstance;
import com.le.dts.common.exception.AccessException;

/**
 * 流程实例访问接口
 * @author tianyao.myc
 *
 */
public interface FlowInstanceAccess {

	/**
	 * 插入
	 * @param flowInstance
	 * @return
	 * @throws AccessException
	 */
	public long insert(FlowInstance flowInstance) throws AccessException;
	
	/**
	 * 查询FlowInstance
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public FlowInstance query(FlowInstance query) throws AccessException;
	
	/**
	 * 更新
	 * @param flowInstance
	 * @return
	 * @throws AccessException
	 */
	public int update(FlowInstance flowInstance) throws AccessException;
	
}
