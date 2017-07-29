package com.le.dts.common.util;

/**
 * Created by gaobo3 on 2016/4/8.
 */
public class DBShardUtil {

    private static final ThreadLocal<String> shardThreadLocal = new ThreadLocal<String>();

    public static String getShardThreadLocal() {
        return shardThreadLocal.get();
    }

    public static void setShardThreadLocal(String shardStr) {
        shardThreadLocal.set(shardStr);
    }

    public static void removeShardThreadLocal() {
        shardThreadLocal.remove();
    }

    public static String getDBShard(String shardKey, int dbCount, int tableCount) {
        int hashCode = Math.abs(shardKey.hashCode());
        int total = dbCount * tableCount;
        return "DB_" + String.valueOf(hashCode % total / tableCount);
    }

    public static String getTableShard(String shardKey, int dbCount, int tableCount) {
        int hashCode = Math.abs(shardKey.hashCode());
        int total = dbCount * tableCount;
        String tableIndex = String.valueOf(hashCode % total);
        if(tableIndex.length() < 4) {
            while (tableIndex.length() < 4)
                tableIndex = "0" + tableIndex;
        }
        return tableIndex;
    }
}
