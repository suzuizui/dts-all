package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.WarningSetup;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.WarningSetupAccess;

/**
 * 报警设置访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class WarningSetupAccess4Hbase implements WarningSetupAccess {

	/**
	 * 插入
	 */
	public long insert(WarningSetup warningSetup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询
	 */
	public List<WarningSetup> query(WarningSetup query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 更新
	 */
	public int update(WarningSetup warningSetup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	public int delete(WarningSetup warningSetup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public WarningSetup queryByJobId(long jobId) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
