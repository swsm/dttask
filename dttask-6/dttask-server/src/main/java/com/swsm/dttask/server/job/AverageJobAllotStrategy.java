package com.swsm.dttask.server.job;

import com.swsm.dttask.common.model.entity.DttaskJob;
import com.swsm.dttask.common.model.entity.Job;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.service.EntityHelpService;
import com.swsm.dttask.server.BeanUseHelper;
import com.swsm.dttask.server.model.NodeInfo;
import com.swsm.dttask.server.model.ServerInfo;
import com.swsm.dttask.server.protocol.CollectDataService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-08
 */
@Component
@Slf4j
public class AverageJobAllotStrategy extends AbstractJobAllotStrategy {

    @Autowired
    private CollectDataService collectDataService;

    @Override
    public JobAllotStrategyType getType() {
        return JobAllotStrategyType.AVERAGE;
    }

    @Override
    public void allotJob() {
        EntityHelpService entityHelpService = BeanUseHelper.entityHelpService();
        Map<Long, List<Job>> allotJobMap = getAllotJobMap();
        log.info("allotJobMap={}", allotJobMap);

        entityHelpService.invalidAllDttaskJob();
        entityHelpService.saveAllDttaskJob(allotJobMap);
        Map<Long, List<DttaskJob>> dttaskJobMap = entityHelpService.queryDttaskJob();
        executeDttaskJob(new ExecuteDttaskJobContext(dttaskJobMap, true));
    }

    private void executeDttaskJob(ExecuteDttaskJobContext executeDttaskJobContext) {
        Map<Long, List<DttaskJob>> dttaskJobMap = executeDttaskJobContext.getDttaskJobMap();
        boolean startFlag = executeDttaskJobContext.getStartFlag();
        dttaskJobMap.forEach((dttaskId, dttaskJobList) -> {
            if (!Objects.equals(ServerInfo.getServerId(), dttaskId)) {
                // 向其它节点发送 任务控制 命令
                Set<Long> dttaskJobIdList = dttaskJobList.stream().map(DttaskJob::getId).collect(Collectors.toSet());
                DttaskMessage controlCollectMessage = DttaskMessage.buildControlCollectMessage(
                        dttaskJobIdList, startFlag, dttaskId);
                log.info("向nodeId={}发送采集控制指令={}", controlCollectMessage);
                ServerInfo.getChannelByServerId(dttaskId).writeAndFlush(controlCollectMessage);
            } else {
                log.info("{}分配给自己的采集任务={}", startFlag ? "执行" : "停止", dttaskJobList);
                Set<Long> dttaskJobIds = dttaskJobList.stream().map(DttaskJob::getId).collect(Collectors.toSet());
                if (startFlag) {
                    collectDataService.startCollectData(dttaskJobIds);
                } else {
                    collectDataService.stopCollectData(dttaskJobIds);
                }
            }
        });
    }

    private Map<Long, List<Job>> getAllotJobMap() {
        List<Job> allJob = getAllJob();
        return average(allJob);
    }

    private <T> Map<Long, List<T>> average(List<T> list) {
        List<NodeInfo> nodeInfoList = ServerInfo.getNodeInfoList();
        int nodeCount = nodeInfoList.size();
        Map<Long, List<T>> allotJobMap = new HashMap<>();
        int averageJobCount = list.size() / nodeCount;
        int remainingJobCount = list.size() % nodeCount;
        int currentIndex = 0;
        for (NodeInfo nodeInfo : nodeInfoList) {
            allotJobMap.put(nodeInfo.getServerId(), list.subList(currentIndex, currentIndex + averageJobCount));
            currentIndex += averageJobCount;
        }
        while (remainingJobCount != 0) {
            for (Map.Entry<Long, List<T>> entry : allotJobMap.entrySet()) {
                entry.getValue().addAll(list.subList(currentIndex, currentIndex + 1));
                currentIndex++;
                remainingJobCount--;
            }
        }
        return allotJobMap;
    }

    private Map<Long, List<DttaskJob>> getDttaskJobMap() {
        List<DttaskJob> allDttaskJob = getAllDttaskJob();
        return average(allDttaskJob);
    }

    @Override
    public void rebalanceJob(RebalanceJobContext rebalanceJobContext) {
        if (rebalanceJobContext.getType().isFollowerOffline()
                || rebalanceJobContext.getType().isControllerOffline()) {
            long offlineServerId = rebalanceJobContext.getServerId();
            log.info("{}节点={}下线->重平衡job={}",
                    rebalanceJobContext.getType().isFollowerOffline() ? "follower" : "controller",
                    offlineServerId,
                    rebalanceJobContext);
            List<DttaskJob> dttaskJobs = getByDttaskId(offlineServerId);
            List<NodeInfo> nodeInfoList = ServerInfo.getNodeInfoList();
            Map<Long, List<DttaskJob>> allotMap = new HashMap<>();
            int i = 0;
            int nodeCount = nodeInfoList.size();
            while (i < dttaskJobs.size()) {
                DttaskJob dttaskJob = dttaskJobs.get(i);
                NodeInfo nodeInfo = nodeInfoList.get(i % nodeCount);
                i++;
                List<DttaskJob> dttaskJobList = allotMap.getOrDefault(nodeInfo.getServerId(), new ArrayList<>());
                dttaskJobList.add(dttaskJob);
                allotMap.put(nodeInfo.getServerId(), dttaskJobList);
            }
            executeDttaskJob(new ExecuteDttaskJobContext(allotMap, true));
        } else if (rebalanceJobContext.getType().isNodeOnline()) {
            log.info("节点上线->重平衡job={}", rebalanceJobContext);
            long onlineServerId = rebalanceJobContext.getServerId();
            Map<Long, List<DttaskJob>> dttaskJobMap = BeanUseHelper.entityHelpService().queryDttaskJob();
            Map<Long, List<DttaskJob>> allotDttaskJobMap = getDttaskJobMap();
            Map<Long, List<DttaskJob>> stopDttaskJobMapOfOldNodes = new HashMap<>();
            Map<Long, List<DttaskJob>> startDttaskJobMapOfNewNodes = new HashMap<>();
            List<DttaskJob> startDttaskJobs = new ArrayList<>();
            dttaskJobMap.forEach((serverId, dttaskJobList) -> {
                int size = dttaskJobList.size();
                int newSize = allotDttaskJobMap.get(serverId).size();
                if (size > newSize) {
                    List<DttaskJob> dttaskJobs = dttaskJobList.subList(0, size - newSize);
                    stopDttaskJobMapOfOldNodes.put(serverId, dttaskJobs);
                    startDttaskJobs.addAll(dttaskJobs);
                }
            });
            startDttaskJobMapOfNewNodes.put(onlineServerId, startDttaskJobs);
            executeDttaskJob(new ExecuteDttaskJobContext(stopDttaskJobMapOfOldNodes, false));
            executeDttaskJob(new ExecuteDttaskJobContext(startDttaskJobMapOfNewNodes, true));
        }
    }
    

}
