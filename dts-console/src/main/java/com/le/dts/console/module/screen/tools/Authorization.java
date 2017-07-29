package com.le.dts.console.module.screen.tools;

import com.le.dts.console.store.UserGroupRelationAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.store.UserGroupRelation;

public class Authorization {

	private static final Log logger = LogFactory.getLog(Authorization.class);
	
	@Autowired
	private UserGroupRelationAccess userGroupRelationAccess;
	
	public void execute(Context context, @Param(name = "userId") String userId, 
			@Param(name = "groupId") long groupId) {
		
		UserGroupRelation userGroupRelation = new UserGroupRelation();
		userGroupRelation.setGroupId(groupId);
		userGroupRelation.setUserId(userId);
		
		long result = 0L;
		try {
			result = userGroupRelationAccess.insert(userGroupRelation);
		} catch (Throwable e) {
			logger.error("[Authorization]: insert error"
					+ ", userId:" + userId 
					+ ", groupId:" + groupId, e);
		}
		
		context.put("result", result);
	}
	
}
