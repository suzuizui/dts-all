package com.le.dts.server.util;


/**
 * DB路由工具类
 * @author tianyao.myc
 *
 */
public class DBRouteUtil {

	public static final String DTS_TASK_SNAPSHOT = "dts_task_snapshot";
	public static final String INSTANCE_ID = "job_instance_id";
	
	/**
	 * 路由条件
	 * @param virtualTableName
	 * @param key
	 * @param value
	 * @return
	 */
//	public static SimpleCondition getSimpleCondition(
//			String virtualTableName, String key, String value) {
//		SimpleCondition condition = new SimpleCondition();
//		condition.setVirtualTableName(virtualTableName);
//		condition.put(key, value);
//		return condition;
//	}
	
	/**
	 * 设置路由字段
	 * @param virtualTableName
	 * @param key
	 * @param value
	 */
//	public static void setThreadLocalMap(
//			String virtualTableName, String key, String value) {
//		ThreadLocalMap.put(ThreadLocalString.ROUTE_CONDITION, 
//				getSimpleCondition(virtualTableName, key, value));	
//	}
	
}
