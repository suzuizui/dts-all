package com.le.dts.sdk;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
import com.le.dts.common.domain.store.assemble.JobExecuteHistory;
import com.le.dts.common.domain.store.assemble.JobInstanceDetailStatus;
import com.le.dts.common.domain.store.assemble.JobStatus;
import com.le.dts.common.util.StringUtil;
import com.le.dts.sdk.context.SDKContext;
import com.le.dts.sdk.util.CookieUtil;

/**
 * 内部环境SDK;
 * Created by luliang on 15/1/21.
 */
public class DtsInnerSDKManager extends DtsCommonSDKManager {

    /**
     * 阿里云账户的用户名;
     */
    private String userId;

    public DtsInnerSDKManager(String userId) {
        super(SDKMode.ONLINE_MODE);
        if(StringUtil.isBlank(userId)) {
            throw new RuntimeException("UserID不能为空!");
        }
        this.userId = userId;
    }

    public DtsInnerSDKManager(String userId, SDKMode mode) {
        super(mode);
        if(StringUtil.isBlank(userId)) {
            throw new RuntimeException("UserID不能为空!");
        }
        this.userId = userId;
    }

    @Override
    public Result<List<Cluster>> getDtsClustersInfo() {
        initRequest();
        return super.getDtsClustersInfo();
    }

    @Override
    public Result<String> createGroup(long clusterId, String groupDescription) {
        initRequest();
        return super.createGroup(clusterId, groupDescription);
    }

    @Override
    public Result<Boolean> deleteGroup(String userGroupId) {
        initRequest();
        return super.deleteGroup(userGroupId);
    }

    @Override
    public Result<List<AssembledUserGroup>> getUserGroups(long clusterId) {
        initRequest();
        return super.getUserGroups(clusterId);
    }

    @Override
    public Result<Long> createJob(String userGroupId, Job job) {
        initRequest();
        return super.createJob(userGroupId, job);
    }

    @Override
    public Result<Integer> deleteJob(long jobId) {
        initRequest();
        return super.deleteJob(jobId);
    }

    @Override
    public Result<Integer> updateJob(String groupId, Job job) {
        initRequest();
        return super.updateJob(groupId, job);
    }

    public Result<Integer> updateJobWithArguments(String groupId, Job job) {
        initRequest();
        return super.updateJobWithArguments(groupId, job);
    }

    @Override
    public Result<Integer> updateJobArguments(long jobId, String jobArguments) {
        initRequest();
        return super.updateJobArguments(jobId, jobArguments);
    }

    @Override
    public Result<List<Job>> getJobsForGroup(String userGroupId) {
        initRequest();
        return super.getJobsForGroup(userGroupId);
    }
    
    public Result<List<Job>> getJobsForGroupByPage(String userGroupId, int pageSize, int pageNumber) {
    	initRequest();
    	return super.getJobsForGroupByPage(userGroupId, pageSize, pageNumber);
    }

    @Override
    public Result<Boolean> enableJob(long jobId) {
        initRequest();
        return super.enableJob(jobId);
    }

    @Override
    public Result<Boolean> disableJob(long jobId) {
        initRequest();
        return super.disableJob(jobId);
    }

    @Override
    public Result<Boolean> instanceRunJob(String userGroupId, long jobId) {
        initRequest();
        return super.instanceRunJob(userGroupId, jobId);
    }

    @Override
    public Result<Boolean> instanceStopJob(long jobId) {
        initRequest();
        return super.instanceStopJob(jobId);
    }

    @Override
    public Result<JobStatus> getJobRunningStatus(long jobId) {
        initRequest();
        return super.getJobRunningStatus(jobId);
    }

    @Override
    public Result<JobInstanceDetailStatus> getJobDetailRunningStatus(long jobId, long instanceId) {
        initRequest();
        return super.getJobDetailRunningStatus(jobId, instanceId);
    }

    @Override
    public Result<Boolean> grantGroupAuth(String userGroupId, String ownerUserId, String grantUserId) {
        initRequest();
        return super.grantGroupAuth(userGroupId, ownerUserId, grantUserId);
    }

    @Override
    public Result<JobExecuteHistory> getJobRunningHistoryStatus(long jobId) {
        initRequest();
        return super.getJobRunningHistoryStatus(jobId);
    }

    public Result<Boolean> resetJobRelation(List<Long> startJobIdList) {
    	initRequest();
        return super.resetJobRelation(startJobIdList);
    }
    
    public Result<Boolean> createRelation(long afterJobId, long beforeJobId) {
    	initRequest();
    	return super.createRelation(afterJobId, beforeJobId);
    }
    
    public Result<Boolean> deleteRelation(long afterJobId, long beforeJobId) {
    	initRequest();
    	return super.deleteRelation(afterJobId, beforeJobId);
    }
    
    public List<String> queryClientGroupIpList(String groupId, long jobId) {
    	initRequest();
    	return super.queryClientGroupIpList(groupId, jobId);
    }
    
    /**
     * 初始化cookie;
     */
    private void initRequest() {
        List<Cookie> cookies = new ArrayList<Cookie>(2);
        Cookie sourceToken = new Cookie(Constants.USER_KEY, userId);
        cookies.add(sourceToken);
        long nowTime = System.currentTimeMillis();
        Cookie timestamp = new Cookie(Constants.TIME_STAMP, String.valueOf(nowTime));
        cookies.add(timestamp);

        SDKContext.setCookie(CookieUtil.cookieToString(cookies));
    }
}
