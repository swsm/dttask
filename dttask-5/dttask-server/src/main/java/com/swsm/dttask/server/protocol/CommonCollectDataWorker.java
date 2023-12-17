package com.swsm.dttask.server.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.swsm.dttask.common.model.entity.DttaskJob;
import com.swsm.dttask.common.thread.CustomThreadFactory;
import com.swsm.dttask.common.thread.InfiniteScheduledThreadPoolExecutor;
import com.swsm.dttask.server.BeanUseHelper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-08
 */
@Component
@Slf4j
public class CommonCollectDataWorker implements ICollectDataWorker {

    private ScheduledExecutorService collectDataExecutor
            = new InfiniteScheduledThreadPoolExecutor(10, new CustomThreadFactory("CommonCollectDataWorker-THREAD-"));
    private Map<Long, ScheduledFuture<Void>> monitorDttaskJobStateMap = new ConcurrentHashMap<>();

    public void addCollectTask(long dttaskJobId, Runnable runnable, int delay, int period, TimeUnit timeUnit) {
        ScheduledFuture<Void> scheduledFuture = (ScheduledFuture<Void>) collectDataExecutor.scheduleAtFixedRate(runnable, delay, period, timeUnit);
        monitorDttaskJobStateMap.put(dttaskJobId, scheduledFuture);
    }

    public synchronized void removeCollectMonitor(long dttaskJobId) {
        monitorDttaskJobStateMap.remove(dttaskJobId);
    }

    public ScheduledFuture<Void> getCollectMonitorScheduledFuture(long dttaskJobId) {
        return monitorDttaskJobStateMap.get(dttaskJobId);
    }
    
    @Override
    public void doCollect(Set<Long> dttaskJobIds) {
        log.info("进入 CommonCollectDataWorker.doCollect, dttaskJobIds={}", dttaskJobIds);
        List<DttaskJob> dttaskJobs = BeanUseHelper.entityHelpService().queryDttaskJob(dttaskJobIds);
        for (DttaskJob dttaskJob : dttaskJobs) {
            ProtocolContext protocolContext = new ProtocolContext();
            protocolContext.setDttaskId(dttaskJob.getDttaskId());
            protocolContext.setDeviceId(dttaskJob.getDeviceId());
            protocolContext.setDttaskJobId(dttaskJob.getId());
            protocolContext.setLinkType(dttaskJob.getLinkType());
            JSONObject param = new JSONObject();
            param.put("linkSpec", JSON.parseObject(JSON.toJSONString(dttaskJob.getLinkSpec())));
            param.put("jobSpec", JSON.parseObject(JSON.toJSONString(dttaskJob.getJobSpec())));
            protocolContext.setParam(param);
            IProtocol protocol = ProtocolManager.getProtocol(protocolContext.getLinkType());
            protocol.start(protocolContext);
        }
    }

    @Override
    public void stopCollect(Set<Long> dttaskJobIds) {
        log.info("进入 CommonCollectDataWorker.stopCollect, dttaskJobIds={}", dttaskJobIds);
        for (Long dttaskJobId : dttaskJobIds) {
            ScheduledFuture<?> scheduledFuture = getCollectMonitorScheduledFuture(dttaskJobId);
            scheduledFuture.cancel(true);
            removeCollectMonitor(dttaskJobId);
        }
    }
}
