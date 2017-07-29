package com.le.dts.console.store.mysql;

import java.util.List;

import com.le.dts.console.store.ClusterAccess;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 * 服务端集群信息访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class ClusterAccess4Mysql implements ClusterAccess {

	
	@Autowired
	private SqlMapClients sqlMapClients;
	
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
		List<Cluster> serverClusterList = null;
		try {
			serverClusterList = (List<Cluster>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("Cluster.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return serverClusterList;
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
		List<Cluster> serverClusterList = null;
		try {
			serverClusterList = (List<Cluster>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("Cluster.queryAll");
		} catch (Throwable e) {
			throw new AccessException("[queryAll]: error", e);
		}
		return serverClusterList;
	}

}
