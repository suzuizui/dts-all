package com.le.dts.console.manager;

import java.util.List;

import com.le.dts.console.store.UserGroupRelationAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.UserGroupRelation;
import com.le.dts.common.exception.AccessException;

/**
 * Created by luliang on 14/12/22.
 */
public class UserGroupRelationManager {

    private static final Log logger = LogFactory.getLog(UserGroupRelationManager.class);

    @Autowired
    private UserGroupRelationAccess userGroupRelationAccess;

    public Result<Long> createUserGroupRelation(UserGroupRelation userGroupRelation) {
        Result<Long> result = new Result<Long>();
        try {
            long id = userGroupRelationAccess.insert(userGroupRelation);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(id);
        } catch (AccessException e) {
            result.setResultCode(ResultCode.CREATE_USER_GROUP_ERROR);
            logger.error("create group relation error!", e);
        }
        return result;
    }

    /**
     * 用户的组；
     * @param userGroupRelation
     * @return
     */
    public Result<List<UserGroupRelation>> queryUserGroupRelation(UserGroupRelation userGroupRelation) {
        Result<List<UserGroupRelation>> result = new Result<List<UserGroupRelation>>();
        try {
            List<UserGroupRelation> userGroupRelationList = userGroupRelationAccess.query(userGroupRelation);
            result.setData(userGroupRelationList);
            result.setResultCode(ResultCode.SUCCESS);
        } catch (AccessException e) {
            result.setResultCode(ResultCode.QUERY_USER_GROUP_ERROR);
            logger.error("query user group relation error!", e);
        }
        return result;
    }

    /**
     * 检查用户是否拥有该资源;
     * @param userGroupRelation
     * @return
     */
    public Result<Boolean> checkUserGroupRelation(UserGroupRelation userGroupRelation) {
        Result<Boolean> result = new Result<Boolean>();
        try {
            List<UserGroupRelation> userGroupRelationList = userGroupRelationAccess.query(userGroupRelation);
            if(userGroupRelationList == null || userGroupRelationList.size() == 0) {
                result.setData(false);
                result.setResultCode(ResultCode.USER_NOT_OWN_RESOURCE);
            } else {
                result.setData(true);
                result.setResultCode(ResultCode.SUCCESS);
            }
        } catch (AccessException e) {
            result.setData(false);
            result.setResultCode(ResultCode.QUERY_USER_GROUP_ERROR);
            logger.error("query user group relation error!", e);
        }
        return result;
    }

    public Result<Long> deleteUserGroupRelation(UserGroupRelation userGroupRelation) {
        Result<Long> result = new Result<Long>();
        try {
            long id = userGroupRelationAccess.delete(userGroupRelation);
            result.setResultCode(ResultCode.SUCCESS);
            result.setData(id);
        } catch (AccessException e) {
            result.setResultCode(ResultCode.DELETE_USER_GROUP_ERROR);
            logger.error("delete group relation error!", e);
        }
        return result;
    }

}
