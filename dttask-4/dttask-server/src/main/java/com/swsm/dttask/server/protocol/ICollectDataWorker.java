package com.swsm.dttask.server.protocol;

import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author swsm
 * @date 2023-12-08
 */
public interface ICollectDataWorker {

    void addCollectTask(long dttaskJobId, Runnable runnable, int delay, int period, TimeUnit timeUnit);

    void removeCollectMonitor(long dttaskJobId);

    ScheduledFuture<Void> getCollectMonitorScheduledFuture(long dttaskJobId);
    
    void doCollect(Set<Long> dttaskJobId);

    void stopCollect(Set<Long> dttaskJobId);
}
