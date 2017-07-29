package com.le.dts.server.job;

import com.le.dts.server.job.executer.TimerExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.server.context.ServerContext;

/**
 * 内部Job实例
 * @author tianyao.myc
 *
 */
public class InternalJob implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(InternalJob.class);
	
	/** Job */
	private Job job;
	
	/** 定时Job详情 */
	private JobDetail jobDetail;
	
	/** 定时触发器 */
	private CronTrigger cronTrigger;
	
	public InternalJob() {
		
	}
	
	public InternalJob(Job job) {
		this.job = job;
	}
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		if(CommonUtil.isApiJob(job.getType())) {
			
			logger.info("[InternalJob]: init API job success, job:" + job.toString());
			
		} else {
			
			try {
				Scheduler scheduler = jobPool.getSchedulerFactory().getScheduler();
				
				jobDetail = new JobDetail(String.valueOf(job.getId()), String.valueOf(job.getClientGroupId()), TimerExecutor.class);
				cronTrigger = new CronTrigger(String.valueOf(job.getId()), String.valueOf(job.getClientGroupId()));
				
				/** 初始化时间表达式 */
				cronTrigger.setCronExpression(job.getCronExpression());
				
				scheduler.scheduleJob(jobDetail, cronTrigger);
                scheduler.getContext().put(jobDetail.getName(), job);
				/** 启动定时任务 */
				scheduler.start();
			} catch (Throwable e) {
				throw new InitException("[InternalJob]: init timer job error, job:" + job.toString(), e);
			}
			
			logger.info("[InternalJob]: init timer job success, job:" + job.toString());
		}
		
	}
	
	/**
	 * 删除调度器中的Job
	 */
	public void delete() throws Throwable {
		try {
			Scheduler scheduler = jobPool.getSchedulerFactory().getScheduler();
			scheduler.deleteJob(String.valueOf(job.getId()), String.valueOf(job.getClientGroupId()));
		} catch (Throwable e) {
			throw new RuntimeException("[InternalJob]: delete timer job error, job:" + job.toString(), e);
		}
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public CronTrigger getCronTrigger() {
		return cronTrigger;
	}

	public void setCronTrigger(CronTrigger cronTrigger) {
		this.cronTrigger = cronTrigger;
	}

	@Override
	public String toString() {
		return "InternalJob [job=" + job + ", jobDetail=" + jobDetail
				+ ", cronTrigger=" + cronTrigger + "]";
	}
	
}
