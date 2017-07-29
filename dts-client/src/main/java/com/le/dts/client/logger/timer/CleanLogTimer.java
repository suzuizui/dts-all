package com.le.dts.client.logger.timer;

import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.util.FileUtil;
import com.le.dts.common.util.PathUtil;

/**
 * 清除日志定时器
 * @author tianyao.myc
 *
 */
public class CleanLogTimer extends TimerTask {

	private static final Log logger = LogFactory.getLog(CleanLogTimer.class);
	
	@Override
	public void run() {
		
		final AtomicInteger fileCounter = new AtomicInteger(0);
		
		try {
			
			String loggerRootPath = PathUtil.getHomeLoggerRootPath();

			//获取子目录列表
			List<String> fileList = FileUtil.getFileList(loggerRootPath);
			
			if(CollectionUtils.isEmpty(fileList)) {
				return ;//如果目录列表为空就返回
			}
			
			for(String file : fileList) {
				
				//处理目录
				handleDir(file, fileCounter);
			}
			
		} catch (Throwable e) {
			logger.error("[CleanLogTimer]: run error", e);
		}
		
		logger.warn("[CleanLogTimer]: run over, fileCounter:" + fileCounter.get());
	}

	/**
	 * 处理目录 只保留1000次执行记录
	 * @param dir
	 * @param fileCounter
	 */
	private void handleDir(String dir, final AtomicInteger fileCounter) {
		
		String loggerTaskPath = PathUtil.getHomeLoggerTaskPath(Long.parseLong(dir));
		
		//获取子文件列表
		List<String> fileList = FileUtil.getFileList(loggerTaskPath);
		
		if(CollectionUtils.isEmpty(fileList) || fileList.size() <= 1000) {
			return ;//如果目录列表为空或者小于1000就返回
		}
		
		//列表排序
		Collections.sort(fileList);
		
		int i = 0;
		for(String file : fileList) {
			
			if(i < (fileList.size() - 1000)) {
				
				//日志文件路径
				String loggerPath = PathUtil.getHomeLoggerPath(Long.parseLong(dir), file);
				
				FileUtil.deleteFile(loggerPath);
				
				//计数器递增
				fileCounter.incrementAndGet();
			}
			
			i ++;
		}
	}
	
}
