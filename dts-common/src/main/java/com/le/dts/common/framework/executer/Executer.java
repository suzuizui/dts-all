package com.le.dts.common.framework.executer;

import java.util.List;

/**
 * 执行器
 * @author tianyao.myc
 *
 * @param <T>
 */
public interface Executer<T> {

	/**
	 * 生产
	 * @param t
	 * @return
	 */
	public List<T> produce(T t);
	
	/**
	 * 消费
	 * @param t
	 */
	public void consume(T t);
	
}
