package com.le.dts.console.module.screen.tools;

import java.util.List;

import com.le.dts.console.store.ServerJobInstanceMappingAccess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;

public class DeleteAllInstanceMapping {
	
	private static final Log logger = LogFactory.getLog(DeleteAllInstanceMapping.class);

	@Autowired
	private ServerJobInstanceMappingAccess serverJobInstanceMappingAccess;
	
	public void execute(Context context) {
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				List<ServerJobInstanceMapping> mappingList = selectLimit();

				int counter = 0;
				while(! CollectionUtils.isEmpty(mappingList)) {
					
					for(ServerJobInstanceMapping mapping : mappingList) {
						
						int result = 0;
						try {
							result = serverJobInstanceMappingAccess.deleteById(mapping);
						} catch (Throwable e) {
							logger.error("[DeleteAllInstanceMapping]: deleteById error", e);
						}
						
						if(result > 0) {
							counter ++;
						}
					}
					
					logger.info("[DeleteAllInstanceMapping]: counter:" + counter);
				}
			}
			
		}).start();
		
		context.put("result", "start ...");
	}
	
	public List<ServerJobInstanceMapping> selectLimit() {
		
		List<ServerJobInstanceMapping> mappingList = null;
		try {
			mappingList = serverJobInstanceMappingAccess.selectLimit();
		} catch (Throwable e) {
			logger.error("[DeleteAllInstanceMapping]: selectLimit error", e);
		}
		
		return mappingList;
	}
	
}
