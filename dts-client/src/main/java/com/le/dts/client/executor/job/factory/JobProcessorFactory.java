package com.le.dts.client.executor.job.factory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.le.dts.client.executor.job.processor.SimpleJobProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import com.le.dts.client.context.ClientContext;
import com.le.dts.client.executor.job.processor.ParallelJobProcessor;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.service.ServerService;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.RandomUtil;
import com.le.dts.common.util.StringUtil;

/**
 * Job处理器工厂
 * @author tianyao.myc
 *
 */
public class JobProcessorFactory implements ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(JobProcessorFactory.class);
	
	/** spring bean上下文 */
	private ApplicationContext applicationContext = null;
	
	/** SimpleJobProcessor缓存 */
	private ConcurrentHashMap<String, ConcurrentHashMap<String, SimpleJobProcessor>> simpleJobProcessorCache =
			new ConcurrentHashMap<String, ConcurrentHashMap<String, SimpleJobProcessor>>();

	/** ParallelJobProcessor缓存 */
	private ConcurrentHashMap<String, ConcurrentHashMap<String, ParallelJobProcessor>> parallelJobProcessorCache = 
			new ConcurrentHashMap<String, ConcurrentHashMap<String, ParallelJobProcessor>>();
	
	private ServerService serverService = clientRemoting.proxyInterface(ServerService.class);
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化检查Job处理器 */
		initCheckJobProcessors();
		
		/** Spring环境初始化 */
		if(clientConfig.isSpring()) {
			
			/** 初始化SimpleJobProcessor缓存 */
			initSimpleJobProcessorCache4Spring();
			
			/** 初始化ParallelJobProcessor缓存 */
			initParallelJobProcessorCache4Spring();
			
		}
		
		if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
			
			//初始化注册Job
			initRegisterJobs();
		}
	}
	
	/**
	 * 初始化注册Job
	 * @throws InitException
	 */
	public void initRegisterJobs() throws InitException {
		
		Map<String, String> jobMap = clientConfig.getJobMap();
		
		if(CollectionUtils.isEmpty(jobMap)) {
			logger.warn("[JobProcessorFactory]: jobMap is empty after check, clientConfig:" + clientConfig);
			return ;
		}
		
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.warn("[JobProcessorFactory]: initRegisterJobs error, serverList is empty");
			return ;
		}
		for(String server : serverList) {

			//向服务器注册JobMap
			initRegisterJobMap(server, jobMap);
		}
	}
	
	/**
	 * 向服务器注册JobMap
	 * @param server
	 * @param jobMap
	 */
	private void initRegisterJobMap(String server, Map<String, String> jobMap) {
		
		//注册到服务器
		InvocationContext.setRemoteMachine(new RemoteMachine(server, 10 * DEFAULT_INVOKE_TIMEOUT));
		Result<Boolean> registerResult = serverService.registerJobs(clientConfig.getMachine(), jobMap);
		
		if(null == registerResult) {
			logger.error("[JobProcessorFactory]: initRegisterJobMap timeout error"
					+ ", jobMap:" + jobMap + ", machine:" + clientConfig.getMachine());
			return ;
		}
		
		logger.warn("[JobProcessorFactory]: initRegisterJobMap"
				+ ", registerResult:" + registerResult + ", machine:" + clientConfig.getMachine());
	}

	/**
	 * 初始化SimpleJobProcessor缓存
	 */
	@SuppressWarnings("rawtypes")
	private void initSimpleJobProcessorCache4Spring() {
		Map<String, SimpleJobProcessor> beanMap = applicationContext.getBeansOfType(SimpleJobProcessor.class);
		if(null == beanMap || beanMap.isEmpty()) {
			logger.warn("[JobProcessorFactory]: initSimpleJobProcessorCache beanMap is empty");
			return ;
		}
		Iterator iterator = beanMap.entrySet().iterator();
		while(iterator.hasNext()) { 
			Map.Entry entry = (Map.Entry)iterator.next();
			String beanId = (String)entry.getKey();
			SimpleJobProcessor simpleJobProcessor = (SimpleJobProcessor)entry.getValue();
			String jobProcessor = simpleJobProcessor.getClass().getName();
			if(AopUtils.isAopProxy(simpleJobProcessor) 
					|| AopUtils.isCglibProxy(simpleJobProcessor) 
					|| AopUtils.isJdkDynamicProxy(simpleJobProcessor)) {
				jobProcessor = AopUtils.getTargetClass(simpleJobProcessor).getName();
			}
			ConcurrentHashMap<String, SimpleJobProcessor> simpleJobProcessorMap = this.simpleJobProcessorCache.get(jobProcessor);
			if(null == simpleJobProcessorMap) {
				simpleJobProcessorMap = new ConcurrentHashMap<String, SimpleJobProcessor>();
				this.simpleJobProcessorCache.put(jobProcessor, simpleJobProcessorMap);
			}
			simpleJobProcessorMap.put(beanId, simpleJobProcessor);
			logger.warn("[JobProcessorFactory]: initSimpleJobProcessorCache jobProcessor:" + jobProcessor + ", beanId:" + beanId);
		}
	}
	
	/**
	 * 初始化ParallelJobProcessor缓存
	 */
	@SuppressWarnings("rawtypes")
	private void initParallelJobProcessorCache4Spring() {
		Map<String, ParallelJobProcessor> beanMap = applicationContext.getBeansOfType(ParallelJobProcessor.class);
		if(null == beanMap || beanMap.isEmpty()) {
			logger.warn("[JobProcessorFactory]: initParallelJobProcessorCache beanMap is empty");
			return ;
		}
		Iterator iterator = beanMap.entrySet().iterator();
		while(iterator.hasNext()) { 
			Map.Entry entry = (Map.Entry)iterator.next();
			String beanId = (String)entry.getKey();
			ParallelJobProcessor parallelJobProcessor = (ParallelJobProcessor)entry.getValue();
			String jobProcessor = parallelJobProcessor.getClass().getName();
			if(AopUtils.isAopProxy(parallelJobProcessor) 
					|| AopUtils.isCglibProxy(parallelJobProcessor) 
					|| AopUtils.isJdkDynamicProxy(parallelJobProcessor)) {
				jobProcessor = AopUtils.getTargetClass(parallelJobProcessor).getName();
			}
			ConcurrentHashMap<String, ParallelJobProcessor> parallelJobProcessorMap = this.parallelJobProcessorCache.get(jobProcessor);
			if(null == parallelJobProcessorMap) {
				parallelJobProcessorMap = new ConcurrentHashMap<String, ParallelJobProcessor>();
				this.parallelJobProcessorCache.put(jobProcessor, parallelJobProcessorMap);
			}
			parallelJobProcessorMap.put(beanId, parallelJobProcessor);
			logger.warn("[JobProcessorFactory]: initParallelJobProcessorCache jobProcessor:" + jobProcessor + ", beanId:" + beanId);
		}
	}
	
	/**
	 * 初始化检查Job处理器
	 * @throws InitException
	 */
	@SuppressWarnings("rawtypes")
	private void initCheckJobProcessors() throws InitException {
		
		if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
			
			Map<String, String> jobMap = clientConfig.getJobMap();
			
			if(CollectionUtils.isEmpty(jobMap)) {
				logger.warn("[JobProcessorFactory]: initCheckJobProcessors jobMap is isEmpty error"
						+ ", clientConfig:" + clientConfig.toString());
				return ;
			}
			
			Iterator iterator = jobMap.entrySet().iterator();
			while(iterator.hasNext()) { 
				Map.Entry entry = (Map.Entry)iterator.next();
				
				String taskName = (String)entry.getKey();
				String jobProcessor = (String)entry.getValue();
				
				Job job = new Job();
				job.setTaskName(taskName);
				job.setJobProcessor(jobProcessor);
				
				createAndGetSimpleJobProcessor(job, true);
				
				createAndGetParallelJobProcessor(job, true);
			}
			
			return ;
		}
		
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[JobProcessorFactory]: initCheckJobProcessors serverList is isEmpty error");
			return ;
		}
		
		InvocationContext.setRemoteMachine(new RemoteMachine(RandomUtil.getRandomObj(serverList)));
		List<Job> jobList = serverService.acquireJobList(clientConfig.getGroupId());
		if(CollectionUtils.isEmpty(jobList)) {
			logger.warn("[JobProcessorFactory]: acquireJobList jobList is isEmpty error");
			return ;
		}
		
		for(Job job : jobList) {
			try {
				if(CommonUtil.isSimpleJob(job.getType())) {
					createAndGetSimpleJobProcessor(job, true);
				} else {
					createAndGetParallelJobProcessor(job, true);
				}
			} catch (Throwable e) {
				logger.warn("[JobProcessorFactory]: initCheckJobProcessors error", e);
			}
		}
		
	}
	
	/**
	 * 创建并获取SimpleJobProcessor
	 * @param job
	 * @param isCheck
	 * @return
	 */
	public SimpleJobProcessor createAndGetSimpleJobProcessor(Job job, boolean isCheck) {
		String[] jobProcessorProperties = job.getJobProcessor().split(COLON);
		String jobProcessor = jobProcessorProperties[POSITION_PROCESSOR].trim();
		
		ConcurrentHashMap<String, SimpleJobProcessor> simpleJobProcessorMap = this.simpleJobProcessorCache.get(jobProcessor);
		if(null == simpleJobProcessorMap && ! clientConfig.isEveryTimeNew()) {
			simpleJobProcessorMap = new ConcurrentHashMap<String, SimpleJobProcessor>();
			this.simpleJobProcessorCache.put(jobProcessor, simpleJobProcessorMap);
		}
		
		SimpleJobProcessor simpleJobProcessor = null;
		
		if(! CollectionUtils.isEmpty(simpleJobProcessorMap) && ! clientConfig.isEveryTimeNew()) {
			
			if(1 == simpleJobProcessorMap.size()) {
				simpleJobProcessor = (SimpleJobProcessor)simpleJobProcessorMap.values().toArray()[0];
				return simpleJobProcessor;
			}
			
			if(jobProcessorProperties.length < 3) {
				simpleJobProcessor = simpleJobProcessorMap.get(jobProcessor);
				if(simpleJobProcessor != null) {
					return simpleJobProcessor;
				}
				throw new RuntimeException("[JobProcessorFactory]: you have more than one jobProcessor instance for " + job.getJobProcessor() 
						+ ", but you do not fill beanId on console. please check job config, jobId:" + job.getId() 
						+ "! you should fill the config item like this 'com.xxx.app.JobProcessor::beanId'.");
			} else {
				String beanId = jobProcessorProperties[POSITION_BEAN_ID].trim();
				if(StringUtil.isNotBlank(beanId)) {
					simpleJobProcessor = simpleJobProcessorMap.get(beanId);
					if(simpleJobProcessor != null) {
						return simpleJobProcessor;
					}
				}
				throw new RuntimeException("[JobProcessorFactory]: you have more than one jobProcessor instance for " + job.getJobProcessor() 
						+ ". you maybe fill wrong beanId on console. please check job config, jobId:" + job.getId() 
						+ "! DtsClient can not find bean:" + beanId);
			}
		}
		
		Object object = proxyService.newInstance(jobProcessor);
		if(null == object) {
			throw new RuntimeException("[JobProcessorFactory]: can not create a new simple job processor, please check:" + job.getJobProcessor());
		}
		
		Type[] types = proxyService.aquireInterface(object);
		if(0 == types.length) {
			throw new RuntimeException("[JobProcessorFactory]: your choice is simple job"
					+ ", but not implements " + SimpleJobProcessor.class.getName() 
					+ ", please check:" + job.getJobProcessor());
		}
		
		checkInterface(types, job);
		
		simpleJobProcessor = (SimpleJobProcessor)object;
		
		if(clientConfig.isSpring()) {
			initSpringJobProcessor(job, simpleJobProcessor);
		}
		
		if(! isCheck && ! clientConfig.isEveryTimeNew()) {
			simpleJobProcessorMap.put(jobProcessor, simpleJobProcessor);
		}
		return simpleJobProcessor;
	}
	
	/**
	 * 创建并获取ParallelJobProcessor
	 * @param job
	 * @param isCheck
	 * @return
	 */
	public ParallelJobProcessor createAndGetParallelJobProcessor(Job job, boolean isCheck) {
		String[] jobProcessorProperties = job.getJobProcessor().split(COLON);
		String jobProcessor = jobProcessorProperties[POSITION_PROCESSOR].trim();
		
		ConcurrentHashMap<String, ParallelJobProcessor> parallelJobProcessorMap = this.parallelJobProcessorCache.get(jobProcessor);
		if(null == parallelJobProcessorMap && ! clientConfig.isEveryTimeNew()) {
			parallelJobProcessorMap = new ConcurrentHashMap<String, ParallelJobProcessor>();
			this.parallelJobProcessorCache.put(jobProcessor, parallelJobProcessorMap);
		}
		
		ParallelJobProcessor parallelJobProcessor = null;
		
		if(! CollectionUtils.isEmpty(parallelJobProcessorMap) && ! clientConfig.isEveryTimeNew()) {
			if(1 == parallelJobProcessorMap.size()) {
				parallelJobProcessor = (ParallelJobProcessor)parallelJobProcessorMap.values().toArray()[0];
				return parallelJobProcessor;
			}
			
			if(jobProcessorProperties.length < 3) {
				parallelJobProcessor = parallelJobProcessorMap.get(jobProcessor);
				if(parallelJobProcessor != null) {
					return parallelJobProcessor;
				}
				throw new RuntimeException("[JobProcessorFactory]: you have more than one jobProcessor instance for " + job.getJobProcessor() 
						+ ", but you do not fill beanId on console. please check job config, jobId:" + job.getId() 
						+ "! you should fill the config item like this 'com.xxx.app.JobProcessor::beanId'.");
			} else {
				String beanId = jobProcessorProperties[POSITION_BEAN_ID].trim();
				if(StringUtil.isNotBlank(beanId)) {
					parallelJobProcessor = parallelJobProcessorMap.get(beanId);
					if(parallelJobProcessor != null) {
						return parallelJobProcessor;
					}
				}
				throw new RuntimeException("[JobProcessorFactory]: you have more than one jobProcessor instance for " + job.getJobProcessor() 
						+ ". you maybe fill wrong beanId on console. please check job config, jobId:" + job.getId() 
						+ "! DtsClient can not find bean:" + beanId);
			}
		}
		
		Object object = proxyService.newInstance(jobProcessor);
		if(null == object) {
			throw new RuntimeException("[JobProcessorFactory]: can not create a new parallel job processor, please check:" + job.getJobProcessor());
		}
		
		Type[] types = proxyService.aquireInterface(object);
		if(0 == types.length) {
			throw new RuntimeException("[JobProcessorFactory]: your choice is parallel job"
					+ ", but not implements " + ParallelJobProcessor.class.getName() 
					+ ", please check:" + job.getJobProcessor());
		}
		
		checkInterface(types, job);
		
		parallelJobProcessor = (ParallelJobProcessor)object;
		
		if(clientConfig.isSpring()) {
			initSpringJobProcessor(job, parallelJobProcessor);
		}
		
		if(! isCheck && ! clientConfig.isEveryTimeNew()) {
			parallelJobProcessorMap.put(jobProcessor, parallelJobProcessor);
		}
		return parallelJobProcessor;
	}
	
	/**
	 * 接口检查
	 * @param types
	 * @param job
	 */
	private void checkInterface(Type[] types, Job job) {
		boolean simple = false;
		boolean parallel = false;
		for(Type type : types) {
			if(type.equals(SimpleJobProcessor.class)) {
				simple = true;
			}
			if(type.equals(ParallelJobProcessor.class)) {
				parallel = true;
			}
		}
		if(CommonUtil.isSimpleJob(job.getType())) {
			if(simple && parallel) {
				throw new RuntimeException("[JobProcessorFactory]: your choice is simple job"
						+ ", can not implements both " + SimpleJobProcessor.class.getName() 
						+ " and " + ParallelJobProcessor.class.getName() 
						+ ", please check:" + job.getJobProcessor());
			} else if(simple && ! parallel) {
				
			} else if(! simple && parallel) {
				throw new RuntimeException("[JobProcessorFactory]: your choice is simple job"
						+ ", but implements " + ParallelJobProcessor.class.getName() 
						+ ", please check:" + job.getJobProcessor());
			} else {
				throw new RuntimeException("[JobProcessorFactory]: your choice is simple job"
						+ ", but not implements " + SimpleJobProcessor.class.getName() 
						+ ", please check:" + job.getJobProcessor());
			}
		} else {
			if(simple && parallel) {
				throw new RuntimeException("[JobProcessorFactory]: your choice is parallel job"
						+ ", can not implements both " + SimpleJobProcessor.class.getName() 
						+ " and " + ParallelJobProcessor.class.getName() 
						+ ", please check:" + job.getJobProcessor());
			} else if(simple && ! parallel) {
				throw new RuntimeException("[JobProcessorFactory]: your choice is parallel job"
						+ ", but implements " + SimpleJobProcessor.class.getName() 
						+ ", please check:" + job.getJobProcessor());
			} else if(! simple && parallel) {
				
			} else {
				throw new RuntimeException("[JobProcessorFactory]: your choice is parallel job"
						+ ", but not implements " + ParallelJobProcessor.class.getName() 
						+ ", please check:" + job.getJobProcessor());
			}
		}
	}
	
	/**
	 * 初始化SpringJobProcessor
	 * @param job
	 * @param jobProcessor
	 */
	private void initSpringJobProcessor(Job job, Object jobProcessor) {
		String[] jobProcessorProperties = job.getJobProcessor().split(COLON);
		
		/** 填充字段 */
		Field[] fields = jobProcessor.getClass().getDeclaredFields();
		for(int i = 0 ; i < fields.length ; i ++) {
			fields[i].setAccessible(true);
			String fieldName = fields[i].getName();
			Object object = null;
			try {
				object = applicationContext.getBean(fieldName);
			} catch (Throwable e) {
				logger.warn("[JobProcessorFactory]: initSpringJobProcessor field not found"
						+ ", jobProcessor:" + jobProcessorProperties[0].trim() 
						+ ", fieldName:" + fieldName);
			}
			if(object != null) {
				try {
					fields[i].set(jobProcessor, object);
				} catch (Throwable e) {
					logger.error("[JobProcessorFactory]: initSpringJobProcessor field set error"
							+ ", jobProcessor:" + jobProcessorProperties[0].trim() 
							+ ", fieldName:" + fieldName 
							+ ", object:" + object, e);
					continue ;
				}
				logger.warn("[JobProcessorFactory]: initSpringJobProcessor set field" 
						+ ", jobProcessor:" + jobProcessorProperties[0].trim() 
						+ ", fieldName:" + fieldName);
			}
		}
		
		/** 调用初始化方法 */
		if(jobProcessorProperties.length >= 2) {
			String initMethod = jobProcessorProperties[1].trim();
			if(StringUtil.isNotBlank(initMethod)) {
				Method method = null;
				try {
					method = jobProcessor.getClass().getDeclaredMethod(initMethod);
				} catch (Throwable e) {
					logger.error("[JobProcessorFactory]: initSpringJobProcessor getDeclaredMethod error"
							+ ", jobProcessor:" + jobProcessorProperties[0].trim() 
							+ ", initMethod:" + initMethod, e);
				}
				if(null == method) {
					logger.error("[JobProcessorFactory]: initSpringJobProcessor getDeclaredMethod failed"
							+ ", jobProcessor:" + jobProcessorProperties[0].trim() 
							+ ", initMethod:" + initMethod);
				} else {
					try {
						method.invoke(jobProcessor);
					} catch (Throwable e) {
						logger.error("[JobProcessorFactory]: initSpringJobProcessor invoke initMethod error"
								+ ", jobProcessor:" + jobProcessorProperties[0].trim() 
								+ ", initMethod:" + initMethod, e);
					}
				}
			} else {
				logger.error("[JobProcessorFactory]: initSpringJobProcessor initMethod is null"
						+ ", jobProcessor:" + jobProcessorProperties[0].trim() 
						+ ", initMethod:" + initMethod);
			}
		}
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
