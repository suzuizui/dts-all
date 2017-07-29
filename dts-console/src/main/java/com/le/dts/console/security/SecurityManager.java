package com.le.dts.console.security;

import io.netty.util.internal.ConcurrentSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 安全Manager,防重防,限流(wait to do);
 * Created by luliang on 15/1/22.
 */
public class SecurityManager {

    private static final Log logger = LogFactory.getLog(SecurityManager.class);

    private static final String REPLAY_THREAD_NAME = "replay-thread";

    final private Set<String> guidCache = new ConcurrentSet<String>();

    private ScheduledExecutorService replayExecutorService = Executors
            .newScheduledThreadPool(1, new ThreadFactory() {

                public Thread newThread(Runnable runnable) {
                    return new Thread(runnable, REPLAY_THREAD_NAME);
                }
            });

    public void init() {
        replayExecutorService.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                guidCache.clear();
                logger.warn("clear replay guid data cache!");
            }
        }, 10 * 1000, 600 * 1000, TimeUnit.MICROSECONDS);
    }



    public Set<String> getGuidCache() {
        return guidCache;
    }

}
