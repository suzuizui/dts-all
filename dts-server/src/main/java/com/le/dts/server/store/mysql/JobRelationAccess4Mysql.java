package com.le.dts.server.store.mysql;

import java.util.List;

import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.JobRelationAccess;

/**
 * Job依赖关系访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class JobRelationAccess4Mysql implements JobRelationAccess, ServerContext {

	/**
	 * 插入
	 */
	public long insert(JobRelation jobRelation) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("JobRelation.insert", jobRelation);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

    @Override
    public List<JobRelation> queryRelation(JobRelation query) throws AccessException {
        List<JobRelation> jobRelationList = null;
        try {
            jobRelationList = (List<JobRelation>)sqlMapClients.getSqlMapClientMeta()
                    .queryForList("JobRelation.queryRelation", query);
        } catch (Throwable e) {
            throw new AccessException("[query]: error", e);
        }
        return jobRelationList;
    }

    @Override
	public List<JobRelation> queryBefore(JobRelation query) throws AccessException {
		List<JobRelation> jobRelationList = null;
		try {
			jobRelationList = (List<JobRelation>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobRelation.queryBefore", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobRelationList;
	}

	@Override
	public List<JobRelation> queryAfter(JobRelation query) throws AccessException {
		List<JobRelation> jobRelationList = null;
		try {
			jobRelationList = (List<JobRelation>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("JobRelation.queryAfter", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobRelationList;
	}

	/**
	 * 更新
	 */
	public int update(JobRelation jobRelation) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("JobRelation.update", jobRelation);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

    @Override
    public int updateFinishCount(JobRelation jobRelation) throws AccessException {
        int result = 0;
        try {
            result = sqlMapClients.getSqlMapClientMeta()
                    .update("JobRelation.updateFinishCount", jobRelation);
        } catch (Throwable e) {
            throw new AccessException("[update]: error", e);
        }
        return result;
    }

    @Override
    public int resetFinishCount(JobRelation jobRelation) throws AccessException {
        int result = 0;
        try {
            result = sqlMapClients.getSqlMapClientMeta()
                    .update("JobRelation.resetFinishCount", jobRelation);
        } catch (Throwable e) {
            throw new AccessException("[update]: error", e);
        }
        return result;
    }

    /**
	 * 删除
	 */
	public int delete(JobRelation jobRelation) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("JobRelation.delete", jobRelation);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

}
