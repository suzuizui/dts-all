package com.le.dts.console.store;

import java.util.List;

import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.page.JobHistoryPageQuery;

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
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public JobInstanceSnapshot query(JobInstanceSnapshot query) throws AccessException;
	
	/**
	 * 查询working;
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<JobInstanceSnapshot> queryWorking(long query) throws AccessException;
	
	/**
	 * 查询working;
	 * 查询用户自定义全局变量
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<JobInstanceSnapshot> pageQuery(JobHistoryPageQuery query) throws AccessException;
	
	/**
	 * 个数
	 * @param jobId
	 * @return
	 * @throws AccessException
	 */
	public int queryJobInstanceCount(long jobId) throws AccessException;
	
	/**
	 * 查询要删除任务数据的实例快照数量
	 * @param statusList
	 * @return
	 * @throws AccessException
	 */
	public long queryDeleteCount(List<Integer> statusList) throws AccessException;
	
	/**
	 * 查询要删除任务数据的实例列表
	 * @param statusList
	 * @param offset
	 * @param length
	 * @return
	 * @throws AccessException
	 */
	public List<JobInstanceSnapshot> queryAllInstanceList(List<Integer> statusList, long offset, int length) throws AccessException;

    public List<JobInstanceSnapshot> queryRuningInstanceList(List<Integer> statusList, long offset, int length, int bulkId, int bulkAmout) throws AccessException;

    /**
     * 查询满足条件的最近一条纪录;
     * @param query
     * @return
     * @throws AccessException
     */
    public JobInstanceSnapshot queryLastInstance(JobInstanceSnapshot query) throws AccessException;
	
    /**
     * 查询出所有的jobId
     * @return
     * @throws AccessException
     */
    public List<Long> queryAllJobIdList() throws AccessException;
    
    /**
     * 根据JobId查询要删除任务数据的实例ID列表
     * @param query
     * @return
     * @throws AccessException
     */
    public List<Long> queryInstanceIdList4DeleteByJobId(JobInstanceSnapshot query) throws AccessException;
    
    public List<Long> queryInstanceIdList4DeleteAllInstanceByJobId(JobInstanceSnapshot query) throws AccessException;
    
	public JobInstanceSnapshot queryInstanceGlobal(JobInstanceSnapshot query) throws AccessException;
	
	public List<JobInstanceSnapshot> queryInstance4Stop(JobInstanceSnapshot query) throws AccessException;
	
	/**
	 * 更新
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int update(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 修改用户自定义全局变量
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateInstanceGlobal(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 更新实例状态
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateInstanceStatus(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 更新实例状态
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateInstanceStatus4JobId(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 更新下一次重试信息
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int updateInstanceNext(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
     * 更新实例运行结果
     * @param jobInstanceSnapshot
     * @return
     * @throws AccessException
     */
    public int updateJobInstanceResult(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 删除
	 * @param jobInstanceSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int delete(JobInstanceSnapshot jobInstanceSnapshot) throws AccessException;
	
	/**
	 * 根据IdList删除
	 * @param idList
	 * @return
	 * @throws AccessException
	 */
	public int deleteInstanceByIdList(List<Long> idList) throws AccessException;

    public JobInstanceSnapshot findByJobIdAndFireTime(long jobId, String fireTime) throws AccessException;
	
}
