package com.swsm.dttask.server.service;

import com.swsm.dttask.common.model.entity.DttaskJob;
import com.swsm.dttask.common.model.message.DttaskMessage;
import com.swsm.dttask.common.service.EntityHelpService;
import com.swsm.dttask.common.util.Constant;
import com.swsm.dttask.server.BeanUseHelper;
import com.swsm.dttask.server.model.NodeInfo;
import com.swsm.dttask.server.model.ServerInfo;
import com.swsm.dttask.server.protocol.CollectDataService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swsm
 * @date 2023-12-05
 */
@Component
@Slf4j
public class NetworkService {

    @Autowired
    private CollectDataService collectDataService;


    public void connectController(long controllerServerId) {
        NodeInfo nodeInfo = ServerInfo.getNodeInfo(controllerServerId);
        doConnectController(nodeInfo);
    }

    private void doConnectController(NodeInfo nodeInfo) {
        try {
            long controllerServerId = nodeInfo.getServerId();
            long localServerId = ServerInfo.getServerId();
            ChannelFuture connect = ServerInfo.getConnectOtherNodeBootStrap().connect(nodeInfo.getIp(),
                    nodeInfo.getPort()).sync();
            connect.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("连接controller={} 成功...", nodeInfo);
                    Channel channel = future.channel();
                    ServerInfo.cacheChannelAnsServerIdRel(controllerServerId, channel);
                    DttaskMessage dttaskMessage = DttaskMessage.buildNodeOnlineMessage(localServerId);
                    log.info("向Node ip-port={},发送NodeOnlineMessage={}", nodeInfo, dttaskMessage);
                    channel.writeAndFlush(dttaskMessage);
                } else {
                    log.info("连接controller={} 失败...", nodeInfo);
                }
            });
        } catch (Exception e) {
            log.error("连接controller节点={}出现异常", nodeInfo, e);
        }
        
    }
    
    private int getVotVersion() {
        return Constant.VOTE_VERSION;
    }

    public void startVote(Long lastControllerServerId, long minServerId) {
        long localServerId = ServerInfo.getServerId();
        int votVersion = getVotVersion();
        if (localServerId < minServerId) {
            log.info("当前节点是最小的，等待别人来选举它");
            return;
        }
        NodeInfo nodeInfo = ServerInfo.getNodeInfo(minServerId);
        doVote(lastControllerServerId, votVersion, minServerId, localServerId, nodeInfo);
    }

    private void doVote(Long lastControllerServerId, int votVersion,
                        long minServerId, long localServerId,
                        NodeInfo nodeInfo) {
        boolean res = false;
        while(true) {
            try {
                ChannelFuture connect = ServerInfo.getConnectOtherNodeBootStrap().connect(nodeInfo.getIp(),
                        nodeInfo.getPort()).sync();
                connect.addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        log.info("连接dttask-server serverId={} 成功...", minServerId);
                        // 向服务端发送 客户端id信息 
                        Channel channel = future.channel();
                        ServerInfo.cacheChannelAnsServerIdRel(minServerId, channel);
                        DttaskMessage dttaskMessage = DttaskMessage.buildVotRequestMessage(
                                lastControllerServerId, localServerId, minServerId, votVersion);
                        log.info("向Node={},发送VotRequestMessage={}", nodeInfo, dttaskMessage);
                        channel.writeAndFlush(dttaskMessage);
                    } else {
                        log.info("连接dttask-server id={} 失败...", minServerId);
                    }
                });
                res = true;
                break;
            } catch (Exception e) {
                log.error("连接节点={}出现异常={}", minServerId, e.getMessage());
            } finally {
                if (!res) {
                    try {
                        Thread.sleep(3L * 1000);
                    } catch (InterruptedException e) {
                        log.error("连接节点={}出现InterruptedException异常={}", minServerId, e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public void stopCollect(Set<Long> dttaskJobIds) {
        long localServerId = ServerInfo.getServerId();
        Map<Long, List<DttaskJob>> groupMap = getGroupDttaskJob(dttaskJobIds);
        List<DttaskJob> localDttaskJob = groupMap.getOrDefault(localServerId, Collections.emptyList());
        if (!localDttaskJob.isEmpty()) {
            // 本节点
            Set<Long> localDttaskJobIds = localDttaskJob.stream().map(DttaskJob::getId).collect(Collectors.toSet());
            collectDataService.stopCollectData(localDttaskJobIds);
        } else {
            // 向目标节点发送 stop job命令
            groupMap.remove(localServerId);
            groupMap.forEach((serverId, dttaskJobList) -> {
                Set<Long> stopDttaskJobIds = dttaskJobList.stream().map(DttaskJob::getId).collect(Collectors.toSet());
                DttaskMessage dttaskMessage = DttaskMessage.buildControlCollectMessage(stopDttaskJobIds, false, serverId);
                log.info("向serverId={}发送停止任务的指令={}", dttaskMessage);
                Channel channel = ServerInfo.getChannelByServerId(serverId);
                channel.writeAndFlush(dttaskMessage);
            });
        }

    }

    private Map<Long, List<DttaskJob>> getGroupDttaskJob(Set<Long> dttaskJobIds) {
        EntityHelpService entityHelpService = BeanUseHelper.entityHelpService();
        List<DttaskJob> dttaskJobs = entityHelpService.queryDttaskJob(dttaskJobIds);
        return dttaskJobs.stream().collect(Collectors.groupingBy(DttaskJob::getDttaskId));
    }

    public void startCollect(Set<Long> dttaskJobIds) {
        long localServerId = ServerInfo.getServerId();
        Map<Long, List<DttaskJob>> groupMap = getGroupDttaskJob(dttaskJobIds);
        List<DttaskJob> localDttaskJob = groupMap.getOrDefault(localServerId, Collections.emptyList());
        if (!localDttaskJob.isEmpty()) {
            // 本节点
            Set<Long> localDttaskJobIds = localDttaskJob.stream().map(DttaskJob::getId).collect(Collectors.toSet());
            collectDataService.startCollectData(localDttaskJobIds);
        } else {
            // 向目标节点发送 start job命令
            groupMap.remove(localServerId);
            groupMap.forEach((serverId, dttaskJobList) -> {
                Set<Long> stopDttaskJobIds = dttaskJobList.stream().map(DttaskJob::getId).collect(Collectors.toSet());
                DttaskMessage dttaskMessage = DttaskMessage.buildControlCollectMessage(stopDttaskJobIds, true, serverId);
                log.info("向serverId={}发送开始任务的指令={}", dttaskMessage);
                Channel channel = ServerInfo.getChannelByServerId(serverId);
                channel.writeAndFlush(dttaskMessage);
            });
        }
    }

}
