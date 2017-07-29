package com.le.dts.server.store;

import java.util.List;
import java.util.Map;

import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.AccessException;

/**
 * 任务快照访问接口
 * @author tianyao.myc
 *
 */
public interface TaskSnapshotAccess {

	/**
	 * 插入
	 * @param taskSnapshot
	 * @return
	 * @throws AccessException
	 */
	public long insert(TaskSnapshot taskSnapshot) throws AccessException;

    /**
     * 批量插入
     * @param snapshots
     * @return
     * @throws AccessException
     */
    public int insertBatch(List<TaskSnapshot> snapshots) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<TaskSnapshot> queryByJobInstanceIdAndStatus(Map<String, Object> query) throws AccessException;
	
	public List<TaskSnapshot> queryByJobInstanceIdAndStatus4Unfinish(Map<String, Object> query) throws AccessException;
	
	/**
	 * 查询跳过的任务列表
	 * @param jobInstanceId
	 * @param status
	 * @param start
	 * @param offset
	 * @return
	 * @throws AccessException
	 */
	public List<TaskSnapshot> querySkipTaskList(long jobInstanceId, int status, long start, long offset) throws AccessException;

	/**
	 * 重试Count
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public long queryTaskSnapshotRetryCount(TaskSnapshot query) throws AccessException;
	
	public long queryTotalCount(TaskSnapshot query) throws AccessException;
	
	public long queryItemCount(TaskSnapshot query) throws AccessException;
	
	public List<String> queryTaskNameList(TaskSnapshot query) throws AccessException;
	
	public long queryDetailTotalCount(TaskSnapshot query) throws AccessException;
	
	public long queryDetailItemCount(TaskSnapshot query) throws AccessException;
	
	/**
	 * 查询重试记录数量
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public long queryRetryCount(TaskSnapshot query) throws AccessException;

    public boolean queryExistsStatus(long jobInstanceId, List<Integer> statusList) throws AccessException;

    public List<TaskSnapshot> queryByStatusAndClient(long jobInstanceId, String clientId, List<Integer> statusList)
            throws AccessException;
    
    /**
	 * 查询ID列表
	 * @param jobInstanceId
	 * @return
	 * @throws AccessException
	 */
	public List<Long> queryIdList(long jobInstanceId) throws AccessException;
	
	/**
	 * 根据重试次数查询ID列表
	 * @param jobInstanceId
	 * @return
	 * @throws AccessException
	 */
	public List<Long> queryIdListByRetryCount(long jobInstanceId) throws AccessException;

    /**
	 * 更新
	 * @param taskSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int update(TaskSnapshot taskSnapshot) throws AccessException;
	
	public int updateStatusAndRetryCount(TaskSnapshot taskSnapshot) throws AccessException;

    /**
     * 批量更新
     * @param snapshotList
     * @param status
     * @throws AccessException
     */
    public void updateStatusBatch(List<TaskSnapshot> snapshotList, int status) throws AccessException;

    /**
     * 任务失败并更新重试次数
     * @param snapshotList
     * @param retryCount
     * @return
     * @throws AccessException
     */
    public int setFailureAndRetryCountBatch(List<TaskSnapshot> snapshotList, int retryCount) throws AccessException;

    /**
     * 批量更新clientId
     * @param taskSnapshotList
     * @param clientId
     * @param status
     * @return
     * @throws AccessException
     */
    public int updateClientIdBatch(List<TaskSnapshot> taskSnapshotList, String clientId, int status) throws AccessException;
    
    /**
     * 更新任务快照
     * @param taskSnapshot
     * @return
     * @throws AccessException
     */
    public int updateTaskSnapshot(TaskSnapshot taskSnapshot) throws AccessException;
	
	/**
	 * 删除
	 * @param taskSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int delete(TaskSnapshot taskSnapshot) throws AccessException;

    /**
     * 从偏移量开始查询
     */
    public List<TaskSnapshot> queryAvailableTaskPage(long jobInstanceId, long offset, int pageSize)
            throws AccessException;
    
    /**
     * 从偏移量开始count
     * @param jobInstanceId
     * @param offset
     * @param pageSize
     * @return
     * @throws AccessException
     */
    public long queryAvailableTaskPageCount(long jobInstanceId, long offset, int pageSize)
            throws AccessException;

    /**
     * 查询重试的任务
     */
    public List<TaskSnapshot> queryRetryTaskPage(long jobInstanceSnapshotId, int jobInstanceRetryCount, long offset,
            int pageSize)
            throws AccessException;

    /**
     * 给任务补偿用
     */
    public List<TaskSnapshot> queryTaskPageByStatus(long jobInstanceSnapshotId, int pageSize, List<Integer> status)
            throws AccessException;
    
    /**
	 * 获取统计任务列表
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<TaskSnapshot> aquireTaskList(TaskSnapshot query) throws AccessException;
    
    /**
	 * 批量删除某个实例任务数据
	 * @param jobInstanceId
	 * @param idList
	 * @return
	 * @throws AccessException
	 */
	public int delete4InstanceByIdList(long jobInstanceId, List<Long> idList) throws AccessException;
}
