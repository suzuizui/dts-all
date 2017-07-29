package com.le.dts.server.store.mysql;

import com.le.dts.common.domain.store.Server;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.ServerAccess;

/**
 * Server访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class ServerAccess4Mysql implements ServerAccess, ServerContext {

	/**
	 * 插入
	 */
	@Override
	public long insert(Server server) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("Server.insert", server);
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
	@Override
	public Server query(Server query) throws AccessException {
		Server server = null;
		try {
			server = (Server)sqlMapClients.getSqlMapClientMeta()
					.queryForObject("Server.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return server;
	}

	/**
	 * 计算Server数量
	 */
	@Override
	public long countServers(Server query) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.queryForObject("Server.countServers", query);
		} catch (Throwable e) {
			throw new AccessException("[countServers]: error", e);
		}
		if(null == result) {
			return 0;
		}
		return result;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(Server server) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("Server.update", server);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	@Override
	public int delete(Server server) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("Server.delete", server);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

}
