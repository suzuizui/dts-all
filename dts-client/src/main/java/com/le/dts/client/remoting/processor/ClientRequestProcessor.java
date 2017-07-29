package com.le.dts.client.remoting.processor;

import com.le.dts.client.context.ClientContext;
import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.protocol.InvokeMethod;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.helper.RemotingHelper;
import com.le.dts.common.proxy.ProxyService;
import com.le.dts.common.remoting.netty.NettyRequestProcessor;
import com.le.dts.common.remoting.protocol.RemotingCommand;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.util.BytesUtil;
import com.le.dts.common.util.StringUtil;

/**
 * 客户端请求处理器
 * @author tianyao.myc
 *
 */
public class ClientRequestProcessor implements NettyRequestProcessor, ClientContext {

	private static final Log logger = LogFactory.getLog(ClientRequestProcessor.class);
	
	/**
	 * 处理请求
	 */
	public RemotingCommand processRequest(ChannelHandlerContext ctx,
			RemotingCommand request) throws Exception {
		byte[] requestBody = request.getBody();
		if(null == requestBody) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ClientRequestProcessor]: requestBody is null, remoteAddress:" + remoteAddress);
			return new RemotingCommand();
		}
		
		String json = null;
		try {
			json = (String)BytesUtil.bytesToObject(requestBody);
		} catch (Throwable e) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ClientRequestProcessor]: bytesToObject error"
					+ ", remoteAddress:" + remoteAddress, e);
			return new RemotingCommand();
		}
		if(StringUtil.isBlank(json)) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ClientRequestProcessor]: json is null"
					+ ", remoteAddress:" + remoteAddress);
			return new RemotingCommand();
		}
		
		InvokeMethod invokeMethod = InvokeMethod.newInstance(json);
		invokeMethod.getRemoteMachine().setChannel(ctx.channel());
		
		/** 设置上下文 */
		InvocationContext.setRemoteMachine(invokeMethod.getRemoteMachine());
		Object result = (Object)proxyService.invokeMethod(clientService, invokeMethod.getMethodName(), invokeMethod.getClassArray(), invokeMethod.getObjectArray());
		/** 清除上下文 */
		InvocationContext.clean();
		
		Class<?> returnClass = ProxyService.getClass(invokeMethod.getReturnType());
		if(void.class == returnClass) {
			return new RemotingCommand();
		}
		
		if(null == result) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ClientRequestProcessor]: result is null, remoteAddress:" + remoteAddress + ", invokeMethod:" + invokeMethod);
			return new RemotingCommand();
		}
		
		if(ResultCode.NO_SUCH_METHOD.equals(result)) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ClientRequestProcessor]: NO_SUCH_METHOD, remoteAddress:" + remoteAddress + ", invokeMethod:" + invokeMethod);
			byte[] resultBody = null;
			try {
				resultBody = BytesUtil.objectToBytes(ResultCode.NO_SUCH_METHOD.toJsonString());
			} catch (Throwable e) {
				logger.error("[ClientRequestProcessor]: NO_SUCH_METHOD objectToBytes error"
						+ ", remoteAddress:" + remoteAddress 
						+ ", invokeMethod:" + invokeMethod, e);
			}
			RemotingCommand response = new RemotingCommand();
			response.setBody(resultBody);
			return response;
		}
		
		byte[] responseBody = null;
		try {
			responseBody = BytesUtil.objectToBytes(RemotingSerializable.toJson(result, false));
		} catch (Throwable e) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ClientRequestProcessor]: objectToBytes error"
					+ ", remoteAddress:" + remoteAddress + ", invokeMethod:" + invokeMethod, e);
			return new RemotingCommand();
		}
		
		RemotingCommand response = new RemotingCommand();
		response.setBody(responseBody);
		return response;
	}
	
	

}
