package com.le.dts.common.domain;

import java.util.List;

import com.le.dts.common.remoting.protocol.RemotingSerializable;

public class ServerList {

	private List<String> servers;

	public List<String> getServers() {
		return servers;
	}

	public void setServers(List<String> servers) {
		this.servers = servers;
	}
	
    @Override
    public String toString() {
        return RemotingSerializable.toJson(this, false);
    }

    public static ServerList newInstance(String json) {
        return RemotingSerializable.fromJson(json, ServerList.class);
    }
	
}
