package com.le.dts.common.summary;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ProgressBar;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.summary.face.TaskList;
import com.le.dts.common.util.TimeUtil;
import jodd.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ProgressBar;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.summary.face.TaskList;
import com.le.dts.common.util.JobUtil;
import com.le.dts.common.util.ListUtil;
import com.le.dts.common.util.TimeUtil;

/**
 * 任务汇总
 * @author tianyao.myc
 *
 */
public class TaskSummary implements Constants {

	private static final Log logger = LogFactory.getLog(TaskSummary.class);
	
	//任务列表接口
	private final TaskList taskList;
	
	private int counter;
	
	private long time;
	
	private int maxRetryCount;
	
	public TaskSummary(TaskList taskList) {
		this(taskList, Integer.MAX_VALUE, 0L);
	}
	
	public TaskSummary(TaskList taskList, int counter, long time) {
		this(taskList, counter, time, 1);
	}
	
	public TaskSummary(TaskList taskList, int counter, long time, int maxRetryCount) {
		this.taskList = taskList;
		this.counter = counter;
		this.time = time;
		this.maxRetryCount = maxRetryCount;
	}
	
	/**
	 * 计算总体进度
	 * @param instance
	 * @return
	 */
	public ProgressBar calculateTotalProgressBar(JobInstanceSnapshot instance) {
		
		ProgressBar totalProgressBar = new ProgressBar();
		
		int counter = 0;
		
		long lastTaskId = 0L;
		List<TaskSnapshot> taskList = aquireTaskList(instance, lastTaskId);
		
		while(! CollectionUtils.isEmpty(taskList)) {
			
			//计算进度条
			calculateProgressBar(totalProgressBar, taskList);
			
			//获取最后一个任务的ID
			lastTaskId = ListUtil.acquireLastObject(taskList).getId();
			
			if(counter > this.counter) {
				try {
					Thread.sleep(this.time);
				} catch (Throwable e) {
					logger.error("[TaskSummary]: aquireTaskList sleep error"
							+ ", instance:" + instance + ", lastTaskId:" + lastTaskId, e);
				}
			}
			
			//获取下一页数据
			taskList = aquireTaskList(instance, lastTaskId);
			
			counter ++;//计数器递增
		}
		
		return totalProgressBar;
	}
	
	/**
	 * 计算进度条
	 * @param totalProgressBar
	 * @param taskList
	 */
	private void calculateProgressBar(ProgressBar totalProgressBar, List<TaskSnapshot> taskList) {
		
		for(TaskSnapshot taskSnapshot : taskList) {
			
			//总体进度条递增
			increaseCounter(totalProgressBar, taskSnapshot);
			
		}
		
	}
	
	/**
	 * 计算进度
	 * @param instance
	 * @return
	 */
	public ProgressDetail calculateProgressDetail(JobInstanceSnapshot instance) {
		return calculateProgressDetail(0, instance);
	}
	
	/**
	 * 计算进度
	 * @param jobType
	 * @param instance
	 * @return
	 */
	public ProgressDetail calculateProgressDetail(int jobType, JobInstanceSnapshot instance) {
		
		Job job = new Job();
		job.setType(jobType);
		
		return calculateProgressDetail(job, instance);
	}
	
	/**
	 * 计算进度
	 * @param job
	 * @param instance
	 * @return
	 */
	public ProgressDetail calculateProgressDetail(Job job, JobInstanceSnapshot instance) {
		
		ProgressDetail progressDetail = new ProgressDetail();
		
		progressDetail.setType(job.getType());//设置job类型
		progressDetail.setFireTime(JobUtil.acquireFireTime(instance.getFireTime()));//设置触发时间
		progressDetail.setFinishTime(TimeUtil.date2SecondsString(new Date()));//设置结束时间
    	progressDetail.setDescription(instance.getDescription());//设置描述
		
    	//初始化总体汇总信息
    	ProgressBar totalProgressBar = new ProgressBar();
    	totalProgressBar.setName(TOTAL_PROGRESS);
    	totalProgressBar.setInstanceId(String.valueOf(instance.getId()));
    	
    	//任务名称维度进行汇总统计的Map
    	Map<String, ProgressBar> progressBarMap = new HashMap<String, ProgressBar>();
    	
    	//按机器维度统计结果
    	Map<String, ProgressBar> machineProgressBarMap = progressDetail.getMachineProgressBarMap();
    	
    	//计算汇总进度
    	calculateProgress(instance, totalProgressBar, progressBarMap, machineProgressBarMap);
    	
    	progressDetail.setTotalProgressBar(totalProgressBar);//设置总体汇总信息
    	progressDetail.add(new ArrayList<ProgressBar>(progressBarMap.values()));//设置进度条列表
    	
		return progressDetail;
	}
	
	/**
	 * 计算汇总进度
	 * @param instance
	 * @param totalProgressBar
	 * @param progressBarMap
	 * @param machineProgressBarMap
	 */
	private void calculateProgress(JobInstanceSnapshot instance, ProgressBar totalProgressBar, 
			Map<String, ProgressBar> progressBarMap, Map<String, ProgressBar> machineProgressBarMap) {
		
		int counter = 0;
		
		long lastTaskId = 0L;
		List<TaskSnapshot> taskList = aquireTaskList(instance, lastTaskId);
		
		while(! CollectionUtils.isEmpty(taskList)) {
			
			//计算进度条
			calculateProgressBar(instance, totalProgressBar, progressBarMap, taskList, machineProgressBarMap);
			
			//获取最后一个任务的ID
			lastTaskId = ListUtil.acquireLastObject(taskList).getId();
			
			if(counter > this.counter) {
				try {
					Thread.sleep(this.time);
				} catch (Throwable e) {
					logger.error("[TaskSummary]: aquireTaskList sleep error"
							+ ", instance:" + instance + ", lastTaskId:" + lastTaskId, e);
				}
			}
			
			//获取下一页数据
			taskList = aquireTaskList(instance, lastTaskId);
			
			counter ++;//计数器递增
		}
		
	}
	
	/**
	 * 获取任务列表
	 * @param instance
	 * @param lastTaskId
	 * @return
	 */
	private List<TaskSnapshot> aquireTaskList(JobInstanceSnapshot instance, long lastTaskId) {
		
		boolean retry = false;
		int retryCount = 0;
		
		List<TaskSnapshot> taskList = null;
		try {
			taskList = this.taskList.aquireTaskList(instance.getId(), lastTaskId, retryCount);
		} catch (Throwable e) {
			
			retry = true;
			
			logger.error("[TaskSummary]: calculateProgress aquireTaskList error"
					+ ", instance:" + instance 
					+ ", lastTaskId:" + lastTaskId 
					+ ", retryCount:" + retryCount 
					+ ", thread:" + Thread.currentThread().getName(), e);
			
		}
		
		while(retry && retryCount < this.maxRetryCount) {
			
			retryCount ++;//重试次数累计
			
			try {
				
				Thread.sleep(100L);//休眠一会儿重试
				
				taskList = this.taskList.aquireTaskList(instance.getId(), lastTaskId, retryCount);
				
				retry = false;
				
			} catch (Throwable e) {

				retry = true;
				
				logger.error("[TaskSummary]: calculateProgress aquireTaskList error"
						+ ", instance:" + instance 
						+ ", lastTaskId:" + lastTaskId 
						+ ", retryCount:" + retryCount 
						+ ", thread:" + Thread.currentThread().getName(), e);
				
			}
			
		}
		
		return taskList;
	}
	
	/**
	 * 计算进度条
	 * @param instance
	 * @param totalProgressBar
	 * @param progressBarMap
	 * @param taskList
	 * @param machineProgressBarMap
	 */
	private void calculateProgressBar(JobInstanceSnapshot instance, ProgressBar totalProgressBar, 
			Map<String, ProgressBar> progressBarMap, List<TaskSnapshot> taskList, Map<String, ProgressBar> machineProgressBarMap) {
		
		for(TaskSnapshot taskSnapshot : taskList) {
			
			//总体进度条递增
			increaseCounter(totalProgressBar, taskSnapshot);
			
			ProgressBar progressBar = progressBarMap.get(taskSnapshot.getTaskName());
			if(null == progressBar) {
				progressBar = new ProgressBar();
				progressBar.setName(taskSnapshot.getTaskName());
				progressBar.setInstanceId(String.valueOf(instance.getId()));
				progressBarMap.put(taskSnapshot.getTaskName(), progressBar);
			}
			
			//各级任务进度条递增
			increaseCounter(progressBar, taskSnapshot);
			
			String clientId = taskSnapshot.getClientId();
			if(StringUtil.isBlank(clientId)) {
				continue ;//如果clientId为空就继续下一次循环
			}
			
			String[] clientIdComponent = clientId.split(COLON);//拆分clientId
			String ip = clientIdComponent.length > 1 ? clientIdComponent[1] : clientIdComponent[0];
			
			ProgressBar machineProgressBar = machineProgressBarMap.get(ip);
			if(null == machineProgressBar) {
				machineProgressBar = new ProgressBar();
				machineProgressBarMap.put(ip, machineProgressBar);
			}
			
			//机器维度进度条递增
			increaseCounter(machineProgressBar, taskSnapshot);
		}
		
	}
	
	/**
	 * 计数器累加
	 * @param progressBar
	 * @param taskSnapshot
	 */
	private void increaseCounter(ProgressBar progressBar, TaskSnapshot taskSnapshot) {
	
		//任务总数递增
		progressBar.setTotalAmount(progressBar.getTotalAmount() + 1L);
		
		switch(taskSnapshot.getStatus()) {
		case TASK_STATUS_INIT:
			
			//初始化状态的递增
			progressBar.setInitAmount(progressBar.getInitAmount() + 1L);
			
			break ;
		case TASK_STATUS_QUEUE:
			
			//入队列状态的递增
			progressBar.setQueueAmount(progressBar.getQueueAmount() + 1L);
			
			break ;
		case TASK_STATUS_START:
			
			//开始状态的递增
			progressBar.setStartAmount(progressBar.getStartAmount() + 1L);
			
			break ;
		case TASK_STATUS_SUCCESS:
			
			//成功状态的递增
			progressBar.setSuccessAmount(progressBar.getSuccessAmount() + 1L);
			
			break ;
		case TASK_STATUS_FAILURE:
			
			//失败状态的递增
			progressBar.setFailureAmount(progressBar.getFailureAmount() + 1L);
			
			break ;
		case TASK_STATUS_FOUND_PROCESSOR_FAILURE:
			
			//未找到状态的递增
			progressBar.setFoundAmount(progressBar.getFoundAmount() + 1L);
			
			break ;
			default:
		}
		
	}
	
}
