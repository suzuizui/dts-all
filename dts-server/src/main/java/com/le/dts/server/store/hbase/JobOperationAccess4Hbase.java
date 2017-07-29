package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.JobOperation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.JobOperationAccess;

/**
 * Job操作访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class JobOperationAccess4Hbase implements JobOperationAccess {

	/**
	 * 插入
	 */
	@Override
	public long insert(JobOperation jobOperation) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 根据server查询
	 */
	@Override
	public List<JobOperation> queryByServer(JobOperation jobOperation)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据ID删除
	 */
	@Override
	public Integer deleteById(JobOperation jobOperation) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
