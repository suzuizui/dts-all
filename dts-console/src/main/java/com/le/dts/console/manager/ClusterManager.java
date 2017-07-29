package com.le.dts.console.manager;

import java.util.List;

import com.le.dts.console.store.ClusterAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.exception.AccessException;

public class ClusterManager {

	private static final Log logger = LogFactory.getLog(ClusterManager.class);
	
	@Autowired
	private ClusterAccess serverClusterAccess;
	
	public Result<List<Cluster>> queryAllCluster() {
		
		Result<List<Cluster>> serverClusterResult = new Result<List<Cluster>>();
		try {
			List<Cluster> serverClusters = serverClusterAccess.queryAll();
			serverClusterResult.setResultCode(ResultCode.SUCCESS);
			serverClusterResult.setData(serverClusters);
		} catch (AccessException e) {
			logger.error("[ServerClusterManager]:query all server cluster error!", e);
			serverClusterResult.setResultCode(ResultCode.QUERY_ALL_CLUSTER_FAILURE);
			return serverClusterResult;
		}
		return serverClusterResult;
	}
}
