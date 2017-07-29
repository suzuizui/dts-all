package com.le.dts.server.manager;

import java.util.List;

import com.le.dts.server.context.ServerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.JobOperation;

/**
 * Job操作管理
 */
public class JobOperationManager implements ServerContext, Constants {

    private static final Log logger = LogFactory.getLog(JobOperationManager.class);
    
    /**
     * 根据server查询操作
     * @param server
     * @return
     */
    public List<JobOperation> queryByServer(String server) {
    	JobOperation query = new JobOperation();
    	query.setServer(server);
    	List<JobOperation> jobOperationList = null;
        try {
        	jobOperationList = store.getJobOperationAccess().queryByServer(query);
        } catch (Throwable e) {
            logger.error("[JobOperationManager]: queryByServer error, query:" + query, e);
        }
        return jobOperationList;
    }

    /**
     * 根据ID删除操作记录
     * @param jobOperation
     */
    public void deleteById(JobOperation jobOperation) {
    	int result = 0;
        try {
        	result = store.getJobOperationAccess().deleteById(jobOperation);
        } catch (Throwable e) {
        	logger.error("[JobOperationManager]: deleteById error, jobOperation:" + jobOperation, e);
        }
        if(result <= 0) {
        	logger.error("[JobOperationManager]: deleteById failed, jobOperation:" + jobOperation);
        }
    }
}
