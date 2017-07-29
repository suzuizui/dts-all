package com.le.dts.common.datasource;

import com.le.dts.common.util.DBShardUtil;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by gaobo3 on 2016/4/8.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return "DB_" + String.valueOf(lookupKey);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String shardStr = DBShardUtil.getShardThreadLocal();
        String[] shards = shardStr.split(",");
        return DBShardUtil.getDBShard(shards[0], Integer.parseInt(shards[1]), Integer.parseInt(shards[2]));
    }

}
