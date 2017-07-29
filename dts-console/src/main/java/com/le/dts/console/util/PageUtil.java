package com.le.dts.console.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTS分页工具;
 * 
 * @author luliang.ll
 * 
 */
public class PageUtil {
	
	public static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * 分页方法
	 * 
	 * @param pageSize
	 *            每页显示条数
	 * @param pageNumber
	 *            要得到第几页的数据
	 * @param listT
	 *            数据集
	 * @param filter
	 *            过滤类
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> pagingList(int pageSize, int pageNumber,
			List<T> listT, PageFilter filter) {

		if (null == listT || listT.size() == 0) {
			return Collections.EMPTY_LIST;
		}

		// 首先执行filter然后再分页
		List<T> listFiltered = new ArrayList<T>();
		for (T t : listT) {
			if (filter.filter(t)) {
				listFiltered.add(t);
			}
		}

		return pagingList(pageSize, pageNumber, listFiltered);

	}

	/**
	 * 分页方法
	 * 
	 * @param pageSize
	 *            每页显示条数
	 * @param pageNumber
	 *            要得到第几页的数据
	 * @param listT
	 *            数据集
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> pagingList(int pageSize, int pageNumber,
			List<T> listT) {

		List<T> resultList = new ArrayList<T>();
		if (null == listT || listT.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		int count = listT.size();
		int startNumber = pageSize * (pageNumber - 1);
		if (startNumber > count) {
			return Collections.EMPTY_LIST;
		}
		for (int i = startNumber; i < startNumber + pageSize; i++) {
			if (i >= listT.size()) {
				break;
			}
			resultList.add(listT.get(i));
		}

		return resultList;
	}

	/**
	 * 分页方法 默认将key按升序排序,key 是string类型
	 * 
	 * @param pageSize
	 *            每页显示条数
	 * @param pageNumber
	 *            要得到第几页的数据
	 * @param listT
	 *            数据集
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Map<String, T> pagingMap(int pageSize, int pageNumber,
			Map<String, T> mapT) {

		Map<String, T> resultMap = new HashMap();
		if (null == mapT || mapT.size() == 0) {
			return resultMap;
		}
		int count = mapT.size();
		int startNumber = pageSize * (pageNumber - 1);
		if (startNumber > count) {
			return resultMap;
		}
		List<String> keyList = new ArrayList(mapT.keySet());
		Collections.sort(keyList);
		List<String> pagedkeyList = new ArrayList<String>();
		for (int i = startNumber; i < startNumber + pageSize; i++) {
			if (i >= keyList.size()) {
				break;
			}
			pagedkeyList.add(keyList.get(i));
		}
		for (String key : pagedkeyList) {
			resultMap.put(key, mapT.get(key));
		}

		return resultMap;
	}
}
