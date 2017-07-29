package com.le.dts.console.store.mysql.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;
import com.ibatis.sqlmap.client.SqlMapClient;

public class SqlMapClients implements Constants {
	
	/** sqlMapClient */
	private SqlMapClientTemplate sqlMapClient;
	
	/** sqlMapClientMeta */
	private SqlMapClientTemplate sqlMapClientMeta;

    @Autowired
	private DataSource dataSource;
    
    private boolean useTddl;

	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		SqlMapClientFactoryBean sqlMapClientFactoryBean = new SqlMapClientFactoryBean();
		ClassPathResource classPathResource = new ClassPathResource(SQL_MAP_CONFIG_PATH);
		sqlMapClientFactoryBean.setConfigLocation(classPathResource);
		
		if(useTddl) {
			sqlMapClientFactoryBean.setDataSource(dataSource.getTaskDataSource());
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
		if(useTddl) {
			sqlMapClient.setDataSource(dataSource.getTaskDataSource());
		} else {
			sqlMapClient.setDataSource(dataSource.getDruidDataSource());
		}
		
		sqlMapClientMeta = new SqlMapClientTemplate(client);
		sqlMapClientMeta.setSqlMapClient(client);
		if(useTddl) {
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

	public boolean isUseTddl() {
		return useTddl;
	}

	public void setUseTddl(boolean useTddl) {
		this.useTddl = useTddl;
	}
	
}
