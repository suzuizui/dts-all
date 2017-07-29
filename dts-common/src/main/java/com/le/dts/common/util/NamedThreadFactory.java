package com.le.dts.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Moshan on 14-12-12.
 */
public class NamedThreadFactory implements ThreadFactory {

    final AtomicInteger threadNumber = new AtomicInteger();
    final String namePrefix;

    public NamedThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override public Thread newThread(Runnable r) {
        Thread result = new Thread(r, namePrefix + threadNumber.getAndIncrement());
        result.setDaemon(true);
        return result;
    }

}
