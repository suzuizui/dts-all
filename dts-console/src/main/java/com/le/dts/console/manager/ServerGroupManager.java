package com.le.dts.console.manager;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.ServerGroup;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.ServerGroupAccess;

public class ServerGroupManager {

	private static final Log logger = LogFactory.getLog(ServerGroupManager.class);
	
	@Autowired
	private ServerGroupAccess serverGroupAccess;
	
	public Result<List<ServerGroup>> queryClusterGroups(ServerGroup serverGroup) {
		Result<List<ServerGroup>> result = new Result<List<ServerGroup>>();
		
		try {
			List<ServerGroup> cluserGroup = serverGroupAccess.query(serverGroup);
			result.setData(cluserGroup);
			result.setResultCode(ResultCode.SUCCESS);
		} catch (AccessException e) {
			logger.error("[ServerGroupManager]:error", e);
			result.setResultCode(ResultCode.QUERY_CLUSTER_GROUP_ERROR);
			return result;
		}
		return result;
	}

    public Result<ServerGroup> queryServerGroup(ServerGroup serverGroup) {
        Result<ServerGroup> result = new Result<ServerGroup>();

        try {
            ServerGroup cluserGroup = serverGroupAccess.queryById(serverGroup);
            result.setData(cluserGroup);
            result.setResultCode(ResultCode.SUCCESS);
        } catch (AccessException e) {
            logger.error("[ServerGroupManager]:error", e);
            result.setResultCode(ResultCode.QUERY_CLUSTER_GROUP_ERROR);
            return result;
        }
        return result;
    }

}
