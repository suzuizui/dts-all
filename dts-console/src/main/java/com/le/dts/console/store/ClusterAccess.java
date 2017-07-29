package com.le.dts.console.store;

import java.util.List;

import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.exception.AccessException;

/**
 * 服务端集群信息访问接口
 * @author tianyao.myc
 *
 */
public interface ClusterAccess {

	/**
	 * 插入
	 * @param cluster
	 * @return
	 * @throws AccessException
	 */
	public long insert(Cluster cluster) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<Cluster> query(Cluster query) throws AccessException;
	
	/**
	 * 查询所有
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<Cluster> queryAll() throws AccessException;
	
	/**
	 * 更新
	 * @param cluster
	 * @return
	 * @throws AccessException
	 */
	public int update(Cluster cluster) throws AccessException;
	
	/**
	 * 删除
	 * @param cluster
	 * @return
	 * @throws AccessException
	 */
	public int delete(Cluster cluster) throws AccessException;
	
}
