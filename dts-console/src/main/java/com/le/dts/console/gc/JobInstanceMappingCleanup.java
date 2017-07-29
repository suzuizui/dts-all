package com.le.dts.console.gc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.le.dts.console.gc.timer.JobInstanceMappingCleanupTimer;
import com.le.dts.console.store.JobInstanceSnapshotAccess;
import com.le.dts.console.store.ServerJobInstanceMappingAccess;
import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.framework.ExecuteFramework;
import com.le.dts.common.framework.executer.Executer;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.util.TimeUtil;

/**
 * 清理
 * @author tianyao.myc
 *
 */
public class JobInstanceMappingCleanup implements Constants {

	private static final Log logger = LogFactory.getLog(JobInstanceMappingCleanup.class);
	
	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(1, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, "DTS-JobInstanceMappingCleanup-thread");
				}
				
			});
	
	@Autowired
    private Zookeeper zookeeper;
	
	@Autowired
	private ServerJobInstanceMappingAccess serverJobInstanceMappingAccess;
	
	@Autowired
	private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化JobInstanceMappingCleanup定时器 */
		initJobInstanceMappingCleanupTimer();
		
	}
	
	/**
	 * 初始化JobInstanceMappingCleanup定时器
	 * @throws InitException
	 */
	private void initJobInstanceMappingCleanupTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new JobInstanceMappingCleanupTimer(this),
					0L, 60 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[JobInstanceMappingCleanup]: initJobInstanceMappingCleanupTimer error", e);
		}
		logger.info("[JobInstanceMappingCleanup]: initJobInstanceMappingCleanupTimer success");
	}
	
	/**
	 * 开始清理
	 */
	public void start() {
		
		logger.info("[JobInstanceMappingCleanup]: start... ");
    	
    	List<String> consoleIpList = zookeeper.getConsoleIpList();
        if(CollectionUtils.isEmpty(consoleIpList)) {
            logger.error("[JobInstanceMappingCleanup]: start consoleIpList is empty");
            return ;
        }
        
        final int consoleAmount = consoleIpList.size();
		final int consoleNumber = consoleIpList.indexOf(RemotingUtil.getLocalAddress());
		
		if(consoleNumber < 0 || consoleNumber >= consoleAmount) {
			logger.error("[JobInstanceMappingCleanup]: start consoleNumber error"
					+ ", consoleAmount:" + consoleAmount 
					+ ", consoleNumber:" + consoleNumber 
					+ ", consoleIpList:" + consoleIpList);
			logger.info("[JobInstanceMappingCleanup]: end... \n\n\n\n");
			return ;
		}
		
		final AtomicInteger total = new AtomicInteger(0);
		final AtomicInteger delete = new AtomicInteger(0);
    	
		ExecuteFramework<ServerJobInstanceMapping> executeFramework = 
				new ExecuteFramework<ServerJobInstanceMapping>(logger, new Executer<ServerJobInstanceMapping>() {
					
			public List<ServerJobInstanceMapping> produce(ServerJobInstanceMapping jobInstanceMapping) {
				
				List<ServerJobInstanceMapping> instanceListResult = new ArrayList<ServerJobInstanceMapping>();
				
				List<ServerJobInstanceMapping> instanceList = loadAll(null == jobInstanceMapping ? 0L : jobInstanceMapping.getId());
				if(CollectionUtils.isEmpty(instanceList)) {
					return instanceListResult;
				}
				
				for(ServerJobInstanceMapping instance : instanceList) {
					
					// 取莫计算当前console处理的实例
					if(consoleNumber == (instance.getId() % consoleAmount)) {
						instanceListResult.add(instance);
					}
				}
				
				return instanceListResult;
			}

			public void consume(ServerJobInstanceMapping jobInstanceMapping) {
				
				total.incrementAndGet();
				
				JobInstanceSnapshot query = new JobInstanceSnapshot();
				query.setId(jobInstanceMapping.getJobInstanceId());
				
				JobInstanceSnapshot instance = null;
				try {
					instance = jobInstanceSnapshotAccess.query(query);
				} catch (Throwable e) {
					logger.error("[JobInstanceMappingCleanup]: query error, query:" + query, e);
					return ;
				}
				
				if(null == instance) {
					
					//删除Mapping
					deleteMapping(jobInstanceMapping, delete);
					return ;
				}
				
				if(JOB_INSTANCE_STATUS_DELETE_SELF == instance.getStatus()) {
					
					Date old = TimeUtil.increaseDate(new Date(), -(1000L * 60 * 60 * 24 * 7));
					
					if(instance.getGmtModified().before(old)) {
						//删除Mapping
						deleteMapping(jobInstanceMapping, delete);
					}
					
				}
			}
			
		}, "ExecuteFramework-JobInstanceMappingCleanup-Thread", 2, DEFAULT_PAGE_SIZE);
		
		//开始执行
		executeFramework.execute();
    	
		logger.info("[JobInstanceMappingCleanup]: total:" + total.get() + ", delete:" + delete.get() + " end... \n\n\n\n");
		
	}
	
	/**
	 * 删除Mapping
	 * @param jobInstanceMapping
	 * @param delete
	 */
	private void deleteMapping(ServerJobInstanceMapping jobInstanceMapping, AtomicInteger delete) {
		
		int result = 0;
		try {
			result = serverJobInstanceMappingAccess.deleteById(jobInstanceMapping);
		} catch (Throwable e) {
			logger.error("[JobInstanceMappingCleanup]: deleteById error, jobInstanceMapping:" + jobInstanceMapping, e);
		}
		
		if(result > 0) {
			delete.incrementAndGet();
		} else {
			logger.error("[JobInstanceMappingCleanup]: deleteById failed, jobInstanceMapping:" + jobInstanceMapping);
		}
		
	}
	
	/**
	 * 加载所有记录
	 * @param id
	 * @return
	 */
	private List<ServerJobInstanceMapping> loadAll(long id) {
		
		List<ServerJobInstanceMapping> mappingList = null;
		try {
			mappingList = serverJobInstanceMappingAccess.loadAll(id);
		} catch (Throwable e) {
			logger.error("[JobInstanceMappingCleanup]: loadAll error, id:" + id, e);
		}
		
		return mappingList;
	}
	
}
