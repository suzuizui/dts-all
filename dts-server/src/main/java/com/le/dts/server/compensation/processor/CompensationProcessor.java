package com.le.dts.server.compensation.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.le.dts.server.compensation.Compensation;
import org.apache.commons.logging.Log;

import com.le.dts.common.domain.store.JobInstanceSnapshot;

/**
 * 补偿线程
 * @author tianyao.myc
 *
 */
public class CompensationProcessor extends Thread {

	private final Log logger;
	
	/** 初始化线程计数器 */
	private final CountDownLatch threadCount;
	
	private final BlockingQueue<JobInstanceSnapshot> queue;
	
	/** 结束标记初始化 */
	private final AtomicBoolean endTag;
	
	private final Compensation compensation;
	
	public CompensationProcessor(Compensation compensation, CountDownLatch threadCount, 
			BlockingQueue<JobInstanceSnapshot> queue, AtomicBoolean endTag, int i, Log logger) {
		this.compensation = compensation;
		this.threadCount = threadCount;
		this.queue = queue;
		this.endTag = endTag;
		this.logger = logger;
		super.setName("CompensationProcessor-" + i);
	}
	
	@Override
	public void run() {
		try {
			while(! endTag.get() || ! queue.isEmpty()) {
				JobInstanceSnapshot jobInstanceSnapshot = null;
				try {
					jobInstanceSnapshot = queue.poll(1L, TimeUnit.SECONDS);
					if(jobInstanceSnapshot != null) {
						compensation.handleInstance(jobInstanceSnapshot);	
					}
				} catch (Throwable e) {
					logger.error("[CompensationProcessor]: handleInstance error, jobInstanceSnapshot:" + jobInstanceSnapshot, e);
				}
			}
		} catch (Throwable e) {
			logger.error("[CompensationProcessor]: run error", e);
		} finally {
			logger.info("[CompensationProcessor]: handleInstance over, thread:" + Thread.currentThread().getName());
			/** 线程计数减一 */
			threadCount.countDown();
		}
	}

}
