package com.le.dts.server.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.monitor.callback.Display;
import com.le.dts.server.monitor.timer.WarningTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.domain.remoting.Pair;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.TimeUtil;
import com.le.dts.server.monitor.timer.MethodCountTimer;

/**
 * 服务器监控
 * @author tianyao.myc
 *
 */
public class ServerMonitor implements ServerContext {

	public static final Log logger = LogFactory.getLog(ServerMonitor.class);
	
	/** 方法统计映射表 */
	private final ConcurrentHashMap<String, Pair<AtomicLong/** 执行次数 */, AtomicLong/** 执行耗时 */>> methodCountTable = 
			new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>();
	
	/** 分组方法统计映射表 */
	private final ConcurrentHashMap<String, Pair<AtomicLong/** 执行次数 */, AtomicLong/** 执行耗时 */>> groupIdMethodCountTable = 
			new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>();
	
	private final ConcurrentHashMap<String, Alert> msgTable = 
			new ConcurrentHashMap<String, Alert>();
	
	//展现信息列表
	private final List<Display> displayList = new ArrayList<Display>();
	
	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(2, new ThreadFactory() {
				
				int index = 0;
				
				public Thread newThread(Runnable runnable) {
					
					index ++;
					
					return new Thread(runnable, "ServerMonitor-Thread-" + index);
				}
				
			});
	
	//报警管理
	private final InnerAlertManager alertManager = new InnerAlertManager();
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		//报警管理初始化
		this.alertManager.init();
		
		/** 初始化方法统计定时器 */
		initMethodCountTimer();
		
		//初始化报警定时器
		initWarningTimer();
		
	}
	
	/**
	 * 初始化方法统计定时器
	 * @throws InitException
	 */
	private void initMethodCountTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new MethodCountTimer(this.displayList, this.methodCountTable, this.groupIdMethodCountTable), 
					0L, 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[ServerMonitor]: initMethodCountTimer error", e);
		}
	}
	
	/**
	 * 初始化报警定时器
	 * @throws InitException
	 */
	private void initWarningTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new WarningTimer(this), 60 * 1000L, 60 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[ServerMonitor]: initWarningTimer error", e);
		}
	}
	
	/**
	 * 添加展现
	 * @param display
	 */
	public void addDisplay(Display display) {
		this.displayList.add(display);
	}
	
	/**
	 * 方法统计
	 * @param key
	 * @param startTime
	 */
	public void methodCount(String key, long startTime) {
		try {
			Pair<AtomicLong, AtomicLong> countPair = getCountPair(this.methodCountTable, key);
			/** 次数累加 */
			AtomicLong counter = countPair.getObject1();
			counter.incrementAndGet();
			/** 耗时累加 */
			AtomicLong totalTime = countPair.getObject2();
			totalTime.addAndGet(System.currentTimeMillis() - startTime);
		} catch (Throwable e) {
			logger.error("[ServerMonitor]: methodCount error, key:" + key + ", startTime:" + startTime);
		}
	}
	
	/**
	 * 方法统计
	 * @param key
	 * @param startTime
	 */
	public void groupIdMethodCount(String key, long startTime) {
		try {
			Pair<AtomicLong, AtomicLong> countPair = getCountPair(this.groupIdMethodCountTable, key);
			/** 次数累加 */
			AtomicLong counter = countPair.getObject1();
			counter.incrementAndGet();
			/** 耗时累加 */
			AtomicLong totalTime = countPair.getObject2();
			totalTime.addAndGet(System.currentTimeMillis() - startTime);
		} catch (Throwable e) {
			logger.error("[ServerMonitor]: groupIdMethodCount error, key:" + key + ", startTime:" + startTime);
		}
	}
	
	/**
	 * 多线程环境同一个key拿到同一个对象
	 * @param methodCountTable
	 * @param key
	 * @return
	 */
	private Pair<AtomicLong, AtomicLong> getCountPair(
			ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>> methodCountTable, String key) {
		Pair<AtomicLong, AtomicLong> countPair = methodCountTable.get(key);
		if(null == countPair) {
			countPair = new Pair<AtomicLong, AtomicLong>(new AtomicLong(0L), new AtomicLong(0L));
			Pair<AtomicLong, AtomicLong> existCountPair = methodCountTable.putIfAbsent(key, countPair);
			if(existCountPair != null) {
				countPair = existCountPair;
			}
		}
		return countPair;
	}
	
	/**
     * 发送旺旺消息
     * @param user
     * @param msg
     */
    public void sendWW(String user, String msg) {
    	if(serverConfig.getClusterId() != 1L && serverConfig.getClusterId() != 101L && serverConfig.getClusterId() != 201L) {
    		this.alertManager.sendWW(user, msg);
    	}
    }
    
    /**
     * 发送短信消息
     * @param key
     * @param user
     * @param msg
     */
    public void sendSMS(String key, String user, String msg) {
    	if((serverConfig.getClusterId() != 1L && serverConfig.getClusterId() != 101L && serverConfig.getClusterId() != 201L) 
    			&& ! TimeUtil.nowIsWorkDayTime(System.currentTimeMillis()) 
    			&& serverConfig.isWarningSwitch()) {
    		
    		Alert alert = this.msgTable.get(key);
    		if(null == alert) {
    			alert = new Alert();
    			this.msgTable.put(key, alert);
    		}
    		
    		alert.set(user, msg);
    		
    	}
    }
    

	/**
	 * 检查processorMap并发出报警信息
	 * @param size
	 */
	public void checkProcessorMapAndAlertMsg(int size) {
		if(size > 100) {
			sendWW("42910", processorMapAlertMsg(size));
		}
	}
	
	/**
	 * processorMap报警信息
	 * @param size
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String processorMapAlertMsg(int size) {
		return "[MethodCountTimer]: processorMapAlertMsg"
				+ ", clusterId:" + serverConfig.getClusterId() 
				+ ", localAddress:" + serverConfig.getLocalAddress() 
				+ ", size:" + size 
				+ ", time:" + new Date().toLocaleString();
	}
	
	/**
	 * 检查requestQueue并发出报警信息
	 * @param size
	 */
	public void checkRequestQueueAndAlertMsg(int size) {
		if(size > 30) {
			sendWW("42910", requestQueueAlertMsg(size));
			sendSMS("RequestQueue", "42910", requestQueueAlertMsg(size));
		}
	}
	
	/**
	 * requestQueue报警信息
	 * @param size
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String requestQueueAlertMsg(int size) {
		return "[MethodCountTimer]: requestQueueAlertMsg"
				+ ", clusterId:" + serverConfig.getClusterId() 
				+ ", localAddress:" + serverConfig.getLocalAddress() 
				+ ", size:" + size 
				+ ", time:" + new Date().toLocaleString();
	}
	
	/**
	 * 检查方法调用并发出报警信息
	 * @param methodName
	 * @param counter
	 * @param responseTime
	 */
	public void checkMethodAndAlertMsg(String methodName, long counter, long responseTime) {
		if("send".equals(methodName)) {
			if(counter > 4000L || responseTime > 10 * 1000L) {
				sendWW("42910", methodAlertMsg(methodName, counter, responseTime));
				sendSMS(methodName, "42910", methodAlertMsg(methodName, counter, responseTime));
			}
		} else if("callDependencyJob".equals(methodName)) {
			if(counter > 4000L || responseTime > 60 * 1000L) {
				sendWW("42910", methodAlertMsg(methodName, counter, responseTime));
				sendSMS(methodName, "42910", methodAlertMsg(methodName, counter, responseTime));
			}
		} else {
			if(counter > 4000L || responseTime > 5 * 1000L) {
				sendWW("42910", methodAlertMsg(methodName, counter, responseTime));
				sendSMS(methodName, "42910", methodAlertMsg(methodName, counter, responseTime));
			}
		}
	}
	
	/**
	 * 方法调用报警信息
	 * @param methodName
	 * @param counter
	 * @param responseTime
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String methodAlertMsg(String methodName, long counter, long responseTime) {
		return "[MethodCountTimer]: methodAlertMsg"
				+ ", clusterId:" + serverConfig.getClusterId() 
				+ ", localAddress:" + serverConfig.getLocalAddress() 
				+ ", methodName:" + methodName 
				+ ", counter:" + counter 
				+ ", responseTime:" + responseTime 
				+ ", time:" + new Date().toLocaleString();
	}
	
	public List<Display> getDisplayList() {
		return displayList;
	}

	public ConcurrentHashMap<String, Alert> getMsgTable() {
		return msgTable;
	}

	public InnerAlertManager getAlertManager() {
		return alertManager;
	}
	
	public class Alert {
		
		private String user;
		
		private String msg;
		
		private AtomicLong counter = new AtomicLong(0L);

		public void set(String user, String msg) {
			this.user = user;
			this.msg = msg;
			this.counter.incrementAndGet();
		}
		
		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public AtomicLong getCounter() {
			return counter;
		}

		public void setCounter(AtomicLong counter) {
			this.counter = counter;
		}
		
	}
	
}

