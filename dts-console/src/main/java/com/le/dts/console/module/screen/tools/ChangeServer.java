package com.le.dts.console.module.screen.tools;

import java.util.Collections;
import java.util.List;

import com.le.dts.console.store.JobServerRelationAccess;
import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.store.JobServerRelation;

public class ChangeServer {

	private static final Log logger = LogFactory.getLog(ChangeServer.class);
	
	@Autowired
	private JobServerRelationAccess jobServerRelationAccess;
	
	@Autowired
	private Zookeeper zookeeper;
	
	public void execute(Context context, @Param(name = "server") String server, 
			@Param(name = "clusterId") final String clusterId, 
			@Param(name = "serverGroupId") String serverGroupId) {
		
		JobServerRelation query = new JobServerRelation();
		query.setServer(server);
		
		List<JobServerRelation> jobMachineRelationList = null;
		try {
			jobMachineRelationList = jobServerRelationAccess.query(query);
		} catch (Throwable e) {
			logger.error("[ChangeServer]: query jobMachineRelationList error, server:" + server, e);
		}
		
		if(CollectionUtils.isEmpty(jobMachineRelationList)) {
			context.put("result", "jobMachineRelationList.size():0" );
			return ;
		}
		
		List<String> serverList = zookeeper.getServerGroupIpList(clusterId, serverGroupId);
        if(CollectionUtils.isEmpty(serverList)) {
			logger.warn("[ChangeServer]: getServerGroupIpList serverList is empty");
			return ;
		}
		
        int counter = 0;
        
		for(JobServerRelation relation : jobMachineRelationList) {
			
			JobServerRelation queryRelation = new JobServerRelation();
			queryRelation.setJobId(relation.getJobId());
			
			List<JobServerRelation> relationList = null;
			try {
				relationList = jobServerRelationAccess.queryByJobId(queryRelation);
			} catch (Throwable e) {
				logger.error("[ChangeServer]: queryByJobId error, jobId:" + relation.getJobId(), e);
			}
			
			if(CollectionUtils.isEmpty(relationList)) {
				logger.warn("[ChangeServer]: relationList is empty, relation:" + relation);
				continue ;
			}
			
			/** 随机列表顺序 */
			Collections.shuffle(serverList);
			
			String before = relation.toString();
			
			for(String tryServer : serverList) {
				
				if(in(relationList, tryServer)) {
					continue ;
				}
				
				relation.setServer(tryServer);
				int result = 0;
				try {
					result = jobServerRelationAccess.update(relation);
				} catch (Throwable e) {
					logger.error("[ChangeServer]: update error, tryServer:" + tryServer + ", relation:" + relation, e);
				}
			
				if(result > 0) {
					counter ++;
				}
				
				logger.info("[ChangeServer]: before:" + before + ", after:" + relation);
				
				break ;
			}
		}
		
		context.put("result", "jobMachineRelationList.size():" + jobMachineRelationList.size() + ", counter:" + counter);
	}
	
	private boolean in(List<JobServerRelation> relationList, String server) {
		
		for(JobServerRelation relation : relationList) {
			if(relation.getServer().equals(server)) {
				return true;
			}
		}
		
		return false;
	}
	
}
