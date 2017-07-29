package com.le.dts.console.store.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.console.store.JobAccess;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.Job;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.page.JobPageQuery;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 * Job信息访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class JobAccess4Mysql implements JobAccess {

	@Autowired
	private SqlMapClients sqlMapClients;
	
	/**
	 * 插入
	 */
	public long insert(Job job) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("Job.insert", job);
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
	public List<Job> pageQuery(JobPageQuery jobPage) throws AccessException {
		List<Job> jobList = null;
		try {
			jobList = (List<Job>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("Job.pageQuery", jobPage);
		} catch (Throwable e) {
			throw new AccessException("[pageQuery]: error", e);
		}
		return jobList;
	}

	/**
	 * 根据id查询Job
	 */
	@Override
	public Job queryJobById(Job query) throws AccessException {
		Job job = null;
		try {
			job = (Job)sqlMapClients.getSqlMapClientMeta()
					.queryForObject("Job.queryJobById", query);
		} catch (Throwable e) {
			throw new AccessException("[queryJobById]: error", e);
		}
		return job;
	}

	/**
	 * 更新
	 */
	public int update(Job job) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("Job.update", job);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

    @Override
    public int updateJobArguments(Job job) throws AccessException {
        int result = 0;
        try {
            result = sqlMapClients.getSqlMapClientMeta()
                    .update("Job.updateJobArguments", job);
        } catch (Throwable e) {
            throw new AccessException("[updateJobArguments]: error", e);
        }
        return result;
    }

    /**
	 * 更新JobStatus
	 */
	@Override
	public int updateJobStatus(Job job) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("Job.updateJobStatus", job);
		} catch (Throwable e) {
			throw new AccessException("[update]: error", e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	public int delete(Job job) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("Job.delete", job);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

	@Override
	public int countJob(Job query) throws AccessException {
		int result = 0;
		try {
			result = (Integer) sqlMapClients.getSqlMapClientMeta()
					.queryForObject("Job.jobCount", query);
		} catch (Throwable e) {
			throw new AccessException("[jobCount]: error", e);
		}
		return result;
	}

    /**
     * 计数启用Job;
     * @return
     */
    public long countEnableJob() throws AccessException {
        long result = 0;
        try {
            result = (Long) sqlMapClients.getSqlMapClientMeta()
                    .queryForObject("Job.jobEnableCount");
        } catch (Throwable e) {
            throw new AccessException("[jobCount]: error", e);
        }
        return result;
    }

    /**
     * 查询启用Job;
     * @param offset
     * @param length
     * @param bulkId
     * @param bulkAmout
     * @return
     * @throws AccessException
     */
    @SuppressWarnings("unchecked")
	public List<Job> queryEnableList(long offset, int length, int bulkId, int bulkAmout) throws AccessException {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("offset", offset);
        queryMap.put("length", length);
        queryMap.put("bulkId", bulkId);
        queryMap.put("bulkAmout", bulkAmout);
        List<Job> jobList = null;
        try {
            jobList = (List<Job>) sqlMapClients
                    .getSqlMapClientMeta().queryForList(
                            "Job.queryEnableList", queryMap);
        } catch (Throwable e) {
            throw new AccessException("[queryEnableList]: error", e);
        }
        return jobList;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Job> queryAllEnableJobList(long offset) throws AccessException {
		Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("offset", offset);
        List<Job> jobList = null;
        try {
            jobList = (List<Job>) sqlMapClients
                    .getSqlMapClientMeta().queryForList(
                            "Job.queryAllEnableJobList", queryMap);
        } catch (Throwable e) {
            throw new AccessException("[queryAllEnableJobList]: error", e);
        }
        return jobList;
	}

	@SuppressWarnings("unchecked")
	public List<Job> query(Job query) throws AccessException {
		List<Job> jobList = null;
		try {
			jobList = (List<Job>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("Job.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return jobList;
	}

}
