package com.le.dts.console;

import com.le.dts.console.config.ConsoleConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.exception.InitException;
import com.le.dts.console.api.ApiService;

/**
 * DTS控制台
 * @author tianyao.myc
 *
 */
public class DtsConsole {

	private static final Log logger = LogFactory.getLog(DtsConsole.class);

	@Autowired
	private ApiService apiService;
	
	@Autowired
	private ConsoleConfig consoleConfig;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 控制台各项参数配置初始化 */
		consoleConfig.init(apiService);

//        try {
//            SpasSdkServiceFacade.init(dAuthBean.getAppName(), dAuthBean.getDauthAccessKey(), dAuthBean.getDauthSecretKey());
//        } catch (Throwable e) {
//            throw new InitException("[DtsConsole]: SpasSdkServiceFacade.init error"
//                    + ", accessKey:" + dAuthBean.getDauthAccessKey()
//                    + ", secretkey:" + dAuthBean.getDauthSecretKey(), e);
//        }
		
		logger.warn("[DtsConsole]: init over, consoleConfig:" + consoleConfig.toString());
	}
}
