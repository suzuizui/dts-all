package com.le.dts.console.store.mysql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.ClientGroupAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 * 客户端集群信息访问接口 Mysql实现
 * 
 * @author tianyao.myc
 * 
 */
public class ClientGroupAccess4Mysql implements ClientGroupAccess {

	@Autowired
	private SqlMapClients sqlMapClients;
	
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

}
