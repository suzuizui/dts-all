package com.le.dts.console.manager;

import java.util.List;

import com.le.dts.console.store.TaskSnapshotAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.ProgressBar;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.summary.TaskSummary;
import com.le.dts.common.summary.face.TaskList;

public class TaskSnapShotManager {

	private static final Log logger = LogFactory.getLog(TaskSnapShotManager.class);
	
	@Autowired
	private TaskSnapshotAccess taskSnapShotAccess;
	
	public Result<ProgressBar> queryJobSnapShotProgress(JobInstanceSnapshot jobSnapShot) {
		Result<ProgressBar> result = new Result<ProgressBar>();
		try {
			ProgressBar processBar = new ProgressBar();
//			processBar.setFailureAmount(taskSnapShotAccess.queryFailureAmout(jobSnapShot.getId()));
//			processBar.setFoundAmount(taskSnapShotAccess.queryFoundFailureAmout(jobSnapShot.getId()));
//			processBar.setInitAmount(taskSnapShotAccess.queryInitAmout(jobSnapShot.getId()));
//			processBar.setQueueAmount(taskSnapShotAccess.queryQueueAmout(jobSnapShot.getId()));
//			processBar.setStartAmount(taskSnapShotAccess.queryStartAmout(jobSnapShot.getId()));
//			processBar.setSuccessAmount(taskSnapShotAccess.querySuccessAmout(jobSnapShot.getId()));
//			processBar.setTotalAmount(processBar.getFailureAmount() + processBar.getFoundAmount() + processBar.getInitAmount()
//                + processBar.getQueueAmount() + processBar.getStartAmount() + processBar.getSuccessAmount());
			
			TaskSummary taskSummary = new TaskSummary(new TaskList() {

				@Override
				public List<TaskSnapshot> aquireTaskList(long jobInstanceId,
						long lastTaskId, int retryCount) throws Throwable {
					
					TaskSnapshot query = new TaskSnapshot();
					query.setJobInstanceId(jobInstanceId);
					query.setId(lastTaskId);
					
					return taskSnapShotAccess.aquireTaskList(query);
				}
				
			}, 200, 10L, 100);
			
			long startTime = System.currentTimeMillis();
			processBar = taskSummary.calculateTotalProgressBar(jobSnapShot);
			logger.info("[TaskSnapShotManager]: queryJobSnapShotProgress taskSummary"
					+ ", cost:" + (System.currentTimeMillis() - startTime) 
					+ ", jobId:" + jobSnapShot.getJobId());
			
			result.setData(processBar);
			result.setResultCode(ResultCode.SUCCESS);
		} catch (Throwable e) {
			logger.error("[TaskSnapShotManager]:query taskSnapShotAccess error!", e);
			result.setResultCode(ResultCode.QUERY_TASK_SNAPSHOT_PROGRESS_ERROR);
		}
		return result;
	}
	
	public Result<ProgressDetail> queryTaskSnapShotDetail(TaskSnapshot taskSnapShot) {
		Result<ProgressDetail> result = new Result<ProgressDetail>();
		ProgressDetail progressDetail = new ProgressDetail();
		JobInstanceSnapshot jobSnapShot = new JobInstanceSnapshot();
		jobSnapShot.setId(taskSnapShot.getJobInstanceId());
		Result<ProgressBar> overallProgressResult = queryJobSnapShotProgress(jobSnapShot);
		if(overallProgressResult.getResultCode() == ResultCode.QUERY_TASK_SNAPSHOT_PROGRESS_ERROR) {
			result.setResultCode(ResultCode.QUERY_TASK_SNAPSHOT_PROGRESS_ERROR);
			return result;
		}
//		progressDetail.setTotalProgressBar(overallProgressResult.getData());
		try {
//			List<String> layerTasks = taskSnapShotAccess.queryTaskLayer(taskSnapShot);
//			for(String layer: layerTasks) {
//				if(!StringUtil.equals(layer, Constants.DEFAULT_ROOT_LEVEL_TASK_NAME)) {
//					TaskSnapshot ts = new TaskSnapshot();
//					ts.setTaskName(layer);
//					ts.setJobInstanceId(taskSnapShot.getJobInstanceId());
//					ProgressBar processBar = new ProgressBar();
//					processBar.setFailureAmount(taskSnapShotAccess.queryLayerFailureAmout(ts));
//					processBar.setFoundAmount(taskSnapShotAccess.queryLayerFoundFailureAmout(ts));
//					processBar.setInitAmount(taskSnapShotAccess.queryLayerInitAmout(ts));
//					processBar.setQueueAmount(taskSnapShotAccess.queryLayerQueueAmout(ts));
//					processBar.setStartAmount(taskSnapShotAccess.queryLayerStartAmout(ts));
//					processBar.setSuccessAmount(taskSnapShotAccess.queryLayerSuccessAmout(ts));
//					processBar.setTotalAmount(taskSnapShotAccess.queryLayerTotalAmout(ts));
//					processBar.setName(layer);
//					progressDetail.getProgressBarList().add(processBar);
//				}
//			}
			
			TaskSummary taskSummary = new TaskSummary(new TaskList() {

				@Override
				public List<TaskSnapshot> aquireTaskList(long jobInstanceId,
						long lastTaskId, int retryCount) throws Throwable {
					
					TaskSnapshot query = new TaskSnapshot();
					query.setJobInstanceId(jobInstanceId);
					query.setId(lastTaskId);
					
					return taskSnapShotAccess.aquireTaskList(query);
				}
				
			}, 200, 10L, 100);
			
			Job job = new Job();
			
			long startTime = System.currentTimeMillis();
			progressDetail = taskSummary.calculateProgressDetail(job, jobSnapShot);
			logger.info("[TaskSnapShotManager]: queryTaskSnapShotDetail taskSummary"
					+ ", cost:" + (System.currentTimeMillis() - startTime) 
					+ ", jobInstanceId:" + taskSnapShot.getJobInstanceId());
			
			result.setResultCode(ResultCode.SUCCESS);
			result.setData(progressDetail);
		} catch (Throwable e) {
			result.setResultCode(ResultCode.QUERY_TASK_SNAPSHOT_DETAIL_PROGRESS_ERROR);
			return result;
		}
		return result;
	}
}
