package com.le.dts.common.domain;

import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * 机器
 * @author tianyao.myc
 *
 */
public class Machine {

	/** 主机ID */
	private String machineId;
	
	/** 主机名 */
	private String hostname;
	
	/** IP地址 */
	private String ipAddress;
	
	/** 远端地址 */
	private String remoteAddress;
	
	public Machine() {
		
	}
	
	public Machine(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
	public Machine(String machineId, String hostname, String ipAddress) {
		this.machineId = machineId;
		this.hostname = hostname;
		this.ipAddress = ipAddress;
	}

	public static Machine newInstance(String json) {
        return RemotingSerializable.fromJson(json, Machine.class);
    }

    /**
     * 对象转换成json
     */
    @Override
    public String toString() {
        return RemotingSerializable.toJson(this, false);
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((remoteAddress == null) ? 0 : remoteAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Machine other = (Machine) obj;
		if (remoteAddress == null) {
			if (other.remoteAddress != null)
				return false;
		} else if (!remoteAddress.equals(other.remoteAddress))
			return false;
		return true;
	}

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
}
