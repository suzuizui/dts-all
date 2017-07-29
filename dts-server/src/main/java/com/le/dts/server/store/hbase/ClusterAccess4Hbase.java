package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.ClusterAccess;

/**
 * 服务端集群信息访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class ClusterAccess4Hbase implements ClusterAccess {

	/**
	 * 插入
	 */
	public long insert(Cluster cluster) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询
	 */
	public List<Cluster> query(Cluster query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据ID查询集群信息
	 */
	@Override
	public Cluster queryById(Cluster query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 更新
	 */
	public int update(Cluster cluster) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	public int delete(Cluster cluster) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Cluster> queryAll() throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
