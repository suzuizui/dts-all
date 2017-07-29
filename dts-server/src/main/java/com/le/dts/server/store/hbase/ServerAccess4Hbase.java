package com.le.dts.server.store.hbase;

import com.le.dts.common.domain.store.Server;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.ServerAccess;

/**
 * Server访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class ServerAccess4Hbase implements ServerAccess {

	/**
	 * 插入
	 */
	@Override
	public long insert(Server server) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询
	 */
	@Override
	public Server query(Server query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 计算Server数量
	 */
	@Override
	public long countServers(Server query) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(Server server) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	@Override
	public int delete(Server server) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

}
