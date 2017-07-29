package com.le.dts.server.store.mysql;

import java.util.List;

import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.ClusterAccess;

/**
 * 服务端集群信息访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class ClusterAccess4Mysql implements ClusterAccess, ServerContext {

	/**
	 * 插入
	 */
	public long insert(Cluster cluster) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("Cluster.insert", cluster);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	/**
	 * 查询
	 */
	@SuppressWarnings("unchecked")
	public List<Cluster> query(Cluster query)
			throws AccessException {
		List<Cluster> clusterList = null;
		try {
			clusterList = (List<Cluster>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("Cluster.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return clusterList;
	}

	/**
	 * 根据ID查询集群信息
	 */
	@Override
	public Cluster queryById(Cluster query) throws AccessException {
		Cluster cluster = null;
		try {
			cluster = (Cluster)sqlMapClients.getSqlMapClientMeta().queryForObject("Cluster.queryById", query);
		} catch (Throwable e) {
			throw new AccessException("[queryById]: error", e);
		}
		return cluster;
	}

	/**
	 * 更新
	 */
	public int update(Cluster cluster) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("Cluster.update", cluster);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	public int delete(Cluster cluster) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("Cluster.delete", cluster);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Cluster> queryAll() throws AccessException {
		List<Cluster> clusterList = null;
		try {
			clusterList = (List<Cluster>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("Cluster.queryAll");
		} catch (Throwable e) {
			throw new AccessException("[queryAll]: error", e);
		}
		return clusterList;
	}

}
