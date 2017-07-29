package com.le.dts.server.store.mysql;

import java.util.List;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.WarningSetupAccess;
import org.springframework.dao.support.DataAccessUtils;

import com.le.dts.common.domain.store.WarningSetup;
import com.le.dts.common.exception.AccessException;

/**
 * 报警设置访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class WarningSetupAccess4Mysql implements WarningSetupAccess, ServerContext {

	/**
	 * 插入
	 */
	public long insert(WarningSetup warningSetup) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("WarningSetup.insert", warningSetup);
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
	public List<WarningSetup> query(WarningSetup query) throws AccessException {
		List<WarningSetup> warningSetupList = null;
		try {
			warningSetupList = (List<WarningSetup>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("WarningSetup.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return warningSetupList;
	}

	/**
	 * 更新
	 */
	public int update(WarningSetup warningSetup) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("WarningSetup.update", warningSetup);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	public int delete(WarningSetup warningSetup) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("WarningSetup.delete", warningSetup);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WarningSetup queryByJobId(long jobId) throws AccessException {
		List<WarningSetup> warningSetup = null;
		try {
			warningSetup = (List<WarningSetup>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("WarningSetup.queryByJobId", jobId);
		} catch (Throwable e) {
			throw new AccessException("[queryByJobId]: error", e);
		}
		return DataAccessUtils.singleResult(warningSetup);
	}

}
