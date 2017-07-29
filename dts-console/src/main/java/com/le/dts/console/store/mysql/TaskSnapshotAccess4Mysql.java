package com.le.dts.console.store.mysql;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.le.dts.common.util.DBShardUtil;
import com.le.dts.console.store.TaskSnapshotAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.AccessException;

/**
 * 任务快照访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class TaskSnapshotAccess4Mysql implements TaskSnapshotAccess {

	private SqlMapClients sqlMapClients;

	private int dbCount;
	private int tableCount;

	public void setDbCount(int dbCount) {
		this.dbCount = dbCount;
	}

	public void setTableCount(int tableCount) {
		this.tableCount = tableCount;
	}

	public void setSqlMapClients(SqlMapClients sqlMapClients) {
		this.sqlMapClients = sqlMapClients;
	}

	/**
	 * 插入
	 */
	public long insert(TaskSnapshot taskSnapshot) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClient()
					.insert("TaskSnapshot.insert", taskSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	/**
	 * 查询
	 */
	@SuppressWarnings("unchecked")
	public List<TaskSnapshot> query(TaskSnapshot query) throws AccessException {
		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = (List<TaskSnapshot>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return taskSnapshotList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaskSnapshot> query4Cleanup(TaskSnapshot query)
			throws AccessException {
		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = (List<TaskSnapshot>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.query4Cleanup", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return taskSnapshotList;
	}

	@Override
	public long queryCount4Cleanup(TaskSnapshot query) throws AccessException {
		Long count = 0L;
		try {
			count = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryCount4Cleanup", query);
		} catch (Throwable e) {
			throw new AccessException("[queryCount4Cleanup]: error", e);
		}
		if(null == count) {
			return 0L;
		}
		return count;
	}

//	/**
//	 * 更新
//	 */
//	public int update(TaskSnapshot taskSnapshot) throws AccessException {
//		int result = 0;
//		try {
//			DBRouteUtil.setThreadLocalMap(DBRouteUtil.DTS_TASK_SNAPSHOT,
//					DBRouteUtil.INSTANCE_ID, String.valueOf(taskSnapshot.getJobInstanceId()));
//			result = sqlMapClients.getSqlMapClient()
//					.update("TaskSnapshot.update", taskSnapshot);
//		} catch (Throwable e) {
//			throw new AccessException("[update]: error", e);
//		}
//		return result;
//	}
//
//	/**
//	 * 删除
//	 */
//	public int delete(TaskSnapshot taskSnapshot) throws AccessException {
//		int result = 0;
//		try {
//			DBRouteUtil.setThreadLocalMap(DBRouteUtil.DTS_TASK_SNAPSHOT,
//					DBRouteUtil.INSTANCE_ID, String.valueOf(taskSnapshot.getJobInstanceId()));
//			result = sqlMapClients.getSqlMapClient()
//					.delete("TaskSnapshot.delete", taskSnapshot);
//		} catch (Throwable e) {
//			throw new AccessException("[delete]: error", e);
//		}
//		return result;
//	}

	@Override
	public Long queryTotalAmout(long instanceId) throws AccessException {
		Long total = 0L;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("jobInstanceId", instanceId);
		paraMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(instanceId), dbCount, tableCount));
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryTotalCount", paraMap);
		} catch (Throwable e) {
			throw new AccessException("[queryTotalAmout]: error", e);
		}
		return total;
	}

	@Override
	public Long queryInitAmout(long instanceId) throws AccessException {
		Long total = 0L;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("jobInstanceId", instanceId);
		paraMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(instanceId), dbCount, tableCount));
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryInitCount", paraMap);
		} catch (Throwable e) {
			throw new AccessException("[queryInitCount]: error", e);
		}
		return total;
	}

	@Override
	public Long queryQueueAmout(long instanceId) throws AccessException {
		Long total = 0L;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("jobInstanceId", instanceId);
		paraMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(instanceId), dbCount, tableCount));
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryQueueCount", paraMap);
		} catch (Throwable e) {
			throw new AccessException("[queryQueueCount]: error", e);
		}
		return total;
	}

	@Override
	public Long queryStartAmout(long instanceId) throws AccessException {
		Long total = 0L;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("jobInstanceId", instanceId);
		paraMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(instanceId), dbCount, tableCount));
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryStartCount", paraMap);
		} catch (Throwable e) {
			throw new AccessException("[queryStartCount]: error", e);
		}
		return total;
	}

	@Override
	public Long querySuccessAmout(long instanceId) throws AccessException {
		Long total = 0L;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("jobInstanceId", instanceId);
		paraMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(instanceId), dbCount, tableCount));
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.querySuccessCount", paraMap);
		} catch (Throwable e) {
			throw new AccessException("[querySuccessCount]: error", e);
		}
		return total;
	}

	@Override
	public Long queryFailureAmout(long instanceId) throws AccessException {
		Long total = 0L;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("jobInstanceId", instanceId);
		paraMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(instanceId), dbCount, tableCount));
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryFailureCount", paraMap);
		} catch (Throwable e) {
			throw new AccessException("[queryFailureCount]: error", e);
		}
		return total;
	}

	@Override
	public Long queryFoundFailureAmout(long instanceId)
			throws AccessException {
		Long total = 0L;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("jobInstanceId", instanceId);
		paraMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(instanceId), dbCount, tableCount));
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryFoundFailureCount", paraMap);
		} catch (Throwable e) {
			throw new AccessException("[queryFoundFailureCount]: error", e);
		}
		return total;
	}

	@Override
	public Long queryLayerTotalAmout(TaskSnapshot taskSnapShot)
			throws AccessException {
		Long total = 0L;
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryLayerTotalCount", taskSnapShot);
		} catch (Throwable e) {
			throw new AccessException("[queryTotalAmout]: error", e);
		}
		return total;
	}

	@Override
	public Long queryLayerInitAmout(TaskSnapshot taskSnapShot)
			throws AccessException {
		Long total = 0L;
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryLayerInitCount", taskSnapShot);
		} catch (Throwable e) {
			throw new AccessException("[queryLayerInitAmout]: error", e);
		}
		return total;
	}

	@Override
	public Long queryLayerQueueAmout(TaskSnapshot taskSnapShot)
			throws AccessException {
		Long total = 0L;
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryLayerQueueCount", taskSnapShot);
		} catch (Throwable e) {
			throw new AccessException("[queryLayerQueueAmout]: error", e);
		}
		return total;
	}

	@Override
	public Long queryLayerStartAmout(TaskSnapshot taskSnapShot)
			throws AccessException {
		Long total = 0L;
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryLayerStartCount", taskSnapShot);
		} catch (Throwable e) {
			throw new AccessException("[queryLayerStartAmout]: error", e);
		}
		return total;
	}

	@Override
	public Long queryLayerSuccessAmout(TaskSnapshot taskSnapShot)
			throws AccessException {
		Long total = 0L;
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryLayerSuccessCount", taskSnapShot);
		} catch (Throwable e) {
			throw new AccessException("[queryLayerSuccessAmout]: error", e);
		}
		return total;
	}

	@Override
	public Long queryLayerFailureAmout(TaskSnapshot taskSnapShot)
			throws AccessException {
		Long total = 0L;
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryLayerFailureCount", taskSnapShot);
		} catch (Throwable e) {
			throw new AccessException("[queryLayerFailureAmout]: error", e);
		}
		return total;
	}

	@Override
	public Long queryLayerFoundFailureAmout(TaskSnapshot taskSnapShot)
			throws AccessException {
		Long total = 0L;
		try {
			total = (Long) sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryLayerFailureCount", taskSnapShot);
		} catch (Throwable e) {
			throw new AccessException("[queryLayerFailureAmout]: error", e);
		}
		return total;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> queryTaskLayer(TaskSnapshot taskSnapShot)
			throws AccessException {
		List<String> layers = new LinkedList<String>();
		try {
			layers = sqlMapClients.getSqlMapClient().queryForList("TaskSnapshot.queryLayer", taskSnapShot);
		} catch (Throwable e) {
			throw new AccessException("[queryTaskLayer]: error", e);
		}
		return layers;
	}

	/**
	 * 查询ID列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryIdList(long jobInstanceId) throws AccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobInstanceId", jobInstanceId);
		map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), dbCount, tableCount));
		List<Long> idList = null;
		try {
			idList = sqlMapClients.getSqlMapClient().queryForList("TaskSnapshot.queryIdList", map);
		} catch (Throwable e) {
			throw new AccessException("[queryIdList]: error", e);
		}
		return idList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaskSnapshot> queryTaskSnapshotList(long jobInstanceId)
			throws AccessException {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobInstanceId", jobInstanceId);
		map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), dbCount, tableCount));

		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = (List<TaskSnapshot>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.queryTaskSnapshotList", map);
		} catch (Throwable e) {
			throw new AccessException("[queryTaskSnapshotList]: error", e);
		}
		return taskSnapshotList;
	}

	/**
	 * 根据重试次数查询ID列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryIdListByRetryCount(long jobInstanceId)
			throws AccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobInstanceId", jobInstanceId);
		map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), dbCount, tableCount));
		List<Long> idList = null;
		try {
			idList = sqlMapClients.getSqlMapClient().queryForList("TaskSnapshot.queryIdListByRetryCount", map);
		} catch (Throwable e) {
			throw new AccessException("[queryIdListByRetryCount]: error", e);
		}
		return idList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaskSnapshot> queryTaskSnapshotListByRetryCount(
			long jobInstanceId) throws AccessException {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobInstanceId", jobInstanceId);
		map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), dbCount, tableCount));

		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = (List<TaskSnapshot>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.queryTaskSnapshotListByRetryCount", map);
		} catch (Throwable e) {
			throw new AccessException("[queryTaskSnapshotListByRetryCount]: error", e);
		}
		return taskSnapshotList;
	}

	/**
	 * 获取统计任务列表
	 */
    @SuppressWarnings("unchecked")
	@Override
	public List<TaskSnapshot> aquireTaskList(TaskSnapshot query)
			throws AccessException {
    	List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = (List<TaskSnapshot>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.aquireTaskList", query);
		} catch (Throwable e) {
			throw new AccessException("[aquireTaskList]: error", e);
		}
		return taskSnapshotList;
	}

	@Override
    public int update(TaskSnapshot taskSnapshot) throws AccessException {
        return 0;
    }

    @Override
    public int delete(TaskSnapshot taskSnapshot) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClient()
					.delete("TaskSnapshot.delete", taskSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
    }

    /**
	 * 删除某个实例所有任务数据
	 */
	@Override
	public int delete4InstanceByIdList(long jobInstanceId, List<Long> idList) throws AccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobInstanceId", jobInstanceId);
		map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), dbCount, tableCount));
		map.put("idList", idList);
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClient()
					.delete("TaskSnapshot.delete4InstanceByIdList", map);
		} catch (Throwable e) {
			throw new AccessException("[delete4InstanceByIdList]: error", e);
		}
		return result;
	}

}
