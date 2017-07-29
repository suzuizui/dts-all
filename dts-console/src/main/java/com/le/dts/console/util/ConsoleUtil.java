package com.le.dts.console.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;

public final class ConsoleUtil {
	private static Log log = LogFactory.getLog(ConsoleUtil.class);

	public static String getIpFromRomoteHostName(String hostName)
			throws UnknownHostException, IOException {
		if (StringUtils.isBlank(hostName)) {
			throw new NullPointerException("host-name");
		}

		String ip = InetAddress.getByName(hostName).getHostAddress().toString();
		log.warn("resolve host name" + hostName + " get ip : " + ip);

		return ip;
	}

	public static String getLocalHostIp() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();
		return ip;
	}

	public static void writeJsonToResponse(HttpServletResponse response,
			JSONObject jsonObj) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			response.setContentType("application/json;charset=utf-8");
			if (null != writer) {
				writer.write(jsonObj.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.flush();
				writer.close();
			}
		}
	}
}
