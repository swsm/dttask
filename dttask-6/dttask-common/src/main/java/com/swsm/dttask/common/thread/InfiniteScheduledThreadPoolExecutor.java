package com.swsm.dttask.common.thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class InfiniteScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public InfiniteScheduledThreadPoolExecutor(int count, ThreadFactory threadFactory) {
        super(count, threadFactory); // 初始线程池大小为1
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        // 在任务执行完成后检查是否需要扩容
        if (!getQueue().isEmpty() && getActiveCount() == getCorePoolSize()) {
            setCorePoolSize(getCorePoolSize() * 2); // 每次扩容为当前大小的两倍
        }
    }
}
