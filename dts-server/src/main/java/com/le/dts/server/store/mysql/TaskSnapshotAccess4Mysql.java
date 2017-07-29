package com.le.dts.server.store.mysql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.common.util.DBShardUtil;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.TaskSnapshotAccess;

/**
 * 任务快照访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class TaskSnapshotAccess4Mysql implements TaskSnapshotAccess, ServerContext {

	private static int DB_COUNT;
	private static int TABLE_COUNT;

	public static TaskSnapshotAccess newInstance() {
		DB_COUNT = serverConfig.getDynamicDBCount();
		TABLE_COUNT = serverConfig.getDynamicTableCount();

		final TaskSnapshotAccess taskSnapshotAccess4Mysql = new TaskSnapshotAccess4Mysql();
		return (TaskSnapshotAccess)Proxy.newProxyInstance(
				//被代理类的ClassLoader
				taskSnapshotAccess4Mysql.getClass().getClassLoader(),
				//要被代理的接口,本方法返回对象会自动声称实现了这些接口
				taskSnapshotAccess4Mysql.getClass().getInterfaces(),
				//代理处理器对象
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if("toString".equals(method.getName()))
							return "TaskSnapshotAccess Proxy";

						String shardKey;
						if(args[0] instanceof TaskSnapshot) {
							TaskSnapshot taskSnapshot = (TaskSnapshot)args[0];
							taskSnapshot.setTableIndex(DBShardUtil.getTableShard(String.valueOf(taskSnapshot.getJobInstanceId()), DB_COUNT, TABLE_COUNT));
							shardKey = String.valueOf(taskSnapshot.getJobInstanceId());
						} else if(args[0] instanceof List) {
							List<TaskSnapshot> list = (List<TaskSnapshot>)args[0];
							shardKey = String.valueOf(list.get(0).getJobInstanceId());
						} else if(args[0] instanceof Map) {
							Map map = (Map)args[0];
							map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(map.get("jobInstanceId")), DB_COUNT, TABLE_COUNT));
							shardKey = String.valueOf(map.get("jobInstanceId"));
						} else if(args[0] instanceof Long) {
							shardKey = String.valueOf(args[0]);
						} else {
							throw new RuntimeException("TaskSnapshotAccess4Mysql Proxy: Param Error");
						}
						DBShardUtil.setShardThreadLocal(shardKey + "," + DB_COUNT + "," + TABLE_COUNT);
						return method.invoke(taskSnapshotAccess4Mysql, args);
					}
				});
	}

	/**
	 * 插入
	 */
	@SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
	@Override 
	public int insertBatch(List<TaskSnapshot> snapshots) throws AccessException {
    	
    	int result = 0;
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(snapshots.get(0).getJobInstanceId()), DB_COUNT, TABLE_COUNT));
		queryMap.put("snapshots", snapshots);
        try {
        	result = sqlMapClients.getSqlMapClient().update("TaskSnapshot.insertBatch", queryMap);
        } catch (Throwable e) {
            throw new AccessException(e);
        }
        
        return result;
    }

    /**
	 * 查询
     * @param query
     */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<TaskSnapshot> queryByJobInstanceIdAndStatus(Map<String, Object> query) throws AccessException {
		
		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = (List<TaskSnapshot>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.queryByJobInstanceIdAndStatus", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		
		return taskSnapshotList;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<TaskSnapshot> queryByJobInstanceIdAndStatus4Unfinish(
			Map<String, Object> query) throws AccessException {
		
		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = (List<TaskSnapshot>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.queryByJobInstanceIdAndStatus4Unfinish", query);
		} catch (Throwable e) {
			throw new AccessException("[queryByJobInstanceIdAndStatus4Unfinish]: error", e);
		}
		
		return taskSnapshotList;
	}

	/**
	 * 查询跳过的任务列表
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<TaskSnapshot> querySkipTaskList(long jobInstanceId, int status, long start, long offset)
			throws AccessException {
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jobInstanceId", jobInstanceId);
		queryMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
        queryMap.put("status", status);
        queryMap.put("start", start);
        queryMap.put("offset", offset);
		
		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = (List<TaskSnapshot>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.querySkipTaskList", queryMap);
		} catch (Throwable e) {
			throw new AccessException("[querySkipTaskList]: error", e);
		}
		
		return taskSnapshotList;
	}

	/**
	 * 重试Count
	 */
	@SuppressWarnings("deprecation")
	@Override
	public long queryTaskSnapshotRetryCount(TaskSnapshot query)
			throws AccessException {
		
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryTaskSnapshotRetryCount", query);
		} catch (Throwable e) {
			throw new AccessException("[queryTaskSnapshotRetryCount]: error", e);
		}
		if(null == result) {
			
			return 0;
		}
		
		return result;
	}

	/**
	 * 查询重试记录数量
	 */
    @SuppressWarnings("deprecation")
	@Override
	public long queryRetryCount(TaskSnapshot query) throws AccessException {
    	
    	Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryRetryCount", query);
		} catch (Throwable e) {
			throw new AccessException("[queryRetryCount]: error", e);
		}
		if(null == result) {
			
			return 0;
		}
		
		return result;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override 
    public List<TaskSnapshot> queryAvailableTaskPage(long jobInstanceSnapshotId, long offset, int pageSize)
            throws AccessException {
		
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jobInstanceId", jobInstanceSnapshotId);
		queryMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceSnapshotId), DB_COUNT, TABLE_COUNT));
        queryMap.put("limit", pageSize);
        queryMap.put("offset", offset);
        
        List<TaskSnapshot> taskList = sqlMapClients.getSqlMapClient().queryForList("TaskSnapshot.queryAvailableTaskPage", queryMap);

        return taskList;
    }

	/**
	 * 从偏移量开始count
	 */
    @SuppressWarnings("deprecation")
	@Override
	public long queryAvailableTaskPageCount(long jobInstanceId, long offset,
			int pageSize) throws AccessException {
    	
    	Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jobInstanceId", jobInstanceId);
		queryMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
        queryMap.put("limit", pageSize);
        queryMap.put("offset", offset);
        
        Long count = (Long)sqlMapClients.getSqlMapClient().queryForObject("TaskSnapshot.queryAvailableTaskPageCount", queryMap);
        
        if(null == count) {
        	
        	return 0L;
        }
        
        return count;
	}

	@Override public List<TaskSnapshot> queryRetryTaskPage(long jobInstanceSnapshotId, int jobInstanceRetryCount,
            long offset, int pageSize)
            throws AccessException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jobInstanceId", jobInstanceSnapshotId);
		queryMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceSnapshotId), DB_COUNT, TABLE_COUNT));
        queryMap.put("retryCount", jobInstanceRetryCount);
        queryMap.put("status", Constants.JOB_INSTANCE_STATUS_FAILED);
        queryMap.put("limit", pageSize);
        queryMap.put("offset", offset);
        return sqlMapClients.getSqlMapClient().queryForList("TaskSnapshot.queryRetryTaskPage", queryMap);
    }

    @SuppressWarnings("unchecked")
	@Override 
    public List<TaskSnapshot> queryTaskPageByStatus(long jobInstanceSnapshotId, int pageSize,
            List<Integer> statusList) throws AccessException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("instanceId", jobInstanceSnapshotId);
		queryMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceSnapshotId), DB_COUNT, TABLE_COUNT));
        queryMap.put("statusList", statusList);
        queryMap.put("limit", pageSize);
        return sqlMapClients.getSqlMapClient().queryForList("TaskSnapshot.queryTaskPageByStatus", queryMap);
    }

    @Override
	public long queryTotalCount(TaskSnapshot query) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryTotalCount", query);
		} catch (Throwable e) {
			throw new AccessException("[queryTotalCount]: error", e);
		}
		if(null == result) {
			return 0;
		}
		return result;
	}

	@Override
	public long queryItemCount(TaskSnapshot query) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryItemCount", query);
		} catch (Throwable e) {
			throw new AccessException("[queryItemCount]: error", e);
		}
		if(null == result) {
			return 0;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> queryTaskNameList(TaskSnapshot query)
			throws AccessException {
		List<String> taskNameList = null;
		try {
			taskNameList = (List<String>)sqlMapClients.getSqlMapClient()
					.queryForList("TaskSnapshot.queryTaskNameList", query);
		} catch (Throwable e) {
			throw new AccessException("[queryTaskNameList]: error", e);
		}
		return taskNameList;
	}

	@Override
	public long queryDetailTotalCount(TaskSnapshot query)
			throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryDetailTotalCount", query);
		} catch (Throwable e) {
			throw new AccessException("[queryDetailTotalCount]: error", e);
		}
		if(null == result) {
			return 0;
		}
		return result;
	}

	@Override
	public long queryDetailItemCount(TaskSnapshot query) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClient()
					.queryForObject("TaskSnapshot.queryDetailItemCount", query);
		} catch (Throwable e) {
			throw new AccessException("[queryDetailItemCount]: error", e);
		}
		if(null == result) {
			return 0;
		}
		return result;
	}

    @Override public boolean queryExistsStatus(long jobInstanceId, List<Integer> statusList) throws AccessException {
        try {
            Map<String, Object> queryObj = new HashMap<String, Object>();
            queryObj.put("instanceId", jobInstanceId);
			queryObj.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
            queryObj.put("statusList", statusList);
            Object result = sqlMapClients.getSqlMapClient().queryForObject("TaskSnapshot.queryExistsStatus", queryObj);
            if (result == null)
                return false;
            else
                return true;
        } catch (Exception e) {
            throw new AccessException("[queryExistsStatus]: error", e);
        }
    }

    @SuppressWarnings("unchecked")
	@Override 
    public List<TaskSnapshot> queryByStatusAndClient(long jobInstanceId, String clientId,
            List<Integer> statusList) throws AccessException {
        try {
            Map<String, Object> queryObj = new HashMap<String, Object>();
            queryObj.put("instanceId", jobInstanceId);
			queryObj.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
            queryObj.put("statusList", statusList);
            queryObj.put("clientId", clientId);
            return (List<TaskSnapshot>) sqlMapClients.getSqlMapClient()
                    .queryForList("TaskSnapshot.queryByStatusAndClient", queryObj);
        } catch (Exception e) {
            throw new AccessException("[queryByStatusAndClient]: error", e);
        }
    }
    
    /**
	 * 查询ID列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryIdList(long jobInstanceId) throws AccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobInstanceId", jobInstanceId);
		map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
		List<Long> idList = null;
		try {
			idList = sqlMapClients.getSqlMapClient().queryForList("TaskSnapshot.queryIdList", map);
		} catch (Throwable e) {
			throw new AccessException("[queryIdList]: error", e);
		}
		return idList;
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
		map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
		List<Long> idList = null;
		try {
			idList = sqlMapClients.getSqlMapClient().queryForList("TaskSnapshot.queryIdListByRetryCount", map);
		} catch (Throwable e) {
			throw new AccessException("[queryIdListByRetryCount]: error", e);
		}
		return idList;
	}

    /**
	 * 更新
	 */
	@SuppressWarnings("deprecation")
	public int update(TaskSnapshot taskSnapshot) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClient()
					.update("TaskSnapshot.update", taskSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

    @SuppressWarnings("deprecation")
	@Override
	public int updateStatusAndRetryCount(TaskSnapshot taskSnapshot)
			throws AccessException {
    	int result = 0;
		try {
			result = sqlMapClients.getSqlMapClient()
					.update("TaskSnapshot.updateStatusAndRetryCount", taskSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateStatusAndRetryCount]: error", e);
		}
		return result;
	}

	@Override 
    public void updateStatusBatch(List<TaskSnapshot> snapshotList, int status) throws AccessException {
        try {
            if (snapshotList.isEmpty())
                return;
            long jobInstanceId = snapshotList.get(0).getJobInstanceId();
        	Map<String, Object> updateMap = new HashMap<String, Object>();
        	updateMap.put("jobInstanceId", jobInstanceId);
			updateMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
        	updateMap.put("status", status);
            updateMap.put("idList", getIdList(snapshotList));
            sqlMapClients.getSqlMapClient()
                    .update("TaskSnapshot.updateStatusBatch", updateMap);
        } catch (Exception e) {
            throw new AccessException(e);
        }
    }

    @SuppressWarnings("deprecation")
	@Override 
    public int setFailureAndRetryCountBatch(List<TaskSnapshot> snapshotList, int retryCount) throws AccessException {
        try {
            if (snapshotList.isEmpty())
                return 0;
            long jobInstanceId = snapshotList.get(0).getJobInstanceId();
            Map<String, Object> updateMap = new HashMap<String, Object>();
            updateMap.put("jobInstanceId", jobInstanceId);
			updateMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
            updateMap.put("status", Constants.TASK_STATUS_FAILURE);
            updateMap.put("retryCount", retryCount);
            updateMap.put("idList", getIdList(snapshotList));
            return sqlMapClients.getSqlMapClient().update("TaskSnapshot.setFailureAndRetryCountBatch", updateMap);
        } catch (Exception e) {
            throw new AccessException(e);
        }
    }

    private List<Long> getIdList(List<TaskSnapshot> taskSnapshotList) {
        List<Long> result = new ArrayList<Long>();
        for (TaskSnapshot task : taskSnapshotList) {
            result.add(task.getId());
        }
        return result;
    }
    
    /**
	 * 获取统计任务列表
	 */
    @SuppressWarnings({ "unchecked", "deprecation" })
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
    public int updateClientIdBatch(List<TaskSnapshot> taskSnapshotList, String clientId, int status) throws AccessException {
        try {
            if (taskSnapshotList.isEmpty())
                return 0;
            long jobInstanceId = taskSnapshotList.get(0).getJobInstanceId();
        	Map<String, Object> updateMap = new HashMap<String, Object>();
        	updateMap.put("idList", getIdList(taskSnapshotList));
        	updateMap.put("clientId", clientId);
        	updateMap.put("status", status);
            updateMap.put("jobInstanceId", jobInstanceId);
			updateMap.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
            return sqlMapClients.getSqlMapClient().update("TaskSnapshot.updateClientIdBatch", updateMap);
        } catch (Exception e) {
            throw new AccessException(e);
        }
    }

    /**
     * 更新任务快照
     */
    @SuppressWarnings("deprecation")
	@Override
	public int updateTaskSnapshot(TaskSnapshot taskSnapshot)
			throws AccessException {
    	int result = 0;
		try {
			result = sqlMapClients.getSqlMapClient()
					.update("TaskSnapshot.updateTaskSnapshot", taskSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateTaskSnapshot]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
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
		map.put("tableIndex", DBShardUtil.getTableShard(String.valueOf(jobInstanceId), DB_COUNT, TABLE_COUNT));
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
