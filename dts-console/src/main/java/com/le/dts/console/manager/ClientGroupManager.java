package com.le.dts.console.manager;

import java.util.List;

import com.le.dts.console.store.ClientGroupAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.exception.AccessException;

public class ClientGroupManager  {

	private static final Log logger = LogFactory.getLog(ClientGroupManager.class);
	
	// DAO
	@Autowired
	private ClientGroupAccess clientGroupAccess;

	@Autowired
	private SqlMapClients sqlMapClients;
	
	public Result<List<ClientGroup>> queryGroup(ClientGroup clientCluster) {
		Result<List<ClientGroup>> userGroups = new Result<List<ClientGroup>>();
		try {
			List<ClientGroup> groupList = clientGroupAccess.query(clientCluster);
			userGroups.setResultCode(ResultCode.SUCCESS);
			userGroups.setData(groupList);
		} catch (AccessException e) {
			logger.error("[clientGroupAccess]:error", e);
			userGroups.setResultCode(ResultCode.QUERY_USER_CERTAIN_GROUP_FAILURE);
		}
		return userGroups;
	}
	
	/**
	 * 创建用户组;
	 * @param clientCluster
	 * @return
	 */
	public Result<Long> createGroup(ClientGroup clientCluster) {
		Result<Long> result = new Result<Long>();
		try {
			long id = clientGroupAccess.insert(clientCluster);
			result.setResultCode(ResultCode.SUCCESS);
			result.setData(id);
		} catch (AccessException e) {
			logger.error("create group error!" + e);
			result.setResultCode(ResultCode.INSERT_USER_GROUP_FAILURE);
		}
		return result;
	}
	
	public Result<String> deleteGroup(ClientGroup clientCluster) {
		Result<String> result = new Result<String>();
		try {
			clientGroupAccess.delete(clientCluster);
			result.setResultCode(ResultCode.SUCCESS);
		} catch (AccessException e) {
			result.setResultCode(ResultCode.DELE_GROUP_ERROR);
		}
		return result;
	}
	
	/**
	 * 获取JobProcessor名称列表
	 * @param groupId
	 * @return
	 */
	public Result<List<String>> getJobProcessorNameList(String groupId) {
		Result<List<String>> result = new Result<List<String>>();
		return result;
	}
}
