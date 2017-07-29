package com.le.dts.server.store.hbase;

import java.util.List;
import java.util.Map;

import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.TaskSnapshotAccess;

/**
 * 任务快照访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class TaskSnapshotAccess4Hbase implements TaskSnapshotAccess {

	/**
	 * 插入
	 */
	public long insert(TaskSnapshot taskSnapshot) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override 
    public int insertBatch(List<TaskSnapshot> snapshots) throws AccessException {
    	return 0;
    }

    /**
	 * 查询
     * @param query
     */
	public List<TaskSnapshot> queryByJobInstanceIdAndStatus(Map<String, Object> query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
	public List<TaskSnapshot> queryByJobInstanceIdAndStatus4Unfinish(
			Map<String, Object> query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TaskSnapshot> querySkipTaskList(long jobInstanceId, int status, long start, long offset)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override public List<TaskSnapshot> queryAvailableTaskPage(long jobInstanceId, long offset, int pageSize)
            throws AccessException {
        return null;
    }

    @Override public List<TaskSnapshot> queryRetryTaskPage(long jobInstanceSnapshotId, int jobInstanceRetryCount,
            long offset, int pageSize)
            throws AccessException {
        return null;
    }

    @Override
	public long queryAvailableTaskPageCount(long jobInstanceId, long offset,
			int pageSize) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override public List<TaskSnapshot> queryTaskPageByStatus(long jobInstanceSnapshotId, int pageSize,
            List<Integer> status) throws AccessException {
        return null;
    }

    @Override
	public long queryTaskSnapshotRetryCount(TaskSnapshot query)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long queryTotalCount(TaskSnapshot query) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long queryItemCount(TaskSnapshot query) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询重试记录数量
	 */
	@Override
	public long queryRetryCount(TaskSnapshot query) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> queryTaskNameList(TaskSnapshot query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long queryDetailTotalCount(TaskSnapshot query)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long queryDetailItemCount(TaskSnapshot query) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override public boolean queryExistsStatus(long jobInstanceId, List<Integer> statusList) throws AccessException {
        return false;
    }

    @Override public List<TaskSnapshot> queryByStatusAndClient(long jobInstanceId, String clientId,
            List<Integer> statusList) throws AccessException {
        return null;
    }

    /**
	 * 更新
	 */
	public int update(TaskSnapshot taskSnapshot) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
	public int updateStatusAndRetryCount(TaskSnapshot taskSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override 
    public void updateStatusBatch(List<TaskSnapshot> snapshotList, int status) throws AccessException {

    }

    @Override public int setFailureAndRetryCountBatch(List<TaskSnapshot> snapshotList, int retryCount) throws AccessException {
    	return 0;
    }
    
	@Override 
    public int updateClientIdBatch(List<TaskSnapshot> taskSnapshotList, String clientId, int status) throws AccessException {
		return 0;
    }

    @Override
	public int updateTaskSnapshot(TaskSnapshot taskSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	public int delete(TaskSnapshot taskSnapshot) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Long> queryIdList(long jobInstanceId) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> queryIdListByRetryCount(long jobInstanceId)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取统计任务列表
	 */
	@Override
	public List<TaskSnapshot> aquireTaskList(TaskSnapshot query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete4InstanceByIdList(long jobInstanceId, List<Long> idList)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

}
