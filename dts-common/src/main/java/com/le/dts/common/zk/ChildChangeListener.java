package com.le.dts.common.zk;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.util.List;

/**
 * 节点变化的lisnter
 * @author hanxuan.mh
 * @since 2014-06-17
 */
public interface ChildChangeListener {

	/**
	 * @param changeEvent
	 */
	public void handleChangeEvent(ChildChangeEvent changeEvent);

	class ChildChangeEvent {
		private ChildChangeEnum changeType;

		/**
		 * 变化的节点
		 */
		private String changedChild;

		/**
		 * 变化后的节点列表
		 */
		private List<String> cachedNodeList;

		public ChildChangeEvent(ChildChangeEnum changeType, String changedChild, List<String> cachedNodeList) {
			this.changeType = changeType;
			this.changedChild = changedChild;
			this.cachedNodeList = cachedNodeList;
		}

		public ChildChangeEnum getChangeType() {
			return changeType;
		}

		public String getChangedChild() {
			return changedChild;
		}

		public List<String> getCachedNodeList() {
			return cachedNodeList;
		}

		@Override public String toString() {
			return "ChildChangeEvent{" +
					"changeType=" + changeType +
					", changedChild='" + changedChild + '\'' +
					", cachedNodeList=" + cachedNodeList +
					'}';
		}
	}

	public static enum ChildChangeEnum {
		CHILD_ADDED,
		CHILD_REMOVED;

		public static ChildChangeEnum convertCuratorType(PathChildrenCacheEvent.Type type) {
			ChildChangeEnum childChangeEnum = null;
			switch (type) {
			case CHILD_ADDED:
				childChangeEnum = CHILD_ADDED;
				break;
			case CHILD_REMOVED:
				childChangeEnum = CHILD_REMOVED;
				break;
			default:
				break;
			}
			return childChangeEnum;
		}
	}
}
