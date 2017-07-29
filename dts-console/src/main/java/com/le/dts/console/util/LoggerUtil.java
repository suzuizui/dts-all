package com.le.dts.console.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.console.global.Global;

public class LoggerUtil {
	
	private static final Log log = LogFactory.getLog(LoggerUtil.class);

	public static void printLog(Log logger, HttpServletRequest request, String class_method, Object[] args) {
		
		try {
			String userId = Global.getDtsUser(request).getUserId();
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("[OperationLog-" + class_method + "]: operator:" + userId);
			
			for(int i = 0 ; i < args.length ; i ++) {
				sb.append(", " + args[i]);
			}
			
			logger.info(sb.toString());
		} catch (Throwable e) {
			log.error("[LoggerUtil]: printLog error, class_method:" + class_method, e);
		}
	}
	
}
