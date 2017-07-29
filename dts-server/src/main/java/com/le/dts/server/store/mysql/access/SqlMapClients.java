package com.le.dts.server.store.mysql.access;

import com.le.dts.server.context.ServerContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * SqlMapClients
 * @author tianyao.myc
 *
 */
public class SqlMapClients implements ServerContext, Constants {

	/** sqlMapClient */
	private SqlMapClientTemplate sqlMapClient;
	
	/** sqlMapClientMeta */
	private SqlMapClientTemplate sqlMapClientMeta;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		SqlMapClientFactoryBean sqlMapClientFactoryBean = new SqlMapClientFactoryBean();
		ClassPathResource classPathResource = new ClassPathResource(SQL_MAP_CONFIG_PATH);
		sqlMapClientFactoryBean.setConfigLocation(classPathResource);
		
		if(serverConfig.isUseTddl()) {
			sqlMapClientFactoryBean.setDataSource(dataSource.getDataSource());
		} else {
			sqlMapClientFactoryBean.setDataSource(dataSource.getDruidDataSource());
		}
		
		try {
			sqlMapClientFactoryBean.afterPropertiesSet();
		} catch (Throwable e) {
			throw new InitException("[SqlMapClients]: init afterPropertiesSet error", e);
		}
		
		SqlMapClient client = (SqlMapClient)sqlMapClientFactoryBean.getObject();
		
		sqlMapClient = new SqlMapClientTemplate(client);
		sqlMapClient.setSqlMapClient(client);
		
		if(serverConfig.isUseTddl()) {
			sqlMapClient.setDataSource(dataSource.getDataSource());
		} else {
			sqlMapClient.setDataSource(dataSource.getDruidDataSource());
		}
		
		sqlMapClientMeta = new SqlMapClientTemplate(client);
		sqlMapClientMeta.setSqlMapClient(client);
		
		if(serverConfig.isUseTddl()) {
			sqlMapClientMeta.setDataSource(dataSource.getGroupDataSource());
		} else {
			sqlMapClientMeta.setDataSource(dataSource.getDruidDataSourceMeta());
		}
	}

	public SqlMapClientTemplate getSqlMapClient() {
		return sqlMapClient;
	}

	public SqlMapClientTemplate getSqlMapClientMeta() {
		return sqlMapClientMeta;
	}

}
