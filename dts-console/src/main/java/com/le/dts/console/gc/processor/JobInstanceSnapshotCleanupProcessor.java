package com.le.dts.console.gc.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.le.dts.console.gc.JobInstanceSnapshotCleanup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 实例记录清理处理器
 * @author tianyao.myc
 *
 */
public class JobInstanceSnapshotCleanupProcessor extends Thread {

	private static final Log logger = LogFactory.getLog(JobInstanceSnapshotCleanupProcessor.class);
	
	/** 初始化线程计数器 */
	private final CountDownLatch threadCount;
	
	private final BlockingQueue<Long> queue;
	
	/** 结束标记初始化 */
	private final AtomicBoolean endTag;
	
	private JobInstanceSnapshotCleanup jobInstanceSnapshotCleanup;
	
	final AtomicLong instanceCounter = new AtomicLong(0L);
	
	public JobInstanceSnapshotCleanupProcessor(JobInstanceSnapshotCleanup jobInstanceSnapshotCleanup, 
			CountDownLatch threadCount, BlockingQueue<Long> queue, 
			AtomicBoolean endTag, int i) {
		this.jobInstanceSnapshotCleanup = jobInstanceSnapshotCleanup;
		this.threadCount = threadCount;
		this.queue = queue;
		this.endTag = endTag;
		super.setName("JobInstanceSnapshotCleanupProcessor-" + i);
	}
	
	@Override
	public void run() {
		try {
			while(! endTag.get() || ! queue.isEmpty()) {
				Long jobId = null;
				try {
					jobId = queue.poll(1L, TimeUnit.SECONDS);
					if(jobId != null) {
						this.jobInstanceSnapshotCleanup.handleJobId(jobId, this.instanceCounter);	
					}
				} catch (Throwable e) {
					logger.error("[JobInstanceSnapshotCleanupProcessor]: handleJobId error, jobId:" + jobId, e);
				}
			}
		} catch (Throwable e) {
			logger.error("[JobInstanceSnapshotCleanupProcessor]: run error", e);
		} finally {
			
			logger.info("[JobInstanceSnapshotCleanupProcessor]: handleJobId over"
					+ ", thread:" + Thread.currentThread().getName() 
					+ ", instanceCounter:" + this.instanceCounter.get());
			
			/** 线程计数减一 */
			threadCount.countDown();
		}
	}

}
