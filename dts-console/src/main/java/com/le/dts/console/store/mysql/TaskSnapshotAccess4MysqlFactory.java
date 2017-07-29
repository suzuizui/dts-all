package com.le.dts.console.store.mysql;

import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.util.DBShardUtil;
import com.le.dts.console.store.TaskSnapshotAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by gaobo3 on 2016/4/8.
 */
public class TaskSnapshotAccess4MysqlFactory implements FactoryBean, ApplicationContextAware {

    private int dbCount;
    private int tableCount;
    private final TaskSnapshotAccess4Mysql taskSnapshotAccess4Mysql = new TaskSnapshotAccess4Mysql();

    public void setDbCount(int dbCount) {
        this.dbCount = dbCount;
    }

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(
                //被代理类的ClassLoader
                taskSnapshotAccess4Mysql.getClass().getClassLoader(),
                //要被代理的接口,本方法返回对象会自动声称实现了这些接口
                taskSnapshotAccess4Mysql.getClass().getInterfaces(),
                //代理处理器对象
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("toString".equals(method.getName()))
                            return "TaskSnapshotAccess Proxy";

                        String shardKey;
                        if (args[0] instanceof TaskSnapshot) {
                            TaskSnapshot taskSnapshot = (TaskSnapshot) args[0];
                            taskSnapshot.setTableIndex(DBShardUtil.getTableShard(String.valueOf(taskSnapshot.getJobInstanceId()), dbCount, tableCount));
                            shardKey = String.valueOf(taskSnapshot.getJobInstanceId());
                        } else if (args[0] instanceof Long) {
                            shardKey = String.valueOf(args[0]);
                        } else {
                            throw new RuntimeException("TaskSnapshotAccess4Mysql Proxy: Param Error");
                        }
                        DBShardUtil.setShardThreadLocal(shardKey + "," + dbCount + "," + tableCount);
                        return method.invoke(taskSnapshotAccess4Mysql, args);
                    }
                });
    }

    @Override
    public Class<?> getObjectType() {
        return TaskSnapshotAccess.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        taskSnapshotAccess4Mysql.setSqlMapClients((SqlMapClients)applicationContext.getBean("sqlMapClients"));
        taskSnapshotAccess4Mysql.setDbCount(dbCount);
        taskSnapshotAccess4Mysql.setTableCount(tableCount);
    }
}
