package com.le.dts.common.logger;

import java.util.Date;
import java.util.List;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.util.FileUtil;
import com.le.dts.common.util.PathUtil;
import com.le.dts.common.util.TimeUtil;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.util.FileUtil;
import com.le.dts.common.util.PathUtil;
import com.le.dts.common.util.TimeUtil;


/**
 * 日志
 * @author tianyao.myc
 *
 */
public class DtsLogger implements Constants {

	/**
	 * 写一行日志
	 * @param jobId
	 * @param instanceId
	 * @param content
	 */
	public static void info(long jobId, long instanceId, String content) {
		
		//获取用户目录日志文件路径
		String loggerTaskPath = PathUtil.getHomeLoggerTaskPath(jobId);
		
		//加上时间
		content = TimeUtil.date2SecondsString(new Date()) + BLANK + HORIZONTAL_LINE + BLANK + content;
		
		//写一行日志到指定日志文件
		FileUtil.write(loggerTaskPath, instanceId + DTS_LOG_EXT, content);
		
	}
	
	/**
	 * 写一行日志
	 * @param jobId
	 * @param instanceId
	 * @param content
	 * @param e
	 */
	public static void info(long jobId, long instanceId, String content, Throwable e) {
		
		//获取用户目录日志文件路径
		String loggerTaskPath = PathUtil.getHomeLoggerTaskPath(jobId);
		
		//加上时间
		content = TimeUtil.date2SecondsString(new Date()) + BLANK + HORIZONTAL_LINE + BLANK + content + NEWLINE + e.toString();
		
		//写一行日志到指定日志文件
		FileUtil.write(loggerTaskPath, instanceId + DTS_LOG_EXT, content);
		
	}
	
	/**
	 * 读取日志文件
	 * @param jobId
	 * @param instanceId
	 * @return
	 */
	public static List<String> read(long jobId, long instanceId) {
		
		//获取用户目录日志文件路径
		String loggerPath = PathUtil.getHomeLoggerPath(jobId, instanceId, DTS_LOG_EXT);
		
		return FileUtil.read(loggerPath);
	}
	
}
