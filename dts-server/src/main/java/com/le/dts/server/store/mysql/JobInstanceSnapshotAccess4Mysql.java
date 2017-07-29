package com.le.dts.server.store.mysql;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.JobInstanceSnapshotAccess;

/**
 * Job实例快照访问接口 Mysql实现
 * 
 * @author tianyao.myc
 * 
 */
public class JobInstanceSnapshotAccess4Mysql implements
		JobInstanceSnapshotAccess, ServerContext {

	/**
	 * 插入
	 */
	public long insert(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		Long result = null;
		try {
			result = (Long) sqlMapClients.getSqlMapClientMeta().insert(
					"JobInstanceSnapshot.insert", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if (null == result) {
			return 0L;
		}
		return result;
	}

    @Override public JobInstanceSnapshot get(long id) throws AccessException {
        JobInstanceSnapshot query = new JobInstanceSnapshot();
        query.setId(id);
        return (JobInstanceSnapshot) sqlMapClients.getSqlMapClientMeta().queryForObject("JobInstanceSnapshot.get", query);
    }

    /**
     * 查询最新的一条记录
     */
    @Override
	public JobInstanceSnapshot queryNewestInstance(long jobId)
			throws AccessException {
    	JobInstanceSnapshot jobInstanceSnapshot = null;
    	JobInstanceSnapshot query = new JobInstanceSnapshot();
        query.setJobId(jobId);
        
        try {
			jobInstanceSnapshot = (JobInstanceSnapshot)sqlMapClients.getSqlMapClientMeta().queryForObject("JobInstanceSnapshot.queryNewestInstance", query);
		} catch (Throwable e) {
			throw new AccessException("[queryNewestInstance]: error", e);
		}
        
		return jobInstanceSnapshot;
	}

	/**
	 * 查询
	 */
	@SuppressWarnings("unchecked")
	public List<JobInstanceSnapshot> query(JobInstanceSnapshot query)
			throws AccessException {
		List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
		try {
			jobInstanceSnapshotList = (List<JobInstanceSnapshot>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobInstanceSnapshotList;
	}

	@SuppressWarnings("unchecked")
	public List<JobInstanceSnapshot> queryWorking(long jobId)
			throws AccessException {
		List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
		try {
			jobInstanceSnapshotList = (List<JobInstanceSnapshot>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.queryWorking", jobId);
		} catch (Throwable e) {
			throw new AccessException("[queryWorking]: error", e);
		}
		return jobInstanceSnapshotList;
	}

	/**
	 * 查询用户自定义全局变量
	 */
	@Override
	public JobInstanceSnapshot queryInstanceGlobal(JobInstanceSnapshot query)
			throws AccessException {
		JobInstanceSnapshot jobInstanceSnapshot = null;
		try {
			jobInstanceSnapshot = (JobInstanceSnapshot)sqlMapClients.getSqlMapClientMeta()
					.queryForObject("JobInstanceSnapshot.queryInstanceGlobal", query);
		} catch (Throwable e) {
			throw new AccessException("[queryInstanceGlobal]: error", e);
		}
		return jobInstanceSnapshot;
	}

	/**
	 * 查询正在运行的实例数量
	 */
	@Override
	public long queryWorkingJobInstanceAmount(JobInstanceSnapshot query)
			throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.queryForObject("JobInstanceSnapshot.queryWorkingJobInstanceAmount", query);
		} catch (Throwable e) {
			throw new AccessException("[queryWorkingJobInstanceAmount]: error", e);
		}
		if(null == result) {
			return 0;
		}
		return result;
	}

	/**
	 * 查询需要重试的记录数量
	 */
	@Override
	public long queryRetryCount(List<Integer> statusList)
			throws AccessException {
		Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("statusList", statusList);
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.queryForObject("JobInstanceSnapshot.queryRetryCount", queryMap);
		} catch (Throwable e) {
			throw new AccessException("[queryRetryCount]: error", e);
		}
		if(null == result) {
			return 0;
		}
		return result;
	}

	/**
	 * 查询要重试的实例列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<JobInstanceSnapshot> queryRetryInstanceList(
			List<Integer> statusList, long offset, int length)
			throws AccessException {
		Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("statusList", statusList);
        queryMap.put("offset", offset);
        queryMap.put("length", length);
		List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
		try {
			jobInstanceSnapshotList = (List<JobInstanceSnapshot>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.queryRetryInstanceList", queryMap);
		} catch (Throwable e) {
			throw new AccessException("[queryRetryInstanceList]: error", e);
		}
		return jobInstanceSnapshotList;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<JobInstanceSnapshot> queryInstanceListPaging(long jobId,
			long lastId) throws AccessException {
		Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jobId", jobId);
        queryMap.put("lastId", lastId);
		List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
		try {
			jobInstanceSnapshotList = (List<JobInstanceSnapshot>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.queryInstanceListPaging", queryMap);
		} catch (Throwable e) {
			throw new AccessException("[queryInstanceListPaging]: error", e);
		}
		return jobInstanceSnapshotList;
	}

	/**
	 * 更新
	 */
	@SuppressWarnings("deprecation")
	public int update(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta().update(
					"JobInstanceSnapshot.update", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int updateLockAndOffset(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta().update(
					"JobInstanceSnapshot.updateLockAndOffset", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateLockAndOffset]: error", e);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int updateInitInstance(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta().update(
					"JobInstanceSnapshot.updateInitInstance", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateInitInstance]: error", e);
		}
		return result;
	}

	/**
	 * 修改用户自定义全局变量
	 */
	@Override
	public int updateInstanceGlobal(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobInstanceSnapshot.updateInstanceGlobal", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

    @Override public boolean updateOffsetWithLock(long id, long offset, long originalVersion) throws AccessException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("id", id);
        queryMap.put("offset", offset);
        queryMap.put("lockVersion", originalVersion + 1);
        queryMap.put("originalLockVersion", originalVersion);
        try {
            long count = sqlMapClients.getSqlMapClientMeta().update("JobInstanceSnapshot.updateOffsetWithLock", queryMap);
            if (count < 1)
                return false;
            else
                return true;
        } catch (Exception e) {
            throw new AccessException(e);
        }
    }

    @Override public boolean setLockedWithCondition(long id, Date threshold, long offset) throws AccessException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("id", id);
        queryMap.put("offset", offset);
        queryMap.put("gmtLocked", threshold);
        try {
            long count = sqlMapClients.getSqlMapClientMeta().update("JobInstanceSnapshot.setLockedWithCondition", queryMap);
            if (count < 1)
                return false;
            else
                return true;
        } catch (Exception e) {
            throw new AccessException(e);
        }
    }

    /**
     * 更新实例运行结果
     */
    @Override
	public int updateJobInstanceResult(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
    	int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobInstanceSnapshot.updateJobInstanceResult", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateJobInstanceResult]: error", e);
		}
		return result;
	}

    /**
     * 更新实例状态
     */
	@SuppressWarnings("deprecation")
	@Override
	public int updateInstanceStatus(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobInstanceSnapshot.updateInstanceStatus", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateInstanceStatus]: error", e);
		}
		return result;
	}

	/**
	 * 更新失败实例状态
	 */
	@SuppressWarnings("deprecation")
	@Override
	public int updateFailureInstanceStatus(
			JobInstanceSnapshot jobInstanceSnapshot) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobInstanceSnapshot.updateFailureInstanceStatus", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateFailureInstanceStatus]: error", e);
		}
		return result;
	}

	/**
	 * 更新锁
	 */
	@Override
	public int updateInstanceLock(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobInstanceSnapshot.updateInstanceLock", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateInstanceLock]: error", e);
		}
		return result;
	}

	/**
	 * 更新通知版本
	 */
	@Override
	public int updateNotifyVersion(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobInstanceSnapshot.updateNotifyVersion", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateNotifyVersion]: error", e);
		}
		return result;
	}

    @Override
    public int updateRelationTag(long id, long lastJobId, long afterJobId) throws AccessException {
    	
    	Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("id", id);
        queryMap.put("lastJobId", lastJobId);
        queryMap.put("afterJobId", afterJobId);
    	
        int result = 0;
        try {
            result = sqlMapClients.getSqlMapClientMeta()
                    .update("JobInstanceSnapshot.updateRelationTag", queryMap);
        } catch (Throwable e) {
            throw new AccessException("[updateRelationTag]: error", e);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
	@Override
	public int updateHandleUnfinishVersion(
			JobInstanceSnapshot jobInstanceSnapshot) throws AccessException {
		
    	int result = 0;
        try {
            result = sqlMapClients.getSqlMapClientMeta()
                    .update("JobInstanceSnapshot.updateHandleUnfinishVersion", jobInstanceSnapshot);
        } catch (Throwable e) {
            throw new AccessException("[updateHandleUnfinishVersion]: error", e);
        }
        return result;
	}

	/**
	 * 删除
	 */
	public int delete(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta().delete(
					"JobInstanceSnapshot.delete", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

	@Override
	public JobInstanceSnapshot findByJobIdAndFireTime(long jobId,
			String fireTime) throws AccessException {
		JobInstanceSnapshot queryObj = new JobInstanceSnapshot();
		queryObj.setJobId(jobId);
		queryObj.setFireTime(fireTime);

		try {
			return (JobInstanceSnapshot) sqlMapClients.getSqlMapClientMeta()
					.queryForObject(
							"JobInstanceSnapshot.findByJobIdAndFireTime",
							queryObj);
		} catch (Exception e) {
			throw new AccessException("fail to find job instance", e);
		}
	}

}
