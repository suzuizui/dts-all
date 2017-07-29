package com.le.dts.server.store;

import java.util.List;

import com.le.dts.common.domain.store.ServerGroup;
import com.le.dts.common.exception.AccessException;

/**
 * 服务端分组访问接口
 * @author tianyao.myc
 *
 */
public interface ServerGroupAccess {

	/**
	 * 插入
	 * @param serverGroup
	 * @return
	 * @throws AccessException
	 */
	public long insert(ServerGroup serverGroup) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<ServerGroup> query(ServerGroup query) throws AccessException;
	
	/**
	 * 更新
	 * @param serverGroup
	 * @return
	 * @throws AccessException
	 */
	public int update(ServerGroup serverGroup) throws AccessException;
	
	/**
	 * 删除
	 * @param serverGroup
	 * @return
	 * @throws AccessException
	 */
	public int delete(ServerGroup serverGroup) throws AccessException;
}
