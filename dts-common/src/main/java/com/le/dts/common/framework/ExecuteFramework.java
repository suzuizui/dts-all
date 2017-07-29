package com.le.dts.common.framework;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.framework.executer.Executer;
import com.le.dts.common.framework.processor.ExecuteProcessor;
import com.le.dts.common.util.ListUtil;

/**
 * 执行框架
 * @author tianyao.myc
 *
 * @param <T>
 */
public class ExecuteFramework<T> implements Constants {

	//外部日志
	private final Log logger;
	
	//线程名称
	private String processorName;
	
	//执行线程数量
	private int executeThreads;
	
	//执行线程组
	@SuppressWarnings("rawtypes")
	private final ExecuteProcessor[] executeProcessors;
	
	//阻塞队列
	private final BlockingQueue<T> queue;
	
	//执行器
	private final Executer<T> executer;
	
	public ExecuteFramework(Log logger, Executer<T> executer) {
		
		this(logger, executer, "ExecuteProcessor-Thread", 4 * AVAILABLE_PROCESSORS, DEFAULT_PAGE_SIZE);
	}
	
	public ExecuteFramework(Log logger, Executer<T> executer, String processorName) {
		
		this(logger, executer, processorName, 4 * AVAILABLE_PROCESSORS, DEFAULT_PAGE_SIZE);
	}
	
	public ExecuteFramework(Log logger, Executer<T> executer, String processorName, int executeThreads, int queueSize) {
		
		if(null == executer) {
			throw new RuntimeException("[ExecuteFramework]: executer is null error");
		}
		
		this.logger = logger;
		this.executer = executer;
		this.processorName = processorName;
		this.executeThreads = executeThreads;
		this.executeProcessors = new ExecuteProcessor[executeThreads];
		this.queue = new LinkedBlockingQueue<T>(queueSize);
	}
	
	/**
	 * 执行列表
	 * @param list
	 * @return
	 */
	public boolean execute() {
		
		logger.info("[ExecuteFramework]: start... ");
		
		/** 初始化线程计数器 */
		final CountDownLatch threadCount = new CountDownLatch(this.executeThreads);
		
		/** 结束标记初始化 */
		final AtomicBoolean endTag = new AtomicBoolean(false);
		
		//初始化执行线程组
		initExecuteProcessors(threadCount, this.queue, endTag);
		
		//入队列线程
		try {
			new Thread(new Runnable() {
				
				public void run() {
					
					try {
						
						List<T> tList = executer.produce(null);
						
						while(! CollectionUtils.isEmpty(tList)) {
							
							for(T t : tList) {
								queue.put(t);
							}
							
							tList = executer.produce(ListUtil.acquireLastObject(tList));
						}
						
					} catch (Throwable e) {
						logger.error("[ExecuteFramework]: start put queue error", e);
					} finally {
						endTag.set(true);
						logger.info("[ExecuteFramework]: start put queue over");
					}
						
				}
				
			}).start();
		} catch (Throwable e) {
			logger.error("[ExecuteFramework]: new Thread error", e);
		}
		
		//启动执行线程组
		startExecuteProcessors();
		
		/** 等待任务分发完成 */
		try {
			threadCount.await();
		} catch (Throwable e) {
			logger.error("[ExecuteFramework]: threadCount await error", e);
		}
		
		logger.info("[ExecuteFramework]: end...");
		return true;
	}
	
	/**
	 * 初始化执行线程组
	 * @param threadCount
	 * @param queue
	 * @param endTag
	 */
	private void initExecuteProcessors(CountDownLatch threadCount, BlockingQueue<T> queue, AtomicBoolean endTag) {
		
		for(int i = 0 ; i < this.executeProcessors.length ; i ++) {
			
			//new执行线程
			this.executeProcessors[i] = new ExecuteProcessor<T>(this.logger, this.processorName, i, threadCount, queue, endTag, this);
		}
		
	}
	
	/**
	 * 启动执行线程组
	 */
	private void startExecuteProcessors() {
		
		for(int i = 0 ; i < this.executeProcessors.length ; i ++) {
			
			//启动线程
			this.executeProcessors[i].start();
		}
		
	}

	public Executer<T> getExecuter() {
		return executer;
	}
	
}
