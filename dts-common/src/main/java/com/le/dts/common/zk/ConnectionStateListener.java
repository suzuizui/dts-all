package com.le.dts.common.zk;

import org.apache.curator.framework.state.ConnectionState;

public interface ConnectionStateListener {

	/**
	 * Called when the zookeeper connection state has changed.
	 * @param state The new state.
	 */
	public void handleStateChanged(ConnectionState state);
}
