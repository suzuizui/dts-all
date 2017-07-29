package com.le.dts.console.gc.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.le.dts.console.gc.GarbageCleanup;
import org.apache.commons.logging.Log;

import com.le.dts.common.domain.store.JobInstanceSnapshot;

/**
 * 垃圾清理处理器
 * @author tianyao.myc
 *
 */
public class GarbageCleanupProcessor extends Thread {

	private final Log logger;
	
	/** 初始化线程计数器 */
	private final CountDownLatch threadCount;
	
	private final BlockingQueue<JobInstanceSnapshot> queue;
	
	/** 结束标记初始化 */
	private final AtomicBoolean endTag;
	
	private final GarbageCleanup garbageCleanup;
	
	public GarbageCleanupProcessor(GarbageCleanup garbageCleanup, CountDownLatch threadCount, 
			BlockingQueue<JobInstanceSnapshot> queue, AtomicBoolean endTag, int i, Log logger) {
		this.garbageCleanup = garbageCleanup;
		this.threadCount = threadCount;
		this.queue = queue;
		this.endTag = endTag;
		this.logger = logger;
		super.setName("GarbageCleanupProcessor-" + i);
	}
	
	@Override
	public void run() {
		try {
			while(! endTag.get() || ! queue.isEmpty()) {
				JobInstanceSnapshot jobInstanceSnapshot = null;
				try {
					jobInstanceSnapshot = queue.poll(1L, TimeUnit.SECONDS);
					if(jobInstanceSnapshot != null) {
						garbageCleanup.handleInstance(jobInstanceSnapshot);	
					}
				} catch (Throwable e) {
					logger.error("[GarbageCleanupProcessor]: handleInstance error, jobInstanceSnapshot:" + jobInstanceSnapshot, e);
				}
			}
		} catch (Throwable e) {
			logger.error("[GarbageCleanupProcessor]: run error", e);
		} finally {
			logger.info("[GarbageCleanupProcessor]: handleInstance over, thread:" + Thread.currentThread().getName());
			/** 线程计数减一 */
			threadCount.countDown();
		}
	}

}
