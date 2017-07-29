package com.le.dts.common.summary.face;

import java.util.List;

import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;

/**
 * 任务列表接口
 * @author tianyao.myc
 *
 */
public interface TaskList {

	/**
	 * 获取任务列表
	 * @param jobInstanceId
	 * @param lastTaskId
	 * @param retryCount
	 * @return
	 * @throws Throwable
	 */
	public List<TaskSnapshot> aquireTaskList(long jobInstanceId, long lastTaskId, int retryCount) throws Throwable;
	
}
