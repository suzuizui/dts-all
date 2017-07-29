package com.le.dts.server.store.mysql;

import java.util.List;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.ClientGroupAccess;
import org.springframework.dao.support.DataAccessUtils;

import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.exception.AccessException;

/**
 * 客户端集群信息访问接口 Mysql实现
 * 
 * @author tianyao.myc
 * 
 */
public class ClientGroupAccess4Mysql implements ClientGroupAccess,
		ServerContext {

	/**
	 * 插入
	 */
	public long insert(ClientGroup clientGroup) throws AccessException {
		Long result = null;
		try {
			result = (Long) sqlMapClients.getSqlMapClientMeta().insert(
					"ClientGroup.insert", clientGroup);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if (null == result) {
			return 0L;
		}
		return result;
	}

	/**
	 * 查询
	 */
	@SuppressWarnings("unchecked")
	public List<ClientGroup> query(ClientGroup query)
			throws AccessException {
		List<ClientGroup> clientGroupList = null;
		try {
			clientGroupList = (List<ClientGroup>) sqlMapClients
					.getSqlMapClientMeta().queryForList("ClientGroup.query",
							query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return clientGroupList;
	}

	@SuppressWarnings("unchecked")
	public List<ClientGroup> queryUser(ClientGroup query)
			throws AccessException {
		List<ClientGroup> clientGroupList = null;
		try {
			clientGroupList = (List<ClientGroup>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"ClientGroup.queryUser", query);
		} catch (Throwable e) {
			throw new AccessException("[queryUser]: error", e);
		}
		return clientGroupList;
	}

	@SuppressWarnings("unchecked")
	public List<ClientGroup> queryAll() throws AccessException {
		List<ClientGroup> clientGroupList = null;
		try {
			clientGroupList = (List<ClientGroup>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"ClientGroup.queryAll");
		} catch (Throwable e) {
			throw new AccessException("[queryAll]: error", e);
		}
		return clientGroupList;
	}

	/**
	 * 更新
	 */
	public int update(ClientGroup clientGroup) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta().update(
					"ClientGroup.update", clientGroup);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	public int delete(ClientGroup clientGroup) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta().delete(
					"ClientGroup.delete", clientGroup);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

	@Override
	public String checkUser(ClientGroup clientGroup) throws AccessException {
		try {
			@SuppressWarnings("unchecked")
			List<String> creates = (List<String>) sqlMapClients.getSqlMapClientMeta()
					.queryForList("ClientGroup.checkUser", clientGroup);
			return DataAccessUtils.singleResult(creates);
		} catch (Throwable e) {
			throw new AccessException("[checkUser]: error", e);
		}
	}

}
