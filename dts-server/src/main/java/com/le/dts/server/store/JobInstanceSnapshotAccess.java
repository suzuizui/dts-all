package com.le.dts.server.store;

import java.util.Date;
import java.util.List;

import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.exception.AccessException;

/**
 * Job实例快照访问接口
 * @author tianyao.myc
 *
 */
public interface JobInstanceSnapshotAccess {

	/**
	 * 插入
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public long insert(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;

    /**
     * 根据id查询
     * @param id
     * @return
     * @throws AccessException
     */
    public JobInstanceSnapshot get(long id) throws AccessException;
    
    /**
     * 查询最新的一条记录
     * @param jobId
     * @return
     * @throws AccessException
     */
    public JobInstanceSnapshot queryNewestInstance(long jobId) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<JobInstanceSnapshot> query(JobInstanceSnapshot query) throws AccessException;
	
	/**
	 * 查询working;
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<JobInstanceSnapshot> queryWorking(long query) throws AccessException;
	
	/**
	 * 查询实例全局变量
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public JobInstanceSnapshot queryInstanceGlobal(JobInstanceSnapshot query) throws AccessException;
	
	/**
	 * 查询正在运行的实例数量
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public long queryWorkingJobInstanceAmount(JobInstanceSnapshot query) throws AccessException;
	
	/**
	 * 查询需要重试的记录数量
	 * @param statusList
	 * @return
	 * @throws AccessException
	 */
	public long queryRetryCount(List<Integer> statusList) throws AccessException;
	
	/**
	 * 查询要重试的实例列表
	 * @param statusList
	 * @param offset
	 * @param length
	 * @return
	 * @throws AccessException
	 */
	public List<JobInstanceSnapshot> queryRetryInstanceList(List<Integer> statusList, long offset, int length) throws AccessException;
	
	public List<JobInstanceSnapshot> queryInstanceListPaging(long jobId, long lastId) throws AccessException;
	
	/**
	 * 更新
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int update(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	public int updateLockAndOffset(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	public int updateInitInstance(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 修改用户自定义全局变量
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateInstanceGlobal(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;

    /**
     * 带乐观锁更新位点
     * @param offset
     * @param originalVersion
     * @return
     * @throws AccessException
     */
    public boolean updateOffsetWithLock(long id, long offset, long originalVersion) throws AccessException;

    /**
     * 悲观锁根据条件设置为锁定
     * @param id
     * @param threshold
     * @param offset
     * @return
     * @throws AccessException
     */
    public boolean setLockedWithCondition(long id, Date threshold, long offset) throws AccessException;
    
    /**
     * 更新实例运行结果
     * @param jobInstanceSnapshot
     * @return
     * @throws AccessException
     */
    public int updateJobInstanceResult(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
    /**
	 * 更新实例状态
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateInstanceStatus(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 更新失败实例状态
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateFailureInstanceStatus(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 更新锁
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateInstanceLock(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 更新通知版本
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateNotifyVersion(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;

   /**
    * 更新通知Job Tag;
    * @param id
    * @param lastJobId
    * @param afterJobId
    * @return
    * @throws AccessException
    */
    public int updateRelationTag(long id, long lastJobId, long afterJobId) throws AccessException;
    
    public int updateHandleUnfinishVersion(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
    
	/**
	 * 删除
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int delete(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;


    public JobInstanceSnapshot findByJobIdAndFireTime(long jobId, String fireTime) throws AccessException;
	
}
