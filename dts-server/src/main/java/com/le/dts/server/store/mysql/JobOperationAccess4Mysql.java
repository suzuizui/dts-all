package com.le.dts.server.store.mysql;

import java.util.List;

import com.le.dts.common.domain.store.JobOperation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.JobOperationAccess;

/**
 * Job操作访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class JobOperationAccess4Mysql implements JobOperationAccess, ServerContext {

	/**
	 * 插入
	 */
	@Override
	public long insert(JobOperation jobOperation) throws AccessException {
		Long result = null;
        try {
            result = (Long)sqlMapClients.getSqlMapClientMeta()
                    .insert("JobOperation.insert", jobOperation);
        } catch (Throwable e) {
            throw new AccessException("[insert]: error", e);
        }
        if(null == result) {
            return 0L;
        }
        return result;
	}

	/**
	 * 根据server查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<JobOperation> queryByServer(JobOperation jobOperation)
			throws AccessException {
		List<JobOperation> jobOperationList = null;
        try {
            jobOperationList = (List<JobOperation>)sqlMapClients.getSqlMapClientMeta()
                    .queryForList("JobOperation.queryByServer", jobOperation);
        } catch (Throwable e) {
            throw new AccessException("[queryByServer]: error", e);
        }
        return jobOperationList;
	}

	/**
	 * 根据ID删除
	 */
	@Override
	public Integer deleteById(JobOperation jobOperation) throws AccessException {
		int result = 0;
        try {
            result = (Integer)sqlMapClients.getSqlMapClientMeta()
                    .delete("JobOperation.delete", jobOperation);
        } catch (Throwable e) {
            throw new AccessException("[delete]: error", e);
        }
        return result;
	}

}
