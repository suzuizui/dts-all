package com.le.dts.client.executor.parallel.processor;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.le.dts.client.executor.parallel.ParallelPool;
import com.le.dts.client.executor.parallel.unit.ExecutorUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.client.context.ClientContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.TaskSnapshot;

/**
 * 拉任务快照线程
 * @author tianyao.myc
 *
 */
public class PullProcessor extends Thread implements Constants, ClientContext {

	private static final Log logger = LogFactory.getLog(PullProcessor.class);
	
	/** 执行单元 */
	private ExecutorUnit executorUnit;
	
	/** 是否停止拉取线程 */
	private volatile boolean stop = false;
	
	public PullProcessor(ExecutorUnit executorUnit) {
		this.executorUnit = executorUnit;
		super.setName(PULL_TASK_THREAD_NAME + this.executorUnit.getExecutableTask().getJob().getId()
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJob().getJobProcessor() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getId() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getFireTime() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getRetryCount());
	}
	
	/**
	 * 刷新线程信息
	 * @param executorUnit
	 */
	public void refresh(ExecutorUnit executorUnit) {
		this.executorUnit = executorUnit;
		super.setName(PULL_TASK_THREAD_NAME + this.executorUnit.getExecutableTask().getJob().getId()
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJob().getJobProcessor() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getId() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getFireTime() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getRetryCount());
	}
	
	@Override
	public void run() {
		try {
			BlockingQueue<TaskSnapshot> queue = this.executorUnit.getQueue();
			while(! stop) {
				
				/** 拉取任务列表并放入队列 */
				try {
					pullAndPut(queue);
				} catch (Throwable e) {
					logger.error("[PullProcessor]: pullAndPut error"
							+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
				}
				
			}
		} catch (Throwable e) {
			logger.error("[PullProcessor]: run error"
					+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
		} finally {
			try {
				ParallelPool parallelPool = executorUnit.getParallelPool();
				parallelPool.stopTask(this.executorUnit.getExecutableTask().getJob().getId(), 
						this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId());
			} catch (Throwable e) {
				logger.error("[PullProcessor]: finally stopTask error"
						+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
			} finally {
				if(clientConfig.isFinishLog()) {
					logger.warn("[PullProcessor]: finally stopTask"
							+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId());
				}
			}
		}
	}
	
	/**
	 * 拉取任务列表并放入队列
	 * @param queue
	 */
	private void pullAndPut(BlockingQueue<TaskSnapshot> queue) {
		Result<ExecutableTask> pullResult = null;
		try {
			pullResult = executor.pull(this.executorUnit.getExecutableTask());
		} catch (Throwable e) {
			logger.error("[PullProcessor]: pull error"
					+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
		}
		if(null == pullResult) {
			logger.error("[PullProcessor]: pullResult is null"
					+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId());
			
			try {
				Thread.sleep(10 * 1000L);//拉到空数据就停歇一段时间 不然会给服务端造成很大压力
			} catch (Throwable e) {
				logger.error("[PullProcessor]: pullResult sleep error, executorUnit:" + executorUnit, e);
			}
			
			return ;
		}
		
		ExecutableTask executableTaskResult = pullResult.getData();
		if(null == executableTaskResult) {
			switch(pullResult.getResultCode()) {
			case PULL_TASK_LIST_OVER:
				try {
					Thread.sleep(clientConfig.getPullTaskListOverSleepTime());//拉到空数据就停歇一段时间 不然会给服务端造成很大压力
				} catch (Throwable e) {
					logger.error("[PullProcessor]: PULL_TASK_LIST_OVER sleep error"
							+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
				}
				break;
			case PULL_TASK_GET_LOCK_FAILURE:
				try {
					Thread.sleep(500L);//抢锁失败就停歇一段时间再抢 不然会给服务端造成很大压力
				} catch (Throwable e) {
					logger.error("[PullProcessor]: PULL_TASK_GET_LOCK_FAILURE sleep error"
							+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
				}
				break;
			case PULL_OVER:
				try {
					Thread.sleep(10 * 1000L);//拉到空数据就停歇一段时间 不然会给服务端造成很大压力
				} catch (Throwable e) {
					logger.error("[PullProcessor]: PULL_OVER sleep before error, executorUnit:" + executorUnit, e);
				}
				try {
					ParallelPool parallelPool = executorUnit.getParallelPool();
					parallelPool.stopTask(this.executorUnit.getExecutableTask().getJob().getId(), 
							this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId());
				} catch (Throwable e) {
					logger.error("[PullProcessor]: PULL_OVER error"
							+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
				} finally {
					if(clientConfig.isFinishLog()) {
						logger.warn("[PullProcessor]: PULL_OVER EXIT"
								+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId());
					}
				}
				try {
					Thread.sleep(10 * 1000L);//拉到空数据就停歇一段时间 不然会给服务端造成很大压力
				} catch (Throwable e) {
					logger.error("[PullProcessor]: PULL_OVER sleep after error, executorUnit:" + executorUnit, e);
				}
				break;
				default:
					logger.error("[PullProcessor]: executableTask is null"
							+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId() 
							+ ", pullResult:" + pullResult.toString());
			}
			return ;
		}
		
		List<TaskSnapshot> taskSnapshotList = executableTaskResult.getTaskSnapshotList();
		if(CollectionUtils.isEmpty(taskSnapshotList)) {
			logger.warn("[PullProcessor]: taskSnapshotList is empty error"
					+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId());
			return ;
		}
		
		for(TaskSnapshot taskSnapshot : taskSnapshotList) {
			try {
				queue.put(taskSnapshot);
			} catch (Throwable e) {
				logger.error("[PullProcessor]: put error"
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId(), e);
			}
		}
		
		try {
			Thread.sleep(500L);//成功拿到数据就停歇一段时间再抢 多给其他客户端一些机会 而且也不然会给服务端造成很大压力
		} catch (Throwable e) {
			logger.error("[PullProcessor]: PULL_TASK_SUCCESS sleep error"
					+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
		}
	}
	
	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
