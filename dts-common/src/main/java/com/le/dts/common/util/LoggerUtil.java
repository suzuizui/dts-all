package com.le.dts.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.le.dts.common.domain.store.ServerJobInstanceMapping.JobInstanceKey;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;

/**
 * 日志相关工具
 * @author tianyao.myc
 *
 */
public class LoggerUtil {

	/**
	 * 展现JobInstanceId
	 * @param keySet
	 * @return
	 */
	public static String displayJobInstanceId(Set<ServerJobInstanceMapping.JobInstanceKey> keySet) {
		
		if(null == keySet || keySet.size() <= 0) {
			return null;
		}
		
		StringBuilder keys = new StringBuilder();
		
		List<ServerJobInstanceMapping.JobInstanceKey> keyList = new ArrayList<ServerJobInstanceMapping.JobInstanceKey>(keySet);
		
		for(ServerJobInstanceMapping.JobInstanceKey key : keyList) {
			keys.append("," + key.getJobInstanceId());
		}
		
		return keys.toString();
	}
	
	/**
	 * 展现表格
	 * @param header
	 * @param body
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String displayTable(String key, List<String> header, Map<String, List<String>> body) {
		StringBuilder table = new StringBuilder();
		
		List<AtomicInteger> maxLengthList = getMaxLengthList(key, header, body);//最大item长度列表
		
		//打印开始隔行
		List<String> head = new ArrayList<String>();
		for(int i = 0 ; i < header.size() ; i ++) {
			head.add("-");
		}
		table.append(displayLine(false, "-", head, maxLengthList));
		
		//打印表头行
		table.append(displayLine(true, key, header, maxLengthList));
		
		
		//打印表头隔行
		List<String> interlaced = new ArrayList<String>();
		for(int i = 0 ; i < header.size() ; i ++) {
			interlaced.add("-");
		}
		table.append(displayLine(false, "-", interlaced, maxLengthList));
		
		//打印body
		Iterator iterator = body.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String topic = (String)entry.getKey();
			List<String> itemList = (List<String>)entry.getValue();
			
			table.append(displayLine(true, topic, itemList, maxLengthList));//打印表内容行
		}
		
		//打印表尾隔行
		List<String> tail = new ArrayList<String>();
		for(int i = 0 ; i < header.size() ; i ++) {
			tail.add("-");
		}
		table.append(displayLine(false, "-", tail, maxLengthList));
		
		return table.toString();
	}

	/**
	 * 打印一行
	 * @param isLine
	 * @param key
	 * @param itemList
	 * @param maxLengthList
	 * @return
	 */
	private static String displayLine(boolean isLine, String key, List<String> itemList, List<AtomicInteger> maxLengthList) {
		StringBuilder line = new StringBuilder();

		line.append((isLine ? " | " : " + ") + key + displayBlank(isLine, maxLengthList.get(0), key));
		for(int i = 0 ; i < itemList.size() ; i ++) {
			line.append((isLine ? " | " : " + ") + itemList.get(i) + displayBlank(isLine, maxLengthList.get(i + 1), itemList.get(i)));
		}
		line.append((isLine ? " | " : " + ") + "\n");

		return line.toString();
	}
	
	/**
	 * 展现空格
	 * @param isLine
	 * @param length
	 * @param item
	 * @return
	 */
	private static String displayBlank(boolean isLine, AtomicInteger length, String item) {
		StringBuilder blank = new StringBuilder(isLine ? " " : "-");
		for(int i = 0 ; i < length.get() - item.length() ; i ++) {
			blank.append(isLine ? " " : "-");
		}
		return blank.toString();
	}
	
	/**
	 * 获取最大item长度列表
	 * @param key
	 * @param header
	 * @param body
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<AtomicInteger> getMaxLengthList(String key, List<String> header, Map<String, List<String>> body) {
		
		List<AtomicInteger> maxLengthList = new ArrayList<AtomicInteger>();//最大item长度列表
		for(int i = 0 ; i < 1 + header.size() ; i ++) {
			maxLengthList.add(new AtomicInteger(0));
		}
		
		refreshMaxLengthList(maxLengthList, key, header);//刷新header各个字段长度
		
		Iterator iterator = body.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String topic = (String)entry.getKey();
			List<String> itemList = (List<String>)entry.getValue();
			
			refreshMaxLengthList(maxLengthList, topic, itemList);//刷新body各个字段长度
		}
		
		return maxLengthList;
	}
	
	/**
	 * 刷新最大item长度列表
	 * @param maxLengthList
	 * @param key
	 * @param itemList
	 */
	private static void refreshMaxLengthList(List<AtomicInteger> maxLengthList, String key, List<String> itemList) {
		for(int i = 0 ; i < maxLengthList.size() ; i ++) {
			AtomicInteger length = maxLengthList.get(i);
			if(length.intValue() < (0 == i ? key.length() : itemList.get(i - 1).length())) {
				length.set(0 == i ? key.length() : itemList.get(i - 1).length());
			}
		}
	}
	
}
