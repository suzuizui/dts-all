package com.le.dts.server.store;

import java.util.List;

import com.le.dts.common.domain.store.WarningSetup;
import com.le.dts.common.exception.AccessException;

/**
 * 报警设置访问接口
 * @author tianyao.myc
 *
 */
public interface WarningSetupAccess {

	/**
	 * 插入
	 * @param warningSetup
	 * @return
	 * @throws AccessException
	 */
	public long insert(WarningSetup warningSetup) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<WarningSetup> query(WarningSetup query) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public WarningSetup queryByJobId(long jobId) throws AccessException;
	
	/**
	 * 更新
	 * @param warningSetup
	 * @return
	 * @throws AccessException
	 */
	public int update(WarningSetup warningSetup) throws AccessException;
	
	/**
	 * 删除
	 * @param warningSetup
	 * @return
	 * @throws AccessException
	 */
	public int delete(WarningSetup warningSetup) throws AccessException;
	
}
