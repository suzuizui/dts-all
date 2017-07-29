package com.le.dts.server.remoting.listener;

import com.le.dts.server.context.ServerContext;
import io.netty.channel.Channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.helper.RemotingHelper;
import com.le.dts.common.remoting.ChannelEventListener;
import com.le.dts.common.util.RemotingUtil;

/**
 * 服务端事件监听器
 * @author tianyao.myc
 *
 */
public class ServerChannelEventListener implements ChannelEventListener, ServerContext {

	private static final Log logger = LogFactory.getLog(ServerChannelEventListener.class);
	
	@Override
	public void onChannelConnect(String remoteAddr, Channel channel) {
		final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
		logger.info("[ServerChannelEventListener]: onChannelConnect {" + remoteAddress + "}");
	}

	@Override
	public void onChannelClose(String remoteAddr, Channel channel) {
		final String remoteAddress = RemotingUtil.socketAddress2String(channel.remoteAddress());
		serverRemoting.deleteConnection(remoteAddress);
		RemotingUtil.closeChannel(channel);
		logger.error("[ServerChannelEventListener]: onChannelClose {" + remoteAddress + "}");
	}

	@Override
	public void onChannelException(String remoteAddr, Channel channel) {
		final String remoteAddress = RemotingUtil.socketAddress2String(channel.remoteAddress());
		serverRemoting.deleteConnection(remoteAddress);
		RemotingUtil.closeChannel(channel);
		logger.error("[ServerChannelEventListener]: onChannelException {" + remoteAddress + "}");
	}

	@Override
	public void onChannelIdle(String remoteAddr, Channel channel) {
		final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
		logger.info("[ServerChannelEventListener]: onChannelIdle {" + remoteAddress + "}");
	}

}
