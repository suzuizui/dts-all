package com.le.dts.common.logger;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.exception.InitException;
import com.le.dts.common.logger.timer.LoggerCleanerTimer;
import com.le.dts.common.util.TimeUtil;

/**
 * 日志清理
 * @author tianyao.myc
 *
 */
public class LoggerCleaner {
	
	private static final Log logger = LogFactory.getLog(LoggerCleaner.class);
	
	public static final String SPLIT_LOGGER = ".log.";
	public static final String TIME_FORMAT_DAY 	= "yyyy-MM-dd";

	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(1, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					
					return new Thread(runnable, "LoggerCleaner-Thread");
				}
				
			});

	private String loggerPath;
	
	private long initialDelay = 24 * 60 * 60 * 1000L;
	
	private long period = 24 * 60 * 60 * 1000L;
	
	private int day = 7;
	
	public LoggerCleaner(String loggerPath) {
		this.loggerPath = loggerPath;
	}
	
	public LoggerCleaner(String loggerPath, int day) {
		this.loggerPath = loggerPath;
		this.day = day;
	}
	
	public LoggerCleaner(String loggerPath, long initialDelay, long period) {
		this.loggerPath = loggerPath;
		this.initialDelay = initialDelay;
		this.period = period;
	}
	
	public LoggerCleaner(String loggerPath, long initialDelay, long period, int day) {
		this.loggerPath = loggerPath;
		this.initialDelay = initialDelay;
		this.period = period;
		this.day = day;
	}
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		//初始化日志清理定时器
		initLoggerCleanerTimer();
	}
	
	/**
	 * 初始化日志清理定时器
	 * @throws InitException
	 */
	private void initLoggerCleanerTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new LoggerCleanerTimer(this), 
					this.initialDelay, this.period, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[LoggerCleaner]: initLoggerCleanerTimer error", e);
		}
	}
	
	/**
	 * 开始执行
	 */
	public void start() {
		
		final AtomicInteger counter = new AtomicInteger(0);
		
		//扫描日志目录
		scan(new File(this.loggerPath), counter);
		
		logger.info("[LoggerCleaner]: scan, counter:" + counter.get());
	}
	
	/**
	 * 扫描日志目录
	 * @param path
	 * @param counter
	 */
	@SuppressWarnings("deprecation")
	private void scan(File path, final AtomicInteger counter) {
		
		if(! path.exists()) {
			logger.warn("[LoggerCleaner]: scan path not exists, path:" + path.getPath());
			return ;
		}
		
		//得到文件列表信息  
        File[] files = path.listFiles();
        if(null == files || files.length <= 0) {
        	return ;
        }
		
        for(File file : files) {
        	
        	if(file.isDirectory()) {
        		
        		//递归扫描日志目录
        		scan(file, counter);
    		} else {
    			
    			try {
					String[] names = file.getName().split(SPLIT_LOGGER);
					if(null == names || names.length < 2) {
						continue ;
					}
					
					Date fileDate = TimeUtil.string2Date(names[1], TIME_FORMAT_DAY);
					if(null == fileDate) {
						continue ;
					}
					
					Date timeLine = TimeUtil.increaseDate(new Date(), Calendar.DAY_OF_MONTH, - this.day);
					
					if(fileDate.before(timeLine)) {
						
						boolean result = false;
						try {
							result = file.delete();
						} catch (Throwable e) {
							logger.error("[LoggerCleaner]: delete file error"
									+ ", file:" + file.getPath() 
									+ ", timeLine:" + timeLine.toLocaleString(), e);
						}
						
						logger.info("[LoggerCleaner]: delete"
								+ ", file:" + file.getPath() 
								+ ", timeLine:" + timeLine.toLocaleString() 
								+ ", result:" + result);
						
						counter.incrementAndGet();
					}
				} catch (Throwable e) {
					logger.error("[LoggerCleaner]: scan file error, file:" + file.getPath(), e);
				}
    		}
        }
        
	}
	
}
