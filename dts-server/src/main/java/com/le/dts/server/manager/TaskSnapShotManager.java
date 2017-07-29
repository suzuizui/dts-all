package com.le.dts.server.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.server.context.ServerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.server.store.TaskSnapshotAccess;

public class TaskSnapShotManager implements ServerContext, Constants {

    private static final Log logger = LogFactory.getLog(TaskSnapShotManager.class);

    public TaskSnapshotAccess getTaskSnapshotAccess() {
        return store.getTaskSnapshotAccess();
    }

    /**
     * 插入TaskSnapshot
     * @param taskSnapshot
     * @return
     */
    public long insertTaskSnapshot(TaskSnapshot taskSnapshot) {
    	TaskSnapshotAccess taskSnapshotAccess = getTaskSnapshotAccess();
    	long result = 0L;
    	try {
			result = taskSnapshotAccess.insert(taskSnapshot);
		} catch (Throwable e) {
			throw new RuntimeException("[TaskSnapShotManager]: insertTaskSnapshot error"
					+ ", taskSnapshot:" + taskSnapshot, e);
		}
    	return result;
    }

    public long queryTotalCount(long jobInstanceId) {
        TaskSnapshot query = new TaskSnapshot();
        query.setJobInstanceId(jobInstanceId);
        long result = 0L;
        try {
            result = getTaskSnapshotAccess().queryTotalCount(query);
        } catch (Throwable e) {
            logger.error("[TaskSnapShotManager]: queryTotalCount error, query:" + query, e);
        }
        return result;
    }

    public long queryItemCount(long jobInstanceId, int status) {
        TaskSnapshot query = new TaskSnapshot();
        query.setJobInstanceId(jobInstanceId);
        query.setStatus(status);
        long result = 0L;
        try {
            result = getTaskSnapshotAccess().queryItemCount(query);
        } catch (Throwable e) {
            logger.error("[TaskSnapShotManager]: queryItemCount error, query:" + query, e);
        }
        return result;
    }

    public List<String> queryTaskNameList(long jobInstanceId) {
        TaskSnapshot query = new TaskSnapshot();
        query.setJobInstanceId(jobInstanceId);
        List<String> taskNameList = null;
        try {
            taskNameList = getTaskSnapshotAccess().queryTaskNameList(query);
        } catch (Throwable e) {
            logger.error("[TaskSnapShotManager]: queryTaskNameList error, query:" + query, e);
        }
        return taskNameList;
    }

    public long queryDetailTotalCount(long jobInstanceId, String taskName) {
        TaskSnapshot query = new TaskSnapshot();
        query.setJobInstanceId(jobInstanceId);
        query.setTaskName(taskName);
        long result = 0L;
        try {
            result = getTaskSnapshotAccess().queryDetailTotalCount(query);
        } catch (Throwable e) {
            logger.error("[TaskSnapShotManager]: queryDetailTotalCount error, query:" + query, e);
        }
        return result;
    }

    public long queryDetailItemCount(long jobInstanceId, String taskName, int status) {
        TaskSnapshot query = new TaskSnapshot();
        query.setJobInstanceId(jobInstanceId);
        query.setTaskName(taskName);
        query.setStatus(status);
        long result = 0L;
        try {
            result = getTaskSnapshotAccess().queryDetailItemCount(query);
        } catch (Throwable e) {
            logger.error("[TaskSnapShotManager]: queryDetailItemCount error, query:" + query, e);
        }
        return result;
    }

    public List<TaskSnapshot> queryInitTask(long jobInstanceId) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("jobInstanceId", jobInstanceId);
        query.put("status", Constants.TASK_STATUS_INIT);
        try {
            return getTaskSnapshotAccess().queryByJobInstanceIdAndStatus4Unfinish(query);
        } catch (Throwable e) {
        	logger.error("[TaskSnapShotManager]: queryInitTask error, query:" + query, e);
        	return null;
        }
    }
}
