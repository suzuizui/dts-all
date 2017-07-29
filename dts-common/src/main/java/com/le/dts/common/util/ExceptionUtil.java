package com.le.dts.common.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 异常工具类
 * @author tianyao.myc
 *
 */
public class ExceptionUtil {

	private static final Log logger = LogFactory.getLog(ExceptionUtil.class);
	
	/**
	 * 判断主键冲突异常
	 * @param error
	 * @return
	 * @throws IOException
	 */
	public static boolean isDuplicate(Throwable error) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		error.printStackTrace(printWriter);
		printWriter.flush();
		LineNumberReader reader = 
				new LineNumberReader(
						new StringReader(stringWriter.toString()));
		boolean duplicate = false;
		try {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.matches("Duplicate") || line.contains("Duplicate")) {
					duplicate = true;
					break ;
				}
			}
		} catch (Throwable e) {
			logger.error("[ExceptionUtil]: isDuplicate read line error", e);
		}
		return duplicate;
	}
	
	/**
	 * 判断是否中断异常
	 * @param error
	 * @return
	 * @throws IOException
	 */
	public static boolean isInterrupted(Throwable error) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		error.printStackTrace(printWriter);
		printWriter.flush();
		LineNumberReader reader = 
				new LineNumberReader(
						new StringReader(stringWriter.toString()));
		boolean interrupted = false;
		try {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.matches("interrupted") || line.contains("interrupted")) {
					interrupted = true;
					break ;
				}
			}
		} catch (Throwable e) {
			logger.error("[ExceptionUtil]: isInterrupted read line error", e);
		}
		return interrupted;
	}
	
	/**
	 * 是否永远不触发异常
	 * @param error
	 * @return
	 * @throws IOException
	 */
	public static boolean isNeverFire(Throwable error) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		error.printStackTrace(printWriter);
		printWriter.flush();
		LineNumberReader reader = 
				new LineNumberReader(
						new StringReader(stringWriter.toString()));
		boolean interrupted = false;
		try {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.matches("the given trigger will never fire") || line.contains("the given trigger will never fire")) {
					interrupted = true;
					break ;
				}
			}
		} catch (Throwable e) {
			logger.error("[ExceptionUtil]: isNeverFire read line error", e);
		}
		return interrupted;
	}
	
	/**
	 * 语法错误异常
	 * @param error
	 * @return
	 */
	public static boolean isSyntaxError(Throwable error) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		error.printStackTrace(printWriter);
		printWriter.flush();
		LineNumberReader reader = 
				new LineNumberReader(
						new StringReader(stringWriter.toString()));
		boolean syntaxError = false;
		try {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.matches("MySQLSyntaxErrorException") || line.contains("MySQLSyntaxErrorException")) {
					syntaxError = true;
					break ;
				}
			}
		} catch (Throwable e) {
			logger.error("[ExceptionUtil]: isSyntaxError read line error", e);
		}
		return syntaxError;
	}
	
}
