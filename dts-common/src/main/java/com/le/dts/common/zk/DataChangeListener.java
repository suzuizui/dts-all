package com.le.dts.common.zk;

/**
 * 节点数据变更的listener
 */
public interface DataChangeListener {

	/**
	 * 节点数据变更时回调，changedData是变更后的数据
	 * @param changedData
	 */
	public void handleChangeData(String changedData);
}