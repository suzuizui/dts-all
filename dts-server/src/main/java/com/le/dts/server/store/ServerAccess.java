package com.le.dts.server.store;

import com.le.dts.common.domain.store.Server;
import com.le.dts.common.exception.AccessException;

/**
 * Server访问接口
 * @author tianyao.myc
 *
 */
public interface ServerAccess {

	/**
	 * 插入
	 * @param server
	 * @return
	 * @throws AccessException
	 */
	public long insert(Server server) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public Server query(Server query) throws AccessException;
	
	/**
	 * 计算Server数量
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public long countServers(Server query) throws AccessException;
	
	/**
	 * 更新
	 * @param server
	 * @return
	 * @throws AccessException
	 */
	public int update(Server server) throws AccessException;
	
	/**
	 * 删除
	 * @param server
	 * @return
	 * @throws AccessException
	 */
	public int delete(Server server) throws AccessException;
	
}
