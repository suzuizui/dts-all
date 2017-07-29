package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.ServerGroup;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.ServerGroupAccess;

/**
 * 服务端分组访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class ServerGroupAccess4Hbase implements ServerGroupAccess {

	/**
	 * 插入
	 */
	@Override
	public long insert(ServerGroup serverGroup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询
	 */
	@Override
	public List<ServerGroup> query(ServerGroup query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(ServerGroup serverGroup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	@Override
	public int delete(ServerGroup serverGroup) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

}
