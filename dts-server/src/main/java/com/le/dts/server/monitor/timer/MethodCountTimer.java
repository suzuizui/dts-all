package com.le.dts.server.monitor.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.monitor.callback.Display;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.remoting.Pair;
import com.le.dts.common.util.LoggerUtil;
import com.le.dts.common.util.TimeUtil;

/**
 * 方法统计定时器
 * @author tianyao.myc
 *
 */
public class MethodCountTimer extends TimerTask implements Constants, ServerContext {

	private static final Log logger = LogFactory.getLog("methodCountTimer");

	//展现信息列表
	private final List<Display> displayList;
	
	private final ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>> methodCountTable;
	
	/** 分组方法统计映射表 */
	private final ConcurrentHashMap<String, Pair<AtomicLong/** 执行次数 */, AtomicLong/** 执行耗时 */>> groupIdMethodCountTable;
	
	public MethodCountTimer(List<Display> displayList, ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>> methodCountTable, 
			ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>> groupIdMethodCountTable) {
		this.displayList = displayList;
		this.methodCountTable = methodCountTable;
		this.groupIdMethodCountTable = groupIdMethodCountTable;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run() {
		StringBuilder counterLog = new StringBuilder("display start\n\n");
		
		if(! CollectionUtils.isEmpty(this.displayList)) {
			
			for(Display display : this.displayList) {
				counterLog.append(" " + display.content() + "\n");
			}
			
			counterLog.append("\n");
		}
		
		List<String> header = new ArrayList<String>();
		String key = "method name";header.add("invoke per second");header.add("response time (ms)");header.add("time");
		
		Map<String, List<String>> body = new HashMap<String, List<String>>();
		
		String date = TimeUtil.date2SecondsString(new Date());
		
		Iterator iterator = methodCountTable.entrySet().iterator();
		while (iterator.hasNext()) {
		    try {
				Map.Entry entry = (Map.Entry) iterator.next();
				String methodName = (String)entry.getKey();
				Pair<AtomicLong, AtomicLong> countPair = (Pair<AtomicLong, AtomicLong>)entry.getValue();
				
				AtomicLong counter = countPair.getObject1();
				AtomicLong totalTime = countPair.getObject2();
				
				List<String> itemList = new ArrayList<String>();
				itemList.add(counter.toString());
				long responseTime = 0L == counter.get() ? 0L : totalTime.get() / counter.get();
				itemList.add(String.valueOf(responseTime));
				itemList.add(date);
				body.put(methodName, itemList);
				
				//检查方法调用并发出报警信息
				serverMonitor.checkMethodAndAlertMsg(methodName, counter.get(), responseTime);
				
				counter.set(0L);//计数器清零
				totalTime.set(0L);//耗时清零
				
			} catch (Throwable e) {
				logger.error("[MethodCountTimer]: count error", e);
			}
		}
		
		counterLog.append(LoggerUtil.displayTable(key, header, body) + "\n\n");
		
		//打印信息
		logger.info(counterLog.toString() + printGroupIdMethodCount(date));
	}

	/**
	 * 打印分组方法监控信息
	 * @param date
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String printGroupIdMethodCount(String date) {
		
		StringBuilder counterLog = new StringBuilder();
		
		Iterator iterator = this.groupIdMethodCountTable.entrySet().iterator();
		while (iterator.hasNext()) {
		    try {
				Map.Entry entry = (Map.Entry) iterator.next();
				
				String groupIdMethod = (String)entry.getKey();
				Pair<AtomicLong, AtomicLong> countPair = (Pair<AtomicLong, AtomicLong>)entry.getValue();
				
				AtomicLong counter = countPair.getObject1();
				AtomicLong totalTime = countPair.getObject2();
				
				if(counter.get() > 100L) {
					counterLog.append(" time:" + date + ", gm:" + groupIdMethod + ", ips:" + counter.get() 
							+ ", rt:" + (0L == counter.get() ? 0L : totalTime.get() / counter.get()) + "\n");
				}
				
				counter.set(0L);//计数器清零
				totalTime.set(0L);//耗时清零
				
		    } catch (Throwable e) {
				logger.error("[MethodCountTimer]: count error", e);
			}
		}
		
		return counterLog.toString() + "\n\n\n";
	}
	
}
