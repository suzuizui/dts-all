package com.le.dts.console.store.mysql.access;

import com.le.dts.console.config.ConsoleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源
 * @author tianyao.myc
 *
 */
public class DataSource implements Constants {

	/** TDDL数据源 */
	private final DynamicDataSource dynamicDataSource = new DynamicDataSource();

	/** 数据源 */
	private final DruidDataSource druidDataSource = new DruidDataSource();

	/** 数据源Meta库 */
	private final DruidDataSource druidDataSourceMeta = new DruidDataSource();
	
	//事务模板
	private final TransactionTemplate transactionTemplateMeta = new TransactionTemplate();
	
	private boolean useTddl;
	
	@Autowired
	private ConsoleConfig consoleConfig;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		if(useTddl) {
			
			//初始化TDDL
			initTddl();
		} else {
			
			//初始化RDS
			initRds();
		}
	}
	
	/**
	 * 初始化RDS
	 * @throws InitException
	 */
	private void initRds() throws InitException {
		
		druidDataSource.setDriverClassName(consoleConfig.getDriverClassName());
		druidDataSource.setUrl(consoleConfig.getUrl().get(0));
		druidDataSource.setUsername(consoleConfig.getUsername().get(0));
		druidDataSource.setPassword(consoleConfig.getPassword().get(0));
		druidDataSource.setMaxActive(consoleConfig.getMaxActive());
		
		druidDataSourceMeta.setDriverClassName(consoleConfig.getDriverClassName4Meta());
		druidDataSourceMeta.setUrl(consoleConfig.getUrl4Meta());
		druidDataSourceMeta.setUsername(consoleConfig.getUsername4Meta());
		druidDataSourceMeta.setPassword(consoleConfig.getPassword4Meta());
		druidDataSourceMeta.setMaxActive(consoleConfig.getMaxActive4Meta());
		
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(druidDataSourceMeta);
		transactionTemplateMeta.setTransactionManager(transactionManager);
	}
	
	/**
	 * 初始化TDDL
	 * @throws InitException
	 */
	@SuppressWarnings("deprecation")
	private void initTddl() throws InitException {
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		for(int i = 0; i < consoleConfig.getUrl().size(); i++) {
			DruidDataSource druidDataSource = new DruidDataSource();
			druidDataSource.setDriverClassName(consoleConfig.getDriverClassName());
			druidDataSource.setUrl(consoleConfig.getUrl().get(i));
			druidDataSource.setUsername(consoleConfig.getUsername().get(i));
			druidDataSource.setPassword(consoleConfig.getPassword().get(i));
			druidDataSource.setMaxActive(consoleConfig.getMaxActive());
			targetDataSources.put(i, druidDataSource);
		}
		dynamicDataSource.setTargetDataSources(targetDataSources);
		dynamicDataSource.afterPropertiesSet();

		druidDataSourceMeta.setDriverClassName(consoleConfig.getDriverClassName4Meta());
		druidDataSourceMeta.setUrl(consoleConfig.getUrl4Meta());
		druidDataSourceMeta.setUsername(consoleConfig.getUsername4Meta());
		druidDataSourceMeta.setPassword(consoleConfig.getPassword4Meta());
		druidDataSourceMeta.setMaxActive(consoleConfig.getMaxActive4Meta());

		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(druidDataSourceMeta);
		transactionTemplateMeta.setTransactionManager(transactionManager);
	}

	public boolean isUseTddl() {
		return useTddl;
	}

	public void setUseTddl(boolean useTddl) {
		this.useTddl = useTddl;
	}

	public DynamicDataSource getTaskDataSource() {
		return dynamicDataSource;
	}

	public DruidDataSource getGroupDataSource() {
		return druidDataSourceMeta;
	}

	public DruidDataSource getDruidDataSource() {
		return druidDataSource;
	}

	public DruidDataSource getDruidDataSourceMeta() {
		return druidDataSourceMeta;
	}

	public TransactionTemplate getTransactionTemplateMeta() {
		return transactionTemplateMeta;
	}

}
