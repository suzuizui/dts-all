package com.le.dts.common.framework.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.framework.ExecuteFramework;
import org.apache.commons.logging.Log;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.framework.ExecuteFramework;
import com.le.dts.common.framework.executer.Executer;

/**
 * 执行线程
 * @author tianyao.myc
 *
 * @param <T>
 */
public class ExecuteProcessor<T> extends Thread implements Constants {

	//外部日志
	private final Log logger;
	
	/** 初始化线程计数器 */
	private final CountDownLatch threadCount;
	
	private final BlockingQueue<T> queue;
	
	/** 结束标记初始化 */
	private final AtomicBoolean endTag;
	
	//执行框架
	private final ExecuteFramework<T> executeFramework;
	
	public ExecuteProcessor(Log logger, String processorName, int i, CountDownLatch threadCount, 
			BlockingQueue<T> queue, AtomicBoolean endTag, ExecuteFramework<T> executeFramework) {
		this.logger = logger;
		this.threadCount = threadCount;
		this.queue = queue;
		this.endTag = endTag;
		this.executeFramework = executeFramework;
		super.setName(processorName + HORIZONTAL_LINE + i);
	}
	
	@Override
	public void run() {

		try {
			Executer<T> executer = executeFramework.getExecuter();
			
			try {
				while(! endTag.get() || ! queue.isEmpty()) {
					T t = null;
					try {
						t = queue.poll(1L, TimeUnit.SECONDS);
						if(t != null) {
							executer.consume(t);
						}
					} catch (Throwable e) {
						logger.error("[ExecuteProcessor]: consume error, t:" + t, e);
					}
				}
			} catch (Throwable e) {
				logger.error("[ExecuteProcessor]: run error", e);
			} finally {
				
				logger.info("[ExecuteProcessor]: consume over, thread:" + Thread.currentThread().getName());
				
				//线程计数减一
				threadCount.countDown();
			}
		} catch (Throwable e) {
			logger.error("[ExecuteProcessor]: outter run error", e);
		}

	}

}
