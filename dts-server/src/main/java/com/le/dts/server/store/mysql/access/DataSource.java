package com.le.dts.server.store.mysql.access;

import com.alibaba.druid.pool.DruidDataSource;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.datasource.DynamicDataSource;
import com.le.dts.common.exception.InitException;
import com.le.dts.server.context.ServerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源
 * @author tianyao.myc
 *
 */
public class DataSource implements ServerContext, Constants {

	/** 动态数据源 */
	private final DynamicDataSource dynamicDataSource = new DynamicDataSource();

	/** 数据源 */
	private final DruidDataSource druidDataSource = new DruidDataSource();
	
	/** 数据源Meta库 */
	private final DruidDataSource druidDataSourceMeta = new DruidDataSource();
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		if(serverConfig.isUseTddl()) {
			
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
		
		druidDataSource.setDriverClassName(serverConfig.getDriverClassName());
		druidDataSource.setUrl(serverConfig.getUrl().get(0));
		druidDataSource.setUsername(serverConfig.getUsername().get(0));
		druidDataSource.setPassword(serverConfig.getPassword().get(0));
		druidDataSource.setMaxActive(serverConfig.getMaxActive());
		
		druidDataSourceMeta.setDriverClassName(serverConfig.getDriverClassName4Meta());
		druidDataSourceMeta.setUrl(serverConfig.getUrl4Meta());
		druidDataSourceMeta.setUsername(serverConfig.getUsername4Meta());
		druidDataSourceMeta.setPassword(serverConfig.getPassword4Meta());
		druidDataSourceMeta.setMaxActive(serverConfig.getMaxActive4Meta());
		
	}
	
	/**
	 * 初始化TDDL
	 * @throws InitException
	 */
	private void initTddl() throws InitException {
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		for(int i = 0; i < serverConfig.getUrl().size(); i++) {
			DruidDataSource druidDataSource = new DruidDataSource();
			druidDataSource.setDriverClassName(serverConfig.getDriverClassName());
			druidDataSource.setUrl(serverConfig.getUrl().get(i));
			druidDataSource.setUsername(serverConfig.getUsername().get(i));
			druidDataSource.setPassword(serverConfig.getPassword().get(i));
			druidDataSource.setMaxActive(serverConfig.getMaxActive());
			targetDataSources.put(i, druidDataSource);
		}
		dynamicDataSource.setTargetDataSources(targetDataSources);
		dynamicDataSource.afterPropertiesSet();

		druidDataSourceMeta.setDriverClassName(serverConfig.getDriverClassName4Meta());
		druidDataSourceMeta.setUrl(serverConfig.getUrl4Meta());
		druidDataSourceMeta.setUsername(serverConfig.getUsername4Meta());
		druidDataSourceMeta.setPassword(serverConfig.getPassword4Meta());
		druidDataSourceMeta.setMaxActive(serverConfig.getMaxActive4Meta());
	}

	public DynamicDataSource getDataSource() {
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

}
