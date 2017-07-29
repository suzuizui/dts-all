//package com.alibaba.dts.console.store;
//
//import java.util.List;
//
//import com.alibaba.dts.common.domain.store.OptimisticLock;
//import com.alibaba.dts.common.exception.AccessException;
//
///**
// * 乐观锁访问接口
// * @author tianyao.myc
// *
// */
//public interface OptimisticLockAccess {
//
//	/**
//	 * 插入
//	 * @param optimisticLock
//	 * @return
//	 * @throws AccessException
//	 */
//	public long insert(OptimisticLock optimisticLock) throws AccessException;
//	
//	/**
//	 * 查询
//	 * @param query
//	 * @return
//	 * @throws AccessException
//	 */
//	public List<OptimisticLock> query(OptimisticLock query) throws AccessException;
//	
//	/**
//	 * 更新
//	 * @param optimisticLock
//	 * @return
//	 * @throws AccessException
//	 */
//	public int update(OptimisticLock optimisticLock) throws AccessException;
//	
//	/**
//	 * 删除
//	 * @param optimisticLock
//	 * @return
//	 * @throws AccessException
//	 */
//	public int delete(OptimisticLock optimisticLock) throws AccessException;
//	
//}
