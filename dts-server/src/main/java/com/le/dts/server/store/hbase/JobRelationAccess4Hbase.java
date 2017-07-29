package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.JobRelationAccess;

/**
 * Job依赖关系访问接口
 * Hbase实现
 * @author tianyao.myc
 *
 */
public class JobRelationAccess4Hbase implements JobRelationAccess {

	/**
	 * 插入
	 */
	public long insert(JobRelation jobRelation) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public List<JobRelation> queryRelation(JobRelation query) throws AccessException {
        return null;
    }

    @Override
	public List<JobRelation> queryBefore(JobRelation query) throws AccessException {
		return null;
	}

	@Override
	public List<JobRelation> queryAfter(JobRelation query) throws AccessException {
		return null;
	}

	/**
	 * 查询
	 */
	public List<JobRelation> query(JobRelation query) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 更新
	 */
	public int update(JobRelation jobRelation) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public int updateFinishCount(JobRelation jobRelation) throws AccessException {
        return 0;
    }

    @Override
    public int resetFinishCount(JobRelation jobRelation) throws AccessException {
        return 0;
    }

    /**
	 * 删除
	 */
	public int delete(JobRelation jobRelation) throws AccessException {
		// TODO Auto-generated method stub
		return 0;
	}

}
