package com.le.dts.console.store.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.console.store.JobInstanceSnapshotAccess;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.page.JobHistoryPageQuery;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 * Job实例快照访问接口 Mysql实现
 * 
 * @author tianyao.myc
 * 
 */
public class JobInstanceSnapshotAccess4Mysql implements
		JobInstanceSnapshotAccess {

	@Autowired
	private SqlMapClients sqlMapClients;
	
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

	/**
	 * 查询
	 */
	public JobInstanceSnapshot query(JobInstanceSnapshot query)
			throws AccessException {
		JobInstanceSnapshot instance = null;
		try {
			instance = (JobInstanceSnapshot) sqlMapClients
					.getSqlMapClientMeta().queryForObject(
							"JobInstanceSnapshot.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public List<JobInstanceSnapshot> queryWorking(long jobId)
			throws AccessException {
		List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
		JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
		jobInstanceSnapshot.setJobId(jobId);
		try {
			jobInstanceSnapshotList = (List<JobInstanceSnapshot>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.queryWorking", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[queryWorking]: error", e);
		}
		return jobInstanceSnapshotList;
	}
	
	/**
	 * 查询出所有的jobId
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryAllJobIdList() throws AccessException {
		List<Long> jobIdList = null;
		try {
			jobIdList = (List<Long>)sqlMapClients.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.queryAllJobIdList");
		} catch (Throwable e) {
			throw new AccessException("[queryAllJobIdList]: error", e);
		}
		return jobIdList;
	}

	/**
	 * 根据JobId查询要删除任务数据的实例ID列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryInstanceIdList4DeleteByJobId(
			JobInstanceSnapshot query) throws AccessException {
		List<Long> jobInstanceIdList = null;
		try {
			jobInstanceIdList = (List<Long>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.queryInstanceIdList4DeleteByJobId", query);
		} catch (Throwable e) {
			throw new AccessException("[queryInstanceIdList4DeleteByJobId]: error", e);
		}
		return jobInstanceIdList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryInstanceIdList4DeleteAllInstanceByJobId(
			JobInstanceSnapshot query) throws AccessException {
		List<Long> jobInstanceIdList = null;
		try {
			jobInstanceIdList = (List<Long>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.queryInstanceIdList4DeleteAllInstanceByJobId", query);
		} catch (Throwable e) {
			throw new AccessException("[queryInstanceIdList4DeleteAllInstanceByJobId]: error", e);
		}
		return jobInstanceIdList;
	}

	public int queryJobInstanceCount(long jobId) throws AccessException {
		int count = 0;
		try {
			count = (Integer) sqlMapClients
					.getSqlMapClientMeta().queryForObject(
							"JobInstanceSnapshot.historyCount", jobId);
		} catch (Throwable e) {
			throw new AccessException("[historyCount]: error", e);
		}
		return count;
	}

	/**
	 * 查询要删除任务数据的实例快照数量
	 */
	@Override
	public long queryDeleteCount(List<Integer> statusList) throws AccessException {
		Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("statusList", statusList);
        Long count = 0L;
		try {
			count = (Long)sqlMapClients
					.getSqlMapClientMeta().queryForObject(
							"JobInstanceSnapshot.queryDeleteCount", queryMap);
		} catch (Throwable e) {
			throw new AccessException("[queryDeleteCount]: error", e);
		}
		if(null == count) {
			return 0L;
		}
		return count;
	}

	/**
	 * 查询要删除任务数据的实例列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<JobInstanceSnapshot> queryAllInstanceList(
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
							"JobInstanceSnapshot.queryAllInstanceList", queryMap);
		} catch (Throwable e) {
			throw new AccessException("[queryAllInstanceList]: error", e);
		}
		return jobInstanceSnapshotList;
	}

    @SuppressWarnings("unchecked")
	public List<JobInstanceSnapshot> queryRuningInstanceList(List<Integer> statusList,
        long offset, int length, int bulkId, int bulkAmout) throws AccessException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("statusList", statusList);
        queryMap.put("offset", offset);
        queryMap.put("length", length);
        queryMap.put("bulkId", bulkId);
        queryMap.put("bulkAmout", bulkAmout);
        List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
        try {
            jobInstanceSnapshotList = (List<JobInstanceSnapshot>) sqlMapClients
                    .getSqlMapClientMeta().queryForList(
                            "JobInstanceSnapshot.queryRuningInstanceList", queryMap);
        } catch (Throwable e) {
            throw new AccessException("[queryRuningInstanceList]: error", e);
        }
        return jobInstanceSnapshotList;
    }

    public JobInstanceSnapshot queryLastInstance(JobInstanceSnapshot query) throws AccessException {
        JobInstanceSnapshot instanceSnapshot = null;
        try {
            instanceSnapshot = (JobInstanceSnapshot)sqlMapClients.getSqlMapClientMeta()
                    .queryForObject("JobInstanceSnapshot.queryLastInstance", query);
        } catch (Throwable e) {
            throw new AccessException("[queryLastInstance]: error", e);
        }
        return instanceSnapshot;
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

	@SuppressWarnings("unchecked")
	@Override
	public List<JobInstanceSnapshot> queryInstance4Stop(
			JobInstanceSnapshot query) throws AccessException {
		List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
		try {
			jobInstanceSnapshotList = (List<JobInstanceSnapshot>) sqlMapClients
					.getSqlMapClientMeta().queryForList(
							"JobInstanceSnapshot.queryInstance4Stop", query);
		} catch (Throwable e) {
			throw new AccessException("[queryInstance4Stop]: error", e);
		}
		return jobInstanceSnapshotList;
	}

	/**
	 * 更新
	 */
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

	/**
	 * 更新实例状态
	 */
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
	 * 更新实例状态
	 */
	@Override
	public int updateInstanceStatus4JobId(
			JobInstanceSnapshot jobInstanceSnapshot) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobInstanceSnapshot.updateInstanceStatus4JobId", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateInstanceStatus4JobId]: error", e);
		}
		return result;
	}

	/**
	 * 更新下一次重试信息
	 */
	@Override
	public int updateInstanceNext(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobInstanceSnapshot.updateInstanceNext", jobInstanceSnapshot);
		} catch (Throwable e) {
			throw new AccessException("[updateInstanceNext]: error", e);
		}
		return result;
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
	
	/**
	 * 根据IdList删除
	 */
	@Override
	public int deleteInstanceByIdList(List<Long> idList) throws AccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("idList", idList);
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("JobInstanceSnapshot.deleteInstanceByIdList", map);
		} catch (Throwable e) {
			throw new AccessException("[deleteInstanceByIdList]: error", e);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<JobInstanceSnapshot> pageQuery(JobHistoryPageQuery query)
			throws AccessException {
		List<JobInstanceSnapshot> lists;
		try {
			lists = sqlMapClients.getSqlMapClientMeta().queryForList("JobInstanceSnapshot.pageQuery", query);
		} catch (Exception e) {
			throw new AccessException("[pageQuery]:JobInstanceSnapshot error", e);
		}
		return lists;
	}

}
