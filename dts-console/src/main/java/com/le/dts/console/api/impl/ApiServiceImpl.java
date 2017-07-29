package com.le.dts.console.api.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.le.dts.common.util.StringUtil;
import com.le.dts.console.config.ConsoleConfig;
import com.le.dts.console.global.Global;
import com.le.dts.console.page.JobHistoryPageQuery;
import com.le.dts.console.page.JobPageQuery;
import com.le.dts.console.store.JobInstanceSnapshotAccess;
import com.le.dts.console.store.UserGroupRelationAccess;
import com.le.dts.console.store.mysql.access.DataSource;
import com.le.dts.console.util.HistoryPageUtil;
import com.le.dts.console.util.UserEnvUtil;
import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.alibaba.citrus.util.collection.DefaultMapEntry;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ProgressBar;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.DesignatedMachine;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.JobOperation;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.domain.store.JobServerRelation;
import com.le.dts.common.domain.store.ServerGroup;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.domain.store.UserGroupRelation;
import com.le.dts.common.domain.store.WarningSetup;
import com.le.dts.common.domain.store.assemble.AssembledDesignatedMachine;
import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
import com.le.dts.common.domain.store.assemble.JobHistoryRecord;
import com.le.dts.common.domain.store.assemble.JobStatus;
import com.le.dts.common.domain.store.assemble.WarningNotifier;
import com.le.dts.common.exception.DtsTransactionException;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.job.OperationContent;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.util.RandomUtil;
import com.le.dts.console.api.ApiService;
import com.le.dts.console.manager.ClientGroupManager;
import com.le.dts.console.manager.ClusterManager;
import com.le.dts.console.manager.DesignatedMachineManager;
import com.le.dts.console.manager.JobInstanceManager;
import com.le.dts.console.manager.JobManager;
import com.le.dts.console.manager.JobOperationManager;
import com.le.dts.console.manager.JobRelationManager;
import com.le.dts.console.manager.ServerGroupManager;
import com.le.dts.console.manager.TaskSnapShotManager;
import com.le.dts.console.manager.UserGroupRelationManager;
import com.le.dts.console.manager.WarningSetupManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 服务端通用基础服务
 * 
 * @author tianyao.myc
 * 
 */
public class ApiServiceImpl implements ApiService {

	private static final Log logger = LogFactory.getLog(ApiServiceImpl.class);

	@Autowired
	private Zookeeper zookeeper;

	@Autowired
	public JobManager jobManager;

	/** 客户端集群管理器 */
	@Autowired
	public ClientGroupManager clientGroupManager;

	/** Server集群管理器 */
	@Autowired
	public ClusterManager clusterManager;
	@Autowired
	public JobInstanceManager jobInstanceManager;
	@Autowired
	public WarningSetupManager warningSetupManager;

	/** 用户任务执行快照 */
	@Autowired
	public TaskSnapShotManager taskSnapShotManager;

	@Autowired
	public ServerGroupManager serverGroupManager;

	/** 用户资源关系 */
	@Autowired
	private UserGroupRelationManager userGroupRelationManager;

    @Autowired
    private JobRelationManager jobRelationManager;

    @Autowired
    private JobOperationManager jobOperationManager;

    @Autowired
	private DataSource dataSource;

	@Autowired
	private HttpServletRequest request;

    @Autowired
    private DesignatedMachineManager designatedMachineManager;
    
	@Autowired
	private UserGroupRelationAccess userGroupRelationAccess;
	
	@Autowired
	private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
	
	@Autowired
	private ConsoleConfig consoleConfig;

    /**
     * 开通服务，在每个环境新增一个默认组;
     * @param userId
     * @return
     */
    @Override
    public Result<Boolean> initDtsService(String userId) {
        final Result<Boolean> result = new Result<Boolean>();
        final TreeMap<Long, Cluster> clusterTreeMap = getUserClusters();

        if(clusterTreeMap.size() > 0) {
            try {
            	dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction(TransactionStatus status) {
                        for(Cluster cluster: clusterTreeMap.values()) {
                            ClientGroup clientGroup = new ClientGroup();
                            clientGroup.setDescription(Global.getDtsUser(request).getUserName() + "的默认分组");
                            long clusterGroup = getClusterRandomGroup(cluster.getId());
                            if(clusterGroup == 0L) {
                                throw new DtsTransactionException("get clusterGroup error!");
                            }
                            clientGroup.setServerGroupId(clusterGroup);
                            Result<String> createResult = createGroup(clientGroup, cluster.getId());
                            if(createResult.getResultCode() != ResultCode.SUCCESS) {
                                result.setResultCode(createResult.getResultCode());
                                result.setData(false);
                                throw new DtsTransactionException("create client Group error!");
                            }
                        }
                        result.setResultCode(ResultCode.SUCCESS);
                        result.setData(true);
                        return result;
                    }
                });
            } catch (Throwable e) {
            	
            	String info = "[ApiServiceImpl]: initDtsService error"
                		+ ", userId:" + userId;
            	
                logger.error(info, e);
                
                result.setResultCode(ResultCode.FAILURE);
        		return result;
            }
        }
        return result;
    }

    /**
     * 随机得到一个集群的里面的Group;
     * @param clusterId
     * @return
     */
    public Long getClusterRandomGroup(long clusterId) {
        Result<List<ServerGroup>> result = getClusterGroups(clusterId);
        if(result.getResultCode() == ResultCode.SUCCESS) {
            ServerGroup selectGroup = RandomUtil.getRandomObj(result.getData());
            return selectGroup.getId();
        } else {
            return 0L;
        }
    }

    @Override
    public Cluster getCluster(long clusterId) {

        return consoleConfig.getServerClusterMap().get(clusterId);
    }

    /**
	 * 设置全局用户自定义参数
	 */
	@Override
	public Result<Boolean> setGlobalArguments(
			JobInstanceSnapshot jobInstanceSnapshot, byte[] globalArguments) {
		return jobInstanceManager.setGlobalArguments(jobInstanceSnapshot,
				globalArguments);
	}

	/**
	 * 获取设置的全局变量
	 */
	@Override
	public Result<byte[]> getGlobalArguments(
			JobInstanceSnapshot jobInstanceSnapshot) {
		return jobInstanceManager.getGlobalArguments(jobInstanceSnapshot);
	}

	/**
	 * 获取备份机器列表
	 */
	@Override
	public List<JobServerRelation> getBackupMachineList(Job job) {
		return jobManager.getBackupServerList(job);
	}

	/**
	 * 获取JobProcessor名称列表
	 */
	@Override
	public Result<List<String>> getJobProcessorNameList(String groupId) {
		return clientGroupManager.getJobProcessorNameList(groupId);
	}

	/**
	 * 创建内部Job和机器之间的映射关系
	 */
	public Result<Boolean> createJobServerRelation(Job job, String ip) {
		return jobManager.createJobServerRelation(job, ip);
	}

    /**
     * 删除Job关联的Server;
     * @param job
     * @return
     */
    public Result<Boolean> deleteJobServerRelation(Job job) {
        return jobManager.deleteJobServerRelation(job);
    }

	/**
	 * 创建持久化Job
	 */
	@Override
	public Result<Long> createJob(final Job job, final Cluster cluster) {
		final Result<Long> result = new Result<Long>();
        // 做校验;
        Result<Boolean> checkAuth = checkUserOwnResource(job.getClientGroupId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
		final List<String> ipList = zookeeper.getServerGroupIpList(String.valueOf(cluster.getId()), String.valueOf(job.getServerGroupId()));
		if (CollectionUtils.isEmpty(ipList)
				|| ipList.size() < cluster.getJobBackupAmount()) {
			logger.error("[ConsoleServiceImpl]: createJob ipList error"
					+ ", serverCluster:" + cluster.toString()
					+ ", ipList:" + ipList + ", job:" + job.toString());
			result.setResultCode(ResultCode.CLUSTER_SERVER_ERROR);
			return result;
		}

		try {
			dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					/** 持久化job配置信息 */
					Result<Long> createPersistenceResult = jobManager
							.createPersistenceJob(job);
					if (createPersistenceResult.getData().longValue() <= 0L) {
						logger.error("[ConsoleServiceImpl]: createPersistenceJob error"
								+ ", serverCluster:" + cluster.toString()
								+ ", ipList:" + ipList + ", createPersistenceResult:"
								+ createPersistenceResult.toString() + ", job:"
								+ job.toString());
						result.setResultCode(createPersistenceResult.getResultCode());
						throw new DtsTransactionException("create persistence job error!");
					}

					job.setId(createPersistenceResult.getData());

					/** 服务器列表排序 */
					Collections.sort(ipList);

					/** 获取随机索引下标 */
					int index = RandomUtil.getRandomIndex(ipList);

					List<String> selectList = new ArrayList<String>(
							cluster.getJobBackupAmount());
					int createSuccessCounter = 0;

					// 创建机器和Job的关系;
                    List<String> serverList = new ArrayList<String>(cluster.getJobBackupAmount());
					for (int i = 0; i < cluster.getJobBackupAmount(); i++) {
						int backupMachineIndex = (index + i) % ipList.size();
						String serverIp = ipList.get(backupMachineIndex);
						selectList.add(serverIp);
						Result<Boolean> createInternalResult = createJobServerRelation(
								job, serverIp);
						if (createInternalResult.getData().booleanValue()) {
							logger.info("[ConsoleServiceImpl]: createInternalJob success"
									+ ", serverCluster:"
									+ cluster.toString()
									+ ", ipList:"
									+ ipList
									+ ", createPersistenceResult:"
									+ createPersistenceResult.toString()
									+ ", job:"
									+ job.toString());
							createSuccessCounter++;
						} else {
							logger.error("[ConsoleServiceImpl]: createInternalJob error"
									+ ", serverCluster:"
									+ cluster.toString()
									+ ", ipList:"
									+ ipList
									+ ", createPersistenceResult:"
									+ createPersistenceResult.toString()
									+ ", job:"
									+ job.toString());

						}
                        serverList.add(serverIp);
					}

					if (createSuccessCounter != cluster.getJobBackupAmount()) {
						result.setResultCode(ResultCode.CREATE_JOB_BACKUP_ERROR);
						throw new DtsTransactionException("create server job relation error!");
					}

					for (String server : selectList) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_CREATE_OPERATE, String.valueOf(job
                                .getId()));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(job.getId());
                        jobOperation.setServer(server);
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("create server job operation error!");
                        }
					}
					result.setResultCode(ResultCode.CREATE_JOB_SUCCESS);
                    result.setData(job.getId());
					return result;
				}
			});

        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: createJob error"
            		+ ", job:" + job + ", cluster:" + cluster;
        	
            logger.error(info, e);
            
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

		return result;
	}

	/**
	 * 启用持久化job
	 */
	@Override
	public Result<Integer> enableJob(final Job job) {
		final Result<Integer> result = new Result<Integer>();
        Result<Boolean> checkAuth = checkUserOwnJobId(job.getId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
		
        return enableJobTransaction(job);
	}
	
	private Result<Integer> enableJobTransaction(final Job job) {
		
		final Result<Integer> result = new Result<Integer>();
		
		try {
			dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					JobServerRelation jobServerRelation = new JobServerRelation();
					jobServerRelation.setJobId(job.getId());
					Result<List<JobServerRelation>> relationResult = jobManager.queryJobServerRelation(jobServerRelation);
					if(relationResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(relationResult.getResultCode());
                        return null;
					}

//                    Result<Job> dbJobResult = jobManager.queryJobById(job);
//                    if(dbJobResult.getResultCode() != ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS) {
//                        result.setResultCode(dbJobResult.getResultCode());
//                        throw null;
//                    }

					Result<Integer> enableResult = jobManager.enablePersistenceJob(job);
					if(enableResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(enableResult.getResultCode());
						throw new DtsTransactionException("enable persistence job error!");
					}

					for(JobServerRelation jsr: relationResult.getData()) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_ENABLE_OPERATE, String.valueOf(job
                                .getId()));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(job.getId());
                        jobOperation.setServer(jsr.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("enable server job operation error!");
                        }
					}
					result.setResultCode(ResultCode.SUCCESS);
					result.setData(enableResult.getData());
					return result;
				}
			});
        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: enableJobTransaction error"
            		+ ", job:" + job;
        	
            logger.error(info, e);
            
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

		return result;
	}

	/**
	 * 禁用持久化job
	 */
	@Override
	public Result<Integer> disableJob(final Job job) {
		final Result<Integer> result = new Result<Integer>();

        Result<Boolean> checkAuth = checkUserOwnJobId(job.getId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }

		return disableJobTransaction(job);
	}
	
	private Result<Integer> disableJobTransaction(final Job job) {
		
		final Result<Integer> result = new Result<Integer>();
		
		try {
			dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					JobServerRelation jobServerRelation = new JobServerRelation();
					jobServerRelation.setJobId(job.getId());
					Result<List<JobServerRelation>> relationResult = jobManager.queryJobServerRelation(jobServerRelation);
					if(relationResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(relationResult.getResultCode());
						return null;
					}

					Result<Integer> disableResult = jobManager.disablePersistenceJob(job);
					if(disableResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(disableResult.getResultCode());
						throw new DtsTransactionException("disable persistence job error!");
					}

					for(JobServerRelation jsr: relationResult.getData()) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_DISABLE_OPERATE, String.valueOf(job
                                .getId()));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(job.getId());
                        jobOperation.setServer(jsr.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("disable server job operation error!");
                        }
					}
					result.setResultCode(ResultCode.SUCCESS);
					result.setData(disableResult.getData());
					return result;
				}
			});
        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: disableJobTransaction error"
            		+ ", job:" + job;
        	
            logger.error(info, e);
            
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }
		
		return result;
	}

	public Result<Integer> enableJobOrDisableJob(final Job job, final String operate) {
		
		final Result<Integer> result = new Result<Integer>();
		
		if(! Constants.JOB_DISABLE_OPERATE.equals(operate) 
				&& ! Constants.JOB_ENABLE_OPERATE.equals(operate)) {
			return result;
		}
		
		if(Constants.JOB_DISABLE_OPERATE.equals(operate)) {
			return disableJobTransaction(job);
		} else {
			return enableJobTransaction(job);
		}
		
	}
	
	/**
	 * 查询持久化Job
	 */
	@Override
	public Result<Job> queryPersistenceJob(Job query) {
		return jobManager.queryJobById(query);
	}

	/**
	 * 更新持久化Job
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Result<Integer> updateJob(final Job job) {

		final Result<Integer> result = new Result<Integer>();
        Result<Boolean> checkAuth = checkUserOwnJobId(job.getId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }

		try {
			dataSource.getTransactionTemplateMeta().execute(new TransactionCallback() {

				@Override
				public Object doInTransaction(TransactionStatus status) {
					JobServerRelation jobServerRelation = new JobServerRelation();
					jobServerRelation.setJobId(job.getId());
					Result<List<JobServerRelation>> relationResult = jobManager.queryJobServerRelation(jobServerRelation);
					if (relationResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(relationResult.getResultCode());
						return null;
					}

					Result<Integer> updateResult = jobManager.updatePersistenceJob(job);
					if (updateResult.getResultCode() != ResultCode.SUCCESS) {
						result.setData(0);
						result.setResultCode(updateResult.getResultCode());
						throw new DtsTransactionException("update persistence job error!");
					}

					for (JobServerRelation jsr : relationResult.getData()) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_UPDATE_OPERATE, String.valueOf(job
                                .getId()));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(job.getId());
                        jobOperation.setServer(jsr.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("update server job operation error!");
                        }
					}
					result.setResultCode(ResultCode.SUCCESS);
					result.setData(updateResult.getData());
					return result;
				}
			});
        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: updateJob error"
            		+ ", job:" + job;
        	
            logger.error(info, e);
            
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

		return result;
	}

    @SuppressWarnings("unchecked")
	@Override
    public Result<Integer> updateJobArguments(final Job job) {
        final Result<Integer> result = new Result<Integer>();
        Result<Boolean> checkAuth = checkUserOwnJobId(job.getId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
        try {
        	dataSource.getTransactionTemplateMeta().execute(new TransactionCallback() {

                @Override
                public Object doInTransaction(TransactionStatus status) {
                    JobServerRelation jobServerRelation = new JobServerRelation();
                    jobServerRelation.setJobId(job.getId());
                    Result<List<JobServerRelation>> relationResult = jobManager.queryJobServerRelation(jobServerRelation);
                    if (relationResult.getResultCode() != ResultCode.SUCCESS) {
                        result.setResultCode(relationResult.getResultCode());
                        return null;
                    }

                    Result<Integer> updateResult = jobManager.updateJobArguments(job);
                    if (updateResult.getResultCode() != ResultCode.SUCCESS) {
                        result.setData(0);
                        result.setResultCode(updateResult.getResultCode());
                        throw new DtsTransactionException("update persistence job error!");
                    }

                    for (JobServerRelation jsr : relationResult.getData()) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_UPDATE_OPERATE, String.valueOf(job
                                .getId()));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(job.getId());
                        jobOperation.setServer(jsr.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("update server job operation error!");
                        }
                    }
                    result.setResultCode(ResultCode.SUCCESS);
                    result.setData(updateResult.getData());
                    return result;
                }
            });
        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: updateJobArguments error"
            		+ ", job:" + job;
        	
            logger.error(info, e);
            
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

        return result;
    }

    /**
	 * 删除持久化Job
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Result<Long> deleteJob(final Job job) {

		final Result<Long> result = new Result<Long>();

        Result<Boolean> checkAuth = checkUserOwnJobId(job.getId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }

		try {
			dataSource.getTransactionTemplateMeta().execute(new TransactionCallback() {
				@Override
				public Object doInTransaction(TransactionStatus status) {

                    JobServerRelation relation = new JobServerRelation();
                    relation.setJobId(job.getId());

                    Result<List<JobServerRelation>> relationResult = jobManager
                            .queryJobServerRelation(relation);
                    if (relationResult.getResultCode() != ResultCode.SUCCESS) {
                        result.setResultCode(relationResult.getResultCode());
                        return null;
                    }

					Result<Boolean> deleteRelation = jobManager
							.deleteJobServerRelation(job);
					if (deleteRelation.getData() == false) {
						result.setResultCode(deleteRelation.getResultCode());
						throw new DtsTransactionException("delete server job error!");
					}
					Result<Long> deletePersistenceResult = jobManager
							.deletePersistenceJob(job);
					if (deletePersistenceResult.getData() <= 0) {
						result.setResultCode(deletePersistenceResult.getResultCode());
						throw new DtsTransactionException("delete persistence job error!");
					}

					//删除所有依赖关系
					jobRelationManager.deleteAllRelation(job.getId());
					
                    for (JobServerRelation re : relationResult.getData()) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_DELETE_OPERATE, String.valueOf(job
                                .getId()));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(job.getId());
                        jobOperation.setServer(re.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("delete server job operation error!");
                        }
                    }
					result.setData(deletePersistenceResult.getData());
					result.setResultCode(ResultCode.SUCCESS);
					return result;
				}
			});
        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: deleteJob error"
            		+ ", job:" + job;
        	
            logger.error(info, e);
            
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }
		
		return result;
	}

	/**
	 * 得到一个用的所有组在的cluster
	 */
	@Override
	public TreeMap<Long, Cluster> getUserClusters() {
		TreeMap<Long, Cluster> clusters = new TreeMap<Long, Cluster>();
		Result<List<Cluster>> allServerClusterResult = clusterManager
				.queryAllCluster();
		if (allServerClusterResult.getResultCode() == ResultCode.SUCCESS) {
			List<Cluster> clusterList = allServerClusterResult.getData();
			for (Cluster serverCluster : clusterList) {
				long clusterId = serverCluster.getId();
				clusters.put(clusterId, serverCluster);
			}
		}
		return clusters;
	}

	public Result<Map<Job, List<Job>>> getUserJobRelations(String userId, Cluster cluster) {
        Result<Map<Job, List<Job>>> result = new Result<Map<Job, List<Job>>>();
        Map<Job, List<Job>> jobListMap = new HashMap<Job, List<Job>>();
        /**
         * 先查询用户在这个环境下所有的组;
         */
        Result<List<AssembledUserGroup>> userGroupsResult = getUserGroups(userId, cluster);
        // 遍历每个组下面的JOB，查询JOB依赖关系是否有;
        if(userGroupsResult.getResultCode() == ResultCode.SUCCESS) {

            List<AssembledUserGroup> userGroups = userGroupsResult.getData();
            if(userGroups != null) {
                for (AssembledUserGroup assembledUserGroup: userGroups) {

                    Result<List<Job>>  jobsResult = getGroupJobs(assembledUserGroup.getSystemDefineGroupId(),
                            0, getGroupJobsCount(assembledUserGroup.getSystemDefineGroupId(), null).getData(), null);
                    if(jobsResult.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS) {
                        if(jobsResult.getData() != null) {
                            for(Job job: jobsResult.getData()) {
                                // 查询当前的JOB有没有依赖其他JOB;
                                com.le.dts.common.domain.store.JobRelation jobRelation
                                        = new com.le.dts.common.domain.store.JobRelation();
                                jobRelation.setJobId(job.getId());
                                Result<List<com.le.dts.common.domain.store.JobRelation>> relationsResult
                                        = jobRelationManager.queryBeforeJob(jobRelation);
                                if(relationsResult.getResultCode() == ResultCode.SUCCESS) {
                                    List<Job> relateJobs = new LinkedList<Job>();
                                    List<com.le.dts.common.domain.store.JobRelation> jobRelations = relationsResult.getData();
                                    for(com.le.dts.common.domain.store.JobRelation jobRelation1: jobRelations) {
                                        Job job1 = new Job();
                                        job1.setId(jobRelation1.getBeforeJobId());
                                        Result<Job> jobResult = jobManager.queryJobById(job1);
                                        relateJobs.add(jobResult.getData());
                                    }
                                    if(relateJobs.size() > 0) {
                                        jobListMap.put(job, relateJobs);
                                    }

                                } else {
                                    result.setResultCode(relationsResult.getResultCode());
                                    return result;
                                }
                            }
                            result.setResultCode(ResultCode.SUCCESS);
                            result.setData(jobListMap);
                        }
                    } else {
                        result.setResultCode(jobsResult.getResultCode());
                        return result;
                    }
                }
            }
        } else {
            result.setResultCode(userGroupsResult.getResultCode());
            return result;
        }
		return result;
	}

	@Override
	public Result<List<ServerGroup>> getClusterGroups(long cluster) {
		ServerGroup serverGroup = new ServerGroup();
		serverGroup.setClusterId(cluster);
		return serverGroupManager.queryClusterGroups(serverGroup);
	}

	/**
	 * 拿到用户的所有分组
	 */
	@Override
	public Result<List<AssembledUserGroup>> getUserGroups(String userId,
			Cluster cluster) {
		Result<List<AssembledUserGroup>> result = new Result<List<AssembledUserGroup>>();
		List<AssembledUserGroup> assembleGroupList = new ArrayList<AssembledUserGroup>();
		ServerGroup serverGroup = new ServerGroup();
		serverGroup.setClusterId(cluster.getId());

		UserGroupRelation userGroupRelation = new UserGroupRelation();
		userGroupRelation.setUserId(userId);
		// 查询用户所有的关联组;
		Result<List<UserGroupRelation>> userGroupRelationResult = userGroupRelationManager.queryUserGroupRelation(userGroupRelation);
		if(userGroupRelationResult.getResultCode() != ResultCode.SUCCESS) {
			result.setResultCode(userGroupRelationResult.getResultCode());
			return result;
		}
		List<UserGroupRelation> userGroupRelations = userGroupRelationResult.getData();
		Map<Long, UserGroupRelation> userGroupRelationMap = new HashMap<Long, UserGroupRelation>();
		// 转换成一个Map好判断;
		for(UserGroupRelation userGroupRelation1: userGroupRelations) {
			userGroupRelationMap.put(userGroupRelation1.getGroupId(), userGroupRelation1);
		}
		// 根据cluster查询每个cluster下面的serverGroup
		Result<List<ServerGroup>> serverGroupResult = serverGroupManager.queryClusterGroups(serverGroup);
		if (serverGroupResult.getResultCode() == ResultCode.SUCCESS) {
			// 根据每个serverGroup 查询关联的用户组;
			List<ServerGroup> serverGroups = serverGroupResult.getData();
			for (ServerGroup sg : serverGroups) {
				ClientGroup clientGroup = new ClientGroup();
				clientGroup.setServerGroupId(sg.getId());
				Result<List<ClientGroup>> clientGroupsResult = this.clientGroupManager
						.queryGroup(clientGroup);
				if (clientGroupsResult.getResultCode() == ResultCode.SUCCESS) {
					List<ClientGroup> clientGroupTmp = clientGroupsResult
							.getData();
					List<ClientGroup> clientGroups = new LinkedList<ClientGroup>();
					// 做交集运算;
					for(ClientGroup group: clientGroupTmp) {
						if(userGroupRelationMap.containsKey(group.getId())) {
							clientGroups.add(group);
						}
					}

					for (ClientGroup cg : clientGroups) {
						AssembledUserGroup aug = new AssembledUserGroup();
						aug.setGroupDesc(cg.getDescription());
						aug.setUserId(userId);
						aug.setClusterId(cluster.getId());
						aug.setSystemDefineGroupId(GroupIdUtil.generateGroupId(cluster.getId(),sg.getId(),
								cluster.getJobBackupAmount(), cg.getId()));
						// 根据Job表查询每个group下面的Job数;
						Job job = new Job();
						job.setClientGroupId(cg.getId());
						job.setServerGroupId(cg.getServerGroupId());
						Result<Integer> countResult = this.jobManager.countClientClusterJobs(job);
						if(countResult.getResultCode() == ResultCode.SUCCESS) {
							aug.setGroupJobNum(countResult.getData());
						} else {
							result.setResultCode(countResult.getResultCode());
							return result;
						}
						
						List<UserGroupRelation> userGroupRelationList = null;
						try {
							UserGroupRelation relationQuery = new UserGroupRelation();
							relationQuery.setGroupId(cg.getId());
							userGroupRelationList = userGroupRelationAccess.queryByGroupId(relationQuery);
						} catch (Throwable e) {
							logger.error("[ApiServiceImpl]: queryByGroupId error, groupId:" + cg.getId(), e);
						}
						
						aug.setSecurityControl(cg.getSecurityControl());
						aug.setRelationList(userGroupRelationList);
						
						assembleGroupList.add(aug);
					}
				} else {
					result.setResultCode(clientGroupsResult.getResultCode());
					return result;
				}
			}
		} else {
			result.setResultCode(serverGroupResult.getResultCode());
			return result;
		}
		result.setData(assembleGroupList);
		result.setResultCode(ResultCode.SUCCESS);
		return result;
	}

	/**
	 * 创建一个分组
	 */
	@Override
	public Result<String> createGroup(final ClientGroup group, final long clusterId) {
		/**
		 * 做一个事务,先创建组，再创建用户组关系;
		 */
		final Result<String> result = new Result<String>();

		try {
			dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					Result<Long> createResult = new Result<Long>();
					createResult = clientGroupManager.createGroup(group);
					if(createResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(createResult.getResultCode());
						throw new DtsTransactionException("create group error!");
					}
					UserGroupRelation userGroupRelation = new UserGroupRelation();
					userGroupRelation.setUserId(Global.getDtsUser(request).getUserId());
					userGroupRelation.setGroupId(createResult.getData());
					Result<Long> relationResult = userGroupRelationManager.createUserGroupRelation(userGroupRelation);
					if(relationResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(relationResult.getResultCode());
						throw new DtsTransactionException("create user group relation error!");
					}
                    Cluster cluster = getCluster(clusterId);
                    String groupId = GroupIdUtil.generateGroupId(cluster.getId(),
                            group.getServerGroupId(), cluster.getJobBackupAmount(), createResult.getData());
                    String userId = Global.getDtsUser(request).getUserId();
//                    String serverName = dAuthBean.getAppName();
//                    // 如果是在阿里云环境，对阿里云用户鉴权
//                    if(UserEnvUtil.isAliyunUser(request)) {
//                        String action = "*";
//                        List<String> actions = new ArrayList<String>(1);
//                        actions.add(action);
//                        DauthResult dauthResult = dauthClient.grantAccessPermission(groupId, actions, serverName, userId, null, null, null);
//                        // 检查鉴权结果;
//                        if(dauthResult.getCode() != 0) {// 失败
//                            result.setResultCode(ResultCode.DAUTH_ERROR);
//                            throw new DtsTransactionException("调用Dauth授权失败！");
//                        }
//                    }

					result.setResultCode(ResultCode.SUCCESS);
                    result.setData(groupId);
                    logger.warn("user:" + userId + " create group:");
					return result;
				}
			});

        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: createGroup error"
            		+ ", group:" + group 
            		+ ", clusterId " + clusterId;
        	
            logger.error(info, e);
            
            result.setData(info);
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

		return result;
	}

	/**
	 * 删除一个用户分组
	 */
	@Override
	public Result<String> deleteGroup(final String groupId, String userId) {
		final Result<String> result = new Result<String>();
		final ClientGroup clientGroup = GroupIdUtil.getClientGroup(groupId);
		// 删除一个组,先删除Job,再删除组;
		final UserGroupRelation userGroupRelation = new UserGroupRelation();
		userGroupRelation.setUserId(userId);
		userGroupRelation.setGroupId(clientGroup.getId());
		// 校验用户是否拥有该资源;
		Result<Boolean> checkResult = userGroupRelationManager.checkUserGroupRelation(userGroupRelation);
		if(checkResult.getData() == false) {
			result.setResultCode(checkResult.getResultCode());
			return result;
		}
		Result<Integer> jobCountResult = getGroupJobsCount(groupId, null);
		if(jobCountResult.getResultCode() != ResultCode.SUCCESS) {
			result.setResultCode(jobCountResult.getResultCode());
			return result;
		}
		int jobCount = jobCountResult.getData();
		final Result<List<Job>> groupJobs = getGroupJobs(groupId, 0, jobCount, null);
		if (groupJobs.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_IS_NULL) {
			result.setResultCode(ResultCode.DELETE_PERSISTENCE_JOB_FAILURE);
			return result;
		}
		try {
			dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					Result<Long> jobDeleteResult = new Result<Long>();
					for (Job job : groupJobs.getData()) {
						jobDeleteResult = deleteJob(job);
						if (jobDeleteResult.getResultCode() != ResultCode.SUCCESS) {
							result.setResultCode(ResultCode.DELETE_PERSISTENCE_JOB_FAILURE);
							result.setData(jobDeleteResult.getData().toString());
							throw new DtsTransactionException("delete group jobs error!");
						}
					}
					Result<String> deleteGroupResult = clientGroupManager.deleteGroup(clientGroup);
					if(deleteGroupResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(deleteGroupResult.getResultCode());
						throw new DtsTransactionException("delete group error!");
					}
					// 删除用户组关系;
					Result<Long> deleteRelationResult = userGroupRelationManager.deleteUserGroupRelation(userGroupRelation);
					if(deleteRelationResult.getResultCode() != ResultCode.SUCCESS) {
						result.setResultCode(deleteRelationResult.getResultCode());
						throw new DtsTransactionException("delete user group relation error!");
					}
					result.setResultCode(deleteRelationResult.getResultCode());
					return result;
				}
			});

        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: deleteGroup error"
            		+ ", groupId:" + groupId 
            		+ ", userId " + userId;
        	
            logger.error(info, e);
            
            result.setData(info);
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }
		return result;
	}

    /**
     * 判断用户是否拥有该组;
     * @param clientGroupId
     * @return
     */
    private Result<Boolean> checkUserOwnResource(long clientGroupId) {
        Result<Boolean> result = new Result<Boolean>();
        UserGroupRelation userGroupRelation = new UserGroupRelation();
        userGroupRelation.setUserId(Global.getDtsUser(request).getUserId());
        userGroupRelation.setGroupId(clientGroupId);
        // 校验用户是否拥有该资源;
        Result<Boolean> checkResult = userGroupRelationManager.checkUserGroupRelation(userGroupRelation);
        if(checkResult.getResultCode() == ResultCode.SUCCESS) {
            if(checkResult.getData() == false) {
                result.setResultCode(ResultCode.USER_NOT_OWN_RESOURCE);
                result.setData(false);
                return result;
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
                return result;
            }
        } else {
            result.setResultCode(ResultCode.INNER_ERROR);
            result.setData(false);
            return result;
        }
    }

    /**
     * 检查用户是否拥有该JOB;
     * @param jobId
     * @return
     */
    private Result<Boolean> checkUserOwnJobId(long jobId) {

        Result<Boolean> result = new Result<Boolean>();
        Job src = new Job();
        src.setId(jobId);
        Result<Job> jobResult = jobManager.queryJobById(src);
        if(jobResult.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS) {
            Job job = jobResult.getData();
            if(job != null) {
                return checkUserOwnResource(job.getClientGroupId());
            } else {
                result.setResultCode(ResultCode.USER_NOT_OWN_RESOURCE);
                result.setData(false);
            }
        } else {
            result.setResultCode(jobResult.getResultCode());
            result.setData(false);
        }
        return result;
    }

	/**
	 * 拿到一个分组中的所有Job
	 */
	@Override
	public Result<List<Job>> getGroupJobs(String groupId, int page,
			int perPageCount, String searchText) {
		ClientGroup clientGroup = GroupIdUtil.getClientGroup(groupId);
        Result<Boolean> checkAuth = checkUserOwnResource(clientGroup.getId());
        if(!checkAuth.getData()) {
            Result<List<Job>> result = new Result<List<Job>>();
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
        // 校验当前用户是否拥有该组;
		JobPageQuery jobPage = new JobPageQuery();
		jobPage.setClientGroupId(clientGroup.getId());
        jobPage.setPageSize(perPageCount);
		jobPage.setStartRow(page * perPageCount);
		if(StringUtil.isNotBlank(searchText)) {
			jobPage.setDescription(searchText);
		}
		Result<List<Job>> jobResult = jobManager.queryPersistenceJob(jobPage);
		return jobResult;
	}

	/**
	 * 得到一个分组Job的数量
	 */
	public Result<Integer> getGroupJobsCount(String groupId, String searchText) {
		ClientGroup clientGroup = GroupIdUtil.getClientGroup(groupId);
		Job job = new Job();
		job.setClientGroupId(clientGroup.getId());
		job.setServerGroupId(clientGroup.getServerGroupId());
		if(StringUtil.isNotBlank(searchText)) {
			job.setDescription(searchText);
		}
		Result<Integer> result = jobManager.countClientClusterJobs(job);
		return result;
	}

	/**
	 * 得到Job的配置信息
	 */
	@Override
	public Result<Job> getJobConfig(long jobId) {
        Result<Boolean> checkAuth = checkUserOwnJobId(jobId);
        if(!checkAuth.getData()) {
            Result<Job> result = new Result<Job>();
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
		Job job = new Job();
		job.setId(jobId);
		Result<Job> resultJob = jobManager.queryJobById(job);
		return resultJob;
	}

	/**
	 * 更新监控数据
	 */
	public Result<String> updateJobMonitor(WarningSetup warningSetup) {
        Result<Boolean> checkAuth = checkUserOwnJobId(warningSetup.getJobId());
        if(!checkAuth.getData()) {
            Result<String> checkResult = new Result<String>();
            checkResult.setResultCode(checkAuth.getResultCode());
            return checkResult;
        }
		Result<String> result = warningSetupManager
				.insertOrUpdateWarningSetup(warningSetup);
		return result;
	}

	/**
	 * 运行一次任务
	 */
	public Result<String> startJob(final String groupId, final long jobId) {

		final Result<String> result = new Result<String>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobId);
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
        /**
         * 检查目标客户端机器
         */
        List<String> clientMachines = zookeeper.getClientGroupIpList(groupId, 0L);
        if(null == clientMachines || clientMachines.size() <= 0) {// 目标没有机器;
            result.setResultCode(ResultCode.CLIENT_MACHINE_EMPTY);
            return result;
        }

        try {
        	dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    JobServerRelation jobServerRelation = new JobServerRelation();
                    jobServerRelation.setJobId(jobId);
                    Result<List<JobServerRelation>> relationResult = jobManager.queryJobServerRelation(jobServerRelation);
                    if(relationResult.getResultCode() != ResultCode.SUCCESS) {
                        result.setResultCode(relationResult.getResultCode());
                        return null;
                    }

                    Date fireTime = new Date();
                    for(JobServerRelation jsr: relationResult.getData()) {
                        // 回查ZK看找到运行的Server;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(Constants.JOB_ID_ITEM, jobId);
                        jsonObject.put(Constants.FIRE_TIME_ITEM, fireTime);
                        OperationContent op = new OperationContent(
                                Constants.JOB_INSTANCE_START_OPERATE, jsonObject.toJSONString());
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(jobId);
                        jobOperation.setServer(jsr.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("start job server operation error!");
                        }
                    }

                    return result;
                }
            });

        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: startJob error"
            		+ ", groupId:" + groupId 
            		+ ", jobId " + jobId;
        	
            logger.error(info, e);
            
            result.setData(info);
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

        result.setResultCode(ResultCode.SUCCESS);
		return result;
	}

	/**
	 * 立即停止任务
	 */
	public Result<String> stopJob(final long jobId) {
		final Result<String> result = new Result<String>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobId);
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }

        try {
        	dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    JobServerRelation jobServerRelation = new JobServerRelation();
                    jobServerRelation.setJobId(jobId);
                    Result<List<JobServerRelation>> relationResult = jobManager.queryJobServerRelation(jobServerRelation);
                    if(relationResult.getResultCode() != ResultCode.SUCCESS) {
                        result.setResultCode(relationResult.getResultCode());
                        return null;
                    }

                    for(JobServerRelation jsr: relationResult.getData()) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_INSTANCE_STOP_OPERATE, String.valueOf(jobId));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(jobId);
                        jobOperation.setServer(jsr.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("stop job server operation error!");
                        }
                    }

                    return result;
                }
            });

        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: stopJob error"
            		+ ", jobId " + jobId;
        	
            logger.error(info, e);
            
            result.setData(info);
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

        result.setResultCode(ResultCode.SUCCESS);
		return result;
	}
	
	@Override
	public Result<String> stopAnyJob(final long jobId) {

		final Result<String> result = new Result<String>();

		try {
			dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    JobServerRelation jobServerRelation = new JobServerRelation();
                    jobServerRelation.setJobId(jobId);
                    Result<List<JobServerRelation>> relationResult = jobManager.queryJobServerRelation(jobServerRelation);
                    if(relationResult.getResultCode() != ResultCode.SUCCESS) {
                        result.setResultCode(relationResult.getResultCode());
                        return null;
                    }

                    for(JobServerRelation jsr: relationResult.getData()) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_INSTANCE_STOP_OPERATE, String.valueOf(jobId));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(jobId);
                        jobOperation.setServer(jsr.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("stop job server operation error!");
                        }
                    }

                    return result;
                }
            });

        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: stopAnyJob error"
            		+ ", jobId " + jobId;
        	
            logger.error(info, e);
            
            result.setData(info);
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

        result.setResultCode(ResultCode.SUCCESS);
		return result;
	}

	/**
	 * 立即停止任务 后门
	 */
	public Result<String> stopJobBackup(final long jobId) {
		final Result<String> result = new Result<String>();
        
        try {
        	dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    JobServerRelation jobServerRelation = new JobServerRelation();
                    jobServerRelation.setJobId(jobId);
                    Result<List<JobServerRelation>> relationResult = jobManager.queryJobServerRelation(jobServerRelation);
                    if(relationResult.getResultCode() != ResultCode.SUCCESS) {
                        result.setResultCode(relationResult.getResultCode());
                        return null;
                    }

                    for(JobServerRelation jsr: relationResult.getData()) {
                        OperationContent op = new OperationContent(
                                Constants.JOB_INSTANCE_STOP_OPERATE, String.valueOf(jobId));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(jobId);
                        jobOperation.setServer(jsr.getServer());
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("stop job server operation error!");
                        }
                    }

                    return result;
                }
            });

        } catch (Throwable e) {
        	
        	String info = "[ApiServiceImpl]: stopJobBackup error"
            		+ ", jobId " + jobId;
        	
            logger.error(info, e);
            
            result.setData(info);
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

        result.setResultCode(ResultCode.SUCCESS);
		return result;
	}

    /**
     * 关联依赖
     * @param jobRelation
     * @return
     */
    public Result<Long> createJobRelation(final com.le.dts.common.domain.store.JobRelation jobRelation) {
        final Result<Long> result = new Result<Long>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobRelation.getJobId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
        Result<Long> relationResult = jobRelationManager.createJobRelation(jobRelation);
        if(relationResult.getResultCode() != ResultCode.SUCCESS) {
            result.setResultCode(relationResult.getResultCode());
            return result;
        }

        result.setResultCode(ResultCode.SUCCESS);
        result.setData(relationResult.getData());
        return result;
    }

    /**
     * 删除依赖
     * @param jobRelation
     * @return
     */
    public Result<Integer> deleteJobRelation(final com.le.dts.common.domain.store.JobRelation jobRelation) {

        final Result<Integer> result = new Result<Integer>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobRelation.getJobId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
        Result<Integer> relationResult = jobRelationManager.deleteJobRelation(jobRelation);
        if(relationResult.getResultCode() != ResultCode.SUCCESS) {
            result.setResultCode(relationResult.getResultCode());
            return result;
        }

        result.setResultCode(ResultCode.SUCCESS);
        result.setData(relationResult.getData());
        return result;
    }

	/**
	 * 拿到Job的运行状态
	 */
	public Result<List<JobStatus>> getGroupJobStatus(String groupId, int page,
			int perPageCount, boolean instance) {
		Result<List<JobStatus>> result = new Result<List<JobStatus>>();
        ClientGroup clientGroup = GroupIdUtil.getClientGroup(groupId);
        Result<Boolean> checkAuth = checkUserOwnResource(clientGroup.getId());
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
		Result<List<Job>> jobResult = this.getGroupJobs(groupId, page,
				perPageCount, null);
		if (jobResult.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_IS_NULL
				|| jobResult.getData() == null
				|| jobResult.getData().size() == 0) {
			result.setResultCode(ResultCode.SUCCESS);
            result.setData(Collections.EMPTY_LIST);
			return result;
		}
		List<JobStatus> jobStatusList = new ArrayList<JobStatus>(jobResult
				.getData().size());
		for (Job job : jobResult.getData()) {
			Result<List<JobInstanceSnapshot>> jobInstanceSnapShotResult = jobInstanceManager
					.queryWorkingJobInstance(job.getId());
			if (jobInstanceSnapShotResult.getResultCode() == ResultCode.QUERY_JOB_INSTANCE_ERROR) {
				result.setResultCode(ResultCode.QUERY_JOB_INSTANCE_ERROR);
				return result;
			}
			List<JobInstanceSnapshot> jobSnapShotList = jobInstanceSnapShotResult
					.getData();
			if (jobSnapShotList != null && jobSnapShotList.size() != 0) {
				JobStatus js = new JobStatus();
				js.setJobDesc(job.getDescription());
				js.setJobId(job.getId());
				js.setRunningStatus(1);
                TreeMap<Long, Map.Entry<String, String>> progresses = new TreeMap<Long, Map.Entry<String, String>>();
                if(instance) {
                    // 统计多个job实例的进度
                    for (JobInstanceSnapshot jobSnapShot : jobSnapShotList) {
                        Result<ProgressBar> taskSnapShotResult = taskSnapShotManager
                                .queryJobSnapShotProgress(jobSnapShot);
                        if (taskSnapShotResult.getResultCode() == ResultCode.SUCCESS) {
                            Map.Entry<String, String> entry = new DefaultMapEntry<String, String>(jobSnapShot.getFireTime(),
                                    String.valueOf(taskSnapShotResult.getData().parseProcessValue()));
                            progresses.put(jobSnapShot.getId(), entry);
                        }
                    }
                    js.setOverallProgress(progresses);
                } else {
                	
                    for (JobInstanceSnapshot jobSnapShot : jobSnapShotList) {
                       progresses.put(jobSnapShot.getId(), new DefaultMapEntry<String, String>(jobSnapShot.getFireTime(),"0"));
                    }
                    js.setOverallProgress(progresses);
                }
				jobStatusList.add(js);
			} else {
				
				JobStatus js = new JobStatus();
				js.setJobDesc(job.getDescription());
				js.setJobId(job.getId());
				js.setRunningStatus(-1);
				
            	JobInstanceSnapshot jobInstanceSnapshot = null;
	            try {
	            	JobInstanceSnapshot query = new JobInstanceSnapshot();
		            query.setJobId(job.getId());
	                jobInstanceSnapshot = jobInstanceSnapshotAccess.queryLastInstance(query);
	            } catch (Throwable e) {
	                logger.error("[ApiServiceImpl]: queryLastInstance error", e);
	            }
            	
	            if(jobInstanceSnapshot != null) {
		            TreeMap<Long, Map.Entry<String, String>> progresses = new TreeMap<Long, Map.Entry<String, String>>();
		            progresses.put(jobInstanceSnapshot.getId(), new DefaultMapEntry<String, String>(jobInstanceSnapshot.getFireTime(),"-1"));
	            	js.setOverallProgress(progresses);
	            	js.setRunningStatus(0);
	            }
	            
				jobStatusList.add(js);
			}
		}
		result.setData(jobStatusList);
		result.setResultCode(ResultCode.SUCCESS);
		return result;
	}

    /**
     * 查询一个JOB的状态;
     * @param jobId
     * @return
     */
    public Result<JobStatus> getJobStatus(long jobId) {
        Result<JobStatus> result = new Result<JobStatus>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobId);
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
        Job job = new Job();
        job.setId(jobId);
        Result<List<JobInstanceSnapshot>> jobInstanceSnapShotResult = jobInstanceManager
                .queryWorkingJobInstance(job.getId());
        if (jobInstanceSnapShotResult.getResultCode() == ResultCode.QUERY_JOB_INSTANCE_ERROR) {
            result.setResultCode(ResultCode.QUERY_JOB_INSTANCE_ERROR);
            return result;
        }
        List<JobInstanceSnapshot> jobSnapShotList = jobInstanceSnapShotResult
                .getData();
        if (jobSnapShotList != null && jobSnapShotList.size() != 0) {
            JobStatus js = new JobStatus();
            js.setJobDesc(job.getDescription());
            js.setJobId(job.getId());
            js.setRunningStatus(1);
            TreeMap<Long, Map.Entry<String, String>> progresses = new TreeMap<Long, Map.Entry<String, String>>();
            // 统计多个job实例的进度
            for (JobInstanceSnapshot jobSnapShot : jobSnapShotList) {
                Result<ProgressBar> taskSnapShotResult = taskSnapShotManager
                        .queryJobSnapShotProgress(jobSnapShot);
                if (taskSnapShotResult.getResultCode() == ResultCode.SUCCESS) {
                    progresses.put(jobSnapShot.getId(), new DefaultMapEntry<String, String>(jobSnapShot.getFireTime(),
                            taskSnapShotResult.getData().parsePercentRate()));
                }
            }
            js.setOverallProgress(progresses);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(js);
        } else {
            JobStatus js = new JobStatus();
            js.setJobDesc(job.getDescription());
            js.setJobId(job.getId());
            js.setRunningStatus(0);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(js);
        }
        return result;
    }

	/**
	 * 得到Job分级执行的进度
	 */
	public Result<ProgressDetail> getJobInstanceDetailStatus(long jobId,
			String instanceId) {
		Result<ProgressDetail> result = new Result<ProgressDetail>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobId);
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
		TaskSnapshot taskSnapShot = new TaskSnapshot();
		taskSnapShot.setJobInstanceId(Long.valueOf(instanceId));
		Result<ProgressDetail> progressDetailResult = taskSnapShotManager
				.queryTaskSnapShotDetail(taskSnapShot);
		if (progressDetailResult.getResultCode() == ResultCode.SUCCESS) {
			ProgressDetail progressDetail = progressDetailResult.getData();
			result.setData(progressDetail);
			result.setResultCode(ResultCode.SUCCESS);
		} else {
			result.setResultCode(progressDetailResult.getResultCode());
		}
		return result;
	}

    /**
     * 一个实例执行的总体的进度;
     *
     * @param instanceId
     * @return
     */
    public Result<Double> getJobInstanceOvaralProgress(long instanceId) {
        Result<Double> result = new Result<Double>();
        JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
        jobInstanceSnapshot.setId(instanceId);
        Result<ProgressBar> taskSnapShotResult = taskSnapShotManager
                .queryJobSnapShotProgress(jobInstanceSnapshot);
        if (taskSnapShotResult.getResultCode() == ResultCode.SUCCESS) {
            double value = taskSnapShotResult.getData().parseProcessValue() * 100;
            BigDecimal bg = new BigDecimal(value);
            Double t = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(t);
        } else {
            result.setResultCode(ResultCode.FAILURE);
        }
        return result;
    }

	/**
	 * 得到job的历史快照
	 */
	public Result<JobHistoryRecord> getJobHistoryRecord(long jobId, int page,
			int perPageCount) {
		Result<JobHistoryRecord> result = new Result<JobHistoryRecord>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobId);
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
		JobHistoryPageQuery jobHistoryPageQuery = new JobHistoryPageQuery();
		jobHistoryPageQuery.setJobId(jobId);
		jobHistoryPageQuery.setPageSize(perPageCount);
		jobHistoryPageQuery.setStartRow(page * perPageCount);
		Result<List<JobInstanceSnapshot>> jobInstanceSnapshotResult = jobInstanceManager
				.pageQuery(jobHistoryPageQuery);
		if (jobInstanceSnapshotResult.getResultCode() == ResultCode.SUCCESS) {
			JobHistoryRecord jobHistoryRecord = new JobHistoryRecord();
			jobHistoryRecord.setJobId(jobId);
			StringBuilder sb = new StringBuilder();
			for (JobInstanceSnapshot jobInstanceSnapshot : jobInstanceSnapshotResult
					.getData()) {
                String jobInstanceResult = jobInstanceSnapshot.getJobInstanceResult();
                if(StringUtil.isNotBlank(jobInstanceResult)) {
                    ProgressDetail progressDetail = ProgressDetail.newInstance(jobInstanceResult);
                    String fireTime = progressDetail.getFireTime();
                    String finishTime = progressDetail.getFinishTime();
                    sb.append("start :" + fireTime + "<br>" + "finish:" + finishTime)
                            .append("@")
                            .append(HistoryPageUtil
                                    .resultToPageInfo(progressDetail)).append("#");
                }
			}
			jobHistoryRecord.setResult(sb.toString());
			result.setData(jobHistoryRecord);
			result.setResultCode(ResultCode.SUCCESS);
		} else {
			result.setResultCode(jobInstanceSnapshotResult.getResultCode());
		}
		return result;
	}

	public Result<Integer> getJobHistoryCount(long jobId) {
		Result<Integer> result = new Result<Integer>();
		result = jobInstanceManager.queryJobInstanceCount(jobId);
		return result;
	}

	/**
	 * 授权
	 */
	public Result<String> grantAuth(String ownerId, final String userId, final String groupId) {
		final Result<String> result = new Result<String>();
		final ClientGroup clientGroup = GroupIdUtil.getClientGroup(groupId);
		UserGroupRelation owner = new UserGroupRelation();
		owner.setGroupId(clientGroup.getId());
		owner.setUserId(ownerId);
		Result<List<UserGroupRelation>> clientsResult = userGroupRelationManager.queryUserGroupRelation(owner);
		if (clientsResult.getResultCode() == ResultCode.SUCCESS) {
			// 只有一个元素;
			if(clientsResult.getData() == null || clientsResult.getData().size() == 0) {
				result.setResultCode(ResultCode.USER_NOT_OWN_RESOURCE);
				return result;
			}
			// 数据库校验一次目标用户是否开通服务用户;
			UserGroupRelation dest = new UserGroupRelation();
			dest.setUserId(userId);
			Result<Boolean> isExists = userGroupRelationManager.checkUserGroupRelation(dest);
			if (isExists.getResultCode() == ResultCode.SUCCESS) {
                try {
                	dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
                        @Override
                        public Object doInTransaction(TransactionStatus status) {
                            UserGroupRelation userGroupRelation = new UserGroupRelation();
                            userGroupRelation.setGroupId(clientGroup.getId());
                            userGroupRelation.setUserId(userId);
                            Result<Long> createResult = userGroupRelationManager.createUserGroupRelation(userGroupRelation);
                            if(createResult.getResultCode() != ResultCode.SUCCESS) {
                                result.setResultCode(createResult.getResultCode());
                                throw new DtsTransactionException("创建关系失败，授权失败！");
                            }
                            result.setResultCode(ResultCode.SUCCESS);
                            return result;
                        }
                    });
                } catch (Throwable e) {
                    
                	String info = "[ApiServiceImpl]: grantAuth error" 
                    		+ ", ownerId" + ownerId
                    		+ ", userId " + userId 
                    		+ ", groupId:" + groupId;
                	
                    logger.error(info, e);
                    
                    result.setData(info);
                    result.setResultCode(ResultCode.FAILURE);
            		return result;
                }
			} else {
				result.setResultCode(isExists.getResultCode());
			}
		} else {
            logger.info("user doesnot own resource!");
			result.setResultCode(clientsResult.getResultCode());
		}
		return result;
	}

	@Override
	public Result<Integer> updatePersistenceJobStatus(Job job) {
		return jobManager.updatePersistenceJobStatus(job);
	}

    @Override
    public Result<List<AssembledDesignatedMachine>> getDesignatedMachine(String groupId, long jobId) {
        final Result<List<AssembledDesignatedMachine>> result
                = new Result<List<AssembledDesignatedMachine>>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobId);
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }

        // 查询ZK;
        List<String> clientMachines = zookeeper.getClientGroupIpList(groupId, 0L);
        if(CollectionUtils.isEmpty(clientMachines)) {
            result.setResultCode(ResultCode.CLIENT_MACHINE_EMPTY);
            return result;
        }
        List<AssembledDesignatedMachine> assembledDesignatedMachines = new ArrayList<AssembledDesignatedMachine>();
        for(String machine: clientMachines) {
            AssembledDesignatedMachine assembledDesignatedMachine = new AssembledDesignatedMachine();
            assembledDesignatedMachine.setJobId(jobId);
            assembledDesignatedMachine.setMachine(machine);
            assembledDesignatedMachine.setClientGroupId(GroupIdUtil.getClientGroup(groupId).getId());
            assembledDesignatedMachines.add(assembledDesignatedMachine);
        }

        // 查询DB;
        List<DesignatedMachine> designatedMachineList = null;
        try {
            designatedMachineList = designatedMachineManager.loadDesignatedMachineList(jobId);
        } catch (InitException e) {
            logger.error(e);
            result.setResultCode(ResultCode.QUERY_DESIGNATEDMATCHINE_ERROR);
            return result;
        }
        for(AssembledDesignatedMachine assembledDesignatedMachine: assembledDesignatedMachines) {
            DesignatedMachine designatedMachine = findDesignated(designatedMachineList, assembledDesignatedMachine.getMachine(), jobId);
            if(designatedMachine != null) {
                assembledDesignatedMachine.setDesignatedMachine(true);
                assembledDesignatedMachine.setPolicy(designatedMachine.getPolicy());
            }
        }
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(assembledDesignatedMachines);
        return result;
    }

    public DesignatedMachine findDesignated(List<DesignatedMachine> designatedMachineList, String machine, long jobId) {
        for(DesignatedMachine designatedMachine: designatedMachineList) {
            if(jobId == designatedMachine.getJobId() && StringUtil.equals(machine, designatedMachine.getMachine())) {
                return designatedMachine;
            }
        }
        return null;
    }

    @Override
    public Result<Integer> designatedMachine(final String groupId, final long jobId, final List<DesignatedMachine> designatedMachineList) {
        final Result<Integer> result = new Result<Integer>();
        Result<Boolean> checkAuth = checkUserOwnJobId(jobId);
        if(!checkAuth.getData()) {
            result.setResultCode(checkAuth.getResultCode());
            return result;
        }
        try {
        	dataSource.getTransactionTemplateMeta().execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    try {
                        designatedMachineManager.deleteDesignnatedMathineList(jobId);
                    } catch (InitException e) {
                        logger.error(e);
                        result.setResultCode(ResultCode.DELETE_DESIGNATEDMATCHINE_ERROR);
                        throw new DtsTransactionException("delete designatedMachine error!");
                    }
                    int count = 0;
                    for(DesignatedMachine designatedMachine: designatedMachineList) {
                        try {
                            designatedMachineManager.createDesignnatedMathineList(designatedMachine);
                            count++;
                        } catch (InitException e) {
                            logger.error(e);
                            result.setResultCode(ResultCode.INSERT__DESIGNATEDMATCHINE_ERROR);
                            throw new DtsTransactionException("delete designatedMachine error!");
                        }
                    }
                    // 通知Server;
                    List<String> serverGroupList = Collections.EMPTY_LIST;
                    try {
                        serverGroupList = zookeeper.getServerGroupIpList(Long.toString(GroupIdUtil.getCluster(groupId).getId()),
                                Long.toString(GroupIdUtil.getClientGroup(groupId).getServerGroupId()));
                    } catch (Throwable e) {
                        result.setResultCode(ResultCode.CLUSTER_SERVER_ERROR);
                        throw new DtsTransactionException("fetch cluster server error!");
                    }

                    for(String server: serverGroupList) {
                        OperationContent op = new OperationContent(
                                Constants.DESIGNATED_MACHINE, String.valueOf(jobId));
                        JobOperation jobOperation = new JobOperation();
                        jobOperation.setJobId(jobId);
                        jobOperation.setServer(server);
                        jobOperation.setOperation(op.toString());
                        Result<Long> insertResult = jobOperationManager.insertOperation(jobOperation);
                        if(insertResult.getData() <= 0) {
                            result.setResultCode(ResultCode.CREATE_JOB_OPERATION_ERROR);
                            throw new DtsTransactionException("enable server job operation error!");
                        }
                    }

                    result.setData(count);
                    result.setResultCode(ResultCode.SUCCESS);
                    return result;
                }
            });
        } catch (Throwable e) {
        
            logger.error("[ApiServiceImpl]: designatedMachine error" 
            		+ ", groupId" + groupId
            		+ ", jobId " + jobId 
            		+ ", designatedMachineList:" + designatedMachineList, e);
            
            result.setResultCode(ResultCode.FAILURE);
    		return result;
        }

        return result;
    }

    /**
     * 复位图式计算
     */
	@Override
	public boolean resetJobRelation(String[] jobIdList) {

		try {
			for(int i = 0 ; i < jobIdList.length ; i ++) {
				recursive(Long.parseLong(jobIdList[i]));
			}
		} catch (Throwable e) {
			logger.error("[ApiServiceImpl]: resetJobRelation error, jobIdList:" + Arrays.toString(jobIdList), e);
			return false;
		}
		
		return true;
	}
    
	/**
	 * 递归清零
	 * @param beforeJobId
	 */
	private void recursive(long beforeJobId) {
		
		jobRelationManager.updateResetFinishCount(beforeJobId);
		
		List<Long> afterJobIds = loadAfterJobs(beforeJobId);
		if(CollectionUtils.isEmpty(afterJobIds)) {
			return ;
		}
		
		for(Long afterJobId : afterJobIds) {
			recursive(afterJobId);
		}
	}
	
	/**
	 * 加载后置job
	 * @param beforeJobId
	 * @return
	 */
    private List<Long> loadAfterJobs(long beforeJobId) {
    	
    	JobRelation jobRelation = new JobRelation();
    	jobRelation.setBeforeJobId(beforeJobId);
    	
    	Result<List<JobRelation>> result = jobRelationManager.queryAfterJob(jobRelation);
    	if(result.getResultCode().equals(ResultCode.QUERY_JOB_RELATION_ERROR)) {
    		return null;
    	}
    	
    	if(CollectionUtils.isEmpty(result.getData())) {
    		return null;
    	}
    	
    	List<Long> afterJobIds = new ArrayList<Long>();
    	for(JobRelation relation : result.getData()) {
    		afterJobIds.add(relation.getJobId());
    	}
    	return afterJobIds;
    }
    
}
