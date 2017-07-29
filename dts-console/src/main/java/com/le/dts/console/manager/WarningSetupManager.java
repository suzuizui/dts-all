package com.le.dts.console.manager;

import java.util.ArrayList;
import java.util.List;

import com.le.dts.console.store.WarningSetupAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.WarningSetup;
import com.le.dts.common.exception.AccessException;

public class WarningSetupManager {
	
	private static final Log logger = LogFactory.getLog(WarningSetupManager.class);
	
	@Autowired
	private WarningSetupAccess warningAccess;
	
	public Result<WarningSetup> queryWarningSetup(long jobId) {
		Result<WarningSetup> result = new Result<WarningSetup>();
		try {
			WarningSetup warningSetup = warningAccess.queryByJobId(jobId);
			if(warningSetup == null) {
				result.setResultCode(ResultCode.MONITOR_NOTSET);
			} else {
				result.setResultCode(ResultCode.SUCCESS);
				result.setData(warningSetup);
			}
		} catch (AccessException e) {
			logger.error("[WarningSetupManager]:query error");
			result.setResultCode(ResultCode.QUERY_MONITOR_ERROR);
		}
		return result;
	}
	
	public Result<List<WarningSetup>> queryWarningSetup(WarningSetup query) {
		Result<List<WarningSetup>> result = new Result<List<WarningSetup>>();
		List<WarningSetup> warningSetupList = new ArrayList<WarningSetup>();
		try {
			warningSetupList = warningAccess.query(query);
			result.setData(warningSetupList);
			result.setResultCode(ResultCode.SUCCESS);
		} catch (AccessException e) {
			logger.error("[WarningSetupManager]:query error!");
			result.setResultCode(ResultCode.QUERY_MONITOR_ERROR);
		}
		return result;
	}
	
	public Result<String> insertOrUpdateWarningSetup(WarningSetup query) {
		Result<String> result = new Result<String>();
		Result<WarningSetup> queryResult = queryWarningSetup(query.getJobId());
		if(queryResult.getResultCode() != ResultCode.SUCCESS && queryResult.getResultCode() != ResultCode.MONITOR_NOTSET) {
			result.setResultCode(ResultCode.MONITOR_UPDATE_ERROR);
			return result;
		}
		if(queryResult.getData() != null) { // UPDATE
			try {
				warningAccess.update(query);
				result.setResultCode(ResultCode.SUCCESS);
			} catch (AccessException e) {
				result.setResultCode(ResultCode.MONITOR_UPDATE_ERROR);
			}
		} else { // INSERT
			try {
				warningAccess.insert(query);
				result.setResultCode(ResultCode.SUCCESS);
			} catch (AccessException e) {
				result.setResultCode(ResultCode.MONITOR_UPDATE_ERROR);
			}
		}
		return result;
	}

}
