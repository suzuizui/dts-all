//package com.alibaba.dts.console.store.mysql;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.alibaba.dts.common.domain.store.OptimisticLock;
//import com.alibaba.dts.common.exception.AccessException;
//import com.alibaba.dts.console.context.ConsoleContext;
//import com.alibaba.dts.console.store.OptimisticLockAccess;
//import SqlMapClients;
//
///**
// * 乐观锁访问接口
// * Mysql实现
// * @author tianyao.myc
// *
// */
//public class OptimisticLockAccess4Mysql implements OptimisticLockAccess, ConsoleContext {
//
//	@Autowired
//	private SqlMapClients sqlMapClients;
//	
//	/**
//	 * 插入
//	 */
//	@Override
//	public long insert(OptimisticLock optimisticLock) throws AccessException {
//		Long result = null;
//		try {
//			result = (Long)sqlMapClients.getSqlMapClientMeta()
//					.insert("OptimisticLock.insert", optimisticLock);
//		} catch (Throwable e) {
//			throw new AccessException("[insert]: error", e);
//		}
//		if(null == result) {
//			return 0L;
//		}
//		return result;
//	}
//
//	/**
//	 * 查询
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<OptimisticLock> query(OptimisticLock query)
//			throws AccessException {
//		List<OptimisticLock> optimisticLockList = null;
//		try {
//			optimisticLockList = (List<OptimisticLock>)sqlMapClients.getSqlMapClientMeta()
//					.queryForList("OptimisticLock.query", query);
//		} catch (Throwable e) {
//			throw new AccessException("[query]: error", e);
//		}
//		return optimisticLockList;
//	}
//
//	/**
//	 * 更新
//	 */
//	@Override
//	public int update(OptimisticLock optimisticLock) throws AccessException {
//		int result = 0;
//		try {
//			result = sqlMapClients.getSqlMapClientMeta()
//					.update("OptimisticLock.update", optimisticLock);
//		} catch (Throwable e) {
//			throw new AccessException("[update]: error", e);
//		}
//		return result;
//	}
//
//	/**
//	 * 删除
//	 */
//	@Override
//	public int delete(OptimisticLock optimisticLock) throws AccessException {
//		int result = 0;
//		try {
//			result = sqlMapClients.getSqlMapClientMeta()
//					.delete("OptimisticLock.delete", optimisticLock);
//		} catch (Throwable e) {
//			throw new AccessException("[delete]: error", e);
//		}
//		return result;
//	}
//
//}
