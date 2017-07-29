package com.le.dts.sdk;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.assemble.*;

import java.util.List;

/**
 * Created by luliang on 14/12/24.
 */
public interface DtsSDKManager {

    /**
     * 所有的集群信息;
     * @return
     */
    public Result<List<Cluster>> getDtsClustersInfo();

    /**
     * 创建用户组;
     * @param clusterId
     * @param groupDescription
     * @return
     */
    public Result<String> createGroup(long clusterId, String groupDescription);

    /**
     * 删除组
     * @param userGroupId:页面显示的组ID，非数据库中的组ID;
     * @return
     */
    public Result<Boolean> deleteGroup(String userGroupId);

    /**
     *
     * @param clusterId 集群的ID;
     * @return
     */
    public Result<List<AssembledUserGroup>> getUserGroups(long clusterId);

    /**
     *
     * @param userGroupId
     * @param job
     * @return
     */
    public Result<Long> createJob(String userGroupId, Job job);

    /**
     *
     * @param jobId
     * @return
     */
    public Result<Integer> deleteJob(long jobId);

    /**
     *
     * @param job
     * @return
     */
    public Result<Integer> updateJob(String groupId, Job job);

    /**
     *
     * @param job
     * @return
     */
    public Result<Integer> updateJobWithArguments(String groupId, Job job);

    /**
     * 修改Job参数;
     * @param jobId
     * @param jobArguments
     * @return
     */
    public Result<Integer> updateJobArguments(long jobId, String jobArguments);

    /**
     * 查询一个组中所有的JOB;
     * @param userGroupId
     * @return
     */
    public Result<List<Job>> getJobsForGroup(String userGroupId);

    /**
     * 使一个任务生效，默认使自动创建生效,也可以调用这个方法再生效;
     * @param jobId
     * @return
     */
    public Result<Boolean> enableJob(long jobId);

    /**
     * 使一个任务实效;
     * @param jobId
     * @return
     */
    public Result<Boolean> disableJob(long jobId);

    /**
     * 使一个任务立即运行一次;
     * @param jobId
     * @return
     */
    public Result<Boolean> instanceRunJob(String userGroupId, long jobId);

    /**
     * 立即停止
     * @param jobId
     * @return
     */
    public Result<Boolean> instanceStopJob(long jobId);

    /**
     * 授权给用户;
     * @param userGroupId   授权的系统显示的组ID
     * @param ownerUserId   拥有者的阿里云账户ID
     * @param grantUserId   要授权给的用户的ID
     * @return
     */
    public Result<Boolean> grantGroupAuth(String userGroupId, String ownerUserId, String grantUserId);


    /**
     * 得到JOB目前的状况
     * @param jobId
     * @return
     */
    public Result<JobStatus> getJobRunningStatus(long jobId);

    /**
     *
     * @param jobId
     * @return
     */
    public Result<JobInstanceDetailStatus> getJobDetailRunningStatus(long jobId, long instanceId);

    public Result<JobExecuteHistory> getJobRunningHistoryStatus(long jobId);

}