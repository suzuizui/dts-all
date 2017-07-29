package com.le.dts.server.manager;

import java.util.List;

import com.le.dts.server.context.ServerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.JobServerRelation;

public class JobServerRelationManager implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(JobServerRelationManager.class);
	
	/**
	 * 获取备份服务器列表
	 * @param jobId
	 * @return
	 */
	public List<String> acquireBackupServerList(long jobId) {
		JobServerRelation query = new JobServerRelation();
		query.setJobId(jobId);
		List<String> serverList = null;
		try {
			serverList = store.getJobServerRelationAccess().queryBackupServerListJobId(query);
		} catch (Throwable e) {
			logger.error("[JobServerRelationManager]: queryBackupServerListJobId error, query:" + query, e);
		}
		return serverList;
	}
	
}
