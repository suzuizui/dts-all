package com.le.dts.server.store.hbase;

import java.util.Date;
import java.util.List;

import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.JobInstanceSnapshotAccess;

/**
 * Job实例快照访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class JobInstanceSnapshotAccess4Hbase implements
		JobInstanceSnapshotAccess {

	/**
	 * 插入
	 */
	public long insert(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override public JobInstanceSnapshot get(long id) throws AccessException {
        return null;
    }

    @Override
	public JobInstanceSnapshot queryNewestInstance(long jobId)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 查询
	 */
	public List<JobInstanceSnapshot> query(JobInstanceSnapshot query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 查询用户自定义全局变量
	 */
	@Override
	public JobInstanceSnapshot queryInstanceGlobal(JobInstanceSnapshot query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 查询正在运行的实例数量
	 */
	@Override
	public long queryWorkingJobInstanceAmount(JobInstanceSnapshot query)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询需要重试的记录数量
	 */
	@Override
	public long queryRetryCount(List<Integer> statusList)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询要重试的实例列表
	 */
	@Override
	public List<JobInstanceSnapshot> queryRetryInstanceList(
			List<Integer> statusList, long offset, int length)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobInstanceSnapshot> queryInstanceListPaging(long jobId,
			long lastId) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 更新
	 */
	public int update(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateInitInstance(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 修改用户自定义全局变量
	 */
	@Override
	public int updateInstanceGlobal(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override public boolean updateOffsetWithLock(long id, long offset, long originalVersion) throws AccessException {
        return false;
    }

    @Override public boolean setLockedWithCondition(long id, Date threshold, long offset) throws AccessException {
        return false;
    }

    @Override
	public int updateLockAndOffset(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
     * 更新实例运行结果
     */
    @Override
	public int updateJobInstanceResult(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    /**
     * 更新实例状态
     */
	@Override
	public int updateInstanceStatus(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 更新失败实例状态
	 */
	@Override
	public int updateFailureInstanceStatus(
			JobInstanceSnapshot jobInstanceSnapshot) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 更新锁
	 */
	@Override
	public int updateInstanceLock(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 更新通知版本
	 */
	@Override
	public int updateNotifyVersion(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public int updateRelationTag(long id, long lastJobId, long afterJobId) throws AccessException {
        return 0;
    }

    @Override
	public int updateHandleUnfinishVersion(
			JobInstanceSnapshot jobInstanceSnapshot) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 删除
	 */
	public int delete(JobInstanceSnapshot jobInstanceSnapshot)
			throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override public JobInstanceSnapshot findByJobIdAndFireTime(long jobId, String fireTime) throws AccessException {
        return null;
    }

	@Override
	public List<JobInstanceSnapshot> queryWorking(long query)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
