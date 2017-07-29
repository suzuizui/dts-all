package com.le.dts.server.job.executer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.server.context.ServerContext;

/**
 * 定时执行器,抽象类;
 * @author tianyao.myc
 *
 */
public class TimerExecutor implements org.quartz.Job, ServerContext, Constants {

    private static final Log logger = LogFactory.getLog("timerExecutor");
	/**
	 * 执行入口
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Job job = null;
        try {
            job = (Job)context.getScheduler().getContext().get(context.getJobDetail().getName());
            JobRelation jobRelation = new JobRelation();
            jobRelation.setJobId(job.getId());
            Result<Boolean> checkResult = jobRelationManager.checkAllBeforeDone(jobRelation);
            if(! checkResult.getData()) {
            	logger.warn("[TimerExecutor]: checkAllBeforeDone failed"
            			+ ", jobId:" + context.getJobDetail().getName() 
	            		+ ", fireTime:" + context.getFireTime().toLocaleString() 
	            		+ ", server:" + serverConfig.getLocalAddress() 
	            		+ ", checkResult:" + checkResult);
                return;
            }

            List<String> machineList = new ArrayList<String>();
            
			long startTime = System.currentTimeMillis();
            Result<Boolean> fireResult = null;
            try {
				fireResult = jobManager.fireJob(job, context.getFireTime(), null, machineList);
			} catch (Throwable e) {
				logger.error("[TimerExecutor]: jobId:" + context.getJobDetail().getName() 
	            		+ ", fireTime:" + context.getFireTime().toLocaleString() 
	            		+ ", server:" + serverConfig.getLocalAddress()
	            		+ ", machineList:" + machineList, e);
			}
            long endTime = System.currentTimeMillis();
            logger.info("[TimerExecutor]: jobId:" + context.getJobDetail().getName() 
            		+ ", fireTime:" + context.getFireTime().toLocaleString() 
            		+ ", server:" + serverConfig.getLocalAddress() 
            		+ ", fireResult:" + fireResult + ", cost:" + (endTime - startTime) 
            		+ ", machineList:" + machineList);
        } catch (Throwable e) {
        	logger.error("[TimerExecutor]: execute error"
        			+ ", jobId:" + context.getJobDetail().getName() 
        			+ ", fireTime:" + context.getFireTime().toLocaleString() 
        			+ ", server:" + serverConfig.getLocalAddress(), e);
        }
	}

}
