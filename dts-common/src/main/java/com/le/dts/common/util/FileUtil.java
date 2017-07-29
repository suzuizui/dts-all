package com.le.dts.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;

/**
 * 文件相关工具
 * @author tianyao.myc
 *
 */
public class FileUtil implements Constants {

	private static final Log logger = LogFactory.getLog(FileUtil.class);
	
	/**
	 * 写文件
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static boolean write(String filePath, String fileName, String content) {
		
		//new文件目录对象
		File path = new File(filePath);
		
		//文件目录不存在就创建
		if(! path.exists()) {
			
			//创建新文件目录
			boolean createResult = false;
			try {
				
				//创建文件夹路径
				createResult = path.mkdirs();
			} catch (Throwable e) {
				
				logger.error("[FileUtil]: write mkdirs error", e);
				
				return false;
			}
			logger.warn("[FileUtil]: write mkdirs, createResult:" + createResult);
		}
		
		//new文件对象
		File file = new File(filePath + System.getProperty(FILE_SEPARATOR) + fileName);
		
		BufferedWriter writer = null;
		try {
			
			//new FileWriter
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			
			//写内容
			writer.write(content + NEWLINE);
			
			//把缓冲区内容写入文件
			writer.flush();
		} catch (Throwable e) {
			
			logger.error("[FileUtil]: FileWriter write error", e);
			
			return false;
		} finally {
			
			if(writer != null) {
				
				try {
					
					//关闭文件
					writer.close();
				} catch (Throwable e) {
					logger.error("[FileUtil]: FileWriter write close error", e);
				}
				
			}
			
		}
		
		return true;
	}
	
	/**
	 * 读文件
	 * @param filePath
	 * @return
	 */
	public static List<String> read(String filePath) {
		
		List<String> lineList = new ArrayList<String>();
		
		//new文件对象
		File file = new File(filePath);
		
		if(! file.exists()) {
			
			//文件不存在就返回空列表
			return lineList;
		}
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			
			String line = null;
			while((line = reader.readLine()) != null) {
				
				//循环读取每一行
				lineList.add(line);
			}
			
		} catch (Throwable e) {

			logger.error("[FileUtil]: FileReader read error", e);

			return lineList;
		} finally {
			
			if(reader != null) {
				
				try {
					
					//关闭文件
					reader.close();
				} catch (Throwable e) {
					logger.error("[FileUtil]: FileReader read close error", e);
				}
				
			}
			
		}
		
		return lineList;
	}
	
	/**
	 * 获取子目录或文件列表
	 * @param path
	 * @return
	 */
	public static List<String> getFileList(String path) {
		
		List<String> fileList = new ArrayList<String>();
		
		//new文件对象
		File file = new File(path);
		
		if(! file.exists()) {
			
			//文件或目录不存在就返回空列表
			return fileList;
		}
		
		File[] fileArray = file.listFiles();
		
		if(null == fileArray || fileArray.length <= 0) {
			
			//没有子目录就返回空列表
			return fileList;
		}
		
		for(int i = 0 ; i < fileArray.length ; i ++) {
			
			//添加进列表
			fileList.add(fileArray[i].getName());
			
		}
		
		return fileList;
	}
	
	/**
	 * 删除文件或目录
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		
		//new文件对象
		File file = new File(path);
	
		if(! file.exists()) {
			
			//文件或目录不存在就返回true
			return true;
		}
		
		//删除文件或目录
		return file.delete();
	}
	
}
