package com.le.dts.server.store;

import java.util.List;

import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.exception.AccessException;

/**
 * 客户端集群信息访问接口
 * @author tianyao.myc
 *
 */
public interface ClientGroupAccess {

	/**
	 * 插入
	 * @param clientGroup
	 * @return
	 * @throws AccessException
	 */
	public long insert(ClientGroup clientGroup) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<ClientGroup> query(ClientGroup query) throws AccessException;
	
	/**
	 * 查询一个用户的组;
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<ClientGroup> queryUser(ClientGroup query) throws AccessException;
	
	/**
	 * 更新
	 * @param clientGroup
	 * @return
	 * @throws AccessException
	 */
	public int update(ClientGroup clientGroup) throws AccessException;
	
	/**
	 * 删除
	 * @param clientGroup
	 * @return
	 * @throws AccessException
	 */
	public int delete(ClientGroup clientGroup) throws AccessException;
	
	/**
	 * 判断用户是否存在;
	 * @param clientGroup
	 * @return
	 * @throws AccessException
	 */
	public String checkUser(ClientGroup clientGroup) throws AccessException;
	
}
