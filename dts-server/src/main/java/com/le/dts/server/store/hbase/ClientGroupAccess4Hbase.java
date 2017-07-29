package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.ClientGroupAccess;

/**
 * 客户端集群信息访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class ClientGroupAccess4Hbase implements ClientGroupAccess {

	/**
	 * 插入
	 */
	public long insert(ClientGroup clientGroup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询
	 */
	public List<ClientGroup> query(ClientGroup query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 更新
	 */
	public int update(ClientGroup clientGroup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	public int delete(ClientGroup clientGroup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ClientGroup> queryUser(ClientGroup query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkUser(ClientGroup clientGroup) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
