package com.le.dts.console.store;

import java.util.List;

import com.le.dts.common.domain.store.UserGroupRelation;
import com.le.dts.common.exception.AccessException;

/**
 * 用户资源关系;
 * Created by luliang on 14/12/22.
 */
public interface UserGroupRelationAccess {
    /**
     * 插入
     * @param userGroupRelation
     * @return
     * @throws com.alibaba.dts.common.exception.AccessException
     */
    public long insert(UserGroupRelation userGroupRelation) throws AccessException;

    /**
     * 查询
     * @param query
     * @return
     * @throws AccessException
     */
    public List<UserGroupRelation> query(UserGroupRelation query) throws AccessException;
    
    public List<UserGroupRelation> queryByGroupId(UserGroupRelation query) throws AccessException;

    /**
     * 更新
     * @param userGroupRelation
     * @return
     * @throws AccessException
     */
    public int update(UserGroupRelation userGroupRelation) throws AccessException;

    /**
     * 删除
     * @param userGroupRelation
     * @return
     * @throws AccessException
     */
    public int delete(UserGroupRelation userGroupRelation) throws AccessException;

}
