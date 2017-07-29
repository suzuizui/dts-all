package com.le.dts.server.store.mysql;

import java.util.List;

import com.le.dts.common.domain.store.ServerGroup;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.ServerGroupAccess;

/**
 * 服务端分组访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class ServerGroupAccess4Mysql implements ServerGroupAccess, ServerContext {

	/**
	 * 插入
	 */
	@Override
	public long insert(ServerGroup serverGroup) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("ServerGroup.insert", serverGroup);
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
	public List<ServerGroup> query(ServerGroup query) throws AccessException {
		List<ServerGroup> serverGroupList = null;
		try {
			serverGroupList = (List<ServerGroup>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("ServerGroup.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return serverGroupList;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(ServerGroup serverGroup) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("ServerGroup.update", serverGroup);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	@Override
	public int delete(ServerGroup serverGroup) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("ServerGroup.delete", serverGroup);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

}
