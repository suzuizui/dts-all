package com.le.dts.common.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;

/**
 * Job相关工具
 * @author tianyao.myc
 *
 */
public class JobUtil implements Constants {

	private static final Log logger = LogFactory.getLog(JobUtil.class);
	
	/**
	 * 获取过去一段时间之内已经开始执行的job列表
	 * @param jobList 是输入的用于检查的所有job
	 * @param time 是过去的一段时间 比如过去30分钟（30 * 60 * 1000L）
	 * @return
	 */
	public List<Job> acquireFiredJobList(List<Job> jobList, long time) {
        List<Job> result = new ArrayList<Job>();
        Date now = new Date();
        Date start = new Date(now.getTime() - time);
        for (Job job : jobList) {
            try {
                CronExpression expression = new CronExpression(job.getCronExpression());
                Date nextFireTime = expression.getNextValidTimeAfter(start);
                if (null == nextFireTime) {
                	continue ;
                }
                if (nextFireTime.compareTo(now) < 0) {
                    result.add(job);
                }
            } catch (Throwable e) {
            	logger.error("[JobUtil]: acquireFiredJobList error, job:" + job, e);
            }
        }
        return result;
	}
	
	/**
	 * 获取触发时间
	 * @param fireTime
	 * @return
	 */
	public static String acquireFireTime(String fireTime) {
		
		if(StringUtil.isBlank(fireTime)) {
			return null;
		}
		
		String[] fireTimeArr = fireTime.split(SPLIT_STRING);
		
		if(fireTimeArr.length < 2) {
			return fireTime;
		} else {
			return fireTimeArr[0];
		}
		
	}
	
}
