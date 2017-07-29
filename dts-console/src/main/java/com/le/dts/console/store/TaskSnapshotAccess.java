package com.le.dts.console.store;

import java.util.List;

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
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<TaskSnapshot> query(TaskSnapshot query) throws AccessException;
	
	public List<TaskSnapshot> query4Cleanup(TaskSnapshot query) throws AccessException;
	
	public long queryCount4Cleanup(TaskSnapshot query) throws AccessException;

	/**
	 * 查询
	 * @param instanceId
	 * @return
	 * @throws AccessException
	 */
	public Long queryTotalAmout(long instanceId) throws AccessException;
	
	/**
	 * 查询
	 * @param instanceId
	 * @return
	 * @throws AccessException
	 */
	public Long queryInitAmout(long instanceId) throws AccessException;
	
	/**
	 * 查询
	 * @param instanceId
	 * @return
	 * @throws AccessException
	 */
	public Long queryQueueAmout(long instanceId) throws AccessException;
	
	/**
	 * 查询
	 * @param instanceId
	 * @return
	 * @throws AccessException
	 */
	public Long queryStartAmout(long instanceId) throws AccessException;
	
	/**
	 * 查询
	 * @param instanceId
	 * @return
	 * @throws AccessException
	 */
	public Long querySuccessAmout(long instanceId) throws AccessException;
	
	/**
	 * 查询
	 * @param instanceId
	 * @return
	 * @throws AccessException
	 */
	public Long queryFailureAmout(long instanceId) throws AccessException;
	
	/**
	 * 查询
	 * @param instanceId
	 * @return
	 * @throws AccessException
	 */
	public Long queryFoundFailureAmout(long instanceId) throws AccessException;
	
	/**
	 * 查询
	 * @param taskSnapShot
	 * @return
	 * @throws AccessException
	 */
	public Long queryLayerTotalAmout(TaskSnapshot taskSnapShot) throws AccessException;
	
	/**
	 * 查询
	 * @param taskSnapShot
	 * @return
	 * @throws AccessException
	 */
	public Long queryLayerInitAmout(TaskSnapshot taskSnapShot) throws AccessException;
	
	/**
	 * 查询
	 * @param taskSnapShot
	 * @return
	 * @throws AccessException
	 */
	public Long queryLayerQueueAmout(TaskSnapshot taskSnapShot) throws AccessException;
	
	/**
	 * 查询
	 * @param taskSnapShot
	 * @return
	 * @throws AccessException
	 */
	public Long queryLayerStartAmout(TaskSnapshot taskSnapShot) throws AccessException;
	
	/**
	 * 查询
	 * @param taskSnapShot
	 * @return
	 * @throws AccessException
	 */
	public Long queryLayerSuccessAmout(TaskSnapshot taskSnapShot) throws AccessException;
	
	/**
	 * 查询
	 * @param taskSnapShot
	 * @return
	 * @throws AccessException
	 */
	public Long queryLayerFailureAmout(TaskSnapshot taskSnapShot) throws AccessException;
	
	/**
	 * 查询
	 * @param taskSnapShot
	 * @return
	 * @throws AccessException
	 */
	public Long queryLayerFoundFailureAmout(TaskSnapshot taskSnapShot) throws AccessException;
	
	/**
	 * 查询
	 * @param taskSnapShot
	 * @return
	 * @throws AccessException
	 */
	public List<String> queryTaskLayer(TaskSnapshot taskSnapShot) throws AccessException;
	
	/**
	 * 查询ID列表
	 * @param jobInstanceId
	 * @return
	 * @throws AccessException
	 */
	public List<Long> queryIdList(long jobInstanceId) throws AccessException;
	
	public List<TaskSnapshot> queryTaskSnapshotList(long jobInstanceId) throws AccessException;
	
	/**
	 * 根据重试次数查询ID列表
	 * @param jobInstanceId
	 * @return
	 * @throws AccessException
	 */
	public List<Long> queryIdListByRetryCount(long jobInstanceId) throws AccessException;
	
	public List<TaskSnapshot> queryTaskSnapshotListByRetryCount(long jobInstanceId) throws AccessException;
	
	/**
	 * 获取统计任务列表
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public List<TaskSnapshot> aquireTaskList(TaskSnapshot query) throws AccessException;
	
	/**
	 * 更新
	 * @param taskSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int update(TaskSnapshot taskSnapshot) throws AccessException;
	
	/**
	 * 删除
	 * @param taskSnapshot
	 * @return
	 * @throws AccessException
	 */
	public int delete(TaskSnapshot taskSnapshot) throws AccessException;
	
	/**
	 * 批量删除某个实例任务数据
	 * @param jobInstanceId
	 * @param idList
	 * @return
	 * @throws AccessException
	 */
	public int delete4InstanceByIdList(long jobInstanceId, List<Long> idList) throws AccessException;
	
}
